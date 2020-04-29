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
import java.lang.annotation.Annotation;
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

    private static final PropertyResolver           env = new StandardEnvironment();

    /**
     * 扫描指定包下的注解。
     *
     * @param pack
     * @param reader
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static <T> void scan(String pack, AnnoReader<T> reader) throws IOException, ClassNotFoundException {
        Anno.scan(Anno.class.getClassLoader(), pack, reader);
    }

    /**
     * 扫描指定包下的注解。
     *
     * @param loader
     * @param pack
     * @param reader
     * @param <T>
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
    public static <T> void scan(ClassLoader loader, String pack, AnnoReader<T> reader) throws IOException {
        String resourcePath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                + ClassUtils.convertClassNameToResourcePath(Anno.env.resolveRequiredPlaceholders(pack))
                + "/**/*.class";
        ResourcePatternResolver res = new PathMatchingResourcePatternResolver(loader);
        MetadataReaderFactory reg = new SimpleMetadataReaderFactory(loader);

        for (Resource resource : res.getResources(resourcePath)) {
            try {
                Class<T> clazz = (Class<T>) Class.forName(reg.getMetadataReader(resource).getClassMetadata().getClassName(), true, loader);
                Anno.scan(clazz, reader);
            } catch (ClassNotFoundException | NoClassDefFoundError | ExceptionInInitializerError e) {}
        }
    }

    /**
     * 扫描指定类下的注解。
     *
     * @param clazz
     * @param reader
     */
    public static <T> void scan(Class<T> clazz, AnnoReader<T> reader) {
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

    private static boolean match(Annotation[] src, Class<? extends Annotation>[] dst) {
        for (Class<? extends Annotation> anno0 : dst) {
            boolean have = false;
            for (Annotation anno1 : src) {
                if (anno0.isAssignableFrom(anno1.annotationType())) {
                    have = true;
                    break;
                }
            }
            if (!have) return false;
        }
        return true;
    }

    /**
     * 判断注解是否匹配。
     *
     * @param clazz
     * @param annotations
     * @return
     */
    public static boolean match(Class<?> clazz, Class<? extends Annotation>... annotations) {
        return Anno.match(clazz.getAnnotations(), annotations);
    }

    /**
     * 判断注解是否匹配。
     *
     * @param method
     * @param annotations
     * @return
     */
    public static boolean match(Method method, Class<? extends Annotation>... annotations) {
        return Anno.match(method.getAnnotations(), annotations);
    }

    /**
     * 判断注解是否匹配。
     *
     * @param parameter
     * @param annotations
     * @return
     */
    public static boolean match(Parameter parameter, Class<? extends Annotation>... annotations) {
        return Anno.match(parameter.getAnnotations(), annotations);
    }

    /**
     * 判断注解是否匹配。
     *
     * @param field
     * @param annotations
     * @return
     */
    public static boolean match(Field field, Class<? extends Annotation>... annotations) {
        return Anno.match(field.getAnnotations(), annotations);
    }

}
