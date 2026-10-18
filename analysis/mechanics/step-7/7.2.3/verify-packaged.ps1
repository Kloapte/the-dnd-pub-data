param(
    [switch]$CaptureBaseline,
    [string]$JavaExecutable = 'C:/Program Files/Java/jdk-21.0.12.1/bin/java.exe',
    [string]$JarPath = 'target/dnd-pub-data-0.0.1-SNAPSHOT.jar'
)

$ErrorActionPreference = 'Stop'
$jarFile = (Resolve-Path -LiteralPath $JarPath).Path
$stdoutPath = Join-Path (Get-Location) 'target/7.2.3-packaged.stdout.log'
$stderrPath = Join-Path (Get-Location) 'target/7.2.3-packaged.stderr.log'
$baselinePath = 'target/7.2.3-http-before.json'
$arguments = @('-jar', ('"{0}"' -f $jarFile), '--server.port=0', '--spring.main.banner-mode=off')
$application = Start-Process -FilePath $JavaExecutable -ArgumentList $arguments -WindowStyle Hidden `
    -WorkingDirectory (Split-Path -Parent $jarFile) `
    -RedirectStandardOutput $stdoutPath -RedirectStandardError $stderrPath -PassThru

function Assert-Inspection($detail) {
    $envelope = $detail.interpretedMechanics
    if ($null -eq $envelope -or $envelope.schemaVersion -ne 1 -or
            $envelope.projection -ne 'PRIVATE_INSPECTION' -or $envelope.availability -ne 'AVAILABLE' -or
            $envelope.processing.state -ne 'NOT_ATTEMPTED' -or $envelope.coverage.status -ne 'NOT_ASSESSED' -or
            $envelope.reviewSummary.total -ne 0 -or $envelope.reviewSummary.verified -ne 0 -or
            $envelope.coverage.unsupportedCount -ne 0 -or $envelope.sourceRevisions.Count -ne 1) {
        throw ('Incorrect unattempted inspection: ' + $detail.id)
    }
    foreach ($field in @('interpretations', 'unsupported', 'evidence', 'diagnostics')) {
        if ($envelope.PSObject.Properties.Name -contains $field) {
            throw ('Unexpected materialized field: ' + $field)
        }
    }
    foreach ($observation in $envelope.observations) {
        if ($observation.kind -ne 'FORMULA_REFERENCE' -or $observation.spellId -ne $detail.id -or
                $observation.evidence.kind -ne 'TEXT_SPAN' -or
                $observation.evidence.span.endExclusive -le $observation.evidence.span.startInclusive) {
            throw 'Invalid formula observation reference'
        }
        foreach ($field in @('expression', 'rawToken', 'rawText', 'arguments')) {
            if ($observation.PSObject.Properties.Name -contains $field) {
                throw 'Unexpected formula payload'
            }
        }
    }
}

try {
    $deadline = [DateTime]::UtcNow.AddSeconds(50)
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
    $base = 'http://localhost:' + $portNumber
    $paths = @('/api/spells/spell:phb:shield', '/api/spells/spell:xphb:shield',
        '/api/spells/spell:phb:fireball', '/api/spells/by-name/Shield?source=PHB', '/api/spells')
    $ordinary = [ordered]@{}
    foreach ($path in $paths) {
        $ordinary[$path] = (Invoke-WebRequest -Uri ($base + $path) -UseBasicParsing).Content
        if ($ordinary[$path].Contains('interpretedMechanics')) {
            throw 'Ordinary response changed'
        }
    }
    if ($CaptureBaseline) {
        $ordinary | ConvertTo-Json -Depth 4 | Set-Content -LiteralPath $baselinePath -Encoding UTF8
        Write-Output 'Captured pre-change packaged HTTP responses.'
        return
    }
    if (Test-Path -LiteralPath $baselinePath) {
        $previous = Get-Content -LiteralPath $baselinePath -Raw | ConvertFrom-Json
        foreach ($path in $paths) {
            if ($ordinary[$path] -cne $previous.PSObject.Properties[$path].Value) {
                throw ('Ordinary HTTP JSON bytes changed: ' + $path)
            }
        }
    }
    $phb = Invoke-RestMethod ($base + '/api/spells/spell:phb:shield?include=interpretedMechanics')
    $xphb = Invoke-RestMethod ($base + '/api/spells/spell:xphb:shield?include=interpretedMechanics')
    $fireball = Invoke-RestMethod ($base + '/api/spells/spell:phb:fireball?include=interpretedMechanics')
    $byName = Invoke-RestMethod ($base + '/api/spells/by-name/Shield?include=interpretedMechanics')
    foreach ($detail in @($phb, $xphb, $fireball) + @($byName)) {
        Assert-Inspection $detail
    }
    if ($phb.id -ne 'spell:phb:shield' -or $xphb.id -ne 'spell:xphb:shield' -or $byName.Count -ne 2 -or
            $phb.formulaProjectionStatus -ne 'NONE_OBSERVED' -or $fireball.formulaProjectionStatus -ne 'WITHHELD' -or
            $fireball.interpretedMechanics.observations.Count -ne 2) {
        throw 'Spell identity, status, formula count or by-name multiplicity changed'
    }
    $invalidStatus = $null
    try {
        Invoke-WebRequest -Uri ($base + '/api/spells/spell:phb:shield?include=invalid') -UseBasicParsing | Out-Null
    } catch {
        $invalidStatus = [int]$_.Exception.Response.StatusCode
    }
    if ($invalidStatus -ne 400) {
        throw 'Invalid include did not return 400'
    }
    $result = [ordered]@{
        startup = 'PASS'
        ordinaryResponseCompatibility = 'PASS'
        byNameMatches = $byName.Count
        invalidIncludeStatus = $invalidStatus
        phbShield = $phb
        xphbShield = $xphb
        fireball = $fireball
    }
    $result | ConvertTo-Json -Depth 50 | Set-Content -LiteralPath 'target/7.2.3-packaged-results.json' -Encoding UTF8
    Write-Output 'Packaged HTTP checks passed: ordinary Shield, opted-in PHB/XPHB Shield, Fireball, by-name and invalid include.'
} finally {
    if (-not $application.HasExited) {
        Stop-Process -Id $application.Id
        $application.WaitForExit()
    }
}
