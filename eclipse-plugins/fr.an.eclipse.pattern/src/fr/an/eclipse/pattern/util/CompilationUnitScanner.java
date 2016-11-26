package fr.an.eclipse.pattern.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.PackageFragment;

import fr.an.eclipse.pattern.util.ICompilationUnitFilter.DefaultIncludeExcludeNameCompilationUnitFilter;

/**
 * helper to expand resources (package, package fragment...) to find all ICompilationUnit
 */
@SuppressWarnings("restriction")
public class CompilationUnitScanner {

	private int countCompilationUnit;
	private int countPackage;

	private Set<ICompilationUnit> resultCompilationUnits = new LinkedHashSet<ICompilationUnit>();
	
	private ICompilationUnitFilter filter;
	
	// ------------------------------------------------------------------------
	
	public CompilationUnitScanner() {
	}

	// ------------------------------------------------------------------------
	
	
	public Set<ICompilationUnit> getResultCompilationUnits() {
		return resultCompilationUnits;
	}
	
	public ICompilationUnitFilter getFilter() {
		return filter;
	}

	public void setFilter(ICompilationUnitFilter p) {
		this.filter = p;
	}
	
	public int getCountCompilationUnit() {
		return countCompilationUnit;
	}

	public void setCountCompilationUnit(int countCompilationUnit) {
		this.countCompilationUnit = countCompilationUnit;
	}

	public int getCountPackage() {
		return countPackage;
	}

	public void setCountPackage(int countPackage) {
		this.countPackage = countPackage;
	}

	public void recursiveScanResourcesToCompilationUnits(IProgressMonitor monitor, Object[] resources) throws Exception {
		List<IJavaElement> jelts = JavaElementUtil.resourcesToJavaElements(resources);
		recursiveScanCompilationUnits(monitor, jelts);
	}

	
	// ------------------------------------------------------------------------
	

	protected void recursiveScanCompilationUnits(IProgressMonitor monitor, IJavaElement[] elts) throws JavaModelException {
		for (IJavaElement elt : elts) {
			recursiveScanCompilationUnits(monitor, elt);
		}
	}

	public void recursiveScanCompilationUnits(IProgressMonitor monitor, Collection<IJavaElement> elts) throws Exception {
		for (IJavaElement elt : elts) {
			recursiveScanCompilationUnits(monitor, elt);
		}
	}


	public void recursiveScanCompilationUnits(IProgressMonitor monitor, IJavaElement elt) throws JavaModelException {
		switch (elt.getElementType()) {
		case IJavaElement.COMPILATION_UNIT: {
			ICompilationUnit cu = (ICompilationUnit) elt;
			if (filter == null || filter.accept(cu)) {
				countCompilationUnit++;
				resultCompilationUnits.add(cu);
			}
		} break;
		case IJavaElement.PACKAGE_FRAGMENT: {
			IPackageFragment p = (IPackageFragment) elt;
			countPackage++;
			IJavaElement[] childUnits = p.getChildren();
			recursiveScanCompilationUnits(monitor, childUnits);

			recursiveScanCompilationUnits(monitor, p);
		} break;
		case IJavaElement.PACKAGE_FRAGMENT_ROOT: {
			IPackageFragmentRoot p = (IPackageFragmentRoot) elt;
			boolean isOpen = p.isOpen();
			if (!isOpen) {
				p.open(monitor);
			}
			recursiveScanCompilationUnits(monitor, p.getChildren());
			if (!isOpen) {
				p.close();
			}
		} break;
		case IJavaElement.JAVA_PROJECT: {
			IJavaProject p = (IJavaProject) elt;
			boolean isOpen = p.isOpen();
			if (isOpen) {
				IPackageFragmentRoot[] pckFrags = p.getAllPackageFragmentRoots();
				for(IPackageFragmentRoot pckFrag : pckFrags) {
					recursiveScanCompilationUnits(monitor, pckFrag);
				}
			} // else .. not open => ignore project
		} break;
		default: {
			// unknown type!
		}
		}
	}


	/**
	 */
	public void recursiveScanCompilationUnits(IProgressMonitor monitor, IPackageFragment p) throws JavaModelException {
		List<IPackageFragment> subPackages = pckFragmentToRecursiveSubPackages(monitor, p);
		countPackage += subPackages.size();
		for(IPackageFragment subPackage : subPackages) {
			IJavaElement[] childLs = subPackage.getChildren();
			if (childLs != null && childLs.length != 0) {
				for (IJavaElement jelt : childLs) {
					if (jelt instanceof ICompilationUnit) {
						resultCompilationUnits.add((ICompilationUnit) jelt);
					}
				}
			}
		}
	}
	
	/**
	 */
	public List<IPackageFragment> pckFragmentToRecursiveSubPackages(IProgressMonitor monitor, IPackageFragment p) throws JavaModelException {
		List<IPackageFragment> result = new ArrayList<IPackageFragment>();
		IJavaElement[] childLs = ((IPackageFragmentRoot)p.getParent()).getChildren();
		String[] names =((PackageFragment)p).names;
		int namesLength = names.length;
		newPackage: for (IJavaElement child : childLs) {
			String[] otherNames = ((PackageFragment) child).names;
			if (otherNames.length <= namesLength)
			  continue;
		
			for (int j = 0; j < namesLength; j++) {
				if (!names[j].equals(otherNames[j]))
					continue newPackage;
			}
			result.add((IPackageFragment) child);
		}
		return result;
	}

	public void recursiveScanCUsOfParentProjects(IProgressMonitor monitor, 
			Collection<? extends IJavaElement> javaElements) throws JavaModelException {
		Set<IJavaProject> alreadyScannedPrjs = new HashSet<>();
		for(IJavaElement javaElement : javaElements) {
			IJavaProject iprj = (IJavaProject) javaElement.getAncestor(IJavaElement.JAVA_PROJECT);
			if (iprj == null) {
				continue;//??
			}
			if (alreadyScannedPrjs.contains(iprj)) {
				continue;
			}
			alreadyScannedPrjs.add(iprj);
			recursiveScanCompilationUnits(monitor, iprj);
		}
	}
	
}
