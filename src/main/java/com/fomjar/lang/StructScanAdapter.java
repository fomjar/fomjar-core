package com.fomjar.lang;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * DS scan adapter.
 * @author fomjar
 */
public abstract class StructScanAdapter implements StructScanReader {

    @Override
    public void read(Class<?> type) throws Exception {
    }

    @Override
    public void read(Class<?> type, Method method) throws Exception {
    }

    @Override
    public void read(Class<?> type, Method method, Parameter parameter) throws Exception {
    }

    @Override
    public void read(Class<?> type, Field field) throws Exception {
    }
}
