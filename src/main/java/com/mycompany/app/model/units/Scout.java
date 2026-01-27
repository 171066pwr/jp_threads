package com.mycompany.app.model.units;

import com.mycompany.app.model.map.*;

public class Scout extends GameObject implements Unit {
    public Scout(Area area) {
        super(area, ObjectType.SCOUT);
    }

    @Override
    protected GameEvent act() {
        orientation = orientation.turnLeft(1);
        return new GameEvent(coordinates, this, GameEvent.EventType.ROTATION);
    }

    @Override
    public boolean remove() {
        return false;
    }

    @Override
    public boolean create(int x, int y, GameObject object) {
        return false;
    }

    @Override
    public boolean move(int x, int y) {
        return false;
    }

    @Override
    public boolean push(int x, int y, int vectorX, int vectorY) {
        return false;
    }
}
