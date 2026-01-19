package com.aparigraha.tuple.dynamic.factories;

import com.aparigraha.tuple.dynamic.templates.PebbleTemplateProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.aparigraha.tuple.dynamic.templates.JavaTemplate.*;


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


    public String generate(Set<Integer> tupleSizes) throws IOException {
        List<String> tupleFactoryMethods = new ArrayList<>();
        for (int tupleSize: tupleSizes) {
            tupleFactoryMethods.add(staticTupleFactoryGenerator.generate(tupleSize));
            tupleFactoryMethods.add(zipperMethodGenerator.generate(tupleSize));
        }

        return pebbleTemplateProcessor.process(
                "DynamicTuple.peb",
                Map.of(
                        "packageName", packageName,
                        "dynamicTupleClassName", dynamicTupleClassName,
                        "dynamicTupleFactoryMethodName", dynamicTupleFactoryMethodName,
                        "staticFactoryMethods", String.join("\n", tupleFactoryMethods)
                )
        );
    }

}
