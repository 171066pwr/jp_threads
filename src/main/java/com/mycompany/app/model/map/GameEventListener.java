package com.mycompany.app.model.map;

public interface GameEventListener {
    default void register(Area area) {
        area.addListener(this);
    }

    void acceptEvent(GameEvent event);
}
