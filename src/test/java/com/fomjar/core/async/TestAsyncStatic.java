package com.fomjar.core.async;

import org.junit.Test;

import java.util.concurrent.Future;

import static com.fomjar.core.async.Async.*;

public class TestAsyncStatic {

    @Test
    public void testAsync() {
        async(() -> {
            System.out.println(Thread.currentThread().getName() + " : async : " + System.currentTimeMillis());
        });
        async(() -> {
            System.out.println(Thread.currentThread().getName() + " : async : " + System.currentTimeMillis());
            return 0;
        });
    }

    @Test
    public void testDelay() {
        delay(() -> {
            System.out.println(Thread.currentThread().getName() + " : delay : " + System.currentTimeMillis());
        }, 100);
        delay(() -> {
            System.out.println(Thread.currentThread().getName() + " : delay : " + System.currentTimeMillis());
            return 0;
        }, 100);
    }

    @Test
    public void testLoop() throws InterruptedException {
        Future<?> f1 = loop(() -> {
            System.out.println(Thread.currentThread().getName() + " : loop1 : " + System.currentTimeMillis());
        }, 500L);
        Future<?> f2 = loop(() -> {
            System.out.println(Thread.currentThread().getName() + " : loop2 : " + System.currentTimeMillis());
        }, 300L);
        Future<?> f3 = loop(() -> {
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
        Future<?> f = loop(() -> {
            System.out.println(Thread.currentThread().getName() + " : cron : " + System.currentTimeMillis());
        }, "0/2 * * * * ?");
        Thread.sleep(9000L);
        f.cancel(true);
    }

}
