package com.fomjar.lio;

import com.fomjar.lang.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPLIOServer extends LIOServer {

    private static final Logger logger = LoggerFactory.getLogger(TCPLIOServer.class);

    private ServerSocket server;

    @Override
    public LIOServer startup(int port) throws IOException {
        this.shutdown();

        this.server = new ServerSocket(port);
        Task.async(() -> {
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
            catch (IOException e) { logger.warn("Shutdown server failed.", e); }
        }
        return this;
    }

    @Override
    public int port() {
        return this.server.getLocalPort();
    }
}
