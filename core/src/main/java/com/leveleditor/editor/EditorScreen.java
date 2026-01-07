package com.leveleditor.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.leveleditor.model.EventType;

/**
 * Main editor screen containing the UI and timeline view.
 */
public class EditorScreen implements Screen {
    private Stage stage;
    private TimelineView timelineView;
    private EditorController controller;
    private Skin skin;

    // Dragging state (EventActor is not part of the Stage)
    private EventActor draggingActor;
    private final Vector2 dragOffset = new Vector2();

    // UI elements
    private Label statusLabel;
    private TextField filenameField;
    private TextButton playButton;
    private TextButton stopButton;

    public EditorScreen() {
        stage = new Stage(new ScreenViewport());
        timelineView = new TimelineView();
        controller = new EditorController(timelineView);
        
        // Create a simple skin for UI
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Use a scalable font on HiDPI displays so text doesn't become blocky.
        applyHiDpiSkinFont();
        
        createUI();

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean scrolled(float amountX, float amountY) {
                timelineView.addScroll(amountY);
                return false;
            }
        });
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void applyHiDpiSkinFont() {
        try {
            float density = Gdx.graphics.getDensity();
            // Generate at least 16px; scale up on HiDPI.
            int fontSize = Math.max(16, Math.round(16f * density));

            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("fonts/NotoSans-Regular.ttf")
            );
            FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
            params.size = fontSize;
            params.magFilter = Texture.TextureFilter.Linear;
            params.minFilter = Texture.TextureFilter.Linear;
            params.color = Color.WHITE;

            BitmapFont uiFont = generator.generateFont(params);
            generator.dispose();

            skin.add("ui-font", uiFont, BitmapFont.class);

            // Replace fonts in the common styles.
            if (skin.has("default", Label.LabelStyle.class)) {
                skin.get(Label.LabelStyle.class).font = uiFont;
            }
            if (skin.has("default", TextButton.TextButtonStyle.class)) {
                skin.get(TextButton.TextButtonStyle.class).font = uiFont;
            }
            if (skin.has("default", TextField.TextFieldStyle.class)) {
                skin.get(TextField.TextFieldStyle.class).font = uiFont;
            }
            if (skin.has("default", Window.WindowStyle.class)) {
                skin.get(Window.WindowStyle.class).titleFont = uiFont;
            }
            if (skin.has("default", List.ListStyle.class)) {
                skin.get(List.ListStyle.class).font = uiFont;
            }
            if (skin.has("default", SelectBox.SelectBoxStyle.class)) {
                SelectBox.SelectBoxStyle style = skin.get(SelectBox.SelectBoxStyle.class);
                style.font = uiFont;
                if (style.listStyle != null) {
                    style.listStyle.font = uiFont;
                }
            }
        } catch (Exception e) {
            // If font generation fails, fall back to the bundled bitmap font.
            e.printStackTrace();
        }
    }

    /**
     * Creates the user interface with buttons and controls.
     */
    private void createUI() {
        Table rootTable = new Table();
        rootTable.setFillParent(true);
        // Important: the root table fills the whole screen; avoid it eating clicks.
        rootTable.setTouchable(Touchable.childrenOnly);
        stage.addActor(rootTable);

        // Top toolbar
        Table toolbar = new Table();
        toolbar.defaults().pad(5);
        toolbar.setTouchable(Touchable.childrenOnly);

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

        // Manage Formations button
        TextButton formationsButton = new TextButton("Manage Formations", skin);
        formationsButton.getColor().set(0.3f, 0.6f, 1f, 1f); // Blue
        formationsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                openFormationManager();
            }
        });
        toolbar.add(formationsButton);

        toolbar.add(new Label(" | ", skin));
        
        // Preview mode buttons
        Label previewLabel = new Label("Preview:", skin);
        toolbar.add(previewLabel);
        
        playButton = new TextButton("Play", skin);
        playButton.getColor().set(0f, 0.8f, 0f, 1f); // Green
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.startPreview();
                timelineView.setPreviewMode(true);
                updatePreviewButtons();
                updateStatus("Preview mode started");
            }
        });
        toolbar.add(playButton);
        
        stopButton = new TextButton("Stop", skin);
        stopButton.getColor().set(0.8f, 0f, 0f, 1f); // Red
        stopButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.stopPreview();
                timelineView.setPreviewMode(false);
                updatePreviewButtons();
                updateStatus("Preview mode stopped");
            }
        });
        stopButton.setVisible(false);
        toolbar.add(stopButton);

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
        
        TextButton editPropertiesButton = new TextButton("Edit Properties", skin);
        editPropertiesButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                editSelectedEventProperties();
            }
        });
        toolbar.add(editPropertiesButton);

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
        statusLabel.setTouchable(Touchable.disabled);

        // Layout
        rootTable.add(toolbar).top().expandX().fillX().row();
        // Spacer row so the status label stays at the bottom
        rootTable.add().expand().fill().row();
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
    
    /**
     * Updates the visibility of preview buttons based on preview mode state.
     */
    private void updatePreviewButtons() {
        boolean isPreview = controller.isPreviewMode();
        playButton.setVisible(!isPreview);
        stopButton.setVisible(isPreview);
    }
    
    /**
     * Opens the formation manager dialog.
     */
    private void openFormationManager() {
        FormationManagerDialog dialog = new FormationManagerDialog(
            "Formation Manager", 
            skin, 
            controller.getFormationData(), 
            controller.getFormationSerializer()
        );
        dialog.show(stage);
        updateStatus("Formation manager opened");
    }
    
    /**
     * Opens the event properties dialog for the selected event.
     */
    private void editSelectedEventProperties() {
        EventActor selectedActor = controller.getSelectedActor();
        if (selectedActor != null) {
            EventPropertiesDialog dialog = new EventPropertiesDialog(
                "Edit Event Properties", 
                skin, 
                selectedActor.getEvent(), 
                controller.getFormationData()
            );
            dialog.show(stage);
            updateStatus("Editing event properties");
        } else {
            updateStatus("No event selected");
        }
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        // Update preview mode
        controller.updatePreview(delta);
        
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
        // Disable edit mode input during preview
        if (controller.isPreviewMode()) {
            return;
        }
        
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
            // Ignore clicks on UI elements
            Vector2 stageCoords = stage.screenToStageCoordinates(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
            Actor hit = stage.hit(stageCoords.x, stageCoords.y, true);
            if (hit != null) {
                draggingActor = null;
                return;
            }

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
                draggingActor = clickedActor;
                dragOffset.set(worldPos.x - clickedActor.getX(), worldPos.y - clickedActor.getY());
                updateStatus("Selected " + clickedActor.getEvent().type + " event");
            } else {
                controller.deselectAll();
                draggingActor = null;
            }
        }

        // Drag selected event while holding left mouse button
        if (draggingActor != null && Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            Vector3 worldPos = timelineView.screenToWorld(Gdx.input.getX(), Gdx.input.getY());

            float newX = worldPos.x - dragOffset.x;
            float newY = worldPos.y - dragOffset.y;

            float maxX = timelineView.getViewportWidth() - EventActor.getEventSize();
            newX = Math.max(0f, Math.min(maxX, newX));
            newY = Math.max(0f, newY);

            draggingActor.setPosition(newX, newY);
        }

        // Update event positions when mouse is released after dragging
        if (draggingActor != null && !Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            controller.updateEventPositions();
            draggingActor = null;
        }
    }

    @Override
    public void resize(int width, int height) {
        // Use backbuffer size to avoid implicit DPI scaling blur.
        int backBufferWidth = Gdx.graphics.getBackBufferWidth();
        int backBufferHeight = Gdx.graphics.getBackBufferHeight();
        stage.getViewport().update(backBufferWidth, backBufferHeight, true);
        timelineView.resize(backBufferWidth, backBufferHeight);
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
