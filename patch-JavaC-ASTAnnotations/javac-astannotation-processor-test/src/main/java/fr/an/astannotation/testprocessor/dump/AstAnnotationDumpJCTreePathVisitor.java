package fr.an.astannotation.testprocessor.dump;

import java.util.List;

import com.sun.source.tree.Tree;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCASTAnnotation;
import com.sun.tools.javac.tree.JCTreePath;

public class AstAnnotationDumpJCTreePathVisitor extends com.sun.tools.javac.tree.JCTreePathScanner {

    @Override
    public void scan(JCTree tree) {
        if (tree.treeExtensions != null && !tree.treeExtensions.isEmpty()) {
            // detected AST Extensions (Comments, ASTAnnotation, ...)
            List<JCASTAnnotation> treeAnnotations = tree.filterASTAnnotations();
            if (treeAnnotations != null && !treeAnnotations.isEmpty()) {
                // detected AST Annotation "@@"...
                for(JCASTAnnotation treeAnn : treeAnnotations) {
                    JCTreePath treePath = super.getCurrentPath();
                    Tree annotationType = treeAnn.getAnnotationType();
                    String annotationTypeName = annotationType.toString();
                    
                    System.out.println("Found AST-level annotation \"@@" + annotationTypeName + " on treePath: " + treePath);
                }
            }
        }
        
        // recursive visit child elements of tree
        super.scan(tree);
    }

    
}
