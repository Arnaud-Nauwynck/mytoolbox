package fr.an.eclipse.pattern.util;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;


public class ASTNode2IJavaElementUtil {

	public static IJavaElement getJavaElement(ASTNode node) {
		int type = node.getNodeType();
		if (type == ASTNode.TYPE_DECLARATION)
			return toIType((TypeDeclaration)node);
		else if (type == ASTNode.METHOD_DECLARATION)
			return bindingToIMethod((MethodDeclaration)node);
		else if (type == ASTNode.FIELD_DECLARATION)
			return getJavaElement((FieldDeclaration)node);
		else if (type == ASTNode.COMPILATION_UNIT)
			return getJavaElement((CompilationUnit)node);
		// else not supported / not implemented yet... 
		return null;
	}

	public static IJavaElement getEnclosingJavaElementDeclaration(ASTNode node) {
		ASTNode decl = JavaASTUtil.getEnclosingDeclarationNode(node);
		IJavaElement res = getJavaElement(decl);
		return res;
	}


	/**
	 * returns the IJavaElement corresponding to the given node.
	 * @param node can be null
	 * @return
	 */
	public static IMethod bindingToIMethod(MethodDeclaration node) {
		IMethodBinding binding = node.resolveBinding();
		if (binding != null) // Bindings can be null
			return (IMethod) binding.getJavaElement();
		return null;
	}
	
	public static IJavaElement getJavaElement(FieldDeclaration node) {
		TypeDeclaration typeNode = JavaASTUtil.getParentTypeDecl(node);
		ITypeBinding binding = typeNode.resolveBinding();
		for (IVariableBinding vb : binding.getDeclaredFields()) {
			IJavaElement element = vb.getJavaElement();
			try {
				if (((ISourceReference)element).getSourceRange().getOffset() == node.getStartPosition())
					return element;

			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		}

		return null;
	}
	
	public static IType toIType(TypeDeclaration node) {
		ITypeBinding binding = node.resolveBinding();
		if (binding != null) // Bindings can be null
			return (IType) binding.getJavaElement();
		return null;
	}

	public static ITypeHierarchy newTypeHierarchyOf(IProgressMonitor monitor, TypeDeclaration typeDecl) {
		IType itype = ASTNode2IJavaElementUtil.toIType(typeDecl);
		try {
			return itype.newTypeHierarchy(monitor);
		} catch (JavaModelException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	/** alias for compilationUnit.getJavaElement().. */ 
	public static IJavaElement getJavaElement(CompilationUnit node) {
		return node.getJavaElement();
	}

}
