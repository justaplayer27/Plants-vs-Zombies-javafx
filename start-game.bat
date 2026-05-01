@echo off
setlocal enabledelayedexpansion

set JAVAFX_HOME=C:\javafx-sdk

echo.
echo Plants vs Zombies Game
echo.

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

if not exist "bin" mkdir bin

echo Compiling source files...
echo.

javac --module-path "%JAVAFX_HOME%\lib" ^
      --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.media ^
      -d bin -cp bin ^
      src\main\java\com\pvz\entities\*.java ^
      src\main\java\com\pvz\entities\zombies\*.java ^
      src\main\java\com\pvz\game\*.java ^
      src\main\java\com\pvz\ui\*.java

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

java --enable-native-access=javafx.graphics ^
     --module-path "%JAVAFX_HOME%\lib" ^
     --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.media ^
     -cp bin com.pvz.game.PlantsVsZombiesApp

pause
