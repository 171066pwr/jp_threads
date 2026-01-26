package com.mycompany.app.model.units;

import com.mycompany.app.model.map.Area;
import com.mycompany.app.model.map.GameObject;
import com.mycompany.app.model.map.ObjectType;
import com.mycompany.app.model.map.Unit;

public class Scout extends GameObject implements Unit {
    public Scout(Area area) {
        super(area, ObjectType.SCOUT);
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

    @Override
    public void run() {

    }
}
