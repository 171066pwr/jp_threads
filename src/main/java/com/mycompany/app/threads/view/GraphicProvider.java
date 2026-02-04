package com.mycompany.app.threads.view;

import com.mycompany.app.threads.model.map.ObjectType;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.ImagePattern;
import lombok.extern.log4j.Log4j2;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
public class GraphicProvider {
    private static final String RESOURCE_PATH = "frames";
    private static final String ALTERNATE_PATH = "src/main/resources/frames";
    private static GraphicProvider instance;
    private Map<ObjectType, List<Background>> graphics;

    private GraphicProvider() {
        graphics = new HashMap<>();
        loadGraphics();
    }

    public static GraphicProvider getInstance() {
        if (instance == null) {
            instance = new GraphicProvider();
        }
        return instance;
    }

    private void loadGraphics() {
        for(ObjectType type : ObjectType.values()) {
            List<Background> images = new ArrayList<>();
            try {
                for (int i = 0; i < 8; i++) {
                    InputStream is = GraphicProvider.class.getClassLoader().getResourceAsStream(formatPath(RESOURCE_PATH, type, i));
                    is = is == null ? new FileInputStream(formatPath(ALTERNATE_PATH, type, i)) : is;
                    try {
                        BackgroundFill backgroundFill = new BackgroundFill(new ImagePattern(new Image(is)), CornerRadii.EMPTY, Insets.EMPTY);
                        images.add(new Background(backgroundFill));
                        graphics.put(type, images);
                    } finally {
                            is.close();
                    }
                }
            } catch (Exception e) {
                log.error(e);
            }
        }
    }

    List<Background> getFrames(ObjectType type) {
        return graphics.get(type);
    }

    private String formatPath(String path, ObjectType type, int frame) {
        return String.format("%s/%s/%d.bmp", path, type.toString(), frame).toLowerCase();
    }
}
