$ErrorActionPreference = 'Stop'

$projectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $projectRoot

$env:JAVA_HOME = 'C:\Program Files\Java\jdk-17'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"

$mvnLocal = Join-Path $projectRoot '.tools\apache-maven-3.9.9\bin\mvn.cmd'
if (-not (Test-Path $mvnLocal)) {
    Write-Host 'Local Maven not found at .tools\apache-maven-3.9.9\bin\mvn.cmd' -ForegroundColor Red
    Write-Host 'Please run setup first or install Maven globally.' -ForegroundColor Yellow
    exit 1
}

Write-Host 'Starting Spring Boot app on http://localhost:8080 ...' -ForegroundColor Cyan
& $mvnLocal spring-boot:run
