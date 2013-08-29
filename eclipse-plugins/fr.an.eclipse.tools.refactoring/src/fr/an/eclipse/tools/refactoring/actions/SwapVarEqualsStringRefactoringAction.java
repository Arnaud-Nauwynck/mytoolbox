package fr.an.eclipse.tools.refactoring.actions;


import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jdt.core.ICompilationUnit;

import fr.an.eclipse.pattern.ui.actions.AbstractJavaSelectionAction;
import fr.an.eclipse.tools.refactoring.helpers.SwapVarEqualsStringRefactoringHelper;

public class SwapVarEqualsStringRefactoringAction extends AbstractJavaSelectionAction {


	// -------------------------------------------------------------------------
	
	protected String getActionTitle() {
		return "recursive swap var.equals(string) => string.equals(var)";
	}
	
	protected void doRun(MultiStatus status, IProgressMonitor monitor, StringBuffer resultMessage, Set<ICompilationUnit> compilationUnits) {
		SwapVarEqualsStringRefactoringHelper helper = null;
		try {
			helper = new SwapVarEqualsStringRefactoringHelper(monitor, compilationUnits);
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
