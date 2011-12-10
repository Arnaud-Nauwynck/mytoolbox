package fr.an.eclipse.pattern.util;

import org.eclipse.jdt.core.ICompilationUnit;

/**
 * 
 */
public interface ICompilationUnitFilter {

	public boolean accept(ICompilationUnit cu);
	
	
	// ------------------------------------------------------------------------
	
	public static class DefaultIncludeExcludeNameCompilationUnitFilter implements ICompilationUnitFilter {
		
		private IncludeExcludePatternList includeExcludes;

		public DefaultIncludeExcludeNameCompilationUnitFilter(IncludeExcludePatternList includeExcludes) {
			super();
			this.includeExcludes = includeExcludes;
		}

		@Override
		public boolean accept(ICompilationUnit cu) {
			String icuName = cu.getElementName();
			String unitName = icuName;
			int suffixFileSepIndex =  unitName.lastIndexOf("."); // remove ".java" in name ???
			if (suffixFileSepIndex != -1) {
				unitName = unitName.substring(0, suffixFileSepIndex);
			}
			boolean res = includeExcludes.accept(unitName);
			return res;
		}
		
		@Override
		public String toString() {
			return "DefaultIncludeExcludeNameCompilationUnitFilter[" + includeExcludes + "]";
		}
		
	}
}
