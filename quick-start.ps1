# Quick Start Script for Windows PowerShell
# This script helps you quickly start the TextRPG application

Write-Host "====================================" -ForegroundColor Cyan
Write-Host "   TextRPG Quick Start for Windows  " -ForegroundColor Cyan
Write-Host "====================================" -ForegroundColor Cyan
Write-Host ""

# Check if Docker is installed
try {
    docker --version | Out-Null
    Write-Host "[✓] Docker is installed" -ForegroundColor Green
} catch {
    Write-Host "[✗] Docker is not installed or not in PATH" -ForegroundColor Red
    Write-Host "Please install Docker Desktop from: https://www.docker.com/products/docker-desktop" -ForegroundColor Yellow
    exit 1
}

# Check if Docker is running
try {
    docker ps | Out-Null
    Write-Host "[✓] Docker is running" -ForegroundColor Green
} catch {
    Write-Host "[✗] Docker is not running" -ForegroundColor Red
    Write-Host "Please start Docker Desktop" -ForegroundColor Yellow
    exit 1
}

# Check if Java is installed
try {
    java -version 2>&1 | Out-Null
    Write-Host "[✓] Java is installed" -ForegroundColor Green
} catch {
    Write-Host "[✗] Java is not installed" -ForegroundColor Red
    Write-Host "Please install Java 21 or higher" -ForegroundColor Yellow
    exit 1
}

Write-Host ""
Write-Host "Starting services..." -ForegroundColor Yellow
Write-Host ""

# Start Docker containers
Write-Host "1. Starting Docker containers..." -ForegroundColor Cyan
docker-compose up -d

if ($LASTEXITCODE -eq 0) {
    Write-Host "   [✓] Docker containers started successfully" -ForegroundColor Green
} else {
    Write-Host "   [✗] Failed to start Docker containers" -ForegroundColor Red
    exit 1
}

# Create logs directory
Write-Host "2. Creating logs directory..." -ForegroundColor Cyan
if (-not (Test-Path "logs")) {
    New-Item -ItemType Directory -Path "logs" | Out-Null
    Write-Host "   [✓] Logs directory created" -ForegroundColor Green
} else {
    Write-Host "   [✓] Logs directory already exists" -ForegroundColor Green
}

# Wait for services
Write-Host "3. Waiting for services to be ready..." -ForegroundColor Cyan
$services = @{
    "MySQL" = @{Port = 3306; Host = "localhost"}
    "Redis" = @{Port = 6379; Host = "localhost"}
    "Elasticsearch" = @{Port = 9200; Host = "localhost"}
    "Kafka" = @{Port = 9092; Host = "localhost"}
}

foreach ($service in $services.Keys) {
    Write-Host "   Checking $service..." -NoNewline
    $port = $services[$service].Port
    $connected = $false
    
    for ($i = 0; $i -lt 30; $i++) {
        try {
            $tcp = New-Object System.Net.Sockets.TcpClient
            $tcp.Connect("localhost", $port)
            $tcp.Close()
            $connected = $true
            break
        } catch {
            Start-Sleep -Seconds 1
        }
    }
    
    if ($connected) {
        Write-Host " [✓]" -ForegroundColor Green
    } else {
        Write-Host " [✗]" -ForegroundColor Red
        Write-Host "   Failed to connect to $service on port $port" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "4. Building application..." -ForegroundColor Cyan
.\gradlew.bat build -x test

if ($LASTEXITCODE -eq 0) {
    Write-Host "   [✓] Application built successfully" -ForegroundColor Green
} else {
    Write-Host "   [✗] Failed to build application" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "====================================" -ForegroundColor Green
Write-Host "    Setup completed successfully!   " -ForegroundColor Green
Write-Host "====================================" -ForegroundColor Green
Write-Host ""
Write-Host "You can now:" -ForegroundColor Cyan
Write-Host "  • Run the application: .\run.ps1 run" -ForegroundColor Yellow
Write-Host "  • View API docs: http://localhost:8080/swagger-ui.html" -ForegroundColor Yellow
Write-Host "  • Stop services: .\run.ps1 docker-down" -ForegroundColor Yellow
Write-Host ""
Write-Host "Happy coding! 🚀" -ForegroundColor Magenta
