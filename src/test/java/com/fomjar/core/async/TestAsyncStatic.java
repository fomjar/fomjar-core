package com.fomjar.core.async;

import org.junit.Test;

import static com.fomjar.core.async.Async.*;

public class TestAsyncStatic {

    @Test
    public void test() {
        async(() -> {
            System.out.println(Thread.currentThread().getName() + ": " + System.currentTimeMillis());
        });
        async(() -> {
            System.out.println(Thread.currentThread().getName() + ": " + System.currentTimeMillis());
            return 0;
        });
        queue(() -> {
            System.out.println(Thread.currentThread().getName() + ": " + System.currentTimeMillis());
        });
        queue(() -> {
            System.out.println(Thread.currentThread().getName() + ": " + System.currentTimeMillis());
            return 0;
        });
        pool(() -> {
            System.out.println(Thread.currentThread().getName() + ": " + System.currentTimeMillis());
        });
        pool(() -> {
            System.out.println(Thread.currentThread().getName() + ": " + System.currentTimeMillis());
            return 0;
        });
    }

    @Test
    public void testTimer() throws InterruptedException {
        delay(() -> {
            System.out.println(Thread.currentThread().getName() + ": " + System.currentTimeMillis());
        }, 1000L);
        loop(() -> {
            System.out.println(Thread.currentThread().getName() + ": " + System.currentTimeMillis());
        }, 1000, 500L);
        Thread.sleep(5000L);
    }

}
