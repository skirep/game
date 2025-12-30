package com.leveleditor.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Represents a reusable enemy formation pattern.
 * A formation defines relative positions of enemies and movement speed.
 */
public class Formation {
    public String id;                           // Unique identifier (e.g., "V", "line", "circle")
    public Array<Vector2> relativePositions;    // Relative enemy positions (normalized coordinates)
    public float speed;                         // Movement speed

    /**
     * Default constructor for JSON deserialization.
     */
    public Formation() {
        this.id = "";
        this.relativePositions = new Array<>();
        this.speed = 1.0f;
    }

    /**
     * Creates a new formation with specified parameters.
     * @param id Formation identifier
     * @param speed Movement speed
     */
    public Formation(String id, float speed) {
        this.id = id;
        this.relativePositions = new Array<>();
        this.speed = speed;
    }

    /**
     * Creates a copy of this formation.
     */
    public Formation copy() {
        Formation copy = new Formation();
        copy.id = this.id;
        copy.speed = this.speed;
        copy.relativePositions = new Array<>();
        for (Vector2 pos : this.relativePositions) {
            copy.relativePositions.add(new Vector2(pos));
        }
        return copy;
    }

    /**
     * Adds a relative position to the formation.
     * @param x Relative X position (normalized)
     * @param y Relative Y position (normalized)
     */
    public void addPosition(float x, float y) {
        relativePositions.add(new Vector2(x, y));
    }
}
