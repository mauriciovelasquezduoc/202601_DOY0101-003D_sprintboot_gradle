@echo off
set DIR=%~dp0
if exist "%DIR%\gradle\wrapper\gradle-wrapper.jar" (
  java -jar "%DIR%\gradle\wrapper\gradle-wrapper.jar" %*
) else (
  gradle %*
)
