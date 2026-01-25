package com.aparigraha.tuple.processors;

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
                "import static com.aparigraha.tuple.dynamic.DynamicTuple.named;",
                "import java.util.stream.Stream;",
                "public class Main {",
                "   public static void main(String[] args) {",
                "       named(Student.class, name -> \"Alice\", age -> 12);",
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
                                "    @Override\n" +
                                "    public boolean equals(Object obj) {\n" +
                                "        if (obj instanceof Student<?, ?> that) {\n" +
                                "            return this.name == that.name && this.age == that.age;\n" +
                                "        } else return false;\n" +
                                "    }\n" +
                                "}"
                ));

        assertThat(compilation)
                .generatedSourceFile("com.aparigraha.tuple.dynamic.DynamicTuple")
                .hasSourceEquivalentTo(JavaFileObjects.forSourceLines(
                        "com.aparigraha.tuple.dynamic.DynamicTuple",
                        "package com.aparigraha.tuple.dynamic;",
                        "",
                        "import java.util.List;",
                        "import java.util.stream.Stream;",
                        "",
                        "import com.aparigraha.tuple.dynamic.factories.FieldSpec;",
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
                        "    public static <T> T named(Class<T> tClass, FieldSpec<?>... fieldSpecs) {",
                        "        return DynamicTupleSeed.of(tClass, fieldSpecs);",
                        "    }",
                        "    public static <T0, T1> Student<T0, T1> named(Class<Student> tClass, FieldSpec<T0> name, FieldSpec<T1> age) {",
                        "        return new Student<>(name.value(null), age.value(null));",
                        "    }",
                        "    ",
                        "}"
                ));
    }
}