package com.pvz.entities;

public abstract class Zombie extends Entity {
    protected double speed;
    protected long lastActionTime;
    protected long attackCooldown;
    protected int attackDamage;
    protected int reward; // Sun reward for defeating
    protected Plant eatingPlant = null; // Cây đang được ăn

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
        // Nếu đang ăn cây, không di chuyển
        if (eatingPlant != null) {
            if (!eatingPlant.isAlive()) {
                // Cây đã chết, tiếp tục di chuyển
                eatingPlant = null;
            } else {
                // Tiếp tục ăn cây
                attack(eatingPlant);
                return;
            }
        }
        
        // Di chuyển sang trái (về phía cây)
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

    // Bắt đầu ăn cây
    public void startEating(Plant plant) {
        this.eatingPlant = plant;
    }

    // Kiểm tra có đang ăn cây không
    public boolean isEating() {
        return eatingPlant != null && eatingPlant.isAlive();
    }

    public int getAttackDamage() { return attackDamage; }
    public int getReward() { return reward; }
    public double getSpeed() { return speed; }
}
