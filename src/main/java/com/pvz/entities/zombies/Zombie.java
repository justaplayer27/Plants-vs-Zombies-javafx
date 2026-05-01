package com.pvz.entities.zombies;

import com.pvz.entities.Entity;
import com.pvz.entities.Plant;

public abstract class Zombie extends Entity {
    protected double speed;
    protected long lastActionTime;
    protected long attackCooldown;
    protected int attackDamage;
    protected int reward; // Sun reward for defeating
    protected Plant eatingPlant = null; // Cây đang được ăn
    protected long stunEndTime = 0; // Thời điểm kết thúc trạng thái bất động
    protected boolean dying = false;
    protected long deathTime = 0;
    protected static final long DEATH_DURATION = 1500; // milliseconds

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
        if (isDying()) {
            if (isDeathAnimFinished()) {
                alive = false;
            }
            return;
        }

        // Nếu đang bị dính bơ (bất động), không làm gì cả
        if (isStunned()) {
            return;
        }

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
        x -= speed * 4.0;
        
        // Điều kiện thua: Zombie chạm vào nhà (x < 0)
        if (x < 0) {
            alive = false;
        }
    }

    @Override
    public boolean intersects(com.pvz.entities.Entity other) {
        // Đối với các thực thể nhỏ như Gà, sử dụng hitbox gốc để dễ trúng đạn/cây hơn
        if (this instanceof com.pvz.entities.Chicken) {
            return super.intersects(other);
        }

        // Thu hẹp hitbox của zombie (chỉ lấy 30% chiều rộng ở giữa)
        double zHitW = this.width * 0.3;
        double zHitX = this.x + (this.width - zHitW) / 2;

        double targetX = other.getX();
        double targetW = other.getWidth();
        double targetY = other.getY();
        double targetH = other.getHeight();

        // Nếu đối tượng bị va chạm là Cây (Plant), chỉ lấy nửa bên phải của cây
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
        // Phải không bị choáng hoặc đang chết mới có thể tấn công (ăn cây)
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

    // Sát thương từ phía trước (mặc định sẽ gọi takeDamage)
    public void takeFrontalDamage(int damage) {
        this.takeDamage(damage);
    }

    // Sát thương từ trên trời (vd: KernelPult)
    public void takeLobbedDamage(int damage) {
        this.takeDamage(damage);
    }

    // Sát thương từ dưới đất (vd: SpikeWeed)
    public void takeBelowDamage(int damage) {
        this.takeDamage(damage);
    }

    // Sát thương từ phía sau (vd: BonkChoy khi zombie đi qua)
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
