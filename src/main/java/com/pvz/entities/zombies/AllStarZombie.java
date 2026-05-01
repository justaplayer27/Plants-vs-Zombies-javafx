package com.pvz.entities.zombies;

import com.pvz.game.GameBoard;
import com.pvz.entities.Plant;

public class AllStarZombie extends Zombie {
    public static final int HEALTH = 600;
    private boolean hasCharged = false;
    private long lastChargeTime = 0;
    private double normalSpeed;

    public AllStarZombie(double x, double y) {
        super(x, y, 40, 60, HEALTH, 0.6, 30, 60); // Bắt đầu với tốc độ cao (0.15)
        this.normalSpeed = 0.05;
    }

    @Override
    public void update() {
        if (isStunned()) {
            return;
        }

        if (!hasCharged) {
            // Kiểm tra va chạm với cây để húc
            for (Plant plant : GameBoard.getInstance().getPlants()) {
                if (plant.isAlive() && this.intersects(plant)) {
                    plant.takeDamage(9999); // Giết luôn cây đầu tiên
                    hasCharged = true;
                    lastChargeTime = System.currentTimeMillis();
                    this.speed = normalSpeed; // Trở về tốc độ bình thường
                    break;
                }
            }
        }
        super.update();
    }

    public boolean hasCharged() {
        return hasCharged;
    }

    public boolean isCharging() {
        return lastChargeTime > 0 && System.currentTimeMillis() - lastChargeTime < 500;
    }

    @Override
    public String toString() {
        return "AllStarZombie";
    }
}
