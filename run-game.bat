@echo off
REM Run Plants vs Zombies game with JavaFX

set JAVAFX_HOME=C:\javafx-sdk

if not exist "%JAVAFX_HOME%" (
    echo Error: JavaFX SDK not found at %JAVAFX_HOME%
    echo Please run setup-javafx.bat first
    pause
    exit /b 1
)

if not exist "bin" (
    echo Error: bin directory not found. Please compile first using setup-javafx.bat
    pause
    exit /b 1
)

echo Starting Plants vs Zombies...
java --module-path "%JAVAFX_HOME%\lib" ^
     --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.media ^
     -cp bin com.pvz.game.PlantsVsZombiesApp

pause
