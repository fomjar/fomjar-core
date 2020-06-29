package com.fomjar.lio;

import com.fomjar.lang.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * 长连接服务端抽象。
 *
 * @author fomjar
 */
public abstract class LIOServer {

    private static final Logger logger = LoggerFactory.getLogger(LIOServer.class);

    private List<LIOServerListener> listeners = new LinkedList<>();

    /**
     * 在任意端口启动服务器。
     *
     * @return 此LIO服务器对象
     * @throws IOException 启动失败
     */
    public LIOServer startup() throws IOException {
        return this.startup(0);
    }

    /**
     * 在指定端口启动服务器。
     *
     * @param port 端口号
     * @return 此LIO服务器对象
     * @throws IOException 启动失败
     */
    public abstract LIOServer startup(int port) throws IOException;

    /**
     * 停止服务器。
     *
     * @return 此LIO服务器对象
     */
    public abstract LIOServer shutdown();

    /**
     * 获取服务器端口。
     *
     * @return 服务端口号
     */
    public abstract int port();

    /**
     * 添加连接监听器。
     *
     * @param listener 连接监听器
     * @return 此LIO服务器对象
     */
    public LIOServer listen(LIOServerListener listener) {
        if (null != listener) this.listeners.add(listener);
        return this;
    }

    /**
     * 连接逻辑的统一封装，由子类回调。
     *
     * @param lio
     */
    void doConnect(LIO lio) {
        for (LIOServerListener listener : this.listeners)
            Task.catchdo(() -> listener.connect(lio));
    }

    /**
     * 断链逻辑的统一封装，由子类回调。
     *
     * @param lio
     */
    void doDisconnect(LIO lio) {
        for (LIOServerListener listener : this.listeners)
            Task.catchdo(() -> listener.disconnect(lio));
    }

}
