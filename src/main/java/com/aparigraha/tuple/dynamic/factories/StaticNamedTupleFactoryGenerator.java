package com.aparigraha.tuple.dynamic.factories;

import com.aparigraha.tuple.dynamic.templates.JavaTemplate;
import com.aparigraha.tuple.dynamic.templates.PebbleTemplateProcessor;
import com.aparigraha.tuple.javac.NamedTupleField;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static com.aparigraha.tuple.dynamic.templates.JavaTemplate.*;


public class StaticNamedTupleFactoryGenerator {
    private final PebbleTemplateProcessor pebbleTemplateProcessor;

    public StaticNamedTupleFactoryGenerator(PebbleTemplateProcessor pebbleTemplateProcessor) {
        this.pebbleTemplateProcessor = pebbleTemplateProcessor;
    }

    public String generate(String className, List<NamedTupleField> fields) throws IOException {
        var orderedFields = fields.stream().sorted(Comparator.comparingInt(NamedTupleField::index)).toList();
        var generics =  generics(fields.size()).toList();
        return pebbleTemplateProcessor
                .process(
                        "StaticNamedTupleFactory.peb",
                        Map.of(
                                "genericsSequence", genericsSequence(fields.size()),
                                "className", className,
                                "methodName", namedTupleFactoryMethodName,
                                "fieldSpecParameters", csvOf(
                                        IntStream.range(0, fields.size()).boxed()
                                                .map(index -> namedTupleFactoryMethodParam(generics.get(index), orderedFields.get(index).name()))
                                ),
                                "fieldSpecValues", csvOf(
                                        orderedFields.stream()
                                                .map(NamedTupleField::name).map(JavaTemplate::namedTupleConstructorParam)
                                ),
                                "genericsParameter", genericsParameter(fields.size())
                        )
                );
    }
}
