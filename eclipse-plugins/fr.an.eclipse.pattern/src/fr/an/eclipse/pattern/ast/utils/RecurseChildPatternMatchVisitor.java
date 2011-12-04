package fr.an.eclipse.pattern.ast.utils;

import java.util.List;

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
import org.eclipse.jdt.core.dom.Assignment.Operator;
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
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
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
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.UnionType;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.core.dom.WildcardType;

import fr.an.eclipse.pattern.ast.IPattern;
import fr.an.eclipse.pattern.ast.PatternMatchVisitor;
import fr.an.eclipse.pattern.ast.impl.StdAlgebraPatterns.GroupCapturePattern;
import fr.an.eclipse.pattern.ast.impl.StdAlgebraPatterns.MatchAllOfPattern;
import fr.an.eclipse.pattern.ast.impl.StdAlgebraPatterns.MatchAndPattern;
import fr.an.eclipse.pattern.ast.impl.StdAlgebraPatterns.MatchNotPattern;
import fr.an.eclipse.pattern.ast.impl.StdAlgebraPatterns.MatchOneOfPattern;
import fr.an.eclipse.pattern.ast.impl.StdAlgebraPatterns.MatchOrPattern;
import fr.an.eclipse.pattern.ast.impl.StdAlgebraPatterns.WildcardContainingPattern;
import fr.an.eclipse.pattern.ast.impl.StdAlgebraPatterns.WildcardPattern;
import fr.an.eclipse.pattern.ast.impl.StdBasePatterns.DefaultBooleanPattern;
import fr.an.eclipse.pattern.ast.impl.StdBasePatterns.DefaultIntegerPattern;
import fr.an.eclipse.pattern.ast.impl.StdBasePatterns.DefaultStringPattern;
import fr.an.eclipse.pattern.ast.impl.StdBasePatterns.RegexpStringPattern;
import fr.an.eclipse.pattern.ast.impl.StdCommentPatterns.BlockCommentPattern;
import fr.an.eclipse.pattern.ast.impl.StdCommentPatterns.CommentPattern;
import fr.an.eclipse.pattern.ast.impl.StdCommentPatterns.DocElementPattern;
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
import fr.an.eclipse.pattern.ast.impl.StdDeclarationASTPatterns.ExtendedModifierPattern;
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
import fr.an.eclipse.pattern.ast.impl.StdDeclarationASTPatterns.VariableDeclarationPattern;
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
import fr.an.eclipse.pattern.ast.impl.StdListPatterns.DefaultASTListPattern;
import fr.an.eclipse.pattern.ast.impl.StdListPatterns.MatchAllEltsOfListPattern;
import fr.an.eclipse.pattern.ast.impl.StdListPatterns.MatchOneEltOfListPattern;
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
import fr.an.eclipse.pattern.ast.impl.StdTypesASTPatterns.TypePattern;
import fr.an.eclipse.pattern.ast.impl.StdTypesASTPatterns.UnionTypePattern;
import fr.an.eclipse.pattern.ast.impl.StdTypesASTPatterns.WildcardTypePattern;

/**
 * default PatternMatchVisitor implementation for traversing IPattern tree using parent->child relationships
 *
 */
public class RecurseChildPatternMatchVisitor extends PatternMatchVisitor {

	
	// ------------------------------------------------------------------------
	
	@Override
	public <T> boolean preVisitMatch(IPattern<T> obj, T node) {
		return false;
	}

	@Override
	public <T> void postVisitMatch(IPattern<T> obj, boolean matchRes, T node) {
	}

	@Override
	public <T> boolean mismatch(String mismatchMessage) {
		return false;
	}
	
	// ------------------------------------------------------------------------

	@Override
	public <T> boolean visitMatch(GroupCapturePattern<T> p, Object node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public <T> boolean visitMatch(WildcardPattern<T> p, Object node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public <T> boolean visitMatch(MatchNotPattern<T> p, Object node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public <T> boolean visitMatch(MatchAndPattern<T> p, Object node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public <T> boolean visitMatch(MatchOrPattern<T> p, Object node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public <T> boolean visitMatch(MatchAllOfPattern<T> p, Object node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public <T> boolean visitMatch(MatchOneOfPattern<T> p, Object node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public <T> boolean visitMatch(WildcardContainingPattern<T> p, Object node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(DefaultIntegerPattern p, int node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(DefaultStringPattern p, String node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(RegexpStringPattern p, String node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(DefaultBooleanPattern p, boolean node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public <T> boolean visitMatch(DefaultASTListPattern<T> p, List<T> node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public <T> boolean visitMatch(MatchOneEltOfListPattern<T> p, List<T> node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public <T> boolean visitMatch(MatchAllEltsOfListPattern<T> p, List<T> node) {
		return p.recursevisitMatch(this, node);
	}

	// ------------------------------------------------------------------------
	
	@Override
	public boolean visitMatch(CompilationUnitPattern p, CompilationUnit node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(PackageDeclarationPattern p, PackageDeclaration node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public <T extends BodyDeclaration> boolean visitMatch(BodyDeclarationPattern<T> p, BodyDeclaration node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public <T extends AbstractTypeDeclaration> boolean visitMatch(AbstractTypeDeclarationPattern<T> p, AbstractTypeDeclaration node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(TypeDeclarationPattern p, TypeDeclaration node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(AnnotationTypeDeclarationPattern p, AnnotationTypeDeclaration node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(EnumDeclarationPattern p, EnumDeclaration node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(EnumConstantDeclarationPattern p, EnumConstantDeclaration node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(AnonymousClassDeclarationPattern p, AnonymousClassDeclaration node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(MethodDeclarationPattern p, MethodDeclaration node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(FieldDeclarationPattern p, FieldDeclaration node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public <T extends VariableDeclaration> boolean visitMatch(VariableDeclarationPattern<T> p, VariableDeclaration node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(SingleVariableDeclarationPattern p, SingleVariableDeclaration node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(VariableDeclarationFragmentPattern p, VariableDeclarationFragment node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(ModifierFlagsPattern p, int node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(ExtendedModifierPattern p, IExtendedModifier node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(AnnotationTypeMemberDeclarationPattern p, AnnotationTypeMemberDeclaration node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(InitializerPattern p, Initializer node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(ImportDeclarationPattern p, ImportDeclaration node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(MarkerAnnotationPattern p, MarkerAnnotation node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(ModifierPattern p, Modifier node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(SimpleNamePattern p, SimpleName node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(QualifiedNamePattern p, QualifiedName node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public <T extends Annotation> boolean visitMatch(AnnotationPattern<T> p, Annotation node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(NormalAnnotationPattern p, NormalAnnotation node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(MemberValuePairPattern p, MemberValuePair node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(SingleMemberAnnotationPattern p, SingleMemberAnnotation node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(ArrayAccessPattern p, ArrayAccess node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(ArrayCreationPattern p, ArrayCreation node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(ArrayInitializerPattern p, ArrayInitializer node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(AssignmentPattern p, Assignment node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(AssignmentOperatorPattern p, Operator node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(BooleanLiteralPattern p, BooleanLiteral node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(CastExpressionPattern p, CastExpression node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(CharacterLiteralPattern p, CharacterLiteral node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(ClassInstanceCreationPattern p, ClassInstanceCreation node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(ConditionalExpressionPattern p, ConditionalExpression node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(FieldAccessPattern p, FieldAccess node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(InfixExpressionPattern p, InfixExpression node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(InfixExpressionOperatorPattern p, org.eclipse.jdt.core.dom.InfixExpression.Operator node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(InstanceofExpressionPattern p, InstanceofExpression node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(MethodInvocationPattern p, MethodInvocation node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(NullLiteralPattern p, NullLiteral node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(NumberLiteralPattern p, NumberLiteral node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(ParenthesizedExpressionPattern p, ParenthesizedExpression node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(PostfixExpressionPattern p, PostfixExpression node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(PostfixExpressionOperatorPattern p,
			org.eclipse.jdt.core.dom.PostfixExpression.Operator node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(PrefixExpressionPattern p, PrefixExpression node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(PrefixExpressionOperatorPattern p, org.eclipse.jdt.core.dom.PrefixExpression.Operator node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(StringLiteralPattern p, StringLiteral node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(SuperFieldAccessPattern p, SuperFieldAccess node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(SuperMethodInvocationPattern p, SuperMethodInvocation node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(ThisExpressionPattern p, ThisExpression node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(VariableDeclarationExpressionPattern p, VariableDeclarationExpression node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(TypeLiteralPattern p, TypeLiteral node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(BlockPattern p, Block node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(AssertStatementPattern p, AssertStatement node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(BreakStatementPattern p, BreakStatement node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(CatchClausePattern p, CatchClause node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(ContinueStatementPattern p, ContinueStatement node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(ConstructorInvocationPattern p, ConstructorInvocation node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(DoStatementPattern p, DoStatement node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(EmptyStatementPattern p, EmptyStatement node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(EnhancedForStatementPattern p, EnhancedForStatement node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(ExpressionStatementPattern p, ExpressionStatement node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(ForStatementPattern p, ForStatement node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(IfStatementPattern p, IfStatement node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(LabeledStatementPattern p, LabeledStatement node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(ReturnStatementPattern p, ReturnStatement node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(SuperConstructorInvocationPattern p, SuperConstructorInvocation node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(SwitchCasePattern p, SwitchCase node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(SwitchStatementPattern p, SwitchStatement node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(SynchronizedStatementPattern p, SynchronizedStatement node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(ThrowStatementPattern p, ThrowStatement node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(TryStatementPattern p, TryStatement node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(TypeDeclarationStatementPattern p, TypeDeclarationStatement node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(VariableDeclarationStatementPattern p, VariableDeclarationStatement node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(WhileStatementPattern p, WhileStatement node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public <T extends Type> boolean visitMatch(TypePattern<T> p, Type node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(TypeParameterPattern p, TypeParameter node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(ArrayTypePattern p, ArrayType node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(ParameterizedTypePattern p, ParameterizedType node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(PrimitiveTypePattern p, PrimitiveType node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(QualifiedTypePattern p, QualifiedType node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(SimpleTypePattern p, SimpleType node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(UnionTypePattern p, UnionType node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(WildcardTypePattern p, WildcardType node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public <T extends Comment> boolean visitMatch(CommentPattern<T> p, Comment node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(BlockCommentPattern p, BlockComment node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(JavadocPattern p, Javadoc node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(LineCommentPattern p, LineComment node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public <T> boolean visitMatch(DocElementPattern<T> p, Object node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(TagElementPattern p, TagElement node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(TextElementPattern p, TextElement node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(MemberRefPattern p, MemberRef node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(MethodRefPattern p, MethodRef node) {
		return p.recursevisitMatch(this, node);
	}

	@Override
	public boolean visitMatch(MethodRefParameterPattern p, MethodRefParameter node) {
		return p.recursevisitMatch(this, node);
	}

}
