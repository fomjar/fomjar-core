package com.fomjar.core.dist;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 分布式工具集。
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
     * @param task
     * @param name
     * @param lease
     * @param unit
     * @throws Exception
     */
    void lock(Runnable task, String name, long lease, TimeUnit unit) throws Exception;

    /**
     * 分布式锁。在锁内执行指定操作。此方法会一直阻塞直到获取锁为止，可以确保执行到方法体。
     *
     * @param task
     * @param name
     * @param lease
     * @param unit
     * @param <T>
     * @return
     * @throws Exception
     */
    <T> T lock(Callable<T> task, String name, long lease, TimeUnit unit) throws Exception;

    /**
     * 分布式锁。在锁内执行指定操作。如果在指定等待时间内没有获取到锁，则不会执行方法体。
     *
     * @param task
     * @param name
     * @param wait
     * @param lease
     * @param unit
     * @throws Exception
     */
    void lock(Runnable task, String name, long wait, long lease, TimeUnit unit) throws Exception;

    /**
     * 分布式锁。在锁内执行指定操作。如果在指定等待时间内没有获取到锁，则不会执行方法体。
     *
     * @param task
     * @param name
     * @param wait
     * @param lease
     * @param unit
     * @param <T>
     * @return
     * @throws Exception
     */
    <T> T lock(Callable<T> task, String name, long wait, long lease, TimeUnit unit) throws Exception;

}
