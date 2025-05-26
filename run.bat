@echo off
REM Batch script for TextRPG on Windows

if "%1"=="" goto help
if "%1"=="help" goto help
if "%1"=="build" goto build
if "%1"=="test" goto test
if "%1"=="run" goto run
if "%1"=="docker-up" goto docker-up
if "%1"=="docker-down" goto docker-down
if "%1"=="clean" goto clean
if "%1"=="docker-build" goto docker-build
if "%1"=="setup" goto setup

echo Unknown command: %1
goto help

:help
echo Available commands:
echo   run.bat build       - Build the application
echo   run.bat test        - Run tests
echo   run.bat run         - Run the application locally
echo   run.bat docker-up   - Start Docker containers
echo   run.bat docker-down - Stop Docker containers
echo   run.bat clean       - Clean build artifacts
echo   run.bat docker-build - Build Docker image
echo   run.bat setup       - Full setup (docker + build)
goto end

:build
echo Building application...
call gradlew.bat clean build -x test
goto end

:test
echo Running tests...
call gradlew.bat test
goto end

:run
echo Running application...
call gradlew.bat bootRun
goto end

:docker-up
echo Starting Docker containers...
docker-compose up -d
echo Waiting for services to be ready...
timeout /t 10 /nobreak > nul
echo Docker containers are ready!
goto end

:docker-down
echo Stopping Docker containers...
docker-compose down
goto end

:clean
echo Cleaning build artifacts...
call gradlew.bat clean
if exist build rmdir /s /q build
goto end

:docker-build
echo Building Docker image...
docker build -t textrpg:latest .
goto end

:setup
echo Setting up environment...
call :docker-up
call :build
echo Setup complete! Run 'run.bat run' to start the application.
goto end

:end
