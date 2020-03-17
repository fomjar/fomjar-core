package com.fomjar.core.anno;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public interface AnnoWatcher<T> {

    void watch(T bean, Annotation[] annos);

    void watch(T bean, Annotation[] annos, Method method);

    void watch(T bean, Annotation[] annos, Method method, Parameter parameter);

    void watch(T bean, Annotation[] annos, Field field);

}
