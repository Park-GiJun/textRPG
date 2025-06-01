@echo off
echo Simple Start - TextRPG Frontend
echo.

REM Kill existing processes
taskkill /f /im node.exe 2>nul

REM Clear problematic cache
if exist ".svelte-kit" (
    echo Clearing SvelteKit cache...
    rmdir /s /q ".svelte-kit"
)

REM Start without TypeScript checking
echo Starting without strict type checking...
set NODE_OPTIONS=--max-old-space-size=4096
npm run dev -- --host 0.0.0.0
