package com.aparigraha.tuple.dynamic.entities;

import com.aparigraha.tuple.dynamic.templates.PebbleTemplateProcessor;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class TupleGeneratorTest {
    @Test
    void shouldGenerateStudentInfoClassWithGivenFields() throws IOException {
        TupleGenerator tupleGenerator = new TupleGenerator(new PebbleTemplateProcessor("templates"));

        var expected = """
        package com.aparigraha.tuples;
        

        public record StudentInfo<T0, T1, T2, T3> (T0 id, T1 name, T2 age, T3 isDayScholar) {
            @Override
            public boolean equals(Object obj) {
                if (obj instanceof StudentInfo<?, ?, ?, ?> that) {
                    return this.id == that.id && this.name == that.name && this.age == that.age && this.isDayScholar == that.isDayScholar;
                } else return false;
            }
        }
        """.trim();

        var tupleSchema = tupleGenerator.generate(
                new TupleGenerationParams(
                        "com.aparigraha.tuples",
                        "StudentInfo",
                        List.of("id", "name", "age", "isDayScholar")
                )
        );
        assertEquals(expected, tupleSchema.javaCode());
        assertEquals("StudentInfo", tupleSchema.className());
        assertEquals("com.aparigraha.tuples", tupleSchema.packageName());
    }


    @Test
    void shouldGenerateTupleClassWith4Fields() throws IOException {
        TupleGenerator tupleGenerator = new TupleGenerator(new PebbleTemplateProcessor("templates"));
        var expected = """
        package com.aparigraha.tuples;

        
        public record Tuple4<T0, T1, T2, T3> (T0 item0, T1 item1, T2 item2, T3 item3) {
            @Override
            public boolean equals(Object obj) {
                if (obj instanceof Tuple4<?, ?, ?, ?> that) {
                    return this.item0 == that.item0 && this.item1 == that.item1 && this.item2 == that.item2 && this.item3 == that.item3;
                } else return false;
            }
        }
        """.trim();

        var tupleSchema = tupleGenerator.generate(
                new TupleGenerationParams(
                        "com.aparigraha.tuples",
                        "Tuple4",
                        "item",
                        4
                )
        );
        assertEquals(expected, tupleSchema.javaCode());
        assertEquals("Tuple4", tupleSchema.className());
        assertEquals("com.aparigraha.tuples", tupleSchema.packageName());
    }
}