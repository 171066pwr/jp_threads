package com.mycompany.app.threads.model.objects;

import com.mycompany.app.threads.model.map.Area;
import com.mycompany.app.threads.model.map.GameEvent;
import com.mycompany.app.threads.model.map.ObjectType;

import java.util.List;

public class Missile extends Bullet {
    public Missile(Area area) {
        super(area, ObjectType.MISSILE);
        excludedTargets.clear();
        speed = 10;
    }

    @Override
    protected List<GameEvent> act() {
        speed ++;
        setRange(speed);
        return super.act();
    }
}
