package com.fomjar.core.async;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

public class SimpleThreadFactory implements ThreadFactory {

    private static final AtomicLong GROUP   = new AtomicLong(0);
    private static final AtomicLong ID      = new AtomicLong(0);

    private String prefix;

    public SimpleThreadFactory() {
        this("fomjar-pool-" + SimpleThreadFactory.GROUP.getAndIncrement());
    }

    public SimpleThreadFactory(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, this.prefix + "-" + SimpleThreadFactory.ID.getAndIncrement());
    }

}
