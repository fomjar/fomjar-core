package com.fomjar.core.lio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPLIOServer extends LIOServer {

    private ServerSocket server;

    @Override
    public LIOServer startup(int port) throws IOException {
        this.server = new ServerSocket(port);
        this.doStartup();
        return this;
    }

    private void doStartup() {
        Pool.submit(() -> {
            while (!this.server.isClosed()) {
                try {
                    Socket socket = this.server.accept();
                    TCPLIO lio = new TCPLIO(socket);
                    lio.server = this;
                    this.doConnect(lio);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public LIOServer shutdown() {
        if (null != this.server) {
            try {this.server.close();}
            catch (IOException e) {e.printStackTrace();}
        }
        return this;
    }

    @Override
    public int port() {
        return this.server.getLocalPort();
    }
}
