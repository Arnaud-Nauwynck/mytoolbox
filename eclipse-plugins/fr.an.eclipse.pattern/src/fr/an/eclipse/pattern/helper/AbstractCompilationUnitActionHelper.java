package fr.an.eclipse.pattern.helper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.ui.IWorkbenchWindow;

import fr.an.eclipse.pattern.PatternUIPlugin;
import fr.an.eclipse.pattern.util.ConsoleUtil;
import fr.an.eclipse.pattern.util.JavaASTUtil;
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
	protected StringBuilder messages = new StringBuilder();
	protected ICompilationUnit currentUnit;
	protected IProgressMonitor monitor;
	protected IWorkbenchWindow window;

	protected static class CURefactoring {
		// WeakReference<CompilationUnit> cu;
		CompilationUnit cu;
		List<CURefactoringCallback<?>> callbacks = new ArrayList<>();
	}
	
	protected static class CURefactoringCallback<T> {
		BiConsumer<CompilationUnit,T> updateCallback;
		T preparedResult;
		
		public CURefactoringCallback(BiConsumer<CompilationUnit, T> updateCallback, T preparedResult) {
			this.updateCallback = updateCallback;
			this.preparedResult = preparedResult;
		}
	}
	
		
	protected static class DependentCURefactoring {
		WeakReference<CompilationUnit> cu;
		List<DependentRefactoringCallback<?>> callbacks = new ArrayList<>();
		
	}
	protected static class DependentRefactoringCallback<T> {
		Function<CompilationUnit,T> prepareCallback;
		BiConsumer<CompilationUnit,T> updateCallback;
		Object preparedResult;
		
		public DependentRefactoringCallback(Function<CompilationUnit,T> prepareCallback,
				BiConsumer<CompilationUnit,T> updateCallback) {
			this.prepareCallback = prepareCallback;
			this.updateCallback = updateCallback;
		}
		
	}
	
	protected Map<ICompilationUnit,CURefactoring> compilationUnitRefactorings = new LinkedHashMap<>();
	
	protected Map<ICompilationUnit,DependentCURefactoring> dependentCompilationUnitRefactorings = new LinkedHashMap<>();
	
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
			
			// Step 2 / 4 : prepare dependentCompilationUnitRefactorings
			if (! dependentCompilationUnitRefactorings.isEmpty()) {
				// step 1: prepare all dependency refactoring
				for(Map.Entry<ICompilationUnit,DependentCURefactoring> depCUTreatment : dependentCompilationUnitRefactorings.entrySet()) {
					ICompilationUnit depICU = depCUTreatment.getKey();
					DependentCURefactoring depCURefactoring = depCUTreatment.getValue();
					List<DependentRefactoringCallback<?>> depCallbacks = depCURefactoring.callbacks;
					
					CompilationUnit depCU;
					try {
						depCU = JavaASTUtil.parseCompilationUnit(depICU, monitor);
						depCURefactoring.cu = new WeakReference<>(depCU);
					} catch(Exception ex) {
						addErrorMsg("Failed to parse dependent unit " + depICU.getElementName() + ":" + ex.toString());
						continue;
					} catch(Throwable ex) {
						addErrorMsg("Failed to parse dependent unit " + depICU.getElementName() + ":" + ex.toString());
						continue;
					}
					
					for(DependentRefactoringCallback<?> depCallback : depCallbacks) {
						try {
							// *** prepare dependent callback ***
							depCallback.preparedResult = depCallback.prepareCallback.apply(depCU);
							
						} catch(Exception ex) {
							addErrorMsg("Failed to execute refactoring in unit " + depICU.getElementName() + ":" + ex.toString());
							PatternUIPlugin.logWarning("Failed to execute refactoring in unit " + depICU.getElementName(), ex);
						} catch(Throwable ex) {
							addErrorMsg("Failed to execute refactoring in unit " + depICU.getElementName() + ":" + ex.toString());
							PatternUIPlugin.logError("Failed to execute refactoring in unit " + depICU.getElementName(), ex);
						}
					
						if (monitor.isCanceled()) {
							throw new OperationCanceledException();
						}
					}
				}
			}
			
			
			
			// Step 3 / 4: update callbacks
			for(Map.Entry<ICompilationUnit,CURefactoring> e : compilationUnitRefactorings.entrySet()) { 
				CURefactoring cuRefactoring = e.getValue();
				
				CompilationUnit cu = cuRefactoring.cu; // .get();
				
				cu.recordModifications();

				for(CURefactoringCallback<?> cuCallback : cuRefactoring.callbacks) {
					Object prepared = cuCallback.preparedResult;
					@SuppressWarnings("unchecked")
					BiConsumer<CompilationUnit,Object> updateCallback2 = (BiConsumer<CompilationUnit,Object>) cuCallback.updateCallback;
					
					// *** The biggy ***
					updateCallback2.accept(cu, prepared);
				}

				countReplacement++;
		
				JavaASTUtil.uiRewriteASTDocument(monitor, cu);
			}


			
			// step 4 / 4: dependency update callback
			if (! dependentCompilationUnitRefactorings.isEmpty()) {
				
				for(Map.Entry<ICompilationUnit,DependentCURefactoring> depCUTreatment : dependentCompilationUnitRefactorings.entrySet()) {
					ICompilationUnit depICU = depCUTreatment.getKey();
					DependentCURefactoring depCURefactoring = depCUTreatment.getValue();
					List<DependentRefactoringCallback<?>> depCallbacks = depCURefactoring.callbacks;
					
					CompilationUnit depCU = depCURefactoring.cu.get();
					depCURefactoring.cu.clear();
					if (depCU == null) {
						try {
							depCU = JavaASTUtil.parseCompilationUnit(depICU, monitor);
						} catch(Exception ex) {
							addErrorMsg("Failed to parse dependent unit " + depICU.getElementName() + ":" + ex.toString());
							continue;
						} catch(Throwable ex) {
							addErrorMsg("Failed to parse dependent unit " + depICU.getElementName() + ":" + ex.toString());
							continue;
						}
					}
					
					depCU.recordModifications();
					
					for(DependentRefactoringCallback<?> depCallback : depCallbacks) {
						try {
							@SuppressWarnings("unchecked")
							BiConsumer<CompilationUnit,Object> updateCallback2 = (BiConsumer<CompilationUnit,Object>) depCallback.updateCallback;
							
							// *** update dependent callback ***
							updateCallback2.accept(depCU, depCallback.preparedResult);
							
						} catch(Exception ex) {
							addErrorMsg("Failed to execute refactoring in unit " + depICU.getElementName() + ":" + ex.toString());
							PatternUIPlugin.logWarning("Failed to execute refactoring in unit " + depICU.getElementName(), ex);
						} catch(Throwable ex) {
							addErrorMsg("Failed to execute refactoring in unit " + depICU.getElementName() + ":" + ex.toString());
							PatternUIPlugin.logError("Failed to execute refactoring in unit " + depICU.getElementName(), ex);
						}
						
						if (monitor.isCanceled()) {
							throw new OperationCanceledException();
						}
					}
					
					JavaASTUtil.uiRewriteASTDocument(monitor, depCU);
				}
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
	

	
	public List<IJavaElement> getJavaElements() {
		return javaElements;
	}
	
	protected <T> void addCompilationUnitTreatment(CompilationUnit cu, 
			BiConsumer<CompilationUnit,T> updateCallback,
			T preparedResult
			) {
		CURefactoring tmp = compilationUnitRefactorings.get(cu);
		if (tmp == null) {
			tmp = new CURefactoring();
			tmp.cu = cu; // new WeakReference<>(cu);
			compilationUnitRefactorings.put((ICompilationUnit) cu.getJavaElement(), tmp);
		}
		tmp.callbacks.add(new CURefactoringCallback<T>(updateCallback, preparedResult));
	}
	
	protected <T> void addDependentCompilationUnitTreatment(ICompilationUnit cu, 
			Function<CompilationUnit,T> prepareCallback,
			BiConsumer<CompilationUnit,T> updateCallback) {
		DependentCURefactoring tmp = dependentCompilationUnitRefactorings.get(cu);
		if (tmp == null) {
			tmp = new DependentCURefactoring();
			dependentCompilationUnitRefactorings.put(cu, tmp);
		}
		tmp.callbacks.add(new DependentRefactoringCallback<T>(prepareCallback, updateCallback));
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