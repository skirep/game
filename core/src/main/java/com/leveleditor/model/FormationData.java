package com.leveleditor.model;

import com.badlogic.gdx.utils.Array;

/**
 * Container for all formation definitions.
 * This is saved as a separate formations.json file.
 */
public class FormationData {
    public Array<Formation> formations;    // All available formations

    /**
     * Default constructor for JSON deserialization.
     */
    public FormationData() {
        this.formations = new Array<>();
    }

    /**
     * Adds a formation to the collection.
     * @param formation Formation to add
     */
    public void addFormation(Formation formation) {
        formations.add(formation);
    }

    /**
     * Removes a formation from the collection.
     * @param formation Formation to remove
     */
    public void removeFormation(Formation formation) {
        formations.removeValue(formation, true);
    }

    /**
     * Finds a formation by ID.
     * @param id Formation ID to search for
     * @return The formation if found, null otherwise
     */
    public Formation getFormationById(String id) {
        if (id == null) {
            return null;
        }
        for (Formation formation : formations) {
            if (formation.id != null && formation.id.equals(id)) {
                return formation;
            }
        }
        return null;
    }

    /**
     * Checks if a formation ID already exists.
     * @param id Formation ID to check
     * @return true if the ID exists, false otherwise
     */
    public boolean hasFormationId(String id) {
        return getFormationById(id) != null;
    }
}
