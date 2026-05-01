package com.pvz.entities;

import com.pvz.game.GameBoard;

public class Peashooter extends Plant {
    public static final int COST = 100;
    public static final int HEALTH = 30;
    public static final long COOLDOWN = 1400; // milliseconds between shots
    public static final int DAMAGE = 20;

    public Peashooter(double x, double y) {
        super(x, y, 50, 60, HEALTH, COST, COOLDOWN);
    }

    @Override
    public void action() {
        if (!canAction()) {
            return;
        }

        GameBoard board = GameBoard.getInstance();
        if (board != null && board.hasZombieAhead(this)) {
            // Tạo đạn đậu bay thẳng (isLobbed = false), sát thương 20
            Pea pea = new Pea(getX() + getWidth() - 10, getY() + getHeight() / 2.0 - 5, DAMAGE, false, 0, false);
            board.addProjectile(pea);
            super.action(); // Cập nhật thời gian bắn cuối cùng để tính cooldown và chạy animation
        }
    }

    @Override
    public String toString() {
        return "Peashooter";
    }
}
