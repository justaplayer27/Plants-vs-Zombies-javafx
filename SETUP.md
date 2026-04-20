# Setup Instructions for Plants vs Zombies Game

## Prerequisites

This project requires Java 11+ with JavaFX support and Maven for building.

### Option 1: Setup with Maven (Recommended)

#### Step 1: Install Maven
1. Download Maven from https://maven.apache.org/download.cgi
2. Extract to a location (e.g., `C:\maven`)
3. Add `C:\maven\bin` to your System PATH environment variable
4. Verify installation: `mvn -version`

#### Step 2: Build the Project
```bash
mvn clean compile
```

#### Step 3: Run the Game
```bash
mvn javafx:run
```

### Option 2: Manual Javac Compilation (Advanced)

If Maven is not available, you need to manually download JavaFX SDK:

1. Download JavaFX SDK from https://gluonhq.com/products/javafx/
2. Extract to a location (e.g., `C:\javafx-sdk`)
3. Compile with:
```bash
javac --module-path C:\javafx-sdk\lib --add-modules javafx.controls,javafx.fxml -d bin src/main/java/com/pvz/entities/*.java
javac --module-path C:\javafx-sdk\lib --add-modules javafx.controls,javafx.fxml -d bin src/main/java/com/pvz/game/*.java
javac --module-path C:\javafx-sdk\lib --add-modules javafx.controls,javafx.fxml -d bin src/main/java/com/pvz/ui/*.java
```

4. Run with:
```bash
java --module-path C:\javafx-sdk\lib --add-modules javafx.controls,javafx.fxml -cp bin com.pvz.game.PlantsVsZombiesApp
```

### Option 3: Using Gradle (Alternative)

If you prefer Gradle, create a `build.gradle` file and use Gradle wrapper:
```bash
gradle run
```

## VS Code Integration

The project includes `.vscode/tasks.json` with build configurations. You can:

1. Press `Ctrl+Shift+B` to run the build task
2. Use "Run and Debug" menu to launch the game

## Troubleshooting

### "JavaFX modules not found"
- Make sure you've installed JavaFX SDK separately
- Update `pom.xml` with correct JavaFX version if needed
- Use `mvn clean compile` to refresh maven dependencies

### "Maven command not found"
- Install Maven and add it to PATH
- Or use the manual javac compilation method with JavaFX SDK

### Port already in use
- Change the port in configuration files or kill processes using the port

## Project Structure

```
.
├── pom.xml                 # Maven configuration
├── README.md              # Project documentation
├── build.bat              # Batch build script
├── src/
│   └── main/
│       └── java/
│           └── com/pvz/   # Source code
└── .vscode/
    └── tasks.json         # VS Code build tasks
```

## Development

The project uses a standard Maven directory structure. Add new classes to the appropriate packages:
- `com.pvz.game` - Core game logic
- `com.pvz.entities` - Game entities (plants, zombies, projectiles)
- `com.pvz.ui` - UI and rendering components
- `com.pvz.utils` - Utility classes

## Next Steps

1. Install Maven or JavaFX SDK
2. Run `mvn clean compile`
3. Run `mvn javafx:run` to start the game
4. Enjoy playing!

For more details on Maven with JavaFX, see:
https://openjfx.io/openjfx-docs/#maven
