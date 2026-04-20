@echo off
REM Simple Plants vs Zombies Game Launcher
REM Step 1: Download JavaFX SDK from: https://gluonhq.com/products/javafx/
REM Step 2: Extract to: C:\javafx-sdk
REM Step 3: Run this file

setlocal enabledelayedexpansion

set JAVAFX_HOME=C:\javafx-sdk

echo.
echo ========================================
echo Plants vs Zombies Game
echo ========================================
echo.

REM Check if JavaFX is installed
if not exist "%JAVAFX_HOME%" (
    echo ERROR: JavaFX SDK not found at %JAVAFX_HOME%
    echo.
    echo SETUP INSTRUCTIONS:
    echo 1. Download JavaFX Windows SDK from:
    echo    https://gluonhq.com/products/javafx/
    echo.
    echo 2. Extract the ZIP file to: C:\javafx-sdk
    echo.
    echo 3. Run this script again
    echo.
    pause
    exit /b 1
)

REM Create bin directory
if not exist "bin" mkdir bin

REM Compile all Java files
echo Compiling source files...
echo.

javac --module-path "%JAVAFX_HOME%\lib" ^
      --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.media ^
      -d bin -cp bin ^
      src/main/java/com/pvz/entities/*.java ^
      src/main/java/com/pvz/game/*.java ^
      src/main/java/com/pvz/ui/*.java

if %ERRORLEVEL% neq 0 (
    echo.
    echo ERROR: Compilation failed!
    pause
    exit /b 1
)

echo.
echo Compilation successful!
echo.
echo Starting game...
echo.

REM Run the game
java --enable-native-access=javafx.graphics ^
     --module-path "%JAVAFX_HOME%\lib" ^
     --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.media ^
     -cp bin com.pvz.game.PlantsVsZombiesApp

REM Pause so window doesn't close immediately
pause
