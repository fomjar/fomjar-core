package com.fomjar.core.async;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

public class SimpleThreadFactory implements ThreadFactory {

    private static final AtomicLong GROUP = new AtomicLong(0);

    private String prefix;
    private AtomicLong id;

    public SimpleThreadFactory() {
        this("pool-" + SimpleThreadFactory.GROUP.getAndIncrement());
    }

    public SimpleThreadFactory(String prefix) {
        this.prefix = prefix;
        this.id = new AtomicLong(0);
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, this.prefix + "-" + this.id.getAndIncrement());
    }

}
