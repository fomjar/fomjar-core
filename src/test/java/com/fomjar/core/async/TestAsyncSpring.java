package com.fomjar.core.async;

import com.fomjar.core.TestFomjarCoreApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestFomjarCoreApplication.class})
public class TestAsyncSpring {

    @Autowired
    private EventQueue eq;

    @Autowired
    private ExecutorService queue;

    @Autowired
    private ExecutorService pool;

    @Test
    public void testEvent() throws InterruptedException {
        this.eq.sub("IntegerEvent", (String name, Integer data) -> {
            System.out.println(Thread.currentThread().getName() + ": " + name + "-" + data);
        });
        this.eq.sub("StringEvent", (String name, String data) -> {
            System.out.println(Thread.currentThread().getName() + ": " + name + "-" + data);
        });
        this.eq.sub("ArrayEvent", (String name, Integer[] data) -> {
            System.out.println(Thread.currentThread().getName() + ": " + name + "-" + data);
        });
        this.eq.sub("ObjectEvent", (String name, Object data) -> {
            System.out.println(Thread.currentThread().getName() + ": " + name + "-" + data);
        });

        for (int i = 0; i < 2; i++) {
            this.eq.pub("IntegerEvent", 3);
            this.eq.pub("StringEvent", "Hello World!");
            this.eq.pub("ArrayEvent", new Integer[]{1, 2, 3});
            this.eq.pub("ObjectEvent", null);
        }

        Thread.sleep(1000L);
    }

    @Test
    public void testQueue() throws InterruptedException {
        for (int i = 0; i < 8; i++) {
            final int n = i;
            this.queue.submit(() -> {
                try {Thread.sleep(100L);}
                catch (InterruptedException e) {e.printStackTrace();}
                System.out.println(Thread.currentThread().getName() + ": " + n);
            });
        }
        this.queue.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    }

    @Test
    public void testPool() throws InterruptedException {
        for (int i = 0; i < 8; i++) {
            final int n = i;
            this.pool.submit(() -> {
                try {Thread.sleep(100L);}
                catch (InterruptedException e) {e.printStackTrace();}
                System.out.println(Thread.currentThread().getName() + ": " + n);
            });
        }
        Thread.sleep(1000L);
    }

}