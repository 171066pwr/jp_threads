package com.mycompany.app.model.concurrency;

import java.util.concurrent.atomic.AtomicInteger;

public interface RegulatedRunnable {
    static final AtomicInteger tick = new AtomicInteger(1000);

    public static void setUpdateTick(int miliseconds) {
        tick.set(miliseconds < 0 ? (-miliseconds) : miliseconds);
    }
}
