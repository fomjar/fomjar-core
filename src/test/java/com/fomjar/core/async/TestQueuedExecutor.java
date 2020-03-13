package com.fomjar.core.async;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class TestQueuedExecutor {

    @Test
    public void testNew() throws InterruptedException {
        QueuedExecutor executor = new QueuedExecutor();
        for (int i = 0; i < 5; i++) {
            final int n = i;
            executor.submit(() -> {
                try {Thread.sleep(100L);}
                catch (InterruptedException e) {e.printStackTrace();}
                System.out.println("task-" + n);
            });
        }
        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    }

    @Test
    public void testMain() throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            final int n = i;
            QueuedExecutor.main.submit(() -> {
                try {Thread.sleep(100L);}
                catch (InterruptedException e) {e.printStackTrace();}
                System.out.println("task-" + n);
            });
        }
        QueuedExecutor.main.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    }

}
