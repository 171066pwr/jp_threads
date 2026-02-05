package com.mycompany.app.threads.view;

import com.mycompany.app.threads.model.map.TileInfo;
import com.mycompany.app.threads.model.objects.GameObject;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class PopupPane extends GridPane {
    Label typeLabel;
    Label idLabel;
    Label experienceLabel;
    Label speedLabel;

    PopupPane() {
        typeLabel = new Label();
        idLabel = new Label();
        speedLabel = new Label();
        experienceLabel = new Label();
        this.add(new Label("Type: "), 0, 0);
        this.add(typeLabel, 1, 0);
        this.add(new Label("ID: "), 0, 1);
        this.add(idLabel, 1, 1);
        this.add(new Label("Experience: "), 0, 2);
        this.add(experienceLabel, 1, 2);
        this.add(new Label("Speed: "), 0, 3);
        this.add(speedLabel, 1, 3);
        this.setPrefSize(120, 120);
        this.setOpacity(1);
    }

    void displayTileInfo(TileInfo info) {
        typeLabel.setText(info.getObjectType());
        idLabel.setText(String.valueOf(info.getObjectId()));
        experienceLabel.setText(String.valueOf(info.getExperience()));
        speedLabel.setText(String.valueOf(info.getSpeed()));
    }
}
