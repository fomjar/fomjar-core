package com.fomjar.core.anno;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * Annotation Scan Reader.
 *
 * @author fomjar
 */
public interface AnnoScanReader {

    void read(Annotation[] annotations, Class<?> clazz) throws Exception;

    void read(Annotation[] annotations, Class<?> clazz, Method method) throws Exception;

    void read(Annotation[] annotations, Class<?> clazz, Method method, Parameter parameter) throws Exception;

    void read(Annotation[] annotations, Class<?> clazz, Field field) throws Exception;

}
