package io.github.amusing_glitch.tuple.dynamic;

import io.github.amusing_glitch.tuple.dynamic.factories.FieldSpec;
import io.github.amusing_glitch.tuple.dynamic.factories.StreamFieldSpec;
import io.github.amusing_glitch.tuple.extensions.stream.Zipper;

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

    public static <T> Stream<T> namedZip(T type, StreamFieldSpec<?>... streamFieldSpecs) {
        throw new RuntimeException("Facade method: Operation not permitted");
    }

    public static Stream<List<Object>> zip(List<Stream<Object>> streams) {
        return Zipper.zip(streams);
    }
}
