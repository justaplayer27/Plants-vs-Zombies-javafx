package com.pvz.entities;

public class Pea extends Entity {
    private double speed = 5.0;
    private int damage = 5;

    public Pea(double x, double y) {
        super(x, y, 10, 10, 1);
        this.damage = 5;
    }

    @Override
    public void update() {
        x += speed;
        
        // Remove if off screen
        if (x > 1000) {
            alive = false;
        }
    }

    public int getDamage() { return damage; }
}
