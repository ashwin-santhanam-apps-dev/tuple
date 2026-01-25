package com.aparigraha.tuple.javac;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.LambdaExpressionTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;

import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.aparigraha.tuple.processors.SupportedTupleDefinitions.NAMED_TUPLE_FACTORY_METHOD_SPEC;


public class TupleDefinitionScanner {
    public TupleDefinitionScanResult scan(
            Set<TupleDefinitionSpec> tupleDefinitionSpecs,
            Trees trees,
            Elements elementUtils,
            Element rootElement
    ) {
        var treePath = trees.getPath(rootElement);
        var imports = extractImports(treePath);
        var packageName = elementUtils.getPackageOf(rootElement)
                .getQualifiedName()
                .toString();

        var treePathScanner = new TreePathScanner<TupleDefinitionScanResult, Void>() {
            @Override
            public TupleDefinitionScanResult visitMethodInvocation(MethodInvocationTree node, Void p) {
                var result = super.visitMethodInvocation(node, p);
                tupleDefinitionSpecs.stream()
                        .filter(expectedSpec -> isTargetMethod(expectedSpec, node))
                        .findFirst()
                        .ifPresent(tupleDefinitionSpec -> {
                                if (tupleDefinitionSpec == NAMED_TUPLE_FACTORY_METHOD_SPEC) {
                                    result.add(processArguments(node, tupleDefinitionSpec));
                                } else {
                                    result.add(new NumberedTupleDefinition(
                                            tupleDefinitionSpec.className(),
                                            tupleDefinitionSpec.methodName(),
                                            node.getArguments().size()
                                    ));
                                }
                            }
                        );
                return result;
            }

            @Override
            public TupleDefinitionScanResult reduce(TupleDefinitionScanResult r1, TupleDefinitionScanResult r2) {
                return getOrCreate(r1).add(getOrCreate(r2));
            }

            private static TupleDefinitionScanResult getOrCreate(TupleDefinitionScanResult scanResult) {
                return scanResult == null ? new TupleDefinitionScanResult() : scanResult;
            }

            private boolean isTargetMethod(TupleDefinitionSpec expectedSpec, MethodInvocationTree node) {
                String caller = node.getMethodSelect().toString();
                if (caller.startsWith(expectedSpec.completeClassName() + "." + expectedSpec.methodName())) {
                    return true;
                } else if (caller.startsWith(expectedSpec.className() + "." + expectedSpec.methodName())) {
                    return imports.stream()
                            .anyMatch(importStatement ->
                                    !importStatement.isStatic() &&
                                            (
                                                    Objects.equals(importStatement.identifier(), expectedSpec.packageName() + "*") ||
                                                    Objects.equals(importStatement.identifier(), expectedSpec.completeClassName())
                                            )
                            );
                } else if (caller.startsWith(expectedSpec.methodName())) {
                    return imports.stream()
                            .anyMatch(importStatement ->
                                    importStatement.isStatic() &&
                                            (
                                                    Objects.equals(importStatement.identifier(), expectedSpec.completeClassName() + ".*") ||
                                                    Objects.equals(importStatement.identifier(), expectedSpec.completeClassName() + "." + caller)
                                            )
                            );
                } else {
                    return false;
                }
            }

            private String fieldName(LambdaExpressionTree argument) {
                return argument
                        .getParameters()
                        .getFirst()
                        .getName()
                        .toString();
            }

            private String className(ExpressionTree argument) {
                return argument.toString().replaceAll("\\.class$", "");
            }

            private NamedTupleDefinition processArguments(MethodInvocationTree node, TupleDefinitionSpec spec) {
                // TODO: Throw exception when remaining args are not FieldSpec<T>
                record IndexedValue<T>(int index, T value) {}
                var arguments = node.getArguments();
                var fields = IntStream.range(0, arguments.size()).boxed()
                        .map(index -> new IndexedValue<>(index - 1, arguments.get(index)))
                        .filter(indexedArgument -> indexedArgument.value() instanceof LambdaExpressionTree)
                        .map(indexedArgument ->
                                new IndexedValue<>(
                                        indexedArgument.index(),
                                        (LambdaExpressionTree) indexedArgument.value()
                                )
                        )
                        .map(indexedArgument ->
                                new NamedTupleField(
                                        indexedArgument.index(),
                                        fieldName(indexedArgument.value()),
                                        null
                                )
                        )
                        .collect(Collectors.toSet());
                return new NamedTupleDefinition(
                        packageName,
                        className(arguments.get(0)),
                        spec.methodName(),
                        fields
                );
            }
        };

        return treePathScanner.scan(
                treePath,
                null
        );
    }

    private Set<ImportStatement> extractImports(TreePath treePath) {
        if (treePath == null) return Set.of();
        CompilationUnitTree compilationUnit = treePath.getCompilationUnit();
        return compilationUnit.getImports().stream()
                .map(importTree -> new ImportStatement(
                        importTree.getQualifiedIdentifier().toString(),
                        importTree.isStatic()
                )).collect(Collectors.toSet());
    }
}

record ImportStatement(
        String identifier,
        boolean isStatic
) {}

