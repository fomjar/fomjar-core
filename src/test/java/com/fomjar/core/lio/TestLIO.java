package com.fomjar.core.lio;

import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class TestLIO {

    @Test
    public void restWebSocketLIO() throws InterruptedException {
        final int port = 3000;

        Thread threadServer = new Thread(() -> {
            try {
                LIOServer server = new WebSocketLIOServer();
                server.listen(new LIOServerListener() {
                    @Override
                    public void connect(LIO lio) {
                        lio.read((LIO lio1, byte[] buf, int off, int len) -> {
                            System.out.println("from client: " + new String(buf, off, len));
                        });
                    }
                    @Override
                    public void disconnect(LIO lio) {}
                });

                server.startup(port);

                Thread.sleep(3000L);

                server.shutdown();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        threadServer.start();

        new Thread(() -> {
            try {
                LIO lio = new WebSocketLIO(new URI("ws://127.0.0.1:" + port + "/hello?a=1&b=2"), null);
                while (!lio.isOpen()) {
                    Thread.sleep(100L);
                }
                for (int i = 0; i < 3; i++) {
                    lio.write("hello server!");
                    Thread.sleep(1000L);
                }
                lio.close();
            } catch (IOException | InterruptedException | URISyntaxException e) {
                e.printStackTrace();
            }
        }).start();

        threadServer.join();

        assert true;
    }

}
