package fr.an.eclipse.pattern.matcher.impl;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;

import fr.an.eclipse.pattern.ast.IPattern;
import fr.an.eclipse.pattern.ast.impl.StdAlgebraPatterns.GroupCapturePattern;
import fr.an.eclipse.pattern.ast.impl.StdAlgebraPatterns.MatchAllOfPattern;
import fr.an.eclipse.pattern.ast.impl.StdAlgebraPatterns.MatchAndPattern;
import fr.an.eclipse.pattern.ast.impl.StdAlgebraPatterns.MatchNotPattern;
import fr.an.eclipse.pattern.ast.impl.StdAlgebraPatterns.MatchOneOfPattern;
import fr.an.eclipse.pattern.ast.impl.StdAlgebraPatterns.MatchOrPattern;
import fr.an.eclipse.pattern.ast.impl.StdAlgebraPatterns.WildcardContainingPattern;
import fr.an.eclipse.pattern.ast.impl.StdAlgebraPatterns.WildcardPattern;
import fr.an.eclipse.pattern.ast.impl.StdListPatterns.MatchAllEltsOfListPattern;
import fr.an.eclipse.pattern.ast.impl.StdListPatterns.MatchOneEltOfListPattern;
import fr.an.eclipse.pattern.ast.utils.RecurseChildPatternMatchVisitor;
import fr.an.eclipse.pattern.matcher.IMatchResult;
import fr.an.eclipse.pattern.matcher.impl.PatternMatchBranchNode.EltInASTNodeFieldListPatternNodeBranchNode;
import fr.an.eclipse.pattern.matcher.impl.PatternMatchBranchNode.EltInListPatternNodeBranchNode;
import fr.an.eclipse.pattern.matcher.impl.PatternMatchBranchNode.SimplePatternNodeBranchNode;
import fr.an.eclipse.pattern.util.ConsoleUtil;

/**
 *
 */
public class PatternMatchStackFetchVisitor extends RecurseChildPatternMatchVisitor {
	
	protected Deque<PatternMatchBranchNode> currBranchStack = new ArrayDeque<PatternMatchBranchNode>();
	
	protected Map<String,Object> resultGroups = new HashMap<String,Object>();
	
	protected IMatchResult innerResult = new IMatchResult() {
		public Object group(String name) {
			return resultGroups.get(name);
		}
	};
	
	// ------------------------------------------------------------------------

	public PatternMatchStackFetchVisitor() {
	}
	
	// ------------------------------------------------------------------------

	
	public <T> void pushSearchNode(IPattern<T> pattern, T node) {
		PatternMatchBranchNode branchNode = new SimplePatternNodeBranchNode(pattern, node);
		pushBranchNode(branchNode);
	}

	public <T> void pushBranchNode(PatternMatchBranchNode branchNode) {
		currBranchStack.addLast(branchNode);
	}

	public void popBranchNode(PatternMatchBranchNode branchNode) {
		PatternMatchBranchNode removed = currBranchStack.removeLast(); 
		assert removed == branchNode;
	}

	public Map<String, Object> getResultGroups() {
		return resultGroups;
	}

	public PatternMatchBranchNode getCurrBranchNode() {
		return currBranchStack.getLast();
	}
		
	public IMatchResult nextMatch() {
		IMatchResult res = null;
		if (currBranchStack.isEmpty()) return null;
		PatternMatchBranchNode branchNode = currBranchStack.getLast();
		
		boolean hasNext = branchNode.next(this);
		if (hasNext) {
			res = innerResult;
		} else {
			popBranchNode(branchNode);
		}
		
		return res;
	}

	// override all algebra ASTNodePattern with special treatment (groups, branchs ..)
	// ------------------------------------------------------------------------
	
	@Override
	public <T> boolean visitMatch(GroupCapturePattern<T> p, Object node) {
		// override to capture the group ...and continue match underlying node
		resultGroups.put(p.getName(), node);
		return super.visitMatch(p, node);
	}
	
	public <T> boolean visitMatch(WildcardPattern<T> p, Object node) {
		// the "wildward star" accept all nodes...
		return true;
	}
	
	public <T> boolean visitMatch(MatchNotPattern<T> p, Object node) {
		// the "!" NotPattern matches when the underlying pattern does NOT matches
		boolean oppositeRes = p.getOperand().acceptMatch(this, node);
		return !oppositeRes;
	}

	public <T> boolean visitMatch(MatchAndPattern<T> p, Object node) {
		// cf default recurseVisit.. 
//		if (lhs != null && !lhs.visitSwitchTree(v, node)) return false;
//		if (rhs != null && !rhs.visitSwitchTree(v, node)) return false;
		boolean res = p.recursevisitMatch(this, node);
		return res;
	}
	
	public <T> boolean visitMatch(MatchOrPattern<T> p, Object node) {
		// cf default recurseVisit.. 
//		boolean res = false;
//		if (!res) {
//			res = (lhs != null && lhs.visitSwitchTree(v, node));
//		}
//		if (!res) {
//			res = (rhs != null && rhs.visitSwitchTree(v, node));
//		}
		boolean res = p.recursevisitMatch(this, node);
		return res;
	}

	public <T> boolean visitMatch(MatchAllOfPattern<T> p, Object node) {
		// cf default recurseVisit..
		boolean res = p.recursevisitMatch(this, node);
		return res;
	}
	
	public <T> boolean visitMatch(MatchOneOfPattern<T> p, Object node) {
		// cf default recurseVisit..
		boolean res = p.recursevisitMatch(this, node);
		return res;
	}
	
	public <T> boolean visitMatch(MatchOneEltOfListPattern<T> p, List<T> nodeList) {
		// not using default recurse!
		IPattern<T> childPattern = p.getElementPattern();
		EltInListPatternNodeBranchNode<T> childBranchesNode = new EltInListPatternNodeBranchNode<T>(childPattern, nodeList);
		pushBranchNode(childBranchesNode);

		// continue search in (first) child elt
		boolean res = childBranchesNode.next(this);
		
		return res;
	}
	
	public <T> boolean visitMatch(MatchAllEltsOfListPattern<T> p, List<T> node) {
		// cf default recurseVisit..
		boolean res = p.recursevisitMatch(this, node);
		return res;
	}
	

	
	
	@Override
	public <T> boolean visitMatch(WildcardContainingPattern<T> p, Object node) {
		boolean res = false;
		
		// *** The Biggy ****
		// recursive traverse all objects (child, list & fields), and try match EVERYTHING
		// => recursively create PatternMatchBranchNode sub branches for each possible child objects of current object
		if (node == null) {
			// nothing to recurse into... => matches ok
			res = true;
		} else if (node instanceof ASTNode) {
			ASTNode astNode = (ASTNode) node;
			// iterate into every child field of the obect
			// ... (using ASTNode visitor?? InnerWildcardEmbeddedASTNodeFieldsToBranchFactory)
			@SuppressWarnings("unchecked")
			Map<String,StructuralPropertyDescriptor> nodePropertiesMap = astNode.properties();
			List<StructuralPropertyDescriptor> nodeProperties = 
					new ArrayList<StructuralPropertyDescriptor>(nodePropertiesMap.values());
			
			IPattern<T> childPattern = new WildcardContainingPattern<T>(p.getUnderlyingPattern());
			EltInASTNodeFieldListPatternNodeBranchNode<T> childBranchesNode = 
					new EltInASTNodeFieldListPatternNodeBranchNode<T>(childPattern, astNode, nodeProperties); 
			
			// continue search in (first) child
			res = childBranchesNode.next(this);
			
		} else if (node instanceof List) {
			@SuppressWarnings("unchecked")
			List<T> listNode = (List<T>) node;
			IPattern<T> childPattern = new WildcardContainingPattern<T>(p.getUnderlyingPattern());
			EltInListPatternNodeBranchNode<T> childBranchesNode = new EltInListPatternNodeBranchNode<T>(childPattern, listNode);
			pushBranchNode(childBranchesNode);

			// continue search in (first) child
			res = childBranchesNode.next(this);
						
		} else if (node.getClass().getName().startsWith("java.lang.")) { // primitives: String, int, Integer...
			res = true;
		} else {
			throw new IllegalStateException("unrecognized object type '" + node.getClass() + "', should not occur");
		}
		
		return res;
	}

	@Override
	public <T> boolean preVisitMatch(IPattern<T> pattern, T node) {
		getCurrBranchNode().pushPreVisitMatch(pattern, node);
		return true;
	}

	@Override
	public <T> void postVisitMatch(IPattern<T> obj, boolean matchRes, T node) {
		super.postVisitMatch(obj, matchRes, node);
	}

	@Override
	public <T> boolean mismatch(String mismatchMessage) {
		ConsoleUtil.debug("pattern mismatch:" + mismatchMessage);
		return false;
	}
	
	
}
