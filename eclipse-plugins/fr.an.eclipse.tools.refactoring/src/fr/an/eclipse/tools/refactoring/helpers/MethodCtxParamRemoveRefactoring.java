package fr.an.eclipse.tools.refactoring.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

@SuppressWarnings({ "unchecked" })
public class MethodCtxParamRemoveRefactoring extends AbstractParsedCompilationUnitsRefactoring {
	
	private static class CURefactoringInfo {
		List<RemoveMethodParamDeclRefactoringInfo> removeParamDecls;
		List<ReplaceParamValueRefactoringInfo> replaceParamValues;
		public CURefactoringInfo(
				List<RemoveMethodParamDeclRefactoringInfo> removeParamDecls,
				List<ReplaceParamValueRefactoringInfo> replaceParamValues
				) {
			this.removeParamDecls = removeParamDecls;
			this.replaceParamValues = replaceParamValues;
		}
	}
	
	private static class ReplaceParamValueRefactoringInfo {
		MethodInvocation methInvoc;
		Expression paramValue;
		public ReplaceParamValueRefactoringInfo(MethodInvocation methInvoc, Expression paramValue) {
			this.methInvoc = methInvoc;
			this.paramValue = paramValue;
		}
		
	}

	private static class RemoveMethodParamDeclRefactoringInfo {
		MethodDeclaration meth;
		SingleVariableDeclaration param;
		public RemoveMethodParamDeclRefactoringInfo(MethodDeclaration meth, SingleVariableDeclaration param) {
			this.meth = meth;
			this.param = param;
		}		
	}

	private String removeParamType;
	
	// ------------------------------------------------------------------------
	
	public MethodCtxParamRemoveRefactoring(Set<ICompilationUnit> compilationUnits) {
		super(compilationUnits);
	}

	// ------------------------------------------------------------------------
	
	public void setRemoveParamType(String removeParamType) {
		this.removeParamType = removeParamType;
	}
	
	@Override
	public String getName() {
		return "MethodCtxParamRemove";
	}

	@Override
	protected Object prepareRefactorUnit(CompilationUnit unit) throws Exception {
		List<RemoveMethodParamDeclRefactoringInfo> removeParamDecls = new ArrayList<>();
		List<ReplaceParamValueRefactoringInfo> replaceParamValues = new ArrayList<>();
		ASTVisitor visitor = new ASTVisitor() {
			@Override
			public boolean visit(MethodDeclaration node) {
				List<SingleVariableDeclaration> params  = node.parameters();
				if (params != null && !params.isEmpty()) {
					for(SingleVariableDeclaration param : params) {
						// detect if containing a param to remove
						ITypeBinding resolvedType = param.getType().resolveBinding();
						String fqnTypeName = resolvedType.getQualifiedName();
						if (fqnTypeName.equals(removeParamType)) {
							removeParamDecls.add(new RemoveMethodParamDeclRefactoringInfo(node, param));
						}
					}
				}
				return super.visit(node);
			}

			@Override
			public boolean visit(MethodInvocation node) {
				IMethodBinding methBinding = node.resolveMethodBinding();
				if (methBinding != null) {
					ITypeBinding[] parameterTypes = methBinding.getParameterTypes();
					for(int i = 0; i < parameterTypes.length; i++) {
						if (parameterTypes[i].getQualifiedName().equals(removeParamType)) {
							List<Expression> arguments = node.arguments();
							Expression expr = arguments.get(i);
							replaceParamValues.add(new ReplaceParamValueRefactoringInfo(node, expr));
						}
					}
				}
				return super.visit(node);
			}
			
		};
		unit.accept(visitor);
				
		if (removeParamDecls.isEmpty() && replaceParamValues.isEmpty()) {
			return null;
		}
		return new CURefactoringInfo(removeParamDecls, replaceParamValues);
	}

	@Override
	protected void doRefactorUnit(CompilationUnit unit, Object prepared) {
		CURefactoringInfo cuRefactoring = (CURefactoringInfo) prepared;
		for(RemoveMethodParamDeclRefactoringInfo r : cuRefactoring.removeParamDecls) {
			MethodDeclaration meth = r.meth;
			List<SingleVariableDeclaration> parameters = meth.parameters();
			parameters.remove(r.param);
		}
		for(ReplaceParamValueRefactoringInfo r : cuRefactoring.replaceParamValues) {
			List<Expression> arguments = r.methInvoc.arguments();
			arguments.remove(r.paramValue);
		}
	}
	
}
