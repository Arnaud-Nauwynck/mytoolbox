/*
 * Copyright (c) 1999, 2013, Oracle and/or its affiliates. All rights reserved.
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

import static fr.an.com.sun.tools.javac.code.Flags.FINAL;
import static fr.an.com.sun.tools.javac.code.Flags.PUBLIC;
import static fr.an.com.sun.tools.javac.code.Flags.STATIC;
import static fr.an.com.sun.tools.javac.code.Kinds.MTH;
import static fr.an.com.sun.tools.javac.code.Kinds.TYP;
import static fr.an.com.sun.tools.javac.code.Kinds.VAR;
import static fr.an.com.sun.tools.javac.code.TypeTag.BOOLEAN;
import static fr.an.com.sun.tools.javac.code.TypeTag.BYTE;
import static fr.an.com.sun.tools.javac.code.TypeTag.CHAR;
import static fr.an.com.sun.tools.javac.code.TypeTag.CLASS;
import static fr.an.com.sun.tools.javac.code.TypeTag.DOUBLE;
import static fr.an.com.sun.tools.javac.code.TypeTag.ERROR;
import static fr.an.com.sun.tools.javac.code.TypeTag.FLOAT;
import static fr.an.com.sun.tools.javac.code.TypeTag.INT;
import static fr.an.com.sun.tools.javac.code.TypeTag.LONG;
import static fr.an.com.sun.tools.javac.code.TypeTag.SHORT;
import static fr.an.com.sun.tools.javac.code.TypeTag.VOID;
import fr.an.com.sun.source.tree.Tree.Kind;
import fr.an.com.sun.tools.javac.code.Attribute;
import fr.an.com.sun.tools.javac.code.BoundKind;
import fr.an.com.sun.tools.javac.code.Flags;
import fr.an.com.sun.tools.javac.code.Scope;
import fr.an.com.sun.tools.javac.code.Symbol;
import fr.an.com.sun.tools.javac.code.Symtab;
import fr.an.com.sun.tools.javac.code.Type;
import fr.an.com.sun.tools.javac.code.TypeTag;
import fr.an.com.sun.tools.javac.code.Types;
import fr.an.com.sun.tools.javac.code.Symbol.ClassSymbol;
import fr.an.com.sun.tools.javac.code.Symbol.MethodSymbol;
import fr.an.com.sun.tools.javac.code.Symbol.TypeSymbol;
import fr.an.com.sun.tools.javac.code.Symbol.VarSymbol;
import fr.an.com.sun.tools.javac.code.Type.ArrayType;
import fr.an.com.sun.tools.javac.code.Type.TypeVar;
import fr.an.com.sun.tools.javac.code.Type.WildcardType;
import fr.an.com.sun.tools.javac.parser.Tokens.AstAnnotationTokenExtension;
import fr.an.com.sun.tools.javac.parser.Tokens.Token;
import fr.an.com.sun.tools.javac.parser.Tokens.TokenExtension;
import fr.an.com.sun.tools.javac.tree.JCTree.JCASTAnnotation;
import fr.an.com.sun.tools.javac.tree.JCTree.JCAnnotatedType;
import fr.an.com.sun.tools.javac.tree.JCTree.JCAnnotation;
import fr.an.com.sun.tools.javac.tree.JCTree.JCArrayAccess;
import fr.an.com.sun.tools.javac.tree.JCTree.JCArrayTypeTree;
import fr.an.com.sun.tools.javac.tree.JCTree.JCAssert;
import fr.an.com.sun.tools.javac.tree.JCTree.JCAssign;
import fr.an.com.sun.tools.javac.tree.JCTree.JCAssignOp;
import fr.an.com.sun.tools.javac.tree.JCTree.JCBinary;
import fr.an.com.sun.tools.javac.tree.JCTree.JCBlock;
import fr.an.com.sun.tools.javac.tree.JCTree.JCBreak;
import fr.an.com.sun.tools.javac.tree.JCTree.JCCase;
import fr.an.com.sun.tools.javac.tree.JCTree.JCCatch;
import fr.an.com.sun.tools.javac.tree.JCTree.JCClassDecl;
import fr.an.com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import fr.an.com.sun.tools.javac.tree.JCTree.JCConditional;
import fr.an.com.sun.tools.javac.tree.JCTree.JCContinue;
import fr.an.com.sun.tools.javac.tree.JCTree.JCDoWhileLoop;
import fr.an.com.sun.tools.javac.tree.JCTree.JCEnhancedForLoop;
import fr.an.com.sun.tools.javac.tree.JCTree.JCErroneous;
import fr.an.com.sun.tools.javac.tree.JCTree.JCExpression;
import fr.an.com.sun.tools.javac.tree.JCTree.JCExpressionStatement;
import fr.an.com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import fr.an.com.sun.tools.javac.tree.JCTree.JCForLoop;
import fr.an.com.sun.tools.javac.tree.JCTree.JCIdent;
import fr.an.com.sun.tools.javac.tree.JCTree.JCIf;
import fr.an.com.sun.tools.javac.tree.JCTree.JCImport;
import fr.an.com.sun.tools.javac.tree.JCTree.JCInstanceOf;
import fr.an.com.sun.tools.javac.tree.JCTree.JCLabeledStatement;
import fr.an.com.sun.tools.javac.tree.JCTree.JCLambda;
import fr.an.com.sun.tools.javac.tree.JCTree.JCLiteral;
import fr.an.com.sun.tools.javac.tree.JCTree.JCMemberReference;
import fr.an.com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import fr.an.com.sun.tools.javac.tree.JCTree.JCMethodInvocation;
import fr.an.com.sun.tools.javac.tree.JCTree.JCModifiers;
import fr.an.com.sun.tools.javac.tree.JCTree.JCNewArray;
import fr.an.com.sun.tools.javac.tree.JCTree.JCNewClass;
import fr.an.com.sun.tools.javac.tree.JCTree.JCParens;
import fr.an.com.sun.tools.javac.tree.JCTree.JCPrimitiveTypeTree;
import fr.an.com.sun.tools.javac.tree.JCTree.JCReturn;
import fr.an.com.sun.tools.javac.tree.JCTree.JCSkip;
import fr.an.com.sun.tools.javac.tree.JCTree.JCStatement;
import fr.an.com.sun.tools.javac.tree.JCTree.JCSwitch;
import fr.an.com.sun.tools.javac.tree.JCTree.JCSynchronized;
import fr.an.com.sun.tools.javac.tree.JCTree.JCThrow;
import fr.an.com.sun.tools.javac.tree.JCTree.JCTry;
import fr.an.com.sun.tools.javac.tree.JCTree.JCTypeApply;
import fr.an.com.sun.tools.javac.tree.JCTree.JCTypeCast;
import fr.an.com.sun.tools.javac.tree.JCTree.JCTypeIntersection;
import fr.an.com.sun.tools.javac.tree.JCTree.JCTypeParameter;
import fr.an.com.sun.tools.javac.tree.JCTree.JCTypeUnion;
import fr.an.com.sun.tools.javac.tree.JCTree.JCUnary;
import fr.an.com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import fr.an.com.sun.tools.javac.tree.JCTree.JCWhileLoop;
import fr.an.com.sun.tools.javac.tree.JCTree.JCWildcard;
import fr.an.com.sun.tools.javac.tree.JCTree.LetExpr;
import fr.an.com.sun.tools.javac.tree.JCTree.Tag;
import fr.an.com.sun.tools.javac.tree.JCTree.TypeBoundKind;
import fr.an.com.sun.tools.javac.util.Assert;
import fr.an.com.sun.tools.javac.util.Context;
import fr.an.com.sun.tools.javac.util.List;
import fr.an.com.sun.tools.javac.util.ListBuffer;
import fr.an.com.sun.tools.javac.util.Name;
import fr.an.com.sun.tools.javac.util.Names;
import fr.an.com.sun.tools.javac.util.Pair;
import fr.an.com.sun.tools.javac.util.Position;
import fr.an.com.sun.tools.javac.util.JCDiagnostic.DiagnosticPosition;


/** Factory class for trees.
 *
 *  <p><b>This is NOT part of any supported API.
 *  If you write code that depends on this, you do so at your own risk.
 *  This code and its internal interfaces are subject to change or
 *  deletion without notice.</b>
 */
public class TreeMaker implements JCTree.Factory {

    /** The context key for the tree factory. */
    protected static final Context.Key<TreeMaker> treeMakerKey =
        new Context.Key<TreeMaker>();

    /** Get the TreeMaker instance. */
    public static TreeMaker instance(Context context) {
        TreeMaker instance = context.get(treeMakerKey);
        if (instance == null)
            instance = new TreeMaker(context);
        return instance;
    }

    /** The position at which subsequent trees will be created.
     */
    private int pos = Position.NOPOS;

    
    /**
     * contains both comments and AST-level annotations
     * ==> to push and transform into List<JCTreeExtension>, then peek by prefix name
     * example:
     * @@Annotation1 lhs = rhs;  ... this is ambiguous!  annotation goes to lhs AST node, or assignement?
     * when not specified => annotation is consumed on the first found AST whle parsing!
     * ==> can attach annotation on a sub AST node, by using "@@prefix:"
     * @@Lhs:Annotation1 @@Assign:Annotation2 lhs = rhs;
     * 
     */
    private List<TokenExtension> pushedTokenExtensionsReverseStack = List.nil();
    private Token lastPushTokenExtension;
    private int lastPushedTokenPos;
    
    
    /** The toplevel tree to which created trees belong.
     */
    public JCCompilationUnit toplevel;

    /** The current name table. */
    Names names;

    Types types;

    /** The current symbol table. */
    Symtab syms;

    /** Create a tree maker with null toplevel and NOPOS as initial position.
     */
    protected TreeMaker(Context context) {
        context.put(treeMakerKey, this);
        this.pos = Position.NOPOS;
        this.toplevel = null;
        this.names = Names.instance(context);
        this.syms = Symtab.instance(context);
        this.types = Types.instance(context);
    }

    /** Create a tree maker with a given toplevel and FIRSTPOS as initial position.
     */
    protected TreeMaker(JCCompilationUnit toplevel, Names names, Types types, Symtab syms) {
        this.pos = Position.FIRSTPOS;
        this.toplevel = toplevel;
        this.names = names;
        this.types = types;
        this.syms = syms;
    }

    /** Create a new tree maker for a given toplevel.
     */
    public TreeMaker forToplevel(JCCompilationUnit toplevel) {
        return new TreeMaker(toplevel, names, types, syms);
    }

    /** @return the current position
     */
    public int getPos() {
        return pos;
    }

    /** Reassign current position.
     */
    public TreeMaker at(int pos) {
        this.pos = pos;
        return this;
    }

    /** Reassign current position... used by Parser to preserve position + comments + AST-level annotations while building AST !!
     * replacement for at(pos) !!
     */
    public TreeMaker newAt(int pos) {
        this.pos = pos;
        return this;
    }
    
    /** Reassign current position.
     */
    public TreeMaker at(DiagnosticPosition pos) {
        this.pos = (pos == null ? Position.NOPOS : pos.getStartPosition());
        return this;
    }


    public void pushTokenExtensions(Token token) {
        this.lastPushTokenExtension = token;
        this.lastPushedTokenPos = token.pos;

        List<TokenExtension> extensions = token.getExtensions();
        if (extensions != null && !extensions.isEmpty()) {
            pushedTokenExtensionsReverseStack = pushedTokenExtensionsReverseStack.prependList(extensions); // push elts on top of (reversed)stack = prepend
        }
    }
    
    protected void fillTreeInfo(JCTree tree) {
        tree.pos = pos;
        if (pushedTokenExtensionsReverseStack != null && !pushedTokenExtensionsReverseStack.isEmpty()) {
            Kind treeKind = tree.getKind();
            
            // loop and consume pushed tokenExtensions, create corresponding JCTree info
            // consume from top of (reversed)stack => head first!
            List<TokenExtension> remainingList = pushedTokenExtensionsReverseStack;
            for (List<TokenExtension> l = pushedTokenExtensionsReverseStack; l.tail != null; l = l.tail) { // List.iterator() does not support remove()...
                TokenExtension tokenExt = l.head;
                switch(tokenExt.getTokenExtensionKind()) {
                case COMMENT:
                    // TODO ... comment not used yet in JCTree AST (only in Class,Field,Method..)! ... ignore them
                    // tree.addTreeExtension(treeComment);
                    break;
                    
                case AST_ANNOTATION:
                    AstAnnotationTokenExtension tokenAnnotation = (AstAnnotationTokenExtension) tokenExt;
                    Name annotationTypePrefix = tokenAnnotation.getAnnotationTypePrefix();
                    if (annotationTypePrefix != null) {
                        // check if prefix match expected tree.Tag ... otherwise stop consuming from stack!
                        String prefixAsString = annotationTypePrefix.toString();
                        if (!treeKind.acceptPrefixIgnoreCase(prefixAsString)) {
                            break; // no match.. this token extension should be consumed later! 
                            // warning: when pushing 1 invalid prefix... the stack is filled and no more consumed ... there is push() but no pop() to check for synchronisation 
                        }
                    }
                    
                    // handle AstAnnotationTokenExtension, transform it to JCTree annotation
                    // TODO ... cf similar code in JavacParser.annotation() for parsing JCAnnotation!
                    Name annotationName = tokenAnnotation.getAnnotationName();
                    JCTree annotationType = new JCIdent(annotationName, null); // TODO cf JavacParser.qualident(); 
                    // ... do not call this.Ident(annotationName) as it would cause StackOverflow
                    annotationType.pos = pos;
                    
                    List<JCExpression> annotationArgs = List.nil(); // TODO cf JavacParser
                    
                    JCASTAnnotation treeAnnotation = new JCASTAnnotation(annotationType, annotationArgs);
                    tree.addTreeExtension(treeAnnotation);
                    
                    break;
                }
                remainingList = l.tail; // idem pop() from (reversed)stack : remove head 
            }
            this.pushedTokenExtensionsReverseStack = remainingList;
        }
    }
    
    
    /**
     * Create given tree node at current position.
     * @param defs a list of ClassDef, Import, and Skip
     */
    public JCCompilationUnit TopLevel(List<JCAnnotation> packageAnnotations,
                                      JCExpression pid,
                                      List<JCTree> defs) {
        Assert.checkNonNull(packageAnnotations);
        for (JCTree node : defs)
            Assert.check(node instanceof JCClassDecl
                || node instanceof JCImport
                || node instanceof JCSkip
                || node instanceof JCErroneous
                || (node instanceof JCExpressionStatement
                    && ((JCExpressionStatement)node).expr instanceof JCErroneous),
                node.getClass().getSimpleName());
        JCCompilationUnit tree = new JCCompilationUnit(packageAnnotations, pid, defs,
                                     null, null, null, null);
        fillTreeInfo(tree);
        return tree;
    }

    public JCImport Import(JCTree qualid, boolean importStatic) {
        JCImport tree = new JCImport(qualid, importStatic);
        fillTreeInfo(tree);
        return tree;
    }

    public JCClassDecl ClassDef(JCModifiers mods,
                                Name name,
                                List<JCTypeParameter> typarams,
                                JCExpression extending,
                                List<JCExpression> implementing,
                                List<JCTree> defs)
    {
        JCClassDecl tree = new JCClassDecl(mods,
                                     name,
                                     typarams,
                                     extending,
                                     implementing,
                                     defs,
                                     null);
        fillTreeInfo(tree);
        return tree;
    }

    public JCMethodDecl MethodDef(JCModifiers mods,
                               Name name,
                               JCExpression restype,
                               List<JCTypeParameter> typarams,
                               List<JCVariableDecl> params,
                               List<JCExpression> thrown,
                               JCBlock body,
                               JCExpression defaultValue) {
        return MethodDef(
                mods, name, restype, typarams, null, params,
                thrown, body, defaultValue);
    }

    public JCMethodDecl MethodDef(JCModifiers mods,
                               Name name,
                               JCExpression restype,
                               List<JCTypeParameter> typarams,
                               JCVariableDecl recvparam,
                               List<JCVariableDecl> params,
                               List<JCExpression> thrown,
                               JCBlock body,
                               JCExpression defaultValue)
    {
        JCMethodDecl tree = new JCMethodDecl(mods,
                                       name,
                                       restype,
                                       typarams,
                                       recvparam,
                                       params,
                                       thrown,
                                       body,
                                       defaultValue,
                                       null);
        fillTreeInfo(tree);
        return tree;
    }

    public JCVariableDecl VarDef(JCModifiers mods, Name name, JCExpression vartype, JCExpression init) {
        JCVariableDecl tree = new JCVariableDecl(mods, name, vartype, init, null);
        fillTreeInfo(tree);
        return tree;
    }

    public JCVariableDecl ReceiverVarDef(JCModifiers mods, JCExpression name, JCExpression vartype) {
        JCVariableDecl tree = new JCVariableDecl(mods, name, vartype);
        fillTreeInfo(tree);
        return tree;
    }

    public JCSkip Skip() {
        JCSkip tree = new JCSkip();
        fillTreeInfo(tree);
        return tree;
    }

    public JCBlock Block(long flags, List<JCStatement> stats) {
        JCBlock tree = new JCBlock(flags, stats);
        fillTreeInfo(tree);
        return tree;
    }

    public JCDoWhileLoop DoLoop(JCStatement body, JCExpression cond) {
        JCDoWhileLoop tree = new JCDoWhileLoop(body, cond);
        fillTreeInfo(tree);
        return tree;
    }

    public JCWhileLoop WhileLoop(JCExpression cond, JCStatement body) {
        JCWhileLoop tree = new JCWhileLoop(cond, body);
        fillTreeInfo(tree);
        return tree;
    }

    public JCForLoop ForLoop(List<JCStatement> init,
                           JCExpression cond,
                           List<JCExpressionStatement> step,
                           JCStatement body)
    {
        JCForLoop tree = new JCForLoop(init, cond, step, body);
        fillTreeInfo(tree);
        return tree;
    }

    public JCEnhancedForLoop ForeachLoop(JCVariableDecl var, JCExpression expr, JCStatement body) {
        JCEnhancedForLoop tree = new JCEnhancedForLoop(var, expr, body);
        fillTreeInfo(tree);
        return tree;
    }

    public JCLabeledStatement Labelled(Name label, JCStatement body) {
        JCLabeledStatement tree = new JCLabeledStatement(label, body);
        fillTreeInfo(tree);
        return tree;
    }

    public JCSwitch Switch(JCExpression selector, List<JCCase> cases) {
        JCSwitch tree = new JCSwitch(selector, cases);
        fillTreeInfo(tree);
        return tree;
    }

    public JCCase Case(JCExpression pat, List<JCStatement> stats) {
        JCCase tree = new JCCase(pat, stats);
        fillTreeInfo(tree);
        return tree;
    }

    public JCSynchronized Synchronized(JCExpression lock, JCBlock body) {
        JCSynchronized tree = new JCSynchronized(lock, body);
        fillTreeInfo(tree);
        return tree;
    }

    public JCTry Try(JCBlock body, List<JCCatch> catchers, JCBlock finalizer) {
        return Try(List.<JCTree>nil(), body, catchers, finalizer);
    }

    public JCTry Try(List<JCTree> resources,
                     JCBlock body,
                     List<JCCatch> catchers,
                     JCBlock finalizer) {
        JCTry tree = new JCTry(resources, body, catchers, finalizer);
        fillTreeInfo(tree);
        return tree;
    }

    public JCCatch Catch(JCVariableDecl param, JCBlock body) {
        JCCatch tree = new JCCatch(param, body);
        fillTreeInfo(tree);
        return tree;
    }

    public JCConditional Conditional(JCExpression cond,
                                   JCExpression thenpart,
                                   JCExpression elsepart)
    {
        JCConditional tree = new JCConditional(cond, thenpart, elsepart);
        fillTreeInfo(tree);
        return tree;
    }

    public JCIf If(JCExpression cond, JCStatement thenpart, JCStatement elsepart) {
        JCIf tree = new JCIf(cond, thenpart, elsepart);
        fillTreeInfo(tree);
        return tree;
    }

    public JCExpressionStatement Exec(JCExpression expr) {
        JCExpressionStatement tree = new JCExpressionStatement(expr);
        fillTreeInfo(tree);
        return tree;
    }

    public JCBreak Break(Name label) {
        JCBreak tree = new JCBreak(label, null);
        fillTreeInfo(tree);
        return tree;
    }

    public JCContinue Continue(Name label) {
        JCContinue tree = new JCContinue(label, null);
        fillTreeInfo(tree);
        return tree;
    }

    public JCReturn Return(JCExpression expr) {
        JCReturn tree = new JCReturn(expr);
        fillTreeInfo(tree);
        return tree;
    }

    public JCThrow Throw(JCExpression expr) {
        JCThrow tree = new JCThrow(expr);
        fillTreeInfo(tree);
        return tree;
    }

    public JCAssert Assert(JCExpression cond, JCExpression detail) {
        JCAssert tree = new JCAssert(cond, detail);
        fillTreeInfo(tree);
        return tree;
    }

    public JCMethodInvocation Apply(List<JCExpression> typeargs,
                       JCExpression fn,
                       List<JCExpression> args)
    {
        JCMethodInvocation tree = new JCMethodInvocation(typeargs, fn, args);
        fillTreeInfo(tree);
        return tree;
    }

    public JCNewClass NewClass(JCExpression encl,
                             List<JCExpression> typeargs,
                             JCExpression clazz,
                             List<JCExpression> args,
                             JCClassDecl def)
    {
        JCNewClass tree = new JCNewClass(encl, typeargs, clazz, args, def);
        fillTreeInfo(tree);
        return tree;
    }

    public JCNewArray NewArray(JCExpression elemtype,
                             List<JCExpression> dims,
                             List<JCExpression> elems)
    {
        JCNewArray tree = new JCNewArray(elemtype, dims, elems);
        fillTreeInfo(tree);
        return tree;
    }

    public JCLambda Lambda(List<JCVariableDecl> params,
                           JCTree body)
    {
        JCLambda tree = new JCLambda(params, body);
        fillTreeInfo(tree);
        return tree;
    }

    public JCParens Parens(JCExpression expr) {
        JCParens tree = new JCParens(expr);
        fillTreeInfo(tree);
        return tree;
    }

    public JCAssign Assign(JCExpression lhs, JCExpression rhs) {
        JCAssign tree = new JCAssign(lhs, rhs);
        fillTreeInfo(tree);
        return tree;
    }

    public JCAssignOp Assignop(JCTree.Tag opcode, JCTree lhs, JCTree rhs) {
        JCAssignOp tree = new JCAssignOp(opcode, lhs, rhs, null);
        fillTreeInfo(tree);
        return tree;
    }

    public JCUnary Unary(JCTree.Tag opcode, JCExpression arg) {
        JCUnary tree = new JCUnary(opcode, arg);
        fillTreeInfo(tree);
        return tree;
    }

    public JCBinary Binary(JCTree.Tag opcode, JCExpression lhs, JCExpression rhs) {
        JCBinary tree = new JCBinary(opcode, lhs, rhs, null);
        fillTreeInfo(tree);
        return tree;
    }

    public JCTypeCast TypeCast(JCTree clazz, JCExpression expr) {
        JCTypeCast tree = new JCTypeCast(clazz, expr);
        fillTreeInfo(tree);
        return tree;
    }

    public JCInstanceOf TypeTest(JCExpression expr, JCTree clazz) {
        JCInstanceOf tree = new JCInstanceOf(expr, clazz);
        fillTreeInfo(tree);
        return tree;
    }

    public JCArrayAccess Indexed(JCExpression indexed, JCExpression index) {
        JCArrayAccess tree = new JCArrayAccess(indexed, index);
        fillTreeInfo(tree);
        return tree;
    }

    public JCFieldAccess Select(JCExpression selected, Name selector) {
        JCFieldAccess tree = new JCFieldAccess(selected, selector, null);
        fillTreeInfo(tree);
        return tree;
    }

    public JCMemberReference Reference(JCMemberReference.ReferenceMode mode, Name name,
            JCExpression expr, List<JCExpression> typeargs) {
        JCMemberReference tree = new JCMemberReference(mode, name, expr, typeargs);
        fillTreeInfo(tree);
        return tree;
    }

    public JCIdent Ident(Name name) {
        JCIdent tree = new JCIdent(name, null);
        fillTreeInfo(tree);
        return tree;
    }

    public JCLiteral Literal(TypeTag tag, Object value) {
        JCLiteral tree = new JCLiteral(tag, value);
        fillTreeInfo(tree);
        return tree;
    }

    public JCPrimitiveTypeTree TypeIdent(TypeTag typetag) {
        JCPrimitiveTypeTree tree = new JCPrimitiveTypeTree(typetag);
        fillTreeInfo(tree);
        return tree;
    }

    public JCArrayTypeTree TypeArray(JCExpression elemtype) {
        JCArrayTypeTree tree = new JCArrayTypeTree(elemtype);
        fillTreeInfo(tree);
        return tree;
    }

    public JCTypeApply TypeApply(JCExpression clazz, List<JCExpression> arguments) {
        JCTypeApply tree = new JCTypeApply(clazz, arguments);
        fillTreeInfo(tree);
        return tree;
    }

    public JCTypeUnion TypeUnion(List<JCExpression> components) {
        JCTypeUnion tree = new JCTypeUnion(components);
        fillTreeInfo(tree);
        return tree;
    }

    public JCTypeIntersection TypeIntersection(List<JCExpression> components) {
        JCTypeIntersection tree = new JCTypeIntersection(components);
        fillTreeInfo(tree);
        return tree;
    }

    public JCTypeParameter TypeParameter(Name name, List<JCExpression> bounds) {
        return TypeParameter(name, bounds, List.<JCAnnotation>nil());
    }

    public JCTypeParameter TypeParameter(Name name, List<JCExpression> bounds, List<JCAnnotation> annos) {
        JCTypeParameter tree = new JCTypeParameter(name, bounds, annos);
        fillTreeInfo(tree);
        return tree;
    }

    public JCWildcard Wildcard(TypeBoundKind kind, JCTree type) {
        JCWildcard tree = new JCWildcard(kind, type);
        fillTreeInfo(tree);
        return tree;
    }

    public TypeBoundKind TypeBoundKind(BoundKind kind) {
        TypeBoundKind tree = new TypeBoundKind(kind);
        fillTreeInfo(tree);
        return tree;
    }

    public JCASTAnnotation ASTAnnotation(JCTree annotationType, List<JCExpression> args) {
        JCASTAnnotation tree = new JCASTAnnotation(annotationType, args);
        // no annotation of annotation !: only position  fillTreePosAndPopTokenExtensions(tree);
        tree.pos = pos;
        return tree;
    }

    public JCAnnotation Annotation(JCTree annotationType, List<JCExpression> args) {
        JCAnnotation tree = new JCAnnotation(Tag.ANNOTATION, annotationType, args);
        fillTreeInfo(tree);
        return tree;
    }

    public JCAnnotation TypeAnnotation(JCTree annotationType, List<JCExpression> args) {
        JCAnnotation tree = new JCAnnotation(Tag.TYPE_ANNOTATION, annotationType, args);
        fillTreeInfo(tree);
        return tree;
    }

    public JCModifiers Modifiers(long flags, List<JCAnnotation> annotations) {
        JCModifiers tree = new JCModifiers(flags, annotations);
        boolean noFlags = (flags & (Flags.ModifierFlags | Flags.ANNOTATION)) == 0;
        // temporary change pos
        int prevPos = this.pos;
        this.pos = (noFlags && annotations.isEmpty()) ? Position.NOPOS : pos;
        fillTreeInfo(tree);
        this.pos = prevPos;
        return tree;
    }

    public JCModifiers Modifiers(long flags) {
        return Modifiers(flags, List.<JCAnnotation>nil());
    }

    public JCAnnotatedType AnnotatedType(List<JCAnnotation> annotations, JCExpression underlyingType) {
        JCAnnotatedType tree = new JCAnnotatedType(annotations, underlyingType);
        fillTreeInfo(tree);
        return tree;
    }

    public JCErroneous Erroneous() {
        return Erroneous(List.<JCTree>nil());
    }

    public JCErroneous Erroneous(List<? extends JCTree> errs) {
        JCErroneous tree = new JCErroneous(errs);
        fillTreeInfo(tree);
        return tree;
    }

    public LetExpr LetExpr(List<JCVariableDecl> defs, JCTree expr) {
        LetExpr tree = new LetExpr(defs, expr);
        fillTreeInfo(tree);
        return tree;
    }

/* ***************************************************************************
 * Derived building blocks.
 ****************************************************************************/

    public JCClassDecl AnonymousClassDef(JCModifiers mods,
                                         List<JCTree> defs)
    {
        return ClassDef(mods,
                        names.empty,
                        List.<JCTypeParameter>nil(),
                        null,
                        List.<JCExpression>nil(),
                        defs);
    }

    public LetExpr LetExpr(JCVariableDecl def, JCTree expr) {
        LetExpr tree = new LetExpr(List.of(def), expr);
        fillTreeInfo(tree);
        return tree;
    }

    /** Create an identifier from a symbol.
     */
    public JCIdent Ident(Symbol sym) {
        return (JCIdent)new JCIdent((sym.name != names.empty)
                                ? sym.name
                                : sym.flatName(), sym)
            .setPos(pos)
            .setType(sym.type);
    }

    /** Create a selection node from a qualifier tree and a symbol.
     *  @param base   The qualifier tree.
     */
    public JCExpression Select(JCExpression base, Symbol sym) {
        return new JCFieldAccess(base, sym.name, sym).setPos(pos).setType(sym.type);
    }

    /** Create a qualified identifier from a symbol, adding enough qualifications
     *  to make the reference unique.
     */
    public JCExpression QualIdent(Symbol sym) {
        return isUnqualifiable(sym)
            ? Ident(sym)
            : Select(QualIdent(sym.owner), sym);
    }

    /** Create an identifier that refers to the variable declared in given variable
     *  declaration.
     */
    public JCExpression Ident(JCVariableDecl param) {
        return Ident(param.sym);
    }

    /** Create a list of identifiers referring to the variables declared
     *  in given list of variable declarations.
     */
    public List<JCExpression> Idents(List<JCVariableDecl> params) {
        ListBuffer<JCExpression> ids = new ListBuffer<JCExpression>();
        for (List<JCVariableDecl> l = params; l.nonEmpty(); l = l.tail)
            ids.append(Ident(l.head));
        return ids.toList();
    }

    /** Create a tree representing `this', given its type.
     */
    public JCExpression This(Type t) {
        return Ident(new VarSymbol(FINAL, names._this, t, t.tsym));
    }

    /** Create a tree representing a class literal.
     */
    public JCExpression ClassLiteral(ClassSymbol clazz) {
        return ClassLiteral(clazz.type);
    }

    /** Create a tree representing a class literal.
     */
    public JCExpression ClassLiteral(Type t) {
        VarSymbol lit = new VarSymbol(STATIC | PUBLIC | FINAL,
                                      names._class,
                                      t,
                                      t.tsym);
        return Select(Type(t), lit);
    }

    /** Create a tree representing `super', given its type and owner.
     */
    public JCIdent Super(Type t, TypeSymbol owner) {
        return Ident(new VarSymbol(FINAL, names._super, t, owner));
    }

    /**
     * Create a method invocation from a method tree and a list of
     * argument trees.
     */
    public JCMethodInvocation App(JCExpression meth, List<JCExpression> args) {
        return Apply(null, meth, args).setType(meth.type.getReturnType());
    }

    /**
     * Create a no-arg method invocation from a method tree
     */
    public JCMethodInvocation App(JCExpression meth) {
        return Apply(null, meth, List.<JCExpression>nil()).setType(meth.type.getReturnType());
    }

    /** Create a method invocation from a method tree and a list of argument trees.
     */
    public JCExpression Create(Symbol ctor, List<JCExpression> args) {
        Type t = ctor.owner.erasure(types);
        JCNewClass newclass = NewClass(null, null, Type(t), args, null);
        newclass.constructor = ctor;
        newclass.setType(t);
        return newclass;
    }

    /** Create a tree representing given type.
     */
    public JCExpression Type(Type t) {
        if (t == null) return null;
        JCExpression tp;
        switch (t.getTag()) {
        case BYTE: case CHAR: case SHORT: case INT: case LONG: case FLOAT:
        case DOUBLE: case BOOLEAN: case VOID:
            tp = TypeIdent(t.getTag());
            break;
        case TYPEVAR:
            tp = Ident(t.tsym);
            break;
        case WILDCARD: {
            WildcardType a = ((WildcardType) t);
            tp = Wildcard(TypeBoundKind(a.kind), Type(a.type));
            break;
        }
        case CLASS:
            Type outer = t.getEnclosingType();
            JCExpression clazz = outer.hasTag(CLASS) && t.tsym.owner.kind == TYP
                ? Select(Type(outer), t.tsym)
                : QualIdent(t.tsym);
            tp = t.getTypeArguments().isEmpty()
                ? clazz
                : TypeApply(clazz, Types(t.getTypeArguments()));
            break;
        case ARRAY:
            tp = TypeArray(Type(types.elemtype(t)));
            break;
        case ERROR:
            tp = TypeIdent(ERROR);
            break;
        default:
            throw new AssertionError("unexpected type: " + t);
        }
        return tp.setType(t);
    }

    /** Create a list of trees representing given list of types.
     */
    public List<JCExpression> Types(List<Type> ts) {
        ListBuffer<JCExpression> lb = new ListBuffer<JCExpression>();
        for (List<Type> l = ts; l.nonEmpty(); l = l.tail)
            lb.append(Type(l.head));
        return lb.toList();
    }

    /** Create a variable definition from a variable symbol and an initializer
     *  expression.
     */
    public JCVariableDecl VarDef(VarSymbol v, JCExpression init) {
        return (JCVariableDecl)
            new JCVariableDecl(
                Modifiers(v.flags(), Annotations(v.getRawAttributes())),
                v.name,
                Type(v.type),
                init,
                v).setPos(pos).setType(v.type);
    }

    /** Create annotation trees from annotations.
     */
    public List<JCAnnotation> Annotations(List<Attribute.Compound> attributes) {
        if (attributes == null) return List.nil();
        ListBuffer<JCAnnotation> result = new ListBuffer<JCAnnotation>();
        for (List<Attribute.Compound> i = attributes; i.nonEmpty(); i=i.tail) {
            Attribute a = i.head;
            result.append(Annotation(a));
        }
        return result.toList();
    }

    public JCLiteral Literal(Object value) {
        JCLiteral result = null;
        if (value instanceof String) {
            result = Literal(CLASS, value).
                setType(syms.stringType.constType(value));
        } else if (value instanceof Integer) {
            result = Literal(INT, value).
                setType(syms.intType.constType(value));
        } else if (value instanceof Long) {
            result = Literal(LONG, value).
                setType(syms.longType.constType(value));
        } else if (value instanceof Byte) {
            result = Literal(BYTE, value).
                setType(syms.byteType.constType(value));
        } else if (value instanceof Character) {
            int v = (int) (((Character) value).toString().charAt(0));
            result = Literal(CHAR, value).
                setType(syms.charType.constType(v));
        } else if (value instanceof Double) {
            result = Literal(DOUBLE, value).
                setType(syms.doubleType.constType(value));
        } else if (value instanceof Float) {
            result = Literal(FLOAT, value).
                setType(syms.floatType.constType(value));
        } else if (value instanceof Short) {
            result = Literal(SHORT, value).
                setType(syms.shortType.constType(value));
        } else if (value instanceof Boolean) {
            int v = ((Boolean) value) ? 1 : 0;
            result = Literal(BOOLEAN, v).
                setType(syms.booleanType.constType(v));
        } else {
            throw new AssertionError(value);
        }
        return result;
    }

    class AnnotationBuilder implements Attribute.Visitor {
        JCExpression result = null;
        public void visitConstant(Attribute.Constant v) {
            result = Literal(v.type.getTag(), v.value);
        }
        public void visitClass(Attribute.Class clazz) {
            result = ClassLiteral(clazz.classType).setType(syms.classType);
        }
        public void visitEnum(Attribute.Enum e) {
            result = QualIdent(e.value);
        }
        public void visitError(Attribute.Error e) {
            result = Erroneous();
        }
        public void visitCompound(Attribute.Compound compound) {
            if (compound instanceof Attribute.TypeCompound) {
                result = visitTypeCompoundInternal((Attribute.TypeCompound) compound);
            } else {
                result = visitCompoundInternal(compound);
            }
        }
        public JCAnnotation visitCompoundInternal(Attribute.Compound compound) {
            ListBuffer<JCExpression> args = new ListBuffer<JCExpression>();
            for (List<Pair<Symbol.MethodSymbol,Attribute>> values = compound.values; values.nonEmpty(); values=values.tail) {
                Pair<MethodSymbol,Attribute> pair = values.head;
                JCExpression valueTree = translate(pair.snd);
                args.append(Assign(Ident(pair.fst), valueTree).setType(valueTree.type));
            }
            return Annotation(Type(compound.type), args.toList());
        }
        public JCAnnotation visitTypeCompoundInternal(Attribute.TypeCompound compound) {
            ListBuffer<JCExpression> args = new ListBuffer<JCExpression>();
            for (List<Pair<Symbol.MethodSymbol,Attribute>> values = compound.values; values.nonEmpty(); values=values.tail) {
                Pair<MethodSymbol,Attribute> pair = values.head;
                JCExpression valueTree = translate(pair.snd);
                args.append(Assign(Ident(pair.fst), valueTree).setType(valueTree.type));
            }
            return TypeAnnotation(Type(compound.type), args.toList());
        }
        public void visitArray(Attribute.Array array) {
            ListBuffer<JCExpression> elems = new ListBuffer<JCExpression>();
            for (int i = 0; i < array.values.length; i++)
                elems.append(translate(array.values[i]));
            result = NewArray(null, List.<JCExpression>nil(), elems.toList()).setType(array.type);
        }
        JCExpression translate(Attribute a) {
            a.accept(this);
            return result;
        }
        JCAnnotation translate(Attribute.Compound a) {
            return visitCompoundInternal(a);
        }
        JCAnnotation translate(Attribute.TypeCompound a) {
            return visitTypeCompoundInternal(a);
        }
    }

    AnnotationBuilder annotationBuilder = new AnnotationBuilder();

    /** Create an annotation tree from an attribute.
     */
    public JCAnnotation Annotation(Attribute a) {
        return annotationBuilder.translate((Attribute.Compound)a);
    }

    public JCAnnotation TypeAnnotation(Attribute a) {
        return annotationBuilder.translate((Attribute.TypeCompound) a);
    }

    /** Create a method definition from a method symbol and a method body.
     */
    public JCMethodDecl MethodDef(MethodSymbol m, JCBlock body) {
        return MethodDef(m, m.type, body);
    }

    /** Create a method definition from a method symbol, method type
     *  and a method body.
     */
    public JCMethodDecl MethodDef(MethodSymbol m, Type mtype, JCBlock body) {
        return (JCMethodDecl)
            new JCMethodDecl(
                Modifiers(m.flags(), Annotations(m.getRawAttributes())),
                m.name,
                Type(mtype.getReturnType()),
                TypeParams(mtype.getTypeArguments()),
                null, // receiver type
                Params(mtype.getParameterTypes(), m),
                Types(mtype.getThrownTypes()),
                body,
                null,
                m).setPos(pos).setType(mtype);
    }

    /** Create a type parameter tree from its name and type.
     */
    public JCTypeParameter TypeParam(Name name, TypeVar tvar) {
        return (JCTypeParameter)
            TypeParameter(name, Types(types.getBounds(tvar))).setPos(pos).setType(tvar);
    }

    /** Create a list of type parameter trees from a list of type variables.
     */
    public List<JCTypeParameter> TypeParams(List<Type> typarams) {
        ListBuffer<JCTypeParameter> tparams = new ListBuffer<JCTypeParameter>();
        for (List<Type> l = typarams; l.nonEmpty(); l = l.tail)
            tparams.append(TypeParam(l.head.tsym.name, (TypeVar)l.head));
        return tparams.toList();
    }

    /** Create a value parameter tree from its name, type, and owner.
     */
    public JCVariableDecl Param(Name name, Type argtype, Symbol owner) {
        return VarDef(new VarSymbol(0, name, argtype, owner), null);
    }

    /** Create a a list of value parameter trees x0, ..., xn from a list of
     *  their types and an their owner.
     */
    public List<JCVariableDecl> Params(List<Type> argtypes, Symbol owner) {
        ListBuffer<JCVariableDecl> params = new ListBuffer<JCVariableDecl>();
        MethodSymbol mth = (owner.kind == MTH) ? ((MethodSymbol)owner) : null;
        if (mth != null && mth.params != null && argtypes.length() == mth.params.length()) {
            for (VarSymbol param : ((MethodSymbol)owner).params)
                params.append(VarDef(param, null));
        } else {
            int i = 0;
            for (List<Type> l = argtypes; l.nonEmpty(); l = l.tail)
                params.append(Param(paramName(i++), l.head, owner));
        }
        return params.toList();
    }

    /** Wrap a method invocation in an expression statement or return statement,
     *  depending on whether the method invocation expression's type is void.
     */
    public JCStatement Call(JCExpression apply) {
        return apply.type.hasTag(VOID) ? Exec(apply) : Return(apply);
    }

    /** Construct an assignment from a variable symbol and a right hand side.
     */
    public JCStatement Assignment(Symbol v, JCExpression rhs) {
        return Exec(Assign(Ident(v), rhs).setType(v.type));
    }

    /** Construct an index expression from a variable and an expression.
     */
    public JCArrayAccess Indexed(Symbol v, JCExpression index) {
        JCArrayAccess tree = new JCArrayAccess(QualIdent(v), index);
        tree.type = ((ArrayType)v.type).elemtype;
        return tree;
    }

    /** Make an attributed type cast expression.
     */
    public JCTypeCast TypeCast(Type type, JCExpression expr) {
        return (JCTypeCast)TypeCast(Type(type), expr).setType(type);
    }

/* ***************************************************************************
 * Helper methods.
 ****************************************************************************/

    /** Can given symbol be referred to in unqualified form?
     */
    boolean isUnqualifiable(Symbol sym) {
        if (sym.name == names.empty ||
            sym.owner == null ||
            sym.owner.kind == MTH || sym.owner.kind == VAR) {
            return true;
        } else if (sym.kind == TYP && toplevel != null) {
            Scope.Entry e;
            e = toplevel.namedImportScope.lookup(sym.name);
            if (e.scope != null) {
                return
                  e.sym == sym &&
                  e.next().scope == null;
            }
            e = toplevel.packge.members().lookup(sym.name);
            if (e.scope != null) {
                return
                  e.sym == sym &&
                  e.next().scope == null;
            }
            e = toplevel.starImportScope.lookup(sym.name);
            if (e.scope != null) {
                return
                  e.sym == sym &&
                  e.next().scope == null;
            }
        }
        return false;
    }

    /** The name of synthetic parameter number `i'.
     */
    public Name paramName(int i)   { return names.fromString("x" + i); }

    /** The name of synthetic type parameter number `i'.
     */
    public Name typaramName(int i) { return names.fromString("A" + i); }
}
