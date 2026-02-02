package com.mycompany.app.model.map;

import com.mycompany.app.model.objects.Cookie;
import com.mycompany.app.model.objects.GameObject;
import com.mycompany.app.model.objects.Scout;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

@RequiredArgsConstructor
public enum ObjectType {
    SCOUT(Scout::new),
    COOKIE(Cookie::new);

    private final Function<Area, GameObject> create;

    public GameObject create(Area area) {
        return create.apply(area);
    }
}
