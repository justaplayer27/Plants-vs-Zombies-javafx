package com.pvz.entities;

public class Peashooter extends Plant {
    public static final int COST = 100;
    public static final int HEALTH = 30;
    public static final long COOLDOWN = 1400; // milliseconds between shots

    public Peashooter(double x, double y) {
        super(x, y, 50, 60, HEALTH, COST, COOLDOWN);
    }

    @Override
    public String toString() {
        return "Peashooter";
    }
}
