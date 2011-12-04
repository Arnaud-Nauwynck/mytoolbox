package fr.an.eclipse.pattern.ast.impl;

import java.util.List;

import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import fr.an.eclipse.pattern.ast.IPattern;
import fr.an.eclipse.pattern.ast.PatternMatchVisitor;
import fr.an.eclipse.pattern.ast.PatternVisitor;
import fr.an.eclipse.pattern.ast.impl.StdExpressionASTPatterns.AnnotationPattern;

public class StdDeclarationASTPatterns {

	/** pattern for org.eclipse.jdt.core.dom.CompilationUnit */
	public static class CompilationUnitPattern extends AbstractASTNodePattern<CompilationUnit> {

//		/**
//		 * Messages reported by the compiler during parsing or name resolution.
//		 */
//		private IPattern<List<Message>> messages;

//		/**
//		 * The comment list (element type: {@link Comment},
//		 * or <code>null</code> if none; initially <code>null</code>.
//		 * @since 3.0
//		 */
//		private IListPattern/*<Comment>*/ optionalCommentList = null;

//		/**
//		 * The comment table, or <code>null</code> if none; initially
//		 * <code>null</code>. This array is the storage underlying
//		 * the <code>optionalCommentList</code> ArrayList.
//		 * @since 3.0
//		 */
//		Comment[] optionalCommentTable = null;

		/**
		 * The package declaration, or <code>null</code> if none; initially
		 * <code>null</code>.
		 */
		private IPattern<PackageDeclaration> optionalPackageDeclaration;

//		/**
//		 * Problems reported by the compiler during parsing or name resolution.
//		 */
//		private ListPatternASTNode/*<IProblemPattern*> problems;
		

		/**
		 * The list of type declarations in textual order order;
		 * initially none (elementType: <code>AbstractTypeDeclaration</code>)
		 */
		private IPattern<List<AbstractTypeDeclaration>> types;

		// ------------------------------------------------------------------------

		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof CompilationUnit)) return false;
			return v.visitMatch(this, (CompilationUnit)node);
		}
		
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (optionalPackageDeclaration != null) optionalPackageDeclaration.accept(v); 
			if (types != null) types.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, CompilationUnit node) {
			if (optionalPackageDeclaration != null && !optionalPackageDeclaration.acceptMatch(v, node.getPackage())) return v.mismatch("optionalPackageDeclaration"); 
			if (types != null && !types.acceptMatch(v, node.types())) return v.mismatch("types");
			return true;
		}

//		public IListPattern getOptionalCommentList() {
//			return optionalCommentList;
//		}
//
//		public void setOptionalCommentList(ASTListPattern optionalCommentList) {
//			this.optionalCommentList = optionalCommentList;
//		}

		public IPattern<PackageDeclaration> getOptionalPackageDeclaration() {
			return optionalPackageDeclaration;
		}

		public void setOptionalPackageDeclaration(IPattern<PackageDeclaration> p) {
			this.optionalPackageDeclaration = p;
		}

		public IPattern<List<AbstractTypeDeclaration>> getTypes() {
			return types;
		}

		public void setTypes(IPattern<List<AbstractTypeDeclaration>> p) {
			this.types = p;
		}

	}
	
	// ------------------------------------------------------------------------
	
	/** pattern for org.eclipse.jdt.core.dom.PackageDeclaration */
	public static class PackageDeclarationPattern extends AbstractASTNodePattern<PackageDeclaration> {

		/**
		 * doc comment pattern
		 */
		private IPattern<Javadoc> optionalDocComment;

		/**
		 * annotations pattern
		 */
		private IPattern<List<Annotation>> annotations;

		/**
		 * package name pattern
		 */
		private IPattern<Name> packageName = null;

		// ------------------------------------------------------------------------

		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof PackageDeclaration)) return false;
			return v.visitMatch(this, (PackageDeclaration)node);
		}
		
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (optionalDocComment != null) optionalDocComment.accept(v); 
			if (annotations != null) annotations.accept(v);
			if (packageName != null) packageName.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, PackageDeclaration node) {
			if (optionalDocComment != null && !optionalDocComment.acceptMatch(v, node.getJavadoc())) return v.mismatch("optionalDocComment"); 
			if (annotations != null && !annotations.acceptMatch(v, node.annotations())) return v.mismatch("annotations");
			if (packageName != null && !packageName.acceptMatch(v, node.getName())) return v.mismatch("packageName");
			return true;
		}

		public IPattern<List<Annotation>> getAnnotations() {
			return annotations;
		}

		public void setAnnotations(IPattern<List<Annotation>> p) {
			this.annotations = p;
		}

		public IPattern<Javadoc> getOptionalDocComment() {
			return optionalDocComment;
		}

		public void setOptionalDocComment(IPattern<Javadoc> optionalDocComment) {
			this.optionalDocComment = optionalDocComment;
		}

		public IPattern<Name> getPackageName() {
			return packageName;
		}

		public void setPackageName(IPattern<Name> packageName) {
			this.packageName = packageName;
		}
		 
	}
	
	// ------------------------------------------------------------------------
	
	/** pattern for org.eclipse.jdt.core.dom.BodyDeclaration */
	public static class BodyDeclarationPattern<T extends BodyDeclaration> extends AbstractASTNodePattern<T> {

		protected IPattern<Javadoc> optionalDocComment;
		
		protected IPattern<Integer> modifierFlags;
		
		protected IPattern<List<IExtendedModifier>> modifiers;
		
		// ------------------------------------------------------------------------

		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof BodyDeclaration)) return false;
			return v.visitMatch(this, (BodyDeclaration)node);
		}
		
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (optionalDocComment != null) optionalDocComment.accept(v);
			if (modifierFlags != null) modifierFlags.accept(v);
			if (modifiers != null) modifiers.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, BodyDeclaration node) {
			if (optionalDocComment != null && !optionalDocComment.acceptMatch(v, node.getJavadoc())) return v.mismatch("optionalDocComment");
			if (modifierFlags != null && !modifierFlags.acceptMatch(v, node.getModifiers())) return v.mismatch("modifierFlags");
			if (modifiers != null && !modifiers.acceptMatch(v, node.modifiers())) return v.mismatch("modifiers");
			return true;
		}


		public IPattern<Integer> getModifierFlags() {
			return modifierFlags;
		}
		public void setModifierFlags(IPattern<Integer> modifierFlags) {
			this.modifierFlags = modifierFlags;
		}
		public IPattern<Javadoc> getOptionalDocComment() {
			return optionalDocComment;
		}
		public void setOptionalDocComment(IPattern<Javadoc> optionalDocComment) {
			this.optionalDocComment = optionalDocComment;
		}
		public IPattern<List<IExtendedModifier>> getModifiers() {
			return modifiers;
		}
		public void setModifiers(IPattern<List<IExtendedModifier>> modifiers) {
			this.modifiers = modifiers;
		}
		
	}
	
	// ------------------------------------------------------------------------
	
	/** pattern for org.eclipse.jdt.core.dom.AbstractTypeDeclaration */
	public static class AbstractTypeDeclarationPattern<T extends AbstractTypeDeclaration> extends BodyDeclarationPattern<T> {
		
		protected IPattern<SimpleName> typeName;
		
		protected IPattern<List<BodyDeclaration>> bodyDeclarations;
		
		// ------------------------------------------------------------------------

		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof AbstractTypeDeclaration)) return false;
			return v.visitMatch(this, (AbstractTypeDeclaration)node);
		}
		
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			super.recurseVisit(v);
			if (typeName != null) typeName.accept(v);
			if (bodyDeclarations != null) bodyDeclarations.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, AbstractTypeDeclaration node) {
			super.recursevisitMatch(v, node);
			if (typeName != null && !typeName.acceptMatch(v, node.getName())) return v.mismatch("typeName");
			if (bodyDeclarations != null && !bodyDeclarations.acceptMatch(v, node.bodyDeclarations())) return v.mismatch("bodyDeclarations");
			return true;
		}

		
		public IPattern<SimpleName> getTypeName() {
			return typeName;
		}
		public void setTypeName(IPattern<SimpleName> p) {
			this.typeName = p;
		}
		public IPattern<List<BodyDeclaration>> getBodyDeclarations() {
			return bodyDeclarations;
		}
		public void setBodyDeclarations(IPattern<List<BodyDeclaration>> p) {
			this.bodyDeclarations = p;
		}

		
	}

	// ------------------------------------------------------------------------

	/** pattern for org.eclipse.jdt.core.dom.TypeDeclaration */
	public static class TypeDeclarationPattern extends AbstractTypeDeclarationPattern<TypeDeclaration> {

		/**
		 * pattern for interface / class
		 */
		private IPattern<Boolean> isInterface;

		/**
		 * The type parameters pattern
		 */
		private IPattern<List<TypeParameter>> typeParameters;

		/**
		 * The optional superclass type pattern
		 */
		private IPattern<Type> optionalSuperclassType;

		/**
		 * The superinterface types pattern
		 */
		private IPattern<List<Type>> superInterfaceTypes;

		// ------------------------------------------------------------------------

		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof TypeDeclaration)) return false;
			return v.visitMatch(this, (TypeDeclaration)node);
		}
		
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			super.recurseVisit(v);
			if (typeParameters != null) typeParameters.accept(v); 
			if (optionalSuperclassType != null) optionalSuperclassType.accept(v);
			if (superInterfaceTypes != null) superInterfaceTypes.accept(v);
			if (isInterface != null) isInterface.accept(v); 
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, TypeDeclaration node) {
			if (!super.recursevisitMatch(v, node)) return false;
			if (typeParameters != null && !typeParameters.acceptMatch(v, node.typeParameters())) return v.mismatch("typeParameters"); 
			if (optionalSuperclassType != null && !optionalSuperclassType.acceptMatch(v, node.getSuperclassType())) return v.mismatch("optionalSuperclassType");
			if (superInterfaceTypes != null && !superInterfaceTypes.acceptMatch(v, node.superInterfaceTypes())) return v.mismatch("superInterfaceTypes");
			if (isInterface != null && !isInterface.acceptMatch(v, node.isInterface())) return v.mismatch("isInterface"); 
			return true;
		}

		
		public IPattern<Boolean> getIsInterface() {
			return isInterface;
		}

		public void setIsInterface(IPattern<Boolean> isInterface) {
			this.isInterface = isInterface;
		}

		public IPattern<List<TypeParameter>> getTypeParameters() {
			return typeParameters;
		}

		public void setTypeParameters(IPattern<List<TypeParameter>> typeParameters) {
			this.typeParameters = typeParameters;
		}

		public IPattern<Type> getOptionalSuperclassType() {
			return optionalSuperclassType;
		}

		public void setOptionalSuperclassType(IPattern<Type> optionalSuperclassType) {
			this.optionalSuperclassType = optionalSuperclassType;
		}

		public IPattern<List<Type>> getSuperInterfaceTypes() {
			return superInterfaceTypes;
		}

		public void setSuperInterfaceTypes(IPattern<List<Type>> superInterfaceTypes) {
			this.superInterfaceTypes = superInterfaceTypes;
		}
		
	}
	
	// ------------------------------------------------------------------------

	/** pattern for org.eclipse.jdt.core.dom.AnnotationTypeDeclaration */
	public static class AnnotationTypeDeclarationPattern extends AbstractTypeDeclarationPattern<AnnotationTypeDeclaration> {
		// no extra fields

		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof AnnotationTypeDeclaration)) return false;
			return v.visitMatch(this, (AnnotationTypeDeclaration)node);
		}

		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			super.recurseVisit(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, AnnotationTypeDeclaration node) {
			if (!super.recursevisitMatch(v, node)) return false;
			return true;
		}

	}
	
	// ------------------------------------------------------------------------
	
	/** pattern for org.eclipse.jdt.core.dom.EnumDeclaration */
	public static class EnumDeclarationPattern extends AbstractTypeDeclarationPattern<EnumDeclaration> {

		/**
		 * The superinterface types pattern.
		 */
		private IPattern<List<Type>> superInterfaceTypes;
	
		/**
		 * The enum constant declarations pattern
		 */
		private IPattern<List<EnumConstantDeclaration>> enumConstants;

		// ------------------------------------------------------------------------

		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof EnumDeclaration)) return false;
			return v.visitMatch(this, (EnumDeclaration)node);
		}
		
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			super.recurseVisit(v);
			if (superInterfaceTypes != null) superInterfaceTypes.accept(v);
			if (enumConstants != null) enumConstants.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, EnumDeclaration node) {
			if (!super.recursevisitMatch(v, node)) return false;
			if (superInterfaceTypes != null && !superInterfaceTypes.acceptMatch(v, node.superInterfaceTypes())) return v.mismatch("superInterfaceTypes");
			if (enumConstants != null && !enumConstants.acceptMatch(v, node.enumConstants())) return v.mismatch("enumConstants");
			return true;
		}

		
		public IPattern<List<Type>> getSuperInterfaceTypes() {
			return superInterfaceTypes;
		}

		public void setSuperInterfaceTypes(IPattern<List<Type>> p) {
			this.superInterfaceTypes = p;
		}

		public IPattern<List<EnumConstantDeclaration>> getEnumConstants() {
			return enumConstants;
		}

		public void setEnumConstants(IPattern<List<EnumConstantDeclaration>> p) {
			this.enumConstants = p;
		}
		
		
	}
	
	// ------------------------------------------------------------------------
	
	/** pattern for org.eclipse.jdt.core.dom.EnumConstantDeclaration */
	public static class EnumConstantDeclarationPattern extends BodyDeclarationPattern<EnumConstantDeclaration> {

		/**
		 * The constant name pattern
		 */
		private IPattern<SimpleName> constantName = null;
	
		/**
		 * The list of argument expressions
		 */
		private IPattern<List<Expression>> arguments;
	
		/**
		 * The optional anonymous class declaration; <code>null</code> for none;
		 * defaults to none.
		 */
		private IPattern<AnonymousClassDeclaration> optionalAnonymousClassDeclaration;

		// ------------------------------------------------------------------------

		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof EnumConstantDeclaration)) return false;
			return v.visitMatch(this, (EnumConstantDeclaration)node);
		}

		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			super.recurseVisit(v);
			if (constantName != null) constantName.accept(v);
			if (arguments != null) arguments.accept(v);
			if (optionalAnonymousClassDeclaration != null) optionalAnonymousClassDeclaration.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, EnumConstantDeclaration node) {
			if (!super.recursevisitMatch(v, node)) return false;
			if (constantName != null && !constantName.acceptMatch(v, node.getName())) return v.mismatch("constantName");
			if (arguments != null && !arguments.acceptMatch(v, node.arguments())) return v.mismatch("arguments");
			if (optionalAnonymousClassDeclaration != null && !optionalAnonymousClassDeclaration.acceptMatch(v, node.getAnonymousClassDeclaration())) return v.mismatch("optionalAnonymousClassDeclaration");
			return true;
		}



		
		public IPattern<SimpleName> getConstantName() {
			return constantName;
		}

		public void setConstantName(IPattern<SimpleName> p) {
			this.constantName = p;
		}

		public IPattern<List<Expression>> getArguments() {
			return arguments;
		}

		public void setArguments(IPattern<List<Expression>> p) {
			this.arguments = p;
		}

		public IPattern<AnonymousClassDeclaration> getOptionalAnonymousClassDeclaration() {
			return optionalAnonymousClassDeclaration;
		}

		public void setOptionalAnonymousClassDeclaration(IPattern<AnonymousClassDeclaration> p) {
			this.optionalAnonymousClassDeclaration = p;
		}
		
		
	}

	// ------------------------------------------------------------------------
	
	/** pattern for org.eclipse.jdt.core.dom.AnonymousClassDeclaration */
	public static class AnonymousClassDeclarationPattern extends AbstractASTNodePattern<AnonymousClassDeclaration> {
		/**
		 * The body declarations
		 */
		private IPattern<List<BodyDeclaration>> bodyDeclarations;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof AnonymousClassDeclaration)) return false;
			return v.visitMatch(this, (AnonymousClassDeclaration)node);
		}
		
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (bodyDeclarations != null) bodyDeclarations.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, AnonymousClassDeclaration node) {
			if (bodyDeclarations != null && !bodyDeclarations.acceptMatch(v, node.bodyDeclarations())) return v.mismatch("bodyDeclarations");
			return true;
		}

		public IPattern<List<BodyDeclaration>> getBodyDeclarations() {
			return bodyDeclarations;
		}

		public void setBodyDeclarations(IPattern<List<BodyDeclaration>> p) {
			this.bodyDeclarations = p;
		}
		
	}
	
	// ------------------------------------------------------------------------
	
	/** pattern for org.eclipse.jdt.core.dom.MethodDeclaration */
	public static class MethodDeclarationPattern extends BodyDeclarationPattern<MethodDeclaration> {
	
		private IPattern<Boolean> isConstructor;
	
		private IPattern<SimpleName> methodName;
	
		private IPattern<List<SingleVariableDeclaration>> parameters;
	
		/**
		 * The return type pattern.
		 */
		private IPattern<Type> returnType;
	
		/**
		 * The type parameters pattern
		 */
		private IPattern<List<TypeParameter>> typeParameters;
	
		/**
		 * The number of array dimensions that appear after the parameters, rather
		 * than after the return type itself; defaults to 0.
		 */
		private IPattern<Integer> extraArrayDimensions;
	
		/**
		 * list of thrown exception names pattern
		 */
		private IPattern<List<Name>> thrownExceptions;
	
		/**
		 * The method body, or <code>null</code> if none.
		 * Defaults to none.
		 */
		private IPattern<Block> body = null;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof MethodDeclaration)) return false;
			return v.visitMatch(this, (MethodDeclaration)node);
		}
		
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			super.recurseVisit(v);
			if (isConstructor != null) isConstructor.accept(v);
			if (parameters != null) parameters.accept(v);
			if (returnType != null) returnType.accept(v);
			if (typeParameters != null) typeParameters.accept(v);
			if (extraArrayDimensions != null) extraArrayDimensions.accept(v);
			if (thrownExceptions != null) thrownExceptions.accept(v);
			if (body != null) body.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, MethodDeclaration node) {
			if (!super.recursevisitMatch(v, node)) return false;
			if (isConstructor != null && !isConstructor.acceptMatch(v, node.isConstructor())) return v.mismatch("isConstructor");
			if (parameters != null && !parameters.acceptMatch(v, node.parameters())) return v.mismatch("parameters");
			if (returnType != null && !returnType.acceptMatch(v, node.getReturnType2())) return v.mismatch("returnType");
			if (typeParameters != null && !typeParameters.acceptMatch(v, node.typeParameters())) return v.mismatch("typeParameters");
			if (extraArrayDimensions != null && !extraArrayDimensions.acceptMatch(v, node.getExtraDimensions())) return v.mismatch("extraArrayDimensions");
			if (thrownExceptions != null && !thrownExceptions.acceptMatch(v, node.thrownExceptions())) return v.mismatch("thrownExceptions");
			if (body != null && !body.acceptMatch(v, node.getBody())) return v.mismatch("body");
			return true;
		}

		
		public IPattern<Boolean> getIsConstructor() {
			return isConstructor;
		}

		public void setIsConstructor(IPattern<Boolean> isConstructor) {
			this.isConstructor = isConstructor;
		}

		public IPattern<SimpleName> getMethodName() {
			return methodName;
		}

		public void setMethodName(IPattern<SimpleName> p) {
			this.methodName = p;
		}

		public IPattern<List<SingleVariableDeclaration>> getParameters() {
			return parameters;
		}

		public void setParameters(IPattern<List<SingleVariableDeclaration>> p) {
			this.parameters = p;
		}

		public IPattern<Type> getReturnType() {
			return returnType;
		}

		public void setReturnType(IPattern<Type> returnType) {
			this.returnType = returnType;
		}

		public IPattern<List<TypeParameter>> getTypeParameters() {
			return typeParameters;
		}

		public void setTypeParameters(IPattern<List<TypeParameter>> p) {
			this.typeParameters = p;
		}

		public IPattern<Integer> getExtraArrayDimensions() {
			return extraArrayDimensions;
		}

		public void setExtraArrayDimensions(IPattern<Integer> p) {
			this.extraArrayDimensions = p;
		}

		public IPattern<List<Name>> getThrownExceptions() {
			return thrownExceptions;
		}

		public void setThrownExceptions(IPattern<List<Name>> thrownExceptions) {
			this.thrownExceptions = thrownExceptions;
		}

		public IPattern<Block> getBody() {
			return body;
		}

		public void setBody(IPattern<Block> p) {
			this.body = p;
		}
	
		
	}

	// ------------------------------------------------------------------------
	
	/*** pattern for FieldDeclaration */
	public static class FieldDeclarationPattern extends BodyDeclarationPattern<FieldDeclaration> {
		
		/**
		 * The base type pattern
		 */
		private IPattern<Type> baseType;
	
		/**
		 * list of variable declaration fragments pattern
		 */
		private IPattern<List<VariableDeclarationFragment>> variableDeclarationFragments;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof FieldDeclaration)) return false;
			return v.visitMatch(this, (FieldDeclaration)node);
		}

		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			super.recurseVisit(v);
			if (baseType != null) baseType.accept(v);
			if (variableDeclarationFragments != null) variableDeclarationFragments.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, FieldDeclaration node) {
			if (!super.recursevisitMatch(v, node)) return false;
			if (baseType != null && !baseType.acceptMatch(v, node.getType())) return v.mismatch("baseType");
			if (variableDeclarationFragments != null && !variableDeclarationFragments.acceptMatch(v, node.fragments())) return v.mismatch("variableDeclarationFragments");
			return true;
		}

		
		public IPattern<Type> getBaseType() {
			return baseType;
		}

		public void setBaseType(IPattern<Type> p) {
			this.baseType = p;
		}

		public IPattern<List<VariableDeclarationFragment>> getVariableDeclarationFragments() {
			return variableDeclarationFragments;
		}

		public void setVariableDeclarationFragments(IPattern<List<VariableDeclarationFragment>> p) {
			this.variableDeclarationFragments = p;
		}
		
	}
	
	// ------------------------------------------------------------------------
	
	/** pattern for org.eclipse.jdt.core.dom.VariableDeclaration */
	public static /*abstract*/ class VariableDeclarationPattern<T extends VariableDeclaration> extends AbstractASTNodePattern<T> {
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof VariableDeclaration)) return false;
			return v.visitMatch(this, (VariableDeclaration)node);
		}
		
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}

		public void recurseVisit(PatternVisitor v) {
			// no recurse
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, VariableDeclaration node) {
			// no recurse
			return true;
		}

	}
	
	// ------------------------------------------------------------------------
	
	/** pattern for org.eclipse.jdt.core.dom.SingleVariableDeclaration */
	public static class SingleVariableDeclarationPattern extends VariableDeclarationPattern<SingleVariableDeclaration> {

		/**
		 * The extended modifiers pattern
		 */
		private IPattern<List<IExtendedModifier>> modifiers;

		/**
		 * The modifiers pattern
		 */
		private IPattern<Integer> modifierFlags;

		/**
		 * The variable name pattern
		 */
		private IPattern<Name> variableName;

		/**
		 * The type pattern
		 */
		private IPattern<Type> type;

		/**
		 * Indicates the last parameter of a variable arity method
		 */
		private IPattern<Boolean> variableArity;

		/**
		 * The number of extra array dimensions that appear after the variable
		 */
		private IPattern<Integer> extraArrayDimensions;

		/**
		 * The initializer expression pattern
		 */
		private IPattern<Expression> optionalInitializer;

		// ------------------------------------------------------------------------

		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof SingleVariableDeclaration)) return false;
			return v.visitMatch(this, (SingleVariableDeclaration)node);
		}
		
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			super.recurseVisit(v);
			if (modifiers != null) modifiers.accept(v);
			if (modifierFlags != null) modifierFlags.accept(v);
			if (variableName != null) variableName.accept(v);
			if (type != null) type.accept(v);
			if (variableArity != null) variableArity.accept(v);
			if (extraArrayDimensions != null) extraArrayDimensions.accept(v);
			if (optionalInitializer != null) optionalInitializer.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, SingleVariableDeclaration node) {
			if (!super.recursevisitMatch(v, node)) return false;
			if (modifiers != null && !modifiers.acceptMatch(v, node.getModifiers())) return v.mismatch("modifiers");
			if (modifierFlags != null && !modifierFlags.acceptMatch(v, node.modifiers())) return v.mismatch("modifierFlags");
			if (variableName != null && !variableName.acceptMatch(v, node.getName())) return v.mismatch("variableName");
			if (type != null && !type.acceptMatch(v, node.getType())) return v.mismatch("type");
			if (variableArity != null && !variableArity.acceptMatch(v, node.isVarargs())) return v.mismatch("variableArity");
			if (extraArrayDimensions != null && !extraArrayDimensions.acceptMatch(v, node.getExtraDimensions())) return v.mismatch("extraArrayDimensions");
			if (optionalInitializer != null && !optionalInitializer.acceptMatch(v, node.getInitializer())) return v.mismatch("optionalInitializer");
			return true;
		}



		public IPattern<List<IExtendedModifier>> getModifiers() {
			return modifiers;
		}

		public void setModifiers(IPattern<List<IExtendedModifier>> p) {
			this.modifiers = p;
		}

		public IPattern<Integer> getModifierFlags() {
			return modifierFlags;
		}

		public void setModifierFlags(IPattern<Integer> p) {
			this.modifierFlags = p;
		}

		public IPattern<Name> getVariableName() {
			return variableName;
		}

		public void setVariableName(IPattern<Name> p) {
			this.variableName = p;
		}

		public IPattern<Type> getType() {
			return type;
		}

		public void setType(IPattern<Type> type) {
			this.type = type;
		}

		public IPattern<Boolean> getVariableArity() {
			return variableArity;
		}

		public void setVariableArity(IPattern<Boolean> variableArity) {
			this.variableArity = variableArity;
		}

		public IPattern<Integer> getExtraArrayDimensions() {
			return extraArrayDimensions;
		}

		public void setExtraArrayDimensions(IPattern<Integer> p) {
			this.extraArrayDimensions = p;
		}

		public IPattern<Expression> getOptionalInitializer() {
			return optionalInitializer;
		}

		public void setOptionalInitializer(IPattern<Expression> p) {
			this.optionalInitializer = p;
		}

		
	}

	// ------------------------------------------------------------------------
	
	/** pattern for org.eclipse.jdt.core.dom.VariableDeclarationFragment */
	public static class VariableDeclarationFragmentPattern extends VariableDeclarationPattern<VariableDeclarationFragment> {

		/**
		 * The variable name pattern
		 */
		private IPattern<SimpleName> variableName;

		/**
		 * The number of extra array dimensions that this variable has
		 */
		private IPattern<Integer> extraArrayDimensions;

		/**
		 * The initializer expression pattern
		 */
		private IPattern<Expression> optionalInitializer;

		// ------------------------------------------------------------------------

		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof VariableDeclarationFragment)) return false;
			return v.visitMatch(this, (VariableDeclarationFragment)node);
		}
		
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			super.recurseVisit(v);
			if (variableName != null) variableName.accept(v);
			if (extraArrayDimensions != null) extraArrayDimensions.accept(v);
			if (optionalInitializer != null) optionalInitializer.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, VariableDeclarationFragment node) {
			if (!super.recursevisitMatch(v, node)) return false;
			if (variableName != null && !variableName.acceptMatch(v, node.getName())) return v.mismatch("variableName");
			if (extraArrayDimensions != null && !extraArrayDimensions.acceptMatch(v, node.getExtraDimensions())) return v.mismatch("extraArrayDimensions");
			if (optionalInitializer != null && !optionalInitializer.acceptMatch(v, node.getInitializer())) return v.mismatch("optionalInitializer");
			return true;
		}


		public IPattern<SimpleName> getVariableName() {
			return variableName;
		}

		public void setVariableName(IPattern<SimpleName> p) {
			this.variableName = p;
		}

		public IPattern<Integer> getExtraArrayDimensions() {
			return extraArrayDimensions;
		}

		public void setExtraArrayDimensions(IPattern<Integer> p) {
			this.extraArrayDimensions = p;
		}

		public IPattern<Expression> getOptionalInitializer() {
			return optionalInitializer;
		}

		public void setOptionalInitializer(IPattern<Expression> p) {
			this.optionalInitializer = p;
		}

		
	}
	
	// ------------------------------------------------------------------------

	/** pattern for int modifiers flags */
	public static class ModifierFlagsPattern extends AbstractASTNodePattern<Integer> {

		protected int modifiers;

		// ------------------------------------------------------------------------

		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof Integer)) return false;
			return v.visitMatch(this, ((Integer)node).intValue());
		}
		
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			// no recurse
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, Integer node) {
			// no recurse
			return true;
		}


		public int getModifiers() {
			return modifiers;
		}

		public void setModifiers(int modifiers) {
			this.modifiers = modifiers;
		}
		
	}
	
	// ------------------------------------------------------------------------
	
	/** pattern for IExtendedModifier */
	public static class ExtendedModifierPattern extends AbstractASTNodePattern<IExtendedModifier> {
		// TODO

		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof IExtendedModifier)) return false;
			return v.visitMatch(this, (IExtendedModifier)node);
		}
		
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			// no recurse
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, IExtendedModifier node) {
			// no recurse
			return true;
		}


	}



	// ------------------------------------------------------------------------
	
	
	/** pattern for org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration */
	public static class AnnotationTypeMemberDeclarationPattern extends BodyDeclarationPattern<AnnotationTypeMemberDeclaration> {
		/**
		 * The member name
		 */
		private IPattern<Name> memberName;
	
		/**
		 * The member type
		 */
		private IPattern<Type> memberType;
	
		/**
		 * The optional default expression pattern
		 */
		private IPattern<Expression> optionalDefaultValue;

		// ------------------------------------------------------------------------

		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof AnnotationTypeMemberDeclaration)) return false;
			return v.visitMatch(this, (AnnotationTypeMemberDeclaration)node);
		}
		
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			super.recurseVisit(v);
			if (memberName != null) memberName.accept(v);
			if (memberType != null) memberType.accept(v);
			if (optionalDefaultValue != null) optionalDefaultValue.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, AnnotationTypeMemberDeclaration node) {
			if (!super.recursevisitMatch(v, node)) return false;
			if (memberName != null && !memberName.acceptMatch(v, node.getName())) return v.mismatch("memberName");
			if (memberType != null && !memberType.acceptMatch(v, node.getType())) return v.mismatch("memberType");
			if (optionalDefaultValue != null && !optionalDefaultValue.acceptMatch(v, node.getDefault())) return v.mismatch("optionalDefaultValue");
			return true;
		}



		public IPattern<Name> getMemberName() {
			return memberName;
		}

		public void setMemberName(IPattern<Name> memberName) {
			this.memberName = memberName;
		}

		public IPattern<Type> getMemberType() {
			return memberType;
		}

		public void setMemberType(IPattern<Type> memberType) {
			this.memberType = memberType;
		}

		public IPattern<Expression> getOptionalDefaultValue() {
			return optionalDefaultValue;
		}

		public void setOptionalDefaultValue(IPattern<Expression> optionalDefaultValue) {
			this.optionalDefaultValue = optionalDefaultValue;
		}
		
	}
	
	// ------------------------------------------------------------------------
	
	/** pattern for org.eclipse.jdt.core.dom.Initializer */
	public static class InitializerPattern extends BodyDeclarationPattern<Initializer> {
		
		/**
		 * The initializer body pattern
		 */
		private IPattern<Block> body;

		// ------------------------------------------------------------------------

		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof Initializer)) return false;
			return v.visitMatch(this, (Initializer)node);
		}
		
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			super.recurseVisit(v);
			if (body != null) body.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, Initializer node) {
			if (!super.recursevisitMatch(v, node)) return false;
			if (body != null && !body.acceptMatch(v, node.getBody())) return v.mismatch("body");
			return true;
		}

		
		public IPattern<Block> getBody() {
			return body;
		}

		public void setBody(IPattern<Block> body) {
			this.body = body;
		}
		
	}

	// ------------------------------------------------------------------------
	
	/** pattern for org.eclipse.jdt.core.dom.ImportDeclaration */
	public static class ImportDeclarationPattern extends AbstractASTNodePattern<ImportDeclaration> {

		/**
		 * The import name
		 */
		private IPattern<Name> importName;

		/**
		 * On demand versus single type import; defaults to single type import.
		 */
		private IPattern<Boolean> onDemand;

		/**
		 * Static versus regular
		 */
		private IPattern<Boolean> isStatic;

		// ------------------------------------------------------------------------

		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof ImportDeclaration)) return false;
			return v.visitMatch(this, (ImportDeclaration)node);
		}
		
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (importName != null) importName.accept(v);
			// TODO BooleanPatternPattern
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, ImportDeclaration node) {
			if (importName != null && !importName.acceptMatch(v, node.getName())) return v.mismatch("importName");
			return true;
		}

		
		public IPattern<Name> getImportName() {
			return importName;
		}

		public void setImportName(IPattern<Name> importName) {
			this.importName = importName;
		}

		public IPattern<Boolean> getOnDemand() {
			return onDemand;
		}

		public void setOnDemand(IPattern<Boolean> onDemand) {
			this.onDemand = onDemand;
		}

		public IPattern<Boolean> getIsStatic() {
			return isStatic;
		}

		public void setIsStatic(IPattern<Boolean> isStatic) {
			this.isStatic = isStatic;
		}

	}

	// ------------------------------------------------------------------------
	
	/** pattern for org.eclipse.jdt.core.dom.MarkerAnnotation */
	public static class MarkerAnnotationPattern extends AnnotationPattern<MarkerAnnotation> {
		// no fields

		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof MarkerAnnotation)) return false;
			return v.visitMatch(this, (MarkerAnnotation)node);
		}
		
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			super.recurseVisit(v);
			// no recurse
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, MarkerAnnotation node) {
			if (!super.recursevisitMatch(v, node)) return false;
			// no recurse
			return true;
		}

	}
	
	// ------------------------------------------------------------------------
	
	/** pattern for org.eclipse.jdt.core.dom.Modifier */
	public static class ModifierPattern extends AbstractASTNodePattern<Modifier> {

		protected IPattern<String> keyword;

		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof Modifier)) return false;
			return v.visitMatch(this, (Modifier)node);
		}
		
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (keyword != null) keyword.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, Modifier node) {
			if (keyword != null && !keyword.acceptMatch(v, node.getKeyword().toString())) return v.mismatch("keyword");
			return true;
		}


		public IPattern<String> getKeyword() {
			return keyword;
		}

		public void setKeyword(IPattern<String> keyword) {
			this.keyword = keyword;
		}
		
	}
	
}
