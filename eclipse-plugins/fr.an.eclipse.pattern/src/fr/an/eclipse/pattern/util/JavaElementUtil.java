package fr.an.eclipse.pattern.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.internal.corext.util.SuperTypeHierarchyCache;
import org.eclipse.jdt.internal.ui.javaeditor.JavaTextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * 
 */
@SuppressWarnings("restriction")
public final class JavaElementUtil {

	/** private to force all static */
	private JavaElementUtil() {
	}
	
	// -------------------------------------------------------------------------


	public static List<IJavaElement> selectionToJavaElements(ISelection selection) {
		List<IJavaElement> jelts = null;
		Object[] resources = null;
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSel = (IStructuredSelection) selection;
			resources = structuredSel.toArray();
			jelts = JavaElementUtil.resourcesToJavaElements(resources);
		} else if (selection instanceof JavaTextSelection) {
			JavaTextSelection sel2 = (JavaTextSelection) selection;
			try {
				IJavaElement[] elts = sel2.resolveElementAtOffset();
				jelts = new ArrayList<>(Arrays.asList(elts));
			} catch(Exception ex) {
				resources = new Object[0];	
			}
		} else {
			// TODO unsupported selection (text selection ?...)
			resources = new Object[0]; 
		}
		
		if (jelts == null && resources != null) {
			jelts = JavaElementUtil.resourcesToJavaElements(resources);
		}
		return jelts;
	}
	
	public static Set<ICompilationUnit> javaElementsToICompilationUnits(IProgressMonitor monitor, List<IJavaElement> jelts) {
		CompilationUnitScanner recursiveCUScanner = new CompilationUnitScanner();
		try {
			recursiveCUScanner.recursiveScanCompilationUnits(monitor, jelts);
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
		return recursiveCUScanner.getResultCompilationUnits();
	}

	
	@SuppressWarnings("unchecked")
	public static <T> T findFirstAncestorOfType(IJavaElement elt, Class<T> ancestorType) {
		T res = null;
		for(IJavaElement p = elt; p != null; p = p.getParent()) {
			if (ancestorType.isInstance(p)) {
				res = (T) p;
				break;
			}
		}
		return res;
	}
	
	public static IType findFirstTypeDeclarationAncestor(IJavaElement elt) {
		return findFirstAncestorOfType(elt, IType.class);
	}
	
	public static List<IJavaElement> resourcesToJavaElements(Object[] resources) {
		List<IJavaElement> jelts = new ArrayList<IJavaElement>();
		for (Object elt : resources) {
			if (elt instanceof IAdaptable) {
				IJavaElement jelt = (IJavaElement) ((IAdaptable) elt).getAdapter(IJavaElement.class);
				if (jelt != null) {
					jelts.add(jelt);
					continue;
				}
			}
			if (elt instanceof IResource) {
				IResource res = (IResource) elt;
				IJavaElement jelt = JavaCore.create(res);
				if (jelt instanceof ICompilationUnit) {
					jelts.add(jelt);
				} else if (jelt instanceof IPackageFragment) {
					jelts.add(jelt);
				}
			} else if (elt instanceof IJavaElement) {
				IJavaElement jelt = (IJavaElement) elt;
				jelts.add(jelt);
			}
		}
		return jelts;
	}

	
	public static ICompilationUnit findCompilationUnit(ITypeBinding classType) {
		String classQN = classType.getQualifiedName();
		IJavaProject jproject = classType.getJavaElement().getJavaProject();
		return findCompilationUnit(jproject, classQN);
	}
	
	public static ICompilationUnit findCompilationUnit(IJavaProject jproject, String fullyQualifiedName) {
		ICompilationUnit res = null;
		try {
			IType type = jproject.findType(fullyQualifiedName);
			res = type.getCompilationUnit();
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
		return res;
	}
	
	
	public static boolean containsErrorProblemsMarkers(ICompilationUnit cu) {
		boolean res = false;
		
		try {
			IResource resource = cu.getCorrespondingResource();
			IMarker[] problems = null;
			int depth = IResource.DEPTH_ONE;
			try {
				problems = resource.findMarkers(IMarker.PROBLEM, true, depth);
			} catch (CoreException e) {
				// something went wrong
			}

			if (problems == null || problems.length == 0) {
				res = false;
			} else {
				// found some markers...
				boolean foundPb = false;
				for (IMarker pb : problems) {
					Object severity = pb.getAttribute(IMarker.SEVERITY);
					if (severity.equals(IMarker.SEVERITY_ERROR)) {
						foundPb = true;
						break;
					}
				}
				res = foundPb;
			}
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
		return res;
	}


    public static Set<IType> getAllSuperTypes(IJavaProject jproject, String fullyQualifiedName, IProgressMonitor monitor) {
        Set<IType> allSuperTypes = new HashSet<IType>(); 
        
        try {
            IType type = jproject.findType(fullyQualifiedName);
            
            if (type == null) {
                throw new RuntimeException("Impossible to retrieve the IType : " + fullyQualifiedName);
            }
            
            allSuperTypes.addAll(Arrays.asList(getTypeHierarchy(type, monitor).getAllSuperclasses(type)));
            allSuperTypes.addAll(Arrays.asList(getTypeHierarchy(type, monitor).getAllSuperInterfaces(type)));
        } catch (JavaModelException ex) {
            throw new RuntimeException(ex);
        }
        
        return allSuperTypes;
    }
    
	public static ITypeHierarchy getTypeHierarchy(IType type, IProgressMonitor monitor) throws JavaModelException {
        //Commented because is calculate always the typeHierarchy
        //return type.newSupertypeHierarchy(monitor);
        
        return SuperTypeHierarchyCache.getTypeHierarchy(type);
    }
	

	public static class MethodOverrideElement {
		public final IType superType;
		public final IMethod superMeth;
		public final IType subType;
		public final IMethod overrideMethod;
		public MethodOverrideElement(IType superType, IMethod superMeth, IType subType, IMethod overrideMethod) {
			this.superType = superType;
			this.superMeth = superMeth;
			this.subType = subType;
			this.overrideMethod = overrideMethod;
		}
	}
	
	public static List<IMethod> findSubTypeMethodsOverride(IType superType, IMethod meth, ITypeHierarchy typeHierarchy) {
		List<IMethod> res = new ArrayList<>();
		IType[] subtypes = typeHierarchy.getAllSubtypes(superType);
		if (subtypes != null && subtypes.length > 0) {
			for(IType subType : subtypes) {
				// ICompilationUnit subTypeICU = subType.getCompilationUnit();
				IMethod[] subTypeMethods = subType.findMethods(meth); // find method in subType matching signature of (super type) method
				if (subTypeMethods != null && subTypeMethods.length > 0) {
					for (IMethod subTypeMethod : subTypeMethods) {
						// assert subType == subTypeMethod.getAncestor(IJavaElement.TYPE;
						res.add(subTypeMethod);
					}
				}
			}
		}
		return res;
	}
	
}
