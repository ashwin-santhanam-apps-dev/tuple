package com.aparigraha.tuple.processors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import java.util.Set;


abstract public class OncePerLifecycleProcessor extends AbstractProcessor {
    private boolean executed = false;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!executed) {
            executed = true;
            return processFirstRound(annotations, roundEnv);
        }
        return false;
    }

    abstract protected boolean processFirstRound(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv);
}
