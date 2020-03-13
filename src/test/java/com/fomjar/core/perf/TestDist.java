package com.fomjar.core.perf;

import ch.qos.logback.classic.Level;
import com.fomjar.core.el.AviatorEL;
import com.fomjar.core.el.EL;
import com.fomjar.core.log.Logs;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class TestDist {

    private static Dist dist = null;

    public static synchronized Dist dist() {
        if (null == dist)
            dist = new RedissonDist("127.0.0.1", 6379, null, 0);
        return dist;
    }

    @BeforeClass
    public static void setup() {
        Logs.level(Level.INFO);
    }

    @Test
    public void testLoopA() throws InterruptedException {
        String timerName = "loop-a-123";
        dist().revoke("loop-a-");
        dist().loop((Runnable & Serializable) () ->
                System.out.println("loop-a-" + System.currentTimeMillis()),
                timerName, 1, 1, TimeUnit.SECONDS);
        Thread.sleep(10000);
        dist().revoke("loop-a");
    }

    @Test
    public void testLoopB() throws InterruptedException {
        String timerName = "loop-b-123";
        dist().revoke("loop-b-");
        dist().loop((Runnable & Serializable) () ->
                        System.out.println("loop-b-" + System.currentTimeMillis()),
                timerName, 1, 1, TimeUnit.SECONDS);
        Thread.sleep(10000);
        dist().revoke("loop-b");
    }

    @Test
    public void testLoopC() throws InterruptedException {
        String timerName = "loop-c-123";
        dist().revoke("loop-c-");
        dist().loop((Runnable & Serializable) () ->
                        System.out.println("loop-c-" + System.currentTimeMillis()),
                timerName, "0/1 * * * * ?");
        Thread.sleep(10000);
        dist().revoke("loop-c");
    }

//    @Test
    public void testRPC() throws Exception {
        dist().rpc(EL.class, new AviatorEL());
        System.out.println(dist().rpc(EL.class).eval("1 + 1"));
    }

}
