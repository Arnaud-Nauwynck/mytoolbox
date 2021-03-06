package fr.an.eclipse.pattern.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.NamingConventions;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.internal.core.SourceMethod;
import org.eclipse.jdt.internal.corext.util.SearchUtils;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.text.edits.TextEdit;

import fr.an.eclipse.pattern.PatternUIPlugin;
import fr.an.eclipse.pattern.util.ICompilationUnitFilter.DefaultIncludeExcludeNameCompilationUnitFilter;


@SuppressWarnings("unchecked")
public class JavaASTUtil {


	/** private to force all static */
	private JavaASTUtil() {
	}
	
	// -------------------------------------------------------------------------


	public static CompilationUnit parseCompilationUnit(ICompilationUnit cu, IProgressMonitor monitor) {
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setSource(cu);
		parser.setResolveBindings(true);
		CompilationUnit unit = (CompilationUnit) parser.createAST(monitor);
		return unit;
	}
	
	public static void scanAndHandlePackagesCompilationUnits(IProgressMonitor monitor, 
			Collection<? extends IJavaElement> elementOfProjects,
			Collection<String> scanPackages,
			String handlerMessage,
			Consumer<CompilationUnit> handler) {
		// scan packages (in projects of elements)
		monitor.beginTask("scan packages", 1);
		DefaultIncludeExcludeNameCompilationUnitFilter cuFilter = new DefaultIncludeExcludeNameCompilationUnitFilter(null, 
				new IncludeExcludePatternList(IncludeExcludePatternList.prefixToPatterns(scanPackages), null));
		CompilationUnitScanner cuScanner = new CompilationUnitScanner();
		cuScanner.setFilter(cuFilter);
		try {
			cuScanner.recursiveScanCUsOfParentProjects(monitor, elementOfProjects);
		} catch (JavaModelException e) {
			throw new RuntimeException("Failed", e);
		}
		Collection<ICompilationUnit> scannedCus = cuScanner.getResultCompilationUnits();
		monitor.done();
		
		// parse and handle CompilationUnits
		monitor.beginTask(handlerMessage, scannedCus.size());
		for(ICompilationUnit scannedCU : scannedCus) {
			CompilationUnit cu = JavaASTUtil.parseCompilationUnit(scannedCU, monitor);
			handler.accept(cu);
			monitor.internalWorked(1);
		}
		monitor.done();
	}
			

	public static void uiHandleAndRewrite(IProgressMonitor monitor, final CompilationUnit unit, Consumer<CompilationUnit> handler) throws CoreException {
		if (UiUtil.isSWTGraphicsThread()) {
			doHandleAndRewrite(monitor, unit, handler);
        } else {
            Display.getDefault().syncExec(() -> doHandleAndRewrite(monitor, unit, handler));                           
        }
	}
	
	private static void doHandleAndRewrite(IProgressMonitor monitor, CompilationUnit unit, Consumer<CompilationUnit> handler) {
		try {
			ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager();
			IPath path = unit.getJavaElement().getPath();
			bufferManager.connect(path, LocationKind.IFILE, monitor);
			try {
		
				// *** the Biggy : do refactoring ***
				if (handler != null) {
					handler.accept(unit);
				}
	
				rewriteASTDocument(monitor, unit);
	
			} catch(Exception ex) {
				PatternUIPlugin.logWarning("Failed to refactor compilation unit " + unit.getJavaElement().getElementName(), ex);
				// ignore.. no rethrow!!
			} finally {
				bufferManager.disconnect(path, LocationKind.IFILE, monitor);
			}
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public static void uiRewriteASTDocument(IProgressMonitor monitor, CompilationUnit unit) {
		if (UiUtil.isSWTGraphicsThread()) {
			rewriteASTDocument(monitor, unit);
	    } else {
	        Display.getDefault().syncExec(() -> rewriteASTDocument(monitor, unit));                          
	    }
	}
	
	public static void rewriteASTDocument(IProgressMonitor monitor, CompilationUnit unit) {
		ICompilationUnit icu = (ICompilationUnit) unit.getJavaElement();
		String cuPath = icu.getPath().toString();
		ConsoleUtil.debug("writing changes to %s", cuPath);

		try {
			if (! icu.hasUnsavedChanges()) {
				return;
			}
			icu.commitWorkingCopy(true, monitor);
		} catch (JavaModelException ex) {
			throw new RuntimeException("Failed to save " + cuPath, ex);
		}
//		try {
//			ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager();
//			ITextFileBuffer textFileBuffer = bufferManager.getTextFileBuffer(path, LocationKind.IFILE);
//			// retrieve the buffer
//			IDocument document = textFileBuffer.getDocument();
//	
//			Map<?,?> options = JavaCore.getOptions();
//			TextEdit textEdit = unit.rewrite(document, options);
//			textEdit.apply(document);
//	
//			// commit changes to underlying file
//			textFileBuffer.commit(monitor, false);
//		} catch(Exception ex) {
//    		throw new RuntimeException(ex);
//    	}
	}

	
	/**
	 * @param node
	 * @return inner-most enclosing ASTNode declaration... 
	 *   one of TypeDeclaration, MethodDeclaration, FieldDeclaration, CompilationUnit
	 */
	public static ASTNode getEnclosingDeclarationNode(ASTNode node) {
		ASTNode res = null;
		for(ASTNode p = node; p != null; p = p.getParent()) {
			if (p instanceof TypeDeclaration) {
				res = p;
				break;
			} else if (p instanceof MethodDeclaration) {
				res = p;
				break;
			} else if (p instanceof FieldDeclaration) {
				res = p;
				break;
			} else if (p instanceof CompilationUnit) {
				res = p;
				break;
			}
		}
		return res;
	}

	// return the method node that is an ancestor of the given node, or null if
	// not found
	public static ASTNode getMethodAncestor(ASTNode node) {
		return getAncestorByType(node, ASTNode.METHOD_DECLARATION);
	}

	// return the method node that is an ancestor of the given node, or null if
	// not found
	public static TypeDeclaration getTypeDeclarationAncestor(ASTNode node) {
		return (TypeDeclaration) getAncestorByType(node, ASTNode.TYPE_DECLARATION);
	}

	// returns an ancestor node of the given type, or null if not found
	public static ASTNode getAncestorByType(ASTNode node, int type) {
		if (node == null)
			return null;
		ASTNode res = null;
		for(ASTNode p = node; p != null; p = p.getParent()) {
			if (p.getNodeType() == type) {
				res = p;
				break;
			}
		}
		return res;
	}

	
	public static MethodDeclaration getParentMethodDeclaration(ASTNode node) {
		MethodDeclaration res = null;
		for(ASTNode p = node; p != null; p = p.getParent()) {
			if (p instanceof MethodDeclaration) {
				res = (MethodDeclaration) p;
				break;
			}
		}
		return res;
	}
	
	public static CompilationUnit getParentCompilationUnit(ASTNode node) {
		CompilationUnit res = null;
		for(ASTNode p = node; p != null; p = p.getParent()) {
			if (p instanceof CompilationUnit) {
				res = (CompilationUnit) p;
				break;
			}
		}
		return res;
	}

	public static TypeDeclaration getParentTypeDecl(ASTNode node) {
		TypeDeclaration res = null;
		for(ASTNode p = node; p != null; p = p.getParent()) {
			if (p instanceof TypeDeclaration) {
				res = (TypeDeclaration) p;
				break;
			}
		}
		return res;
	}

	public static List<String> methodToParameterTypeNames(MethodDeclaration meth) {
		List<String> res = new ArrayList<String>(meth.parameters().size());
		for (SingleVariableDeclaration param : (List<SingleVariableDeclaration>) meth.parameters()) {
			ITypeBinding typeBinding = param.getType().resolveBinding();
			String paramTypeName = typeBinding.getQualifiedName();
			res.add(paramTypeName);
		}
		return res;
	}
	
	public static MethodDeclaration findMethod(TypeDeclaration typeDecl, String methodName,  List<String> paramTypeNames) {
		MethodDeclaration res = null;
		for(BodyDeclaration elt : (List<BodyDeclaration>) typeDecl.bodyDeclarations()) {
			if (elt instanceof MethodDeclaration) {
				MethodDeclaration m = (MethodDeclaration) elt;
				if (isMethodSignatureMatch(m, methodName, paramTypeNames)) {
					res = m;
					break;
				}
			}
		}
		return res;
	}
	
	public static MethodDeclaration findGetterMethodDecl(IJavaProject prj, TypeDeclaration typeDecl, int modifiers, String fieldName) {
		MethodDeclaration res = null;
		String getterName = NamingConventions.suggestGetterName(prj, fieldName, modifiers, false, new String[0]);
		String isGetterName = NamingConventions.suggestGetterName(prj, fieldName, modifiers, true, new String[0]);
		List<String> paramTypeNames = new ArrayList<String>(); 
		List<BodyDeclaration> bodyDeclarations = (List<BodyDeclaration>) typeDecl.bodyDeclarations();
		for(BodyDeclaration elt : bodyDeclarations) {
			if (elt instanceof MethodDeclaration) {
				MethodDeclaration m = (MethodDeclaration) elt;
				if (isMethodSignatureMatch(m, getterName, paramTypeNames)) {
					res = m;
					break;
				}
				if (isMethodSignatureMatch(m, isGetterName, paramTypeNames)) {
					res = m;
					break;
				}
			}
		}
		return res;
	}


	public static boolean isVoidType(Type p) {
		return (p instanceof PrimitiveType)
			&& ((PrimitiveType) p).getPrimitiveTypeCode() == PrimitiveType.VOID; 
	}
	
	public static String methIsGetter(MethodDeclaration mdecl) {
		int modifiers = mdecl.getModifiers();
		if (mdecl.isConstructor() 
				|| Modifier.isStatic(modifiers)
				|| !Modifier.isPublic(modifiers)
				|| Modifier.isAbstract(modifiers)
				|| mdecl.parameters().size() != 0
				|| isVoidType(mdecl.getReturnType2())) {
			return null;
		}
		@SuppressWarnings("unchecked")
		List<Statement> stmts = mdecl.getBody().statements();
		if (stmts.size() != 1) {
			return null;
		}
		Statement stmt = stmts.get(0);
		if (!(stmt instanceof ReturnStatement)) {
			return null;
		}
		ReturnStatement retStmt = (ReturnStatement) stmt;
		Expression retExpr = retStmt.getExpression();
		return exprToThisSimpleName(retExpr);
	}
	
	public static String exprToThisSimpleName(Expression expr) {
		String res = null;
		if (expr instanceof SimpleName) {
			SimpleName expr2 = (SimpleName) expr;
			res = expr2.getIdentifier();
		} else if (expr instanceof FieldAccess) {
			FieldAccess fa = (FieldAccess) expr;
			if (!isEqualSimpleName(fa.getExpression(), "this")) {
				return null;
			}
			res = fa.getName().getIdentifier();
		}
		return res;
	}
	
	public static String exprToSimpleName(Expression expr) {
		if (!(expr instanceof SimpleName)) {
			return null;
		}
		return ((SimpleName) expr).getIdentifier();
	}
	
	public static boolean isEqualSimpleName(Expression expr, String name) {
		if (!(expr instanceof SimpleName)) {
			return false;
		}
		String id = ((SimpleName) expr).getIdentifier();
		return name.equals(id);
	}
	
	public static boolean isMethodSignatureMatch(MethodDeclaration method, String expectedMethodName, List<String> expectedParamTypeNames) {
		String name = method.getName().getIdentifier();
		if (!name.equals(expectedMethodName)) return false;
		if (expectedParamTypeNames.size() != method.parameters().size()) return false;
		List<String> paramTypeNames = methodToParameterTypeNames(method);
		for (Iterator<String> iterExpected = paramTypeNames.iterator(), iter = paramTypeNames.iterator(); 
				iterExpected.hasNext(); ) {
			String expectedParamTypeName = iterExpected.next();
			String paramTypeName = iter.next();
			if (!expectedParamTypeName.equals(paramTypeName)) {
				return false;
			}
		}		
		return true;
	}
	
	
	@SuppressWarnings("unchecked")
	public static List<MethodDeclaration> compilationUnitToMethodDecl(CompilationUnit unit) {
		List<MethodDeclaration> res = new ArrayList<MethodDeclaration>();
		for(TypeDeclaration typeDecl : (Collection<TypeDeclaration>) unit.types()) {
			res.addAll(typeDeclToMethodDecl(typeDecl));
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	public static List<MethodDeclaration> typeDeclToMethodDecl(TypeDeclaration typeDecl) {
		List<MethodDeclaration> res = new ArrayList<MethodDeclaration>();
		for(BodyDeclaration decl : (Collection<BodyDeclaration>) typeDecl.bodyDeclarations()) {
			if (decl instanceof MethodDeclaration) {
				res.add((MethodDeclaration) decl);
			}
		}
		return res;
	}
	
	@SuppressWarnings("unchecked")
	public static List<MethodDeclaration> typeDeclToCtorDecl(TypeDeclaration typeDecl) {
		List<MethodDeclaration> res = new ArrayList<MethodDeclaration>();
		for(BodyDeclaration decl : (Collection<BodyDeclaration>) typeDecl.bodyDeclarations()) {
			if (decl instanceof MethodDeclaration) {
				MethodDeclaration meth = (MethodDeclaration) decl;
				if (meth.isConstructor()) {
					res.add(meth);
				}
			}
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	public static FieldDeclaration findFieldDeclByName(AbstractTypeDeclaration typeDecl, String name) {
		FieldDeclaration res = null;
		for(BodyDeclaration decl : (Collection<BodyDeclaration>) typeDecl.bodyDeclarations()) {
			if (decl instanceof FieldDeclaration) {
				FieldDeclaration fdecl = (FieldDeclaration) decl;
				Collection<VariableDeclarationFragment> fragments = fdecl.fragments();
				VariableDeclarationFragment frag = findVarDeclFragmentByName(fragments, name);
				if (frag != null) {
					res = fdecl;
					break;
				}
			}
		}
		return res;
	}
	
	public static VariableDeclarationFragment findVarDeclFragmentByName(Collection<VariableDeclarationFragment> ls, String name) {
		VariableDeclarationFragment res = null;
		for(VariableDeclarationFragment elt : ls) {
			if (elt.getName().getIdentifier().equals(name)) {
				res = elt;
				break;
			}
		}
		return res;
	}
	

	public static void replaceAstNodeInParent(ASTNode node, ASTNode newNode) {
		ASTNode parent = node.getParent();
		StructuralPropertyDescriptor locationInParent = node.getLocationInParent();
		if (!locationInParent.isChildListProperty()) {
			try {
				parent.setStructuralProperty(locationInParent, newNode);
			} catch(Exception ex) {
				throw new RuntimeException("Failed to refactor ast", ex);
			}
		} else {
			try {
				@SuppressWarnings("unchecked")
				List<ASTNode> childList = (List<ASTNode>) parent.getStructuralProperty(locationInParent);
				int index = childList.indexOf(node);
				childList.remove(index);
				childList.add(index, newNode);
			} catch(Exception ex) {
				throw new RuntimeException("Failed to refactor ast list prop", ex);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static MethodDeclaration findMethDeclByName(TypeDeclaration typeDecl, String name) {
		MethodDeclaration res = null;
		for(BodyDeclaration decl : (List<BodyDeclaration>) typeDecl.bodyDeclarations()) {
			if (decl instanceof MethodDeclaration) {
				MethodDeclaration meth = (MethodDeclaration) decl;
				String methName = meth.getName().getIdentifier();
				if (methName.equals(name)) {
					res = meth;
					break;
				}
			}
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	public static MethodDeclaration findFirstConstructorDecl(TypeDeclaration typeDecl) {
		MethodDeclaration res = null;
		String name = typeDecl.getName().getIdentifier();
		for(BodyDeclaration decl : (List<BodyDeclaration>) typeDecl.bodyDeclarations()) {
			if (decl instanceof MethodDeclaration) {
				MethodDeclaration meth = (MethodDeclaration) decl;
				if (!meth.isConstructor()) {
					continue;
				}
				String methName = meth.getName().getIdentifier();
				if (methName.equals(name)) {
					res = meth;
					break;
				}
			}
		}
		return res;
	}
	

	@SuppressWarnings("unchecked")
	public static void addMissingImports(final CompilationUnit unit, List<Statement> stmts, List<ImportDeclaration> importsToAdd) {
//		final AST unitAST = unit.getAST();
		final List<ImportDeclaration> imports = (List<ImportDeclaration>) unit.imports();

//		ASTVisitor visitor = new ASTVisitor() {
//			@SuppressWarnings("unchecked")
//			@Override
//			public boolean visit(SimpleType node) {
//				IBinding binding = node.resolveBinding();
//				if (binding instanceof ITypeBinding) {
//					// find corresponding import
//					ImportDeclaration foundImport = findImport(imports, (ITypeBinding) binding);
//					if (foundImport == null) {
//						ImportDeclaration add = unitAST.newImportDeclaration();
//						Name name = unitAST.newSimpleName(binding.getName()); 
//						add.setName(name);
//						imports.add(add); // TODO should insert at correct location..
//					}
//				}
//				return true;
//			}
//		};
//		for (Statement stmt : stmts) {
//			stmt.visitSwitchTree(visitor);
//		}
		
		for(ImportDeclaration uccImport : importsToAdd) {
			// find corresponding import
			if (uccImport.isOnDemand() || uccImport.isStatic()) {
				continue;
			}
			Name importName = uccImport.getName();
			String fqn = importName.getFullyQualifiedName().toString();
			ImportDeclaration foundImport = findImport(imports, fqn);
			if (foundImport == null) {
				addImport(unit, fqn);
			}
		}
		
	}

	public static void addImport(final CompilationUnit unit, String fqn) {
		final AST unitAST = unit.getAST();
		@SuppressWarnings("unchecked")
		final List<ImportDeclaration> imports = (List<ImportDeclaration>) unit.imports();
		
		if (null == findImport(imports, fqn)) {
			ImportDeclaration importToAdd = unitAST.newImportDeclaration();
			String[] fqnElts = tokenizeFQN(fqn);
			Name name = unitAST.newName(fqnElts); 
			importToAdd.setName(name);
			// TOADD should insert at correct location.. (cf organize import preferences in project!)
			imports.add(importToAdd);
		}
	}

	public static void addImports(final CompilationUnit unit, Collection<String> fqns) {
		for(String fqn : fqns) {
			addImport(unit, fqn);
		}
	}
	
	public static void checkMissingImports(Set<String> dest, CompilationUnit unit, Set<String> importFQNToChecks) {
		for(String importFQNToCheck : importFQNToChecks) {
			checkMissingImport(dest, unit, importFQNToCheck);
		}
	}

	private static void checkMissingImport(Set<String> dest, CompilationUnit unit, String importFQNToCheck) {
		@SuppressWarnings("unchecked")
		List<ImportDeclaration> imports = unit.imports();
		ImportDeclaration found = findImport(imports, importFQNToCheck);
		if (found == null) {
			if (!dest.contains(importFQNToCheck)) {
				dest.add(importFQNToCheck);
			}
		}
	}
	
	
	public static ImportDeclaration findImport(List<ImportDeclaration> imports, String importFQN) {
		ImportDeclaration res = null;
		for(ImportDeclaration imp : imports) {
			// find corresponding import
			if (imp.isStatic()) {
				continue;
			}
			if (imp.isOnDemand()) {
				continue; // TODO not supported yet!
			}
			Name importName = imp.getName();
			String fqn = importName.toString();
			if (fqn.equals(importFQN)) {
				res = imp; // ok found it
				break;
			}
		}
		return res;
	}
			
	public static String[] tokenizeFQN(String fqn) {
		String[] res = fqn.split("\\.");
		return res;
	}
	
	public static String fqnToUnqualifiedName(String fqn) {
		String res = fqn;
		int indexLastDot = fqn.lastIndexOf('.');
		if (indexLastDot != -1) {
			res = res.substring(indexLastDot + 1);
		}
		int indexLastDollar = fqn.lastIndexOf('$');
		if (indexLastDollar != -1) {
			res = res.substring(indexLastDollar + 1);
		}
		return res;
	}
	
	public static ImportDeclaration findImport(List<ImportDeclaration> imports, ITypeBinding type) {
		ImportDeclaration res = null;
		String fqn = type.getQualifiedName();
		for (ImportDeclaration p : imports) {
			if (p.getName().getFullyQualifiedName().equals(fqn)) {
				res = p;
				break;
			}
		}
		return res;
	}

    public static IJavaProject getJavaProject(ASTNode node) {
        IJavaProject result = null;  
        IResource resource = getResource(node);
        
        if (resource != null) {
            result = JavaCore.create(resource.getProject());
        }
        
        return result;
    }
    
    public static IResource getResource(ASTNode node) {
        ASTNode rootNode = node.getRoot();
        if (rootNode instanceof CompilationUnit) {
            return ((CompilationUnit) rootNode).getJavaElement().getResource();
        }
        
        return null;
    }

	public static Annotation findAnnotationFQN(BodyDeclaration decl, String annotationFQN) {
		List<IExtendedModifier> declModifiers = decl.modifiers();
		Annotation res = findAnnotation(declModifiers, annotationFQN);
		return res;
	}

	public static Annotation findAnnotation(List<IExtendedModifier> declModifiers, String annotationFQN) {
		String annotationUnqualifiedName = fqnToUnqualifiedName(annotationFQN);
		for(IExtendedModifier declModifier : declModifiers) {
			if (declModifier instanceof Annotation) {
				Annotation annotation = (Annotation) declModifier;
				IAnnotationBinding annotationBinding = annotation.resolveAnnotationBinding();
				ITypeBinding annotationTypeBinding = (annotationBinding != null)? annotationBinding.getAnnotationType() : null;
				if (annotationTypeBinding != null) {
					if (annotationFQN.equals(annotationTypeBinding.getQualifiedName())) {
						return annotation;
					}
				} else {
					// unable to resolve annotation?
					String annotationNameStr = annotation.getTypeName().getFullyQualifiedName(); // qualified or unqualified!
					if (annotationNameStr.equals(annotationFQN) || annotationNameStr.equals(annotationUnqualifiedName)) {
						return annotation;
					}
				}
			}
		}
		return null;
	}
	
	public static SingleMemberAnnotation findSingleMemberAnnotationFQN(BodyDeclaration decl, String fqn) {
		SingleMemberAnnotation res = null;
		Annotation tmp = findAnnotationFQN(decl, fqn);
		if (tmp instanceof SingleMemberAnnotation) {
			res = (SingleMemberAnnotation) tmp;
		}
		return res;
	}

	

	public static Annotation findFirstAnnotation(List<IExtendedModifier> modifiers, String fqn) {
		String shortName = fqn.substring(fqn.lastIndexOf('.') + 1, fqn.length());
		for(IExtendedModifier modifier : modifiers) {
			if (modifier.isAnnotation()) {
				Annotation ann = (Annotation) modifier;
				Name annTypeName = ann.getTypeName();
				String annFQN = annTypeName.getFullyQualifiedName();
				if (annFQN.equals(fqn)) {
					return ann;
				}
				if (annFQN.equals(shortName)) {
					// resolve for ambiguous import
					IAnnotationBinding resolveAnn = ann.resolveAnnotationBinding();
					ITypeBinding annType = resolveAnn.getAnnotationType();
					if (annType == null) {
						continue;
					}
					String annQN = annType.getQualifiedName();
					if (annQN.equals(fqn)) {
						return ann;
					}
				}
			}
		}
		return null;
	}
	
	
	public static List<SourceMethod> searchMethodInvocations(IMethod meth) {
//		MethodWrapper methCaller = CallHierarchy.getDefault().getCallerRoots(new IMember[] { meth })[0];
//		MethodWrapper[] calls = methCaller.getCalls(null);
//		if (calls != null && calls.length != 0) 
//		{
//		}

		final List<SourceMethod> res = new ArrayList<>();
		SearchRequestor searchRequestor = new SearchRequestor() {
			public void acceptSearchMatch(SearchMatch match) {
				Object searchElt = match.getElement();
				if (searchElt instanceof SourceMethod) {
					res.add((SourceMethod) searchElt);
				}
			}
		};

		int limitTo= IJavaSearchConstants.REFERENCES;
		SearchPattern pattern= SearchPattern.createPattern(meth, limitTo, SearchUtils.GENERICS_AGNOSTIC_MATCH_RULE);
		SearchEngine searchEngine= new SearchEngine();
		IJavaSearchScope searchScope = SearchEngine.createWorkspaceScope();
		SearchParticipant[] searchParticipants = new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() };
		try {
			searchEngine.search(pattern, searchParticipants, searchScope, searchRequestor, null);
		} catch (CoreException e) {
			throw new RuntimeException("search references failed for meth " + meth, e);
		}
		return res;
	}
	

	/**
	 * Clones the AST Tree.
	 * 
	 * @param astNode
	 * @return
	 */
	public static <T extends ASTNode> T cloneASTNode(final T astNode) {
		final AST ast = astNode.getAST();
		return cloneASTNode(astNode, ast);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends ASTNode> T cloneASTNode(final T srcNode, AST ast) {
		final ASTNode res = ast.createInstance(srcNode.getClass());
		final List<?> structuralPropertiesForType = srcNode.structuralPropertiesForType();
		for (final Object o : structuralPropertiesForType) {
			final StructuralPropertyDescriptor descriptor = (StructuralPropertyDescriptor) o;
			if (descriptor.isChildListProperty()) {
				final List<Object> srcList = (List<Object>) srcNode.getStructuralProperty(descriptor);
				final List<Object> resList = (List<Object>) res.getStructuralProperty(descriptor);
				for (final Object propertyValue : srcList) {
					resList.add(propertyValue instanceof ASTNode ? cloneASTNode((ASTNode) propertyValue, ast) : propertyValue);
				}
			} else {
				final Object propertyValue = srcNode.getStructuralProperty(descriptor);
				res.setStructuralProperty(descriptor,
						propertyValue instanceof ASTNode ? cloneASTNode((ASTNode) propertyValue, ast) : propertyValue);
			}
		}
		return (T) res;
	}

	public static <T extends ASTNode> List<T> cloneASTNodeList(List<T> astNode, AST ast) {
		List<T> res = new ArrayList<T>();
		for(T e : astNode) {
			res.add(cloneASTNode(e, ast));
		}
		return res;
	}

	public static <T extends ASTNode> void insertBefore(List<T> parent, T insertLocation, T element) {
		int index = parent.indexOf(insertLocation);
		if (index != -1) {
			parent.add(index, element);
		} else {
			parent.add(element);
		}
	}

	public static void insertBefore(ASTNode insertLocation, ASTNode element) {
		ASTNode parent = insertLocation.getParent();
		StructuralPropertyDescriptor locationInParent = insertLocation.getLocationInParent();
		if (locationInParent.isChildListProperty()) {
			@SuppressWarnings("unchecked")
			List<ASTNode> ls = (List<ASTNode>) parent.getStructuralProperty(locationInParent);
			insertBefore(ls, insertLocation, element);
		} else {
			throw new IllegalArgumentException("can not insert before .. not in a list");
		}
	}

	public static void addMarkerAnnotation(
			CompilationUnit cu, List<IExtendedModifier> modifiers, 
			String annotationPackage, String annotationName) {
		AST ast = cu.getAST();
		// TODO .. may check for import name conflict and use explicit fqn
		MarkerAnnotation ann = ast.newMarkerAnnotation();
		ann.setTypeName(ast.newSimpleName(annotationName));
		modifiers.add(0, ann);
		
		String fqn = annotationPackage + "." + annotationName;
		addImport(cu, fqn);
	}

}
