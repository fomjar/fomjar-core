package com.fomjar.io;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 缓冲过滤器，一般用来计数。
 *
 * @author fomjar
 */
public abstract class BufferFilter implements DataFilter<ByteBuffer> {

    private AtomicLong counter;

    public BufferFilter() {
        this.counter = new AtomicLong(0);
    }

    @Override
    public ByteBuffer filter(ByteBuffer data) {
        this.count(data.remaining(), this.counter.addAndGet(data.remaining()));
        return data;
    }

    /**
     *
     * @param len 本次计数数据长度
     * @param now 已计数数据总长度
     */
    public abstract void count(int len, long now);

    public BufferFilter set(long count) {
        this.counter.set(count);
        return this;
    }

    public BufferFilter reset() {
        return this.set(0);
    }

}
