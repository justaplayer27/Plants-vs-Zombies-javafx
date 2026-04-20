@echo off
REM Plants vs Zombies Game Launcher
REM Compiles and runs the game using Java

javac GameLauncher.java

if %ERRORLEVEL% neq 0 (
    echo Error: Failed to compile GameLauncher
    pause
    exit /b 1
)

java GameLauncher
pause
