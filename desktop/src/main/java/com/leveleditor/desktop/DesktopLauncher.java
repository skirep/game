package com.leveleditor.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.leveleditor.EditorGame;

/**
 * Desktop launcher for the Level Editor.
 * Uses LWJGL3 backend for desktop deployment.
 */
public class DesktopLauncher {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        
        // Window configuration
        config.setTitle("Level Editor - Vertical Shoot'em Up");
        config.setWindowedMode(1280, 720);
        config.setResizable(true);
        config.setForegroundFPS(60);
        
        // Create the application
        new Lwjgl3Application(new EditorGame(), config);
    }
}
