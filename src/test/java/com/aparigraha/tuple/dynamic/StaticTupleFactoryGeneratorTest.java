package com.aparigraha.tuple.dynamic;

import com.aparigraha.tuple.templates.PebbleTemplateProcessor;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class StaticTupleFactoryGeneratorTest {
    @Test
    void shouldGenerateStaticTupleFactoryMethods() throws IOException {
        StaticTupleFactoryGenerator generator = new StaticTupleFactoryGenerator(
                new PebbleTemplateProcessor("templates")
        );
        var expected = """
        public static <T0, T1> Tuple2<T0, T1> of(T0 item0, T1 item1) {
            return new Tuple2<>(item0, item1);
        }
        """.trim();
        assertEquals(expected, generator.generate(2));
    }
}