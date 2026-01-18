package com.aparigraha.tuple.dynamic;

import com.aparigraha.tuple.templates.PebbleTemplateProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class DynamicTupleGenerator {
    private final PebbleTemplateProcessor pebbleTemplateProcessor;
    private final StaticTupleFactoryGenerator staticTupleFactoryGenerator;


    public DynamicTupleGenerator(
            PebbleTemplateProcessor pebbleTemplateProcessor,
            StaticTupleFactoryGenerator staticTupleFactoryGenerator
    ) {
        this.pebbleTemplateProcessor = pebbleTemplateProcessor;
        this.staticTupleFactoryGenerator = staticTupleFactoryGenerator;
    }


    public String generate(Set<Integer> tupleSizes) throws IOException {
        List<String> tupleFactoryMethods = new ArrayList<>();
        for (int tupleSize: tupleSizes) {
            tupleFactoryMethods.add(staticTupleFactoryGenerator.generate(tupleSize));
        }

        return pebbleTemplateProcessor.process(
                "DynamicTuple.peb",
                Map.of("staticFactoryMethods", String.join("\n", tupleFactoryMethods))
        );
    }

}
