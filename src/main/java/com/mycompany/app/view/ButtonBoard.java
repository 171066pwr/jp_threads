package com.mycompany.app.view;

import com.mycompany.app.model.concurrency.PausableRunnable;
import com.mycompany.app.model.map.GameEvent;
import com.mycompany.app.model.map.GameObject;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Log4j2
public class ButtonBoard extends GameBoard {
    private final GridPane grid = new GridPane();
    private final GraphicButton[][] buttons;
    private final GraphicProvider graphicProvider;
    private final Label queueLabel;
    private final Label speedLabel;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public ButtonBoard(int width, int height, GraphicProvider graphicProvider) {
        super(width, height);
        this.graphicProvider = graphicProvider;
        buttons = new GraphicButton[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                buttons[i][j] = new GraphicButton("S", 0, null);
                grid.add(buttons[i][j], i, j);
            }
        }
        speedLabel = new Label();
        queueLabel = new Label();
        grid.add(speedLabel, 13, height, 5, 1);
        grid.add(queueLabel, 18, height, 5, 1);
        grid.add(createPauseButton(), 0, height, 3, 1);
        grid.add(createSpeedSlider(), 3, height, 10, 1);

        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                updateQueueCounter();
            }
        }, 1, 1, TimeUnit.SECONDS);
        grid.setPrefSize(100, 100);
    }

    @Override
    protected void processEvent(GameEvent event) {
        log.info("Processing event: " + event);
        GraphicButton button = getButton(event.point());
        if (event.eventType().change) {
            button.setFrames(graphicProvider.getFrames(event.object().getType()));
        }
        button.incrementState();
        repaint(button);
    }

    public Scene createScene() {
        return new Scene(grid);
    }

    public void repaintAll() {
        for (int i = 0; i < area.width; i++) {
            for (int j = 0; j < area.height; j++) {
                repaint(i, j);
            }
        }
    }

    public void repaint(Point p) {
        repaint(p.x, p.y);
    }

    public void repaint(int x, int y) {
        Platform.runLater(new Painter(buttons[x][y]));
    }

    public void repaint(GraphicButton button) {
        Platform.runLater(new Painter(button));
    }

    private GraphicButton getButton(Point p) {
        return buttons[p.x][p.y];
    }

    private void updateQueueCounter() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                queueLabel.setText(String.format("Event backlog: %d", events.size()));
            }
        });
    }

    private Button createPauseButton() {
        Button pauseButton = new Button("Pause");
        pauseButton.setOnAction(
                (event) -> {
                    PausableRunnable.flipPaused();
                }
        );
        return pauseButton;
    }

    private Slider createSpeedSlider() {
        Slider slider = new Slider(100, 10000, 100);
        slider.setMajorTickUnit(10);
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            GameObject.updateTick(newValue.intValue());
            setSpeedLabelText(newValue.intValue());
        });
        slider.setValue(1000);
        return slider;
    }

    private void setSpeedLabelText(int speed) {
        speedLabel.setText(String.format("Base tick: %dms", speed));
    }

    @RequiredArgsConstructor
    private static class Painter implements Runnable {
        final GraphicButton button;

        @Override
        public void run() {
            button.repaint();
        }
    }
}
