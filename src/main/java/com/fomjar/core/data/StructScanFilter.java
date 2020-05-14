package com.fomjar.core.data;

/**
 * DS scan filter.
 *
 * @author fomjar
 */
public interface StructScanFilter {

    boolean filter(Class<?> type);

    static StructScanFilter name(String name) {
        return type -> type.getName().contains(name);
    }

    static StructScanFilter type(Class<?> type) {
        return type::isAssignableFrom;
    }

}
