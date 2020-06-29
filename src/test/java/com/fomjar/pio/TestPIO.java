package com.fomjar.pio;

import com.fomjar.io.BufferPool;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class TestPIO {

    private static final Logger logger = LoggerFactory.getLogger(TestPIO.class);

    @BeforeClass
    public static void setup() throws IOException {
        InputStream is = TestPIO.class.getResourceAsStream("/test.py");
        new BufferPool().write(is).read(new File("test.py"));
        is.close();

    }

    @Test
    public void testPython() throws IOException, InterruptedException {
        PythonPIO pio = new PythonPIO();
        pio.startup()
                .readInput(new PIOLineReader() {
                    @Override
                    public void readLine(String line) {
                        logger.info("[OUT] {}", line);
                    }
                })
                .readError(new PIOLineReader() {
                    @Override
                    public void readLine(String line) {
                        logger.info("[ERR] {}", line);
                    }
                });

        pio.imp("test");
        pio.printer().println("print test.add(1, 2)");
        pio.printer().println("print test.minus(2, 3)");
        pio.printer().println("print test.multiply(2, 3)");
        pio.printer().println("print test.divide(3.0, 2)");

        pio.await(1, TimeUnit.SECONDS);
        pio.shutdown();

        assert true;
    }

    @Test
    public void testJava() throws IOException, InterruptedException {
        new PIO()
                .readInput(new PIOLineReader() {
                    @Override
                    public void readLine(String line) {
                        logger.info("[OUT] {}", line);
                    }
                })
                .readError(new PIOLineReader() {
                    @Override
                    public void readLine(String line) {
                        logger.info("[ERR] {}", line);
                    }
                })
                .startup("java -h")
                .await();
        assert true;
    }

}
