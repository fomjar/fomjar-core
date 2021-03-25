package com.fomjar.io;

/**
 * 数据过滤器。
 *
 * @param <T> 需要过滤的数据类型
 *
 * @author fomjar
 */
public interface DataFilter<T> {

    /**
     *
     * @param data 待过滤的数据
     * @return 过滤后的数据
     */
    T filter(T data);

}
