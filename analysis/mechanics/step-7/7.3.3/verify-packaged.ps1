param(
    [string]$JavaExecutable = 'java',
    [string]$JarPath = 'target/dnd-pub-data-0.0.1-SNAPSHOT.jar',
    [string]$BaselinePath = 'target/7.3.3-http-before.json',
    [switch]$CaptureBaseline
)

$ErrorActionPreference = 'Stop'
$jarFile = (Resolve-Path -LiteralPath $JarPath).Path
if (-not $CaptureBaseline -and -not (Test-Path -LiteralPath $BaselinePath)) {
    throw 'Capture the ordinary-response baseline before applying the Shield integration.'
}
$stdoutPath = Join-Path (Get-Location) 'target/7.3.3-packaged.stdout.log'
$stderrPath = Join-Path (Get-Location) 'target/7.3.3-packaged.stderr.log'
$arguments = @('-jar', ('"{0}"' -f $jarFile), '--server.address=127.0.0.1',
    '--server.port=0', '--spring.main.banner-mode=off')
$application = Start-Process -FilePath $JavaExecutable -ArgumentList $arguments -WindowStyle Hidden `
    -WorkingDirectory (Split-Path -Parent $jarFile) `
    -RedirectStandardOutput $stdoutPath -RedirectStandardError $stderrPath -PassThru

function Assert-Shield($detail, $edition) {
    $inspection = $detail.interpretedMechanics
    if ($detail.id -cne ('spell:' + $edition.ToLowerInvariant() + ':shield') -or
            $inspection.projection -ne 'PRIVATE_INSPECTION' -or $inspection.availability -ne 'AVAILABLE' -or
            $inspection.processing.state -ne 'PROCESSED' -or $inspection.coverage.status -ne 'PARTIAL' -or
            $inspection.interpretations.Count -ne 5 -or $inspection.evidence.Count -ne 13 -or
            $inspection.reviewSummary.total -ne 5 -or $inspection.reviewSummary.pendingReview -ne 2 -or
            $inspection.reviewSummary.needsMechanicsReview -ne 3 -or $inspection.reviewSummary.verified -ne 0 -or
            $inspection.coverage.unsupportedCount -ne 0 -or $inspection.coverage.unassessedSourcePaths.Count -eq 0) {
        throw ('Invalid processed Shield envelope: ' + $edition)
    }
    $claims = @{}
    foreach ($claim in $inspection.interpretations) {
        if ($claim.licensing.status -ne 'NEEDS_REVIEW' -or $claim.review.recordedStatus -ne 'PENDING_REVIEW' -or
                $claim.review.effectiveStatus -notin @('PENDING_REVIEW', 'NEEDS_MECHANICS_REVIEW') -or
                $claim.evidenceAssociations.Count -eq 0 -or $claim.context.subject.kind -ne 'CASTER') {
            throw 'Invalid claim metadata or source associations'
        }
        if ($claims.ContainsKey($claim.derivation.patternId)) {
            throw 'Duplicate extraction pattern'
        }
        $claims[$claim.derivation.patternId] = $claim
    }
    $ac = $claims['shield-ac-v1']
    if ($ac.payload.kind -ne 'ARMOR_CLASS_MODIFIER' -or $ac.payload.amount.value -ne 5 -or
            $ac.payload.amount.authoredToken -cne '+5' -or $ac.context.bindings.Count -ne 3 -or
            $ac.context.bindings[0].kind -ne 'ACTIVATED_BY' -or
            $ac.context.bindings[1].kind -ne 'APPLIES_FOR_DURATION' -or
            $ac.context.bindings[2].kind -ne 'INCLUDES_TRIGGERING_ATTACK') {
        throw 'Missing source-qualified AC interpretation or qualifiers'
    }
    $reaction = $claims['shield-reaction-v1'].payload
    $expectedEvent = if ($edition -eq 'PHB') { 'ATTACK_HIT' } else { 'ATTACK_ROLL_HIT' }
    $expectedMention = if ($edition -eq 'PHB') { 'TAG_REFERENCE' } else { 'PROSE_MENTION' }
    if ($reaction.kind -ne 'REACTION_ACTIVATION' -or $reaction.cost.amount -ne 1 -or
            $reaction.trigger.kind -ne 'ANY' -or $reaction.trigger.alternatives.Count -ne 2 -or
            $reaction.trigger.alternatives[0].event -ne $expectedEvent -or
            $reaction.trigger.alternatives[1].spell.form -ne $expectedMention -or
            $null -ne $reaction.trigger.alternatives[1].spell.authoredSource -or
            $null -ne $reaction.trigger.alternatives[1].spell.targetId) {
        throw 'Edition-specific ordered reaction alternatives changed'
    }
    $turn = $claims['shield-turn-boundary-v1'].payload.specification
    $declared = $claims['spell-declared-duration-v1'].payload.specification
    if ($turn.kind -ne 'UNTIL_TURN_BOUNDARY' -or $turn.actor -ne 'CASTER' -or
            $turn.boundary -ne 'START_OF_NEXT_TURN' -or $declared.kind -ne 'DECLARED_SPELL_DURATION' -or
            $declared.spellId.value -cne $detail.id -or $declared.durationIndex -ne 0) {
        throw 'Structured and prose duration distinction changed'
    }
    $prevention = $claims['shield-special-interaction-v1'].payload
    $expectedSource = if ($edition -eq 'XPHB') { 'XPHB' } else { $null }
    if ($prevention.kind -ne 'SPELL_DAMAGE_PREVENTION' -or
            $prevention.spell.authoredSource -cne $expectedSource -or
            $null -ne $prevention.spell.targetId -or $prevention.spell.resolution -ne 'NOT_ATTEMPTED') {
        throw 'Spell-specific protection or authored qualification changed'
    }
    $json = $detail | ConvertTo-Json -Depth 60 -Compress
    foreach ($forbidden in @('{@spell', 'humanDecision', 'reviewerId', 'An invisible barrier', 'An imperceptible barrier')) {
        if ($json.Contains($forbidden)) {
            throw ('Restricted content or reviewer metadata leaked: ' + $forbidden)
        }
    }
}

try {
    $deadline = [DateTime]::UtcNow.AddSeconds(60)
    $portNumber = $null
    do {
        if ($application.HasExited) {
            throw 'Packaged application exited before startup'
        }
        $startup = Get-Content -LiteralPath $stdoutPath -Raw
        if ($startup -match 'Tomcat started on port (\d+)' -and $startup -match 'Started DndPubDataApplication') {
            $portNumber = [int]([regex]::Match($startup, 'Tomcat started on port (\d+)').Groups[1].Value)
            break
        }
        Start-Sleep -Milliseconds 250
    } while ([DateTime]::UtcNow -lt $deadline)
    if ($null -eq $portNumber) {
        throw 'Packaged application startup timed out'
    }
    $base = 'http://127.0.0.1:' + $portNumber
    if ($CaptureBaseline) {
        $ordinary = [ordered]@{}
        foreach ($route in @('/api/spells/spell:phb:shield', '/api/spells/spell:xphb:shield',
                '/api/spells/spell:phb:fireball', '/api/spells/spell:phb:healing-word',
                '/api/spells/by-name/Shield?source=PHB', '/api/spells?limit=2')) {
            $ordinary[$route] = (Invoke-WebRequest -Uri ($base + $route) -UseBasicParsing).Content
        }
        $shield = [ordered]@{}
        foreach ($edition in @('phb', 'xphb')) {
            $shield[$edition] = Invoke-RestMethod ($base + '/api/spells/spell:' + $edition + ':shield?include=interpretedMechanics')
        }
        [ordered]@{ ordinary = $ordinary; shield = $shield } | ConvertTo-Json -Depth 60 |
            Set-Content -LiteralPath $BaselinePath -Encoding UTF8
        Write-Output 'Captured pre-expansion ordinary responses and complete Shield inspections.'
        return
    }
    $baseline = Get-Content -LiteralPath $BaselinePath -Raw | ConvertFrom-Json
    foreach ($property in $baseline.ordinary.PSObject.Properties) {
        $ordinary = (Invoke-WebRequest -Uri ($base + $property.Name) -UseBasicParsing).Content
        if ($ordinary -cne $property.Value -or $ordinary.Contains('interpretedMechanics')) {
            throw ('Ordinary response bytes changed: ' + $property.Name)
        }
    }
    $phb = Invoke-RestMethod ($base + '/api/spells/spell:phb:shield?include=interpretedMechanics')
    $xphb = Invoke-RestMethod ($base + '/api/spells/spell:xphb:shield?include=interpretedMechanics')
    Assert-Shield $phb 'PHB'
    Assert-Shield $xphb 'XPHB'
    foreach ($edition in @('phb', 'xphb')) {
        $actualShield = if ($edition -eq 'phb') { $phb } else { $xphb }
        if (($actualShield | ConvertTo-Json -Depth 60 -Compress) -cne
                ($baseline.shield.$edition | ConvertTo-Json -Depth 60 -Compress)) {
            throw ('Shield inspection changed: ' + $edition)
        }
    }
    $byName = Invoke-RestMethod ($base + '/api/spells/by-name/Shield?include=interpretedMechanics')
    if ($byName.Count -ne 2) {
        throw 'By-name multiplicity changed'
    }
    foreach ($detail in $byName) {
        Assert-Shield $detail $detail.source.source
    }
    $fireball = Invoke-RestMethod ($base + '/api/spells/spell:phb:fireball?include=interpretedMechanics')
    $fi = $fireball.interpretedMechanics
    if ($fi.processing.state -ne 'PROCESSED' -or $fi.coverage.status -ne 'PARTIAL' -or
            $fi.observations.Count -ne 2 -or $fi.interpretations.Count -ne 2 -or $fi.unsupported.Count -ne 3 -or
            $fi.interpretations[0].payload.ability.ability -ne 'DEXTERITY' -or
            $fi.interpretations[0].payload.difficulty.kind -ne 'UNKNOWN' -or
            $fi.interpretations[1].payload.amount.count -ne 8 -or
            $fi.interpretations[1].payload.amount.sides -ne 6 -or
            $fi.interpretations[1].payload.damageType.authoredCode -cne 'fire' -or
            $fi.interpretations[1].context.bindings[0].outcome -ne 'FAILURE') {
        throw 'Fireball partial save/damage interpretation or abstentions are incorrect'
    }
    $healing = Invoke-RestMethod ($base + '/api/spells/spell:phb:healing-word?include=interpretedMechanics')
    $hi = $healing.interpretedMechanics
    $amount = $hi.interpretations[0].payload.amount
    if ($hi.processing.state -ne 'PROCESSED' -or $hi.coverage.status -ne 'PARTIAL' -or
            $hi.interpretations.Count -ne 1 -or $hi.unsupported.Count -ne 2 -or $hi.observations.Count -ne 2 -or
            $amount.kind -ne 'SUM' -or $amount.terms.Count -ne 2 -or
            $amount.terms[0].count -ne 1 -or $amount.terms[0].sides -ne 4 -or
            $amount.terms[1].symbol -ne 'CASTER_SPELLCASTING_ABILITY_MODIFIER') {
        throw 'Healing Word lost the symbolic modifier, evidence context or deferred restrictions'
    }
    foreach ($document in @($fi, $hi)) {
        foreach ($claim in $document.interpretations) {
            if ($claim.review.recordedStatus -ne 'PENDING_REVIEW' -or
                    $claim.review.effectiveStatus -ne 'NEEDS_MECHANICS_REVIEW' -or
                    $claim.licensing.status -ne 'NEEDS_REVIEW' -or $claim.assessment.completeness -ne 'PARTIAL' -or
                    $claim.evidenceAssociations.Count -eq 0) {
                throw 'Unexpected review, rights, completeness or evidence metadata'
            }
        }
    }
    $unattempted = Invoke-RestMethod ($base + '/api/spells/spell:phb:magic-missile?include=interpretedMechanics')
    if ($unattempted.interpretedMechanics.processing.state -ne 'NOT_ATTEMPTED' -or
            $unattempted.interpretedMechanics.coverage.status -ne 'NOT_ASSESSED' -or
            $null -ne $unattempted.interpretedMechanics.interpretations) {
        throw 'Out-of-scope Spell was interpreted'
    }
    foreach ($include in @('invalid', 'InterpretedMechanics', '')) {
        $invalidStatus = $null
        try {
            Invoke-WebRequest -Uri ($base + '/api/spells/spell:phb:shield?include=' + $include) -UseBasicParsing | Out-Null
        } catch {
            $invalidStatus = [int]$_.Exception.Response.StatusCode
        }
        if ($invalidStatus -ne 400) {
            throw 'Invalid include did not return 400'
        }
    }
    [ordered]@{
        startup = 'PASS'
        ordinaryResponseCompatibility = 'PASS'
        invalidIncludes = 'PASS'
        phbShield = $phb
        xphbShield = $xphb
        byName = $byName
        fireball = $fireball
        healingWord = $healing
        unattempted = $unattempted
    } | ConvertTo-Json -Depth 60 | Set-Content -LiteralPath 'target/7.3.3-packaged-results.json' -Encoding UTF8
    Write-Output 'Packaged Shield stability, Fireball/Healing Word extraction, unattempted owner, default bytes and invalid includes passed.'
} finally {
    if (-not $application.HasExited) {
        Stop-Process -Id $application.Id
        $application.WaitForExit()
    }
}
