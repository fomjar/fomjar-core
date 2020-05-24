package com.fomjar.pio;

/**
 * PIO输出读取Reader。标准输出和错误输出均通过此接口异步输出。
 *
 * @author fomjar
 */
public interface PIOReader {

    void read(byte[] buf, int off, int len) throws Exception;

}
