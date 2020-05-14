package com.fomjar.core.spring;

import com.alibaba.fastjson.JSONObject;
import com.fomjar.core.anno.Anno;
import com.fomjar.core.anno.AnnoScanAdapter;
import com.fomjar.core.anno.AnnoScanFilter;
import com.fomjar.core.async.Async;
import com.fomjar.core.async.EventQueue;
import com.fomjar.core.async.QueuedExecutor;
import com.fomjar.core.async.SimpleThreadFactory;
import com.fomjar.core.dist.Dist;
import com.fomjar.core.dist.RedisDist;
import com.fomjar.core.el.AviatorEL;
import com.fomjar.core.el.EL;
import com.fomjar.core.el.FreeMarkerEL;
import com.fomjar.core.lio.*;
import com.fomjar.core.lio.annotation.LIOConnect;
import com.fomjar.core.lio.annotation.LIOController;
import com.fomjar.core.lio.annotation.LIODisconnect;
import com.fomjar.core.lio.annotation.LIORequest;
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

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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

    @Bean
    @ConditionalOnProperty({
            prefix + ".lio.package",
    })
    public LIOServer lioServer() throws Exception {
        System.out.println("========");

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
                if (!this.controllers.containsKey(clazz.getSimpleName()))
                    this.controllers.put(clazz.getSimpleName(), clazz.newInstance());
            }

            @Override
            public void read(Annotation[] annotations, Class<?> clazz, Method method) throws Exception {
                Object controller = this.controllers.get(clazz.getSimpleName());
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
                    System.out.println(String.format("LIO controller scanned: %s.%s", clazz.getSimpleName(), method.getName()));
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
        System.out.println(String.format("Started LIO (%s) Server at port: %d", type, port));
        System.out.println("========");
        return server;
    }

}
