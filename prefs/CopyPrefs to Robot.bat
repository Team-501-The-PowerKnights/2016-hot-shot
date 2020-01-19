@echo off

echo.

REM set ROBOT=stubot
REM set ROBOT=woodbot
set ROBOT=robot
echo ROBOT=%ROBOT%

set PATH=C:\Program Files (x86)\PuTTY;%PATH%
REM echo %PATH%

set PREFSFILE=networktables.ini
REM echo %PREFSFILE%

cd C:\F_FIRST\GitRepos\2016repos\%ROBOT%_2016\prefs
dir %PREFSFILE%

echo.

echo Copy will take several seconds. Wait until it completes.
echo.
pscp %PREFSFILE% lvuser@roboRIO-501-FRC.local:/home/lvuser
echo.

PAUSE