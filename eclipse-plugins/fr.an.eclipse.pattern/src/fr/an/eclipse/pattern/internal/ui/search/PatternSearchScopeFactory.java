package fr.an.eclipse.pattern.internal.ui.search;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.browsing.LogicalPackage;
import org.eclipse.jdt.internal.ui.viewsupport.BasicElementLabels;
import org.eclipse.jdt.internal.ui.workingsets.WorkingSetComparator;
import org.eclipse.jdt.ui.JavaElementLabels;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.IWorkingSetSelectionDialog;

import fr.an.eclipse.pattern.PatternUIPlugin;

public class PatternSearchScopeFactory {

	private static PatternSearchScopeFactory fgInstance = new PatternSearchScopeFactory();

	private static String[] DEFAULT_FILENAME_PATTERNS = new String[] { ".*\\.java" };
	
	private final PatternSearchScope EMPTY_SCOPE= 
			PatternSearchScope.newSearchScope(new IJavaElement[] {}, DEFAULT_FILENAME_PATTERNS, false);

	private PatternSearchScopeFactory() {
	}

	public static PatternSearchScopeFactory getInstance() {
		return fgInstance;
	}

	public IWorkingSet[] queryWorkingSets() throws InterruptedException {
		Shell shell= PatternUIPlugin.getActiveWorkbenchShell();
		if (shell == null)
			return null;
		IWorkingSetSelectionDialog dialog= PlatformUI.getWorkbench().getWorkingSetManager().createWorkingSetSelectionDialog(shell, true);
		if (dialog.open() != Window.OK) {
			throw new InterruptedException();
		}

		IWorkingSet[] workingSets= dialog.getSelection();
		if (workingSets.length > 0)
			return workingSets;
		return null; // 'no working set' selected
	}

	public PatternSearchScope createJavaSearchScope(IWorkingSet[] workingSets) {
		if (workingSets == null || workingSets.length < 1)
			return EMPTY_SCOPE;

		Set<IJavaElement> javaElements= new HashSet<IJavaElement>(workingSets.length * 10);
		for (int i= 0; i < workingSets.length; i++) {
			IWorkingSet workingSet= workingSets[i];
			if (workingSet.isEmpty() && workingSet.isAggregateWorkingSet()) {
				return createWorkspaceScope();
			}
			addJavaElements(javaElements, workingSet);
		}
		return createJavaSearchScope(javaElements);
	}

	public PatternSearchScope createJavaSearchScope(IWorkingSet workingSet) {
		Set<IJavaElement> javaElements= new HashSet<IJavaElement>(10);
		if (workingSet.isEmpty() && workingSet.isAggregateWorkingSet()) {
			return createWorkspaceScope();
		}
		addJavaElements(javaElements, workingSet);
		return createJavaSearchScope(javaElements);
	}

	public PatternSearchScope createJavaSearchScope(IResource[] resources) {
		if (resources == null)
			return EMPTY_SCOPE;
		Set<IJavaElement> javaElements= new HashSet<IJavaElement>(resources.length);
		addJavaElements(javaElements, resources);
		return createJavaSearchScope(javaElements);
	}

	public PatternSearchScope createJavaSearchScope(ISelection selection) {
		return createPatternSearchScope(getJavaElements(selection));
	}

	public PatternSearchScope createProjectSearchScope(String[] projectNames) {
		ArrayList<IJavaElement> res= new ArrayList<IJavaElement>();
		IWorkspaceRoot root= ResourcesPlugin.getWorkspace().getRoot();
		for (int i= 0; i < projectNames.length; i++) {
			IJavaProject project= JavaCore.create(root.getProject(projectNames[i]));
			if (project.exists()) {
				res.add(project);
			}
		}
		return createJavaSearchScope(res);
	}

//	public PatternFileSearchScope createProjectSearchScope(IJavaProject project) {
//		return new PatternFileSearchScope(new IJavaElement[] { project });
//	}
//
//	public PatternFileSearchScope createJavaProjectSearchScope(IEditorInput editorInput) {
//		IJavaElement elem= JavaUI.getEditorInputJavaElement(editorInput);
//		if (elem != null) {
//			IJavaProject project= elem.getJavaProject();
//			if (project != null) {
//				return createProjectSearchScope(project);
//			}
//		}
//		return EMPTY_SCOPE;
//	}

	public String getWorkspaceScopeDescription() {
		return PatternSearchMessages.WorkspaceScope;
	}

	public String getProjectScopeDescription(String[] projectNames) {
		if (projectNames.length == 0) {
			return PatternSearchMessages.JavaSearchScopeFactory_undefined_projects;
		}
		String scopeDescription;
		if (projectNames.length == 1) {
			String label= PatternSearchMessages.EnclosingProjectScope;
			scopeDescription= MessageFormat.format(label, BasicElementLabels.getJavaElementName(projectNames[0]));
		} else if (projectNames.length == 2) {
			String label= PatternSearchMessages.EnclosingProjectsScope2;
			scopeDescription= MessageFormat.format(label, BasicElementLabels.getJavaElementName(projectNames[0]), BasicElementLabels.getJavaElementName(projectNames[1]));
		} else {
			String label= PatternSearchMessages.EnclosingProjectsScope;
			scopeDescription= MessageFormat.format(label, BasicElementLabels.getJavaElementName(projectNames[0]), BasicElementLabels.getJavaElementName(projectNames[1]));
		}
		return scopeDescription;
	}

	public String getProjectScopeDescription(IJavaProject project) {
		return MessageFormat.format(PatternSearchMessages.ProjectScope, BasicElementLabels.getJavaElementName(project.getElementName()));
	}

	public String getProjectScopeDescription(IEditorInput editorInput) {
		IJavaElement elem= JavaUI.getEditorInputJavaElement(editorInput);
		if (elem != null) {
			IJavaProject project= elem.getJavaProject();
			if (project != null) {
				return getProjectScopeDescription(project);
			}
		}
		return MessageFormat.format(PatternSearchMessages.ProjectScope, "");  //$NON-NLS-1$
	}

	public String getHierarchyScopeDescription(IType type) {
		return MessageFormat.format(PatternSearchMessages.HierarchyScope, new String[] { JavaElementLabels.getElementLabel(type, JavaElementLabels.ALL_DEFAULT) });
	}


	public String getSelectionScopeDescription(IJavaElement[] javaElements) {
		if (javaElements.length == 0) {
			return PatternSearchMessages.JavaSearchScopeFactory_undefined_selection;
		}
		String scopeDescription;
		if (javaElements.length == 1) {
			String label= PatternSearchMessages.SingleSelectionScope;
			scopeDescription= MessageFormat.format(label, JavaElementLabels.getElementLabel(javaElements[0], JavaElementLabels.ALL_DEFAULT));
		} else if (javaElements.length == 2) {
			String label= PatternSearchMessages.DoubleSelectionScope;
			scopeDescription= MessageFormat.format(label, new String[] { JavaElementLabels.getElementLabel(javaElements[0], JavaElementLabels.ALL_DEFAULT), JavaElementLabels.getElementLabel(javaElements[1], JavaElementLabels.ALL_DEFAULT)});
		}  else {
			String label= PatternSearchMessages.SelectionScope;
			scopeDescription= MessageFormat.format(label, new String[] { JavaElementLabels.getElementLabel(javaElements[0], JavaElementLabels.ALL_DEFAULT), JavaElementLabels.getElementLabel(javaElements[1], JavaElementLabels.ALL_DEFAULT)});
		}
		return scopeDescription;
	}

	public String getWorkingSetScopeDescription(IWorkingSet[] workingSets) {
		if (workingSets.length == 0) {
			return PatternSearchMessages.JavaSearchScopeFactory_undefined_workingsets;
		}
		if (workingSets.length == 1) {
			String label= PatternSearchMessages.SingleWorkingSetScope;
			return MessageFormat.format(label, BasicElementLabels.getWorkingSetLabel(workingSets[0]));
		}
		Arrays.sort(workingSets, new WorkingSetComparator());
		if (workingSets.length == 2) {
			String label= PatternSearchMessages.DoubleWorkingSetScope;
			return MessageFormat.format(label, new String[] { BasicElementLabels.getWorkingSetLabel(workingSets[0]), BasicElementLabels.getWorkingSetLabel(workingSets[1])});
		}
		String label= PatternSearchMessages.WorkingSetsScope;
		return MessageFormat.format(label, new String[] { BasicElementLabels.getWorkingSetLabel(workingSets[0]), BasicElementLabels.getWorkingSetLabel(workingSets[1])});
	}

	public IJavaProject[] getProjects(PatternSearchScope scope) {
		IJavaElement[] roots= scope.getRoots();
		HashSet<IJavaProject> temp= new HashSet<IJavaProject>();
//		for (int i= 0; i < paths.length; i++) {
//			IResource resource= ResourcesPlugin.getWorkspace().getRoot().findMember(paths[i]);
//			if (resource != null && resource.getType() == IResource.PROJECT)
//				temp.add(resource);
//		}
		for(IJavaElement root : roots) {
			IJavaProject prj = root.getJavaProject();
			temp.add(prj);
		}
		return temp.toArray(new IJavaProject[temp.size()]);
	}

	public IJavaElement[] getJavaElements(ISelection selection) {
		if (selection instanceof IStructuredSelection && !selection.isEmpty()) {
			return getJavaElements(((IStructuredSelection)selection).toArray());
		} else {
			return new IJavaElement[0];
		}
	}

	private IJavaElement[] getJavaElements(Object[] elements) {
		if (elements.length == 0)
			return new IJavaElement[0];

		Set<IJavaElement> result= new HashSet<IJavaElement>(elements.length);
		for (int i= 0; i < elements.length; i++) {
			Object selectedElement= elements[i];
			if (selectedElement instanceof IJavaElement) {
				addJavaElements(result, (IJavaElement) selectedElement);
			} else if (selectedElement instanceof IResource) {
				addJavaElements(result, (IResource) selectedElement);
			} else if (selectedElement instanceof LogicalPackage) {
				addJavaElements(result, (LogicalPackage) selectedElement);
			} else if (selectedElement instanceof IWorkingSet) {
				IWorkingSet ws= (IWorkingSet)selectedElement;
				addJavaElements(result, ws);
			} else if (selectedElement instanceof IAdaptable) {
				IResource resource= (IResource) ((IAdaptable) selectedElement).getAdapter(IResource.class);
				if (resource != null)
					addJavaElements(result, resource);
			}

		}
		return result.toArray(new IJavaElement[result.size()]);
	}

	public PatternSearchScope createPatternSearchScope(IJavaElement[] javaElements) {
		if (javaElements.length == 0)
			return EMPTY_SCOPE;
		return PatternSearchScope.newSearchScope(javaElements, DEFAULT_FILENAME_PATTERNS, false);
	}

	private PatternSearchScope createJavaSearchScope(Collection<IJavaElement> javaElements) {
		if (javaElements.isEmpty())
			return EMPTY_SCOPE;
		IJavaElement[] elementArray= javaElements.toArray(new IJavaElement[javaElements.size()]);
		return PatternSearchScope.newSearchScope(elementArray, DEFAULT_FILENAME_PATTERNS, false);
	}

	private void addJavaElements(Set<IJavaElement> javaElements, IResource[] resources) {
		for (int i= 0; i < resources.length; i++)
			addJavaElements(javaElements, resources[i]);
	}

	private void addJavaElements(Set<IJavaElement> javaElements, IResource resource) {
		IJavaElement javaElement= (IJavaElement)resource.getAdapter(IJavaElement.class);
		if (javaElement == null)
			// not a Java resource
			return;

		if (javaElement.getElementType() == IJavaElement.PACKAGE_FRAGMENT) {
			// add other possible package fragments
			try {
				addJavaElements(javaElements, ((IFolder)resource).members());
			} catch (CoreException ex) {
				// don't add elements
			}
		}

		javaElements.add(javaElement);
	}

	private void addJavaElements(Set<IJavaElement> javaElements, IJavaElement javaElement) {
		javaElements.add(javaElement);
	}

	private void addJavaElements(Set<IJavaElement> javaElements, IWorkingSet workingSet) {
		if (workingSet == null)
			return;

		if (workingSet.isAggregateWorkingSet() && workingSet.isEmpty()) {
			try {
				IJavaProject[] projects= JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()).getJavaProjects();
				javaElements.addAll(Arrays.asList(projects));
			} catch (JavaModelException e) {
				JavaPlugin.log(e);
			}
			return;
		}

		IAdaptable[] elements= workingSet.getElements();
		for (int i= 0; i < elements.length; i++) {
			IJavaElement javaElement=(IJavaElement) elements[i].getAdapter(IJavaElement.class);
			if (javaElement != null) {
				addJavaElements(javaElements, javaElement);
				continue;
			}
			IResource resource= (IResource)elements[i].getAdapter(IResource.class);
			if (resource != null) {
				addJavaElements(javaElements, resource);
			}

			// else we don't know what to do with it, ignore.
		}
	}

	private void addJavaElements(Set<IJavaElement> javaElements, LogicalPackage selectedElement) {
		IPackageFragment[] packages= selectedElement.getFragments();
		for (int i= 0; i < packages.length; i++)
			addJavaElements(javaElements, packages[i]);
	}

	public PatternSearchScope createWorkspaceScope() {
		return PatternSearchScope.newWorkspaceScope(DEFAULT_FILENAME_PATTERNS, false);
	}

}
