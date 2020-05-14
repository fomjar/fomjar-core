package com.fomjar.core.lio;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.extensions.Frame;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class WebSocketLIO extends LIO {

    private WebSocketClient client;
    private Session         session;

    public WebSocketLIO(URI uri) throws IOException {
        try {
            this.client = new WebSocketClient();
            this.client.start();
            this.client.connect(new WebSocketHandler(), uri);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    WebSocketLIO(Session session) {
        this.setup(session);
    }

    private void setup(Session session) {
        this.session = session;

        Map<String, Object> args = new HashMap<>();
        this.session.getUpgradeRequest().getParameterMap().entrySet().forEach(e -> {
            if (null == e.getValue() || 0 == e.getValue().size()) {
                args.put(e.getKey(), null);
            } else if (1 == e.getValue().size()) {
                args.put(e.getKey(), e.getValue().get(0));
            } else {
                args.put(e.getKey(), e.getValue());
            }
        });
        this.attach("path", this.session.getUpgradeRequest().getRequestURI().getPath());
        this.attach("args", args);
    }

    @Override
    public LIO write(byte[] buf, int off, int len) throws IOException {
        this.session.getRemote().sendBytes(ByteBuffer.wrap(buf, off, len));
        this.session.getRemote().flush();
        return this;
    }

    @Override
    public boolean isOpen() {
        return null != this.session && this.session.isOpen();
    }

    @Override
    public void close() throws IOException {
        try {
            if (null != this.client)    this.client.stop();
            if (null != this.session)   this.session.close();
        } catch (Exception e) {
            if (e instanceof IOException)   throw (IOException) e;
            else                            throw new IOException(e);
        }
    }

    @Override
    public String localHost() {
        return this.isOpen() ? this.session.getLocalAddress().getHostName() : null;
    }

    @Override
    public int localPort() {
        return this.isOpen() ? this.session.getLocalAddress().getPort() : -1;
    }

    @Override
    public String remoteHost() {
        return this.isOpen() ? this.session.getRemoteAddress().getHostName() : null;
    }

    @Override
    public int remotePort() {
        return this.isOpen() ? this.session.getRemoteAddress().getPort() : -1;
    }


    @WebSocket
    public class WebSocketHandler {

        @OnWebSocketConnect
        public void onWebSocketConnect(Session session) {
            WebSocketLIO.this.setup(session);
        }

        @OnWebSocketClose
        public void onWebSocketClose(Session session, int code, String reason) throws IOException {
            WebSocketLIO.this.close();
        }

        @OnWebSocketMessage
        public void onWebSocketMessage(Session session, byte[] buf, int off, int len) {
            WebSocketLIO.this.doRead(buf, off, len);
        }

        @OnWebSocketError
        public void onWebSocketError(Session session, Throwable cause) {
        }

        @OnWebSocketFrame
        public void onWebSocketFrame(Session session, Frame frame) {
        }

    }
}
