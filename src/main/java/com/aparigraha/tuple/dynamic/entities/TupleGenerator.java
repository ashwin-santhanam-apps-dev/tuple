package com.aparigraha.tuple.dynamic.entities;

import com.aparigraha.tuple.dynamic.GeneratedClassSchema;
import com.aparigraha.tuple.dynamic.templates.PebbleTemplateProcessor;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.aparigraha.tuple.dynamic.templates.JavaTemplate.*;


public class TupleGenerator {
    private final PebbleTemplateProcessor pebbleTemplateProcessor;

    public TupleGenerator(PebbleTemplateProcessor pebbleTemplateProcessor) {
        this.pebbleTemplateProcessor = pebbleTemplateProcessor;
    }

    public GeneratedClassSchema generate(TupleGenerationParams params) throws IOException {
        return new GeneratedClassSchema(
                params.packageName(),
                params.className(),
                pebbleTemplateProcessor.process("Tuple.peb", templateParams(params.packageName(), params.className(), params.fields()))
        );
    }

    private static Map<String, Object> templateParams(String packageName, String className, List<String> fields) {
        int size = fields.size();
        return Map.of(
                "packageName", packageName,
                "className", className,
                "genericsSequence", genericsSequence(size),
                "fields", fields(fields),
                "equalsMethod", Map.of(
                        "wildcardGenericSequence", wildcardGenericSequence(size),
                        "tupleEqualsParameter", tupleEqualsParameter,
                        "tupleEqualsCondition", tupleEqualsCondition(fields)
                ),
                "typeReferenceFieldName", typeReferenceFieldName
        );
    }
}
