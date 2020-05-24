package com.fomjar.lang;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

public class TestFunc {

    @Test
    public void testFunction() {
        System.out.println(((Func) (args) -> {
            List<String> list = new LinkedList<>();
            list.add("a");
            return list;
        }).call());
    }
}
