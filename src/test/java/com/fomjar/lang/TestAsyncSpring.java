package com.fomjar.lang;

import com.fomjar.TestFomjarCoreApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestFomjarCoreApplication.class})
public class TestAsyncSpring {

    @Autowired
    private Event event;

    @Test
    public void testEventDispatcher() throws InterruptedException {
        this.event.sub("IntegerEvent", (String name, Integer data) -> {
            System.out.println(Thread.currentThread().getName() + ": " + name + "-" + data);
        });
        this.event.sub("StringEvent", (String name, String data) -> {
            System.out.println(Thread.currentThread().getName() + ": " + name + "-" + data);
        });
        this.event.sub("ArrayEvent", (String name, Integer[] data) -> {
            System.out.println(Thread.currentThread().getName() + ": " + name + "-" + data);
        });
        this.event.sub("ObjectEvent", (String name, Object data) -> {
            System.out.println(Thread.currentThread().getName() + ": " + name + "-" + data);
        });

        for (int i = 0; i < 2; i++) {
            this.event.pub("IntegerEvent", 3);
            this.event.pub("StringEvent", "Hello World!");
            this.event.pub("ArrayEvent", new Integer[]{1, 2, 3});
            this.event.pub("ObjectEvent", null);
        }

        Thread.sleep(1000L);
    }

}
