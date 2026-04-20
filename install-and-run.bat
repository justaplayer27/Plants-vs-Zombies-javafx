@echo off
REM Automated JavaFX SDK Download and Installation

setlocal enabledelayedexpansion

set JAVAFX_HOME=C:\javafx-sdk
set TEMP_ZIP=C:\javafx-temp.zip

echo ========================================
echo JavaFX SDK Installer
echo ========================================

if exist "%JAVAFX_HOME%" (
    echo JavaFX SDK already installed at %JAVAFX_HOME%
    goto compile
)

echo.
echo Downloading JavaFX SDK (this may take a minute)...
echo.

REM Try multiple download methods
powershell -NoProfile -Command "^
    $ProgressPreference = 'SilentlyContinue'; ^
    $urls = @( ^
        'https://download2.gluonhq.com/openjfx/21.0.2/openjfx-21.0.2-windows-x64-sdk.zip', ^
        'https://gluonhq.com/download/javafx-21.0.2-windows-sdk/' ^
    ); ^
    foreach ($url in $urls) { ^
        try { ^
            Write-Host 'Trying: ' $url; ^
            Invoke-WebRequest -Uri $url -OutFile '%TEMP_ZIP%' -UseBasicParsing -TimeoutSec 60; ^
            Write-Host 'Download successful!'; ^
            break; ^
        } catch { ^
            Write-Host 'Failed, trying next source...'; ^
        } ^
    }; ^
    if (Test-Path '%TEMP_ZIP%') { ^
        Write-Host 'Extracting...'; ^
        Expand-Archive -Path '%TEMP_ZIP%' -DestinationPath 'C:\' -Force; ^
        if (Test-Path 'C:\javafx-sdk-21.0.2') { ^
            Rename-Item -Path 'C:\javafx-sdk-21.0.2' -NewName '%JAVAFX_HOME%' -Force; ^
        } ^
        Remove-Item '%TEMP_ZIP%' -ErrorAction SilentlyContinue; ^
        Write-Host 'Installation complete!'; ^
    } ^
"

if not exist "%JAVAFX_HOME%" (
    echo.
    echo ERROR: JavaFX SDK installation failed.
    echo.
    echo Please download manually:
    echo 1. Visit: https://gluonhq.com/products/javafx/
    echo 2. Download Windows SDK
    echo 3. Extract to: C:\javafx-sdk
    echo 4. Run this script again
    echo.
    pause
    exit /b 1
)

:compile
echo.
echo ========================================
echo Compiling Plants vs Zombies...
echo ========================================
echo.

if not exist "bin" mkdir bin

powershell -NoProfile -Command "^
    $files = Get-ChildItem -Recurse -Filter '*.java' -Path 'src/main/java'; ^
    foreach ($file in $files) { ^
        Write-Host 'Compiling:' $file.Name; ^
        & javac --module-path '%JAVAFX_HOME%\lib' ^
                --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.media ^
                -d bin -cp bin $($file.FullName); ^
        if ($LASTEXITCODE -ne 0) { ^
            Write-Host 'Compilation error!'; ^
            exit 1; ^
        } ^
    } ^
    Write-Host 'Build successful!'; ^
"

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Compilation failed!
    echo.
    pause
    exit /b 1
)

echo.
echo ========================================
echo Build Complete!
echo ========================================
echo.
echo Starting game...
echo.

java --module-path "%JAVAFX_HOME%\lib" ^
     --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.media ^
     -cp bin com.pvz.game.PlantsVsZombiesApp

pause
