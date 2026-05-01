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
import com.pvz.entities.Chicken;
import com.pvz.entities.Sun;
import com.pvz.entities.Sunflower;
import com.pvz.entities.Wallnut;
import com.pvz.entities.SpikeWeed;
import com.pvz.entities.KernelPult;
import com.pvz.entities.BonkChoy;
import com.pvz.entities.zombies.*;
import javax.imageio.ImageIO;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.Iterator;

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
    private Image chickenImage;
    private Image chickenDeath;
    private Image sunflowerIdle;
    private Image shovelImage;
    private Image sunflowerProduce;
    private Image bonkChoyAttackFront;
    private Image bonkChoyFinisherFront;
    private Image bonkChoyAttackBack;
    private Image bonkChoyFinisherBack;
    private Image wallnutDegrade1;
    private Image wallnutDegrade3;
    private Image allStarTackle;
    private Image allStarWalking;
    private Image allStarKick;
    private Image chickenWranglerNoChickens;
    private Image bonkChoyAttack2;
    private Image allStarRunning;
    private Image basicZombieIdle;
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
    private Map<String, Image> plantIdleImages;
    private Map<String, Image> plantActionImages;
    private Map<String, Image> zombieIdleImages;
    private Map<String, Image> zombieActionImages;
    private double gridOffsetX = 0;
    private double gridOffsetY = 0;

    private static class FlyingSun {
        double x, y;
        FlyingSun(double x, double y) { this.x = x; this.y = y; }
    }
    private final java.util.List<FlyingSun> flyingSuns = new java.util.ArrayList<>();

    public GameCanvas(GameBoard gameBoard) {
        super(800, 650); // Fill the entire window
        this.gameBoard = gameBoard;
        this.gc = this.getGraphicsContext2D();
        
        try {
            java.io.File fallbackPng = new java.io.File("src/main/resources/Backyard.png");
            java.io.InputStream     is = new java.io.FileInputStream(fallbackPng);
            backgroundImage = new Image(is);
        } catch (Exception e) {
            System.err.println("Could not load backyard image: " + e.getMessage());
        }

        plantIdleImages = new HashMap<>();
        plantActionImages = new HashMap<>();
        zombieIdleImages = new HashMap<>();
        zombieActionImages = new HashMap<>();

        try {
            // Plant sprites and animations
            sunflowerProduce = loadImage("/Mobile - Plants vs. Zombies 2 - Sunflower - Sun Produce.gif", 110, 132);
            sunflowerIdle = loadImage("/Mobile - Plants vs. Zombies 2 - Sunflower - Idle.gif", 100, 120);
            // if (sunflowerProduce != null) {
            //     long duration = calculateGifDuration(getClass().getResourceAsStream("/Mobile - Plants vs. Zombies 2 - Sunflower - Sun Produce.gif"));
            //     if (duration > 0) {
            //         com.pvz.entities.Sunflower.setProduceAnimationDuration(duration);
            //     }
            // }

            plantIdleImages.put("Peashooter", loadImage("/Mobile - Plants vs. Zombies 2 - Peashooter - Idle.gif", 100, 120));
            plantActionImages.put("Peashooter", loadImage("/Mobile - Plants vs. Zombies 2 - Peashooter - Attack.gif", 100, 120));
            plantIdleImages.put("Wallnut", loadImage("/Mobile - Plants vs. Zombies 2 - Wall-nut - Idle.gif", 120, 120));
            plantIdleImages.put("SpikeWeed", loadImage("/Mobile - Plants vs. Zombies 2 - Spikeweed - Idle - 2.gif", 100, 100));
            plantActionImages.put("SpikeWeed", loadImage("/Mobile - Plants vs. Zombies 2 - Spikeweed - Attack.gif", 100, 100));
            plantIdleImages.put("KernelPult", loadImage("/Mobile - Plants vs. Zombies 2 - Kernel-pult - Idle.gif", 110, 130));
            plantActionImages.put("KernelPult", loadImage("/Mobile - Plants vs. Zombies 2 - Kernel-pult - Attack.gif", 150, 177));
            plantIdleImages.put("BonkChoy", loadImage("/Mobile - Plants vs. Zombies 2 - Bonk Choy - Idle - 2.gif", 110, 130));
            bonkChoyAttackFront = loadImage("/Mobile - Plants vs. Zombies 2 - Bonk Choy - Attack.gif", 150, 130);
            bonkChoyFinisherFront = loadImage("/Mobile - Plants vs. Zombies 2 - Bonk Choy - Attack - 4.gif", 150, 130);
            bonkChoyAttackBack = loadImage("/Mobile - Plants vs. Zombies 2 - Bonk Choy - Attack - 2.gif", 150, 130);
            bonkChoyFinisherBack = loadImage("/Mobile - Plants vs. Zombies 2 - Bonk Choy - Attack - 5.gif", 150, 130);
            plantIdleImages.put("Sunflower", sunflowerIdle);
            plantActionImages.put("Sunflower", sunflowerProduce);
            wallnutDegrade1 = loadImage("/Mobile - Plants vs. Zombies 2 - Wall-nut - Idle - Degrade 1.gif", 120, 120);
            wallnutDegrade3 = loadImage("/Mobile - Plants vs. Zombies 2 - Wall-nut - Idle - Degrade 3.gif", 120, 120);

            // Zombie sprites and animations
            basicZombieIdle = loadImage("/Mobile - Plants vs. Zombies 2 - Basic Zombie - Walking.gif", 200, 216);
            zombieIdleImages.put("BasicZombie", basicZombieIdle);
            zombieActionImages.put("BasicZombie_Eating", loadImage("/Mobile - Plants vs. Zombies 2 - Basic Zombie - Eating.gif", 168, 216));
            zombieIdleImages.put("FlagZombie", loadImage("/Mobile - Plants vs. Zombies 2 - Basic Zombie - Walking - Flag Zombie.gif", 182, 234));
            zombieActionImages.put("FlagZombie_Eating", loadImage("/Mobile - Plants vs. Zombies 2 - Basic Zombie - Eating - Flag Zombie.gif", 182, 234));
            zombieIdleImages.put("AllStarZombie", loadImage("/Mobile - Plants vs. Zombies 2 - All-Star Zombie - Walking.gif", 270, 390));
            allStarRunning = loadImage("/Mobile - Plants vs. Zombies 2 - All-Star Zombie - Running.gif", 270, 390);
            allStarTackle = loadImage("/Mobile - Plants vs. Zombies 2 - All-Star Zombie - Tackle.gif", 270, 390);
            allStarKick = loadImage("/Mobile - Plants vs. Zombies 2 - All-Star Zombie - Kick.gif", 270, 390);
            allStarWalking = loadImage("/Mobile - Plants vs. Zombies 2 - All-Star Zombie - Walking.gif", 270, 390);
            zombieIdleImages.put("ChickenWranglerZombie", loadImage("/Mobile - Plants vs. Zombies 2 - Chicken Wrangler Zombie - Walking.gif", 168, 216));
            zombieActionImages.put("ChickenWranglerZombie_Eating", loadImage("/Mobile - Plants vs. Zombies 2 - Chicken Wrangler Zombie - Eating.gif", 168, 216));
            zombieActionImages.put("ChickenWranglerZombie_Releasing", loadImage("/Mobile - Plants vs. Zombies 2 - Chicken Wrangler Zombie - Plague Pharmacy - Without Chickens - Battle (Chinese Only - Removed).gif", 175, 225));
            zombieIdleImages.put("ExcavatorZombie", loadImage("/Mobile - Plants vs. Zombies 2 - Excavator Zombie - Walking.gif", 168, 216));
            zombieActionImages.put("ExcavatorZombie_Eating", loadImage("/Mobile - Plants vs. Zombies 2 - Excavator Zombie - Eating.gif", 168, 216));
            zombieIdleImages.put("Chicken", loadImage("/Mobile - Plants vs. Zombies 2 - Zombie Chicken - Idle_Walking.gif", 100, 120));
            chickenWranglerNoChickens = loadImage("/Mobile - Plants vs. Zombies 2 - Chicken Wrangler Zombie - Walking - Without Chickens.gif", 168, 216);

            basicZombieDeath = loadImage("/Mobile - Plants vs. Zombies 2 - Basic Zombie - Death - Flag Zombie.gif", 140, 180); // For FlagZombie
            allStarZombieDeath = loadImage("/Mobile - Plants vs. Zombies 2 - All-Star Zombie - Death.gif", 270, 390);
            basicZombieDeathAnim = loadImage("/Mobile - Plants vs. Zombies 2 - Basic Zombie - Death.gif", 140, 180); // For BasicZombie
            chickenWranglerZombieDeath = loadImage("/Mobile - Plants vs. Zombies 2 - Chicken Wrangler Zombie - Death.gif", 140, 190);
            excavatorZombieDeath = loadImage("/Mobile - Plants vs. Zombies 2 - Excavator Zombie - Death.gif", 140, 180);
            pharaohWalkingArmor = loadImage("/Mobile - Plants vs. Zombies 2 - Pharaoh Zombie - Walking - Sarcophagus.gif", 182, 234);
            pharaohEatingArmor = loadImage("/Mobile - Plants vs. Zombies 2 - Pharaoh Zombie - Eating - Sarcophagus.gif", 182, 234);
            pharaohArmorDestroyed = loadImage("/Mobile - Plants vs. Zombies 2 - Pharaoh Zombie - Destroyed - Sarcophagus.gif", 700, 900);
            pharaohWalkingNoArmor = loadImage("/Mobile - Plants vs. Zombies 2 - Pharaoh Zombie - Walking.gif", 168, 216);
            pharaohEatingNoArmor = loadImage("/Mobile - Plants vs. Zombies 2 - Pharaoh Zombie - Eating.gif", 168, 216);
            pharaohDeath = loadImage("/Mobile - Plants vs. Zombies 2 - Pharaoh Zombie - Death.gif", 140, 180);

            // Misc objects
            shovelImage = loadImage("/111px-Shovel2.png", 100, 100);
            mowerImage = loadImage("/120px-Lawn_mower_2.png");
            peaImage = loadImage("/Pea.png");
            kernelPeaImage = loadImage("/Kernel_2.png");
            butterImage = loadImage("/Butter_2.png");
            butterOverlayImage = loadImage("/Butter on face.png");
            sunImage = loadImage("/Sun_PvZ2.png");
            chickenImage = loadImage("/Mobile - Plants vs. Zombies 2 - Zombie Chicken - Idle_Walking.gif");
            chickenDeath = loadImage("/Mobile - Plants vs. Zombies 2 - Zombie Chicken - Feather Burst.gif", 100, 120);
        } catch (Exception e) {
            System.err.println("Could not load object animations: " + e.getMessage());
        }

        // Mouse input handling
        setOnMouseClicked(this::handleMouseClick);
        setOnMouseMoved(this::handleMouseMove);
        setOnDragOver(this::handleDragOver);
        setOnDragDropped(this::handleDragDropped);
        setOnDragExited(this::handleDragExited);
        
        // Keyboard input for debug
        setFocusTraversable(true);
        setOnKeyPressed(this::handleKeyPressed);
    }

    // private long calculateGifDuration(java.io.InputStream inputStream) {
    //     try (ImageInputStream iis = ImageIO.createImageInputStream(inputStream)) {
    //         if (iis == null) {
    //             return 0;
    //         }
    //         Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
    //         if (!readers.hasNext()) {
    //             return 0;
    //         }
    //         ImageReader reader = readers.next();
    //         reader.setInput(iis, false);
    //         int frames = reader.getNumImages(true);
    //         long duration = 0;
    //         for (int i = 0; i < frames; i++) {
    //             IIOMetadata metadata = reader.getImageMetadata(i);
    //             if (metadata != null) {
    //                 Node tree = metadata.getAsTree("javax_imageio_gif_image_1.0");
    //                 if (tree != null) {
    //                     NodeList children = tree.getChildNodes();
    //                     for (int j = 0; j < children.getLength(); j++) {
    //                         Node node = children.item(j);
    //                         if ("GraphicControlExtension".equals(node.getNodeName())) {
    //                             Node delayNode = node.getAttributes().getNamedItem("delayTime");
    //                             if (delayNode != null) {
    //                                 try {
    //                                     duration += Integer.parseInt(delayNode.getNodeValue()) * 10;
    //                                 } catch (NumberFormatException ignored) {
    //                                 }
    //                             }
    //                         }
    //                     }
    //                 }
    //             }
    //         }
    //         reader.dispose();
    //         return duration;
    //     } catch (Exception e) {
    //         return 0;
    //     }
    // }

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
                    image = new Image(is, requestedWidth, requestedHeight, true, true, false);
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

        // Draw grid
        drawGrid();
        drawDragHighlight();

        // Draw left-side mowers for each row
        for (Mower mower : gameBoard.getMowers()) {
            drawMower(mower);
        }

        // Draw plants
        for (Plant plant : gameBoard.getPlants()) {
            drawPlant(plant);
        }

        // Draw zombies (including spawned chickens as normal zombie units)
        for (Zombie zombie : gameBoard.getZombies()) {
            drawZombie(zombie);
        }

        // Draw projectiles
        for (Pea pea : gameBoard.getProjectiles()) {
            drawPea(pea);
        }

        // Draw suns
        for (Sun sun : gameBoard.getSuns()) {
            drawSun(sun);
        }

        drawFlyingSuns();

        // Draw selected plant preview if mouse is over board
        if (selectedPlant != null) {
            drawSelectedPlantPreview();
        }

        // Vẽ hình ảnh xẻng và khung nền đen mờ khi đang kéo (Drag)
        if (dragPlantType != null && "Shovel".equals(dragPlantType) && shovelImage != null) {
            gc.setFill(Color.rgb(0, 0, 0, 0.5));
            gc.fillOval(mouseX - 35, mouseY - 35, 70, 70);
            gc.drawImage(shovelImage, mouseX - 35, mouseY - 35, 70, 70);
        }
    }

    private void drawFlyingSuns() {
        java.util.Iterator<FlyingSun> it = flyingSuns.iterator();
        double targetX = -gridOffsetX + 30; // Vị trí icon Sun trên HUD
        double targetY = -gridOffsetY + 30; 
        
        while (it.hasNext()) {
            FlyingSun fs = it.next();
            double dx = targetX - fs.x;
            double dy = targetY - fs.y;
            double dist = Math.sqrt(dx * dx + dy * dy);
            
            if (dist < 15) {
                it.remove();
            } else {
                fs.x += dx * 0.2; // Tốc độ bay
                fs.y += dy * 0.2;
                if (sunImage != null) {
                    gc.drawImage(sunImage, tx(fs.x), ty(fs.y), 40, 40);
                }
            }
        }
    }

    private void drawGrid() {
        gc.setStroke(Color.rgb(255, 255, 255, 0.3)); // Slightly more visible for alignment
        gc.setLineWidth(1);
        
        int cellWidth = GameBoard.getCellWidth();
        int cellHeight = GameBoard.getCellHeight();
        int width = GameBoard.getGridWidth();
        int height = GameBoard.getGridHeight();

        for (int i = 0; i <= width; i++) {
            gc.strokeLine(tx(i * cellWidth), ty(0), tx(i * cellWidth), ty(height * cellHeight));
        }
        for (int i = 0; i <= height; i++) {
            gc.strokeLine(tx(0), ty(i * cellHeight), tx(width * cellWidth), ty(i * cellHeight));
        }
    }

    private double tx(double x) {
        return x + gridOffsetX;
    }

    private double ty(double y) {
        return y + gridOffsetY;
    }

    private void drawPlant(Plant plant) {
        int cellWidth = GameBoard.getCellWidth();
        int cellHeight = GameBoard.getCellHeight();
        
        double plantShift = 5;    // Cây xích xuống nhẹ để tạo độ sâu, theo yêu cầu
        double shadowShift = 15;  // Đẩy bóng xuống sâu hẳn để nằm dưới chân thực thể

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
            // Scale: Wallnut và Sunflower giữ 1.6, các cây khác 2.2
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
        String key = plant.getClass().getSimpleName();
        if (plant instanceof Sunflower && ((Sunflower) plant).isProducing()) {
            return plantActionImages.get(key);
        }
        if (plant instanceof Wallnut) {
            double hpPercent = (double) plant.getHealth() / 500.0;
            if (hpPercent < 0.25) return wallnutDegrade3 != null ? wallnutDegrade3 : plantIdleImages.get(key);
            if (hpPercent < 0.50) return wallnutDegrade1 != null ? wallnutDegrade1 : plantIdleImages.get(key);
        }
        if (plant instanceof Peashooter && plant.isRecentlyActive(900)) {
            return plantActionImages.get(key);
        }
        if (plant instanceof KernelPult && plant.isRecentlyActive(900)) {
            return plantActionImages.get(key);
        }
        if (plant instanceof BonkChoy && plant.isRecentlyActive(900)) {
            // Tìm zombie gần nhất để quyết định hướng đánh
            Zombie target = gameBoard.findNearestZombie(plant);
            if (target == null) {
                // Nếu không thấy zombie phía trước, kiểm tra xem có zombie nào đang ở trong tầm đánh (có thể ở sau lưng)
                for (Zombie z : gameBoard.getZombies()) {
                    if (Math.abs(z.getY() - plant.getY()) < GameBoard.getCellHeight() / 3.0 && Math.abs(z.getX() - plant.getX()) < GameBoard.getCellWidth() * 1.5) {
                        target = z;
                        break;
                    }
                }
            }
            
            if (target != null) {
                boolean isFront = target.getX() > plant.getX();
                boolean isFinisher = target.getHealth() <= 25; // Giả định là đòn kết liễu nếu zombie sắp chết
                
                if (isFront) {
                    return isFinisher ? bonkChoyFinisherFront : bonkChoyAttackFront;
                } else {
                    return isFinisher ? bonkChoyFinisherBack : bonkChoyAttackBack;
                }
            }
            return bonkChoyAttackFront;
        }
        if (plant instanceof SpikeWeed && plant.isRecentlyActive(900)) {
            return plantActionImages.get(key);
        }
        return plantIdleImages.get(key);
    }

    private void drawZombie(Zombie zombie) {
        Image zombieImg = getZombieImage(zombie);
        int cellHeight = GameBoard.getCellHeight();
        
        // Offset để dịch chuyển hình ảnh zombie theo yêu cầu
        double zombieVisualXOffset = -15; // Dịch sang trái 15px
        double zombieVisualYOffset = -15; // Dịch lên trên 15px

        double verticalShift = 5; // Offset ban đầu cho vị trí Y của thực thể zombie
        double zombieShadowShift = 12; // Bóng zombie cũng cần xích xuống thêm
        double offsetY = (cellHeight - zombie.getHeight()) / 2 + verticalShift;
        double visualWidth = zombie.getWidth() * 2.2;
        double visualHeight = zombie.getHeight() * 2.2;
        double vOffsetY = (cellHeight - visualHeight) / 2 + verticalShift + zombieVisualYOffset;

        // Vẽ bóng dưới chân Zombie (To và mờ viền)
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
                    currentImg = allStarTackle != null ? allStarTackle : zombieActionImages.get("AllStarZombie_Eating");
                } else if (az.isEating()) {
                    currentImg = allStarKick != null ? allStarKick : zombieActionImages.get("AllStarZombie_Eating");
                } else if (zombie.getSpeed() > 0.3) { // Đang chạy
                    currentImg = allStarRunning != null ? allStarRunning : zombieIdleImages.get("AllStarZombie");
                } else { // Đã húc xong và đang đi bộ
                    currentImg = allStarWalking != null ? allStarWalking : zombieIdleImages.get("AllStarZombie");
                }
                if (currentImg != null) {
                    gc.drawImage(currentImg, tx(zombie.getX() + zombieVisualXOffset), ty(zombie.getY() + vOffsetY), visualWidth, visualHeight);
                }
            } else {
                gc.drawImage(zombieImg, tx(zombie.getX() + zombieVisualXOffset), ty(zombie.getY() + vOffsetY), visualWidth, visualHeight);
            }
        } else if (!zombie.isDying()) {
            // Chỉ vẽ hình chữ nhật debug nếu zombie còn sống và không có ảnh
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
                gc.setFill(Color.GRAY); // Basic
            }
            gc.fillRect(tx(zombie.getX() + zombieVisualXOffset), ty(zombie.getY() + offsetY + zombieVisualYOffset), zombie.getWidth(), zombie.getHeight());
            gc.setFill(Color.RED);
            gc.fillOval(tx(zombie.getX() + 5 + zombieVisualXOffset), ty(zombie.getY() + 10 + zombieVisualYOffset), 8, 8);
            gc.fillOval(tx(zombie.getX() + 25 + zombieVisualXOffset), ty(zombie.getY() + 10 + zombieVisualYOffset), 8, 8);
        }

        // Vẽ bơ dính lên mặt nếu zombie bị bất động
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
        gc.setFont(javafx.scene.text.Font.font(10)); // Font size
        gc.fillText(type, tx(zombie.getX() + zombieVisualXOffset), ty(zombie.getY() + offsetY + zombieVisualYOffset - 5));

        drawHealthBar(zombie, zombieVisualXOffset, offsetY + zombieVisualYOffset);
    }

    private Image getZombieImage(Zombie zombie) {
        String key = zombie.getClass().getSimpleName();
        if (zombie instanceof PharaohZombie) {
            PharaohZombie pz = (PharaohZombie) zombie;
            if (pz.isDying()) {
                // If death animation is finished, draw nothing to prevent looping GIFs from re-playing.
                if (pz.isDeathAnimFinished()) {
                    return null;
                }
                return pharaohDeath != null ? pharaohDeath : zombieActionImages.get("PharaohZombie_Death");
            }
            if (pz.isArmorBreakAnim()) {
                return pharaohArmorDestroyed != null ? pharaohArmorDestroyed : zombieActionImages.get("PharaohZombie_Destroyed");
            }
            if (zombie.isEating()) {
                return !pz.isArmorBroken() ? pharaohEatingArmor : pharaohEatingNoArmor;
            }
            return !pz.isArmorBroken() ? pharaohWalkingArmor : pharaohWalkingNoArmor;
        }

        if (zombie.isDying()) {
            // If death animation is finished, draw nothing to prevent looping GIFs from re-playing.
            if (zombie.isDeathAnimFinished()) {
                return null;
            }

            if (zombie instanceof AllStarZombie) {
                return allStarZombieDeath != null ? allStarZombieDeath : zombieIdleImages.get("AllStarZombie");
            }
            if (zombie instanceof ChickenWranglerZombie) {
                return chickenWranglerZombieDeath != null ? chickenWranglerZombieDeath : zombieIdleImages.get("ChickenWranglerZombie");
            }
            if (zombie instanceof ExcavatorZombie) {
                return excavatorZombieDeath != null ? excavatorZombieDeath : zombieIdleImages.get("ExcavatorZombie");
            }
            if (zombie instanceof FlagZombie) {
                // basicZombieDeath is actually the death animation for FlagZombie
                return basicZombieDeath != null ? basicZombieDeath : zombieIdleImages.get("FlagZombie");
            }
            if (zombie instanceof Chicken) {
                return chickenDeath != null ? chickenDeath : zombieIdleImages.get("Chicken");
            }
            // Fallback for BasicZombie and any other types
            return basicZombieDeathAnim != null ? basicZombieDeathAnim : zombieIdleImages.get("BasicZombie");
        }

        if (zombie instanceof ChickenWranglerZombie && ((ChickenWranglerZombie) zombie).isReleasing()) {
            return zombieActionImages.get("ChickenWranglerZombie_Releasing");
        }
        if (zombie.isEating()) {
            Image eatingImg = zombieActionImages.get(key + "_Eating");
            if (eatingImg != null) return eatingImg;

            // Fallback cho zombie đội nón, đội xô hoặc Pharaoh dùng hoạt ảnh ăn của zombie thường
            if (zombie instanceof PharaohZombie) {
                return zombieActionImages.get("BasicZombie_Eating");
            }
        }
        Image idle = zombieIdleImages.get(key);
        if (idle != null) return idle;
        return zombieIdleImages.get("BasicZombie");
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
        // Draw sun rays
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
        
        // Background bar
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
        
        // Main health bar
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

        double boardX = screenToBoardX(event.getX());
        double boardY = screenToBoardY(event.getY());
        int cellWidth = GameBoard.getCellWidth();
        int cellHeight = GameBoard.getCellHeight();
        int gridX = (int) (boardX / cellWidth);
        int gridY = (int) (boardY / cellHeight);

        // Kiểm tra xem click có nằm trong lưới không
        if (gridX < 0 || gridX >= GameBoard.getGridWidth() || gridY < 0 || gridY >= GameBoard.getGridHeight()) {
            return;
        }

        if (event.getButton() == MouseButton.PRIMARY) {
            // Tăng hitbox sun: Duyệt và kiểm tra khoảng cách thay vì chỉ click điểm
            Sun hitSun = null;
            for (Sun s : gameBoard.getSuns()) {
                double centerX = s.getX() + s.getWidth() / 2;
                double centerY = s.getY() + s.getHeight() / 2;
                double dist = Math.sqrt(Math.pow(boardX - centerX, 2) + Math.pow(boardY - centerY, 2));
                if (dist < 65) { // Hitbox rộng 65px
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

            // Debug: Place zombie if selected
            if (selectedZombieType != null) {
                double spawnY = gridY * GameBoard.getCellHeight() + 10;
                gameBoard.addZombie(gameBoard.spawnZombieByType(selectedZombieType, boardX, spawnY));
                event.consume();
                return;
            }

            // Then, place plant if no sun collected
            if (selectedPlant != null) {
                Plant newPlant = createPlantCopy(selectedPlant);
                if (gameBoard.plantAt(newPlant, gridX, gridY)) {
                    setSelectedPlant(null);
                    event.consume();
                }
            }
        } else if (event.getButton() == MouseButton.SECONDARY) {
            // Right-click to remove plant
            gameBoard.removePlantAt(boardX, boardY);
            event.consume();
        }
    }

    private void handleKeyPressed(javafx.scene.input.KeyEvent event) {
        // Chỉ cho phép chọn zombie nếu là màn Custom (Level 6) hoặc đang bật Debug Mode
        boolean isCustomLevel = gameBoard.getLevelConfig() != null && gameBoard.getLevelConfig().getLevelNumber() == 6;
        if (!isCustomLevel && !gameBoard.isDebugMode()) {
            return;
        }

        switch (event.getCode()) {
            case D:
                gameBoard.setDebugMode(!gameBoard.isDebugMode());
                System.out.println("Debug Mode (Auto-spawn OFF): " + gameBoard.isDebugMode());
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
    }
}
