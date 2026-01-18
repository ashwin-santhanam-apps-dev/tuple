package com.aparigraha.tuple.extensions.stream;

import com.aparigraha.tuple.dynamic.DynamicTupleGenerator;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;


class DynamicTupleGeneratorTest {
    @Test
    void shouldGenerateDynamicTupleClass() throws IOException {
        var generator = new DynamicTupleGenerator();

        var expected = """
        package com.aparigraha.tuple.dynamic;
        
        
        public class DynamicTuple {
            public static Object of(Object... args) {
                return DynamicTupleSeed.of(args);
            }
        }
        """.trim();
        assertEquals(expected, generator.generate());
    }
}