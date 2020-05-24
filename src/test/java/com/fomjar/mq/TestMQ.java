package com.fomjar.mq;

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