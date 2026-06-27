@echo off
setlocal enabledelayedexpansion

cd /d "%~dp0"

if exist ".env" (
    for /f "usebackq tokens=1,* delims==" %%a in (".env") do (
        set "env_name=%%a"
        if not "!env_name!"=="" if not "!env_name:~0,1!"=="#" (
            set "%%a=%%b"
        )
    )
    echo Environment variables loaded from .env
) else (
    echo .env file not found. Using local development defaults for this session.
)

if "%DATABASE_URL%"=="" set "DATABASE_URL=jdbc:postgresql://localhost:5432/devorbit_db"
if "%DATABASE_USERNAME%"=="" set "DATABASE_USERNAME=postgres"
if "%DATABASE_PASSWORD%"=="" (
    echo PostgreSQL password for %DATABASE_USERNAME%@%DATABASE_URL% is empty.
    set /p "DATABASE_PASSWORD=Enter PostgreSQL password (leave blank to try empty password): "
)
if "%JPA_DDL_AUTO%"=="" set "JPA_DDL_AUTO=update"
if "%JPA_SHOW_SQL%"=="" set "JPA_SHOW_SQL=false"
if "%JPA_FORMAT_SQL%"=="" set "JPA_FORMAT_SQL=false"
if "%SQL_INIT_MODE%"=="" set "SQL_INIT_MODE=never"
if "%SERVER_PORT%"=="" set "SERVER_PORT=8080"
if "%CORS_ALLOWED_ORIGINS%"=="" set "CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:5173,http://localhost:5174"
if "%JWT_SECRET%"=="" set "JWT_SECRET=devorbit-local-development-secret-key-change-before-production-2026"
if "%OPENCODE_API_URL%"=="" set "OPENCODE_API_URL=https://opencode.ai/zen/go/v1"
if "%OPENCODE_MODEL%"=="" set "OPENCODE_MODEL=deepseek-v4-flash"
if "%FIRECRAWL_ENABLED%"=="" set "FIRECRAWL_ENABLED=false"
if "%EMBEDDING_OFFLINE%"=="" set "EMBEDDING_OFFLINE=true"
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
echo EXA_ENABLED=%EXA_ENABLED%
echo EXA_API_URL=%EXA_API_URL%
if "%EXA_API_KEY%"=="" (
    echo WARNING: EXA_API_KEY is empty. Web search will fall back to Firecrawl or empty results.
) else (
    echo EXA_API_KEY loaded
)
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
echo Starting DevOrbit API with JVM tuning...
set MAVEN_OPTS=-Xms256m -Xmx512m -XX:+UseG1GC -XX:+ParallelRefProcEnabled -XX:MaxGCPauseMillis=200 -Xlog:gc*:file=target/gc.log:time,uptimemillis,tags

echo.

call .\mvnw.cmd spring-boot:run -DskipTests
echo.
echo DevOrbit API process exited. If this was unexpected, check the Spring Boot error above.

endlocal
