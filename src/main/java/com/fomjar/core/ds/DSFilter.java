package com.fomjar.core.ds;

/**
 * DS scan filter.
 *
 * @author fomjar
 */
public interface DSFilter {

    boolean filter(Class<?> type);

    static DSFilter name(String name) {
        return type -> type.getName().contains(name);
    }

    static DSFilter type(Class<?> type) {
        return type::isAssignableFrom;
    }

}
