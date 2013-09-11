package fr.an.com.sun.source.tree;

import java.util.List;

/**
 * A tree node used for AST-level annotation 
 *
 * For example:
 * <pre>
 *    {@code @}{@code @}<em>prefixTargetAstElementType</em>:<em>annotationType</em>
 *    {@code @}{@code @}<em>prefixTargetAstElementType</em>:<em>annotationType</em> ( <em>arguments</em> )
 *    {@code @}{@code @}<em>annotationType</em>
 *    {@code @}{@code @}<em>annotationType</em> ( <em>arguments</em> )
 * </pre>
 * 
 * @jls ??
 *
 * @author Arnaud Nauwynck
 * @since 1.9-ARN
 */
public interface ASTAnnotationTree extends ASTExtensionTree { 

    public Tree getAnnotationType();
    List<? extends ExpressionTree> getArguments();

}
