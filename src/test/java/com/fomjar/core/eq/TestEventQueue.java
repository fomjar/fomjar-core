package com.fomjar.core.eq;

import org.junit.Test;

public class TestEventQueue {

    @Test
    public void test() throws InterruptedException {

        EventQueue.main.sub("Integer", (String name, Integer data) -> {
            System.out.println(Thread.currentThread().getName() + ": " + name + "-" + data);
        });
        EventQueue.main.sub("String", (String name, String data) -> {
            System.out.println(Thread.currentThread().getName() + ": " + name + "-" + data);
        });
        EventQueue.main.sub("Array", (String name, Integer[] data) -> {
            System.out.println(Thread.currentThread().getName() + ": " + name + "-" + data);
        });
        EventQueue.main.sub("Object", (String name, Object data) -> {
            System.out.println(Thread.currentThread().getName() + ": " + name + "-" + data);
        });

        EventQueue.main.pub("Integer", 3);
        EventQueue.main.pub("String", "Hello World!");
        EventQueue.main.pub("Array", new Integer[] {1, 2, 3});
        EventQueue.main.pub("Object", null);

        Thread.sleep(500L);

    }

}
