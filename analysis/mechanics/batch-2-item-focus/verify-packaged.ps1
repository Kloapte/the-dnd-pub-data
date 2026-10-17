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
    -RedirectStandardOutput (Join-Path $targetPath 'batch-2-jar.stdout.log') `
    -RedirectStandardError (Join-Path $targetPath 'batch-2-jar.stderr.log')

try {
    $baseUrl = "http://127.0.0.1:$port/api"
    $tool = $null
    $deadline = (Get-Date).AddSeconds(60)
    while ((Get-Date) -lt $deadline) {
        if ($jarProcess.HasExited) {
            throw 'Packaged application exited before readiness.'
        }
        try {
            $tool = Invoke-RestMethod "$baseUrl/items/item:tce:1-all-purpose-tool" -TimeoutSec 2
            break
        } catch {
            Start-Sleep -Milliseconds 500
        }
    }
    if ($null -eq $tool) {
        throw 'Packaged application did not become ready.'
    }

    $arrayItems = @(
        'item:tce:1-all-purpose-tool',
        'item:ftd:slumbering-dragon-touched-focus',
        'item:ftd:stirring-dragon-touched-focus',
        'item:ftd:wakened-dragon-touched-focus',
        'item:ftd:ascendant-dragon-touched-focus'
    )
    foreach ($id in $arrayItems) {
        $item = Invoke-RestMethod "$baseUrl/items/$id"
        if ($item.PSObject.Properties.Name -contains 'focus' `
                -or $item.PSObject.Properties.Name -contains 'focusEnabled') {
            throw "New focus information leaked through the public projection: $id"
        }
        if ($item.classifications -notcontains 'Artificer') {
            throw "Legacy classification metadata changed: $id"
        }
    }
    foreach ($sourceCode in @('xge', 'xdmg')) {
        $ruby = Invoke-RestMethod "$baseUrl/items/item:${sourceCode}:ruby-of-the-war-mage"
        if ($ruby.focusEnabled -ne $true -or $ruby.PSObject.Properties.Name -contains 'focus') {
            throw 'Existing Boolean focus compatibility changed.'
        }
    }
    $matches = Invoke-RestMethod "$baseUrl/items/by-name/Ascendant%20Dragon-Touched%20Focus?source=FTD"
    if (@($matches).Count -ne 1 -or $matches[0].id -ne 'item:ftd:ascendant-dragon-touched-focus') {
        throw 'Copy-effective Item by-name lookup changed.'
    }

    $items = Invoke-RestMethod "$baseUrl/items"
    $variants = Invoke-RestMethod "$baseUrl/magic-variants"
    if (@($items).Count -ne 2742 -or @($variants).Count -ne 230) {
        throw 'Packaged catalog counts changed.'
    }

    $result = [ordered]@{
        packagedJar = 'target/dnd-pub-data-0.0.1-SNAPSHOT.jar'
        workingDirectory = 'target'
        itemDefinitions = @($items).Count
        magicVariantDefinitions = @($variants).Count
        copyEffectiveItemLookupsVerified = $true
        booleanCompatibilityVerified = $true
        noNewPublicFocusField = $true
        legacyClassificationsVerified = $true
    }
    $json = $result | ConvertTo-Json
    $json | Set-Content -Encoding UTF8 (Join-Path $targetPath 'batch-2-packaged-verification.json')
    $json
} finally {
    if (-not $jarProcess.HasExited) {
        Stop-Process -Id $jarProcess.Id
    }
}
