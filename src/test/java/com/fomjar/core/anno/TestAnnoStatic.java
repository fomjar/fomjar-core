package com.fomjar.core.anno;

import org.junit.Test;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class TestAnnoStatic {

    @Test
    @SuppressWarnings("unchecked")
    public void testScanSelf() throws IOException, ClassNotFoundException {
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

    @Test
    public void testScanRequestMapping() throws IOException, ClassNotFoundException {
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
                new AnnoAdapter() {
                    @Override
                    public void read(Annotation[] annotations, Class clazz, Method method) {
                        if (!Anno.match(method, RequestMapping.class)
                                && !Anno.match(method, PostMapping.class)
                                && !Anno.match(method, GetMapping.class))
                            return;

                        String[] path0 = null;
                        String[] path1 = null;
                        for (Annotation anno : clazz.getAnnotations()) {
                            if (RequestMapping.class.isAssignableFrom(anno.annotationType())) {
                                path0 = ((RequestMapping) anno).value();
                            }
                            if (GetMapping.class.isAssignableFrom(anno.annotationType())) {
                                path0 = ((GetMapping) anno).value();
                            }
                            if (PostMapping.class.isAssignableFrom(anno.annotationType())) {
                                path0 = ((PostMapping) anno).value();
                            }
                        }
                        for (Annotation anno : method.getAnnotations()) {
                            if (RequestMapping.class.isAssignableFrom(anno.annotationType())) {
                                path1 = ((RequestMapping) anno).value();
                            }
                            if (GetMapping.class.isAssignableFrom(anno.annotationType())) {
                                path1 = ((GetMapping) anno).value();
                            }
                            if (PostMapping.class.isAssignableFrom(anno.annotationType())) {
                                path1 = ((PostMapping) anno).value();
                            }
                        }
                        if (null == path1 || 0 == path1.length)
                            return;

                        path0 = null == path0 ? new String[] {} : path0;
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
