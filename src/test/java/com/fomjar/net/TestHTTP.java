package com.fomjar.net;

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

public class TestHTTP {

    @Test
    public void testString() throws IOException {
        HTTP.open()
                .url("www.baidu.com")
                .get((Map<String, String> head, String body) -> {
            System.out.println(body);
        });
    }

    @Test
    public void testJSONget() throws IOException {
        HTTP.open()
                .url("https://www.baidu.com/sugrec")
                .param("from", "pc_web")
                .param("json", "1")
                .get((Map<String, String> head, JSONObject body) -> {
            System.out.println(body);
        });
    }

    @Test
    public void testJSONpost() throws IOException {
        HTTP.open()
                .url("ynuf.alipay.com/service/um.json")
                .body("123123")
                .post((Map<String, String> head, JSONObject body) -> {
            System.out.println(body);
        });
    }


}
