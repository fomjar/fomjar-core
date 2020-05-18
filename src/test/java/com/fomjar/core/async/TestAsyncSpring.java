package com.fomjar.core.async;

import com.fomjar.core.TestFomjarCoreApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestFomjarCoreApplication.class})
public class TestAsyncSpring {

    @Autowired
    private EventQueue eventQueue;

    @Test
    public void testEventQueue() throws InterruptedException {
        this.eventQueue.sub("IntegerEvent", (String name, Integer data) -> {
            System.out.println(Thread.currentThread().getName() + ": " + name + "-" + data);
        });
        this.eventQueue.sub("StringEvent", (String name, String data) -> {
            System.out.println(Thread.currentThread().getName() + ": " + name + "-" + data);
        });
        this.eventQueue.sub("ArrayEvent", (String name, Integer[] data) -> {
            System.out.println(Thread.currentThread().getName() + ": " + name + "-" + data);
        });
        this.eventQueue.sub("ObjectEvent", (String name, Object data) -> {
            System.out.println(Thread.currentThread().getName() + ": " + name + "-" + data);
        });

        for (int i = 0; i < 2; i++) {
            this.eventQueue.pub("IntegerEvent", 3);
            this.eventQueue.pub("StringEvent", "Hello World!");
            this.eventQueue.pub("ArrayEvent", new Integer[]{1, 2, 3});
            this.eventQueue.pub("ObjectEvent", null);
        }

        Thread.sleep(1000L);
    }

}
