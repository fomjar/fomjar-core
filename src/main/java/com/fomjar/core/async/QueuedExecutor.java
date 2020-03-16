package com.fomjar.core.async;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 队列执行器。单个线程复用队列化执行多个任务。
 *
 * @author fomjar
 */
public class QueuedExecutor implements ExecutorService {

    private static final AtomicLong ID = new AtomicLong(0);

    /**
     * 主队列执行器。
     */
    public static final QueuedExecutor main = new QueuedExecutor("main-queue");

    private String name;
    private boolean run;
    private BlockingQueue<FutureTask<?>> queue;
    private FutureTask<?> current;

    public QueuedExecutor() {
        this("queued-executor-" + QueuedExecutor.ID.getAndIncrement());
    }

    public QueuedExecutor(String name) {
        this.name       = name;
        this.run        = false;
        this.queue      = new LinkedBlockingQueue<>();
        this.current    = null;

        this.start();
    }

    private void start() {
        if (!this.isTerminated())
            return;

        this.run = true;
        new Thread(() -> {
            while (this.run || !this.queue.isEmpty()) {

                try {this.current = this.queue.poll(3, TimeUnit.SECONDS);}
                catch (InterruptedException e) {e.printStackTrace();}

                if (null != this.current) {
                    try {this.current.run();}
                    catch (Throwable e) {e.printStackTrace();}
                }

                this.current = null;
            }
        }, this.name).start();
    }

    @Override
    public void shutdown() {
        this.run = false;
    }

    @Override
    public List<Runnable> shutdownNow() {
        this.run = false;
        List<Runnable> tasks = this.queue.stream()
                .map(task -> (Runnable) task).collect(Collectors.toList());
        this.queue.clear();

        if (null != this.current) this.current.cancel(true);

        return tasks;
    }

    @Override
    public boolean isShutdown() {
        return !this.run;
    }

    @Override
    public boolean isTerminated() {
        return !this.run && null == this.current && this.queue.isEmpty();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        long passed = 0;
        long begin = System.currentTimeMillis();

        Future<?> task = null;
        while (!this.queue.isEmpty() || null != (task = this.current)) {
            if (null != task) {
                try {
                    task.get(timeout - passed, unit);
                } catch (ExecutionException | TimeoutException e) {
                    return false;
                }
            }
            passed = System.currentTimeMillis() - begin;
        }

        return true;
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        if (this.isShutdown())
            throw new RejectedExecutionException();

        FutureTask<T> futureTask = new FutureTask<>(task);
        this.queue.offer(futureTask);
        return futureTask;
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        if (this.isShutdown())
            throw new RejectedExecutionException();

        FutureTask<T> futureTask = new FutureTask<>(task, result);
        this.queue.offer(futureTask);
        return futureTask;
    }

    @Override
    public Future<?> submit(Runnable task) {
        if (this.isShutdown())
            throw new RejectedExecutionException();

        FutureTask<?> futureTask = new FutureTask<>(task, null);
        this.queue.offer(futureTask);
        return futureTask;
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return this.invokeAll(tasks, Long.MAX_VALUE, TimeUnit.DAYS);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        List<Future<T>> list = tasks.stream().map(this::submit).collect(Collectors.toList());
        long passed = 0;
        long begin = System.currentTimeMillis();
        for (Future<T> task : list) {
            try {
                task.get(timeout - passed, unit);
            } catch (ExecutionException | TimeoutException e) {
                break;
            }
            passed = System.currentTimeMillis() - begin;
        }
        return list;
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return this.submit(tasks.iterator().next()).get();
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.submit(tasks.iterator().next()).get(timeout, unit);
    }

    @Override
    public void execute(Runnable command) {
        this.submit(command);
    }

}
