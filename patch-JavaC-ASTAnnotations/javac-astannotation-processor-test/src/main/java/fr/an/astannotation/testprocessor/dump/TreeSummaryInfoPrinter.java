package fr.an.astannotation.testprocessor.dump;

import javax.tools.JavaFileObject;

import com.sun.source.tree.ASTAnnotationTree;
import com.sun.source.tree.AnnotatedTypeTree;
import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ArrayAccessTree;
import com.sun.source.tree.ArrayTypeTree;
import com.sun.source.tree.AssertTree;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.BreakTree;
import com.sun.source.tree.CaseTree;
import com.sun.source.tree.CatchTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.ConditionalExpressionTree;
import com.sun.source.tree.ContinueTree;
import com.sun.source.tree.DoWhileLoopTree;
import com.sun.source.tree.EmptyStatementTree;
import com.sun.source.tree.EnhancedForLoopTree;
import com.sun.source.tree.ErroneousTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.InstanceOfTree;
import com.sun.source.tree.IntersectionTypeTree;
import com.sun.source.tree.LabeledStatementTree;
import com.sun.source.tree.LambdaExpressionTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MemberReferenceTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.NewArrayTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ParameterizedTypeTree;
import com.sun.source.tree.ParenthesizedTree;
import com.sun.source.tree.PrimitiveTypeTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.SwitchTree;
import com.sun.source.tree.SynchronizedTree;
import com.sun.source.tree.ThrowTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TreeVisitor;
import com.sun.source.tree.TryTree;
import com.sun.source.tree.TypeCastTree;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.tree.UnionTypeTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.tree.WhileLoopTree;
import com.sun.source.tree.WildcardTree;

/**
 * a simple Visitor on Tree, to print summary info of node on a single line
 *
 */
public class TreeSummaryInfoPrinter implements TreeVisitor<String, Void> {

    public String visitASTAnnotation(ASTAnnotationTree node, Void p) {
        return "@@" + node.getAnnotationType().toString();
    }

    public String visitAnnotatedType(AnnotatedTypeTree node, Void p) {
        return "AnnotatedType"; // TODO
    }

    public String visitAnnotation(AnnotationTree node, Void p) {
        return "@" + node.getAnnotationType().toString();
    }

    public String visitMethodInvocation(MethodInvocationTree node, Void p) {
        String methodInfo = "";
        ExpressionTree methodSel = node.getMethodSelect();
        if (methodSel instanceof IdentifierTree) {
            methodInfo = ((IdentifierTree) methodSel).getName().toString(); 
        }
        return methodInfo + "(..)";
    }

    public String visitAssert(AssertTree node, Void p) {
        return "assert";
    }

    public String visitAssignment(AssignmentTree node, Void p) {
        return "=";
    }

    public String visitCompoundAssignment(CompoundAssignmentTree node, Void p) {
        return node.getKind().getDisplayName();
    }

    public String visitBinary(BinaryTree node, Void p) {
        return "binaryOp " + node.getKind().getDisplayName();
    }

    public String visitBlock(BlockTree node, Void p) {
        return "{}";
    }

    public String visitBreak(BreakTree node, Void p) {
        return "break";
    }

    public String visitCase(CaseTree node, Void p) {
        return "case";
    }

    public String visitCatch(CatchTree node, Void p) {
        return "catch";
    }

    public String visitClass(ClassTree node, Void p) {
        return "class " + node.getSimpleName();
    }

    public String visitConditionalExpression(ConditionalExpressionTree node, Void p) {
        return "()?:";
    }

    public String visitContinue(ContinueTree node, Void p) {
        return "continue";
    }

    public String visitDoWhileLoop(DoWhileLoopTree node, Void p) {
        return "do-while";
    }

    public String visitErroneous(ErroneousTree node, Void p) {
        return "Erroneous";
    }

    public String visitExpressionStatement(ExpressionStatementTree node, Void p) {
        return "expr;";
    }

    public String visitEnhancedForLoop(EnhancedForLoopTree node, Void p) {
        return "for(:)";
    }

    public String visitForLoop(ForLoopTree node, Void p) {
        return "for(;;)";
    }

    public String visitIdentifier(IdentifierTree node, Void p) {
        return "ident " + node.getName();
    }

    public String visitIf(IfTree node, Void p) {
        return "if";
    }

    public String visitImport(ImportTree node, Void p) {
        return "import " + node.getQualifiedIdentifier();
    }

    public String visitArrayAccess(ArrayAccessTree node, Void p) {
        return "[.]";
    }

    public String visitLabeledStatement(LabeledStatementTree node, Void p) {
        return "label " + node.getLabel();
    }

    public String visitLiteral(LiteralTree node, Void p) {
        return "literal : " + node.getValue();
    }

    public String visitMethod(MethodTree node, Void p) {
        return "method " + node.getName(); 
    }

    public String visitModifiers(ModifiersTree node, Void p) {
        return "modifiers " + node.getFlags();
    }

    public String visitNewArray(NewArrayTree node, Void p) {
        return "new []";
    }

    public String visitNewClass(NewClassTree node, Void p) {
        return "new " + node.getIdentifier() + "()";
    }

    public String visitLambdaExpression(LambdaExpressionTree node, Void p) {
        return "lambda";
    }

    public String visitParenthesized(ParenthesizedTree node, Void p) {
        return "(expr)";
    }

    public String visitReturn(ReturnTree node, Void p) {
        return "return";
    }

    public String visitMemberSelect(MemberSelectTree node, Void p) {
        return "." + node.getIdentifier();
    }

    public String visitMemberReference(MemberReferenceTree node, Void p) {
        return "#" + node.getName();
    }

    public String visitEmptyStatement(EmptyStatementTree node, Void p) {
        return "empty";
    }

    public String visitSwitch(SwitchTree node, Void p) {
        return "switch";
    }

    public String visitSynchronized(SynchronizedTree node, Void p) {
        return "synchronized";
    }

    public String visitThrow(ThrowTree node, Void p) {
        return "throw";
    }

    public String visitCompilationUnit(CompilationUnitTree node, Void p) {
        JavaFileObject sourceFile = node.getSourceFile();
        return "CompilationUnit " + ((sourceFile != null)? sourceFile.getName() : "");
    }

    public String visitTry(TryTree node, Void p) {
        return "try";
    }

    public String visitParameterizedType(ParameterizedTypeTree node, Void p) {
        return "Type<T1,T2>";
    }

    public String visitUnionType(UnionTypeTree node, Void p) {
        return "type|type";
    }

    public String visitIntersectionType(IntersectionTypeTree node, Void p) {
        return "intersectType";
    }

    public String visitArrayType(ArrayTypeTree node, Void p) {
        return "ArrayType";
    }

    public String visitTypeCast(TypeCastTree node, Void p) {
        return "typecast()";
    }

    public String visitPrimitiveType(PrimitiveTypeTree node, Void p) {
        return "primitiveType " + node.getPrimitiveTypeKind();
    }

    public String visitTypeParameter(TypeParameterTree node, Void p) {
        return "TypeParam";
    }

    public String visitInstanceOf(InstanceOfTree node, Void p) {
        return "instanceof";
    }

    public String visitUnary(UnaryTree node, Void p) {
        return "unary " + node.getKind().getDisplayName();
    }

    public String visitVariable(VariableTree node, Void p) {
        return "var " + node.getName();
    }

    public String visitWhileLoop(WhileLoopTree node, Void p) {
        return "while";
    }

    public String visitWildcard(WildcardTree node, Void p) {
        return "wildcard?";
    }

    public String visitOther(Tree node, Void p) {
        return "other";
    }

    
}
