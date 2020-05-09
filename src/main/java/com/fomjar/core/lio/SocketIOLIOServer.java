package com.fomjar.core.lio;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SocketIOLIOServer extends LIOServer {

    private Map<String, LIO> lios;
    private Configuration config;
    private SocketIOServer server;

    public SocketIOLIOServer() {
        this.lios = new HashMap<>();
        this.config = new Configuration();
//        this.config.setUpgradeTimeout(1000 * 10);
//        this.config.setPingTimeout(1000 * 10);
//        this.config.setPingInterval(1000 * 3);
        this.config.setAuthorizationListener(data -> true);
    }

    @Override
    public LIOServer startup(int port) throws IOException {
        this.shutdown();

        this.config.setHostname(null);
        if (0 != port) this.config.setPort(port);

        this.server = new SocketIOServer(this.config);
        this.server.addConnectListener(client -> {
            try {
                LIO lio = new SocketIOLIO(client);
                this.lios.put(client.getSessionId().toString(), lio);
                this.doConnect(lio);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        this.server.addDisconnectListener(client -> {
            LIO lio = this.lios.remove(client.getSessionId().toString());
            this.doDisconnect(lio);
            try { lio.close(); }
            catch (IOException e) { e.printStackTrace(); }
        });
        this.server.addEventListener(LIO.class.getSimpleName(), byte[].class,
                (client, data, ackRequest)
                        -> this.lios.get(client.getSessionId().toString()).doRead(data, 0, data.length));
        this.server.start();

        return this;
    }

    @Override
    public LIOServer shutdown() {
        if (null != this.server) {
            this.server.stop();
            this.server = null;
        }
        return this;
    }

    @Override
    public int port() {
        return this.server.getConfiguration().getPort();
    }
}
