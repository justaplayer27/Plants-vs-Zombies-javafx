package com.pvz.ui;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.input.ClipboardContent;
import java.util.function.Supplier;
import com.pvz.game.GameBoard;
import com.pvz.game.GameProgress;
import com.pvz.game.LevelConfig;
import com.pvz.entities.Peashooter;
import com.pvz.entities.Plant;
import com.pvz.entities.Sunflower;
import com.pvz.entities.Wallnut;
import com.pvz.entities.SpikeWeed;
import com.pvz.entities.KernelPult;
import com.pvz.entities.BonkChoy;
import com.pvz.entities.zombies.*;

public class GameWindow {
    private Stage primaryStage;
    private GameBoard gameBoard;
    private GameCanvas gameCanvas;
    private Label sunLabel;
    private Label waveLabel;
    private Button pauseButton;
    private boolean isPaused;
    private VBox specialHealthVBox;
    private LevelConfig levelConfig;
    private AnimationTimer gameTimer;
    
    private StackPane rootPane;
    private VBox overlayPanel;
    private Label overlayStatusLabel;
    private Button overlayResumeBtn;
    private Button overlayRestartBtn;
    private Button overlayNextBtn;
    private Button overlayMenuBtn;
    
    private Button notificationBtn;
    private StackPane idCardOverlay;
    private VBox idCardContent;

    public GameWindow(Stage primaryStage, LevelConfig config) {
        this.primaryStage = primaryStage;
        this.levelConfig = config;
        this.gameBoard = new GameBoard(config);
        GameBoard.setInstance(this.gameBoard);
        this.isPaused = false;
        setupUI();
    }

    private void setupUI() {
        // Main container for game and HUD
        StackPane gameContainer = new StackPane();

        // 1. The World (Canvas)
        gameCanvas = new GameCanvas(gameBoard);

        // 2. HUD Layers (Top and Bottom)
        HBox topPanel = createTopPanel();
        topPanel.setPickOnBounds(false); // Allow clicking through to canvas
        StackPane.setAlignment(topPanel, Pos.TOP_LEFT);

        VBox sidePanel = createSidePanel();
        sidePanel.setPickOnBounds(false); // Allow clicking through to canvas
        sidePanel.setMaxWidth(160);
        StackPane.setAlignment(sidePanel, Pos.CENTER_LEFT);

        gameContainer.getChildren().addAll(gameCanvas, topPanel, sidePanel);

        // Create StackPane to hold game and overlay
        rootPane = new StackPane();
        gameContainer.setPickOnBounds(false);
        
        createOverlay();
        createIdCardOverlay();
        rootPane.getChildren().addAll(gameContainer, overlayPanel, idCardOverlay);

        gameCanvas.widthProperty().bind(rootPane.widthProperty());
        gameCanvas.heightProperty().bind(rootPane.heightProperty());

        Scene scene = new Scene(rootPane, 800, 650); // Slightly increased height for better UI clearance
        primaryStage.setTitle("Plants vs Zombies - Level " + (levelConfig != null ? levelConfig.getLevelNumber() : 1));
        primaryStage.setScene(scene);
        scene.addEventHandler(javafx.scene.input.KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                toggleFullscreen();
                e.consume();
            }
        });
        primaryStage.setResizable(true);

        primaryStage.setFullScreenExitHint("");
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.keyCombination("ESCAPE"));

        primaryStage.maximizedProperty().addListener((obs, wasMax, isMax) -> {
            if (isMax) {
                primaryStage.setMaximized(false);
                primaryStage.setFullScreen(true);
            }
        });

        startGameLoop();
    }

    private void createOverlay() {
        overlayPanel = new VBox(20);
        overlayPanel.setAlignment(Pos.CENTER);
        overlayPanel.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8);");
        overlayPanel.setVisible(false);
        overlayPanel.setMouseTransparent(true);

        overlayStatusLabel = new Label("ĐANG TẠM DỪNG");
        overlayStatusLabel.setStyle("-fx-text-fill: white; -fx-font-size: 40; -fx-font-weight: bold;");

        overlayResumeBtn = new Button("Resume");
        overlayResumeBtn.setStyle("-fx-font-size: 18; -fx-min-width: 150;");
        overlayResumeBtn.setOnAction(e -> togglePause());

        overlayRestartBtn = new Button("Restart");
        overlayRestartBtn.setStyle("-fx-font-size: 18; -fx-min-width: 150; -fx-background-color: #ff4444; -fx-text-fill: white;");
        overlayRestartBtn.setOnAction(e -> {
            overlayPanel.setVisible(false);
            restartGame();
        });

        overlayNextBtn = new Button("Next Level");
        overlayNextBtn.setStyle("-fx-font-size: 18; -fx-min-width: 150; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        overlayNextBtn.setOnAction(e -> {
            if (levelConfig != null && levelConfig.getLevelNumber() < 5) {
                int nextLevel = levelConfig.getLevelNumber() + 1;
                GameProgress.getInstance().unlockLevel(nextLevel);
                LevelConfig nextConfig = LevelConfig.getLevel(nextLevel);
                GameWindow nextWindow = new GameWindow(primaryStage, nextConfig);
                nextWindow.show();
            }
        });

        overlayMenuBtn = new Button("Menu");
        overlayMenuBtn.setStyle("-fx-font-size: 18; -fx-min-width: 150;");
        overlayMenuBtn.setOnAction(e -> {
            LevelSelectScreen menu = new LevelSelectScreen(primaryStage);
            menu.show();
        });

        overlayPanel.getChildren().addAll(overlayStatusLabel, overlayResumeBtn, overlayRestartBtn, overlayNextBtn, overlayMenuBtn);
    }

    private void createIdCardOverlay() {
        idCardOverlay = new StackPane();
        idCardOverlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.85);");
        idCardOverlay.setVisible(false);
        idCardOverlay.setMouseTransparent(true);

        idCardContent = new VBox(20);
        idCardContent.setAlignment(Pos.CENTER);
        idCardContent.setMaxSize(550, 350);
        idCardContent.setStyle("-fx-background-color: #f0e68c; -fx-padding: 25; -fx-border-color: #8b4513; -fx-border-width: 5; -fx-background-radius: 15; -fx-border-radius: 10;");

        Label cardTitle = new Label("ZOMBIE IDENTIFICATION CARD");
        cardTitle.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #8b4513;");
        
        idCardContent.getChildren().add(cardTitle);
        
        Button okBtn = new Button("OK");
        okBtn.setStyle("-fx-font-size: 16; -fx-min-width: 100; -fx-background-color: #8b4513; -fx-text-fill: white;");
        okBtn.setOnAction(e -> {
            idCardOverlay.setVisible(false);
            idCardOverlay.setMouseTransparent(true);
            togglePause();
        });
        
        idCardOverlay.getChildren().add(idCardContent);
    }

    private HBox createTopPanel() {
        HBox topPanel = new HBox(20);
        topPanel.setPadding(new Insets(10));
        topPanel.setStyle("-fx-background-color: transparent;"); // Transparent background
        topPanel.setMaxHeight(100);

        VBox leftPanel = new VBox(5);
        // Thiết kế lại UI hiển thị Sun: Icon bên trái, số bên phải với nền đen mờ
        HBox sunContainer = new HBox(5);
        sunContainer.setAlignment(Pos.CENTER_LEFT);
        ImageView sunIcon = loadButtonGraphic("/Sun_PvZ2.png", 35, 35);
        sunLabel = new Label("100");
        sunLabel.setStyle("-fx-font-size: 18; -fx-text-fill: white; -fx-font-weight: bold; " +
                         "-fx-background-color: rgba(0, 0, 0, 0.5); -fx-padding: 2 10 2 10; -fx-background-radius: 12;");
        if (sunIcon != null) sunContainer.getChildren().add(sunIcon);
        sunContainer.getChildren().add(sunLabel);

        waveLabel = new Label("Wave: 0");
        waveLabel.setStyle("-fx-font-size: 18; -fx-text-fill: #ff0000; -fx-font-weight: bold;");
        leftPanel.getChildren().addAll(sunContainer, waveLabel);

        pauseButton = new Button("||");
        pauseButton.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-min-width: 40;");
        pauseButton.setOnAction(e -> togglePause());

        Button shovelBtn = new Button();
        ImageView shovelIcon = loadButtonGraphic("/111px-Shovel2.png", 35, 35);
        if (shovelIcon != null) {
            shovelBtn.setGraphic(shovelIcon);
        } else {
            shovelBtn.setText("Shovel");
        }
        shovelBtn.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5); -fx-background-radius: 10; -fx-min-width: 55; -fx-min-height: 55;");
        shovelBtn.setOnDragDetected(e -> {
            Dragboard db = shovelBtn.startDragAndDrop(TransferMode.COPY);
            ClipboardContent content = new ClipboardContent();
            content.putString("Shovel");
            db.setContent(content);
            if (shovelIcon != null && shovelIcon.getImage() != null) {
                Image img = shovelIcon.getImage();
                db.setDragView(img, img.getWidth() / 2, img.getHeight() / 2);
            }
            gameCanvas.startDragPlantPreview("Shovel");
            gameCanvas.setSelectedPlant(null);
            e.consume();
        });
        shovelBtn.setOnDragDone(e -> {
            gameCanvas.clearDragPlantPreview();
            e.consume();
        });

        notificationBtn = new Button("");
        notificationBtn.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white; -fx-font-weight: bold;");
        notificationBtn.setVisible(false);
        notificationBtn.setOnAction(e -> showZombieIdCard((String)notificationBtn.getUserData()));

        // Thêm một spacer để đẩy nút pause sang bên phải
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        topPanel.getChildren().addAll(leftPanel, notificationBtn, spacer, shovelBtn, pauseButton);
        return topPanel;
    }

    private VBox createSidePanel() {
        VBox sidePanel = new VBox(10);
        sidePanel.setPadding(new Insets(80, 10, 10, 10)); // Giảm padding để cân đối với Sun Label
        sidePanel.setStyle("-fx-background-color: transparent;"); 

        VBox plantSelector = new VBox(2); // Sát nhau chỉ 2px
        
        java.util.List<String> allowedPlants = levelConfig != null ? levelConfig.getAvailablePlantTypes() : 
                java.util.Arrays.asList("Peashooter", "Sunflower", "Wallnut", "SpikeWeed", "KernelPult", "BonkChoy");

        // Always start with Peashooter and Sunflower
        Button peashooterBtn = createPlantButton("", "/PeashooterSeedPacketPvZ2C.png", "Peashooter", () -> new Peashooter(0, 0));
        plantSelector.getChildren().add(peashooterBtn);

        Button sunflowerBtn = createPlantButton("", "/SunflowerPvZ2SeedPacket.png", "Sunflower", () -> new Sunflower(0, 0));
        plantSelector.getChildren().add(sunflowerBtn);

        if (allowedPlants.contains("Wallnut")) {
            Button wallnutBtn = createPlantButton("", "/SeedPacketWall-nut.jpg", "Wallnut", () -> new Wallnut(0, 0));
            plantSelector.getChildren().add(wallnutBtn);
        }

        if (allowedPlants.contains("SpikeWeed")) {
            Button spikeWeedBtn = createPlantButton("", "/SpikeweedPvZ2SeedPacket.png", "SpikeWeed", () -> new SpikeWeed(0, 0));
            plantSelector.getChildren().add(spikeWeedBtn);
        }

        if (allowedPlants.contains("KernelPult")) {
            Button kernelPultBtn = createPlantButton("", "/KernelpultPvZ2SeedPacket.png", "KernelPult", () -> new KernelPult(0, 0));
            plantSelector.getChildren().add(kernelPultBtn);
        }

        if (allowedPlants.contains("BonkChoy")) {
            Button bonkChoyBtn = createPlantButton("", "/BonkChoySeedPacket.png", "BonkChoy", () -> new BonkChoy(0, 0));
            plantSelector.getChildren().add(bonkChoyBtn);
        }

        sidePanel.getChildren().add(plantSelector);
        return sidePanel;
    }

    private Button createPlantButton(String label, String imagePath, String plantName, Supplier<Plant> supplier) {
        Button button = new Button(label);
        button.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-min-width: 150; -fx-min-height: 85; -fx-padding: 0;");
        ImageView graphic = loadButtonGraphic(imagePath, 130, 85);
        if (graphic != null) {
            button.setGraphic(graphic);
        }
        configurePlantDragButton(button, plantName, supplier, graphic != null ? graphic.getImage() : null);
        return button;
    }

    private ImageView loadButtonGraphic(String imagePath, double width, double height) {
        try {
            java.io.InputStream is = getClass().getResourceAsStream(imagePath);
            if (is == null) {
                java.io.File fallbackFile = new java.io.File("src/main/resources" + imagePath);
                if (fallbackFile.exists()) {
                    is = new java.io.FileInputStream(fallbackFile);
                }
            }
            if (is != null) {
                Image image = new Image(is, width, height, true, true);
                is.close();
                return new ImageView(image);
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private void configurePlantDragButton(Button button, String plantName, Supplier<Plant> supplier, Image dragImage) {
        button.setOnAction(e -> gameCanvas.setSelectedPlant(supplier.get()));
        button.setOnDragDetected(e -> {
            Dragboard db = button.startDragAndDrop(TransferMode.COPY);
            ClipboardContent content = new ClipboardContent();
            content.putString(plantName);
            db.setContent(content);
            if (dragImage != null) {
                db.setDragView(dragImage, dragImage.getWidth() / 2, dragImage.getHeight() / 2);
            }
            gameCanvas.startDragPlantPreview(plantName);
            e.consume();
        });
        button.setOnDragDone(e -> {
            gameCanvas.clearDragPlantPreview();
            e.consume();
        });
    }

    private void showZombieIdCard(String type) {
        if (!isPaused) togglePause();
        
        idCardContent.getChildren().clear();
        
        Label cardTitle = new Label("ZOMBIE IDENTIFICATION CARD");
        cardTitle.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #8b4513;");
        
        // Horizontal Layout for ID Card Body
        HBox idBody = new HBox(25);
        idBody.setAlignment(Pos.CENTER_LEFT);
        
        // Left Side: Image
        javafx.scene.shape.Rectangle imgPlaceholder = new javafx.scene.shape.Rectangle(120, 150);
        imgPlaceholder.setArcWidth(10);
        imgPlaceholder.setArcHeight(10);
        imgPlaceholder.setStroke(javafx.scene.paint.Color.DARKGRAY);

        // Right Side: Details
        VBox details = new VBox(10);
        details.setAlignment(Pos.CENTER_LEFT);

        String desc = "";
        String stats = "";
        
        switch(type) {
            case "BasicZombie": imgPlaceholder.setFill(javafx.scene.paint.Color.GRAY); desc = "A standard garden-variety zombie."; stats = "Health: 100 | Speed: Slow"; break;
            case "PharaohZombie": imgPlaceholder.setFill(javafx.scene.paint.Color.GOLDENROD); desc = "His ancient Pharaoh mask provides incredible protection from damage."; stats = "Health: 200 | Speed: Slow"; break;
            case "FlagZombie": imgPlaceholder.setFill(javafx.scene.paint.Color.GOLD); desc = "A rallying zombie that leads a stronger push."; stats = "Health: 120 | Speed: Fast"; break;
            case "AllStarZombie": imgPlaceholder.setFill(javafx.scene.paint.Color.DARKRED); desc = "A powerful all-star zombie with heavy damage."; stats = "Health: 300 | Speed: Moderate"; break;
            case "ChickenWranglerZombie": imgPlaceholder.setFill(javafx.scene.paint.Color.DARKORANGE); desc = "He protects himself with a chicken shield until it breaks."; stats = "Health: 180 | Speed: Slow-to-Fast"; break;
            case "ExcavatorZombie": imgPlaceholder.setFill(javafx.scene.paint.Color.DARKOLIVEGREEN); desc = "He jumps over the first plant he meets."; stats = "Health: 220 | Speed: Slow"; break;
        }
        
        Label nameLabel = new Label("Name: " + type);
        nameLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        Label descLabel = new Label("Description: " + desc);
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(300);
        descLabel.setStyle("-fx-font-size: 14; -fx-font-style: italic;");
        
        Label statsLabel = new Label("Stats: " + stats);
        statsLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #d32f2f;");

        details.getChildren().addAll(nameLabel, descLabel, statsLabel);
        idBody.getChildren().addAll(imgPlaceholder, details);

        Button okBtn = new Button("OK");
        okBtn.setStyle("-fx-font-size: 16; -fx-min-width: 100; -fx-background-color: #8b4513; -fx-text-fill: white;");
        okBtn.setOnAction(e -> {
            idCardOverlay.setVisible(false);
            idCardOverlay.setMouseTransparent(true);
            notificationBtn.setVisible(false);
            togglePause();
        });

        idCardContent.getChildren().addAll(cardTitle, idBody, okBtn);
        idCardOverlay.setVisible(true);
        idCardOverlay.setMouseTransparent(false);
    }

    private void togglePause() {
        isPaused = !isPaused;
        gameBoard.setPaused(isPaused);
        
        if (isPaused) {
            overlayStatusLabel.setText("GAME PAUSED");
            overlayResumeBtn.setVisible(true);
            overlayRestartBtn.setVisible(true);
            overlayNextBtn.setVisible(false);
            overlayMenuBtn.setVisible(true);
            overlayPanel.setVisible(true);
            overlayPanel.setMouseTransparent(false);
        } else {
            overlayPanel.setVisible(false);
            overlayPanel.setMouseTransparent(true);
        }
    }

    private void restartGame() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
        gameBoard.reset(levelConfig);
        isPaused = false;
        gameBoard.setPaused(false);
        overlayPanel.setVisible(false);
        overlayPanel.setMouseTransparent(true);
        idCardOverlay.setVisible(false);
        idCardOverlay.setMouseTransparent(true);
        sunLabel.setText("1000");
        waveLabel.setText("Wave: 0");
        gameCanvas.setSelectedPlant(null);
        startGameLoop();
    }

    private void startGameLoop() {
        if (gameTimer != null) {
            gameTimer.stop();
        }

        gameTimer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (lastUpdate == 0) {
                    lastUpdate = now;
                }

                if (!isPaused && !gameBoard.isGameOver()) {
                    long elapsedNanos = now - lastUpdate;
                    // Tăng độ nhạy: Cập nhật logic mỗi 16ms (60 FPS) thay vì 50ms (20 FPS)
                    if (elapsedNanos >= 16_666_666L) {
                        gameBoard.update();
                        lastUpdate = now;
                    }
                }

                gameCanvas.render();
                updateLabels();
                
                String newZombie = gameBoard.getAndClearLastNewZombieType();
                if (newZombie != null) {
                    notificationBtn.setText("(New) " + newZombie);
                    notificationBtn.setUserData(newZombie);
                    notificationBtn.setVisible(true);
                }

                if (gameBoard.isGameOver()) {
                    stop();
                    showGameOverOverlay();
                }
            }
        };
        gameTimer.start();
    }

    private void showGameOverOverlay() {
        overlayPanel.setVisible(true);
        overlayPanel.setMouseTransparent(false);
        overlayResumeBtn.setVisible(false);
        if (gameBoard.isPlayerWon()) {
            int currentLevel = levelConfig != null ? levelConfig.getLevelNumber() : 1;
            String status = "LEVEL " + currentLevel + " CLEARED!";
            
            // Xác định cây mới mở khóa dựa trên màn chơi vừa hoàn thành
            String unlockMsg = "";
            switch (currentLevel) {
                case 1: unlockMsg = "\nNEW PLANT: Wallnut!"; break;
                case 2: unlockMsg = "\nNEW PLANT: Kernel-Pult!"; break;
                case 3: unlockMsg = "\nNEW PLANT: Spike Weed!"; break;
                case 4: unlockMsg = "\nNEW PLANT: Bonk Choy!"; break;
            }
            
            overlayStatusLabel.setText(status + unlockMsg);
            overlayStatusLabel.setStyle("-fx-text-fill: #00ff00; -fx-font-size: 32; -fx-font-weight: bold; -fx-text-alignment: center;");
            overlayRestartBtn.setVisible(false);
            boolean hasNext = levelConfig != null && levelConfig.getLevelNumber() < 5;
            overlayNextBtn.setVisible(hasNext);
            overlayMenuBtn.setVisible(true);
            if (levelConfig != null) {
                GameProgress.getInstance().unlockLevel(levelConfig.getLevelNumber() + 1);
            }
        } else {
            overlayStatusLabel.setText("GAME OVER!");
            overlayStatusLabel.setStyle("-fx-text-fill: #ff4444; -fx-font-size: 40; -fx-font-weight: bold;");
            overlayRestartBtn.setVisible(true);
            overlayNextBtn.setVisible(false);
            overlayMenuBtn.setVisible(true);
        }
    }

    private void updateLabels() {
        // Đã chạy trong AnimationTimer nên không cần Platform.runLater
        sunLabel.setText(String.valueOf(gameBoard.getSun()));
        waveLabel.setText("Wave: " + gameBoard.getWave());
    }

    private void toggleFullscreen() {
        primaryStage.setFullScreen(!primaryStage.isFullScreen());
    }

    public void show() {
        primaryStage.show();
    }
    
}
