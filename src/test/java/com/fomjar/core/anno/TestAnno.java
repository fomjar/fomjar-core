package com.fomjar.core.anno;

import org.junit.Test;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class TestAnno {

    @Test
    public void test() throws IOException, ClassNotFoundException {
        Anno.scan("com.fomjar.core", new AnnoAdapter() {

            @Override
            public void read(Annotation[] annos, Class clazz) {
                if (0 < annos.length)
                    System.out.println(clazz);
                for (Annotation anno : annos) {
                    System.out.println("[CLASS  ANNO] " + anno.annotationType());
                }
            }

            @Override
            public void read(Annotation[] annos, Class clazz, Method method) {
                if (0 < annos.length)
                    System.out.println(method);
                for (Annotation anno : annos) {
                    System.out.println("[METHOD ANNO] " + anno.annotationType());
                }
            }

            @Override
            public void read(Annotation[] annos, Class clazz, Method method, Parameter parameter) {
                if (0 < annos.length)
                    System.out.println(parameter);
                for (Annotation anno : annos) {
                    System.out.println("[PARAM  ANNO] " + anno.annotationType());
                }
            }

            @Override
            public void read(Annotation[] annos, Class clazz, Field field) {
                if (0 < annos.length)
                    System.out.println(field);
                for (Annotation anno : annos) {
                    System.out.println("[FIELD  ANNO] " + anno.annotationType());
                }
            }

        });
    }

}
