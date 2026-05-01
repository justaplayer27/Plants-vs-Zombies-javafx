package com.pvz.game;

import javafx.application.Application;
import javafx.stage.Stage;
import com.pvz.ui.LevelSelectScreen;

public class PlantsVsZombiesApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        LevelSelectScreen levelSelect = new LevelSelectScreen(primaryStage);
        levelSelect.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
