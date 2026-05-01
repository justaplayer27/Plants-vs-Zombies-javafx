package com.pvz.entities;

import java.util.Random;

public class Sun extends Entity {
    public static final int VALUE = 50;
    private double speed = 1.5;
    private double targetY;
    private double initialY;
    private boolean bouncePhase = false;
    private double bouncePeak;
    private boolean isFromPlant = false;
    private static final double FALL_SPEED = 1.5;

    public Sun(double x, double y) {
        super(x, y, 30, 30, 1); 
        
        Random rand = new Random();
        this.targetY = 100 + rand.nextInt(4) * 90;
        this.speed = 1.5;
    }

    public Sun(double x, double y, boolean fromPlant) {
        super(x, y, 30, 30, 1);
        this.isFromPlant = fromPlant;
        if (fromPlant) {
            this.initialY = y;
            this.bouncePhase = true;
            this.bouncePeak = y - 40;
            this.targetY = y + 50;
            this.speed = 3.0;
        } else {
            Random rand = new Random();
            this.targetY = 100 + rand.nextInt(4) * 90;
            this.speed = 1.5;
        }
    }

@Override
    public void update() {
        if (bouncePhase) {
            if (y > bouncePeak) {
                y -= speed;
            } else {
                bouncePhase = false;
                speed = FALL_SPEED;
            }
        } 
        
        if (!bouncePhase) {
            y += speed;
            if (y >= targetY) {
                y = targetY;
                speed = 0;
            }
        }
    }

    public int getValue() {
        return VALUE;
    }
}