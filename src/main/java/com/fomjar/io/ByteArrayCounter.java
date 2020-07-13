package com.fomjar.io;

import com.aliyun.openservices.shade.io.netty.util.internal.ConcurrentSet;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 字节数组计数器。
 *
 * <pre>
 * // 初始化
 * ByteArrayCounter counter = new ByteArrayCounter();
 * counter.count((len, now) -> System.out.println("Downloading... " + now + " bytes"));
 *
 * // 数据处理线程
 * counter.notify(bytes, off, len);
 * </pre>
 *
 * @author fomjar
 */
public class ByteArrayCounter extends DataFilter<byte[]> {

    public interface Counter {

        /**
         *
         * @param len 本次计数数据长度
         * @param now 已计数数据总长度
         */
        void count(int len, long now);

    }

    public static ByteArrayCounter create(Counter... counter) {
        return new ByteArrayCounter().counter(counter);
    }

    private AtomicLong      counter;
    private Set<Counter>    counters;

    public ByteArrayCounter() {
        this.counter    = new AtomicLong(0);
        this.counters   = new ConcurrentSet<>();
        this.filter(((data, off, len) ->
                this.counters.forEach(c ->
                        c.count(len, this.counter.addAndGet(len)))));
    }

    public ByteArrayCounter counter(Counter... counter) {
        this.counters.addAll(Arrays.asList(counter));
        return this;
    }

    public ByteArrayCounter set(long count) {
        this.counter.set(count);
        return this;
    }

    public ByteArrayCounter reset() {
        return this.set(0);
    }

}
