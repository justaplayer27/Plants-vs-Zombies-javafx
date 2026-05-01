package com.pvz.entities.zombies;

import com.pvz.entities.Chicken;
import com.pvz.game.GameBoard;

public class ChickenWranglerZombie extends Zombie {
    public static final int HEALTH = 360;
    private int chickensSpawnedCount = 0;
    private int chickensToSpawn = 0;
    private long lastSpawnTime = 0;
    private static final long SPAWN_DELAY = 600; // Khoảng cách giữa mỗi lần sinh gà (0.6s)
    private boolean releasingStarted = false;

    public ChickenWranglerZombie(double x, double y) {
        super(x, y, 40, 60, HEALTH, 0.045, 20, 35);
    }

    @Override
    public void update() {
        if (isStunned()) {
            return;
        }

        // Nếu đang trong quá trình giải phóng gà, zombie đứng yên
        if (chickensToSpawn > 0) {
            if (System.currentTimeMillis() - lastSpawnTime > SPAWN_DELAY) {
                spawnOneChicken();
                chickensToSpawn--;
                lastSpawnTime = System.currentTimeMillis();
            }
            // Không gọi super.update() để dừng hoàn toàn việc di chuyển và ăn cây
            return; 
        }
        super.update();
    }

    @Override
    public void takeDamage(int damage) {
        super.takeDamage(damage);
        if (chickensToSpawn == 0 && !releasingStarted) {
            startReleasingChickens();
        }
    }

    @Override
    public void takeFrontalDamage(int damage) {
        takeDamage(damage);
    }

    @Override
    public void takeLobbedDamage(int damage) {
        takeDamage(damage);
    }

    @Override
    public void takeBelowDamage(int damage) {
        takeDamage(damage);
    }

    @Override
    public void takeBehindDamage(int damage) {
        takeDamage(damage);
    }

    @Override
    public void startEating(com.pvz.entities.Plant plant) {
        if (!releasingStarted) {
            startReleasingChickens();
        }
        super.startEating(plant);
    }

    private void startReleasingChickens() {
        if (!releasingStarted) {
            releasingStarted = true;
            chickensToSpawn = 4; // Đặt hàng đợi sinh 4 con gà
            speed *= 1.5; // Tăng tốc độ di chuyển ngay khi bung gà
            lastSpawnTime = 0; // Đảm bảo con gà đầu tiên sinh ra ngay lập tức
        }
    }

    private void spawnOneChicken() {
        GameBoard board = GameBoard.getInstance();
        if (board != null) {
            Chicken chicken = new Chicken(this.getX() - 10, this.getY() + 20);
            board.addZombie(chicken); // Thêm vào danh sách zombie để tự động có logic tấn công và thanh máu
            board.addChicken(chicken); // Giữ lại danh sách riêng cho SpikeWeed và độ tương thích cũ
            chickensSpawnedCount++;
        }
    }

    public boolean isReleasing() {
        return chickensToSpawn > 0;
    }



    @Override
    public String toString() {
        return "ChickenWranglerZombie";
    }
}
