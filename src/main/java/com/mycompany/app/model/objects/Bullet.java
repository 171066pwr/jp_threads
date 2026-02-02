package com.mycompany.app.model.objects;

import com.mycompany.app.model.map.Area;
import com.mycompany.app.model.map.GameEvent;
import com.mycompany.app.model.map.ObjectType;
import com.mycompany.app.model.map.Tile;

import java.util.ArrayList;
import java.util.List;

public class Bullet extends GameObject {
    private int ttl = 10;

    public Bullet(Area area) {
        super(area, ObjectType.BULLET);
        speed = 15;
    }

    @Override
    protected List<GameEvent> act() {
        List<GameEvent> events = new ArrayList<>();
        if (ttl > 0) {
            ttl--;
            List<Tile> probed = probe(orientation.getDirection(), 1);
            if (!probed.isEmpty()) {
                Tile tile = probed.getFirst();
                synchronized (tile) {
                    if (tile.isOccupied()) {
                        if(tile.getObjectType() != ObjectType.TANK) {
                            destroy(tile).ifPresent(events::add);
                        }
                        events.add(this.selfDestruct());
                    } else {
                        events.addAll(move(tile));
                    }
                }
            } else {
                events.add(this.selfDestruct());
            }
        } else {
            events.add(this.selfDestruct());
        }
        return events;
    }
}
