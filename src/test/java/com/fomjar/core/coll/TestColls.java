package com.fomjar.core.coll;

import org.junit.Test;

public class TestColls {

    @Test
    public void testColls() {
        assert Colls.wrapList()                         .add(3)             .get().size() == 1;
        assert Colls.wrapList(String.class)             .add("a")           .get().size() == 1;
        assert Colls.wrapMap()                          .put("a", 3)    .get().size() == 1;
        assert Colls.wrapMap(String.class, Object.class).put("a", 3)    .get().size() == 1;
        assert Colls.wrapSet()                          .add(3)             .get().size() == 1;
        assert Colls.wrapSet(String.class)              .add("a")           .get().size() == 1;
    }

}
