package com.mycompany.app.threads.view;

import com.mycompany.app.threads.model.concurrency.PausableRunnable;
import com.mycompany.app.threads.model.map.Area;
import com.mycompany.app.threads.model.map.GameEvent;
import com.mycompany.app.threads.model.map.GameEventListener;
import com.mycompany.app.threads.model.objects.Creator;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Log4j2
public abstract class GameBoard implements GameEventListener, PausableRunnable {
    protected Area area;
    protected Creator creator;
    protected BlockingQueue<GameEvent> events = new LinkedBlockingQueue<>();

    public GameBoard(int width, int height) {
        this(width, height, Creator.CreatorOptions.builder().build());
    }

    public GameBoard(int width, int height, Creator.CreatorOptions creatorOptions) {
        this.area = new Area(width, height);
        this.creator = new Creator(area, creatorOptions);
        area.addListener(this);
    }

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            checkPaused();
            if(events.size() > 0) {
                GameEvent event = events.poll();
                if(event != null) {
                    processEvent(event);
                }
            }
        }
        log.error(Thread.currentThread().getName() + " interrupted");
    }

    protected abstract void processEvent(GameEvent event);

    public void start() {
        new Thread(creator).start();
        new Thread(this).start();
    }

    @Override
    public void acceptEvent(GameEvent event) {
        events.add(event);
    }
}
