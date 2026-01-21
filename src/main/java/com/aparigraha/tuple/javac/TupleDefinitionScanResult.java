package com.aparigraha.tuple.javac;

import java.util.HashSet;
import java.util.Set;


public record TupleDefinitionScanResult (
        Set<NumberedTupleDefinition> numberedTupleDefinitions,
        Set<NamedTupleDefinition> namedTupleDefinitions
) {
    public TupleDefinitionScanResult() {
        this(new HashSet<>(), new HashSet<>());
    }

    public TupleDefinitionScanResult add(TupleDefinitionScanResult scanResult) {
        numberedTupleDefinitions.addAll(scanResult.numberedTupleDefinitions);
        namedTupleDefinitions.addAll(scanResult.namedTupleDefinitions);
        return this;
    }

    public TupleDefinitionScanResult add(NumberedTupleDefinition numberedTupleDefinition) {
        numberedTupleDefinitions.add(numberedTupleDefinition);
        return this;
    }

    public TupleDefinitionScanResult add(NamedTupleDefinition tupleDefinition) {
        namedTupleDefinitions.add(tupleDefinition);
        return this;
    }
}
