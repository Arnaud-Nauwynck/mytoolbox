package fr.an.eclipse.pattern.internal.ui.search;

import org.eclipse.swt.custom.BusyIndicator;

import org.eclipse.jface.action.Action;


public class SortAction extends Action {
	private int fSortOrder;
	private PatternSearchResultPage fPage;

	public SortAction(String label, PatternSearchResultPage page, int sortOrder) {
		super(label);
		fPage= page;
		fSortOrder= sortOrder;
	}

	@Override
	public void run() {
		BusyIndicator.showWhile(fPage.getViewer().getControl().getDisplay(), new Runnable() {
			public void run() {
				fPage.setSortOrder(fSortOrder);
			}
		});
	}

	public int getSortOrder() {
		return fSortOrder;
	}
}
