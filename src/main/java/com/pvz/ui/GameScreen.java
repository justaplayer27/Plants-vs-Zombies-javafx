package com.pvz.ui;

public interface GameScreen {
    void show();

    default void close() {
    }
}
