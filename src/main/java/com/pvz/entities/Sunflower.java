package com.pvz.entities;

public class Sunflower extends Plant {
    public static final int COST = 50;
    public static final int HEALTH = 20;
    public static final long COOLDOWN = 20000; // milliseconds between sun drops
    public static final int SUN_VALUE = 25;

    public Sunflower(double x, double y) {
        super(x, y, 40, 50, HEALTH, COST, COOLDOWN);
    }

    @Override
    public String toString() {
        return "Sunflower";
    }
}
