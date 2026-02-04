package com.mycompany.app.threads.model.objects;

import com.mycompany.app.threads.model.map.Area;
import com.mycompany.app.threads.model.map.GameEvent;
import com.mycompany.app.threads.model.map.ObjectType;

import java.util.Collections;
import java.util.List;

public class Cookie extends GameObject {
    public Cookie(Area area) {
        super(area, ObjectType.COOKIE);
        speed = 1;
    }

    @Override
    protected List<GameEvent> act() {
        return Collections.EMPTY_LIST;
    }
}
