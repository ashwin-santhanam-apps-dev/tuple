package io.github.amusing_glitch.tuple.processors;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;


class TupleSpecProcessorNamedTupleIntegrationTest {
    @Test
    void shouldGenerateClassesForNamedTupleSpec() {
        JavaFileObject dependant = JavaFileObjects.forSourceLines(
                "com.example.Main",
                "package com.example;",
                "import static io.github.amusing_glitch.tuple.dynamic.DynamicTuple.*;",
                "import java.util.stream.Stream;",
                "public class Main {",
                "   public static void main(String[] args) {",
                "       named(Student.type, name -> \"Alice\", age -> 12);",
                "       namedZip(Student.type, name -> Stream.of(\"A\"), age -> Stream.of(12));",
                "   }",
                "}"
        );

        Compilation compilation = javac()
                .withProcessors(new TupleSpecProcessor())
                .compile(dependant);

        assertThat(compilation).succeeded();
        assertThat(compilation)
                .generatedSourceFile("com.example.Student")
                .hasSourceEquivalentTo(JavaFileObjects.forSourceLines(
                        "com.example.Student",
                        "package com.example;\n" +
                                "\n" +
                                "\n" +
                                "public record Student<T0, T1> (T0 name, T1 age) {\n" +
                                "    public static final Student type = null;" +
                                "    @Override\n" +
                                "    public boolean equals(Object obj) {\n" +
                                "        if (obj instanceof Student<?, ?> that) {\n" +
                                "            return this.name == that.name && this.age == that.age;\n" +
                                "        } else return false;\n" +
                                "    }\n" +
                                "}"
                ));

        assertThat(compilation)
                .generatedSourceFile("io.github.amusing_glitch.tuple.dynamic.DynamicTuple")
                .hasSourceEquivalentTo(JavaFileObjects.forSourceLines(
                        "io.github.amusing_glitch.tuple.dynamic.DynamicTuple",
                        "package io.github.amusing_glitch.tuple.dynamic;",
                        "",
                        "import java.util.List;",
                        "import java.util.stream.Stream;",
                        "",
                        "import io.github.amusing_glitch.tuple.dynamic.factories.FieldSpec;",
                        "import io.github.amusing_glitch.tuple.dynamic.factories.StreamFieldSpec;",
                        "",
                        "import com.example.Student;",
                        "",
                        "public class DynamicTuple {",
                        "    public static Object of(Object... args) {",
                        "        return DynamicTupleSeed.of(args);",
                        "    }",
                        "    public static Stream<Object> zip(Stream<?>... streams) {",
                        "        return DynamicTupleSeed.zip(streams);",
                        "    }",
                        "    public static <T> T named(T type, FieldSpec<?>... fieldSpecs) {",
                        "        return DynamicTupleSeed.of(type, fieldSpecs);",
                        "    }",
                        "    public static <T> Stream<T> namedZip(T type, StreamFieldSpec<?>... streamFieldSpecs) {",
                        "       return DynamicTupleSeed.namedZip(type, streamFieldSpecs);",
                        "    }",
                        "    public static <T0, T1> Student<T0, T1> named(Student type, FieldSpec<T0> name, FieldSpec<T1> age) {",
                        "        return new Student<>(name.value(null), age.value(null));",
                        "    }",
                        "    ",
                        "    public static <T0, T1> Stream<Student<T0, T1>> namedZip(Student type, StreamFieldSpec<T0> stream0, StreamFieldSpec<T1> stream1) {",
                        "            return DynamicTupleSeed.zip(List.of(",
                        "                   (Stream<Object>) stream0.value(null), (Stream<Object>) stream1.value(null)",
                        "            )).map(zipped -> new Student<>((T0) zipped.get(0), (T1) zipped.get(1)));",
                        "    }",
                        "}"
                ));
    }
}