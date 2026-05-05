$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$frontend = Join-Path $root "frontend"
$backend = Join-Path $root "backend"
$backendStatic = Join-Path $backend "src\main\resources\static"

Write-Host "== Frontend install/build =="
Push-Location $frontend
if (Test-Path "package-lock.json") {
  npm ci
} else {
  npm install
}
npm run build
Pop-Location

Write-Host "== Copy frontend dist -> backend static =="
if (Test-Path $backendStatic) { Remove-Item $backendStatic -Recurse -Force }
New-Item -ItemType Directory -Force -Path $backendStatic | Out-Null
Copy-Item -Path (Join-Path $frontend "dist\*") -Destination $backendStatic -Recurse -Force

Write-Host "== Backend package (Spring Boot JAR) =="
Push-Location $backend
if (Test-Path ".\mvnw.cmd") {
  .\mvnw.cmd -DskipTests package
} else {
  mvn -DskipTests package
}
Pop-Location

Write-Host ""
Write-Host "Done. WAR/JAR artifacts are in backend\target\"

