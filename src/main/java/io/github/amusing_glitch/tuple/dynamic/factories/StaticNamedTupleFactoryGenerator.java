package io.github.amusing_glitch.tuple.dynamic.factories;

import io.github.amusing_glitch.tuple.dynamic.templates.JavaTemplate;
import io.github.amusing_glitch.tuple.dynamic.templates.PebbleTemplateProcessor;
import io.github.amusing_glitch.tuple.javac.NamedTupleDefinition;
import io.github.amusing_glitch.tuple.javac.NamedTupleField;

import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import static io.github.amusing_glitch.tuple.dynamic.templates.JavaTemplate.*;


public class StaticNamedTupleFactoryGenerator {
    private final PebbleTemplateProcessor pebbleTemplateProcessor;

    public StaticNamedTupleFactoryGenerator(PebbleTemplateProcessor pebbleTemplateProcessor) {
        this.pebbleTemplateProcessor = pebbleTemplateProcessor;
    }

    public String generate(String className, Set<NamedTupleField> fields) throws IOException {
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
