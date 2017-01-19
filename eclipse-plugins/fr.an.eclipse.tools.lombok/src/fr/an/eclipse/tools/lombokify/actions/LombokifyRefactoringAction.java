package fr.an.eclipse.tools.lombokify.actions;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.internal.ui.refactoring.actions.RefactoringStarter;
import org.eclipse.jdt.ui.refactoring.RefactoringSaveHelper;
import org.eclipse.swt.widgets.Display;

import fr.an.eclipse.pattern.ui.actions.AbstractJavaSelectionAction;
import fr.an.eclipse.tools.lombokify.helpers.LombokifyRefactoring;
import fr.an.eclipse.tools.lombokify.helpers.LombokifyRefactoringWizard;

@SuppressWarnings("restriction")
public class LombokifyRefactoringAction extends AbstractJavaSelectionAction {

	// -------------------------------------------------------------------------
	
	@Override
	protected String getActionTitle() {
		return "recursive Lombokify";
	}

	@Override
	protected void doRun(MultiStatus status, IProgressMonitor monitor, StringBuffer resultMessage, Set<ICompilationUnit> compilationUnits) {
		LombokifyRefactoring refactoring= new LombokifyRefactoring(compilationUnits);
		Display.getDefault().syncExec(() -> { 
			new RefactoringStarter().activate(new LombokifyRefactoringWizard(refactoring), getShell(), "replace interface by impl", RefactoringSaveHelper.SAVE_NOTHING);
		});
	}

	
}
