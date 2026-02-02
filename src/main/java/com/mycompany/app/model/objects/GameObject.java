package com.mycompany.app.model.objects;

import com.mycompany.app.model.concurrency.PausableRunnable;
import com.mycompany.app.model.map.*;
import lombok.Getter;

import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public abstract class GameObject implements PausableRunnable {
    private static final AtomicLong counter = new AtomicLong(0);
    private static final AtomicInteger tick = new AtomicInteger(500);
    public final long id;
    public final ObjectType type;
    @Getter
    protected Tile currentTile;
    @Getter
    protected Point coordinates = null;
    protected Orientation orientation;
    protected int speed = 10;
    private final Area area;

    public static void setUpdateTick(int miliseconds) {
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
        registerObject();
        while (!Thread.currentThread().isInterrupted()) {
            checkPaused();
            Optional<GameEvent> event = act();
            event.ifPresent(area::notifyListeners);
            waitTick(tick.get()/speed);
        }
        unregisterObject();
    }

    public int getState() {
        return orientation.ordinal();
    }

    protected List<Tile> probe(Point direction, int depth) {
        return area.probe(coordinates, direction, depth);
    }

    protected abstract Optional<GameEvent> act();

    protected abstract GameEvent remove();

    protected abstract GameEvent create(int x, int y, GameObject object);

    protected GameEvent move(Tile target) {
        synchronized (target) {
            if (target.add(this)) {
                Point origin = coordinates;
                if (getCurrentTile() != null) {
                    getCurrentTile().remove(this);
                }
                setCurrentTile(target);
                return new GameEvent(origin, target.coordinates, this, GameEvent.EventType.MOVE);
            }
        }
        return null;
    }

    protected abstract GameEvent push(int x, int y, int vectorX, int vectorY);

    protected synchronized GameEvent rotate(int turns) {
        orientation = orientation.turnRight(turns);
        return new GameEvent(coordinates,this, GameEvent.EventType.ROTATION);
    }

    void setCurrentTile(Tile tile) {
        currentTile = tile;
        coordinates = tile.coordinates;
    }

    private void waitTick(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void registerObject() {
        area.registerObject(this);
    }

    private void unregisterObject() {
        area.unregisterObject(this);
    }
}
