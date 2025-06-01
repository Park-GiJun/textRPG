@echo off
echo Quick Start - TextRPG Frontend
echo.

REM Check Node.js
node --version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Node.js is not installed!
    echo Please install Node.js from: https://nodejs.org
    pause
    exit /b 1
)

echo [OK] Node.js is installed

REM Install if needed
if not exist "node_modules" (
    echo [INFO] Installing dependencies...
    npm install --no-optional
    if errorlevel 1 (
        echo [ERROR] npm install failed!
        pause
        exit /b 1
    )
    echo [OK] Dependencies installed
) else (
    echo [OK] Dependencies already installed
)

echo.
echo Starting development server...
echo Frontend: http://localhost:3000
echo Backend should be running on: http://localhost:8080
echo.

npm run dev
