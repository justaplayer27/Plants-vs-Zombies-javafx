package com.pvz.entities;

public class Pea extends Entity {
    private double speed = 10.0;
    private int damage = 5;
    private boolean lobbed;
    private boolean butter;
    private double startX;
    private double targetX;
    private double totalDistance;

    public Pea(double x, double y) {
        super(x, y, 10, 10, 1);
        this.damage = 5;
        this.lobbed = false;
        this.butter = false;
        this.startX = x;
        this.targetX = x + 200;
        this.totalDistance = Math.max(1, targetX - startX);
    }

    public Pea(double x, double y, int damage, boolean lobbed, double targetX, boolean butter) {
        super(x, y, 10, 10, 1);
        this.damage = damage;
        this.lobbed = lobbed;
        this.butter = butter;
        this.startX = x;
        this.targetX = targetX;
        this.totalDistance = Math.max(1, Math.abs(targetX - startX));
    }

    @Override
    public void update() {
        x += speed;
        
        if (x > 1000) {
            alive = false;
        }
    }

    public int getDamage() { return damage; }

    public boolean isLobbed() {
        return lobbed;
    }

    public boolean isButter() {
        return butter;
    }

    public double getVisualY() {
        if (!lobbed) {
            return y;
        }
        double progress = (x - startX) / totalDistance;
        progress = Math.max(0, Math.min(1, progress));
        return y - Math.sin(progress * Math.PI) * 40;
    }
}
