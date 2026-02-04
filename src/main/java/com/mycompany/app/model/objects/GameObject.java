package com.mycompany.app.model.objects;

import com.mycompany.app.model.concurrency.PausableRunnable;
import com.mycompany.app.model.concurrency.RegulatedRunnable;
import com.mycompany.app.model.map.*;
import lombok.Getter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public abstract class GameObject implements PausableRunnable, RegulatedRunnable {
    public static final int retryLimit = 20;
    private static final AtomicLong counter = new AtomicLong(0);
    private final AtomicBoolean isAlive = new AtomicBoolean(true);
    public final long id;
    protected final Area area;
    protected final Random rand = new Random();

    @Getter
    protected final ObjectType type;
    @Getter
    protected Tile currentTile;
    @Getter
    protected Point coordinates = null;
    protected Orientation orientation;
    protected int experience = 0;
    protected int sight = 0;
    protected int speed = 10;

    public GameObject(Area area, ObjectType type) {
        this.area = area;
        this.type = type;
        this.id = counter.incrementAndGet();
        orientation = Orientation.NORTH;
        rand.setSeed(System.currentTimeMillis());
    }

    protected abstract List<GameEvent> act();

    @Override
    public void run() {
        registerObject();
        while (isAlive.getAcquire() && !Thread.currentThread().isInterrupted()) {
            checkPaused();
            List<GameEvent> events = act();
            events.forEach(area::notifyListeners);
            waitTick(tick.get()/speed);
        }
    }

    public int getState() {
        return orientation.ordinal();
    }
    protected List<Tile> probe(Point direction, int depth) {
        return area.probe(coordinates, direction, depth);
    }

    protected GameEvent selfDestruct() {
        currentTile.remove(this);
        this.kill();
        return new GameEvent(coordinates, this, GameEvent.EventType.DESTRUCTION);
    }

    protected Optional<GameEvent> destroy(Tile target) {
        GameObject object = target.getObject();
        if(target.remove(object)) {
            object.kill();
            levelUp();
            return Optional.of(new GameEvent(target.coordinates, target.coordinates, object, GameEvent.EventType.DESTRUCTION));
        }
        return Optional.empty();
    }

    protected List<GameEvent> move(Tile target) {
        List<GameEvent> events = new ArrayList<>();
        if(ObjectType.COOKIE == target.getObjectType()) {
            destroy(target).ifPresent(events::add);
        }
        if (target.add(this)) {
            Point currentPosition = coordinates;
            if (getCurrentTile() != null) {
                getCurrentTile().remove(this);
            }
            setCurrentTile(target);
            events.add(new GameEvent(currentPosition != null ? currentPosition : coordinates, coordinates, this, GameEvent.EventType.MOVE));
        }
        return events;
    }

    protected synchronized GameEvent rotate(int turns) {
        orientation = orientation.turnRight(turns);
        return new GameEvent(coordinates,this, GameEvent.EventType.ROTATION);
    }

    protected GameEvent randomRotate(List<Tile> probed) {
        int roll = rand.nextInt(100);
        int rotation = probed.isEmpty() ? rand.nextInt(3): 1;
        return (roll < 50 ? rotate(-rotation) : rotate(rotation));
    }

    protected boolean spotTarget(Point direction, List<ObjectType> types) {
        return probe(direction, sight).stream()
                .limit(1)
                .anyMatch(t -> types.contains(t.getObjectType()));
    }

    protected boolean spotTarget(Point direction, ObjectType type) {
        return probe(direction, sight).stream()
                .limit(1)
                .anyMatch(t -> type.equals(t.getObjectType()));
    }

    protected void levelUp() {
        experience++;
    }

    protected void kill() {
        if(isAlive.compareAndSet(true, false)) {
            unregisterObject();
        }
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
