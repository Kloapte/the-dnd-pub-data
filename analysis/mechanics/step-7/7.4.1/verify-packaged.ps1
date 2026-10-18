param(
    [string]$JavaExecutable = 'java',
    [string]$JarPath = 'target/dnd-pub-data-0.0.1-SNAPSHOT.jar'
)

$ErrorActionPreference = 'Stop'
$jarFile = (Resolve-Path -LiteralPath $JarPath).Path
$stdoutPath = Join-Path (Get-Location) 'target/7.4.1-packaged.stdout.log'
$stderrPath = Join-Path (Get-Location) 'target/7.4.1-packaged.stderr.log'
$arguments = @('-jar', ('"{0}"' -f $jarFile), '--server.address=127.0.0.1',
    '--server.port=0', '--spring.main.banner-mode=off')
$application = Start-Process -FilePath $JavaExecutable -ArgumentList $arguments -WindowStyle Hidden `
    -WorkingDirectory (Split-Path -Parent $jarFile) `
    -RedirectStandardOutput $stdoutPath -RedirectStandardError $stderrPath -PassThru

try {
    $deadline = [DateTime]::UtcNow.AddSeconds(60)
    $portNumber = $null
    do {
        if ($application.HasExited) {
            throw 'Packaged application exited before startup.'
        }
        $startup = Get-Content -LiteralPath $stdoutPath -Raw
        if ($startup -match 'Tomcat started on port (\d+)' -and
                $startup -match 'Started DndPubDataApplication') {
            $portNumber = [int]([regex]::Match($startup, 'Tomcat started on port (\d+)').Groups[1].Value)
            break
        }
        Start-Sleep -Milliseconds 250
    } while ([DateTime]::UtcNow -lt $deadline)
    if ($null -eq $portNumber) {
        throw 'Packaged application startup timed out.'
    }

    $base = 'http://127.0.0.1:' + $portNumber
    $checks = @(
        @{ route = '/api/spells/spell:phb:burning-hands'; kind = 'DAMAGE'; amount = 3 },
        @{ route = '/api/spells/spell:phb:cone-of-cold'; kind = 'DAMAGE'; amount = 8 },
        @{ route = '/api/spells/spell:phb:cure-wounds'; kind = 'HEALING'; amount = 8 },
        @{ route = '/api/spells/spell:phb:mass-healing-word'; kind = 'HEALING'; amount = 4 }
    )
    $results = [ordered]@{}
    foreach ($check in $checks) {
        $detail = Invoke-RestMethod ($base + $check.route + '?include=interpretedMechanics')
        $inspection = $detail.interpretedMechanics
        $claim = $inspection.interpretations | Where-Object { $_.payload.kind -eq $check.kind } | Select-Object -First 1
        if ($inspection.processing.state -ne 'PROCESSED' -or $null -eq $claim) {
            throw ('No processed ' + $check.kind + ' claim for ' + $check.route)
        }
        $numericValue = if ($check.kind -eq 'HEALING') {
            $claim.payload.amount.terms[0].sides
        } else {
            $claim.payload.amount.count
        }
        if ($numericValue -ne $check.amount -or
                $claim.review.recordedStatus -ne 'PENDING_REVIEW' -or
                $claim.licensing.status -ne 'NEEDS_REVIEW' -or
                $claim.assessment.completeness -ne 'PARTIAL') {
            throw ('Invalid source-backed inspection for ' + $check.route + ': ' +
                ($inspection | ConvertTo-Json -Depth 40 -Compress))
        }
        if ($check.kind -eq 'HEALING' -and
                $claim.payload.amount.kind -ne 'SUM' -or $check.kind -eq 'HEALING' -and
                $claim.payload.amount.terms[1].symbol -ne 'CASTER_SPELLCASTING_ABILITY_MODIFIER') {
            throw ('Healing modifier was not preserved for ' + $check.route)
        }
        if ($check.kind -eq 'DAMAGE' -and
                $inspection.interpretations[0].payload.kind -ne 'SAVING_THROW_REQUIREMENT') {
            throw ('Saving throw requirement is missing for ' + $check.route)
        }
        $results[$check.route] = $inspection
    }

    $unattempted = Invoke-RestMethod ($base + '/api/spells/spell:phb:prestidigitation?include=interpretedMechanics')
    if ($unattempted.interpretedMechanics.processing.state -ne 'NOT_ATTEMPTED' -or
            $unattempted.interpretedMechanics.coverage.status -ne 'NOT_ASSESSED') {
        throw 'Out-of-scope Spell should remain unattempted.'
    }
    foreach ($route in @('/api/spells/spell:phb:fireball', '/api/spells/spell:phb:cure-wounds')) {
        $ordinary = (Invoke-WebRequest -Uri ($base + $route) -UseBasicParsing).Content
        if ($ordinary.Contains('interpretedMechanics')) {
            throw ('Inspection leaked into an ordinary response: ' + $route)
        }
    }
    $invalidStatus = $null
    try {
        Invoke-WebRequest -Uri ($base + '/api/spells/spell:phb:cure-wounds?include=invalid') `
            -UseBasicParsing | Out-Null
    } catch {
        $invalidStatus = [int]$_.Exception.Response.StatusCode
    }
    if ($invalidStatus -ne 400) {
        throw 'Invalid include did not return HTTP 400.'
    }

    [ordered]@{
        startup = 'PASS'
        newProcessedSpells = $results
        unattempted = $unattempted.interpretedMechanics.processing.state
        ordinaryResponses = 'PASS'
        invalidInclude = $invalidStatus
    } | ConvertTo-Json -Depth 60 | Set-Content -LiteralPath 'target/7.4.1-packaged-results.json' -Encoding UTF8
    Write-Output 'Packaged generalized Spell inspections, unattempted owner and route boundaries passed.'
} finally {
    if (-not $application.HasExited) {
        Stop-Process -Id $application.Id
        $application.WaitForExit()
    }
}
