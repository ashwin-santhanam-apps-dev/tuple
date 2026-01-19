package com.aparigraha.tuple.dynamic;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;


public class JavaFileWriter {
    public void write(ProcessingEnvironment processingEnvironment, GeneratedClassSchema schema) throws IOException {
        JavaFileObject javaFileObject = processingEnvironment
                .getFiler()
                .createSourceFile(schema.completeClassName());

        try (Writer writer = javaFileObject.openWriter()) {
            writer.write(schema.javaCode());
        }
    }
}
