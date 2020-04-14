package com.fomjar.core.coll;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Easy List. Easy to use.
 *
 * <code>
 * Lists.wrap(new LinkedList<>()).add().add().add().get()
 * </code>
 *
 * @author fomjar
 * @param <T>
 */
public class Lists<T> {

    public static <T> Lists<T> wrap()               {return wrap(new LinkedList<>());}
    public static <T> Lists<T> wrap(Class<T> clazz) {return wrap(new LinkedList<>());}
    public static <T> Lists<T> wrap(List<T> list)   {return new Lists<>(list);}
    public static <T> Lists<T> wrap(T[] array)      {return Lists.wrap(Arrays.asList(array));}

    private List<T> list;
    private Lists(List<T> list) {this.list = list;}

    public Lists<T> add(T e)                {this.list.add(e);          return this;}
    public Lists<T> addIf(boolean c, T e)   {if (c) this.list.add(e);   return this;}
    public List<T>  get()                   {return this.list;}
}