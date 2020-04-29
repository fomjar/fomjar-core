package com.fomjar.core.anno;

import com.fomjar.core.TestFomjarCoreApplication;
import com.fomjar.core.spring.Beans;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestFomjarCoreApplication.class})
public class TestAnnoSpring {

    @Test
    @SuppressWarnings("unchecked")
    public void test() {
        Beans.get(RunWith.class, SpringBootTest.class).values().forEach(bean -> {
            System.out.println(bean.getClass().getName());
        });
    }

}
