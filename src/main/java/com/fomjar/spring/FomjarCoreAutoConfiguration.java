package com.fomjar.spring;

import com.alibaba.fastjson.JSONObject;
import com.fomjar.lang.Anno;
import com.fomjar.lang.AnnoScanAdapter;
import com.fomjar.lang.AnnoScanFilter;
import com.fomjar.lang.Async;
import com.fomjar.lang.Event;
import com.fomjar.lang.Struct;
import com.fomjar.dist.Dist;
import com.fomjar.dist.RedisDist;
import com.fomjar.el.AviatorEL;
import com.fomjar.el.EL;
import com.fomjar.el.FreeMarkerEL;
import com.fomjar.lio.*;
import com.fomjar.lio.annotation.LIOConnect;
import com.fomjar.lio.annotation.LIOController;
import com.fomjar.lio.annotation.LIODisconnect;
import com.fomjar.lio.annotation.LIORequest;
import com.fomjar.mq.MQ;
import com.fomjar.mq.RedisMQ;
import com.fomjar.oss.AliyunOSS;
import com.fomjar.oss.MinioOSS;
import com.fomjar.oss.OSS;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

@Configuration
public class FomjarCoreAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(FomjarCoreAutoConfiguration.class);
    private static final String prefix = "fomjar";

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
    public Event event() {
        return Event.main;
    }

    @Bean
    @Lazy
    public ExecutorService pool() throws NoSuchFieldException, IllegalAccessException {
        return Struct.get(Async.class, ExecutorService.class, "pool");
    }

    @Bean
    @Lazy
    public ThreadPoolTaskScheduler scheduler() throws NoSuchFieldException, IllegalAccessException {
        return Struct.get(Async.class, ThreadPoolTaskScheduler.class, "scheduler");
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

    @Bean
    @ConditionalOnProperty({
            prefix + ".lio.package",
    })
    public LIOServer lioServer() throws Exception {
        logger.info("========");

        String pkg  = Props.get(prefix + ".lio.package");
        String type = ifnull(Props.get(prefix + ".lio.type"), "websocket");
        int    port = ifnull(Props.get(prefix + ".lio.port"), 9001);
        LIOServer server = null;
        switch (type) {
            case "websocket":
                server = new WebSocketLIOServer();
                break;
            case "socket-io":
                server = new SocketIOLIOServer();
                break;
            case "tcp":
                server = new TCPLIOServer();
                break;
            default:
                throw new InvalidPropertyException(String.class, prefix + ".lio.type", "Invalid lio type: " + type);
        }

        LIOServer finalServer = server;
        Anno.scan(pkg, null, AnnoScanFilter.all(LIOController.class), new AnnoScanAdapter() {

            private Map<String, Object> controllers = new HashMap<>();

            @Override
            public void read(Annotation[] annotations, Class<?> clazz) throws Exception {
                this.controllers.putIfAbsent(clazz.getName(), clazz.newInstance());
            }

            @Override
            public void read(Annotation[] annotations, Class<?> clazz, Method method) throws Exception {
                Object controller = this.controllers.get(clazz.getName());
                if (null != Anno.any(annotations, LIORequest.class)) {
                    finalServer.listen(new LIOServerListener() {
                        @Override
                        public void connect(LIO lio) {
                            lio.read((buf, off, len) -> {
                                Object[] parameters = new Object[method.getParameterCount()];
                                for (int i = 0; i < method.getParameterTypes().length; i++) {
                                    if (LIO.class.isAssignableFrom(method.getParameterTypes()[i])) {
                                        parameters[i] = lio;
                                    }
                                    if (byte[].class.isAssignableFrom(method.getParameterTypes()[i])) {
                                        parameters[i] = Arrays.copyOfRange(buf, off, off + len);
                                    }
                                    if (String.class.isAssignableFrom(method.getParameterTypes()[i])) {
                                        parameters[i] = new String(buf, off, len);
                                    }
                                    if (JSONObject.class.isAssignableFrom(method.getParameterTypes()[i])
                                            || Map.class.isAssignableFrom(method.getParameterTypes()[i])) {
                                        parameters[i] = JSONObject.parseObject(new String(buf, off, len));
                                    }
                                }
                                Object result = method.invoke(controller, parameters);
                                if (null == result) {
                                    // Nothing to do.
                                } else if (result instanceof byte[]) {
                                    lio.write((byte[]) result);
                                } else {
                                    lio.write(result.toString());
                                }
                            });
                        }

                        @Override
                        public void disconnect(LIO lio) {
                        }
                    });
                    logger.info("LIO controller scanned: {}.{}()", clazz.getName(), method.getName());
                }
                if (null != Anno.any(annotations, LIOConnect.class)) {
                    finalServer.listen(new LIOServerListener() {
                        @Override
                        public void connect(LIO lio) throws InvocationTargetException, IllegalAccessException {
                            Object[] parameters = new Object[method.getParameterCount()];
                            for (int i = 0; i < method.getParameterTypes().length; i++) {
                                if (LIO.class.isAssignableFrom(method.getParameterTypes()[i])) {
                                    parameters[i] = lio;
                                }
                            }
                            method.invoke(controller, parameters);
                        }

                        @Override
                        public void disconnect(LIO lio) {

                        }
                    });
                }
                if (null != Anno.any(annotations, LIODisconnect.class)) {
                    finalServer.listen(new LIOServerListener() {
                        @Override
                        public void connect(LIO lio) {
                        }

                        @Override
                        public void disconnect(LIO lio) throws InvocationTargetException, IllegalAccessException {
                            Object[] parameters = new Object[method.getParameterCount()];
                            for (int i = 0; i < method.getParameterTypes().length; i++) {
                                if (LIO.class.isAssignableFrom(method.getParameterTypes()[i])) {
                                    parameters[i] = lio;
                                }
                            }
                            method.invoke(controller, parameters);
                        }
                    });
                }
            }
        });

        server.startup(port);
        logger.info("Started LIO ({}) Server at port: {}", type, port);
        logger.info("========");
        return server;
    }

}
