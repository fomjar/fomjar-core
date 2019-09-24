package com.fomjar.core.lio;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Pool {

    private static final ExecutorService pool = Executors.newCachedThreadPool();

    public static void submit(Runnable runnable) {
        Pool.pool.submit(runnable);
    }

}
