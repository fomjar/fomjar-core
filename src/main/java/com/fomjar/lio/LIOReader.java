package com.fomjar.lio;

/**
 * 长连接数据异步读取器。
 *
 * @author fomjar
 */
public interface LIOReader {

    void read(byte[] buf, int off, int len);

}
