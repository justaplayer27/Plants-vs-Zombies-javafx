package com.pvz.game;

import javafx.application.Application;
import javafx.stage.Stage;
import com.pvz.ui.GameWindow;

public class PlantsVsZombiesApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        GameWindow gameWindow = new GameWindow(primaryStage);
        gameWindow.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
