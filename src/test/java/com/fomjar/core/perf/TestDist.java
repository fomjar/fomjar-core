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
            dist = new RedissonDist("172.16.23.223", 6382, "zetsoft6382", 2);
        return dist;
    }

//    @BeforeClass
    public static void setup() {
        Logs.level(Level.INFO);
    }

//    @Test
    public void testLoop1() throws InterruptedException {
        String timerName = "some-loop";
        dist().loop((Runnable & Serializable) () ->
                System.out.println("loop-1-" + System.currentTimeMillis()),
                timerName, 1, 1, TimeUnit.SECONDS);
        Thread.sleep(300000);
        dist().revoke(timerName);
    }

//    @Test
    public void testLoop2() throws InterruptedException {
        String timerName = "some-loop";
        dist().loop((Runnable & Serializable) () ->
                System.out.println("loop-2-" + System.currentTimeMillis()),
                timerName, 1, 1, TimeUnit.SECONDS);
        Thread.sleep(3000);
        dist().revoke(timerName);
    }

//    @Test
    public void testAyncAndLock() throws InterruptedException {
        String lockName = "same-lock";
        String taskName = "some-task";
        dist().unlock(lockName);
        for (int i = 0; i < 3; i++) {
            dist().async((Runnable & Serializable) () -> {
                try {
                    dist().lock(() -> {
                        System.out.println("== before ==");
                        try {Thread.sleep(1000L);}
                        catch (InterruptedException e) {e.printStackTrace();}
                        System.out.println(System.currentTimeMillis());
                        System.out.println("==  end   ==");
                    }, lockName, 10, TimeUnit.HOURS);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, taskName);
        }
        Thread.sleep(4 * 1000L);
        dist().unlock(lockName);
    }

//    @Test
    public void testRPC() throws Exception {
        dist().rpc(EL.class, new AviatorEL());
        System.out.println(dist().rpc(EL.class).eval("1 + 1"));
    }

}
