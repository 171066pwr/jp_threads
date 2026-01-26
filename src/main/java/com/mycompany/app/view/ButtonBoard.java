package com.mycompany.app.view;

import com.mycompany.app.model.map.GameEvent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;

import java.awt.*;

public class ButtonBoard extends GameBoard {
    private final GridPane grid = new GridPane();
    private final GraphicButton[][] buttons;
    private final GraphicProvider graphicProvider;

    public ButtonBoard(int width, int height, GraphicProvider graphicProvider) {
        super(width, height);
        this.graphicProvider = graphicProvider;
        buttons = new GraphicButton[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                buttons[i][j] = new GraphicButton("", 0, null);
                grid.add(buttons[i][j], i, j);
            }
        }
    }

    public Scene createScene() {
        return new Scene(grid, area.width*30, area.height*30);
    }

    public void repaintAll() {
        for (int i = 0; i < area.width; i++) {
            for (int j = 0; j < area.height; j++) {
                buttons[i][j].repaint();
            }
        }
    }

    public void repaint(Point p) {
        buttons[p.x][p.y].repaint();
    }

    private GraphicButton getButton(Point p) {
        return buttons[p.x][p.y];
    }

    @Override
    public void acceptEvent(GameEvent event) {
        GraphicButton button = getButton(event.point());
        if (event.eventType().change) {
            button.setFrames(graphicProvider.getFrames(event.object().getType()));
        }
        repaint(event.point());
    }
}
