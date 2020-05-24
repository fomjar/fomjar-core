package com.fomjar.dist;

import org.redisson.Redisson;
import org.redisson.RedissonNode;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

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
    }

    public RedisDist(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
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
    public void lock(Runnable task, String name, long hold, TimeUnit unit) {
        RLock lock = this.lock(name);
        try {
            lock.lock(hold, unit);
            task.run();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public <T> T lock(Callable<T> task, String name, long hold, TimeUnit unit) {
        RLock lock = this.lock(name);
        try {
            lock.lock(hold, unit);
            try     {return task.call();}
            catch   (Exception e) {throw new RuntimeException(e);}
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void lock(Runnable task, String name, long wait, long hold, TimeUnit unit) {
        RLock lock = this.lock(name);
        try {
            if (lock.tryLock(wait, hold, unit))
                task.run();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public <T> T lock(Callable<T> task, String name, long wait, long hold, TimeUnit unit) {
        RLock lock = this.lock(name);
        try {
            if (lock.tryLock(wait, hold, unit))
                return task.call();
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

}
