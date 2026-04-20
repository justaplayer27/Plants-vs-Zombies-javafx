# Plants vs Zombies - Java Implementation

A JavaFX-based implementation of the classic Plants vs Zombies tower defense game.

## Features

- **Grid-based gameplay**: 9x5 grid for game board
- **Plant types**:
  - **Peashooter**: Shoots peas at zombies (Cost: 100 Sun, Cooldown: 1.4s)
  - **Sunflower**: Generates sun for currency (Cost: 50 Sun, Generates: 25 Sun every 8s)
- **Zombie types**:
  - **Basic Zombie**: Standard zombie with moderate health and speed
- **Game mechanics**:
  - Wave-based zombie spawning
  - Resource management (Sun currency)
  - Collision detection
  - Health system
  - Score tracking
  - Pause functionality
- **UI**:
  - Real-time game stats display (Sun, Score, Wave)
  - Visual grid system
  - Plant selection buttons
  - Game canvas with rendering

## Project Structure

```
src/main/java/com/pvz/
├── game/
│   ├── PlantsVsZombiesApp.java    # Main application entry point
│   └── GameBoard.java              # Core game logic and state management
├── entities/
│   ├── Entity.java                 # Base class for game objects
│   ├── Plant.java                  # Base class for plants
│   ├── Peashooter.java             # Peashooter plant implementation
│   ├── Sunflower.java              # Sunflower plant implementation
│   ├── Zombie.java                 # Base class for zombies
│   ├── BasicZombie.java            # Basic zombie implementation
│   └── Pea.java                    # Projectile class
└── ui/
    ├── GameWindow.java             # Main game window and controls
    └── GameCanvas.java             # Game rendering canvas
```

## Build & Run

### Prerequisites
- Java 11 or higher (already installed)
- JavaFX SDK 21 (need to download)

### Quick Start (Windows)

**Option 1: Automatic (Easiest)**
1. Double-click `download-javafx.bat` to open download page
2. Download JavaFX Windows SDK
3. Extract to `C:\javafx-sdk`
4. Double-click `start-game.bat` to play!

**Option 2: Manual**
1. Download JavaFX: https://gluonhq.com/products/javafx/
2. Extract to `C:\javafx-sdk`
3. Double-click `start-game.bat`

## How to Play

1. **Starting Resources**: You begin with 100 Sun
2. **Planting**: 
   - Select a plant from the bottom panel
   - Click on a grid cell to place the plant
   - Each plant costs Sun
3. **Objective**: Defend against zombie waves
4. **Winning**: Clear 3 waves to win
5. **Losing**: Let zombies reach the left side of the screen

## Controls

- **Left Click**: Place selected plant on grid
- **Right Click**: Remove plant (if implemented)
- **Pause Button**: Pause/Resume the game
- **Plant Buttons**: Select which plant to place

## Gameplay Mechanics

### Sun System
- Starting Sun: 100
- Sunflower generates 25 Sun every 8 seconds
- Defeating zombies awards Sun

### Plant Costs
- Peashooter: 100 Sun
- Sunflower: 50 Sun

### Zombies
- Spawn in waves
- Move from right to left
- Attack plants in their path
- Award Sun when defeated

## Future Enhancements

- Additional plant types (Cherry Bomb, Wall-nut, etc.)
- More zombie variety
- Sound effects and background music
- Particle effects and animations
- Level progression
- Save/Load game state
- Difficulty settings
- Leaderboard

## Technical Notes

- Built with JavaFX 21
- Grid-based collision detection
- Multi-threaded game loop (60 FPS target)
- Event-driven UI interactions
- Object-oriented entity system

## License

This is an educational project inspired by PopCap Games' Plants vs Zombies.
