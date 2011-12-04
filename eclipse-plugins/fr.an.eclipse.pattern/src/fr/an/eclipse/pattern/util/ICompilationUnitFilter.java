package fr.an.eclipse.pattern.util;

import org.eclipse.jdt.core.ICompilationUnit;

/**
 * 
 */
public interface ICompilationUnitFilter {

	public boolean visitSwitchTree(ICompilationUnit cu);
	
}
