$ErrorActionPreference = 'Stop'

$workspacePath = (Resolve-Path (Join-Path $PSScriptRoot '../../..')).Path
$targetPath = Join-Path $workspacePath 'target'
$jarPath = Join-Path $targetPath 'dnd-pub-data-0.0.1-SNAPSHOT.jar'
if (-not (Test-Path -LiteralPath $jarPath)) {
    throw 'Package the application before running this verification.'
}
$evidence = Get-Content -LiteralPath (Join-Path $PSScriptRoot 'field-evidence.json') -Raw | ConvertFrom-Json

$listener = [System.Net.Sockets.TcpListener]::new([System.Net.IPAddress]::Loopback, 0)
$listener.Start()
$port = $listener.LocalEndpoint.Port
$listener.Stop()

$arguments = @('-jar', ('"{0}"' -f $jarPath), "--server.port=$port", '--server.address=127.0.0.1')
$jarProcess = Start-Process -FilePath java -ArgumentList $arguments -WorkingDirectory $targetPath `
    -WindowStyle Hidden -PassThru `
    -RedirectStandardOutput (Join-Path $targetPath 'batch-3a-jar.stdout.log') `
    -RedirectStandardError (Join-Path $targetPath 'batch-3a-jar.stderr.log')

try {
    $baseUrl = "http://127.0.0.1:$port/api/feats"
    $summaries = $null
    $deadline = (Get-Date).AddSeconds(60)
    while ((Get-Date) -lt $deadline) {
        if ($jarProcess.HasExited) {
            throw 'Packaged application exited before readiness.'
        }
        try {
            $summaries = Invoke-RestMethod $baseUrl -TimeoutSec 2
            break
        } catch {
            Start-Sleep -Milliseconds 500
        }
    }
    if (@($summaries).Count -ne 314) {
        throw 'Packaged application did not return all 314 Feats.'
    }

    $owners = @($evidence.affectedRoots) + @($evidence.versions)
    foreach ($owner in $owners) {
        $matches = @($summaries | Where-Object {
            $_.name -ceq $owner.name -and $_.source.source -ceq $owner.source
        })
        if ($matches.Count -ne 1) {
            throw "Packaged owner identity changed: $($owner.name) | $($owner.source)"
        }
        $detail = Invoke-RestMethod "$baseUrl/$($matches[0].id)"
        $properties = @($detail.PSObject.Properties.Name)
        if ('mechanics' -in $properties -or 'entries' -in $properties) {
            throw "New internal content leaked for $($owner.name)"
        }
        if (($detail | ConvertTo-Json -Depth 80 -Compress).Contains('{@')) {
            throw "Raw markup leaked for $($owner.name)"
        }
    }

    $skilled = Invoke-RestMethod "$baseUrl/by-name/Skilled?source=PHB"
    if (@($skilled).Count -ne 1 -or $skilled[0].id -ne 'feat:phb:skilled') {
        throw 'Source-aware Feat lookup changed.'
    }
    $parent = Invoke-RestMethod "$baseUrl/by-name/Magic%20Initiate?source=XPHB"
    if (@($parent).Count -ne 1 -or $parent[0].id -ne 'feat:xphb:magic-initiate') {
        throw 'Version parent by-name lookup changed.'
    }
    # Existing HTTP security rejects encoded semicolons. Record that limit without changing it.
    $versionName = [System.Uri]::EscapeDataString('Magic Initiate; Cleric')
    $encodedVersionLookupStatus = 200
    try {
        $null = Invoke-RestMethod "$baseUrl/by-name/${versionName}?source=XPHB"
    } catch {
        if ($null -eq $_.Exception.Response -or [int]$_.Exception.Response.StatusCode -ne 403) {
            throw
        }
        $encodedVersionLookupStatus = 403
    }

    $result = [ordered]@{
        packagedJar = 'target/dnd-pub-data-0.0.1-SNAPSHOT.jar'
        workingDirectory = 'target'
        definitions = @($summaries).Count
        affectedRootsVerified = @($evidence.affectedRoots).Count
        versionsVerified = @($evidence.versions).Count
        noNewPublicMechanics = $true
        sourceAwareLookupVerified = $true
        versionIdLookupsVerified = $true
        versionParentByNameLookupVerified = $true
        encodedVersionByNameStatus = $encodedVersionLookupStatus
    }
    $json = $result | ConvertTo-Json
    $json | Set-Content -Encoding UTF8 (Join-Path $targetPath 'batch-3a-packaged-verification.json')
    $json
} finally {
    if (-not $jarProcess.HasExited) {
        Stop-Process -Id $jarProcess.Id
    }
}
