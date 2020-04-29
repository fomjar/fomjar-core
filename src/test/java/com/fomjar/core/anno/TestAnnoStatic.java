package com.fomjar.core.anno;

import org.junit.Test;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class TestAnnoStatic {

    @Test
    @SuppressWarnings("unchecked")
    public void test() throws IOException, ClassNotFoundException {
        Anno.scan("com.fomjar.core", new AnnoAdapter() {

            @Override
            public void read(Annotation[] annotations, Class clazz) {
                if (0 < annotations.length)
                    System.out.println(clazz);
                for (Annotation annotation : annotations) {
                    System.out.println("[CLASS  ANNO] " + annotation.annotationType());
                }
            }

            @Override
            public void read(Annotation[] annotations, Class clazz, Method method) {
                if (0 < annotations.length)
                    System.out.println(method);
                for (Annotation annotation : annotations) {
                    System.out.println("[METHOD ANNO] " + annotation.annotationType());
                }
            }

            @Override
            public void read(Annotation[] annotations, Class clazz, Method method, Parameter parameter) {
                if (0 < annotations.length)
                    System.out.println(parameter);
                for (Annotation annotation : annotations) {
                    System.out.println("[PARAM  ANNO] " + annotation.annotationType());
                }
            }

            @Override
            public void read(Annotation[] annotations, Class clazz, Field field) {
                if (0 < annotations.length)
                    System.out.println(field);
                for (Annotation annotation : annotations) {
                    System.out.println("[FIELD  ANNO] " + annotation.annotationType());
                }
            }

        });
    }

}
