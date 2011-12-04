package fr.an.eclipse.pattern.internal.ui.search;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IParent;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.search.IMatchPresentation;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.IEditorMatchAdapter;
import org.eclipse.search.ui.text.IFileMatchAdapter;
import org.eclipse.search.ui.text.Match;
import org.eclipse.search.ui.text.MatchFilter;
import org.eclipse.ui.IEditorPart;

public class PatternSearchResult extends AbstractTextSearchResult implements IEditorMatchAdapter, IFileMatchAdapter { 
	

	protected static final Match[] NO_MATCHES= new Match[0];

	private static final MatchFilter[] EMPTY_MATCH_FILTERS = new MatchFilter[0];

	private final PatternSearchQuery fQuery;
	
	private final Map<Object, IMatchPresentation> fElementsToParticipants;

	// ------------------------------------------------------------------------
	
	public PatternSearchResult(PatternSearchQuery query) {
		fQuery= query;
		fElementsToParticipants= new HashMap<Object, IMatchPresentation>();
		setActiveMatchFilters(EMPTY_MATCH_FILTERS);
	}

	// ------------------------------------------------------------------------
	


	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.text.IEditorMatchAdapter#computeContainedMatches(org.eclipse.search.ui.text.AbstractTextSearchResult, org.eclipse.ui.IEditorPart)
	 */
	public Match[] computeContainedMatches(AbstractTextSearchResult result, IEditorPart editor) {
		return computeContainedMatches(editor.getEditorInput());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.text.IEditorMatchAdapter#computeContainedMatches(org.eclipse.search.ui.text.AbstractTextSearchResult, org.eclipse.ui.IEditorPart)
	 */
	public Match[] computeContainedMatches(AbstractTextSearchResult result, IFile file) {
		return computeContainedMatches(file);
	}

	private Match[] computeContainedMatches(IAdaptable adaptable) {
		IJavaElement javaElement= (IJavaElement) adaptable.getAdapter(IJavaElement.class);
		Set<Match> matches= new HashSet<Match>();
		if (javaElement != null) {
			collectMatches(matches, javaElement);
		}
		IFile file= (IFile) adaptable.getAdapter(IFile.class);
		if (file != null) {
			collectMatches(matches, file);
		}
		if (!matches.isEmpty()) {
			return matches.toArray(new Match[matches.size()]);
		}
		return NO_MATCHES;
	}

	private void collectMatches(Set<Match> matches, IFile element) {
		Match[] m= getMatches(element);
		if (m.length != 0) {
			for (int i= 0; i < m.length; i++) {
				matches.add(m[i]);
			}
		}
	}

	private void collectMatches(Set<Match> matches, IJavaElement element) {
		Match[] m= getMatches(element);
		if (m.length != 0) {
			for (int i= 0; i < m.length; i++) {
				matches.add(m[i]);
			}
		}
		if (element instanceof IParent) {
			IParent parent= (IParent) element;
			try {
				IJavaElement[] children= parent.getChildren();
				for (int i= 0; i < children.length; i++) {
					collectMatches(matches, children[i]);
				}
			} catch (JavaModelException e) {
				// we will not be tracking these results
			}
		}
	}
	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchResultCategory#getFile(java.lang.Object)
	 */
	public IFile getFile(Object element) {
		if (element instanceof IJavaElement) {
			IJavaElement javaElement= (IJavaElement) element;
			ICompilationUnit cu= (ICompilationUnit) javaElement.getAncestor(IJavaElement.COMPILATION_UNIT);
			if (cu != null) {
				return (IFile) cu.getResource();
			} else {
				IClassFile cf= (IClassFile) javaElement.getAncestor(IJavaElement.CLASS_FILE);
				if (cf != null)
					return (IFile) cf.getResource();
			}
			return null;
		}
		if (element instanceof IFile)
			return (IFile) element;
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.text.IEditorMatchAdapter#isShownInEditor(org.eclipse.search.ui.text.Match, org.eclipse.ui.IEditorPart)
	 */
	public boolean isShownInEditor(Match match, IEditorPart editor) {
		Object element= match.getElement();
		if (element instanceof IJavaElement) {
			element= ((IJavaElement) element).getOpenable(); // class file or compilation unit
			return element != null && element.equals(editor.getEditorInput().getAdapter(IJavaElement.class));
		} else if (element instanceof IFile) {
			return element.equals(editor.getEditorInput().getAdapter(IFile.class));
		}
		return false;
	}

	@Override
	public IFileMatchAdapter getFileMatchAdapter() {
		return this;
	}

	@Override
	public IEditorMatchAdapter getEditorMatchAdapter() {
		return this;
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchResult#getImageDescriptor()
	 */
	public ImageDescriptor getImageDescriptor() {
		return fQuery.getImageDescriptor();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchResult#getLabel()
	 */
	public String getLabel() {
		return fQuery.getResultLabel(getMatchCount());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchResult#getTooltip()
	 */
	public String getTooltip() {
		return getLabel();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.text.AbstractTextSearchResult#setMatchFilters(org.eclipse.search.ui.text.MatchFilter[])
	 */
	@Override
	public void setActiveMatchFilters(MatchFilter[] filters) {
		super.setActiveMatchFilters(filters);
		// PatternMatchFilter.setLastUsedFilters(filters);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.text.AbstractTextSearchResult#getAllMatchFilters()
	 */
	@Override
	public MatchFilter[] getAllMatchFilters() {
		return EMPTY_MATCH_FILTERS;
		// return PatternMatchFilter.allFilters(fQuery);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchResult#getQuery()
	 */
	public ISearchQuery getQuery() {
		return fQuery;
	}

	synchronized IMatchPresentation getSearchParticpant(Object element) {
		return fElementsToParticipants.get(element);
	}

	@Override
	public void removeAll() {
		synchronized(this) {
			fElementsToParticipants.clear();
		}
		super.removeAll();
	}

	@Override
	public void removeMatch(Match match) {
		synchronized(this) {
			if (getMatchCount(match.getElement()) == 1)
				fElementsToParticipants.remove(match.getElement());
		}
		super.removeMatch(match);
	}
	
}
