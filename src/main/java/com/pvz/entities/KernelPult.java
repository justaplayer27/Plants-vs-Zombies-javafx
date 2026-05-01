package com.pvz.entities;

import com.pvz.game.GameBoard;
import com.pvz.entities.zombies.Zombie;

public class KernelPult extends Plant {
    public static final int COST = 175;
    public static final int HEALTH = 35;
    public static final long COOLDOWN = 1600; // Trở về tốc độ bắn bình thường
    public static final int DAMAGE = 12;

    public KernelPult(double x, double y) {
        super(x, y, 50, 60, HEALTH, COST, COOLDOWN);
    }

    @Override
    public void action() {
        if (!canAction()) {
            return;
        }

        GameBoard board = GameBoard.getInstance();
        if (board != null && board.hasZombieAhead(this)) {
            shootKernel();
            super.action();
        }
    }

    private void shootKernel() {
        GameBoard board = GameBoard.getInstance();
        if (board != null) {
            // Tìm zombie gần nhất để làm đích đến cho hình vòng cung
            Zombie target = board.findNearestZombie(this);
            double tX = (target != null) ? target.getX() : getX() + 500;
        boolean isButter = Math.random() < 0.1; // Sửa lại đúng 10% cơ hội bắn ra bơ
            Pea pea = new Pea(getX() + getWidth(), getY() + getHeight() / 2.0 - 5, DAMAGE, true, tX, isButter);
            board.addProjectile(pea);
        }
    }

    @Override
    public String toString() {
        return "KernelPult";
    }
}
