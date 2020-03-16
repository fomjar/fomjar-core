package com.fomjar.core.el;

import com.fomjar.core.TestFomjarCoreApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestFomjarCoreApplication.class})
public class TestEL {

    @Autowired
    private EL el;

    @Test
    public void test() throws Exception {
        this.testEL(el);
        assert true;
    }

    private void testEL(EL el) throws Exception {
        el.register(Math.class);
        el.register(Long.class.getDeclaredMethod("valueOf", String.class));
        el.register("abs", args -> Math.abs(Integer.valueOf(args[0].toString())));

        System.out.println(el.eval("'[BEGIN " + el.getClass().getSimpleName() + "] ' + now('yyyy/MM/dd HH:mm:ss.SSS')"));  // 默认方法
        for (int i = -100; i< 100; i++) {
            el.eval("if(1 > 0, 'grater', 'smaller')");  // 默认方法
            el.eval("Math.PI + " + i);      // 类静态常量
            el.eval("Math.abs(" + i + ")"); // 类静态方法
            el.eval("valueOf('" + i + "')");  // 静态Method方法
            el.eval("abs(" + i + ")");      // 静态自定义方法
        }
        System.out.println(el.eval("'[ END  " + el.getClass().getSimpleName() + "] ' + now('yyyy/MM/dd HH:mm:ss.SSS')"));  // 默认方法
    }


}
