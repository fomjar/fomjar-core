package com.fomjar.io;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

public class TestBufferForwarder {

    @Test
    public void test() throws IOException, ExecutionException, InterruptedException {
        try (InputStream is = TestBufferForwarder.class.getResourceAsStream("/pio.py")) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BufferForwarder.forward(is, baos).get();
            System.out.println(baos.toString());
        }
    }

}
