$ErrorActionPreference = 'Stop'
$settingsPath = Join-Path $PSScriptRoot '.env'

if (-not (Test-Path -LiteralPath $settingsPath)) {
    throw 'Create .env from .env.example and fill in your local settings.'
}

$allowedNames = @('OPENWEATHER_API_KEY', 'DB_URL', 'DB_USERNAME', 'DB_PASSWORD')
foreach ($line in [System.IO.File]::ReadAllLines($settingsPath)) {
    $entry = $line.Trim()
    if ($entry.Length -eq 0 -or $entry.StartsWith('#')) {
        continue
    }

    $separator = $entry.IndexOf('=')
    if ($separator -lt 1) {
        throw 'Each .env setting must use NAME=value.'
    }

    $name = $entry.Substring(0, $separator).Trim()
    if ($name -notin $allowedNames) {
        throw "Unknown .env setting: $name"
    }

    $value = $entry.Substring($separator + 1)
    [Environment]::SetEnvironmentVariable($name, $value, 'Process')
}

if ([string]::IsNullOrWhiteSpace($env:OPENWEATHER_API_KEY) -or $env:OPENWEATHER_API_KEY -like 'replace_with_*') {
    throw 'Add your OpenWeather API key to .env before starting the app.'
}
if ([string]::IsNullOrWhiteSpace($env:DB_USERNAME) -or $env:DB_USERNAME -like 'replace_with_*') {
    throw 'Add your MySQL username to .env before starting the app.'
}
if ($env:DB_PASSWORD -like 'replace_with_*') {
    throw 'Replace the sample MySQL password in .env or leave DB_PASSWORD blank.'
}

Push-Location (Join-Path $PSScriptRoot 'weathering\weathering')
try {
    & .\mvnw.cmd spring-boot:run
    if ($LASTEXITCODE -ne 0) {
        throw "Maven exited with code $LASTEXITCODE."
    }
} finally {
    Pop-Location
}
