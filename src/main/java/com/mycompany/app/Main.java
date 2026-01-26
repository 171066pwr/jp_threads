package com.mycompany.app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main extends Application {
    List<GraphicButton> buttons = new ArrayList<>();
    @Override
    public void start(Stage primaryStage) throws Exception {
        GridPane grid = new GridPane();
        List<Background> frames = loadFrames();

        int number = 0;
        for(int i = 0; i < 20; i++) {
            for(int j = 0; j < 20; j++, number++) {
                GraphicButton button = new GraphicButton("" + number, j%8, frames);
                button.setPrefHeight(Integer.MAX_VALUE);
                button.setPrefWidth(Integer.MAX_VALUE);
                button.setBorder(new Border(new BorderStroke(Color.BLACK,
                        BorderStrokeStyle.DOTTED, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                grid.add(button, j, i);
                buttons.add(button);
            }
        }
        Scene scene = new Scene(grid, 900, 900);
        primaryStage.setScene(scene);
        primaryStage.show();
        PerfTest runner = new PerfTest(buttons);
        new Thread(runner).start();
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
            while(true) {
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
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    System.out.println("INTERRUPTED!");
                    throw new RuntimeException(e);
                }
                iterations++;
            }
        }

    }
}