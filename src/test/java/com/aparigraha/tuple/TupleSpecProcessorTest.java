package com.aparigraha.tuple;

import com.aparigraha.tuple.generator.TupleGenerationParams;
import com.aparigraha.tuple.generator.TupleGenerator;
import com.aparigraha.tuple.generator.TupleSchemaWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.VariableElement;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TupleSpecProcessorTest {
    @Mock
    private TupleGenerator tupleGenerator;

    @Mock
    private TupleSchemaWriter tupleSchemaWriter;

    @Spy
    private RoundEnvironment roundEnvironment;


    @Test
    void shouldGenerateTuplesWhenAnnotationsAreAreDefinedAcrossMultipleElements() throws IOException {
        var tupleSpec1 = mock(TupleSpec.class);
        when(tupleSpec1.value())
                .thenReturn(new int[] {2, 4});

        var tupleSpec2 = mock(TupleSpec.class);
        when(tupleSpec2.value())
                .thenReturn(new int[] {1, 3});

        var element1 = mock(VariableElement.class);
        when(element1.getAnnotation(TupleSpec.class)).thenReturn(tupleSpec1);

        var element2 = mock(VariableElement.class);
        when(element2.getAnnotation(TupleSpec.class)).thenReturn(tupleSpec2);

        when(tupleGenerator.generate(any())).thenAnswer(invocation -> {
            TupleGenerationParams params = invocation.getArgument(0);

            if ("Tuple4".equals(params.className()) &&
                    "com.aparigraha.tuples".equals(params.packageName()) &&
                    Objects.equals(params.fields(), List.of("item0", "item1", "item2", "item3"))
            ) {
                return "Tuple code for Tuple4";
            }
            else if ("Tuple2".equals(params.className()) &&
                    "com.aparigraha.tuples".equals(params.packageName()) &&
                    Objects.equals(params.fields(), List.of("item0", "item1"))
            ) {
                return "Tuple code for Tuple2";
            }
            else if ("Tuple3".equals(params.className()) &&
                    "com.aparigraha.tuples".equals(params.packageName()) &&
                    Objects.equals(params.fields(), List.of("item0", "item1", "item2"))
            ) {
                return "Tuple code for Tuple3";
            }
            else if ("Tuple1".equals(params.className()) &&
                    "com.aparigraha.tuples".equals(params.packageName()) &&
                    Objects.equals(params.fields(), List.of("item0"))
            ) {
                return "Tuple code for Tuple1";
            }
            else return null;
        });

        doReturn(Set.of(element1, element2))
                .when(roundEnvironment)
                .getElementsAnnotatedWith(TupleSpec.class);


        when(tupleSchemaWriter.write(eq("Tuple code for Tuple4"), eq("com.aparigraha.tuples.Tuple4"), any()))
                .thenReturn(true);
        when(tupleSchemaWriter.write(eq("Tuple code for Tuple2"), eq("com.aparigraha.tuples.Tuple2"), any()))
                .thenReturn(true);
        when(tupleSchemaWriter.write(eq("Tuple code for Tuple3"), eq("com.aparigraha.tuples.Tuple3"), any()))
                .thenReturn(true);
        when(tupleSchemaWriter.write(eq("Tuple code for Tuple1"), eq("com.aparigraha.tuples.Tuple1"), any()))
                .thenReturn(true);

        TupleSpecProcessor tupleSpecProcessor = new TupleSpecProcessor(tupleGenerator, tupleSchemaWriter);

        assertTrue(tupleSpecProcessor.process(Set.of(), roundEnvironment));
    }
}