package com.aparigraha.tuple.javac;

import java.util.Set;

public record NamedTupleDefinition(
        String packageName,
        String className,
        String methodName,
        Set<NamedTupleField> fields
) {
}
