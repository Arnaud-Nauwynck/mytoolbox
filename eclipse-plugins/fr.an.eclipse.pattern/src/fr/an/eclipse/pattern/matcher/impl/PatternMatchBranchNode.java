package fr.an.eclipse.pattern.matcher.impl;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;

import fr.an.eclipse.pattern.ast.IPattern;

/**
 * internal structure for (stack-based) pattern matching traversal
 *
 */
public abstract class PatternMatchBranchNode {

	protected IPattern<?> ownerPattern;
	

	static class PreMatchElt {
		IPattern<Object> pattern;
		Object node;
		
		@SuppressWarnings("unchecked")
		public <T> PreMatchElt(IPattern<T> pattern, T node) {
			super();
			this.pattern = (IPattern<Object>) pattern;
			this.node = node;
		}
	}

	protected Deque<PreMatchElt> currPreMatchPath = new ArrayDeque<PreMatchElt>();
	
	// ------------------------------------------------------------------------
		
	public PatternMatchBranchNode(IPattern<?> ownerPattern) {
		super();
		this.ownerPattern = ownerPattern;
	}

	// ------------------------------------------------------------------------
	
	public abstract boolean next(PatternMatchStackFetchVisitor owner);
	

	public IPattern<?> getOwnerPattern() {
		return ownerPattern;
	}

	
	/*pp*/ Deque<PreMatchElt> getCurrPreMatchPath() {
		return currPreMatchPath;
	}
	
	/*pp*/ <T> void pushPreVisitMatch(IPattern<T> pattern, T node) {
		currPreMatchPath.addLast(new PreMatchElt(pattern, node));
	}


	/*pp*/ void popPreVisitMatch() {
		currPreMatchPath.removeLast();
	}

	// ------------------------------------------------------------------------

	/**
	 * 
	 */
	public static class SimplePatternNodeBranchNode extends PatternMatchBranchNode {
		
		protected Object currASTNode;
		protected Object nextASTNode;

		public SimplePatternNodeBranchNode(IPattern<?> ownerPattern, Object astNode) {
			super(ownerPattern);
			this.nextASTNode = astNode;
		}

		public Object getCurrASTNode() {
			return currASTNode;
		}

		public void setCurrASTNode(Object p) {
			this.currASTNode = p;
		}

		public Object getNextASTNode() {
			return nextASTNode;
		}

		public void setNextASTNode(Object p) {
			this.nextASTNode = p;
		}
		
		public boolean next(PatternMatchStackFetchVisitor owner) {
			boolean res;
			if (nextASTNode != null) {
				Object tmpNode = nextASTNode;
				currASTNode = nextASTNode;
				nextASTNode = null;
				// *** do test match object ***
				res = ownerPattern.acceptMatch(owner, tmpNode);
			} else {
				res = false;
			}
			return res;
		}
	}
	
	
	// ------------------------------------------------------------------------
	
	/**
	 * 
	 * @param <T>
	 */
	public static class EltInListPatternNodeBranchNode<T> extends PatternMatchBranchNode {
		
		protected List<T> currNodeList;
		protected int currIndexInList;

		public EltInListPatternNodeBranchNode(IPattern<?> ownerPattern, List<T> currNodeList) {
			this(ownerPattern, currNodeList, -1);
		}
		
		public EltInListPatternNodeBranchNode(IPattern<?> ownerPattern, List<T> currNodeList, int currIndexInList) {
			super(ownerPattern);
			this.currNodeList = currNodeList;
			this.currIndexInList = currIndexInList;
		}

		public boolean next(PatternMatchStackFetchVisitor owner) {
			boolean res = false;
			if (currNodeList != null && (currIndexInList+1) < currNodeList.size()) {
				currIndexInList++;
				Object currElt = currNodeList.get(currIndexInList);
				// *** do test match object ***
				res = ownerPattern.acceptMatch(owner, currElt);
			}
			return res;
		}

	}

	// ------------------------------------------------------------------------
	
	/**
	 *
	 * @param <T>
	 */
	public static class EltInASTNodeFieldListPatternNodeBranchNode<T> extends PatternMatchBranchNode {
		
		protected ASTNode parentASTNode;
		protected List<StructuralPropertyDescriptor> childPropertyDescriptors;
		protected int currChildPropertyIndex;
		
		public EltInASTNodeFieldListPatternNodeBranchNode(IPattern<?> ownerPattern, 
				ASTNode parentASTNode,
				List<StructuralPropertyDescriptor> childPropertyDescriptors) {
			super(ownerPattern);
			this.parentASTNode = parentASTNode;
			this.childPropertyDescriptors = childPropertyDescriptors;
			this.currChildPropertyIndex = -1;
		}



		public boolean next(PatternMatchStackFetchVisitor owner) {
			boolean res = false;
			if (parentASTNode != null && childPropertyDescriptors != null
					&& (currChildPropertyIndex+1) < childPropertyDescriptors.size()) {
				currChildPropertyIndex++;
				StructuralPropertyDescriptor propDescr = childPropertyDescriptors.get(currChildPropertyIndex);
				Object childProp = parentASTNode.getProperty(propDescr.getId());
				// *** do test match object ***
				res = super.ownerPattern.acceptMatch(owner, childProp);				
			} else {
				res = false;
			}
			return res;
		}
	
	}


	// ------------------------------------------------------------------------

	/**
	 *
	 */
	public static class PatternsOneOfPatternNodeBranchNode extends PatternMatchBranchNode {
		
		protected List<PatternMatchBranchNode> branchNodes;
		protected int currBranchNodeIndex;
		
		public PatternsOneOfPatternNodeBranchNode(IPattern<?> ownerPattern, List<PatternMatchBranchNode> branchNodes) {
			super(ownerPattern);
			this.branchNodes = branchNodes;
			this.currBranchNodeIndex = -1;
		}
		
		public boolean next(PatternMatchStackFetchVisitor owner) {
			boolean res = false;
			if (branchNodes != null && (currBranchNodeIndex+1) < branchNodes.size()) {
				currBranchNodeIndex++;
				PatternMatchBranchNode currBranchNode = branchNodes.get(currBranchNodeIndex);
				// *** do test next branch node ***
				res = currBranchNode.next(owner);
			}
			return res;
		}

	}
	

}
