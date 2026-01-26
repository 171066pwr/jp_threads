package com.mycompany.app.model.map;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Area {
    public final int width;
    public final int height;
    private final Tile[][] tiles;
    private final List<GameObject> objects = new ArrayList<>();
    private final List<GameEventListener> listeners = new ArrayList<>();;

    public Area(int width, int height) {
        this.width = width;
        this.height = height;
        tiles = new Tile[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = new Tile(x, y);
            }
        }
    }

    public synchronized boolean create(Point point, ObjectType type) {
        Tile tile = tiles[point.x][point.y];
        if (tile.isOccupied()) {
            return false;
        } else {
            GameObject object = type.create(this);
            objects.add(object);
            tile.add(object);
            notifyListeners(new GameEvent(point, object, GameEvent.EventType.CREATION));
            return true;
        }
    }

    public synchronized Tile probe(Point point) {
        return tiles[point.x][point.y];
    }

    public synchronized long getObjectCount() {
        return objects.size();
    }

    public synchronized long getUnitCount() {
        return objects.stream()
                .filter(o -> o instanceof Unit)
                .count();
    }

    public synchronized Map<Class, Long> countObjects() {
        return objects.stream().collect(Collectors.groupingBy(GameObject::getClass, Collectors.counting()));
    }

    public void addListener(GameEventListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners(GameEvent event) {
        for (GameEventListener listener : listeners) {
            listener.acceptEvent(event);
        }
    }
}
