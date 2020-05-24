package com.fomjar.el;

/**
 * EL自定义方法，与Method注册具有相同的效果。
 * @author fomjar
 */
public interface ELMethod {

    /**
     * 方法执行体。
     * @param args 参数
     * @return 返回值
     * @throws Exception 执行失败
     */
    Object invoke(Object... args) throws Exception;

}
