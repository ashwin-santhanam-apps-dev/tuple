package io.github.amusing_glitch.tuple.dynamic.factories;

import java.util.stream.Stream;


@FunctionalInterface
public interface StreamFieldSpec<T> {
    Stream<T> value(Object fieldName);
}
