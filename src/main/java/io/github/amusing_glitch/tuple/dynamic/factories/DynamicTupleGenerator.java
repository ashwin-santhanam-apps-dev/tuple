package io.github.amusing_glitch.tuple.dynamic.factories;

import io.github.amusing_glitch.tuple.dynamic.GeneratedClassSchema;
import io.github.amusing_glitch.tuple.dynamic.templates.PebbleTemplateProcessor;
import io.github.amusing_glitch.tuple.javac.NamedTupleDefinition;
import io.github.amusing_glitch.tuple.javac.NamedTupleField;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class DynamicTupleGenerator {
    private final PebbleTemplateProcessor pebbleTemplateProcessor;
    private final StaticTupleFactoryGenerator staticTupleFactoryGenerator;
    private final ZipperMethodGenerator zipperMethodGenerator;
    private final NamedZipperMethodGenerator namedZipperMethodGenerator;
    private final StaticNamedTupleFactoryGenerator staticNamedTupleFactoryGenerator;


    public DynamicTupleGenerator(
            PebbleTemplateProcessor pebbleTemplateProcessor,
            StaticTupleFactoryGenerator staticTupleFactoryGenerator,
            ZipperMethodGenerator zipperMethodGenerator,
            NamedZipperMethodGenerator namedZipperMethodGenerator,
            StaticNamedTupleFactoryGenerator staticNamedTupleFactoryGenerator
    ) {
        this.pebbleTemplateProcessor = pebbleTemplateProcessor;
        this.staticTupleFactoryGenerator = staticTupleFactoryGenerator;
        this.zipperMethodGenerator = zipperMethodGenerator;
        this.namedZipperMethodGenerator = namedZipperMethodGenerator;
        this.staticNamedTupleFactoryGenerator = staticNamedTupleFactoryGenerator;
    }


    public GeneratedClassSchema generate(DynamicTupleGenerationParam param) throws IOException {
        List<String> tupleFactoryMethods = new ArrayList<>();
        List<String> imports = new ArrayList<>();
        for (int tupleSize: param.tupleSizes()) {
            tupleFactoryMethods.add(staticTupleFactoryGenerator.generate(tupleSize));
            tupleFactoryMethods.add(zipperMethodGenerator.generate(tupleSize));
        }

        record ClassNameAndFields(String qualifiedName, String className, Set<NamedTupleField> fields) {}
        var distinctNamedTupleDefinitions = param.namedTupleDefinitions().stream()
                .map(namedTupleDefinition ->
                        new ClassNameAndFields(
                                namedTupleDefinition.qualifiedName(),
                                namedTupleDefinition.className(),
                                namedTupleDefinition.fields())
                )
                .distinct()
                .toList();
        for (ClassNameAndFields namedTupleDefinition: distinctNamedTupleDefinitions) {
            imports.add(namedTupleDefinition.qualifiedName());
            tupleFactoryMethods.add(staticNamedTupleFactoryGenerator.generate(namedTupleDefinition.className(), namedTupleDefinition.fields()));
            tupleFactoryMethods.add(namedZipperMethodGenerator.generate(namedTupleDefinition.className(), namedTupleDefinition.fields().size()));
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
                                "dynamicNamedTupleZipMethodName", param.dynamicNamedTupleZipMethodName(),
                                "namedTupleFactoryMethodName", param.namedTupleFactoryMethodName(),
                                "staticFactoryMethods", String.join("\n", tupleFactoryMethods)
                        )
                )
        );
    }
}
