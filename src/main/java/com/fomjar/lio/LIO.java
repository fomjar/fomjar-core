package com.fomjar.lio;

import com.fomjar.lang.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 长连接客户端抽象。
 *
 * @author fomjar
 */
public abstract class LIO {

    private static final Logger logger = LoggerFactory.getLogger(LIO.class);

    private List<LIOReader>         readers = new LinkedList<>();
    private ByteArrayOutputStream   buffers = new ByteArrayOutputStream();
    private Map<String, Object>     attach;

    public LIO writeLine(String line) throws IOException {
        return this.write(line + System.lineSeparator());
    }

    /**
     * 写入字符串。
     *
     * @param string 待写入的内容
     * @return 此LIO对象
     * @throws IOException 写入失败
     */
    public LIO write(String string) throws IOException {
        return this.write(string.getBytes());
    }

    /**
     * 写入字节。
     *
     * @param buf 待写入的内容
     * @return 此LIO对象
     * @throws IOException 写入失败
     */
    public LIO write(byte[] buf) throws IOException {
        return this.write(buf, 0, buf.length);
    }

    /**
     * 写入字节。
     *
     * @param buf 待写入的字节数组
     * @param off 偏移位置
     * @param len 字节长度
     * @return 此LIO对象
     * @throws IOException 写入失败
     */
    public abstract LIO write(byte[] buf, int off, int len) throws IOException;

    /**
     * 异步读取数据。
     *
     * @param reader 异步读取回调对象
     * @return 此LIO对象
     */
    public LIO read(LIOReader reader) {
        if (null != reader) this.readers.add(reader);
        this.doRead(new byte[0], 0, 0); // 触发添加reader之前的缓存数据的读回调
        return this;
    }

    /**
     * 获取附加的业务信息。
     *
     * @param key 键
     * @return 值
     */
    public Object attach(String key) {
        if (null == this.attach) return null;
        return this.attach.get(key);
    }

    /**
     * 附加业务信息。
     *
     * @param key 键
     * @param val 值
     * @return 此LIO对象
     */
    public LIO attach(String key, Object val) {
        if (null == this.attach)
            this.attach = new HashMap<>();
        this.attach.put(key, val);
        return this;
    }

    /**
     * 读取数据的逻辑的统一封装，由子类回调此逻辑。
     *
     * @param buf
     * @param off
     * @param len
     */
    void doRead(byte[] buf, int off, int len) {
        if (this.readers.isEmpty() || 0 < this.buffers.size()) {
            synchronized (this.buffers) {
                if (this.readers.isEmpty()) {
                    if (0 < len) this.buffers.write(buf, off, len);
                    return;
                }

                if (0 < this.buffers.size()) {
                    if (0 < len) this.buffers.write(buf, off, len);
                    buf = this.buffers.toByteArray();
                    off = 0;
                    len = buf.length;
                    this.buffers.reset();
                }
            }
        }

        if (0 < len) {
            byte[] finalBuf = buf;
            int finalOff = off;
            int finalLen = len;
            for (LIOReader reader : this.readers)
                Task.catchdo(() -> reader.read(finalBuf, finalOff, finalLen));
        }
    }

    /**
     * 判断此长连接是否为开启状态。
     *
     * @return true为开启，false为关闭
     */
    public abstract boolean isOpen();

    /**
     * 关闭长连接。
     *
     * @throws IOException 关闭失败
     */
    public abstract void close() throws IOException;

    /**
     * 本地主机名。
     *
     * @return 主机名
     */
    public abstract String localHost();

    /**
     * 本地端口。
     *
     * @return 端口号
     */
    public abstract int localPort();

    /**
     * 远端主机名。
     *
     * @return 主机名
     */
    public abstract String remoteHost();

    /**
     * 远端端口。
     *
     * @return 端口号
     */
    public abstract int remotePort();

}
