package com.pvz.entities.zombies;

public class Chicken extends Zombie {
    public static final int MAX_HEALTH = 25;

    public Chicken(double x, double y) {
        super(x, y, 25, 25, MAX_HEALTH,1.3, 25, 5);
        this.attackCooldown = 500; 
    }
    
    @Override
    public void update() { super.update(); }

    @Override
    public String toString() {
        return "Chicken";
    }
}
