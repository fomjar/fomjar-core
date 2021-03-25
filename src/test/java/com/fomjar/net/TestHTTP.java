package com.fomjar.net;

import com.alibaba.fastjson.JSONObject;
import com.fomjar.io.Buffers;
import org.junit.Test;
import org.w3c.dom.Document;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public class TestHTTP {

    @Test
    public void testString() throws IOException {
        HTTP.get("www.baidu.com", (Map<String, String> head, String body) -> {
            System.out.println(body);
        });
    }

    @Test
    public void testInputStream() throws IOException {
        HTTP.get("www.baidu.com", (Map<String, String> head, InputStream body) -> {
            try { System.out.println(new Buffers().write(body).readString()); }
            catch (UnsupportedEncodingException e) { e.printStackTrace(); }
        });
    }

    @Test
    public void testDocument() throws IOException {
        HTTP.get("www.baidu.com", (Map<String, String> head, Document body) -> {
            System.out.println(body.toString());
        });
    }

    @Test
    public void testJSONget() throws IOException {
        HTTP.get("https://www.baidu.com/sugrec?from=pc_web&json=1", (Map<String, String> head, JSONObject body) -> {
            System.out.println(body);
        });
    }

    @Test
    public void testJSONpost() throws IOException {
        HTTP.post("ynuf.alipay.com/service/um.json", "123123", (Map<String, String> head, JSONObject body) -> {
            System.out.println(body);
        });
    }


}
