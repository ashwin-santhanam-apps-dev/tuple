package com.aparigraha.tuple;

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.LambdaExpressionTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.*;


@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class TupleSpecProcessor extends AbstractProcessor {
    private Trees trees;

    public TupleSpecProcessor() {}


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.trees = Trees.instance(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        var methodCallScanner = new TreePathScanner<Void, Void>() {
            @Override
            public Void visitMethodInvocation(MethodInvocationTree node, Void p) {
                if (isTargetMethod(node)) {
                    for (ExpressionTree argument: node.getArguments().stream().skip(1).toList()) {
                        if (argument instanceof LambdaExpressionTree lambdaArgument) {
                            var fieldName = lambdaArgument
                                    .getParameters()
                                    .getFirst()
                                    .getName()
                                    .toString();
                            System.out.println("Field: " + fieldName);

                            var lambdaPath = getCurrentPath();
                            trees.getElement(lambdaPath);
                            TreePath bodyPath = new TreePath(lambdaPath, lambdaArgument.getBody());
                            TypeMirror bodyType = trees.getTypeMirror(bodyPath);
                            System.out.println("Type: " + bodyType);
                        }
                    }
                }
                return super.visitMethodInvocation(node, p);
            }

            private boolean isTargetMethod(MethodInvocationTree node) {
                return node.getMethodSelect().toString().equals("DynamicTuple.of");
            }
        };

        for (Element element : roundEnv.getRootElements()) {
            if (element.getKind().isClass() || element.getKind().isInterface()) {
                methodCallScanner.scan(trees.getPath(element), null);
            }
        }
        return false;
    }
}
