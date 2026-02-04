package com.mycompany.app.threads.model.map;

import com.mycompany.app.threads.model.objects.*;
import lombok.AllArgsConstructor;

import java.util.function.Function;

import static com.mycompany.app.threads.model.map.ObjectType.Subtype.*;

@AllArgsConstructor
public enum ObjectType {
    BULLET(Bullet::new, PROJECTILE),
    COOKIE(Cookie::new, PERK),
    RANGER(Ranger::new, UNIT),
    SCOUT(Scout::new, UNIT),
    TANK(Tank::new, UNIT);

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
