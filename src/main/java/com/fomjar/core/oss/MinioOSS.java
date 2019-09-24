package com.fomjar.core.oss;

import com.fomjar.core.coll.Maps;
import io.minio.MinioClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * OSS的Minio实现。
 *
 * @author fomjar
 */
public class MinioOSS extends OSS {

    private MinioClient client;

    public MinioOSS setup(String endPoint, String accessKey, String secretKey) {
        this.shutdown();
        try {this.client = new MinioClient(endPoint, accessKey, secretKey);}
        catch (Exception e) {throw new IllegalArgumentException(e);}
        return this;
    }

    @Override
    public String upload(String name, InputStream is) throws IOException {
        if (null == this.bucket() || 0 == this.bucket().length())
            throw new IllegalStateException("Empty bucket specified!");

        try {
            if (!this.client.bucketExists(this.bucket()))
                this.client.makeBucket(this.bucket());

            this.client.putObject(this.bucket(),
                    name,
                    is,
                    null,
                    Maps.wrap(new HashMap<String, String>())
                            .put("Content-Disposition", String.format("attachment; filename*=utf-8''%s", URLEncoder.encode(name, "utf-8")))
                            .get(),
                    null,
                    filename2contenttype(name));
            return this.client.getObjectUrl(this.bucket(), name);
        } catch (Exception e) {throw new IOException(e);}
    }

    @Override
    public void shutdown() {
        this.client = null;
    }
}
