package com.fomjar.core.el;

/**
 * EL自定义方法，与Method注册具有相同的效果。
 * @author fomjar
 */
public interface ELMethod {

    /**
     * 方法执行体。
     * @param args
     * @return
     */
    Object invoke(Object... args) throws Exception;

}
