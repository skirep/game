package com.leveleditor;

import com.badlogic.gdx.Game;
import com.leveleditor.editor.EditorScreen;

/**
 * Main game class that initializes the level editor.
 */
public class EditorGame extends Game {
    
    @Override
    public void create() {
        // Set the editor screen as the main screen
        setScreen(new EditorScreen());
    }

    @Override
    public void dispose() {
        super.dispose();
        if (getScreen() != null) {
            getScreen().dispose();
        }
    }
}
