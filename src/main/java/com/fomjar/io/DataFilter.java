package com.fomjar.io;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * 数据过滤器。
 *
 * @param <T> 需要过滤的数据类型
 *
 * @author fomjar
 */
public class DataFilter<T> {

    public interface Filter<T> {

        /**
         *
         * @param data
         * @param off 当data非数组类型时无效
         * @param len 当data非数组类型时无效
         */
        void filter(T data, int off, int len);

    }

    public static <T> DataFilter<T> create(Filter<T>... filter) {
        return new DataFilter<T>().filter(filter);
    }

    private Set<Filter<T>> filters;

    public DataFilter() {
        this.filters = new ConcurrentSkipListSet<>();
    }

    public DataFilter<T> filter(Filter<T>... filter) {
        this.filters.addAll(Arrays.asList(filter));
        return this;
    }

    public DataFilter<T> notify(T data, int off, int len) {
        this.filters.forEach(filter -> filter.filter(data, off, len));
        return this;
    }

}
