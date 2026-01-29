package com.aparigraha.tuple.dynamic;

import com.aparigraha.tuple.dynamic.factories.FieldSpec;
import com.aparigraha.tuple.extensions.stream.Zipper;

import java.util.List;
import java.util.stream.Stream;


public class DynamicTupleSeed {
    public static Object of(Object... args) {
        throw new RuntimeException("Facade method: Operation not permitted");
    }

    public static <T> T of(T type, FieldSpec<?>... fieldSpecs) {
        throw new RuntimeException("Facade method: Operation not permitted");
    }

    public static Stream<Object> zip(Stream<?>... streams) {
        throw new RuntimeException("Facade method: Operation not permitted");
    }

    public static Stream<List<Object>> zip(List<Stream<Object>> streams) {
        return Zipper.zip(streams);
    }
}
