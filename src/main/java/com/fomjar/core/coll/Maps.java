package com.fomjar.core.coll;

import java.util.HashMap;
import java.util.Map;

/**
 * Easy Map. Easy to use.
 *
 * <code>
 * Maps.wrap(new HashMap<>()).put().put().put().get()
 * </code>
 *
 * @author fomjar
 * @param <K>
 * @param <V>
 */
public class Maps<K, V> {

    public static <K, V> Maps<K, V> wrap()                                      {return wrap(new HashMap<>());}
    public static <K, V> Maps<K, V> wrap(Class<K> clazz_k, Class<V> clazz_v)    {return wrap(new HashMap<>());}
    public static <K, V> Maps<K, V> wrap(Map<K, V> map)                         {return new Maps<>(map);}

    private Map<K, V> map;
    private Maps(Map<K, V> map) {this.map = map;}

    public Maps<K, V>   put(K k, V v)               {this.map.put(k, v);        return this;}
    public Maps<K, V>   putIf(boolean c, K k, V v)  {if (c) this.map.put(k, v); return this;}
    public Map<K, V>    get()                       {return this.map;}
}