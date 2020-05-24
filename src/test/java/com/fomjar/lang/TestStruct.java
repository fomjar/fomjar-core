package com.fomjar.lang;

import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

public class TestStruct {

    @Test
    public void testAccess() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException {
        System.out.println(Struct.call("123456", "length"));
        System.out.println(Struct.call("123456", "substring", 1));
        System.out.println(Struct.call("123456", "substring", 1, 4));
        System.out.println(Struct.get(new LinkedList<>(), "size"));
    }

    @Test
    public void testUnsafe() throws NoSuchFieldException {
//        DS.setFinalBoolean(Boolean.class, "TRUE", false);
        Struct.setFinalByte(Byte.class,         "MAX_VALUE", (byte) 1);
        Struct.setFinalChar(Character.class,    "MAX_VALUE", '1');
        Struct.setFinalShort(Short.class,       "MAX_VALUE", (short) 1);
        Struct.setFinalInt(Integer.class,       "MAX_VALUE", 1);
        Struct.setFinalLong(Long.class,         "MAX_VALUE", 1L);
        Struct.setFinalFloat(Float.class,       "MAX_VALUE", 1.1F);
        Struct.setFinalDouble(Double.class,     "MAX_VALUE", 1.1D);
//        System.out.println(DS.get(Boolean.class, "TRUE"));
        System.out.println(Byte.MAX_VALUE);
        System.out.println(Character.MAX_VALUE);
        System.out.println(Short.MAX_VALUE);
        System.out.println(Integer.MAX_VALUE);
        System.out.println(Long.MAX_VALUE);
        System.out.println(Float.MAX_VALUE);
        System.out.println(Double.MAX_VALUE);

        String s = "12345";
        Struct.setFinalObject(s, "value", new char[] {'a', 'b', 'c', 'd', 'e'});
        System.out.println(s);
    }

}
