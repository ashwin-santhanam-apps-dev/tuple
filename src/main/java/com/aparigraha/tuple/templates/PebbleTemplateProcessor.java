package com.aparigraha.tuple.templates;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.loader.ClasspathLoader;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class PebbleTemplateProcessor {
    public static final String classPrefix = "Tuple";
    public static final String genericsPrefix = "T";
    public static final String parameterPrefix = "item";

    private final PebbleEngine engine;

    public PebbleTemplateProcessor(String templatePrefix) {
        ClasspathLoader loader = new ClasspathLoader();
        loader.setPrefix(templatePrefix);
        engine = new PebbleEngine.Builder()
                .strictVariables(true)
                .autoEscaping(false)
                .loader(loader)
                .build();
    }


    public String process(String templateName, Map<String, Object> context) throws IOException {
        Writer writer = new StringWriter();
        engine.getTemplate(templateName).evaluate(writer, context);
        return writer.toString();
    }


    public static String genericsSequence(int size) {
        return csvOf(
                range(size).map(index -> genericsPrefix + index)
        );
    }

    public static String genericsParameter(int size) {
        return csvOf(
                range(size)
                        .map(index -> "%s%d %s%d".formatted(genericsPrefix, index, parameterPrefix, index))
        );
    }

    public static String parameterSequence(int size) {
        return csvOf(
                range(size).map(index -> parameterPrefix + index)
        );
    }

    public static String className(int size) {
        return classPrefix + size;
    }


    private static String csvOf(Stream<String> items) {
        return items.collect(Collectors.joining(", "));
    }


    private static Stream<Integer> range(int size) {
        return IntStream.range(0, size).boxed();
    }
}
