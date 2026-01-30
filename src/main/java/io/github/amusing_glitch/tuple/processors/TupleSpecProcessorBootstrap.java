package io.github.amusing_glitch.tuple.processors;

import io.github.amusing_glitch.tuple.dynamic.JavaFileWriter;
import io.github.amusing_glitch.tuple.dynamic.entities.TupleGenerator;
import io.github.amusing_glitch.tuple.dynamic.factories.*;
import io.github.amusing_glitch.tuple.dynamic.templates.PebbleTemplateProcessor;
import io.github.amusing_glitch.tuple.javac.TupleDefinitionScanner;
import io.github.amusing_glitch.tuple.validators.NamedTupleArgumentOrderValidator;
import io.github.amusing_glitch.tuple.validators.Validator;

import java.util.List;

class TupleSpecProcessorBootstrap {
    private static final PebbleTemplateProcessor PEBBLE_TEMPLATE_PROCESSOR = new PebbleTemplateProcessor("templates");
    public static final TupleGenerator TUPLE_GENERATOR = new TupleGenerator(PEBBLE_TEMPLATE_PROCESSOR);
    public static final DynamicTupleGenerator DYNAMIC_TUPLE_GENERATOR = new DynamicTupleGenerator(
            PEBBLE_TEMPLATE_PROCESSOR,
            new StaticTupleFactoryGenerator(PEBBLE_TEMPLATE_PROCESSOR),
            new ZipperMethodGenerator(PEBBLE_TEMPLATE_PROCESSOR),
            new NamedZipperMethodGenerator(PEBBLE_TEMPLATE_PROCESSOR),
            new StaticNamedTupleFactoryGenerator(PEBBLE_TEMPLATE_PROCESSOR)
    );
    public static final TupleDefinitionScanner TUPLE_DEFINITION_SCANNER = new TupleDefinitionScanner(false);
    public static final JavaFileWriter JAVA_FILE_WRITER = new JavaFileWriter();
    public static final List<Validator> VALIDATORS = List.of(new NamedTupleArgumentOrderValidator());
}
