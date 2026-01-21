package com.aparigraha.tuple.domain;


public record NumberedTupleSpec(
        String className,
        String methodName,
        int argumentCount
) {
}
