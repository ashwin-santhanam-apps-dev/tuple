package com.aparigraha.tuple.dynamic;

import com.sun.source.tree.*;
import com.sun.source.util.*;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.stream.Collectors;


public class DynamicTuplePlugin implements Plugin {
    private Trees trees;

    @Override
    public String getName() {
        return DynamicTuplePlugin.class.getSimpleName();
    }

    @Override
    public void init(JavacTask task, String... args) {
        System.out.println("DynamicTuplePlugin: Hello!!");
        trees = Trees.instance(task);
        task.addTaskListener(new TaskListener() {
            @Override
            public void finished(TaskEvent e) {
                switch (e.getKind()) {
                    case ANALYZE -> scanForUsages(e.getCompilationUnit());
                }
            }
        });
    }

    private void scanForUsages(CompilationUnitTree unit) {
        unit.accept(new TreeScanner<Void, Void>() {
            @Override
            public Void visitMethodInvocation(MethodInvocationTree node, Void v) {
                TreePath path = trees.getPath(unit, node);
                Element element = trees.getElement(path);
                if (element instanceof ExecutableElement method) {
                    String methodName = method.getSimpleName().toString();
                    if (method.getEnclosingElement() instanceof TypeElement enclosingClass) {
                        String className = enclosingClass.getQualifiedName().toString();
                        String methodQualifiedName = "%s.%s".formatted(className, methodName);
                        if (methodQualifiedName.equals("com.aparigraha.tuple.dynamic.DynamicTuple.of")) {
                            List<? extends ExpressionTree> arguments = node.getArguments();
                            for (ExpressionTree argument: arguments) {
                                if (argument instanceof LiteralTree literalTree) {
                                    System.out.println("FOUND DYNAMIC TUPLE: " + literalTree.getValue());
                                } else if (argument instanceof LambdaExpressionTree lambda) {
                                    List<? extends VariableTree> params = lambda.getParameters();
                                    String paramNames = params.stream()
                                            .map(p -> p.getName().toString())
                                            .collect(Collectors.joining(", "));
                                    System.out.println("Params: " + paramNames);
                                    Tree body = lambda.getBody();
                                    if (body instanceof LiteralTree) {
                                        TreePath argPath = trees.getPath(unit, body);
                                        TypeMirror typeMirror = trees.getTypeMirror(argPath);
                                        System.out.println("Type: " + typeMirror);
                                        Object value = ((LiteralTree) body).getValue();
                                        System.out.println("Lambda returns literal: " + value);
                                    } else {
                                        TreePath argPath = trees.getPath(unit, body);
                                        TypeMirror typeMirror = trees.getTypeMirror(argPath);
                                        System.out.println("Non-Literal Type: " + typeMirror);
                                    }
                                }
                            }
                        }
                    }
                }

//                ExpressionTree expressionTree = node.getMethodSelect();
//                var expression = expressionTree.toString();
//                // Check if calling A.B
//                if (expressionTree.toString().contains("DynamicTuple.of")) {
//                    System.out.println("DynamicTuplePlugin: " + expression);
//                    for (ExpressionTree arg : node.getArguments()) {
//                        // In a real plugin, you'd use Trees.getTypeMirror(new TreePath(unit, arg))
//                        // For this example, we assume we want to handle the type found
//                    }
//                }
                return super.visitMethodInvocation(node, v);
            }
        }, null);
    }
}
