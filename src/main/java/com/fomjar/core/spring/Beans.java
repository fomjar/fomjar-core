package com.fomjar.core.spring;

import com.fomjar.core.anno.Anno;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Spring bean toolkit.
 *
 * @author fomjar
 */
@Component
public class Beans implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(@SuppressWarnings("NullableProblems") ApplicationContext applicationContext) throws BeansException {
        Beans.applicationContext = applicationContext;
    }

    public static <T> T get(Class<T> type) {
        return Beans.applicationContext.getBean(type);
    }

    public static Object get(String name) {
        return Beans.applicationContext.getBean(name);
    }

    public static <T> T get(String name, Class<T> type) {
        return Beans.applicationContext.getBean(name, type);
    }

    @SafeVarargs
    public static Map<String, Object> get(Class<? extends Annotation>... types) {
        if (null == types || 0 == types.length) return null;

        return Beans.applicationContext.getBeansWithAnnotation(types[0])
                .entrySet()
                .stream()
                .filter(e -> null != Anno.any(e.getValue().getClass().getAnnotations(), types))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static <T> Map<String, T> getAll(Class<T> type) {
        return Beans.applicationContext.getBeansOfType(type);
    }

}
