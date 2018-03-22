@echo off


set fileLocation=%1
set lic=%2

set line=license=
set temp=%line%%lic%
echo %temp%
REM ECHO %lic% >> %fileLocation%

REM ECHO %temp% >> %fileLocation%


