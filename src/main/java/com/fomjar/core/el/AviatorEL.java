package com.fomjar.core.el;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.AviatorEvaluatorInstance;
import com.googlecode.aviator.runtime.function.AbstractVariadicFunction;
import com.googlecode.aviator.runtime.type.AviatorFunction;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorRuntimeJavaType;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;

public class AviatorEL extends AbstractEL {

    private AviatorEvaluatorInstance aviator;

    public AviatorEL() {
        this.aviator = AviatorEvaluator.newInstance();
        this.registerDefaults();
    }


    @Override
    public EL register(String name, Class<?> clazz) {
        for (Method method : clazz.getMethods()) {
            if (!Modifier.isStatic(method.getModifiers()))
                continue;

            this.register(name + "." + method.getName(), this.encodeFunction(method.getName(), method));
        }

        Map<String, Object> vars = new LinkedHashMap<>();
        for (Field field : clazz.getFields()) {
            if (!Modifier.isStatic(field.getModifiers()))
                continue;

            try {vars.put(field.getName(), field.get(null));}
            catch (IllegalAccessException e) {e.printStackTrace();}
        }
        this.register(name, vars);

        return this;
    }

    @Override
    public EL register(String name, Method method) {
        this.aviator.addFunction(this.encodeFunction(name, method));
        return this;
    }

    @Override
    public EL register(String name, ELMethod method) {
        this.aviator.addFunction(this.encodeFunction(name, method));
        return this;
    }

    @Override
    public String eval(String exp) {
        Object result = this.aviator.execute(exp, this.context());
        return null != result ? result.toString() : "";
    }

    private AviatorFunction encodeFunction(String name, Method method) {
        return new AbstractVariadicFunction() {
            @Override
            public AviatorObject variadicCall(Map<String, Object> env, AviatorObject... args) {
                try {return AviatorRuntimeJavaType.valueOf(method.invoke(null, AviatorEL.this.decodeArgs(args)));}
                catch (IllegalAccessException | InvocationTargetException e) {throw new IllegalArgumentException(e);}
            }
            @Override
            public String getName() {
                return name;
            }
        };
    }

    private AviatorFunction encodeFunction(String name, ELMethod method) {
        return new AbstractVariadicFunction() {
            @Override
            public AviatorObject variadicCall(Map<String, Object> env, AviatorObject... args) {
                try {return AviatorRuntimeJavaType.valueOf(method.invoke(AviatorEL.this.decodeArgs(args)));}
                catch (Exception e) {throw new IllegalArgumentException(e);}
            }
            @Override
            public String getName() {
                return name;
            }
        };
    }

    private Object[] decodeArgs(AviatorObject[] args) {
        Object[] args0 = null;
        if (null != args) {
            args0 = new Object[args.length];
            for (int i = 0; i < args.length; i++) {
                args0[i] = args[i].getValue(this.context());
            }
        }
        return args0;
    }

}
