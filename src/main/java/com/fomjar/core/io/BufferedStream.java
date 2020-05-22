package com.fomjar.core.io;

import com.fomjar.core.data.Struct;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 缓冲流
 */
public class BufferedStream {

    private String                  charset;
    private ByteArrayOutputStream   baos;
    private ReadWriteLock           lock;

    public BufferedStream() {
        this("utf-8");
    }

    public BufferedStream(String charset) {
        this.charset    = null == charset ? "utf-8" : charset;
        this.baos       = new ByteArrayOutputStream();
        this.lock       = new ReentrantReadWriteLock();
    }

    public BufferedStream write(String string) throws UnsupportedEncodingException {
        return this.write(string.getBytes(this.charset));
    }

    public BufferedStream write(InputStream is) throws IOException {
        byte[] buf = new byte[1024 * 4];
        int len = 0;
        Lock lock = this.lock.writeLock();
        try {
            lock.lock();
            while (-1 != (len = is.read(buf)))
                this.write(buf, 0, len);
        } finally {
            lock.unlock();
        }
        return this;
    }

    public BufferedStream write(byte[] buf) {
        return this.write(buf, 0, buf.length);
    }

    public BufferedStream write(byte[] buf, int off, int len) {
        Lock lock = this.lock.writeLock();
        try {
            lock.lock();
            this.baos.write(buf, off, len);
        } finally {
            lock.unlock();
        }
        return this;
    }

    public BufferedStream writeTo(File file) throws IOException {
        if (!file.isFile()) file.createNewFile();

        // 有追加就会有循环，此方法设计为一次性的，禁止循环，影响性能
        FileOutputStream os = new FileOutputStream(file, false);
        try {
            this.writeTo(os);
            os.flush();
        } finally {
            os.close();
        }

        return this;
    }

    public BufferedStream writeTo(OutputStream os) throws IOException {
        Lock lock = this.lock.writeLock();
        try {
            lock.lock();
            this.baos.writeTo(os);
            this.baos.reset();
        } finally {
            lock.unlock();
        }
        return this;
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

    public String readString() throws UnsupportedEncodingException {
        return new String(this.read(), this.charset);
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

    public int read(byte[] buf) {
        Lock lock = this.lock.readLock();
        try {
            lock.lock();

            if (0 == this.baos.size())
                return -1;

            byte[]  buf0    = Struct.get(this.baos, byte[].class, "buf");
            int     len0    = Struct.get(this.baos, int.class, "count");
            int     len     = Math.min(buf.length, len0);
            System.arraycopy(buf0, 0, buf, 0, len);

            if (len < len0) {
                // 没读完
                byte[] rest = new byte[len0 - len];
                System.arraycopy(buf0, len, rest, 0, rest.length);
                this.baos.reset();
                this.baos.write(rest);
            } else {
                // 读完了
                this.baos.reset();
            }
            return len;
        } catch (IllegalAccessException | NoSuchFieldException | IOException e) {
            // never happened
            e.printStackTrace();
            return 0;
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        return this.baos.size();
    }
}
