package com.pvz.entities;

public class Mower extends Entity {
    private final int row;
    private boolean active;
    private double speed = 9.0;

    public Mower(double x, double y, int row) {
        super(x, y, 40, 30, 1);
        this.row = row;
        this.active = false;
    }

    @Override
    public void update() {
        if (active) {
            x += speed;
            if (x > 1200) {
                alive = false;
            }
        }
    }

    public void activate() {
        active = true;
    }

    public boolean isActive() {
        return active;
    }

    public int getRow() {
        return row;
    }
}
