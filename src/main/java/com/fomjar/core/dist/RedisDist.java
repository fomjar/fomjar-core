package com.fomjar.core.dist;

import org.redisson.Redisson;
import org.redisson.RedissonNode;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.RedissonNodeConfig;

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

}
