# PowerShell script for TextRPG on Windows

param(
    [string]$Command = "help"
)

function Show-Help {
    Write-Host "Available commands:" -ForegroundColor Cyan
    Write-Host "  .\run.ps1 build       - Build the application" -ForegroundColor Yellow
    Write-Host "  .\run.ps1 test        - Run tests" -ForegroundColor Yellow
    Write-Host "  .\run.ps1 run         - Run the application locally" -ForegroundColor Yellow
    Write-Host "  .\run.ps1 docker-up   - Start Docker containers" -ForegroundColor Yellow
    Write-Host "  .\run.ps1 docker-down - Stop Docker containers" -ForegroundColor Yellow
    Write-Host "  .\run.ps1 clean       - Clean build artifacts" -ForegroundColor Yellow
    Write-Host "  .\run.ps1 docker-build - Build Docker image" -ForegroundColor Yellow
    Write-Host "  .\run.ps1 setup       - Full setup (docker + build)" -ForegroundColor Yellow
}

function Build-Application {
    Write-Host "Building application..." -ForegroundColor Green
    .\gradlew.bat clean build -x test
}

function Run-Tests {
    Write-Host "Running tests..." -ForegroundColor Green
    .\gradlew.bat test
}

function Run-Application {
    Write-Host "Running application..." -ForegroundColor Green
    .\gradlew.bat bootRun
}

function Start-DockerContainers {
    Write-Host "Starting Docker containers..." -ForegroundColor Green
    docker-compose up -d
    Write-Host "Waiting for services to be ready..." -ForegroundColor Yellow
    Start-Sleep -Seconds 10
    Write-Host "Docker containers are ready!" -ForegroundColor Green
}

function Stop-DockerContainers {
    Write-Host "Stopping Docker containers..." -ForegroundColor Green
    docker-compose down
}

function Clean-Build {
    Write-Host "Cleaning build artifacts..." -ForegroundColor Green
    .\gradlew.bat clean
    if (Test-Path "build") {
        Remove-Item -Path "build" -Recurse -Force
    }
}

function Build-DockerImage {
    Write-Host "Building Docker image..." -ForegroundColor Green
    docker build -t textrpg:latest .
}

function Setup-Environment {
    Write-Host "Setting up environment..." -ForegroundColor Green
    Start-DockerContainers
    Build-Application
    Write-Host "Setup complete! Run '.\run.ps1 run' to start the application." -ForegroundColor Green
}

# Main script logic
switch ($Command) {
    "help" { Show-Help }
    "build" { Build-Application }
    "test" { Run-Tests }
    "run" { Run-Application }
    "docker-up" { Start-DockerContainers }
    "docker-down" { Stop-DockerContainers }
    "clean" { Clean-Build }
    "docker-build" { Build-DockerImage }
    "setup" { Setup-Environment }
    default { 
        Write-Host "Unknown command: $Command" -ForegroundColor Red
        Show-Help 
    }
}
