package com.fomjar.el;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;
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
                        null != args && 1 <= args.length
                                ? this.eval(args[0].toString())
                                : "")
                // now()
                // now('yyyy/MM/dd HH:mm:ss.SSS')
                .register("now", args ->
                        null != args && 1 <= args.length
                                ? new SimpleDateFormat(args[0].toString()).format(new Date())
                                : System.currentTimeMillis())
                // randomDouble() -> 0.12312312312
                .register("randomBoolean",  args -> new Random().nextBoolean())
                .register("randomInt",      args -> new Random().nextInt())
                .register("randomLong",     args -> new Random().nextLong())
                .register("randomFloat",    args -> new Random().nextFloat())
                .register("randomDouble",   args -> new Random().nextDouble())
                // length('abcde') -> 5
                .register("length", args ->
                        null != args && 1 <= args.length
                                ? args[0].toString().length()
                                : 0)
                // indexOf('abcde', 'cd') -> 2
                .register("indexOf", args ->
                        null != args && 2 <= args.length
                                ? args[0].toString().indexOf(args[1].toString())
                                : -1)
                // lastIndexOf('abcde', 'cd') -> 2
                .register("lastIndexOf", args ->
                        null != args && 2 <= args.length
                                ? args[0].toString().lastIndexOf(args[1].toString())
                                : -1)
                // trim(' abcde ') -> 'abcde'
                .register("trim", args ->
                        null != args && 1 <= args.length
                                ? args[0].toString().trim()
                                : null)
                // reverse('abcde') -> 'edcba'
                .register("reverse", args ->
                        null != args && 1 <= args.length
                                ? new StringBuilder(args[0].toString()).reverse().toString()
                                : null)
                // substring('abcde', 1, 3) -> 'bc'
                // substring('abcde', 3) -> 'de'
                .register("substring", args ->
                        null != args && 3 <= args.length
                                ? args[0].toString().substring(Integer.valueOf(args[1].toString()), Integer.valueOf(args[2].toString()))
                                : null != args && 2 <= args.length
                                ? args[0].toString().substring(Integer.valueOf(args[1].toString()))
                                : null)
                // replace('abcde', 'c', 'm') -> 'abmde'
                .register("replace", args ->
                        null != args && 3 <= args.length
                                ? args[0].toString().replace(args[1].toString(), args[2].toString())
                                : null)
                // split('abcde', 'c')[0] -> 'ab'
                .register("split", args ->
                        null != args && 2 <= args.length
                                ? args[0].toString().split(args[1].toString())
                                : null)
                // ifblank ('', 'abcd')
                .register("ifblank", args ->
                        null != args && 2 <= args.length
                                ? (null == args[0] || 0 == args[0].toString().length()
                                        ? args[1].toString()
                                        : args[0].toString())
                                : null)
                // Math.abs(-1)
                .register(Math.class);
    }


    @Override
    public Map<String, Object> context() {
        return this.context;
    }

}
