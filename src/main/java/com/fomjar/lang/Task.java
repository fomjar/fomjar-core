package com.fomjar.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import java.util.Date;
import java.util.concurrent.*;

/**
 * 异步执行器汇总工具集。
 */
public abstract class Task {

    private static final Logger logger = LoggerFactory.getLogger(Task.class);

    private static final ExecutorService            pool        = Executors.newCachedThreadPool(new IncrementThreadFactory("main-pool"));
    private static final ThreadPoolTaskScheduler    scheduler   = Task.catchdo(() -> {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadFactory(new IncrementThreadFactory("main-scheduler"));
        scheduler.initialize();
        scheduler.setPoolSize(10);
        return scheduler;
    });

    public static     Runnable    catcher(Runnable    task) {
        return () -> {
            try { task.run(); }
            catch (Exception e) { logger.error("Oops!", e); }
        };
    }
    public static <T> Callable<T> catcher(Callable<T> task) { return Task.catcher(task, null); }
    public static <T> Callable<T> catcher(Callable<T> task, T result) {
        return () -> {
            try { return task.call(); }
            catch (Exception e) { logger.error("Oops!", e); }
            return result;
        };
    }
    public static     void        catchdo(Runnable    task) { Task.catcher(task).run(); }
    public static <T> T           catchdo(Callable<T> task) {
        try { return Task.catcher(task).call(); }
        catch (Exception e) { e.printStackTrace(); }    // never happened
        return null;
    }
    public static <T> T           catchdo(Callable<T> task, T result) {
        try { return Task.catcher(task, result).call(); }
        catch (Exception e) { e.printStackTrace(); }    // never happened
        return result;
    }

    public static     Future<?> async(Runnable    task) { return Task.pool.submit(Task.catcher(task)); }
    public static <T> Future<T> async(Callable<T> task) { return Task.pool.submit(Task.catcher(task)); }

    public static     Future<?> delay(Runnable    task, Date time ) { return Task.scheduler.schedule(Task.catcher(task), time); }
    public static <T> Future<T> delay(Callable<T> task, Date time ) { return Task.delay(task, time.getTime() - System.currentTimeMillis()); }
    public static     Future<?> delay(Runnable    task, long delay) { return Task.delay(task, new Date(System.currentTimeMillis() + delay)); }
    public static <T> Future<T> delay(Callable<T> task, long delay) { return Task.scheduler.getScheduledExecutor().schedule(Task.catcher(task), delay, TimeUnit.MILLISECONDS); }

    public static     Future<?> loop(Runnable   task, Date time,  long period) { return Task.scheduler.scheduleAtFixedRate(Task.catcher(task), time, period); }
    public static     Future<?> loop(Runnable   task, long delay, long period) { return Task.loop(task, new Date(System.currentTimeMillis() + delay), period); }
    public static     Future<?> loop(Runnable   task,             long period) { return Task.loop(task, 0, period); }
    public static     Future<?> loop(Runnable   task,             String cron) { return Task.scheduler.schedule(Task.catcher(task), new CronTrigger(cron)); }

}
