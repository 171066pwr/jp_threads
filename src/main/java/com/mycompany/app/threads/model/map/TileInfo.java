package com.mycompany.app.threads.model.map;

import lombok.Getter;

@Getter
public class TileInfo {
    private final boolean isOccupied;
    private final String objectType;
    private final long objectId;
    private final int speed;
    private final int experience;

    public TileInfo(Tile tile) {
        this.isOccupied = tile.isOccupied();
        this.objectType = isOccupied ? tile.getObjectType().toString() : "";
        this.objectId = isOccupied ? tile.getObject().getId() : 0;
        this.speed = isOccupied ? tile.getObject().getSpeed() : 0;
        this.experience = isOccupied ? tile.getObject().getExperience() : 0;
    }
}
