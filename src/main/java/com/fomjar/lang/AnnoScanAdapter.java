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
    public void read(Annotation[] annotations, Class<?> clazz) {

    }

    @Override
    public void read(Annotation[] annotations, Class<?> clazz, Method method) {

    }

    @Override
    public void read(Annotation[] annotations, Class<?> clazz, Method method, Parameter parameter) {

    }

    @Override
    public void read(Annotation[] annotations, Class<?> clazz, Field field) {

    }

}
