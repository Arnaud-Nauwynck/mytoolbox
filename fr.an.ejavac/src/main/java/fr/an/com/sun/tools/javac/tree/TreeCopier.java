/*
 * Copyright (c) 2006, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package fr.an.com.sun.tools.javac.tree;

import fr.an.com.sun.source.tree.*;
import fr.an.com.sun.tools.javac.tree.JCTree.*;
import fr.an.com.sun.tools.javac.util.List;
import fr.an.com.sun.tools.javac.util.ListBuffer;

/**
 * Creates a copy of a tree, using a given TreeMaker.
 * Names, literal values, etc are shared with the original.
 *
 *  <p><b>This is NOT part of any supported API.
 *  If you write code that depends on this, you do so at your own risk.
 *  This code and its internal interfaces are subject to change or
 *  deletion without notice.</b>
 */
public class TreeCopier<P> implements TreeVisitor<JCTree,P> {
    private TreeMaker M;

    /** Creates a new instance of TreeCopier */
    public TreeCopier(TreeMaker M) {
        this.M = M;
    }

    public <T extends JCTree> T copy(T tree) {
        return copy(tree, null);
    }

    @SuppressWarnings("unchecked")
    public <T extends JCTree> T copy(T tree, P p) {
        if (tree == null)
            return null;
        return (T) (tree.accept(this, p));
    }

    public <T extends JCTree> List<T> copy(List<T> trees) {
        return copy(trees, null);
    }

    public <T extends JCTree> List<T> copy(List<T> trees, P p) {
        if (trees == null)
            return null;
        ListBuffer<T> lb = new ListBuffer<T>();
        for (T tree: trees)
            lb.append(copy(tree, p));
        return lb.toList();
    }

    private TreeMaker newAt(JCTree t) {
    	return M .at(t.pos);
    }

    public JCTree visitASTAnnotation(ASTAnnotationTree node, P p) {
        JCASTAnnotation t = (JCASTAnnotation) node;
        JCTree annotationType = copy(t.annotationType, p);
        List<JCExpression> args = copy(t.args, p);
        JCASTAnnotation newTA = newAt(t).ASTAnnotation(annotationType, args);
        return newTA;
    }

    public JCTree visitAnnotatedType(AnnotatedTypeTree node, P p) {
        JCAnnotatedType t = (JCAnnotatedType) node;
        List<JCAnnotation> annotations = copy(t.annotations, p);
        JCExpression underlyingType = copy(t.underlyingType, p);
        return newAt(t).AnnotatedType(annotations, underlyingType);
    }

    public JCTree visitAnnotation(AnnotationTree node, P p) {
        JCAnnotation t = (JCAnnotation) node;
        JCTree annotationType = copy(t.annotationType, p);
        List<JCExpression> args = copy(t.args, p);
        if (t.getKind() == Tree.Kind.TYPE_ANNOTATION) {
            JCAnnotation newTA = newAt(t).TypeAnnotation(annotationType, args);
            newTA.attribute = t.attribute;
            return newTA;
        } else {
            JCAnnotation newT = newAt(t).Annotation(annotationType, args);
            newT.attribute = t.attribute;
            return newT;
        }
    }

    public JCTree visitAssert(AssertTree node, P p) {
        JCAssert t = (JCAssert) node;
        JCExpression cond = copy(t.cond, p);
        JCExpression detail = copy(t.detail, p);
        return newAt(t).Assert(cond, detail);
    }

    public JCTree visitAssignment(AssignmentTree node, P p) {
        JCAssign t = (JCAssign) node;
        JCExpression lhs = copy(t.lhs, p);
        JCExpression rhs = copy(t.rhs, p);
        return newAt(t).Assign(lhs, rhs);
    }

    public JCTree visitCompoundAssignment(CompoundAssignmentTree node, P p) {
        JCAssignOp t = (JCAssignOp) node;
        JCTree lhs = copy(t.lhs, p);
        JCTree rhs = copy(t.rhs, p);
        return newAt(t).Assignop(t.getTag(), lhs, rhs);
    }

    public JCTree visitBinary(BinaryTree node, P p) {
        JCBinary t = (JCBinary) node;
        JCExpression lhs = copy(t.lhs, p);
        JCExpression rhs = copy(t.rhs, p);
        return newAt(t).Binary(t.getTag(), lhs, rhs);
    }

    public JCTree visitBlock(BlockTree node, P p) {
        JCBlock t = (JCBlock) node;
        List<JCStatement> stats = copy(t.stats, p);
        return newAt(t).Block(t.flags, stats);
    }

    public JCTree visitBreak(BreakTree node, P p) {
        JCBreak t = (JCBreak) node;
        return newAt(t).Break(t.label);
    }

    public JCTree visitCase(CaseTree node, P p) {
        JCCase t = (JCCase) node;
        JCExpression pat = copy(t.pat, p);
        List<JCStatement> stats = copy(t.stats, p);
        return newAt(t).Case(pat, stats);
    }

    public JCTree visitCatch(CatchTree node, P p) {
        JCCatch t = (JCCatch) node;
        JCVariableDecl param = copy(t.param, p);
        JCBlock body = copy(t.body, p);
        return newAt(t).Catch(param, body);
    }

    public JCTree visitClass(ClassTree node, P p) {
        JCClassDecl t = (JCClassDecl) node;
        JCModifiers mods = copy(t.mods, p);
        List<JCTypeParameter> typarams = copy(t.typarams, p);
        JCExpression extending = copy(t.extending, p);
        List<JCExpression> implementing = copy(t.implementing, p);
        List<JCTree> defs = copy(t.defs, p);
        return newAt(t).ClassDef(mods, t.name, typarams, extending, implementing, defs);
    }

    public JCTree visitConditionalExpression(ConditionalExpressionTree node, P p) {
        JCConditional t = (JCConditional) node;
        JCExpression cond = copy(t.cond, p);
        JCExpression truepart = copy(t.truepart, p);
        JCExpression falsepart = copy(t.falsepart, p);
        return newAt(t).Conditional(cond, truepart, falsepart);
    }

    public JCTree visitContinue(ContinueTree node, P p) {
        JCContinue t = (JCContinue) node;
        return newAt(t).Continue(t.label);
    }

    public JCTree visitDoWhileLoop(DoWhileLoopTree node, P p) {
        JCDoWhileLoop t = (JCDoWhileLoop) node;
        JCStatement body = copy(t.body, p);
        JCExpression cond = copy(t.cond, p);
        return newAt(t).DoLoop(body, cond);
    }

    public JCTree visitErroneous(ErroneousTree node, P p) {
        JCErroneous t = (JCErroneous) node;
        List<? extends JCTree> errs = copy(t.errs, p);
        return newAt(t).Erroneous(errs);
    }

    public JCTree visitExpressionStatement(ExpressionStatementTree node, P p) {
        JCExpressionStatement t = (JCExpressionStatement) node;
        JCExpression expr = copy(t.expr, p);
        return newAt(t).Exec(expr);
    }

    public JCTree visitEnhancedForLoop(EnhancedForLoopTree node, P p) {
        JCEnhancedForLoop t = (JCEnhancedForLoop) node;
        JCVariableDecl var = copy(t.var, p);
        JCExpression expr = copy(t.expr, p);
        JCStatement body = copy(t.body, p);
        return newAt(t).ForeachLoop(var, expr, body);
    }

    public JCTree visitForLoop(ForLoopTree node, P p) {
        JCForLoop t = (JCForLoop) node;
        List<JCStatement> init = copy(t.init, p);
        JCExpression cond = copy(t.cond, p);
        List<JCExpressionStatement> step = copy(t.step, p);
        JCStatement body = copy(t.body, p);
        return newAt(t).ForLoop(init, cond, step, body);
    }

    public JCTree visitIdentifier(IdentifierTree node, P p) {
        JCIdent t = (JCIdent) node;
        return newAt(t).Ident(t.name);
    }

    public JCTree visitIf(IfTree node, P p) {
        JCIf t = (JCIf) node;
        JCExpression cond = copy(t.cond, p);
        JCStatement thenpart = copy(t.thenpart, p);
        JCStatement elsepart = copy(t.elsepart, p);
        return newAt(t).If(cond, thenpart, elsepart);
    }

    public JCTree visitImport(ImportTree node, P p) {
        JCImport t = (JCImport) node;
        JCTree qualid = copy(t.qualid, p);
        return newAt(t).Import(qualid, t.staticImport);
    }

    public JCTree visitArrayAccess(ArrayAccessTree node, P p) {
        JCArrayAccess t = (JCArrayAccess) node;
        JCExpression indexed = copy(t.indexed, p);
        JCExpression index = copy(t.index, p);
        return newAt(t).Indexed(indexed, index);
    }

    public JCTree visitLabeledStatement(LabeledStatementTree node, P p) {
        JCLabeledStatement t = (JCLabeledStatement) node;
        JCStatement body = copy(t.body, p);
        return newAt(t).Labelled(t.label, t.body);
    }

    public JCTree visitLiteral(LiteralTree node, P p) {
        JCLiteral t = (JCLiteral) node;
        return newAt(t).Literal(t.typetag, t.value);
    }

    public JCTree visitMethod(MethodTree node, P p) {
        JCMethodDecl t  = (JCMethodDecl) node;
        JCModifiers mods = copy(t.mods, p);
        JCExpression restype = copy(t.restype, p);
        List<JCTypeParameter> typarams = copy(t.typarams, p);
        List<JCVariableDecl> params = copy(t.params, p);
        JCVariableDecl recvparam = copy(t.recvparam, p);
        List<JCExpression> thrown = copy(t.thrown, p);
        JCBlock body = copy(t.body, p);
        JCExpression defaultValue = copy(t.defaultValue, p);
        return newAt(t).MethodDef(mods, t.name, restype, typarams, recvparam, params, thrown, body, defaultValue);
    }

    public JCTree visitMethodInvocation(MethodInvocationTree node, P p) {
        JCMethodInvocation t = (JCMethodInvocation) node;
        List<JCExpression> typeargs = copy(t.typeargs, p);
        JCExpression meth = copy(t.meth, p);
        List<JCExpression> args = copy(t.args, p);
        return newAt(t).Apply(typeargs, meth, args);
    }

    public JCTree visitModifiers(ModifiersTree node, P p) {
        JCModifiers t = (JCModifiers) node;
        List<JCAnnotation> annotations = copy(t.annotations, p);
        return newAt(t).Modifiers(t.flags, annotations);
    }

    public JCTree visitNewArray(NewArrayTree node, P p) {
        JCNewArray t = (JCNewArray) node;
        JCExpression elemtype = copy(t.elemtype, p);
        List<JCExpression> dims = copy(t.dims, p);
        List<JCExpression> elems = copy(t.elems, p);
        return newAt(t).NewArray(elemtype, dims, elems);
    }

    public JCTree visitNewClass(NewClassTree node, P p) {
        JCNewClass t = (JCNewClass) node;
        JCExpression encl = copy(t.encl, p);
        List<JCExpression> typeargs = copy(t.typeargs, p);
        JCExpression clazz = copy(t.clazz, p);
        List<JCExpression> args = copy(t.args, p);
        JCClassDecl def = copy(t.def, p);
        return newAt(t).NewClass(encl, typeargs, clazz, args, def);
    }

    public JCTree visitLambdaExpression(LambdaExpressionTree node, P p) {
        JCLambda t = (JCLambda) node;
        List<JCVariableDecl> params = copy(t.params, p);
        JCTree body = copy(t.body, p);
        return newAt(t).Lambda(params, body);
    }

    public JCTree visitParenthesized(ParenthesizedTree node, P p) {
        JCParens t = (JCParens) node;
        JCExpression expr = copy(t.expr, p);
        return newAt(t).Parens(expr);
    }

    public JCTree visitReturn(ReturnTree node, P p) {
        JCReturn t = (JCReturn) node;
        JCExpression expr = copy(t.expr, p);
        return newAt(t).Return(expr);
    }

    public JCTree visitMemberSelect(MemberSelectTree node, P p) {
        JCFieldAccess t = (JCFieldAccess) node;
        JCExpression selected = copy(t.selected, p);
        return newAt(t).Select(selected, t.name);
    }

    public JCTree visitMemberReference(MemberReferenceTree node, P p) {
        JCMemberReference t = (JCMemberReference) node;
        JCExpression expr = copy(t.expr, p);
        List<JCExpression> typeargs = copy(t.typeargs, p);
        return newAt(t).Reference(t.mode, t.name, expr, typeargs);
    }

    public JCTree visitEmptyStatement(EmptyStatementTree node, P p) {
        JCSkip t = (JCSkip) node;
        return newAt(t).Skip();
    }

    public JCTree visitSwitch(SwitchTree node, P p) {
        JCSwitch t = (JCSwitch) node;
        JCExpression selector = copy(t.selector, p);
        List<JCCase> cases = copy(t.cases, p);
        return newAt(t).Switch(selector, cases);
    }

    public JCTree visitSynchronized(SynchronizedTree node, P p) {
        JCSynchronized t = (JCSynchronized) node;
        JCExpression lock = copy(t.lock, p);
        JCBlock body = copy(t.body, p);
        return newAt(t).Synchronized(lock, body);
    }

    public JCTree visitThrow(ThrowTree node, P p) {
        JCThrow t = (JCThrow) node;
        JCExpression expr = copy(t.expr, p);
        return newAt(t).Throw(expr);
    }

    public JCTree visitCompilationUnit(CompilationUnitTree node, P p) {
        JCCompilationUnit t = (JCCompilationUnit) node;
        List<JCAnnotation> packageAnnotations = copy(t.packageAnnotations, p);
        JCExpression pid = copy(t.pid, p);
        List<JCTree> defs = copy(t.defs, p);
        return newAt(t).TopLevel(packageAnnotations, pid, defs);
    }

    public JCTree visitTry(TryTree node, P p) {
        JCTry t = (JCTry) node;
        List<JCTree> resources = copy(t.resources, p);
        JCBlock body = copy(t.body, p);
        List<JCCatch> catchers = copy(t.catchers, p);
        JCBlock finalizer = copy(t.finalizer, p);
        return newAt(t).Try(resources, body, catchers, finalizer);
    }

    public JCTree visitParameterizedType(ParameterizedTypeTree node, P p) {
        JCTypeApply t = (JCTypeApply) node;
        JCExpression clazz = copy(t.clazz, p);
        List<JCExpression> arguments = copy(t.arguments, p);
        return newAt(t).TypeApply(clazz, arguments);
    }

    public JCTree visitUnionType(UnionTypeTree node, P p) {
        JCTypeUnion t = (JCTypeUnion) node;
        List<JCExpression> components = copy(t.alternatives, p);
        return newAt(t).TypeUnion(components);
    }

    public JCTree visitIntersectionType(IntersectionTypeTree node, P p) {
        JCTypeIntersection t = (JCTypeIntersection) node;
        List<JCExpression> bounds = copy(t.bounds, p);
        return newAt(t).TypeIntersection(bounds);
    }

    public JCTree visitArrayType(ArrayTypeTree node, P p) {
        JCArrayTypeTree t = (JCArrayTypeTree) node;
        JCExpression elemtype = copy(t.elemtype, p);
        return newAt(t).TypeArray(elemtype);
    }

    public JCTree visitTypeCast(TypeCastTree node, P p) {
        JCTypeCast t = (JCTypeCast) node;
        JCTree clazz = copy(t.clazz, p);
        JCExpression expr = copy(t.expr, p);
        return newAt(t).TypeCast(clazz, expr);
    }

    public JCTree visitPrimitiveType(PrimitiveTypeTree node, P p) {
        JCPrimitiveTypeTree t = (JCPrimitiveTypeTree) node;
        return newAt(t).TypeIdent(t.typetag);
    }

    public JCTree visitTypeParameter(TypeParameterTree node, P p) {
        JCTypeParameter t = (JCTypeParameter) node;
        List<JCAnnotation> annos = copy(t.annotations, p);
        List<JCExpression> bounds = copy(t.bounds, p);
        return newAt(t).TypeParameter(t.name, bounds, annos);
    }

    public JCTree visitInstanceOf(InstanceOfTree node, P p) {
        JCInstanceOf t = (JCInstanceOf) node;
        JCExpression expr = copy(t.expr, p);
        JCTree clazz = copy(t.clazz, p);
        return newAt(t).TypeTest(expr, clazz);
    }

    public JCTree visitUnary(UnaryTree node, P p) {
        JCUnary t = (JCUnary) node;
        JCExpression arg = copy(t.arg, p);
        return newAt(t).Unary(t.getTag(), arg);
    }

    public JCTree visitVariable(VariableTree node, P p) {
        JCVariableDecl t = (JCVariableDecl) node;
        JCModifiers mods = copy(t.mods, p);
        JCExpression vartype = copy(t.vartype, p);
        if (t.nameexpr == null) {
            JCExpression init = copy(t.init, p);
            return newAt(t).VarDef(mods, t.name, vartype, init);
        } else {
            JCExpression nameexpr = copy(t.nameexpr, p);
            return newAt(t).ReceiverVarDef(mods, nameexpr, vartype);
        }
    }

    public JCTree visitWhileLoop(WhileLoopTree node, P p) {
        JCWhileLoop t = (JCWhileLoop) node;
        JCStatement body = copy(t.body, p);
        JCExpression cond = copy(t.cond, p);
        return newAt(t).WhileLoop(cond, body);
    }

    public JCTree visitWildcard(WildcardTree node, P p) {
        JCWildcard t = (JCWildcard) node;
        TypeBoundKind kind = M.at(t.kind.pos).TypeBoundKind(t.kind.kind);
        JCTree inner = copy(t.inner, p);
        return newAt(t).Wildcard(kind, inner);
    }

    public JCTree visitOther(Tree node, P p) {
        JCTree tree = (JCTree) node;
        switch (tree.getTag()) {
            case LETEXPR: {
                LetExpr t = (LetExpr) node;
                List<JCVariableDecl> defs = copy(t.defs, p);
                JCTree expr = copy(t.expr, p);
                return newAt(t).LetExpr(defs, expr);
            }
            default:
                throw new AssertionError("unknown tree tag: " + tree.getTag());
        }
    }

}
