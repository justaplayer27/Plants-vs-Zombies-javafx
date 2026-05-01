package com.pvz.entities.zombies;

public class FlagZombie extends Zombie {
    public static final int HEALTH = 240;

    public FlagZombie(double x, double y) {
        super(x, y, 38, 55, HEALTH, 0.06, 30, 20);
    }

    @Override
    public String toString() {
        return "FlagZombie";
    }
}
