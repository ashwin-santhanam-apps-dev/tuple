package com.aparigraha.tuple;

import com.aparigraha.tuple.generator.TupleGenerationParams;
import com.aparigraha.tuple.generator.TupleGenerator;
import com.aparigraha.tuple.generator.TupleSchema;
import com.aparigraha.tuple.generator.TupleSchemaWriter;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;


@SupportedAnnotationTypes("com.aparigraha.tuple.TupleSpec")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class TupleSpecProcessor extends AbstractProcessor {
    private static final String packageName = "com.aparigraha.tuples";
    private static final String tuple = "Tuple";
    private static final String fieldPrefix = "item";

    private final TupleGenerator tupleGenerator;
    private final TupleSchemaWriter tupleSchemaWriter;

    public TupleSpecProcessor(TupleGenerator tupleGenerator, TupleSchemaWriter tupleSchemaWriter) {
        super();
        this.tupleGenerator = tupleGenerator;
        this.tupleSchemaWriter = tupleSchemaWriter;
    }

    public TupleSpecProcessor() {
        this(new TupleGenerator(), new TupleSchemaWriter());
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        annotations.stream()
                .map(roundEnv::getElementsAnnotatedWith)
                .flatMap(Set::stream)
                .map(element -> element.getAnnotation(TupleSpec.class))
                .flatMap(tupleSpec -> Arrays.stream(tupleSpec.value()).boxed())
                .distinct()
                .map(size -> new TupleGenerationParams(
                        packageName,
                        tuple + size,
                        fieldPrefix,
                        size
                ))
                .map(this::generateTuple)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(this::saveTupleSchema);

        return canClaim(annotations);
    }


    private boolean canClaim(Set<? extends TypeElement> annotations) {
        var supportedAnnotations = getSupportedAnnotationTypes();
        return annotations.stream()
                .map(a -> a.getQualifiedName().toString())
                .anyMatch(supportedAnnotations::contains);
    }


    private Optional<TupleSchema> generateTuple(TupleGenerationParams params) {
        try {
            return Optional.of(tupleGenerator.generate(params));
        } catch (IOException exception) {
            if (processingEnv != null)
                processingEnv.getMessager().printMessage(
                        Diagnostic.Kind.ERROR,
                        "Failed creating tuple: \"%s.%s\". Exception: %s".formatted(
                                params.packageName(),
                                params.className(),
                                exception.getMessage()
                        )
                );
            return Optional.empty();
        }
    }


    private void saveTupleSchema(TupleSchema tupleSchema) {
        try {
            tupleSchemaWriter.write(tupleSchema.javaCode(), tupleSchema.completeClassName(), processingEnv);
        } catch (IOException exception) {
            if (processingEnv != null)
                processingEnv.getMessager().printMessage(
                        Diagnostic.Kind.ERROR,
                        "Failed creating tuple: \"%s.%s\". Exception: %s".formatted(
                                tupleSchema.packageName(),
                                tupleSchema.className(),
                                exception.getMessage()
                        )
                );
        }
    }
}
