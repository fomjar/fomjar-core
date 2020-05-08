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
    public void testAccess() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException {
        System.out.println(DS.call("123456", "length"));
        System.out.println(DS.call("123456", "substring", 1));
        System.out.println(DS.call("123456", "substring", 1, 4));
        System.out.println(DS.get(new LinkedList<>(), "size"));
    }

    @Test
    public void testUnsafe() throws NoSuchFieldException, IllegalAccessException {
//        DS.setFinalBoolean(Boolean.class, "TRUE", false);
        DS.setFinalByte(Byte.class,         "MAX_VALUE", (byte) 1);
        DS.setFinalChar(Character.class,    "MAX_VALUE", '1');
        DS.setFinalShort(Short.class,       "MAX_VALUE", (short) 1);
        DS.setFinalInt(Integer.class,       "MAX_VALUE", 1);
        DS.setFinalLong(Long.class,         "MAX_VALUE", 1L);
        DS.setFinalFloat(Float.class,       "MAX_VALUE", 1.1F);
        DS.setFinalDouble(Double.class,     "MAX_VALUE", 1.1D);
//        System.out.println(DS.get(Boolean.class, "TRUE"));
        System.out.println(DS.get(Byte.class,       "MAX_VALUE"));
        System.out.println(DS.get(Character.class,  "MAX_VALUE"));
        System.out.println(DS.get(Short.class,      "MAX_VALUE"));
        System.out.println(DS.get(Integer.class,    "MAX_VALUE"));
        System.out.println(DS.get(Long.class,       "MAX_VALUE"));
        System.out.println(DS.get(Float.class,      "MAX_VALUE"));
        System.out.println(DS.get(Double.class,     "MAX_VALUE"));

        String s = "12345";
        DS.setFinalObject(s, "value", new char[] {'a', 'b', 'c', 'd', 'e'});
        System.out.println(s);
    }

}
