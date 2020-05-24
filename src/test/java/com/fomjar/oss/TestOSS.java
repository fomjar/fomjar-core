package com.fomjar.oss;

/*
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestFomjarCoreApplication.class})
public class TestOSS {

    @Autowired
    private OSS oss;

    @Test
    public void testMinioOSS() throws IOException {
//        OSS oss = new MinioOSS().setup("http://127.0.0.1:9000",
//                "HTZSKWNQC8Y5GO5Z9TXJ",
//                "Q1NM+uA2i7Gl21P1EQcuPlVwWrF+msGDi99KsR8X");
//
//        oss.bucket("test");
        long time = System.currentTimeMillis();
        System.out.println(oss.upload("test-" + time + ".txt", ("hello world! " + time).getBytes()));
    }

}*/
