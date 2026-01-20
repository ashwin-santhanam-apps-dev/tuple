package com.aparigraha.tuple.javac;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;

import java.util.*;
import java.util.stream.Collectors;


public class StaticMethodScanner {
    public List<MethodInvocationTree> scan(
            Set<StaticMethodSpec> methodSpecs,
            TreePath treePath
    ) {
        var imports = extractImports(treePath);

        var treePathScanner = new TreePathScanner<List<MethodInvocationTree>, Void>() {
            @Override
            public List<MethodInvocationTree> visitMethodInvocation(MethodInvocationTree node, Void p) {
                var result = super.visitMethodInvocation(node, p);
                if (methodSpecs.stream().anyMatch(expectedSpec -> isTargetMethod(expectedSpec, node))) {
                    result.add(node);
                }
                return result;
            }

            @Override
            public List<MethodInvocationTree> reduce(List<MethodInvocationTree> r1, List<MethodInvocationTree> r2) {
                var list1 = r1 == null ? new ArrayList<MethodInvocationTree>() : r1;
                var list2 = r2 == null ? new ArrayList<MethodInvocationTree>() : r2;
                list1.addAll(list2);
                return list1;
            }

            private boolean isTargetMethod(StaticMethodSpec expectedSpec, MethodInvocationTree node) {
                String caller = node.getMethodSelect().toString();
                if (caller.startsWith(expectedSpec.completeClassName())) {
                    return true;
                } else if (caller.startsWith(expectedSpec.className())) {
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

