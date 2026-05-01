package com.pvz.entities;

import com.pvz.game.GameBoard;
import com.pvz.entities.zombies.Zombie;

public class BonkChoy extends Plant {
    public static final int COST = 150;
    public static final int HEALTH = 450;
    public static final long COOLDOWN = 400;
    public static final int DAMAGE = 12;

    public BonkChoy(double x, double y) {
        super(x, y, 50, 60, HEALTH, COST, COOLDOWN);
    }

    @Override
    public void action() {
        if (canAction()) {
            Zombie target = findNearbyZombie();
            if (target != null) {
                // Kiểm tra vị trí Zombie so với Bonk Choy để xác định hướng đánh
                if (target.getX() > this.getX()) {
                    // Zombie ở bên phải (phía trước mặt zombie đang đi tới)
                    target.takeFrontalDamage(DAMAGE);
                } else {
                    // Zombie đã đi qua Bonk Choy (đánh vào lưng)
                    target.takeBehindDamage(DAMAGE);
                }
                super.action();
            }
        }
    }

    private Zombie findNearbyZombie() {
        GameBoard board = GameBoard.getInstance();
        if (board == null) {
            return null;
        }

        int myRow = (int) (getY() / GameBoard.getCellHeight());
        double range = GameBoard.getCellWidth() * 2.65; // Tầm đánh 2.5 - 2.8 ô
        for (Zombie zombie : board.getZombies()) {
            int zombieRow = (int) (zombie.getY() / GameBoard.getCellHeight());
            if (myRow == zombieRow) {
                if (Math.abs(zombie.getX() - getX()) < range) {
                    return zombie;
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "BonkChoy";
    }
}
