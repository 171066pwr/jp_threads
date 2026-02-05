package com.mycompany.app.threads.model.objects;

import com.mycompany.app.threads.model.map.Area;
import com.mycompany.app.threads.model.map.GameEvent;
import com.mycompany.app.threads.model.map.ObjectType;
import com.mycompany.app.threads.model.map.Tile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Bullet extends GameObject implements Projectile {
    protected final List<ObjectType> excludedTargets = new ArrayList<>(Arrays.asList(ObjectType.TANK));
    private GameObject owner;
    private Integer ttl;

    public Bullet(Area area) {
        super(area, ObjectType.BULLET);
        speed = 15;
    }

    protected Bullet(Area area, ObjectType type) {
        super(area, type);
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
                        if(!excludedTargets.contains(tile.getObjectType())) {
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

    public void setRange(int range) {
        this.ttl = range;
    }

    @Override
    public void setOwner(GameObject owner) {
        this.owner = this.owner == null ? owner : this.owner;
    }

    @Override
    public GameObject getOwner() {
        return owner;
    }

    @Override
    protected void levelUp(int exp) {
        if(owner != null) {
            owner.levelUp(exp);
        }
    }
}
