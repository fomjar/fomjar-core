package com.fomjar.net;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.fomjar.lang.Task;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

public class HTTP {

    private static final Logger logger = LoggerFactory.getLogger(HTTP.class);

    /**
     * Available body type:
     *
     * <ul>
     *     <li>{@link Reader}</li>
     *     <li>{@link InputStreamReader}</li>
     *     <li>{@link BufferedReader}</li>
     *     <li>{@link InputStream}</li>
     *     <li>{@link BufferedInputStream}</li>
     *     <li>{@link byte[]}</li>
     *     <li>{@link String}</li>
     *     <li>{@link JSONObject}</li>
     *     <li>{@link Map}</li>
     *     <li>{@link JSONArray}</li>
     *     <li>{@link List}</li>
     *     <li>{@link org.w3c.dom.Document}</li>
     * </ul>
     *
     * @param <T>
     */
    public interface Response<T> {
        void response(Map<String, String> head, T body);
    }

    public static HTTP open() { return new HTTP(); }
    public static void get(String url, Response<?> response) throws IOException {
        HTTP.open()
                .url(url)
                .get(response);
    }
    public static void post(String url, Object body, Response<?> response) throws IOException {
        HTTP.open()
                .url(url)
                .body(body)
                .post(response);

    }

    private static final PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
    private static final HttpClientBuilder builder = HttpClients.custom();

    static {
        HTTP.manager.setMaxTotal(1000);
        HTTP.manager.setDefaultMaxPerRoute(100);
        HTTP.builder.setConnectionManager(HTTP.manager);
    }

    private String url;
    private Map<String, List<String>> params;
    private Map<String, String> header;
    private InputStream body;

    private HTTP() {
        this.params = new LinkedHashMap<>();
        this.header = new HashMap<>();
        this.body   = new ByteArrayInputStream(new byte[0]);

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

        if (url.contains("?")) {
            String[] urls = url.split("\\?");
            for (String kv : url.substring(urls[0].length() + 1).split("&")) {
                if (0 < kv.length()) {
                    String[] kvs = kv.split("=");
                    if (0 < kvs.length) {
                        String k = kvs[0];
                        String v = kvs.length <= 1 ? "" : kv.substring(k.length() + 1);
                        this.param(k, v);
                    }
                }
            }
            url = urls[0];
        }

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
            if (body instanceof InputStream)
                this.body = (InputStream) body;
            else if (body instanceof byte[])
                this.body = new ByteArrayInputStream((byte[]) body);
            else
                this.body = new ByteArrayInputStream(body.toString().getBytes());
        }
        return this;
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
                            .map(v -> Task.catchdo(() -> URLEncoder.encode(e.getKey(), "utf-8") + "=" + URLEncoder.encode(v, "utf-8")))
                            .collect(Collectors.joining("&")))
                    .collect(Collectors.joining("&"));
        }
        return this.url
                + (0 < params.length() ? "?" : "")
                + params;
    }

    public <T> void get(Response<T> response) throws IOException {
        HttpGet get = new HttpGet(this.urlParamsCombine());
        this.header.forEach(get::setHeader);

        logger.info("[HTTP GET ] Request: {}", get.getURI().toURL().toString());
        try (CloseableHttpResponse r = HTTP.builder.build().execute(get)) {
            logger.info("[HTTP GET ] Response: {}", r.getStatusLine());
            Map<String, String> head = Arrays.stream(r.getAllHeaders()).collect(Collectors.toMap(NameValuePair::getName, NameValuePair::getValue));
            this.doResponse(response, head, r.getEntity());
        }
    }

    public <T> void post(Response<T> response) throws IOException {
        HttpPost post = new HttpPost(this.urlParamsCombine());
        post.setEntity(new InputStreamEntity(this.body));
        this.header.forEach(post::setHeader);

        logger.info("[HTTP POST] Request: {}", post.getURI().toURL().toString());
        try (CloseableHttpResponse r = HTTP.builder.build().execute(post)) {
            logger.info("[HTTP POST] Response: {}", r.getStatusLine());
            Map<String, String> head = Arrays.stream(r.getAllHeaders()).collect(Collectors.toMap(NameValuePair::getName, NameValuePair::getValue));
            this.doResponse(response, head, r.getEntity());
        }
    }

    @SuppressWarnings("unchecked")
    private <T> void doResponse(Response<T> response, Map<String, String> head, HttpEntity body) throws IOException {
        // Reader, InputStreamReader, BufferedReader
        try { response.response(head, (T) new BufferedReader(new InputStreamReader(body.getContent()))); return; }
        catch (ClassCastException e) { }
        // InputStream, BufferedInputStream
        try { response.response(head, (T) new BufferedInputStream(body.getContent())); return; }
        catch (ClassCastException e) { }

        // byte[]
        byte[] bytes = EntityUtils.toByteArray(body);
        try { response.response(head, (T) bytes); return; }
        catch (ClassCastException e) { }
        // String
        String string = EntityUtils.toString(new ByteArrayEntity(bytes));
        try { response.response(head, (T) string); return; }
        catch (ClassCastException e) { }
        // JSONObject, Map<String, Object>
        try { response.response(head, (T) JSONObject.parseObject(string)); return; }
        catch (JSONException e) {  }
        catch (ClassCastException e) { }
        // JSONArray, List<Object>
        try { response.response(head, (T) JSONArray.parseArray(string)); return; }
        catch (JSONException e) {  }
        catch (ClassCastException e) { }

        // Document
        HTTP.checkDocumentBuilder();
        try { response.response(head, (T) HTTP.documentBuilder.parse(new ByteArrayInputStream(bytes))); return; }
        catch (SAXException e) { }
        catch (ClassCastException e) { }

        logger.error("Failed to infer response callback type: {}", response);
    }

    private static DocumentBuilder documentBuilder = null;

    private static void checkDocumentBuilder() {
        if (null == HTTP.documentBuilder) {
            synchronized (HTTP.class) {
                if (null == HTTP.documentBuilder) {
                    try { HTTP.documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder(); }
                    catch (ParserConfigurationException e) { logger.error("Error occurred while initializing DocumentBuilder", e); }
                }
            }
        }
    }

}
