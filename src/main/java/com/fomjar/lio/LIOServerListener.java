package com.fomjar.lio;

/**
 * 长连接服务端的连接监听器。监听连接和断连事件。
 *
 * @author fomjar
 */
public interface LIOServerListener {

    void connect(LIO lio);

    void disconnect(LIO lio);

}
