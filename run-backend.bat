@echo off
setlocal

pushd "%~dp0devorbit-api"
call .\mvnw.cmd spring-boot:run
popd
