package com.fomjar.core.el;

import freemarker.cache.StringTemplateLoader;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.*;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

/**
 * EL的FreeMarker实现。
 * @author fomjar
 */
public class FreeMarkerEL extends AbstractEL {

    private BeansWrapper            beansWrapper;
    private Configuration           configuration;
    private StringTemplateLoader    templateLoader;

    public FreeMarkerEL() {
        BeansWrapperBuilder beansWrapperBuilder = new BeansWrapperBuilder(Configuration.getVersion());
        beansWrapperBuilder.setExposureLevel(BeansWrapper.EXPOSE_SAFE);
        this.beansWrapper   = beansWrapperBuilder.build();
        this.configuration  = new Configuration(Configuration.getVersion());
        this.templateLoader = new StringTemplateLoader();

        this.configuration.setTemplateLoader(this.templateLoader);
        this.configuration.setBooleanFormat("TRUE,FALSE");
        this.configuration.setNumberFormat("computer");

        super.registerDefaults();
    }

    @Override
    public EL register(String name, Class clazz) {
        try {return this.register(name, this.beansWrapper.getStaticModels().get(clazz.getName()));}
        catch (TemplateModelException e) {e.printStackTrace();}
        return this;
    }

    @Override
    public EL register(String name, Method method) {
        return this.register(name, (TemplateMethodModelEx) args -> {
            try {return method.invoke(null, this.decodeArgs(args));}
            catch (Exception e) {throw new TemplateModelException(e);}
        });
    }

    @Override
    public EL register(String name, ELMethod method) {
        return this.register(name, (TemplateMethodModelEx) args -> {
            try {return method.invoke(this.decodeArgs(args));}
            catch (Exception e) {throw new TemplateModelException(e);}
        });
    }

    @Override
    public String eval(String exp) {
        String name = "el-" + UUID.randomUUID().toString();
        StringWriter writer = new StringWriter();
        exp = exp.trim();

        if (!exp.startsWith("${"))
            exp = String.format("${%s}", exp);

        synchronized(this.context()) {
            try {
                // StringTemplateLoader 内部使用 HashMap 来存储 template，
                // putTemplate() 方法和 findTemplateSource() 方法没有加锁，无法支持多线程并发，
                // 因此只能通过实例化不同ELEngine对象的方法实现多线程并发，
                // 那么也没有必要缓存 template。
                this.templateLoader.putTemplate(name, exp);
                this.configuration.getTemplate(name).process(this.context(), writer);
                this.templateLoader.removeTemplate(name);
                this.configuration.clearTemplateCache();
            } catch (IOException | TemplateException e) {
                throw new RuntimeException("evaluate expression failed: " + exp, e);
            }
        }

        return writer.toString();
    }

    private Object[] decodeArgs(List args) throws TemplateModelException {
        Object[] args0 = null;
        if (null != args) {
            args0 = new Object[args.size()];
            for (int i = 0; i < args.size(); i++) {
                Object arg = args.get(i);
                if (arg instanceof TemplateModel) {
                    args0[i] = beansWrapper.unwrap((TemplateModel) arg);
                }
                else
                    args0[i] = arg;
            }
        }
        return args0;
    }

}
