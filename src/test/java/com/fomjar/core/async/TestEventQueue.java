package com.fomjar.core.async;

import com.fomjar.core.TestFomjarCoreApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestFomjarCoreApplication.class})
public class TestEventQueue {

    @Autowired
    private EventQueue eq;

    @Test
    public void test() throws InterruptedException {
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

        this.eq.pub("IntegerEvent", 3);
        this.eq.pub("StringEvent", "Hello World!");
        this.eq.pub("ArrayEvent", new Integer[] {1, 2, 3});
        this.eq.pub("ObjectEvent", null);

        Thread.sleep(500L);
    }

}
