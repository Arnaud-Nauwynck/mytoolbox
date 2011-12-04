package fr.an.eclipse.pattern.ast.impl;

import java.util.List;

import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import fr.an.eclipse.pattern.ast.IPattern;
import fr.an.eclipse.pattern.ast.PatternMatchVisitor;
import fr.an.eclipse.pattern.ast.PatternVisitor;
import fr.an.eclipse.pattern.ast.impl.StdBasePatterns.DefaultStringPattern;


public class StdExpressionASTPatterns {


	
	/** pattern for @see org.eclipse.jdt.core.dom.Expression */
	public static abstract class ExpressionPattern<T extends Expression> extends AbstractASTNodePattern<T> {
	}

	// ------------------------------------------------------------------------
	
	/** pattern for @see org.eclipse.jdt.core.dom.Name */
	public static abstract class NamePattern<T extends Name> extends ExpressionPattern<T> {
		
	}

	/** pattern for @see org.eclipse.jdt.core.dom.SimpleName */
	public static class SimpleNamePattern extends NamePattern<SimpleName> {
		/**
		 * The identifier pattern
		 */
		private IPattern<String> identifier;

		// ------------------------------------------------------------------------

		public SimpleNamePattern() {
		}

		public SimpleNamePattern(IPattern<String> identifier) {
			this.identifier = identifier;
		}

		public SimpleNamePattern(String identifier) {
			this.identifier = new DefaultStringPattern(identifier);
		}

		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof SimpleName)) return false;
			return v.visitMatch(this, (SimpleName)node);
		}
		
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (identifier != null) identifier.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, SimpleName node) {
			if (identifier != null && !identifier.acceptMatch(v, node.getIdentifier())) return v.mismatch("identifier");
			return true;
		}

		
		public IPattern<String> getIdentifier() {
			return identifier;
		}

		public void setIdentifier(IPattern<String> identifier) {
			this.identifier = identifier;
		}
		
	}

	// ------------------------------------------------------------------------
	
	/** pattern for org.eclipse.jdt.core.dom.QualifiedName */
	public static class QualifiedNamePattern extends NamePattern<QualifiedName> {

		/** Qualifier part (potentially <code>null</code>). */
		private IPattern<Name> qualifier;

		/** Local name part. */
		private IPattern<SimpleName> localName;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof QualifiedName)) return false;
			return v.visitMatch(this, (QualifiedName)node);
		}
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (qualifier != null) qualifier.accept(v);
			if (localName != null) localName.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, QualifiedName node) {
			if (qualifier != null && !qualifier.acceptMatch(v, node.getQualifier())) return v.mismatch("qualifier");
			if (localName != null && !localName.acceptMatch(v, node.getName())) return v.mismatch("localName");
			return true;
		}

		
		public IPattern<Name> getQualifier() {
			return qualifier;
		}

		public void setQualifier(IPattern<Name> qualifier) {
			this.qualifier = qualifier;
		}

		public IPattern<SimpleName> getLocalName() {
			return localName;
		}

		public void setLocalName(IPattern<SimpleName> localName) {
			this.localName = localName;
		}
		
	}

	// ------------------------------------------------------------------------
	
	/** pattern for org.eclipse.jdt.core.dom.Annotation */
	public static class AnnotationPattern<T extends Annotation> extends ExpressionPattern<T> {
		/**
		 * The annotation type name
		 */
		private IPattern<Name> typeName;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof Annotation)) return false;
			return v.visitMatch(this, (Annotation)node);
		}
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (typeName != null) typeName.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, Annotation node) {
			if (typeName != null && !typeName.acceptMatch(v, node.getTypeName())) return v.mismatch("typeName");
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
	
	/** pattern for org.eclipse.jdt.core.dom.NormalAnnotation */
	public static class NormalAnnotationPattern extends AnnotationPattern<NormalAnnotation> {
		/**
		 * The list of member value pairs
		 */
		private IPattern<List<MemberValuePairPattern>> values;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof NormalAnnotation)) return false;
			return v.visitMatch(this, (NormalAnnotation)node);
		}
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (values != null) values.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, NormalAnnotation node) {
			if (values != null && !values.acceptMatch(v, node.values())) return v.mismatch("values");
			return true;
		}


		public IPattern<List<MemberValuePairPattern>> getValues() {
			return values;
		}

		public void setValues(IPattern<List<MemberValuePairPattern>> values) {
			this.values = values;
		}
		
	}
	
	// ------------------------------------------------------------------------
	
	/** pattern for org.eclipse.jdt.core.dom.MemberValuePair */
	public static class MemberValuePairPattern extends AbstractASTNodePattern<MemberValuePair> {

		/**
		 * The member name
		 */
		private IPattern<SimpleName> name = null;

		/**
		 * The value
		 */
		private IPattern<Expression> value = null;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof MemberValuePair)) return false;
			return v.visitMatch(this, (MemberValuePair)node);
		}
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (name != null) name.accept(v);
			if (value != null) value.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, MemberValuePair node) {
			if (name != null && !name.acceptMatch(v, node.getName())) return v.mismatch("name");
			if (value != null && !value.acceptMatch(v, node.getValue())) return v.mismatch("value");
			return true;
		}

		
		public IPattern<SimpleName> getName() {
			return name;
		}

		public void setName(IPattern<SimpleName> name) {
			this.name = name;
		}

		public IPattern<Expression> getValue() {
			return value;
		}

		public void setValue(IPattern<Expression> value) {
			this.value = value;
		}
		
	}
	
	// ------------------------------------------------------------------------

	/** pattern for org.eclipse.jdt.core.dom.SingleMemberAnnotation */
	public static class SingleMemberAnnotationPattern extends AnnotationPattern<SingleMemberAnnotation> {
	
		private IPattern<Expression> value;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof SingleMemberAnnotation)) return false;
			return v.visitMatch(this, (SingleMemberAnnotation)node);
		}
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			super.recurseVisit(v);
			if (value != null) value.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, SingleMemberAnnotation node) {
			if (!super.recursevisitMatch(v, node)) return false;
			if (value != null && !value.acceptMatch(v, node.getValue())) return v.mismatch("value");
			return true;
		}

		
		public IPattern<Expression> getValue() {
			return value;
		}

		public void setValue(IPattern<Expression> value) {
			this.value = value;
		}
		
		
	}
	
	// ------------------------------------------------------------------------

	/** pattern for org.eclipse.jdt.core.dom.ArrayAccess */
	public static class ArrayAccessPattern extends ExpressionPattern<ArrayAccess> {
		/**
		 * The array expression
		 */
		private IPattern<Expression> arrayExpression = null;
	
		/**
		 * The index expression
		 */
		private IPattern<Expression> indexExpression = null;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof ArrayAccess)) return false;
			return v.visitMatch(this, (ArrayAccess)node);
		}
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (arrayExpression != null) arrayExpression.accept(v);
			if (indexExpression != null) indexExpression.accept(v);			
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, ArrayAccess node) {
			if (arrayExpression != null && !arrayExpression.acceptMatch(v, node.getArray())) return v.mismatch("arrayExpression");
			if (indexExpression != null && !indexExpression.acceptMatch(v, node.getIndex())) return v.mismatch("indexExpression");			
			return true;			
		}

		
		public IPattern<Expression> getArrayExpression() {
			return arrayExpression;
		}

		public void setArrayExpression(IPattern<Expression> arrayExpression) {
			this.arrayExpression = arrayExpression;
		}

		public IPattern<Expression> getIndexExpression() {
			return indexExpression;
		}

		public void setIndexExpression(IPattern<Expression> indexExpression) {
			this.indexExpression = indexExpression;
		}
		
	}
	
	// ------------------------------------------------------------------------

	/** pattern for org.eclipse.jdt.core.dom.ArrayCreation */
	public static class ArrayCreationPattern extends ExpressionPattern<ArrayCreation> {
		/**
		 * The array type pattern
		 */
		private IPattern<ArrayType> arrayType;
	
		/**
		 * The list of dimension expressions
		 */
		private IPattern<List<Expression>> dimensions;
	
		/**
		 * The optional array initializer pattern
		 */
		private IPattern<ArrayInitializer> optionalInitializer;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof ArrayCreation)) return false;
			return v.visitMatch(this, (ArrayCreation)node);
		}
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (arrayType != null) arrayType.accept(v);
			if (dimensions != null) dimensions.accept(v);
			if (optionalInitializer != null) optionalInitializer.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, ArrayCreation node) {
			if (arrayType != null && !arrayType.acceptMatch(v, node.getType())) return v.mismatch("arrayType");
			if (dimensions != null && !dimensions.acceptMatch(v, node.dimensions())) return v.mismatch("dimensions");
			if (optionalInitializer != null && !optionalInitializer.acceptMatch(v, node.getInitializer())) return v.mismatch("optionalInitializer");
			return true;
		}

		
		public IPattern<ArrayType> getArrayType() {
			return arrayType;
		}

		public void setArrayType(IPattern<ArrayType> arrayType) {
			this.arrayType = arrayType;
		}

		public IPattern<List<Expression>> getDimensions() {
			return dimensions;
		}

		public void setDimensions(IPattern<List<Expression>> p) {
			this.dimensions = p;
		}

		public IPattern<ArrayInitializer> getOptionalInitializer() {
			return optionalInitializer;
		}

		public void setOptionalInitializer(IPattern<ArrayInitializer> optionalInitializer) {
			this.optionalInitializer = optionalInitializer;
		}
		
	}
	
	// ------------------------------------------------------------------------

	/** pattern for org.eclipse.jdt.core.dom.ArrayInitializer */
	public static class ArrayInitializerPattern extends ExpressionPattern<ArrayInitializer> {
		
		/**
		 * The list of expressions pattern
		 */
		private IPattern<List<Expression>> expressions;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof ArrayInitializer)) return false;
			return v.visitMatch(this, (ArrayInitializer)node);
		}
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (expressions != null) expressions.accept(v);	
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, ArrayInitializer node) {
			if (expressions != null && !expressions.acceptMatch(v, node.expressions())) return v.mismatch("expressions");	
			return true;	
		}

		
		public IPattern<List<Expression>> getExpressions() {
			return expressions;
		}

		public void setExpressions(IPattern<List<Expression>> p) {
			this.expressions = p;
		}
		
	}

	// ------------------------------------------------------------------------
	
	/** pattern for org.eclipse.jdt.core.dom.Assignment */
	public static class AssignmentPattern extends ExpressionPattern<Assignment> {

		/**
		 * The assignment operator; defaults to Assignment.Operator.ASSIGN
		 */
		private IPattern<Assignment.Operator> assignmentOperator;

		/**
		 * The left hand side
		 */
		private IPattern<Expression> leftHandSide;

		/**
		 * The right hand side
		 */
		private IPattern<Expression> rightHandSide;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof Assignment)) return false;
			return v.visitMatch(this, (Assignment)node);
		}
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (assignmentOperator != null) assignmentOperator.accept(v);
			if (leftHandSide != null) leftHandSide.accept(v);
			if (rightHandSide != null) rightHandSide.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, Assignment node) {
			if (assignmentOperator != null && !assignmentOperator.acceptMatch(v, node.getOperator())) return v.mismatch("assignmentOperator");
			if (leftHandSide != null && !leftHandSide.acceptMatch(v, node.getLeftHandSide())) return v.mismatch("leftHandSide");
			if (rightHandSide != null && !rightHandSide.acceptMatch(v, node.getRightHandSide())) return v.mismatch("rightHandSide");
			return true;
		}

		
		public IPattern<Assignment.Operator> getAssignmentOperator() {
			return assignmentOperator;
		}

		public void setAssignmentOperator(IPattern<Assignment.Operator> p) {
			this.assignmentOperator = p;
		}

		public IPattern<Expression> getLeftHandSide() {
			return leftHandSide;
		}

		public void setLeftHandSide(IPattern<Expression> leftHandSide) {
			this.leftHandSide = leftHandSide;
		}

		public IPattern<Expression> getRightHandSide() {
			return rightHandSide;
		}

		public void setRightHandSide(IPattern<Expression> rightHandSide) {
			this.rightHandSide = rightHandSide;
		}
		
	}
	
	public static class AssignmentOperatorPattern extends AbstractASTNodePattern<Assignment.Operator> {
		/**
		 * The name of the operator
		 */
		private IPattern<String> op;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof Assignment.Operator)) return false;
			return v.visitMatch(this, (Assignment.Operator)node);
		}
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (op != null) op.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, Assignment.Operator node) {
			if (op != null && !op.acceptMatch(v, node.toString())) return v.mismatch("op");
			return true;
		}

		
		public IPattern<String> getOp() {
			return op;
		}

		public void setOp(IPattern<String> op) {
			this.op = op;
		}
		
	}
	
	// ------------------------------------------------------------------------

	/** pattern for org.eclipse.jdt.core.dom.BooleanLiteral */
	public static class BooleanLiteralPattern extends ExpressionPattern<BooleanLiteral> {
	
		private IPattern<Boolean> value;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof BooleanLiteral)) return false;
			return v.visitMatch(this, (BooleanLiteral)node);
		}
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (value != null) value.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, BooleanLiteral node) {
			if (value != null && !value.acceptMatch(v, node.booleanValue())) return v.mismatch("value");
			return true;
		}

		
		public IPattern<Boolean> getValue() {
			return value;
		}

		public void setValue(IPattern<Boolean> value) {
			this.value = value;
		}
		
	}
	
	// ------------------------------------------------------------------------

	/** pattern for org.eclipse.jdt.core.dom.CastExpression */
	public static class CastExpressionPattern extends ExpressionPattern<CastExpression> {

		/**
		 * The type
		 */
		private IPattern<Type> type;

		/**
		 * The expression
		 */
		private IPattern<Expression> expression;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof CastExpression)) return false;
			return v.visitMatch(this, (CastExpression)node);
		}
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (type != null) type.accept(v);
			if (expression != null) expression.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, CastExpression node) {
			if (type != null && !type.acceptMatch(v, node.getType())) return v.mismatch("type");
			if (expression != null && !expression.acceptMatch(v, node.getExpression())) return v.mismatch("expression");
			return true;
		}


		public IPattern<Type> getType() {
			return type;
		}

		public void setType(IPattern<Type> type) {
			this.type = type;
		}

		public IPattern<Expression> getExpression() {
			return expression;
		}

		public void setExpression(IPattern<Expression> expression) {
			this.expression = expression;
		}
		
	}
	

	// ------------------------------------------------------------------------

	/** pattern for org.eclipse.jdt.core.dom.CharacterLiteral */
	public static class CharacterLiteralPattern extends ExpressionPattern<CharacterLiteral> {

		private Character value;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof CharacterLiteral)) return false;
			return v.visitMatch(this, (CharacterLiteral)node);
		}
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			// TODO CharacterPattern
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, CharacterLiteral node) {
			// TODO CharacterPattern
			return true;
		}


		public Character getValue() {
			return value;
		}

		public void setValue(Character value) {
			this.value = value;
		}
		
	}

	// ------------------------------------------------------------------------
	
	/** pattern for org.eclipse.jdt.core.dom.ClassInstanceCreation */
	public static class ClassInstanceCreationPattern extends ExpressionPattern<ClassInstanceCreation> {

		/**
		 * The optional expression pattern
		 */
		private IPattern<Expression> optionalExpression;

		/**
		 * The type arguments
		 */
		private IPattern<List<Type>> typeArguments;

		/**
		 * The type 
		 */
		private IPattern<Type> type = null;

		/**
		 * The list of argument expressions
		 */
		private IPattern<List<Expression>> arguments;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof ClassInstanceCreation)) return false;
			return v.visitMatch(this, (ClassInstanceCreation)node);
		}
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (optionalExpression != null) optionalExpression.accept(v);
			if (typeArguments != null) typeArguments.accept(v);
			if (type!= null) type.accept(v);
			if (arguments != null) arguments.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, ClassInstanceCreation node) {
			if (optionalExpression != null && !optionalExpression.acceptMatch(v, node.getExpression())) return v.mismatch("optionalExpression");
			if (typeArguments != null && !typeArguments.acceptMatch(v, node.typeArguments())) return v.mismatch("typeArguments");
			if (type!= null && !type.acceptMatch(v, node.getType())) return v.mismatch("type");
			if (arguments != null && !arguments.acceptMatch(v, node.arguments())) return v.mismatch("arguments");
			return true;
		}

		
		public IPattern<Expression> getOptionalExpression() {
			return optionalExpression;
		}

		public void setOptionalExpression(IPattern<Expression> optionalExpression) {
			this.optionalExpression = optionalExpression;
		}

		public IPattern<List<Type>> getTypeArguments() {
			return typeArguments;
		}

		public void setTypeArguments(IPattern<List<Type>> typeArguments) {
			this.typeArguments = typeArguments;
		}

		public IPattern<Type> getType() {
			return type;
		}

		public void setType(IPattern<Type> type) {
			this.type = type;
		}

		public IPattern<List<Expression>> getArguments() {
			return arguments;
		}

		public void setArguments(IPattern<List<Expression>> arguments) {
			this.arguments = arguments;
		}
		
	}

	// ------------------------------------------------------------------------
	
	/** pattern for org.eclipse.jdt.core.dom.ConditionalExpression */
	public static class ConditionalExpressionPattern extends ExpressionPattern<ConditionalExpression> {

		/**
		 * The condition expression
		 */
		private IPattern<Expression> conditionExpression = null;

		/**
		 * The "then" expression
		 */
		private IPattern<Expression> thenExpression = null;

		/**
		 * The "else" expression
		 */
		private IPattern<Expression> elseExpression = null;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof ConditionalExpression)) return false;
			return v.visitMatch(this, (ConditionalExpression)node);
		}

		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (conditionExpression != null) conditionExpression.accept(v);
			if (thenExpression != null) thenExpression.accept(v);
			if (elseExpression != null) elseExpression.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, ConditionalExpression node) {
			if (conditionExpression != null && !conditionExpression.acceptMatch(v, node.getExpression())) return v.mismatch("conditionExpression");
			if (thenExpression != null && !thenExpression.acceptMatch(v, node.getThenExpression())) return v.mismatch("thenExpression");
			if (elseExpression != null && !elseExpression.acceptMatch(v, node.getElseExpression())) return v.mismatch("elseExpression");
			return true;
		}

		
		public IPattern<Expression> getConditionExpression() {
			return conditionExpression;
		}

		public void setConditionExpression(IPattern<Expression> conditionExpression) {
			this.conditionExpression = conditionExpression;
		}

		public IPattern<Expression> getThenExpression() {
			return thenExpression;
		}

		public void setThenExpression(IPattern<Expression> thenExpression) {
			this.thenExpression = thenExpression;
		}

		public IPattern<Expression> getElseExpression() {
			return elseExpression;
		}

		public void setElseExpression(IPattern<Expression> elseExpression) {
			this.elseExpression = elseExpression;
		}
		
	}

	// ------------------------------------------------------------------------
	
	/** pattern for pattern for org.eclipse.jdt.core.dom.FieldAccess */
	public static class FieldAccessPattern extends ExpressionPattern<FieldAccess> {

		/**
		 * The expression pattern
		 */
		private IPattern<Expression> expression = null;

		/**
		 * The field pattern
		 */
		private IPattern<SimpleName> fieldName = null;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof FieldAccess)) return false;
			return v.visitMatch(this, (FieldAccess)node);
		}
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (expression != null) expression.accept(v);
			if (fieldName != null) fieldName.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, FieldAccess node) {
			if (expression != null && !expression.acceptMatch(v, node.getExpression())) return v.mismatch("expression");
			if (fieldName != null && !fieldName.acceptMatch(v, node.getName())) return v.mismatch("fieldName");
			return true;
		}

		
		public IPattern<Expression> getExpression() {
			return expression;
		}

		public void setExpression(IPattern<Expression> expression) {
			this.expression = expression;
		}

		public IPattern<SimpleName> getFieldName() {
			return fieldName;
		}

		public void setFieldName(IPattern<SimpleName> fieldName) {
			this.fieldName = fieldName;
		}
		
	}

	// ------------------------------------------------------------------------
	
	/** pattern for pattern for org.eclipse.jdt.core.dom.InfixExpression */
	public static class InfixExpressionPattern extends ExpressionPattern<InfixExpression> {

		/**
		 * The infix operator
		 */
		private IPattern<InfixExpression.Operator> operator;

		/**
		 * The left operand
		 */
		private IPattern<Expression> leftOperand = null;

		/**
		 * The right operand
		 */
		private IPattern<Expression> rightOperand = null;

		/**
		 * The list of extended operand expressions
		 */
		private IPattern<List<Expression>> extendedOperands;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof InfixExpression)) return false;
			return v.visitMatch(this, (InfixExpression)node);
		}
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (operator != null) operator.accept(v);
			if (leftOperand != null) leftOperand.accept(v);
			if (rightOperand != null) rightOperand.accept(v);
			if (extendedOperands != null) extendedOperands.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, InfixExpression node) {
			if (operator != null && !operator.acceptMatch(v, node.getOperator())) return v.mismatch("operator");
			if (leftOperand != null && !leftOperand.acceptMatch(v, node.getLeftOperand())) return v.mismatch("leftOperand");
			if (rightOperand != null && !rightOperand.acceptMatch(v, node.getRightOperand())) return v.mismatch("rightOperand");
			if (extendedOperands != null && !extendedOperands.acceptMatch(v, node.extendedOperands())) return v.mismatch("extendedOperands");
			return true;
		}

		
		public IPattern<InfixExpression.Operator> getOperator() {
			return operator;
		}

		public void setOperator(IPattern<InfixExpression.Operator> operator) {
			this.operator = operator;
		}

		public IPattern<Expression> getLeftOperand() {
			return leftOperand;
		}

		public void setLeftOperand(IPattern<Expression> p) {
			this.leftOperand = p;
		}

		public IPattern<Expression> getRightOperand() {
			return rightOperand;
		}

		public void setRightOperand(IPattern<Expression> p) {
			this.rightOperand = p;
		}

		public IPattern<List<Expression>> getExtendedOperands() {
			return extendedOperands;
		}

		public void setExtendedOperands(IPattern<List<Expression>> p) {
			this.extendedOperands = p;
		}

		
	}

	// ------------------------------------------------------------------------
	
	/** pattern for InfixExpression.Operator */
	public static class InfixExpressionOperatorPattern extends AbstractASTNodePattern<InfixExpression.Operator> {

		private IPattern<String> op;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof InfixExpression.Operator)) return false;
			return v.visitMatch(this, (InfixExpression.Operator)node);
		}
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (op != null) op.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, InfixExpression.Operator node) {
			if (op != null && !op.acceptMatch(v, node.toString())) return v.mismatch("op");
			return true;
		}


		public IPattern<String> getOp() {
			return op;
		}

		public void setOp(IPattern<String> op) {
			this.op = op;
		}
		
	}
	
	// ------------------------------------------------------------------------
	
	/** pattern for pattern for org.eclipse.jdt.core.dom.InstanceofExpression */
	public static class InstanceofExpressionPattern extends ExpressionPattern<InstanceofExpression> {

		/**
		 * The left operand
		 */
		private IPattern<Expression> leftOperand = null;

		/**
		 * The right operand
		 */
		private IPattern<Type> rightOperand = null;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof InstanceofExpression)) return false;
			return v.visitMatch(this, (InstanceofExpression)node);
		}

		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (leftOperand != null) leftOperand.accept(v);
			if (rightOperand != null) rightOperand.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, InstanceofExpression node) {
			if (leftOperand != null && !leftOperand.acceptMatch(v, node.getLeftOperand())) return v.mismatch("leftOperand");
			if (rightOperand != null && !rightOperand.acceptMatch(v, node.getRightOperand())) return v.mismatch("rightOperand");
			return true;
		}


		public IPattern<Expression> getLeftOperand() {
			return leftOperand;
		}

		public void setLeftOperand(IPattern<Expression> leftOperand) {
			this.leftOperand = leftOperand;
		}

		public IPattern<Type> getRightOperand() {
			return rightOperand;
		}

		public void setRightOperand(IPattern<Type> rightOperand) {
			this.rightOperand = rightOperand;
		}

	}

	
	// ------------------------------------------------------------------------
	
	/** pattern for pattern for org.eclipse.jdt.core.dom.MethodInvocation */
	public static class MethodInvocationPattern extends ExpressionPattern<MethodInvocation> {

		/**
		 * The expression
		 */
		private IPattern<Expression> optionalExpression;

		/**
		 * The type arguments
		 */
		private IPattern<List<Type>> typeArguments;

		/**
		 * The method name
		 */
		private IPattern<SimpleName> methodName;

		/**
		 * The list of argument expressions
		 */
		private IPattern<List<Expression>> arguments;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof MethodInvocation)) return false;
			return v.visitMatch(this, (MethodInvocation)node);
		}
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (optionalExpression != null) optionalExpression.accept(v);
			if (typeArguments != null) typeArguments.accept(v);
			if (methodName != null) methodName.accept(v);
			if (arguments != null) arguments.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, MethodInvocation node) {
			if (optionalExpression != null && !optionalExpression.acceptMatch(v, node.getExpression())) return v.mismatch("optionalExpression");
			if (typeArguments != null && !typeArguments.acceptMatch(v, node.typeArguments())) return v.mismatch("typeArguments");
			if (methodName != null && !methodName.acceptMatch(v, node.getName())) return v.mismatch("methodName");
			if (arguments != null && !arguments.acceptMatch(v, node.arguments())) return v.mismatch("arguments");
			return true;
		}

		
		public IPattern<Expression> getOptionalExpression() {
			return optionalExpression;
		}

		public void setOptionalExpression(IPattern<Expression> p) {
			this.optionalExpression = p;
		}

		public IPattern<List<Type>> getTypeArguments() {
			return typeArguments;
		}

		public void setTypeArguments(IPattern<List<Type>> p) {
			this.typeArguments = p;
		}

		public IPattern<SimpleName> getMethodName() {
			return methodName;
		}

		public void setMethodName(IPattern<SimpleName> p) {
			this.methodName = p;
		}

		public IPattern<List<Expression>> getArguments() {
			return arguments;
		}

		public void setArguments(IPattern<List<Expression>> p) {
			this.arguments = p;
		}
		
	}

	// ------------------------------------------------------------------------
	
	/** pattern for pattern for org.eclipse.jdt.core.dom.NullLiteral */
	public static class NullLiteralPattern extends ExpressionPattern<NullLiteral> {
		// no fields
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof NullLiteral)) return false;
			return v.visitMatch(this, (NullLiteral)node);
		}
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			// no recurse
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, NullLiteral node) {
			// no recurse
			return true;
		}

	}

	// ------------------------------------------------------------------------
	
	/** pattern for pattern for org.eclipse.jdt.core.dom.NumberLiteral */
	public static class NumberLiteralPattern extends ExpressionPattern<NumberLiteral> {
		/**
		 * The token string pattern
		 */
		private IPattern<String> tokenValue;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof NumberLiteral)) return false;
			return v.visitMatch(this, (NumberLiteral)node);
		}
		
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (tokenValue != null) tokenValue.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, NumberLiteral node) {
			if (tokenValue != null && !tokenValue.acceptMatch(v, node.getToken())) return v.mismatch("tokenValue");
			return true;
		}


		public IPattern<String> getTokenValue() {
			return tokenValue;
		}

		public void setTokenValue(IPattern<String> tokenValue) {
			this.tokenValue = tokenValue;
		}
		
	}

	// ------------------------------------------------------------------------
	
	/** pattern for pattern for org.eclipse.jdt.core.dom.ParenthesizedExpression */
	public static class ParenthesizedExpressionPattern extends ExpressionPattern<ParenthesizedExpression> {
		/**
		 * The expression
		 */
		private IPattern<Expression> expression;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof ParenthesizedExpression)) return false;
			return v.visitMatch(this, (ParenthesizedExpression)node);
		}
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (expression != null) expression.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, ParenthesizedExpression node) {
			if (expression != null && !expression.acceptMatch(v, node.getExpression())) return v.mismatch("expression");
			return true;
		}


		public IPattern<Expression> getExpression() {
			return expression;
		}

		public void setExpression(IPattern<Expression> expression) {
			this.expression = expression;
		}
		
	}
	
	// ------------------------------------------------------------------------
	
	/** pattern for pattern for org.eclipse.jdt.core.dom.PostfixExpression */
	public static class PostfixExpressionPattern extends ExpressionPattern<PostfixExpression> {

		/**
		 * The operator
		 */
		private IPattern<PostfixExpression.Operator> operator;

		/**
		 * The operand
		 */
		private IPattern<Expression> operand;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof PostfixExpression)) return false;
			return v.visitMatch(this, (PostfixExpression)node);
		}

		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (operator != null) operator.accept(v);
			if (operand != null) operand.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, PostfixExpression node) {
			if (operator != null && !operator.acceptMatch(v, node.getOperator())) return v.mismatch("operator");
			if (operand != null && !operand.acceptMatch(v, node.getOperand())) return v.mismatch("operand");
			return true;
		}


		
		public IPattern<PostfixExpression.Operator> getOperator() {
			return operator;
		}

		public void setOperator(IPattern<PostfixExpression.Operator> p) {
			this.operator = p;
		}

		public IPattern<Expression> getOperand() {
			return operand;
		}

		public void setOperand(IPattern<Expression> p) {
			this.operand = p;
		}
		
	}

	// ------------------------------------------------------------------------
	
	public static class PostfixExpressionOperatorPattern extends AbstractASTNodePattern<PostfixExpression.Operator> {
		
		private IPattern<String> op;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof PostfixExpression.Operator)) return false;
			return v.visitMatch(this, (PostfixExpression.Operator)node);
		}
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (op != null) op.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, PostfixExpression.Operator node) {
			if (op != null && !op.acceptMatch(v, node.toString())) return v.mismatch("op");
			return true;
		}


		public IPattern<String> getOp() {
			return op;
		}

		public void setOp(IPattern<String> op) {
			this.op = op;
		}
	}
	

	// ------------------------------------------------------------------------
	
	/** pattern for pattern for org.eclipse.jdt.core.dom.PrefixExpression */
	public static class PrefixExpressionPattern extends ExpressionPattern<PrefixExpression> {

		/**
		 * The operator
		 */
		private IPattern<PrefixExpression.Operator> operator;

		/**
		 * The operand
		 */
		private IPattern<Expression> operand;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof PrefixExpression)) return false;
			return v.visitMatch(this, (PrefixExpression)node);
		}

		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (operator != null) operator.accept(v);
			if (operand != null) operand.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, PrefixExpression node) {
			if (operator != null && !operator.acceptMatch(v, node.getOperator())) return v.mismatch("operator");
			if (operand != null && !operand.acceptMatch(v, node.getOperand())) return v.mismatch("operand");
			return true;
		}

	
		public IPattern<PrefixExpression.Operator> getOperator() {
			return operator;
		}

		public void setOperator(IPattern<PrefixExpression.Operator> p) {
			this.operator = p;
		}

		public IPattern<Expression> getOperand() {
			return operand;
		}

		public void setOperand(IPattern<Expression> p) {
			this.operand = p;
		}
		
	}

	// ------------------------------------------------------------------------
	
	public static class PrefixExpressionOperatorPattern extends AbstractASTNodePattern<PrefixExpression.Operator> {
	
		private IPattern<String> op;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof PrefixExpression.Operator)) return false;
			return v.visitMatch(this, (PrefixExpression.Operator)node);
		}

		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (op != null) op.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, PrefixExpression.Operator node) {
			if (op != null && !op.acceptMatch(v, node.toString())) return v.mismatch("op");
			return true;
		}

		
		public IPattern<String> getOp() {
			return op;
		}

		public void setOp(IPattern<String> op) {
			this.op = op;
		}
	}
	
	// ------------------------------------------------------------------------
	
	/** pattern for pattern for org.eclipse.jdt.core.dom.StringLiteral */
	public static class StringLiteralPattern extends ExpressionPattern<StringLiteral> {
		
		private IPattern<String> value;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof StringLiteral)) return false;
			return v.visitMatch(this, (StringLiteral)node);
		}
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (value != null) value.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, StringLiteral node) {
			if (value != null && !value.acceptMatch(v, node.getEscapedValue())) return v.mismatch("value");
			return true;
		}


		public IPattern<String> getValue() {
			return value;
		}

		public void setValue(IPattern<String> value) {
			this.value = value;
		}
		
	}

	// ------------------------------------------------------------------------
	
	/** pattern for pattern for org.eclipse.jdt.core.dom.SuperFieldAccess */
	public static class SuperFieldAccessPattern extends ExpressionPattern<SuperFieldAccess> {

		/**
		 * The optional qualifier; <code>null</code> for none
		 */
		private IPattern<Name> optionalQualifier;

		/**
		 * The field
		 */
		private IPattern<SimpleName> fieldName;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof SuperFieldAccess)) return false;
			return v.visitMatch(this, (SuperFieldAccess)node);
		}

		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (optionalQualifier != null) optionalQualifier.accept(v);
			if (fieldName != null) fieldName.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, SuperFieldAccess node) {
			if (optionalQualifier != null && !optionalQualifier.acceptMatch(v, node.getQualifier())) return v.mismatch("optionalQualifier");
			if (fieldName != null && !fieldName.acceptMatch(v, node.getName())) return v.mismatch("fieldName");
			return true;
		}
		
		public IPattern<Name> getOptionalQualifier() {
			return optionalQualifier;
		}

		public void setOptionalQualifier(IPattern<Name> optionalQualifier) {
			this.optionalQualifier = optionalQualifier;
		}

		public IPattern<SimpleName> getFieldName() {
			return fieldName;
		}

		public void setFieldName(IPattern<SimpleName> fieldName) {
			this.fieldName = fieldName;
		}

	}

	// ------------------------------------------------------------------------
	
	/** pattern for pattern for org.eclipse.jdt.core.dom.SuperMethodInvocation */
	public static class SuperMethodInvocationPattern extends ExpressionPattern<SuperMethodInvocation> {

		/**
		 * The optional qualifier; <code>null</code> for none
		 */
		private IPattern<Name> optionalQualifier;

		/**
		 * The type arguments
		 */
		private IPattern<List<Type>> typeArguments;

		/**
		 * The method name
		 */
		private IPattern<SimpleName> methodName;

		/**
		 * The list of argument expressions
		 */
		private IPattern<List<Expression>> arguments;
		
		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof SuperMethodInvocation)) return false;
			return v.visitMatch(this, (SuperMethodInvocation)node);
		}
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (optionalQualifier != null) optionalQualifier.accept(v);
			if (typeArguments != null) typeArguments.accept(v);
			if (methodName != null) methodName.accept(v);
			if (arguments != null) arguments.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, SuperMethodInvocation node) {
			if (optionalQualifier != null && !optionalQualifier.acceptMatch(v, node.getQualifier())) return v.mismatch("optionalQualifier");
			if (typeArguments != null && !typeArguments.acceptMatch(v, node.typeArguments())) return v.mismatch("typeArguments");
			if (methodName != null && !methodName.acceptMatch(v, node.getName())) return v.mismatch("methodName");
			if (arguments != null && !arguments.acceptMatch(v, node.arguments())) return v.mismatch("arguments");
			return true;
		}
	
		public IPattern<Name> getOptionalQualifier() {
			return optionalQualifier;
		}

		public void setOptionalQualifier(IPattern<Name> p) {
			this.optionalQualifier = p;
		}

		public IPattern<List<Type>> getTypeArguments() {
			return typeArguments;
		}

		public void setTypeArguments(IPattern<List<Type>> p) {
			this.typeArguments = p;
		}

		public IPattern<SimpleName> getMethodName() {
			return methodName;
		}

		public void setMethodName(IPattern<SimpleName> methodName) {
			this.methodName = methodName;
		}

		public IPattern<List<Expression>> getArguments() {
			return arguments;
		}

		public void setArguments(IPattern<List<Expression>> arguments) {
			this.arguments = arguments;
		}
		
	}

	
	
	// ------------------------------------------------------------------------
	
	/** pattern for pattern for org.eclipse.jdt.core.dom.ThisExpression */
	public static class ThisExpressionPattern extends ExpressionPattern<ThisExpression> {
		/**
		 * The optional qualifier
		 */
		private IPattern<Name> optionalQualifier;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof ThisExpression)) return false;
			return v.visitMatch(this, (ThisExpression)node);
		}
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (optionalQualifier != null) optionalQualifier.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, ThisExpression node) {
			if (optionalQualifier != null && !optionalQualifier.acceptMatch(v, node.getQualifier())) return v.mismatch("optionalQualifier");
			return true;
		}

		public IPattern<Name> getOptionalQualifier() {
			return optionalQualifier;
		}

		public void setOptionalQualifier(IPattern<Name> p) {
			this.optionalQualifier = p;
		}
		
	}
	
	// ------------------------------------------------------------------------
	
	/** pattern for pattern for org.eclipse.jdt.core.dom.VariableDeclarationExpression */
	public static class VariableDeclarationExpressionPattern extends ExpressionPattern<VariableDeclarationExpression> {

		/**
		 * The extended modifiers
		 */
		private IPattern<List<IExtendedModifier>> modifiers;

		/**
		 * The modifier flags pattern
		 */
		private IPattern<Integer> modifierFlags;

		/**
		 * The base type
		 */
		private IPattern<Type> baseType;

		/**
		 * The list of variable declaration fragments
		 */
		private IPattern<List<VariableDeclarationFragment>> variableDeclarationFragments;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof VariableDeclarationExpression)) return false;
			return v.visitMatch(this, (VariableDeclarationExpression)node);
		}
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (modifiers != null) modifiers.accept(v);
			if (modifierFlags != null) modifierFlags.accept(v);
			if (baseType != null) baseType.accept(v);
			if (variableDeclarationFragments != null) variableDeclarationFragments.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, VariableDeclarationExpression node) {
			if (modifiers != null && !modifiers.acceptMatch(v, node.getModifiers())) return v.mismatch("modifiers");
			if (modifierFlags != null && !modifierFlags.acceptMatch(v, node.modifiers())) return v.mismatch("modifierFlags");
			if (baseType != null && !baseType.acceptMatch(v, node.getType())) return v.mismatch("baseType");
			if (variableDeclarationFragments != null && !variableDeclarationFragments.acceptMatch(v, node.fragments())) return v.mismatch("variableDeclarationFragments");
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

		public void setModifierFlags(IPattern<Integer> modifierFlags) {
			this.modifierFlags = modifierFlags;
		}

		public IPattern<Type> getBaseType() {
			return baseType;
		}

		public void setBaseType(IPattern<Type> baseType) {
			this.baseType = baseType;
		}

		public IPattern<List<VariableDeclarationFragment>> getVariableDeclarationFragments() {
			return variableDeclarationFragments;
		}

		public void setVariableDeclarationFragments(IPattern<List<VariableDeclarationFragment>> p) {
			this.variableDeclarationFragments = p;
		}

		
	}
	
	// ------------------------------------------------------------------------
	
	/** pattern for pattern for org.eclipse.jdt.core.dom.TypeLiteral */
	public static class TypeLiteralPattern extends ExpressionPattern<TypeLiteral> {
		/**
		 * The type
		 */
		private IPattern<Type> type;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof TypeLiteral)) return false;
			return v.visitMatch(this, (TypeLiteral)node);
		}
		
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (type != null) type.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, TypeLiteral node) {
			if (type != null && !type.acceptMatch(v, node.getType())) return v.mismatch("type");
			return true;
		}

		
		public IPattern<Type> getType() {
			return type;
		}

		public void setType(IPattern<Type> type) {
			this.type = type;
		}
		
	}
	
	
}
