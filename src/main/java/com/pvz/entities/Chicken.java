package com.pvz.entities;

import com.pvz.entities.zombies.Zombie; 

public class Chicken extends Zombie {
    public static final int MAX_HEALTH = 2;

    public Chicken(double x, double y) {
        // Tốc độ rất nhanh (0.375), máu giấy nhưng đã tăng gấp đôi
        super(x, y, 25, 25, MAX_HEALTH, 0.375, 5, 5);
        this.attackCooldown = 500; // Tấn công rất nhanh (0.5s)
    }
    
    @Override
    public void update() { super.update(); }

    @Override
    public String toString() {
        return "Chicken";
    }
}
