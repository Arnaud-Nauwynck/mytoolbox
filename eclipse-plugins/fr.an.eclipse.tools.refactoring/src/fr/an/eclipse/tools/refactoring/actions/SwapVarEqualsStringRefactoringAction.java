package fr.an.eclipse.tools.refactoring.actions;


import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.internal.ui.refactoring.actions.RefactoringStarter;
import org.eclipse.jdt.ui.refactoring.RefactoringSaveHelper;
import org.eclipse.swt.widgets.Display;

import fr.an.eclipse.pattern.ui.actions.AbstractJavaSelectionAction;
import fr.an.eclipse.tools.refactoring.helpers.SwapVarEqualsStringRefactoring;
import fr.an.eclipse.tools.refactoring.helpers.SwapVarEqualsStringRefactoringWizard;

@SuppressWarnings("restriction")
public class SwapVarEqualsStringRefactoringAction extends AbstractJavaSelectionAction {

	// -------------------------------------------------------------------------
	
	protected String getActionTitle() {
		return "recursive swap var.equals(string) => string.equals(var)";
	}
	
	protected void doRun(MultiStatus status, IProgressMonitor monitor, StringBuffer resultMessage, Set<ICompilationUnit> compilationUnits) {
		SwapVarEqualsStringRefactoring refactoring= new SwapVarEqualsStringRefactoring(compilationUnits);
		Display.getDefault().syncExec(() -> { 
			new RefactoringStarter().activate(new SwapVarEqualsStringRefactoringWizard(refactoring), getShell(), "swap var.equals(\"string\")", RefactoringSaveHelper.SAVE_NOTHING);
		});
	}

	
}
