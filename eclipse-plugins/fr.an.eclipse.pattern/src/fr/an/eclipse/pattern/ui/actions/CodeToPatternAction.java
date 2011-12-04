package fr.an.eclipse.pattern.ui.actions;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.swt.widgets.Display;

import fr.an.eclipse.pattern.helper.CodeToPatternHelper;
import fr.an.eclipse.pattern.util.UiUtil;

public class CodeToPatternAction extends AbstractJavaSelectionAction {


	// -------------------------------------------------------------------------
	
	protected String getActionTitle() {
		return "Code To Pattern";
	}
	
	protected void doRun(MultiStatus status, IProgressMonitor monitor, StringBuffer resultMessage, Set<ICompilationUnit> compilationUnits) {
		CodeToPatternHelper helper = null;
		try {
			List<IJavaElement> jelts = getSelectedJavaElements();
			helper = new CodeToPatternHelper(monitor, compilationUnits, jelts);
			helper.run();
			
			final String result = helper.getResult();
			
			resultMessage.append("done " + getActionTitle() + " :\n " 
					+ result);

			Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                	UiUtil.copyTextToClipboard(getDisplay(), result);
                }
            });

		} catch(Exception ex) {
			resultMessage.append("FAILED refactor " + getActionTitle() + ": " 
					+ "ERROR: " + ex.getMessage()
					+ "\n" + helper.getStringResult());
		}
	}

}
