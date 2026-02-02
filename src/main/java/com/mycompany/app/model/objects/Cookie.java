package com.mycompany.app.model.objects;

import com.mycompany.app.model.map.Area;
import com.mycompany.app.model.map.GameEvent;
import com.mycompany.app.model.map.ObjectType;
import com.mycompany.app.model.map.Perk;

import java.util.Optional;

public class Cookie extends GameObject implements Perk {
    public Cookie(Area area) {
        super(area, ObjectType.COOKIE);
    }

    @Override
    protected Optional<GameEvent> act() {
        return Optional.empty();
    }

    @Override
    protected GameEvent remove() {
        return new GameEvent(coordinates, this, GameEvent.EventType.DESTRUCTION);
    }

    @Override
    protected GameEvent create(int x, int y, GameObject object) {
        return null;
    }

    @Override
    protected GameEvent push(int x, int y, int vectorX, int vectorY) {
        return null;
    }
}
