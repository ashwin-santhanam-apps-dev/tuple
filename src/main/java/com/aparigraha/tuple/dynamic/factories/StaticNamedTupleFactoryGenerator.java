package com.aparigraha.tuple.dynamic.factories;

import com.aparigraha.tuple.dynamic.templates.JavaTemplate;
import com.aparigraha.tuple.dynamic.templates.PebbleTemplateProcessor;
import com.aparigraha.tuple.javac.scan.result.NamedTupleDefinition;
import com.aparigraha.tuple.javac.scan.result.NamedTupleField;

import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.IntStream;

import static com.aparigraha.tuple.dynamic.templates.JavaTemplate.*;


public class StaticNamedTupleFactoryGenerator {
    private final PebbleTemplateProcessor pebbleTemplateProcessor;

    public StaticNamedTupleFactoryGenerator(PebbleTemplateProcessor pebbleTemplateProcessor) {
        this.pebbleTemplateProcessor = pebbleTemplateProcessor;
    }

    public String generate(NamedTupleDefinition namedTupleDefinition) throws IOException {
        var orderedFields = namedTupleDefinition.fields().stream().sorted(Comparator.comparingInt(NamedTupleField::index)).toList();
        var generics =  generics(namedTupleDefinition.fields().size()).toList();
        return pebbleTemplateProcessor
                .process(
                        "StaticNamedTupleFactory.peb",
                        Map.of(
                                "genericsSequence", genericsSequence(namedTupleDefinition.fields().size()),
                                "className", namedTupleDefinition.className(),
                                "methodName", namedTupleFactoryMethodName,
                                "fieldSpecParameters", csvOf(
                                        IntStream.range(0, namedTupleDefinition.fields().size()).boxed()
                                                .map(index -> namedTupleFactoryMethodParam(generics.get(index), orderedFields.get(index).name()))
                                ),
                                "fieldSpecValues", csvOf(
                                        orderedFields.stream()
                                                .map(NamedTupleField::name).map(JavaTemplate::namedTupleConstructorParam)
                                ),
                                "genericsParameter", genericsParameter(namedTupleDefinition.fields().size())
                        )
                );
    }
}
