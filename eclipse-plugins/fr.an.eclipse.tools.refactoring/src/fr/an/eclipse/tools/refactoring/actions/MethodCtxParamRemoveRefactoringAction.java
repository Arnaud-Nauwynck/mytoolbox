package fr.an.eclipse.tools.refactoring.actions;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.internal.ui.refactoring.actions.RefactoringStarter;
import org.eclipse.jdt.ui.refactoring.RefactoringSaveHelper;
import org.eclipse.swt.widgets.Display;

import fr.an.eclipse.pattern.ui.actions.AbstractJavaSelectionAction;
import fr.an.eclipse.tools.refactoring.helpers.MethodCtxParamRemoveRefactoring;
import fr.an.eclipse.tools.refactoring.helpers.MethodCtxParamRemoveWizard;

@SuppressWarnings("restriction")
public class MethodCtxParamRemoveRefactoringAction extends AbstractJavaSelectionAction {

	// -------------------------------------------------------------------------
	
	@Override
	protected String getActionTitle() {
		return "recursive remove Ctx param from methods";
	}

	@Override
	protected void doRun(MultiStatus status, IProgressMonitor monitor, StringBuffer resultMessage, Set<ICompilationUnit> compilationUnits) {
		MethodCtxParamRemoveRefactoring refactoring= new MethodCtxParamRemoveRefactoring(compilationUnits);
		Display.getDefault().syncExec(() -> { 
			new RefactoringStarter().activate(new MethodCtxParamRemoveWizard(refactoring), getShell(), "Methods Ctx Param Remove", RefactoringSaveHelper.SAVE_NOTHING);
		});
	}

	
}
