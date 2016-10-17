package fr.an.eclipse.pattern.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.CompilationUnit;

import fr.an.eclipse.pattern.PatternUIPlugin;
import fr.an.eclipse.pattern.util.JavaASTUtil;
import fr.an.eclipse.pattern.util.JavaElementUtil;

/**
 * 
 */
public abstract class AbstractParsedCompilationUnitsActionHelper {

	protected IProgressMonitor monitor;

	protected List<IJavaElement> javaElements;
	protected Set<ICompilationUnit> compilationUnits;
	private int countCompilationUnit;
	
	private int errorCount;
	protected StringBuilder messages = new StringBuilder();
	protected ICompilationUnit currentUnit;
		
	// ------------------------------------------------------------------------
	
	protected AbstractParsedCompilationUnitsActionHelper(IProgressMonitor monitor, Set<ICompilationUnit> compilationUnits, List<IJavaElement> javaElements) {
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
		if (currentUnit != null) {
			messages.append(currentUnit.getPath() + " ");
		}
		messages.append(msg);
		messages.append("\n");
	}
	
	protected void addErrorMsg(String msg) {
		errorCount++;
		if (currentUnit != null) {
			messages.append(currentUnit.getPath() + " ");
		}
		messages.append(msg);
		messages.append("\n");
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
			
			// Step 1 / 4: handle prepare compilation units
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
			
		} catch(Exception ex) {
			addErrorMsg("Failed :" + ex.toString());
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
	
	
	protected void handleCompilationUnit(ICompilationUnit cu) {
		monitor.worked(1);
		monitor.subTask(cu.getElementName());

		try {
			CompilationUnit unit = JavaASTUtil.parseCompilationUnit(cu, monitor);

			currentUnit = (ICompilationUnit) unit.getJavaElement();

			handleUnit(unit);

		} catch(Exception ex) {
			addErrorMsg("Failed to handle unit " + cu.getElementName() + ":" + ex.toString());
			PatternUIPlugin.logWarning("Failed to handle unit " + cu.getElementName(), ex);
		} catch(Throwable ex) {
			addErrorMsg("Failed to handle unit " + cu.getElementName() + ":" + ex.toString());
			PatternUIPlugin.logError("Failed to handle unit " + cu.getElementName(), ex);
		} finally {
			currentUnit = null;
		}
	}


	protected abstract void handleUnit(CompilationUnit unit) throws Exception;
	

	
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