# Plants vs Zombies - Quick Start Guide

## 2-Minute Setup

### Step 1: Download JavaFX SDK

**Option A (Easiest):**
- Double-click `download-javafx.bat` 
- It will open the download page automatically
- Download "JavaFX Windows SDK"

**Option B (Manual):**
- Visit: https://gluonhq.com/products/javafx/
- Download "JavaFX Windows SDK"

### Step 2: Extract JavaFX

1. Extract the downloaded ZIP file
2. Rename the folder to `javafx-sdk`
3. Move it to your C: drive: `C:\javafx-sdk`

### Step 3: Play!

Double-click `start-game.bat` 

That's it! The game will compile and start automatically.

---

## Troubleshooting

**"C:\javafx-sdk not found"**
- Make sure JavaFX is extracted to exactly: `C:\javafx-sdk`
- Check that the path has `lib` folder inside it

**Compilation errors**
- Run `start-game.bat` again - sometimes it takes a moment
- Make sure all Java files are saved before running

**Game won't start**
- Close the console window that appears after compilation
- Make sure you have Java 11+ installed (check: `java -version`)

---

## Game Instructions

- **Select Plant**: Click a plant button at the bottom
- **Place Plant**: Click a grid cell to place it
- **Survive Waves**: Defend against zombie waves for 3 rounds
- **Pause**: Click the Pause button to pause/resume

---

## Files Reference

- `start-game.bat` - Play the game (main file to use)
- `download-javafx.bat` - Download JavaFX SDK
- `src/` - Game source code
- `bin/` - Compiled game files (created automatically)
