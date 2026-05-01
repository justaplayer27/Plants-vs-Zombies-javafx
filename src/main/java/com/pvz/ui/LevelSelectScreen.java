package com.pvz.ui;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.util.Set;
import com.pvz.game.GameProgress;
import com.pvz.game.LevelConfig;

public class LevelSelectScreen {
    private Stage primaryStage;
    private StackPane almanacOverlay;
    private VBox almanacInfoContent;
    private HBox categoriesBox;
    private Font gameFont;

    public LevelSelectScreen(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void show() {
        StackPane rootPane = new StackPane();

        gameFont = loadPvZFont(18);
        rootPane.setStyle("-fx-font-family: '" + gameFont.getFamily() + "';");

        VBox root = new VBox(20);
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: transparent;");
        root.setPadding(new Insets(20));

        ImageView logoView = loadLogoImage("/Plants-Vs-Zombies-Logo-PNG-Isolated-HD.png", 1498, 461);
        if (logoView != null) {
            logoView.setPreserveRatio(true);
        }

        Label subtitle = new Label("Select Level");
        subtitle.setFont(Font.font(gameFont.getFamily(), 22));
        subtitle.setStyle("-fx-text-fill: white;");

        StackPane titlePane = new StackPane();
        titlePane.setMaxWidth(860);
        titlePane.setMinHeight(220);
        titlePane.setPadding(new Insets(20, 20, 0, 20));

        if (logoView != null) {
            titlePane.getChildren().add(logoView);
        } else {
            Label title = new Label("PLANTS VS ZOMBIES");
            title.setFont(Font.font(gameFont.getFamily(), 64));
            title.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
            titlePane.getChildren().add(title);
        }

        Button almanacToggle = new Button("Almanac");
        almanacToggle.setFont(Font.font(gameFont.getFamily(), 18));
        almanacToggle.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-min-width: 140; -fx-min-height: 50;");
        almanacToggle.setOnAction(e -> {
            if (almanacOverlay != null) {
                almanacOverlay.setVisible(true);
                almanacOverlay.setMouseTransparent(false);
            }
        });
        StackPane.setAlignment(almanacToggle, Pos.TOP_RIGHT);
        titlePane.getChildren().add(almanacToggle);

        HBox subtitleRow = new HBox();
        subtitleRow.setAlignment(Pos.CENTER);
        subtitleRow.getChildren().add(subtitle);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        HBox levelBox = new HBox(15);
        levelBox.setAlignment(Pos.CENTER);

        int maxUnlocked = GameProgress.getInstance().getMaxUnlockedLevel();

        for (int i = 1; i <= 6; i++) {
            final int level = i;
            String label = (i == 6) ? "Custom (Test)" : "Level " + i;
            Button btn = new Button(label);

            // Luôn mở khóa màn Custom (Level 6) để test
            if (i == 6 || i <= maxUnlocked) {
                btn.setFont(Font.font(gameFont.getFamily(), 16));
                btn.setStyle("-fx-font-size: 16; -fx-min-width: 100; -fx-min-height: 60; -fx-background-color: #4CAF50; -fx-text-fill: white;");
                btn.setOnAction(e -> startLevel(level));
            } else {
                btn.setFont(Font.font(gameFont.getFamily(), 16));
                btn.setStyle("-fx-font-size: 16; -fx-min-width: 100; -fx-min-height: 60; -fx-background-color: #555555; -fx-text-fill: #888888;");
                btn.setDisable(true);
                btn.setText("Level " + i + "\n(Locked)");
            }
            levelBox.getChildren().add(btn);
        }

Button resetBtn = new Button("Reset Progress");
        resetBtn.setFont(Font.font(gameFont.getFamily(), 14));
        resetBtn.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white; -fx-min-width: 160; -fx-min-height: 42;");
        resetBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Reset");
            alert.setHeaderText("Are you sure you want to reset your progress?");
            alert.setContentText("This will erase all your game progress, including unlocked levels and Almanac entries. This action cannot be undone.");

            ButtonType buttonTypeYes = new ButtonType("Yes");
            ButtonType buttonTypeNo = new ButtonType("No");
            alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

            alert.showAndWait().ifPresent(response -> {
                if (response == buttonTypeYes) {
                    GameProgress.getInstance().reset();
                    show(); // refresh
                }
            });
        });

        root.getChildren().addAll(titlePane, subtitleRow, spacer, levelBox, resetBtn);

        almanacOverlay = createAlmanacOverlay();

        ImageView backgroundImage = loadBackgroundImage("/R.jpg", 900, 700);
        Rectangle darkOverlay = new Rectangle(900, 700, Color.rgb(0, 0, 0, 0.45));
        darkOverlay.widthProperty().bind(rootPane.widthProperty());
        darkOverlay.heightProperty().bind(rootPane.heightProperty());

        rootPane.getChildren().addAll(backgroundImage, darkOverlay, root, almanacOverlay);

        Scene scene = new Scene(rootPane, 900, 700);
        primaryStage.setTitle("Plants vs Zombies - Level Select");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private StackPane createAlmanacOverlay() {
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.85);");
        overlay.setVisible(false);
        overlay.setMouseTransparent(true);

        VBox container = new VBox(15);
        container.setAlignment(Pos.TOP_CENTER);
        container.setMaxSize(820, 600);
        container.setStyle("-fx-background-color: #1f1f1f; -fx-padding: 20; -fx-background-radius: 15;");

        Label heading = new Label("Almanac");
        heading.setFont(Font.font(gameFont.getFamily(), 28));
        heading.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        Button closeBtn = new Button("Close");
        closeBtn.setFont(Font.font(gameFont.getFamily(), 14));
        closeBtn.setStyle("-fx-background-color: #8b4513; -fx-text-fill: white; -fx-min-width: 100;");
        closeBtn.setOnAction(e -> {
            overlay.setVisible(false);
            overlay.setMouseTransparent(true);
            // Reset view to show list again when Almanac is closed
            almanacInfoContent.setVisible(false);
            almanacInfoContent.setManaged(false);
            categoriesBox.setVisible(true);
            categoriesBox.setManaged(true);
        });

        HBox headerRow = new HBox(10);
        headerRow.setAlignment(Pos.CENTER);
        headerRow.getChildren().addAll(heading, closeBtn);

        categoriesBox = createAlmanacSection();
        categoriesBox.setPrefWidth(780);

        almanacInfoContent = new VBox(15);
        almanacInfoContent.setAlignment(Pos.CENTER);
        almanacInfoContent.setMaxWidth(760);
        almanacInfoContent.setStyle("-fx-background-color: #424242; -fx-padding: 20; -fx-background-radius: 12;");
        almanacInfoContent.setVisible(false);
        almanacInfoContent.setManaged(false);

        container.getChildren().addAll(headerRow, categoriesBox, almanacInfoContent);
        overlay.getChildren().add(container);
        return overlay;
    }

    private HBox createAlmanacSection() {
        HBox section = new HBox(20);
        section.setAlignment(Pos.CENTER);
        section.setPadding(new Insets(15));
        section.setStyle("-fx-background-color: #1f1f1f; -fx-background-radius: 15;");

        VBox plantCategory = createAlmanacCategory("Plants", getAllPlantTypes(), "Plant");
        VBox zombieCategory = createAlmanacCategory("Zombies", getAllZombieTypes(), "Zombie");

        section.getChildren().addAll(plantCategory, zombieCategory);
        return section;
    }

    private VBox createAlmanacCategory(String title, java.util.List<String> items, String category) {
        VBox container = new VBox(10);
        container.setAlignment(Pos.TOP_CENTER);

        Label heading = new Label(title);
        heading.setFont(Font.font(gameFont.getFamily(), 18));
        heading.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        int col = 0;
        int row = 0;
        for (String item : items) {
            boolean isDiscovered = "Plant".equals(category) ? 
                                   GameProgress.getInstance().isPlantDiscovered(item) : 
                                   GameProgress.getInstance().isZombieDiscovered(item);
            
            if (isDiscovered) {
                String displayName = item.replace("Zombie", "").replace("Pult", " Pult").replace("BonkChoy", "Bonk Choy");
                Button tile = createAlmanacEntry(category, item, displayName.trim(), getAlmanacImagePath(category, item));
                grid.add(tile, col, row);
            } else {
                Button lockedTile = createAlmanacEntry(category, item, "???", null);
                grid.add(lockedTile, col, row);
            }
            col++;
            if (col >= 3) {
                col = 0;
                row++;
            }
        }

        container.getChildren().addAll(heading, grid);
        return container;
    }

    private Button createAlmanacEntry(String category, String itemId, String displayName, String imagePath) {
        Button entry = new Button(displayName);
        entry.setFont(Font.font(gameFont.getFamily(), 12));
        entry.setWrapText(true);
        String baseStyle = "-fx-background-color: #555555; -fx-text-fill: white; -fx-font-family: '" + gameFont.getFamily() + "'; -fx-font-size: 12; -fx-background-radius: 10; -fx-min-width: 120; -fx-min-height: 90;";
        entry.setStyle(baseStyle);

        entry.setOnMouseEntered(e -> entry.setStyle("-fx-background-color: #666666; -fx-text-fill: white; -fx-font-family: '" + gameFont.getFamily() + "'; -fx-font-size: 12; -fx-background-radius: 10; -fx-min-width: 120; -fx-min-height: 90;"));
        entry.setOnMouseExited(e -> entry.setStyle(baseStyle));

        ImageView graphic = loadLevelSelectGraphic(imagePath, 60, 60);
        if (graphic != null) {
            entry.setGraphic(graphic);
            entry.setContentDisplay(javafx.scene.control.ContentDisplay.TOP);
        }

        entry.setOnAction(e -> showAlmanacInfo(category, itemId, displayName));
        return entry;
    }

    private ImageView loadLevelSelectGraphic(String imagePath, double width, double height) {
        if (imagePath == null) {
            return null;
        }
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
                ImageView imageView = new ImageView(image);
                imageView.setPreserveRatio(true);
                return imageView;
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private ImageView loadLogoImage(String imagePath, double width, double height) {
        ImageView imageView = loadLevelSelectGraphic(imagePath, width, height);
        if (imageView == null) {
            return new ImageView();
        }
        return imageView;
    }

    private ImageView loadBackgroundImage(String imagePath, double width, double height) {
        ImageView imageView = loadLevelSelectGraphic(imagePath, width, height);
        if (imageView == null) {
            imageView = new ImageView();
        }
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setPreserveRatio(false);
        return imageView;
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

    private void showAlmanacInfo(String category, String itemId, String displayName) {
        almanacInfoContent.getChildren().clear();
        
        // Hide the grid list and show the info card
        categoriesBox.setVisible(false);
        categoriesBox.setManaged(false);
        almanacInfoContent.setVisible(true);
        almanacInfoContent.setManaged(true);

        Label title = new Label(displayName);
        title.setFont(Font.font(gameFont.getFamily(), 28));
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");

        HBox idBody = new HBox(25);
        idBody.setAlignment(Pos.CENTER_LEFT);

        // Load entity image
        String imagePath = getAlmanacImagePath(category, itemId);
        ImageView entityImage = loadLevelSelectGraphic(imagePath, 140, 140);
        
        javafx.scene.Node imageRegion;
        if (entityImage != null) {
            entityImage.setPreserveRatio(true);
            entityImage.setSmooth(true);
            imageRegion = entityImage;
        } else {
            javafx.scene.shape.Rectangle imgPlaceholder = new javafx.scene.shape.Rectangle(140, 140);
            imgPlaceholder.setArcWidth(12);
            imgPlaceholder.setArcHeight(12);
            imgPlaceholder.setFill(javafx.scene.paint.Color.DARKGRAY);
            imageRegion = imgPlaceholder;
        }

        VBox details = new VBox(10);
        details.setAlignment(Pos.CENTER_LEFT);

        Label descLabel = new Label(getAlmanacDescription(category, itemId));
        descLabel.setFont(Font.font(gameFont.getFamily(), 15));
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(400);
        descLabel.setStyle("-fx-text-fill: #f0f0f0;");

        Label statsLabel = new Label(getAlmanacStats(category, itemId));
        statsLabel.setFont(Font.font(gameFont.getFamily(), 15));
        statsLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #ffcc00;");

        details.getChildren().addAll(descLabel, statsLabel);
        idBody.getChildren().addAll(imageRegion, details);

        Button backBtn = new Button("Back to List");
        backBtn.setFont(Font.font(gameFont.getFamily(), 14));
        backBtn.setStyle("-fx-background-color: #8b4513; -fx-text-fill: white; -fx-min-width: 120;");
        backBtn.setOnAction(e -> {
            almanacInfoContent.setVisible(false);
            almanacInfoContent.setManaged(false);
            categoriesBox.setVisible(true);
            categoriesBox.setManaged(true);
        });

        almanacInfoContent.getChildren().addAll(title, idBody, backBtn);
        almanacOverlay.setVisible(true);
        almanacOverlay.setMouseTransparent(false);
    }

    private String getAlmanacImagePath(String category, String itemId) {
        if ("Plant".equals(category)) {
            switch (itemId) {
                case "Peashooter": return "/PeashooterSeedPacketPvZ2C.png";
                case "Sunflower": return "/SunflowerPvZ2SeedPacket.png";
                case "Wallnut": return "/SeedPacketWall-nut.png";
                case "SpikeWeed": return "/SpikeweedPvZ2SeedPacket.png";
                case "KernelPult": return "/KernelpultPvZ2SeedPacket.png";
                case "BonkChoy": return "/BonkChoySeedPacket.png";
                default: return null;
            }
        }
        if ("Zombie".equals(category)) {
            switch (itemId) {
                case "BasicZombie": return "/Mobile - Plants vs. Zombies 2 - Basic Zombie - Walking.gif";
                case "FlagZombie": return "/Mobile - Plants vs. Zombies 2 - Basic Zombie - Walking - Flag Zombie.gif";
                case "PharaohZombie": return "/Mobile - Plants vs. Zombies 2 - Pharaoh Zombie - Walking - Sarcophagus.gif";
                case "ExcavatorZombie": return "/Mobile - Plants vs. Zombies 2 - Excavator Zombie - Walking.gif";
                case "ChickenWranglerZombie": return "/Mobile - Plants vs. Zombies 2 - Chicken Wrangler Zombie - Walking.gif";
                case "AllStarZombie": return "/Mobile - Plants vs. Zombies 2 - All-Star Zombie - Walking.gif";
                default: return null;
            }
        }
        return null;
    }

    private String getAlmanacDescription(String category, String itemId) {
        if ("Plant".equals(category)) {
            switch (itemId) {
                case "Peashooter": return "A reliable ranged attacker that fires peas at zombies. Good early on for basic line defense.";
                case "Sunflower": return "She is the heartbeat of the garden, a radiator of pure optimism. While others focus on the battle, she focuses on the light, knowing that as long as she keeps smiling, the rest of the family will never have to fight in the dark.";
                case "Wallnut": return "He doesn't have thorns or projectiles; all he has is his presence. There’s a quiet bravery in standing perfectly still while the world cracks around you. He’s the friend who says, 'It's okay, I'll take the hit so you don't have to.'";
                case "SpikeWeed": return "Born from the shadows of the garden floor, he’s a loner who prefers to stay grounded. He doesn't seek glory or height; he simply offers a sharp reminder to those who try to trample over the things he loves.";
                case "KernelPult": return "He’s a bit of a dreamer, often launching butter when the world expects corn. But beneath that flighty exterior is a loyal defender who understands that sometimes, the best way to stop a monster is to just make them pause for a moment of sticky confusion.";
                case "BonkChoy": return "A soul with a fighter’s spirit and a gardener’s heart. He doesn't wait for the trouble to come to him; he meets it head-on with a flurry of passion. He’s the protector who believes that sometimes, a stern talk isn't enough—you need to put some muscle behind your convictions.";
            }
        } else {
            switch (itemId) {
                case "BasicZombie": return "Slow-moving walker that attacks plants when it reaches them. The first enemy you will face.";
                case "PharaohZombie": return "Wrapped in the gold of a forgotten era, he is a king without a kingdom. He clings to his heavy sarcophagus as both a shield and a burden, a tragic soul trying to preserve his ancient dignity in a world that has long since moved on to the next life.";
                case "FlagZombie": return "In life, he was always the one leading the parade, the first to volunteer for the cause. In death, that leadership has turned into a haunting duty. He carries the tattered flag not for victory, but because it’s the only thing left that reminds him he was once part of something bigger.";
                case "AllStarZombie": return "Runs through the first plant it contacts and continues on a slower but powerful charge.";
                case "ChickenWranglerZombie": return "Releases chickens when hurt. Those chickens sprint forward and can overwhelm your defenses.";
                case "ExcavatorZombie": return "He spent his life digging for treasures and truth beneath the earth. Now, his shovel is just a tool of frustration, a way to push aside the very life he used to cultivate. He’s a restless wanderer, still searching for something he can't quite remember losing.";
            }
        }
        return "No information available for this entry.";
    }

    private String getAlmanacStats(String category, String itemId) {
        if ("Plant".equals(category)) {
            switch (itemId) {
                case "Peashooter": return "Damage: 45 | Speed: Normal | Cost: 100";
                case "Sunflower": return "Generates: 25 sun | Cooldown: 24s | Cost: 50";
                case "Wallnut": return "Health: Very High | Slow | Cost: 50";
                case "SpikeWeed": return "Damage: 10/tick | Slow effect | Cost: 100";
                case "KernelPult": return "Damage: 80 | Lobbed attack | Cost: 125";
                case "BonkChoy": return "Damage: 90 | Melee | Cost: 150";
            }
        } else {
            switch (itemId) {
                case "BasicZombie": return "Health: 200 | Speed: Slow";
                case "PharaohZombie": return "Health: 400 + 300 armor | Speed: Slow";
                case "FlagZombie": return "Health: 240 | Speed: Fast";
                case "AllStarZombie": return "Health: 600 | Speed: Charge then slow";
                case "ChickenWranglerZombie": return "Health: 360 | Speed: Slow-to-Fast";
                case "ExcavatorZombie": return "Health: 440 | Speed: Slow";
            }
        }
        return "Stats unknown.";
    }

    private java.util.List<String> getAllPlantTypes() {
        return java.util.Arrays.asList("Peashooter", "Sunflower", "Wallnut", "SpikeWeed", "KernelPult", "BonkChoy");
    }

    private java.util.List<String> getAllZombieTypes() {
        return java.util.Arrays.asList("BasicZombie", "FlagZombie", "PharaohZombie", "ExcavatorZombie", "ChickenWranglerZombie", "AllStarZombie");
    }

    private void startLevel(int level) {
        LevelConfig config = LevelConfig.getLevel(level);
        GameWindow gameWindow = new GameWindow(primaryStage, config);
        gameWindow.show();
    }
}
