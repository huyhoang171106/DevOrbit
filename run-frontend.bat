@echo off
setlocal

set FRONTEND_DIR=devorbit-web
if "%1"=="showcase" set FRONTEND_DIR=devorbit-showcase

echo Starting %FRONTEND_DIR%...
pushd "%~dp0%FRONTEND_DIR%"
call npm install
call npm run dev
popd

