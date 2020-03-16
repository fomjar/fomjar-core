package com.fomjar.core.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

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

    public static <T> Map<String, T> all(Class<T> clazz) {
        return Beans.applicationContext.getBeansOfType(clazz);
    }

}
