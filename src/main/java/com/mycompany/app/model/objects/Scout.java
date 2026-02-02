package com.mycompany.app.model.objects;

import com.mycompany.app.model.map.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Scout extends GameObject {
    private final int promotion_threshold = 5;
    private final Random rand = new Random();

    public Scout(Area area) {
        super(area, ObjectType.SCOUT);
        rand.setSeed(System.currentTimeMillis());
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
            if(probed.isEmpty()) {
                roll = 0;
            } else if(spotTarget(orientation.getDirection(), 3, ObjectType.COOKIE)) {
                roll = 50;
            }
            if (roll < 50) {
                roll = rand.nextInt(100);
                int rotation = probed.isEmpty() ? 2: 1;
                events.add(roll < 50 ? rotate(-rotation) : rotate(rotation));
            } else {
                Tile tile = probed.getFirst();
                synchronized (tile) {
                    events.addAll(move(tile));
                    if(experience >= promotion_threshold) {
                        events.addAll(promote(tile));
                    }
                }
            }
        }
        return events;
    }

    List<GameEvent> promote(Tile tile) {
        List<GameEvent> events = new ArrayList<>();
        events.add(this.selfDestruct());
        GameObject ranger = ObjectType.RANGER.create(area);
        ranger.orientation = this.orientation;
        create(tile, ranger).ifPresent(events::add);
        return events;
    }

    private Optional<GameEvent> create(Tile target, GameObject object) {
        if (target.add(object)) {
            object.setCurrentTile(target);
            new Thread(object).start();
            return Optional.of(new GameEvent(target.coordinates, object, GameEvent.EventType.PROMOTION));
        }
        return Optional.empty();
    }
}
