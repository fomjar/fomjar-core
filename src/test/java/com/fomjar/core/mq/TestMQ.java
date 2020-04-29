package com.fomjar.core.mq;

import com.fomjar.core.TestFomjarCoreApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
/*
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestFomjarCoreApplication.class})
public class TestMQ {

    @Autowired
    private MQ mq;

    @Test
    public void test() throws InterruptedException {
        this.mq.consume("test", msg -> {
            System.out.println(msg.toString());
        });
        for (int i = 0; i < 3; i++) {
            this.mq.produce(new MQMsg().tag("test").data("Hello world!"));
            Thread.sleep(200L);
        }
    }

}
*/