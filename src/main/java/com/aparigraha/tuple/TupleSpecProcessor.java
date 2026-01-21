package com.aparigraha.tuple;

import com.aparigraha.tuple.domain.TupleSpecs;
import com.aparigraha.tuple.domain.NumberedTupleSpec;
import com.aparigraha.tuple.dynamic.JavaFileWriter;
import com.aparigraha.tuple.dynamic.factories.DynamicTupleGenerator;
import com.aparigraha.tuple.dynamic.factories.DynamicTupleGenerationParam;
import com.aparigraha.tuple.dynamic.entities.TupleGenerationParams;
import com.aparigraha.tuple.dynamic.entities.TupleGenerator;
import com.aparigraha.tuple.dynamic.GeneratedClassSchema;
import com.aparigraha.tuple.javac.*;
import com.sun.source.util.Trees;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.aparigraha.tuple.SupportedTupleDefinitions.*;
import static com.aparigraha.tuple.TupleSpecProcessorBootstrap.*;
import static com.aparigraha.tuple.dynamic.templates.JavaTemplate.*;
import static javax.lang.model.element.ElementKind.*;

@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class TupleSpecProcessor extends OncePerLifecycleProcessor {
    private static final Set<TupleDefinitionSpec> tupleDefinitionSpecs = Set.of(
            TUPLE_FACTORY_METHOD_SPEC,
            TUPLE_ZIP_METHOD_SPEC,
            NAMED_TUPLE_FACTORY_METHOD_SPEC
    );
    private static final Set<ElementKind> supportedRootElements = Set.of(CLASS, INTERFACE, RECORD);

    private final DynamicTupleGenerator dynamicTupleGenerator;
    private final TupleGenerator tupleGenerator;
    private final TupleDefinitionScanner tupleDefinitionScanner;
    private final JavaFileWriter javaFileWriter;

    private Trees trees;

    public TupleSpecProcessor(
            TupleGenerator tupleGenerator,
            DynamicTupleGenerator dynamicTupleGenerator,
            TupleDefinitionScanner tupleDefinitionScanner,
            JavaFileWriter javaFileWriter
    ) {
        this.tupleGenerator = tupleGenerator;
        this.dynamicTupleGenerator = dynamicTupleGenerator;
        this.tupleDefinitionScanner = tupleDefinitionScanner;
        this.javaFileWriter = javaFileWriter;
    }

    // Required constructor for Service Discovery
    public TupleSpecProcessor() {
        this(
                TUPLE_GENERATOR,
                DYNAMIC_TUPLE_GENERATOR,
                TUPLE_DEFINITION_SCANNER,
                JAVA_FILE_WRITER
        );
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        trees = Trees.instance(processingEnv);
    }

    @Override
    public boolean processFirstRound(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        var rootElements = extractValidRootElements(roundEnv);

        var tupleSpecs = extractTupleDefinitions(rootElements);

        createTupleClasses(tupleSpecs);

        createFactoryClass(tupleSpecs);

        return false;
    }

    private List<TypeElement> extractValidRootElements(RoundEnvironment roundEnv) {
        return roundEnv.getRootElements().stream()
                .filter(element -> supportedRootElements.contains(element.getKind()))
                .filter(element -> element instanceof TypeElement)
                .map(element -> (TypeElement) element)
                .toList();
    }


    private TupleSpecs extractTupleDefinitions(List<TypeElement> elements) {
        return elements.stream()
                .map(element -> tupleDefinitionScanner.scan(
                        tupleDefinitionSpecs,
                        trees,
                        processingEnv.getElementUtils(),
                        element
                )).reduce(
                        new TupleSpecs(),
                        TupleSpecs::add
                );
    }

    private void createTupleClasses(TupleSpecs tupleSpecs) {
        tupleSpecs.numberedTupleSpecs().stream()
                .map(spec -> new TupleGenerationParams(
                        packageName,
                        className(spec.argumentCount()),
                        parameterPrefix,
                        spec.argumentCount()
                )).map(this::generateTupleClass)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(this::save);
    }

    private void createFactoryClass(TupleSpecs tupleSpecs) {
        generateDynamicTupleClass(new DynamicTupleGenerationParam(
                packageName,
                dynamicTupleClassName,
                dynamicTupleFactoryMethodName,
                dynamicTupleZipMethodName,
                namedTupleFactoryMethodName,
                tupleSpecs.numberedTupleSpecs().stream()
                        .map(NumberedTupleSpec::argumentCount)
                        .collect(Collectors.toSet())
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
