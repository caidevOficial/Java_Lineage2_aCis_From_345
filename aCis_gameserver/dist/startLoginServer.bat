@echo off
title aCis loginserver console
:start
java -Xmx128m -cp ./libs/*; net.sf.l2j.loginserver.L2LoginServer
if ERRORLEVEL 2 goto restart
if ERRORLEVEL 1 goto error
goto end
:restart
echo.
echo Admin have restarted, please wait.
echo.
goto start
:error
echo.
echo Server have terminated abnormaly.
echo.
:end
echo.
echo Server terminated.
echo.
pause
