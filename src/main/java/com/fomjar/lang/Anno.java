package com.fomjar.lang;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * Easy to scan annotation.
 *
 * @author fomjar
 */
public abstract class Anno {

    /**
     * 扫描指定包下的注解。
     *
     * @param pack 包路径
     * @param reader 读取器
     * @throws IOException 读取失败
     */
    public static void scan(String pack, AnnoScanReader reader) throws IOException {
        Anno.scan(Anno.class.getClassLoader(), pack, null, null, reader);
    }

    /**
     * 扫描指定包下的注解。
     *
     * @param loader 指定的类加载器
     * @param pack 包路径
     * @param reader 读取器
     * @throws IOException 读取失败
     */
    public static void scan(ClassLoader loader, String pack, AnnoScanReader reader) throws IOException {
        Anno.scan(loader, pack, null, null, reader);
    }

    /**
     * 扫描指定包下的注解。
     *
     * @param pack 包路径
     * @param structScanFilter 类过滤器
     * @param annoScanFilter 注解过滤器
     * @param reader 读取器
     * @throws IOException 读取失败
     */
    public static void scan(String pack, StructScanFilter structScanFilter, AnnoScanFilter annoScanFilter, AnnoScanReader reader) throws IOException {
        Anno.scan(Anno.class.getClassLoader(), pack, structScanFilter, annoScanFilter, reader);
    }

    /**
     * 扫描指定包下的注解。
     *
     * @param loader 指定的类加载器
     * @param pack 包路径
     * @param structScanFilter 类过滤器
     * @param annoScanFilter 注解过滤器
     * @param reader 读取器
     * @throws IOException 读取失败
     */
    public static void scan(ClassLoader loader, String pack, StructScanFilter structScanFilter, AnnoScanFilter annoScanFilter, AnnoScanReader reader) throws IOException {
        Struct.scan(loader, pack, structScanFilter, new StructScanReader() {
            @Override
            public void read(Class<?> type) {
                if (null != annoScanFilter && !annoScanFilter.filter(type.getAnnotations()))
                    return;

                reader.read(type.getAnnotations(), type);
            }
            @Override
            public void read(Class<?> type, Method method) {
                if (null != annoScanFilter && !annoScanFilter.filter(type.getAnnotations()))
                    return;

                reader.read(method.getAnnotations(), type, method);
            }
            @Override
            public void read(Class<?> type, Method method, Parameter parameter) {
                if (null != annoScanFilter && !annoScanFilter.filter(type.getAnnotations()))
                    return;

                reader.read(parameter.getAnnotations(), type, method, parameter);
            }
            @Override
            public void read(Class<?> type, Field field) {
                if (null != annoScanFilter && !annoScanFilter.filter(type.getAnnotations()))
                    return;

                reader.read(field.getAnnotations(), type, field);
            }
        });
    }

    /**
     * 从一组注解中任意取出一个满足指定类型的注解。未找到指定类型则返回null。
     *
     * @param annotations 待查找的注解
     * @param types 匹配类型
     * @return 匹配到的注解
     */
    @SafeVarargs
    public static Annotation any(Annotation[] annotations, Class<? extends Annotation>... types) {
        for (Class<? extends Annotation> type : types) {
            for (Annotation annotation : annotations) {
                if (type.isAssignableFrom(annotation.annotationType()))
                    return annotation;
            }
        }
        return null;
    }

}
