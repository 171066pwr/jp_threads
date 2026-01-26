package com.mycompany.app.model.map;

import lombok.Getter;

public abstract class GameObject implements Runnable {
    private final Area area;
    @Getter
    private final ObjectType type;
    Orientation orientation;

    public GameObject(Area area, ObjectType type) {
        this.area = area;
        this.type = type;
    }

    public abstract boolean remove();

    public abstract boolean create(int x, int y, GameObject object);

    public abstract boolean move(int x, int y);

    public abstract boolean push(int x, int y, int vectorX, int vectorY);
}
