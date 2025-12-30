package com.leveleditor.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.leveleditor.model.EventType;
import com.leveleditor.model.Formation;
import com.leveleditor.model.FormationData;
import com.leveleditor.model.LevelEvent;

import com.badlogic.gdx.utils.Array;

/**
 * The main timeline view that displays the level events on a scrollable canvas.
 * Y axis represents time (seconds), X axis represents horizontal position (0-1 normalized).
 */
public class TimelineView {
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    
    // View dimensions
    private static final float VIEWPORT_WIDTH = 800f;
    private static final float VIEWPORT_HEIGHT = 600f;
    
    // Grid settings
    private static final float PIXELS_PER_SECOND = 100f; // Vertical scaling
    private static final float TIME_GRID_STEP = 1f; // Grid line every 1 second
    private static final float X_GRID_STEP = 0.1f; // Grid line every 0.1 normalized units
    
    // Zoom and pan
    private float zoom = 1f;
    private static final float MIN_ZOOM = 0.5f;
    private static final float MAX_ZOOM = 3f;
    
    private Vector2 lastDragPos = new Vector2();
    private boolean isPanning = false;

    private Array<EventActor> eventActors;
    
    // Preview mode state
    private boolean previewMode;
    
    // Formation data for rendering formation previews
    private FormationData formationData;

    public TimelineView() {
        camera = new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        camera.position.set(VIEWPORT_WIDTH / 2, VIEWPORT_HEIGHT / 2, 0);
        camera.update();
        
        shapeRenderer = new ShapeRenderer();
        eventActors = new Array<>();
        previewMode = false;
        formationData = null;
    }

    /**
     * Handles input for panning and zooming.
     */
    public void handleInput() {
        // Disable manual controls in preview mode
        if (previewMode) {
            return;
        }
        
        // Zoom with mouse wheel
        int scrollAmount = Gdx.input.getDeltaY();
        if (scrollAmount != 0) {
            zoom += scrollAmount * 0.1f;
            zoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, zoom));
            camera.zoom = zoom;
            camera.update();
        }

        // Pan with right mouse button or middle mouse button
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT) || 
            Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) {
            
            if (!isPanning) {
                lastDragPos.set(Gdx.input.getX(), Gdx.input.getY());
                isPanning = true;
            } else {
                float deltaX = Gdx.input.getX() - lastDragPos.x;
                float deltaY = Gdx.input.getY() - lastDragPos.y;
                
                camera.translate(-deltaX * zoom, deltaY * zoom);
                camera.update();
                
                lastDragPos.set(Gdx.input.getX(), Gdx.input.getY());
            }
        } else {
            isPanning = false;
        }
    }

    /**
     * Renders the timeline grid.
     */
    public void render() {
        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);

        // Draw grid
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.DARK_GRAY);

        // Vertical grid lines (X position)
        for (float x = 0; x <= 1.0f; x += X_GRID_STEP) {
            float screenX = x * VIEWPORT_WIDTH;
            shapeRenderer.line(screenX, 0, screenX, 10000); // Draw long lines
        }

        // Horizontal grid lines (time)
        float viewBottom = camera.position.y - VIEWPORT_HEIGHT / 2 * zoom;
        float viewTop = camera.position.y + VIEWPORT_HEIGHT / 2 * zoom;
        
        int startTime = (int) (viewBottom / PIXELS_PER_SECOND);
        int endTime = (int) (viewTop / PIXELS_PER_SECOND) + 1;
        
        for (int t = startTime; t <= endTime; t++) {
            float screenY = t * PIXELS_PER_SECOND;
            shapeRenderer.line(0, screenY, VIEWPORT_WIDTH, screenY);
        }

        shapeRenderer.end();

        // Draw events
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (EventActor actor : eventActors) {
            actor.draw(shapeRenderer, 1f);
        }
        shapeRenderer.end();
        
        // Draw formation previews for FORMATION events
        if (formationData != null) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.LIGHT_GRAY);
            for (EventActor actor : eventActors) {
                if (actor.getEvent().type == EventType.FORMATION && 
                    actor.getEvent().formationId != null && 
                    !actor.getEvent().formationId.equals("none")) {
                    Formation formation = formationData.getFormationById(actor.getEvent().formationId);
                    if (formation != null) {
                        drawFormationPreview(actor.getEvent(), formation);
                    }
                }
            }
            shapeRenderer.end();
        }
    }

    /**
     * Converts time (seconds) to screen Y coordinate.
     */
    public float timeToY(float time) {
        return time * PIXELS_PER_SECOND;
    }

    /**
     * Converts screen Y coordinate to time (seconds).
     */
    public float yToTime(float y) {
        return y / PIXELS_PER_SECOND;
    }

    /**
     * Converts normalized X position (0-1) to screen X coordinate.
     */
    public float normalizedXToScreen(float normalizedX) {
        return normalizedX * VIEWPORT_WIDTH;
    }

    /**
     * Converts screen X coordinate to normalized position (0-1).
     */
    public float screenXToNormalized(float screenX) {
        return Math.max(0, Math.min(1.0f, screenX / VIEWPORT_WIDTH));
    }

    /**
     * Converts screen coordinates to world coordinates.
     */
    public Vector3 screenToWorld(float screenX, float screenY) {
        return camera.unproject(new Vector3(screenX, screenY, 0));
    }

    /**
     * Adds an event actor to the timeline.
     */
    public void addEventActor(EventActor actor) {
        eventActors.add(actor);
    }

    /**
     * Removes an event actor from the timeline.
     */
    public void removeEventActor(EventActor actor) {
        eventActors.removeValue(actor, true);
    }

    /**
     * Gets all event actors.
     */
    public Array<EventActor> getEventActors() {
        return eventActors;
    }

    /**
     * Updates positions of all event actors based on their associated events.
     */
    public void updateEventActorPositions() {
        for (EventActor actor : eventActors) {
            LevelEvent event = actor.getEvent();
            float screenX = normalizedXToScreen(event.x) - EventActor.getEventSize() / 2;
            float screenY = timeToY(event.time) - EventActor.getEventSize() / 2;
            actor.setPosition(screenX, screenY);
        }
    }

    /**
     * Updates events based on actor positions.
     */
    public void updateEventsFromActors() {
        for (EventActor actor : eventActors) {
            LevelEvent event = actor.getEvent();
            event.x = screenXToNormalized(actor.getX() + EventActor.getEventSize() / 2);
            event.time = Math.max(0, yToTime(actor.getY() + EventActor.getEventSize() / 2));
        }
    }

    public OrthographicCamera getCamera() {
        return camera;
    }
    
    /**
     * Scrolls the camera to center on the given time.
     */
    public void scrollToTime(float time) {
        float targetY = timeToY(time);
        camera.position.y = targetY;
        camera.update();
    }
    
    /**
     * Resets the camera to the start position (time 0).
     */
    public void resetCameraToStart() {
        camera.position.set(VIEWPORT_WIDTH / 2, VIEWPORT_HEIGHT / 2, 0);
        camera.update();
    }
    
    /**
     * Sets whether preview mode is active.
     */
    public void setPreviewMode(boolean previewMode) {
        this.previewMode = previewMode;
    }
    
    /**
     * Sets the formation data for rendering formation previews.
     */
    public void setFormationData(FormationData formationData) {
        this.formationData = formationData;
    }
    
    /**
     * Draws a visual preview of a formation on the timeline.
     */
    private void drawFormationPreview(LevelEvent event, Formation formation) {
        float baseX = normalizedXToScreen(event.x);
        float baseY = timeToY(event.time);
        float previewScale = 30f; // Scale factor for preview visualization
        
        // Draw small circles for each enemy in the formation
        for (Vector2 relPos : formation.relativePositions) {
            float enemyX = baseX + relPos.x * previewScale;
            float enemyY = baseY + relPos.y * previewScale;
            shapeRenderer.circle(enemyX, enemyY, 5f);
        }
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
