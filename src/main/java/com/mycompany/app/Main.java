package com.mycompany.app;

import com.mycompany.app.model.objects.Creator;
import com.mycompany.app.view.ButtonBoard;
import com.mycompany.app.view.GraphicProvider;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        GraphicProvider graphicProvider = GraphicProvider.getInstance();
        Creator.CreatorOptions creatorOptions = Creator.CreatorOptions.builder()
                .maxUnits(144)
                .build();
        ButtonBoard board = new ButtonBoard(24, 24, graphicProvider, creatorOptions);

        Scene scene = board.createScene();
        primaryStage.setScene(scene);
        board.repaintAll();
        primaryStage.sizeToScene();
        primaryStage.show();
        board.start();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                Platform.exit();
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}