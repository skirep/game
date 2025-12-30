package com.leveleditor.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.leveleditor.model.EventType;

/**
 * Main editor screen containing the UI and timeline view.
 */
public class EditorScreen implements Screen {
    private Stage stage;
    private TimelineView timelineView;
    private EditorController controller;
    private Skin skin;

    // UI elements
    private Label statusLabel;
    private TextField filenameField;

    public EditorScreen() {
        stage = new Stage(new ScreenViewport());
        timelineView = new TimelineView();
        controller = new EditorController(timelineView);
        
        // Create a simple skin for UI
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        
        createUI();
        
        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Creates the user interface with buttons and controls.
     */
    private void createUI() {
        Table rootTable = new Table();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        // Top toolbar
        Table toolbar = new Table();
        toolbar.defaults().pad(5);

        // File operations
        Label fileLabel = new Label("File:", skin);
        toolbar.add(fileLabel);

        filenameField = new TextField("level1.json", skin);
        filenameField.setWidth(150);
        toolbar.add(filenameField).width(150);

        TextButton newButton = new TextButton("New", skin);
        newButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.newLevel();
                updateStatus("New level created");
            }
        });
        toolbar.add(newButton);

        TextButton saveButton = new TextButton("Save", skin);
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.saveLevel(filenameField.getText());
                updateStatus("Level saved: " + filenameField.getText());
            }
        });
        toolbar.add(saveButton);

        TextButton loadButton = new TextButton("Load", skin);
        loadButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.loadLevel(filenameField.getText());
                updateStatus("Level loaded: " + filenameField.getText());
            }
        });
        toolbar.add(loadButton);

        toolbar.add(new Label(" | ", skin));

        // Event creation buttons
        Label addLabel = new Label("Add Event:", skin);
        toolbar.add(addLabel);

        TextButton addEnemyButton = new TextButton("Enemy", skin);
        addEnemyButton.getColor().set(1f, 0.2f, 0.2f, 1f);
        addEnemyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                addEventAtCenter(EventType.ENEMY);
            }
        });
        toolbar.add(addEnemyButton);

        TextButton addFormationButton = new TextButton("Formation", skin);
        addFormationButton.getColor().set(1f, 0.5f, 0f, 1f);
        addFormationButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                addEventAtCenter(EventType.FORMATION);
            }
        });
        toolbar.add(addFormationButton);

        TextButton addBossButton = new TextButton("Boss", skin);
        addBossButton.getColor().set(0.8f, 0f, 0.8f, 1f);
        addBossButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                addEventAtCenter(EventType.BOSS);
            }
        });
        toolbar.add(addBossButton);

        TextButton addPowerUpButton = new TextButton("PowerUp", skin);
        addPowerUpButton.getColor().set(0f, 0.8f, 0f, 1f);
        addPowerUpButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                addEventAtCenter(EventType.POWER_UP);
            }
        });
        toolbar.add(addPowerUpButton);

        toolbar.add(new Label(" | ", skin));

        TextButton deleteButton = new TextButton("Delete", skin);
        deleteButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.deleteSelectedEvent();
                updateStatus("Event deleted");
            }
        });
        toolbar.add(deleteButton);

        // Status label
        statusLabel = new Label("Level Editor Ready", skin);
        statusLabel.setColor(Color.LIGHT_GRAY);

        // Layout
        rootTable.add(toolbar).top().expandX().fillX().row();
        rootTable.add(statusLabel).bottom().left().pad(10).row();
    }

    /**
     * Adds an event at the center of the current view.
     */
    private void addEventAtCenter(EventType type) {
        Vector3 center = timelineView.getCamera().position;
        float time = timelineView.yToTime(center.y);
        float x = timelineView.screenXToNormalized(center.x);
        
        controller.createEvent(type, time, x);
        updateStatus("Added " + type + " event at time " + String.format("%.1f", time));
    }

    /**
     * Updates the status label text.
     */
    private void updateStatus(String message) {
        statusLabel.setText(message);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        // Handle input
        handleInput();
        timelineView.handleInput();

        // Clear screen
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Render timeline
        timelineView.render();

        // Render UI
        stage.act(delta);
        stage.draw();
    }

    /**
     * Handles keyboard input.
     */
    private void handleInput() {
        // Delete key to remove selected event
        if (Gdx.input.isKeyJustPressed(Input.Keys.DEL) || 
            Gdx.input.isKeyJustPressed(Input.Keys.FORWARD_DEL)) {
            controller.deleteSelectedEvent();
            updateStatus("Event deleted");
        }

        // Escape to deselect
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            controller.deselectAll();
        }

        // Handle event selection with left click
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            Vector3 worldPos = timelineView.screenToWorld(Gdx.input.getX(), Gdx.input.getY());
            
            EventActor clickedActor = null;
            for (EventActor actor : timelineView.getEventActors()) {
                float dx = worldPos.x - (actor.getX() + EventActor.getEventSize() / 2);
                float dy = worldPos.y - (actor.getY() + EventActor.getEventSize() / 2);
                float distSq = dx * dx + dy * dy;
                
                if (distSq <= EventActor.getEventSize() * EventActor.getEventSize()) {
                    clickedActor = actor;
                    break;
                }
            }
            
            if (clickedActor != null) {
                controller.selectEventActor(clickedActor);
                updateStatus("Selected " + clickedActor.getEvent().type + " event");
            } else {
                controller.deselectAll();
            }
        }

        // Update event positions when mouse is released after dragging
        if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            controller.updateEventPositions();
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        stage.dispose();
        timelineView.dispose();
        skin.dispose();
    }
}
