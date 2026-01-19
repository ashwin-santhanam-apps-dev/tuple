package com.aparigraha.tuple.dynamic.factories;

import com.aparigraha.tuple.dynamic.templates.PebbleTemplateProcessor;

import java.io.IOException;
import java.util.Map;

import static com.aparigraha.tuple.dynamic.templates.JavaTemplate.*;


public class ZipperMethodGenerator {
    private final PebbleTemplateProcessor pebbleTemplateProcessor;

    public ZipperMethodGenerator(PebbleTemplateProcessor pebbleTemplateProcessor) {
        this.pebbleTemplateProcessor = pebbleTemplateProcessor;
    }

    public String generate(int size) throws IOException {
        return pebbleTemplateProcessor.process("ZipperMethod.peb", Map.of(
                "genericsSequence", genericsSequence(size),
                "className", className(size),
                "objectStreamSequence", objectStreamSequence(size),
                "zipParameters", zipParameters(size),
                "listToTupleParameter", listToTupleParameter,
                "listToTupleSequence", listToTupleSequence(size)
        ));
    }
}
