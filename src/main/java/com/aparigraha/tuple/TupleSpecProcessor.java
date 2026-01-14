package com.aparigraha.tuple;

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
import java.util.function.Predicate;


@SupportedAnnotationTypes("com.aparigraha.tuple.TupleSpec")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class TupleSpecProcessor extends AbstractProcessor {
    private final TupleSchemaGenerator tupleSchemaGenerator = new TupleSchemaGenerator(
            "com.aparigraha.tuple",
            "Tuple",
            "T",
            "item"
    );


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        return roundEnv.getElementsAnnotatedWith(TupleSpec.class)
                .stream()
                .map(element -> element.getAnnotation(TupleSpec.class))
                .flatMap(tupleSpec -> Arrays.stream(tupleSpec.value()).boxed())
                .distinct()
                .map(tupleSchemaGenerator::generate)
                .map(this::saveTupleSchema)
                .allMatch(Predicate.isEqual(true));
    }


    private boolean saveTupleSchema(TupleSchema tupleSchema) {
        try {
            JavaFileObject file = processingEnv
                    .getFiler()
                    .createSourceFile(tupleSchema.fullyQualifiedClassName());
            try (PrintWriter out = new PrintWriter(file.openWriter())) {
                out.print(tupleSchema.textContent());
            }
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.NOTE,
                    "Created Tuple class: " + tupleSchema.className()
            );
            return true;
        } catch (IOException exception) {
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    "Failed creating Tuple class: " + tupleSchema.className() + "\n" + exception.getMessage()
            );
            return false;
        }
    }
}
