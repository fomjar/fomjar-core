package com.fomjar.lang;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * Annotation Scan Adapter.
 *
 * @author fomjar
 */
public abstract class AnnoScanAdapter implements AnnoScanReader {

    @Override
    public void read(Annotation[] annotations, Class<?> clazz) throws Exception {

    }

    @Override
    public void read(Annotation[] annotations, Class<?> clazz, Method method) throws Exception {

    }

    @Override
    public void read(Annotation[] annotations, Class<?> clazz, Method method, Parameter parameter) throws Exception {

    }

    @Override
    public void read(Annotation[] annotations, Class<?> clazz, Field field) throws Exception {

    }

}
