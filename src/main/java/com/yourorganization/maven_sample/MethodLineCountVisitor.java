package com.yourorganization.maven_sample;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileNotFoundException;


        // Define a visitor to count the number of lines in each constructor method
        public class MethodLineCountVisitor extends VoidVisitorAdapter<Void> {

            public void visit(MethodDeclaration md, ConstructorDeclaration cd, Void arg) {


            }
        }
