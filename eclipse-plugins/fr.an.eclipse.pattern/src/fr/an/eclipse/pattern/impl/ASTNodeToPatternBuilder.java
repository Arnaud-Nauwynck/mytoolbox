package fr.an.eclipse.pattern.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InfixExpression.Operator;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MemberRef;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.MethodRef;
import org.eclipse.jdt.core.dom.MethodRefParameter;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.UnionType;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.core.dom.WildcardType;

import fr.an.eclipse.pattern.ast.IPattern;
import fr.an.eclipse.pattern.ast.impl.StdListPatterns.DefaultASTListPattern;
import fr.an.eclipse.pattern.ast.impl.StdBasePatterns.DefaultBooleanPattern;
import fr.an.eclipse.pattern.ast.impl.StdBasePatterns.DefaultIntegerPattern;
import fr.an.eclipse.pattern.ast.impl.StdBasePatterns.RegexpStringPattern;
import fr.an.eclipse.pattern.ast.impl.StdCommentPatterns.BlockCommentPattern;
import fr.an.eclipse.pattern.ast.impl.StdCommentPatterns.CommentPattern;
import fr.an.eclipse.pattern.ast.impl.StdCommentPatterns.JavadocPattern;
import fr.an.eclipse.pattern.ast.impl.StdCommentPatterns.LineCommentPattern;
import fr.an.eclipse.pattern.ast.impl.StdCommentPatterns.MemberRefPattern;
import fr.an.eclipse.pattern.ast.impl.StdCommentPatterns.MethodRefParameterPattern;
import fr.an.eclipse.pattern.ast.impl.StdCommentPatterns.MethodRefPattern;
import fr.an.eclipse.pattern.ast.impl.StdCommentPatterns.TagElementPattern;
import fr.an.eclipse.pattern.ast.impl.StdCommentPatterns.TextElementPattern;
import fr.an.eclipse.pattern.ast.impl.StdDeclarationASTPatterns.AbstractTypeDeclarationPattern;
import fr.an.eclipse.pattern.ast.impl.StdDeclarationASTPatterns.AnnotationTypeDeclarationPattern;
import fr.an.eclipse.pattern.ast.impl.StdDeclarationASTPatterns.AnnotationTypeMemberDeclarationPattern;
import fr.an.eclipse.pattern.ast.impl.StdDeclarationASTPatterns.AnonymousClassDeclarationPattern;
import fr.an.eclipse.pattern.ast.impl.StdDeclarationASTPatterns.BodyDeclarationPattern;
import fr.an.eclipse.pattern.ast.impl.StdDeclarationASTPatterns.CompilationUnitPattern;
import fr.an.eclipse.pattern.ast.impl.StdDeclarationASTPatterns.EnumConstantDeclarationPattern;
import fr.an.eclipse.pattern.ast.impl.StdDeclarationASTPatterns.EnumDeclarationPattern;
import fr.an.eclipse.pattern.ast.impl.StdDeclarationASTPatterns.FieldDeclarationPattern;
import fr.an.eclipse.pattern.ast.impl.StdDeclarationASTPatterns.ImportDeclarationPattern;
import fr.an.eclipse.pattern.ast.impl.StdDeclarationASTPatterns.InitializerPattern;
import fr.an.eclipse.pattern.ast.impl.StdDeclarationASTPatterns.MarkerAnnotationPattern;
import fr.an.eclipse.pattern.ast.impl.StdDeclarationASTPatterns.MethodDeclarationPattern;
import fr.an.eclipse.pattern.ast.impl.StdDeclarationASTPatterns.ModifierFlagsPattern;
import fr.an.eclipse.pattern.ast.impl.StdDeclarationASTPatterns.ModifierPattern;
import fr.an.eclipse.pattern.ast.impl.StdDeclarationASTPatterns.PackageDeclarationPattern;
import fr.an.eclipse.pattern.ast.impl.StdDeclarationASTPatterns.SingleVariableDeclarationPattern;
import fr.an.eclipse.pattern.ast.impl.StdDeclarationASTPatterns.TypeDeclarationPattern;
import fr.an.eclipse.pattern.ast.impl.StdDeclarationASTPatterns.VariableDeclarationFragmentPattern;
import fr.an.eclipse.pattern.ast.impl.StdExpressionASTPatterns.AnnotationPattern;
import fr.an.eclipse.pattern.ast.impl.StdExpressionASTPatterns.ArrayAccessPattern;
import fr.an.eclipse.pattern.ast.impl.StdExpressionASTPatterns.ArrayCreationPattern;
import fr.an.eclipse.pattern.ast.impl.StdExpressionASTPatterns.ArrayInitializerPattern;
import fr.an.eclipse.pattern.ast.impl.StdExpressionASTPatterns.AssignmentOperatorPattern;
import fr.an.eclipse.pattern.ast.impl.StdExpressionASTPatterns.AssignmentPattern;
import fr.an.eclipse.pattern.ast.impl.StdExpressionASTPatterns.BooleanLiteralPattern;
import fr.an.eclipse.pattern.ast.impl.StdExpressionASTPatterns.CastExpressionPattern;
import fr.an.eclipse.pattern.ast.impl.StdExpressionASTPatterns.CharacterLiteralPattern;
import fr.an.eclipse.pattern.ast.impl.StdExpressionASTPatterns.ClassInstanceCreationPattern;
import fr.an.eclipse.pattern.ast.impl.StdExpressionASTPatterns.ConditionalExpressionPattern;
import fr.an.eclipse.pattern.ast.impl.StdExpressionASTPatterns.FieldAccessPattern;
import fr.an.eclipse.pattern.ast.impl.StdExpressionASTPatterns.InfixExpressionOperatorPattern;
import fr.an.eclipse.pattern.ast.impl.StdExpressionASTPatterns.InfixExpressionPattern;
import fr.an.eclipse.pattern.ast.impl.StdExpressionASTPatterns.InstanceofExpressionPattern;
import fr.an.eclipse.pattern.ast.impl.StdExpressionASTPatterns.MemberValuePairPattern;
import fr.an.eclipse.pattern.ast.impl.StdExpressionASTPatterns.MethodInvocationPattern;
import fr.an.eclipse.pattern.ast.impl.StdExpressionASTPatterns.NormalAnnotationPattern;
import fr.an.eclipse.pattern.ast.impl.StdExpressionASTPatterns.NullLiteralPattern;
import fr.an.eclipse.pattern.ast.impl.StdExpressionASTPatterns.NumberLiteralPattern;
import fr.an.eclipse.pattern.ast.impl.StdExpressionASTPatterns.ParenthesizedExpressionPattern;
import fr.an.eclipse.pattern.ast.impl.StdExpressionASTPatterns.PostfixExpressionOperatorPattern;
import fr.an.eclipse.pattern.ast.impl.StdExpressionASTPatterns.PostfixExpressionPattern;
import fr.an.eclipse.pattern.ast.impl.StdExpressionASTPatterns.PrefixExpressionOperatorPattern;
import fr.an.eclipse.pattern.ast.impl.StdExpressionASTPatterns.PrefixExpressionPattern;
import fr.an.eclipse.pattern.ast.impl.StdExpressionASTPatterns.QualifiedNamePattern;
import fr.an.eclipse.pattern.ast.impl.StdExpressionASTPatterns.SimpleNamePattern;
import fr.an.eclipse.pattern.ast.impl.StdExpressionASTPatterns.SingleMemberAnnotationPattern;
import fr.an.eclipse.pattern.ast.impl.StdExpressionASTPatterns.StringLiteralPattern;
import fr.an.eclipse.pattern.ast.impl.StdExpressionASTPatterns.SuperFieldAccessPattern;
import fr.an.eclipse.pattern.ast.impl.StdExpressionASTPatterns.SuperMethodInvocationPattern;
import fr.an.eclipse.pattern.ast.impl.StdExpressionASTPatterns.ThisExpressionPattern;
import fr.an.eclipse.pattern.ast.impl.StdExpressionASTPatterns.TypeLiteralPattern;
import fr.an.eclipse.pattern.ast.impl.StdExpressionASTPatterns.VariableDeclarationExpressionPattern;
import fr.an.eclipse.pattern.ast.impl.StdStatementASTPatterns.AssertStatementPattern;
import fr.an.eclipse.pattern.ast.impl.StdStatementASTPatterns.BlockPattern;
import fr.an.eclipse.pattern.ast.impl.StdStatementASTPatterns.BreakStatementPattern;
import fr.an.eclipse.pattern.ast.impl.StdStatementASTPatterns.CatchClausePattern;
import fr.an.eclipse.pattern.ast.impl.StdStatementASTPatterns.ConstructorInvocationPattern;
import fr.an.eclipse.pattern.ast.impl.StdStatementASTPatterns.ContinueStatementPattern;
import fr.an.eclipse.pattern.ast.impl.StdStatementASTPatterns.DoStatementPattern;
import fr.an.eclipse.pattern.ast.impl.StdStatementASTPatterns.EmptyStatementPattern;
import fr.an.eclipse.pattern.ast.impl.StdStatementASTPatterns.EnhancedForStatementPattern;
import fr.an.eclipse.pattern.ast.impl.StdStatementASTPatterns.ExpressionStatementPattern;
import fr.an.eclipse.pattern.ast.impl.StdStatementASTPatterns.ForStatementPattern;
import fr.an.eclipse.pattern.ast.impl.StdStatementASTPatterns.IfStatementPattern;
import fr.an.eclipse.pattern.ast.impl.StdStatementASTPatterns.LabeledStatementPattern;
import fr.an.eclipse.pattern.ast.impl.StdStatementASTPatterns.ReturnStatementPattern;
import fr.an.eclipse.pattern.ast.impl.StdStatementASTPatterns.SuperConstructorInvocationPattern;
import fr.an.eclipse.pattern.ast.impl.StdStatementASTPatterns.SwitchCasePattern;
import fr.an.eclipse.pattern.ast.impl.StdStatementASTPatterns.SwitchStatementPattern;
import fr.an.eclipse.pattern.ast.impl.StdStatementASTPatterns.SynchronizedStatementPattern;
import fr.an.eclipse.pattern.ast.impl.StdStatementASTPatterns.ThrowStatementPattern;
import fr.an.eclipse.pattern.ast.impl.StdStatementASTPatterns.TryStatementPattern;
import fr.an.eclipse.pattern.ast.impl.StdStatementASTPatterns.TypeDeclarationStatementPattern;
import fr.an.eclipse.pattern.ast.impl.StdStatementASTPatterns.VariableDeclarationStatementPattern;
import fr.an.eclipse.pattern.ast.impl.StdStatementASTPatterns.WhileStatementPattern;
import fr.an.eclipse.pattern.ast.impl.StdTypesASTPatterns.ArrayTypePattern;
import fr.an.eclipse.pattern.ast.impl.StdTypesASTPatterns.ParameterizedTypePattern;
import fr.an.eclipse.pattern.ast.impl.StdTypesASTPatterns.PrimitiveTypePattern;
import fr.an.eclipse.pattern.ast.impl.StdTypesASTPatterns.QualifiedTypePattern;
import fr.an.eclipse.pattern.ast.impl.StdTypesASTPatterns.SimpleTypePattern;
import fr.an.eclipse.pattern.ast.impl.StdTypesASTPatterns.TypeParameterPattern;
import fr.an.eclipse.pattern.ast.impl.StdTypesASTPatterns.UnionTypePattern;
import fr.an.eclipse.pattern.ast.impl.StdTypesASTPatterns.WildcardTypePattern;


@SuppressWarnings("unchecked")
public class ASTNodeToPatternBuilder extends ASTVisitor {


	private IPattern<?> resPattern;

	// ------------------------------------------------------------------------

	public ASTNodeToPatternBuilder() {
	}

	// ------------------------------------------------------------------------

	protected boolean pushRes(IPattern<?> p) {
		this.resPattern = p;
		return false;
	}

	public <T extends ASTNode> IPattern<T> toPattern(T node) {
		if (node == null) return null;
		resPattern = null;
		node.accept(this);
		return (IPattern<T>) resPattern;
	}

	public <T extends ASTNode> IPattern<List<T>> toPattern(List<T> nodes) {
		if (nodes == null) return null;
		List<IPattern<T>> ls = new ArrayList<IPattern<T>>(); 
		for(T node : nodes) {
			IPattern<T> nodePattern = (IPattern<T>) toPattern(node);
			ls.add(nodePattern);
		}
		return new DefaultASTListPattern<T>(ls);
	}

//	public <T extends ASTNode> IASTListPattern<T> toPattern(List<T> nodes, Class<P> clss) {
//		return (ASTListPattern<P>) toPattern(nodes);
//	}
	
	public IPattern<Name> toPattern(Name node) {
		if (node == null) return null;
		IPattern<? extends Name> res = null;
		if (node instanceof SimpleName) {
			res = toPattern((SimpleName) node);
		} else if (node instanceof QualifiedName) {
			res = toPattern((QualifiedName) node);
		} else {
			res = null; // should not occur
		}
		return (IPattern<Name>) res;
	}

	public IPattern<SimpleName> toPattern(SimpleName node) {
		if (node == null) return null;
		SimpleNamePattern res = new SimpleNamePattern();
		res.setIdentifier(toPattern(node.getIdentifier()));
		return res;
	}

	public IPattern<QualifiedName> toPattern(QualifiedName node) {
		if (node == null) return null;
		QualifiedNamePattern res = new QualifiedNamePattern();
		res.setQualifier(toPattern(node.getQualifier()));
		res.setLocalName(toPattern(node.getName()));
		return res;
	}


	public IPattern<Integer> toPatternModifiers(int modifiers) {
		ModifierFlagsPattern res = new ModifierFlagsPattern();
		res.setModifiers(modifiers);
		return res;
	}

	public IPattern<Integer> toPattern(int value) {
		DefaultIntegerPattern res = new DefaultIntegerPattern();
		res.setValue(value);
		return res;
	}

	public static IPattern<String> toPattern(String text) {
		RegexpStringPattern res = new RegexpStringPattern();
		res.setRegexp(text);
		return res;
	}

	public IPattern<Boolean> toPattern(boolean value) {
		DefaultBooleanPattern res = new DefaultBooleanPattern();
		res.setValue(value);
		return res;
	}

	// ------------------------------------------------------------------------
	
	protected void visitAnnotation2(Annotation node, AnnotationPattern<?> res) {
		res.setTypeName(toPattern(node.getTypeName()));
	}

	@Override
	public boolean visit(AnnotationTypeDeclaration node) {
		AnnotationTypeDeclarationPattern res = new AnnotationTypeDeclarationPattern();
		visitAbstractTypeDeclaration2(node, res);
		return pushRes(res);
	}

	protected void visitAbstractTypeDeclaration2(AbstractTypeDeclaration node, AbstractTypeDeclarationPattern<?> res) {
		visitBodyDeclaration2(node, res);
		res.setTypeName(toPattern(node.getName()));
		res.setBodyDeclarations(toPattern(node.bodyDeclarations()));
	}

	protected void visitBodyDeclaration2(BodyDeclaration node, BodyDeclarationPattern<?> res) {
		res.setOptionalDocComment(toPattern(node.getJavadoc()));
		res.setModifierFlags(toPatternModifiers(node.getModifiers()));
		res.setModifiers(toPattern(node.modifiers()));
	}

	@Override
	public boolean visit(AnnotationTypeMemberDeclaration node) {
		AnnotationTypeMemberDeclarationPattern res = new AnnotationTypeMemberDeclarationPattern();
		visitBodyDeclaration2(node, res);
		return pushRes(res);
	}

	@Override
	public boolean visit(AnonymousClassDeclaration node) {
		AnonymousClassDeclarationPattern res = new AnonymousClassDeclarationPattern();
		res.setBodyDeclarations(toPattern(node.bodyDeclarations()));
		return pushRes(res);
	}

	@Override
	public boolean visit(ArrayAccess node) {
		ArrayAccessPattern res = new ArrayAccessPattern();
		res.setArrayExpression(toPattern(node.getArray()));
		res.setIndexExpression(toPattern(node.getIndex()));
		return pushRes(res);
	}

	@Override
	public boolean visit(ArrayCreation node) {
		ArrayCreationPattern res = new ArrayCreationPattern();
		res.setArrayType(toPattern(node.getType()));
		res.setDimensions(toPattern(node.dimensions()));
		res.setOptionalInitializer(toPattern(node.getInitializer()));
		return pushRes(res);
	}

	@Override
	public boolean visit(ArrayInitializer node) {
		ArrayInitializerPattern res = new ArrayInitializerPattern();
		res.setExpressions(toPattern(node.expressions()));
		return pushRes(res);
	}

	@Override
	public boolean visit(ArrayType node) {
		ArrayTypePattern res = new ArrayTypePattern();
		res.setComponentType(toPattern(node.getComponentType()));
		return pushRes(res);
	}

	@Override
	public boolean visit(AssertStatement node) {
		AssertStatementPattern res = new AssertStatementPattern();
		res.setExpression(toPattern(node.getExpression()));
		res.setOptionalMessageExpression(toPattern(node.getMessage()));
		return pushRes(res);
	}

	@Override
	public boolean visit(Assignment node) {
		AssignmentPattern res = new AssignmentPattern();
		res.setLeftHandSide(toPattern(node.getLeftHandSide()));
		res.setAssignmentOperator(toPattern(node.getOperator()));
		res.setRightHandSide(toPattern(node.getRightHandSide()));
		return pushRes(res);
	}

	private AssignmentOperatorPattern toPattern(Assignment.Operator operator) {
		AssignmentOperatorPattern res = new AssignmentOperatorPattern();
		res.setOp(toPattern(operator.toString()));
		return res;
	}

	@Override
	public boolean visit(Block node) {
		BlockPattern res = new BlockPattern();
		res.setStatements(toPattern(node.statements()));
		return pushRes(res);
	}

	@Override
	public boolean visit(BlockComment node) {
		BlockCommentPattern res = new BlockCommentPattern();
		visitComment2(node, res);
		return pushRes(res);
	}

	private void visitComment2(Comment node, CommentPattern<?> res) {
		res.setAlternateRoot(toPattern(node.getAlternateRoot()));
	}

	@Override
	public boolean visit(BooleanLiteral node) {
		BooleanLiteralPattern res = new BooleanLiteralPattern();
		res.setValue(toPattern(node.booleanValue()));
		return pushRes(res);
	}

	@Override
	public boolean visit(BreakStatement node) {
		BreakStatementPattern res = new BreakStatementPattern();
		res.setLabel(toPattern(node.getLabel()));
		return pushRes(res);
	}

	@Override
	public boolean visit(CastExpression node) {
		CastExpressionPattern res = new CastExpressionPattern();
		res.setType(toPattern(node.getType()));
		res.setExpression(toPattern(node.getExpression()));
		return pushRes(res);
	}

	@Override
	public boolean visit(CatchClause node) {
		CatchClausePattern res = new CatchClausePattern();
		res.setBody(toPattern(node.getBody()));
		res.setExceptionDecl(toPattern(node.getException()));
		return pushRes(res);
	}

	@Override
	public boolean visit(CharacterLiteral node) {
		CharacterLiteralPattern res = new CharacterLiteralPattern();
		res.setValue(node.charValue());
		return pushRes(res);
	}

	@Override
	public boolean visit(ClassInstanceCreation node) {
		ClassInstanceCreationPattern res = new ClassInstanceCreationPattern();
		res.setArguments(toPattern(node.arguments()));
		res.setOptionalExpression(toPattern(node.getExpression()));
		res.setType(toPattern(node.getType()));
		res.setTypeArguments(toPattern(node.typeArguments()));
		return pushRes(res);
	}

	@Override
	public boolean visit(CompilationUnit node) {
		CompilationUnitPattern res = new CompilationUnitPattern();
		res.setOptionalPackageDeclaration(toPattern(node.getPackage()));
		// res.setImports();
		res.setTypes(toPattern((List<AbstractTypeDeclaration>)node.types()));
		return pushRes(res);
	}

	@Override
	public boolean visit(ConditionalExpression node) {
		ConditionalExpressionPattern res = new ConditionalExpressionPattern();
		res.setConditionExpression(toPattern(node.getExpression()));
		res.setThenExpression(toPattern(node.getThenExpression()));
		res.setElseExpression(toPattern(node.getElseExpression()));
		return pushRes(res);
	}

	@Override
	public boolean visit(ConstructorInvocation node) {
		ConstructorInvocationPattern res = new ConstructorInvocationPattern();
		res.setArguments(toPattern(node.arguments()));
		res.setTypeArguments(toPattern(node.typeArguments()));
		return pushRes(res);
	}

	@Override
	public boolean visit(ContinueStatement node) {
		ContinueStatementPattern res = new ContinueStatementPattern();
		res.setOptionalLabel(toPattern(node.getLabel()));
		return pushRes(res);
	}

	@Override
	public boolean visit(DoStatement node) {
		DoStatementPattern res = new DoStatementPattern();
		res.setExpression(toPattern(node.getExpression()));
		res.setBody(toPattern(node.getBody()));
		return pushRes(res);
	}

	@Override
	public boolean visit(EmptyStatement node) {
		EmptyStatementPattern res = new EmptyStatementPattern();
		return pushRes(res);
	}

	@Override
	public boolean visit(EnhancedForStatement node) {
		EnhancedForStatementPattern res = new EnhancedForStatementPattern();
		res.setParameter(toPattern(node.getParameter()));
		res.setExpression(toPattern(node.getExpression()));
		res.setBody(toPattern(node.getBody()));
		return pushRes(res);
	}

	@Override
	public boolean visit(EnumConstantDeclaration node) {
		EnumConstantDeclarationPattern res = new EnumConstantDeclarationPattern();
		res.setArguments(toPattern(node.arguments()));
		res.setConstantName(toPattern(node.getName()));
		res.setModifierFlags(toPatternModifiers(node.getModifiers()));
		res.setModifiers(toPattern(node.modifiers()));
		res.setOptionalAnonymousClassDeclaration(toPattern(node.getAnonymousClassDeclaration()));
		res.setOptionalDocComment(toPattern(node.getJavadoc()));
		return pushRes(res);
	}

	@Override
	public boolean visit(EnumDeclaration node) {
		EnumDeclarationPattern res = new EnumDeclarationPattern();
		res.setBodyDeclarations(toPattern(node.bodyDeclarations()));
		res.setEnumConstants(toPattern(node.enumConstants()));
		res.setModifierFlags(toPatternModifiers(node.getModifiers()));
		res.setModifiers(toPattern(node.modifiers()));
		res.setOptionalDocComment(toPattern(node.getJavadoc()));
		res.setSuperInterfaceTypes(toPattern(node.superInterfaceTypes()));
		res.setTypeName(toPattern(node.getName()));
		return pushRes(res);
	}

	@Override
	public boolean visit(ExpressionStatement node) {
		ExpressionStatementPattern res = new ExpressionStatementPattern();
		res.setExpression(toPattern(node.getExpression()));
		return pushRes(res);
	}

	@Override
	public boolean visit(FieldAccess node) {
		FieldAccessPattern res = new FieldAccessPattern();
		res.setExpression(toPattern(node.getExpression()));
		res.setFieldName(toPattern(node.getName()));
		return pushRes(res);
	}

	@Override
	public boolean visit(FieldDeclaration node) {
		FieldDeclarationPattern res = new FieldDeclarationPattern();
		res.setBaseType(toPattern(node.getType()));
		res.setModifierFlags(toPatternModifiers(node.getModifiers()));
		res.setModifiers(toPattern(node.modifiers()));
		res.setOptionalDocComment(toPattern(node.getJavadoc()));
		res.setVariableDeclarationFragments(toPattern(node.fragments()));
		return pushRes(res);
	}

	@Override
	public boolean visit(ForStatement node) {
		ForStatementPattern res = new ForStatementPattern();
		res.setBody(toPattern(node.getBody()));
		res.setInitializers(toPattern(node.initializers()));
		res.setOptionalConditionExpression(toPattern(node.getExpression()));
		res.setUpdaters(toPattern(node.updaters()));
		return pushRes(res);
	}

	@Override
	public boolean visit(IfStatement node) {
		IfStatementPattern res = new IfStatementPattern();
		res.setExpression(toPattern(node.getExpression()));
		res.setThenStatement(toPattern(node.getThenStatement()));
		res.setOptionalElseStatement(toPattern(node.getElseStatement()));
		return pushRes(res);
	}

	@Override
	public boolean visit(ImportDeclaration node) {
		ImportDeclarationPattern res = new ImportDeclarationPattern();
		res.setImportName(toPattern(node.getName()));
		res.setIsStatic(toPattern(node.isStatic()));
		res.setOnDemand(toPattern(node.isOnDemand()));
		return pushRes(res);
	}

	@Override
	public boolean visit(InfixExpression node) {
		InfixExpressionPattern res = new InfixExpressionPattern();
		res.setExtendedOperands(toPattern(node.extendedOperands()));
		res.setLeftOperand(toPattern(node.getLeftOperand()));
		res.setOperator(toPattern(node.getOperator()));
		res.setRightOperand(toPattern(node.getRightOperand()));
		return pushRes(res);
	}

	private InfixExpressionOperatorPattern toPattern(Operator operator) {
		InfixExpressionOperatorPattern res = new InfixExpressionOperatorPattern();
		res.setOp(toPattern(operator.toString()));
		return res;
	}

	@Override
	public boolean visit(InstanceofExpression node) {
		InstanceofExpressionPattern res = new InstanceofExpressionPattern();
		res.setLeftOperand(toPattern(node.getLeftOperand()));
		res.setRightOperand(toPattern(node.getRightOperand()));
		return pushRes(res);
	}

	@Override
	public boolean visit(Initializer node) {
		InitializerPattern res = new InitializerPattern();
		res.setBody(toPattern(node.getBody()));
		visitBodyDeclaration2(node, res);
		return pushRes(res);
	}

	@Override
	public boolean visit(Javadoc node) {
		JavadocPattern res = new JavadocPattern();
		visitComment2(node, res);
		res.setTags(toPattern(node.tags()));
		return pushRes(res);
	}

	@Override
	public boolean visit(LabeledStatement node) {
		LabeledStatementPattern res = new LabeledStatementPattern();
		res.setLabelName(toPattern(node.getLabel()));
		res.setBody(toPattern(node.getBody()));
		return pushRes(res);
	}

	@Override
	public boolean visit(LineComment node) {
		LineCommentPattern res = new LineCommentPattern();
		visitComment2(node, res);
		return pushRes(res);
	}

	@Override
	public boolean visit(MarkerAnnotation node) {
		MarkerAnnotationPattern res = new MarkerAnnotationPattern();
		visitAnnotation2(node, res);
		return pushRes(res);
	}

	@Override
	public boolean visit(MemberRef node) {
		MemberRefPattern res = new MemberRefPattern();
		res.setMemberName(toPattern(node.getName()));
		res.setOptionalQualifier(toPattern(node.getQualifier()));
		return pushRes(res);
	}

	@Override
	public boolean visit(MemberValuePair node) {
		MemberValuePairPattern res = new MemberValuePairPattern();
		res.setName(toPattern(node.getName()));
		res.setValue(toPattern(node.getValue()));
		return pushRes(res);
	}

	@Override
	public boolean visit(MethodRef node) {
		MethodRefPattern res = new MethodRefPattern();
		res.setMethodName(toPattern(node.getName()));
		res.setParameters(toPattern(node.parameters()));
		res.setOptionalQualifier(toPattern(node.getQualifier()));
		return pushRes(res);
	}

	@Override
	public boolean visit(MethodRefParameter node) {
		MethodRefParameterPattern res = new MethodRefParameterPattern();
		res.setOptionalParameterName(toPattern(node.getName()));
		res.setType(toPattern(node.getType()));
		res.setVariableArity(toPattern(node.isVarargs()));
		return pushRes(res);
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		MethodDeclarationPattern res = new MethodDeclarationPattern();
		visitBodyDeclaration2(node, res);
		res.setIsConstructor(toPattern(node.isConstructor()));
		res.setMethodName(toPattern(node.getName()));
		res.setParameters(toPattern(node.parameters()));
		res.setReturnType(toPattern(node.getReturnType2()));
		res.setTypeParameters(toPattern(node.typeParameters()));
		res.setExtraArrayDimensions(toPattern(node.getExtraDimensions()));
		res.setThrownExceptions(toPattern(node.thrownExceptions()));
		res.setBody(toPattern(node.getBody()));
		return pushRes(res);
	}

	@Override
	public boolean visit(MethodInvocation node) {
		MethodInvocationPattern res = new MethodInvocationPattern();
		res.setOptionalExpression(toPattern(node.getExpression()));
		res.setMethodName(toPattern(node.getName()));
		res.setArguments(toPattern(node.arguments()));
		res.setTypeArguments(toPattern(node.typeArguments()));
		return pushRes(res);
	}

	@Override
	public boolean visit(Modifier node) {
		ModifierPattern res = new ModifierPattern();
		res.setKeyword(toPattern(node.getKeyword().toString()));
		return pushRes(res);
	}

	@Override
	public boolean visit(NormalAnnotation node) {
		NormalAnnotationPattern res = new NormalAnnotationPattern();
		visitAnnotation2(node, res);
		res.setValues(toPattern(node.values()));
		return pushRes(res);
	}

	@Override
	public boolean visit(NullLiteral node) {
		NullLiteralPattern res = new NullLiteralPattern();
		return pushRes(res);
	}

	@Override
	public boolean visit(NumberLiteral node) {
		NumberLiteralPattern res = new NumberLiteralPattern();
		res.setTokenValue(toPattern(node.getToken()));
		return pushRes(res);
	}

	@Override
	public boolean visit(PackageDeclaration node) {
		PackageDeclarationPattern res = new PackageDeclarationPattern();
		res.setAnnotations(toPattern(node.annotations()));
		res.setOptionalDocComment(toPattern(node.getJavadoc()));
		res.setPackageName(toPattern(node.getName()));
		return pushRes(res);
	}

	@Override
	public boolean visit(ParameterizedType node) {
		ParameterizedTypePattern res = new ParameterizedTypePattern();
		res.setType(toPattern(node.getType()));
		res.setTypeArguments(toPattern(node.typeArguments()));
		return pushRes(res);
	}

	@Override
	public boolean visit(ParenthesizedExpression node) {
		ParenthesizedExpressionPattern res = new ParenthesizedExpressionPattern();
		res.setExpression(toPattern(node.getExpression()));
		return pushRes(res);
	}

	@Override
	public boolean visit(PostfixExpression node) {
		PostfixExpressionPattern res = new PostfixExpressionPattern();
		res.setOperand(toPattern(node.getOperand()));
		res.setOperator(toPattern(node.getOperator()));
		return pushRes(res);
	}

	private PostfixExpressionOperatorPattern toPattern(PostfixExpression.Operator node) {
		PostfixExpressionOperatorPattern res = new PostfixExpressionOperatorPattern();
		res.setOp(toPattern(node.toString()));
		return res;
	}

	@Override
	public boolean visit(PrefixExpression node) {
		PrefixExpressionPattern res = new PrefixExpressionPattern();
		res.setOperand(toPattern(node.getOperand()));
		res.setOperator(toPattern(node.getOperator()));
		return pushRes(res);
	}

	private PrefixExpressionOperatorPattern toPattern(PrefixExpression.Operator node) {
		PrefixExpressionOperatorPattern res = new PrefixExpressionOperatorPattern();
		res.setOp(toPattern(node.toString()));
		return res;
	}
	
	@Override
	public boolean visit(PrimitiveType node) {
		PrimitiveTypePattern res = new PrimitiveTypePattern();
		res.setCode(toPattern(node.getPrimitiveTypeCode().toString()));
		return pushRes(res);
	}

	@Override
	public boolean visit(QualifiedName node) {
		QualifiedNamePattern res = new QualifiedNamePattern();
		res.setQualifier(toPattern(node.getQualifier()));
		res.setLocalName(toPattern(node.getName()));
		return pushRes(res);
	}

	@Override
	public boolean visit(QualifiedType node) {
		QualifiedTypePattern res = new QualifiedTypePattern();
		res.setQualifier(toPattern(node.getQualifier()));
		res.setName(toPattern(node.getName()));
		return pushRes(res);
	}

	@Override
	public boolean visit(ReturnStatement node) {
		ReturnStatementPattern res = new ReturnStatementPattern();
		res.setExpression(toPattern(node.getExpression()));
		return pushRes(res);
	}

	@Override
	public boolean visit(SimpleName node) {
		SimpleNamePattern res = new SimpleNamePattern();
		res.setIdentifier(toPattern(node.getIdentifier()));
		return pushRes(res);
	}

	@Override
	public boolean visit(SimpleType node) {
		SimpleTypePattern res = new SimpleTypePattern();
		res.setTypeName(toPattern(node.getName()));
		return pushRes(res);
	}

	@Override
	public boolean visit(SingleMemberAnnotation node) {
		SingleMemberAnnotationPattern res = new SingleMemberAnnotationPattern();
		visitAnnotation2(node, res);
		res.setValue(toPattern(node.getValue()));
		return pushRes(res);
	}

	@Override
	public boolean visit(SingleVariableDeclaration node) {
		SingleVariableDeclarationPattern res = new SingleVariableDeclarationPattern();
		/*TODO*/
		pushRes(res);
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(StringLiteral node) {
		StringLiteralPattern res = new StringLiteralPattern();
		res.setValue(toPattern(node.getEscapedValue()));
		return pushRes(res);
	}

	@Override
	public boolean visit(SuperConstructorInvocation node) {
		SuperConstructorInvocationPattern res = new SuperConstructorInvocationPattern();
		res.setArguments(toPattern(node.arguments()));
		res.setTypeArguments(toPattern(node.typeArguments()));
		res.setExpression(toPattern(node.getExpression()));
		return pushRes(res);
	}

	@Override
	public boolean visit(SuperFieldAccess node) {
		SuperFieldAccessPattern res = new SuperFieldAccessPattern();
		res.setOptionalQualifier(toPattern(node.getQualifier()));
		res.setFieldName(toPattern(node.getName()));
		return pushRes(res);
	}

	@Override
	public boolean visit(SuperMethodInvocation node) {
		SuperMethodInvocationPattern res = new SuperMethodInvocationPattern();
		res.setArguments(toPattern(node.arguments()));
		res.setMethodName(toPattern(node.getName()));
		res.setOptionalQualifier(toPattern(node.getQualifier()));
		res.setTypeArguments(toPattern(node.typeArguments()));
		return pushRes(res);
	}

	@Override
	public boolean visit(SwitchCase node) {
		SwitchCasePattern res = new SwitchCasePattern();
		res.setOptionalExpression(toPattern(node.getExpression()));
		return pushRes(res);
	}

	@Override
	public boolean visit(SwitchStatement node) {
		SwitchStatementPattern res = new SwitchStatementPattern();
		res.setExpression(toPattern(node.getExpression()));
		res.setStatements(toPattern(node.statements()));
		return pushRes(res);
	}

	@Override
	public boolean visit(SynchronizedStatement node) {
		SynchronizedStatementPattern res = new SynchronizedStatementPattern();
		res.setExpression(toPattern(node.getExpression()));
		res.setBody(toPattern(node.getBody()));
		return pushRes(res);
	}

	@Override
	public boolean visit(TagElement node) {
		TagElementPattern res = new TagElementPattern();
		res.setFragments(toPattern(node.fragments()));
		res.setOptionalTagName(toPattern(node.getTagName()));
		return pushRes(res);
	}

	@Override
	public boolean visit(TextElement node) {
		TextElementPattern res = new TextElementPattern();
		res.setText(toPattern(node.getText()));
		return pushRes(res);
	}

	@Override
	public boolean visit(ThisExpression node) {
		ThisExpressionPattern res = new ThisExpressionPattern();
		res.setOptionalQualifier(toPattern(node.getQualifier()));
		return pushRes(res);
	}

	@Override
	public boolean visit(ThrowStatement node) {
		ThrowStatementPattern res = new ThrowStatementPattern();
		res.setExpression(toPattern(node.getExpression()));
		return pushRes(res);
	}

	@Override
	public boolean visit(TryStatement node) {
		TryStatementPattern res = new TryStatementPattern();
		res.setBody(toPattern(node.getBody()));
		res.setCatchClauses(toPattern(node.catchClauses()));
		res.setOptionalFinallyBody(toPattern(node.getFinally()));
		res.setResources(toPattern(node.resources()));
		return pushRes(res);
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		TypeDeclarationPattern res = new TypeDeclarationPattern();
		visitAbstractTypeDeclaration2(node, res);
		res.setIsInterface(toPattern(node.isInterface()));
		res.setTypeParameters(toPattern(node.typeParameters()));
		res.setOptionalSuperclassType(toPattern(node.getSuperclassType()));
		res.setSuperInterfaceTypes(toPattern(node.superInterfaceTypes()));
		return pushRes(res);
	}

	@Override
	public boolean visit(TypeDeclarationStatement node) {
		TypeDeclarationStatementPattern res = new TypeDeclarationStatementPattern();
		res.setTypeDecl(toPattern(node.getDeclaration()));
		return pushRes(res);
	}

	@Override
	public boolean visit(TypeLiteral node) {
		TypeLiteralPattern res = new TypeLiteralPattern();
		res.setType(toPattern(node.getType()));
		return pushRes(res);
	}

	@Override
	public boolean visit(TypeParameter node) {
		TypeParameterPattern res = new TypeParameterPattern();
		res.setTypeBounds(toPattern(node.typeBounds()));
		res.setTypeVariableName(toPattern(node.getName()));
		return pushRes(res);
	}

	@Override
	public boolean visit(UnionType node) {
		UnionTypePattern res = new UnionTypePattern();
		res.setTypes(toPattern(node.types()));
		return pushRes(res);
	}

	@Override
	public boolean visit(VariableDeclarationExpression node) {
		VariableDeclarationExpressionPattern res = new VariableDeclarationExpressionPattern();
		res.setModifiers(toPattern(node.modifiers()));
		res.setModifierFlags(toPatternModifiers(node.getModifiers()));
		res.setBaseType(toPattern(node.getType()));
		res.setVariableDeclarationFragments(toPattern(node.fragments()));
		return pushRes(res);
	}

	@Override
	public boolean visit(VariableDeclarationStatement node) {
		VariableDeclarationStatementPattern res = new VariableDeclarationStatementPattern();
		res.setModifiers(toPattern(node.modifiers()));
		res.setModifierFlags(toPatternModifiers(node.getModifiers()));
		res.setBaseType(toPattern(node.getType()));
		res.setVariableDeclarationFragments(toPattern(node.fragments()));
		return pushRes(res);
	}

	@Override
	public boolean visit(VariableDeclarationFragment node) {
		VariableDeclarationFragmentPattern res = new VariableDeclarationFragmentPattern();
		res.setVariableName(toPattern(node.getName()));
		res.setExtraArrayDimensions(toPattern(node.getExtraDimensions()));
		res.setOptionalInitializer(toPattern(node.getInitializer()));
		return pushRes(res);
	}

	@Override
	public boolean visit(WhileStatement node) {
		WhileStatementPattern res = new WhileStatementPattern();
		res.setExpression(toPattern(node.getExpression()));
		res.setBody(toPattern(node.getBody()));
		return pushRes(res);
	}

	@Override
	public boolean visit(WildcardType node) {
		WildcardTypePattern res = new WildcardTypePattern();
		res.setIsUpperBound(toPattern(node.isUpperBound()));
		res.setOptionalBound(toPattern(node.getBound()));
		return pushRes(res);
	}
	
}
