package fr.an.eclipse.tools.refactoring.helpers;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.refactoring.CompilationUnitChange;
import org.eclipse.jface.text.Document;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.text.edits.TextEdit;

import fr.an.eclipse.pattern.PatternUIPlugin;
import fr.an.eclipse.pattern.util.JavaASTUtil;

/**
 * 
 */
public abstract class AbstractParsedCompilationUnitsRefactoring extends Refactoring {

	protected static class CancelRefactorUnitException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		public CancelRefactorUnitException(String msg) {
			super(msg);
		}
	}

	protected Set<ICompilationUnit> compilationUnits;

	private int countCompilationUnit;
	private int errorCount;
	protected int countReplacement;
	protected StringBuilder messages = new StringBuilder();
	protected ICompilationUnit currentUnit;

	protected static class CURefactoring {
		// WeakReference<CompilationUnit> cu;
		CompilationUnit cu;
		List<CURefactoringCallback<?>> callbacks = new ArrayList<>();
	}
	
	protected static class CURefactoringCallback<T> {
		T preparedResult;
		BiConsumer<CompilationUnit,T> updateCallback;
		
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
		T preparedResult;
		
		public DependentRefactoringCallback(Function<CompilationUnit,T> prepareCallback,
				BiConsumer<CompilationUnit,T> updateCallback) {
			this.prepareCallback = prepareCallback;
			this.updateCallback = updateCallback;
		}
		
	}
	
	protected Map<ICompilationUnit,CURefactoring> compilationUnitRefactorings = new LinkedHashMap<>();
	
	protected Map<ICompilationUnit,DependentCURefactoring> dependentCompilationUnitRefactorings = new LinkedHashMap<>();
	
	// ------------------------------------------------------------------------
	
	protected AbstractParsedCompilationUnitsRefactoring(Set<ICompilationUnit> compilationUnits) {
		this.compilationUnits = compilationUnits;
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


	@Override
	public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		return new RefactoringStatus(); // RefactoringStatus.createInfoStatus("checkInitialConditions ok?");
	}

	@Override
	public RefactoringStatus checkFinalConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		return new RefactoringStatus(); // RefactoringStatus.createInfoStatus("checkFinalConditions ok?");
	}
	
	@Override
	public Change createChange(IProgressMonitor monitor) throws CoreException {
		CompositeChange res = new CompositeChange(getName());
		final String changeName = getName();
		try {
			monitor.setTaskName("scanning Compilation Units");
			
			onPreRun();
			
			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}
			
			// *** the biggy ***
			monitor.beginTask("treating files", compilationUnits.size());
			
			// Step 1 / 4: handle prepare compilation units
			for(ICompilationUnit icu : compilationUnits) {
				// monitor.setTaskName(unit.getElementName());
				
				if (isAcceptCompilationUnit(icu)) {
					countCompilationUnit++;
					
					monitor.worked(1);
					monitor.subTask(icu.getElementName());

					try {
						CompilationUnit unit = JavaASTUtil.parseCompilationUnit(icu, monitor);

						currentUnit = (ICompilationUnit) unit.getJavaElement();

						handleUnit(unit);

					} catch(Exception ex) {
						addErrorMsg("Failed to execute refactoring in unit " + icu.getElementName() + ":" + ex.toString());
						PatternUIPlugin.logWarning("Failed to execute refactoring in unit " + icu.getElementName(), ex);
					} catch(Throwable ex) {
						addErrorMsg("Failed to execute refactoring in unit " + icu.getElementName() + ":" + ex.toString());
						PatternUIPlugin.logError("Failed to execute refactoring in unit " + icu.getElementName(), ex);
					} finally {
						currentUnit = null;
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
						@SuppressWarnings("unchecked")
						DependentRefactoringCallback<Object> depCallback2 = (DependentRefactoringCallback<Object>) depCallback;
						try {
							// *** prepare dependent callback ***
							depCallback2.preparedResult = depCallback2.prepareCallback.apply(depCU);
							
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
				ICompilationUnit icu = e.getKey();
				CURefactoring cuRefactoring = e.getValue();
				CompilationUnit cu = cuRefactoring.cu; // .get();
				
				String source = icu.getSource();
				Document document = new Document(source);
				   
				cu.recordModifications();

				for(CURefactoringCallback<?> cuCallback : cuRefactoring.callbacks) {
					Object prepared = cuCallback.preparedResult;
					@SuppressWarnings("unchecked")
					BiConsumer<CompilationUnit,Object> updateCallback2 = (BiConsumer<CompilationUnit,Object>) cuCallback.updateCallback;
					
					// *** The biggy ***
					updateCallback2.accept(cu, prepared);
				}

				CompilationUnitChange cuChange = new CompilationUnitChange(changeName, icu);
				TextEdit textEdit = cu.rewrite(document, icu.getJavaProject().getOptions(true));
				cuChange.setEdit(textEdit);
				
				res.add(cuChange);
				
				countReplacement++;
			}

			
			// step 4 / 4: dependency update callback
			if (! dependentCompilationUnitRefactorings.isEmpty()) {
				
				for(Map.Entry<ICompilationUnit,DependentCURefactoring> depCUTreatment : dependentCompilationUnitRefactorings.entrySet()) {
					ICompilationUnit icu = depCUTreatment.getKey();
					DependentCURefactoring depCURefactoring = depCUTreatment.getValue();
					List<DependentRefactoringCallback<?>> depCallbacks = depCURefactoring.callbacks;
					
					CompilationUnit cu = depCURefactoring.cu.get();
					depCURefactoring.cu.clear();
					if (cu == null) {
						try {
							cu = JavaASTUtil.parseCompilationUnit(icu, monitor);
						} catch(Exception ex) {
							addErrorMsg("Failed to parse dependent unit " + icu.getElementName() + ":" + ex.toString());
							continue;
						} catch(Throwable ex) {
							addErrorMsg("Failed to parse dependent unit " + icu.getElementName() + ":" + ex.toString());
							continue;
						}
					}
					
					String source = icu.getSource();
					Document document = new Document(source);

					cu.recordModifications();
					
					for(DependentRefactoringCallback<?> depCallback : depCallbacks) {
						// if (depCallback.preparedResult == null) continue;
						try {
							@SuppressWarnings("unchecked")
							BiConsumer<CompilationUnit,Object> updateCallback2 = (BiConsumer<CompilationUnit,Object>) depCallback.updateCallback;
							
							// *** update dependent callback ***
							updateCallback2.accept(cu, depCallback.preparedResult);
							
						} catch(Exception ex) {
							addErrorMsg("Failed to execute refactoring in unit " + icu.getElementName() + ":" + ex.toString());
							PatternUIPlugin.logWarning("Failed to execute refactoring in unit " + icu.getElementName(), ex);
						} catch(Throwable ex) {
							addErrorMsg("Failed to execute refactoring in unit " + icu.getElementName() + ":" + ex.toString());
							PatternUIPlugin.logError("Failed to execute refactoring in unit " + icu.getElementName(), ex);
						}
						
						if (monitor.isCanceled()) {
							throw new OperationCanceledException();
						}
					}
					
					CompilationUnitChange cuChange = new CompilationUnitChange(changeName, icu);
					TextEdit textEdit = cu.rewrite(document, icu.getJavaProject().getOptions(true));
					cuChange.addEdit(textEdit);
					
					res.add(cuChange);
				}
			}
			
		} catch(Exception ex) {
			addErrorMsg("Failed :" + ex.toString());
		} finally {
			onFinishRun();
		}		
		
		return res;
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
	
	protected void handleUnit(CompilationUnit unit) throws Exception {
		Object preparedRefactoredParams;
		try {
			// *** the Biggy : prepare refactoring ***
			preparedRefactoredParams = prepareRefactorUnit(unit);
		} catch(CancelRefactorUnitException ex) {
			return;
		}
		
		if (preparedRefactoredParams != null) {
			BiConsumer<CompilationUnit,Object> updateCallback = (cu, prepared) -> doRefactorUnit(cu, prepared);  
			addCompilationUnitTreatment(unit, updateCallback, preparedRefactoredParams);
		}
	}
	
	// -------------------------------------------------------------------------

	protected abstract Object prepareRefactorUnit(CompilationUnit unit) throws Exception;
	
	protected abstract void doRefactorUnit(CompilationUnit unit, Object preparedParams);
	
	

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
	
}