package fr.an.eclipse.pattern.internal.ui.search;

import org.eclipse.swt.widgets.Shell;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import org.eclipse.search.ui.NewSearchUI;

import org.eclipse.jdt.internal.ui.JavaPlugin;

/**
 * Opens the Search Dialog and brings the Java search page to front
 */
public class OpenPatternSearchPageAction implements IWorkbenchWindowActionDelegate {

	private static final String PATTERN_SEARCH_PAGE_ID= "fr.an.eclipse.pattern.ui.PatternSearchPage"; //$NON-NLS-1$

	private IWorkbenchWindow fWindow;

	public OpenPatternSearchPageAction() {
	}

	public void init(IWorkbenchWindow window) {
		fWindow= window;
	}

	public void run(IAction action) {
		if (fWindow == null || fWindow.getActivePage() == null) {
			beep();
			JavaPlugin.logErrorMessage("Could not open the search dialog - for some reason the window handle was null"); //$NON-NLS-1$
			return;
		}
		NewSearchUI.openSearchDialog(fWindow, PATTERN_SEARCH_PAGE_ID);
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// do nothing since the action isn't selection dependent.
	}

	public void dispose() {
		fWindow= null;
	}

	protected void beep() {
		Shell shell= JavaPlugin.getActiveWorkbenchShell();
		if (shell != null && shell.getDisplay() != null)
			shell.getDisplay().beep();
	}
}
