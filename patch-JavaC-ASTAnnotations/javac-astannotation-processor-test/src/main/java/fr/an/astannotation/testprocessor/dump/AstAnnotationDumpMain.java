package fr.an.astannotation.testprocessor.dump;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.processing.AbstractProcessor;

import fr.an.astannotation.testprocessor.FileSetUtils;
import fr.an.astannotation.testprocessor.ProcessorExecutor;

/**
 * Sample usage:
 * 
 * <PRE>
 * java -jar .. fr.an.astannotation.testprocessor.dump.AstAnnotationDumpMain --file src/main/java/fr/an/testastannotation/hello/HelloAstAnnotationMain.java
 * </PRE>
 * 
 * use with this source code file "test-hello-ast-annotations/src/main/java/fr/an/testastannotation/hello/HelloAstAnnotationMain.java"
 * which content is : 
 * <PRE>
 * public class HelloAstAnnotationMain {

    public static void main(String[] args) {
        fooWithASTAnnotation();
        foo();
    }

    private static void fooWithASTAnnotation() {
        &#047;*@@TestASTAnnotation1*&#047;
        System.out.println("Hello AST Annotations");
    }

    private static void foo() {
        &#047;* not an annotation *&#047;;
        System.out.println("Hello AST Annotations");
    }

}
 * </PRE>
 * 
 * ==> Then, you should be able to see these result:
 * <PRE>
Found AST-level annotation "@@TestASTAnnotation1 on treePath:
CompilationUnit CompilationUnit test-ast-annotations/src/main/java/fr/an/testastannotation/hello/HelloAstAnnotationMain.java
    Class class HelloAstAnnotationMain
        Method method fooWithASTAnnotation
            Block {}
                ExpressionStatement expr;
                    MethodInvocation (..)
                        MemberSelect .println
                            MemberSelect .out

Finished - OK
 * </PRE>
 * 
 * 
 * @author arnaud
 *
 */
public class AstAnnotationDumpMain {

    private List<File> files = new ArrayList<File>();
    private boolean displayAsMultiLinesTree = true;

    public static void main(String[] args) {
        try {
            AstAnnotationDumpMain mainObj = new AstAnnotationDumpMain();
            mainObj.parseArgs(args);
            mainObj.run();
            System.out.println("Finished - OK");
        } catch(Exception ex) {
            System.err.println("Finished - ERROR");
            ex.printStackTrace(System.err);
            System.exit(-1);
        }
    }
    
    private void parseArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("--file")) {
                files.add(new File(args[++i]));
            } else if (arg.equals("--files")) {
                files.addAll(FileSetUtils.getFilesAsList(args[++i]));
            }
        }
        
        if (files.isEmpty()) {
            throw new IllegalArgumentException("you must set at least one file to process using " 
                    + "\"--file file\" or \"--files csvFilesList\" ");
        }
    }

    public void run() {
        AstAnnotationDumpProcessor astDumpProcessor = new AstAnnotationDumpProcessor(displayAsMultiLinesTree);
        List<AbstractProcessor> processors = Collections.singletonList((AbstractProcessor)astDumpProcessor);
        
        ProcessorExecutor processorExecutor = new ProcessorExecutor();
        processorExecutor.invokeProcessor(files, processors);
    }
}
