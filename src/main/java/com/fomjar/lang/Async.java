package com.fomjar.lang;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import java.util.Date;
import java.util.concurrent.*;

/**
 * 异步执行器汇总工具集。<br>
 * 推荐用<code>
 *     import static com.fomjar.core.async.Async.*;
 * </code>
 * 的方式来调用。
 */
public abstract class Async {

    private static final ExecutorService            pool        = Executors.newCachedThreadPool(new SingleThreadFactory("main-pool"));
    private static final ThreadPoolTaskScheduler    scheduler   = ((Func<ThreadPoolTaskScheduler>) (args) -> {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadFactory(new SingleThreadFactory("main-scheduler"));
        scheduler.initialize();
        scheduler.setPoolSize(10);
        return scheduler;
    }).call();

    public static     Future<?> async(Runnable    task) {
        return Async.pool.submit(task);
    }
    public static <T> Future<T> async(Callable<T> task) {
        return Async.pool.submit(task);
    }

    public static     Future<?> delay(Runnable    task, Date time ) { return Async.scheduler.schedule(task, time); }
    public static <T> Future<T> delay(Callable<T> task, Date time ) { return Async.scheduler.getScheduledExecutor().schedule(task, time.getTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS); }
    public static     Future<?> delay(Runnable    task, long delay) { return Async.delay(task, new Date(System.currentTimeMillis() + delay)); }
    public static <T> Future<T> delay(Callable<T> task, long delay) { return Async.delay(task, new Date(System.currentTimeMillis() + delay)); }

    public static     Future<?> loop(Runnable    task, Date time,  long period) { return Async.scheduler.scheduleAtFixedRate(task, time, period); }
    public static <T> Future<T> loop(Callable<T> task, Date time,  long period) { return (Future<T>) Async.loop(new FutureTask<>(task), time, period); }
    public static     Future<?> loop(Runnable    task, long delay, long period) { return Async.loop(task, new Date(System.currentTimeMillis() + delay), period); }
    public static <T> Future<T> loop(Callable<T> task, long delay, long period) { return Async.loop(task, new Date(System.currentTimeMillis() + delay), period); }
    public static     Future<?> loop(Runnable    task,             long period) { return Async.loop(task, 0, period); }
    public static <T> Future<T> loop(Callable<T> task,             long period) { return Async.loop(task, 0, period); }
    public static     Future<?> loop(Runnable    task,             String cron) { return Async.scheduler.schedule(task, new CronTrigger(cron)); }
    public static <T> Future<T> loop(Callable<T> task,             String cron) { return (Future<T>) Async.loop(new FutureTask<>(task), cron); }

}
