package fr.an.eclipse.tools.refactoring.helpers;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import fr.an.eclipse.pattern.helper.AbstractASTRewriteRefactoringHelper;
import fr.an.eclipse.tools.refactoring.Activator;

/**
 * refactoring helper class, for swaping 
 * <code>var.equals("string literal"</code>" 
 * --> <code>stringliteral".equals(var)</code>
 */
public class SwapVarEqualsStringRefactoringHelper extends AbstractASTRewriteRefactoringHelper {

	private static class RefactoringInfo {
		final List<SwapVarEqualsRefactoringInfo> ls = new ArrayList<SwapVarEqualsRefactoringInfo>();
	}
	
	private static class SwapVarEqualsRefactoringInfo {
		MethodInvocation equalsMethodInvocation;
		Expression lhsExpr; // TODO stricter type as SimpleName??
		StringLiteral rhsString;
		
		public SwapVarEqualsRefactoringInfo(MethodInvocation equalsMethodInvocation, Expression lhsExpr, StringLiteral rhsString) {
			super();
			this.equalsMethodInvocation = equalsMethodInvocation;
			this.lhsExpr = lhsExpr;
			this.rhsString = rhsString;
		}
		
	}
	
	public SwapVarEqualsStringRefactoringHelper(IProgressMonitor monitor, Set<ICompilationUnit> selection) {
		super(monitor, selection);
	}
	
	// -------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	@Override
	protected Object prepareRefactorUnit(CompilationUnit unit) throws Exception {
		final RefactoringInfo res = new RefactoringInfo();
		
		// find corresponding ClassDescriptor 
		List<AbstractTypeDeclaration> jdt_types = (List<AbstractTypeDeclaration>) unit.types();
		if (jdt_types == null || jdt_types.size() == 0) return null;
		AbstractTypeDeclaration jdt_abstracttype = (AbstractTypeDeclaration) jdt_types.get(0);
		if (!(jdt_abstracttype instanceof TypeDeclaration)) return null;
		TypeDeclaration jdt_type = (TypeDeclaration) jdt_abstracttype;
		
		ASTVisitor visitor = new ASTVisitor() {
			@Override
			public boolean visit(MethodInvocation node) {
				Name methName = node.getName();
				Expression e = node.getExpression();
				Expression a = (node.arguments().size() == 1)? (Expression) node.arguments().get(0) : null;
				if (node.arguments().size() == 1
						&& a instanceof StringLiteral
						&& e != null
						&& e instanceof SimpleName
						&& methName.isSimpleName() 
						&& "equals".equals(((SimpleName) methName).getIdentifier())
						) {
					StringLiteral strLiteral = (StringLiteral) a;
					// Activator.logDebug("swap var.equals(string) in : " + node);
					res.ls.add(new SwapVarEqualsRefactoringInfo(node, e, strLiteral));
				}
				
				return super.visit(node);
			}
			
		};
		jdt_type.accept(visitor);
		
		if (res.ls.isEmpty()) {
			return null;
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void doRefactorUnit(CompilationUnit unit, Object refactoringInfoObject) throws Exception {
		RefactoringInfo refactoringInfo = (RefactoringInfo) refactoringInfoObject;
		for(SwapVarEqualsRefactoringInfo swapVarInfo : refactoringInfo.ls) {
			MethodInvocation m = swapVarInfo.equalsMethodInvocation;
			Expression e = swapVarInfo.lhsExpr;
			StringLiteral a = swapVarInfo.rhsString;

			m.arguments().remove(0); // should clone for moving??
			m.setExpression(a);
			m.arguments().add(e);
		}
	}
	
}
