package com.fomjar.core.oss;

import org.junit.Test;

import java.io.IOException;

public class TestOSS {

//    @Test
    public void testMinioOSS() throws IOException {
        OSS oss = new MinioOSS().setup("http://127.0.0.1:9000",
                "HTZSKWNQC8Y5GO5Z9TXJ",
                "Q1NM+uA2i7Gl21P1EQcuPlVwWrF+msGDi99KsR8X");

        oss.bucket("test");
        long time = System.currentTimeMillis();
        System.out.println(oss.upload("test-" + time + ".txt", ("hello world! " + time).getBytes()));

        oss.shutdown();
        assert true;
    }

}
