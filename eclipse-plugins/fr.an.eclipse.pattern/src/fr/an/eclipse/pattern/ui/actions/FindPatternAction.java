package fr.an.eclipse.pattern.ui.actions;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.action.IAction;

import fr.an.eclipse.pattern.ast.IPattern;
import fr.an.eclipse.pattern.ast.utils.PatternXStreamUtils;
import fr.an.eclipse.pattern.helper.FindPatternHelper;
import fr.an.eclipse.pattern.util.UiUtil;

public class FindPatternAction extends AbstractJavaSelectionAction {

	private IPattern<?> pattern;

	// -------------------------------------------------------------------------
	
	protected String getActionTitle() {
		return "Code Pattern: find pattern";
	}
	
	public void run(IAction action) {
		String patternXml = UiUtil.askTextFileContent("samples/pattern.xml");
		this.pattern = (IPattern<?>) PatternXStreamUtils.snewXStream().fromXML(patternXml);
		
		super.run(action);
	}
	
	protected void doRun(MultiStatus status, IProgressMonitor monitor, StringBuffer resultMessage, Set<ICompilationUnit> compilationUnits) {
		FindPatternHelper helper = null;
		try {
			List<IJavaElement> javaElements = getSelectedJavaElements();
			
			helper = new FindPatternHelper(monitor, compilationUnits, javaElements, pattern);
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
