package com.aparigraha.tuple.validators;


import com.aparigraha.tuple.javac.scan.result.NamedTupleDefinition;
import com.aparigraha.tuple.javac.scan.result.NamedTupleField;
import com.aparigraha.tuple.javac.scan.result.TupleDefinitionScanResult;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class NamedTupleArgumentOrderValidatorTest {
    @Test
    void numberOfFieldsMustBeConsistent() {
        var result = new TupleDefinitionScanResult(
                Set.of(),
                Set.of(
                        new NamedTupleDefinition(
                                "com.example",
                                "Student",
                                "named",
                                Set.of(
                                        new NamedTupleField(0, "name", "java.lang.String"),
                                        new NamedTupleField(1, "age", "int")
                                )
                        ),
                        new NamedTupleDefinition(
                                "com.example",
                                "Student",
                                "named",
                                Set.of(
                                        new NamedTupleField(0, "name", "java.lang.String")
                                )
                        ),

                        new NamedTupleDefinition(
                                "com.example",
                                "Teacher",
                                "named",
                                Set.of(
                                        new NamedTupleField(0, "name", "java.lang.String"),
                                        new NamedTupleField(1, "age", "int")
                                )
                        ),
                        new NamedTupleDefinition(
                                "com.example",
                                "Teacher",
                                "named",
                                Set.of(
                                        new NamedTupleField(0, "name", "java.lang.String")
                                )
                        ),

                        new NamedTupleDefinition(
                                "com.example",
                                "Department",
                                "named",
                                Set.of(
                                        new NamedTupleField(0, "name", "java.lang.String")
                                )
                        )
                )
        );

        var validator = new NamedTupleArgumentOrderValidator();

        var exception = assertThrows(InconsistentArgumentException.class, () -> validator.validate(result));
        assertEquals(
                Set.of(
                        "com.example.Student",
                        "com.example.Teacher"
                ),
                exception.qualifiedClassNames()
        );
    }

    @Test
    void shouldThrowExceptionWhenAllTheNamedTupleSpecArgumentsAreNotConsistent() {
        var result = new TupleDefinitionScanResult(
                Set.of(),
                Set.of(
                        new NamedTupleDefinition(
                                "com.example",
                                "Student",
                                "named",
                                Set.of(
                                        new NamedTupleField(0, "name", "java.lang.String"),
                                        new NamedTupleField(1, "age", "int")
                                )
                        ),
                        new NamedTupleDefinition(
                                "com.example",
                                "Student",
                                "named",
                                Set.of(
                                        new NamedTupleField(1, "name", "java.lang.String"),
                                        new NamedTupleField(0, "age", "int")
                                )
                        )
                )
        );

        var validator = new NamedTupleArgumentOrderValidator();

        var exception = assertThrows(InconsistentArgumentException.class, () -> validator.validate(result));
        assertEquals(Set.of("com.example.Student"), exception.qualifiedClassNames());
    }


    @Test
    void shouldThrowExceptionWhenAllTheNamedTupleSpecArgumentsTypesAreNotConsistent() {
        var result = new TupleDefinitionScanResult(
                Set.of(),
                Set.of(
                        new NamedTupleDefinition(
                                "com.example",
                                "Student",
                                "named",
                                Set.of(
                                        new NamedTupleField(0, "name", "java.lang.String"),
                                        new NamedTupleField(1, "age", "int")
                                )
                        ),
                        new NamedTupleDefinition(
                                "com.example",
                                "Student",
                                "named",
                                Set.of(
                                        new NamedTupleField(0, "name", "java.lang.String"),
                                        new NamedTupleField(1, "age", "long")
                                )
                        )
                )
        );

        var validator = new NamedTupleArgumentOrderValidator();

        var exception = assertThrows(InconsistentArgumentException.class, () -> validator.validate(result));
        assertEquals(Set.of("com.example.Student"), exception.qualifiedClassNames());
    }


    @Test
    void shouldNotThrowExceptionWhenAllTheNamedTupleSpecArgumentsTypesAreConsistent() {
        var result = new TupleDefinitionScanResult(
                Set.of(),
                Set.of(
                        new NamedTupleDefinition(
                                "com.example",
                                "Student",
                                "named",
                                Set.of(
                                        new NamedTupleField(0, "name", "java.lang.String"),
                                        new NamedTupleField(1, "age", "int")
                                )
                        ),
                        new NamedTupleDefinition(
                                "com.example",
                                "Student",
                                "named2",
                                Set.of(
                                        new NamedTupleField(0, "name", "java.lang.String"),
                                        new NamedTupleField(1, "age", "int")
                                )
                        ),
                        new NamedTupleDefinition(
                                "com.example",
                                "Student",
                                "named3",
                                Set.of(
                                        new NamedTupleField(0, "name", "java.lang.String"),
                                        new NamedTupleField(1, "age", "int")
                                )
                        )
                )
        );

        var validator = new NamedTupleArgumentOrderValidator();

        assertDoesNotThrow(() -> validator.validate(result));
    }
}