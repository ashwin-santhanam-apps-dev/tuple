package com.aparigraha.tuple.dynamic.factories;

import com.aparigraha.tuple.dynamic.GeneratedClassSchema;
import com.aparigraha.tuple.dynamic.templates.PebbleTemplateProcessor;
import com.aparigraha.tuple.javac.scan.result.NamedTupleDefinition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class DynamicTupleGenerator {
    private final PebbleTemplateProcessor pebbleTemplateProcessor;
    private final StaticTupleFactoryGenerator staticTupleFactoryGenerator;
    private final ZipperMethodGenerator zipperMethodGenerator;
    private final StaticNamedTupleFactoryGenerator staticNamedTupleFactoryGenerator;


    public DynamicTupleGenerator(
            PebbleTemplateProcessor pebbleTemplateProcessor,
            StaticTupleFactoryGenerator staticTupleFactoryGenerator,
            ZipperMethodGenerator zipperMethodGenerator,
            StaticNamedTupleFactoryGenerator staticNamedTupleFactoryGenerator
    ) {
        this.pebbleTemplateProcessor = pebbleTemplateProcessor;
        this.staticTupleFactoryGenerator = staticTupleFactoryGenerator;
        this.zipperMethodGenerator = zipperMethodGenerator;
        this.staticNamedTupleFactoryGenerator = staticNamedTupleFactoryGenerator;
    }


    public GeneratedClassSchema generate(DynamicTupleGenerationParam param) throws IOException {
        List<String> tupleFactoryMethods = new ArrayList<>();
        List<String> imports = new ArrayList<>();
        for (int tupleSize: param.tupleSizes()) {
            tupleFactoryMethods.add(staticTupleFactoryGenerator.generate(tupleSize));
            tupleFactoryMethods.add(zipperMethodGenerator.generate(tupleSize));
        }
        for (NamedTupleDefinition namedTupleDefinition: param.namedTupleDefinitions()) {
            imports.add(namedTupleDefinition.qualifiedName());
            tupleFactoryMethods.add(staticNamedTupleFactoryGenerator.generate(namedTupleDefinition));
        }

        return new GeneratedClassSchema(
                param.packageName(),
                param.dynamicTupleClassName(),
                pebbleTemplateProcessor.process(
                        "DynamicTuple.peb",
                        Map.of(
                                "packageName", param.packageName(),
                                "imports", imports.stream().map("import %s;"::formatted).collect(Collectors.joining("\n")),
                                "dynamicTupleClassName", param.dynamicTupleClassName(),
                                "dynamicTupleFactoryMethodName", param.dynamicTupleFactoryMethodName(),
                                "dynamicTupleZipMethodName", param.dynamicTupleZipMethodName(),
                                "namedTupleFactoryMethodName", param.namedTupleFactoryMethodName(),
                                "staticFactoryMethods", String.join("\n", tupleFactoryMethods)
                        )
                )
        );
    }
}
