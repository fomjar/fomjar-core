package com.fomjar.net;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

public class HTTP {

    /**
     * @param <T> {@link JSONObject}、{@link JSONArray}、{@link String}、{@link byte[]}
     */
    public interface Response<T> {
        void response(Map<String, String> head, T body);
    }

    public static HTTP open() { return new HTTP();
    }

    private static final PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();

    static {
        HTTP.manager.setMaxTotal(1000);
        HTTP.manager.setDefaultMaxPerRoute(100);
    }

    private String url;
    private Map<String, List<String>> params;
    private Map<String, String> header;
    private byte[] body;

    public HTTP() {
        this.params = new LinkedHashMap<>();
        this.header = new HashMap<>();

        this.contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
    }

    public HTTP url(String host, int port, String path) {
        return this.url("http", host, port, path);
    }

    public HTTP url(String schema, String host, int port, String path) {
        if (null == path) path = "/";
        if (!path.startsWith("/")) path = "/" + path;

        return this.url(String.format("%s://%s:%d%s", schema, host, port, path));
    }

    public HTTP url(String url) {
        if (!url.startsWith("http://")
                && !url.startsWith("https://"))
            url = "http://" + url;
        this.url = url;
        return this;
    }

    public HTTP param(String key, String... val) {
        this.params.putIfAbsent(key, new LinkedList<>());
        this.params.get(key).addAll(Arrays.asList(val));
        return this;
    }

    public String header(String key) {
        return this.header.get(key);
    }

    public HTTP header(String key, String val) {
        this.header.put(key, val);
        return this;
    }

    public HTTP body(Object body) {
        if (null != body) {
            if (body instanceof byte[])
                this.body = (byte[]) body;
            else
                this.body = body.toString().getBytes();

            this.contentTypeInfer();
        }
        return this;
    }

    private void contentTypeInfer() {
        if (null == this.body)
            return;

        String trim = new String(this.body).trim();
        if (0 == trim.length())
            return;

        char c = trim.charAt(0);
        switch (trim.charAt(0)) {
            case '{':
            case '[':
                this.contentTypeJSON();
                break;
            case '<':
                this.contentTypeXML();
                break;
            default:
                if (trim.contains("="))
                    this.contentTypeForm();
                break;
        }
    }

    public String contentType() {
        return this.header("Content-Type");
    }

    public HTTP contentType(String contentType) {
        return this.header("Content-Type", contentType);
    }

    public HTTP contentTypeJSON() {
        return this.contentType(MediaType.APPLICATION_JSON_VALUE);
    }

    public HTTP contentTypeXML() {
        return this.contentType(MediaType.APPLICATION_XML_VALUE);
    }

    public HTTP contentTypeForm() {
        return this.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);
    }

    private String urlParamsCombine() {
        String params = "";
        if (!this.params.isEmpty()) {
            params = this.params.entrySet()
                    .stream()
                    .map(e -> e.getValue()
                            .stream()
                            .map(v -> {
                                try { return URLEncoder.encode(e.getKey(), "utf-8") + "=" + URLEncoder.encode(v, "utf-8"); }
                                catch (UnsupportedEncodingException unsupportedEncodingException) { unsupportedEncodingException.printStackTrace(); }
                                return null;
                            })
                            .collect(Collectors.joining("&")))
                    .collect(Collectors.joining("&"));
        }
        return this.url
                + (0 < params.length() ? "?" : "")
                + params;
    }

    @SuppressWarnings("unchecked")
    public <T> void get(Response<T> response) throws IOException {
        HttpGet get = new HttpGet(this.urlParamsCombine());
        this.header.entrySet().forEach(e -> get.setHeader(e.getKey(), e.getValue()));

        CloseableHttpResponse r = HttpClients.custom().setConnectionManager(HTTP.manager).build().execute(get);
        try {
            Map<String, String> head = Arrays.stream(r.getAllHeaders()).collect(Collectors.toMap(NameValuePair::getName, NameValuePair::getValue));
            String contentType = head.get("Content-Type").toLowerCase();
            byte[] bytes = EntityUtils.toByteArray(r.getEntity());
            String string = new String(bytes);
            String trim = string.trim();
            if (trim.startsWith("{"))
                response.response(head, (T) JSONObject.parseObject(string));
            else if (trim.startsWith("["))
                response.response(head, (T) JSONArray.parseArray(string));
            else if (contentType.contains("text"))
                response.response(head, (T) string);
            else
                response.response(head, (T) bytes);
        } finally {
            r.close();
        }
    }

    @SuppressWarnings("unchecked")
    public <T> void post(Response<T> response) throws IOException {
        HttpPost post = new HttpPost(this.urlParamsCombine());
        post.setEntity(new ByteArrayEntity(this.body));
        this.header.entrySet().forEach(e -> post.setHeader(e.getKey(), e.getValue()));

        CloseableHttpResponse r = HttpClients.custom().setConnectionManager(HTTP.manager).build().execute(post);
        try {
            Map<String, String> head = Arrays.stream(r.getAllHeaders()).collect(Collectors.toMap(NameValuePair::getName, NameValuePair::getValue));
            String contentType = head.get("Content-Type").toLowerCase();
            byte[] bytes = EntityUtils.toByteArray(r.getEntity());
            String string = new String(bytes);
            String trim = string.trim();
            if (trim.startsWith("{"))
                response.response(head, (T) JSONObject.parseObject(string));
            else if (trim.startsWith("["))
                response.response(head, (T) JSONArray.parseArray(string));
            else if (contentType.contains("text"))
                response.response(head, (T) string);
            else
                response.response(head, (T) bytes);
        } finally {
            r.close();
        }
    }

}
