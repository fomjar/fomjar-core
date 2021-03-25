package com.fomjar.io;

import com.fomjar.lang.Struct;
import com.fomjar.lang.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 缓冲池。支持数据的缓冲形式写入和读取，用于数据缓冲转发或实时转发
 *
 * @author fomjar
 */
public class Buffers {

    private static final Logger logger = LoggerFactory.getLogger(Buffers.class);

    /**
     * 数据流转发。
     *
     * @param is 原始流
     * @param oss 目的流，多个目的流的写入数据相同
     * @return 可中断的 Future 对象
     */
    public static Future<?> forward(InputStream is, OutputStream... oss) {
        return Buffers.forward(null, is, oss);
    }

    /**
     * 数据流转发。
     *
     * @param filter 数据过滤器
     * @param is 原始流
     * @param oss 目的流，多个目的流的写入数据相同
     * @return 可中断的 Future 对象
     */
    public static Future<?> forward(DataFilter<ByteBuffer> filter, InputStream is, OutputStream... oss) {
        return Task.async(() -> {
            logger.info("Buffer forwarding startups.");

            Thread  thread  = Thread.currentThread();
            byte[]  buf = new byte[1024 * 4];
            int     len = -1;
            ByteBuffer buffer = ByteBuffer.wrap(buf);

            try {
                while (!thread.isInterrupted()
                        && -1 != (len = is.read(buf))) {

                    buffer.position(0);
                    buffer.limit(len);

                    if (null != filter)
                        buffer = filter.filter(buffer);

                    for (OutputStream os : oss) {
                        os.write(buffer.array(), buffer.position(), buffer.remaining());
                        os.flush();
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            logger.info("Buffer forwarding terminated.");
        });
    }

    private String                  charset;
    private ByteArrayOutputStream   baos;
    private Lock                    lock;

    public Buffers() {
        this("utf-8");
    }

    public Buffers(String charset) {
        this.charset    = null == charset ? "utf-8" : charset;
        this.baos       = new ByteArrayOutputStream();
        this.lock       = new ReentrantLock();
    }

    /**
     * 将字符串写入缓冲池中。
     *
     * @param string 待写入的字符串
     * @return 此缓冲池
     * @throws UnsupportedEncodingException
     */
    public Buffers write(String string) throws UnsupportedEncodingException {
        return this.write(string.getBytes(this.charset));
    }

    /**
     * 将输入流中的数据写入缓冲池中。
     *
     * @param is 待写入的输入流
     * @return 此缓冲池
     */
    public Buffers write(InputStream is) {
        try {
            this.lock.lock();
            Buffers.forward(is, this.baos).get();
        } catch (InterruptedException | ExecutionException e) { logger.error("Exception occurred while writing from input stream.", e); }
        finally { this.lock.unlock(); }
        return this;
    }

    /**
     * 将字节数组写入缓冲池中。
     *
     * @param buf 待写入的字节数组
     * @return 此缓冲池
     */
    public Buffers write(byte[] buf) { return this.write(buf, 0, buf.length); }

    /**
     * 将字节数组写入缓冲池中。
     *
     * @param buf 待写入的字节数组
     * @param off 数据偏移量
     * @param len 数据长度
     * @return 此缓冲池
     */
    public Buffers write(byte[] buf, int off, int len) {
        try {
            this.lock.lock();
            this.baos.write(buf, off, len);
        } finally {
            this.lock.unlock();
        }
        return this;
    }

    /**
     * 从缓冲池中按行读取字符串。最后一行若不是以换行符结尾，则会被保留在缓冲池中。
     *
     * @return 行数组
     * @throws UnsupportedEncodingException
     */
    public String[] readLines() throws UnsupportedEncodingException {
        return this.readString(System.lineSeparator());
    }

    /**
     * 从缓冲池中按指定分隔符分割读取字符串。缓冲池中的数据不是以指定分隔符作为结尾，则最后一个分隔符以后的数据会被保留在缓冲池中。
     *
     * @param separator 分隔符
     * @return 字符串数组
     * @throws UnsupportedEncodingException
     */
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

    /**
     * 从缓冲池中读取字符串数据。
     *
     * @return 读取到的字符串
     * @throws UnsupportedEncodingException
     */
    public String readString() throws UnsupportedEncodingException {
        return new String(this.readBytes(), this.charset);
    }

    /**
     * 从缓冲池中读取所有字节。
     *
     * @return 字节数组
     */
    public byte[] readBytes() {
        try {
            this.lock.lock();
            return this.baos.toByteArray();
        } finally {
            this.baos.reset();
            this.lock.unlock();
        }
    }

    /**
     * 从缓冲池中读取所有数据并写入指定文件中。
     *
     * @param file 待写入的文件
     * @throws IOException
     */
    public void read(File file) throws IOException {
        if (!file.isFile())
            if (!file.createNewFile())
                throw new IOException("Create file failed: " + file.getAbsolutePath());

        // 有追加就会有循环，此方法设计为一次性的，禁止循环，影响性能
        try (FileOutputStream os = new FileOutputStream(file, false)) {
            this.read(os);
        }
    }

    /**
     * 从缓冲池中读取所有数据并广播到指定的输出流中。
     *
     * @param oss 待写入的输出流
     * @throws IOException
     */
    public void read(OutputStream... oss) throws IOException {
        try {
            this.lock.lock();
            for (OutputStream os : oss) {
                this.baos.writeTo(os);
                os.flush();
            }
            this.baos.reset();
        } finally {
            this.lock.unlock();
        }
    }

    /**
     * 从缓冲池中读取数据到给定缓冲区中。
     *
     * @param buf 待存放数据的缓冲区
     * @return 实际读取到的数据长度
     */
    public int read(byte[] buf) {
        if (null == buf)
            throw new NullPointerException();
        return this.read(buf, 0, buf.length);
    }

    /**
     * 从缓冲池中读取数据到给定缓冲区中。
     *
     * @param buf 待存放数据的缓冲区
     * @param off 可用缓冲偏移位置
     * @param len 可用缓冲长度
     * @return 实际读取到的数据长度
     */
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

    /**
     * 缓冲数据大小。
     *
     * @return 缓冲数据大小。
     */
    public int size() {
        return this.baos.size();
    }

    /**
     * 关闭和清理缓冲区。
     */
    public void close() {
        try {
            this.lock.lock();
            this.baos = new ByteArrayOutputStream();
        } finally {
            this.lock.unlock();
        }
    }
}
