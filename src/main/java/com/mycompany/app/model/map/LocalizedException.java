package com.mycompany.app.model.map;

import java.awt.*;

public class LocalizedException extends Exception {
    final Point coordinates;
    final String origin;

    public LocalizedException(String origin, String message, Point coords) {
        super(String.format("%s[%d;%d]: %s", origin, coords.x, coords.y, message));
        coordinates = coords;
        this.origin = origin;
    }
}
