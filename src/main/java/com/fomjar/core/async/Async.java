package com.fomjar.core.async;

import com.fomjar.core.data.Struct;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;

/**
 * 异步执行器汇总工具集。<br/>
 * 推荐用<code>
 *     import static com.fomjar.core.async.Async.*;
 * </code>
 * 的方式来调用。
 */
public abstract class Async {

    private static final ExecutorService queue  = QueuedExecutor.main;
    private static final ExecutorService pool   = Executors.newScheduledThreadPool(Async.DEFAULT_POOL_SIZE, new SimpleThreadFactory("main-pool"));
    private static final int DEFAULT_POOL_SIZE  = 10;
    private static final Timer timer = new Timer("main-timer", true);

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
        return Async.pool.submit(task);
    }

    public static <T> Future<T> pool(Runnable task, T result) {
        return Async.pool.submit(task, result);
    }

    public static <T> Future<T> pool(Callable<T> task) {
        return Async.pool.submit(task);
    }

    public static Future<?> delay(Runnable task, long delay) {
        return Async.delay(task, null, delay);
    }

    public static <T> Future<T> delay(Runnable task, T result, long delay) {
        FutureTask<T> futureTask = new FutureTask<>(task, result);
        Async.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                futureTask.run();
            }
        }, delay);
        return futureTask;
    }

    public static <T> Future<T> delay(Callable<T> task, long delay) {
        FutureTask<T> futureTask = new FutureTask<>(task);
        Async.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                futureTask.run();
            }
        }, delay);
        return futureTask;
    }

    public static Future<?> delay(Runnable task, Date time) {
        return Async.delay(task, null, time);
    }

    public static <T> Future<T> delay(Runnable task, T result, Date time) {
        FutureTask<T> futureTask = new FutureTask<>(task, result);
        Async.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                futureTask.run();
            }
        }, time);
        return futureTask;
    }

    public static <T> Future<T> delay(Callable<T> task, Date time) {
        FutureTask<T> futureTask = new FutureTask<>(task);
        Async.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                futureTask.run();
            }
        }, time);
        return futureTask;
    }

    public static void loop(Runnable task, long period) {
        Async.loop(task, 0, period);
    }

    public static void loop(Runnable task, long delay, long period) {
        Async.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                task.run();
            }
        }, delay, period);
    }

    public static void loop(Callable<?> task, long period) {
        Async.loop(task, 0, period);
    }

    public static void loop(Callable<?> task, long delay, long period) {
        Async.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try { task.call(); }
                catch (Exception e) { e.printStackTrace(); }
            }
        }, delay, period);
    }

    public static void loop(Runnable task, Date time, long period) {
        Async.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                task.run();
            }
        }, time, period);
    }

    public static void loop(Callable<?> task, Date time, long period) {
        Async.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try { task.call(); }
                catch (Exception e) { e.printStackTrace(); }
            }
        }, time, period);
    }

}
