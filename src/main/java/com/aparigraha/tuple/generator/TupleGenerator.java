package com.aparigraha.tuple.generator;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.loader.ClasspathLoader;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TupleGenerator {
    private static final String genericsPrefix = "T";
    private static final String wildcard = "?";
    private static final String tupleEqualsParameter = "that";
    private static final String listToTupleParameter = "zipped";

    private static final PebbleTemplate template = getTemplate();


    public String generate(TupleGenerationParams params) throws IOException {
        Writer writer = new StringWriter();
        template.evaluate(writer, templateParams(params.packageName(), params.className(), params.fields()));
        return writer.toString();
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
                "zipMethod", Map.of(
                        "zipParameters", zipParameters(size),
                        "objectStreamSequence", objectStreamSequence(size),
                        "listToTupleParameter", listToTupleParameter,
                        "listToTupleSequence", listToTupleSequence(size)
                )
        );
    }

    private static String genericsSequence(int size) {
        return csvOf(generics(size));
    }

    private static String fields(List<String> fields) {
        var generics = generics(fields.size()).toList();
        return csvOf(range(fields.size()).map(index -> "%s %s".formatted(generics.get(index), fields.get(index))));
    }

    private static Stream<String> generics(int size) {
        return range(size).map(index -> genericsPrefix + index);
    }

    private static String wildcardGenericSequence(int size) {
        return csvOf(Stream.generate(() -> wildcard).limit(size));
    }

    private static String tupleEqualsCondition(List<String> fields) {
        return logicalAndOf(
                fields.stream().map(field -> "this.%s == %s.%s".formatted(field, tupleEqualsParameter, field))
        );
    }

    private static String zipParameters(int size) {
        return csvOf(range(size).map(index -> "Stream<T%d> stream%d".formatted(index, index)));
    }

    private static String objectStreamSequence(int size) {
        return csvOf(range(size).map("(Stream<Object>) stream%d"::formatted));
    }

    private static String listToTupleSequence(int size) {
        return csvOf(range(size).map(index -> "(%s%d) %s.get(%d)".formatted(genericsPrefix, index, listToTupleParameter, index)));
    }

    private static Stream<Integer> range(int size) {
        return IntStream.range(0, size).boxed();
    }

    private static String csvOf(Stream<String> items) {
        return items.reduce("%s, %s"::formatted).orElseThrow();
    }

    private static String logicalAndOf(Stream<String> conditions) {
        return conditions.reduce("%s && %s"::formatted).orElseThrow();
    }

    private static PebbleTemplate getTemplate() {
        ClasspathLoader loader = new ClasspathLoader();
        loader.setPrefix("templates");
        PebbleEngine engine = new PebbleEngine.Builder()
                .strictVariables(true)
                .autoEscaping(false)
                .loader(loader)
                .build();
        return engine.getTemplate("Tuple.peb");
    }
}
