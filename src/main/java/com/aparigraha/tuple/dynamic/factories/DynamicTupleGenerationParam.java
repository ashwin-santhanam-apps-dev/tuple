package com.aparigraha.tuple.dynamic.factories;

import com.aparigraha.tuple.javac.scan.result.NamedTupleDefinition;

import java.util.Set;


public record DynamicTupleGenerationParam(
        String packageName,
        String dynamicTupleClassName,
        String dynamicTupleFactoryMethodName,
        String dynamicTupleZipMethodName,
        String namedTupleFactoryMethodName,
        Set<Integer> tupleSizes,
        Set<NamedTupleDefinition> namedTupleDefinitions
) { }
