package com.aparigraha.tuple.generator;

import java.util.List;
import java.util.stream.IntStream;

public record TupleGenerationParams(
        String packageName,
        String className,
        List<String> fields
) {
    public TupleGenerationParams(
            String packageName,
            String className,
            String fieldPrefix,
            int fieldCount
    ) {
       this(
               packageName,
               className,
               IntStream.range(0, fieldCount).boxed().map(index -> fieldPrefix + index).toList()
       );
    }
}
