package com.fomjar.core.coll;

import java.util.*;

public abstract class Colls {

    public static <T>       Lists<T>    wrapList()                                  {return Lists.wrap();}
    public static <T>       Lists<T>    wrapList(Class<T> clazz)                    {return Lists.wrap(clazz);}
    public static <K, V>    Maps<K, V>  wrapMap()                                   {return Maps.wrap();}
    public static <K, V>    Maps<K, V>  wrapMap(Class<K> clazz_k, Class<V> clazz_v) {return Maps.wrap(clazz_k, clazz_v);}
    public static <T>       Sets<T>     wrapSet()                                   {return Sets.wrap();}
    public static <T>       Sets<T>     wrapSet(Class<T> clazz)                     {return Sets.wrap(clazz);}

    public static <T>       Lists<T>    wrap(List<T>    list)   {return Lists.wrap(list);}
    public static <T>       Lists<T>    wrap(T[]        array)  {return Lists.wrap(array);}
    public static <K, V>    Maps<K, V>  wrap(Map<K, V>  map)    {return Maps.wrap(map);}
    public static <T>       Sets<T>     wrap(Set<T>     set)    {return Sets.wrap(set);}

}
