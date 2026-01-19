package com.aparigraha.tuple.dynamic.factories;

import com.aparigraha.tuple.dynamic.templates.PebbleTemplateProcessor;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class ZipperMethodGeneratorTest {
    @Test
    void shouldGenerateTheZipperDynamicMethod() throws IOException {
        var generator = new ZipperMethodGenerator(
                new PebbleTemplateProcessor("templates")
        );

        System.out.println(generator.generate(3));
    }
}