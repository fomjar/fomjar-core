package com.fomjar.lang;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 序号递增线程工厂。
 * <ul>
 *     <li>pool-1-3</li>
 *     <li>pool-1-4</li>
 * </ul>
 *
 * @author fomjar
 */
public class IncrementThreadFactory implements ThreadFactory {

    private static final AtomicLong GROUP = new AtomicLong(0);

    private String prefix;
    private AtomicLong id;

    public IncrementThreadFactory() {
        this("pool-" + IncrementThreadFactory.GROUP.getAndIncrement());
    }

    public IncrementThreadFactory(String prefix) {
        this.prefix = prefix;
        this.id = new AtomicLong(0);
    }

    @Override
    public Thread newThread(Runnable r) {
        return ((Func<Thread>) (args) -> {
            Thread t =  new Thread(r, this.prefix + "-" + this.id.getAndIncrement());
            t.setDaemon(true);
            return t;
        }).call();
    }

}
