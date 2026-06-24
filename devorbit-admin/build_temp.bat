@echo off
set JAVA_HOME=C:\Program Files\Android\Android Studio\jbr
set ANDROID_HOME=C:\Users\Hoang\AppData\Local\Android\Sdk
pushd D:\temp\devorbit\devorbit-admin
call gradlew.bat %*
popd
