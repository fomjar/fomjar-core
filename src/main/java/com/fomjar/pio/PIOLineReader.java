package com.fomjar.pio;

import com.fomjar.io.BufferedStream;

public abstract class PIOLineReader implements PIOReader {

    private BufferedStream channel;

    public PIOLineReader() {
        this.channel = new BufferedStream();
    }

    public PIOLineReader(String charset) {
        this.channel = new BufferedStream(charset);
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
