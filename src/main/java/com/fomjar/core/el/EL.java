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
     * @param clazz
     * @return
     */
    default EL register(Class clazz) {
        return this.register(clazz.getSimpleName(), clazz);
    }

    /**
     * 在上下文中注册静态类，此操作会注册此静态类下的所有静态方法和静态常量。
     *
     * @param name
     * @param clazz
     * @return
     */
    EL register(String name, Class clazz);

    /**
     * 在上下文中注册静态方法。
     *
     * @param method
     * @return
     */
    default EL register(Method method) {
        return this.register(method.getName(), method);
    }

    /**
     * 在上下文中注册静态方法。
     *
     * @param name
     * @param method
     * @return
     */
    EL register(String name, Method method);

    /**
     * 在上下文中注入自定义方法。
     *
     * @param name
     * @param method
     * @return
     */
    EL register(String name, ELMethod method);

    /**
     * 在上下文中注册一个对象。
     *
     * @param name
     * @param object
     * @return
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
     * @param names
     * @return
     */
    default EL deregister(String... names) {
        for (String name : names)
            this.context().remove(name);

        return this;
    }

    /**
     * 根据给定表达式求值。
     *
     * @param exp
     * @return
     */
    String eval(String exp);

    /**
     * 获取Context。
     *
     * @return
     */
    Map<String, Object> context();

}
