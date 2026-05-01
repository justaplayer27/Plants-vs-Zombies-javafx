package com.pvz.entities;

import com.pvz.game.GameBoard;
import com.pvz.entities.zombies.Zombie;
import java.util.List;
import java.util.ArrayList;

public class SpikeWeed extends Plant {
    public static final int COST = 125;
    public static final int HEALTH = 40;
    public static final long COOLDOWN = 900;
    public static final int DAMAGE = 15;

    public SpikeWeed(double x, double y) {
        super(x, y, 50, 20, HEALTH, COST, COOLDOWN);
    }

    @Override
    public void update() {
        if (!canAction()) return;

        GameBoard board = GameBoard.getInstance();
        if (board == null) return;

        int myRow = (int) (getY() / GameBoard.getCellHeight());
        double cellWidth = GameBoard.getCellWidth();
        double plantX = getX();
        boolean hitAny = false;

        List<Zombie> targets = new ArrayList<>(board.getZombies());

        for (Zombie zombie : targets) {
            int zombieRow = (int) (zombie.getY() / GameBoard.getCellHeight());
            if (myRow == zombieRow) {
                if (zombie.getX() >= plantX - cellWidth * 0.8 && zombie.getX() <= plantX + cellWidth * 1.5) {
                    zombie.takeBelowDamage(DAMAGE);
                    hitAny = true;
                }
            }
        }

        if (hitAny) {
            action();
        }
    }

    @Override
    public String toString() {
        return "SpikeWeed";
    }
}
