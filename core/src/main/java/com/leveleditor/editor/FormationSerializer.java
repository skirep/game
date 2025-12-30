package com.leveleditor.editor;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.leveleditor.model.FormationData;

/**
 * Handles serialization and deserialization of formation data to/from JSON files.
 */
public class FormationSerializer {
    private final Json json;

    public FormationSerializer() {
        json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
    }

    /**
     * Saves formation data to a JSON file.
     * @param formationData The formation data to save
     * @param file The file to save to
     */
    public void save(FormationData formationData, FileHandle file) {
        try {
            String jsonString = json.prettyPrint(formationData);
            file.writeString(jsonString, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads formation data from a JSON file.
     * @param file The file to load from
     * @return The loaded formation data, or a new empty FormationData if loading fails
     */
    public FormationData load(FileHandle file) {
        try {
            if (file.exists()) {
                String jsonString = file.readString();
                return json.fromJson(FormationData.class, jsonString);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new FormationData();
    }
}
