package com.fomjar.core.pio;

import java.io.ByteArrayOutputStream;

public abstract class PIOLineReader implements PIOReader {

    private String                  charset;
    private ByteArrayOutputStream   baos;

    public PIOLineReader() {
        this("utf-8");
    }

    public PIOLineReader(String charset) {
        this.charset    = charset;
        this.baos       = new ByteArrayOutputStream();
    }

    @Override
    public void read(byte[] buf, int off, int len) throws Exception {
        this.baos.write(buf, off, len);
        String string = this.baos.toString(this.charset);

        // no lines
        if (!string.contains(System.lineSeparator()))
            return;

        // skip last incomplete line
        boolean skipLast = !string.endsWith(System.lineSeparator());

        String[] lines = string.split(System.lineSeparator());
        for (int i = 0; i < lines.length; i++) {
            // skip last incomplete line
            if (skipLast && i == lines.length - 1)
                break;
            // read line
            this.readLine(lines[i]);
        }

        this.baos.reset();
        // keep last incomplete line
        if (skipLast) {
            this.baos.write(
                    string.substring(
                            string.lastIndexOf(System.lineSeparator())
                                    + System.lineSeparator().length()
                    ).getBytes(this.charset));
        }
    }

    public abstract void readLine(String line) throws Exception;

}
