<<<<<<< HEAD
# Plants vs Zombies Java

A simple Plants vs Zombies game built using JavaFX.

## Purpose
To preserve the source code and necessary scripts to run the game on a new Windows machine, even if JavaFX is not yet installed.

## Requirements
- Java 11 or newer must be installed.
- Windows OS.
- Internet connection to download the JavaFX SDK when running `install-and-run.bat` for the first time.

## How to Run
### 1. First-time setup and run
- Double-click `install-and-run.bat`.
- The script will:
  - Download the JavaFX SDK.
  - Extract it to `C:\javafx-sdk`.
  - Compile the source code in `src/main/java`.
  - Launch the game.

### 2. If JavaFX is already installed
- Double-click `start-game.bat`.
- The script will compile and run the game using the existing JavaFX installation.

## Notes
- `src/` contains the entire Java source code.
- `pom.xml` is the Maven configuration file, used if you prefer building with Maven.
- `bin/` and `target/` are build artifact directories and should not be included in the repository.
- `progress.txt` is the save file for game progress, generated during gameplay.

## Troubleshooting
- If the script reports that `java` or `javac` is not found, ensure JDK 11+ is installed and added to your `PATH`.
- If JavaFX fails to download automatically, visit: https://gluonhq.com/products/javafx/ and download the `JavaFX Windows SDK` manually.

## Summary of Essential Files
- `install-and-run.bat` — Main setup file for new users.
- `start-game.bat` — Run the game if JavaFX is already configured.
- `install-and-run.ps1` — PowerShell alternative.
- `src/` — Source code.
- `pom.xml` — Maven configuration.
=======
# Plants-vs-Zombies-javafx
 A Plants vs Zombies clone built with Java &amp; JavaFX
>>>>>>> 8bcadfecbdc4208b0912c53bcb3a94cffc61760d
