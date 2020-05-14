package com.fomjar.core.data;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * DS scan reader.
 * @author fomjar
 */
public interface StructScanReader {

    void read(Class<?> type) throws Exception;

    void read(Class<?> type, Method method) throws Exception;

    void read(Class<?> type, Method method, Parameter parameter) throws Exception;

    void read(Class<?> type, Field field) throws Exception;

}
