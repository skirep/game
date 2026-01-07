package com.leveleditor.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.leveleditor.model.Formation;
import com.leveleditor.model.FormationData;

/**
 * Dialog for managing formations (Create, Edit, Delete).
 */
public class FormationManagerDialog extends Dialog {
    private FormationData formationData;
    private FormationSerializer serializer;
    private List<String> formationList;
    private Array<String> formationIds;
    private TextField idField;
    private TextField speedField;
    private TextArea positionsArea;
    private Formation currentFormation;

    public FormationManagerDialog(String title, Skin skin, FormationData formationData, FormationSerializer serializer) {
        super(title, skin);
        this.formationData = formationData;
        this.serializer = serializer;
        this.formationIds = new Array<>();
        
        createUI();
        refreshFormationList();
    }

    private void createUI() {
        Table content = getContentTable();
        content.pad(10);

        // Left side - Formation list
        Table leftPanel = new Table();
        leftPanel.defaults().pad(5);
        
        Label listLabel = new Label("Formations:", getSkin());
        leftPanel.add(listLabel).top().left().row();

        formationList = new List<>(getSkin());
        ScrollPane scrollPane = new ScrollPane(formationList, getSkin());
        scrollPane.setScrollingDisabled(true, false);
        leftPanel.add(scrollPane).width(150).height(300).row();

        // Buttons for list operations
        Table listButtons = new Table();
        listButtons.defaults().pad(2);

        TextButton newButton = new TextButton("New", getSkin());
        newButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                createNewFormation();
            }
        });
        listButtons.add(newButton);

        TextButton deleteButton = new TextButton("Delete", getSkin());
        deleteButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                deleteSelectedFormation();
            }
        });
        listButtons.add(deleteButton);

        leftPanel.add(listButtons).row();

        // Right side - Formation editor
        Table rightPanel = new Table();
        rightPanel.defaults().pad(5).left();

        Label editorLabel = new Label("Formation Editor:", getSkin());
        rightPanel.add(editorLabel).colspan(2).row();

        rightPanel.add(new Label("ID:", getSkin()));
        idField = new TextField("", getSkin());
        idField.setWidth(200);
        rightPanel.add(idField).width(200).row();

        rightPanel.add(new Label("Speed:", getSkin()));
        speedField = new TextField("1.0", getSkin());
        speedField.setWidth(200);
        rightPanel.add(speedField).width(200).row();

        rightPanel.add(new Label("Positions (x,y per line):", getSkin())).colspan(2).top().left().row();
        positionsArea = new TextArea("0.0,0.0\n0.1,0.1\n-0.1,0.1", getSkin());
        ScrollPane positionsScroll = new ScrollPane(positionsArea, getSkin());
        rightPanel.add(positionsScroll).colspan(2).width(200).height(200).row();

        TextButton saveButton = new TextButton("Save Formation", getSkin());
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                saveCurrentFormation();
            }
        });
        rightPanel.add(saveButton).colspan(2).center().row();

        // Add panels to content
        content.add(leftPanel).top();
        content.add(rightPanel).top();

        // Dialog buttons
        button("Close", true);

        // Selection listener
        formationList.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (formationList.getSelected() != null) {
                    loadSelectedFormation();
                }
            }
        });
    }

    private void refreshFormationList() {
        formationIds.clear();
        for (Formation formation : formationData.formations) {
            formationIds.add(formation.id);
        }
        formationList.setItems(formationIds);
    }

    private void createNewFormation() {
        currentFormation = new Formation("new_formation", 1.0f);
        currentFormation.addPosition(0f, 0f);
        currentFormation.addPosition(0.1f, 0.1f);
        currentFormation.addPosition(-0.1f, 0.1f);
        displayFormation(currentFormation);
    }

    private void loadSelectedFormation() {
        String selectedId = formationList.getSelected();
        if (selectedId != null) {
            currentFormation = formationData.getFormationById(selectedId);
            if (currentFormation != null) {
                displayFormation(currentFormation);
            }
        }
    }

    private void displayFormation(Formation formation) {
        idField.setText(formation.id);
        speedField.setText(String.valueOf(formation.speed));

        StringBuilder sb = new StringBuilder();
        for (Vector2 pos : formation.relativePositions) {
            sb.append(pos.x).append(",").append(pos.y).append("\n");
        }
        positionsArea.setText(sb.toString().trim());
    }

    private void saveCurrentFormation() {
        try {
            String id = idField.getText().trim();
            if (id.isEmpty()) {
                showError("ID cannot be empty");
                return;
            }

            float speed = Float.parseFloat(speedField.getText().trim());

            // Parse positions
            Array<Vector2> positions = new Array<>();
            String[] lines = positionsArea.getText().split("\n");
            for (String line : lines) {
                line = line.trim();
                if (!line.isEmpty()) {
                    String[] parts = line.split(",");
                    if (parts.length == 2) {
                        float x = Float.parseFloat(parts[0].trim());
                        float y = Float.parseFloat(parts[1].trim());
                        positions.add(new Vector2(x, y));
                    }
                }
            }

            if (positions.size == 0) {
                showError("At least one position required");
                return;
            }

            // Check if ID exists (and it's not the current formation being edited)
            Formation existing = formationData.getFormationById(id);
            if (existing != null && existing != currentFormation) {
                showError("Formation ID already exists");
                return;
            }

            // Remove old formation if ID changed
            if (currentFormation != null && !currentFormation.id.equals(id)) {
                formationData.removeFormation(currentFormation);
            }

            // Create or update formation
            Formation formation = new Formation(id, speed);
            formation.relativePositions = positions;
            
            if (currentFormation == null || !formationData.formations.contains(formation, true)) {
                formationData.addFormation(formation);
            } else {
                // Update in place
                int index = formationData.formations.indexOf(currentFormation, true);
                if (index >= 0) {
                    formationData.formations.set(index, formation);
                }
            }

            currentFormation = formation;

            // Save to file
            FileHandle file = Gdx.files.local("formations.json");
            file.parent().mkdirs();
            serializer.save(formationData, file);

            refreshFormationList();
            formationList.setSelected(id);

        } catch (NumberFormatException e) {
            showError("Invalid number format");
        } catch (Exception e) {
            showError("Error saving formation: " + e.getMessage());
        }
    }

    private void deleteSelectedFormation() {
        String selectedId = formationList.getSelected();
        if (selectedId != null) {
            Formation formation = formationData.getFormationById(selectedId);
            if (formation != null) {
                formationData.removeFormation(formation);

                // Save to file
                FileHandle file = Gdx.files.local("formations.json");
                file.parent().mkdirs();
                serializer.save(formationData, file);

                refreshFormationList();
                clearEditor();
            }
        }
    }

    private void clearEditor() {
        currentFormation = null;
        idField.setText("");
        speedField.setText("1.0");
        positionsArea.setText("0.0,0.0\n0.1,0.1\n-0.1,0.1");
    }

    private void showError(String message) {
        Dialog errorDialog = new Dialog("Error", getSkin());
        errorDialog.text(message);
        errorDialog.button("OK", true);
        errorDialog.show(getStage());
    }
}
