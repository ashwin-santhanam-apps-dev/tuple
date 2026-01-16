package com.aparigraha.tuple;

import com.aparigraha.tuple.generator.TupleGenerator;
import com.aparigraha.tuple.generator.TupleSchemaWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
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
                .thenReturn(new int[] {4});

        var element1 = mock(VariableElement.class);
        when(element1.getAnnotation(TupleSpec.class)).thenReturn(tupleSpec1);

        when(tupleGenerator.generate(
                argThat(params -> Objects.equals(params.className(), "Tuple4") &&
                        Objects.equals(params.packageName(), "com.aparigraha.tuples") &&
                        Objects.equals(params.fields(), List.of("item0", "item1", "item2", "item3"))
                )
        )).thenReturn("Tuple code");

        doReturn(Set.of(element1))
                .when(roundEnvironment)
                .getElementsAnnotatedWith(TupleSpec.class);


        when(tupleSchemaWriter.write(eq("Tuple code"), eq("com.aparigraha.tuples.Tuple4"), any()))
                .thenReturn(true);

        TupleSpecProcessor tupleSpecProcessor = new TupleSpecProcessor(tupleGenerator, tupleSchemaWriter);

        assertTrue(tupleSpecProcessor.process(Set.of(), roundEnvironment));
    }
}