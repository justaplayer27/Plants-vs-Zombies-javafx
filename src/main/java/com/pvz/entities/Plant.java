package com.pvz.entities;

public abstract class Plant extends Entity {
    protected int cost; // Sun cost to plant
    protected long lastActionTime;
    protected long cooldown; // in milliseconds

    public Plant(double x, double y, double width, double height, int health, int cost, long cooldown) {
        super(x, y, width, height, health);
        this.cost = cost;
        this.cooldown = cooldown;
        this.lastActionTime = System.currentTimeMillis();
    }

    public boolean canAction() {
        return System.currentTimeMillis() - lastActionTime >= cooldown;
    }

    public void action() {
        lastActionTime = System.currentTimeMillis();
    }

    public boolean isRecentlyActive(long millis) {
        return System.currentTimeMillis() - lastActionTime < millis;
    }

    @Override
    public void update() {
        // Default plant update - no movement
    }

    public int getCost() { return cost; }
}
