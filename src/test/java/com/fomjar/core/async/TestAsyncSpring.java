package com.fomjar.core.async;

import com.fomjar.core.TestFomjarCoreApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestFomjarCoreApplication.class})
public class TestAsyncSpring {

    @Autowired
    private EventDispatcher eventDispatcher;

    @Test
    public void testEventDispatcher() throws InterruptedException {
        this.eventDispatcher.sub("IntegerEvent", (String name, Integer data) -> {
            System.out.println(Thread.currentThread().getName() + ": " + name + "-" + data);
        });
        this.eventDispatcher.sub("StringEvent", (String name, String data) -> {
            System.out.println(Thread.currentThread().getName() + ": " + name + "-" + data);
        });
        this.eventDispatcher.sub("ArrayEvent", (String name, Integer[] data) -> {
            System.out.println(Thread.currentThread().getName() + ": " + name + "-" + data);
        });
        this.eventDispatcher.sub("ObjectEvent", (String name, Object data) -> {
            System.out.println(Thread.currentThread().getName() + ": " + name + "-" + data);
        });

        for (int i = 0; i < 2; i++) {
            this.eventDispatcher.pub("IntegerEvent", 3);
            this.eventDispatcher.pub("StringEvent", "Hello World!");
            this.eventDispatcher.pub("ArrayEvent", new Integer[]{1, 2, 3});
            this.eventDispatcher.pub("ObjectEvent", null);
        }

        Thread.sleep(1000L);
    }

}
