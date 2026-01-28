package com.aparigraha.tuple.validators;

import java.util.Set;

public class InconsistentArgumentException extends Exception {
    private final Set<String> qualifiedClassNames;

    public InconsistentArgumentException(Set<String> qualifiedClassNames) {
        this.qualifiedClassNames = qualifiedClassNames;
    }

    public Set<String> qualifiedClassNames() {
        return qualifiedClassNames;
    }

    @Override
    public String getMessage() {
        return "Inconsistent arguments for named tuples: %s".formatted(qualifiedClassNames);
    }

    @Override
    public String toString() {
        return getMessage();
    }
}
