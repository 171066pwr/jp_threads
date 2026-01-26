package com.mycompany.app.model.map;

import lombok.RequiredArgsConstructor;

import java.awt.*;

public record GameEvent (
    Point point,
    GameObject object,
    EventType eventType
) {
    @RequiredArgsConstructor
    public static enum EventType {
        CREATION(true),
        MOVE(true),
        DESTRUCTION(true),
        ROTATION(false);

        public final boolean change;
    }
}
