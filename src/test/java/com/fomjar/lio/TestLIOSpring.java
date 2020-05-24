package com.fomjar.lio;

import com.alibaba.fastjson.JSONObject;
import com.fomjar.TestFomjarCoreApplication;
import com.fomjar.lio.annotation.LIOConnect;
import com.fomjar.lio.annotation.LIOController;
import com.fomjar.lio.annotation.LIODisconnect;
import com.fomjar.lio.annotation.LIORequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestFomjarCoreApplication.class})
@LIOController
public class TestLIOSpring {

    @LIORequest
    public String accept(JSONObject json) {
        System.out.println("server received: " + json.toString());
        return json.toString();
    }

    @LIOConnect
    public void connect(LIO lio) {
        System.out.println(String.format("Client %s:%d connect", lio.remoteHost(), lio.remotePort()));
    }

    @LIODisconnect
    public void disconnect(LIO lio) {
        System.out.println(String.format("Client %s:%d disconnect", lio.remoteHost(), lio.remotePort()));
    }

    @Test
    public void test() throws IOException, InterruptedException, URISyntaxException {
//        LIO lio = new TCPLIO(new Socket("127.0.0.1", 9001));
        LIO lio = new WebSocketLIO(new URI("ws://127.0.0.1:" + 9001 + "/hello?a=1&b=2"));
        while (!lio.isOpen()) {
            Thread.sleep(100L);
        }
        for (int i = 0; i < 3; i++) {
            lio.write("{\"hello\" : \"world!\"}");
            Thread.sleep(200L);
        }
        lio.close();
    }

}
