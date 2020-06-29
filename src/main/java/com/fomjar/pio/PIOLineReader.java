package com.fomjar.pio;

import com.fomjar.io.BufferPool;
import com.fomjar.lang.Task;

public abstract class PIOLineReader implements PIOReader {

    private BufferPool buffer;

    public PIOLineReader() {
        this.buffer = new BufferPool();
    }

    public PIOLineReader(String charset) {
        this.buffer = new BufferPool(charset);
    }

    @Override
    public void read(byte[] buf, int off, int len) {
        Task.catchdo(() -> {
            this.buffer.write(buf, off, len);
            String[] lines = this.buffer.readLines();
            if (null != lines) {
                for (String line : lines) {
                    this.readLine(line);
                }
            }
            return 0;
        });
    }

    public abstract void readLine(String line);

}
