package com.aparigraha.tuple.processors;

import com.aparigraha.tuple.javac.TupleDefinitionSpec;

import static com.aparigraha.tuple.dynamic.templates.JavaTemplate.*;


public class SupportedTupleDefinitions {
    public static final TupleDefinitionSpec TUPLE_FACTORY_METHOD_SPEC = new TupleDefinitionSpec(
            packageName,
            dynamicTupleClassName,
            dynamicTupleFactoryMethodName
    );

    public static final TupleDefinitionSpec TUPLE_ZIP_METHOD_SPEC = new TupleDefinitionSpec(
            packageName,
            dynamicTupleClassName,
            dynamicTupleZipMethodName
    );

    public static final TupleDefinitionSpec NAMED_TUPLE_FACTORY_METHOD_SPEC = new TupleDefinitionSpec(
            packageName,
            dynamicTupleClassName,
            namedTupleFactoryMethodName
    );
}
