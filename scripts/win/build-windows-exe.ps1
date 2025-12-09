Param(
    [string]$AppVersion = "1.0.0"
)

$ErrorActionPreference = "Stop"

Write-Host "Ejecutando mvn clean package (crea JAR con dependencias)..." -ForegroundColor Cyan
mvn clean package

$repoRoot = Resolve-Path (Join-Path $PSScriptRoot "..\..")
$targetDir = Join-Path $repoRoot "target"
$fatJar = Join-Path $targetDir "integrales-generator-1.0-0-shaded.jar"
if (-not (Test-Path $fatJar)) {
    throw "No se encontró el JAR sombreado en $fatJar. Revisa el log de Maven."
}

$distDir = Join-Path $repoRoot "dist"
New-Item -ItemType Directory -Force -Path $distDir | Out-Null

Write-Host "Construyendo instalador .exe con jpackage..." -ForegroundColor Cyan
jpackage `
    --type exe `
    --name GeneradorIntegrales `
    --input $targetDir `
    --main-jar (Split-Path $fatJar -Leaf) `
    --main-class com.main.Main `
    --app-version $AppVersion `
    --dest $distDir

Write-Host "Listo. El instalador está en: $distDir" -ForegroundColor Green
