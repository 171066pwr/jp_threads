package com.mycompany.app.model.map;

import com.mycompany.app.model.objects.*;
import lombok.AllArgsConstructor;

import java.util.function.Function;

import static com.mycompany.app.model.map.ObjectType.Subtype.*;

@AllArgsConstructor
public enum ObjectType {
    BULLET(Bullet::new, PROJECTILE),
    COOKIE(Cookie::new, PERK),
    RANGER(Ranger::new, UNIT),
    SCOUT(Scout::new, UNIT);

    private final Function<Area, GameObject> create;
    public final Subtype subtype;

    public GameObject create(Area area) {
        return create.apply(area);
    }

    public boolean isUnit() {
        return this.subtype == UNIT;
    }

    public boolean isPerk() {
        return this.subtype == PERK;
    }

    public enum Subtype {
        PROJECTILE,
        PERK,
        UNIT
    }
}
