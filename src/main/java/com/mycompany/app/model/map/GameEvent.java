package com.mycompany.app.model.map;

import com.mycompany.app.model.objects.GameObject;
import lombok.RequiredArgsConstructor;

import java.awt.*;

public record GameEvent (
    Point origin,
    Point target,
    GameObject object,
    EventType eventType
) {
    public GameEvent(Point origin, GameObject object, EventType eventType) {
        this(origin, origin, object, eventType);
    }

    @RequiredArgsConstructor
    public enum EventType {
        CONSUMPTION(false),
        CREATION(true),
        DESTRUCTION(true),
        MOVE(true),
        PROMOTION(true),
        ROTATION(false);
        public final boolean change;
    }
}
