package com.pvz.entities.zombies;

import com.pvz.game.GameBoard;
import com.pvz.entities.Plant;

public class ExcavatorZombie extends Zombie {
    public static final int HEALTH = 440;
    private boolean hasVaulted = false;

    public ExcavatorZombie(double x, double y) {
        super(x, y, 40, 60, HEALTH, 0.045, 25, 50);
    }

    @Override
    public void update() {
        // Kiểm tra trạng thái bất động trước khi thực hiện bất kỳ logic nào
        if (isStunned()) {
            return;
        }

        if (eatingPlant != null) {
            if (!eatingPlant.isAlive()) {
                eatingPlant = null;
            } else {
                attack(eatingPlant);
                return;
            }
        }

        if (!hasVaulted) {
            for (Plant plant : GameBoard.getInstance().getPlants()) {
                if (plant.isAlive() && this.intersects(plant)) {
                    x += GameBoard.getCellWidth() * 0.45;
                    hasVaulted = true;
                    break;
                }
            }
        }

        x -= speed * 4.0;
    }

    @Override
    public void takeFrontalDamage(int damage) {
        // Chỉ chặn sát thương khi gọi đúng hàm takeFrontalDamage
        System.out.println("Excavator blocked frontal damage with shovel!");
    }

    @Override
    public void takeDamage(int damage) {
        // Hàm này là gốc của Entity, không nên ghi đè để block hoàn toàn
        // vì các hàm như takeBehindDamage cần gọi super.takeDamage() để trừ máu.
        super.takeDamage(damage);
    }

    @Override
    public void takeLobbedDamage(int damage) {
        // Bỏ qua lớp bảo vệ phía trước, trừ máu trực tiếp vào thực thể
        super.takeDamage(damage);
    }

    @Override
    public void takeBelowDamage(int damage) {
        // Bỏ qua lớp bảo vệ phía trước, trừ máu trực tiếp vào thực thể
        super.takeDamage(damage);
    }

    @Override
    public void takeBehindDamage(int damage) {
        // Bỏ qua lớp bảo vệ phía trước, trừ máu trực tiếp vào thực thể
        super.takeDamage(damage);
    }

    @Override
    public String toString() {
        return "ExcavatorZombie";
    }
}
