package com.fomjar.lio;

import com.fomjar.io.Buffers;
import com.fomjar.lang.Task;

public abstract class LIOLineReader implements LIOReader {

    private Buffers channel;

    public LIOLineReader() {
        this.channel = new Buffers();
    }

    public LIOLineReader(String charset) {
        this.channel = new Buffers(charset);
    }

    @Override
    public void read(byte[] buf, int off, int len) {
        Task.catchdo(() -> {
            this.channel.write(buf, off, len);
            String[] lines = this.channel.readLines();
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
