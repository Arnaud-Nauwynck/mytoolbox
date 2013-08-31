package fr.an.astannotation.testprocessor.dump;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;

/**
 * Bridge between javac javax.annotation.processing.AbstractProcessor and AST Visitor
 * used to print extended AstAnnotation (> jdk1.8-ARN) found in AST
 * 
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("*")
public class AstAnnotationDumpProcessor extends AbstractProcessor {

    private boolean displayAsMultiLinesTree;

    private Trees trees;

    // ------------------------------------------------------------------------

    public AstAnnotationDumpProcessor(boolean displayAsMultiLinesTree) {
        this.displayAsMultiLinesTree = displayAsMultiLinesTree;
    }

    // ------------------------------------------------------------------------

    @Override
    public void init(ProcessingEnvironment pe) {
        super.init(pe);
        trees = Trees.instance(pe);
    }

    /**
     * Processes the annotation types defined for this processor.
     * 
     * @param annotations
     *            the annotation types requested to be processed
     * @param roundEnvironment
     *            environment to get information about the current and prior
     *            round
     * @return whether or not the set of annotations are claimed by this
     *         processor
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations,
            RoundEnvironment roundEnvironment) {

        // processUsingJCTreePathScanner(roundEnvironment);

        processUsingTreePathScanner(roundEnvironment);
        
        return true;
    }


    private void processUsingTreePathScanner(RoundEnvironment roundEnvironment) {
        // Scanner class to scan through various component elements
        AstAnnotationDumpTreePathVisitor visitor = new AstAnnotationDumpTreePathVisitor(displayAsMultiLinesTree);
        
        for (Element e : roundEnvironment.getRootElements()) {
            TreePath tp = trees.getPath(e);
            // Tree tree = tp.getLeaf();
                
            visitor.scan(tp, null);

        }
    }
    
//    @Deprecated
//    protected void processUsingJCTreePathScanner(RoundEnvironment roundEnvironment) {
//        // Scanner class to scan through various component elements
//        AstAnnotationDumpJCTreePathVisitor visitor = new AstAnnotationDumpJCTreePathVisitor();
//        
//        for (Element e : roundEnvironment.getRootElements()) {
//            TreePath tp = trees.getPath(e);
//            Tree tree = tp.getLeaf();
//                
//                // TODO ... 
////            JCTreePath jcTreePath = (JCTreePath) tp; // can not downcast from TreePath to JCTreePath !! 
////            JCTree javacTree = (JCTree) tree; 
////            
////            // invoke the visitor (recursive scan into tree)
////            // javacTree.accept(visitor);  // <= NPE... 
////            visitor.scan(tp);
//                
//        }
//    }

    
}
