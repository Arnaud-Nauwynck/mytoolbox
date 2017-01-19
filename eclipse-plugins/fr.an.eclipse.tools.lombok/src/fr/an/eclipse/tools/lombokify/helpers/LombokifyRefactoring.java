package fr.an.eclipse.tools.lombokify.helpers;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

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

	protected boolean useGetterSetter = true;
	

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
	}
	
	protected void doRefactorUnit_GetterSetter(CompilationUnit unit) {
		ASTVisitor vis = new ASTVisitor() {
			@Override
			public boolean visit(TypeDeclaration node) {
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
						for(VariableDeclarationFragment fieldDecl : fieldDecls) {
							String fieldName = fieldDecl.getName().getIdentifier();
							FieldGetterSetterDetection fo = fieldInfos.get(fieldName);
							if (fo != null) {
								fo.fieldDecl = fieldDecl;
							
								if (fo.getterDecl != null) {
									// refactor: add lombok annotation @Getter .. delete getter method
									JavaASTUtil.addMarkerAnnotation(unit, fo.getterDecl.modifiers(), 
											LOMBOK_PACKAGE, ANNOTATION_LOMBOK_GETTER_SIMPLENAME);
									fo.getterDecl.delete();
								}
								if (fo.setterDecl != null) {
									// refactor: add lombok annotation @Setter .. delete setter method
									JavaASTUtil.addMarkerAnnotation(unit, fo.getterDecl.modifiers(), 
											LOMBOK_PACKAGE, ANNOTATION_LOMBOK_SETTER_SIMPLENAME);
									fo.setterDecl.delete();
								}
							}							
						}
					}
				}
				
				return super.visit(node);
			}
			
		};
		unit.accept(vis);
	}
	
	protected static FieldGetterSetterDetection getOrCreateField(Map<String,FieldGetterSetterDetection> fieldInfos, String name) {
		FieldGetterSetterDetection res = fieldInfos.get(name);
		if (res == null) {
			res = new FieldGetterSetterDetection(name);
			fieldInfos.put(name, res);
		}
		return res;
	}

	
}
