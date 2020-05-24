package com.fomjar.el;

import com.fomjar.TestFomjarCoreApplication;
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
//    private EL el = new FreeMarkerEL();

    @Test
    public void testDefaults() {
        System.out.println(this.el.eval("now('yyyy/MM/dd HH/mm/ss.SSS')"));
        System.out.println(this.el.eval("randomBoolean()"));
        System.out.println(this.el.eval("randomInt()"));
        System.out.println(this.el.eval("randomLong()"));
        System.out.println(this.el.eval("randomFloat()"));
        System.out.println(this.el.eval("randomDouble()"));
        System.out.println(this.el.eval("length('abcde')"));
        System.out.println(this.el.eval("indexOf('abcde', 'cd')"));
        System.out.println(this.el.eval("lastIndexOf('abcde', 'cd')"));
        System.out.println(this.el.eval("trim('  abcde')"));
        System.out.println(this.el.eval("reverse('abcde')"));
        System.out.println(this.el.eval("substring('abcde', 1, 3)"));
        System.out.println(this.el.eval("substring('abcde', 3)"));
        System.out.println(this.el.eval("replace('abcde', 'c', 'm')"));
        System.out.println(this.el.eval("split('abcde', 'c')[0]"));
        System.out.println(this.el.eval("if(1 > 0, 'g', 'l')"));
        System.out.println(this.el.eval("ifblank('', 'abcd')"));
        System.out.println(this.el.eval("ifblank('1234', 'abcd')"));
        System.out.println(this.el.eval("Math.PI"));
        System.out.println(this.el.eval("Math.E"));
        System.out.println(this.el.eval("Math.abs(-15)"));
        System.out.println(this.el.eval("Math.sqrt(2)"));
    }

    @Test
    public void testTemplate() {
        System.out.println(this.el.eval("'123' + 'abc'"));
    }

}
