package com.mycompany.app.model.map;

public class Tile {
    public final int x;
    public final int y;
    GameObject object;

    public Tile(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void add(GameObject object) {
        this.object = object;
    }

    public void remove() {
        this.object = null;
    }

    public boolean isOccupied() {
        return object != null;
    }
}
