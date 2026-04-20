package com.pvz.entities;

public abstract class Zombie extends Entity {
    protected double speed;
    protected long lastActionTime;
    protected long attackCooldown;
    protected int attackDamage;
    protected int reward; // Sun reward for defeating

    public Zombie(double x, double y, double width, double height, int health, 
                  double speed, int attackDamage, int reward) {
        super(x, y, width, height, health);
        this.speed = speed;
        this.attackDamage = attackDamage;
        this.reward = reward;
        this.attackCooldown = 1500; // milliseconds
        this.lastActionTime = System.currentTimeMillis();
    }

    @Override
    public void update() {
        // Move left (towards plants)
        x -= speed;
        
        // Remove if off screen
        if (x < -width) {
            alive = false;
        }
    }

    public boolean canAttack() {
        return System.currentTimeMillis() - lastActionTime >= attackCooldown;
    }

    public void attack(Entity target) {
        if (target != null && canAttack()) {
            target.takeDamage(attackDamage);
            lastActionTime = System.currentTimeMillis();
        }
    }

    public int getAttackDamage() { return attackDamage; }
    public int getReward() { return reward; }
    public double getSpeed() { return speed; }
}
