package com.fomjar.core.spring;

import com.fomjar.core.async.Async;
import com.fomjar.core.async.EventQueue;
import com.fomjar.core.async.QueuedExecutor;
import com.fomjar.core.async.SimpleThreadFactory;
import com.fomjar.core.dist.Dist;
import com.fomjar.core.dist.RedisDist;
import com.fomjar.core.el.AviatorEL;
import com.fomjar.core.el.EL;
import com.fomjar.core.el.FreeMarkerEL;
import com.fomjar.core.mq.MQ;
import com.fomjar.core.mq.RedisMQ;
import com.fomjar.core.oss.AliyunOSS;
import com.fomjar.core.oss.MinioOSS;
import com.fomjar.core.oss.OSS;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class FomjarCoreAutoConfiguration {

    private static final String prefix = "fomjar.core";

    @SuppressWarnings("unchecked")
    private static <T> T ifnull(Object t0, T t1) {
        if (null != t0) return (T) t1.getClass().cast(t0);
        return t1;
    }

    @Bean
    @Lazy
    public EL el() {
        String type = ifnull(Props.get(prefix + ".el.type"), "aviator");
        switch (type) {
            case "aviator":
                return new AviatorEL();
            case "freemarker":
                return new FreeMarkerEL();
            default:
                throw new InvalidPropertyException(String.class, prefix + ".el.type", "Invalid el type: " + type);
        }
    }

    @Bean
    @Lazy
    public EventQueue eventQueue() {
        return EventQueue.main;
    }

    @Bean
    @Lazy
    public ExecutorService queue() {
        return QueuedExecutor.main;
    }

    @Bean
    @Lazy
    public ExecutorService pool() {
        return null != Async.pool
                ? Async.pool
                : (Async.pool = Executors.newScheduledThreadPool(
                    ifnull(Props.get(prefix + ".pool.size"), Async.DEFAULT_POOL_SIZE),
                    new SimpleThreadFactory("main-pool")));
    }

    @Bean
    @ConditionalOnProperty({
            prefix + ".redis.host",
    })
    public RedissonClient redisson() {
        String  host    = ifnull(Props.get(prefix + ".redis.host"), "127.0.0.1");
        int     port    = ifnull(Props.get(prefix + ".redis.port"), 6379);
        String  pass    = ifnull(Props.get(prefix + ".redis.pass"), null);
        int     db      = ifnull(Props.get(prefix + ".redis.db"),   0);

        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + host + ":" + port)
                .setPassword(pass)
                .setDatabase(db);
        return Redisson.create(config);
    }

    @Bean
    @ConditionalOnBean(RedissonClient.class)
    public Dist dist() {
        return new RedisDist(Beans.get(RedissonClient.class));
    }

    @Bean
    @ConditionalOnBean(RedissonClient.class)
    @ConditionalOnProperty({
            prefix + ".mq.topic",
            prefix + ".mq.group",
    })
    public MQ mq() {
        String topic = Props.get(prefix + ".mq.topic");
        String group = Props.get(prefix + ".mq.group");
        return new RedisMQ(topic).setup(Beans.get(RedissonClient.class)).group(group);
    }

    @Bean
    @ConditionalOnProperty({
            prefix + ".oss.type",
            prefix + ".oss.ep",
            prefix + ".oss.ak",
            prefix + ".oss.sk",
            prefix + ".oss.bucket",
    })
    public OSS oss() {
        String type = Props.get(prefix + ".oss.type");
        String ep   = Props.get(prefix + ".oss.ep");
        String ak   = Props.get(prefix + ".oss.ak");
        String sk   = Props.get(prefix + ".oss.sk");
        String bucket   = Props.get(prefix + ".oss.bucket");

        switch (type.trim().toLowerCase()) {
            case "aliyun":
                return new AliyunOSS().setup(ep, ak, sk).bucket(bucket);
            case "minio":
                return new MinioOSS().setup(ep, ak, sk).bucket(bucket);
            default:
                throw new InvalidPropertyException(String.class, prefix + ".oss.type", "Invalid oss type: " + type);
        }
    }

}
