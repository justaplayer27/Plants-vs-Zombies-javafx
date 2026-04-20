package com.pvz.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;
import com.pvz.game.GameBoard;
import com.pvz.entities.*;

public class GameCanvas extends Canvas {
    private GameBoard gameBoard;
    private Plant selectedPlant;
    private GraphicsContext gc;

    public GameCanvas(GameBoard gameBoard) {
        super(720, 600);
        this.gameBoard = gameBoard;
        this.gc = this.getGraphicsContext2D();
        
        setStyle("-fx-background-color: #1a1a1a;");
        
        // Mouse input handling
        setOnMouseClicked(this::handleMouseClick);
        setOnMouseMoved(this::handleMouseMove);
    }

    public void render() {
        // Clear canvas
        gc.setFill(Color.web("#1a1a1a"));
        gc.fillRect(0, 0, getWidth(), getHeight());

        // Draw grid
        drawGrid();

        // Draw plants
        for (Plant plant : gameBoard.getPlants()) {
            drawPlant(plant);
        }

        // Draw zombies
        for (Zombie zombie : gameBoard.getZombies()) {
            drawZombie(zombie);
        }

        // Draw projectiles
        for (Pea pea : gameBoard.getProjectiles()) {
            drawPea(pea);
        }

        // Draw selected plant preview if mouse is over board
        if (selectedPlant != null) {
            drawSelectedPlantPreview();
        }
    }

    private void drawGrid() {
        gc.setStroke(Color.web("#444444"));
        gc.setLineWidth(1);
        
        int cellSize = GameBoard.getCellSize();
        int width = GameBoard.getGridWidth();
        int height = GameBoard.getGridHeight();

        for (int i = 0; i <= width; i++) {
            gc.strokeLine(i * cellSize, 0, i * cellSize, height * cellSize);
        }
        for (int i = 0; i <= height; i++) {
            gc.strokeLine(0, i * cellSize, width * cellSize, i * cellSize);
        }
    }

    private void drawPlant(Plant plant) {
        if (plant instanceof Peashooter) {
            gc.setFill(Color.GREEN);
        } else if (plant instanceof Sunflower) {
            gc.setFill(Color.YELLOW);
        } else {
            gc.setFill(Color.LIGHTGREEN);
        }
        
        gc.fillRect(plant.getX(), plant.getY(), plant.getWidth(), plant.getHeight());
        
        // Draw health bar
        drawHealthBar(plant);
    }

    private void drawZombie(Zombie zombie) {
        gc.setFill(Color.GRAY);
        gc.fillRect(zombie.getX(), zombie.getY(), zombie.getWidth(), zombie.getHeight());
        
        // Draw eyes
        gc.setFill(Color.RED);
        gc.fillOval(zombie.getX() + 5, zombie.getY() + 10, 8, 8);
        gc.fillOval(zombie.getX() + 25, zombie.getY() + 10, 8, 8);
        
        // Draw health bar
        drawHealthBar(zombie);
    }

    private void drawPea(Pea pea) {
        gc.setFill(Color.YELLOW);
        gc.fillOval(pea.getX(), pea.getY(), pea.getWidth(), pea.getHeight());
    }

    private void drawHealthBar(Entity entity) {
        int maxHealth = 30; // Approximate max health for display
        if (entity instanceof Peashooter) maxHealth = Peashooter.HEALTH;
        else if (entity instanceof Sunflower) maxHealth = Sunflower.HEALTH;
        else if (entity instanceof BasicZombie) maxHealth = BasicZombie.HEALTH;

        double barWidth = entity.getWidth();
        double healthPercent = Math.max(0, Math.min(1, (double) entity.getHealth() / maxHealth));
        
        // Background bar
        gc.setFill(Color.RED);
        gc.fillRect(entity.getX(), entity.getY() - 5, barWidth, 3);
        
        // Health bar
        gc.setFill(Color.GREEN);
        gc.fillRect(entity.getX(), entity.getY() - 5, barWidth * healthPercent, 3);
    }

    private void drawSelectedPlantPreview() {
        // This would be called during mouse movement to show preview
        // Implementation can be added for better UX
    }

    private void handleMouseClick(MouseEvent event) {
        if (selectedPlant == null) return;

        int cellSize = GameBoard.getCellSize();
        int gridX = (int) (event.getX() / cellSize);
        int gridY = (int) (event.getY() / cellSize);

        if (event.getButton() == MouseButton.PRIMARY) {
            // Place plant
            Plant newPlant = createPlantCopy(selectedPlant);
            if (gameBoard.plantAt(newPlant, gridX, gridY)) {
                // Plant placed successfully
            }
        } else if (event.getButton() == MouseButton.SECONDARY) {
            // Remove plant (not implemented yet)
        }
    }

    private void handleMouseMove(MouseEvent event) {
        // Could update preview position here
    }

    private Plant createPlantCopy(Plant original) {
        if (original instanceof Peashooter) {
            return new Peashooter(0, 0);
        } else if (original instanceof Sunflower) {
            return new Sunflower(0, 0);
        }
        return new Peashooter(0, 0);
    }

    public void setSelectedPlant(Plant plant) {
        this.selectedPlant = plant;
    }
}
