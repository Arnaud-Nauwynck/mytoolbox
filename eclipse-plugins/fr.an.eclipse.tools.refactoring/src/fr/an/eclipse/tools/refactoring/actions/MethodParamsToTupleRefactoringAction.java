package fr.an.eclipse.tools.refactoring.actions;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jdt.core.ICompilationUnit;

import fr.an.eclipse.pattern.ui.actions.AbstractJavaSelectionAction;
import fr.an.eclipse.tools.refactoring.helpers.MethodParamsToTupleRefactoringHelper;

public class MethodParamsToTupleRefactoringAction extends AbstractJavaSelectionAction {


	// -------------------------------------------------------------------------
	
	protected String getActionTitle() {
		return "recursive JAX-RS method(param1,param2,..) -> method(new MethodRequest(param1,param2..))";
	}
	
	protected void doRun(MultiStatus status, IProgressMonitor monitor, StringBuffer resultMessage, Set<ICompilationUnit> compilationUnits) {
		MethodParamsToTupleRefactoringHelper helper = null;
		try {
			helper = new MethodParamsToTupleRefactoringHelper(monitor, compilationUnits);
			helper.run();
			
			resultMessage.append("done " + getActionTitle() + ": " 
					+ helper.getStringResult());
		} catch(Exception ex) {
			resultMessage.append("FAILED refactor " + getActionTitle() + ": " 
					+ "ERROR: " + ex.getMessage()
					+ "\n" + helper.getStringResult());
		}
	}

	
}
