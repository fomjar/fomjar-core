package com.fomjar.core.anno;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * Annotation Scan Adapter.
 *
 * @author fomjar
 */
public abstract class AnnoAdapter implements AnnoReader {

    @Override
    public void read(Annotation[] annos, Class clazz) {

    }

    @Override
    public void read(Annotation[] annos, Class clazz, Method method) {

    }

    @Override
    public void read(Annotation[] annos, Class clazz, Method method, Parameter parameter) {

    }

    @Override
    public void read(Annotation[] annos, Class clazz, Field field) {

    }

}
