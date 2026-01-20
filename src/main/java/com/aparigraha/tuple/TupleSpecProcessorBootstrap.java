package com.aparigraha.tuple;

import com.aparigraha.tuple.dynamic.JavaFileWriter;
import com.aparigraha.tuple.dynamic.entities.TupleGenerator;
import com.aparigraha.tuple.dynamic.factories.DynamicTupleGenerator;
import com.aparigraha.tuple.dynamic.factories.StaticTupleFactoryGenerator;
import com.aparigraha.tuple.dynamic.factories.ZipperMethodGenerator;
import com.aparigraha.tuple.dynamic.templates.PebbleTemplateProcessor;
import com.aparigraha.tuple.javac.StaticMethodScanner;

class TupleSpecProcessorBootstrap {
    private static final PebbleTemplateProcessor PEBBLE_TEMPLATE_PROCESSOR = new PebbleTemplateProcessor("templates");
    public static final TupleGenerator TUPLE_GENERATOR = new TupleGenerator(PEBBLE_TEMPLATE_PROCESSOR);
    public static final DynamicTupleGenerator DYNAMIC_TUPLE_GENERATOR = new DynamicTupleGenerator(
            PEBBLE_TEMPLATE_PROCESSOR,
            new StaticTupleFactoryGenerator(PEBBLE_TEMPLATE_PROCESSOR),
            new ZipperMethodGenerator(PEBBLE_TEMPLATE_PROCESSOR)
    );
    public static final StaticMethodScanner STATIC_METHOD_SCANNER = new StaticMethodScanner();
    public static final JavaFileWriter JAVA_FILE_WRITER = new JavaFileWriter();
}
