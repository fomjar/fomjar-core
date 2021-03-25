package com.fomjar.pio;

import com.fomjar.io.Buffers;
import com.fomjar.lang.Task;

public abstract class PIOLineReader implements PIOReader {

    private Buffers buffer;

    public PIOLineReader() {
        this.buffer = new Buffers();
    }

    public PIOLineReader(String charset) {
        this.buffer = new Buffers(charset);
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
