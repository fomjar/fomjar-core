package com.fomjar.core.lio;

/**
 * 长连接数据异步读取器。
 *
 * @author fomjar
 */
public interface LIOReader {

    void read(LIO lio, byte[] buf, int off, int len) throws Exception;

}
