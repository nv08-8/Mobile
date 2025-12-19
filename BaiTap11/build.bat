@echo off
echo Building ToDoList App...
echo.
cd /d "%~dp0"
call gradlew.bat clean
call gradlew.bat assembleDebug
if %ERRORLEVEL% EQU 0 (
    echo.
    echo BUILD SUCCESSFUL!
    echo APK Location: app\build\outputs\apk\debug\app-debug.apk
) else (
    echo.
    echo BUILD FAILED!
    echo Check the error messages above.
)
pause

