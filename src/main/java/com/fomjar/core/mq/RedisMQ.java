package com.fomjar.core.mq;

import com.alibaba.fastjson.JSONObject;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;

import java.util.concurrent.TimeUnit;

/**
 * MQ的Redis实现。
 *
 * @author fomjar
 */
public class RedisMQ extends MQ {

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
     * @param host
     * @param port
     * @param pass
     * @param db
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

        this.initConsumer();

        return this;
    }

    private void initConsumer() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + this.host + ":" + this.port)
                .setPassword(pass)
                .setDatabase(db);
        this.redisson = Redisson.create(config);
        this.codec = new StringCodec();

        this.redisson.getTopic(this.topic(), this.codec).addListener(String.class, (channel, msg) -> {
            super.doConsume(MQMsg.fromString(JSONObject.parse(msg).toString()));
        });
    }

    @Override
    protected boolean lock(String msg) {
        try {return this.redisson.getLock(msg).tryLock(0, 1, TimeUnit.HOURS);}
        catch (InterruptedException e) {e.printStackTrace();}   // never happen
        return false;
    }

    @Override
    protected MQ doProduce(MQMsg msg) {
        this.redisson.getTopic(this.topic(), this.codec).publish(msg.toString());
        return this;
    }

    @Override
    public void shutdown() {
        if (null != this.redisson) this.redisson.shutdown();
        this.redisson = null;
    }
}
