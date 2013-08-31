package fr.an.astannotation.testprocessor.dump;

import java.util.ArrayList;
import java.util.List;

import com.sun.source.tree.ASTAnnotationTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TreeVisitor;
import com.sun.source.util.TreePath;

public class AstAnnotationDumpTreePathVisitor extends com.sun.source.util.TreePathScanner<Void,Void> {

    boolean displayAsMultiLinesTree;
    private List<TreePath> foundAnnotatedTreePaths = new ArrayList<TreePath>();
    private TreeVisitor<String, Void> treeSummaryInfoPrinter = new TreeSummaryInfoPrinter();
    private boolean printTreeKind = true;
    
    public AstAnnotationDumpTreePathVisitor(boolean displayAsMultiLinesTree) {
        this.displayAsMultiLinesTree = displayAsMultiLinesTree;
    }

    @Override
    public Void scan(Tree tree, Void p) {
        if (tree == null) {
            return null;
        }
        
        List<ASTAnnotationTree> treeAnnotations = tree.getASTAnnotations();
        if (treeAnnotations != null && !treeAnnotations.isEmpty()) {
            // detected AST extensions...
            TreePath treePath = super.getCurrentPath();
            foundAnnotatedTreePaths.add(treePath);

            for(ASTAnnotationTree treeAnn : treeAnnotations) {
                Tree annotationType = treeAnn.getAnnotationType();
                String annotationTypeName = annotationType.toString();
                                
                String dumpTreePath = dumpTreePath(treePath, true, 0);
                System.out.println("Found AST-level annotation \"@@" + annotationTypeName + " on treePath:\n" + dumpTreePath);
                
                System.out.println();
            }
        }
        
        // recursive visit child elements of tree
        super.scan(tree, p);
        
        return null;
    }

    private String dumpTreePath(TreePath treePath, boolean displayAsMultiLinesTree, int startIndentLevel) {
        StringBuilder sb = new StringBuilder();
        
        // iterate from top (CompilationUnit) to bottom ... so in reverse order of tTreePath.iterator()!
        int treePathSize = 0;
        for(Tree t : treePath) {
            assert t != null;
            treePathSize++;
        }
        Tree[] reverseTreePath = new Tree[treePathSize];
        int i = treePathSize - 1;
        for (Tree t : treePath) {
            reverseTreePath[i--] = t;
        }
        
        int indentLevel = startIndentLevel;
        for (i = 0; i < treePathSize; i++, indentLevel+=4) {
            Tree tree = reverseTreePath[i];
            if (displayAsMultiLinesTree) {
                indent(sb, indentLevel);
            }

            if (treeSummaryInfoPrinter == null || printTreeKind) {
                String treeKindDisplayName = tree.getKind().getDisplayName();
                sb.append(treeKindDisplayName);
                sb.append(" ");
            }
            if (treeSummaryInfoPrinter != null) {
                String summaryInfo = tree.accept(treeSummaryInfoPrinter, null);
                sb.append(summaryInfo);
            }
            
            if (i + 1 < treePathSize) {
                if (displayAsMultiLinesTree) {
                    sb.append("\n");
                } else {
                    sb.append(" / ");
                }
            }
        }
        return sb.toString();
    }

    private static void indent(StringBuilder sb, int level) {
        for (int i = 0; i < level; i++) {
            sb.append(" ");
        }
    }
    
}
