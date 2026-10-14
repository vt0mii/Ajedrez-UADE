# Build and run tests script

Write-Host "Compiling main sources..." -ForegroundColor Cyan
$mainFiles = Get-ChildItem -Path "src\main\java" -Recurse -Filter "*.java" | ForEach-Object { $_.FullName }
if ($mainFiles.Count -eq 0) {
    Write-Host "Error: No source files found!" -ForegroundColor Red
    exit 1
}
javac -d bin $mainFiles
if ($LASTEXITCODE -ne 0) {
    Write-Host "Compilation failed!" -ForegroundColor Red
    exit 1
}
Write-Host "Main sources compiled successfully!" -ForegroundColor Green

Write-Host "`nCompiling test sources..." -ForegroundColor Cyan
$testFiles = Get-ChildItem -Path "src\test\java" -Recurse -Filter "*.java" | ForEach-Object { $_.FullName }
if ($testFiles.Count -gt 0) {
    javac -cp bin -d bin $testFiles
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Test compilation failed!" -ForegroundColor Red
        exit 1
    }
    Write-Host "Test sources compiled successfully!" -ForegroundColor Green
    
    Write-Host "`nRunning tests..." -ForegroundColor Cyan
    java -ea -cp bin com.uade.ajedrez.AllTests
} else {
    Write-Host "No test files found. Skipping tests." -ForegroundColor Yellow
}
