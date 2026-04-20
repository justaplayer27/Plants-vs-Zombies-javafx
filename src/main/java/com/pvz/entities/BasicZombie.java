package com.pvz.entities;

public class BasicZombie extends Zombie {
    public static final int HEALTH = 20;
    public static final double SPEED = 0.2;
    public static final int ATTACK_DAMAGE = 5;
    public static final int REWARD = 25;

    public BasicZombie(double x, double y) {
        super(x, y, 40, 60, HEALTH, SPEED, ATTACK_DAMAGE, REWARD);
    }

    @Override
    public String toString() {
        return "BasicZombie";
    }
}
