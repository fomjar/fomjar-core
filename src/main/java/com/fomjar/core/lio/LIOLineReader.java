package com.fomjar.core.lio;

import com.fomjar.core.data.BufferedChannel;

public abstract class LIOLineReader implements LIOReader {

    private BufferedChannel channel;

    public LIOLineReader() {
        this.channel = new BufferedChannel();
    }

    public LIOLineReader(String charset) {
        this.channel = new BufferedChannel(charset);
    }

    @Override
    public void read(byte[] buf, int off, int len) throws Exception {
        this.channel.write(buf, off, len);
        String[] lines = this.channel.readLines();
        if (null != lines) {
            for (String line : lines) {
                this.readLine(line);
            }
        }
    }

    public abstract void readLine(String line) throws Exception;

}
