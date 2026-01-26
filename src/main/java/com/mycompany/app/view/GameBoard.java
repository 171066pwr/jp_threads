package com.mycompany.app.view;

import com.mycompany.app.model.creator.Creator;
import com.mycompany.app.model.map.Area;
import com.mycompany.app.model.map.GameEventListener;

public abstract class GameBoard implements GameEventListener {
    protected Area area;
    protected Creator creator;

    public GameBoard(int width, int height) {
        this(width, height, Creator.CreatorOptions.builder().build());
    }

    public GameBoard(int width, int height, Creator.CreatorOptions creatorOptions) {
        this.area = new Area(width, height);
        this.creator = new Creator(area, creatorOptions);
    }

    public void start() {
        creator.run();
    }
}
