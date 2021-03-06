package com.fomjar.lang;

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

    void read(Annotation[] annotations, Class<?> clazz);

    void read(Annotation[] annotations, Class<?> clazz, Method method);

    void read(Annotation[] annotations, Class<?> clazz, Method method, Parameter parameter);

    void read(Annotation[] annotations, Class<?> clazz, Field field);

}
