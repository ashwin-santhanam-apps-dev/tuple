package io.github.amusing_glitch.tuple.dynamic.factories;

import io.github.amusing_glitch.tuple.dynamic.templates.PebbleTemplateProcessor;

import java.io.IOException;
import java.util.Map;

import static io.github.amusing_glitch.tuple.dynamic.templates.JavaTemplate.*;


public class NamedZipperMethodGenerator {
    private final PebbleTemplateProcessor pebbleTemplateProcessor;

    public NamedZipperMethodGenerator(PebbleTemplateProcessor pebbleTemplateProcessor) {
        this.pebbleTemplateProcessor = pebbleTemplateProcessor;
    }

    public String generate(String className, int size) throws IOException {
        return pebbleTemplateProcessor.process("NamedZipperMethod.peb", Map.of(
                "genericsSequence", genericsSequence(size),
                "className", className,
                "objectStreamFieldSequence", objectStreamFieldSequence(size),
                "dynamicTupleZipMethodName", dynamicNamedTupleZipMethodName,
                "zipFieldParameters", zipFieldParameters(size),
                "listToTupleParameter", listToTupleParameter,
                "listToTupleSequence", listToTupleSequence(size)
        ));
    }
}
