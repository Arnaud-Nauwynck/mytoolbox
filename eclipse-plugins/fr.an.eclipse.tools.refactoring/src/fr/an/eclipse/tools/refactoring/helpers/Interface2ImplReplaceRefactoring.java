package fr.an.eclipse.tools.refactoring.helpers;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;

import fr.an.eclipse.pattern.util.AbstractParsedCompilationUnitsRefactoring;
import fr.an.eclipse.pattern.util.JavaASTUtil;
import fr.an.eclipse.tools.refactoring.Activator;

/**
 * refactoring helper class, for replacing interface by impl
 * 
 * typically used for ejb @Local interfaces ... moving to simpler SpringFramework code   
 */
@SuppressWarnings("unchecked")
public class Interface2ImplReplaceRefactoring extends AbstractParsedCompilationUnitsRefactoring {

	private static class RefactoringInfo {
		List<FieldDeclReplacement> fieldDeclRepls = new ArrayList<>();
		List<SingleVarDeclReplacement> singleVarDecRepls = new ArrayList<>();
		List<VariableDeclExprReplacement> varDecExprRepls = new ArrayList<>();
		public boolean isEmpty() {
			return fieldDeclRepls.isEmpty() && singleVarDecRepls.isEmpty() && varDecExprRepls.isEmpty();
		}
	}

	private static class FieldDeclReplacement extends Replacement<FieldDeclaration> {
		public FieldDeclReplacement(FieldDeclaration replace, String newTypeName) {
			super(replace, newTypeName);
		}
	}
	private static class SingleVarDeclReplacement extends Replacement<SingleVariableDeclaration> {
		public SingleVarDeclReplacement(SingleVariableDeclaration replace, String newTypeName) {
			super(replace, newTypeName);
		}
	}
	private static class VariableDeclExprReplacement extends Replacement<VariableDeclarationExpression> {
		public VariableDeclExprReplacement(VariableDeclarationExpression replace, String newTypeName) {
			super(replace, newTypeName);
		}
	}
	
	private static class Replacement<T> {
		T replace;
		String newTypeName;
		public Replacement(T replace, String newTypeName) {
			this.replace = replace;
			this.newTypeName = newTypeName;
		}		
	}
	
	private Map<String,String> replacements = new HashMap<>();
	private List<String> scanPackages = new ArrayList<>();
	private boolean detectEjb = true;
	
	// ------------------------------------------------------------------------
	
	public Interface2ImplReplaceRefactoring(Set<ICompilationUnit> selection) {
		super(selection);
	}
	
	public void setReplacements(Map<String, String> p) {
		this.replacements = p;
	}
	
	public void setScanPackages(List<String> p) {
		this.scanPackages = p;
	}
	public void setDetectEjb(boolean value) {
		this.detectEjb = value;
	}
	
	
	// -------------------------------------------------------------------------
	
	@Override
	public String getName() {
		return "replace interface by impl";
	}

	private static class ReplCandidates {
		Set<String> interfaceCandidateFQNs = new HashSet<>();
		Map<String,String> itf2ImplCandidateFQNs = new HashMap<>();
	}
	
	@Override
	protected void onPreRun(IProgressMonitor monitor) {
		super.onPreRun(monitor);
		
		// scan CUs in packages
		ReplCandidates replCandidates = new ReplCandidates();
		JavaASTUtil.scanAndHandlePackagesCompilationUnits(monitor, compilationUnits, scanPackages,
				"analyse to detect interface to impl replacement", cu -> analyzeCandidateReplacements(cu, replCandidates));
		
		Map<String,String> detectedRepls = new HashMap<>();
		for(String itfFqn : replCandidates.interfaceCandidateFQNs) {
			String replFqn = replCandidates.itf2ImplCandidateFQNs.get(itfFqn);
			if (replFqn != null) {
				detectedRepls.put(itfFqn, replFqn);
			}
		}
		if (! detectedRepls.isEmpty()) {
			Activator.logInfo("detected " + detectedRepls.size() + " interface to class repalcement(s): " + detectedRepls);
			replacements = new HashMap<>((replacements != null)? replacements : Collections.emptyMap());
			replacements.putAll(detectedRepls);
		}
	}
	
	private void analyzeCandidateReplacements(CompilationUnit cu, ReplCandidates replCandidates) {
		cu.accept(new ASTVisitor() {
			@Override 
			public boolean visit(TypeDeclaration node) {
				String thisFQN = node.resolveBinding().getQualifiedName();
				if (node.isInterface()) {
					List<IExtendedModifier> modifiers = node.modifiers();
					if (modifiers != null && !modifiers.isEmpty()) {
						if (detectEjb) {
							if (null != JavaASTUtil.findAnnotation(modifiers, "javax.ejb.Local")) {
								replCandidates.interfaceCandidateFQNs.add(thisFQN);
							} else if (null != JavaASTUtil.findAnnotation(modifiers, "javax.ejb.Remote")) {
								replCandidates.interfaceCandidateFQNs.add(thisFQN);
							}
						}
					}
				} else {
					List<Type> superInterfaceTypes = node.superInterfaceTypes();
					if (superInterfaceTypes != null && !superInterfaceTypes.isEmpty()) {
						// for(Type superItf : superInterfaceTypes) {
						Type superItf = superInterfaceTypes.get(0); // no iter.. use primary interface
						ITypeBinding superItfBinding = superItf.resolveBinding();
						String superFQN = superItfBinding.getQualifiedName();
						replCandidates.itf2ImplCandidateFQNs.put(superFQN, thisFQN);
					}
				}
				return true;
			}
		});
	}

	@Override
	protected Object prepareRefactorUnit(CompilationUnit unit) throws Exception {
		RefactoringInfo res = new RefactoringInfo();
		unit.accept(new ASTVisitor() {

			@Override
			public boolean visit(FieldDeclaration node) {
				String replaceFqn = resolveTypeReplacement(node.getType());
				if (replaceFqn != null) {
					res.fieldDeclRepls.add(new FieldDeclReplacement(node, replaceFqn));
				}
				return super.visit(node);
			}


			@Override
			public boolean visit(SingleVariableDeclaration node) {
				String replaceFqn = resolveTypeReplacement(node.getType());
				if (replaceFqn != null) {
					res.singleVarDecRepls.add(new SingleVarDeclReplacement(node, replaceFqn));
				}
				return super.visit(node);
			}

			@Override
			public boolean visit(VariableDeclarationExpression node) {
				String replaceFqn = resolveTypeReplacement(node.getType());
				if (replaceFqn != null) {
					res.varDecExprRepls.add(new VariableDeclExprReplacement(node, replaceFqn));
				}
				return super.visit(node);
			}
			
		});
		if (res.isEmpty()) {
			return null;
		}
		return res;
	}
	
	private String resolveTypeReplacement(Type type) {
		ITypeBinding typeBinding = type.resolveBinding();
		String fqn = typeBinding.getQualifiedName();
		String replaceFqn = replacements.get(fqn);
		return replaceFqn;
	}

	
	@Override
	protected void doRefactorUnit(CompilationUnit unit, Object refactoringInfoObject) {
		// AST ast = unit.getAST();
		RefactoringInfo refactoringInfo = (RefactoringInfo) refactoringInfoObject;
		for(FieldDeclReplacement repl : refactoringInfo.fieldDeclRepls) {
			Type newType = createTypeAndImport(unit, repl.newTypeName); 
			repl.replace.setType(newType);
		}
		for(SingleVarDeclReplacement repl : refactoringInfo.singleVarDecRepls) {
			Type newType = createTypeAndImport(unit, repl.newTypeName); 
			repl.replace.setType(newType);
		}
		for(VariableDeclExprReplacement repl : refactoringInfo.varDecExprRepls) {
			Type newType = createTypeAndImport(unit, repl.newTypeName); 
			repl.replace.setType(newType);
		}
	}
	
	protected Type createTypeAndImport(CompilationUnit unit, String fqn) {
		AST ast = unit.getAST();
		int indexLastDot = fqn.lastIndexOf('.');
		String simpleName = (indexLastDot != -1)? fqn.substring(indexLastDot + 1, fqn.length()) : fqn;
		Type type = ast.newSimpleType(ast.newSimpleName(simpleName));
		fr.an.eclipse.pattern.util.JavaASTUtil.addImport(unit, fqn);
		return type;
	}

}
