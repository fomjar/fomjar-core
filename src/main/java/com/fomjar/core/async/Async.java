package com.fomjar.core.async;

import com.fomjar.core.data.Struct;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 异步执行期汇总工具集。<br/>
 * 推荐用<code>
 *     import static com.fomjar.core.async.Async.*;
 * </code>
 * 的方式来调用。
 */
public abstract class Async {

    private static final ExecutorService queue  = QueuedExecutor.main;
    private static final ExecutorService pool   = null; // initialize later
    private static final int DEFAULT_POOL_SIZE  = 10;

    private static ExecutorService checkPool() {
        if (null == Async.pool) {
            synchronized (Async.class) {
                if (null == Async.pool) {
                    ExecutorService pool = Executors.newScheduledThreadPool(Async.DEFAULT_POOL_SIZE,
                            new SimpleThreadFactory("main-pool"));
                    try { Struct.setFinalObject(Async.class, "pool", pool); }
                    catch (NoSuchFieldException e) { e.printStackTrace(); }
                }
            }
        }
        return Async.pool;
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
        return Async.checkPool().submit(task);
    }

    public static <T> Future<T> pool(Runnable task, T result) {
        return Async.checkPool().submit(task, result);
    }

    public static <T> Future<T> pool(Callable<T> task) {
        return Async.checkPool().submit(task);
    }

}
