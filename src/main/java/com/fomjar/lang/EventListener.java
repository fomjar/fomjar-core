package com.fomjar.lang;

/**
 * 事件监听器。
 *
 * @param <T> 事件数据的类型
 * @author fomjar
 */
public interface EventListener<T> {

    /**
     * 接受事件的回调方法。
     *
     * @param event 事件名称
     * @param data 事件数据
     */
    void on(String event, T data);

}
