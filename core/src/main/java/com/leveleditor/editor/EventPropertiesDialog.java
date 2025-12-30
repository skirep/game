package com.leveleditor.editor;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.leveleditor.model.EventType;
import com.leveleditor.model.Formation;
import com.leveleditor.model.FormationData;
import com.leveleditor.model.LevelEvent;
import com.badlogic.gdx.utils.Array;

/**
 * Dialog for editing event properties like enemyType and formationId.
 */
public class EventPropertiesDialog extends Dialog {
    private LevelEvent event;
    private FormationData formationData;
    private TextField enemyTypeField;
    private SelectBox<String> formationSelectBox;
    private Label formationLabel;

    public EventPropertiesDialog(String title, Skin skin, LevelEvent event, FormationData formationData) {
        super(title, skin);
        this.event = event;
        this.formationData = formationData;
        
        createUI();
        loadEventData();
    }

    private void createUI() {
        Table content = getContentTable();
        content.pad(10);
        content.defaults().pad(5).left();

        // Event type (read-only)
        content.add(new Label("Type:", getSkin()));
        Label typeLabel = new Label(event.type.toString(), getSkin());
        content.add(typeLabel).row();

        // Time (read-only)
        content.add(new Label("Time:", getSkin()));
        Label timeLabel = new Label(String.format("%.2f", event.time), getSkin());
        content.add(timeLabel).row();

        // Position (read-only)
        content.add(new Label("X Position:", getSkin()));
        Label posLabel = new Label(String.format("%.2f", event.x), getSkin());
        content.add(posLabel).row();

        // Enemy type
        content.add(new Label("Enemy Type:", getSkin()));
        enemyTypeField = new TextField(event.enemyType != null ? event.enemyType : "basic", getSkin());
        enemyTypeField.setWidth(150);
        content.add(enemyTypeField).width(150).row();

        // Formation ID (only for FORMATION type)
        if (event.type == EventType.FORMATION) {
            formationLabel = new Label("Formation ID:", getSkin());
            content.add(formationLabel);
            
            // Create list of formation IDs
            Array<String> formationIds = new Array<>();
            formationIds.add("none");
            for (Formation formation : formationData.formations) {
                formationIds.add(formation.id);
            }
            
            formationSelectBox = new SelectBox<>(getSkin());
            formationSelectBox.setItems(formationIds);
            formationSelectBox.setSelected(event.formationId != null ? event.formationId : "none");
            content.add(formationSelectBox).width(150).row();
        }

        // Buttons
        button("Save", true);
        button("Cancel", false);
    }

    private void loadEventData() {
        if (event.enemyType != null) {
            enemyTypeField.setText(event.enemyType);
        }
    }

    @Override
    protected void result(Object object) {
        if ((Boolean) object) {
            // Save button clicked
            event.enemyType = enemyTypeField.getText().trim();
            
            if (event.type == EventType.FORMATION && formationSelectBox != null) {
                event.formationId = formationSelectBox.getSelected();
            }
        }
    }
}
