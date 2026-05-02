package com.pvz.game;

import javafx.application.Application;
import javafx.stage.Stage;
import com.pvz.ui.GameScreen;
import com.pvz.ui.LevelSelectScreen;

public class PlantsVsZombiesApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        GameScreen currentScreen = new LevelSelectScreen(primaryStage);
        currentScreen.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
