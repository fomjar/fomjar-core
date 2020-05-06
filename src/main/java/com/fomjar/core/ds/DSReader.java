package com.fomjar.core.ds;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * DS scan reader.
 * @author fomjar
 */
public interface DSReader {

    void read(Class<?> type);

    void read(Class<?> type, Method method);

    void read(Class<?> type, Method method, Parameter parameter);

    void read(Class<?> type, Field field);

}
