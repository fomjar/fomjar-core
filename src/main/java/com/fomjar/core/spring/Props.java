package com.fomjar.core.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Easy Property. Easy to get property.
 *
 * @author fomjar
 */
@Component
public class Props implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Props.applicationContext = applicationContext;
    }

    public static String get(String key) {
        return Props.applicationContext.getEnvironment().getProperty(key);
    }

    public static <T> T get(String key, Class<T> clazz) {
        return Props.applicationContext.getEnvironment().getProperty(key, clazz);
    }

}
