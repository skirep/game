package com.leveleditor.editor;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.leveleditor.model.LevelData;

/**
 * Handles serialization and deserialization of level data to/from JSON files.
 */
public class LevelSerializer {
    private final Json json;

    public LevelSerializer() {
        json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
    }

    /**
     * Saves level data to a JSON file.
     * @param levelData The level data to save
     * @param file The file to save to
     */
    public void save(LevelData levelData, FileHandle file) {
        try {
            String jsonString = json.prettyPrint(levelData);
            file.writeString(jsonString, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads level data from a JSON file.
     * @param file The file to load from
     * @return The loaded level data, or a new empty level if loading fails
     */
    public LevelData load(FileHandle file) {
        try {
            if (file.exists()) {
                String jsonString = file.readString();
                return json.fromJson(LevelData.class, jsonString);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new LevelData();
    }
}
