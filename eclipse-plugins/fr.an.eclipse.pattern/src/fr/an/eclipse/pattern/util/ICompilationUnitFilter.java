package fr.an.eclipse.pattern.util;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.JavaModelException;

/**
 * 
 */
public interface ICompilationUnitFilter {

	public boolean accept(ICompilationUnit cu);
	
	
	// ------------------------------------------------------------------------
	
	public static class DefaultIncludeExcludeNameCompilationUnitFilter implements ICompilationUnitFilter {
		
		private IncludeExcludePatternList cuNamePatterns;
		private IncludeExcludePatternList packageNamePatterns;

		public DefaultIncludeExcludeNameCompilationUnitFilter(IncludeExcludePatternList cuNamePatterns) {
			this(cuNamePatterns, null);
		}

		public DefaultIncludeExcludeNameCompilationUnitFilter(IncludeExcludePatternList cuNamePatterns, IncludeExcludePatternList packageNamePatterns) {
			this.cuNamePatterns = cuNamePatterns;
			this.packageNamePatterns = packageNamePatterns;
		}

		@Override
		public boolean accept(ICompilationUnit cu) {
			String icuName = cu.getElementName();
			String unitName = icuName;
			int suffixFileSepIndex =  unitName.lastIndexOf("."); // remove ".java" in name ???
			if (suffixFileSepIndex != -1) {
				unitName = unitName.substring(0, suffixFileSepIndex);
			}
			boolean res = true;
			if (cuNamePatterns != null) {
				res = cuNamePatterns.accept(unitName);
				if (!res) {
					return false;
				}
			}
			
			if (packageNamePatterns != null) {
				IPackageDeclaration[] pckDecls;
				try {
					pckDecls = cu.getPackageDeclarations();
				} catch (JavaModelException e) {
					throw new RuntimeException("Failed", e);
				}
				if (pckDecls == null || pckDecls.length != 1) {
					return false;
				}
				
				String pckName = pckDecls[0].getElementName();
				res = packageNamePatterns.accept(pckName);
				if (!res) {
					return false;
				}
			}
			
			return res;
		}
		
		@Override
		public String toString() {
			return "DefaultIncludeExcludeNameCompilationUnitFilter[" + 
					((cuNamePatterns != null)? "cuNamePatterns:" + cuNamePatterns : "") +
					((packageNamePatterns != null)? "packageNamePatterns:" + packageNamePatterns : "") +
					"]";
		}
		
	}
}
