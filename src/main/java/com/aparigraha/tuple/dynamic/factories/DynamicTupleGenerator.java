package com.aparigraha.tuple.dynamic.factories;

import com.aparigraha.tuple.dynamic.GeneratedClassSchema;
import com.aparigraha.tuple.dynamic.templates.PebbleTemplateProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class DynamicTupleGenerator {
    private final PebbleTemplateProcessor pebbleTemplateProcessor;
    private final StaticTupleFactoryGenerator staticTupleFactoryGenerator;
    private final ZipperMethodGenerator zipperMethodGenerator;


    public DynamicTupleGenerator(
            PebbleTemplateProcessor pebbleTemplateProcessor,
            StaticTupleFactoryGenerator staticTupleFactoryGenerator,
            ZipperMethodGenerator zipperMethodGenerator
    ) {
        this.pebbleTemplateProcessor = pebbleTemplateProcessor;
        this.staticTupleFactoryGenerator = staticTupleFactoryGenerator;
        this.zipperMethodGenerator = zipperMethodGenerator;
    }


    public GeneratedClassSchema generate(DynamicTupleGenerationParam param) throws IOException {
        List<String> tupleFactoryMethods = new ArrayList<>();
        for (int tupleSize: param.tupleSizes()) {
            tupleFactoryMethods.add(staticTupleFactoryGenerator.generate(tupleSize));
            tupleFactoryMethods.add(zipperMethodGenerator.generate(tupleSize));
        }

        return new GeneratedClassSchema(
                param.packageName(),
                param.dynamicTupleClassName(),
                pebbleTemplateProcessor.process(
                        "DynamicTuple.peb",
                        Map.of(
                                "packageName", param.packageName(),
                                "dynamicTupleClassName", param.dynamicTupleClassName(),
                                "dynamicTupleFactoryMethodName", param.dynamicTupleFactoryMethodName(),
                                "staticFactoryMethods", String.join("\n", tupleFactoryMethods)
                        )
                )
        );
    }
}
