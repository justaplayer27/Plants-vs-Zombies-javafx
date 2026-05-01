package com.pvz.entities.zombies;

import com.pvz.entities.zombies.Chicken;
import com.pvz.game.GameBoard;

public class ChickenWranglerZombie extends Zombie {
    public static final int HEALTH = 360;
    private int chickensSpawnedCount = 0;
    private int chickensToSpawn = 0;
    private long lastSpawnTime = 0;
    private static final long SPAWN_DELAY = 600;
    private boolean releasingStarted = false;

    public ChickenWranglerZombie(double x, double y) {
        super(x, y, 40, 60, HEALTH, 0.1, 20, 35);
    }

    @Override
    public void update() {
        if (isStunned()) {
            return;
        }

        if (chickensToSpawn > 0) {
            if (System.currentTimeMillis() - lastSpawnTime > SPAWN_DELAY) {
                spawnOneChicken();
                chickensToSpawn--;
                lastSpawnTime = System.currentTimeMillis();
            }
            return; 
        }
        super.update();
    }

    @Override
    public void takeDamage(int damage) {
        super.takeDamage(damage);
        if (chickensToSpawn == 0 && !releasingStarted) {
            startReleasingChickens();
        }
    }

    @Override
    public void takeFrontalDamage(int damage) {
        takeDamage(damage);
    }

    @Override
    public void takeLobbedDamage(int damage) {
        takeDamage(damage);
    }

    @Override
    public void takeBelowDamage(int damage) {
        takeDamage(damage);
    }

    @Override
    public void takeBehindDamage(int damage) {
        takeDamage(damage);
    }

    @Override
    public void startEating(com.pvz.entities.Plant plant) {
        if (!releasingStarted) {
            startReleasingChickens();
        }
        super.startEating(plant);
    }

    private void startReleasingChickens() {
        if (!releasingStarted) {
            releasingStarted = true;
            chickensToSpawn = 4;
            speed *= 1.5;
            lastSpawnTime = 0;
        }
    }

    private void spawnOneChicken() {
        GameBoard board = GameBoard.getInstance();
        if (board != null) {
            Chicken chicken = new Chicken(this.getX() - 10, this.getY() + 20);
            board.addZombie(chicken);
            board.addChicken(chicken);
            chickensSpawnedCount++;
        }
    }

    public boolean isReleasing() {
        return chickensToSpawn > 0;
    }

    public boolean hasReleasingStarted() {
        return releasingStarted;
    }

    @Override
    public String toString() {
        return "ChickenWranglerZombie";
    }
}
