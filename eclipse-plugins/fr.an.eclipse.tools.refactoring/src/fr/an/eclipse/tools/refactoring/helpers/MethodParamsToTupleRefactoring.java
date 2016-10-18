package fr.an.eclipse.tools.refactoring.helpers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTVisitor;
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

import fr.an.eclipse.pattern.util.ASTNode2IJavaElementUtil;
import fr.an.eclipse.pattern.util.JavaASTUtil;
import fr.an.eclipse.pattern.util.JavaElement2ASTNodeUtil;
import fr.an.eclipse.pattern.util.JavaElementUtil;
import fr.an.eclipse.pattern.util.JavaNamingUtil;
import fr.an.eclipse.tools.refactoring.Activator;

@SuppressWarnings({ "unchecked", "restriction" })
public class MethodParamsToTupleRefactoring extends AbstractParsedCompilationUnitsRefactoring {

	private static final String FQN_Path = "javax.ws.rs.Path";
	private static final String FQN_POST_TupleBodyFragments = "fr.an.refactor.POST_TupleBodyFragments";
	// private static final String NAME_RequestBody = "RequestBody";
	
	private static class CURefactoringInfo {
		List<TypeDeclMethodRefactoringInfo> ls;
		public CURefactoringInfo(List<TypeDeclMethodRefactoringInfo> ls) {
			this.ls = ls;
		}
	}

	private static class TypeDeclMethodRefactoringInfo {
		List<MethodRefactoringInfo> ls;
		public TypeDeclMethodRefactoringInfo(List<MethodRefactoringInfo> ls) {
			this.ls = ls;
		}
	}

	private static class ParamRefactoring {
		int index;
		Type paramType;
		String paramName; // paramName ... equals request.fieldName
		public ParamRefactoring(int index, Type paramType, String paramName) {
			this.index = index;
			this.paramType = paramType;
			this.paramName = paramName;
		}
	}
	private static class MethodRefactoringInfo {
		MethodDeclaration meth;
		String requestClassName;
		String requestClassFQN;
		String reqParamName = "req"; // assume not ambiguous (not already used in method body)
		List<ParamRefactoring> paramRefactorings;
		public MethodRefactoringInfo(MethodDeclaration meth, String requestClassName, String requestClassFQN, List<ParamRefactoring> paramRefactorings) {
			this.meth = meth;
			this.requestClassName = requestClassName;
			this.requestClassFQN = requestClassFQN;
			this.paramRefactorings = paramRefactorings;
		}		
	}

	private static class OverrideIMethod {
		MethodRefactoringInfo methRefactoring;
		IMethod methOverride; // cf OverrideMethodRefactoringInfo to lookup corresponding MethodDeclaration
		public OverrideIMethod(MethodRefactoringInfo methRefactoring, IMethod methOverride) {
			this.methRefactoring = methRefactoring;
			this.methOverride = methOverride;
		}		
	}
	private static class SubCURefactoringInfo {
		CompilationUnit subTypesCU;
		List<SubTypeDeclRefactoringInfo> ls;
		public SubCURefactoringInfo(CompilationUnit subTypesCU, List<SubTypeDeclRefactoringInfo> ls) {
			this.subTypesCU = subTypesCU;
			this.ls = ls;
		}
	}
	private static class SubTypeDeclRefactoringInfo {
		TypeDeclaration subType;
		List<OverrideMethodRefactoringInfo> ls;
		public SubTypeDeclRefactoringInfo(TypeDeclaration subType, List<OverrideMethodRefactoringInfo> ls) {
			this.subType = subType;
			this.ls = ls;
		}
	}
	private static class OverrideMethodRefactoringInfo {
		MethodRefactoringInfo superMethRefactoring;
		// IMethod imethOverride; 
		MethodDeclaration overrideMethDecl;
		public OverrideMethodRefactoringInfo(MethodRefactoringInfo superMethRefactoring, MethodDeclaration overrideMethDecl) {
			this.superMethRefactoring = superMethRefactoring;
			this.overrideMethDecl = overrideMethDecl;
		}
		
	}
	
	private static class PreparedDependentRefactor {
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
		final List<TypeDeclMethodRefactoringInfo> resClassRefactoringInfos = new ArrayList<>();
		
		List<TypeDeclaration> refactorableTypeDecls = scanRefactorableTypeDecls(unit);
		if (refactorableTypeDecls != null && !refactorableTypeDecls.isEmpty()) {
			for(TypeDeclaration typeDecl : refactorableTypeDecls) {
				List<MethodRefactoringInfo> methRefactorings = new ArrayList<>();
				ASTVisitor visitor = new ASTVisitor() {
					@Override
					public boolean visit(MethodDeclaration node) {
						List<SingleVariableDeclaration> params = node.parameters();
						if (params.size() > 1) {
							List<IExtendedModifier> modifiers = node.modifiers();
							// detect if this is a jax-rs method (@GET, @POST, @PUT, @DELETE, ... with @Path ... ) 
							if (null != JavaASTUtil.findFirstAnnotation(modifiers, FQN_POST_TupleBodyFragments)) {
								MethodRefactoringInfo tmp = prepareRefactorUnit_Method(node);
								methRefactorings.add(tmp);
							}
						}
						return super.visit(node);
					}
				};
				typeDecl.accept(visitor);
				
				resClassRefactoringInfos.add(finishPrepareTypeDeclRefactoringMethods(typeDecl, methRefactorings));
			}
		}
		
		if (resClassRefactoringInfos.isEmpty()) {
			return null;
		}
		return new CURefactoringInfo(resClassRefactoringInfos);
	}

	protected List<TypeDeclaration> scanRefactorableTypeDecls(CompilationUnit unit) {
		 final List<TypeDeclaration> res = new ArrayList<>();
		 ASTVisitor visitor = new ASTVisitor() {
				@Override
				public boolean visit(TypeDeclaration node) {
					List<IExtendedModifier> modifiers = node.modifiers();
					// detect if this is a jax-rs class (with @Path ... @Component or @Service) 
					if (null != JavaASTUtil.findFirstAnnotation(modifiers, FQN_Path)) {
						res.add(node);
					}
					return super.visit(node);
				}
		 };
		 unit.accept(visitor);
		 return res;
	}
	
	private MethodRefactoringInfo prepareRefactorUnit_Method(MethodDeclaration meth) {
		// find method invocations for this refactored declaration
		String requestTypeName = JavaNamingUtil.capitalize(meth.getName().getIdentifier()) + "Request";
		String requestClassFQN = meth.resolveBinding().getDeclaringClass().getQualifiedName() + "." + requestTypeName;
		
		List<ParamRefactoring> paramRefactorings = new ArrayList<>();
		List<SingleVariableDeclaration> params = meth.parameters();
		int paramIndex = 0;
		for(SingleVariableDeclaration param : params) {
			// TODO check if param should be kept: when having another annotation, like @RequestHeader @QueryParam, ... 
			Type paramType = param.getType();
			paramRefactorings.add(new ParamRefactoring(paramIndex, paramType, param.getName().getIdentifier()));
			paramIndex++;
		}
		MethodRefactoringInfo resMethRefactor = new MethodRefactoringInfo(meth, requestTypeName, requestClassFQN, paramRefactorings );

		// search caller methods
		IMethod imeth = ASTNode2IJavaElementUtil.bindingToIMethod(meth);
		final List<SourceMethod> methodRefs = JavaASTUtil.searchMethodInvocations(imeth);
		if (!methodRefs.isEmpty()) {
			for(SourceMethod methodRef : methodRefs) {
				ICompilationUnit cuRef = methodRef.getCompilationUnit();
				Function<CompilationUnit,PreparedDependentRefactor> prepareDep = depCu -> doPrepareDependentUnit_callerMeth(depCu, resMethRefactor, methodRef);
				BiConsumer<CompilationUnit,PreparedDependentRefactor> updateDep = (depCu,prepared) -> doUpdateDependentUnit_callerMeth(depCu, prepared);
				addDependentCompilationUnitTreatment(cuRef, prepareDep, updateDep);
			}
		}
		return resMethRefactor;
	}

	protected TypeDeclMethodRefactoringInfo finishPrepareTypeDeclRefactoringMethods(TypeDeclaration typeDecl, List<MethodRefactoringInfo> methRefactorings) {
		TypeDeclMethodRefactoringInfo res = new TypeDeclMethodRefactoringInfo(methRefactorings);

		IType itype = ASTNode2IJavaElementUtil.toIType(typeDecl);
		ITypeHierarchy typeHierarchy = ASTNode2IJavaElementUtil.newTypeHierarchyOf(monitor, typeDecl);
		Map<ICompilationUnit,List<OverrideIMethod>> subICU2Meths = new LinkedHashMap<>();
		for(MethodRefactoringInfo methRefactoring : methRefactorings) {
			MethodDeclaration meth = methRefactoring.meth;
			IMethod imeth = ASTNode2IJavaElementUtil.bindingToIMethod(meth);
			// search sub-class method override
			// find (or cache) TypeHierarchy for sub-classes of type
			List<IMethod> imethOverrides = JavaElementUtil.findSubTypeMethodsOverride(itype, imeth, typeHierarchy);
			if (imethOverrides != null && !imethOverrides.isEmpty()) {
				for(IMethod imethOverride : imethOverrides) {
					IType subIType = (IType) imethOverride.getAncestor(IJavaElement.TYPE);
					ICompilationUnit subICU = (ICompilationUnit) subIType.getAncestor(IJavaElement.COMPILATION_UNIT);
					if (subICU == null) {
						// no source found for sub-type?
						continue;
					}
					List<OverrideIMethod> tmpls = subICU2Meths.get(subICU);
					if (tmpls == null) {
						tmpls = new ArrayList<>();
						subICU2Meths.put(subICU, tmpls);
					}
					tmpls.add(new OverrideIMethod(methRefactoring, imethOverride));
				}
			}
		}

		for(Map.Entry<ICompilationUnit,List<OverrideIMethod>> e : subICU2Meths.entrySet()) {
			ICompilationUnit subICU = e.getKey();
			List<OverrideIMethod> overrideMethodRefactorings = e.getValue();
			
			Function<CompilationUnit,SubCURefactoringInfo> prepareDep = depCu -> doPrepareDependentUnit_methodOverride(depCu, overrideMethodRefactorings);
			BiConsumer<CompilationUnit,SubCURefactoringInfo> updateDep = (depCu,prepared) -> doUpdateDependentUnit_methodOverride(depCu, prepared);
			addDependentCompilationUnitTreatment(subICU, prepareDep, updateDep);
		}
		return res;
	}
	

	private SubCURefactoringInfo doPrepareDependentUnit_methodOverride(CompilationUnit subTypeCU, List<OverrideIMethod> overrideMethodRefactorings) {
		List<SubTypeDeclRefactoringInfo> subTypeRefactorings = new ArrayList<>();
		for(TypeDeclaration subType : (List<TypeDeclaration>)subTypeCU.types()) {
			List<OverrideMethodRefactoringInfo> subMethRefactorings = new ArrayList<>();
			
			for(OverrideIMethod overrideMethodRefactoring : overrideMethodRefactorings) {
				// lookup MethodDeclaration in subTypeDecl corresponding to IMethod
				MethodDeclaration overrideMeth = JavaElement2ASTNodeUtil.getMethodDeclarationNode(overrideMethodRefactoring.methOverride, subTypeCU);
				if (overrideMeth == null) {
					Activator.logWarning("override method (from type hierarchy) not found in AST CompilationUnit:" + overrideMethodRefactoring.methOverride);
					continue;
				}
				
				subMethRefactorings.add(new OverrideMethodRefactoringInfo(overrideMethodRefactoring.methRefactoring, overrideMeth));
			}
			
			if (! subMethRefactorings.isEmpty()) {
				subTypeRefactorings.add(new SubTypeDeclRefactoringInfo(subType, subMethRefactorings));
			}
		}
		return new SubCURefactoringInfo(subTypeCU, subTypeRefactorings);
	}
	
	private void doUpdateDependentUnit_methodOverride(CompilationUnit subTypeCU, SubCURefactoringInfo prepared) {
		for (SubTypeDeclRefactoringInfo subTypeDeclRefactoring : prepared.ls) {
			for(OverrideMethodRefactoringInfo overrideMethRefactoring : subTypeDeclRefactoring.ls) {
				MethodDeclaration methDecl = overrideMethRefactoring.overrideMethDecl;
				MethodRefactoringInfo methRefactoring = overrideMethRefactoring.superMethRefactoring;
				doRefactor_addLocalRequestVars(methDecl, methRefactoring);
				doRefactor_changeSignatureParamsRequest(methDecl, methRefactoring);
			}
		}
	}


	
	protected PreparedDependentRefactor doPrepareDependentUnit_callerMeth(CompilationUnit unit, 
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
	protected void doRefactorUnit(CompilationUnit unit, Object prepared) {
		CURefactoringInfo cuRefactoring = (CURefactoringInfo) prepared;
		for (TypeDeclMethodRefactoringInfo typeDeclRefactoring : cuRefactoring.ls) {
			for(MethodRefactoringInfo methRefactoring : typeDeclRefactoring.ls) {
				MethodDeclaration meth = methRefactoring.meth;
				
				doRefactor_addRequestClassDecl(meth, methRefactoring);
				
				doRefactor_addLocalRequestVars(meth, methRefactoring);
				
				doRefactor_changeSignatureParamsRequest(meth, methRefactoring);
			}
			// addMsg("Found " + typeDeclRefactoring.ls.size() + " " + FQN_POST_TupleBodyFragments);
		}
	}

	private void doRefactor_changeSignatureParamsRequest(MethodDeclaration meth, MethodRefactoringInfo methRefactoring) {
		AST ast = meth.getAST();
		List<SingleVariableDeclaration> paramDecls = meth.parameters();
		for(int i = methRefactoring.paramRefactorings.size()-1; i >= 0; i--) {
			int paramIndex = methRefactoring.paramRefactorings.get(i).index;
			paramDecls.remove(paramIndex);
		}
		SingleVariableDeclaration reqParamDecl = ast.newSingleVariableDeclaration();
		// MarkerAnnotation requestBodyAnnotation = ast.newMarkerAnnotation();
		// requestBodyAnnotation.setTypeName(ast.newSimpleName(NAME_RequestBody));
		// reqParamDecl.modifiers().add(requestBodyAnnotation);
		reqParamDecl.setType(ast.newSimpleType(ast.newSimpleName(methRefactoring.requestClassName)));
		reqParamDecl.setName(ast.newSimpleName(methRefactoring.reqParamName));
		paramDecls.add(reqParamDecl);
	}

	/**
	 * for a given "RetType  fooMeth(Request req) { // where class Request { Type1 arg1; Type2 arg2; .. }
	 * add lcoal variables
	 * <PRE>
	 *   Type1 arg1 = req.arg1; Type2 arg2 = req.arg2; ..
	 * </PRE>
	 * @param meth
	 */
	private void doRefactor_addLocalRequestVars(MethodDeclaration meth, MethodRefactoringInfo methRefactoring) {
		Block body = meth.getBody();
		if (body == null) { 
			// interface..
			return;
		}
		final AST ast = meth.getAST();
		String reqParamName = methRefactoring.reqParamName;
		List<Statement> stmts = body.statements();
		int insertIndex = 0;
		for(ParamRefactoring paramRefactoring : methRefactoring.paramRefactorings) {			
			Type paramType = paramRefactoring.paramType;
			String paramName = paramRefactoring.paramName;
			
			VariableDeclarationFragment localVarDeclFrag = ast.newVariableDeclarationFragment();
			localVarDeclFrag.setName(ast.newSimpleName(paramName));
			FieldAccess reqFieldAcces = ast.newFieldAccess();
			reqFieldAcces.setExpression(ast.newSimpleName(reqParamName));
			reqFieldAcces.setName(ast.newSimpleName(paramName));
			localVarDeclFrag.setInitializer(reqFieldAcces);
			VariableDeclarationStatement locaVarDecl = ast.newVariableDeclarationStatement(localVarDeclFrag);
			locaVarDecl.setType(JavaASTUtil.cloneASTNode(paramType, ast)); // /can not clone paramType : ast of override meth subType != ast superType  ..  JavaASTUtil.cloneASTNode(paramType));
			stmts.add(insertIndex, locaVarDecl);
			insertIndex++;
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
	private void doRefactor_addRequestClassDecl(MethodDeclaration meth, MethodRefactoringInfo methRefactoring) {
		final AST ast = meth.getAST();
		String requestTypeName = methRefactoring.requestClassName;
		TypeDeclaration requestTypeDecl = ast.newTypeDeclaration();
		requestTypeDecl.setName(ast.newSimpleName(requestTypeName));
		requestTypeDecl.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		requestTypeDecl.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.STATIC_KEYWORD));
		// add fields
		for(ParamRefactoring paramRefactoring : methRefactoring.paramRefactorings) {
			Type paramType = paramRefactoring.paramType;
			String paramName = paramRefactoring.paramName;

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
		for(ParamRefactoring paramRefactoring : methRefactoring.paramRefactorings) {
			Type paramType = paramRefactoring.paramType;
			String paramName = paramRefactoring.paramName;
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
	
	protected void doUpdateDependentUnit_callerMeth(CompilationUnit unit, PreparedDependentRefactor prepared) {
		final AST ast = unit.getAST();
		Set<String> importFQNs = new HashSet<>();
		MethodRefactoringInfo methCalleeRefactor = prepared.methCalleeRefactor;
		for(MethodInvocation methCaller : prepared.methodCallers) {
			List<Expression> callerArgs = methCaller.arguments();
			List<Expression> detachedCallerArgs = new ArrayList<>(callerArgs);
			callerArgs.clear();
			ClassInstanceCreation newReq = ast.newClassInstanceCreation();
			String reqClassName = methCalleeRefactor.requestClassName;
			Type type = ast.newSimpleType(ast.newSimpleName(reqClassName));
			newReq.setType(type );
			for(Expression e : detachedCallerArgs) {
				newReq.arguments().add(e);
			}
			callerArgs.add(newReq);
			
			importFQNs.add(methCalleeRefactor.requestClassFQN);
		}
		
		JavaASTUtil.addImports(unit, importFQNs);
	}


}
