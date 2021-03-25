package com.fomjar.oss;

import com.fomjar.lang.Task;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * OSS的Minio实现。
 *
 * @author fomjar
 */
public class MinioOSS extends OSS {

    private static final Logger logger = LoggerFactory.getLogger(MinioOSS.class);

    private MinioClient client;

    public MinioOSS setup(String endPoint, String accessKey, String secretKey) {
        this.shutdown();
        this.client = MinioClient.builder()
                .endpoint(endPoint)
                .credentials(accessKey, secretKey)
                .build();
        return this;
    }

    @Override
    public String upload(String name, InputStream is) throws IOException {
        if (null == this.bucket() || 0 == this.bucket().length())
            throw new IllegalStateException("Empty bucket specified!");

        try {
            if (!this.client.bucketExists(BucketExistsArgs.builder().bucket(this.bucket()).build()))
                this.client.makeBucket(MakeBucketArgs.builder().bucket(this.bucket()).build());

            this.client.putObject(PutObjectArgs.builder()
                    .bucket(this.bucket())
                    .object(name)
                    .stream(is, 0, 0)
                    .headers(Task.catchdo(() -> {
                        Map<String, String> map = new HashMap<>();
                        map.put("Content-Disposition", String.format("attachment; filename*=utf-8''%s", URLEncoder.encode(name, "utf-8")));
                        return map;
                    }))
                    .contentType(OSS.filename2contenttype(name))
                    .build());
            return this.client.getObjectUrl(this.bucket(), name);
        } catch (Exception e) {throw new IOException(e);}
    }

    @Override
    public void shutdown() {
        this.client = null;
    }
}
