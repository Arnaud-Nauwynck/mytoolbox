package fr.an.eclipse.pattern.ast;

import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclaration;

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

public abstract class PatternVisitor {

	// generic pre-post visit
	// ------------------------------------------------------------------------
	
	public abstract <T> boolean preVisit(IPattern<T> obj);
	public abstract <T> void postVisit(IPattern<T> obj);
	
	
	// Algebra Patterns
	// ------------------------------------------------------------------------
	
	public abstract <T> void visit(GroupCapturePattern<T> p);
	public abstract <T> void visit(WildcardPattern<T> p);
	public abstract <T> void visit(MatchNotPattern<T> p);

	public abstract <T> void visit(MatchAndPattern<T> p);
	public abstract <T> void visit(MatchOrPattern<T> p);
 
	public abstract <T> void visit(MatchAllOfPattern<T> p);
	public abstract <T> void visit(MatchOneOfPattern<T> p);
	
	public abstract <T> void visit(WildcardContainingPattern<T> p);
	
	// base objects
	// ------------------------------------------------------------------------
	
	public abstract void visit(DefaultIntegerPattern p);
	public abstract void visit(DefaultStringPattern p);
	public abstract void visit(RegexpStringPattern p);
	public abstract void visit(DefaultBooleanPattern p);
	
	public abstract <T> void visit(DefaultASTListPattern<T> p);
	public abstract <T> void visit(MatchOneEltOfListPattern<T> p);
	public abstract <T> void visit(MatchAllEltsOfListPattern<T> p);

	// AST Declarations
	// ------------------------------------------------------------------------
	
	public abstract void visit(CompilationUnitPattern p);
	public abstract void visit(PackageDeclarationPattern p);
	public abstract <T extends BodyDeclaration> void visit(BodyDeclarationPattern<T> p);
	public abstract <T extends AbstractTypeDeclaration> void visit(AbstractTypeDeclarationPattern<T> p);
	public abstract void visit(TypeDeclarationPattern p);
	public abstract void visit(AnnotationTypeDeclarationPattern p);
	public abstract void visit(EnumDeclarationPattern p);
	public abstract void visit(EnumConstantDeclarationPattern p);
	public abstract void visit(AnonymousClassDeclarationPattern p);
	public abstract void visit(MethodDeclarationPattern p);
	public abstract void visit(FieldDeclarationPattern p);
	public abstract <T extends VariableDeclaration> void visit(VariableDeclarationPattern<T> p);
	public abstract void visit(SingleVariableDeclarationPattern p);
	public abstract void visit(VariableDeclarationFragmentPattern p);
	public abstract void visit(ModifierFlagsPattern p);
	public abstract void visit(ExtendedModifierPattern p);
	public abstract void visit(AnnotationTypeMemberDeclarationPattern p);
	public abstract void visit(InitializerPattern p);
	public abstract void visit(ImportDeclarationPattern p);
	public abstract void visit(MarkerAnnotationPattern p);
	public abstract void visit(ModifierPattern p);
	
	// AST Expressions
	// ------------------------------------------------------------------------
	
	public abstract void visit(SimpleNamePattern p);
	public abstract void visit(QualifiedNamePattern p);
	public abstract <T extends Annotation> void visit(AnnotationPattern<T> p);
	public abstract void visit(NormalAnnotationPattern p);
	public abstract void visit(MemberValuePairPattern p);
	public abstract void visit(SingleMemberAnnotationPattern p);
	public abstract void visit(ArrayAccessPattern p);
	public abstract void visit(ArrayCreationPattern p);
	public abstract void visit(ArrayInitializerPattern p);
	public abstract void visit(AssignmentPattern p);
	public abstract void visit(AssignmentOperatorPattern p);
	public abstract void visit(BooleanLiteralPattern p);
	public abstract void visit(CastExpressionPattern p);
	public abstract void visit(CharacterLiteralPattern p);
	public abstract void visit(ClassInstanceCreationPattern p);
	public abstract void visit(ConditionalExpressionPattern p);
	public abstract void visit(FieldAccessPattern p);
	public abstract void visit(InfixExpressionPattern p);
	public abstract void visit(InfixExpressionOperatorPattern p);
	public abstract void visit(InstanceofExpressionPattern p);
	public abstract void visit(MethodInvocationPattern p);
	public abstract void visit(NullLiteralPattern p);
	public abstract void visit(NumberLiteralPattern p);
	public abstract void visit(ParenthesizedExpressionPattern p);
	public abstract void visit(PostfixExpressionPattern p);
	public abstract void visit(PostfixExpressionOperatorPattern p);
	public abstract void visit(PrefixExpressionPattern p);
	public abstract void visit(PrefixExpressionOperatorPattern p);
	public abstract void visit(StringLiteralPattern p);
	public abstract void visit(SuperFieldAccessPattern p);
	public abstract void visit(SuperMethodInvocationPattern p);
	public abstract void visit(ThisExpressionPattern p);
	public abstract void visit(VariableDeclarationExpressionPattern p);
	public abstract void visit(TypeLiteralPattern p);
	
	// AST Statements
	// ------------------------------------------------------------------------
	
	public abstract void visit(BlockPattern p);
	public abstract void visit(AssertStatementPattern p);
	public abstract void visit(BreakStatementPattern p);
	public abstract void visit(CatchClausePattern p);
	public abstract void visit(ContinueStatementPattern p);
	public abstract void visit(ConstructorInvocationPattern p);
	public abstract void visit(DoStatementPattern p);
	public abstract void visit(EmptyStatementPattern p);
	public abstract void visit(EnhancedForStatementPattern p);
	public abstract void visit(ExpressionStatementPattern p);
	public abstract void visit(ForStatementPattern p);
	public abstract void visit(IfStatementPattern p);
	public abstract void visit(LabeledStatementPattern p);
	public abstract void visit(ReturnStatementPattern p);
	public abstract void visit(SuperConstructorInvocationPattern p);
	public abstract void visit(SwitchCasePattern p);
	public abstract void visit(SwitchStatementPattern p);
	public abstract void visit(SynchronizedStatementPattern p);
	public abstract void visit(ThrowStatementPattern p);
	public abstract void visit(TryStatementPattern p);
	public abstract void visit(TypeDeclarationStatementPattern p);
	public abstract void visit(VariableDeclarationStatementPattern p);
	public abstract void visit(WhileStatementPattern p);

	// AST Types
	// ------------------------------------------------------------------------
	
	public abstract <T extends Type> void visit(TypePattern<T> p);
	public abstract void visit(TypeParameterPattern p);
	public abstract void visit(ArrayTypePattern p);
	public abstract void visit(ParameterizedTypePattern p);
	public abstract void visit(PrimitiveTypePattern p);
	public abstract void visit(QualifiedTypePattern p);
	public abstract void visit(SimpleTypePattern p);
	public abstract void visit(UnionTypePattern p);
	public abstract void visit(WildcardTypePattern p);
	
	// AST Comments 
	// ------------------------------------------------------------------------
	
	public abstract <T extends Comment> void visit(CommentPattern<T> p);
	public abstract void visit(BlockCommentPattern p);
	public abstract void visit(JavadocPattern p);
	public abstract void visit(LineCommentPattern p);
	public abstract <T> void visit(DocElementPattern<T> p);
	public abstract void visit(TagElementPattern p);
	public abstract void visit(TextElementPattern p);
	public abstract void visit(MemberRefPattern p);
	public abstract void visit(MethodRefPattern p);
	public abstract void visit(MethodRefParameterPattern p);
	
}
