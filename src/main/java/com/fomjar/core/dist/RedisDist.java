package com.fomjar.core.dist;

import org.redisson.Redisson;
import org.redisson.RedissonNode;
import org.redisson.api.*;
import org.redisson.config.Config;
import org.redisson.config.RedissonNodeConfig;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RedisDist implements Dist {

    private RedissonClient  redissonClient;
    private RedissonNode    redissonNode;

    public RedisDist(String host, int port, String pass, int db) {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + host + ":" + port)
                .setPassword(pass)
                .setDatabase(db);
        this.redissonClient = Redisson.create(config);

        this.setup();
    }

    public RedisDist(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;

        this.setup();
    }

    private void setup() {
        this.redissonNode = RedissonNode.create(new RedissonNodeConfig(this.redissonClient().getConfig()), this.redissonClient());
        this.redissonNode().start();
    }

    public RedissonClient redissonClient() {
        return this.redissonClient;
    }

    public RedissonNode redissonNode() {
        return this.redissonNode;
    }

    private RLock lock(String name) {
        return this.redissonClient().getLock("dist-lock-" + name);
    }

    @Override
    public void unlock(String name) {
        this.lock(name).forceUnlock();
    }

    @Override
    public void lock(Runnable task, String name, long lease, TimeUnit unit) throws Exception {
        RLock lock = this.lock(name);
        try {
            lock.lock(lease, unit);
            try     {task.run();}
            catch   (Exception e) {throw e;}
        } finally {
            lock.unlock();
        }
    }

    @Override
    public <T> T lock(Callable<T> task, String name, long lease, TimeUnit unit) throws Exception {
        RLock lock = this.lock(name);
        try {
            lock.lock(lease, unit);
            try     {return task.call();}
            catch   (Exception e) {throw e;}
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void lock(Runnable task, String name, long wait, long lease, TimeUnit unit) throws Exception {
        RLock lock = this.lock(name);
        try {
            if (lock.tryLock(wait, lease, unit)) {
                try     {
                    task.run();}
                catch   (Exception e) {throw e;}
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public <T> T lock(Callable<T> task, String name, long wait, long lease, TimeUnit unit) throws Exception {
        RLock lock = this.lock(name);
        try {
            if (lock.tryLock(wait, lease, unit)) {
                try     {return task.call();}
                catch   (Exception e) {throw e;}
            }
            return null;
        } finally {
            lock.unlock();
        }
    }

    private static final String TASK_MAP = "dist-task";

    private RScheduledExecutorService task = null;
    private RScheduledExecutorService task() {
        if (null == this.task) {
            synchronized (this) {
                if (null == this.task) {
                    this.task = this.redissonClient().getExecutorService(RedisDist.TASK_MAP);
                    this.task.registerWorkers(Integer.MAX_VALUE / 2);
                }
            }
        }
        return this.task;
    }
    private RMap<String, String> tasks() {
        return this.redissonClient().getMap(RedisDist.TASK_MAP);
    }
    private void addTask(String name, String task) {
        this.tasks().put(name, task);
    }
    private void delTask(String name) {
        String task = this.tasks().remove(name);
        if (null != task)
            this.task().cancelTask(task);
    }

    @Override
    public void revoke(String task) {
        Pattern pattern = Pattern.compile(task);
        this.tasks().entrySet()
                .stream()
                .map(Map.Entry::getKey)
                .filter(n -> pattern.matcher(n).find())
                .collect(Collectors.toList())
                .forEach(this::delTask);
    }

    @Override
    public Future<?> loop(Runnable task, String name, String cron) {
        try {
            return this.lock((Callable<Future<?>>) () -> {
                this.delTask(name);
                RScheduledFuture<?> future = this.task().scheduleAsync(task, CronSchedule.of(cron));
                this.addTask(name, future.getTaskId());
                return future;
            }, name, 1, TimeUnit.MINUTES);
        } catch (Exception e) {e.printStackTrace();}
        return null;
    }

    @Override
    public Future<?> loop(Runnable task, String name, long delay, long period, TimeUnit unit) {
        try {
            return this.lock((Callable<Future<?>>) () -> {
                this.delTask(name);
                RScheduledFuture<?> future = this.task().scheduleAtFixedRateAsync(task, delay, period, unit);
                this.addTask(name, future.getTaskId());
                return future;
            }, name, 1, TimeUnit.MINUTES);
        } catch (Exception e) {e.printStackTrace();}
        return null;
    }

    @Override
    public Future<?> delay(Runnable task, String name, long delay, TimeUnit unit) {
        try {
            return this.lock((Callable<Future<?>>) () -> {
                this.delTask(name);
                RScheduledFuture<?> future = this.task().scheduleAsync(task, delay, unit);
                this.addTask(name, future.getTaskId());
                return future;
            }, name, 1, TimeUnit.MINUTES);
        } catch (Exception e) {e.printStackTrace();}
        return null;
    }

    @Override
    public <T> Future<T> delay(Callable<T> task, String name, long delay, TimeUnit unit) {
        try {
            return this.lock((Callable<Future<T>>) () -> {
                this.delTask(name);
                RScheduledFuture<T> future = this.task().scheduleAsync(task, delay, unit);
                this.addTask(name, future.getTaskId());
                return future;
            }, name, 1, TimeUnit.MINUTES);
        } catch (Exception e) {e.printStackTrace();}
        return null;
    }

    @Override
    public <T> void rpc(Class<T> type, T service) {
        this.redissonClient().getRemoteService().register(type, service, Integer.MAX_VALUE);
    }

    @Override
    public <T> T rpc(Class<T> type) {
        return this.redissonClient().getRemoteService().get(type);
    }

}
