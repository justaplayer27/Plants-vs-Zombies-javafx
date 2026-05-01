package com.pvz.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Wallnut extends Plant {
    public static final int HEALTH = 1500;

    public Wallnut(double x, double y) {
        super(x, y, 50, 60, HEALTH, 50, 0L); // Giảm kích thước xuống 50x60 để không chắn 2 hàng
    }

    public void render(GraphicsContext gc) {
        // Draw wallnut body (two layers for shell effect)
        gc.setFill(Color.SADDLEBROWN);
        gc.fillOval(x + 5, y + 5, width - 10, height - 10);
        gc.setFill(Color.BROWN);
        gc.fillOval(x + 10, y + 10, width - 20, height - 20);
        
        // Vẽ thanh máu (đã điều chỉnh vị trí và độ dày để dễ nhìn hơn)
        gc.setFill(Color.DARKRED);
        gc.fillRect(x, y - 10, width, 6);
        gc.setFill(Color.LIME);
        double healthPercent = Math.max(0, (double) health / HEALTH);
        gc.fillRect(x, y - 10, width * healthPercent, 6);
    }

}