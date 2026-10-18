$ErrorActionPreference = 'Stop'

# Run from the repository root. Only target/ receives downloaded documents and extracted prose.
$analysisDirectory = 'analysis/mechanics/public-v1/spells/p1b'
$cacheDirectory = 'target/p1b-official'
$manifest = Get-Content -LiteralPath "$analysisDirectory/official-documents.json" -Raw | ConvertFrom-Json
New-Item -ItemType Directory -Path $cacheDirectory -Force | Out-Null

function Get-VerifiedDownload {
    param(
        [string]$Url,
        [string]$Destination,
        [string]$ExpectedSha256
    )

    if (-not (Test-Path -LiteralPath $Destination)) {
        Invoke-WebRequest -Uri $Url -OutFile $Destination -UseBasicParsing
    }
    $actual = (Get-FileHash -LiteralPath $Destination -Algorithm SHA256).Hash.ToLowerInvariant()
    if ($actual -ne $ExpectedSha256) {
        throw "Fingerprint mismatch; review required before using $Destination"
    }
}

function Invoke-DocumentExtraction {
    param(
        [string]$PdfPath,
        [string]$OutputDirectory
    )

    # Windows PowerShell can treat native stderr font warnings as terminating errors.
    # Capture diagnostics separately and use the actual JVM exit code to decide success.
    $previousErrorAction = $ErrorActionPreference
    try {
        $ErrorActionPreference = 'Continue'
        & java -cp "$cacheDirectory;$toolPath" ExtractOfficialSrd $PdfPath $OutputDirectory `
            2> "$OutputDirectory-stderr.log"
        $extractionExit = $LASTEXITCODE
    } finally {
        $ErrorActionPreference = $previousErrorAction
    }
    if ($extractionExit -ne 0) {
        throw "PDF extraction failed for $PdfPath; see $OutputDirectory-stderr.log"
    }
}

$toolPath = "$cacheDirectory/pdfbox-app-3.0.4.jar"
Get-VerifiedDownload -Url $manifest.analysisTool.url -Destination $toolPath `
    -ExpectedSha256 $manifest.analysisTool.sha256

& javac -cp $toolPath -d $cacheDirectory "$analysisDirectory/ExtractOfficialSrd.java"
if ($LASTEXITCODE -ne 0) {
    throw 'Analysis PDF extractor compilation failed'
}

foreach ($document in $manifest.documents) {
    $pdfPath = "$cacheDirectory/srd-$($document.version).pdf"
    Get-VerifiedDownload -Url $document.url -Destination $pdfPath -ExpectedSha256 $document.sha256
    Invoke-DocumentExtraction -PdfPath $pdfPath -OutputDirectory "$cacheDirectory/$($document.version)"
}

foreach ($source in $manifest.supportingPrimarySources) {
    if (-not $source.cacheFile) {
        continue
    }
    $pdfPath = "$cacheDirectory/$($source.cacheFile)"
    Get-VerifiedDownload -Url $source.url -Destination $pdfPath -ExpectedSha256 $source.sha256
    Invoke-DocumentExtraction -PdfPath $pdfPath -OutputDirectory "$cacheDirectory/$($source.extractDirectory)"
}
