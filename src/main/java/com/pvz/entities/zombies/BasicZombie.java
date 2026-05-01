package com.pvz.entities.zombies;

public class BasicZombie extends Zombie {
    public static final int HEALTH = 200;

    public BasicZombie(double x, double y) {
        super(x, y, 35, 50, HEALTH, 0.075, 20, 25); // Thu nhỏ hitbox
    }

    @Override
    public String toString() {
        return "BasicZombie";
    }
}
