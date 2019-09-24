package com.fomjar.core.oss;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Date;

/**
 * OSS的AliyunOSS实现。
 *
 * @author fomjar
 */
public class AliyunOSS extends OSS {

    private OSSClient client;

    public AliyunOSS setup(String endPoint, String accessKey, String secretKey) {
        this.shutdown();
        this.client = new OSSClient(endPoint, accessKey, secretKey);
        return this;
    }

    @Override
    public String upload(String name, InputStream is) throws IOException {
        if (null == this.bucket() || 0 == this.bucket().length())
            throw new IllegalStateException("Empty bucket specified!");

        try {
            if (!this.client.doesBucketExist(this.bucket()))
                this.client.createBucket(this.bucket());

            // 创建上传Object的Metadata
            ObjectMetadata meta = new ObjectMetadata();
            // 指定该Object被下载时的网页的缓存行为
            meta.setCacheControl("no-cache");
            // 指定该Object下设置Header
            meta.setHeader("Pragma", "no-cache");
            // 指定该Object被下载时的内容编码格式
            meta.setContentEncoding("utf-8");
            // 文件的MIME，定义文件的类型及网页编码，决定浏览器将以什么形式、什么编码读取文件。如果用户没有指定则根据Key或文件名的扩展名生成，
            // 如果没有扩展名则填默认值application/octet-stream
            meta.setContentType(filename2contenttype(name));
            // 指定该Object被下载时的名称（指示MINME用户代理如何显示附加的文件，打开或下载，及文件名称）
            meta.setContentDisposition(String.format("attachment; filename*=utf-8''%s", URLEncoder.encode(name, "utf-8")));

            this.client.putObject(this.bucket(), name, is, meta);
            return this.client.generatePresignedUrl(this.bucket(), name, new Date(System.currentTimeMillis() + 1000L * 3600 * 24 * 365 * 99)).toString();
        } catch (Exception e) {throw new IOException(e);}
    }

    @Override
    public void shutdown() {
        if (null != this.client) this.client.shutdown();
        this.client = null;
    }

}
