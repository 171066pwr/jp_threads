package com.mycompany.app.threads.model.map;

public interface GameEventListener {
    default void register(Area area) {
        area.addListener(this);
    }

    void acceptEvent(GameEvent event);
}
