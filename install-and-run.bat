@echo off
setlocal enabledelayedexpansion

rem Change to the directory where this bat file is located
cd /d "%~dp0"

set JAVAFX_HOME=C:\javafx-sdk
set TEMP_ZIP=%USERPROFILE%\Downloads\javafx-temp.zip
set JAVAFX_URL=https://download2.gluonhq.com/openjfx/21.0.7/openjfx-21.0.7_windows-x64_bin-sdk.zip
set JDK_URL=https://download.oracle.com/java/21/latest/jdk-21_windows-x64_bin.zip
set JDK_ZIP=%USERPROFILE%\Downloads\jdk21.zip
set PF=%ProgramFiles%

echo ========================================
echo Checking Java...
echo ========================================

rem Try common Java install locations
for /d %%i in ("%PF%\Java\jdk*") do (
    if exist "%%i\bin\javac.exe" set "PATH=%%i\bin;%PATH%"
)
for /d %%i in ("%PF%\Eclipse Adoptium\jdk*") do (
    if exist "%%i\bin\javac.exe" set "PATH=%%i\bin;%PATH%"
)
for /d %%i in ("%PF%\Microsoft\jdk*") do (
    if exist "%%i\bin\javac.exe" set "PATH=%%i\bin;%PATH%"
)
if exist "C:\jdk-21\bin\javac.exe" set "PATH=C:\jdk-21\bin;%PATH%"

javac -version >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo Java OK!
    goto check_javafx
)

echo Java JDK not found. Downloading JDK 21...
echo This may take a few minutes...
echo.

curl -L -o "%JDK_ZIP%" "%JDK_URL%"

if not exist "%JDK_ZIP%" (
    echo ERROR: Failed to download JDK 21!
    echo Please install manually from: https://adoptium.net/
    pause
    exit /b 1
)

echo Extracting JDK 21...
powershell -NoProfile -Command "Expand-Archive -Path '%JDK_ZIP%' -DestinationPath 'C:\' -Force"

del "%JDK_ZIP%" 2>nul

rem Rename extracted folder to jdk-21
for /d %%i in ("C:\jdk-21*") do (
    if not "%%i"=="C:\jdk-21" rename "%%i" "jdk-21"
)

if exist "C:\jdk-21\bin\javac.exe" (
    set "PATH=C:\jdk-21\bin;%PATH%"
) else (
    echo ERROR: JDK 21 extraction failed!
    echo Please install manually from: https://adoptium.net/
    pause
    exit /b 1
)

javac -version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: JDK 21 installation failed!
    pause
    exit /b 1
)
echo Java JDK 21 installed successfully!

:check_javafx
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

curl -L -o "%TEMP_ZIP%" "%JAVAFX_URL%"

if not exist "%TEMP_ZIP%" (
    echo Download failed!
    goto manual_install
)

echo Extracting...
powershell -NoProfile -Command "Expand-Archive -Path '%TEMP_ZIP%' -DestinationPath 'C:\' -Force"

if exist "C:\javafx-sdk-21.0.7" (
    rename "C:\javafx-sdk-21.0.7" "javafx-sdk"
)

del "%TEMP_ZIP%" 2>nul

if not exist "%JAVAFX_HOME%" (
    :manual_install
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

echo Installation complete!

:compile
echo.
echo ========================================
echo Compiling Plants vs Zombies...
echo ========================================
echo.

if exist "bin" rmdir /s /q bin
mkdir bin

if exist sources.txt del sources.txt
for /r "src\main\java" %%f in (*.java) do echo %%f >> sources.txt

if not exist sources.txt (
    echo ERROR: No .java files found in src\main\java
    pause
    exit /b 1
)

echo Compiling all source files...
javac --module-path "%JAVAFX_HOME%\lib" ^
      --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.media ^
      -d bin -cp bin ^
      @sources.txt

del sources.txt 2>nul

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
