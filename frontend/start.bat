@echo off
echo Starting TextRPG Frontend...
echo.

REM Check if node_modules exists
if not exist "node_modules" (
    echo Installing dependencies...
    npm install
    echo.
)

echo Starting development server...
echo Frontend will be available at: http://localhost:3000
echo Backend API proxy: http://localhost:8080/api
echo.

npm run dev
