package com.fomjar.core.coll;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * Easy Set. Easy to use.
 *
 * <code>
 * Sets.wrap(new HashSet<>()).add().add().add().get()
 * </code>
 *
 * @author fomjar
 * @param <T>
 */
public class Sets<T> {

    public static <T> Sets<T> wrap()                {return wrap(new HashSet<>());}
    public static <T> Sets<T> wrap(Class<T> clazz)  {return wrap(new HashSet<>());}
    public static <T> Sets<T> wrap(Set<T> set)      {return new Sets<>(set);}

    private Set<T> set;
    private Sets(Set<T> set) {this.set = set;}

    public Sets<T>  add(T e)                {this.set.add(e);return this;}
    public Sets<T>  addIf(boolean c, T e)   {if (c) this.set.add(e);return this;}
    public Set<T>   get()                   {return this.set;}
}