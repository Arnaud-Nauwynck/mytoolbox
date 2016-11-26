package fr.an.eclipse.tools.refactoring.actions;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.internal.ui.refactoring.actions.RefactoringStarter;
import org.eclipse.jdt.ui.refactoring.RefactoringSaveHelper;
import org.eclipse.swt.widgets.Display;

import fr.an.eclipse.pattern.ui.actions.AbstractJavaSelectionAction;
import fr.an.eclipse.tools.refactoring.helpers.Interface2ImplReplaceRefactoring;
import fr.an.eclipse.tools.refactoring.helpers.Interface2ImplReplaceRefactoringWizard;

@SuppressWarnings("restriction")
public class Interface2ImplReplaceRefactoringAction extends AbstractJavaSelectionAction {

	// -------------------------------------------------------------------------
	
	@Override
	protected String getActionTitle() {
		return "recursive replace interface by impl";
	}

	@Override
	protected void doRun(MultiStatus status, IProgressMonitor monitor, StringBuffer resultMessage, Set<ICompilationUnit> compilationUnits) {
		Interface2ImplReplaceRefactoring refactoring= new Interface2ImplReplaceRefactoring(compilationUnits);
		Display.getDefault().syncExec(() -> { 
			new RefactoringStarter().activate(new Interface2ImplReplaceRefactoringWizard(refactoring), getShell(), "replace interface by impl", RefactoringSaveHelper.SAVE_NOTHING);
		});
	}

	
}
