package com.fomjar.core.anno;

import java.lang.annotation.Annotation;

/**
 * Annotation scan filter.
 * @author fomjar
 */
public interface AnnoScanFilter {

    boolean filter(Annotation[] annotations);

    static AnnoScanFilter all(Class<? extends Annotation>... types) {
        return annotations -> {
            for (Class<? extends Annotation> type : types) {
                boolean exist = false;
                for (Annotation annotation : annotations) {
                    if (type.isAssignableFrom(annotation.annotationType())) {
                        exist = true;
                        break;
                    }
                }
                if (!exist) return false;
            }
            return true;
        };
    }

    @SafeVarargs
    static AnnoScanFilter any(Class<? extends Annotation>... types) {
        return annotations -> {
            for (Class<? extends Annotation> type : types) {
                for (Annotation annotation : annotations) {
                    if (type.isAssignableFrom(annotation.annotationType())) {
                        return true;
                    }
                }
            }
            return false;
        };
    }

}
