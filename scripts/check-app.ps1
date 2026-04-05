$ErrorActionPreference = 'Stop'

function Check {
    param(
        [string]$Name,
        [scriptblock]$Run
    )

    try {
        & $Run
        Write-Host "PASS  $Name" -ForegroundColor Green
    }
    catch {
        Write-Host "FAIL  $Name  -> $($_.Exception.Message)" -ForegroundColor Red
    }
}

$base = 'http://localhost:8080'

Check 'Health endpoint' {
    $r = Invoke-WebRequest -Uri "$base/api/health" -UseBasicParsing
    if ($r.StatusCode -ne 200) { throw 'Expected 200' }
}

Check 'Unauthorized request blocked' {
    try {
        Invoke-WebRequest -Uri "$base/api/records" -UseBasicParsing | Out-Null
        throw 'Expected 401 but request succeeded'
    }
    catch {
        if (-not $_.Exception.Response) { throw }
        if ([int]$_.Exception.Response.StatusCode -ne 401) { throw "Expected 401 got $([int]$_.Exception.Response.StatusCode)" }
    }
}

Check 'Viewer can read records' {
    $h = @{ 'X-User-Id' = '3' }
    $r = Invoke-WebRequest -Uri "$base/api/records?page=0&size=3" -Headers $h -UseBasicParsing
    if ($r.StatusCode -ne 200) { throw 'Expected 200' }
}

Check 'Viewer cannot create records' {
    $h = @{ 'X-User-Id' = '3'; 'Content-Type' = 'application/json' }
    $body = '{"amount":99.99,"type":"EXPENSE","category":"DeniedTry","date":"2026-04-05","notes":"blocked"}'
    try {
        Invoke-WebRequest -Uri "$base/api/records" -Method POST -Headers $h -Body $body -UseBasicParsing | Out-Null
        throw 'Expected 403 but request succeeded'
    }
    catch {
        if (-not $_.Exception.Response) { throw }
        if ([int]$_.Exception.Response.StatusCode -ne 403) { throw "Expected 403 got $([int]$_.Exception.Response.StatusCode)" }
    }
}

Check 'Analyst dashboard summary works' {
    $h = @{ 'X-User-Id' = '2' }
    $r = Invoke-WebRequest -Uri "$base/api/dashboard/summary?from=2026-01-01&to=2026-12-31" -Headers $h -UseBasicParsing
    if ($r.StatusCode -ne 200) { throw 'Expected 200' }
}

Check 'Admin users list works' {
    $h = @{ 'X-User-Id' = '1' }
    $r = Invoke-WebRequest -Uri "$base/api/users" -Headers $h -UseBasicParsing
    if ($r.StatusCode -ne 200) { throw 'Expected 200' }
}

Write-Host ''
Write-Host 'Done. If all lines are PASS, your backend is working.' -ForegroundColor Cyan
