package com.mycompany.app.threads.model.concurrency;

import java.util.concurrent.atomic.AtomicBoolean;

public interface PausableRunnable extends Runnable {
    static AtomicBoolean paused = new AtomicBoolean(false);

    static void setPaused(boolean value) {
        paused.set(value);
    }

    static void flipPaused() {
        paused.set(!paused.get());
    }

    default boolean isPaused() {
        return paused.get();
    }

    default void checkPaused() {
        while(isPaused()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                paused.notifyAll();
            }
        }
    }
}
