package fr.an.eclipse.pattern.matcher;

import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;

import fr.an.eclipse.pattern.ast.IPattern;

public interface IMatchResultIterator<T> extends Iterator<IMatchResult> {

	public IPattern<T> getParentPattern();
	public T getParentNode();

	
	public Map<String,IPattern<?>> getPatternGroups();

	// TODO ... replacement for hasNext()  :  boolean findNext(IProgressMonitor monitor);

	// cf from Iterator<>
	boolean hasNext();
	IMatchResult next();
	public void remove(); // unused
	
}
