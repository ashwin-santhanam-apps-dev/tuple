package com.aparigraha.tuple.javac;

public record StaticMethodSpec(
        String packageName,
        String className,
        String methodName
) {
    public String completeClassName() {
        return packageName + "." + className;
    }
}
