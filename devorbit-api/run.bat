@echo off
setlocal enabledelayedexpansion

cd /d "%~dp0"

if not exist ".env" (
    echo .env file not found.
    exit /b 1
)

for /f "usebackq tokens=1,* delims==" %%a in (".env") do (
    set "env_name=%%a"
    if not "!env_name!"=="" if not "!env_name:~0,1!"=="#" (
        set "%%a=%%b"
    )
)

echo Environment variables loaded from .env
if "%EMBEDDING_OFFLINE%"=="" set "EMBEDDING_OFFLINE=false"
if "%EMBEDDING_PROVIDER%"=="" set "EMBEDDING_PROVIDER=fireworks"
if "%EMBEDDING_MODEL%"=="" set "EMBEDDING_MODEL=accounts/fireworks/models/qwen3-embedding-8b"
if "%EMBEDDING_DIMENSIONS%"=="" set "EMBEDDING_DIMENSIONS=4096"

if "%OPENCODE_API_KEY%"=="" (
    echo WARNING: OPENCODE_API_KEY is empty. AI chat will use offline fallback.
) else (
    set "key_len=0"
    for /l %%i in (0,1,4096) do (
        if not "!OPENCODE_API_KEY:~%%i,1!"=="" set /a key_len=%%i+1
    )
    echo OPENCODE_API_KEY loaded ^(length: !key_len! chars^)
)
echo OPENCODE_API_URL=%OPENCODE_API_URL%
echo OPENCODE_MODEL=%OPENCODE_MODEL%
echo FIRECRAWL_ENABLED=%FIRECRAWL_ENABLED%
if "%FIRECRAWL_API_KEY%"=="" (
    echo WARNING: FIRECRAWL_API_KEY is empty. Firecrawl scraping will fail.
) else (
    echo FIRECRAWL_API_KEY loaded
)
echo EMBEDDING_OFFLINE=%EMBEDDING_OFFLINE%
echo EMBEDDING_PROVIDER=%EMBEDDING_PROVIDER%
echo EMBEDDING_MODEL=%EMBEDDING_MODEL%
echo EMBEDDING_DIMENSIONS=%EMBEDDING_DIMENSIONS%
if "%FIREWORKS_API_KEY%"=="" (
    echo WARNING: FIREWORKS_API_KEY is empty. Fireworks embeddings will fail.
) else (
    echo FIREWORKS_API_KEY loaded
)
echo Starting DevOrbit API...
echo.

.\mvnw.cmd spring-boot:run -Dmaven.test.skip=true

endlocal
