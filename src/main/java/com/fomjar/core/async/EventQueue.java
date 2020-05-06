package com.fomjar.core.async;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 事件队列。方便地发布/订阅自定义事件。<br>
 *
 * <b>不支持分布式！</b>
 *
 * @author fomjar
 */
public class EventQueue {

    private static final AtomicLong ID = new AtomicLong(0);

    /**
     * 主事件队列。
     */
    public static final EventQueue main = new EventQueue("main-event-queue");

    private Map<String, List<EventListener<?>>> listeners;
    private ReadWriteLock   lock;
    private ExecutorService executor;

    public EventQueue() {
        this("event-queue-" + EventQueue.ID.getAndIncrement());
    }

    public EventQueue(String name) {
        this(new QueuedExecutor(name));
    }

    public EventQueue(ExecutorService executor) {
        this.listeners  = new HashMap<>();
        this.lock       = new ReentrantReadWriteLock(true);
        this.executor   = executor;
    }

    @SuppressWarnings("unchecked")
    public <T> void pub(String event, T data) {
        this.executor.submit(() -> {
            Lock lock = this.lock.readLock();
            try {
                lock.lock();
                if (!this.listeners.containsKey(event)) return;
                this.listeners.get(event).forEach(listener -> {
                    try {((EventListener<T>) listener).on(event, data);}
                    catch (Exception e) {e.printStackTrace();}
                });
            } finally {
                lock.unlock();
            }
        });
    }

    public void sub(String event, EventListener<?> listener) {
        Lock lock = this.lock.writeLock();
        try {
            lock.lock();
            this.listeners.putIfAbsent(event, new LinkedList<>());
            this.listeners.get(event).add(listener);
        } finally {
            lock.unlock();
        }
    }

    public void close() {
        this.executor.shutdown();
    }

}
