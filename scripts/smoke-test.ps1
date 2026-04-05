$ErrorActionPreference = 'Stop'

function Invoke-Test {
    param(
        [string]$Name,
        [string]$Method,
        [string]$Url,
        [hashtable]$Headers,
        [string]$Body,
        [int]$Expected
    )

    try {
        if ($Body) {
            $resp = Invoke-WebRequest -Uri $Url -Method $Method -Headers $Headers -Body $Body -ContentType 'application/json' -UseBasicParsing
        }
        else {
            $resp = Invoke-WebRequest -Uri $Url -Method $Method -Headers $Headers -UseBasicParsing
        }

        $code = [int]$resp.StatusCode
        if ($code -eq $Expected) {
            Write-Output "PASS | $Name | status=$code"
        }
        else {
            Write-Output "FAIL | $Name | expected=$Expected actual=$code"
        }

        return @{ ok = ($code -eq $Expected); status = $code; body = $resp.Content }
    }
    catch {
        $resp = $_.Exception.Response
        if ($null -ne $resp) {
            $code = [int]$resp.StatusCode
            $stream = $resp.GetResponseStream()
            $reader = New-Object System.IO.StreamReader($stream)
            $content = $reader.ReadToEnd()

            if ($code -eq $Expected) {
                Write-Output "PASS | $Name | status=$code"
            }
            else {
                Write-Output "FAIL | $Name | expected=$Expected actual=$code body=$content"
            }

            return @{ ok = ($code -eq $Expected); status = $code; body = $content }
        }

        Write-Output "FAIL | $Name | exception=$($_.Exception.Message)"
        return @{ ok = $false; status = -1; body = '' }
    }
}

$base = 'http://localhost:8080'
$admin = @{ 'X-User-Id' = '1' }
$analyst = @{ 'X-User-Id' = '2' }
$viewer = @{ 'X-User-Id' = '3' }

$r1 = Invoke-Test -Name 'Health check' -Method GET -Url "$base/api/health" -Headers @{} -Expected 200
$r2 = Invoke-Test -Name 'Missing auth denied' -Method GET -Url "$base/api/records" -Headers @{} -Expected 401
$r3 = Invoke-Test -Name 'Viewer can list records' -Method GET -Url "$base/api/records?page=0&size=5" -Headers $viewer -Expected 200
$r4 = Invoke-Test -Name 'Viewer cannot create record' -Method POST -Url "$base/api/records" -Headers $viewer -Body '{"amount":99.99,"type":"EXPENSE","category":"ForbiddenTry","date":"2026-04-05","notes":"nope"}' -Expected 403
$r5 = Invoke-Test -Name 'Admin create record' -Method POST -Url "$base/api/records" -Headers $admin -Body '{"amount":199.99,"type":"EXPENSE","category":"Travel","date":"2026-04-05","notes":"cab"}' -Expected 201

$createdId = $null
if ($r5.status -eq 201 -and $r5.body) {
    try { $createdId = (ConvertFrom-Json $r5.body).id } catch {}
}

if ($createdId) {
    $r6 = Invoke-Test -Name 'Admin update record' -Method PUT -Url "$base/api/records/$createdId" -Headers $admin -Body '{"notes":"cab and bus"}' -Expected 200
    $r7 = Invoke-Test -Name 'Admin soft delete record' -Method DELETE -Url "$base/api/records/$createdId" -Headers $admin -Expected 204
    $r8 = Invoke-Test -Name 'Deleted record not found' -Method GET -Url "$base/api/records/$createdId" -Headers $admin -Expected 404
}
else {
    Write-Output 'FAIL | Record update/delete sequence | create did not return id'
}

$r9 = Invoke-Test -Name 'Validation invalid amount' -Method POST -Url "$base/api/records" -Headers $admin -Body '{"amount":0,"type":"EXPENSE","category":"Invalid","date":"2026-04-05","notes":"bad"}' -Expected 400
$r10 = Invoke-Test -Name 'Analyst dashboard summary' -Method GET -Url "$base/api/dashboard/summary?from=2026-01-01&to=2026-12-31" -Headers $analyst -Expected 200
$r11 = Invoke-Test -Name 'Viewer blocked from users list' -Method GET -Url "$base/api/users" -Headers $viewer -Expected 403
$r12 = Invoke-Test -Name 'Admin users list' -Method GET -Url "$base/api/users" -Headers $admin -Expected 200
$r13 = Invoke-Test -Name 'Any active role can hit me endpoint' -Method GET -Url "$base/api/users/me" -Headers $viewer -Expected 200
