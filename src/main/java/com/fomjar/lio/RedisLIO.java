package com.fomjar.lio;

import com.fomjar.lang.Task;
import org.redisson.api.RBinaryStream;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class RedisLIO extends LIO {

    private static final Logger logger = LoggerFactory.getLogger(RedisLIO.class);

    private String          channel;
    private RedissonClient  redisson;
    private InputStream     inputStream;
    private OutputStream    outputStream;
    private byte[]          buf;
    private boolean         isOpen;

    public RedisLIO(RedissonClient redisson, String channel) {
        this.channel = channel;
        this.redisson = redisson;

        RBinaryStream binaryStream = this.redisson.getBinaryStream(this.channel);
        this.inputStream    = binaryStream.getInputStream();
        this.outputStream   = binaryStream.getOutputStream();
        this.isOpen         = true;
        this.buf            = new byte[1024 * 4];

        Task.async(() -> {
            try {
                while (this.isOpen()) {
                    int len = this.inputStream.read(this.buf);
                    if (-1 == len)  break;
                    if (0 < len)    this.doRead(this.buf, 0, len);
                }
            } catch (IOException e) { logger.error("Exception occurred while reading data.", e); }
            finally {
                try {this.close();}
                catch (IOException e) { logger.warn("Exception occurred while closing redis stream.", e); }
            }
        });
    }

    @Override
    public LIO write(byte[] buf, int off, int len) throws IOException {
        this.outputStream.write(buf, off, len);
        this.outputStream.flush();
        return this;
    }

    @Override
    public boolean isOpen() {
        return this.isOpen;
    }

    @Override
    public void close() throws IOException {
        this.isOpen = false;
        this.redisson.getBinaryStream(this.channel).delete();
        // Do not close redisson client.
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
