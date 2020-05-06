package com.fomjar.core.ds;

import org.springframework.core.env.PropertyResolver;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;

/**
 * 数据结构访问和处理工具集。
 *
 * @author fomjar
 */
public abstract class DS {

    /**
     * 调用一个对象的成员方法。
     *
     * @param object
     * @param method
     * @param params
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static Object call(Object object, String method, Object... params) throws InvocationTargetException, IllegalAccessException {
        return DS.call(object, method, null, params);
    }

    /**
     * 调用一个对象的成员方法。
     *
     * @param object
     * @param method
     * @param type
     * @param params
     * @param <T>
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static <T> T call(Object object, String method, Class<? extends T> type, Object... params) throws InvocationTargetException, IllegalAccessException {
        for (Method m : DS.getMethods(object.getClass())) {
            if (null != method && !method.equals(m.getName()))
                continue;
            if (null != type && !type.isAssignableFrom(m.getReturnType()))
                continue;

            m.setAccessible(true);
            return (T) m.invoke(object, params);
        }
        return null;
    }

    /**
     * 访问一个对象的成员变量。
     *
     * @param object
     * @param field
     * @return
     * @throws IllegalAccessException
     */
    public static Object get(Object object, String field) throws IllegalAccessException {
        return DS.get(object, field, Object.class);
    }

    /**
     * 访问一个对象的成员变量。
     *
     * @param object
     * @param field
     * @param type
     * @param <T>
     * @return
     * @throws IllegalAccessException
     */
    public static <T> T get(Object object, String field, Class<? extends T> type) throws IllegalAccessException {
        for (Field f : DS.getFields(object.getClass())) {
            if (null != field && !field.equals(f.getName()))
                continue;
            if (null != type && !type.isAssignableFrom(f.getType()))
                continue;

            f.setAccessible(true);
            return (T) f.get(object);
        }
        return null;
    }

    /**
     * 修改一个对象的成员变量。
     *
     * @param object
     * @param field
     * @param value
     * @throws IllegalAccessException
     */
    public static void set(Object object, String field, Object value) throws IllegalAccessException {
        for (Field f : DS.getFields(object.getClass())) {
            if (!field.equals(f.getName()))
                continue;

            f.setAccessible(true);
            f.set(object, value);
            break;
        }
    }

    private static Field[] getFields(Class<?> type) {
        Set<Field> fields = new HashSet<>();
        fields.addAll(Arrays.asList(type.getFields()));
        fields.addAll(Arrays.asList(type.getDeclaredFields()));
        return fields.toArray(new Field[fields.size()]);
    }

    private static Method[] getMethods(Class<?> type) {
        Set<Method> methods = new HashSet<>();
        methods.addAll(Arrays.asList(type.getMethods()));
        methods.addAll(Arrays.asList(type.getDeclaredMethods()));
        return methods.toArray(new Method[methods.size()]);
    }


    /**
     * 扫描指定包下的类。
     *
     * @param pack
     * @param reader
     * @throws IOException
     */
    public static void scan(String pack, DSReader reader) throws IOException {
        DS.scan(DS.class.getClassLoader(), pack, null, reader);
    }

    /**
     * 扫描指定包下的类。
     *
     * @param pack
     * @param filter
     * @param reader
     * @throws IOException
     */
    public static void scan(String pack, DSFilter filter, DSReader reader) throws IOException {
        DS.scan(DS.class.getClassLoader(), pack, filter, reader);
    }

    /**
     * 扫描指定包下的类。
     *
     * @param loader
     * @param pack
     * @param reader
     * @throws IOException
     */
    public static void scan(ClassLoader loader, String pack, DSReader reader) throws IOException {
        DS.scan(loader, pack, null, reader);
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
    public static void scan(ClassLoader loader, String pack, DSFilter filter, DSReader reader) throws IOException {
        String resourcePath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                + ClassUtils.convertClassNameToResourcePath(DS.env.resolveRequiredPlaceholders(pack))
                + "/**/*.class";
        ResourcePatternResolver res = new PathMatchingResourcePatternResolver(loader);
        MetadataReaderFactory reg = new SimpleMetadataReaderFactory(loader);

        for (Resource resource : res.getResources(resourcePath)) {
            try {
                Class<?> type = Class.forName(reg.getMetadataReader(resource).getClassMetadata().getClassName(), true, loader);
                if (null != filter && !filter.filter(type))
                    continue;

                DS.scan(type, reader);
            } catch (ClassNotFoundException | NoClassDefFoundError | ExceptionInInitializerError e) {}
        }
    }

    private static void scan(Class<?> type, DSReader reader) {
        // Class-level
        reader.read(type);

        // Public methods within this class and super classes
        for (Method method : type.getMethods()) {
            reader.read(type, method);

            // Method parameters
            for (Parameter parameter : method.getParameters()) {
                reader.read(type, method, parameter);
            }
        }

        // Non-public methods within this class
        for (Method method : type.getDeclaredMethods()) {
            if (Modifier.isPublic(method.getModifiers()))
                continue;

            reader.read(type, method);

            // Method parameters
            for (Parameter parameter : method.getParameters()) {
                reader.read(type, method, parameter);
            }
        }

        // Public fields within this class and super classes
        for (Field field : type.getFields())
            reader.read(type, field);

        // Non-public fields within this class
        for (Field field : type.getDeclaredFields()) {
            if (Modifier.isPublic(field.getModifiers()))
                continue;

            reader.read(type, field);
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
