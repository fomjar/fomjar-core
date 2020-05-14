package com.fomjar.core.anno;

import com.fomjar.core.data.Struct;
import org.junit.Test;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.net.URLClassLoader;

public class TestAnnoStatic {

    @Test
    public void testScanSelf() throws Exception {
        Anno.scan("com.fomjar.core", new AnnoScanAdapter() {

            @Override
            public void read(Annotation[] annotations, Class<?> clazz) {
                if (0 < annotations.length)
                    System.out.println(clazz);
                for (Annotation annotation : annotations) {
                    System.out.println("[CLASS  ANNO] " + annotation.annotationType());
                }
            }

            @Override
            public void read(Annotation[] annotations, Class<?> clazz, Method method) {
                if (0 < annotations.length)
                    System.out.println(method);
                for (Annotation annotation : annotations) {
                    System.out.println("[METHOD ANNO] " + annotation.annotationType());
                }
            }

            @Override
            public void read(Annotation[] annotations, Class<?> clazz, Method method, Parameter parameter) {
                if (0 < annotations.length)
                    System.out.println(parameter);
                for (Annotation annotation : annotations) {
                    System.out.println("[PARAM  ANNO] " + annotation.annotationType());
                }
            }

            @Override
            public void read(Annotation[] annotations, Class<?> clazz, Field field) {
                if (0 < annotations.length)
                    System.out.println(field);
                for (Annotation annotation : annotations) {
                    System.out.println("[FIELD  ANNO] " + annotation.annotationType());
                }
            }

        });
    }

    @SuppressWarnings("unchecked")
//    @Test
    public void testScanController() throws Exception {
        Anno.scan(new URLClassLoader(new URL[]{
                        new File("/Users/fomjar/Documents/work/code/df/df-common/target/classes").toURI().toURL(),
                        new File("/Users/fomjar/Documents/work/code/df/df-iot/target/classes").toURI().toURL(),
                        new File("/Users/fomjar/Documents/work/code/df/df-logistics/target/classes").toURI().toURL(),
                        new File("/Users/fomjar/Documents/work/code/df/df-portal/target/classes").toURI().toURL(),
                        new File("/Users/fomjar/Documents/work/code/df/df-push/target/classes").toURI().toURL(),
                        new File("/Users/fomjar/Documents/work/code/df/df-report/target/classes").toURI().toURL(),
                        new File("/Users/fomjar/Documents/work/code/df/df-settle/target/classes").toURI().toURL(),
                        new File("/Users/fomjar/Documents/work/code/df/df-video/target/classes").toURI().toURL(),
                }),
                "com.oceangreate.df",
                 null,
                AnnoScanFilter.any(Controller.class, RestController.class),
                new AnnoScanAdapter() {
                    @Override
                    public void read(Annotation[] annotations, Class<?> type, Method method) {
                        Annotation anno0 = Anno.any(type.getAnnotations(), RequestMapping.class, GetMapping.class, PostMapping.class);
                        Annotation anno1 = Anno.any(method.getAnnotations(), RequestMapping.class, GetMapping.class, PostMapping.class);
                        if (null == anno1) return;

                        String[] path0 = new String[0];
                        String[] path1 = new String[0];
                        try {
                            path0 = null == anno0 ? new String[] {} : Struct.call(anno0, String[].class, "value");
                            path1 = Struct.call(anno1, String[].class, "value");
                        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }

                        if (0 == path1.length) return;

                        for (String p0 : path0) {
                            for (String p1 : path1) {
                                String path = "/" + p0 + "/" + p1;
                                while (path.contains("//"))
                                    path = path.replace("//", "/");
                                while (path.endsWith("/"))
                                    path = path.substring(0, path.length() - 1);

                                System.out.println(path);
                            }
                        }
                    }
                });
    }

}
