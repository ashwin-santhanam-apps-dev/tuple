package com.aparigraha.tuple.dynamic.factories;

import com.aparigraha.tuple.dynamic.templates.PebbleTemplateProcessor;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


class DynamicTupleGeneratorTest {
    @Test
    void shouldGenerateDynamicTupleClass() throws IOException {
        var pebbleTemplateProcessor = new PebbleTemplateProcessor("templates");
        var generator = new DynamicTupleGenerator(
                pebbleTemplateProcessor,
                new StaticTupleFactoryGenerator(pebbleTemplateProcessor),
                new ZipperMethodGenerator(pebbleTemplateProcessor)
        );

        var expected = """
        package com.aparigraha.tuple.dynamic;

        import java.util.List;
        import java.util.stream.Stream;
        

        public class DynamicTuple {
            public static Object of(Object... args) {
                return DynamicTupleSeed.of(args);
            }
        }
        """.trim();
        assertEquals(expected, generator.generate(Set.of()));
    }


    @Test
    void shouldGenerateDynamicTupleClassForTuple2AndTuple3() throws IOException {
        var pebbleTemplateProcessor = new PebbleTemplateProcessor("templates");
        var generator = new DynamicTupleGenerator(
                pebbleTemplateProcessor,
                new StaticTupleFactoryGenerator(pebbleTemplateProcessor),
                new ZipperMethodGenerator(pebbleTemplateProcessor)
        );

        var expected = """
        package com.aparigraha.tuple.dynamic;
        
        import java.util.List;
        import java.util.stream.Stream;


        public class DynamicTuple {
            public static Object of(Object... args) {
                return DynamicTupleSeed.of(args);
            }
        public static <T0, T1> Tuple2<T0, T1> of(T0 item0, T1 item1) {
            return new Tuple2<>(item0, item1);
        }
        public static <T0, T1> Stream<Tuple2<T0, T1>> zip(Stream<T0> stream0, Stream<T1> stream1) {
            List<Stream<Object>> streams = List.of((Stream<Object>) stream0, (Stream<Object>) stream1);
            return DynamicTupleSeed.zip(streams).map(zipped -> new Tuple2<>((T0) zipped.get(0), (T1) zipped.get(1)));
        }
        public static <T0, T1, T2> Tuple3<T0, T1, T2> of(T0 item0, T1 item1, T2 item2) {
            return new Tuple3<>(item0, item1, item2);
        }
        public static <T0, T1, T2> Stream<Tuple3<T0, T1, T2>> zip(Stream<T0> stream0, Stream<T1> stream1, Stream<T2> stream2) {
            List<Stream<Object>> streams = List.of((Stream<Object>) stream0, (Stream<Object>) stream1, (Stream<Object>) stream2);
            return DynamicTupleSeed.zip(streams).map(zipped -> new Tuple3<>((T0) zipped.get(0), (T1) zipped.get(1), (T2) zipped.get(2)));
        }}
        """.trim();
        var tupleSizes = new LinkedHashSet<Integer>();
        tupleSizes.add(2);
        tupleSizes.add(3);
        assertEquals(expected, generator.generate(tupleSizes));
    }
}
