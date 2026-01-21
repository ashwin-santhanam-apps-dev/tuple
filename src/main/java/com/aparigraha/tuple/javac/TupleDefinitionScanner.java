package com.aparigraha.tuple.javac;

import com.aparigraha.tuple.domain.NamedTupleField;
import com.aparigraha.tuple.domain.NamedTupleSpec;
import com.aparigraha.tuple.domain.NumberedTupleSpec;
import com.aparigraha.tuple.domain.TupleSpecs;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.LambdaExpressionTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;

import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.aparigraha.tuple.SupportedTupleDefinitions.NAMED_TUPLE_FACTORY_METHOD_SPEC;


public class TupleDefinitionScanner {
    public TupleSpecs scan(
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

        var treePathScanner = new TreePathScanner<TupleSpecs, Void>() {
            @Override
            public TupleSpecs visitMethodInvocation(MethodInvocationTree node, Void p) {
                var result = super.visitMethodInvocation(node, p);
                tupleDefinitionSpecs.stream()
                        .filter(expectedSpec -> isTargetMethod(expectedSpec, node))
                        .findFirst()
                        .ifPresent(tupleDefinitionSpec -> {
                                if (tupleDefinitionSpec == NAMED_TUPLE_FACTORY_METHOD_SPEC) {
                                    result.add(processArguments(node, tupleDefinitionSpec));
                                } else {
                                    result.add(new NumberedTupleSpec(
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
            public TupleSpecs reduce(TupleSpecs r1, TupleSpecs r2) {
                return getOrCreate(r1).add(getOrCreate(r2));
            }

            private static TupleSpecs getOrCreate(TupleSpecs scanResult) {
                return scanResult == null ? new TupleSpecs() : scanResult;
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

            private String type(LambdaExpressionTree argument) {
                var lambdaPath = getCurrentPath();
                trees.getElement(lambdaPath);
                TreePath bodyPath = new TreePath(lambdaPath, argument.getBody());
                return trees.getTypeMirror(bodyPath).toString();
            }

            private String className(ExpressionTree argument) {
                var currentPath = getCurrentPath();
                trees.getElement(currentPath);
                TreePath argumentPath = new TreePath(currentPath, argument);
                TypeMirror argType = trees.getTypeMirror(argumentPath);
                if (argType instanceof DeclaredType declaredType) {
                    return declaredType.toString().replaceAll("\\.class$", "");
                } else {
                    // TODO: Throw exception if unable to get class name
                    return null;
                }
            }

            private NamedTupleSpec processArguments(MethodInvocationTree node, TupleDefinitionSpec spec) {
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
                                        type(indexedArgument.value())
                                )
                        )
                        .collect(Collectors.toSet());
                return new NamedTupleSpec(
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

