package com.pvz.entities;

import java.util.Random;

public class Sun extends Entity {
    public static final int VALUE = 25;
    private double speed = 1.5;
    private double targetY;
    private double initialY;
    private boolean bouncePhase = false;
    private double bouncePeak;
    private boolean isFromPlant = false;
    private static final double FALL_SPEED = 1.5; // Tốc độ rơi cố định, không có gia tốc

    // Constructor cho Mặt trời rơi từ trên trời xuống
    public Sun(double x, double y) {
        // Kích thước 30x30 như bạn muốn
        super(x, y, 30, 30, 1); 
        
        Random rand = new Random();
        // Giả sử mỗi hàng cao khoảng 100 pixel, bắt đầu từ y=100
        // Ta chọn ngẫu nhiên một giá trị y từ 100 đến 500 để nó luôn nằm trên cỏ
        this.targetY = 100 + rand.nextInt(4) * 90; // 90 là khoảng cách giữa các hàng
        this.speed = 1.5;
    }

    // Constructor cho Sunflower: bounce up fast then fall ~100px
    public Sun(double x, double y, boolean fromPlant) {
        super(x, y, 30, 30, 1);
        this.isFromPlant = fromPlant;
        if (fromPlant) {
            this.initialY = y;
            this.bouncePhase = true;
            this.bouncePeak = y - 40; // Bounce up 40px fast
            this.targetY = y + 50; // Final fall to ~100px below start
            this.speed = 3.0; // Fast bounce speed
        } else {
            Random rand = new Random();
            this.targetY = 100 + rand.nextInt(4) * 90;
            this.speed = 1.5;
        }
    }

@Override
    public void update() {
        if (bouncePhase) {
            if (y > bouncePeak) {
                // Bounce up phase (fast)
                y -= speed;
            } else {
                // Bounce finished, start fall phase with constant speed (no gravity)
                bouncePhase = false;
                speed = FALL_SPEED; // Tốc độ rơi cố định
            }
        } 
        
        // Fall phase without gravity - constant speed
        if (!bouncePhase) {
            y += speed;
            if (y >= targetY) {
                y = targetY;
                speed = 0;
            }
        }
    }

    public int getValue() {
        return VALUE;
    }
}