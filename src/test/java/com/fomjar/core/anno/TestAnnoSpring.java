package com.fomjar.core.anno;

import com.fomjar.core.TestFomjarCoreApplication;
import com.fomjar.core.async.EventQueue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestFomjarCoreApplication.class})
public class TestAnnoSpring {

    @Test
    public void test() throws InterruptedException {
        Thread.sleep(1000L);
    }

    @AutoAnnoReader
    public static class MyAnnoReader extends AnnoAdapter {

        @Override
        public void read(Annotation[] annos, Class clazz, Method method) {
            boolean isBean = false;
            boolean isLazy = false;

            for (Annotation anno : annos) {
                if (anno instanceof Bean) isBean = true;
                if (anno instanceof Lazy) isLazy = true;
            }

            if (isBean && isLazy) {
                System.out.println("Annotation auto-scan result: @Bean && @Lazy : " + method.getName());
            }
        }

    }

}
