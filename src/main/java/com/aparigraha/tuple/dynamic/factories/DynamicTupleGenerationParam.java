package com.aparigraha.tuple.dynamic.factories;

import java.util.Set;


public record DynamicTupleGenerationParam(
        String packageName,
        String dynamicTupleClassName,
        String dynamicTupleFactoryMethodName,
        Set<Integer> tupleSizes
) { }
