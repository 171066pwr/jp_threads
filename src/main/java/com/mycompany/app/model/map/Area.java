package com.mycompany.app.model.map;

import com.mycompany.app.model.objects.GameObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Area {
    private static final Logger log = LogManager.getLogger(Area.class);
    public final int width;
    public final int height;
    private final Tile[][] tiles;
    private final List<GameObject> objects = new ArrayList<>();
    private final List<GameObject> removed = new ArrayList<>();
    private final List<GameEventListener> listeners = new ArrayList<>();

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

    public synchronized Optional<Tile> probe(Point point) {
        if(point.x < width && point.y < height) {
            return Optional.of(tiles[point.x][point.y]);
        }
        return Optional.empty();
    }

    /**
     * @param start - origin tile of probing
     * @param direction - azimuth to probe the tiles in straight line
     * @param depth - maximum tiles to probe
     * @return probed tiles
     */
    public synchronized List<Tile> probe(Point start, Point direction, int depth) {
        List<Tile> result = new ArrayList<>();
        int x = start.x;
        int y = start.y;
        for(int i = 0; i < depth; i++) {
            x += direction.x;
            y += direction.y;
            if(x >= 0  && x < width && y >= 0 && y < height) {
                result.add(tiles[x][y]);
            }
        }
        return result;
    }

    public synchronized void registerObject(GameObject object) {
        objects.add(object);
    }

    public synchronized void unregisterObject(GameObject object) {
        if(objects.remove(object)) {
            removed.add(object);
        }
    }

    public synchronized long getObjectCount() {
        return objects.size();
    }

    public synchronized long getUnitCount() {
        return objects.stream()
                .map(GameObject::getType)
                .filter(ObjectType::isUnit)
                .count();
    }

    public synchronized long getPerkCount() {
        return objects.stream()
                .map(GameObject::getType)
                .filter(ObjectType::isPerk)
                .count();
    }

    public synchronized Map<Class, Long> countObjects() {
        return objects.stream().collect(Collectors.groupingBy(GameObject::getClass, Collectors.counting()));
    }

    public void addListener(GameEventListener listener) {
        listeners.add(listener);
    }

    public void notifyListeners(GameEvent event) {
        for (GameEventListener listener : listeners) {
            listener.acceptEvent(event);
        }
    }
}
