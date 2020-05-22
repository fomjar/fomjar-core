package com.fomjar.core.async;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 事件分发器。方便地发布/订阅自定义事件。<br>
 *
 * @author fomjar
 */
public class EventDispatcher {

    /**
     * 事件监听器。
     *
     * @param <T> 事件数据的类型
     */
    public interface Listener<T> {

        /**
         * 接受事件的回调方法。
         *
         * @param event 事件名称
         * @param data 事件数据
         */
        void on(String event, T data);

    }

    /**
     * 主事件分发器。
     */
    public static final EventDispatcher main = new EventDispatcher("event-dispatcher");

    private static final AtomicLong ID = new AtomicLong(0);

    private Map<String, List<Listener<?>>> listeners;
    private ReadWriteLock   lock;
    private ExecutorService queue;

    public EventDispatcher() {
        this("event-dispatcher-" + EventDispatcher.ID.getAndIncrement());
    }

    public EventDispatcher(String name) {
        this.listeners  = new HashMap<>();
        this.lock       = new ReentrantReadWriteLock(true);
        this.queue      = Executors.newSingleThreadExecutor(new SingleThreadFactory(name));
    }

    @SuppressWarnings("unchecked")
    public <T> void pub(String event, T data) {
        this.queue.submit(() -> {
            Lock lock = this.lock.readLock();
            try {
                lock.lock();
                if (!this.listeners.containsKey(event)) return;
                this.listeners.get(event).forEach(listener -> {
                    try {((Listener<T>) listener).on(event, data);}
                    catch (Exception e) {e.printStackTrace();}
                });
            } finally {
                lock.unlock();
            }
        });
    }

    public void sub(String event, Listener<?> listener) {
        Lock lock = this.lock.writeLock();
        try {
            lock.lock();
            this.listeners.putIfAbsent(event, new LinkedList<>());
            this.listeners.get(event).add(listener);
        } finally {
            lock.unlock();
        }
    }

}
