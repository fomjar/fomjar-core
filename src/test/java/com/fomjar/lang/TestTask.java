package com.fomjar.lang;

import org.junit.Test;

import java.util.concurrent.Future;

public class TestTask {

    @Test
    public void testAsync() {
        Task.async(() -> {
            System.out.println(Thread.currentThread().getName() + " : async : " + System.currentTimeMillis());
        });
        Task.async(() -> {
            System.out.println(Thread.currentThread().getName() + " : async : " + System.currentTimeMillis());
            return 0;
        });
    }

    @Test
    public void testDelay() {
        Task.delay(() -> {
            System.out.println(Thread.currentThread().getName() + " : delay : " + System.currentTimeMillis());
        }, 100);
        Task.delay(() -> {
            System.out.println(Thread.currentThread().getName() + " : delay : " + System.currentTimeMillis());
            return 0;
        }, 100);
    }

    @Test
    public void testLoop() throws InterruptedException {
        Future<?> f1 = Task.loop(() -> {
            System.out.println(Thread.currentThread().getName() + " : loop1 : " + System.currentTimeMillis());
        }, 500L);
        Future<?> f2 = Task.loop(() -> {
            System.out.println(Thread.currentThread().getName() + " : loop2 : " + System.currentTimeMillis());
        }, 300L);
        Future<?> f3 = Task.loop(() -> {
            System.out.println(Thread.currentThread().getName() + " : loop3 : " + System.currentTimeMillis());
        }, 100L);

        Thread.sleep(3000L);
        System.out.println("cancel: 2, 3");
        f2.cancel(true);
        f3.cancel(true);
        Thread.sleep(2000L);
        f1.cancel(true);
        System.out.println("cancel: 1");
    }

    @Test
    public void testCron() throws InterruptedException {
        Future<?> f = Task.loop(() -> {
            System.out.println(Thread.currentThread().getName() + " : cron : " + System.currentTimeMillis());
        }, "0/2 * * * * ?");
        Thread.sleep(9000L);
        f.cancel(true);
    }

}
