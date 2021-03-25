package com.fomjar.io;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

public class TestBuffers {

    @Test
    public void test() throws IOException {
        try (InputStream is = TestBufferForwarder.class.getResourceAsStream("/pio.py")) {
            System.out.println(new Buffers()
                    .write("12312312312313\n")
                    .write(is)
                    .write(new byte[]{'1', '2', '3', '4', '5', '\n'})
                    .readString());
        }
    }

}
