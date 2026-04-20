#!/bin/bash
# Setup and run Plants vs Zombies on Linux/Mac

JAVAFX_HOME="/opt/javafx-sdk"

if [ ! -d "$JAVAFX_HOME" ]; then
    echo "JavaFX SDK not found at $JAVAFX_HOME"
    echo "Download from: https://gluonhq.com/products/javafx/"
    echo "Then extract to $JAVAFX_HOME"
    exit 1
fi

mkdir -p bin

echo "Building project with JavaFX..."
javac --module-path "$JAVAFX_HOME/lib" \
      --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.media \
      -d bin \
      src/main/java/com/pvz/entities/*.java \
      src/main/java/com/pvz/game/*.java \
      src/main/java/com/pvz/ui/*.java

if [ $? -eq 0 ]; then
    echo "Build successful!"
    echo ""
    echo "Running game..."
    java --module-path "$JAVAFX_HOME/lib" \
         --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.media \
         -cp bin com.pvz.game.PlantsVsZombiesApp
else
    echo "Compilation failed!"
    exit 1
fi
