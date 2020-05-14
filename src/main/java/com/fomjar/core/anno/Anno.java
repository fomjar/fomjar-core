package com.fomjar.core.anno;

import com.fomjar.core.data.Struct;
import com.fomjar.core.data.StructScanFilter;
import com.fomjar.core.data.StructScanReader;

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
     * @param pack
     * @param reader
     * @throws IOException
     */
    public static void scan(String pack, AnnoScanReader reader) throws Exception {
        Anno.scan(Anno.class.getClassLoader(), pack, null, null, reader);
    }

    /**
     * 扫描指定包下的注解。
     *
     * @param loader
     * @param pack
     * @param reader
     * @throws IOException
     */
    public static void scan(ClassLoader loader, String pack, AnnoScanReader reader) throws Exception {
        Anno.scan(loader, pack, null, null, reader);
    }

    /**
     * 扫描指定包下的注解。
     *
     * @param pack
     * @param structScanFilter
     * @param annoScanFilter class-level filter
     * @param reader
     * @throws IOException
     */
    public static void scan(String pack, StructScanFilter structScanFilter, AnnoScanFilter annoScanFilter, AnnoScanReader reader) throws Exception {
        Anno.scan(Anno.class.getClassLoader(), pack, structScanFilter, annoScanFilter, reader);
    }

    /**
     * 扫描指定包下的注解。
     *
     * @param loader
     * @param pack
     * @param structScanFilter
     * @param annoScanFilter class-level filter
     * @param reader
     * @throws IOException
     */
    public static void scan(ClassLoader loader, String pack, StructScanFilter structScanFilter, AnnoScanFilter annoScanFilter, AnnoScanReader reader) throws Exception {
        Struct.scan(loader, pack, structScanFilter, new StructScanReader() {
            @Override
            public void read(Class<?> type) throws Exception {
                if (null != annoScanFilter && !annoScanFilter.filter(type.getAnnotations()))
                    return;

                reader.read(type.getAnnotations(), type);
            }
            @Override
            public void read(Class<?> type, Method method) throws Exception {
                if (null != annoScanFilter && !annoScanFilter.filter(type.getAnnotations()))
                    return;

                reader.read(method.getAnnotations(), type, method);
            }
            @Override
            public void read(Class<?> type, Method method, Parameter parameter) throws Exception {
                if (null != annoScanFilter && !annoScanFilter.filter(type.getAnnotations()))
                    return;

                reader.read(parameter.getAnnotations(), type, method, parameter);
            }
            @Override
            public void read(Class<?> type, Field field) throws Exception {
                if (null != annoScanFilter && !annoScanFilter.filter(type.getAnnotations()))
                    return;

                reader.read(field.getAnnotations(), type, field);
            }
        });
    }

    /**
     * 从一组注解中任意取出一个满足指定类型的注解。未找到指定类型则返回null。
     *
     * @param annotations
     * @param types
     * @return
     */
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
