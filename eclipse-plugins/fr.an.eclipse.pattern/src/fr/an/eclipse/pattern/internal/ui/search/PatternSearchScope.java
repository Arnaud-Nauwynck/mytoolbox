package fr.an.eclipse.pattern.internal.ui.search;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.search.core.text.TextSearchScope;
import org.eclipse.search.internal.core.text.PatternConstructor;
import org.eclipse.search.internal.ui.WorkingSetComparator;
import org.eclipse.search.internal.ui.text.BasicElementLabels;
import org.eclipse.search.internal.ui.util.FileTypeEditor;
import org.eclipse.ui.IWorkingSet;

import fr.an.eclipse.pattern.PatternUIPlugin;
import fr.an.eclipse.pattern.util.CompilationUnitScanner;

/**
 * A text search scope used by the file search dialog. Additionally to roots it allows to define file name
 * patterns and exclude all derived resources.
 *
 * <p>
 * Clients should not instantiate or subclass this class.
 * </p>
 * @since 3.2
 *
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public final class PatternSearchScope { // extends TextSearchScope

	private static final boolean IS_CASE_SENSITIVE_FILESYSTEM = !new File("Temp").equals(new File("temp")); //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * Returns a scope for the workspace. The created scope contains all resources in the workspace
	 * that match the given file name patterns. Depending on <code>includeDerived</code>, derived resources or
	 * resources inside a derived container are part of the scope or not.
	 *
	 * @param fileNamePatterns file name pattern that all files have to match <code>null</code> to include all file names.
	 * @param includeDerived defines if derived files and files inside derived containers are included in the scope.
	 * @return a scope containing all files in the workspace that match the given file name patterns.
	 */
	public static PatternSearchScope newWorkspaceScope(String[] fileNamePatterns, boolean includeDerived) {
		IJavaProject[] projects;
		try {
			projects = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()).getJavaProjects();
		} catch (JavaModelException ex) {
			PatternUIPlugin.logError("Failed!", ex);
			projects = new IJavaProject[0];
		}
		return new PatternSearchScope(PatternSearchMessages.WorkspaceScope, projects,
				null, fileNamePatterns, includeDerived);
	}

	/**
	 * Returns a scope for the given root resources. The created scope contains all root resources and their
	 * children that match the given file name patterns. Depending on <code>includeDerived</code>, derived resources or
	 * resources inside a derived container are part of the scope or not.
	 *
	 * @param roots the roots resources defining the scope.
	 * @param fileNamePatterns file name pattern that all files have to match <code>null</code> to include all file names.
	 * @param includeDerived defines if derived files and files inside derived containers are included in the scope.
	 * @return a scope containing the resources and its children if they match the given file name patterns.
	 */
	public static PatternSearchScope newSearchScope(IJavaElement[] roots, String[] fileNamePatterns, boolean includeDerived) {
		roots= removeRedundantEntries(roots, includeDerived);

		String description;
		if (roots.length == 0) {
			description= PatternSearchMessages.PatternFileSearchScope_scope_empty;
		} else if (roots.length == 1) {
			String label= PatternSearchMessages.PatternFileSearchScope_scope_single;
			description= MessageFormat.format(label, roots[0].getElementName());
		} else if (roots.length == 2) {
			String label= PatternSearchMessages.PatternFileSearchScope_scope_double;
			description= MessageFormat.format(label, roots[0].getElementName(), roots[1].getElementName());
		} else {
			String label= PatternSearchMessages.PatternFileSearchScope_scope_multiple;
			description= MessageFormat.format(label, roots[0].getElementName(), roots[1].getElementName());
		}
		return new PatternSearchScope(description, roots, null, fileNamePatterns, includeDerived);
	}

	/**
	 * Returns a scope for the given working sets. The created scope contains all resources in the
	 * working sets that match the given file name patterns. Depending on <code>includeDerived</code>, derived resources or
	 * resources inside a derived container are part of the scope or not.
	 *
	 * @param workingSets the working sets defining the scope.
	 * @param fileNamePatterns file name pattern that all files have to match <code>null</code> to include all file names.
	 * @param includeDerived defines if derived files and files inside derived containers are included in the scope.
	 * @return a scope containing the resources in the working set if they match the given file name patterns.
	 */
	public static PatternSearchScope newSearchScope(IWorkingSet[] workingSets, String[] fileNamePatterns, boolean includeDerived) {
		String description;
		Arrays.sort(workingSets, new WorkingSetComparator());
		if (workingSets.length == 0) {
			description= PatternSearchMessages.PatternFileSearchScope_ws_scope_empty;
		} else if (workingSets.length == 1) {
			String label= PatternSearchMessages.PatternFileSearchScope_ws_scope_single;
			description= MessageFormat.format(label, workingSets[0].getLabel());
		} else if (workingSets.length == 2) {
			String label= PatternSearchMessages.PatternFileSearchScope_ws_scope_double;
			description= MessageFormat.format(label, workingSets[0].getLabel(), workingSets[1].getLabel());
		} else {
			String label= PatternSearchMessages.PatternFileSearchScope_ws_scope_multiple;
			description= MessageFormat.format(label, workingSets[0].getLabel(), workingSets[1].getLabel());
		}
		PatternSearchScope scope= new PatternSearchScope(description, convertToResources(workingSets, includeDerived), workingSets, fileNamePatterns, includeDerived);
		return scope;
	}

	private final String fDescription;
	private final IJavaElement[] fRootElements;
	private final String[] fFileNamePatterns;
	private final Matcher fPositiveFileNameMatcher;
	private final Matcher fNegativeFileNameMatcher;

	private boolean fVisitDerived;
	private IWorkingSet[] fWorkingSets;

	private PatternSearchScope(String description, IJavaElement[] resources, 
			IWorkingSet[] workingSets, String[] fileNamePatterns, boolean visitDerived) {
		fDescription= description;
		fRootElements= resources;
		fFileNamePatterns= fileNamePatterns;
		fVisitDerived= visitDerived;
		fWorkingSets= workingSets;
		fPositiveFileNameMatcher= createMatcher(fileNamePatterns, false);
		fNegativeFileNameMatcher= createMatcher(fileNamePatterns, true);
	}

	/**
	 * Returns the description of the scope
	 *
	 * @return the description of the scope
	 */
	public String getDescription() {
		return fDescription;
	}

	/**
	 * Returns the file name pattern configured for this scope or <code>null</code> to match
	 * all file names.
	 *
	 * @return the file name pattern starings
	 */
	public String[] getFileNamePatterns() {
		return fFileNamePatterns;
	}

	/**
	 * Returns the working-sets that were used to  configure this scope or <code>null</code>
	 * if the scope was not created off working sets.
	 *
	 * @return the working-sets the scope is based on.
	 */
	public IWorkingSet[] getWorkingSets() {
		return fWorkingSets;
	}

	/**
	 * Returns the content types configured for this scope or <code>null</code> to match
	 * all content types.
	 *
	 * @return the file name pattern starings
	 */
	public IContentType[] getContentTypes() {
		return null;  // to be implemented in the future
	}

	/**
	 * Returns a description describing the file name patterns and content types.
	 *
	 * @return the description of the scope
	 */
	public String getFilterDescription() {
		String[] ext= fFileNamePatterns;
		if (ext == null) {
			return BasicElementLabels.getFilePattern("*"); //$NON-NLS-1$
		}
		Arrays.sort(ext);
		StringBuffer buf= new StringBuffer();
		for (int i= 0; i < ext.length; i++) {
			if (i > 0) {
				buf.append(", "); //$NON-NLS-1$
			}
			buf.append(ext[i]);
		}
		return BasicElementLabels.getFilePattern(buf.toString());
	}

	/**
	 * Returns whether derived resources are included in this search scope.
	 *
	 * @return whether derived resources are included in this search scope.
	 */
	public boolean includeDerived() {
		return fVisitDerived;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.core.text.FileSearchScope#getRoots()
	 */
	public IJavaElement[] getRoots() {
		return fRootElements;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.core.text.FileSearchScope#contains(org.eclipse.core.resources.IResourceProxy)
	 */
	public boolean contains(IResourceProxy proxy) {
		if (!fVisitDerived && proxy.isDerived()) {
			return false; // all resources in a derived folder are considered to be derived, see bug 103576
		}

		if (proxy.getType() == IResource.FILE) {
			return matchesFileName(proxy.getName());
		}
		return true;
	}

	private boolean matchesFileName(String fileName) {
		if (fPositiveFileNameMatcher != null && !fPositiveFileNameMatcher.reset(fileName).matches()) {
			return false;
		}
		if (fNegativeFileNameMatcher != null && fNegativeFileNameMatcher.reset(fileName).matches()) {
			return false;
		}
		return true;
	}

	private Matcher createMatcher(String[] fileNamePatterns, boolean negativeMatcher) {
		if (fileNamePatterns == null || fileNamePatterns.length == 0) {
			return null;
		}
		ArrayList patterns= new ArrayList();
		for (int i= 0; i < fileNamePatterns.length; i++) {
			String pattern= fFileNamePatterns[i];
			if (negativeMatcher == pattern.startsWith(FileTypeEditor.FILE_PATTERN_NEGATOR)) {
				if (negativeMatcher) {
					pattern= pattern.substring(FileTypeEditor.FILE_PATTERN_NEGATOR.length()).trim();
				}
				if (pattern.length() > 0) {
					patterns.add(pattern);
				}
			}
		}
		if (!patterns.isEmpty()) {
			String[] patternArray= (String[]) patterns.toArray(new String[patterns.size()]);
			Pattern pattern= PatternConstructor.createPattern(patternArray, IS_CASE_SENSITIVE_FILESYSTEM);
			return pattern.matcher(""); //$NON-NLS-1$
		}
		return null;
	}

	private static IJavaElement[] removeRedundantEntries(IJavaElement[] elements, boolean includeDerived) {
		ArrayList res= new ArrayList();
		for (int i= 0; i < elements.length; i++) {
			IJavaElement curr= elements[i];
			addToList(res, curr, includeDerived);
		}
		return (IJavaElement[])res.toArray(new IJavaElement[res.size()]);
	}

	private static IJavaElement[] convertToResources(IWorkingSet[] workingSets, boolean includeDerived) {
		ArrayList res= new ArrayList();
		for (int i= 0; i < workingSets.length; i++) {
			IWorkingSet workingSet= workingSets[i];
			if (workingSet.isAggregateWorkingSet() && workingSet.isEmpty()) {
				try {
					return JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()).getJavaProjects();
				} catch (JavaModelException ex) {
					PatternUIPlugin.logError("Failed to get java projects list => use empty", ex);
				}
			}
			IAdaptable[] elements= workingSet.getElements();
			for (int k= 0; k < elements.length; k++) {
				IJavaElement curr= (IJavaElement) elements[k].getAdapter(IJavaElement.class);
				if (curr != null) {
					addToList(res, curr, includeDerived);
				}
			}
		}
		return (IJavaElement[]) res.toArray(new IJavaElement[res.size()]);
	}

	private static void addToList(ArrayList<IJavaElement> res, IJavaElement curr, boolean includeDerived) {
		if (!includeDerived && (curr.getResource() == null || curr.getResource().isDerived(IResource.CHECK_ANCESTORS))) {
			return;
		}
		IPath currPath= curr.getPath(); // ?? curr.getResource().getFullPath()
		for (int k= res.size() - 1; k >= 0 ; k--) {
			IJavaElement other= (IJavaElement) res.get(k);
			IPath otherPath= other.getPath();
			if (otherPath.isPrefixOf(currPath)) {
				return;
			}
			if (currPath.isPrefixOf(otherPath)) {
				res.remove(k);
			}
		}
		res.add(curr);
	}
	
	/**
	 * Evaluates all files in this scope.
	 *
	 * @param status a {@link MultiStatus} to collect the error status that occurred while collecting resources.
	 * @return returns the files in the scope.
	 */
	public ICompilationUnit[] evaluateCompilationUnitsInScope(IProgressMonitor monitor, MultiStatus status) {
		CompilationUnitScanner cuScanner = new CompilationUnitScanner();

		// TOADD configure filter patterns... 
		
		
		for(IJavaElement root : fRootElements) {
			if (root == null) continue;
			try {
				// for field, method... => use parent ICompilationUnit
				IJavaElement parentCu = null;
				if (root.getElementType() == IJavaElement.COMPILATION_UNIT) {
					parentCu = root;
				} else {
					IJavaElement primaryElt = root.getPrimaryElement();
					if (primaryElt != null && primaryElt.getElementType() == IJavaElement.COMPILATION_UNIT) {
						parentCu = primaryElt;
					}
				}
			
				if (parentCu != null) {
					cuScanner.recursiveScanCompilationUnits(monitor, parentCu);
				} else {
					cuScanner.recursiveScanCompilationUnits(monitor, root);
				}
			} catch(JavaModelException ex) {
				status.add(PatternUIPlugin.newStatusError("Failed to scan compilation units for " + root, ex));
			}
		}
		Set<ICompilationUnit> tmpres = cuScanner.getResultCompilationUnits();
		return tmpres.toArray(new ICompilationUnit[tmpres.size()]);
	}

}
