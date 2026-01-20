package com.aparigraha.tuple;

import com.aparigraha.tuple.dynamic.JavaFileWriter;
import com.aparigraha.tuple.dynamic.factories.DynamicTupleGenerator;
import com.aparigraha.tuple.dynamic.factories.DynamicTupleGenerationParam;
import com.aparigraha.tuple.dynamic.factories.StaticTupleFactoryGenerator;
import com.aparigraha.tuple.dynamic.factories.ZipperMethodGenerator;
import com.aparigraha.tuple.dynamic.entities.TupleGenerationParams;
import com.aparigraha.tuple.dynamic.entities.TupleGenerator;
import com.aparigraha.tuple.dynamic.GeneratedClassSchema;
import com.aparigraha.tuple.dynamic.templates.PebbleTemplateProcessor;
import com.sun.source.util.Trees;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.aparigraha.tuple.dynamic.templates.JavaTemplate.*;

@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class TupleSpecProcessor extends OncePerLifecycleProcessor {
    private static final Set<String> targetMethods = Set.of(dynamicTupleFactoryMethod(), dynamicTupleZipMethod());

    private final DynamicTupleGenerator dynamicTupleGenerator;
    private final TupleGenerator tupleGenerator;
    private final MethodScanner methodScanner;
    private final JavaFileWriter javaFileWriter;

    private Trees trees;

    public TupleSpecProcessor(
            TupleGenerator tupleGenerator,
            DynamicTupleGenerator dynamicTupleGenerator,
            MethodScanner methodScanner,
            JavaFileWriter javaFileWriter
    ) {
        this.tupleGenerator = tupleGenerator;
        this.dynamicTupleGenerator = dynamicTupleGenerator;
        this.methodScanner = methodScanner;
        this.javaFileWriter = javaFileWriter;
    }

    // Required constructor for Service Discovery
    public TupleSpecProcessor() {
        this(
                TupleSpecProcessorDependencies.tupleGenerator,
                TupleSpecProcessorDependencies.dynamicTupleGenerator,
                TupleSpecProcessorDependencies.methodScanner,
                TupleSpecProcessorDependencies.javaFileWriter
        );
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        trees = Trees.instance(processingEnv);
    }

    @Override
    public boolean processFirstRound(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        var tupleDefinitions = extractTupleDefinitions(roundEnv);

        createTupleClasses(tupleDefinitions);

        createFactoryClass(tupleDefinitions);

        return false;
    }

    private Set<Integer> extractTupleDefinitions(RoundEnvironment roundEnv) {
        return roundEnv.getRootElements().stream()
                .filter(element -> element.getKind().isClass() || element.getKind().isInterface())
                .map(element -> trees.getPath(element))
                .map(treePath ->
                        methodScanner.scan(
                                node -> targetMethods.contains(node.getMethodSelect().toString()),
                                treePath
                        )
                )
                .flatMap(Collection::stream)
                .map(node -> node.getArguments().size())
                .collect(Collectors.toSet());
    }

    private void createTupleClasses(Set<Integer> tupleDefinitions) {
        tupleDefinitions.stream()
                .map(size -> new TupleGenerationParams(
                        packageName,
                        classPrefix + size,
                        parameterPrefix,
                        size
                )).map(this::generateTupleClass)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(this::save);
    }

    private void createFactoryClass(Set<Integer> tupleDefinitions) {
        generateDynamicTupleClass(new DynamicTupleGenerationParam(
                packageName,
                dynamicTupleClassName,
                dynamicTupleFactoryMethodName,
                tupleDefinitions
        )).ifPresent(this::save);
    }

    private Optional<GeneratedClassSchema> generateTupleClass(TupleGenerationParams params) {
        try {
            return Optional.of(tupleGenerator.generate(params));
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    "Error creating Tuple class: " + params.className() + "\n" + e.getMessage()
            );
            return Optional.empty();
        }
    }

    private Optional<GeneratedClassSchema> generateDynamicTupleClass(DynamicTupleGenerationParam param) {
        try {
            return Optional.of(dynamicTupleGenerator.generate(param));
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    "Error creating DynamicTuple class"
            );
            return Optional.empty();
        }
    }

    private void save(GeneratedClassSchema schema) {
        try {
            javaFileWriter.write(processingEnv, schema);
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    "Error creating DynamicTuple class"
            );
        }
    }
}


class TupleSpecProcessorDependencies {
    private static final PebbleTemplateProcessor pebbleTemplateProcessor = new PebbleTemplateProcessor("templates");
    public static final TupleGenerator tupleGenerator = new TupleGenerator(pebbleTemplateProcessor);
    public static final DynamicTupleGenerator dynamicTupleGenerator = new DynamicTupleGenerator(
            pebbleTemplateProcessor,
            new StaticTupleFactoryGenerator(pebbleTemplateProcessor),
            new ZipperMethodGenerator(pebbleTemplateProcessor)
    );
    public static final MethodScanner methodScanner = new MethodScanner();
    public static final JavaFileWriter javaFileWriter = new JavaFileWriter();
}
