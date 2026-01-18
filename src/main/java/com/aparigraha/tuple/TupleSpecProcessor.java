package com.aparigraha.tuple;

import com.aparigraha.tuple.dynamic.DynamicTupleGenerator;
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
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.*;


@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class TupleSpecProcessor extends AbstractProcessor {
    private final DynamicTupleGenerator dynamicTupleGenerator;
    private boolean hasGenerated = false;

    private Trees trees;

    public TupleSpecProcessor(DynamicTupleGenerator dynamicTupleGenerator) {
        this.dynamicTupleGenerator = dynamicTupleGenerator;
    }

    public TupleSpecProcessor() {
        this(new DynamicTupleGenerator());
    }


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
                    for (ExpressionTree argument: node.getArguments()) {
                        var currentPath = getCurrentPath();
                        trees.getElement(currentPath); // Vital for resolving the types of arguments
                        TreePath argumentPath = new TreePath(currentPath, argument);
                        TypeMirror argumentType = trees.getTypeMirror(argumentPath);
                        System.out.println("Type: " + argumentType);
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

        if (!hasGenerated) {
            generateDynamicTupleFactoryClass();
            hasGenerated = true;
        }
        return false;
    }


    private void generateDynamicTupleFactoryClass() {
        try {
            JavaFileObject javaFileObject = processingEnv.getFiler().createSourceFile("com.aparigraha.tuple.dynamic.DynamicTuple");
            try (Writer writer = javaFileObject.openWriter()) {
                writer.write(dynamicTupleGenerator.generate());
            }
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    "Error creating DynamicTuple class"
            );
        }
    }
}
