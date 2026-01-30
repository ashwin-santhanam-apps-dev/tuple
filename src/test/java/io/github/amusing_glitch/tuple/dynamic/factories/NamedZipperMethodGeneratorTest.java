package io.github.amusing_glitch.tuple.dynamic.factories;

import io.github.amusing_glitch.tuple.dynamic.templates.PebbleTemplateProcessor;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;


class NamedZipperMethodGeneratorTest {
    @Test
    void shouldGenerateNamedZipperMethod() throws IOException {
        var generator = new NamedZipperMethodGenerator(
                new PebbleTemplateProcessor("templates")
        );

        var expected = """
        public static <T0, T1, T2> Stream<Student<T0, T1, T2>> namedZip(Student type, StreamFieldSpec<T0> stream0, StreamFieldSpec<T1> stream1, StreamFieldSpec<T2> stream2) {
            return DynamicTupleSeed.zip(List.of(
                (Stream<Object>) stream0.value(null), (Stream<Object>) stream1.value(null), (Stream<Object>) stream2.value(null)
            )).map(zipped -> new Student<>((T0) zipped.get(0), (T1) zipped.get(1), (T2) zipped.get(2)));
        }
        """.trim();

        var actual = generator.generate("Student", 3);

        assertEquals(expected, actual);
    }
}