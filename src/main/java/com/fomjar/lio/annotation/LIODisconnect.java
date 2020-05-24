package com.fomjar.lio.annotation;

import com.fomjar.lio.LIO;

import java.lang.annotation.*;

/**
 * LIO 客户端断连标记。支持的参数：
 *  <ul>
 *      <li>{@link LIO}: LIO 连接具柄</li>
 *  </ul>
 *
 * @author fomjar
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LIODisconnect {
}
