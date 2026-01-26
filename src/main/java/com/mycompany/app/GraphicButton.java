package com.mycompany.app;

import javafx.scene.control.Button;
import javafx.scene.layout.Background;

import java.util.List;

public class GraphicButton extends Button {
    int state;
    List<Background> frames;

    public GraphicButton(String text, int state, List<Background> frames) {
        super(text);
        this.state = state;
        this.frames = frames;
        repaint();
    }

    public void incrementState() {
        if(frames != null) {
            state = (state + 1)%frames.size();
            repaint();
        }
        setText(state+"");
    }

    public void repaint() {
        if(frames != null) {
            this.setBackground(frames.get(state));
        } else {
            this.setBackground(null);
        }
    }
}
