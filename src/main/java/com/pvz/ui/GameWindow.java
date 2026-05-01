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
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
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
    private Font gameFont;
    
    private StackPane rootPane;
    private VBox overlayPanel;
    private Label overlayStatusLabel;
    private Button overlayResumeBtn;
    private Button overlayRestartBtn;
    private Button overlayNextBtn;
    private Button overlayMenuBtn;
    
    private Button notificationBtn;
    private Button unlockInfoBtn;
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
        gameFont = loadPvZFont(16);
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
        overlayStatusLabel.setFont(Font.font(gameFont.getFamily(), 40));
        overlayStatusLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        overlayResumeBtn = new Button("Resume");
        overlayResumeBtn.setFont(Font.font(gameFont.getFamily(), 18));
        overlayResumeBtn.setStyle("-fx-min-width: 150;");
        overlayResumeBtn.setOnAction(e -> togglePause());

        overlayRestartBtn = new Button("Restart");
        overlayRestartBtn.setFont(Font.font(gameFont.getFamily(), 18));
        overlayRestartBtn.setStyle("-fx-min-width: 150; -fx-background-color: #ff4444; -fx-text-fill: white;");
        overlayRestartBtn.setOnAction(e -> {
            overlayPanel.setVisible(false);
            restartGame();
        });

        overlayNextBtn = new Button("Next Level");
        overlayNextBtn.setFont(Font.font(gameFont.getFamily(), 18));
        overlayNextBtn.setStyle("-fx-min-width: 150; -fx-background-color: #4CAF50; -fx-text-fill: white;");
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
        overlayMenuBtn.setFont(Font.font(gameFont.getFamily(), 18));
        overlayMenuBtn.setStyle("-fx-min-width: 150;");
        overlayMenuBtn.setOnAction(e -> {
            LevelSelectScreen menu = new LevelSelectScreen(primaryStage);
            menu.show();
        });

        unlockInfoBtn = new Button();
        unlockInfoBtn.setFont(Font.font(gameFont.getFamily(), 16));
        unlockInfoBtn.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 240;");
        unlockInfoBtn.setVisible(false);
        unlockInfoBtn.setOnAction(e -> showEntityInfoCard((String) unlockInfoBtn.getUserData()));

        overlayPanel.getChildren().addAll(overlayStatusLabel, overlayResumeBtn, overlayRestartBtn, overlayNextBtn, unlockInfoBtn, overlayMenuBtn);
    }

    private void createIdCardOverlay() {
        idCardOverlay = new StackPane();
        idCardOverlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.85);");
        idCardOverlay.setVisible(false);
        idCardOverlay.setMouseTransparent(true);

        idCardContent = new VBox(20);
        idCardContent.setAlignment(Pos.CENTER);
        idCardContent.setMaxSize(550, 350);
        idCardContent.setStyle("-fx-background-color: #424242; -fx-padding: 25; -fx-background-radius: 15;");

        Label cardTitle = new Label("IDENTIFICATION CARD");
        cardTitle.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #8b4513;");
        
        idCardContent.getChildren().add(cardTitle);
        
        Button okBtn = new Button("OK");
        okBtn.setStyle("-fx-font-size: 16; -fx-min-width: 100; -fx-background-color: #8b4513; -fx-text-fill: white;");
        okBtn.setOnAction(e -> {
            idCardOverlay.setVisible(false);
            idCardOverlay.setMouseTransparent(true);
            togglePause();
        });
        idCardContent.getChildren().add(okBtn);
        
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
        sunLabel.setFont(Font.font(gameFont.getFamily(), 18));
        sunLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; " +
                         "-fx-background-color: rgba(0, 0, 0, 0.5); -fx-padding: 2 10 2 10; -fx-background-radius: 12;");
        if (sunIcon != null) sunContainer.getChildren().add(sunIcon);
        sunContainer.getChildren().add(sunLabel);

        waveLabel = new Label("Wave: 0");
        waveLabel.setFont(Font.font(gameFont.getFamily(), 18));
        waveLabel.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
        leftPanel.getChildren().addAll(sunContainer, waveLabel);

        pauseButton = new Button("||");
        pauseButton.setFont(Font.font(gameFont.getFamily(), 16));
        pauseButton.setStyle("-fx-font-weight: bold; -fx-min-width: 40;");
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
        notificationBtn.setOnAction(e -> showEntityInfoCard((String)notificationBtn.getUserData()));

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
            Button wallnutBtn = createPlantButton("", "/SeedPacketWall-nut.png", "Wallnut", () -> new Wallnut(0, 0));
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

    private Font loadPvZFont(double size) {
        try {
            java.io.InputStream is = getClass().getResourceAsStream("/PvZ2 Regular By Beast and MF.ttf");
            if (is == null) {
                java.io.File fallbackFile = new java.io.File("src/main/resources/PvZ2 Regular By Beast and MF.ttf");
                if (fallbackFile.exists()) {
                    is = new java.io.FileInputStream(fallbackFile);
                }
            }
            if (is != null) {
                Font font = Font.loadFont(is, size);
                is.close();
                if (font != null) {
                    return font;
                }
            }
        } catch (Exception ignored) {
        }
        return Font.font("System", size);
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

    private void showNotification(String category, String itemId) {
        if (itemId == null || itemId.isEmpty()) {
            return;
        }
        if ("Zombie".equals(category)) {
            notificationBtn.setText("*New: " + itemId);
            notificationBtn.setUserData(category + ":" + itemId);
            notificationBtn.setVisible(true);
        }
    }

    private void showEntityInfoCard(String itemKey) {
        if (itemKey == null || itemKey.isEmpty()) {
            return;
        }
        if (!isPaused && !gameBoard.isGameOver()) {
            togglePause();
        }

        idCardContent.getChildren().clear();
        idCardContent.setStyle("-fx-background-color: #424242; -fx-padding: 25; -fx-background-radius: 15;");
        String[] parts = itemKey.split(":", 2);
        String category = parts.length > 1 ? parts[0] : "Zombie";
        String type = parts.length > 1 ? parts[1] : parts[0];

        String imagePath = null;
        String desc = "";
        String stats = "";
        String titleText = type;

        switch (category) {
            case "Plant":
                titleText = type;
                switch (type) {
                    case "Peashooter":
                        imagePath = "/PeashooterSeedPacketPvZ2C.png";
                        desc = "A reliable ranged attacker that fires peas at zombies. Good early on for basic line defense.";
                        stats = "Damage: 45 | Speed: Normal | Cost: 100";
                        break;
                    case "Sunflower":
                        imagePath = "/SunflowerPvZ2SeedPacket.png";
                        desc = "She is the heartbeat of the garden, a radiator of pure optimism. While others focus on the battle, she focuses on the light, knowing that as long as she keeps smiling, the rest of the family will never have to fight in the dark.";
                        stats = "Generates: 25 sun | Cooldown: 24s | Cost: 50";
                        break;
                    case "Wallnut":
                        imagePath = "/SeedPacketWall-nut.png";
                        desc = "He doesn't have thorns or projectiles; all he has is his presence. There’s a quiet bravery in standing perfectly still while the world cracks around you. He’s the friend who says, 'It's okay, I'll take the hit so you don't have to.'";
                        stats = "Health: Very High | Slow | Cost: 50";
                        break;
                    case "SpikeWeed":
                        imagePath = "/SpikeweedPvZ2SeedPacket.png";
                        desc = "Born from the shadows of the garden floor, he’s a loner who prefers to stay grounded. He doesn't seek glory or height; he simply offers a sharp reminder to those who try to trample over the things he loves.";
                        stats = "Damage: 10/tick | Slow effect | Cost: 100";
                        break;
                    case "KernelPult":
                        imagePath = "/KernelpultPvZ2SeedPacket.png";
                        desc = "He’s a bit of a dreamer, often launching butter when the world expects corn. But beneath that flighty exterior is a loyal defender who understands that sometimes, the best way to stop a monster is to just make them pause for a moment of sticky confusion.";
                        stats = "Damage: 80 | Lobbed attack | Cost: 125";
                        break;
                    case "BonkChoy":
                        imagePath = "/BonkChoySeedPacket.png";
                        desc = "A soul with a fighter’s spirit and a gardener’s heart. He doesn't wait for the trouble to come to him; he meets it head-on with a flurry of passion. He’s the protector who believes that sometimes, a stern talk isn't enough—you need to put some muscle behind your convictions.";
                        stats = "Damage: 90 | Melee | Cost: 150";
                        break;
                    default:
                        desc = "Unknown plant. Use it carefully and learn its strengths in battle.";
                        stats = "Cost: ? | Effect: ?";
                        break;
                }
                break;
            default:
                switch(type) {
                    case "BasicZombie":
                        imagePath = "/Mobile - Plants vs. Zombies 2 - Basic Zombie - Walking.gif";
                        desc = "Slow-moving walker that relentlessly eats plants when it reaches them. Good for early defense and training your pea shooters.";
                        stats = "Health: 200 | Speed: Slow";
                        break;
                    case "PharaohZombie":
                        imagePath = "/Mobile - Plants vs. Zombies 2 - Pharaoh Zombie - Walking - Sarcophagus.gif";
                        desc = "Wrapped in the gold of a forgotten era, he is a king without a kingdom. He clings to his heavy sarcophagus as both a shield and a burden, a tragic soul trying to preserve his ancient dignity in a world that has long since moved on to the next life.";
                        stats = "Health: 400 + 300 armor | Speed: Slow";
                        break;
                    case "FlagZombie":
                        imagePath = "/Mobile - Plants vs. Zombies 2 - Basic Zombie - Walking - Flag Zombie.gif";
                        desc = "In life, he was always the one leading the parade, the first to volunteer for the cause. In death, that leadership has turned into a haunting duty. He carries the tattered flag not for victory, but because it’s the only thing left that reminds him he was once part of something bigger.";
                        stats = "Health: 240 | Speed: Fast";
                        break;
                    case "AllStarZombie":
                        imagePath = "/Mobile - Plants vs. Zombies 2 - All-Star Zombie - Walking.gif";
                        desc = "Charges through the first plant it hits, destroying it instantly, then slows down to a heavy walk. Treat it as a high-priority target.";
                        stats = "Health: 600 | Speed: Charge then slow";
                        break;
                    case "ChickenWranglerZombie":
                        imagePath = "/Mobile - Plants vs. Zombies 2 - Chicken Wrangler Zombie - Walking.gif";
                        desc = "When damaged, it stops and releases chickens in bursts. The chickens sprint forward quickly, so take down the wrangler fast.";
                        stats = "Health: 360 | Speed: Slow-to-Fast | Chicken delay: 0.6s";
                        break;
                    case "ExcavatorZombie":
                        imagePath = "/Mobile - Plants vs. Zombies 2 - Excavator Zombie - Walking.gif";
                        desc = "He spent his life digging for treasures and truth beneath the earth. Now, his shovel is just a tool of frustration, a way to push aside the very life he used to cultivate. He’s a restless wanderer, still searching for something he can't quite remember losing.";
                        stats = "Health: 440 | Speed: Slow";
                        break;
                    default:
                        desc = "Unknown zombie type. Stay alert and watch its behavior in battle.";
                        stats = "Health: ? | Speed: ?";
                        break;
                }
                break;
        }

        ImageView entityImage = null;
        if (imagePath != null) {
            entityImage = loadButtonGraphic(imagePath, 140, 140);
            if (entityImage != null) {
                entityImage.setPreserveRatio(true);
                entityImage.setSmooth(true);
            }
        }

        javafx.scene.Node imageRegion;
        if (entityImage != null) {
            imageRegion = entityImage;
        } else {
            javafx.scene.shape.Rectangle imgPlaceholder = new javafx.scene.shape.Rectangle(140, 140);
            imgPlaceholder.setArcWidth(12);
            imgPlaceholder.setArcHeight(12);
            imgPlaceholder.setFill(javafx.scene.paint.Color.DARKGRAY);
            imageRegion = imgPlaceholder;
        }

        HBox idBody = new HBox(25);
        idBody.setAlignment(Pos.CENTER_LEFT);

        VBox details = new VBox(10);
        details.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label(titleText);
        nameLabel.setStyle("-fx-font-size: 22; -fx-font-weight: bold; -fx-text-fill: white;");

        Label descLabel = new Label(desc);
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(320);
        descLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #f0f0f0;");

        Label statsLabel = new Label(stats);
        statsLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #ffcc00;");

        details.getChildren().addAll(nameLabel, descLabel, statsLabel);
        idBody.getChildren().addAll(imageRegion, details);

        Button okBtn = new Button("OK");
        okBtn.setStyle("-fx-font-size: 16; -fx-min-width: 100; -fx-background-color: #8b4513; -fx-text-fill: white;");
        okBtn.setOnAction(e -> {
            idCardOverlay.setVisible(false);
            idCardOverlay.setMouseTransparent(true);
            notificationBtn.setVisible(false);
            unlockInfoBtn.setVisible(false);
            if (!gameBoard.isGameOver() && isPaused) {
                togglePause();
            }
        });

        Label cardTitle = new Label(titleText);
        cardTitle.setStyle("-fx-font-size: 28; -fx-font-weight: bold; -fx-text-fill: white;");
        idCardContent.getChildren().addAll(cardTitle, idBody, okBtn);
        idCardOverlay.setVisible(true);
        idCardOverlay.setMouseTransparent(false);
    }

    private String getUnlockedPlantName(int currentLevel) {
        switch (currentLevel) {
            case 1: return "Wallnut";
            case 2: return "KernelPult";
            case 3: return "SpikeWeed";
            case 4: return "BonkChoy";
            default: return null;
        }
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
                    showNotification("Zombie", newZombie);
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
            String unlockPlant = getUnlockedPlantName(currentLevel);
            if (unlockPlant != null) {
                unlockInfoBtn.setText("*New: " + unlockPlant);
                unlockInfoBtn.setUserData("Plant:" + unlockPlant);
                unlockInfoBtn.setVisible(true);
                GameProgress.getInstance().discoverPlant(unlockPlant);
            } else {
                unlockInfoBtn.setVisible(false);
            }
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
