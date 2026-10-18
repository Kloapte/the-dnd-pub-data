param(
    [string]$JavaExecutable = 'C:/Program Files/Java/jdk-21.0.12.1/bin/java.exe',
    [string]$JarPath = 'target/dnd-pub-data-0.0.1-SNAPSHOT.jar'
)

$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.IO.Compression.FileSystem
$jarFile = (Resolve-Path -LiteralPath $JarPath).Path
$archive = [System.IO.Compression.ZipFile]::OpenRead($jarFile)
try {
    $prefix = 'BOOT-INF/classes/publication/spell-formulas/v1/'
    $entries = @($archive.Entries | Where-Object { $_.FullName.StartsWith($prefix) -and $_.Name.EndsWith('.json') })
    if ($entries.Count -ne 4 -or $archive.GetEntry($prefix + 'decisions.json')) {
        throw 'Initial production JAR must contain four publication resources and no decisions'
    }
    $runtimeInputs = @{}
    foreach ($entry in $entries) {
        $reader = [System.IO.StreamReader]::new($entry.Open())
        try {
            $runtimeInputs[$entry.Name] = $reader.ReadToEnd() | ConvertFrom-Json
        } finally {
            $reader.Dispose()
        }
    }
    $policy = $runtimeInputs['policy.json']
    $candidates = $runtimeInputs['candidates.json'].candidates
    if ($candidates.Count -ne 250 -or @($candidates | Where-Object { $_.initialReviewState -ne 'PENDING_REVIEW' }).Count) {
        throw 'Packaged candidates must all remain pending'
    }
    if ($policy.authorities.Count -or $null -ne $policy.releaseApproval -or $null -ne $policy.decisionsSha256 -or
            $null -ne $runtimeInputs['notices.json'].approval) {
        throw 'Initial production JAR contains unexpected authority or approval configuration'
    }
    if (@($archive.Entries | Where-Object { $_.FullName -match 'SpellPublicationCatalogTests' }).Count) {
        throw 'Test-only approval fixtures must not be packaged'
    }
} finally {
    $archive.Dispose()
}

$stdoutPath = Join-Path (Get-Location) 'target/p1d-packaged.stdout.log'
$stderrPath = Join-Path (Get-Location) 'target/p1d-packaged.stderr.log'
$arguments = @('-jar', ('"{0}"' -f $jarFile), '--server.port=0', '--spring.main.banner-mode=off')
$application = Start-Process -FilePath $JavaExecutable -ArgumentList $arguments -WindowStyle Hidden `
    -WorkingDirectory (Split-Path -Parent $jarFile) `
    -RedirectStandardOutput $stdoutPath -RedirectStandardError $stderrPath -PassThru
try {
    $deadline = [DateTime]::UtcNow.AddSeconds(50)
    $portNumber = $null
    do {
        if ($application.HasExited) {
            throw 'Packaged application exited before startup; inspect the local redirected logs'
        }
        $startupText = Get-Content -LiteralPath $stdoutPath -Raw
        if ($startupText -match 'Tomcat started on port (\d+)' -and $startupText -match 'Started DndPubDataApplication') {
            $portNumber = [int]([regex]::Match($startupText, 'Tomcat started on port (\d+)').Groups[1].Value)
            break
        }
        Start-Sleep -Milliseconds 250
    } while ([DateTime]::UtcNow -lt $deadline)
    if ($null -eq $portNumber) {
        throw 'Packaged application did not finish startup within 50 seconds'
    }
    $base = 'http://localhost:' + $portNumber
    $fireball = Invoke-RestMethod ($base + '/api/spells/spell:phb:fireball')
    $shield = Invoke-RestMethod ($base + '/api/spells/spell:phb:shield')
    $byName = Invoke-RestMethod ($base + '/api/spells/by-name/Fireball?source=PHB')
    $summaries = Invoke-RestMethod ($base + '/api/spells')
    if ($fireball.formulaProjectionStatus -ne 'WITHHELD' -or $shield.formulaProjectionStatus -ne 'NONE_OBSERVED' -or
            $byName[0].formulaProjectionStatus -ne 'WITHHELD' -or $summaries.Count -ne 969) {
        throw 'Packaged Spell contract changed'
    }
    foreach ($detail in @($fireball, $shield, $byName[0])) {
        if ($detail.PSObject.Properties.Name -contains 'formulaObservations') {
            throw 'P1D must not publish formula payloads'
        }
    }
    if (@($summaries | Where-Object { $_.PSObject.Properties.Name -contains 'formulaProjectionStatus' }).Count) {
        throw 'Summary contract must remain unchanged'
    }
    $result = [ordered]@{
        packagedStartup = 'PASS'
        internalStartupValidation = 'PASS'
        candidates = $candidates.Count
        pendingCandidates = $candidates.Count
        productionDecisionArtifacts = 0
        authorizedObservations = 0
        spellSummaries = $summaries.Count
        fireballStatus = $fireball.formulaProjectionStatus
        shieldStatus = $shield.formulaProjectionStatus
        formulaPayloadPublished = $false
        testFixturesPackaged = $false
    }
    $result | ConvertTo-Json | Set-Content -LiteralPath 'target/p1d-packaged-verification.json' -Encoding UTF8
    $result | ConvertTo-Json -Compress
} finally {
    if (-not $application.HasExited) {
        Stop-Process -Id $application.Id
        $application.WaitForExit()
    }
}
