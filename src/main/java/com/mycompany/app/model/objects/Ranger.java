package com.mycompany.app.model.objects;

import com.mycompany.app.model.map.Area;
import com.mycompany.app.model.map.GameEvent;
import com.mycompany.app.model.map.ObjectType;
import com.mycompany.app.model.map.Tile;

import java.util.*;

public class Ranger extends GameObject {
    private final List<ObjectType> targets = Arrays.asList(ObjectType.SCOUT, ObjectType.RANGER);

    public Ranger(Area area) {
        super(area, ObjectType.RANGER);
        speed = 8;
    }

    @Override
    protected List<GameEvent> act() {
        List<GameEvent> events = new ArrayList<>();
        int roll;
        int limit = retryLimit;
        while (limit > 0 && events.isEmpty()) {
            roll = rand.nextInt(100);
            limit--;
            List<Tile> probed = probe(orientation.getDirection(), 1);
            if(spotTarget(orientation.getDirection(), 10, targets)) {
                roll = 99;
            }
            if(probed.isEmpty() || roll < 40) {
                events.add(randomRotate(probed));
            } else {
                Tile tile = probed.getFirst();
                if(roll < 90) {
                    synchronized (tile) {
                        events.addAll(move(tile));
                    }
                } else {
                    synchronized (tile) {
                        events.addAll(shoot(tile));
                    }
                }
            }
        }
        return events;
    }

    private List<GameEvent> shoot(Tile target) {
        List<GameEvent> events = new ArrayList<>();
        if(target.getObjectType() != null && target.getObjectType() != ObjectType.TANK) {
            destroy(target).ifPresent(events::add);
        } else {
            GameObject bullet = ObjectType.BULLET.create(area);
            bullet.orientation = this.orientation;
            create(target, bullet).ifPresent(events::add);
        }
        return events;
    }

    private Optional<GameEvent> create(Tile target, GameObject object) {
        if (target.add(object)) {
            object.setCurrentTile(target);
            new Thread(object).start();
            return Optional.of(new GameEvent(target.coordinates, object, GameEvent.EventType.CREATION));
        }
        return Optional.empty();
    }
}
