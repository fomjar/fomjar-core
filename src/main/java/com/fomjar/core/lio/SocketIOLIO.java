package com.fomjar.core.lio;

import com.corundumstudio.socketio.SocketIOClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SocketIOLIO extends LIO {

    private SocketIOClient client;

    public SocketIOLIO(SocketIOClient client) throws IOException {
        this.client = client;

        Map<String, Object> args = new HashMap<>();
        this.client.getHandshakeData().getUrlParams().entrySet().forEach(e -> {
            if (null == e.getValue() || 0 == e.getValue().size()) {
                args.put(e.getKey(), null);
            } else if (1 == e.getValue().size()) {
                args.put(e.getKey(), e.getValue().get(0));
            } else {
                args.put(e.getKey(), e.getValue());
            }
        });

        try {
            this.attach("path", new URI(client.getHandshakeData().getUrl()).getPath());
            this.attach("args", args);
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
    }

    @Override
    public LIO write(byte[] buf, int off, int len) throws IOException {
        byte[] data;
        if (0 == off && len == buf.length) data = buf;
        else data = Arrays.copyOfRange(buf, off, off + len);

        try {this.client.sendEvent(LIO.class.getSimpleName(), new Object[] {data});}
        catch (Exception e) {throw new IOException(e);}
        return this;
    }

    @Override
    public boolean isOpen() {
        return this.client.isChannelOpen();
    }

    @Override
    public void close() throws IOException {
        // Do not close socket-io client.
    }

    @Override
    public String localHost() {
        return this.isOpen() ? this.client.getHandshakeData().getLocal().getHostName() : null;
    }

    @Override
    public int localPort() {
        return this.isOpen() ? this.client.getHandshakeData().getLocal().getPort() : -1;
    }

    @Override
    public String remoteHost() {
        return this.isOpen() ? this.client.getHandshakeData().getAddress().getHostName() : null;
    }

    @Override
    public int remotePort() {
        return this.isOpen() ? this.client.getHandshakeData().getAddress().getPort() : -1;
    }


}
