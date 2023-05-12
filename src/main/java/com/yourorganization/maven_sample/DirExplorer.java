/*
*
* CODE WAS SOURCED BY Federico Tomassetti
* https://github.com/ftomassetti/analyze-java-code-examples
* https://tomassetti.me/getting-started-with-javaparser-analyzing-java-code-programmatically/
*
* */

package com.yourorganization.maven_sample;//package me.tomassetti.support;

import java.io.File;
import java.io.FileNotFoundException;

public class DirExplorer {
    public interface FileHandler {
        void handle(int level, String path, File file) throws Exception;
    }

    public interface Filter {
        boolean interested(int level, String path, File file);
    }

    private FileHandler fileHandler;
    private Filter filter;

    public DirExplorer(Filter filter, FileHandler fileHandler) {
        this.filter = filter;
        this.fileHandler = fileHandler;
    }

    public void explore(File root) throws Exception {
        explore(0, "", root);
    }

    private void explore(int level, String path, File file) throws Exception {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                explore(level + 1, path + "/" + child.getName(), child);
            }
        } else {
            if (filter.interested(level, path, file)) {
                fileHandler.handle(level, path, file);
            }
        }
    }

}
