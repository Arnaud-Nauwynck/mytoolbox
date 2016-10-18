package fr.an.eclipse.tools.refactoring.helpers;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.internal.core.SourceMethod;

import fr.an.eclipse.pattern.util.JavaASTUtil;
import fr.an.eclipse.pattern.util.JavaNamingUtil;

@SuppressWarnings("unchecked")
public class MethodParamsToTupleRefactoring extends AbstractParsedCompilationUnitsRefactoring {

	private static final String FQN_Path = "javax.ws.rs.Path";
	private static final String FQN_POST_TupleBodyFragments = "fr.an.refactor.POST_TupleBodyFragments";
	// private static final String NAME_RequestBody = "RequestBody";
	
	private static class RefactoringInfo {
		List<MethodRefactoringInfo> ls = new ArrayList<>();
	}
	
	private static class MethodRefactoringInfo {
		MethodDeclaration meth;
		String requestClassName;
		
		public MethodRefactoringInfo(MethodDeclaration meth, String requestClassName) {
			this.meth = meth;
			this.requestClassName = requestClassName;
		}
		
	}

	protected static class PreparedDependentRefactor {
		MethodRefactoringInfo methCalleeRefactor;
		Set<MethodInvocation> methodCallers;
		
		public PreparedDependentRefactor(MethodRefactoringInfo methCalleeRefactor, Set<MethodInvocation> methodCallers) {
			this.methCalleeRefactor = methCalleeRefactor;
			this.methodCallers = methodCallers;
		}
		
	}
	

	// ------------------------------------------------------------------------
	
	public MethodParamsToTupleRefactoring(Set<ICompilationUnit> compilationUnits) {
		super(compilationUnits);
	}

	// ------------------------------------------------------------------------
	

	@Override
	public String getName() {
		return "MethodParamsToTuple";
	}


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
			public boolean visit(TypeDeclaration node) {
				List<IExtendedModifier> modifiers = node.modifiers();
				// detect if this is a jax-rs class (with @Path ... @Component or @Service) 
				if (null == JavaASTUtil.findFirstAnnotation(modifiers, FQN_Path)) {
					return false; // TODO recurse in inner classes?
				}
				return super.visit(node);
			}

			@Override
			public boolean visit(MethodDeclaration node) {
				List<SingleVariableDeclaration> params = node.parameters();
				if (params.size() > 1) {
					List<IExtendedModifier> modifiers = node.modifiers();
					// detect if this is a jax-rs method (@GET, @POST, @PUT, @DELETE, ... with @Path ... ) 
					if (null == JavaASTUtil.findFirstAnnotation(modifiers, FQN_POST_TupleBodyFragments)) {
						return false;
					}
					prepareRefactorUnit_Method(res, node);				
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

	private void prepareRefactorUnit_Method(final RefactoringInfo res, MethodDeclaration node) {
		// find method invocations for this refactored declaration
		IMethodBinding methBinding = node.resolveBinding();
		IMethod meth = (IMethod) methBinding.getJavaElement();

		String requestTypeName = JavaNamingUtil.capitalize(node.getName().getIdentifier()) + "Request";

		MethodRefactoringInfo methRefactor = new MethodRefactoringInfo(node, requestTypeName);
		res.ls.add(methRefactor);

		final List<SourceMethod> methodRefs = JavaASTUtil.searchMethodInvocations(meth);
		if (!methodRefs.isEmpty()) {
			for(SourceMethod methodRef : methodRefs) {
				ICompilationUnit cuRef = methodRef.getCompilationUnit();
				Function<CompilationUnit,PreparedDependentRefactor> prepareDep = depCu -> doPrepareDependentUnit(depCu, methRefactor, methodRef);
				BiConsumer<CompilationUnit,PreparedDependentRefactor> updateDep = (depCu,prepared) -> doUpdateDependentUnit(depCu, prepared);
				addDependentCompilationUnitTreatment(cuRef, prepareDep, updateDep);
			}
		}
	}
	
	protected PreparedDependentRefactor doPrepareDependentUnit(CompilationUnit unit, 
			MethodRefactoringInfo methCalleeRefactor, SourceMethod methodCallerRef) {
		final Set<MethodInvocation> methodCallers = new LinkedHashSet<>();
		final IMethodBinding calleeMethodBinding = methCalleeRefactor.meth.resolveBinding();
		ASTVisitor visitor = new ASTVisitor() {
			@Override
			public boolean visit(MethodInvocation node) {
				IMethodBinding methBind = node.resolveMethodBinding();
				if (methBind.isEqualTo(calleeMethodBinding)) {
					methodCallers.add(node);
				}
				return super.visit(node);
			}
		};
		unit.accept(visitor);
		return new PreparedDependentRefactor(methCalleeRefactor, methodCallers);
	}
	
	
	@Override
	protected void doRefactorUnit(CompilationUnit unit, Object refactoringInfoObject) {
		AST ast = unit.getAST();
		RefactoringInfo refactoringInfo = (RefactoringInfo) refactoringInfoObject;
		for(MethodRefactoringInfo methInfo : refactoringInfo.ls) {
			MethodDeclaration meth = methInfo.meth;
			List<SingleVariableDeclaration> paramDecls = meth.parameters();
			
			String requestTypeName = methInfo.requestClassName;
			doRefactor_addRequestClassDecl(meth, requestTypeName);
			
			String reqParamName = "req"; // assume not ambiguous (not already used in method body)
			doRefactor_addLocalRequestVars(meth, reqParamName);
			
			// also find sub-class override for meth
			// TODO 
			
			
			paramDecls.clear();
			SingleVariableDeclaration reqParamDecl = ast.newSingleVariableDeclaration();
			// MarkerAnnotation requestBodyAnnotation = ast.newMarkerAnnotation();
			// requestBodyAnnotation.setTypeName(ast.newSimpleName(NAME_RequestBody));
			// reqParamDecl.modifiers().add(requestBodyAnnotation);
			reqParamDecl.setType(ast.newSimpleType(ast.newSimpleName(requestTypeName)));
			reqParamDecl.setName(ast.newSimpleName(reqParamName));
			paramDecls.add(reqParamDecl);
		}
		addMsg("Found " + refactoringInfo.ls.size() + " " + FQN_POST_TupleBodyFragments);
	}

	/**
	 * for a given "RetType  fooMeth(Request req) { // where class Request { Type1 arg1; Type2 arg2; .. }
	 * add lcoal variables
	 * <PRE>
	 *   Type1 arg1 = req.arg1; Type2 arg2 = req.arg2; ..
	 * </PRE>
	 * @param meth
	 */
	private void doRefactor_addLocalRequestVars(MethodDeclaration meth, String reqParamName) {
		final AST ast = meth.getAST();
		final List<SingleVariableDeclaration> paramDecls = meth.parameters();
		Block body = meth.getBody();
		if (body == null) { 
			// interface..
			return;
		}
		List<Statement> stmts = body.statements();
		for(int i = 0; i < paramDecls.size(); i++) {
			SingleVariableDeclaration paramDecl = paramDecls.get(i);
			Type paramType = paramDecl.getType();
			String paramName = paramDecl.getName().getIdentifier();
			
			VariableDeclarationFragment localVarDeclFrag = ast.newVariableDeclarationFragment();
			localVarDeclFrag.setName(ast.newSimpleName(paramName));
			FieldAccess reqFieldAcces = ast.newFieldAccess();
			reqFieldAcces.setExpression(ast.newSimpleName(reqParamName));
			reqFieldAcces.setName(ast.newSimpleName(paramName));
			localVarDeclFrag.setInitializer(reqFieldAcces);
			VariableDeclarationStatement locaVarDecl = ast.newVariableDeclarationStatement(localVarDeclFrag);
			locaVarDecl.setType(JavaASTUtil.cloneASTNode(paramType));
			stmts.add(i, locaVarDecl);
		}
	}

	/**
	 * 	 for a given "RetType fooMeth(Type1 arg1, Type2 arg2..) {}
	 * add 
	 * <PRE> 
	 public static class <<FooMeth>>Request {
	   public Type1 arg1;
	   public Type2 arg2;
	   ..
	   public <<FooMeth>>Request(Type1 arg1, Type2 arg2..) {
	     this.arg1 = arg1; this.arg2 = arg2; ..
	   } 
 	 * </PRE>
	 */	 
	private void doRefactor_addRequestClassDecl(MethodDeclaration meth, String requestTypeName) {
		final AST ast = meth.getAST();
		final List<SingleVariableDeclaration> paramDecls = meth.parameters();
		TypeDeclaration requestTypeDecl = ast.newTypeDeclaration();
		requestTypeDecl.setName(ast.newSimpleName(requestTypeName));
		requestTypeDecl.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		requestTypeDecl.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.STATIC_KEYWORD));
		// add fields
		for(SingleVariableDeclaration paramDecl : paramDecls) {
			Type paramType = paramDecl.getType();
			String paramName = paramDecl.getName().getIdentifier();

			VariableDeclarationFragment fieldVarDecl = ast.newVariableDeclarationFragment();
			fieldVarDecl.setName(ast.newSimpleName(paramName));
			FieldDeclaration fieldDecl = ast.newFieldDeclaration(fieldVarDecl);
			fieldDecl.setType(JavaASTUtil.cloneASTNode(paramType));
			fieldDecl.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
			requestTypeDecl.bodyDeclarations().add(fieldDecl);
		}
		// add constructor
		MethodDeclaration ctorDecl = ast.newMethodDeclaration();
		ctorDecl.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		ctorDecl.setConstructor(true);
		ctorDecl.setName(ast.newSimpleName(requestTypeName));
		Block ctorBody = ast.newBlock();
		ctorDecl.setBody(ctorBody);
		for(SingleVariableDeclaration paramDecl : paramDecls) {
			Type paramType = paramDecl.getType();
			String paramName = paramDecl.getName().getIdentifier();
			SingleVariableDeclaration ctorParamDecl = ast.newSingleVariableDeclaration();
			ctorParamDecl.setName(ast.newSimpleName(paramName));
			ctorParamDecl.setType(JavaASTUtil.cloneASTNode(paramType));
			ctorDecl.parameters().add(ctorParamDecl);
			// this.arg = arg
			Assignment newAssignment = ast.newAssignment();
			FieldAccess thisFieldAcces = ast.newFieldAccess();
			thisFieldAcces.setExpression(ast.newThisExpression());
			thisFieldAcces.setName(ast.newSimpleName(paramName));
			newAssignment.setLeftHandSide(thisFieldAcces);
			newAssignment.setRightHandSide(ast.newSimpleName(paramName));
			ctorBody.statements().add(ast.newExpressionStatement(newAssignment));
		}
		
		requestTypeDecl.bodyDeclarations().add(ctorDecl);
		
		JavaASTUtil.insertBefore(meth, requestTypeDecl);
	}
	
	protected void doUpdateDependentUnit(CompilationUnit unit, PreparedDependentRefactor prepared) {
		final AST ast = unit.getAST();
		for(MethodInvocation methCaller : prepared.methodCallers) {
			List<Expression> callerArgs = methCaller.arguments();
			List<Expression> detachedCallerArgs = new ArrayList<>(callerArgs);
			callerArgs.clear();
			ClassInstanceCreation newReq = ast.newClassInstanceCreation();
			String reqClassName = prepared.methCalleeRefactor.requestClassName;
			Type type = ast.newSimpleType(ast.newSimpleName(reqClassName));
			newReq.setType(type );
			for(Expression e : detachedCallerArgs) {
				newReq.arguments().add(e);
			}
			callerArgs.add(newReq);
		}
	}


}
