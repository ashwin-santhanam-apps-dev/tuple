package com.aparigraha.tuple.validators;

import com.aparigraha.tuple.javac.scan.result.NamedTupleDefinition;
import com.aparigraha.tuple.javac.scan.result.TupleDefinitionScanResult;

import java.util.stream.Collectors;


public class NamedTupleArgumentOrderValidator implements Validator {
    @Override
    public void validate(TupleDefinitionScanResult tupleDefinitionScanResult) throws InconsistentArgumentException {
        record DetailedFieldInfo (String qualifiedClassName, int index, String argumentName, String type) {}

        var tupleDefinitionsFieldCount = tupleDefinitionScanResult.namedTupleDefinitions()
                .stream()
                .flatMap(namedTupleDefinition ->
                    namedTupleDefinition.fields().stream().map(fieldDefinition ->
                            new DetailedFieldInfo(
                                    namedTupleDefinition.qualifiedName(),
                                    fieldDefinition.index(),
                                    fieldDefinition.name(),
                                    fieldDefinition.type()
                            )
                    )
                )
                .distinct()
                .collect(Collectors.groupingBy(
                        DetailedFieldInfo::qualifiedClassName,
                        Collectors.counting()
                ));

        var invalidTuples = tupleDefinitionScanResult.namedTupleDefinitions().stream()
                .filter(namedTupleDefinition ->
                        tupleDefinitionsFieldCount.get(namedTupleDefinition.qualifiedName()) != namedTupleDefinition.fields().size()
                )
                .map(NamedTupleDefinition::qualifiedName)
                .collect(Collectors.toSet());

        if (!invalidTuples.isEmpty()) {
            throw new InconsistentArgumentException(invalidTuples);
        }
    }

}
