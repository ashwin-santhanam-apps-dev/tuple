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
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.aparigraha.tuple.TupleSpecProcessorDependencies.*;
import static com.aparigraha.tuple.dynamic.templates.JavaTemplate.*;
import static javax.lang.model.element.ElementKind.*;

@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class TupleSpecProcessor extends OncePerLifecycleProcessor {
    private static final Set<ElementKind> supportedRootElements = Set.of(CLASS, INTERFACE, RECORD);

    private final DynamicTupleGenerator dynamicTupleGenerator;
    private final TupleGenerator tupleGenerator;
    private final StaticMethodScanner staticMethodScanner;
    private final JavaFileWriter javaFileWriter;

    private Trees trees;

    public TupleSpecProcessor(
            TupleGenerator tupleGenerator,
            DynamicTupleGenerator dynamicTupleGenerator,
            StaticMethodScanner staticMethodScanner,
            JavaFileWriter javaFileWriter
    ) {
        this.tupleGenerator = tupleGenerator;
        this.dynamicTupleGenerator = dynamicTupleGenerator;
        this.staticMethodScanner = staticMethodScanner;
        this.javaFileWriter = javaFileWriter;
    }

    // Required constructor for Service Discovery
    public TupleSpecProcessor() {
        this(
                TUPLE_GENERATOR,
                DYNAMIC_TUPLE_GENERATOR,
                STATIC_METHOD_SCANNER,
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

        var imports = extractImports(rootElements);

        var tupleDefinitions = extractTupleDefinitions(rootElements, imports);

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

    private Map<String, List<ImportStatement>> extractImports(List<TypeElement> rootElements) {
        var imports = new HashMap<String, List<ImportStatement>>();
        for (TypeElement rootElement: rootElements) {
            TreePath path = trees.getPath(rootElement);
            if (path == null) continue;
            CompilationUnitTree compilationUnit = path.getCompilationUnit();
            var currentImports = compilationUnit.getImports().stream()
                    .map(importTree -> new ImportStatement(
                            importTree.getQualifiedIdentifier().toString(),
                            importTree.isStatic()
                    )).toList();
            imports.put(
                    rootElement.getQualifiedName().toString(),
                    currentImports
            );
        }
        return imports;
    }

    private Set<Integer> extractTupleDefinitions(List<TypeElement> elements, Map<String, List<ImportStatement>> imports) {
        return elements.stream()
                .map(element -> {
                    var treePath = trees.getPath(element);
                    var currentImports = imports.get(element.getQualifiedName().toString());
                    return staticMethodScanner.scan(
                            node -> {
                                String caller = node.getMethodSelect().toString();
                                if (caller.startsWith(packageName + "." + dynamicTupleClassName)) {
                                    return true;
                                } else if (caller.startsWith(dynamicTupleClassName)) {
                                    return currentImports.stream()
                                            .anyMatch(importStatement ->
                                                    !importStatement.isStatic() &&
                                                    (
                                                            Objects.equals(importStatement.identifier(), packageName + "*") ||
                                                            Objects.equals(importStatement.identifier(), packageName + "." + dynamicTupleClassName)
                                                    )
                                            );
                                } else if (caller.startsWith(dynamicTupleFactoryMethodName) || caller.startsWith(dynamicTupleZipMethodName)) {
                                    return currentImports.stream()
                                            .anyMatch(importStatement ->
                                                importStatement.isStatic() &&
                                                (
                                                        Objects.equals(importStatement.identifier(), "com.aparigraha.tuple.dynamic.DynamicTuple.*") ||
                                                        Objects.equals(importStatement.identifier(), "com.aparigraha.tuple.dynamic.DynamicTuple." + caller)
                                                )
                                            );
                                } else {
                                    return false;
                                }
                            },
                            treePath
                    );
                })
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

record ImportStatement(
        String identifier,
        boolean isStatic
) {}
