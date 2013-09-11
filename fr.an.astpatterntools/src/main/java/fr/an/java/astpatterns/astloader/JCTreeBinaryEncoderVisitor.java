package fr.an.java.astpatterns.astloader;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCAnnotatedType;
import com.sun.tools.javac.tree.JCTree.JCAnnotation;
import com.sun.tools.javac.tree.JCTree.JCArrayAccess;
import com.sun.tools.javac.tree.JCTree.JCArrayTypeTree;
import com.sun.tools.javac.tree.JCTree.JCAssert;
import com.sun.tools.javac.tree.JCTree.JCAssign;
import com.sun.tools.javac.tree.JCTree.JCAssignOp;
import com.sun.tools.javac.tree.JCTree.JCBinary;
import com.sun.tools.javac.tree.JCTree.JCBlock;
import com.sun.tools.javac.tree.JCTree.JCBreak;
import com.sun.tools.javac.tree.JCTree.JCCase;
import com.sun.tools.javac.tree.JCTree.JCCatch;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCConditional;
import com.sun.tools.javac.tree.JCTree.JCContinue;
import com.sun.tools.javac.tree.JCTree.JCDoWhileLoop;
import com.sun.tools.javac.tree.JCTree.JCEnhancedForLoop;
import com.sun.tools.javac.tree.JCTree.JCErroneous;
import com.sun.tools.javac.tree.JCTree.JCExpressionStatement;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCForLoop;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.tree.JCTree.JCIf;
import com.sun.tools.javac.tree.JCTree.JCImport;
import com.sun.tools.javac.tree.JCTree.JCInstanceOf;
import com.sun.tools.javac.tree.JCTree.JCLabeledStatement;
import com.sun.tools.javac.tree.JCTree.JCLambda;
import com.sun.tools.javac.tree.JCTree.JCLiteral;
import com.sun.tools.javac.tree.JCTree.JCMemberReference;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;
import com.sun.tools.javac.tree.JCTree.JCModifiers;
import com.sun.tools.javac.tree.JCTree.JCNewArray;
import com.sun.tools.javac.tree.JCTree.JCNewClass;
import com.sun.tools.javac.tree.JCTree.JCParens;
import com.sun.tools.javac.tree.JCTree.JCPrimitiveTypeTree;
import com.sun.tools.javac.tree.JCTree.JCReturn;
import com.sun.tools.javac.tree.JCTree.JCSkip;
import com.sun.tools.javac.tree.JCTree.JCSwitch;
import com.sun.tools.javac.tree.JCTree.JCSynchronized;
import com.sun.tools.javac.tree.JCTree.JCThrow;
import com.sun.tools.javac.tree.JCTree.JCTry;
import com.sun.tools.javac.tree.JCTree.JCTypeApply;
import com.sun.tools.javac.tree.JCTree.JCTypeCast;
import com.sun.tools.javac.tree.JCTree.JCTypeIntersection;
import com.sun.tools.javac.tree.JCTree.JCTypeParameter;
import com.sun.tools.javac.tree.JCTree.JCTypeUnion;
import com.sun.tools.javac.tree.JCTree.JCUnary;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.tree.JCTree.JCWhileLoop;
import com.sun.tools.javac.tree.JCTree.JCWildcard;
import com.sun.tools.javac.tree.JCTree.LetExpr;
import com.sun.tools.javac.tree.JCTree.TypeBoundKind;
import com.sun.tools.javac.util.Assert;
import com.sun.tools.javac.util.List;

import fr.an.util.encoder.varlength.BitOutputStreamEncoder;

public class JCTreeBinaryEncoderVisitor extends com.sun.tools.javac.tree.JCTree.Visitor {

    protected static final int MAX_COUNT = 1024;
    
    protected BitOutputStreamEncoder bitsEncoder;
    
    
    
    
    // ------------------------------------------------------------------------

    public JCTreeBinaryEncoderVisitor(BitOutputStreamEncoder bitsEncoder) {
        this.bitsEncoder = bitsEncoder;
    }

    // ------------------------------------------------------------------------
    
    protected void encodeUInt(int value, int maxValue) {
        bitsEncoder.writeUInt(value, maxValue);
    }

    
    // TODO... should replace by encode(JCTreeFieldInfo fieldInfo, JCTree tree)
    protected void encode(JCTree tree) {
        // TODO... encode tree class
        if (tree != null) {
            tree.accept(this);
        }
    }
    
    protected void encode(com.sun.tools.javac.tree.JCTreeFieldInfo ctx, JCTree tree) {
        // TODO... encode tree class
        if (tree != null) {
            tree.accept(this);
        }
    }
    
    protected <T extends JCTree> void encode(List<T> treeList) {
        if (treeList != null) {
            encodeUInt(treeList.size(), MAX_COUNT);
            for(T tree : treeList) {
                encode(tree);
            }
        }
    }
    
    // ------------------------------------------------------------------------
    
    public void visitTopLevel(JCCompilationUnit tree) {
        encode(tree.packageAnnotations);
        encode(tree.pid);
        encode(tree.defs);
    }

    public void visitImport(JCImport tree) {
        encode(tree.qualid);
    }

    public void visitClassDef(JCClassDecl tree) {
        encode(tree.mods);
        encode(tree.typarams);
        encode(tree.extending);
        encode(tree.implementing);
        encode(tree.defs);
    }

    public void visitMethodDef(JCMethodDecl tree) {
        encode(tree.mods);
        encode(tree.restype);
        encode(tree.typarams);
        encode(tree.recvparam);
        encode(tree.params);
        encode(tree.thrown);
        encode(tree.defaultValue);
        encode(tree.body);
    }

    public void visitVarDef(JCVariableDecl tree) {
        encode(tree.mods);
        encode(tree.vartype);
        encode(tree.nameexpr);
        encode(tree.init);
    }

    public void visitSkip(JCSkip tree) {
    }

    public void visitBlock(JCBlock tree) {
        encode(tree.stats);
    }

    public void visitDoLoop(JCDoWhileLoop tree) {
        encode(tree.body);
        encode(tree.cond);
    }

    public void visitWhileLoop(JCWhileLoop tree) {
        encode(tree.cond);
        encode(tree.body);
    }

    public void visitForLoop(JCForLoop tree) {
        encode(tree.init);
        encode(tree.cond);
        encode(tree.step);
        encode(tree.body);
    }

    public void visitForeachLoop(JCEnhancedForLoop tree) {
        encode(tree.var);
        encode(tree.expr);
        encode(tree.body);
    }

    public void visitLabelled(JCLabeledStatement tree) {
        encode(tree.body);
    }

    public void visitSwitch(JCSwitch tree) {
        encode(tree.selector);
        encode(tree.cases);
    }

    public void visitCase(JCCase tree) {
        encode(tree.pat);
        encode(tree.stats);
    }

    public void visitSynchronized(JCSynchronized tree) {
        encode(tree.lock);
        encode(tree.body);
    }

    public void visitTry(JCTry tree) {
        encode(tree.resources);
        encode(tree.body);
        encode(tree.catchers);
        encode(tree.finalizer);
    }

    public void visitCatch(JCCatch tree) {
        encode(tree.param);
        encode(tree.body);
    }

    public void visitConditional(JCConditional tree) {
        encode(tree.cond);
        encode(tree.truepart);
        encode(tree.falsepart);
    }

    public void visitIf(JCIf tree) {
        encode(tree.cond);
        encode(tree.thenpart);
        encode(tree.elsepart);
    }

    public void visitExec(JCExpressionStatement tree) {
        encode(tree.expr);
    }

    public void visitBreak(JCBreak tree) {
    }

    public void visitContinue(JCContinue tree) {
    }

    public void visitReturn(JCReturn tree) {
        encode(tree.expr);
    }

    public void visitThrow(JCThrow tree) {
        encode(tree.expr);
    }

    public void visitAssert(JCAssert tree) {
        encode(tree.cond);
        encode(tree.detail);
    }

    public void visitApply(JCMethodInvocation tree) {
        encode(tree.typeargs);
        encode(tree.meth);
        encode(tree.args);
    }

    public void visitNewClass(JCNewClass tree) {
        encode(tree.encl);
        encode(tree.typeargs);
        encode(tree.clazz);
        encode(tree.args);
        encode(tree.def);
    }

    public void visitNewArray(JCNewArray tree) {
        encode(tree.annotations);
        encode(tree.elemtype);
        encode(tree.dims);
        for (List<JCAnnotation> annos : tree.dimAnnotations)
            encode(annos);
        encode(tree.elems);
    }

    public void visitLambda(JCLambda tree) {
        encode(tree.body);
        encode(tree.params);
    }

    public void visitParens(JCParens tree) {
        encode(tree.expr);
    }

    public void visitAssign(JCAssign tree) {
        encode(tree.lhs);
        encode(tree.rhs);
    }

    public void visitAssignop(JCAssignOp tree) {
        encode(tree.lhs);
        encode(tree.rhs);
    }

    public void visitUnary(JCUnary tree) {
        encode(tree.arg);
    }

    public void visitBinary(JCBinary tree) {
        encode(tree.lhs);
        encode(tree.rhs);
    }

    public void visitTypeCast(JCTypeCast tree) {
        encode(tree.clazz);
        encode(tree.expr);
    }

    public void visitTypeTest(JCInstanceOf tree) {
        encode(tree.expr);
        encode(tree.clazz);
    }

    public void visitIndexed(JCArrayAccess tree) {
        encode(tree.indexed);
        encode(tree.index);
    }

    public void visitSelect(JCFieldAccess tree) {
        encode(tree.selected);
    }

    public void visitReference(JCMemberReference tree) {
        encode(tree.expr);
        encode(tree.typeargs);
    }

    public void visitIdent(JCIdent tree) {
    }

    public void visitLiteral(JCLiteral tree) {
    }

    public void visitTypeIdent(JCPrimitiveTypeTree tree) {
    }

    public void visitTypeArray(JCArrayTypeTree tree) {
        encode(tree.elemtype);
    }

    public void visitTypeApply(JCTypeApply tree) {
        encode(tree.clazz);
        encode(tree.arguments);
    }

    public void visitTypeUnion(JCTypeUnion tree) {
        encode(tree.alternatives);
    }

    public void visitTypeIntersection(JCTypeIntersection tree) {
        encode(tree.bounds);
    }

    public void visitTypeParameter(JCTypeParameter tree) {
        encode(tree.annotations);
        encode(tree.bounds);
    }

    @Override
    public void visitWildcard(JCWildcard tree) {
        encode(tree.kind);
        if (tree.inner != null)
            encode(tree.inner);
    }

    @Override
    public void visitTypeBoundKind(TypeBoundKind that) {
    }

    public void visitModifiers(JCModifiers tree) {
        encode(tree.annotations);
    }

    public void visitAnnotation(JCAnnotation tree) {
        encode(tree.annotationType);
        encode(tree.args);
    }

    public void visitAnnotatedType(JCAnnotatedType tree) {
        encode(tree.annotations);
        encode(tree.underlyingType);
    }

    public void visitErroneous(JCErroneous tree) {
    }

    public void visitLetExpr(LetExpr tree) {
        encode(tree.defs);
        encode(tree.expr);
    }

    public void visitTree(JCTree tree) {
        Assert.error();
    }

    
}
