@echo off
setlocal

cd /d "%~dp0"

set "WORK_DIR=work"

if not exist "%WORK_DIR%" mkdir "%WORK_DIR%"
if not exist "%WORK_DIR%\input" mkdir "%WORK_DIR%\input"
if not exist "%WORK_DIR%\output" mkdir "%WORK_DIR%\output"
if not exist "%WORK_DIR%\quality" mkdir "%WORK_DIR%\quality"
if not exist "%WORK_DIR%\req_copy" mkdir "%WORK_DIR%\req_copy"
if not exist "%WORK_DIR%\revise" mkdir "%WORK_DIR%\revise"

echo Work directories are ready:
echo   %CD%\%WORK_DIR%\input
echo   %CD%\%WORK_DIR%\output
echo   %CD%\%WORK_DIR%\quality
echo   %CD%\%WORK_DIR%\req_copy
echo   %CD%\%WORK_DIR%\revise

endlocal
