package com.fomjar.core.lio;

import com.fomjar.core.async.Async;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPLIOServer extends LIOServer {

    private ServerSocket server;

    @Override
    public LIOServer startup(int port) throws IOException {
        this.shutdown();

        this.server = new ServerSocket(port);
        Async.async(() -> {
            while (!this.server.isClosed()) {
                try {
                    Socket socket = this.server.accept();
                    TCPLIO lio = new TCPLIO(socket);
                    lio.server = this;
                    this.doConnect(lio);
                } catch (IOException e) {
                    // Ignore. Server closed.
                }
            }
            this.shutdown();
        });
        return this;
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
