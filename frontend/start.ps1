# PowerShell script to start TextRPG Frontend

Write-Host "====================================" -ForegroundColor Cyan
Write-Host "   TextRPG Frontend 시작 중...    " -ForegroundColor Cyan
Write-Host "====================================" -ForegroundColor Cyan
Write-Host ""

# Check if Node.js is installed
try {
    $nodeVersion = node --version
    Write-Host "[✓] Node.js 버전: $nodeVersion" -ForegroundColor Green
} catch {
    Write-Host "[✗] Node.js가 설치되지 않았습니다" -ForegroundColor Red
    Write-Host "Node.js를 설치해주세요: https://nodejs.org" -ForegroundColor Yellow
    exit 1
}

# Check if dependencies are installed
if (-not (Test-Path "node_modules")) {
    Write-Host "[!] 의존성 설치 중..." -ForegroundColor Yellow
    npm install
    if ($LASTEXITCODE -ne 0) {
        Write-Host "[✗] 의존성 설치 실패" -ForegroundColor Red
        exit 1
    }
    Write-Host "[✓] 의존성 설치 완료" -ForegroundColor Green
} else {
    Write-Host "[✓] 의존성이 이미 설치되어 있습니다" -ForegroundColor Green
}

Write-Host ""
Write-Host "====================================" -ForegroundColor Green
Write-Host "   개발 서버 시작 중...           " -ForegroundColor Green
Write-Host "====================================" -ForegroundColor Green
Write-Host ""
Write-Host "Frontend URL: http://localhost:3000" -ForegroundColor Yellow
Write-Host "Backend API:  http://localhost:8080/api" -ForegroundColor Yellow
Write-Host ""
Write-Host "서버를 중지하려면 Ctrl+C를 누르세요" -ForegroundColor Cyan
Write-Host ""

# Start development server
npm run dev
