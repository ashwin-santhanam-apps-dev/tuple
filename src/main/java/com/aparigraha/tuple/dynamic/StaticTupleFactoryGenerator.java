package com.aparigraha.tuple.dynamic;

import com.aparigraha.tuple.templates.PebbleTemplateProcessor;

import java.io.IOException;
import java.util.Map;

import static com.aparigraha.tuple.templates.PebbleTemplateProcessor.*;


public class StaticTupleFactoryGenerator {
    private final PebbleTemplateProcessor pebbleTemplateProcessor;

    public StaticTupleFactoryGenerator(PebbleTemplateProcessor pebbleTemplateProcessor) {
        this.pebbleTemplateProcessor = pebbleTemplateProcessor;
    }

    public String generate(int tupleSize) throws IOException {
        return pebbleTemplateProcessor
                .process(
                        "StaticTupleFactory.peb",
                        Map.of(
                                "genericsSequence", genericsSequence(tupleSize),
                                "className", className(tupleSize),
                                "genericsParameter", genericsParameter(tupleSize),
                                "parameterSequence", parameterSequence(tupleSize)
                        )
                );
    }
}
