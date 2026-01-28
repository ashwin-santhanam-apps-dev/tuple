package com.aparigraha.tuple.javac.scan.result;


public record NumberedTupleDefinition(
        String className,
        String methodName,
        int argumentCount
) {
}
