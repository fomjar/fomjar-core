package com.fomjar.core.anno;

import org.springframework.core.env.PropertyResolver;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;

/**
 * Easy to scan annotation.
 *
 * @author fomjar
 */
public abstract class Anno {


    private static final ResourcePatternResolver    res = new PathMatchingResourcePatternResolver();
    private static final MetadataReaderFactory      reg = new SimpleMetadataReaderFactory();
    private static final PropertyResolver           env = new StandardEnvironment();


    /**
     * 扫描指定包下的注解。
     *
     * @param packaje
     * @param reader
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static void scan(String packaje, AnnoReader reader) throws IOException, ClassNotFoundException {
        String resourcePath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                + ClassUtils.convertClassNameToResourcePath(Anno.env.resolveRequiredPlaceholders(packaje))
                + "/**/*.class";
        for (Resource resource : Anno.res.getResources(resourcePath)) {
            Class clazz = Class.forName(Anno.reg.getMetadataReader(resource).getClassMetadata().getClassName());
            Anno.scan(clazz, reader);
        }
    }

    /**
     * 扫描指定类下的注解。
     *
     * @param clazz
     * @param reader
     */
    public static void scan(Class clazz, AnnoReader reader) {
        // Class-level annotations
        reader.read(clazz.getAnnotations(), clazz);

        // Public methods within this class and super classes
        for (Method method : clazz.getMethods()) {
            reader.read(method.getAnnotations(), clazz, method);

            // Method parameters annotations
            for (Parameter parameter : method.getParameters()) {
                reader.read(parameter.getAnnotations(), clazz, method, parameter);
            }
        }

        // Non-public methods within this class
        for (Method method : clazz.getDeclaredMethods()) {
            if (Modifier.isPublic(method.getModifiers()))
                continue;

            reader.read(method.getAnnotations(), clazz, method);

            // Method parameters annotations
            for (Parameter parameter : method.getParameters()) {
                reader.read(parameter.getAnnotations(), clazz, method, parameter);
            }
        }

        // Public fields within this class and super classes
        for (Field field : clazz.getFields())
            reader.read(field.getAnnotations(), clazz, field);

        // Non-public fields within this class
        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isPublic(field.getModifiers()))
                continue;

            reader.read(field.getAnnotations(), clazz, field);
        }
    }

}
