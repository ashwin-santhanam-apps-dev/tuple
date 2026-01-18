package com.aparigraha.tuple.dynamic;


public class DynamicTupleSeed {
    public static Object of(Object... args) {
        throw new RuntimeException("Facade method: Operation not permitted");
    }
}
