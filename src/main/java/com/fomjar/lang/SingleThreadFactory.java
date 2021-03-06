package com.fomjar.lang;

import java.util.concurrent.ThreadFactory;

/**
 * 单线程工厂。用于指定线程名称。
 */
public class SingleThreadFactory implements ThreadFactory {

    private String name;

    public SingleThreadFactory(String name) {
        this.name = name;
    }

    @Override
    public Thread newThread(Runnable r) {
        return Task.catchdo(() -> {
            Thread t = new Thread(r, this.name);
            t.setDaemon(true);
            return t;
        });
    }
}
