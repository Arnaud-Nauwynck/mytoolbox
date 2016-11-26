package fr.an.eclipse.tools.refactoring.helpers;


import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

import fr.an.eclipse.tools.refactoring.Activator;

public class Interface2ImplReplaceRefactoringWizard extends RefactoringWizard {

	public Interface2ImplReplaceRefactoringWizard(Interface2ImplReplaceRefactoring ref){
		super(ref, DIALOG_BASED_USER_INTERFACE | PREVIEW_EXPAND_FIRST_NODE);
		setDefaultPageTitle("");
		// setDialogSettings(JavaPlugin.getDefault().getDialogSettings());
	}

	public Change createChange(){
		// creating the change is cheap. So we don't need to show progress.
		try {
			return getRefactoring().createChange(new NullProgressMonitor());
		} catch (CoreException e) {
			Activator.logError("Failed createChange", e);
			return null;
		}
	}

	@Override
	protected void addUserInputPages(){
		addPage(new Interface2ImplReplaceRefactoringInputPage(""));
	}
}
