package com.aparigraha.tuple.dynamic.templates;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class JavaTemplate {
    public static final String genericsPrefix = "T";
    public static final String wildcard = "?";
    public static final String tupleEqualsParameter = "that";
    public static final String listToTupleParameter = "zipped";
    public static final String parameterPrefix = "item";
    public static final String classPrefix = "Tuple";
    public static final String dynamicTupleClassName = "DynamicTuple";
    public static final String dynamicTupleFactoryMethodName = "of";
    public static final String dynamicTupleZipMethodName = "zip";
    public static final String packageName = "com.aparigraha.tuple.dynamic";


    public static String dynamicTupleFactoryMethod() {
        return dynamicTupleClassName + "." + dynamicTupleFactoryMethodName;
    }

    public static String dynamicTupleZipMethod() {
        return dynamicTupleClassName + "." + dynamicTupleZipMethodName;
    }

    public static String genericsSequence(int size) {
        return csvOf(generics(size));
    }

    public static String fields(List<String> fields) {
        var generics = generics(fields.size()).toList();
        return csvOf(range(fields.size()).map(index -> "%s %s".formatted(generics.get(index), fields.get(index))));
    }

    public static Stream<String> generics(int size) {
        return range(size).map(index -> genericsPrefix + index);
    }

    public static String wildcardGenericSequence(int size) {
        return csvOf(Stream.generate(() -> wildcard).limit(size));
    }

    public static String tupleEqualsCondition(List<String> fields) {
        return logicalAndOf(
                fields.stream().map(field -> "this.%s == %s.%s".formatted(field, tupleEqualsParameter, field))
        );
    }

    public static String zipParameters(int size) {
        return csvOf(range(size).map(index -> "Stream<T%d> stream%d".formatted(index, index)));
    }

    public static String objectStreamSequence(int size) {
        return csvOf(range(size).map("(Stream<Object>) stream%d"::formatted));
    }

    public static String listToTupleSequence(int size) {
        return csvOf(range(size).map(index -> "(%s%d) %s.get(%d)".formatted(genericsPrefix, index, listToTupleParameter, index)));
    }

    public static Stream<Integer> range(int size) {
        return IntStream.range(0, size).boxed();
    }

    public static String csvOf(Stream<String> items) {
        return items.reduce("%s, %s"::formatted).orElseThrow();
    }

    public static String logicalAndOf(Stream<String> conditions) {
        return conditions.reduce("%s && %s"::formatted).orElseThrow();
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
}
