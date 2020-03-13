package com.fomjar.core.async;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

    /**
     * 事件监听器。
     *
     * @param <T>
     */
    public interface Listener<T> {

        void on(String name, T data);

    }

    /**
     * 主事件队列。
     */
    public static final EventQueue main = new EventQueue("main-event-queue");


    private static AtomicLong ID = new AtomicLong(1);
    private static long nextID() {
        if (null == EventQueue.ID)
            EventQueue.ID = new AtomicLong(1);

        return EventQueue.ID.getAndIncrement();
    }

    private Map<String, List<Listener<?>>> listeners;
    private ReadWriteLock   lock;
    private QueuedExecutor  executor;

    public EventQueue() {
        this("event-queue-" + EventQueue.nextID());
    }

    public EventQueue(String name) {
        this.listeners  = new HashMap<>();
        this.lock       = new ReentrantReadWriteLock();
        this.executor   = new QueuedExecutor(name);
    }

    public <T> void pub(String name, T data) {
        this.executor.submit(() -> {
            Lock lock = this.lock.readLock();
            try {
                lock.lock();
                if (!this.listeners.containsKey(name)) return;
                this.listeners.get(name).forEach(listener -> ((Listener<T>) listener).on(name, data));
            } finally {
                lock.unlock();
            }
        });
    }

    public void sub(String name, Listener<?> listener) {
        Lock lock = this.lock.writeLock();
        try {
            lock.lock();
            this.listeners.putIfAbsent(name, new LinkedList<>());
            this.listeners.get(name).add(listener);
        } finally {
            lock.unlock();
        }
    }

    public void close() {
        this.executor.shutdown();
    }

}
