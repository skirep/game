package com.leveleditor.model;

/**
 * Represents a single event in the level timeline.
 * Each event has a time (in seconds), position, type, and optional metadata.
 */
public class LevelEvent {
    public float time;           // Time in seconds when the event occurs
    public EventType type;       // Type of event
    public float x;              // Horizontal position (0.0 - 1.0 normalized)
    public String enemyType;     // Type of enemy (e.g., "basic", "fast", "tank")
    public String formationId;   // ID of formation pattern (e.g., "V", "line", "circle")

    /**
     * Default constructor for JSON deserialization.
     */
    public LevelEvent() {
    }

    /**
     * Creates a new level event.
     * @param time Time in seconds
     * @param type Event type
     * @param x Horizontal position (0.0 - 1.0)
     */
    public LevelEvent(float time, EventType type, float x) {
        this.time = time;
        this.type = type;
        this.x = x;
        this.enemyType = "basic";
        this.formationId = "none";
    }

    /**
     * Creates a copy of this event.
     */
    public LevelEvent copy() {
        LevelEvent copy = new LevelEvent();
        copy.time = this.time;
        copy.type = this.type;
        copy.x = this.x;
        copy.enemyType = this.enemyType;
        copy.formationId = this.formationId;
        return copy;
    }
}
