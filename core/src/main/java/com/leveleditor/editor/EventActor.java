package com.leveleditor.editor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.leveleditor.model.EventType;
import com.leveleditor.model.LevelEvent;

/**
 * Visual representation of a level event on the timeline.
 * This actor can be dragged and selected.
 */
public class EventActor extends Actor {
    private LevelEvent event;
    private boolean selected;
    private Color color;
    private static final float SIZE = 20f;

    // Drag state
    private float dragStartX;
    private float dragStartY;
    private boolean dragging;

    public EventActor(LevelEvent event) {
        this.event = event;
        this.selected = false;
        this.dragging = false;

        // Set color based on event type
        switch (event.type) {
            case ENEMY:
                color = new Color(1f, 0.2f, 0.2f, 1f); // Red
                break;
            case FORMATION:
                color = new Color(1f, 0.5f, 0f, 1f); // Orange
                break;
            case BOSS:
                color = new Color(0.8f, 0f, 0.8f, 1f); // Purple
                break;
            case POWER_UP:
                color = new Color(0f, 0.8f, 0f, 1f); // Green
                break;
            default:
                color = Color.WHITE;
        }

        setSize(SIZE, SIZE);

        // Add input listener for dragging
        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                dragStartX = x;
                dragStartY = y;
                dragging = true;
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                if (dragging) {
                    moveBy(x - dragStartX, y - dragStartY);
                }
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                dragging = false;
            }
        });
    }

    /**
     * Renders the event as a colored circle.
     */
    public void draw(ShapeRenderer shapeRenderer, float parentAlpha) {
        shapeRenderer.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        
        if (selected) {
            // Draw selection outline
            shapeRenderer.set(ShapeRenderer.ShapeType.Line);
            shapeRenderer.circle(getX() + SIZE / 2, getY() + SIZE / 2, SIZE / 2 + 3);
        }
        
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.circle(getX() + SIZE / 2, getY() + SIZE / 2, SIZE / 2);
    }

    public LevelEvent getEvent() {
        return event;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public static float getEventSize() {
        return SIZE;
    }
}
