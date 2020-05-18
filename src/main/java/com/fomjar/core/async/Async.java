package com.fomjar.core.async;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * 异步执行器汇总工具集。<br/>
 * 推荐用<code>
 *     import static com.fomjar.core.async.Async.*;
 * </code>
 * 的方式来调用。
 */
public abstract class Async {

    private static final ThreadPoolTaskScheduler pool = new ThreadPoolTaskScheduler();
    static {
        Async.pool.setThreadFactory(new SimpleThreadFactory("main-pool"));
        Async.pool.initialize();
        Async.poolSize(10);
    }

    public static int poolSize() { return Async.pool.getPoolSize(); }
    public static void poolSize(int size) { Async.pool.setPoolSize(size); }

    public static Future<?> async(Runnable task) {
        return Async.pool.submit(task);
    }

    public static <T> Future<T> async(Callable<T> task) {
        return Async.pool.submit(task);
    }

    public static Future<?> delay(Runnable task, long delay) {
        return Async.pool.scheduleWithFixedDelay(task, delay);
    }

    @SuppressWarnings("unchecked")
    public static <T> Future<T> delay(Callable<T> task, long delay) {
        return (Future<T>) Async.pool.scheduleWithFixedDelay(new FutureTask<>(task), delay);
    }

    public static Future<?> delay(Runnable task, Date time) {
        return Async.pool.scheduleWithFixedDelay(task, time, 0);
    }

    @SuppressWarnings("unchecked")
    public static <T> Future<T> delay(Callable<T> task, Date time) {
        return (Future<T>) Async.pool.scheduleWithFixedDelay(new FutureTask<>(task), time, 0);
    }

    public static Future<?> loop(Runnable task, long period) {
        return Async.loop(task, 0, period);
    }

    public static Future<?> loop(Runnable task, long delay, long period) {
        return Async.pool.scheduleAtFixedRate(task, new Date(System.currentTimeMillis() + delay), period);
    }

    public static <T> Future<T> loop(Callable<T> task, long period) {
        return Async.loop(task, 0, period);
    }

    @SuppressWarnings("unchecked")
    public static <T> Future<T> loop(Callable<T> task, long delay, long period) {
        return (Future<T>) Async.pool.scheduleAtFixedRate(new FutureTask<>(task), new Date(System.currentTimeMillis() + delay), period);
    }

    public static Future<?> loop(Runnable task, String cron) {
        return Async.pool.schedule(task, new CronTrigger(cron));
    }

    @SuppressWarnings("unchecked")
    public static <T> Future<T> loop(Callable<T> task, String cron) {
        return (Future<T>) Async.pool.schedule(new FutureTask<>(task), new CronTrigger(cron));
    }

}
