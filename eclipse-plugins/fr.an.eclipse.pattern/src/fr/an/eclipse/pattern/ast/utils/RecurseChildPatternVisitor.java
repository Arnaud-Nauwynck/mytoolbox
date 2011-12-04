package fr.an.eclipse.pattern.ast.utils;

import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclaration;

import fr.an.eclipse.pattern.ast.IPattern;
import fr.an.eclipse.pattern.ast.PatternVisitor;
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
 * default PatternVisitor implementation for traversing IPattern tree using parent->child relationships
 *
 */
public class RecurseChildPatternVisitor extends PatternVisitor {

	// generic pre-post visit part
	// ------------------------------------------------------------------------
	
	@Override
	public <T> boolean preVisit(IPattern<T> obj) {
		return true;
	}

	@Override
	public <T> void postVisit(IPattern<T> obj) {
	}

	
	// ------------------------------------------------------------------------
	
	@Override
	public <T> void visit(GroupCapturePattern<T> p) {
		p.recurseVisit(this);
	}

	@Override
	public <T> void visit(WildcardPattern<T> p) {
		// no recurse
	}

	@Override
	public <T> void visit(MatchNotPattern<T> p) {
		p.recurseVisit(this);
	}

	@Override
	public <T> void visit(MatchAndPattern<T> p) {
		p.recurseVisit(this);
	}

	@Override
	public <T> void visit(MatchOrPattern<T> p) {
		p.recurseVisit(this);
	}

	@Override
	public <T> void visit(MatchAllOfPattern<T> p) {
		p.recurseVisit(this);
	}

	@Override
	public <T> void visit(MatchOneOfPattern<T> p) {
		p.recurseVisit(this);
	}

	@Override
	public <T> void visit(WildcardContainingPattern<T> p) {
		p.recurseVisit(this);
	}
	
	@Override
	public void visit(DefaultIntegerPattern p) {
		// no recurse
	}

	@Override
	public void visit(DefaultStringPattern p) {
		// no recurse
	}

	@Override
	public void visit(RegexpStringPattern p) {
		// no recurse
	}

	@Override
	public void visit(DefaultBooleanPattern p) {
		// no recurse
	}

	@Override
	public <T> void visit(DefaultASTListPattern<T> p) {
		p.recurseVisit(this);
	}

	@Override
	public <T> void visit(MatchOneEltOfListPattern<T> p) {
		p.recurseVisit(this);
	}

	@Override
	public <T> void visit(MatchAllEltsOfListPattern<T> p) {
		p.recurseVisit(this);
	}

	// ------------------------------------------------------------------------
	
	@Override
	public void visit(CompilationUnitPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(PackageDeclarationPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public <T extends BodyDeclaration> void visit(BodyDeclarationPattern<T> p) {
		p.recurseVisit(this);
	}

	@Override
	public <T extends AbstractTypeDeclaration> void visit(AbstractTypeDeclarationPattern<T> p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(TypeDeclarationPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(AnnotationTypeDeclarationPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(EnumDeclarationPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(EnumConstantDeclarationPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(AnonymousClassDeclarationPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(MethodDeclarationPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(FieldDeclarationPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public <T extends VariableDeclaration> void visit(VariableDeclarationPattern<T> p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(SingleVariableDeclarationPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(VariableDeclarationFragmentPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(ModifierFlagsPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(ExtendedModifierPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(AnnotationTypeMemberDeclarationPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(InitializerPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(ImportDeclarationPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(MarkerAnnotationPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(ModifierPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(SimpleNamePattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(QualifiedNamePattern p) {
		p.recurseVisit(this);
	}

	@Override
	public <T extends Annotation> void visit(AnnotationPattern<T> p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(NormalAnnotationPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(MemberValuePairPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(SingleMemberAnnotationPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(ArrayAccessPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(ArrayCreationPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(ArrayInitializerPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(AssignmentPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(AssignmentOperatorPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(BooleanLiteralPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(CastExpressionPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(CharacterLiteralPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(ClassInstanceCreationPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(ConditionalExpressionPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(FieldAccessPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(InfixExpressionPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(InfixExpressionOperatorPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(InstanceofExpressionPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(MethodInvocationPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(NullLiteralPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(NumberLiteralPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(ParenthesizedExpressionPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(PostfixExpressionPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(PostfixExpressionOperatorPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(PrefixExpressionPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(PrefixExpressionOperatorPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(StringLiteralPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(SuperFieldAccessPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(SuperMethodInvocationPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(ThisExpressionPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(VariableDeclarationExpressionPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(TypeLiteralPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(BlockPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(AssertStatementPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(BreakStatementPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(CatchClausePattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(ContinueStatementPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(ConstructorInvocationPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(DoStatementPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(EmptyStatementPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(EnhancedForStatementPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(ExpressionStatementPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(ForStatementPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(IfStatementPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(LabeledStatementPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(ReturnStatementPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(SuperConstructorInvocationPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(SwitchCasePattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(SwitchStatementPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(SynchronizedStatementPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(ThrowStatementPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(TryStatementPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(TypeDeclarationStatementPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(VariableDeclarationStatementPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(WhileStatementPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public <T extends Type> void visit(TypePattern<T> p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(TypeParameterPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(ArrayTypePattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(ParameterizedTypePattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(PrimitiveTypePattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(QualifiedTypePattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(SimpleTypePattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(UnionTypePattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(WildcardTypePattern p) {
		p.recurseVisit(this);
	}

	@Override
	public <T extends Comment> void visit(CommentPattern<T> p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(BlockCommentPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(JavadocPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(LineCommentPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public <T> void visit(DocElementPattern<T> p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(TagElementPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(TextElementPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(MemberRefPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(MethodRefPattern p) {
		p.recurseVisit(this);
	}

	@Override
	public void visit(MethodRefParameterPattern p) {
		p.recurseVisit(this);
	}

}
