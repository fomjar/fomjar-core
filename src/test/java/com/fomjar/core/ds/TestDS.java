package com.fomjar.core.ds;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;

public class TestDS {

    @Test
    public void testCollection() {
        assert DS.wrapList()                            .add(3)             .get().size() == 1;
        assert DS.wrapList(String.class)                .add("a")           .get().size() == 1;
        assert DS.wrapMap()                             .put("a", 3)    .get().size() == 1;
        assert DS.wrapMap(String.class, Object.class)   .put("a", 3)    .get().size() == 1;
        assert DS.wrapSet()                             .add(3)             .get().size() == 1;
        assert DS.wrapSet(String.class)                 .add("a")           .get().size() == 1;
    }

    @Test
    public void testAccessible() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException {
        System.out.println(DS.call("123456", Integer.class, "length"));
        System.out.println(DS.call("123456", String.class, "substring", 1));
        System.out.println(DS.call("123456", String.class, "substring", 1, 4));
        DS.set(new LinkedList<>(), "size", 3);
        System.out.println(DS.get(new LinkedList<>(), Integer.class, "size"));
    }

    @Test
    public void testUnsafe() {
        long p = DS.unsafe.allocateMemory(8);
        System.out.println(DS.unsafe.getInt(p));
        DS.unsafe.freeMemory(p);
    }

}
