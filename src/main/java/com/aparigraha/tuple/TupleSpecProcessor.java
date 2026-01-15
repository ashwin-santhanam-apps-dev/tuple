package com.aparigraha.tuple;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.loader.ClasspathLoader;
import io.pebbletemplates.pebble.template.PebbleTemplate;

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
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;


@SupportedAnnotationTypes("com.aparigraha.tuple.TupleSpec")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class TupleSpecProcessor extends AbstractProcessor {
    private static final PebbleTemplate template = getTemplate();


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        return roundEnv.getElementsAnnotatedWith(TupleSpec.class)
                .stream()
                .map(element -> element.getAnnotation(TupleSpec.class))
                .flatMap(tupleSpec -> Arrays.stream(tupleSpec.value()).boxed())
                .distinct()
                .allMatch(size -> saveTupleSchema(generateTuple(size), size));
    }


    private static PebbleTemplate getTemplate() {
        ClasspathLoader loader = new ClasspathLoader();
        loader.setPrefix("templates");
        PebbleEngine engine = new PebbleEngine.Builder().strictVariables(true).loader(loader).build();
        return engine.getTemplate("Tuple.peb");
    }


    private String generateTuple(int size) {
        Writer writer = new StringWriter();
        try {
            template.evaluate(writer, Map.of("size", size));
            return writer.toString();
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
                    .createSourceFile("com.aparigraha.tuples.Tuple" + size);
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
}
