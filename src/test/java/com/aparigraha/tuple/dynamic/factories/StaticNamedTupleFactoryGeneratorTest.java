package com.aparigraha.tuple.dynamic.factories;

import com.aparigraha.tuple.dynamic.templates.PebbleTemplateProcessor;
import com.aparigraha.tuple.javac.NamedTupleDefinition;
import com.aparigraha.tuple.javac.NamedTupleField;
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

        var content = generator.generate(new NamedTupleDefinition(
                "",
                "Student",
                "",
                Set.of(
                        new NamedTupleField(0, "name", null),
                        new NamedTupleField(1, "age", null)
                )
        ));

        var expected = """
        public static <T0, T1> Student<T0, T1> named(Student type, FieldSpec<T0> name, FieldSpec<T1> age) {
            return new Student<>(name.value(null), age.value(null));
        }
        """.trim();

        assertEquals(expected, content);
    }
}