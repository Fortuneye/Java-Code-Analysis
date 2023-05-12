package com.yourorganization.maven_sample;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileNotFoundException;

import java.util.concurrent.atomic.AtomicInteger;

public class ParameterVisitor extends VoidVisitorAdapter<Void> {

    //AtomicInteger paramCount = new AtomicInteger();
    int paramCount = 0;

    @Override
    public void visit(MethodDeclaration md, Void arg) {
        paramCount = md.getParameters().size();
        //System.out.println("Method " + md.getName() + " has " + md.getParameters().size() + " parameters");

        super.visit(md, arg);
    }
}
