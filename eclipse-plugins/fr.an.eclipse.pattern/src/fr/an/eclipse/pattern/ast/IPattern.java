package fr.an.eclipse.pattern.ast;


public interface IPattern<T> {

	/** 
	 * design pattern Visitor 
	 */
	public void accept(PatternVisitor v);

	/** 
	 * extended design pattern Visitor, using both match in Pattern tree and typed ASTNode tree 
	 * @return true if match shoudl continue (false to interrupt traversal)
	 */
	public boolean acceptMatch(PatternMatchVisitor v, Object param);


}
