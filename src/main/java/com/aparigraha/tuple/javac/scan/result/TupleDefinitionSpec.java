package com.aparigraha.tuple.javac.scan.result;

public record TupleDefinitionSpec(
        String packageName,
        String className,
        String methodName
) {
    public String completeClassName() {
        return packageName + "." + className;
    }
}
