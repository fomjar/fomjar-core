package com.fomjar.io;

import com.fomjar.lang.Struct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 缓冲池。
 *
 * @author fomjar
 */
public class BufferPool {

    private static final Logger logger = LoggerFactory.getLogger(BufferPool.class);

    private String                  charset;
    private ByteArrayOutputStream   baos;
    private Lock                    lock;

    public BufferPool() {
        this("utf-8");
    }

    public BufferPool(String charset) {
        this.charset    = null == charset ? "utf-8" : charset;
        this.baos       = new ByteArrayOutputStream();
        this.lock       = new ReentrantLock();
    }

    public void write(String string) throws UnsupportedEncodingException {
        this.write(string.getBytes(this.charset));
    }

    public BufferPool write(InputStream is) {
        try {
            this.lock.lock();
            BufferForwarder.forward(is, this.baos).get();
        } catch (InterruptedException | ExecutionException e) { logger.error("Exception occurred while writing from input stream.", e); }
        finally { this.lock.unlock(); }
        return this;
    }

    public void write(byte[] buf) {
        this.write(buf, 0, buf.length);
    }

    public void write(byte[] buf, int off, int len) {
        try {
            this.lock.lock();
            this.baos.write(buf, off, len);
        } finally {
            this.lock.unlock();
        }
    }

    public String[] readLines() throws UnsupportedEncodingException {
        return this.readString(System.lineSeparator());
    }

    public String[] readString(String separator) throws UnsupportedEncodingException {
        String string = this.readString();

        // no separator
        if (null == separator || 0 == separator.length() || !string.contains(separator))
            return new String[] {string};

        // skip last incomplete string
        boolean skipLast = !string.endsWith(separator);

        List<String> strings = new LinkedList<>();
        String[] strings0 = string.split(separator);
        for (int i = 0; i < strings0.length; i++) {
            // skip last incomplete string
            if (skipLast && i == strings0.length - 1)
                break;
            // read string
            strings.add(strings0[i]);
        }

        // keep last incomplete line
        if (skipLast)
            this.write(strings0[strings0.length - 1].getBytes(this.charset));

        return strings.toArray(new String[0]);
    }

    public String readString() throws UnsupportedEncodingException {
        return new String(this.readBytes(), this.charset);
    }

    public byte[] readBytes() {
        try {
            this.lock.lock();
            return this.baos.toByteArray();
        } finally {
            this.baos.reset();
            this.lock.unlock();
        }
    }

    public void read(File file) throws IOException {
        if (!file.isFile())
            if (!file.createNewFile())
                throw new IOException("Create file failed: " + file.getAbsolutePath());

        // 有追加就会有循环，此方法设计为一次性的，禁止循环，影响性能
        try (FileOutputStream os = new FileOutputStream(file, false)) {
            this.read(os);
        }
    }

    public void read(OutputStream os) throws IOException {
        try {
            this.lock.lock();
            this.baos.writeTo(os);
            os.flush();
            this.baos.reset();
        } finally {
            this.lock.unlock();
        }
    }

    public int read(byte[] buf) {
        if (null == buf)
            throw new NullPointerException();
        return this.read(buf, 0, buf.length);
    }

    public int read(byte[] buf, int off, int len) {
        if (0 == this.size())
            return 0;
        if (null == buf)
            throw new NullPointerException();
        if (buf.length == 0 || off < 0 || len <= 0)
            throw new IllegalArgumentException();

        try {
            this.lock.lock();

            byte[]  buf0    = Struct.get(this.baos, byte[].class, "buf");
            int     len0    = Struct.get(this.baos, int.class, "count");
            int     len1     = Math.min(Math.min(buf.length - off, len), len0);
            System.arraycopy(buf0, 0, buf, off, len1);

            this.baos.reset();
            if (len1 < len0) {
                // 没读完
                this.baos.write(buf0, len1, len0 - len1);
            } else {
                // 读完了
            }
            return len1;
        } catch (IllegalAccessException | NoSuchFieldException e) {
            // never happened
            logger.error("Exception occurred while reading.", e);
            return 0;
        } finally {
            this.lock.unlock();
        }
    }

    public int size() {
        return this.baos.size();
    }

    public void close() {
        try {
            this.lock.lock();
            this.baos = new ByteArrayOutputStream();
        } finally {
            this.lock.unlock();
        }
    }
}
