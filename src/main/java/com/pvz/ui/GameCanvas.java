package com.pvz.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.input.ClipboardContent;
import com.pvz.game.GameBoard;
import com.pvz.entities.Entity;
import com.pvz.entities.Mower;
import com.pvz.entities.Peashooter;
import com.pvz.entities.Plant;
import com.pvz.entities.Pea;
import com.pvz.entities.zombies.Chicken;
import com.pvz.entities.Sun;
import com.pvz.entities.Sunflower;
import com.pvz.entities.Wallnut;
import com.pvz.entities.SpikeWeed;
import com.pvz.entities.KernelPult;
import com.pvz.entities.BonkChoy;
import com.pvz.entities.zombies.*;

public class GameCanvas extends Canvas {
    private GameBoard gameBoard;
    private Plant selectedPlant;
    private String selectedZombieType;
    private String dragPlantType;
    private int dragHighlightX = -1;
    private int dragHighlightY = -1;
    private double mouseX = 0;
    private double mouseY = 0;
    private GraphicsContext gc;
    private Image backgroundImage;
    private Image mowerImage;
    private Image peaImage;
    private Image kernelPeaImage;
    private Image butterImage;
    private Image butterOverlayImage;
    private Image sunImage;
    private Image chickenIdle;
    private Image chickenDeath;
    private Image shovelImage;
    
    // Plant specific images
    private Image peashooterIdle;
    private Image peashooterAction;
    private Image sunflowerIdle;
    private Image sunflowerProduce;
    private Image wallnutIdle;
    private Image wallnutDegrade1;
    private Image wallnutDegrade3;
    private Image spikeWeedIdle;
    private Image spikeWeedAction;
    private Image kernelPultIdle;
    private Image kernelPultAction;
    private Image bonkChoyIdle;
    private Image bonkChoyAttackFront;
    private Image bonkChoyFinisherFront;
    private Image bonkChoyAttackBack;
    private Image bonkChoyFinisherBack;

    // Zombie specific images
    private Image basicZombieIdle;
    private Image basicZombieEating;
    private Image flagZombieIdle;
    private Image flagZombieEating;
    private Image allStarTackle;
    private Image allStarWalking;
    private Image allStarKick;
    private Image chickenWranglerNoChickens;
    private Image bonkChoyAttack2;
    private Image allStarRunning;
    private Image chickenWranglerIdle;
    private Image chickenWranglerEating;
    private Image chickenWranglerReleasing;
    private Image excavatorIdle;
    private Image excavatorEating;
    private Image basicZombieDeath;
    private Image basicZombieDeathAnim;
    private Image allStarZombieDeath;
    private Image chickenWranglerZombieDeath;
    private Image excavatorZombieDeath;
    private Image pharaohWalkingArmor;
    private Image pharaohEatingArmor;
private Image pharaohArmorDestroyed;
    private Image pharaohWalkingNoArmor;
    private Image pharaohEatingNoArmor;
    private Image pharaohDeath;
    private double gridOffsetX = 0;
    private double gridOffsetY = 0;
    private final String[] debugZombies = {"BasicZombie", "PharaohZombie", "FlagZombie", "AllStarZombie", "ExcavatorZombie", "ChickenWranglerZombie"};

    private String sunMessage = null;
    private double sunMsgX, sunMsgY;
    private int sunMessageTimer = 0;
    private static final int SUN_MSG_DURATION = 90; // Khoảng 1.5 giây ở 60fps

    private static class FlyingSun {
        double x, y;
        FlyingSun(double x, double y) { this.x = x; this.y = y; }
    }
    private final java.util.List<FlyingSun> flyingSuns = new java.util.ArrayList<>();

    public GameCanvas(GameBoard gameBoard) {
        super(800, 650);
        this.gameBoard = gameBoard;
        this.gc = this.getGraphicsContext2D();
        
        try {
            java.io.File fallbackPng = new java.io.File("src/main/resources/Backyard.png");
            java.io.InputStream     is = new java.io.FileInputStream(fallbackPng);
            backgroundImage = new Image(is);
        } catch (Exception e) {
            System.err.println("Could not load backyard image: " + e.getMessage());
        }

        try {
            peashooterIdle = loadImage("/Mobile - Plants vs. Zombies 2 - Peashooter - Idle.gif", 100, 120);
            peashooterAction = loadImage("/Mobile - Plants vs. Zombies 2 - Peashooter - Attack.gif", 100, 120);
            
            sunflowerIdle = loadImage("/Mobile - Plants vs. Zombies 2 - Sunflower - Idle.gif", 100, 120);
            sunflowerProduce = loadImage("/Mobile - Plants vs. Zombies 2 - Sunflower - Sun Produce.gif", 110, 132);
            
            wallnutIdle = loadImage("/Mobile - Plants vs. Zombies 2 - Wall-nut - Idle.gif", 120, 120);
            wallnutDegrade1 = loadImage("/Mobile - Plants vs. Zombies 2 - Wall-nut - Idle - Degrade 1.gif", 120, 120);
            wallnutDegrade3 = loadImage("/Mobile - Plants vs. Zombies 2 - Wall-nut - Idle - Degrade 3.gif", 120, 120);
            
            spikeWeedIdle = loadImage("/Mobile - Plants vs. Zombies 2 - Spikeweed - Idle - 2.gif", 100, 100);
            spikeWeedAction = loadImage("/Mobile - Plants vs. Zombies 2 - Spikeweed - Attack.gif", 100, 100);
            
            kernelPultIdle = loadImage("/Mobile - Plants vs. Zombies 2 - Kernel-pult - Idle.gif", 110, 130);
            kernelPultAction = loadImage("/Mobile - Plants vs. Zombies 2 - Kernel-pult - Attack.gif", 150, 177);
            
            bonkChoyIdle = loadImage("/Mobile - Plants vs. Zombies 2 - Bonk Choy - Idle - 2.gif", 110, 130);
            bonkChoyAttackFront = loadImage("/Mobile - Plants vs. Zombies 2 - Bonk Choy - Attack.gif", 150, 130);
            bonkChoyFinisherFront = loadImage("/Mobile - Plants vs. Zombies 2 - Bonk Choy - Attack - 4.gif", 150, 130);
bonkChoyAttackBack = loadImage("/Mobile - Plants vs. Zombies 2 - Bonk Choy - Attack - 2.gif", 150, 130);
            bonkChoyFinisherBack = loadImage("/Mobile - Plants vs. Zombies 2 - Bonk Choy - Attack - 5.gif", 150, 130);

            basicZombieIdle = loadImage("/Mobile - Plants vs. Zombies 2 - Basic Zombie - Walking.gif", 200, 216);
            basicZombieEating = loadImage("/Mobile - Plants vs. Zombies 2 - Basic Zombie - Eating.gif", 168, 216);
            flagZombieIdle = loadImage("/Mobile - Plants vs. Zombies 2 - Basic Zombie - Walking - Flag Zombie.gif", 182, 234);
            flagZombieEating = loadImage("/Mobile - Plants vs. Zombies 2 - Basic Zombie - Eating - Flag Zombie.gif", 182, 234);
            allStarRunning = loadImage("/Mobile - Plants vs. Zombies 2 - All-Star Zombie - Running.gif", 270, 390);
            allStarTackle = loadImage("/Mobile - Plants vs. Zombies 2 - All-Star Zombie - Tackle.gif", 270, 390);
            allStarKick = loadImage("/Mobile - Plants vs. Zombies 2 - All-Star Zombie - Kick.gif", 270, 390);
            allStarWalking = loadImage("/Mobile - Plants vs. Zombies 2 - All-Star Zombie - Walking.gif", 270, 390);
            chickenWranglerIdle = loadImage("/Mobile - Plants vs. Zombies 2 - Chicken Wrangler Zombie - Walking.gif", 168, 216);
            chickenWranglerEating = loadImage("/Mobile - Plants vs. Zombies 2 - Chicken Wrangler Zombie - Eating.gif", 168, 216);
            chickenWranglerReleasing = loadImage("/Mobile - Plants vs. Zombies 2 - Chicken Wrangler Zombie - Plague Pharmacy - Without Chickens - Battle (Chinese Only - Removed).gif", 175, 225);
            excavatorIdle = loadImage("/Mobile - Plants vs. Zombies 2 - Excavator Zombie - Walking.gif", 168, 216);
            excavatorEating = loadImage("/Mobile - Plants vs. Zombies 2 - Excavator Zombie - Eating.gif", 168, 216);
            chickenIdle = loadImage("/Mobile - Plants vs. Zombies 2 - Zombie Chicken - Idle_Walking.gif", 100, 120);
            chickenWranglerNoChickens = loadImage("/Mobile - Plants vs. Zombies 2 - Chicken Wrangler Zombie - Walking - Without Chickens.gif", 168, 216);

            basicZombieDeath = loadImage("/Mobile - Plants vs. Zombies 2 - Basic Zombie - Death - Flag Zombie.gif", 140, 180);
            allStarZombieDeath = loadImage("/Mobile - Plants vs. Zombies 2 - All-Star Zombie - Death.gif", 270, 390);
            basicZombieDeathAnim = loadImage("/Mobile - Plants vs. Zombies 2 - Basic Zombie - Death.gif", 140, 180);
            chickenWranglerZombieDeath = loadImage("/Mobile - Plants vs. Zombies 2 - Chicken Wrangler Zombie - Death.gif", 140, 190);
            excavatorZombieDeath = loadImage("/Mobile - Plants vs. Zombies 2 - Excavator Zombie - Death.gif", 140, 180);
            pharaohWalkingArmor = loadImage("/Mobile - Plants vs. Zombies 2 - Pharaoh Zombie - Walking - Sarcophagus.gif", 182, 234);
pharaohEatingArmor = loadImage("/Mobile - Plants vs. Zombies 2 - Pharaoh Zombie - Eating - Sarcophagus.gif", 182, 234);
            pharaohArmorDestroyed = loadImage("/Mobile - Plants vs. Zombies 2 - Pharaoh Zombie - Destroyed - Sarcophagus.gif", 700, 900);
            pharaohWalkingNoArmor = loadImage("/Mobile - Plants vs. Zombies 2 - Pharaoh Zombie - Walking.gif", 168, 216);
            pharaohEatingNoArmor = loadImage("/Mobile - Plants vs. Zombies 2 - Pharaoh Zombie - Eating.gif", 168, 216);
            pharaohDeath = loadImage("/Mobile - Plants vs. Zombies 2 - Pharaoh Zombie - Death.gif", 140, 180);

            shovelImage = loadImage("/111px-Shovel2.png", 100, 100);
            mowerImage = loadImage("/120px-Lawn_mower_2.png");
            peaImage = loadImage("/Pea.png");
            kernelPeaImage = loadImage("/Kernel_2.png");
            butterImage = loadImage("/Butter_2.png");
            butterOverlayImage = loadImage("/Butter on face.png");
            sunImage = loadImage("/Sun_PvZ2.png");
            chickenDeath = loadImage("/Mobile - Plants vs. Zombies 2 - Zombie Chicken - Feather Burst.gif", 100, 120);
        } catch (Exception e) {
            System.err.println("Could not load object animations: " + e.getMessage());
        }

        setOnMouseClicked(this::handleMouseClick);
        setOnMouseMoved(this::handleMouseMove);
        setOnDragOver(this::handleDragOver);
        setOnDragDropped(this::handleDragDropped);
        setOnDragExited(this::handleDragExited);
        
        setFocusTraversable(true);
        setOnKeyPressed(this::handleKeyPressed);
    }

    private Image loadImage(String path) {
        return loadImage(path, 0, 0);
    }

    private Image loadImage(String path, double requestedWidth, double requestedHeight) {
        try {
            java.io.InputStream is = getClass().getResourceAsStream(path);
            if (is == null) {
                java.io.File fallbackFile = new java.io.File("src/main/resources" + path);
                if (fallbackFile.exists()) {
                    is = new java.io.FileInputStream(fallbackFile);
                }
            }
            if (is != null) {
                Image image;
                if (requestedWidth > 0 || requestedHeight > 0) {
                    image = new Image(is);
                } else {
                    image = new Image(is);
                }
                is.close();
                return image;
            }
        } catch (OutOfMemoryError oom) {
            System.err.println("Image load failed: out of heap for " + path);
        } catch (Exception ignored) {
        }
        return null;
    }

    private Image shovelDragImage;

    private void loadShovelDragImage() {
        if (shovelDragImage == null) {
            shovelDragImage = loadImage("/111px-Shovel2.png", 40, 40);
        }
    }

    public void render() {
        int cellWidth = GameBoard.getCellWidth();
int cellHeight = GameBoard.getCellHeight();
        int gridWidth = GameBoard.getGridWidth() * cellWidth;
        int gridHeight = GameBoard.getGridHeight() * cellHeight;
        double canvasW = getWidth();
        double canvasH = getHeight();

        double desiredX = canvasW * 0.34;
        double maxX = Math.max(0, canvasW - gridWidth - 20);
        gridOffsetX = Math.min(Math.max(0, desiredX), maxX);

        double desiredY = canvasH * 0.18;
        double maxY = Math.max(0, canvasH - gridHeight - 20);
        gridOffsetY = Math.min(Math.max(0, desiredY), maxY);

        gc.clearRect(0, 0, canvasW, canvasH);
        if (backgroundImage != null) {
            double imgW = backgroundImage.getWidth();
            double imgH = backgroundImage.getHeight();
            double zoom = 1.34;
            double scale = Math.max(canvasW / imgW, canvasH / imgH) * zoom;
            double srcW = canvasW / scale;
            double srcH = canvasH / scale;
            double srcX = Math.min(Math.max(0, (imgW - srcW) * 0.19), imgW - srcW);
            double srcY = Math.min(Math.max(0, (imgH - srcH) * 0.59), imgH - srcH);
            gc.drawImage(backgroundImage, srcX, srcY, srcW, srcH,
                    0, 0, canvasW, canvasH);
        }

        // drawGrid();
        drawDragHighlight();

        for (Mower mower : gameBoard.getMowers()) {
            drawMower(mower);
        }

        for (Plant plant : gameBoard.getPlants()) {
            drawPlant(plant);
        }

        for (Zombie zombie : gameBoard.getZombies()) {
            drawZombie(zombie);
        }

        for (Pea pea : gameBoard.getProjectiles()) {
            drawPea(pea);
        }

        for (Sun sun : gameBoard.getSuns()) {
            drawSun(sun);
        }

        drawFlyingSuns();

        if (selectedPlant != null) {
            drawSelectedPlantPreview();
        }

        if (dragPlantType != null && "Shovel".equals(dragPlantType) && shovelImage != null) {
            gc.setFill(Color.rgb(0, 0, 0, 0.5));
            gc.fillOval(mouseX - 35, mouseY - 35, 70, 70);
            gc.drawImage(shovelImage, mouseX - 35, mouseY - 35, 70, 70);
        }

        if (gameBoard.isDebugMode() || (gameBoard.getLevelConfig() != null && gameBoard.getLevelConfig().getLevelNumber() == 6)) {
            drawZombieSelectionBar();
        }

        // Vẽ thông báo không đủ nắng (Not enough sun) mờ dần
        if (sunMessageTimer > 0 && sunMessage != null) {
            double alpha = Math.min(1.0, sunMessageTimer / 30.0); // Mờ dần trong 30 khung hình cuối
            gc.setFill(Color.rgb(255, 0, 0, alpha));
            gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 22));
            gc.fillText(sunMessage, sunMsgX - 60, sunMsgY);
            sunMessageTimer--;
        }
    }

    private void drawZombieSelectionBar() {
        double barWidth = debugZombies.length * 80 + 20;
        double barX = (getWidth() - barWidth) / 2;
double barY = getHeight() - 95; // Đặt ở gần sát mép dưới

        // Vẽ nền cho thanh công cụ
        gc.setFill(Color.rgb(40, 40, 40, 0.8));
        gc.fillRoundRect(barX, barY, barWidth, 85, 15, 15);
        gc.setStroke(Color.GOLD);
        gc.setLineWidth(3);
        gc.strokeRoundRect(barX, barY, barWidth, 85, 15, 15);

        for (int i = 0; i < debugZombies.length; i++) {
            String type = debugZombies[i];
            double btnX = barX + 10 + i * 80;
            double btnY = barY + 10;

            // Highlight nếu zombie này đang được chọn
            if (type.equals(selectedZombieType)) {
                gc.setFill(Color.rgb(255, 215, 0, 0.5));
                gc.fillRoundRect(btnX, btnY, 70, 65, 10, 10);
            } else {
                gc.setFill(Color.rgb(70, 70, 70, 0.9));
                gc.fillRoundRect(btnX, btnY, 70, 65, 10, 10);
            }

            // Vẽ icon Zombie
            Image img = getZombieIdleImageByType(type);
            if (img != null) {
                double iconSize = 55;
                gc.drawImage(img, btnX + (70 - iconSize) / 2.0, btnY + (65 - iconSize) / 2.0, iconSize, iconSize);
            }
            
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(1);
            gc.strokeRoundRect(btnX, btnY, 70, 65, 10, 10);
        }
    }

    private Image getZombieIdleImageByType(String type) {
        switch (type) {
            case "BasicZombie": return basicZombieIdle;
            case "PharaohZombie": return pharaohWalkingArmor;
            case "FlagZombie": return flagZombieIdle;
            case "AllStarZombie": return allStarWalking;
            case "ExcavatorZombie": return excavatorIdle;
            case "ChickenWranglerZombie": return chickenWranglerIdle;
            default: return null;
        }
    }

    private void drawFlyingSuns() {
        java.util.Iterator<FlyingSun> it = flyingSuns.iterator();
        double targetX = -gridOffsetX + 30;
        double targetY = -gridOffsetY + 30; 
        
        while (it.hasNext()) {
            FlyingSun fs = it.next();
            double dx = targetX - fs.x;
            double dy = targetY - fs.y;
            double dist = Math.sqrt(dx * dx + dy * dy);
            
            if (dist < 15) {
                it.remove();
            } else {
                fs.x += dx * 0.2;
                fs.y += dy * 0.2;
                if (sunImage != null) {
                    gc.drawImage(sunImage, tx(fs.x), ty(fs.y), 40, 40);
                }
            }
        }
    }

    // private void drawGrid() {
    //     gc.setStroke(Color.rgb(255, 255, 255, 0.3));
    //     gc.setLineWidth(1);
        
    //     int cellWidth = GameBoard.getCellWidth();
    //     int cellHeight = GameBoard.getCellHeight();
    //     int width = GameBoard.getGridWidth();
    //     int height = GameBoard.getGridHeight();

    //     for (int i = 0; i <= width; i++) {
//         gc.strokeLine(tx(i * cellWidth), ty(0), tx(i * cellWidth), ty(height * cellHeight));
    //     }
    //     for (int i = 0; i <= height; i++) {
    //         gc.strokeLine(tx(0), ty(i * cellHeight), tx(width * cellWidth), ty(i * cellHeight));
    //     }
    // }

    private double tx(double x) {
        return x + gridOffsetX;
    }

    private double ty(double y) {
        return y + gridOffsetY;
    }

    private void drawPlant(Plant plant) {
        int cellWidth = GameBoard.getCellWidth();
        int cellHeight = GameBoard.getCellHeight();
        
        double plantShift = 5;
        double shadowShift = 15;

        if (!(plant instanceof SpikeWeed)) {
            double shadowWidth = plant.getWidth() * 1.6;
            double shadowHeight = 14;
            double shadowY = plant.getY() + (cellHeight - plant.getHeight()) / 2 + plant.getHeight() + shadowShift;
            
            javafx.scene.paint.RadialGradient shadowGradient = new javafx.scene.paint.RadialGradient(
                0, 0, 0.45, 0.6, 0.5, true, javafx.scene.paint.CycleMethod.NO_CYCLE,
                new javafx.scene.paint.Stop(0, Color.rgb(0, 0, 0, 0.9)),
                new javafx.scene.paint.Stop(1, Color.TRANSPARENT)
            );
            
            gc.setFill(shadowGradient);
            gc.fillOval(tx(plant.getX() + (cellWidth - shadowWidth) / 2), 
                       ty(shadowY - shadowHeight / 2), 
                       shadowWidth, shadowHeight);
        }

        Image plantImg = getPlantImage(plant);
        double offsetX = (cellWidth - plant.getWidth()) / 2;
        double offsetY = (cellHeight - plant.getHeight()) / 2;
        double baseY = plant instanceof SpikeWeed ? plant.getY() : plant.getY() + offsetY + plantShift;

        if (plantImg != null) {
            double scale = (plant instanceof Wallnut || plant instanceof Sunflower) ? 1.6 : 2.2;
            double visualWidth = plant.getWidth() * scale;
            double visualHeight = plant.getHeight() * scale;
            double vOffsetX = (cellWidth - visualWidth) / 2;
            double vOffsetY = plant instanceof SpikeWeed ? (plant.getHeight() - visualHeight) : (cellHeight - visualHeight) / 2 + plantShift;
            gc.drawImage(plantImg, tx(plant.getX() + vOffsetX), ty(plant.getY() + vOffsetY), visualWidth, visualHeight);
        } else {
            if (plant instanceof Peashooter) gc.setFill(Color.GREEN);
            else if (plant instanceof SpikeWeed) gc.setFill(Color.DARKOLIVEGREEN);
            else if (plant instanceof KernelPult) gc.setFill(Color.DARKGOLDENROD);
            else if (plant instanceof BonkChoy) gc.setFill(Color.LIGHTBLUE);
            else if (plant instanceof Wallnut) gc.setFill(Color.BROWN);
            else if (plant instanceof Sunflower) gc.setFill(Color.GOLD);
            else gc.setFill(Color.LIGHTGREEN);

            gc.fillRect(tx(plant.getX() + offsetX), ty(baseY), plant.getWidth(), plant.getHeight());
        }
drawHealthBar(plant, offsetX, plant instanceof SpikeWeed ? 0 : offsetY + plantShift);
    }

    private Image getPlantImage(Plant plant) {
        if (plant instanceof Sunflower) {
            return ((Sunflower) plant).isProducing() ? sunflowerProduce : sunflowerIdle;
        }
        if (plant instanceof Wallnut) {
            double hpPercent = (double) plant.getHealth() / Wallnut.HEALTH;
            if (hpPercent < 0.25) return wallnutDegrade3 != null ? wallnutDegrade3 : wallnutIdle;
            if (hpPercent < 0.50) return wallnutDegrade1 != null ? wallnutDegrade1 : wallnutIdle;
            return wallnutIdle;
        }
        if (plant instanceof Peashooter) {
            return plant.isRecentlyActive(900) ? peashooterAction : peashooterIdle;
        }
        if (plant instanceof KernelPult) {
            return plant.isRecentlyActive(900) ? kernelPultAction : kernelPultIdle;
        }
        if (plant instanceof BonkChoy) {
            if (plant.isRecentlyActive(900)) {
                Zombie target = gameBoard.findNearestZombie(plant);
                if (target == null) {
                    for (Zombie z : gameBoard.getZombies()) {
                        if (Math.abs(z.getY() - plant.getY()) < GameBoard.getCellHeight() / 3.0 && Math.abs(z.getX() - plant.getX()) < GameBoard.getCellWidth() * 1.5) {
                            target = z;
                            break;
                        }
                    }
                }
                if (target != null) {
                    boolean isFront = target.getX() > plant.getX();
                    boolean isFinisher = target.getHealth() <= 25;
                    if (isFront) return isFinisher ? bonkChoyFinisherFront : bonkChoyAttackFront;
                    else return isFinisher ? bonkChoyFinisherBack : bonkChoyAttackBack;
                }
                return bonkChoyAttackFront;
            }
            return bonkChoyIdle;
        }
        if (plant instanceof SpikeWeed) {
            return plant.isRecentlyActive(900) ? spikeWeedAction : spikeWeedIdle;
        }
        return null;
    }

    private void drawZombie(Zombie zombie) {
        Image zombieImg = getZombieImage(zombie);
        int cellHeight = GameBoard.getCellHeight();
        
        double zombieVisualXOffset = -15;
        double zombieVisualYOffset = -15;

        double verticalShift = 5;
        double zombieShadowShift = 12;
        double offsetY = (cellHeight - zombie.getHeight()) / 2 + verticalShift;
        double visualWidth = zombie.getWidth() * 2.2;
        double visualHeight = zombie.getHeight() * 2.2;
        double vOffsetY = (cellHeight - visualHeight) / 2 + verticalShift + zombieVisualYOffset;

        double shadowWidth = zombie.getWidth() * 1.5;
        double shadowHeight = 14;
        double zombieBottomY = zombie.getY() + offsetY + zombie.getHeight() + zombieShadowShift;
javafx.scene.paint.RadialGradient shadowGradient = new javafx.scene.paint.RadialGradient(
            0, 0, 0.6, 0.6, 0.45, true, javafx.scene.paint.CycleMethod.NO_CYCLE,
            new javafx.scene.paint.Stop(0, Color.rgb(0, 0, 0, 0.55)),
            new javafx.scene.paint.Stop(1, Color.TRANSPARENT)
        );
        
        gc.setFill(shadowGradient);
        gc.fillOval(tx(zombie.getX() + (zombie.getWidth() - shadowWidth) / 2), 
                   ty(zombieBottomY - shadowHeight / 2), 
                   shadowWidth, shadowHeight);

        if (zombieImg != null) {
            if (zombie.isDying()) {
                gc.drawImage(zombieImg, tx(zombie.getX() + zombieVisualXOffset), ty(zombie.getY() + vOffsetY), visualWidth, visualHeight);
            } else if (zombie instanceof AllStarZombie) {
                AllStarZombie az = (AllStarZombie) zombie;
                Image currentImg;
                if (az.isCharging()) {
                    currentImg = allStarTackle;
                } else if (az.isEating()) {
                    currentImg = allStarKick;
                } else if (zombie.getSpeed() > 0.3) {
                    currentImg = allStarRunning;
                } else {
                    currentImg = allStarWalking;
                }
                if (currentImg != null) {
                    gc.drawImage(currentImg, tx(zombie.getX() + zombieVisualXOffset), ty(zombie.getY() + vOffsetY), visualWidth, visualHeight);
                }
            } else {
                gc.drawImage(zombieImg, tx(zombie.getX() + zombieVisualXOffset), ty(zombie.getY() + vOffsetY), visualWidth, visualHeight);
            }
        } else if (!zombie.isDying()) {
            if (zombie instanceof FlagZombie) {
                gc.setFill(Color.GOLD);
            } else if (zombie instanceof AllStarZombie) {
                gc.setFill(Color.DARKRED);
            } else if (zombie instanceof ChickenWranglerZombie) {
                gc.setFill(Color.DARKORANGE);
            } else if (zombie instanceof ExcavatorZombie) {
                gc.setFill(Color.DARKOLIVEGREEN);
            } else if (zombie instanceof PharaohZombie) {
                gc.setFill(Color.SILVER);
            } else {
                gc.setFill(Color.GRAY);
            }
            gc.fillRect(tx(zombie.getX() + zombieVisualXOffset), ty(zombie.getY() + offsetY + zombieVisualYOffset), zombie.getWidth(), zombie.getHeight());
            gc.setFill(Color.RED);
            gc.fillOval(tx(zombie.getX() + 5 + zombieVisualXOffset), ty(zombie.getY() + 10 + zombieVisualYOffset), 8, 8);
            gc.fillOval(tx(zombie.getX() + 25 + zombieVisualXOffset), ty(zombie.getY() + 10 + zombieVisualYOffset), 8, 8);
        }

        if (zombie.isStunned() && butterOverlayImage != null) {
            gc.drawImage(butterOverlayImage, tx(zombie.getX() + 10 + zombieVisualXOffset), ty(zombie.getY() + vOffsetY - 10), zombie.getWidth() * 1.5, zombie.getWidth() * 1.5);
        }
String type = "Basic";
        if (zombie instanceof FlagZombie) type = "Flag";
        else if (zombie instanceof AllStarZombie) type = "AllStar";
        else if (zombie instanceof ChickenWranglerZombie) type = "Chicken";
        else if (zombie instanceof ExcavatorZombie) type = "Excavator";
        else if (zombie instanceof PharaohZombie) type = "Pharaoh";
        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font(10));
        gc.fillText(type, tx(zombie.getX() + zombieVisualXOffset), ty(zombie.getY() + offsetY + zombieVisualYOffset - 5));

        drawHealthBar(zombie, zombieVisualXOffset, offsetY + zombieVisualYOffset);
    }

    private Image getZombieImage(Zombie zombie) {
        if (zombie instanceof PharaohZombie) {
            PharaohZombie pz = (PharaohZombie) zombie;
            if (pz.isDying()) {
                return pz.isDeathAnimFinished() ? null : pharaohDeath;
            }
            if (pz.isArmorBreakAnim()) return pharaohArmorDestroyed;
            if (zombie.isEating()) return !pz.isArmorBroken() ? pharaohEatingArmor : pharaohEatingNoArmor;
            return !pz.isArmorBroken() ? pharaohWalkingArmor : pharaohWalkingNoArmor;
        }

        if (zombie.isDying()) {
            if (zombie.isDeathAnimFinished()) return null;
            if (zombie instanceof AllStarZombie) return allStarZombieDeath;
            if (zombie instanceof ChickenWranglerZombie) return chickenWranglerZombieDeath;
            if (zombie instanceof ExcavatorZombie) return excavatorZombieDeath;
            if (zombie instanceof FlagZombie) return basicZombieDeath;
            if (zombie instanceof Chicken) return chickenDeath;
            return basicZombieDeathAnim;
        }

        if (zombie.isEating()) {
            if (zombie instanceof BasicZombie) return basicZombieEating;
            if (zombie instanceof FlagZombie) return flagZombieEating;
            if (zombie instanceof AllStarZombie) return allStarKick;
            if (zombie instanceof ChickenWranglerZombie) {
                ChickenWranglerZombie cw = (ChickenWranglerZombie) zombie;
                return cw.isReleasing() ? chickenWranglerReleasing : chickenWranglerEating;
            }
            if (zombie instanceof ExcavatorZombie) return excavatorEating;
        }

        if (zombie instanceof BasicZombie) return basicZombieIdle;
        if (zombie instanceof FlagZombie) return flagZombieIdle;
        if (zombie instanceof AllStarZombie) return allStarWalking;
        if (zombie instanceof ChickenWranglerZombie) {
            ChickenWranglerZombie cw = (ChickenWranglerZombie) zombie;
            if (cw.isReleasing()) return chickenWranglerReleasing;
            return cw.hasReleasingStarted() ? chickenWranglerNoChickens : chickenWranglerIdle;
        }
        if (zombie instanceof ExcavatorZombie) return excavatorIdle;
        if (zombie instanceof Chicken) return chickenIdle;

        return basicZombieIdle;
    }
private void showSunMessage(double x, double y) {
        this.sunMessage = "Not enough sun!";
        this.sunMsgX = x;
        this.sunMsgY = y;
        this.sunMessageTimer = SUN_MSG_DURATION;
    }

    private void drawPea(Pea pea) {
        Image currentPeaImg = peaImage;
        if (pea.isLobbed()) {
            currentPeaImg = pea.isButter() ? butterImage : kernelPeaImage;
        }

        if (currentPeaImg != null) {
            double visualWidth = pea.getWidth() * 2.0;
            double visualHeight = pea.getHeight() * 2.0;
            gc.drawImage(currentPeaImg, tx(pea.getX()), ty(pea.getVisualY()), visualWidth, visualHeight);
            return;
        }
        gc.setFill(Color.YELLOW);
        gc.fillOval(tx(pea.getX()), ty(pea.getVisualY()), pea.getWidth(), pea.getHeight());
    }

    private void drawSun(Sun sun) {
        if (sunImage != null) {
            gc.drawImage(sunImage, tx(sun.getX()), ty(sun.getY()), sun.getWidth() * 1.8, sun.getHeight() * 1.8);
            return;
        }
        gc.setFill(Color.GOLD);
        gc.fillOval(tx(sun.getX()), ty(sun.getY()), sun.getWidth(), sun.getHeight());
        gc.setStroke(Color.ORANGE);
        gc.setLineWidth(2);
        double centerX = tx(sun.getX() + sun.getWidth() / 2);
        double centerY = ty(sun.getY() + sun.getHeight() / 2);
        double radius = sun.getWidth() / 2;
        for (int i = 0; i < 8; i++) {
            double angle = (Math.PI / 4) * i;
            double x1 = centerX + radius * Math.cos(angle);
            double y1 = centerY + radius * Math.sin(angle);
            double x2 = centerX + (radius + 5) * Math.cos(angle);
            double y2 = centerY + (radius + 5) * Math.sin(angle);
            gc.strokeLine(x1, y1, x2, y2);
        }
    }

    private void drawMower(Mower mower) {
        if (mowerImage != null) {
            gc.drawImage(mowerImage, tx(mower.getX() - 35), ty(mower.getY()), mower.getWidth() * 2.0, mower.getHeight() * 2.0);
            return;
        }
        gc.setFill(Color.DIMGRAY);
        gc.fillRect(tx(mower.getX() - 35), ty(mower.getY()), mower.getWidth(), mower.getHeight());
        gc.setFill(Color.SILVER);
        gc.fillRect(tx(mower.getX() - 25), ty(mower.getY() + 8), mower.getWidth() - 10, mower.getHeight() - 16);
    }

    private void drawHealthBar(Entity entity, double offsetX, double offsetY) {
        int maxHealth = 100;
        if (entity instanceof Peashooter) maxHealth = Peashooter.HEALTH;
        else if (entity instanceof Sunflower) maxHealth = Sunflower.HEALTH;
        else if (entity instanceof Wallnut) maxHealth = Wallnut.HEALTH;
        else if (entity instanceof SpikeWeed) maxHealth = SpikeWeed.HEALTH;
        else if (entity instanceof KernelPult) maxHealth = KernelPult.HEALTH;
        else if (entity instanceof BonkChoy) maxHealth = BonkChoy.HEALTH;
        else if (entity instanceof Chicken) maxHealth = Chicken.MAX_HEALTH;
        else if (entity instanceof Zombie) {
if (entity instanceof BasicZombie) {
                maxHealth = BasicZombie.HEALTH;
            } else if (entity instanceof FlagZombie) {
                maxHealth = FlagZombie.HEALTH;
            } else if (entity instanceof AllStarZombie) {
                maxHealth = AllStarZombie.HEALTH;
            } else if (entity instanceof ChickenWranglerZombie) {
                maxHealth = ChickenWranglerZombie.HEALTH;
            } else if (entity instanceof ExcavatorZombie) {
                maxHealth = ExcavatorZombie.HEALTH;
            } else if (entity instanceof PharaohZombie) {
                maxHealth = PharaohZombie.BASE_HEALTH;
            } else {
                maxHealth = 100;
            }
        }

        double healthPercent = Math.max(0, Math.min(1, (double) Math.max(entity.getHealth(), 0) / maxHealth));
        
        gc.setFill(Color.DARKRED);
        gc.fillRect(tx(entity.getX() + offsetX), ty(entity.getY() + offsetY - 15), entity.getWidth(), 4);
        
        // Special armor bar
        if (entity instanceof PharaohZombie) {
            PharaohZombie pz = (PharaohZombie) entity;
            if (!pz.isArmorBroken()) {
                double armorPercent = Math.max(0, Math.min(1, (double) Math.max(pz.getArmorHealth(), 0) / 300));
                gc.setFill(Color.CYAN);
                gc.fillRect(tx(entity.getX() + offsetX), ty(entity.getY() + offsetY - 22), entity.getWidth() * armorPercent, 4);
            }
        }
        
        if (entity instanceof PharaohZombie) {
            gc.setFill(Color.LIME);
        } else {
            gc.setFill(Color.ORANGE);
        }
        gc.fillRect(tx(entity.getX() + offsetX), ty(entity.getY() + offsetY - 15), entity.getWidth() * healthPercent, 4);
    }

    private void drawSelectedPlantPreview() {
        if (selectedPlant == null) {
            return;
        }

        double boardX = screenToBoardX(mouseX);
        double boardY = screenToBoardY(mouseY);
        int cellWidth = GameBoard.getCellWidth();
        int cellHeight = GameBoard.getCellHeight();
        int gridX = (int) (boardX / cellWidth);
        int gridY = (int) (boardY / cellHeight);

        if (gridX < 0 || gridX >= GameBoard.getGridWidth() || gridY < 0 || gridY >= GameBoard.getGridHeight()) {
            return;
        }

        double x = tx(gridX * cellWidth);
        double y = ty(gridY * cellHeight);
        gc.setFill(Color.rgb(255, 255, 255, 0.18));
        gc.fillRoundRect(x, y, cellWidth, cellHeight, 18, 18);
        gc.setStroke(Color.rgb(255, 255, 255, 0.8));
        gc.setLineWidth(2);
        gc.strokeRoundRect(x + 1.5, y + 1.5, cellWidth - 3, cellHeight - 3, 18, 18);
    }

    private double screenToBoardX(double screenX) {
        return screenX - gridOffsetX;
    }

    private double screenToBoardY(double screenY) {
        return screenY - gridOffsetY;
    }

    private void handleMouseClick(MouseEvent event) {
        if (gameBoard.isPaused()) {
            return;
        }
// Kiểm tra xem có click vào thanh chọn Zombie hay không
        if (gameBoard.isDebugMode() || (gameBoard.getLevelConfig() != null && gameBoard.getLevelConfig().getLevelNumber() == 6)) {
            double barWidth = debugZombies.length * 80 + 20;
            double barX = (getWidth() - barWidth) / 2;
            double barY = getHeight() - 95;

            if (event.getX() >= barX && event.getX() <= barX + barWidth &&
                event.getY() >= barY && event.getY() <= barY + 85) {
                int index = (int) ((event.getX() - barX - 10) / 80);
                if (index >= 0 && index < debugZombies.length) {
                    setSelectedZombieType(debugZombies[index]);
                }
                event.consume();
                return;
            }
        }

        double boardX = screenToBoardX(event.getX());
        double boardY = screenToBoardY(event.getY());
        int cellWidth = GameBoard.getCellWidth();
        int cellHeight = GameBoard.getCellHeight();
        int gridX = (int) (boardX / cellWidth);
        int gridY = (int) (boardY / cellHeight);

        if (gridX < 0 || gridX >= GameBoard.getGridWidth() || gridY < 0 || gridY >= GameBoard.getGridHeight()) {
            return;
        }

        if (event.getButton() == MouseButton.PRIMARY) {
            Sun hitSun = null;
            for (Sun s : gameBoard.getSuns()) {
                double centerX = s.getX() + s.getWidth() / 2;
                double centerY = s.getY() + s.getHeight() / 2;
                double dist = Math.sqrt(Math.pow(boardX - centerX, 2) + Math.pow(boardY - centerY, 2));
                if (dist < 65) {
                    hitSun = s;
                    break;
                }
            }

            if (hitSun != null) {
                flyingSuns.add(new FlyingSun(hitSun.getX(), hitSun.getY()));
                gameBoard.collectSunAt(hitSun.getX() + hitSun.getWidth()/2, hitSun.getY() + hitSun.getHeight()/2);
                event.consume();
                return;
            }

            if (selectedZombieType != null) {
                double spawnX = gridX * GameBoard.getCellWidth();
                spawnX = Math.max(0, Math.min(spawnX, GameBoard.getGridWidth() * GameBoard.getCellWidth() - 1));
                double spawnY = gridY * GameBoard.getCellHeight() + 10;
                gameBoard.addZombie(gameBoard.spawnZombieByType(selectedZombieType, spawnX, spawnY));
                event.consume();
                return;
            }

            if (selectedPlant != null) {
                Plant newPlant = createPlantCopy(selectedPlant);
                if (!gameBoard.isFreePlantsMode() && gameBoard.getSun() < newPlant.getCost()) {
                    showSunMessage(event.getX(), event.getY());
                    event.consume();
                    return;
                }
                if (gameBoard.plantAt(newPlant, gridX, gridY)) {
                    setSelectedPlant(null);
                    event.consume();
}
            }
        } else if (event.getButton() == MouseButton.SECONDARY) {
            gameBoard.removePlantAt(boardX, boardY);
            event.consume();
        }
    }

    private void handleKeyPressed(javafx.scene.input.KeyEvent event) {
        boolean isCustomLevel = gameBoard.getLevelConfig() != null && gameBoard.getLevelConfig().getLevelNumber() == 6;
        if (!isCustomLevel && !gameBoard.isDebugMode()) {
            return;
        }

        switch (event.getCode()) {
            case D:
                gameBoard.setDebugMode(!gameBoard.isDebugMode());
                System.out.println("Debug Mode (Auto-spawn OFF): " + gameBoard.isDebugMode());
                break;
            case U:
                gameBoard.setFreePlantsMode(!gameBoard.isFreePlantsMode());
                System.out.println("Free Plants Mode: " + gameBoard.isFreePlantsMode());
                break;
            case DIGIT1: setSelectedZombieType("BasicZombie"); break;
            case DIGIT2: setSelectedZombieType("PharaohZombie"); break;
            case DIGIT3: setSelectedZombieType("FlagZombie"); break;
            case DIGIT4: setSelectedZombieType("AllStarZombie"); break;
            case DIGIT5: setSelectedZombieType("ExcavatorZombie"); break;
            case DIGIT6: setSelectedZombieType("ChickenWranglerZombie"); break;
            case ESCAPE:
                selectedZombieType = null;
                selectedPlant = null;
                System.out.println("Selection cleared");
                break;
            default:
                break;
        }
    }

    private void handleMouseMove(MouseEvent event) {
        mouseX = event.getX();
        mouseY = event.getY();
        if (selectedPlant != null || dragPlantType != null) {
            event.consume();
        }
    }

    private void handleDragOver(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasString()) {
            event.acceptTransferModes(TransferMode.COPY);
            updateDragHighlight(event.getX(), event.getY());
            mouseX = event.getX();
            mouseY = event.getY();
        }
        event.consume();
    }

    private void handleDragDropped(DragEvent event) {
        boolean success = false;
        Dragboard db = event.getDragboard();
        if (db.hasString()) {
            double boardX = screenToBoardX(event.getX());
            double boardY = screenToBoardY(event.getY());
            int cellWidth = GameBoard.getCellWidth();
            int cellHeight = GameBoard.getCellHeight();
            int gridX = (int) (boardX / cellWidth);
            int gridY = (int) (boardY / cellHeight);

            if (gridX >= 0 && gridX < GameBoard.getGridWidth() && gridY >= 0 && gridY < GameBoard.getGridHeight()) {
                if ("Shovel".equals(db.getString())) {
                    success = gameBoard.removePlantAt(boardX, boardY);
                } else {
Plant newPlant = createPlantFromType(db.getString());
                    if (newPlant != null && !gameBoard.isFreePlantsMode() && gameBoard.getSun() < newPlant.getCost()) {
                        showSunMessage(event.getX(), event.getY());
                        event.setDropCompleted(false);
                        return;
                    }
                    if (newPlant != null && gameBoard.plantAt(newPlant, gridX, gridY)) {
                        success = true;
                    }
                }
            }
        }
        event.setDropCompleted(success);
        clearDragPlantPreview();
        event.consume();
    }

    private void handleDragExited(DragEvent event) {
        clearDragHighlight();
        event.consume();
    }

    public void startDragPlantPreview(String plantType) {
        this.dragPlantType = plantType;
        updateDragHighlight(mouseX, mouseY);
    }

    public void clearDragPlantPreview() {
        this.dragPlantType = null;
        clearDragHighlight();
    }

    private void updateDragHighlight(double screenX, double screenY) {
        double boardX = screenToBoardX(screenX);
        double boardY = screenToBoardY(screenY);
        int cellWidth = GameBoard.getCellWidth();
        int cellHeight = GameBoard.getCellHeight();
        int gridX = (int) (boardX / cellWidth);
        int gridY = (int) (boardY / cellHeight);

        if (gridX >= 0 && gridX < GameBoard.getGridWidth() && gridY >= 0 && gridY < GameBoard.getGridHeight()) {
            dragHighlightX = gridX;
            dragHighlightY = gridY;
        } else {
            clearDragHighlight();
        }
    }

    private void clearDragHighlight() {
        dragHighlightX = -1;
        dragHighlightY = -1;
    }

    private void drawDragHighlight() {
        if (dragHighlightX < 0 || dragHighlightY < 0) {
            return;
        }

        double x = tx(dragHighlightX * GameBoard.getCellWidth());
        double y = ty(dragHighlightY * GameBoard.getCellHeight());
        double width = GameBoard.getCellWidth();
        double height = GameBoard.getCellHeight();

        gc.setFill(Color.rgb(255, 255, 255, 0.14));
        gc.fillRoundRect(x, y, width, height, 18, 18);
        gc.setStroke(Color.rgb(255, 255, 255, 0.75));
        gc.setLineWidth(3);
        gc.strokeRoundRect(x + 1.5, y + 1.5, width - 3, height - 3, 18, 18);
    }

    private Plant createPlantFromType(String type) {
        switch (type) {
            case "Peashooter": return new Peashooter(0, 0);
            case "Sunflower": return new Sunflower(0, 0);
            case "Wallnut": return new Wallnut(0, 0);
            case "SpikeWeed": return new SpikeWeed(0, 0);
            case "KernelPult": return new KernelPult(0, 0);
            case "BonkChoy": return new BonkChoy(0, 0);
            default: return null;
        }
    }

    private Plant createPlantCopy(Plant original) {
        if (original instanceof Peashooter) {
return new Peashooter(0, 0);
        } else if (original instanceof Sunflower) {
            return new Sunflower(0, 0);
        } else if (original instanceof SpikeWeed) {
            return new SpikeWeed(0, 0);
        } else if (original instanceof KernelPult) {
            return new KernelPult(0, 0);
        } else if (original instanceof BonkChoy) {
            return new BonkChoy(0, 0);
        } else if (original instanceof Wallnut) {
            return new Wallnut(0, 0);
        }
        return new Peashooter(0, 0);
    }

    public void setSelectedPlant(Plant plant) {
        this.selectedPlant = plant;
        this.selectedZombieType = null;
    }

    public void setSelectedZombieType(String type) {
        this.selectedZombieType = type;
        this.selectedPlant = null;
        System.out.println("Selected Zombie: " + type);
    }}