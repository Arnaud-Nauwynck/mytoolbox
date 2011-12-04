package fr.an.eclipse.pattern.internal.ui.search;

import org.eclipse.jface.action.Action;


public class GroupAction extends Action {
	private int fGrouping;
	private PatternSearchResultPage fPage;

	public GroupAction(String label, String tooltip, PatternSearchResultPage page, int grouping) {
		super(label);
		setToolTipText(tooltip);
		fPage= page;
		fGrouping= grouping;
	}

	@Override
	public void run() {
		fPage.setGrouping(fGrouping);
	}

	public int getGrouping() {
		return fGrouping;
	}
}
