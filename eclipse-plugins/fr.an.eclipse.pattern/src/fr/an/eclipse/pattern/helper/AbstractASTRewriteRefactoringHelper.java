package fr.an.eclipse.pattern.helper;



import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.swt.widgets.Display;

import fr.an.eclipse.pattern.PatternUIPlugin;
import fr.an.eclipse.pattern.util.JavaASTUtil;
import fr.an.eclipse.pattern.util.UiUtil;


/**
 * 
 */
public abstract class AbstractASTRewriteRefactoringHelper extends AbstractCompilationUnitActionHelper {
		
	// -------------------------------------------------------------------------

	protected AbstractASTRewriteRefactoringHelper(IProgressMonitor monitor, Set<ICompilationUnit> compilationUnits) {
		this(monitor, compilationUnits, null);
	}

	protected AbstractASTRewriteRefactoringHelper(IProgressMonitor monitor, Set<ICompilationUnit> compilationUnits, List<IJavaElement> javaElements) {
		super(monitor, compilationUnits, javaElements);
	}

	// -------------------------------------------------------------------------

	protected abstract Object prepareRefactorUnit(CompilationUnit unit) throws Exception;
	
	protected abstract void doRefactorUnit(CompilationUnit unit, Object preparedParams);
	
	
	// -------------------------------------------------------------------------

	@Override
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
			super.addCompilationUnitTreatment(unit, updateCallback, preparedRefactoredParams);
		}
	}

//	protected void handleAndRewrite(CompilationUnit unit, Consumer<CompilationUnit> handler) throws Exception {
//		unit.recordModifications();
//
//		ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager();
//		IPath path = unit.getJavaElement().getPath();
//		try {
//			bufferManager.connect(path, LocationKind.IFILE, monitor);
//		
//			handler.accept(cu);
//
//			try {
//				rewriteASTDocument(unit, path, bufferManager);
//			} catch(Exception ex) {
//				PatternUIPlugin.logWarning("Failed to save dependent treatments on " + unit.getJavaElement().getElementName(), ex);
//				// ignore.. no rethrow!!
//			}
//			
//		} finally {
//			bufferManager.disconnect(path, LocationKind.IFILE, monitor);
//		}
//	}
//	


	
	

	 
}
