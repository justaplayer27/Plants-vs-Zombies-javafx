package com.pvz.entities.zombies;

import com.pvz.game.GameBoard;
import com.pvz.entities.Plant;

public class ExcavatorZombie extends Zombie {
    public static final int HEALTH = 440;
    private boolean hasVaulted = false;

    public ExcavatorZombie(double x, double y) {
        super(x, y, 40, 60, HEALTH, 0.1, 25, 50);
    }

    @Override
    public void update() {
        if (!hasVaulted) {
            for (Plant plant : GameBoard.getInstance().getPlants()) {
                if (plant.isAlive() && this.intersects(plant)) {
                    x += GameBoard.getCellWidth() * 0.45;
                    hasVaulted = true;
                    break;
                }
            }
        }

        super.update();
    }

    @Override
    public void takeFrontalDamage(int damage) {
        System.out.println("Excavator blocked frontal damage with shovel!");
    }

    @Override
    public void takeDamage(int damage) {
        super.takeDamage(damage);
    }

    @Override
    public void takeLobbedDamage(int damage) {
        super.takeDamage(damage);
    }

    @Override
    public void takeBelowDamage(int damage) {
        super.takeDamage(damage);
    }

    @Override
    public void takeBehindDamage(int damage) {
        super.takeDamage(damage);
    }

    @Override
    public String toString() {
        return "ExcavatorZombie";
    }
}
