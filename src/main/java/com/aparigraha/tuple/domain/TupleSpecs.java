package com.aparigraha.tuple.domain;

import java.util.HashSet;
import java.util.Set;


public record TupleSpecs(
        Set<NumberedTupleSpec> numberedTupleSpecs,
        Set<NamedTupleSpec> namedTupleSpecs
) {
    public TupleSpecs() {
        this(new HashSet<>(), new HashSet<>());
    }

    public TupleSpecs add(TupleSpecs tupleSpecs) {
        numberedTupleSpecs.addAll(tupleSpecs.numberedTupleSpecs);
        namedTupleSpecs.addAll(tupleSpecs.namedTupleSpecs);
        return this;
    }

    public TupleSpecs add(NumberedTupleSpec numberedTupleSpec) {
        numberedTupleSpecs.add(numberedTupleSpec);
        return this;
    }

    public TupleSpecs add(NamedTupleSpec namedTupleSpec) {
        namedTupleSpecs.add(namedTupleSpec);
        return this;
    }
}
