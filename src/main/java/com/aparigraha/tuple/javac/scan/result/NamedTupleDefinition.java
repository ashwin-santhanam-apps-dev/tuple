package com.aparigraha.tuple.javac.scan.result;

import java.util.Set;

public record NamedTupleDefinition(
        String packageName,
        String className,
        String methodName,
        Set<NamedTupleField> fields
) {
    public String qualifiedName() {
        return "%s.%s".formatted(packageName, className);
    }
}
