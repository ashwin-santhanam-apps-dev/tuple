package io.github.amusing_glitch.tuple.dynamic.factories;

import io.github.amusing_glitch.tuple.dynamic.templates.PebbleTemplateProcessor;
import io.github.amusing_glitch.tuple.javac.NamedTupleDefinition;
import io.github.amusing_glitch.tuple.javac.NamedTupleField;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


class StaticNamedTupleFactoryGeneratorTest {
    @Test
    void shouldGenerateNamedTupleStaticMethod() throws IOException {
        var generator = new StaticNamedTupleFactoryGenerator(
                new PebbleTemplateProcessor("templates")
        );

        var content = generator.generate(
                "Student",
                Set.of(
                        new NamedTupleField(0, "name", null),
                        new NamedTupleField(1, "age", null)
                )
        );

        var expected = """
        public static <T0, T1> Student<T0, T1> named(Student type, FieldSpec<T0> name, FieldSpec<T1> age) {
            return new Student<>(name.value(null), age.value(null));
        }
        """.trim();

        assertEquals(expected, content);
    }
}