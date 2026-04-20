@echo off
REM Setup JavaFX for Plants vs Zombies game

echo ========================================
echo Plants vs Zombies - JavaFX Setup
echo ========================================

REM Download JavaFX SDK
set JAVAFX_URL=https://gluonhq.com/download/javafx-21-windows-sdk/
set JAVAFX_ZIP=C:\javafx-sdk.zip
set JAVAFX_HOME=C:\javafx-sdk

echo.
echo Checking if JavaFX is already installed...
if exist "%JAVAFX_HOME%" (
    echo JavaFX SDK found at %JAVAFX_HOME%
    goto compile
)

echo.
echo JavaFX SDK not found. Please download it manually:
echo URL: https://gluonhq.com/products/javafx/
echo 1. Download the Windows SDK zip file
echo 2. Extract it to C:\javafx-sdk
echo 3. Run this script again
echo.
pause
exit /b 1

:compile
echo.
echo Building project with JavaFX...
echo.

if not exist "bin" mkdir bin

echo Compiling source files...
setlocal enabledelayedexpansion
for /r "src\main\java" %%f in (*.java) do (
    echo Compiling %%f
    javac --module-path "%JAVAFX_HOME%\lib" ^
           --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.media ^
           -d bin -cp bin "%%f"
    if errorlevel 1 (
        echo Error compiling %%f
        pause
        exit /b 1
    )
)

echo.
echo Build successful!
echo.
echo To run the game, execute:
echo   java --module-path "%JAVAFX_HOME%\lib" ^
echo        --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.media ^
echo        -cp bin com.pvz.game.PlantsVsZombiesApp
echo.
pause
