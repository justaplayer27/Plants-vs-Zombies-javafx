package com.pvz.entities;

public abstract class Entity {
    protected double x;
    protected double y;
    protected double width;
    protected double height;
    protected int health;
    protected boolean alive;

    public Entity(double x, double y, double width, double height, int health) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.health = health;
        this.alive = true;
    }

    public abstract void update();

    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            alive = false;
        }
    }

    public boolean intersects(Entity other) {
        return this.x < other.x + other.width &&
               this.x + this.width > other.x &&
               this.y < other.y + other.height &&
               this.y + this.height > other.y;
    }

    // Getters and setters
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    public double getWidth() { return width; }
    public double getHeight() { return height; }

    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }

    public boolean isAlive() { return alive; }
    public void setAlive(boolean alive) { this.alive = alive; }
}
