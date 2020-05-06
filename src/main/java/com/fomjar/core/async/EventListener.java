package com.fomjar.core.async;

/**
 * 事件监听器。
 *
 * @param <T>
 * @author fomjar
 */
public interface EventListener<T> {

    void on(String event, T data);

}
