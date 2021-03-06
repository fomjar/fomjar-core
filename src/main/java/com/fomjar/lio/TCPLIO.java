package com.fomjar.lio;

import com.fomjar.lang.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TCPLIO extends LIO {

    private static final Logger logger = LoggerFactory.getLogger(TCPLIO.class);

    LIOServer server;
    private Socket          so;
    private InputStream     is;
    private OutputStream    os;
    private byte[]          buf;

    public TCPLIO(Socket socket) throws IOException {
        this.so = socket;
        this.so.setKeepAlive(true);
        this.is = this.so.getInputStream();
        this.os = this.so.getOutputStream();
        this.buf = new byte[1024 * 4];
        Task.async(() -> {
            try {
                while (this.isOpen()) {
                    int len = this.is.read(this.buf);
                    if (-1 == len)  break;
                    if (0 < len)    this.doRead(this.buf, 0, len);
                }
            } catch (IOException e) {
                // Remote closed.
                if (null != this.server)
                    this.server.doDisconnect(this);
            } finally {
                try {this.close();}
                catch (IOException e) { logger.warn("Close connection failed.", e); }
            }
        });
    }

    @Override
    public LIO write(byte[] buf, int off, int len) throws IOException {
        this.os.write(buf, off, len);
        this.os.flush();
        return this;
    }

    @Override
    public boolean isOpen() {
        return !this.so.isClosed();
    }

    @Override
    public void close() throws IOException {
        if (null != this.so)
            this.so.close();
    }

    @Override
    public String localHost() {
        return this.isOpen() ? this.so.getLocalAddress().getHostName() : null;
    }

    @Override
    public int localPort() {
        return this.isOpen() ? this.so.getLocalPort() : -1;
    }

    @Override
    public String remoteHost() {
        return this.isOpen() ? this.so.getInetAddress().getHostName() : null;
    }

    @Override
    public int remotePort() {
        return this.isOpen() ? this.so.getPort() : -1;
    }
}
