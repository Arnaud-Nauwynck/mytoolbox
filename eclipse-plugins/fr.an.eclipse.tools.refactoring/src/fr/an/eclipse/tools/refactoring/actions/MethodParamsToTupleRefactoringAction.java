package fr.an.eclipse.tools.refactoring.actions;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.internal.ui.refactoring.actions.RefactoringStarter;
import org.eclipse.jdt.ui.refactoring.RefactoringSaveHelper;
import org.eclipse.swt.widgets.Display;

import fr.an.eclipse.pattern.ui.actions.AbstractJavaSelectionAction;
import fr.an.eclipse.tools.refactoring.helpers.MethodParamsToTupleRefactoring;
import fr.an.eclipse.tools.refactoring.helpers.MethodParamsToTupleWizard;

@SuppressWarnings("restriction")
public class MethodParamsToTupleRefactoringAction extends AbstractJavaSelectionAction {

	// -------------------------------------------------------------------------
	
	@Override
	protected String getActionTitle() {
		return "recursive JAX-RS method(param1,param2,..) -> method(new MethodRequest(param1,param2..))";
	}

	@Override
	protected void doRun(MultiStatus status, IProgressMonitor monitor, StringBuffer resultMessage, Set<ICompilationUnit> compilationUnits) {
		MethodParamsToTupleRefactoring refactoring= new MethodParamsToTupleRefactoring(compilationUnits);
		Display.getDefault().syncExec(() -> { 
			new RefactoringStarter().activate(new MethodParamsToTupleWizard(refactoring), getShell(), "JAX-RS method params to Request", RefactoringSaveHelper.SAVE_NOTHING);
		});
	}

	
}
