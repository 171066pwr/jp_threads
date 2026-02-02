package com.mycompany.app.model.map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.awt.*;

@Getter
@RequiredArgsConstructor
public enum Orientation {
    NORTH(0, new Point(0, -1)),
    NORTH_EAST(1, new Point(1, -1)),
    EAST(2, new Point(1, 0)),
    SOUTH_EAST(3, new Point(1, 1)),
    SOUTH(4, new Point(0, 1)),
    SOUTH_WEST(5, new Point(-1, 1)),
    WEST(6, new Point(-1, 0)),
    NORTH_WEST(7, new Point(-1, -1));

    private final int value;
    private final Point direction;

    public Orientation turnRight(int turns) {
        turns = turns % 8;
        return values()[(this.value + 8 + turns) % 8];
    }

    public Orientation turnLeft(int turns) {
        turns = turns % 8;
        return values()[(this.value + 8 - turns) % 8];
    }

    public Point addDirection(Point direction) {
        return new Point(direction.x + this.direction.x, direction.y + this.direction.y);
    }
}
