package com.leveleditor.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.leveleditor.model.EventType;
import com.leveleditor.model.LevelData;
import com.leveleditor.model.LevelEvent;

/**
 * Controller that manages the level data and coordinates between the model and view.
 */
public class EditorController {
    private LevelData levelData;
    private LevelSerializer serializer;
    private TimelineView timelineView;
    private EventActor selectedActor;

    public EditorController(TimelineView timelineView) {
        this.timelineView = timelineView;
        this.levelData = new LevelData(120f); // Default 2 minutes
        this.serializer = new LevelSerializer();
        this.selectedActor = null;
    }

    /**
     * Creates a new level event at the specified position and time.
     */
    public void createEvent(EventType type, float time, float x) {
        LevelEvent event = new LevelEvent(time, type, x);
        levelData.addEvent(event);
        
        EventActor actor = new EventActor(event);
        timelineView.addEventActor(actor);
        timelineView.updateEventActorPositions();
    }

    /**
     * Deletes the currently selected event.
     */
    public void deleteSelectedEvent() {
        if (selectedActor != null) {
            levelData.removeEvent(selectedActor.getEvent());
            timelineView.removeEventActor(selectedActor);
            selectedActor = null;
        }
    }

    /**
     * Selects an event actor.
     */
    public void selectEventActor(EventActor actor) {
        if (selectedActor != null) {
            selectedActor.setSelected(false);
        }
        selectedActor = actor;
        if (selectedActor != null) {
            selectedActor.setSelected(true);
        }
    }

    /**
     * Deselects the current selection.
     */
    public void deselectAll() {
        if (selectedActor != null) {
            selectedActor.setSelected(false);
            selectedActor = null;
        }
    }

    /**
     * Updates event positions based on actor movements.
     */
    public void updateEventPositions() {
        timelineView.updateEventsFromActors();
        levelData.sortEvents();
    }

    /**
     * Saves the current level to a file.
     */
    public void saveLevel(String filename) {
        FileHandle file = Gdx.files.local("assets/levels/" + filename);
        serializer.save(levelData, file);
        System.out.println("Level saved to: " + file.path());
    }

    /**
     * Loads a level from a file.
     */
    public void loadLevel(String filename) {
        FileHandle file = Gdx.files.local("assets/levels/" + filename);
        levelData = serializer.load(file);
        
        // Clear existing actors
        timelineView.getEventActors().clear();
        
        // Create actors for loaded events
        for (LevelEvent event : levelData.events) {
            EventActor actor = new EventActor(event);
            timelineView.addEventActor(actor);
        }
        
        timelineView.updateEventActorPositions();
        System.out.println("Level loaded from: " + file.path());
    }

    /**
     * Creates a new empty level.
     */
    public void newLevel() {
        levelData = new LevelData(120f);
        timelineView.getEventActors().clear();
        deselectAll();
    }

    public LevelData getLevelData() {
        return levelData;
    }

    public EventActor getSelectedActor() {
        return selectedActor;
    }
}
