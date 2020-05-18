package com.fomjar.core.data;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.LinkedList;

public class TestStruct {

    @Test
    public void testCollection() {
        assert Struct.wrapList()                            .add(3)             .get().size() == 1;
        assert Struct.wrapList(String.class)                .add("a")           .get().size() == 1;
        assert Struct.wrapMap()                             .put("a", 3)    .get().size() == 1;
        assert Struct.wrapMap(String.class, Object.class)   .put("a", 3)    .get().size() == 1;
        assert Struct.wrapSet()                             .add(3)             .get().size() == 1;
        assert Struct.wrapSet(String.class)                 .add("a")           .get().size() == 1;
    }

    @Test
    public void testAccess() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException {
        System.out.println(Struct.call("123456", "length"));
        System.out.println(Struct.call("123456", "substring", 1));
        System.out.println(Struct.call("123456", "substring", 1, 4));
        System.out.println(Struct.get(new LinkedList<>(), "size"));
    }

    @Test
    public void testUnsafe() throws NoSuchFieldException, IllegalAccessException, IOException, InstantiationException {
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
