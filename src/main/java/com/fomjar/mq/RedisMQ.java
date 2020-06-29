package com.fomjar.mq;

import com.alibaba.fastjson.JSONObject;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * MQ的Redis实现。
 *
 * @author fomjar
 */
public class RedisMQ extends MQ {

    private static final Logger logger = LoggerFactory.getLogger(RedisMQ.class);

    /** redis主机 */
    private String  host;
    /** redis端口 */
    private int     port;
    /** redis密码 */
    private String  pass;
    /** redis库号 */
    private int     db;
    /** redis操作对象 */
    private RedissonClient  redisson;
    /** 解码器 */
    private Codec   codec;

    public RedisMQ(String topic) {super(topic);}

    /**
     * 初始化RedisMQ配置。
     *
     * @param host Redis主机
     * @param port Redis端口
     * @param pass Redis密码
     * @param db Redis库
     * @return 此MQ对象
     */
    public RedisMQ setup(
            String  host,
            int     port,
            String  pass,
            int     db) {

        this.shutdown();

        this.host   = host;
        this.port   = port;
        this.pass   = pass;
        this.db     = db;

        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + this.host + ":" + this.port)
                .setPassword(pass)
                .setDatabase(db);
        this.redisson = Redisson.create(config);

        this.initConsumer();

        return this;
    }

    public RedisMQ setup(RedissonClient redissonClient) {
        this.redisson = redissonClient;

        this.initConsumer();

        return this;
    }

    private void initConsumer() {
        this.codec = new StringCodec();

        this.redisson.getTopic(this.topic(), this.codec).addListener(String.class, (channel, msg) -> {
            super.doConsume(MQMsg.fromString(JSONObject.parse(msg).toString()));
        });
    }

    @Override
    protected boolean lock(String msg) {
        try { return this.redisson.getLock(msg).tryLock(0, 1, TimeUnit.HOURS); }
        catch (InterruptedException e) { logger.warn("Message lock was interrupted", e); }   // never happen
        return false;
    }

    @Override
    protected void doProduce(MQMsg msg) {
        this.redisson.getTopic(this.topic(), this.codec).publish(msg.toString());
    }

    @Override
    public void shutdown() {
        if (null != this.redisson) this.redisson.shutdown();
        this.redisson = null;
    }
}
