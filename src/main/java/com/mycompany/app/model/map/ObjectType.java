package com.mycompany.app.model.map;

import com.mycompany.app.model.units.Scout;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

@RequiredArgsConstructor
public enum ObjectType {
    SCOUT(Scout::new);

    private final Function<Area, GameObject> create;

    public GameObject create(Area area) {
        return create.apply(area);
    }
}
