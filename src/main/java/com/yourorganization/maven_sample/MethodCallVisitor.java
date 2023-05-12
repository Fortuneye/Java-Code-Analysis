package com.yourorganization.maven_sample;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.*;

public class MethodCallVisitor extends VoidVisitorAdapter<Void> {

    private Set<MethodDeclaration> visitedMethods = new HashSet<>();
    int recursiveCount = 0;
    int nonRecursiveCount = 0;

    @Override
    public void visit(MethodDeclaration method, Void arg) {
        visitedMethods.add(method);
        super.visit(method, arg);
    }

    @Override
    public void visit(MethodCallExpr call, Void arg) {
        //recursively traverse the AST
        super.visit(call, arg);

        //if the method doesnt have a scope
        if (!call.getScope().isPresent()) {
            // This is a method call without a scope
            return;
        }
        String methodName = call.getNameAsString();
        for (MethodDeclaration visitedMethod : visitedMethods) {
            if (visitedMethod.getNameAsString().equals(methodName) && visitedMethod != call.findAncestor(MethodDeclaration.class).get()) {
                // This is a recursive method call
                recursiveCount++;
                return;
            }
        }
        nonRecursiveCount++;
    }
}