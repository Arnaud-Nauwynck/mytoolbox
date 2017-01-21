package fr.an.eclipse.tools.lombokify.helpers;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import fr.an.eclipse.pattern.util.AbstractParsedCompilationUnitsRefactoring;
import fr.an.eclipse.pattern.util.JavaASTUtil;

/**
 * refactoring helper class to Lombokify
 * 
 */
@SuppressWarnings("unchecked")
public class LombokifyRefactoring extends AbstractParsedCompilationUnitsRefactoring {
	
	private static final String LOMBOK_PACKAGE = "lombok";
	private static final String ANNOTATION_LOMBOK_GETTER_SIMPLENAME = "Getter";
	private static final String ANNOTATION_LOMBOK_SETTER_SIMPLENAME = "Setter";
	private static final String LOMBOK_VAL = "val";
	private static final String LOMBOK_VAR = "var";

	protected boolean useGetterSetter = true;
	protected boolean useValVar = true;
	

	protected static class FieldGetterSetterDetection {
		final String fieldName;
		VariableDeclarationFragment fieldDecl;
		MethodDeclaration getterDecl;
		MethodDeclaration setterDecl;
		
		public FieldGetterSetterDetection(String fieldName) {
			this.fieldName = fieldName;
		}
	}
	
	
	// ------------------------------------------------------------------------
	
	public LombokifyRefactoring(Set<ICompilationUnit> selection) {
		super(selection);
	}
	
	// -------------------------------------------------------------------------

	public void setUseGetterSetter(boolean value) {
		this.useGetterSetter = value;
	}

	public void setUseValVar(boolean value) {
		this.useValVar = value;
	}


	@Override
	public String getName() {
		return "Lombokify";
	}

	@Override
	protected Object prepareRefactorUnit(CompilationUnit unit) throws Exception {
		return Boolean.TRUE;
	}
	
	
	@Override
	protected void doRefactorUnit(CompilationUnit unit, Object refactoringInfoObject) {
		if (useGetterSetter) {
			doRefactorUnit_GetterSetter(unit);
		}
		if (useValVar) {
			doRefactorUnit_ValVar(unit);
		}
	}
	
	protected void doRefactorUnit_GetterSetter(CompilationUnit unit) {
		ASTVisitor vis = new ASTVisitor() {
			@Override
			public boolean visit(TypeDeclaration node) {
				doVisitRefactorGetterSetter(unit, node);
				return super.visit(node);
			}
		};
		unit.accept(vis);
	}

	private void doVisitRefactorGetterSetter(CompilationUnit unit, TypeDeclaration node) {
		List<BodyDeclaration> decls = node.bodyDeclarations();
		Map<String,FieldGetterSetterDetection> fieldInfos = new HashMap<>();
		// scan methods, detect getter/setter
		for(BodyDeclaration decl : decls) {
			if (decl instanceof MethodDeclaration) {
				MethodDeclaration m = (MethodDeclaration) decl;
				String methName = m.getName().getIdentifier();
				if (methName.startsWith("get") || methName.startsWith("is")) {
					String propName = MatchASTUtils.matchBasicGetterMeth(m);
					if (propName != null) {
						FieldGetterSetterDetection fo = getOrCreateField(fieldInfos, propName);
						fo.getterDecl = m;
					}
				} else if (methName.startsWith("set")) {
					String propName = MatchASTUtils.matchBasicSetterMeth(m);
					if (propName != null) {
						FieldGetterSetterDetection fo = getOrCreateField(fieldInfos, propName);
						fo.setterDecl = m;
					}
				}
			}
		}
		// scan fields, lookup corresponding getter/setter
		for(BodyDeclaration decl : decls) {
			if (decl instanceof FieldDeclaration) {
				FieldDeclaration f = (FieldDeclaration) decl;
				List<VariableDeclarationFragment> fieldDecls = f.fragments();
				if (fieldDecls.size() != 1) {
					continue; // unsupported yet! "private Type x, y;" ... only "private Type x;"
				}
				for(VariableDeclarationFragment fieldDecl : fieldDecls) {
					String fieldName = fieldDecl.getName().getIdentifier();
					FieldGetterSetterDetection fo = fieldInfos.get(fieldName);
					if (fo != null) {
						fo.fieldDecl = fieldDecl;
						
						if (fo.setterDecl != null) {
							// refactor: add lombok annotation @Setter .. delete setter method
							JavaASTUtil.addMarkerAnnotation(unit, f.modifiers(), 
									LOMBOK_PACKAGE, ANNOTATION_LOMBOK_SETTER_SIMPLENAME);
							fo.setterDecl.delete();
						}
						if (fo.getterDecl != null) {
							// refactor: add lombok annotation @Getter .. delete getter method
							JavaASTUtil.addMarkerAnnotation(unit, f.modifiers(), 
									LOMBOK_PACKAGE, ANNOTATION_LOMBOK_GETTER_SIMPLENAME);
							fo.getterDecl.delete();
						}
					}							
				}
			}
		}
	}
	
	protected static FieldGetterSetterDetection getOrCreateField(Map<String,FieldGetterSetterDetection> fieldInfos, String name) {
		FieldGetterSetterDetection res = fieldInfos.get(name);
		if (res == null) {
			res = new FieldGetterSetterDetection(name);
			fieldInfos.put(name, res);
		}
		return res;
	}

	private static class RequiredLombokImports {
		private boolean useLombokVar;
		private boolean useLombokVal;

		public void setUseValOrVar(boolean v) {
			if (v) {
				useLombokVal = true;
			} else {
				useLombokVar = true;
			}
		}
		public void setUseVal() {
			useLombokVal = true;
		}
		public void addImports(CompilationUnit unit) {
			if (useLombokVal) {
				JavaASTUtil.addImport(unit, "lombok.val");
			}
			if (useLombokVar) {
				JavaASTUtil.addImport(unit, "lombok.var");
			}
		}
	}
	
	protected void doRefactorUnit_ValVar(CompilationUnit unit) {
		RequiredLombokImports requiredImports = new RequiredLombokImports();
		ASTVisitor vis = new ASTVisitor() {
			@Override
			public boolean visit(VariableDeclarationStatement node) {
				// detect pattern "Type varName = XXX"
				Expression varDeclInit = MatchASTUtils.matchVarDeclSingleInitializer(node);
				if (varDeclInit == null) {
					return super.visit(node);
				}
				Type lhsType = node.getType();
				boolean needReplaceByVal = needReplaceTypeNameToVar(lhsType);
				if (! needReplaceByVal) {
					return super.visit(node);
				}
				if (MatchASTUtils.matchClassInstanceCreationDiamon(varDeclInit)) {
					// detected jdk8 diamond syntax: example: "List<SomeType> ls = new ArrayList<>();" 
					// need to extract type from left hand side type decl, and put it back to right hand side!
					if (lhsType.isParameterizedType()) {
						List<Type> lhsTypeParams = ((ParameterizedType) lhsType).typeArguments();
						// clone lhs type and add to rhs
						ClassInstanceCreation rhsNew = (ClassInstanceCreation) varDeclInit;
						ParameterizedType rhsNewType = (ParameterizedType) rhsNew.getType();
						rhsNewType.typeArguments().addAll(JavaASTUtil.cloneASTNodeList(lhsTypeParams, unit.getAST()));
					}
				}

				// Transform "Type varName = XXX" into "var varName = XX"
				// check if variable is const or re-assigned later
				VariableDeclarationStatement vdecl = (VariableDeclarationStatement) node;
				List<IExtendedModifier> modifiers = vdecl.modifiers();
				int finalModifierIdx = MatchASTUtils.findModifier(modifiers, ModifierKeyword.FINAL_KEYWORD);
				boolean isConst = (finalModifierIdx != -1);
				if (!isConst) {
					// TODO detect if "effective final"
				}
				String lombokTypeRepl = isConst? LOMBOK_VAL : LOMBOK_VAR; 
				requiredImports.setUseValOrVar(isConst);
				if (isConst && (finalModifierIdx != -1)) {
					// transform variable decl "final Type varName" => "val varName"
					modifiers.remove(finalModifierIdx);
				}
				AST ast = unit.getAST();
				vdecl.setType(ast.newSimpleType(ast.newName(lombokTypeRepl)));
				
				return super.visit(node);
			}
			
			@Override
			public boolean visit(EnhancedForStatement node) {
				SingleVariableDeclaration forVarDecl = node.getParameter();
				boolean needReplaceByVal = needReplaceTypeNameToVar(forVarDecl.getType());
				if (needReplaceByVal) {
					// do replace "for(Type varName : ..) .." => "for(var varName : ..) .."
					AST ast = unit.getAST();
					forVarDecl.setType(ast.newSimpleType(ast.newName(LOMBOK_VAL)));
					requiredImports.setUseVal();
				}				
				return super.visit(node);
			}
		};
		unit.accept(vis);
		requiredImports.addImports(unit);
	}

	private static boolean needReplaceTypeNameToVar(Type varDeclType) {
		String varDeclTypeName = MatchASTUtils.matchSimpleTypeName(varDeclType);
		if (varDeclTypeName != null && (varDeclTypeName.equals(LOMBOK_VAL) || varDeclTypeName.equals(LOMBOK_VAR))) {
			return false; // ok, already a lombok type!
		}
		if (varDeclType.isPrimitiveType() || "String".equals(varDeclTypeName)) {
			// do not replace already primitive type int, double, boolean, and also String...  
			return false;
		}
		return true;
	}

}
