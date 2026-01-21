package com.aparigraha.tuple.domain;

import java.util.Set;

public record NamedTupleSpec(
        String packageName,
        String className,
        String methodName,
        Set<NamedTupleField> fields
) {
}
