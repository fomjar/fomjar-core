package com.fomjar.core.el;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * EL（Evaluation Language）表达式语言引擎统一接口定义。
 *
 * @author fomjar
 */
public interface EL {

    /**
     * 在上下文中注册静态类，此操作会注册此静态类下的所有静态方法和静态常量。
     *
     * @param clazz 待注册类
     * @return 此EL对象
     */
    default EL register(Class<?> clazz) {
        return this.register(clazz.getSimpleName(), clazz);
    }

    /**
     * 在上下文中注册静态类，此操作会注册此静态类下的所有静态方法和静态常量。
     *
     * @param name 注册名称
     * @param clazz 待注册待类
     * @return 此EL对象
     */
    EL register(String name, Class<?> clazz);

    /**
     * 在上下文中注册静态方法。
     *
     * @param method 带注册的方法
     * @return 此EL对象
     */
    default EL register(Method method) {
        return this.register(method.getName(), method);
    }

    /**
     * 在上下文中注册静态方法。
     *
     * @param name 注册名称
     * @param method 待注册的方法
     * @return 此EL对象
     */
    EL register(String name, Method method);

    /**
     * 在上下文中注入自定义方法。
     *
     * @param name 注册名称
     * @param method 待注册的方法
     * @return 此EL对象
     */
    EL register(String name, ELMethod method);

    /**
     * 在上下文中注册一个对象。可以是类、方法、或其他对象
     *
     * @param name 注册名称
     * @param object 待注册待对象
     * @return 此EL对象
     */
    default EL register(String name, Object object) {
        if (object instanceof Class)    return this.register(name, (Class) object);
        if (object instanceof Method)   return this.register(name, (Method) object);
        if (object instanceof ELMethod) return this.register(name, (ELMethod) object);

        this.context().put(name, object);
        return this;
    }

    /**
     * 从上下文中撤销指定的注册对象。
     *
     * @param names 待撤销的对象名称
     * @return 此EL对象
     */
    default EL deregister(String... names) {
        for (String name : names)
            this.context().remove(name);

        return this;
    }

    /**
     * 根据给定表达式求值。
     *
     * @param exp 表达式
     * @return 值串
     */
    String eval(String exp);

    /**
     * 获取上下文执行环境。
     *
     * @return 执行环境
     */
    Map<String, Object> context();

}
