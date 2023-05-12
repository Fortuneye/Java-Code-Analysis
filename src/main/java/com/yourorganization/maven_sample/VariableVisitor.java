package com.yourorganization.maven_sample;

import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.concurrent.atomic.AtomicInteger;

public class VariableVisitor extends VoidVisitorAdapter<Void> {

    AtomicInteger primitiveTypeCount = new AtomicInteger();
    AtomicInteger compsositeTypeCount = new AtomicInteger();

    AtomicInteger booleanCount = new AtomicInteger();
    AtomicInteger byteCount = new AtomicInteger();
    AtomicInteger shortCount = new AtomicInteger();
    AtomicInteger charCount = new AtomicInteger();
    AtomicInteger intCount = new AtomicInteger();
    AtomicInteger longCount = new AtomicInteger();
    AtomicInteger floatCount = new AtomicInteger();
    AtomicInteger doubleCount = new AtomicInteger();

    @Override
    public void visit(VariableDeclarator declarator, Void arg) {
        //System.out.println(declarator.getType());

        if(declarator.getType().isPrimitiveType()){
            primitiveTypeCount.getAndIncrement();

            switch (declarator.getType().asString()) {
                case "boolean":
                    booleanCount.getAndIncrement();
                    break;

                case "byte":
                    byteCount.getAndIncrement();
                    break;

                case "short":
                    shortCount.getAndIncrement();
                    break;

                case "char":
                    charCount.getAndIncrement();
                    break;

                case "int":
                    intCount.getAndIncrement();
                    break;

                case "long":
                    longCount.getAndIncrement();

                    break;
                case "double":
                    doubleCount.getAndIncrement();
                    break;

                case "float":
                    floatCount.getAndIncrement();
                    break;
            }
        }else{
            compsositeTypeCount.getAndIncrement();
        }

    }
}
