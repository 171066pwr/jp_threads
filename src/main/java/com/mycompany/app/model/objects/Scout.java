package com.mycompany.app.model.objects;

import com.mycompany.app.model.map.*;

import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Scout extends GameObject implements Unit {
    Random rand = new Random();

    public Scout(Area area) {
        super(area, ObjectType.SCOUT);
        rand.setSeed(System.currentTimeMillis());
    }

    @Override
    protected Optional<GameEvent> act() {
        int roll = rand.nextInt(100);
        int limit = 100;
        GameEvent event = null;
        while (limit > 0) {
            if (roll < 50) {
                roll = rand.nextInt(100);
                event = roll < 50 ? rotate(-1) : rotate(1);
                break;
            } else {
                List<Tile> probed = probe(orientation.getDirection(), 1);
                if (!probed.isEmpty()) {
                    Tile tile = probed.getFirst();
                    synchronized (tile) {
                        event = move(tile);
                    }
                    break;
                }
            }
            roll = rand.nextInt(100);
            limit--;
        }
        return Optional.ofNullable(event);
    }

    @Override
    protected GameEvent remove() {
        return null;
    }

    @Override
    protected GameEvent create(int x, int y, GameObject object) {
        return null;
    }

    @Override
    protected GameEvent move(Tile target) {
        synchronized (target) {
            if (target.add(this)) {
                Point currentPosition = coordinates;
                if (getCurrentTile() != null) {
                    getCurrentTile().remove(this);
                }
                setCurrentTile(target);
                return new GameEvent(currentPosition != null ? currentPosition : coordinates, coordinates, this, GameEvent.EventType.MOVE);
            }
        }
        return null;
    }

    @Override
    public GameEvent push(int x, int y, int vectorX, int vectorY) {
        return null;
    }
}
