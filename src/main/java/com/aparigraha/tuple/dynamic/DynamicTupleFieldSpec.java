package com.aparigraha.tuple.dynamic;


public interface DynamicTupleFieldSpec<T> {
    T value(T argumentName);
}
