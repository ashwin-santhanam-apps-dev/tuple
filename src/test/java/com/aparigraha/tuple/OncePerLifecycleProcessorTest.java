package com.aparigraha.tuple;

import org.junit.jupiter.api.Test;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


class OncePerLifecycleProcessorTest {
    @Test
    void shouldExecuteOnlyOnce() {
        final int[] count = {0};
        var oncePerLifecycleProcessor = new OncePerLifecycleProcessor() {
            @Override
            protected boolean processFirstRound(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
                count[0]++;
                return true;
            }
        };

        assertTrue(oncePerLifecycleProcessor.process(Set.of(), null));
        assertFalse(oncePerLifecycleProcessor.process(Set.of(), null));
        assertFalse(oncePerLifecycleProcessor.process(Set.of(), null));
        assertEquals(1, count[0]);
    }
}