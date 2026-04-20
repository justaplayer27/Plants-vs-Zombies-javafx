package com.pvz.game;

import com.pvz.entities.*;
import java.util.*;

public class GameBoard {
    private static final int GRID_WIDTH = 9;
    private static final int GRID_HEIGHT = 5;
    private static final int CELL_SIZE = 80;

    private List<Plant> plants;
    private List<Zombie> zombies;
    private List<Pea> projectiles;
    private int score;
    private int sun;
    private boolean gameOver;
    private boolean playerWon;
    private int wave;
    private int wavesCleared;
    private long lastZombieSpawnTime;
    private long zombieSpawnInterval = 3000; // milliseconds

    public GameBoard() {
        plants = new ArrayList<>();
        zombies = new ArrayList<>();
        projectiles = new ArrayList<>();
        score = 0;
        sun = 100; // Starting sun
        gameOver = false;
        playerWon = false;
        wave = 1;
        wavesCleared = 0;
        lastZombieSpawnTime = System.currentTimeMillis();
    }

    public boolean plantAt(Plant plant, int gridX, int gridY) {
        if (gridX < 0 || gridX >= GRID_WIDTH || gridY < 0 || gridY >= GRID_HEIGHT) {
            return false;
        }
        
        if (sun < plant.getCost()) {
            return false;
        }

        // Check if position is occupied
        for (Plant p : plants) {
            if (Math.abs(p.getX() - gridX * CELL_SIZE) < 10 && 
                Math.abs(p.getY() - gridY * CELL_SIZE) < 10) {
                return false;
            }
        }

        double x = gridX * CELL_SIZE;
        double y = gridY * CELL_SIZE;
        plant.setX(x);
        plant.setY(y);
        plants.add(plant);
        sun -= plant.getCost();
        return true;
    }

    public void addZombie(Zombie zombie) {
        zombies.add(zombie);
    }

    public void addProjectile(Pea pea) {
        projectiles.add(pea);
    }

    public void update() {
        if (gameOver) return;

        // Update plants
        for (Plant plant : plants) {
            plant.update();
        }

        // Update zombies
        for (Zombie zombie : zombies) {
            zombie.update();
        }

        // Update projectiles
        for (Pea pea : projectiles) {
            pea.update();
        }

        // Spawn zombies based on wave
        if (System.currentTimeMillis() - lastZombieSpawnTime > zombieSpawnInterval && 
            zombies.size() < (2 + wave)) {
            spawnZombie();
            lastZombieSpawnTime = System.currentTimeMillis();
        }

        // Check collisions (projectiles hitting zombies)
        checkProjectileCollisions();

        // Check plant-zombie collisions (zombies eating plants)
        checkZombieCollisions();

        // Remove dead entities
        plants.removeIf(p -> !p.isAlive());
        zombies.removeIf(z -> !z.isAlive());
        projectiles.removeIf(p -> !p.isAlive());

        // Check win/lose conditions
        if (zombies.isEmpty() && wave >= 3) {
            playerWon = true;
            gameOver = true;
        }

        if (zombies.stream().anyMatch(z -> z.getX() < 50)) {
            gameOver = true;
        }
    }

    private void spawnZombie() {
        int lane = (int) (Math.random() * GRID_HEIGHT);
        zombies.add(new BasicZombie(1000, lane * CELL_SIZE + 10));
    }

    private void checkProjectileCollisions() {
        for (Pea pea : new ArrayList<>(projectiles)) {
            for (Zombie zombie : new ArrayList<>(zombies)) {
                if (pea.intersects(zombie)) {
                    zombie.takeDamage(pea.getDamage());
                    pea.setAlive(false);
                    
                    if (!zombie.isAlive()) {
                        score += 10;
                        sun += zombie.getReward();
                    }
                    break;
                }
            }
        }
    }

    private void checkZombieCollisions() {
        for (Zombie zombie : zombies) {
            for (Plant plant : plants) {
                if (zombie.intersects(plant)) {
                    if (zombie.canAttack()) {
                        zombie.attack(plant);
                    }
                }
            }
        }
    }

    // Getters
    public List<Plant> getPlants() { return new ArrayList<>(plants); }
    public List<Zombie> getZombies() { return new ArrayList<>(zombies); }
    public List<Pea> getProjectiles() { return new ArrayList<>(projectiles); }
    public int getScore() { return score; }
    public int getSun() { return sun; }
    public boolean isGameOver() { return gameOver; }
    public boolean isPlayerWon() { return playerWon; }
    public int getWave() { return wave; }
    public static int getGridWidth() { return GRID_WIDTH; }
    public static int getGridHeight() { return GRID_HEIGHT; }
    public static int getCellSize() { return CELL_SIZE; }
}
