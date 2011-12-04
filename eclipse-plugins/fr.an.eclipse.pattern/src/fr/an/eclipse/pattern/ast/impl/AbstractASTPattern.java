package fr.an.eclipse.pattern.ast.impl;

import fr.an.eclipse.pattern.ast.IPattern;
import fr.an.eclipse.pattern.ast.PatternMatchVisitor;
import fr.an.eclipse.pattern.ast.PatternVisitor;

/**
 * abstract base class for ASTNode Pattern
 *
 * @param <T>
 */
public abstract class AbstractASTPattern<T> implements IPattern<T> {

	/**
	 * Accepts the given visitor on a type-specific visit of the current node.
	 * This method must be implemented in all concrete AST node types.
	 * <p>
	 * </pre>
	 * Note that the caller (<code>accept</code>) take cares of invoking
	 * <code>visitor.preVisit(this)</code> and <code>visitor.postVisit(this)</code>.
	 * </p>
	 *
	 * @param visitor the visitor object
	 */
	public final void accept(PatternVisitor visitor) {
		// begin with the generic pre-visit
		if (visitor.preVisit(this)) {
			// dynamic dispatch to internal method for type-specific visit/endVisit
			accept0(visitor);
		}
		// end with the generic post-visit
		visitor.postVisit(this);
	}

	/** package protected, called by accept() always after preVisit(), before postVisit()*/
	/*pp*/ abstract void accept0(PatternVisitor visitor);
	
	
	
	/**
	 * Accepts the given visitor on a type-specific visit of the current node.
	 * This method must be implemented in all concrete AST node types.
	 * <p>
	 * </pre>
	 * Note that the caller (<code>accept</code>) take cares of invoking
	 * <code>visitor.preVisit(this)</code> and <code>visitor.postVisit(this)</code>.
	 * </p>
	 *
	 * @param visitor the visitor object
	 */
	@SuppressWarnings("unchecked")
	public final boolean acceptMatch(PatternMatchVisitor visitor, Object param) {
		// begin with the generic pre-visit
		boolean res = visitor.preVisitMatch(this, (T) param);
		if (res) {
			// dynamic dispatch to internal method for type-specific visit/endVisit
			res = acceptMatch0(visitor, param);
		}
		// end with the generic post-visit
		visitor.postVisitMatch(this, res, (T) param);
		return res;
	}

	/** package protected, called by acceptMatch() always after preVisit(), before postVisit()*/
	/*pp*/ abstract boolean acceptMatch0(PatternMatchVisitor visitor, Object param);
	

	
}
