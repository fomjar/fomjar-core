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
public interface AnnoReader {

    void read(Annotation[] annos, Class clazz);

    void read(Annotation[] annos, Class clazz, Method method);

    void read(Annotation[] annos, Class clazz, Method method, Parameter parameter);

    void read(Annotation[] annos, Class clazz, Field field);

}
