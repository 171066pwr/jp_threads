package com.mycompany.app.model.map;

import com.mycompany.app.model.objects.GameObject;
import lombok.Getter;

import java.awt.*;

public class Tile {
    public final Point coordinates;
    @Getter
    private GameObject object;

    public Tile(int x, int y) {
        coordinates = new Point(x, y);
    }

    public synchronized boolean add(GameObject object) {
        if(isOccupied()) {
            return false;
        }
        this.object = object;
        return true;
    }

    public synchronized boolean remove(GameObject object) {
        if(this.object == object) {
            this.object = null;
            return true;
        }
        return false;
    }

    public boolean isOccupied() {
        return object != null;
    }

    public ObjectType getObjectType() {
        if(object != null) {
            return object.getType();
        }
        return null;
    }
}
