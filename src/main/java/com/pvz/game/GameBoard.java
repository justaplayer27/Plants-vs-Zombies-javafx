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
    private List<Sun> suns;
    private int score;
    private int sun;
    private boolean gameOver;
    private boolean playerWon;
    private int wave;
    private int wavesCleared;
    private long lastZombieSpawnTime;
    private long zombieSpawnInterval = 3000; // milliseconds
    private long lastSunSpawnTime;
    private long sunSpawnInterval = 5000; // milliseconds

    public GameBoard() {
        plants = new ArrayList<>();
        zombies = new ArrayList<>();
        projectiles = new ArrayList<>();
        suns = new ArrayList<>();
        score = 0;
        sun = 100; // Starting sun
        gameOver = false;
        playerWon = false;
        wave = 1;
        wavesCleared = 0;
        lastZombieSpawnTime = System.currentTimeMillis();
        lastSunSpawnTime = System.currentTimeMillis();
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

    public boolean collectSunAt(double x, double y) {
        for (Sun s : suns) {
            if (s.getX() <= x && x <= s.getX() + s.getWidth() &&
                s.getY() <= y && y <= s.getY() + s.getHeight()) {
                sun += s.getValue();
                s.setAlive(false);
                return true;
            }
        }
        return false;
    }

    public void update() {
        if (gameOver) return;

        // Update plants and make them shoot
        for (Plant plant : plants) {
            plant.update();
            
            // Peashooter shoots at zombies
            if (plant instanceof Peashooter && plant.canAction()) {
                Zombie target = findNearestZombie(plant);
                if (target != null) {
                    // Create pea and shoot it
                    Pea pea = new Pea(plant.getX() + plant.getWidth(), plant.getY() + plant.getHeight() / 2);
                    projectiles.add(pea);
                    plant.action();
                }
            }
            
            // Sunflower produces sun
            if (plant instanceof Sunflower && plant.canAction()) {
                suns.add(new Sun(plant.getX() + plant.getWidth()/2 - 10, plant.getY() - 20));
                plant.action();
            }
        }

        // Update zombies
        for (Zombie zombie : zombies) {
            zombie.update();
        }

        // Update suns
        for (Sun s : suns) {
            s.update();
        }

        // Spawn suns periodically
        if (System.currentTimeMillis() - lastSunSpawnTime > sunSpawnInterval) {
            spawnSun();
            lastSunSpawnTime = System.currentTimeMillis();
        }

        // Update projectiles
        for (Pea pea : projectiles) {
            pea.update();
        }

        // Spawn zombies - always try to maintain zombie count
        if (System.currentTimeMillis() - lastZombieSpawnTime > zombieSpawnInterval) {
            int maxZombies = 2 + (wave * 2);
            if (zombies.size() < maxZombies) {
                spawnZombie();
            }
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
        suns.removeIf(s -> !s.isAlive());

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
        zombies.add(new BasicZombie(720, lane * CELL_SIZE + 10));
    }

    private void spawnSun() {
        double x = Math.random() * (GRID_WIDTH * CELL_SIZE - 20);
        suns.add(new Sun(x, 0));
    }
    
    private Zombie findNearestZombie(Plant plant) {
        Zombie nearest = null;
        double minDist = Double.MAX_VALUE;
        
        for (Zombie zombie : zombies) {
            if (zombie.getY() == plant.getY() || Math.abs(zombie.getY() - plant.getY()) < 30) {
                double dist = Math.abs(zombie.getX() - plant.getX());
                if (dist < minDist && zombie.getX() > plant.getX()) {
                    minDist = dist;
                    nearest = zombie;
                }
            }
        }
        return nearest;
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
    public List<Sun> getSuns() { return new ArrayList<>(suns); }
    public int getScore() { return score; }
    public int getSun() { return sun; }
    public boolean isGameOver() { return gameOver; }
    public boolean isPlayerWon() { return playerWon; }
    public int getWave() { return wave; }
    public static int getGridWidth() { return GRID_WIDTH; }
    public static int getGridHeight() { return GRID_HEIGHT; }
    public static int getCellSize() { return CELL_SIZE; }
}