package com.mycompany.app.view;

import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import lombok.Setter;

import java.util.List;

public class GraphicButton extends Button {
    int state;
    @Setter
    String label = "";
    @Setter
    List<Background> frames;

    public GraphicButton(String text, int state, List<Background> frames) {
        super(text);
        this.state = state;
        this.frames = frames;
        initButton();
    }

    public void reset() {
        state = 0;
        label = "";
        frames = null;
    }

    public void setState(int state) {
        if(frames != null && !frames.isEmpty()) {
            this.state = (state) % frames.size();
        }
    }

    public void repaint() {
        setText(label);
        if(frames != null) {
            this.setBackground(frames.get(state));
        } else {
            this.setBackground(null);
        }
    }

    private void initButton() {
        setPrefHeight(Integer.MAX_VALUE);
        setPrefWidth(Integer.MAX_VALUE);
        setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.DOTTED, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
    }
}
