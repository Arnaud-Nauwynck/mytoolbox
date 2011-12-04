package fr.an.eclipse.pattern.helper;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.CompilationUnit;

import fr.an.eclipse.pattern.PatternUIPlugin;
import fr.an.eclipse.pattern.util.JavaASTUtil;

/**
 *
 */
public abstract class AbstractASTCompilationUnitActionHelper extends AbstractCompilationUnitActionHelper {

	// ------------------------------------------------------------------------
	
	protected AbstractASTCompilationUnitActionHelper(IProgressMonitor monitor, Set<ICompilationUnit> compilationUnits) {
		this(monitor, compilationUnits, null);
	}

	protected AbstractASTCompilationUnitActionHelper(IProgressMonitor monitor, Set<ICompilationUnit> compilationUnits, List<IJavaElement> javaElements) {
		super(monitor, compilationUnits, javaElements);
	}
	
	
	// ------------------------------------------------------------------------

	protected void handleCompilationUnit(ICompilationUnit cu) {
		monitor.worked(1);
		monitor.subTask(cu.getElementName());

		try {
			CompilationUnit unit = JavaASTUtil.parseCompilationUnit(cu, monitor);

			currentUnit = (ICompilationUnit) unit.getJavaElement();

			handleUnit(unit);

		} catch(Exception ex) {
			addErrorMsg("Failed to execute refactoring in unit " + cu.getElementName() + ":" + ex.toString());
			PatternUIPlugin.logWarning("Failed to execute refactoring in unit " + cu.getElementName(), ex);
		} catch(Throwable ex) {
			addErrorMsg("Failed to execute refactoring in unit " + cu.getElementName() + ":" + ex.toString());
			PatternUIPlugin.logError("Failed to execute refactoring in unit " + cu.getElementName(), ex);
		} finally {
			currentUnit = null;
		}
	}


	protected abstract void handleUnit(CompilationUnit unit) throws Exception;
	
}
