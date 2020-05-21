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
     * @param name 锁名
     */
    void unlock(String name);

    /**
     * 分布式锁。在锁内执行指定操作。此方法会一直阻塞直到获取锁为止，可以确保执行到方法体。
     *
     * @param task 待执行的任务
     * @param name 锁名
     * @param hold 锁定持续最长时间，一般设定远大于任务执行时长，任务提前结束将提前释放锁，单位毫秒
     */
    default void lock(Runnable task, String name, long hold) {
        this.lock(task, name, hold, TimeUnit.MILLISECONDS);
    }

    /**
     * 分布式锁。在锁内执行指定操作。此方法会一直阻塞直到获取锁为止，可以确保执行到方法体。
     *
     * @param task 待执行的任务
     * @param name 锁名
     * @param hold 锁定持续最长时间，一般设定远大于任务执行时长，任务提前结束将提前释放锁
     * @param unit 时间单位
     */
    void lock(Runnable task, String name, long hold, TimeUnit unit);

    /**
     * 分布式锁。在锁内执行指定操作。此方法会一直阻塞直到获取锁为止，可以确保执行到方法体。
     *
     * @param task 待执行的任务
     * @param name 锁名
     * @param hold 锁定持续最长时间，一般设定远大于任务执行时长，任务提前结束将提前释放锁，单位毫秒
     * @param <T> 任务执行结果的返回类型
     * @return 任务的执行结果
     */
    default <T> T lock(Callable<T> task, String name, long hold) {
        return this.lock(task, name, hold, TimeUnit.MILLISECONDS);
    }

    /**
     * 分布式锁。在锁内执行指定操作。此方法会一直阻塞直到获取锁为止，可以确保执行到方法体。
     *
     * @param task 待执行的任务
     * @param name 锁名
     * @param hold 锁定持续最长时间，一般设定远大于任务执行时长，任务提前结束将提前释放锁
     * @param unit 时间单位
     * @param <T> 任务执行结果的返回类型
     * @return 任务的执行结果
     */
    <T> T lock(Callable<T> task, String name, long hold, TimeUnit unit);

    /**
     * 分布式锁。在锁内执行指定操作。如果在指定等待时间内没有获取到锁，则不会执行方法体。
     *
     * @param task 待执行的任务
     * @param name 锁名
     * @param wait 等待获取锁的最长时间
     * @param hold 锁定持续最长时间，一般设定远大于任务执行时长，任务提前结束将提前释放锁
     */
    default void lock(Runnable task, String name, long wait, long hold) {
        this.lock(task, name, wait, hold, TimeUnit.MILLISECONDS);
    }

    /**
     * 分布式锁。在锁内执行指定操作。如果在指定等待时间内没有获取到锁，则不会执行方法体。
     *
     * @param task 待执行的任务
     * @param name 锁名
     * @param wait 等待获取锁的最长时间
     * @param hold 锁定持续最长时间，一般设定远大于任务执行时长，任务提前结束将提前释放锁
     * @param unit 时间单位
     */
    void lock(Runnable task, String name, long wait, long hold, TimeUnit unit);

    /**
     * 分布式锁。在锁内执行指定操作。如果在指定等待时间内没有获取到锁，则不会执行方法体。
     *
     * @param task 待执行的任务
     * @param name 锁名
     * @param wait 等待获取锁的最长时间
     * @param hold 锁定持续最长时间，一般设定远大于任务执行时长，任务提前结束将提前释放锁
     * @param <T> 任务执行结果的返回类型
     * @return 任务的执行结果
     */
    default <T> T lock(Callable<T> task, String name, long wait, long hold) {
        return this.lock(task, name, wait, hold, TimeUnit.MILLISECONDS);
    }

    /**
     * 分布式锁。在锁内执行指定操作。如果在指定等待时间内没有获取到锁，则不会执行方法体。
     *
     * @param task 待执行的任务
     * @param name 锁名
     * @param wait 等待获取锁的最长时间
     * @param hold 锁定持续最长时间，一般设定远大于任务执行时长，任务提前结束将提前释放锁
     * @param unit 时间单位
     * @param <T> 任务执行结果的返回类型
     * @return 任务的执行结果
     */
    <T> T lock(Callable<T> task, String name, long wait, long hold, TimeUnit unit);

}
