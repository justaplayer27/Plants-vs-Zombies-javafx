package com.pvz.entities;

public class Sunflower extends Plant {
    public static final int COST = 50;
    public static final int HEALTH = 200;
    public static final long COOLDOWN = 20000; // milliseconds between sun drops
    public static final int SUN_VALUE = 50;

    private static long produceAnimationDuration = 1000;

    public Sunflower(double x, double y) {
        super(x, y, 40, 50, HEALTH, COST, COOLDOWN);
    }

    public static void setProduceAnimationDuration(long duration) {
        produceAnimationDuration = duration;
    }

    public boolean isProducing() {
        return System.currentTimeMillis() - lastActionTime < produceAnimationDuration;
    }

    @Override
    public String toString() {
        return "Sunflower";
    }
}
