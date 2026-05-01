package com.pvz.entities.zombies;

import com.pvz.entities.Entity;
import com.pvz.entities.Plant;

public abstract class Zombie extends Entity {
    protected double speed;
    protected long lastActionTime;
    protected long attackCooldown;
    protected int attackDamage;
    protected int reward;
    protected Plant eatingPlant = null;
    protected long stunEndTime = 0;
    protected boolean dying = false;
    protected long deathTime = 0;
    protected static final long DEATH_DURATION = 1500;

    public Zombie(double x, double y, double width, double height, int health, 
                  double speed, int attackDamage, int reward) {
        super(x, y, width, height, health);
        this.speed = speed;
        this.attackDamage = attackDamage;
        this.reward = reward;
        this.attackCooldown = 1500;
        this.lastActionTime = System.currentTimeMillis();
    }

    @Override
    public void update() {
        if (isDying()) {
            if (isDeathAnimFinished()) {
                alive = false;
            }
            return;
        }

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
        
        x -= speed * 4.0;
        
        if (x < 0) {
            alive = false;
        }
    }

    @Override
    public boolean intersects(com.pvz.entities.Entity other) {
if (this instanceof com.pvz.entities.zombies.Chicken) {
            return super.intersects(other);
        }

        double zHitW = this.width * 0.3;
        double zHitX = this.x + (this.width - zHitW) / 2;

        double targetX = other.getX();
        double targetW = other.getWidth();
        double targetY = other.getY();
        double targetH = other.getHeight();

        if (other instanceof com.pvz.entities.Plant) {
            targetX = other.getX() + other.getWidth() * 0.5;
            targetW = other.getWidth() * 0.5;
        }

        return zHitX < targetX + targetW &&
               zHitX + zHitW > targetX &&
               this.y < targetY + targetH &&
               this.y + this.height > targetY;
    }

    public boolean canAttack() {
        return !isStunned() && !isDying() && System.currentTimeMillis() - lastActionTime >= attackCooldown;
    }

    public boolean isDying() {
        return dying;
    }

    public boolean isDeathAnimFinished() {
        return dying && System.currentTimeMillis() - deathTime >= DEATH_DURATION;
    }

    public void setDying(boolean dying) {
        this.dying = dying;
    }

    public void attack(Entity target) {
        if (target != null && canAttack()) {
            target.takeDamage(attackDamage);
            lastActionTime = System.currentTimeMillis();
        }
    }

    public void takeFrontalDamage(int damage) {
        this.takeDamage(damage);
    }

    public void takeLobbedDamage(int damage) {
        this.takeDamage(damage);
    }

    public void takeBelowDamage(int damage) {
        this.takeDamage(damage);
    }

    public void takeBehindDamage(int damage) {
        this.takeDamage(damage);
    }

    @Override
    public void takeDamage(int damage) {
        if (isDying()) {
            return;
        }

        super.takeDamage(damage);
        if (!alive && !dying) {
            dying = true;
            deathTime = System.currentTimeMillis();
            alive = true;
        }
    }

    public void stun(long durationMillis) {
        this.stunEndTime = System.currentTimeMillis() + durationMillis;
    }

    public boolean isStunned() {
        return System.currentTimeMillis() < stunEndTime;
    }

    public void startEating(Plant plant) {
        this.eatingPlant = plant;
    }

    public boolean isEating() {
        return eatingPlant != null && eatingPlant.isAlive();
    }

    public int getAttackDamage() { return attackDamage; }
    public int getReward() { return reward; }
    public double getSpeed() { return speed; }
}
