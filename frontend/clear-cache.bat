@echo off
echo Clearing Vite cache and restarting...

REM Stop any running processes
taskkill /f /im node.exe 2>nul

REM Clear caches
if exist ".svelte-kit" rmdir /s /q ".svelte-kit"
if exist "node_modules/.vite" rmdir /s /q "node_modules/.vite"
if exist "node_modules/.cache" rmdir /s /q "node_modules/.cache"

echo Cache cleared. Starting fresh...
echo.

npm run dev
