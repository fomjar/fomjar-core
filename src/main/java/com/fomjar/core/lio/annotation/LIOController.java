package com.fomjar.core.lio.annotation;

import java.lang.annotation.*;

/**
 * LIO 容器控制器。标记 LIO 消息处理类，内部使用 {@link LIORequest} 来标记消息处理方法。
 * 容器类别和扫描包范围根据配置文件来设定。
 *
 * @author fomjar
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LIOController {

}
