package com.aparigraha.tuple.validators;

import com.aparigraha.tuple.javac.scan.result.TupleDefinitionScanResult;


public interface Validator {
    void validate(TupleDefinitionScanResult tupleDefinitionScanResult) throws Exception;
}
