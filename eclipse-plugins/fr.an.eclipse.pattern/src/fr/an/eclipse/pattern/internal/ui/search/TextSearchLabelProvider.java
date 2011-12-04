package fr.an.eclipse.pattern.internal.ui.search;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;

import org.eclipse.search.ui.text.AbstractTextSearchViewPage;

import org.eclipse.jdt.internal.corext.util.Messages;


public abstract class TextSearchLabelProvider extends LabelProvider {

	private AbstractTextSearchViewPage fPage;

	public TextSearchLabelProvider(AbstractTextSearchViewPage page) {
		fPage= page;
	}

	public AbstractTextSearchViewPage getPage() {
		return fPage;
	}

	protected final StyledString getColoredLabelWithCounts(Object element, StyledString coloredName) {
		String name= coloredName.getString();
		String decorated= getLabelWithCounts(element, name);
		if (decorated.length() > name.length()) {
			StyledCellLabelProvider.styleDecoratedString(decorated, StyledString.COUNTER_STYLER, coloredName);
		}
		return coloredName;
	}

	protected final String getLabelWithCounts(Object element, String elementName) {
		int matchCount= fPage.getInput().getMatchCount(element);
		if (matchCount < 2)
			return elementName;

		return Messages.format(PatternSearchMessages.TextSearchLabelProvider_matchCountFormat, new String[] { elementName, String.valueOf(matchCount)});
	}
}
