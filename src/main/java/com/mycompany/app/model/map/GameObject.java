package com.mycompany.app.model.map;

import com.mycompany.app.model.concurrency.PausableRunnable;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public abstract class GameObject implements PausableRunnable {
    private static final AtomicLong counter = new AtomicLong(0);
    private static final AtomicInteger tick = new AtomicInteger(500);
    @Getter
    private final long id;
    private final Area area;
    @Getter
    private final ObjectType type;
    protected Orientation orientation;
    @Setter
    protected Point coordinates;
    protected int speed = 10;

    public static void updateTick(int miliseconds) {
        tick.set(miliseconds);
    }

    public GameObject(Area area, ObjectType type) {
        this.area = area;
        this.type = type;
        this.id = counter.incrementAndGet();
        orientation = Orientation.NORTH;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            checkPaused();
            area.notifyListeners(act());
            waitTick(tick.get()/speed);
        }
    }

    protected abstract GameEvent act();

    public abstract boolean remove();

    public abstract boolean create(int x, int y, GameObject object);

    public abstract boolean move(int x, int y);

    public abstract boolean push(int x, int y, int vectorX, int vectorY);

    private void waitTick(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
