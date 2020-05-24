package com.fomjar.lio.annotation;

import com.fomjar.lio.LIO;

import java.lang.annotation.*;

/**
 * LIO 消息处理方法标记。支持的参数：
 * <ul>
 *     <li>{@link LIO}: LIO 连接具柄，随时向客户端读写任意数据</li>
 *     <li>{@link byte[]}: 收到的客户端发送的字节数据</li>
 *     <li>{@link String}: 收到的客户端发送的字符串数据</li>
 *     <li>{@link com.alibaba.fastjson.JSONObject}: 收到的客户端发送的JSON数据</li>
 * </ul>
 * 当该标记方法包含返回值时容器将自动返回此值到客户端。
 *
 * @author fomjar
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LIORequest {
}
