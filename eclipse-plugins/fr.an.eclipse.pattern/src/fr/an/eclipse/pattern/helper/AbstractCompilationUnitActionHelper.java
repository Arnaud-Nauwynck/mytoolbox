package fr.an.eclipse.pattern.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.ui.IWorkbenchWindow;

import fr.an.eclipse.pattern.util.JavaElementUtil;

/**
 * 
 */
public abstract class AbstractCompilationUnitActionHelper {

	protected static class CancelRefactorUnitException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		public CancelRefactorUnitException(String msg) {
			super(msg);
		}
	}

	protected List<IJavaElement> javaElements;
	protected Set<ICompilationUnit> compilationUnits;
	private int countCompilationUnit;
	private int errorCount;
	protected int countReplacement;
	protected StringBuffer messages = new StringBuffer();
	protected ICompilationUnit currentUnit;
	protected IProgressMonitor monitor;
	protected IWorkbenchWindow window;

	// ------------------------------------------------------------------------
	
	protected AbstractCompilationUnitActionHelper(IProgressMonitor monitor, Set<ICompilationUnit> compilationUnits, List<IJavaElement> javaElements) {
		this.monitor = monitor;
		this.compilationUnits = compilationUnits;
		this.javaElements = javaElements;
	}
	
	// ------------------------------------------------------------------------
	

	public String getStringResult() {
		return "count " 
		+ " files=" + countCompilationUnit
		// + ", replacements=" + countReplacement
		+ ", errors=" + errorCount
		+ " messages=" + messages;
	}

	protected void addMsg(String msg) {
		messages.append(currentUnit.getPath() + " ");
		messages.append(msg);
		messages.append("\n");
	}
	
	protected void addErrorMsg(String msg) {
		errorCount++;
		messages.append(currentUnit.getPath() + " ");
		messages.append(msg);
		messages.append("\n");
	}
	

	protected RuntimeException cancelDiscardRefactorUnit(String msg) {
		addMsg(msg);
		throw new CancelRefactorUnitException(msg);
	}

	public void run() throws Exception {
		try {
	
			monitor.setTaskName("scanning Compilation Units");
			
			onPreRun();
			
			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}
			
			// *** the biggy ***
			monitor.beginTask("treating files", compilationUnits.size());
			
			for(ICompilationUnit unit : compilationUnits) {
				// monitor.setTaskName(unit.getElementName());
				
				if (isAcceptCompilationUnit(unit)) {
					countCompilationUnit++;
					
					IProgressMonitor subProgress = 
						new NullProgressMonitor();
						// new SubProgressMonitor(monitor, 100);  ///??? does not work... blink on screen, display last level?
					IProgressMonitor oldMonitor = monitor;
					monitor = subProgress; // HACK...
					try {
						
						handleCompilationUnit(unit);
						
					} finally {
						monitor = oldMonitor; // RESTORE from HACK...
					}
				}
				
				if (monitor.isCanceled()) {
					throw new OperationCanceledException();
				}
				
				monitor.worked(1);
			}
			
		} finally {
			onFinishRun();
		}
	}

	/** overrideable */
	protected void onPreRun() {
	}

	/** overrideable */
	protected void onFinishRun() {
	}


	protected boolean isAcceptCompilationUnit(ICompilationUnit cu) {
		return true;
	}
	
	
	protected abstract void handleCompilationUnit(ICompilationUnit cu);

	public List<IJavaElement> getJavaElements() {
		return javaElements;
	}
	
	public List<IJavaElement> getJavaElementsForCompilationUnit(ICompilationUnit cu) {
		List<IJavaElement> res = new ArrayList<IJavaElement>(); 
		for(IJavaElement jelt : javaElements) {
			if (cu == JavaElementUtil.findFirstAncestorOfType(jelt, ICompilationUnit.class)) {
				res.add(jelt);
			}
		}
		return res;
	}
	
}