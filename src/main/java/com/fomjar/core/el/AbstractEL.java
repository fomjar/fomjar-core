package com.fomjar.core.el;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fomjar
 */
public abstract class AbstractEL implements EL {

    private Map<String, Object> context = new ConcurrentHashMap<>();

    /**
     * 注册默认对象。
     */
    protected void registerDefaults() {
        this.register("this", this)
                // eval -> el.eval()
                .register("eval", args ->
                        null == args || 0 == args.length
                                ? null
                                : this.eval(args[0].toString()))
                // now()
                // now('yyyy/MM/dd HH:mm:ss')
                .register("now", args ->
                        null != args && 0 != args.length
                                ? new SimpleDateFormat(args[0].toString()).format(new Date())
                                : System.currentTimeMillis())
                // if (2 > 1, 'true', 'false')
                .register("if", args ->
                        null != args && 3 <= args.length
                                ? (Boolean.valueOf(args[0].toString())
                                        ? args[1].toString()
                                        : args[2].toString())
                                : null);
    }


    @Override
    public Map<String, Object> context() {
        return this.context;
    }

}
