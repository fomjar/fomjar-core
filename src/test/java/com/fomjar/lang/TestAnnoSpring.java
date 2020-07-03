package com.fomjar.lang;

import com.fomjar.TestFomjarCoreApplication;
import com.fomjar.spring.Beans;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestFomjarCoreApplication.class})
public class TestAnnoSpring {

    @Test
    public void test() {
        Beans.get(RunWith.class, SpringBootTest.class).values().forEach(bean -> {
            System.out.println(bean.getClass().getName());
        });
    }

}
