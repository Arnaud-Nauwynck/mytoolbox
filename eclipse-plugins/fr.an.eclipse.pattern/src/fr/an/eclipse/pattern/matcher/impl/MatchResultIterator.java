package fr.an.eclipse.pattern.matcher.impl;

import java.util.HashMap;
import java.util.Map;

import fr.an.eclipse.pattern.ast.IPattern;
import fr.an.eclipse.pattern.ast.PatternVisitor;
import fr.an.eclipse.pattern.ast.impl.StdAlgebraPatterns.GroupCapturePattern;
import fr.an.eclipse.pattern.ast.utils.RecurseChildPatternVisitor;
import fr.an.eclipse.pattern.matcher.IMatchResult;
import fr.an.eclipse.pattern.matcher.IMatchResultIterator;

/**
 *
 * @param <T>
 */
public class MatchResultIterator<T> implements IMatchResultIterator<T> {

	private IPattern<T> parentPattern;
	private T parentNode;
	
	private Map<String,IPattern<?>> patternGroups;

	private PatternMatchStackFetchVisitor patternVisitor;
	
	private IMatchResult currMatchRes;
	private boolean nextMatchResFetched;
	private IMatchResult nextMatchRes;
	
	// ------------------------------------------------------------------------
	

	public MatchResultIterator(IPattern<T> parentPattern, T parentNode) {
		super();
		this.parentPattern = parentPattern;
		this.parentNode = parentNode;
		this.patternGroups = compileFindGroups();
		this.patternVisitor = new PatternMatchStackFetchVisitor();
		patternVisitor.pushSearchNode(parentPattern, parentNode);
	}
	
	
	// ------------------------------------------------------------------------

	private Map<String, IPattern<?>> compileFindGroups() {
		final Map<String, IPattern<?>> res = new HashMap<String, IPattern<?>>();
		PatternVisitor visitor = new RecurseChildPatternVisitor() {
			public <TT> void visit(GroupCapturePattern<TT> p) {
				res.put(p.getName(), p);
				super.visit(p);
			}
		};
		parentPattern.accept(visitor);
		return res;
	}


	public IPattern<T> getParentPattern() {
		return parentPattern;
	}

	public T getParentNode() {
		return parentNode;
	}
	
	
	@Override
	public Map<String, IPattern<?>> getPatternGroups() {
		return patternGroups;
	}


	@Override
	public boolean hasNext() {
		if (!nextMatchResFetched) {
			// *** The Bigggy ***
			nextMatchRes = patternVisitor.nextMatch();
			
			nextMatchResFetched = true;
		}
		return (nextMatchRes != null);
	}

	@Override
	public IMatchResult next() {
		if (!nextMatchResFetched) {
			hasNext();
		}
		currMatchRes = nextMatchRes;
		nextMatchResFetched = false;
		nextMatchRes = null;
		return currMatchRes;
	}


	@Override
	public void remove() {
		throw new IllegalArgumentException();
	}


}
