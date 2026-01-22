package com.aparigraha.tuple.javac;


public record NumberedTupleDefinition(
        String className,
        String methodName,
        int argumentCount
) {
}
