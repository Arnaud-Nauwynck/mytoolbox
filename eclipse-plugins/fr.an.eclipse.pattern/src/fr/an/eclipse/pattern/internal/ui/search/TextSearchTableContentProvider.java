package fr.an.eclipse.pattern.internal.ui.search;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import org.eclipse.search.ui.text.AbstractTextSearchResult;

/**
 * TODO: this class should replace JavaSearchTableContentProvider
 * (must generalize type of fResult to AbstractTextSearchResult in JavaSearchContentProvider)
 */
public class TextSearchTableContentProvider implements IStructuredContentProvider {
	protected final Object[] EMPTY_ARRAY= new Object[0];
	private AbstractTextSearchResult fSearchResult;
	private TableViewer fTableViewer;

	/*
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof AbstractTextSearchResult)
			return ((AbstractTextSearchResult) inputElement).getElements();
		return EMPTY_ARRAY;
	}

	/*
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		//nothing
	}

	/*
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		fTableViewer= (TableViewer) viewer;
		fSearchResult= (AbstractTextSearchResult) newInput;
	}

	public void elementsChanged(Object[] updatedElements) {
		//TODO: copied from JavaSearchTableContentProvider
		for (int i= 0; i < updatedElements.length; i++) {
			if (fSearchResult.getMatchCount(updatedElements[i]) > 0) {
				if (fTableViewer.testFindItem(updatedElements[i]) != null)
					fTableViewer.refresh(updatedElements[i]);
				else
					fTableViewer.add(updatedElements[i]);
			} else {
				fTableViewer.remove(updatedElements[i]);
			}
		}
	}

	public void clear() {
		//TODO: copied from JavaSearchTableContentProvider
		fTableViewer.refresh();
	}
}
