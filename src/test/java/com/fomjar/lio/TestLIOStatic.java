package com.fomjar.lio;

import org.junit.Test;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

public class TestLIOStatic {

    private int port = 3000;

    private void startServer(LIOServer server) throws IOException {
        server.listen(new LIOServerListener() {
            @Override
            public void connect(LIO lio) {
                lio.read(new LIOLineReader() {
                    @Override
                    public void readLine(String line) throws Exception {
                        System.out.println("from client: " + line);
                    }
                });
            }
            @Override
            public void disconnect(LIO lio) {}
        });
        server.startup(3000);
    }

    private void startClient(LIO lio) throws IOException, InterruptedException {
        while (!lio.isOpen()) {
            Thread.sleep(100L);
        }
        for (int i = 0; i < 3; i++) {
            lio.writeLine("hello server!");
            Thread.sleep(200L);
        }
        lio.close();
    }

    @Test
    public void testWebSocketLIO() throws InterruptedException, URISyntaxException, IOException {
        LIOServer server = new WebSocketLIOServer();
        this.startServer(server);
        this.startClient(new WebSocketLIO(new URI("ws://127.0.0.1:" + port + "/hello?a=1&b=2")));
        server.shutdown();
    }

    @Test
    public void testTCPLIO() throws IOException, InterruptedException {
        LIOServer server = new TCPLIOServer();
        this.startServer(server);
        this.startClient(new TCPLIO(new Socket("127.0.0.1", 3000)));
        server.shutdown();
    }

}
