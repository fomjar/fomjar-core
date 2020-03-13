package com.fomjar.core.eq;

import org.junit.Test;

public class TestEventQueue {

    @Test
    public void test() throws InterruptedException {

        EventQueue.main.sub("IntegerEvent", (String name, Integer data) -> {
            System.out.println(Thread.currentThread().getName() + ": " + name + "-" + data);
        });
        EventQueue.main.sub("StringEvent", (String name, String data) -> {
            System.out.println(Thread.currentThread().getName() + ": " + name + "-" + data);
        });
        EventQueue.main.sub("ArrayEvent", (String name, Integer[] data) -> {
            System.out.println(Thread.currentThread().getName() + ": " + name + "-" + data);
        });
        EventQueue.main.sub("ObjectEvent", (String name, Object data) -> {
            System.out.println(Thread.currentThread().getName() + ": " + name + "-" + data);
        });

        EventQueue.main.pub("IntegerEvent", 3);
        EventQueue.main.pub("StringEvent", "Hello World!");
        EventQueue.main.pub("ArrayEvent", new Integer[] {1, 2, 3});
        EventQueue.main.pub("ObjectEvent", null);

        Thread.sleep(500L);

    }

}
