package com.fomjar.core.spring;

import com.fomjar.core.anno.Anno;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Beans.applicationContext = applicationContext;
    }

    public static <T> T get(Class<T> clazz) {
        return Beans.applicationContext.getBean(clazz);
    }

    public static Object get(String name) {
        return Beans.applicationContext.getBean(name);
    }

    public static <T> T get(String name, Class<T> clazz) {
        return Beans.applicationContext.getBean(name, clazz);
    }

    public static Map<String, Object> get(Class<? extends Annotation>... annotations) {
        if (null == annotations || 0 == annotations.length) return null;

        return Beans.applicationContext.getBeansWithAnnotation(annotations[0])
                .entrySet()
                .stream()
                .filter(e -> Anno.match(e.getValue().getClass(), annotations))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static <T> Map<String, T> getAll(Class<T> clazz) {
        return Beans.applicationContext.getBeansOfType(clazz);
    }

}
