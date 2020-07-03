package com.fomjar.dist;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class RedisDist extends AbstractDist {

    private static final Logger logger = LoggerFactory.getLogger(RedisDist.class);

    private RedissonClient  redissonClient;

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

    private RLock getLock(String name) { return this.redissonClient.getLock("dist-lock-" + name); }

    @Override
    public void unlock(String name) { this.getLock(name).forceUnlock(); }

    @Override
    public boolean lock(String name, long wait, long hold, TimeUnit unit) {
        try { return this.getLock(name).tryLock(wait, hold, unit); }
        catch (InterruptedException e) { logger.warn("Lock({}) was interrupted.", name, e); }
        return false;
    }

}
