package com.mycompany.app.threads.model.objects;

import com.mycompany.app.threads.model.map.Area;
import com.mycompany.app.threads.model.map.GameEvent;
import com.mycompany.app.threads.model.map.ObjectType;
import com.mycompany.app.threads.model.map.Tile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tank extends GameObject {
    private final List<ObjectType> targets = Arrays.asList(ObjectType.SCOUT, ObjectType.RANGER);

    public Tank(Area area) {
        super(area, ObjectType.TANK);
        speed = 6;
        sight = 5;
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
            if (spotTarget(orientation.getDirection(), targets)) {
                roll = 99;
            }
            if (probed.isEmpty() || roll < 40) {
                events.add(randomRotate(probed));
            } else {
                Tile tile = probed.getFirst();
                synchronized (tile) {
                    events.addAll(push(tile));
                }
            }
        }
        return events;
    }

    synchronized List<GameEvent> push(Tile tile) {
        List<GameEvent> events = new ArrayList<>();
        if(tile.isOccupied()) {
            switch (tile.getObjectType()) {
                case TANK:
                    return events;
                case BULLET:
                    destroy(tile).ifPresent(events::add);
                    break;
                default:
                    GameObject object = tile.getObject();
                    Tile target = object.probe(orientation.getDirection(), 1).stream().findFirst().orElse(tile);
                    if (target == tile || !events.addAll(object.move(target))) {
                        destroy(tile).ifPresent(events::add);
                    }
            }
        }
        events.addAll(move(tile));
        return events;
    }
}
