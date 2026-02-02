package com.mycompany.app.model.objects;

import com.mycompany.app.model.map.*;

import java.util.*;

public class Tank extends GameObject {
    private final Random rand = new Random();
    private final List<ObjectType> targets = Arrays.asList(ObjectType.SCOUT, ObjectType.RANGER);

    public Tank(Area area) {
        super(area, ObjectType.TANK);
        rand.setSeed(System.currentTimeMillis());
        speed = 6;
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
            if (spotTarget(orientation.getDirection(), 5, targets)) {
                roll = 99;
            }
            if (probed.isEmpty() || roll < 40) {
                roll = rand.nextInt(100);
                int rotation = probed.isEmpty() ? 2 : 1;
                events.add(roll < 50 ? rotate(-rotation) : rotate(rotation));
            } else {
                Tile tile = probed.getFirst();
                synchronized (tile) {
                    events.addAll(push(tile));
                }
            }
        }
        return events;
    }

    List<GameEvent> push(Tile tile) {
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
