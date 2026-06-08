@echo off
setlocal enabledelayedexpansion

echo ========================================
echo       DevOrbit — Start All Services
echo ========================================
echo.

rem ─── Resolve root directory ────
set "ROOT=%~dp0"

rem ─── Load API .env ────
echo [1/2] Loading DevOrbit API environment...
if exist "%ROOT%devorbit-api\.env" (
    for /f "tokens=1,* delims==" %%a in (%ROOT%devorbit-api\.env) do (
        set "%%a=%%b"
    )
    echo   Loaded .env from devorbit-api
) else (
    echo WARNING: devorbit-api\.env not found. API may fail.
)

echo [1/2] Starting DevOrbit API...
start "DevOrbit API" cmd /c "cd /d %ROOT%devorbit-api && .\mvnw.cmd spring-boot:run -Dmaven.test.skip=true"

rem ─── Start Web ────
echo [2/2] Starting DevOrbit Web...
start "DevOrbit Web" cmd /c "cd /d %ROOT%devorbit-web && npm run dev"

echo.
echo Both services launching in separate windows.
echo   API  → http://localhost:8080
echo   Web  → http://localhost:5173
echo.
echo Close each window to stop the corresponding service.
echo.

endlocal
