@echo off
setlocal

pushd "%~dp0devorbit-web"
npm install
npm run dev
popd
