package com.fomjar.core.async;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Async {

    public static final ExecutorService queue   = QueuedExecutor.main;
    public static       ExecutorService pool    = null;

    private static void check() {
        if (null == Async.pool)
            Async.pool = Executors.newScheduledThreadPool(10,
                    new SimpleThreadFactory("main-pool"));
    }

    public static Future<?> async(Runnable task) {
        return Async.queue.submit(task);
    }

    public static <T> Future<T> async(Runnable task, T result) {
        return Async.queue.submit(task, result);
    }

    public static <T> Future<T> async(Callable<T> task) {
        return Async.queue.submit(task);
    }

    public static Future<?> queue(Runnable task) {
        return Async.queue.submit(task);
    }

    public static <T> Future<T> queue(Runnable task, T result) {
        return Async.queue.submit(task, result);
    }

    public static <T> Future<T> queue(Callable<T> task) {
        return Async.queue.submit(task);
    }

    public static Future<?> pool(Runnable task) {
        Async.check();
        return Async.pool.submit(task);
    }

    public static <T> Future<T> pool(Runnable task, T result) {
        Async.check();
        return Async.pool.submit(task, result);
    }

    public static <T> Future<T> pool(Callable<T> task) {
        Async.check();
        return Async.pool.submit(task);
    }

}