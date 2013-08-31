package fr.an.astannotation.testprocessor;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.annotation.processing.AbstractProcessor;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;

public class ProcessorExecutor {
    
    /**
     * Invokes the annotation processor passing the list of file names
     * 
     * @param fileNames
     *            Names of files to be verified
     */
    public void invokeProcessor(List<File> files, List<AbstractProcessor> processors) {
        // Gets the Java programming language compiler
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        // Get a new instance of the standard file manager implementation
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(
                null, null, null);
        // Get the valid source files as a list
        if (files.size() > 0) {
            // Get the list of java file objects
            Iterable<? extends JavaFileObject> compilationUnits1 = fileManager
                    .getJavaFileObjectsFromFiles(files);
            // Create the compilation task
            CompilationTask task = compiler.getTask(null, fileManager, null,
                    null, null, compilationUnits1);
            // Get the list of annotation processors
            task.setProcessors(processors);
            // Perform the compilation task.
            task.call();
            try {
                fileManager.close();
            } catch (IOException e) {
                System.out.println(e.getLocalizedMessage());
            }
        } else {
            System.out.println("No valid source files to process. "
                    + "Extiting from the program");
            System.exit(0);
        }
    }

}
