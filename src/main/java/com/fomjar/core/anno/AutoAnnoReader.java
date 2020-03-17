package com.fomjar.core.anno;

import java.lang.annotation.*;

/**
 * 注解于{@link AnnoReader}实现类上，用于在Spring-Boot环境下执行自动扫描。
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface AutoAnnoReader {
}
