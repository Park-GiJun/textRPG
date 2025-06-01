# PowerShell script to fix TypeScript issues and start frontend

Write-Host "====================================" -ForegroundColor Cyan
Write-Host "   TextRPG Frontend 문제 해결      " -ForegroundColor Cyan
Write-Host "====================================" -ForegroundColor Cyan
Write-Host ""

# Stop existing processes
Write-Host "[1/6] 기존 프로세스 중지..." -ForegroundColor Yellow
Get-Process node -ErrorAction SilentlyContinue | Stop-Process -Force

# Clear caches
Write-Host "[2/6] 캐시 정리..." -ForegroundColor Yellow
if (Test-Path ".svelte-kit") { Remove-Item ".svelte-kit" -Recurse -Force }
if (Test-Path "node_modules\.vite") { Remove-Item "node_modules\.vite" -Recurse -Force }

# Reinstall dependencies (clean)
Write-Host "[3/6] 의존성 재설치..." -ForegroundColor Yellow
if (Test-Path "node_modules") { Remove-Item "node_modules" -Recurse -Force }
if (Test-Path "package-lock.json") { Remove-Item "package-lock.json" -Force }

npm install --no-optional

# Clear any TypeScript config cache
Write-Host "[4/6] TypeScript 캐시 정리..." -ForegroundColor Yellow
if (Test-Path "tsconfig.tsbuildinfo") { Remove-Item "tsconfig.tsbuildinfo" -Force }

# Fix the problematic file one more time
Write-Host "[5/6] 문제 파일 수정..." -ForegroundColor Yellow
$notificationContent = @'
<script>
  export let type = 'info';
  export let message = '';
  export let onClose = () => {};
  
  let bgColor, icon;
  
  if (type === 'success') {
    bgColor = 'bg-green-600';
    icon = '✓';
  } else if (type === 'error') {
    bgColor = 'bg-red-600';
    icon = '✗';
  } else {
    bgColor = 'bg-blue-600';
    icon = 'ℹ';
  }
</script>

<div class="fixed top-4 right-4 z-50">
  <div class="{bgColor} text-white px-6 py-4 rounded-lg shadow-lg flex items-center space-x-3 max-w-md">
    <span class="text-xl font-bold">{icon}</span>
    <span class="flex-1">{message}</span>
    <button class="text-white hover:text-gray-300 font-bold text-xl" on:click={onClose}>×</button>
  </div>
</div>
'@

$notificationContent | Out-File -FilePath "src\lib\components\Notification.svelte" -Encoding UTF8

Write-Host "[6/6] 개발 서버 시작..." -ForegroundColor Green
Write-Host ""
Write-Host "Frontend URL: http://localhost:3000" -ForegroundColor Yellow
Write-Host "서버를 중지하려면 Ctrl+C를 누르세요" -ForegroundColor Cyan
Write-Host ""

# Start with explicit no-typescript-check
$env:NODE_OPTIONS = "--max-old-space-size=4096"
npm run dev
