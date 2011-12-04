package fr.an.eclipse.pattern.ast.impl;

import java.util.List;

import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.UnionType;
import org.eclipse.jdt.core.dom.WildcardType;

import fr.an.eclipse.pattern.ast.IPattern;
import fr.an.eclipse.pattern.ast.PatternMatchVisitor;
import fr.an.eclipse.pattern.ast.PatternVisitor;


/**
 *
 */
public class StdTypesASTPatterns {

	// ------------------------------------------------------------------------
	
	/** pattern for org.eclipse.jdt.core.dom.Type */
	public static class TypePattern<T extends Type> extends AbstractASTNodePattern<Type> {
		// no field
		

		@Override
		void accept0(PatternVisitor visitor) {
			visitor.visit(this);
		}

		@Override
		boolean acceptMatch0(PatternMatchVisitor visitor, Object node) {
			if (node != null && !(node instanceof Type)) return visitor.mismatch("not a Type");
			return true;
		}

		public void recurseVisit(PatternVisitor v) {
			// no recurse
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, Type node) {
			// no recurse
			return true;
		}

		
	}
	
	// ------------------------------------------------------------------------

	/** pattern for org.eclipse.jdt.core.dom.TypeParameter */
	public static class TypeParameterPattern extends AbstractASTNodePattern<TypeParameter> {

		/**
		 * The type variable node
		 */
		private IPattern<SimpleName> typeVariableName;

		/**
		 * The type bounds
		 */
		private IPattern<List<Type>> typeBounds;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof TypeParameter)) return false;
			return v.visitMatch(this, (TypeParameter)node);
		}
		
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (typeVariableName != null) typeVariableName.accept(v);
			if (typeBounds != null) typeBounds.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, TypeParameter node) {
			if (typeVariableName != null && !typeVariableName.acceptMatch(v, node.getName())) return v.mismatch("typeVariableName");
			if (typeBounds != null && !typeBounds.acceptMatch(v, node.typeBounds())) return v.mismatch("typeBounds");
			return true;
		}



		public IPattern<SimpleName> getTypeVariableName() {
			return typeVariableName;
		}

		public void setTypeVariableName(IPattern<SimpleName> typeVariableName) {
			this.typeVariableName = typeVariableName;
		}

		public IPattern<List<Type>> getTypeBounds() {
			return typeBounds;
		}

		public void setTypeBounds(IPattern<List<Type>> typeBounds) {
			this.typeBounds = typeBounds;
		}
		
	}
	
	// ------------------------------------------------------------------------
	
	/** pattern for org.eclipse.jdt.core.dom.ArrayType */
	public static class ArrayTypePattern extends TypePattern<ArrayType> {
		/**
		 * The component type pattern
		 */
		private IPattern<Type> componentType;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof ArrayType)) return false;
			return v.visitMatch(this, (ArrayType)node);
		}
		
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			super.recurseVisit(v);
			if (componentType != null) componentType.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, ArrayType node) {
			if (!super.recursevisitMatch(v, node)) return false;
			if (componentType != null && !componentType.acceptMatch(v, node.getComponentType())) return v.mismatch("componentType");
			return true;
		}

		
		public IPattern<Type> getComponentType() {
			return componentType;
		}

		public void setComponentType(IPattern<Type> componentType) {
			this.componentType = componentType;
		}
		
	}
	
	// ------------------------------------------------------------------------
	
	/** pattern for org.eclipse.jdt.core.dom.ParameterizedType */
	public static class ParameterizedTypePattern extends TypePattern<ParameterizedType> {
//		/**
//	     * This index represents the position inside a parameterized qualified type.
//	     */
//	    int index;

		/**
		 * The type node
		 */
		private IPattern<Type> type = null;

		/**
		 * The type arguments
		 */
		private IPattern<List<Type>> typeArguments;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof ParameterizedType)) return false;
			return v.visitMatch(this, (ParameterizedType)node);
		}
		
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			super.recurseVisit(v);
			if (type != null) type.accept(v);
			if (typeArguments != null) typeArguments.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, ParameterizedType node) {
			if (!super.recursevisitMatch(v, node)) return false;
			if (type != null && !type.acceptMatch(v, node.getType())) return v.mismatch("type");
			if (typeArguments != null && !typeArguments.acceptMatch(v, node.typeArguments())) return v.mismatch("typeArguments");
			return true;
		}
		
		public IPattern<Type> getType() {
			return type;
		}

		public void setType(IPattern<Type> type) {
			this.type = type;
		}

		public IPattern<List<Type>> getTypeArguments() {
			return typeArguments;
		}

		public void setTypeArguments(IPattern<List<Type>> typeArguments) {
			this.typeArguments = typeArguments;
		}
		
	}
	
	// ------------------------------------------------------------------------
	
	/** pattern for org.eclipse.jdt.core.dom.PrimitiveType */
	public static class PrimitiveTypePattern extends TypePattern<PrimitiveType> {

		private IPattern<String> typeCode;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof PrimitiveType)) return false;
			return v.visitMatch(this, (PrimitiveType)node);
		}
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			super.recurseVisit(v);
			if (typeCode != null) typeCode.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, PrimitiveType node) {
			if (!super.recursevisitMatch(v, node)) return false;
			if (typeCode != null && !typeCode.acceptMatch(v, node.getPrimitiveTypeCode().toString())) return v.mismatch("typeCode");
			return true;
		}


		public IPattern<String> getCode() {
			return typeCode;
		}

		public void setCode(IPattern<String> code) {
			this.typeCode = code;
		}
		
	}
	
	// ------------------------------------------------------------------------
	
	/** pattern for org.eclipse.jdt.core.dom.QualifiedType */
	public static class QualifiedTypePattern extends TypePattern<QualifiedType> {

		/**
		 * The type node
		 */
		private IPattern<Type> qualifier;

		/**
		 * The name being qualified
		 */
		private IPattern<SimpleName> name;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof QualifiedType)) return false;
			return v.visitMatch(this, (QualifiedType)node);
		}
		
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			super.recurseVisit(v);
			if (qualifier != null) qualifier.accept(v);
			if (name != null) name.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, QualifiedType node) {
			if (!super.recursevisitMatch(v, node)) return false;
			if (qualifier != null && !qualifier.acceptMatch(v, node.getQualifier())) return v.mismatch("qualifier");
			if (name != null && !name.acceptMatch(v, node.getName())) return v.mismatch("name");
			return true;
		}


		public IPattern<Type> getQualifier() {
			return qualifier;
		}

		public void setQualifier(IPattern<Type> qualifier) {
			this.qualifier = qualifier;
		}

		public IPattern<SimpleName> getName() {
			return name;
		}

		public void setName(IPattern<SimpleName> name) {
			this.name = name;
		}
		
	}

	// ------------------------------------------------------------------------
	
	/** pattern for org.eclipse.jdt.core.dom.SimpleType */
	public static class SimpleTypePattern extends TypePattern<SimpleType> {

		/**
		 * The type name pattern
		 */
		private IPattern<Name> typeName;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof SimpleType)) return false;
			return v.visitMatch(this, (SimpleType)node);
		}
		
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			super.recurseVisit(v);
			if (typeName != null) typeName.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, SimpleType node) {
			if (!super.recursevisitMatch(v, node)) return false;
			if (typeName != null && !typeName.acceptMatch(v, node.getName())) return v.mismatch("typeName");
			return true;
		}
		
		public IPattern<Name> getTypeName() {
			return typeName;
		}

		public void setTypeName(IPattern<Name> typeName) {
			this.typeName = typeName;
		}
		
	}

	// ------------------------------------------------------------------------
	
	/** pattern for org.eclipse.jdt.core.dom.UnionType */ 
	public static class UnionTypePattern extends TypePattern<Type> {
		/**
		 * The list of types
		 */
		private IPattern<List<Type>> types;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof UnionType)) return false;
			return v.visitMatch(this, (UnionType)node);
		}
		
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			super.recurseVisit(v);
			if (types != null) types.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, UnionType node) {
			if (!super.recursevisitMatch(v, node)) return false;
			if (types != null && !types.acceptMatch(v, node.types())) return v.mismatch("types");
			return true;
		}


		public IPattern<List<Type>> getTypes() {
			return types;
		}

		public void setTypes(IPattern<List<Type>> types) {
			this.types = types;
		}

	}

	
	// ------------------------------------------------------------------------
	
	/** pattern for org.eclipse.jdt.core.dom.WildcardType */ 
	public static class WildcardTypePattern extends TypePattern<WildcardType> {

		/**
		 * The optional type bound node
		 */
		private IPattern<Type> optionalBound = null;

		/**
		 * Indicates whether the wildcard bound is an upper bound
		 * ("extends") as opposed to a lower bound ("super").
		 */
		private IPattern<Boolean> isUpperBound;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof WildcardType)) return false;
			return v.visitMatch(this, (WildcardType)node);
		}

		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			super.recurseVisit(v);
			if (optionalBound != null) optionalBound.accept(v);
			if (isUpperBound != null) isUpperBound.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, WildcardType node) {
			if (!super.recursevisitMatch(v, node)) return false;
			if (optionalBound != null && !optionalBound.acceptMatch(v, node.getBound())) return v.mismatch("optionalBound");
			if (isUpperBound != null &&!isUpperBound.acceptMatch(v, node.isUpperBound())) return v.mismatch("isUpperBound");
			return true;
		}


		public IPattern<Type> getOptionalBound() {
			return optionalBound;
		}

		public void setOptionalBound(IPattern<Type> optionalBound) {
			this.optionalBound = optionalBound;
		}

		public IPattern<Boolean> getIsUpperBound() {
			return isUpperBound;
		}

		public void setIsUpperBound(IPattern<Boolean> isUpperBound) {
			this.isUpperBound = isUpperBound;
		}

	}
	
}
