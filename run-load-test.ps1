# run-load-test.ps1
# Simulates a dogpile / cache stampede attack on the auction price endpoint.

$url = "http://localhost:8081/api/v1/auctions/auc-123/price"
$concurrency = 500

Write-Host "Simulating $concurrency concurrent users querying the auction price..."
Write-Host "Watch the Spring Boot logs! You should see exactly ONE log line for 'FETCHING PRICE FROM DB' thanks to Singleflight."

$jobs = @()
for ($i = 0; $i -lt $concurrency; $i++) {
    $jobs += Start-ThreadJob -ScriptBlock {
        param($u)
        Invoke-RestMethod -Uri $u -Method Get -ErrorAction SilentlyContinue
    } -ArgumentList $url
}

Receive-Job -Job $jobs -Wait | Out-Null
Remove-Job -Job $jobs

Write-Host "Load test complete!"
