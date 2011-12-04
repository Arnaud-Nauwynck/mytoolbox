package fr.an.eclipse.pattern.internal.ui.search;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.search.ui.ISearchPageScoreComputer;
import org.eclipse.ui.IEditorInput;

public class PatternSearchPageScoreComputer implements ISearchPageScoreComputer {
	
	public static final String EXTENSION_POINT_ID= "fr.an.eclipse.pattern.ui.PatternSearchPage"; //$NON-NLS-1$

	public int computeScore(String id, Object element) {
		if (!EXTENSION_POINT_ID.equals(id))
			// Can't decide
			return ISearchPageScoreComputer.UNKNOWN;

		if (element instanceof IJavaElement // || element instanceof LogicalPackage
				|| (element instanceof IEditorInput && JavaUI.getEditorInputJavaElement((IEditorInput)element) != null))
			return 95;

		return ISearchPageScoreComputer.LOWEST;
	}
}
