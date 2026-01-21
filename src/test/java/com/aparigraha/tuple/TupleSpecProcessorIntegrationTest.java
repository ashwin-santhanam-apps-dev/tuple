package com.aparigraha.tuple;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;
import static org.junit.jupiter.api.Assertions.assertTrue;


class TupleSpecProcessorIntegrationTest {
    @Test
    void shouldCreateTheTupleClassesAndFactoryMethodForDefinitionInClass() {
        JavaFileObject dependant = JavaFileObjects.forSourceLines(
                "com.example.Main",
                "package com.example;",
                "import com.aparigraha.tuple.dynamic.DynamicTuple;",
                "import java.util.stream.Stream;",
                "public class Main {",
                "   public static void main(String[] args) {",
                "       DynamicTuple.of(\"Alice\", 21);",
                "       DynamicTuple.of(\"Alice\", 21);",
                "       DynamicTuple.zip(",
                "           Stream.of(\"Alice\"),",
                "           Stream.of(1),",
                "           Stream.of(false)",
                "       );",
                "       DynamicTuple.zip(",
                "           Stream.of(\"Alice\"),",
                "           Stream.of(1),",
                "           Stream.of(false)",
                "       );",
                "   }",
                "}"
        );

        Compilation compilation = javac()
                .withProcessors(new TupleSpecProcessor())
                .compile(dependant);

        assertThat(compilation).succeeded();
        assertThat(compilation)
                .generatedSourceFile("com.aparigraha.tuple.dynamic.Tuple2")
                .hasSourceEquivalentTo(JavaFileObjects.forSourceLines(
                        "com.aparigraha.tuple.dynamic.Tuple2",
                        "package com.aparigraha.tuple.dynamic;",
                        "public record Tuple2<T0, T1> (T0 item0, T1 item1) {",
                        "   @Override",
                        "   public boolean equals(Object obj) {",
                        "       if (obj instanceof Tuple2<?, ?> that) {",
                        "            return this.item0 == that.item0 && this.item1 == that.item1;",
                        "        } else return false;",
                        "    }",
                        "}"
                ));

        assertThat(compilation)
                .generatedSourceFile("com.aparigraha.tuple.dynamic.Tuple3")
                .hasSourceEquivalentTo(JavaFileObjects.forSourceLines(
                        "com.aparigraha.tuple.dynamic.Tuple3",
                        "package com.aparigraha.tuple.dynamic;",
                                "    ",
                                "    ",
                                "    public record Tuple3<T0, T1, T2> (T0 item0, T1 item1, T2 item2) {",
                                "        @Override",
                                "        public boolean equals(Object obj) {",
                                "            if (obj instanceof Tuple3<?, ?, ?> that) {",
                                "                return this.item0 == that.item0 && this.item1 == that.item1 && this.item2 == that.item2;",
                                "            } else return false;",
                                "        }",
                                "    }"
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
                                "",
                                "public class DynamicTuple {",
                                "    public static Object of(Object... args) {",
                                "        return DynamicTupleSeed.of(args);",
                                "    }",
                                "    public static Stream<Object> zip(Stream<?>... streams) {",
                                "        return DynamicTupleSeed.zip(streams);",
                                "    }",
                                "public static <T0, T1> Tuple2<T0, T1> of(T0 item0, T1 item1) {",
                                "    return new Tuple2<>(item0, item1);",
                                "}",
                                "public static <T0, T1> Stream<Tuple2<T0, T1>> zip(Stream<T0> stream0, Stream<T1> stream1) {",
                                "    List<Stream<Object>> streams = List.of((Stream<Object>) stream0, (Stream<Object>) stream1);",
                                "    return DynamicTupleSeed.zip(streams).map(zipped -> new Tuple2<>((T0) zipped.get(0), (T1) zipped.get(1)));",
                                "}",
                                "public static <T0, T1, T2> Tuple3<T0, T1, T2> of(T0 item0, T1 item1, T2 item2) {",
                                "    return new Tuple3<>(item0, item1, item2);",
                                "}",
                                "public static <T0, T1, T2> Stream<Tuple3<T0, T1, T2>> zip(Stream<T0> stream0, Stream<T1> stream1, Stream<T2> stream2) {",
                                "    List<Stream<Object>> streams = List.of((Stream<Object>) stream0, (Stream<Object>) stream1, (Stream<Object>) stream2);",
                                "    return DynamicTupleSeed.zip(streams).map(zipped -> new Tuple3<>((T0) zipped.get(0), (T1) zipped.get(1), (T2) zipped.get(2)));",
                                "}}"
                ));
    }


    @Test
    void shouldCreateTheTupleClassesAndFactoryMethodForDefinitionInInterface() {
        JavaFileObject dependant = JavaFileObjects.forSourceLines(
                "com.example.Main",
                "package com.example;",
                "import com.aparigraha.tuple.dynamic.DynamicTuple;",
                "import java.util.stream.Stream;",
                "public interface Main {",
                "   default void main(String[] args) {",
                "       DynamicTuple.of(\"Alice\", 21);",
                "       DynamicTuple.zip(",
                "           Stream.of(\"Alice\"),",
                "           Stream.of(1),",
                "           Stream.of(false)",
                "       );",
                "       DynamicTuple.of(\"Alice\", 21);",
                "       DynamicTuple.zip(",
                "           Stream.of(\"Alice\"),",
                "           Stream.of(1),",
                "           Stream.of(false)",
                "       );",

                "   }",
                "}"
        );

        Compilation compilation = javac()
                .withProcessors(new TupleSpecProcessor())
                .compile(dependant);

        assertThat(compilation).succeeded();
        assertThat(compilation)
                .generatedSourceFile("com.aparigraha.tuple.dynamic.Tuple2")
                .hasSourceEquivalentTo(JavaFileObjects.forSourceLines(
                        "com.aparigraha.tuple.dynamic.Tuple2",
                        "package com.aparigraha.tuple.dynamic;",
                        "public record Tuple2<T0, T1> (T0 item0, T1 item1) {",
                        "   @Override",
                        "   public boolean equals(Object obj) {",
                        "       if (obj instanceof Tuple2<?, ?> that) {",
                        "            return this.item0 == that.item0 && this.item1 == that.item1;",
                        "        } else return false;",
                        "    }",
                        "}"
                ));

        assertThat(compilation)
                .generatedSourceFile("com.aparigraha.tuple.dynamic.Tuple3")
                .hasSourceEquivalentTo(JavaFileObjects.forSourceLines(
                        "com.aparigraha.tuple.dynamic.Tuple3",
                        "package com.aparigraha.tuple.dynamic;",
                        "    ",
                        "    ",
                        "    public record Tuple3<T0, T1, T2> (T0 item0, T1 item1, T2 item2) {",
                        "        @Override",
                        "        public boolean equals(Object obj) {",
                        "            if (obj instanceof Tuple3<?, ?, ?> that) {",
                        "                return this.item0 == that.item0 && this.item1 == that.item1 && this.item2 == that.item2;",
                        "            } else return false;",
                        "        }",
                        "    }"
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
                        "",
                        "public class DynamicTuple {",
                        "    public static Object of(Object... args) {",
                        "        return DynamicTupleSeed.of(args);",
                        "    }",
                        "    public static Stream<Object> zip(Stream<?>... streams) {",
                        "        return DynamicTupleSeed.zip(streams);",
                        "    }",
                        "public static <T0, T1> Tuple2<T0, T1> of(T0 item0, T1 item1) {",
                        "    return new Tuple2<>(item0, item1);",
                        "}",
                        "public static <T0, T1> Stream<Tuple2<T0, T1>> zip(Stream<T0> stream0, Stream<T1> stream1) {",
                        "    List<Stream<Object>> streams = List.of((Stream<Object>) stream0, (Stream<Object>) stream1);",
                        "    return DynamicTupleSeed.zip(streams).map(zipped -> new Tuple2<>((T0) zipped.get(0), (T1) zipped.get(1)));",
                        "}",
                        "public static <T0, T1, T2> Tuple3<T0, T1, T2> of(T0 item0, T1 item1, T2 item2) {",
                        "    return new Tuple3<>(item0, item1, item2);",
                        "}",
                        "public static <T0, T1, T2> Stream<Tuple3<T0, T1, T2>> zip(Stream<T0> stream0, Stream<T1> stream1, Stream<T2> stream2) {",
                        "    List<Stream<Object>> streams = List.of((Stream<Object>) stream0, (Stream<Object>) stream1, (Stream<Object>) stream2);",
                        "    return DynamicTupleSeed.zip(streams).map(zipped -> new Tuple3<>((T0) zipped.get(0), (T1) zipped.get(1), (T2) zipped.get(2)));",
                        "}}"
                ));
    }


    @Test
    void shouldCreateTheTupleClassesAndFactoryMethodForDefinitionInRecord() {
        JavaFileObject dependant = JavaFileObjects.forSourceLines(
                "com.example.Main",
                "package com.example;",
                "import com.aparigraha.tuple.dynamic.DynamicTuple;",
                "import java.util.stream.Stream;",
                "public record Main() {",
                "   public static void main(String[] args) {",
                "       DynamicTuple.of(\"Alice\", 21);",
                "       DynamicTuple.zip(",
                "           Stream.of(\"Alice\"),",
                "           Stream.of(1),",
                "           Stream.of(false)",
                "       );",
                "       DynamicTuple.of(\"Alice\", 21);",
                "       DynamicTuple.zip(",
                "           Stream.of(\"Alice\"),",
                "           Stream.of(1),",
                "           Stream.of(false)",
                "       );",

                "   }",
                "}"
        );

        Compilation compilation = javac()
                .withProcessors(new TupleSpecProcessor())
                .compile(dependant);

        assertThat(compilation).succeeded();
        assertThat(compilation)
                .generatedSourceFile("com.aparigraha.tuple.dynamic.Tuple2")
                .hasSourceEquivalentTo(JavaFileObjects.forSourceLines(
                        "com.aparigraha.tuple.dynamic.Tuple2",
                        "package com.aparigraha.tuple.dynamic;",
                        "public record Tuple2<T0, T1> (T0 item0, T1 item1) {",
                        "   @Override",
                        "   public boolean equals(Object obj) {",
                        "       if (obj instanceof Tuple2<?, ?> that) {",
                        "            return this.item0 == that.item0 && this.item1 == that.item1;",
                        "        } else return false;",
                        "    }",
                        "}"
                ));

        assertThat(compilation)
                .generatedSourceFile("com.aparigraha.tuple.dynamic.Tuple3")
                .hasSourceEquivalentTo(JavaFileObjects.forSourceLines(
                        "com.aparigraha.tuple.dynamic.Tuple3",
                        "package com.aparigraha.tuple.dynamic;",
                        "    ",
                        "    ",
                        "    public record Tuple3<T0, T1, T2> (T0 item0, T1 item1, T2 item2) {",
                        "        @Override",
                        "        public boolean equals(Object obj) {",
                        "            if (obj instanceof Tuple3<?, ?, ?> that) {",
                        "                return this.item0 == that.item0 && this.item1 == that.item1 && this.item2 == that.item2;",
                        "            } else return false;",
                        "        }",
                        "    }"
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
                        "",
                        "public class DynamicTuple {",
                        "    public static Object of(Object... args) {",
                        "        return DynamicTupleSeed.of(args);",
                        "    }",
                        "    public static Stream<Object> zip(Stream<?>... streams) {",
                        "        return DynamicTupleSeed.zip(streams);",
                        "    }",
                        "public static <T0, T1> Tuple2<T0, T1> of(T0 item0, T1 item1) {",
                        "    return new Tuple2<>(item0, item1);",
                        "}",
                        "public static <T0, T1> Stream<Tuple2<T0, T1>> zip(Stream<T0> stream0, Stream<T1> stream1) {",
                        "    List<Stream<Object>> streams = List.of((Stream<Object>) stream0, (Stream<Object>) stream1);",
                        "    return DynamicTupleSeed.zip(streams).map(zipped -> new Tuple2<>((T0) zipped.get(0), (T1) zipped.get(1)));",
                        "}",
                        "public static <T0, T1, T2> Tuple3<T0, T1, T2> of(T0 item0, T1 item1, T2 item2) {",
                        "    return new Tuple3<>(item0, item1, item2);",
                        "}",
                        "public static <T0, T1, T2> Stream<Tuple3<T0, T1, T2>> zip(Stream<T0> stream0, Stream<T1> stream1, Stream<T2> stream2) {",
                        "    List<Stream<Object>> streams = List.of((Stream<Object>) stream0, (Stream<Object>) stream1, (Stream<Object>) stream2);",
                        "    return DynamicTupleSeed.zip(streams).map(zipped -> new Tuple3<>((T0) zipped.get(0), (T1) zipped.get(1), (T2) zipped.get(2)));",
                        "}}"
                ));
    }


    @Test
    void shouldCreateTheTupleClassesAndFactoryMethodWhenImportedStatically() {
        JavaFileObject dependant = JavaFileObjects.forSourceLines(
                "com.example.Main",
                "package com.example;",
                "import static com.aparigraha.tuple.dynamic.DynamicTuple.of;",
                "import static com.aparigraha.tuple.dynamic.DynamicTuple.zip;",
                "import java.util.stream.Stream;",
                "public class Main {",
                "   public static void main(String[] args) {",
                "       of(\"Alice\", 21);",
                "       of(\"Alice\", 21);",
                "       zip(",
                "           Stream.of(\"Alice\"),",
                "           Stream.of(1),",
                "           Stream.of(false)",
                "       );",
                "       zip(",
                "           Stream.of(\"Alice\"),",
                "           Stream.of(1),",
                "           Stream.of(false)",
                "       );",
                "   }",
                "}"
        );

        Compilation compilation = javac()
                .withProcessors(new TupleSpecProcessor())
                .compile(dependant);

        assertThat(compilation).succeeded();
        assertThat(compilation)
                .generatedSourceFile("com.aparigraha.tuple.dynamic.Tuple2")
                .hasSourceEquivalentTo(JavaFileObjects.forSourceLines(
                        "com.aparigraha.tuple.dynamic.Tuple2",
                        "package com.aparigraha.tuple.dynamic;",
                        "public record Tuple2<T0, T1> (T0 item0, T1 item1) {",
                        "   @Override",
                        "   public boolean equals(Object obj) {",
                        "       if (obj instanceof Tuple2<?, ?> that) {",
                        "            return this.item0 == that.item0 && this.item1 == that.item1;",
                        "        } else return false;",
                        "    }",
                        "}"
                ));

        assertThat(compilation)
                .generatedSourceFile("com.aparigraha.tuple.dynamic.Tuple3")
                .hasSourceEquivalentTo(JavaFileObjects.forSourceLines(
                        "com.aparigraha.tuple.dynamic.Tuple3",
                        "package com.aparigraha.tuple.dynamic;",
                        "    ",
                        "    ",
                        "    public record Tuple3<T0, T1, T2> (T0 item0, T1 item1, T2 item2) {",
                        "        @Override",
                        "        public boolean equals(Object obj) {",
                        "            if (obj instanceof Tuple3<?, ?, ?> that) {",
                        "                return this.item0 == that.item0 && this.item1 == that.item1 && this.item2 == that.item2;",
                        "            } else return false;",
                        "        }",
                        "    }"
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
                        "",
                        "public class DynamicTuple {",
                        "    public static Object of(Object... args) {",
                        "        return DynamicTupleSeed.of(args);",
                        "    }",
                        "    public static Stream<Object> zip(Stream<?>... streams) {",
                        "        return DynamicTupleSeed.zip(streams);",
                        "    }",
                        "public static <T0, T1> Tuple2<T0, T1> of(T0 item0, T1 item1) {",
                        "    return new Tuple2<>(item0, item1);",
                        "}",
                        "public static <T0, T1> Stream<Tuple2<T0, T1>> zip(Stream<T0> stream0, Stream<T1> stream1) {",
                        "    List<Stream<Object>> streams = List.of((Stream<Object>) stream0, (Stream<Object>) stream1);",
                        "    return DynamicTupleSeed.zip(streams).map(zipped -> new Tuple2<>((T0) zipped.get(0), (T1) zipped.get(1)));",
                        "}",
                        "public static <T0, T1, T2> Tuple3<T0, T1, T2> of(T0 item0, T1 item1, T2 item2) {",
                        "    return new Tuple3<>(item0, item1, item2);",
                        "}",
                        "public static <T0, T1, T2> Stream<Tuple3<T0, T1, T2>> zip(Stream<T0> stream0, Stream<T1> stream1, Stream<T2> stream2) {",
                        "    List<Stream<Object>> streams = List.of((Stream<Object>) stream0, (Stream<Object>) stream1, (Stream<Object>) stream2);",
                        "    return DynamicTupleSeed.zip(streams).map(zipped -> new Tuple3<>((T0) zipped.get(0), (T1) zipped.get(1), (T2) zipped.get(2)));",
                        "}}"
                ));
    }


    @Test
    void shouldCreateTheTupleClassesAndFactoryMethodWhenImportedStaticallyWithWildCard() {
        JavaFileObject dependant = JavaFileObjects.forSourceLines(
                "com.example.Main",
                "package com.example;",
                "import static com.aparigraha.tuple.dynamic.DynamicTuple.*;",
                "import java.util.stream.Stream;",
                "public class Main {",
                "   public static void main(String[] args) {",
                "       of(\"Alice\", 21);",
                "       of(\"Alice\", 21);",
                "       zip(",
                "           Stream.of(\"Alice\"),",
                "           Stream.of(1),",
                "           Stream.of(false)",
                "       );",
                "       zip(",
                "           Stream.of(\"Alice\"),",
                "           Stream.of(1),",
                "           Stream.of(false)",
                "       );",
                "   }",
                "}"
        );

        Compilation compilation = javac()
                .withProcessors(new TupleSpecProcessor())
                .compile(dependant);

        assertThat(compilation).succeeded();
        assertThat(compilation)
                .generatedSourceFile("com.aparigraha.tuple.dynamic.Tuple2")
                .hasSourceEquivalentTo(JavaFileObjects.forSourceLines(
                        "com.aparigraha.tuple.dynamic.Tuple2",
                        "package com.aparigraha.tuple.dynamic;",
                        "public record Tuple2<T0, T1> (T0 item0, T1 item1) {",
                        "   @Override",
                        "   public boolean equals(Object obj) {",
                        "       if (obj instanceof Tuple2<?, ?> that) {",
                        "            return this.item0 == that.item0 && this.item1 == that.item1;",
                        "        } else return false;",
                        "    }",
                        "}"
                ));

        assertThat(compilation)
                .generatedSourceFile("com.aparigraha.tuple.dynamic.Tuple3")
                .hasSourceEquivalentTo(JavaFileObjects.forSourceLines(
                        "com.aparigraha.tuple.dynamic.Tuple3",
                        "package com.aparigraha.tuple.dynamic;",
                        "    ",
                        "    ",
                        "    public record Tuple3<T0, T1, T2> (T0 item0, T1 item1, T2 item2) {",
                        "        @Override",
                        "        public boolean equals(Object obj) {",
                        "            if (obj instanceof Tuple3<?, ?, ?> that) {",
                        "                return this.item0 == that.item0 && this.item1 == that.item1 && this.item2 == that.item2;",
                        "            } else return false;",
                        "        }",
                        "    }"
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
                        "",
                        "public class DynamicTuple {",
                        "    public static Object of(Object... args) {",
                        "        return DynamicTupleSeed.of(args);",
                        "    }",
                        "    public static Stream<Object> zip(Stream<?>... streams) {",
                        "        return DynamicTupleSeed.zip(streams);",
                        "    }",
                        "public static <T0, T1> Tuple2<T0, T1> of(T0 item0, T1 item1) {",
                        "    return new Tuple2<>(item0, item1);",
                        "}",
                        "public static <T0, T1> Stream<Tuple2<T0, T1>> zip(Stream<T0> stream0, Stream<T1> stream1) {",
                        "    List<Stream<Object>> streams = List.of((Stream<Object>) stream0, (Stream<Object>) stream1);",
                        "    return DynamicTupleSeed.zip(streams).map(zipped -> new Tuple2<>((T0) zipped.get(0), (T1) zipped.get(1)));",
                        "}",
                        "public static <T0, T1, T2> Tuple3<T0, T1, T2> of(T0 item0, T1 item1, T2 item2) {",
                        "    return new Tuple3<>(item0, item1, item2);",
                        "}",
                        "public static <T0, T1, T2> Stream<Tuple3<T0, T1, T2>> zip(Stream<T0> stream0, Stream<T1> stream1, Stream<T2> stream2) {",
                        "    List<Stream<Object>> streams = List.of((Stream<Object>) stream0, (Stream<Object>) stream1, (Stream<Object>) stream2);",
                        "    return DynamicTupleSeed.zip(streams).map(zipped -> new Tuple3<>((T0) zipped.get(0), (T1) zipped.get(1), (T2) zipped.get(2)));",
                        "}}"
                ));
    }


    @Test
    void shouldCreateTheTupleClassesAndFactoryMethodWhenUsedDirectly() {
        JavaFileObject dependant = JavaFileObjects.forSourceLines(
                "com.example.Main",
                "package com.example;",
                "import java.util.stream.Stream;",
                "public class Main {",
                "   public static void main(String[] args) {",
                "       com.aparigraha.tuple.dynamic.DynamicTuple.of(\"Alice\", 21);",
                "       com.aparigraha.tuple.dynamic.DynamicTuple.of(\"Alice\", 21);",
                "       com.aparigraha.tuple.dynamic.DynamicTuple.zip(",
                "           Stream.of(\"Alice\"),",
                "           Stream.of(1),",
                "           Stream.of(false)",
                "       );",
                "       com.aparigraha.tuple.dynamic.DynamicTuple.zip(",
                "           Stream.of(\"Alice\"),",
                "           Stream.of(1),",
                "           Stream.of(false)",
                "       );",
                "   }",
                "}"
        );

        Compilation compilation = javac()
                .withProcessors(new TupleSpecProcessor())
                .compile(dependant);

        assertThat(compilation).succeeded();
        assertThat(compilation)
                .generatedSourceFile("com.aparigraha.tuple.dynamic.Tuple2")
                .hasSourceEquivalentTo(JavaFileObjects.forSourceLines(
                        "com.aparigraha.tuple.dynamic.Tuple2",
                        "package com.aparigraha.tuple.dynamic;",
                        "public record Tuple2<T0, T1> (T0 item0, T1 item1) {",
                        "   @Override",
                        "   public boolean equals(Object obj) {",
                        "       if (obj instanceof Tuple2<?, ?> that) {",
                        "            return this.item0 == that.item0 && this.item1 == that.item1;",
                        "        } else return false;",
                        "    }",
                        "}"
                ));

        assertThat(compilation)
                .generatedSourceFile("com.aparigraha.tuple.dynamic.Tuple3")
                .hasSourceEquivalentTo(JavaFileObjects.forSourceLines(
                        "com.aparigraha.tuple.dynamic.Tuple3",
                        "package com.aparigraha.tuple.dynamic;",
                        "    ",
                        "    ",
                        "    public record Tuple3<T0, T1, T2> (T0 item0, T1 item1, T2 item2) {",
                        "        @Override",
                        "        public boolean equals(Object obj) {",
                        "            if (obj instanceof Tuple3<?, ?, ?> that) {",
                        "                return this.item0 == that.item0 && this.item1 == that.item1 && this.item2 == that.item2;",
                        "            } else return false;",
                        "        }",
                        "    }"
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
                        "",
                        "public class DynamicTuple {",
                        "    public static Object of(Object... args) {",
                        "        return DynamicTupleSeed.of(args);",
                        "    }",
                        "    public static Stream<Object> zip(Stream<?>... streams) {",
                        "        return DynamicTupleSeed.zip(streams);",
                        "    }",
                        "public static <T0, T1> Tuple2<T0, T1> of(T0 item0, T1 item1) {",
                        "    return new Tuple2<>(item0, item1);",
                        "}",
                        "public static <T0, T1> Stream<Tuple2<T0, T1>> zip(Stream<T0> stream0, Stream<T1> stream1) {",
                        "    List<Stream<Object>> streams = List.of((Stream<Object>) stream0, (Stream<Object>) stream1);",
                        "    return DynamicTupleSeed.zip(streams).map(zipped -> new Tuple2<>((T0) zipped.get(0), (T1) zipped.get(1)));",
                        "}",
                        "public static <T0, T1, T2> Tuple3<T0, T1, T2> of(T0 item0, T1 item1, T2 item2) {",
                        "    return new Tuple3<>(item0, item1, item2);",
                        "}",
                        "public static <T0, T1, T2> Stream<Tuple3<T0, T1, T2>> zip(Stream<T0> stream0, Stream<T1> stream1, Stream<T2> stream2) {",
                        "    List<Stream<Object>> streams = List.of((Stream<Object>) stream0, (Stream<Object>) stream1, (Stream<Object>) stream2);",
                        "    return DynamicTupleSeed.zip(streams).map(zipped -> new Tuple3<>((T0) zipped.get(0), (T1) zipped.get(1), (T2) zipped.get(2)));",
                        "}}"
                ));
    }



    @Test
    void shouldIgnoreTheTupleDefinitionsForOtherSimilarMethods() {
        JavaFileObject other = JavaFileObjects.forSourceLines(
                "com.example.DynamicTuple",
                "package com.example;",
                "import java.util.stream.Stream;",
                "public class DynamicTuple {",
                "   public static Object of(Object... args) {",
                "       throw new RuntimeException();",
                "   }",
                "   public static Stream<Object> zip(Stream<Object>... args) {",
                "       throw new RuntimeException();",
                "   }",
                "}"
        );

        JavaFileObject dependant = JavaFileObjects.forSourceLines(
                "com.example.Main",
                "package com.example;",
                "import com.example.DynamicTuple;",
                "import java.util.stream.Stream;",
                "public class Main {",
                "   public static void main(String[] args) {",
                "       DynamicTuple.of(\"Alice\", 21);",
                "       DynamicTuple.of(\"Alice\", 21);",
                "       DynamicTuple.zip(",
                "           Stream.of(\"Alice\"),",
                "           Stream.of(1),",
                "           Stream.of(false)",
                "       );",
                "       DynamicTuple.zip(",
                "           Stream.of(\"Alice\"),",
                "           Stream.of(1),",
                "           Stream.of(false)",
                "       );",
                "   }",
                "}"
        );

        Compilation compilation = javac()
                .withProcessors(new TupleSpecProcessor())
                .compile(other, dependant);

        assertThat(compilation).succeeded();
        assertTrue(compilation.generatedSourceFiles().stream()
                .noneMatch(javaFileObject ->
                        javaFileObject.getName().equals("com/aparigraha/tuple/dynamic/Tuple3.java")
                ));

        assertTrue(compilation.generatedSourceFiles().stream()
                .noneMatch(javaFileObject ->
                        javaFileObject.getName().equals("com/aparigraha/tuple/dynamic/Tuple2.java")
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
                        "",
                        "public class DynamicTuple {",
                        "    public static Object of(Object... args) {",
                        "        return DynamicTupleSeed.of(args);",
                        "    }",
                        "    public static Stream<Object> zip(Stream<?>... streams) {",
                        "        return DynamicTupleSeed.zip(streams);",
                        "    }",
                        "}"
                ));
    }


    @Test
    void shouldIgnoreTheTupleDefinitionsForOtherSimilarStaticallyImportedMethodsWithWildCard() {
        JavaFileObject other = JavaFileObjects.forSourceLines(
                "com.example.DynamicTuple",
                "package com.example;",
                "import java.util.stream.Stream;",
                "public class DynamicTuple {",
                "   public static Object of(Object... args) {",
                "       throw new RuntimeException();",
                "   }",
                "   public static Stream<Object> zip(Stream<Object>... args) {",
                "       throw new RuntimeException();",
                "   }",
                "}"
        );

        JavaFileObject dependant = JavaFileObjects.forSourceLines(
                "com.example.Main",
                "package com.example;",
                "import static com.example.DynamicTuple.*;",
                "import java.util.stream.Stream;",
                "public class Main {",
                "   public static void main(String[] args) {",
                "       of(\"Alice\", 21);",
                "       of(\"Alice\", 21);",
                "       zip(",
                "           Stream.of(\"Alice\"),",
                "           Stream.of(1),",
                "           Stream.of(false)",
                "       );",
                "       zip(",
                "           Stream.of(\"Alice\"),",
                "           Stream.of(1),",
                "           Stream.of(false)",
                "       );",
                "   }",
                "}"
        );

        Compilation compilation = javac()
                .withProcessors(new TupleSpecProcessor())
                .compile(other, dependant);

        assertThat(compilation).succeeded();
        assertTrue(compilation.generatedSourceFiles().stream()
                .noneMatch(javaFileObject ->
                        javaFileObject.getName().equals("com/aparigraha/tuple/dynamic/Tuple3.java")
                ));

        assertTrue(compilation.generatedSourceFiles().stream()
                .noneMatch(javaFileObject ->
                        javaFileObject.getName().equals("com/aparigraha/tuple/dynamic/Tuple2.java")
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
                        "",
                        "public class DynamicTuple {",
                        "    public static Object of(Object... args) {",
                        "        return DynamicTupleSeed.of(args);",
                        "    }",
                        "    public static Stream<Object> zip(Stream<?>... streams) {",
                        "        return DynamicTupleSeed.zip(streams);",
                        "    }",
                        "}"
                ));
    }


    @Test
    void shouldIgnoreTheTupleDefinitionsForOtherSimilarStaticallyImportedMethods() {
        JavaFileObject other = JavaFileObjects.forSourceLines(
                "com.example.DynamicTuple",
                "package com.example;",
                "import java.util.stream.Stream;",
                "public class DynamicTuple {",
                "   public static Object of(Object... args) {",
                "       throw new RuntimeException();",
                "   }",
                "   public static Stream<Object> zip(Stream<Object>... args) {",
                "       throw new RuntimeException();",
                "   }",
                "}"
        );

        JavaFileObject dependant = JavaFileObjects.forSourceLines(
                "com.example.Main",
                "package com.example;",
                "import static com.example.DynamicTuple.of;",
                "import static com.example.DynamicTuple.zip;",
                "import java.util.stream.Stream;",
                "public class Main {",
                "   public static void main(String[] args) {",
                "       of(\"Alice\", 21);",
                "       of(\"Alice\", 21);",
                "       zip(",
                "           Stream.of(\"Alice\"),",
                "           Stream.of(1),",
                "           Stream.of(false)",
                "       );",
                "       zip(",
                "           Stream.of(\"Alice\"),",
                "           Stream.of(1),",
                "           Stream.of(false)",
                "       );",
                "   }",
                "}"
        );

        Compilation compilation = javac()
                .withProcessors(new TupleSpecProcessor())
                .compile(other, dependant);

        assertThat(compilation).succeeded();
        assertTrue(compilation.generatedSourceFiles().stream()
                .noneMatch(javaFileObject ->
                        javaFileObject.getName().equals("com/aparigraha/tuple/dynamic/Tuple3.java")
                ));

        assertTrue(compilation.generatedSourceFiles().stream()
                .noneMatch(javaFileObject ->
                        javaFileObject.getName().equals("com/aparigraha/tuple/dynamic/Tuple2.java")
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
                        "",
                        "public class DynamicTuple {",
                        "    public static Object of(Object... args) {",
                        "        return DynamicTupleSeed.of(args);",
                        "    }",
                        "    public static Stream<Object> zip(Stream<?>... streams) {",
                        "        return DynamicTupleSeed.zip(streams);",
                        "    }",
                        "}"
                ));
    }


    @Test
    void shouldIgnoreTheTupleDefinitionsForOtherSimilarFullyQualifiedMethod() {
        JavaFileObject other = JavaFileObjects.forSourceLines(
                "com.example.DynamicTuple",
                "package com.example;",
                "import java.util.stream.Stream;",
                "public class DynamicTuple {",
                "   public static Object of(Object... args) {",
                "       throw new RuntimeException();",
                "   }",
                "   public static Stream<Object> zip(Stream<Object>... args) {",
                "       throw new RuntimeException();",
                "   }",
                "}"
        );

        JavaFileObject dependant = JavaFileObjects.forSourceLines(
                "com.example.Main",
                "package com.example;",
                "import java.util.stream.Stream;",
                "public class Main {",
                "   public static void main(String[] args) {",
                "       com.example.DynamicTuple.of(\"Alice\", 21);",
                "       com.example.DynamicTuple.of(\"Alice\", 21);",
                "       com.example.DynamicTuple.zip(",
                "           Stream.of(\"Alice\"),",
                "           Stream.of(1),",
                "           Stream.of(false)",
                "       );",
                "       com.example.DynamicTuple.zip(",
                "           Stream.of(\"Alice\"),",
                "           Stream.of(1),",
                "           Stream.of(false)",
                "       );",
                "   }",
                "}"
        );

        Compilation compilation = javac()
                .withProcessors(new TupleSpecProcessor())
                .compile(other, dependant);

        assertThat(compilation).succeeded();
        assertTrue(compilation.generatedSourceFiles().stream()
                .noneMatch(javaFileObject ->
                        javaFileObject.getName().equals("com/aparigraha/tuple/dynamic/Tuple3.java")
                ));

        assertTrue(compilation.generatedSourceFiles().stream()
                .noneMatch(javaFileObject ->
                        javaFileObject.getName().equals("com/aparigraha/tuple/dynamic/Tuple2.java")
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
                        "",
                        "public class DynamicTuple {",
                        "    public static Object of(Object... args) {",
                        "        return DynamicTupleSeed.of(args);",
                        "    }",
                        "    public static Stream<Object> zip(Stream<?>... streams) {",
                        "        return DynamicTupleSeed.zip(streams);",
                        "    }",
                        "}"
                ));
    }

}