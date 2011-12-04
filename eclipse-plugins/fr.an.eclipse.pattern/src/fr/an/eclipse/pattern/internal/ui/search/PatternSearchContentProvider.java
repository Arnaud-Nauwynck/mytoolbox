package fr.an.eclipse.pattern.internal.ui.search;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import org.eclipse.search.ui.text.AbstractTextSearchResult;

public abstract class PatternSearchContentProvider implements IStructuredContentProvider {
	
	protected final Object[] EMPTY_ARR= new Object[0];

	private PatternSearchResult fResult;
	
	private PatternSearchResultPage fPage;

	// ------------------------------------------------------------------------
	
	PatternSearchContentProvider(PatternSearchResultPage page) {
		fPage= page;
	}

	// ------------------------------------------------------------------------
	
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		initialize((PatternSearchResult) newInput);

	}

	protected void initialize(PatternSearchResult result) {
		fResult= result;
	}

	public abstract void elementsChanged(Object[] updatedElements);
	public abstract void clear();

	public void dispose() {
		// nothing to do
	}

	PatternSearchResultPage getPage() {
		return fPage;
	}

	PatternSearchResult getSearchResult() {
		return fResult;
	}

}
