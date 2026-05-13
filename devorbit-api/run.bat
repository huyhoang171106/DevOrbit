@echo off
setlocal enabledelayedexpansion

cd /d "%~dp0"

if not exist ".env" (
    echo .env file not found.
    exit /b 1
)

for /f "usebackq tokens=1,* delims==" %%a in (".env") do (
    set "%%a=%%b"
)

echo Environment variables loaded from .env
echo Starting DevOrbit API...
echo.

.\mvnw.cmd spring-boot:run -Dmaven.test.skip=true

endlocal
