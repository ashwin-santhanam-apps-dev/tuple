package com.aparigraha.tuple;

import com.aparigraha.tuple.dynamic.factories.DynamicTupleGenerator;
import com.aparigraha.tuple.dynamic.factories.StaticTupleFactoryGenerator;
import com.aparigraha.tuple.dynamic.factories.ZipperMethodGenerator;
import com.aparigraha.tuple.dynamic.entities.TupleGenerationParams;
import com.aparigraha.tuple.dynamic.entities.TupleGenerator;
import com.aparigraha.tuple.dynamic.GeneratedClassSchema;
import com.aparigraha.tuple.dynamic.templates.PebbleTemplateProcessor;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

import static com.aparigraha.tuple.dynamic.templates.JavaTemplate.*;

@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class TupleSpecProcessor extends AbstractProcessor {
    private static final Set<String> targetMethods = Set.of(dynamicTupleFactoryMethod(), dynamicTupleZipMethod());


    private final DynamicTupleGenerator dynamicTupleGenerator;
    private final TupleGenerator tupleGenerator;
    private boolean hasGenerated = false;
    private Trees trees;

    public TupleSpecProcessor(TupleGenerator tupleGenerator, DynamicTupleGenerator dynamicTupleGenerator) {
        this.tupleGenerator = tupleGenerator;
        this.dynamicTupleGenerator = dynamicTupleGenerator;
    }


    public TupleSpecProcessor(PebbleTemplateProcessor pebbleTemplateProcessor) {
        this(
                new TupleGenerator(pebbleTemplateProcessor),
                new DynamicTupleGenerator(
                        pebbleTemplateProcessor,
                        new StaticTupleFactoryGenerator(pebbleTemplateProcessor),
                        new ZipperMethodGenerator(pebbleTemplateProcessor)
                )
        );
    }

    public TupleSpecProcessor() {
        this(new PebbleTemplateProcessor("templates"));
    }


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.trees = Trees.instance(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!hasGenerated) {
            var fields = new HashSet<Integer>();
            var methodCallScanner = new TreePathScanner<Void, Void>() {
                @Override
                public Void visitMethodInvocation(MethodInvocationTree node, Void p) {
                    if (isTargetMethod(node)) {
                        fields.add(node.getArguments().size());
                    }
                    return super.visitMethodInvocation(node, p);
                }

                private boolean isTargetMethod(MethodInvocationTree node) {
                    return targetMethods
                            .contains(node.getMethodSelect().toString());
                }
            };

            for (Element element : roundEnv.getRootElements()) {
                if (element.getKind().isClass() || element.getKind().isInterface()) {
                    methodCallScanner.scan(trees.getPath(element), null);
                }
            }

            fields.stream()
                    .map(size -> new TupleGenerationParams(
                            packageName,
                            classPrefix + size,
                            parameterPrefix,
                            size
                    )).map(this::generateTuple)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(generatedClassSchema -> save(generatedClassSchema.javaCode(), generatedClassSchema.completeClassName()));

            generateDynamicTupleFactoryClass(fields);
            hasGenerated = true;
        }
        return false;
    }

    private Optional<GeneratedClassSchema> generateTuple(TupleGenerationParams params) {
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


    private void generateDynamicTupleFactoryClass(Set<Integer> fields) {
        try {
            save(dynamicTupleGenerator.generate(fields), packageName + ".DynamicTuple");
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    "Error creating DynamicTuple class"
            );
        }
    }


    private void save(String javaCode, String path) {
        try {
            JavaFileObject javaFileObject = processingEnv.getFiler().createSourceFile(path);
            try (Writer writer = javaFileObject.openWriter()) {
                writer.write(javaCode);
            }
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    "Error creating DynamicTuple class"
            );
        }
    }
}
