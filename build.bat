@echo off
REM Plants vs Zombies Build Script

echo Building Plants vs Zombies Game...

REM Check if bin directory exists
if not exist "bin" mkdir bin

REM Compile all Java files
echo Compiling source files...
for /r "src\main\java" %%f in (*.java) do (
    echo Compiling %%f
    javac -d bin -cp bin "%%f"
    if errorlevel 1 (
        echo Compilation failed!
        exit /b 1
    )
)

echo Build complete! Classes compiled to bin/

REM Run the application
echo Running application...
java -cp bin com.pvz.game.PlantsVsZombiesApp

pause
