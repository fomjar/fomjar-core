package com.fomjar.lio;

import com.fomjar.lang.Task;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.extensions.Frame;
import org.eclipse.jetty.websocket.servlet.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WebSocketLIOServer extends LIOServer {

    private Server server;
    private ServletContextHandler handler;

    public WebSocketLIOServer() {
    }

    @Override
    public LIOServer startup(int port) throws IOException {
        this.shutdown();

        this.server = new Server(port);
        this.handler = new ServletContextHandler();
        this.handler.setContextPath("/");
        this.handler.addServlet(new ServletHolder(new Servlet()), "/*");

        this.server.setHandler(this.handler);
        this.server.setStopAtShutdown(true);

        try {this.server.start();}
        catch (Exception e) {throw new IOException(e);}
        return this;
    }

    @Override
    public LIOServer shutdown() {
        if (null != this.server) {
            Task.catchdo(() -> {
                this.handler.shutdown();
                this.server.stop();

                this.handler = null;
                this.server = null;

                return 0;
            });
        }
        return this;
    }

    @Override
    public int port() {
        return ((ServerConnector) this.server.getConnectors()[0]).getLocalPort();
    }

    private class Servlet extends WebSocketServlet {
        @Override
        public void configure(WebSocketServletFactory webSocketServletFactory) {
            // set a 10 second timeout
            // webSocketServletFactory.getPolicy().setIdleTimeout(10000);
            // register MyEchoSocket as the WebSocket to create on Upgrade
            webSocketServletFactory.setCreator(new Creator());
        }
    }

    private class Creator implements WebSocketCreator {
        private WebSocketHandler handler = new WebSocketHandler();
        @Override
        public Object createWebSocket(ServletUpgradeRequest servletUpgradeRequest, ServletUpgradeResponse servletUpgradeResponse) {
            return this.handler;
        }
    }

    @WebSocket
    public class WebSocketHandler {

        private Map<String, LIO> lios = new HashMap<>();

        private String id(Session session) {
            return session.getRemoteAddress().getHostName() + ":" + session.getRemoteAddress().getPort();
        }

        @OnWebSocketConnect
        public void onWebSocketConnect(Session session) {
            LIO lio = new WebSocketLIO(session);
            this.lios.put(this.id(session), lio);
            WebSocketLIOServer.this.doConnect(lio);
        }

        @OnWebSocketClose
        public void onWebSocketClose(Session session, int code, String reason) throws IOException {
            LIO lio = this.lios.remove(this.id(session));
            WebSocketLIOServer.this.doDisconnect(lio);
            lio.close();
        }

        @OnWebSocketMessage
        public void onWebSocketMessage(Session session, byte[] buf, int off, int len) {
            this.lios.get(this.id(session)).doRead(buf, off, len);
        }

        @OnWebSocketError
        public void onWebSocketError(Session session, Throwable cause) {
        }

        @OnWebSocketFrame
        public void onWebSocketFrame(Session session, Frame frame) {
        }

    }
}
