package com.fomjar.core.net;

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

public class TestHTTP {

    @Test
    public void test() throws IOException {
        HTTP.open()
                .url("www.baidu.com")
                .get((Map<String, String> head, String body) -> {
            System.out.println(body);
        });
        HTTP.open()
                .url("https://www.baidu.com/sugrec")
                .params("from", "pc_web")
                .params("json", "1")
                .get((Map<String, String> head, JSONObject body) -> {
            System.out.println(body);
        });
    }

}
