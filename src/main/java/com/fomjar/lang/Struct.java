package com.fomjar.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.PropertyResolver;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.util.ClassUtils;
import sun.misc.Unsafe;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.IntFunction;

/**
 * 数据结构访问和处理工具集。
 *
 * @author fomjar
 */
public abstract class Struct {

    private static final Logger logger = LoggerFactory.getLogger(Struct.class);

    /**
     * 获取类内部定义的指定成员字段。范围包括各类访问权限及各级父类定义的。
     *
     * @param clazz 类
     * @param field 字段名
     * @return 字段对应Field对象
     * @throws NoSuchFieldException 未找到该字段
     */
    public static Field field(Class<?> clazz, String field) throws NoSuchFieldException {
        try { return clazz.getField(field); }
        catch (NoSuchFieldException e) { return clazz.getDeclaredField(field); }
    }

    /**
     * 获取类内部定义的指定成员方法。范围包括各类访问权限及各级父类定义的。
     *
     * @param clazz 类
     * @param method 方法名
     * @param params 方法参数类型
     * @return 方法对应Method对象
     * @throws NoSuchMethodException 未找到该方法
     */
    public static Method method(Class<?> clazz, String method, Class<?>... params) throws NoSuchMethodException {
        try { return clazz.getMethod(method, params); }
        catch (NoSuchMethodException e) {
            try { return clazz.getDeclaredMethod(method, params); }
            catch (NoSuchMethodException noSuchMethodException) {
                // 根据方法名和参数数量进行粗略判断
                // 原因：在参数自动装箱的情况下，无法根据参数类型准确提取方法
                // 误判：方法重载、存在自动装箱（含原始数据类型参数）、且参数数量相同的情况下，可能存在误判
                for (Method m : Struct.methods(clazz)) {
                    if (m.getName().equals(method)
                            && ((null == params && 0 == m.getParameterCount())
                                    || params.length == m.getParameterCount()))
                        return m;
                }
                throw new NoSuchMethodException(String.format("%s(%s)", method, Arrays.toString(params)));
            }
        }
    }

    /**
     * 获取类内部定义的所有成员字段。范围包括各类访问权限及各级父类定义的。
     *
     * @param clazz 类
     * @return 所有字段对应的Field数组
     */
    public static Field[] fields(Class<?> clazz) {
        Set<Field> fields = new HashSet<>();
        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        fields.addAll(Arrays.asList(clazz.getFields()));
        return fields.toArray(new Field[0]);
    }

    /**
     * 获取类内部定义的所有成员方法。范围包括各类访问权限及各级父类定义的。
     *
     * @param clazz 类
     * @return 所有方法对应的Method数组
     */
    public static Method[] methods(Class<?> clazz) {
        Set<Method> methods = new HashSet<>();
        methods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
        methods.addAll(Arrays.asList(clazz.getMethods()));
        return methods.toArray(new Method[0]);
    }

    /**
     * 访问一个对象的成员变量或类的静态变量。
     *
     * @param object object or class
     * @param field 字段名
     * @return 字段值
     * @throws IllegalAccessException 非法访问该字段
     * @throws NoSuchFieldException 未找到该字段
     */
    public static Object get(Object object, String field) throws NoSuchFieldException, IllegalAccessException {
        return Struct.get(object, Object.class, field);
    }

    /**
     * 访问一个对象的成员变量或类的静态变量。
     *
     * @param object object or class
     * @param result 字段类型
     * @param field 字段名
     * @param <T> 字段类型
     * @return 字段值
     * @throws NoSuchFieldException 未找到该字段
     * @throws IllegalAccessException 非法访问该字段
     */
    public static <T> T get(Object object, Class<? extends T> result, String field) throws NoSuchFieldException, IllegalAccessException {
        Field f = Struct.field(object instanceof Class<?> ? (Class<?>) object : object.getClass(), field);

        f.setAccessible(true);
        return result.cast(f.get(object));
    }

    /**
     * 修改一个对象的成员变量或类的静态变量。
     *
     * @param object object or class
     * @param field 字段名
     * @param value 新的值
     * @throws NoSuchFieldException 未找到该字段
     * @throws IllegalAccessException 非法访问该字段
     */
    public static void set(Object object, String field, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field f = Struct.field(object instanceof Class<?> ? (Class<?>) object : object.getClass(), field);

        f.setAccessible(true);
        f.set(object, value);
    }

    /**
     * 调用一个对象的成员方法或类的静态方法。
     *
     * @param object object or class
     * @param method 方法名
     * @param params 调用参数
     * @return 方法返回值
     * @throws NoSuchMethodException 未找到该方法
     * @throws IllegalAccessException 非法访问该方法
     * @throws InvocationTargetException 调用目标错误
     */
    public static Object call(Object object, String method, Object... params) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return Struct.call(object, Object.class, method, params);
    }

    /**
     * 调用一个对象的成员方法或类的静态方法。
     *
     * @param object object or class
     * @param result 返回值的类型
     * @param method 方法名
     * @param params 调用参数
     * @param <T> 返回值的类型
     * @return 方法执行返回值
     * @throws NoSuchMethodException 未找到该方法
     * @throws IllegalAccessException 非法访问该方法
     * @throws InvocationTargetException 调用目标错误
     */
    public static <T> T call(Object object, Class<? extends T> result, String method, Object... params) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method m = Struct.method(object instanceof Class<?> ? (Class<?>) object : object.getClass(),
                method,
                null == params ? null : Arrays.stream(params).map(Object::getClass).toArray((IntFunction<Class<?>[]>) Class[]::new));

        m.setAccessible(true);
        return result.cast(m.invoke(object, params));
    }

    /**
     * Unsafe instance offered.
     */
    public static Unsafe unsafe = null;
    static {
        try { Struct.unsafe = Struct.get(Unsafe.class, Unsafe.class, "theUnsafe");}
        catch (NoSuchFieldException | IllegalAccessException e) { logger.warn("Setup the \"Unsafe\" failed.", e); }
    }

    /**
     * 强制修改对象的成员常量或类的静态常量。
     *
     * @param object instance or class
     * @param field 字段名
     * @param value 字段值
     * @throws NoSuchFieldException 未找到该字段
     */
    public static void setFinalBoolean(Object object, String field, boolean value) throws NoSuchFieldException {
        Struct.unsafe.putBoolean(object,
                object instanceof Class<?>
                        ? Struct.unsafe.staticFieldOffset(Struct.field((Class<?>) object, field))
                        : Struct.unsafe.objectFieldOffset(Struct.field(object.getClass(), field)),
                value);
    }

    /**
     * 强制修改对象的成员常量或类的静态常量。
     *
     * @param object instance or class
     * @param field 字段名
     * @param value 字段值
     * @throws NoSuchFieldException 未找到该字段
     */
    public static void setFinalByte(Object object, String field, byte value) throws NoSuchFieldException {
        Struct.unsafe.putByte(object,
                object instanceof Class<?>
                        ? Struct.unsafe.staticFieldOffset(Struct.field((Class<?>) object, field))
                        : Struct.unsafe.objectFieldOffset(Struct.field(object.getClass(), field)),
                value);
    }

    /**
     * 强制修改对象的成员常量或类的静态常量。
     *
     * @param object instance or class
     * @param field 字段名
     * @param value 字段值
     * @throws NoSuchFieldException 未找到该字段
     */
    public static void setFinalChar(Object object, String field, char value) throws NoSuchFieldException {
        Struct.unsafe.putChar(object,
                object instanceof Class<?>
                        ? Struct.unsafe.staticFieldOffset(Struct.field((Class<?>) object, field))
                        : Struct.unsafe.objectFieldOffset(Struct.field(object.getClass(), field)),
                value);
    }

    /**
     * 强制修改对象的成员常量或类的静态常量。
     *
     * @param object instance or class
     * @param field 字段名
     * @param value 字段值
     * @throws NoSuchFieldException 未找到该字段
     */
    public static void setFinalShort(Object object, String field, short value) throws NoSuchFieldException {
        Struct.unsafe.putShort(object,
                object instanceof Class<?>
                        ? Struct.unsafe.staticFieldOffset(Struct.field((Class<?>) object, field))
                        : Struct.unsafe.objectFieldOffset(Struct.field(object.getClass(), field)),
                value);
    }

    /**
     * 强制修改对象的成员常量或类的静态常量。
     *
     * @param object instance or class
     * @param field 字段名
     * @param value 字段值
     * @throws NoSuchFieldException 未找到该字段
     */
    public static void setFinalInt(Object object, String field, int value) throws NoSuchFieldException {
        Struct.unsafe.putInt(object,
                object instanceof Class<?>
                        ? Struct.unsafe.staticFieldOffset(Struct.field((Class<?>) object, field))
                        : Struct.unsafe.objectFieldOffset(Struct.field(object.getClass(), field)),
                value);
    }

    /**
     * 强制修改对象的成员常量或类的静态常量。
     *
     * @param object instance or class
     * @param field 字段名
     * @param value 字段值
     * @throws NoSuchFieldException 未找到该字段
     */
    public static void setFinalLong(Object object, String field, long value) throws NoSuchFieldException {
        Struct.unsafe.putLong(object,
                object instanceof Class<?>
                        ? Struct.unsafe.staticFieldOffset(Struct.field((Class<?>) object, field))
                        : Struct.unsafe.objectFieldOffset(Struct.field(object.getClass(), field)),
                value);
    }

    /**
     * 强制修改对象的成员常量或类的静态常量。
     *
     * @param object instance or class
     * @param field 字段名
     * @param value 字段值
     * @throws NoSuchFieldException 未找到该字段
     */
    public static void setFinalFloat(Object object, String field, float value) throws NoSuchFieldException {
        Struct.unsafe.putFloat(object,
                object instanceof Class<?>
                        ? Struct.unsafe.staticFieldOffset(Struct.field((Class<?>) object, field))
                        : Struct.unsafe.objectFieldOffset(Struct.field(object.getClass(), field)),
                value);
    }

    /**
     * 强制修改对象的成员常量或类的静态常量。
     *
     * @param object instance or class
     * @param field 字段名
     * @param value 字段值
     * @throws NoSuchFieldException 未找到该字段
     */
    public static void setFinalDouble(Object object, String field, double value) throws NoSuchFieldException {
        Struct.unsafe.putDouble(object,
                object instanceof Class<?>
                        ? Struct.unsafe.staticFieldOffset(Struct.field((Class<?>) object, field))
                        : Struct.unsafe.objectFieldOffset(Struct.field(object.getClass(), field)),
                value);
    }

    /**
     * 强制修改对象的成员常量或类的静态常量。
     *
     * @param object instance or class
     * @param field 字段名
     * @param value 字段值
     * @throws NoSuchFieldException 未找到该字段
     */
    public static void setFinalObject(Object object, String field, Object value) throws NoSuchFieldException {
        Struct.unsafe.putObject(object,
                object instanceof Class<?>
                        ? Struct.unsafe.staticFieldOffset(Struct.field((Class<?>) object, field))
                        : Struct.unsafe.objectFieldOffset(Struct.field(object.getClass(), field)),
                value);
    }

    /**
     * 定义一个类，此方法会跳过JVM的所有安全检查。
     *
     * @param bytes 字节数组表示的类
     * @return Class对象
     */
    public static Class<?> defineClass(byte[] bytes) {
        return Struct.unsafe.defineClass(null, bytes, 0, bytes.length, null, null);
    }

    /**
     * 绕过构造方法和初始化代码来直接创建对象。
     *
     * @param clazz 类
     * @return 创建的实例
     * @throws InstantiationException 实例化失败
     */
    public static Object allocateInstance(Class<?> clazz) throws InstantiationException {
        return Struct.unsafe.allocateInstance(clazz);
    }


    /**
     * 扫描指定包下的类。
     *
     * @param pack 包路径
     * @param reader 读取器
     * @throws IOException 读取失败
     */
    public static void scan(String pack, StructScanReader reader) throws IOException {
        Struct.scan(Struct.class.getClassLoader(), pack, null, reader);
    }

    /**
     * 扫描指定包下的类。
     *
     * @param pack 包路径
     * @param filter 过滤器
     * @param reader 读取器
     * @throws IOException 读取失败
     */
    public static void scan(String pack, StructScanFilter filter, StructScanReader reader) throws IOException {
        Struct.scan(Struct.class.getClassLoader(), pack, filter, reader);
    }

    /**
     * 扫描指定包下的类。
     *
     * @param loader 指定的类加载器
     * @param pack 包路径
     * @param reader 读取器
     * @throws IOException 读取失败
     */
    public static void scan(ClassLoader loader, String pack, StructScanReader reader) throws IOException {
        Struct.scan(loader, pack, null, reader);
    }

    private static final PropertyResolver env = new StandardEnvironment();

    /**
     * 扫描指定包下的类。
     *
     * @param loader 指定的类加载器
     * @param pack 包路径
     * @param filter 过滤器
     * @param reader 读取器
     * @throws IOException 读取失败
     */
    public static void scan(ClassLoader loader, String pack, StructScanFilter filter, StructScanReader reader) throws IOException {
        String resourcePath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                + ClassUtils.convertClassNameToResourcePath(Struct.env.resolveRequiredPlaceholders(pack))
                + "/**/*.class";
        ResourcePatternResolver res = new PathMatchingResourcePatternResolver(loader);
        MetadataReaderFactory reg = new SimpleMetadataReaderFactory(loader);

        for (Resource resource : res.getResources(resourcePath)) {
            Class<?> type = null;
            try {
                type = Class.forName(reg.getMetadataReader(resource).getClassMetadata().getClassName(), true, loader);

                if (null != filter && !filter.filter(type))
                    continue;

                Struct.scan(type, reader);
            } catch (ClassNotFoundException | NoClassDefFoundError | ExceptionInInitializerError | UnsatisfiedLinkError e) { }
        }
    }

    private static void scan(Class<?> type, StructScanReader reader) {
        // Class-level
        reader.read(type);

        // Fields within this class and super classes
        for (Field field : Struct.fields(type))
            reader.read(type, field);

        // Methods within this class and super classes
        for (Method method : Struct.methods(type)) {
            reader.read(type, method);

            // Method parameters
            for (Parameter parameter : method.getParameters()) {
                reader.read(type, method, parameter);
            }
        }
    }

}
