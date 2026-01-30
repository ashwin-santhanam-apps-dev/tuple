package io.github.amusing_glitch.tuple.dynamic.factories;

import io.github.amusing_glitch.tuple.dynamic.templates.PebbleTemplateProcessor;
import io.github.amusing_glitch.tuple.javac.NamedTupleDefinition;
import io.github.amusing_glitch.tuple.javac.NamedTupleField;
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
                new NamedZipperMethodGenerator(pebbleTemplateProcessor),
                new StaticNamedTupleFactoryGenerator(pebbleTemplateProcessor)
        );

        var expected = """
        package io.github.amusing_glitch.tuple.dynamic;

        import java.util.List;
        import java.util.stream.Stream;
        
        import io.github.amusing_glitch.tuple.dynamic.factories.FieldSpec;
        import io.github.amusing_glitch.tuple.dynamic.factories.StreamFieldSpec;
        



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
            public static <T> Stream<T> namedZip(T type, StreamFieldSpec<?>... streamFieldSpecs) {
                return DynamicTupleSeed.namedZip(type, streamFieldSpecs);
            }
        }
        """.trim();
        var schema = generator.generate(new DynamicTupleGenerationParam(
                "io.github.amusing_glitch.tuple.dynamic",
                "DynamicTuple",
                "of",
                "zip",
                "namedZip",
                "named",
                Set.of(),
                Set.of()
        ));
        assertEquals(expected, schema.javaCode());
        assertEquals("io.github.amusing_glitch.tuple.dynamic", schema.packageName());
        assertEquals("DynamicTuple", schema.className());
    }


    @Test
    void shouldGenerateDynamicTupleClassForTuple2AndTuple3() throws IOException {
        var pebbleTemplateProcessor = new PebbleTemplateProcessor("templates");
        var generator = new DynamicTupleGenerator(
                pebbleTemplateProcessor,
                new StaticTupleFactoryGenerator(pebbleTemplateProcessor),
                new ZipperMethodGenerator(pebbleTemplateProcessor),
                new NamedZipperMethodGenerator(pebbleTemplateProcessor),
                new StaticNamedTupleFactoryGenerator(pebbleTemplateProcessor)
        );
        var expected = """
        package io.github.amusing_glitch.tuple.dynamic;
        
        import java.util.List;
        import java.util.stream.Stream;
        
        import io.github.amusing_glitch.tuple.dynamic.factories.FieldSpec;
        import io.github.amusing_glitch.tuple.dynamic.factories.StreamFieldSpec;
        
        import com.example.Student;
        import com.example1.Staff;
        
        
        public class DynamicTuple {
            public static Object of(Object... args) {
                return DynamicTupleSeed.of(args);
            }
            public static Stream<Object> zip(Stream<?>... streams) {
                return DynamicTupleSeed.zip(streams);
            }
            public static <T> T namedZip(T type, FieldSpec<?>... fieldSpecs) {
                return DynamicTupleSeed.of(type, fieldSpecs);
            }
            public static <T> Stream<T> named(T type, StreamFieldSpec<?>... streamFieldSpecs) {
                return DynamicTupleSeed.namedZip(type, streamFieldSpecs);
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
        public static <T0, T1> Stream<Student<T0, T1>> namedZip(Student type, StreamFieldSpec<T0> stream0, StreamFieldSpec<T1> stream1) {
            return DynamicTupleSeed.zip(List.of(
                (Stream<Object>) stream0.value(null), (Stream<Object>) stream1.value(null)
            )).map(zipped -> new Student<>((T0) zipped.get(0), (T1) zipped.get(1)));
        }
        public static <T0, T1> Staff<T0, T1> named(Staff type, FieldSpec<T0> name, FieldSpec<T1> age) {
            return new Staff<>(name.value(null), age.value(null));
        }
        public static <T0, T1> Stream<Staff<T0, T1>> namedZip(Staff type, StreamFieldSpec<T0> stream0, StreamFieldSpec<T1> stream1) {
            return DynamicTupleSeed.zip(List.of(
                (Stream<Object>) stream0.value(null), (Stream<Object>) stream1.value(null)
            )).map(zipped -> new Staff<>((T0) zipped.get(0), (T1) zipped.get(1)));
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
                "io.github.amusing_glitch.tuple.dynamic",
                "DynamicTuple",
                "of",
                "zip",
                "named",
                "namedZip",
                tupleSizes,
                namedTupleDefinitions
        ));
        assertEquals(expected, schema.javaCode());
        assertEquals("io.github.amusing_glitch.tuple.dynamic", schema.packageName());
        assertEquals("DynamicTuple", schema.className());
    }
}
