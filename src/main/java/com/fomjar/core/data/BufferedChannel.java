package com.fomjar.core.data;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BufferedChannel {

    private String                  charset;
    private ByteArrayOutputStream   baos;
    private ReadWriteLock           lock;

    public BufferedChannel() {
        this("utf-8");
    }

    public BufferedChannel(String charset) {
        this.charset    = null == charset ? "utf-8" : charset;
        this.baos       = new ByteArrayOutputStream();
        this.lock       = new ReentrantReadWriteLock();
    }

    public void write(String string) throws UnsupportedEncodingException {
        this.write(string.getBytes(this.charset));
    }

    public void write(byte[] buf) {
        this.write(buf, 0, buf.length);
    }

    public void write(byte[] buf, int off, int len) {
        Lock lock = this.lock.writeLock();
        try {
            lock.lock();
            this.baos.write(buf, off, len);
        } finally {
            lock.unlock();
        }
    }

    public byte[] read() {
        Lock lock = this.lock.readLock();
        try {
            lock.lock();
            return this.baos.toByteArray();
        } finally {
            this.baos.reset();
            lock.unlock();
        }
    }

    public String readString() throws UnsupportedEncodingException {
        return new String(this.read(), this.charset);
    }

    public String[] readLines() throws UnsupportedEncodingException {
        List<String> lines = new LinkedList<>();
        String string = this.readString();

        // no lines
        if (!string.contains(System.lineSeparator()))
            return null;

        // skip last incomplete line
        boolean skipLast = !string.endsWith(System.lineSeparator());

        String[] lines0 = string.split(System.lineSeparator());
        for (int i = 0; i < lines0.length; i++) {
            // skip last incomplete line
            if (skipLast && i == lines0.length - 1)
                break;
            // read line
            lines.add(lines0[i]);
        }

        // keep last incomplete line
        if (skipLast) {
            this.write(
                    string.substring(
                            string.lastIndexOf(System.lineSeparator())
                                    + System.lineSeparator().length()
                    ).getBytes(this.charset));
        }

        return lines.toArray(new String[0]);
    }
}
