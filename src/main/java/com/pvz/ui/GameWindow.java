package com.pvz.ui;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.geometry.Insets;
import com.pvz.game.GameBoard;
import com.pvz.entities.*;

public class GameWindow {
    private Stage primaryStage;
    private GameBoard gameBoard;
    private GameCanvas gameCanvas;
    private Label sunLabel;
    private Label scoreLabel;
    private Label waveLabel;
    private Button pauseButton;
    private boolean isPaused;

    public GameWindow(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.gameBoard = new GameBoard();
        this.isPaused = false;
        setupUI();
    }

    private void setupUI() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #2a2a2a;");

        // Top panel with info
        HBox topPanel = createTopPanel();
        root.setTop(topPanel);

        // Game canvas
        gameCanvas = new GameCanvas(gameBoard);
        root.setCenter(gameCanvas);

        // Bottom panel with plant selection
        VBox bottomPanel = createBottomPanel();
        root.setBottom(bottomPanel);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Plants vs Zombies");
        primaryStage.setScene(scene);
        
        // Set to fullscreen to hide taskbar
        primaryStage.setFullScreen(true);
        primaryStage.setResizable(false);

        // Start game loop
        startGameLoop();
    }

    private HBox createTopPanel() {
        HBox topPanel = new HBox(20);
        topPanel.setPadding(new Insets(10));
        topPanel.setStyle("-fx-background-color: #1a1a1a; -fx-border-color: #444444; -fx-border-width: 0 0 1 0;");

        sunLabel = new Label("Sun: 100");
        sunLabel.setStyle("-fx-font-size: 18; -fx-text-fill: #ffff00;");

        scoreLabel = new Label("Score: 0");
        scoreLabel.setStyle("-fx-font-size: 18; -fx-text-fill: #00ff00;");

        waveLabel = new Label("Wave: 1");
        waveLabel.setStyle("-fx-font-size: 18; -fx-text-fill: #ff0000;");

        pauseButton = new Button("Pause");
        pauseButton.setStyle("-fx-font-size: 14;");
        pauseButton.setOnAction(e -> togglePause());

        topPanel.getChildren().addAll(sunLabel, scoreLabel, waveLabel, pauseButton);
        return topPanel;
    }

    private VBox createBottomPanel() {
        VBox bottomPanel = new VBox(10);
        bottomPanel.setPadding(new Insets(10));
        bottomPanel.setStyle("-fx-background-color: #1a1a1a; -fx-border-color: #444444; -fx-border-width: 1 0 0 0;");

        Label instructionLabel = new Label("Click to place plants | Right-click to remove");
        instructionLabel.setStyle("-fx-text-fill: #aaaaaa;");

        HBox plantSelector = new HBox(10);
        
        Button peashooterBtn = new Button("Peashooter (100)");
        peashooterBtn.setStyle("-fx-font-size: 12;");
        peashooterBtn.setOnAction(e -> gameCanvas.setSelectedPlant(new Peashooter(0, 0)));

        Button sunflowerBtn = new Button("Sunflower (50)");
        sunflowerBtn.setStyle("-fx-font-size: 12;");
        sunflowerBtn.setOnAction(e -> gameCanvas.setSelectedPlant(new Sunflower(0, 0)));

        plantSelector.getChildren().addAll(peashooterBtn, sunflowerBtn);
        bottomPanel.getChildren().addAll(instructionLabel, plantSelector);
        return bottomPanel;
    }

    private void togglePause() {
        isPaused = !isPaused;
        pauseButton.setText(isPaused ? "Resume" : "Pause");
    }

    private void startGameLoop() {
        Thread gameThread = new Thread(() -> {
            long lastUpdate = System.currentTimeMillis();
            while (!gameBoard.isGameOver()) {
                if (!isPaused) {
                    gameBoard.update();
                }
                gameCanvas.render();
                updateLabels();

                // Control frame rate
                long now = System.currentTimeMillis();
                long elapsed = now - lastUpdate;
                if (elapsed < 50) {
                    try {
                        Thread.sleep(50 - elapsed);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                lastUpdate = System.currentTimeMillis();
            }
            
            // Game over
            javafx.application.Platform.runLater(() -> {
                if (gameBoard.isPlayerWon()) {
                    scoreLabel.setText("YOU WON!");
                    scoreLabel.setStyle("-fx-font-size: 24; -fx-text-fill: #00ff00;");
                } else {
                    scoreLabel.setText("GAME OVER!");
                    scoreLabel.setStyle("-fx-font-size: 24; -fx-text-fill: #ff0000;");
                }
            });
        });
        gameThread.setDaemon(true);
        gameThread.start();
    }

    private void updateLabels() {
        javafx.application.Platform.runLater(() -> {
            sunLabel.setText("Sun: " + gameBoard.getSun());
            scoreLabel.setText("Score: " + gameBoard.getScore());
            waveLabel.setText("Wave: " + gameBoard.getWave());
        });
    }

    public void show() {
        primaryStage.show();
    }
}
