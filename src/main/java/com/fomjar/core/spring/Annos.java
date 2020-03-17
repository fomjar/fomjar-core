package com.fomjar.core.spring;

import com.fomjar.core.anno.Anno;
import com.fomjar.core.anno.AnnoAdapter;
import com.fomjar.core.anno.AnnoReader;
import com.fomjar.core.anno.AutoAnnoReader;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * DIY annotation auto-scan.
 *
 * @author fomjar
 */
@Component
public class Annos implements ApplicationContextAware {

    @Value("${fomjar.core.anno}")
    private String packaje;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        List<Class<AnnoReader>> readers = new LinkedList<>();

        try {
            Anno.scan(this.packaje, new AnnoAdapter() {
                @Override
                public void read(Annotation[] annos, Class clazz) {
                    boolean isAutoAnnoReader = false;

                    for (Annotation anno : annos) {
                        if (anno instanceof AutoAnnoReader) {
                            isAutoAnnoReader = true;
                            break;
                        }
                    }

                    if (isAutoAnnoReader
                            && AnnoReader.class.isAssignableFrom(clazz)
                            && !clazz.isAnonymousClass()
                            && !clazz.getSimpleName().equals(AnnoReader.class.getSimpleName())
                            && !clazz.getSimpleName().equals(AnnoAdapter.class.getSimpleName()))
                        readers.add(clazz);
                }
            });

            for (Class<AnnoReader> reader : readers)
                Anno.scan(this.packaje, reader.newInstance());

        } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new BeanCreationException("Annotations scan failed!", e);
        }
    }

}
