package com.aparigraha.tuple.generator;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;

public class TupleSchemaWriter {
    public boolean write(String tupleSchema, String path, ProcessingEnvironment processingEnv) {
        try {
            JavaFileObject file = processingEnv
                    .getFiler()
                    .createSourceFile(path);
            try (PrintWriter out = new PrintWriter(file.openWriter())) {
                out.print(tupleSchema);
            }
            return true;
        } catch (IOException exception) {
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    "Failed creating Tuple class %s\n%s".formatted(path, exception.getMessage())
            );
            return false;
        }
    }
}
