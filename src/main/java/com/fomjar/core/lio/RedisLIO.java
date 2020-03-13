package com.fomjar.core.lio;

import org.redisson.api.RBinaryStream;
import org.redisson.api.RedissonClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class RedisLIO extends LIO {

    private String          channel;
    private RedissonClient  redisson;
    private InputStream     inputStream;
    private OutputStream    outputStream;
    private byte[]          buf;
    private boolean         isOpen;

    public RedisLIO(RedissonClient redisson, String channel) throws IOException {
        this.channel = channel;
        this.handler(redisson);
    }

    @Override
    public LIO write(byte[] buf, int off, int len) throws IOException {
        this.outputStream.write(buf, off, len);
        this.outputStream.flush();
        return this;
    }

    @Override
    public LIO handler(Object handler) throws IOException {
        try {this.close();}
        catch (IOException e) {e.printStackTrace();}

        this.redisson = (RedissonClient) handler;
        this.setup();
        return this;
    }

    private void setup() {
        RBinaryStream binaryStream = this.redisson.getBinaryStream(this.channel);
        this.inputStream    = binaryStream.getInputStream();
        this.outputStream   = binaryStream.getOutputStream();
        this.isOpen         = true;
        this.buf            = new byte[1024 * 4];
        Pool.submit(() -> {
            while (this.isOpen()) {
                try {
                    int len = this.inputStream.read(this.buf);
                    if (-1 == len)  break;
                    if (0 < len)    this.doRead(this.buf, 0, len);
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
            try {this.close();}
            catch (IOException e) {e.printStackTrace();}
        });
    }

    @Override
    public boolean isOpen() {
        return this.isOpen;
    }

    @Override
    public void close() throws IOException {
        // the redissonClient instance is used everywhere
        // do not close it
        this.redisson.getBinaryStream(this.channel).delete();
        this.isOpen = false;
    }

    @Override
    public String localHost() {
        return null;
    }

    @Override
    public int localPort() {
        return 0;
    }

    @Override
    public String remoteHost() {
        return null;
    }

    @Override
    public int remotePort() {
        return 0;
    }
}
