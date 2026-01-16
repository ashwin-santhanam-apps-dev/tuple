package com.aparigraha.tuple;

import com.aparigraha.tuple.generator.TupleGenerationParams;
import com.aparigraha.tuple.generator.TupleGenerator;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TupleGeneratorTest {
    @Test
    void shouldGenerateStudentInfoClassWithGivenFields() throws IOException {
        TupleGenerator tupleGenerator = new TupleGenerator();

        var expected = """
        package com.aparigraha.tuples;
        
        import java.util.List;
        import java.util.stream.Stream;
        
        
        import com.aparigraha.tuple.extensions.stream.Zipper;
        
        
        public record StudentInfo<T0, T1, T2, T3> (T0 id, T1 name, T2 age, T3 isDayScholar) {
            @Override
            public boolean equals(Object obj) {
                if (obj instanceof StudentInfo<?, ?, ?, ?> that) {
                    return this.id == that.id && this.name == that.name && this.age == that.age && this.isDayScholar == that.isDayScholar;
                } else return false;
            }
        
        
            public static <T0, T1, T2, T3> Stream<StudentInfo<T0, T1, T2, T3>> zip(Stream<T0> stream0, Stream<T1> stream1, Stream<T2> stream2, Stream<T3> stream3) {
                List<Stream<Object>> streams = List.of((Stream<Object>) stream0, (Stream<Object>) stream1, (Stream<Object>) stream2, (Stream<Object>) stream3);
                return Zipper.zip(streams).map(zipped -> new StudentInfo<>((T0) zipped.get(0), (T1) zipped.get(1), (T2) zipped.get(2), (T3) zipped.get(3)));
            }
        }
        """.trim();

        assertEquals(expected, tupleGenerator.generate(
                new TupleGenerationParams(
                        "com.aparigraha.tuples",
                        "StudentInfo",
                        List.of("id", "name", "age", "isDayScholar")
                )
        ));
    }


    @Test
    void shouldGenerateTupleClassWith4Fields() throws IOException {
        TupleGenerator tupleGenerator = new TupleGenerator();
        var expected = """
        package com.aparigraha.tuples;
        
        import java.util.List;
        import java.util.stream.Stream;
        
        
        import com.aparigraha.tuple.extensions.stream.Zipper;
        
        
        public record Tuple4<T0, T1, T2, T3> (T0 item0, T1 item1, T2 item2, T3 item3) {
            @Override
            public boolean equals(Object obj) {
                if (obj instanceof Tuple4<?, ?, ?, ?> that) {
                    return this.item0 == that.item0 && this.item1 == that.item1 && this.item2 == that.item2 && this.item3 == that.item3;
                } else return false;
            }
        
        
            public static <T0, T1, T2, T3> Stream<Tuple4<T0, T1, T2, T3>> zip(Stream<T0> stream0, Stream<T1> stream1, Stream<T2> stream2, Stream<T3> stream3) {
                List<Stream<Object>> streams = List.of((Stream<Object>) stream0, (Stream<Object>) stream1, (Stream<Object>) stream2, (Stream<Object>) stream3);
                return Zipper.zip(streams).map(zipped -> new Tuple4<>((T0) zipped.get(0), (T1) zipped.get(1), (T2) zipped.get(2), (T3) zipped.get(3)));
            }
        }
        """.trim();

        assertEquals(expected, tupleGenerator.generate(
                new TupleGenerationParams(
                        "com.aparigraha.tuples",
                        "Tuple4",
                        "item",
                        4
                )
        ));
    }
}