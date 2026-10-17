$ErrorActionPreference = 'Stop'

$workspacePath = (Resolve-Path (Join-Path $PSScriptRoot '../../..')).Path
$targetPath = Join-Path $workspacePath 'target'
$jarPath = Join-Path $targetPath 'dnd-pub-data-0.0.1-SNAPSHOT.jar'
if (-not (Test-Path -LiteralPath $jarPath)) {
    throw 'Package the application before running this verification.'
}

$listener = [System.Net.Sockets.TcpListener]::new([System.Net.IPAddress]::Loopback, 0)
$listener.Start()
$port = $listener.LocalEndpoint.Port
$listener.Stop()

$arguments = @('-jar', ('"{0}"' -f $jarPath), "--server.port=$port", '--server.address=127.0.0.1')
$jarProcess = Start-Process -FilePath java -ArgumentList $arguments -WorkingDirectory $targetPath `
    -WindowStyle Hidden -PassThru `
    -RedirectStandardOutput (Join-Path $targetPath 'batch-1-jar.stdout.log') `
    -RedirectStandardError (Join-Path $targetPath 'batch-1-jar.stderr.log')

try {
    $baseUrl = "http://127.0.0.1:$port/api"
    $barbarian = $null
    $deadline = (Get-Date).AddSeconds(60)
    while ((Get-Date) -lt $deadline) {
        if ($jarProcess.HasExited) {
            throw 'Packaged application exited before readiness.'
        }
        try {
            $barbarian = Invoke-RestMethod "$baseUrl/classes/class:phb:barbarian" -TimeoutSec 2
            break
        } catch {
            Start-Sleep -Milliseconds 500
        }
    }
    if ($null -eq $barbarian) {
        throw 'Packaged application did not become ready.'
    }

    $row = $barbarian.tables[0].rows[0]
    if ($row.cells.Count -ne 1 -or $row.cells[0].number -ne 2 `
            -or $row.positionedCells[0].columnIndex -ne 1 -or $row.positionedCells[0].number -ne 2) {
        throw 'Barbarian packaged projection mismatch.'
    }

    $owners = @(
        'classes/class:efa:artificer',
        'classes/class:phb:barbarian',
        'classes/class:xphb:barbarian',
        'classes/class:phb:warlock',
        'subclasses/subclass:xphb:xphb:fighter:psi-warrior',
        'subclasses/subclass:xphb:xphb:rogue:soulknife'
    )
    foreach ($owner in $owners) {
        $response = Invoke-RestMethod "$baseUrl/$owner"
        $positions = @($response.tables | ForEach-Object {
            $_.rows | ForEach-Object { $_.positionedCells }
        })
        if ($positions.Count -eq 0) {
            throw "Missing positioned cells at $owner"
        }
        $publicJson = $response | ConvertTo-Json -Depth 80 -Compress
        if ($publicJson.Contains('PRESENTATION_TEXT') -or $publicJson.Contains('{@')) {
            throw "Unsafe public projection at $owner"
        }
    }

    $warlock = Invoke-RestMethod "$baseUrl/classes/class:phb:warlock"
    if (@($warlock.spellcasting.pactTables[0].rows[0].positionedCells).Count -eq 0) {
        throw 'Missing nested pact table positions.'
    }
    $copied = Invoke-RestMethod "$baseUrl/subclasses/subclass:phb:xphb:fighter:eldritch-knight"
    if (@($copied.tables[0].rows[0].positionedCells).Count -eq 0) {
        throw 'Missing copy-effective table positions.'
    }
    $matches = Invoke-RestMethod "$baseUrl/classes/by-name/Barbarian?source=PHB"
    if (@($matches).Count -ne 1 `
            -or $matches[0].tables[0].rows[0].positionedCells[0].columnIndex -ne 1) {
        throw 'By-name contract mismatch.'
    }

    $expectedCounts = [ordered]@{
        'classes' = 30
        'subclasses' = 330
        'class-features' = 677
        'subclass-features' = 1499
    }
    $counts = [ordered]@{}
    foreach ($catalog in $expectedCounts.Keys) {
        # Assign first: Invoke-RestMethod emits a JSON array as one pipeline object.
        $payload = Invoke-RestMethod "$baseUrl/$catalog"
        $counts[$catalog] = @($payload).Count
        if ($counts[$catalog] -ne $expectedCounts[$catalog]) {
            throw "Packaged $catalog count changed: $($counts[$catalog])"
        }
    }

    $result = [ordered]@{
        packagedJar = 'target/dnd-pub-data-0.0.1-SNAPSHOT.jar'
        workingDirectory = 'target'
        sixAffectedOwnersVerified = $true
        barbarianColumnIndex = 1
        legacyBonus = 2
        nestedPactTablesVerified = $true
        copyEffectiveSubclassVerified = $true
        byNameVerified = $true
        counts = $counts
    }
    $json = $result | ConvertTo-Json -Depth 6
    $json | Set-Content -Encoding UTF8 (Join-Path $targetPath 'batch-1-packaged-verification.json')
    $json
} finally {
    if (-not $jarProcess.HasExited) {
        Stop-Process -Id $jarProcess.Id
    }
}
