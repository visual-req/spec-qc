@echo off
setlocal

cd /d "%~dp0"

if "%~1"=="" (
  java -jar "spec-qc-0.1.0.jar" web
) else (
  java -jar "spec-qc-0.1.0.jar" %*
)
