package com.mycompany.app;

import com.mycompany.app.view.ButtonBoard;
import com.mycompany.app.view.GraphicButton;
import com.mycompany.app.view.GraphicProvider;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.ImagePattern;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        GraphicProvider graphicProvider = GraphicProvider.getInstance();
        ButtonBoard board = new ButtonBoard(30, 30, graphicProvider);

        Scene scene = board.createScene();
        primaryStage.setScene(scene);
        board.repaintAll();
        primaryStage.show();
        board.start();
    }

    List<Background> loadFrames() {
        List<Background> images = new ArrayList<>();
        try {
            for (int i = 0; i < 8; i++) {
                BackgroundFill backgroundFill = new BackgroundFill(new ImagePattern(new Image(new FileInputStream("src/main/resources/frames/scout_" + i + ".bmp"))), CornerRadii.EMPTY, Insets.EMPTY);
                images.add(new Background(backgroundFill));
            }
        } catch (Exception e){
            System.out.println(e);
        }
        return images;
    }

    public static void main(String[] args) {
        launch(args);
    }

    class PerfTest implements Runnable {
        List<GraphicButton> board;

        PerfTest(List<GraphicButton> buttons) {
            this.board = buttons;
        }

        @Override
        public void run() {
            long iterations = 0;
            while(!Thread.currentThread().isInterrupted()) {
                System.out.println("" + iterations);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        for (GraphicButton button : board) {
                            button.incrementState();
                        }
                    }
                });
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    System.out.println("INTERRUPTED!");
                    throw new RuntimeException(e);
                }
                iterations++;
            }
        }

    }
}