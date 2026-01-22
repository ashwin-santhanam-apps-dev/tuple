package com.aparigraha.tuple;

import com.aparigraha.tuple.dynamic.JavaFileWriter;
import com.aparigraha.tuple.dynamic.entities.*;
import com.aparigraha.tuple.dynamic.factories.DynamicTupleGenerator;
import com.aparigraha.tuple.dynamic.factories.DynamicTupleGenerationParam;
import com.aparigraha.tuple.dynamic.GeneratedClassSchema;
import com.aparigraha.tuple.javac.NumberedTupleDefinition;
import com.aparigraha.tuple.javac.TupleDefinitionScanResult;
import com.aparigraha.tuple.javac.TupleDefinitionScanner;
import com.aparigraha.tuple.javac.TupleDefinitionSpec;
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
    private final NamedTupleGenerator namedTupleGenerator;
    private final TupleDefinitionScanner tupleDefinitionScanner;
    private final JavaFileWriter javaFileWriter;

    private Trees trees;

    public TupleSpecProcessor(
            TupleGenerator tupleGenerator,
            NamedTupleGenerator namedTupleGenerator,
            DynamicTupleGenerator dynamicTupleGenerator,
            TupleDefinitionScanner tupleDefinitionScanner,
            JavaFileWriter javaFileWriter
    ) {
        this.tupleGenerator = tupleGenerator;
        this.namedTupleGenerator = namedTupleGenerator;
        this.dynamicTupleGenerator = dynamicTupleGenerator;
        this.tupleDefinitionScanner = tupleDefinitionScanner;
        this.javaFileWriter = javaFileWriter;
    }

    // Required constructor for Service Discovery
    public TupleSpecProcessor() {
        this(
                TUPLE_GENERATOR,
                NAMED_TUPLE_GENERATOR,
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

        var tupleDefinitions = extractTupleDefinitions(rootElements);

        createTupleClasses(tupleDefinitions);

        createFactoryClass(tupleDefinitions);

        return false;
    }

    private List<TypeElement> extractValidRootElements(RoundEnvironment roundEnv) {
        return roundEnv.getRootElements().stream()
                .filter(element -> supportedRootElements.contains(element.getKind()))
                .filter(element -> element instanceof TypeElement)
                .map(element -> (TypeElement) element)
                .toList();
    }


    private TupleDefinitionScanResult extractTupleDefinitions(List<TypeElement> elements) {
        return elements.stream()
                .map(element -> tupleDefinitionScanner.scan(
                        tupleDefinitionSpecs,
                        trees,
                        processingEnv.getElementUtils(),
                        element
                ))
                .reduce(TupleDefinitionScanResult::add)
                .orElseGet(TupleDefinitionScanResult::new);
    }


    private void createTupleClasses(TupleDefinitionScanResult scanResult) {
        scanResult.numberedTupleDefinitions().stream()
                .map(definition -> new TupleGenerationParams(
                        packageName,
                        className(definition.argumentCount()),
                        parameterPrefix,
                        definition.argumentCount()
                )).map(this::generateTupleClass)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(this::save);

        scanResult.namedTupleDefinitions().stream()
                .map(definition ->
                        new NamedTupleGenerationParams(
                                definition.packageName(),
                                definition.className(),
                                definition.fields().stream().map( fieldDefinition ->
                                        new NamedTupleGenerationFieldParams(
                                                fieldDefinition.index(),
                                                fieldDefinition.type(),
                                                fieldDefinition.name()
                                        )
                                ).collect(Collectors.toSet())
                        )
                )
                .map(this::generateNamedTupleClass)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(this::save);
    }

    private void createFactoryClass(TupleDefinitionScanResult scanResult) {
        generateDynamicTupleClass(new DynamicTupleGenerationParam(
                packageName,
                dynamicTupleClassName,
                dynamicTupleFactoryMethodName,
                dynamicTupleZipMethodName,
                namedTupleFactoryMethodName,
                scanResult.numberedTupleDefinitions().stream()
                        .map(NumberedTupleDefinition::argumentCount)
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

    private Optional<GeneratedClassSchema> generateNamedTupleClass(NamedTupleGenerationParams params) {
        try {
            return Optional.of(namedTupleGenerator.generate(params));
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
