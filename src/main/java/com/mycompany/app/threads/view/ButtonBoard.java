package com.mycompany.app.threads.view;

import com.mycompany.app.threads.model.concurrency.PausableRunnable;
import com.mycompany.app.threads.model.concurrency.RegulatedRunnable;
import com.mycompany.app.threads.model.map.GameEvent;
import com.mycompany.app.threads.model.map.Tile;
import com.mycompany.app.threads.model.map.TileInfo;
import com.mycompany.app.threads.model.objects.Creator;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.stage.Popup;
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
    private final Label periodLabel;
    private final PopupPane popupPane;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public ButtonBoard(int width, int height, GraphicProvider graphicProvider, Creator.CreatorOptions creatorOptions) {
        super(width, height, creatorOptions);
        this.graphicProvider = graphicProvider;
        buttons = new GraphicButton[width][height];
        speedLabel = new Label();
        queueLabel = new Label();
        periodLabel = new Label();
        popupPane = new PopupPane();
        initComponents(width, height);
    }

    public ButtonBoard(int width, int height, GraphicProvider graphicProvider) {
        super(width, height);
        this.graphicProvider = graphicProvider;
        buttons = new GraphicButton[width][height];
        speedLabel = new Label();
        queueLabel = new Label();
        periodLabel = new Label();
        popupPane = new PopupPane();
        initComponents(width, height);
    }

    private void initComponents(int width, int height) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                buttons[i][j] = new GraphicButton("S", 0, new Point(i, j), null);
                grid.add(buttons[i][j], i, j);
            }
        }
        GridPane menu = new GridPane();
        menu.add(createPauseButton(), 0, height, 5, 2);
        menu.add(createSpeedSlider(), 5, height, 10, 1);
        menu.add(createPeriodSlider(), 15, height, 10, 1);
        menu.add(speedLabel, 5, height+1, 5, 1);
        menu.add(periodLabel, 15, height+1, 5, 1);
        menu.add(queueLabel, 25, height+1, 5, 1);
        menu.setHgap(10);
        grid.add(menu, 0, height, width, 1);
        initializeTooltip();

        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                updateQueueCounter();
            }
        }, 1, 1, TimeUnit.SECONDS);
        grid.setPrefSize(width*40, height*40);
    }

    @Override
    protected void processEvent(GameEvent event) {
        log.info("Processing event {}#{}: {} [{};{}] -> [{};{}]", event.object().getType(), event.object().getId(), event.eventType(), event.origin().x, event.origin().y, event.target().x, event.target().y);
        GraphicButton origin = getButton(event.origin());
        GraphicButton target = getButton(event.target());
        if (event.eventType().change) {
            if(event.eventType() == GameEvent.EventType.DESTRUCTION) {
                target.reset();
                repaint(target);
                return;
            }
            if(event.object().getType().isUnit()) {
                target.setLabel(String.valueOf(event.object().getId()));
            }
            target.setFrames(graphicProvider.getFrames(event.object().getType()));
            if (origin != target) {
                origin.reset();
                repaint(origin);
            }
        }
        target.setState(event.object().getState());
        repaint(target);
    }

    public Scene createScene() {
        Scene scene = new Scene(grid);
        scene.setOnKeyPressed(e ->{
            if (e.getCode() == KeyCode.P) {
                    PausableRunnable.flipPaused();
                }
            });
        return scene;
    }

    public void repaintAll() {
        for (int i = 0; i < area.width; i++) {
            for (int j = 0; j < area.height; j++) {
                repaint(i, j);
            }
        }
    }

    public void repaint(int x, int y) {
        Platform.runLater(new Painter(buttons[x][y]));
    }

    public void repaint(GraphicButton button) {
        Platform.runLater(new Painter(button));
    }

    private void initializeTooltip() {
        Popup popup = new Popup();
        popup.getContent().add(popupPane);

        for(GraphicButton[] arr: buttons) {
            for(GraphicButton button: arr) {
                button.hoverProperty().addListener((observable, oldValue, newValue) -> {
                    if(newValue) {
                        TileInfo info = area.getTileInfo(button.coordinates);
                        if(info.isOccupied()) {
                            popupPane.displayTileInfo(info);
                            Bounds bnds = button.localToScreen(button.getLayoutBounds());
                            double x = bnds.getMinX() - (popupPane.getWidth() / 2) + (button.getWidth() / 2);
                            double y = bnds.getMinY();
                            popup.show(button, x, y);
                        }
                    } else {
                        popup.hide();
                    }
                });
            }

        }
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
        Button pauseButton = new Button("Pause (P)");
        pauseButton.setOnAction(
                (event) -> {
                    PausableRunnable.flipPaused();
                }
        );
        return pauseButton;
    }

    private Slider createSpeedSlider() {
        Slider slider = new Slider(100, 10000, 1000);
        slider.setMajorTickUnit(10);
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            RegulatedRunnable.setUpdateTick(newValue.intValue());
            setSpeedLabelText(newValue.intValue());
        });
        slider.setValue(2000);
        return slider;
    }

    private Slider createPeriodSlider() {
        Slider slider = new Slider(50, 10000, 500);
        slider.setMajorTickUnit(50);
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            creator.setPeriod(newValue.intValue());
            setPeriodLabelText(newValue.intValue());
        });
        slider.setValue(1000);
        return slider;
    }

    private void setSpeedLabelText(int speed) {
        speedLabel.setText(String.format("Base tick: %dms", speed));
    }

    private void setPeriodLabelText(int period) {
        periodLabel.setText(String.format("Creator tick: %dms", period));
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
