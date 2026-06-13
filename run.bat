@echo off
setlocal

echo ========================================
echo       DevOrbit - Start All Services
echo ========================================
echo.

set "ROOT=%~dp0"

echo [1/2] Starting DevOrbit API...
start "DevOrbit API" cmd /k "cd /d ""%ROOT%devorbit-api"" && run.bat"

echo [2/2] Starting DevOrbit Web...
start "DevOrbit Web" cmd /k "cd /d ""%ROOT%devorbit-web"" && npm run dev"

echo.
echo Both services launching in separate windows.
echo   API  -> http://localhost:8080
echo   Web  -> http://localhost:5173
echo.
echo Close each window to stop the corresponding service.
echo If a service exits early, its window will stay open with the error.
echo.

endlocal
