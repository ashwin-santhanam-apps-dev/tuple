package com.aparigraha.tuple;

import com.aparigraha.tuple.generator.TupleGenerationParams;
import com.aparigraha.tuple.generator.TupleGenerator;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Set;


@SupportedAnnotationTypes("com.aparigraha.tuple.TupleSpec")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class TupleSpecProcessor extends AbstractProcessor {
    private static final String packageName = "com.aparigraha.tuples";
    private static final String tuple = "Tuple";
    private static final String fieldPrefix = "item";

    private final TupleGenerator tupleGenerator;

    public TupleSpecProcessor(TupleGenerator tupleGenerator) {
        this.tupleGenerator = tupleGenerator;
    }

    public TupleSpecProcessor() {
        this(new TupleGenerator());
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        return roundEnv.getElementsAnnotatedWith(TupleSpec.class)
                .stream()
                .map(element -> element.getAnnotation(TupleSpec.class))
                .flatMap(tupleSpec -> Arrays.stream(tupleSpec.value()).boxed())
                .distinct()
                .allMatch(size -> saveTupleSchema(generateTuple(size), size));
    }


    private String generateTuple(int size) {
        try {
            return tupleGenerator.generate(
                    new TupleGenerationParams(
                            packageName,
                            className(size),
                            fieldPrefix,
                            size
                    )
            );
        } catch (IOException exception) {
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    "Failed creating Tuple with size: " + size + "\n" + exception.getMessage()
            );
        }
        return null;
    }

    private boolean saveTupleSchema(String tupleSchema, int size) {
        if (tupleSchema == null) {
            return false;
        }
        try {
            JavaFileObject file = processingEnv
                    .getFiler()
                    .createSourceFile(completeClassName(size));
            try (PrintWriter out = new PrintWriter(file.openWriter())) {
                out.print(tupleSchema);
            }
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.NOTE,
                    "Created Tuple class with size: " + size
            );
            return true;
        } catch (IOException exception) {
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    "Failed creating Tuple class with size: " + size + "\n" + exception.getMessage()
            );
            return false;
        }
    }

    private static String className(int size) {
        return tuple + size;
    }

    private static String completeClassName(int size) {
        return "%s.%s".formatted(packageName, className(size));
    }
}
