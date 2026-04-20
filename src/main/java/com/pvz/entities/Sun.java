package com.pvz.entities;

public class Sun extends Entity {
    public static final int VALUE = 25;
    private double fallSpeed = 2.0; // pixels per update

    public Sun(double x, double y) {
        super(x, y, 20, 20, 1); // Small health, but not really used
    }

    @Override
    public void update() {
        // Fall down
        y += fallSpeed;

        // If it falls off the screen, remove it
        if (y > 600) {
            alive = false;
        }
    }

    public int getValue() {
        return VALUE;
    }
}