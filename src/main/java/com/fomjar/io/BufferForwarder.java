package com.fomjar.io;

import com.fomjar.lang.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * 缓冲转发器。
 *
 * @author fomjar
 */
public class BufferForwarder {

    private static final Logger logger = LoggerFactory.getLogger(BufferForwarder.class);

    public static Future<?> forward(InputStream is, OutputStream... os) {
        return new BufferForwarder()
                .input(is)
                .outputs(os)
                .startup();
    }

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    private InputStream         is;
    private List<OutputStream>  os;
    private Worker              worker;
    private byte[]              buf;


    public BufferForwarder() {
        this.setup();
    }

    public BufferForwarder(InputStream is) {
        this.setup();

        this.input(is);
    }

    private void setup() {
        this.is     = null;
        this.os     = new LinkedList<>();
        this.worker = null;
        this.buf    = null;
    }

    public BufferForwarder buffer(int length) {
        this.buf = new byte[length];
        return this;
    }

    public BufferForwarder input(InputStream is) {
        this.is = is;
        return this;
    }

    public BufferForwarder outputs(OutputStream... os) {
        this.os.addAll(Arrays.asList(os));
        return this;
    }

    public Future<?> startup() {
        if (null != this.worker || null == this.is || this.os.isEmpty())
            throw new IllegalStateException();

        if (null == this.buf)
            this.buffer(BufferForwarder.DEFAULT_BUFFER_SIZE);

        return Task.async(this.worker = new Worker());
    }

    public void shutdown() {
        if (null == this.worker)
            return;

        this.worker.close();
        this.worker = null;
    }

    private class Worker implements Runnable {

        private boolean isRun;
        private Thread  thread;

        @Override
        public void run() {
            logger.info("Forwarding starts.");

            this.thread = Thread.currentThread();
            this.isRun  = true;
            byte[]  buf = BufferForwarder.this.buf;
            int     len = -1;
            try {
                while (this.isRun && -1 != (len = BufferForwarder.this.is.read(buf))) {
                    for (OutputStream o : BufferForwarder.this.os) {
                        o.write(buf, 0, len);
                        o.flush();
                    }
                }
            } catch (IOException e) { logger.warn("Forwarding terminated.", e); }
            finally { BufferForwarder.this.shutdown(); }
        }

        public void close() {
            logger.info("Forwarding stops.");

            this.isRun = false;
            if (null != this.thread)
                this.thread.interrupt();
        }
    }

}
