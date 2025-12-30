package com.leveleditor.model;

import com.badlogic.gdx.utils.Array;

/**
 * Main data structure for a complete level.
 * Contains the level length and all events that occur during the level.
 */
public class LevelData {
    public float levelLength;           // Total length of the level in seconds
    public Array<LevelEvent> events;    // All events in the level

    /**
     * Default constructor for JSON deserialization.
     */
    public LevelData() {
        this.levelLength = 60.0f;  // Default 60 seconds
        this.events = new Array<>();
    }

    /**
     * Creates a new level with specified length.
     * @param levelLength Length in seconds
     */
    public LevelData(float levelLength) {
        this.levelLength = levelLength;
        this.events = new Array<>();
    }

    /**
     * Adds an event to the level.
     * @param event Event to add
     */
    public void addEvent(LevelEvent event) {
        events.add(event);
        sortEvents();
    }

    /**
     * Removes an event from the level.
     * @param event Event to remove
     */
    public void removeEvent(LevelEvent event) {
        events.removeValue(event, true);
    }

    /**
     * Sorts events by time (ascending order).
     */
    public void sortEvents() {
        events.sort((a, b) -> Float.compare(a.time, b.time));
    }
}
