package com.fomjar.core.dist;

import com.fomjar.core.TestFomjarCoreApplication;
import com.fomjar.core.el.AviatorEL;
import com.fomjar.core.el.EL;
import com.fomjar.core.spring.Beans;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestFomjarCoreApplication.class})
public class TestDist {

    @Autowired
    private Dist dist;

    @Test
    public void testLoopA() throws InterruptedException {
        String timerName = "loop-a-123";
        this.dist.revoke("loop-a-");
        this.dist.loop((Runnable & Serializable) () ->
                        System.out.println(Thread.currentThread().getName() + "-loop-a-" + System.currentTimeMillis()),
                timerName, 100, 1000, TimeUnit.MILLISECONDS);
        Thread.sleep(1000L);
        this.dist.revoke("loop-a-");
    }

    @Test
    public void testLoopB() throws InterruptedException {
        String timerName = "loop-b-123";
        this.dist.revoke("loop-b-");
        this.dist.loop((Runnable & Serializable) () ->
                        System.out.println(Thread.currentThread().getName() + "-loop-b-" + System.currentTimeMillis()),
                timerName, 100, 1000, TimeUnit.MILLISECONDS);
        Thread.sleep(1000L);
        this.dist.revoke("loop-b-");
    }

    @Test
    public void testLoopC() throws InterruptedException {
        String timerName = "loop-c-123";
        this.dist.revoke("loop-c-");
        this.dist.loop((Runnable & Serializable) () ->
                        System.out.println(Thread.currentThread().getName() + "-loop-c-" + System.currentTimeMillis()),
                timerName, "0/1 * * * * ?");
        Thread.sleep(3000L);
        this.dist.revoke("loop-c-");
    }

    @Test
    public void testLock() throws InterruptedException {
        String name = "123";
        for (int i = 0; i < 3; i++) {
            this.dist.async((Runnable & Serializable) () -> {
                try {
                    Beans.get(Dist.class).lock(() -> {
                        System.out.println(Thread.currentThread().getName() + ": " + System.currentTimeMillis());
                        try {
                            Thread.sleep(100L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }, name,5, TimeUnit.SECONDS);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, name + "-" + i);
        }
        Thread.sleep(2000L);
    }

    @Test
    public void testRPC() throws Exception {
        this.dist.rpc(EL.class, new AviatorEL());
        System.out.println(this.dist.rpc(EL.class).eval("1 + 1"));
    }

}
