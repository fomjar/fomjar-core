package com.fomjar.core.data;

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
import java.lang.reflect.*;
import java.util.*;
import java.util.function.IntFunction;

/**
 * 数据结构访问和处理工具集。
 *
 * @author fomjar
 */
public abstract class Struct {

    /**
     * 获取类内部定义的指定成员字段。范围包括各类访问权限及各级父类定义的。
     *
     * @param clazz
     * @param field
     * @return
     * @throws NoSuchFieldException
     */
    public static Field field(Class<?> clazz, String field) throws NoSuchFieldException {
        try { return clazz.getField(field); }
        catch (NoSuchFieldException e) { return clazz.getDeclaredField(field); }
    }

    /**
     * 获取类内部定义的指定成员方法。范围包括各类访问权限及各级父类定义的。
     *
     * @param clazz
     * @param method
     * @param params
     * @return
     * @throws NoSuchMethodException
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
     * @param clazz
     * @return
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
     * @param clazz
     * @return
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
     * @param field
     * @return
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     */
    public static Object get(Object object, String field) throws NoSuchFieldException, IllegalAccessException {
        return Struct.get(object, Object.class, field);
    }

    /**
     * 访问一个对象的成员变量或类的静态变量。
     *
     * @param object object or class
     * @param result
     * @param field
     * @param <T>
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
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
     * @param field
     * @param value
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
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
     * @param method
     * @param params
     * @return
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static Object call(Object object, String method, Object... params) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return Struct.call(object, Object.class, method, params);
    }

    /**
     * 调用一个对象的成员方法或类的静态方法。
     *
     * @param object object or class
     * @param result
     * @param method
     * @param params
     * @param <T>
     * @return
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
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
        try {
            Struct.unsafe = Struct.get(Unsafe.class, Unsafe.class, "theUnsafe");}
        catch (NoSuchFieldException | IllegalAccessException e) {e.printStackTrace();}
    }

    /**
     * 强制修改对象的成员常量或类的静态常量。
     *
     * @param object instance or class
     * @param field
     * @param value
     * @throws NoSuchFieldException
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
     * @param field
     * @param value
     * @throws NoSuchFieldException
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
     * @param field
     * @param value
     * @throws NoSuchFieldException
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
     * @param field
     * @param value
     * @throws NoSuchFieldException
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
     * @param field
     * @param value
     * @throws NoSuchFieldException
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
     * @param field
     * @param value
     * @throws NoSuchFieldException
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
     * @param field
     * @param value
     * @throws NoSuchFieldException
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
     * @param field
     * @param value
     * @throws NoSuchFieldException
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
     * @param field
     * @param value
     * @throws NoSuchFieldException
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
     * @param bytes
     * @return
     */
    public static Class<?> defineClass(byte[] bytes) {
        return Struct.unsafe.defineClass(null, bytes, 0, bytes.length, null, null);
    }

    /**
     * 绕过构造方法和初始化代码来直接创建对象。
     *
     * @param clazz
     * @return
     * @throws InstantiationException
     */
    public static Object allocateInstance(Class<?> clazz) throws InstantiationException {
        return Struct.unsafe.allocateInstance(clazz);
    }


    /**
     * 扫描指定包下的类。
     *
     * @param pack
     * @param reader
     * @throws IOException
     */
    public static void scan(String pack, StructScanReader reader) throws Exception {
        Struct.scan(Struct.class.getClassLoader(), pack, null, reader);
    }

    /**
     * 扫描指定包下的类。
     *
     * @param pack
     * @param filter
     * @param reader
     * @throws IOException
     */
    public static void scan(String pack, StructScanFilter filter, StructScanReader reader) throws Exception {
        Struct.scan(Struct.class.getClassLoader(), pack, filter, reader);
    }

    /**
     * 扫描指定包下的类。
     *
     * @param loader
     * @param pack
     * @param reader
     * @throws IOException
     */
    public static void scan(ClassLoader loader, String pack, StructScanReader reader) throws Exception {
        Struct.scan(loader, pack, null, reader);
    }

    private static final PropertyResolver env = new StandardEnvironment();

    /**
     * 扫描指定包下的类。
     *
     * @param loader
     * @param pack
     * @param filter
     * @param reader
     * @throws IOException
     */
    public static void scan(ClassLoader loader, String pack, StructScanFilter filter, StructScanReader reader) throws Exception {
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
            } catch (ClassNotFoundException | NoClassDefFoundError | ExceptionInInitializerError | UnsatisfiedLinkError e) {}
        }
    }

    private static void scan(Class<?> type, StructScanReader reader) throws Exception {
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

    public static <T>       ListWrapper<T>      wrapList()                                  {return ListWrapper.wrap();}
    public static <T>       ListWrapper<T>      wrapList(Class<T> type)                     {return ListWrapper.wrap(type);}
    public static <K, V>    MapWrapper<K, V>    wrapMap()                                   {return MapWrapper.wrap();}
    public static <K, V>    MapWrapper<K, V>    wrapMap(Class<K> typeK, Class<V> typeV)     {return MapWrapper.wrap(typeK, typeV);}
    public static <T>       SetWrapper<T>       wrapSet()                                   {return SetWrapper.wrap();}
    public static <T>       SetWrapper<T>       wrapSet(Class<T> type)                      {return SetWrapper.wrap(type);}

    public static <T>       ListWrapper<T>      wrap(List<T>    list)   {return ListWrapper.wrap(list);}
    public static <T>       ListWrapper<T>      wrap(T[]        array)  {return ListWrapper.wrap(array);}
    public static <K, V>    MapWrapper<K, V>    wrap(Map<K, V>  map)    {return MapWrapper.wrap(map);}
    public static <T>       SetWrapper<T>       wrap(Set<T>     set)    {return SetWrapper.wrap(set);}


    /**
     * Easy List. Easy to use.
     *
     * <code>
     * ListWrapper.wrap(new LinkedList<>()).add().add().add().get()
     * </code>
     *
     * @author fomjar
     * @param <T>
     */
    public static class ListWrapper<T> {

        public static <T> ListWrapper<T> wrap()             {return wrap(new LinkedList<>());}
        public static <T> ListWrapper<T> wrap(Class<T> type){return wrap(new LinkedList<>());}
        public static <T> ListWrapper<T> wrap(List<T> list) {return new ListWrapper<>(list);}
        public static <T> ListWrapper<T> wrap(T[] array)    {return ListWrapper.wrap(Arrays.asList(array));}

        private List<T> list;
        private ListWrapper(List<T> list) {this.list = list;}

        public ListWrapper<T>   add(T e)                {this.list.add(e);          return this;}
        public ListWrapper<T>   addIf(boolean c, T e)   {if (c) this.list.add(e);   return this;}
        public List<T>          get()                   {return this.list;}
    }

    /**
     * Easy Map. Easy to use.
     *
     * <code>
     * MapWrapper.wrap(new HashMap<>()).put().put().put().get()
     * </code>
     *
     * @author fomjar
     * @param <K>
     * @param <V>
     */
    public static class MapWrapper<K, V> {

        public static <K, V> MapWrapper<K, V> wrap()                                {return wrap(new HashMap<>());}
        public static <K, V> MapWrapper<K, V> wrap(Class<K> typeK, Class<V> typeV)  {return wrap(new HashMap<>());}
        public static <K, V> MapWrapper<K, V> wrap(Map<K, V> map)                   {return new MapWrapper<>(map);}

        private Map<K, V> map;
        private MapWrapper(Map<K, V> map) {this.map = map;}

        public MapWrapper<K, V> put(K k, V v)               {this.map.put(k, v);        return this;}
        public MapWrapper<K, V> putIf(boolean c, K k, V v)  {if (c) this.map.put(k, v); return this;}
        public Map<K, V>        get()                       {return this.map;}
    }

    /**
     *
     * Easy Set. Easy to use.
     *
     * <code>
     * SetWrapper.wrap(new HashSet<>()).add().add().add().get()
     * </code>
     *
     * @author fomjar
     * @param <T>
     */
    public static class SetWrapper<T> {

        public static <T> SetWrapper<T> wrap()              {return wrap(new HashSet<>());}
        public static <T> SetWrapper<T> wrap(Class<T> type) {return wrap(new HashSet<>());}
        public static <T> SetWrapper<T> wrap(Set<T> set)    {return new SetWrapper<>(set);}

        private Set<T> set;
        private SetWrapper(Set<T> set) {this.set = set;}

        public SetWrapper<T>    add(T e)                {this.set.add(e);return this;}
        public SetWrapper<T>    addIf(boolean c, T e)   {if (c) this.set.add(e);return this;}
        public Set<T>           get()                   {return this.set;}
    }

}
