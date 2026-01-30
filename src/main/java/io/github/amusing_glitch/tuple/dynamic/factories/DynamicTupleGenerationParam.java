package io.github.amusing_glitch.tuple.dynamic.factories;

import io.github.amusing_glitch.tuple.javac.NamedTupleDefinition;

import java.util.Set;


public record DynamicTupleGenerationParam(
        String packageName,
        String dynamicTupleClassName,
        String dynamicTupleFactoryMethodName,
        String dynamicTupleZipMethodName,
        String dynamicNamedTupleZipMethodName,
        String namedTupleFactoryMethodName,
        Set<Integer> tupleSizes,
        Set<NamedTupleDefinition> namedTupleDefinitions
) { }
