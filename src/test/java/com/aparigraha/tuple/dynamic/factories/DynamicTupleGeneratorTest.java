package com.aparigraha.tuple.dynamic.factories;

import com.aparigraha.tuple.dynamic.templates.PebbleTemplateProcessor;
import com.aparigraha.tuple.javac.NamedTupleDefinition;
import com.aparigraha.tuple.javac.NamedTupleField;
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
                new ZipperMethodGenerator(pebbleTemplateProcessor),
                new StaticNamedTupleFactoryGenerator(pebbleTemplateProcessor)
        );

        var expected = """
        package com.aparigraha.tuple.dynamic;

        import java.util.List;
        import java.util.stream.Stream;
        
        import com.aparigraha.tuple.dynamic.factories.FieldSpec;
        



        public class DynamicTuple {
            public static Object of(Object... args) {
                return DynamicTupleSeed.of(args);
            }
            public static Stream<Object> zip(Stream<?>... streams) {
                return DynamicTupleSeed.zip(streams);
            }
            public static <T> T named(T type, FieldSpec<?>... fieldSpecs) {
                return DynamicTupleSeed.of(type, fieldSpecs);
            }
        }
        """.trim();
        var schema = generator.generate(new DynamicTupleGenerationParam(
                "com.aparigraha.tuple.dynamic",
                "DynamicTuple",
                "of",
                "zip",
                "named",
                Set.of(),
                Set.of()
        ));
        assertEquals(expected, schema.javaCode());
        assertEquals("com.aparigraha.tuple.dynamic", schema.packageName());
        assertEquals("DynamicTuple", schema.className());
    }


    @Test
    void shouldGenerateDynamicTupleClassForTuple2AndTuple3() throws IOException {
        var pebbleTemplateProcessor = new PebbleTemplateProcessor("templates");
        var generator = new DynamicTupleGenerator(
                pebbleTemplateProcessor,
                new StaticTupleFactoryGenerator(pebbleTemplateProcessor),
                new ZipperMethodGenerator(pebbleTemplateProcessor),
                new StaticNamedTupleFactoryGenerator(pebbleTemplateProcessor)
        );

        var expected = """
        package com.aparigraha.tuple.dynamic;
        
        import java.util.List;
        import java.util.stream.Stream;
        
        import com.aparigraha.tuple.dynamic.factories.FieldSpec;
        
        import com.example.Student;
        import com.example1.Staff;


        public class DynamicTuple {
            public static Object of(Object... args) {
                return DynamicTupleSeed.of(args);
            }
            public static Stream<Object> zip(Stream<?>... streams) {
                return DynamicTupleSeed.zip(streams);
            }
            public static <T> T named(T type, FieldSpec<?>... fieldSpecs) {
                return DynamicTupleSeed.of(type, fieldSpecs);
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
        }
        public static <T0, T1> Student<T0, T1> named(Student type, FieldSpec<T0> name, FieldSpec<T1> age) {
            return new Student<>(name.value(null), age.value(null));
        }
        public static <T0, T1> Staff<T0, T1> named(Staff type, FieldSpec<T0> name, FieldSpec<T1> age) {
            return new Staff<>(name.value(null), age.value(null));
        }}
        """.trim();
        var tupleSizes = new LinkedHashSet<Integer>();
        tupleSizes.add(2);
        tupleSizes.add(3);

        var namedTupleDefinitions = new LinkedHashSet<NamedTupleDefinition>();
        namedTupleDefinitions.add(new NamedTupleDefinition(
                "com.example",
                "Student",
                "",
                Set.of(
                        new NamedTupleField(0, "name", null),
                        new NamedTupleField(1, "age", null)
                )
        ));
        namedTupleDefinitions.add(new NamedTupleDefinition(
                "com.example1",
                "Staff",
                "",
                Set.of(
                        new NamedTupleField(0, "name", null),
                        new NamedTupleField(1, "age", null)
                )
        ));

        var schema = generator.generate(new DynamicTupleGenerationParam(
                "com.aparigraha.tuple.dynamic",
                "DynamicTuple",
                "of",
                "zip",
                "named",
                tupleSizes,
                namedTupleDefinitions
        ));
        assertEquals(expected, schema.javaCode());
        assertEquals("com.aparigraha.tuple.dynamic", schema.packageName());
        assertEquals("DynamicTuple", schema.className());
    }
}
