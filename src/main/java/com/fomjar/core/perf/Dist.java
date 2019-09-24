package com.fomjar.core.perf;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 分布式支持。
 *
 * @author fomjar
 */
public interface Dist {

    /**
     * 强制解锁。注意：此方法会解其他线程/进程加的锁。
     *
     * @param name
     */
    void unlock(String name);

    /**
     * 分布式锁。在锁内执行指定操作。此方法会一直阻塞直到获取锁为止，可以确保执行到方法体。
     *
     * @param runnable
     * @param name
     * @param lease
     * @param unit
     * @throws Exception
     */
    void lock(Runnable runnable, String name, long lease, TimeUnit unit) throws Exception;

    /**
     * 分布式锁。在锁内执行指定操作。此方法会一直阻塞直到获取锁为止，可以确保执行到方法体。
     *
     * @param callable
     * @param name
     * @param lease
     * @param unit
     * @param <T>
     * @return
     * @throws Exception
     */
    <T> T lock(Callable<T> callable, String name, long lease, TimeUnit unit) throws Exception;

    /**
     * 分布式锁。在锁内执行指定操作。如果在指定等待时间内没有获取到锁，则不会执行方法体。
     *
     * @param runnable
     * @param name
     * @param wait
     * @param lease
     * @param unit
     * @throws Exception
     */
    void lock(Runnable runnable, String name, long wait, long lease, TimeUnit unit) throws Exception;

    /**
     * 分布式锁。在锁内执行指定操作。如果在指定等待时间内没有获取到锁，则不会执行方法体。
     *
     * @param callable
     * @param name
     * @param wait
     * @param lease
     * @param unit
     * @param <T>
     * @return
     * @throws Exception
     */
    <T> T lock(Callable<T> callable, String name, long wait, long lease, TimeUnit unit) throws Exception;

    /**
     * 终止调度任务。包括循环任务、延迟任务、异步任务。
     *
     * @param pattern
     */
    void revoke(String pattern);

    /**
     * 分布式定时任务。一个任务名称只运行一个调度任务，后启动的任务会覆盖之前启动的同名任务。请确保每个独立的任务拥有独立唯一的名称。
     *
     * @param runnable
     * @param name
     * @param cron
     * @return
     */
    Future<?> loop(Runnable runnable, String name, String cron);

    /**
     * 分布式循环任务。一个任务名称只运行一个调度任务，后启动的任务会覆盖之前启动的同名任务。请确保每个独立的任务拥有独立唯一的名称。
     *
     * @param runnable
     * @param name
     * @param delay
     * @param period
     * @param unit
     * @return
     */
    Future<?> loop(Runnable runnable, String name, long delay, long period, TimeUnit unit);

    /**
     * 分布式异步任务。
     *
     * @param runnable
     * @param name
     * @param delay
     * @param unit
     * @return
     */
    Future<?> delay(Runnable runnable, String name, long delay, TimeUnit unit);

    /**
     * 分布式异步任务。
     *
     * @param callable
     * @param delay
     * @param name
     * @param unit
     * @param <T>
     * @return
     */
    <T> Future<T> delay(Callable<T> callable, String name, long delay, TimeUnit unit);

    /**
     * 分布式异步任务。
     *
     * @param runnable
     * @param name
     * @return
     */
    default Future<?> async(Runnable runnable, String name) {
        return this.delay(runnable, name, 0, TimeUnit.SECONDS);
    }

    /**
     * 分布式异步任务。
     *
     * @param callable
     * @param name
     * @param <T>
     * @return
     */
    default <T> Future<T> async(Callable<T> callable, String name) {
        return this.delay(callable, name, 0, TimeUnit.SECONDS);
    }

    /**
     * RPC服务注册。
     *
     * @param type
     * @param service
     * @param <T>
     */
    <T> void rpc(Class<T> type, T service);

    /**
     * RPC服务获取。
     *
     * @param type
     * @param <T>
     * @return
     */
    <T> T rpc(Class<T> type);

}
