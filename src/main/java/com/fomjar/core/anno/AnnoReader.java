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
public interface AnnoReader<T> {

    void read(Annotation[] annotations, Class<T> clazz);

    void read(Annotation[] annotations, Class<T> clazz, Method method);

    void read(Annotation[] annotations, Class<T> clazz, Method method, Parameter parameter);

    void read(Annotation[] annotations, Class<T> clazz, Field field);

}
