package fr.an.com.sun.tools.javac.tree;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.tools.JavaFileObject;

import fr.an.com.sun.source.tree.MemberReferenceTree.ReferenceMode;
import fr.an.com.sun.tools.javac.code.Attribute;
import fr.an.com.sun.tools.javac.code.BoundKind;
import fr.an.com.sun.tools.javac.code.Symbol;
import fr.an.com.sun.tools.javac.code.Type;
import fr.an.com.sun.tools.javac.code.TypeTag;
import fr.an.com.sun.tools.javac.code.Scope.ImportScope;
import fr.an.com.sun.tools.javac.code.Scope.StarImportScope;
import fr.an.com.sun.tools.javac.code.Symbol.ClassSymbol;
import fr.an.com.sun.tools.javac.code.Symbol.MethodSymbol;
import fr.an.com.sun.tools.javac.code.Symbol.PackageSymbol;
import fr.an.com.sun.tools.javac.code.Symbol.VarSymbol;
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
import fr.an.com.sun.tools.javac.tree.JCTree.JCFunctionalExpression;
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
import fr.an.com.sun.tools.javac.tree.JCTree.JCPolyExpression;
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
import fr.an.com.sun.tools.javac.tree.JCTree.JCLambda.ParameterKind;
import fr.an.com.sun.tools.javac.tree.JCTree.JCMemberReference.ReferenceKind;
import fr.an.com.sun.tools.javac.tree.JCTree.JCPolyExpression.PolyKind;
import fr.an.com.sun.tools.javac.util.List;
import fr.an.com.sun.tools.javac.util.Name;
import fr.an.com.sun.tools.javac.util.Position;

/**
 * facade for meta-class describing JCTree class hierarchy
 */
public abstract class JCTreeInfo<T extends JCTree> {
 
    private static Map<Class<JCTree>,JCTreeClassInfo<? extends JCTree>> classInfoMap = new HashMap<Class<JCTree>,JCTreeClassInfo<? extends JCTree>>(); 
    
    // NOTE: params String name, JCTree.Tag jcTreeTag ... could be set from @ClassInfo annotation on JCTree classes...
    public static <T extends JCTree> JCTreeClassInfo<T> classInfoOf(Class<T> owner, String name, JCTree.Tag jcTreeTag) {
        return classInfoOf(owner, name, jcTreeTag, null);
    }
    public static <T extends JCTree> JCTreeClassInfo<T> classInfoOf(Class<T> owner, String name, JCTree.Tag[] jcTreeOpTags) {
        return classInfoOf(owner, name, null, jcTreeOpTags);
    }
    
    @SuppressWarnings("unchecked")
    private static <T extends JCTree> JCTreeClassInfo<T> classInfoOf(Class<T> owner, String name, JCTree.Tag jcTreeTag, JCTree.Tag[] jcTreeOpTags) {
        JCTreeClassInfo<T> res = (JCTreeClassInfo<T>) classInfoMap.get(owner);
        if (res == null) {
            res = doBuildClassInfoOf(owner,
                    name, jcTreeTag, jcTreeOpTags);
            classInfoMap.put((Class<JCTree>) owner, (JCTreeClassInfo<? extends JCTree>) res);
        }
        return res;
    }
    
    
    public static <T> JCTreeFieldInfo<T> fieldInfoOf(JCTreeClassInfo<?> owner, String name) {
        return fieldInfoOf(owner, name, null);
    }
    
    // NOTE: params fieldListElementType ... could be set from @FieldInfo annotation on JCTree classes...
    public static <T> JCTreeFieldInfo<T> fieldInfoOf(JCTreeClassInfo<?> owner, String name, Class<?> fieldListElementType) {
        @SuppressWarnings("unchecked")
        JCTreeFieldInfo<T> res = (JCTreeFieldInfo<T>) owner.getField(name);
        if (res == null) {
            res = doBuildFieldInfoOf(owner, name, fieldListElementType);
        }
        return res;
    }
    

    
        
    private static <T extends JCTree> JCTreeClassInfo<T> doBuildClassInfoOf(Class<T> jcTreeClass, String name, JCTree.Tag jcTreeTag, JCTree.Tag[] jcTreeOpTags) {
        JCTreeClassInfo<T> res = new JCTreeClassInfo<T>(jcTreeClass, name, jcTreeTag, jcTreeOpTags);
        
        // TODO .. scan class annotation...
        // TODO scan fields + field annotations to pre-build field list, them mark as read-only
        
        return res;
    }

    
    private static <T> JCTreeFieldInfo<T> doBuildFieldInfoOf(JCTreeClassInfo<?> owner, 
            String fieldName, 
            Class<?> fieldListElementType) {
        // find java.lang.reflect.Field by introspection
        Field field;
        try {
            field = owner.getJcTreeClass().getField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException();
        } catch (SecurityException e) {
            throw new IllegalStateException();
        }
        JCTreeFieldInfo<T> res = new JCTreeFieldInfo<T>(owner, field, fieldListElementType);
        return res;
    }
    
    
    
    
    /* Tree tag values, identifying kinds of trees */
    public enum JCTreeFieldTag {
        
        /** For methods that return an invalid tag if a given condition is not met
         */
        NO_FIELDTAG,
        
        /**For "@@" Ast-level annotation 
        */
        // AST_ANNOTATION,
        AST_ANNOTATION_annotationType,
        AST_ANNOTATION_args,

        /** Toplevel nodes, of type TopLevel, representing entire source files.
        */
        // TOPLEVEL,
        TOPLEVEL_packageAnnotations,
        TOPLEVEL_pid,
        TOPLEVEL_defs,
        TOPLEVEL_sourcefile,
        TOPLEVEL_packge,
        TOPLEVEL_namedImportScope, 
        TOPLEVEL_starImportScope,
        TOPLEVEL_lineMap,
        TOPLEVEL_docComments,
        TOPLEVEL_endPositions,

        
        /** Import clauses, of type Import.
         */
        IMPORT_staticImport,
        IMPORT_qualid,

        /** Class definitions, of type ClassDef.
         */
        CLASSDEF_mods,
        CLASSDEF_name,
        CLASSDEF_typarams,
        CLASSDEF_extending,
        CLASSDEF_implementing,
        CLASSDEF_defs,
        CLASSDEF_sym,
        
        
        /** Method definitions, of type MethodDef.
         */
        METHODDEF_mods,
        METHODDEF_name,
        METHODDEF_restype,
        METHODDEF_typarams,
        METHODDEF_recvparam,
        METHODDEF_params,
        METHODDEF_thrown,
        METHODDEF_body,
        METHODDEF_defaultValue,
        METHODDEF_sym,

        /** Variable definitions, of type VarDef.
         */
        VARDEF_mods,
        VARDEF_name,
        VARDEF_nameexpr,
        VARDEF_vartype,
        VARDEF_init,
        VARDEF_sym,

        
        /** The no-op statement ";", of type Skip
         */
        SKIP,

        /** Blocks, of type Block.
         */
        BLOCK_flags(JCBlockInfo.F_flags),
        BLOCK_stats(JCBlockInfo.F_stats),
        // BLOCK_endpos(JCBlockInfo.F_endpos),

        
        /** Do-while loops, of type DoLoop.
         */
        DOLOOP_body(JCDoWhileLoopInfo.F_body),
        DOLOOP_cond(JCDoWhileLoopInfo.F_cond),

        /** While-loops, of type WhileLoop.
         */
        WHILELOOP_cond(JCWhileLoopInfo.F_cond),
        WHILELOOP_body(JCWhileLoopInfo.F_body),

        /** For-loops, of type ForLoop.
         */
        FORLOOP_init(JCForLoopInfo.F_init),
        FORLOOP_cond(JCForLoopInfo.F_cond),
        FORLOOP_step(JCForLoopInfo.F_step),
        FORLOOP_body(JCForLoopInfo.F_body),

        
        /** Foreach-loops, of type ForeachLoop.
         */
        FOREACHLOOP_var(JCEnhancedForLoopInfo.F_var),
        FOREACHLOOP_expr(JCEnhancedForLoopInfo.F_expr),
        FOREACHLOOP_body(JCEnhancedForLoopInfo.F_body),

        /** Labelled statements, of type Labelled.
         */
        LABELLED_label(JCLabeledStatementInfo.F_label),
        LABELLED_body(JCLabeledStatementInfo.F_body),

        /** Switch statements, of type Switch.
         */
        SWITCH,

        /** Case parts in switch statements, of type Case.
         */
        CASE,

        /** Synchronized statements, of type Synchonized.
         */
        SYNCHRONIZED,

        /** Try statements, of type Try.
         */
        TRY,

        /** Catch clauses in try statements, of type Catch.
         */
        CATCH,

        /** Conditional expressions, of type Conditional.
         */
        CONDEXPR,

        /** Conditional statements, of type If.
         */
        IF,

        /** Expression statements, of type Exec.
         */
        EXEC,

        /** Break statements, of type Break.
         */
        BREAK,

        /** Continue statements, of type Continue.
         */
        CONTINUE,

        /** Return statements, of type Return.
         */
        RETURN,

        /** Throw statements, of type Throw.
         */
        THROW,

        /** Assert statements, of type Assert.
         */
        ASSERT,

        /** Method invocation expressions, of type Apply.
         */
        APPLY,

        /** Class instance creation expressions, of type NewClass.
         */
        NEWCLASS,

        /** Array creation expressions, of type NewArray.
         */
        NEWARRAY,

        /** Lambda expression, of type Lambda.
         */
        LAMBDA,

        /** Parenthesized subexpressions, of type Parens.
         */
        PARENS,

        /** Assignment expressions, of type Assign.
         */
        ASSIGN,

        /** Type cast expressions, of type TypeCast.
         */
        TYPECAST,

        /** Type test expressions, of type TypeTest.
         */
        TYPETEST,

        /** Indexed array expressions, of type Indexed.
         */
        INDEXED,

        /** Selections, of type Select.
         */
        SELECT,

        /** Member references, of type Reference.
         */
        REFERENCE,

        /** Simple identifiers, of type Ident.
         */
        IDENT,

        /** Literals, of type Literal.
         */
        LITERAL,

        /** Basic type identifiers, of type TypeIdent.
         */
        TYPEIDENT,

        /** Array types, of type TypeArray.
         */
        TYPEARRAY,

        /** Parameterized types, of type TypeApply.
         */
        TYPEAPPLY,

        /** Union types, of type TypeUnion.
         */
        TYPEUNION,

        /** Intersection types, of type TypeIntersection.
         */
        TYPEINTERSECTION,

        /** Formal type parameters, of type TypeParameter.
         */
        TYPEPARAMETER,

        /** Type argument.
         */
        WILDCARD,

        /** Bound kind: extends, super, exact, or unbound
         */
        TYPEBOUNDKIND,

        /** metadata: Annotation.
         */
        ANNOTATION,

        /** metadata: Type annotation.
         */
        TYPE_ANNOTATION,

        /** metadata: Modifiers
         */
        MODIFIERS,

        /** An annotated type tree.
         */
        ANNOTATED_TYPE,

        /** Error trees, of type Erroneous.
         */
        ERRONEOUS,

        /** Unary operators, of type Unary.
         */
        POS,                             // +
        NEG,                             // -
        NOT,                             // !
        COMPL,                           // ~
        PREINC,                          // ++ _
        PREDEC,                          // -- _
        POSTINC,                         // _ ++
        POSTDEC,                         // _ --

        /** unary operator for null reference checks, only used internally.
         */
        NULLCHK,

        /** Binary operators, of type Binary.
         */
        OR,                              // ||
        AND,                             // &&
        BITOR,                           // |
        BITXOR,                          // ^
        BITAND,                          // &
        EQ,                              // ==
        NE,                              // !=
        LT,                              // <
        GT,                              // >
        LE,                              // <=
        GE,                              // >=
        SL,                              // <<
        SR,                              // >>
        USR,                             // >>>
        PLUS,                            // +
        MINUS,                           // -
        MUL,                             // *
        DIV,                             // /
        MOD,                             // %

        /** Assignment operators, of type Assignop.
         */
        BITOR_ASG(BITOR),                // |=
        BITXOR_ASG(BITXOR),              // ^=
        BITAND_ASG(BITAND),              // &=

        SL_ASG(SL),                      // <<=
        SR_ASG(SR),                      // >>=
        USR_ASG(USR),                    // >>>=
        PLUS_ASG(PLUS),                  // +=
        MINUS_ASG(MINUS),                // -=
        MUL_ASG(MUL),                    // *=
        DIV_ASG(DIV),                    // /=
        MOD_ASG(MOD),                    // %=

        /** A synthetic let expression, of type LetExpr.
         */
        LETEXPR;                         // ala scheme

        private final JCTreeFieldInfo<?> classFieldInfo;

        private final JCTreeFieldTag noAssignTag;

        private static final int numberOfOperators = MOD.ordinal() - POS.ordinal() + 1;

        private JCTreeFieldTag(JCTreeFieldInfo<?> classFieldInfo) {
            this(classFieldInfo, null);
        }
        
        private JCTreeFieldTag(JCTreeFieldInfo<?> classFieldInfo, JCTreeFieldTag noAssignTag) {
            this.classFieldInfo = classFieldInfo; 
            this.noAssignTag = noAssignTag;
        }
        
        public JCTreeFieldInfo<?> getClassFieldInfo() {
            return classFieldInfo;
        }

        @Deprecated // TODO.. add classFieldInfo
        private JCTreeFieldTag(JCTreeFieldTag noAssignTag) {
            this(null, noAssignTag);
        }
        @Deprecated // TODO.. add classFieldInfo
        private JCTreeFieldTag() {
            this(null, null);
        }
        
        public static int getNumberOfOperators() {
            return numberOfOperators;
        }

        public JCTreeFieldTag noAssignOp() {
            if (noAssignTag != null)
                return noAssignTag;
            throw new AssertionError("noAssignOp() method is not available for non assignment tags");
        }

        public boolean isPostUnaryOp() {
            return (this == POSTINC || this == POSTDEC);
        }

        public boolean isIncOrDecUnaryOp() {
            return (this == PREINC || this == PREDEC || this == POSTINC || this == POSTDEC);
        }

        public boolean isAssignop() {
            return noAssignTag != null;
        }

        public int operatorIndex() {
            return (this.ordinal() - POS.ordinal());
        }
    }



//    public static final JCTreeFieldInfo<JCTreeClassInfo> F_type = JCTreeInfo.fieldInfoOf(CLSS, "type");

    
    /* empty constructor
     */
    /*pp*/ JCTreeInfo() {
    }
    
    /* The tag of this node -- one of the constants declared above.
     */
//    public abstract JCTreeClassInfo<?> getClassInfo();
//
//    /**  */
//    @Override
//    public String toString() {
//        return getClassInfo().getName();
//    }


    /**
     * extension element to any ASTnode JCTree
     * @author arnaud
     *
     * cf attached to JCTree.treeExtensions()
     */
    public static abstract class JCTreeExtensionInfo<T extends JCTree> extends JCExpressionInfo<T> {
        
    }
        
    /**
     * extension for allowing annotation at AST-level  "@@(...)" 
     */
    public static class JCASTAnnotationInfo extends JCTreeExtensionInfo<JCASTAnnotation> {
        public static final JCTreeClassInfo<JCASTAnnotation> CLSS = JCTreeInfo.classInfoOf(JCASTAnnotation.class, "ASTAnnotation", Tag.AST_ANNOTATION);
        
        /** the annotation class name (simple name or qualified name) */
        public static final JCTreeFieldInfo<JCTree> F_annotationType = JCTreeInfo.fieldInfoOf(CLSS, "annotationType");
        public static final JCTreeFieldInfo<List<JCExpression>> F_args = JCTreeInfo.fieldInfoOf(CLSS, "args", JCExpression.class);

    }

    /**
     * Everything in one source file is kept in a {@linkplain JCCompilationUnit} structure.
     */
    public static class JCCompilationUnitClassInfo extends JCTreeInfo<JCCompilationUnit> {
        public static final JCTreeClassInfo<JCCompilationUnit> CLSS = JCTreeInfo.classInfoOf(JCCompilationUnit.class, "CompilationUnit", Tag.TOPLEVEL);
        
        public static final JCTreeFieldInfo<List<JCAnnotation>> F_packageAnnotations = JCTreeInfo.fieldInfoOf(CLSS, "packageAnnotations", JCAnnotation.class);
        /** The tree representing the package clause. */
        public static final JCTreeFieldInfo<JCExpression> F_pid = JCTreeInfo.fieldInfoOf(CLSS, "pid");
        /** All definitions in this file (ClassDef, Import, and Skip) */
        public static final JCTreeFieldInfo<List<JCTree>> F_defs = JCTreeInfo.fieldInfoOf(CLSS, "defs");
        /* The source file name. */
        public static final JCTreeFieldInfo<JavaFileObject> F_sourcefile = JCTreeInfo.fieldInfoOf(CLSS, "sourceFile");
        /** The package to which this compilation unit belongs. */
        public static final JCTreeFieldInfo<PackageSymbol> F_packge = JCTreeInfo.fieldInfoOf(CLSS, "packge");
        /** A scope for all named imports. */
        public static final JCTreeFieldInfo<ImportScope> F_namedImportScope = JCTreeInfo.fieldInfoOf(CLSS, "namedImportScope"); 
        /** A scope for all import-on-demands. */
        public static final JCTreeFieldInfo<StarImportScope> F_starImportScope = JCTreeInfo.fieldInfoOf(CLSS, "starImportScope");
        /** Line starting positions, defined only if option -g is set. */
        public static final JCTreeFieldInfo<Position.LineMap> F_lineMap = JCTreeInfo.fieldInfoOf(CLSS, "lineMap");
        /** A table that stores all documentation comments indexed by the tree
         * nodes they refer to. defined only if option -s is set. */
        public static final JCTreeFieldInfo<DocCommentTable> F_docComments = JCTreeInfo.fieldInfoOf(CLSS, "docComments");
        /* An object encapsulating ending positions of source ranges indexed by
         * the tree nodes they belong to. Defined only if option -Xjcov is set. */
        public static final JCTreeFieldInfo<EndPosTable> F_endPositions = JCTreeInfo.fieldInfoOf(CLSS, "endPositions");

    }

    /**
     * An import clause.
     */
    public static class JCImportInfo extends JCTreeInfo<JCImport> {
        public static final JCTreeClassInfo<JCImport> CLSS = JCTreeInfo.classInfoOf(JCImport.class, "Import", Tag.IMPORT);
        
        public static final JCTreeFieldInfo<Boolean> F_staticImport = JCTreeInfo.fieldInfoOf(CLSS, "staticImport");
        /** The imported class(es). */
        public static final JCTreeFieldInfo<JCTree> F_qualid = JCTreeInfo.fieldInfoOf(CLSS, "qualid");

    }

    /**
     * base class for all statements
     * @param <T>
     */
    public static abstract class JCStatementInfo<T extends JCTree> extends JCTreeInfo<T> {
        
    }

    /**
     * base class for all expressions
     * @param <T>
     */
    public static abstract class JCExpressionInfo<T extends JCTree> extends JCTreeInfo<T> {

    }

    /**
     * Common supertype for all poly expression trees (lambda, method references,
     * conditionals, method and constructor calls)
     */
    public static abstract class JCPolyExpressionInfo<T extends JCPolyExpression> extends JCExpressionInfo<T> {
        public static final JCTreeClassInfo<JCImport> CLSS = JCTreeInfo.classInfoOf(JCImport.class, "Import", Tag.IMPORT);
        
        /** is this poly expression a 'true' poly expression? */
        public static final JCTreeFieldInfo<PolyKind> F_polyKind = JCTreeInfo.fieldInfoOf(CLSS, "polyKind");
    }

    /**
     * Common supertype for all functional expression trees (lambda and method references)
     */
    public static abstract class JCFunctionalExpressionInfo<T extends JCFunctionalExpression> extends JCPolyExpressionInfo<T> {
        //  abstract class... public static final JCTreeClassInfo<JCFunctionalExpression> CLSS = JCTreeInfo.classInfoOf(JCFunctionalExpression.class, "FunctionalExpression", null);
        
        /** list of target types inferred for this functional expression. */
        public static final JCTreeFieldInfo<List<Type>> F_targets = JCTreeInfo.fieldInfoOf(CLSS, "targets", Type.class);

    }

    /**
     * A class definition.
     */
    public static class JCClassDeclInfo extends JCStatementInfo<JCClassDecl> {
        public static final JCTreeClassInfo<JCClassDecl> CLSS = JCTreeInfo.classInfoOf(JCClassDecl.class, "ClassDecl", Tag.CLASSDEF);
        
        /** the modifiers */
        public static final JCTreeFieldInfo<JCModifiers> F_mods = JCTreeInfo.fieldInfoOf(CLSS, "mods");
        /** the name of the class */
        public static final JCTreeFieldInfo<Name> F_name = JCTreeInfo.fieldInfoOf(CLSS, "name");
        /** formal class parameters */
        public static final JCTreeFieldInfo<List<JCTypeParameter>> F_typarams = JCTreeInfo.fieldInfoOf(CLSS, "typarams", JCTypeParameter.class);
        /** the classes this class extends */
        public static final JCTreeFieldInfo<JCExpression> F_extending = JCTreeInfo.fieldInfoOf(CLSS, "extending");
        /** the interfaces implemented by this class */
        public static final JCTreeFieldInfo<List<JCExpression>> F_implementing = JCTreeInfo.fieldInfoOf(CLSS, "implementing", JCExpression.class);
        /** all variables and methods defined in this class */
        public static final JCTreeFieldInfo<List<JCTree>> F_defs = JCTreeInfo.fieldInfoOf(CLSS, "defs", JCTree.class);
        /** the symbol */
        public static final JCTreeFieldInfo<ClassSymbol> F_sym = JCTreeInfo.fieldInfoOf(CLSS, "sym");
    }

    /**
     * A method definition.
     */
    public static class JCMethodDeclInfo extends JCTreeInfo<JCMethodDecl> {
        public static final JCTreeClassInfo<JCMethodDecl> CLSS = JCTreeInfo.classInfoOf(JCMethodDecl.class, "MethodDecl", Tag.METHODDEF);
        
        /** method modifiers */
        public static final JCTreeFieldInfo<JCModifiers> F_mods = JCTreeInfo.fieldInfoOf(CLSS, "mods");
        /** method name */
        public static final JCTreeFieldInfo<Name> F_name = JCTreeInfo.fieldInfoOf(CLSS, "name");
        /** type of method return value */
        public static final JCTreeFieldInfo<JCExpression> F_restype = JCTreeInfo.fieldInfoOf(CLSS, "restype");
        /** type parameters */
        public static final JCTreeFieldInfo<List<JCTypeParameter>> F_typarams = JCTreeInfo.fieldInfoOf(CLSS, "typarams", JCTypeParameter.class);
        /** receiver parameter */
        public static final JCTreeFieldInfo<JCVariableDecl> F_recvparam = JCTreeInfo.fieldInfoOf(CLSS, "recvparam");
        /** value parameters */
        public static final JCTreeFieldInfo<List<JCVariableDecl>> F_params = JCTreeInfo.fieldInfoOf(CLSS, "params", JCVariableDecl.class);
        /** exceptions thrown by this method */
        public static final JCTreeFieldInfo<List<JCExpression>> F_thrown = JCTreeInfo.fieldInfoOf(CLSS, "thrown", JCExpression.class);
        /** statements in the method */
        public static final JCTreeFieldInfo<JCBlock> F_body = JCTreeInfo.fieldInfoOf(CLSS, "body");
        /** default value, for annotation types */
        public static final JCTreeFieldInfo<JCExpression> F_defaultValue = JCTreeInfo.fieldInfoOf(CLSS, "defaultValue");
        /** method symbol */
        public static final JCTreeFieldInfo<MethodSymbol> F_sym = JCTreeInfo.fieldInfoOf(CLSS, "sym");

  }

    /**
     * A variable definition.
     */
    public static class JCVariableDeclInfo extends JCStatementInfo<JCVariableDecl> {
        public static final JCTreeClassInfo<JCVariableDecl> CLSS = JCTreeInfo.classInfoOf(JCVariableDecl.class, "VariableDecl", Tag.VARDEF);
        
        /** variable modifiers */
        public static final JCTreeFieldInfo<JCModifiers> F_mods = JCTreeInfo.fieldInfoOf(CLSS, "mods");
        /** variable name */
        public static final JCTreeFieldInfo<Name> F_name = JCTreeInfo.fieldInfoOf(CLSS, "name");
        /** variable name expression */
        public static final JCTreeFieldInfo<JCExpression> F_nameexpr = JCTreeInfo.fieldInfoOf(CLSS, "nameexpr");
        /** type of the variable */
        public static final JCTreeFieldInfo<JCExpression> F_vartype = JCTreeInfo.fieldInfoOf(CLSS, "vartype");
        /** variable's initial value */
        public static final JCTreeFieldInfo<JCExpression> F_init = JCTreeInfo.fieldInfoOf(CLSS, "init");
        /** symbol */
        public static final JCTreeFieldInfo<VarSymbol> F_sym = JCTreeInfo.fieldInfoOf(CLSS, "sym");

    }

    /**
     * A no-op statement ";".
     */
    public static class JCSkipInfo extends JCStatementInfo<JCSkip> {
        public static final JCTreeClassInfo<JCSkip> CLSS = JCTreeInfo.classInfoOf(JCSkip.class, "Skip", Tag.SKIP);
        
    }

    /**
     * A statement block.
     */
    public static class JCBlockInfo extends JCStatementInfo<JCBlock> {
        public static final JCTreeClassInfo<JCBlock> CLSS = JCTreeInfo.classInfoOf(JCBlock.class, "Block", Tag.BLOCK);
        
        /** flags */
        public static final JCTreeFieldInfo<Long> F_flags = JCTreeInfo.fieldInfoOf(CLSS, "flags");
        /** statements */
        public static final JCTreeFieldInfo<List<JCStatement>> F_stats = JCTreeInfo.fieldInfoOf(CLSS, "stats", JCStatement.class);
//        /** Position of closing brace, optional. */
//        public static final JCTreeFieldInfo<int> F_endpos = JCTreeInfo.fieldInfoOf(CLSS, "endpos");
    }

    /**
     * A do loop
     */
    public static class JCDoWhileLoopInfo extends JCStatementInfo<JCDoWhileLoop> {
        public static final JCTreeClassInfo<JCDoWhileLoop> CLSS = JCTreeInfo.classInfoOf(JCDoWhileLoop.class, "doWhile", Tag.DOLOOP);
        
        public static final JCTreeFieldInfo<JCStatement> F_body = JCTreeInfo.fieldInfoOf(CLSS, "body");
        public static final JCTreeFieldInfo<JCExpression> F_cond = JCTreeInfo.fieldInfoOf(CLSS, "cond");
    }

    /**
     * A while loop
     */
    public static class JCWhileLoopInfo extends JCStatementInfo<JCWhileLoop> {
        public static final JCTreeClassInfo<JCWhileLoop> CLSS = JCTreeInfo.classInfoOf(JCWhileLoop.class, "while", Tag.WHILELOOP);

        public static final JCTreeFieldInfo<JCExpression> F_cond = JCTreeInfo.fieldInfoOf(CLSS, "cond");
        public static final JCTreeFieldInfo<JCStatement> F_body = JCTreeInfo.fieldInfoOf(CLSS, "body");

    }

    /**
     * A for loop.
     */
    public static class JCForLoopInfo extends JCStatementInfo<JCForLoop> {
        public static final JCTreeClassInfo<JCForLoop> CLSS = JCTreeInfo.classInfoOf(JCForLoop.class, "for", Tag.FORLOOP);

        public static final JCTreeFieldInfo<List<JCStatement>> F_init = JCTreeInfo.fieldInfoOf(CLSS, "init", JCStatement.class);
        public static final JCTreeFieldInfo<JCExpression> F_cond = JCTreeInfo.fieldInfoOf(CLSS, "cond");
        public static final JCTreeFieldInfo<List<JCExpressionStatement>> F_step = JCTreeInfo.fieldInfoOf(CLSS, "step", JCExpressionStatement.class);
        public static final JCTreeFieldInfo<JCStatement> F_body = JCTreeInfo.fieldInfoOf(CLSS, "body");

    }

    /**
     * The enhanced for loop.
     */
    public static class JCEnhancedForLoopInfo extends JCStatementInfo<JCEnhancedForLoop> {
        public static final JCTreeClassInfo<JCEnhancedForLoop> CLSS = JCTreeInfo.classInfoOf(JCEnhancedForLoop.class, "foreach", Tag.FOREACHLOOP);

        public static final JCTreeFieldInfo<JCVariableDecl> F_var = JCTreeInfo.fieldInfoOf(CLSS, "var");
        public static final JCTreeFieldInfo<JCExpression> F_expr = JCTreeInfo.fieldInfoOf(CLSS, "expr");
        public static final JCTreeFieldInfo<JCStatement> F_body = JCTreeInfo.fieldInfoOf(CLSS, "body");
    }

    /**
     * A labelled expression or statement.
     */
    public static class JCLabeledStatementInfo extends JCStatementInfo<JCLabeledStatement> {
        public static final JCTreeClassInfo<JCLabeledStatement> CLSS = JCTreeInfo.classInfoOf(JCLabeledStatement.class, "label", Tag.LABELLED);

        public static final JCTreeFieldInfo<Name> F_label = JCTreeInfo.fieldInfoOf(CLSS, "label");
        public static final JCTreeFieldInfo<JCStatement> F_body = JCTreeInfo.fieldInfoOf(CLSS, "body");
    }

    /**
     * A "switch ( ) { }" construction.
     */
    public static class JCSwitchInfo extends JCStatementInfo<JCSwitch> {
        public static final JCTreeClassInfo<JCLabeledStatement> CLSS = JCTreeInfo.classInfoOf(JCLabeledStatement.class, "switch", Tag.SWITCH);

        public static final JCTreeFieldInfo<JCExpression> F_selector = JCTreeInfo.fieldInfoOf(CLSS, "selector");
        public static final JCTreeFieldInfo<List<JCCase>> F_cases = JCTreeInfo.fieldInfoOf(CLSS, "cases", JCCase.class);
    }

    /**
     * A "case  :" of a switch.
     */
    public static class JCCaseInfo extends JCStatementInfo<JCCase> {
        public static final JCTreeClassInfo<JCCase> CLSS = JCTreeInfo.classInfoOf(JCCase.class, "case", Tag.CASE);

        public static final JCTreeFieldInfo<JCExpression> F_pat = JCTreeInfo.fieldInfoOf(CLSS, "pat");
        public static final JCTreeFieldInfo<List<JCStatement>> F_stats = JCTreeInfo.fieldInfoOf(CLSS, "stats", JCStatement.class);
    }

    /**
     * A synchronized block.
     */
    public static class JCSynchronizedInfo extends JCStatementInfo<JCSynchronized> {
        public static final JCTreeClassInfo<JCSynchronized> CLSS = JCTreeInfo.classInfoOf(JCSynchronized.class, "synchronized", Tag.SYNCHRONIZED);

        public static final JCTreeFieldInfo<JCExpression> F_lock = JCTreeInfo.fieldInfoOf(CLSS, "lock");
        public static final JCTreeFieldInfo<JCBlock> F_body = JCTreeInfo.fieldInfoOf(CLSS, "body");
    }

    /**
     * A "try { } catch ( ) { } finally { }" block.
     */
    public static class JCTryInfo extends JCStatementInfo<JCTry> {
        public static final JCTreeClassInfo<JCTry> CLSS = JCTreeInfo.classInfoOf(JCTry.class, "try", Tag.TRY);

        public static final JCTreeFieldInfo<JCBlock> F_body = JCTreeInfo.fieldInfoOf(CLSS, "body");
        public static final JCTreeFieldInfo<List<JCCatch>> F_catchers = JCTreeInfo.fieldInfoOf(CLSS, "catchers", JCCatch.class);
        public static final JCTreeFieldInfo<JCBlock> F_finalizer = JCTreeInfo.fieldInfoOf(CLSS, "finalizer");
        public static final JCTreeFieldInfo<List<JCTree>> F_resources = JCTreeInfo.fieldInfoOf(CLSS, "resources", JCTree.class);
        public static final JCTreeFieldInfo<Boolean> F_finallyCanCompleteNormally = JCTreeInfo.fieldInfoOf(CLSS, "finallyCanCompleteNormally");
    }

    /**
     * A catch block.
     */
    public static class JCCatchInfo extends JCTreeInfo<JCCatch> {
        public static final JCTreeClassInfo<JCCatch> CLSS = JCTreeInfo.classInfoOf(JCCatch.class, "catch", Tag.CATCH);

        public static final JCTreeFieldInfo<JCVariableDecl> F_param = JCTreeInfo.fieldInfoOf(CLSS, "param");
        public static final JCTreeFieldInfo<JCBlock> F_body = JCTreeInfo.fieldInfoOf(CLSS, "body");
    }

    /**
     * A ( ) ? ( ) : ( ) conditional expression
     */
    public static class JCConditionalInfo extends JCPolyExpressionInfo<JCConditional> {
        public static final JCTreeClassInfo<JCConditional> CLSS = JCTreeInfo.classInfoOf(JCConditional.class, "Conditional", Tag.CONDEXPR);

        public static final JCTreeFieldInfo<JCExpression> F_cond = JCTreeInfo.fieldInfoOf(CLSS, "cond");
        public static final JCTreeFieldInfo<JCExpression> F_truepart = JCTreeInfo.fieldInfoOf(CLSS, "truepart");
        public static final JCTreeFieldInfo<JCExpression> F_falsepart = JCTreeInfo.fieldInfoOf(CLSS, "falsepart");
    }

    /**
     * An "if ( ) { } else { }" block
     */
    public static class JCIfInfo extends JCStatementInfo<JCIf> {
        public static final JCTreeClassInfo<JCIf> CLSS = JCTreeInfo.classInfoOf(JCIf.class, "if", Tag.IF);

        public static final JCTreeFieldInfo<JCExpression> F_cond = JCTreeInfo.fieldInfoOf(CLSS, "cond");
        public static final JCTreeFieldInfo<JCStatement> F_thenpart = JCTreeInfo.fieldInfoOf(CLSS, "thenpart");
        public static final JCTreeFieldInfo<JCStatement> F_elsepart = JCTreeInfo.fieldInfoOf(CLSS, "elsepart");
    }

    /**
     * an expression statement
     */
    public static class JCExpressionStatementInfo extends JCStatementInfo<JCExpressionStatement> {
        public static final JCTreeClassInfo<JCExpressionStatement> CLSS = JCTreeInfo.classInfoOf(JCExpressionStatement.class, "ExprStmt", Tag.EXEC);

        public static final JCTreeFieldInfo<JCExpression> F_expr = JCTreeInfo.fieldInfoOf(CLSS, "expr");
    }

    /**
     * A break from a loop or switch.
     */
    public static class JCBreakInfo extends JCStatementInfo<JCBreak> {
        public static final JCTreeClassInfo<JCBreak> CLSS = JCTreeInfo.classInfoOf(JCBreak.class, "break", Tag.BREAK);

        public static final JCTreeFieldInfo<Name> F_label = JCTreeInfo.fieldInfoOf(CLSS, "label");
        public static final JCTreeFieldInfo<JCTree> F_target = JCTreeInfo.fieldInfoOf(CLSS, "target");
    }

    /**
     * A continue of a loop.
     */
    public static class JCContinueInfo extends JCStatementInfo<JCContinue> {
        public static final JCTreeClassInfo<JCContinue> CLSS = JCTreeInfo.classInfoOf(JCContinue.class, "continue", Tag.CONTINUE);
        
        public static final JCTreeFieldInfo<Name> F_label = JCTreeInfo.fieldInfoOf(CLSS, "label");
        public static final JCTreeFieldInfo<JCTree> F_target = JCTreeInfo.fieldInfoOf(CLSS, "target");
    }

    /**
     * A return statement.
     */
    public static class JCReturnInfo extends JCStatementInfo<JCReturn> {
        public static final JCTreeClassInfo<JCReturn> CLSS = JCTreeInfo.classInfoOf(JCReturn.class, "", Tag.RETURN);

        public static final JCTreeFieldInfo<JCExpression> F_expr = JCTreeInfo.fieldInfoOf(CLSS, "expr");
    }

    /**
     * A throw statement.
     */
    public static class JCThrowInfo extends JCStatementInfo<JCThrow> {
        public static final JCTreeClassInfo<JCThrow> CLSS = JCTreeInfo.classInfoOf(JCThrow.class, "throw", Tag.THROW);
        
        public static final JCTreeFieldInfo<JCExpression> F_expr = JCTreeInfo.fieldInfoOf(CLSS, "expr");
    }

    /**
     * An assert statement.
     */
    public static class JCAssertInfo extends JCStatementInfo<JCAssert> {
        public static final JCTreeClassInfo<JCAssert> CLSS = JCTreeInfo.classInfoOf(JCAssert.class, "assert", Tag.ASSERT);
        
        public static final JCTreeFieldInfo<JCExpression> F_cond = JCTreeInfo.fieldInfoOf(CLSS, "cond");
        public static final JCTreeFieldInfo<JCExpression> F_detail = JCTreeInfo.fieldInfoOf(CLSS, "detail");
    }

    /**
     * A method invocation
     */
    public static class JCMethodInvocationInfo extends JCPolyExpressionInfo<JCMethodInvocation> {
        public static final JCTreeClassInfo<JCMethodInvocation> CLSS = JCTreeInfo.classInfoOf(JCMethodInvocation.class, "MethodInvocation", Tag.APPLY);
        
        public static final JCTreeFieldInfo<List<JCExpression>> F_typeargs = JCTreeInfo.fieldInfoOf(CLSS, "typeargs", JCExpression.class);
        public static final JCTreeFieldInfo<JCExpression> F_meth = JCTreeInfo.fieldInfoOf(CLSS, "meth");
        public static final JCTreeFieldInfo<List<JCExpression>> F_args = JCTreeInfo.fieldInfoOf(CLSS, "args", JCExpression.class);
        public static final JCTreeFieldInfo<Type> F_varargsElement = JCTreeInfo.fieldInfoOf(CLSS, "varargsElement");
    }

    /**
     * A new(...) operation.
     */
    public static class JCNewClassInfo extends JCPolyExpressionInfo<JCNewClass> {
        public static final JCTreeClassInfo<JCNewClass> CLSS = JCTreeInfo.classInfoOf(JCNewClass.class, "NewClass", Tag.NEWCLASS);
        
        public static final JCTreeFieldInfo<JCExpression> F_encl = JCTreeInfo.fieldInfoOf(CLSS, "encl");
        public static final JCTreeFieldInfo<List<JCExpression>> F_typeargs = JCTreeInfo.fieldInfoOf(CLSS, "typeargs", JCExpression.class);
        public static final JCTreeFieldInfo<JCExpression> F_clazz = JCTreeInfo.fieldInfoOf(CLSS, "clazz");
        public static final JCTreeFieldInfo<List<JCExpression>> F_args = JCTreeInfo.fieldInfoOf(CLSS, "args", JCExpression.class);
        public static final JCTreeFieldInfo<JCClassDecl> F_def = JCTreeInfo.fieldInfoOf(CLSS, "def");
        public static final JCTreeFieldInfo<Symbol> F_constructor = JCTreeInfo.fieldInfoOf(CLSS, "constructor");
        public static final JCTreeFieldInfo<Type> F_varargsElement = JCTreeInfo.fieldInfoOf(CLSS, "varargsElement");
        public static final JCTreeFieldInfo<Type> F_constructorType = JCTreeInfo.fieldInfoOf(CLSS, "constructorType");
    }

    /**
     * A new[...] operation.
     */
    public static class JCNewArrayInfo extends JCExpressionInfo<JCNewArray> {
        public static final JCTreeClassInfo<JCNewArray> CLSS = JCTreeInfo.classInfoOf(JCNewArray.class, "NewArray", Tag.NEWARRAY);
        
        public static final JCTreeFieldInfo<JCExpression> F_elemtype = JCTreeInfo.fieldInfoOf(CLSS, "elemtype");
        public static final JCTreeFieldInfo<List<JCExpression>> F_dims = JCTreeInfo.fieldInfoOf(CLSS, "dims", JCExpression.class);
        // type annotations on inner-most component
        public static final JCTreeFieldInfo<List<JCAnnotation>> F_annotations = JCTreeInfo.fieldInfoOf(CLSS, "annotations", JCAnnotation.class);
        // type annotations on dimensions
        public List<List<JCAnnotation>> dimAnnotations;
        public static final JCTreeFieldInfo<List<JCExpression>> F_elems = JCTreeInfo.fieldInfoOf(CLSS, "elems", JCExpression.class);
    }

    /**
     * A lambda expression.
     */
    public static class JCLambdaInfo extends JCFunctionalExpressionInfo<JCLambda> {
        public static final JCTreeClassInfo<JCLambda> CLSS = JCTreeInfo.classInfoOf(JCLambda.class, "Lambda", Tag.LAMBDA);

        public static final JCTreeFieldInfo<List<JCVariableDecl>> F_params = JCTreeInfo.fieldInfoOf(CLSS, "params", JCVariableDecl.class);
        public static final JCTreeFieldInfo<JCTree> F_body = JCTreeInfo.fieldInfoOf(CLSS, "body");
        public static final JCTreeFieldInfo<Boolean> F_canCompleteNormally = JCTreeInfo.fieldInfoOf(CLSS, "canCompleteNormally");
        public static final JCTreeFieldInfo<List<Type>> F_inferredThrownTypes = JCTreeInfo.fieldInfoOf(CLSS, "inferredThrownTypes", Type.class);
        public static final JCTreeFieldInfo<ParameterKind> F_paramKind = JCTreeInfo.fieldInfoOf(CLSS, "paramKind");
    }

    /**
     * A parenthesized subexpression ( ... )
     */
    public static class JCParensInfo extends JCExpressionInfo<JCParens> {
        public static final JCTreeClassInfo<JCParens> CLSS = JCTreeInfo.classInfoOf(JCParens.class, "Parens", Tag.PARENS);
        
        public static final JCTreeFieldInfo<JCExpression> F_expr = JCTreeInfo.fieldInfoOf(CLSS, "expr");
    }

    /**
     * A assignment with "=".
     */
    public static class JCAssignInfo extends JCExpressionInfo<JCAssign> {
        public static final JCTreeClassInfo<JCAssign> CLSS = JCTreeInfo.classInfoOf(JCAssign.class, "assign", Tag.ASSIGN);
        
        public static final JCTreeFieldInfo<JCExpression> F_lhs = JCTreeInfo.fieldInfoOf(CLSS, "lhs");
        public static final JCTreeFieldInfo<JCExpression> F_rhs = JCTreeInfo.fieldInfoOf(CLSS, "rhs");
    }

    /**
     * An assignment with "+=", "|=" ...
     */
    public static class JCAssignOpInfo extends JCExpressionInfo<JCAssignOp> {
        // TODO... may split for several Tag?
        public static final JCTreeClassInfo<JCAssignOp> CLSS = JCTreeInfo.classInfoOf(JCAssignOp.class, "AssignOp", null, new Tag[] {
            Tag.BITOR_ASG,
            Tag.BITXOR_ASG,
            Tag.BITAND_ASG,
            Tag.SL_ASG,
            Tag.SR_ASG,
            Tag.USR_ASG,
            Tag.PLUS_ASG,
            Tag.MINUS_ASG,
            Tag.MUL_ASG,
            Tag.DIV_ASG,
            Tag.MOD_ASG 
        });

        public static final JCTreeFieldInfo<Tag> F_opcode = JCTreeInfo.fieldInfoOf(CLSS, "opcode");
        public static final JCTreeFieldInfo<JCExpression> F_lhs = JCTreeInfo.fieldInfoOf(CLSS, "lhs");
        public static final JCTreeFieldInfo<JCExpression> F_rhs = JCTreeInfo.fieldInfoOf(CLSS, "rhs");
        public static final JCTreeFieldInfo<Symbol> F_operator = JCTreeInfo.fieldInfoOf(CLSS, "operator");
    }

    /**
     * A unary operation.
     */
    public static class JCUnaryInfo extends JCExpressionInfo<JCUnary> {
        // TODO... split for several Tag
        public static final JCTreeClassInfo<JCUnary> CLSS = JCTreeInfo.classInfoOf(JCUnary.class, "Unary", new Tag[] {
            Tag.POS,
            Tag.NEG,
            Tag.NOT,
            Tag.COMPL,
            Tag.PREINC,
            Tag.PREDEC,
            Tag.POSTINC,
            Tag.POSTDEC
        });

        public static final JCTreeFieldInfo<JCExpression> F_arg = JCTreeInfo.fieldInfoOf(CLSS, "arg");
        public static final JCTreeFieldInfo<Symbol> F_operator = JCTreeInfo.fieldInfoOf(CLSS, "operator");
    }

    /**
     * A binary operation.
     */
    public static class JCBinaryInfo extends JCExpressionInfo<JCBinary> {
        // TODO... split for several Tag
        public static final JCTreeClassInfo<JCBinary> CLSS = JCTreeInfo.classInfoOf(JCBinary.class, "Binary", new Tag[] {
            Tag.OR,                              // ||
            Tag.AND,                             // &&
            Tag.BITOR,                           // |
            Tag.BITXOR,                          // ^
            Tag.BITAND,                          // &
            Tag.EQ,                              // ==
            Tag.NE,                              // !=
            Tag.LT,                              // <
            Tag.GT,                              // >
            Tag.LE,                              // <=
            Tag.GE,                              // >=
            Tag.SL,                              // <<
            Tag.SR,                              // >>
            Tag.USR,                             // >>>
            Tag.PLUS,                            // +
            Tag.MINUS,                           // -
            Tag.MUL,                             // *
            Tag.DIV,                             // /
            Tag.MOD                              // %
        });

        public static final JCTreeFieldInfo<JCExpression> F_lhs = JCTreeInfo.fieldInfoOf(CLSS, "lhs");
        public static final JCTreeFieldInfo<JCExpression> F_rhs = JCTreeInfo.fieldInfoOf(CLSS, "rhs");
        public static final JCTreeFieldInfo<Symbol> F_operator = JCTreeInfo.fieldInfoOf(CLSS, "operator");
    }

    /**
     * A type cast.
     */
    public static class JCTypeCastInfo extends JCExpressionInfo<JCTypeCast> {
        public static final JCTreeClassInfo<JCTypeCast> CLSS = JCTreeInfo.classInfoOf(JCTypeCast.class, "TypeCast", Tag.TYPECAST);

        public static final JCTreeFieldInfo<JCTree> F_clazz = JCTreeInfo.fieldInfoOf(CLSS, "clazz");
        public static final JCTreeFieldInfo<JCExpression> F_expr = JCTreeInfo.fieldInfoOf(CLSS, "expr");
    }

    /**
     * A type test.
     */
    public static class JCInstanceOfInfo extends JCExpressionInfo<JCInstanceOf> {
        public static final JCTreeClassInfo<JCInstanceOf> CLSS = JCTreeInfo.classInfoOf(JCInstanceOf.class, "InstanceOf", Tag.TYPETEST);

        public static final JCTreeFieldInfo<JCExpression> F_expr = JCTreeInfo.fieldInfoOf(CLSS, "expr");
        public static final JCTreeFieldInfo<JCTree> F_clazz = JCTreeInfo.fieldInfoOf(CLSS, "clazz");
    }

    /**
     * An array selection
     */
    public static class JCArrayAccessInfo extends JCExpressionInfo<JCArrayAccess> {
        public static final JCTreeClassInfo<JCArrayAccess> CLSS = JCTreeInfo.classInfoOf(JCArrayAccess.class, "ArrayAccess", Tag.INDEXED);

        public static final JCTreeFieldInfo<JCExpression> F_indexed = JCTreeInfo.fieldInfoOf(CLSS, "indexed");
        public static final JCTreeFieldInfo<JCExpression> F_index = JCTreeInfo.fieldInfoOf(CLSS, "index");
    }

    /**
     * Selects through packages and classes
     */
    public static class JCFieldAccessInfo extends JCExpressionInfo<JCFieldAccess> {
        public static final JCTreeClassInfo<JCFieldAccess> CLSS = JCTreeInfo.classInfoOf(JCFieldAccess.class, "FieldAccess", Tag.SELECT);

        public static final JCTreeFieldInfo<JCExpression> F_selected = JCTreeInfo.fieldInfoOf(CLSS, "selected");
        public static final JCTreeFieldInfo<Name> F_name = JCTreeInfo.fieldInfoOf(CLSS, "name");
        public static final JCTreeFieldInfo<Symbol> F_sym = JCTreeInfo.fieldInfoOf(CLSS, "sym");
    }

    /**
     * Selects a member expression.
     */
    public static class JCMemberReferenceInfo extends JCFunctionalExpressionInfo<JCMemberReference> {
        public static final JCTreeClassInfo<JCMemberReference> CLSS = JCTreeInfo.classInfoOf(JCMemberReference.class, "MemberReference", Tag.REFERENCE);
        
        public static final JCTreeFieldInfo<ReferenceMode> F_mode = JCTreeInfo.fieldInfoOf(CLSS, "mode");
        public static final JCTreeFieldInfo<ReferenceKind> F_kind = JCTreeInfo.fieldInfoOf(CLSS, "kind");
        public static final JCTreeFieldInfo<Name> F_name = JCTreeInfo.fieldInfoOf(CLSS, "name");
        public static final JCTreeFieldInfo<JCExpression> F_expr = JCTreeInfo.fieldInfoOf(CLSS, "expr");
        public static final JCTreeFieldInfo<List<JCExpression>> F_typeargs = JCTreeInfo.fieldInfoOf(CLSS, "typeargs", JCExpression.class);
        public static final JCTreeFieldInfo<Symbol> F_sym = JCTreeInfo.fieldInfoOf(CLSS, "sym");
        public static final JCTreeFieldInfo<Type> F_varargsElement = JCTreeInfo.fieldInfoOf(CLSS, "varargsElement");
        public static final JCTreeFieldInfo<PolyKind> F_refPolyKind = JCTreeInfo.fieldInfoOf(CLSS, "refPolyKind");
        public static final JCTreeFieldInfo<Boolean> F_ownerAccessible = JCTreeInfo.fieldInfoOf(CLSS, "ownerAccessible");

    }

    /**
     * An identifier
     */
    public static class JCIdentInfo extends JCExpressionInfo<JCIdent> {
        public static final JCTreeClassInfo<JCIdent> CLSS = JCTreeInfo.classInfoOf(JCIdent.class, "Ident", Tag.IDENT);

        public static final JCTreeFieldInfo<Name> F_name = JCTreeInfo.fieldInfoOf(CLSS, "name");
        public static final JCTreeFieldInfo<Symbol> F_sym = JCTreeInfo.fieldInfoOf(CLSS, "sym");
    }

    /**
     * A constant value given literally.
     */
    public static class JCLiteralInfo extends JCExpressionInfo<JCLiteral> {
        public static final JCTreeClassInfo<JCLiteral> CLSS = JCTreeInfo.classInfoOf(JCLiteral.class, "Literal", Tag.LITERAL);

        public static final JCTreeFieldInfo<TypeTag> F_typetag = JCTreeInfo.fieldInfoOf(CLSS, "typetag");
        public static final JCTreeFieldInfo<Object> F_value = JCTreeInfo.fieldInfoOf(CLSS, "value");
    }

    /**
     * Identifies a basic type.
     * @see TypeTag
     */
    public static class JCPrimitiveTypeTreeInfo extends JCExpressionInfo<JCPrimitiveTypeTree> {
        public static final JCTreeClassInfo<JCPrimitiveTypeTree> CLSS = JCTreeInfo.classInfoOf(JCPrimitiveTypeTree.class, "PrimitiveType", Tag.TYPEIDENT);
        
        /** the basic type id */
        public static final JCTreeFieldInfo<TypeTag> F_typetag = JCTreeInfo.fieldInfoOf(CLSS, "typetag");
    }

    /**
     * An array type, A[]
     */
    public static class JCArrayTypeTreeInfo extends JCExpressionInfo<JCArrayTypeTree> {
        public static final JCTreeClassInfo<JCArrayTypeTree> CLSS = JCTreeInfo.classInfoOf(JCArrayTypeTree.class, "ArrayType", Tag.TYPEARRAY);
        
        public static final JCTreeFieldInfo<JCExpression> F_elemtype = JCTreeInfo.fieldInfoOf(CLSS, "elemtype");
    }

    /**
     * A parameterized type, {@literal T<...>}
     */
    public static class JCTypeApplyInfo extends JCExpressionInfo<JCTypeApply> {
        public static final JCTreeClassInfo<JCTypeApply> CLSS = JCTreeInfo.classInfoOf(JCTypeApply.class, "TypeApply", Tag.TYPEAPPLY);
        
        public static final JCTreeFieldInfo<JCExpression> F_clazz = JCTreeInfo.fieldInfoOf(CLSS, "clazz");
        public static final JCTreeFieldInfo<List<JCExpression>> F_arguments = JCTreeInfo.fieldInfoOf(CLSS, "arguments", JCExpression.class);
    }

    /**
     * A union type, T1 | T2 | ... Tn (used in multicatch statements)
     */
    public static class JCTypeUnionInfo extends JCExpressionInfo<JCTypeUnion> {
        public static final JCTreeClassInfo<JCTypeUnion> CLSS = JCTreeInfo.classInfoOf(JCTypeUnion.class, "TypeUnion", Tag.TYPEUNION);

        public static final JCTreeFieldInfo<List<JCExpression>> F_alternatives = JCTreeInfo.fieldInfoOf(CLSS, "alternatives", JCExpression.class);
    }

    /**
     * An intersection type, T1 & T2 & ... Tn (used in cast expressions)
     */
    public static class JCTypeIntersectionInfo extends JCExpressionInfo<JCTypeIntersection> {
        public static final JCTreeClassInfo<JCTypeIntersection> CLSS = JCTreeInfo.classInfoOf(JCTypeIntersection.class, "TypeIntersection", Tag.TYPEINTERSECTION);

        public static final JCTreeFieldInfo<List<JCExpression>> F_bounds = JCTreeInfo.fieldInfoOf(CLSS, "bounds", JCExpression.class);
    }

    /**
     * A formal class parameter.
     */
    public static class JCTypeParameterInfo extends JCTreeInfo<JCTypeParameter> {
        public static final JCTreeClassInfo<JCTypeParameter> CLSS = JCTreeInfo.classInfoOf(JCTypeParameter.class, "TypeParameter", Tag.TYPEPARAMETER);
        
        /** name */
        public static final JCTreeFieldInfo<Name> F_name = JCTreeInfo.fieldInfoOf(CLSS, "name");
        /** bounds */
        public static final JCTreeFieldInfo<List<JCExpression>> F_bounds = JCTreeInfo.fieldInfoOf(CLSS, "bounds", JCExpression.class);
        /** type annotations on type parameter */
        public static final JCTreeFieldInfo<List<JCAnnotation>> F_annotations = JCTreeInfo.fieldInfoOf(CLSS, "annotations", JCAnnotation.class);
    }

    public static class JCWildcardInfo extends JCExpressionInfo<JCWildcard> {
        public static final JCTreeClassInfo<JCWildcard> CLSS = JCTreeInfo.classInfoOf(JCWildcard.class, "Wildcard", Tag.WILDCARD);

        public static final JCTreeFieldInfo<TypeBoundKind> F_kind = JCTreeInfo.fieldInfoOf(CLSS, "kind");
        public static final JCTreeFieldInfo<JCTree> F_inner = JCTreeInfo.fieldInfoOf(CLSS, "inner");
    }

    public static class TypeBoundKindInfo extends JCTreeInfo<TypeBoundKind> {
        public static final JCTreeClassInfo<TypeBoundKind> CLSS = JCTreeInfo.classInfoOf(TypeBoundKind.class, "", Tag.TYPEBOUNDKIND);

        public static final JCTreeFieldInfo<BoundKind> F_kind = JCTreeInfo.fieldInfoOf(CLSS, "kind");
    }

    public static class JCAnnotationInfo extends JCExpressionInfo<JCAnnotation> {
        // TODO... split for several Tag
        public static final JCTreeClassInfo<JCAnnotation> CLSS = JCTreeInfo.classInfoOf(JCAnnotation.class, "Annotation", new Tag[]{
            Tag.ANNOTATION,
            Tag.TYPE_ANNOTATION
        });

        public static final JCTreeFieldInfo<Tag> F_tag = JCTreeInfo.fieldInfoOf(CLSS, "tag");
        public static final JCTreeFieldInfo<JCTree> F_annotationType = JCTreeInfo.fieldInfoOf(CLSS, "annotationType");
        public static final JCTreeFieldInfo<List<JCExpression>> F_args = JCTreeInfo.fieldInfoOf(CLSS, "args", JCExpression.class);
        public static final JCTreeFieldInfo<Attribute.Compound> F_attribute = JCTreeInfo.fieldInfoOf(CLSS, "attribute");
    }

    public static class JCModifiersInfo extends JCTreeInfo<JCModifiers> {
        public static final JCTreeClassInfo<JCModifiers> CLSS = JCTreeInfo.classInfoOf(JCModifiers.class, "Modifiers", Tag.MODIFIERS);

        public static final JCTreeFieldInfo<Long> F_flags = JCTreeInfo.fieldInfoOf(CLSS, "flags");
        public static final JCTreeFieldInfo<List<JCAnnotation>> F_annotations = JCTreeInfo.fieldInfoOf(CLSS, "annotations", JCAnnotation.class);
    }

    public static class JCAnnotatedTypeInfo extends JCExpressionInfo<JCAnnotatedType> {
        public static final JCTreeClassInfo<JCAnnotatedType> CLSS = JCTreeInfo.classInfoOf(JCAnnotatedType.class, "AnnotatedType", Tag.ANNOTATED_TYPE);

        public static final JCTreeFieldInfo<List<JCAnnotation>> F_annotations = JCTreeInfo.fieldInfoOf(CLSS, "annotations", JCAnnotation.class);
        public static final JCTreeFieldInfo<JCExpression> F_underlyingType = JCTreeInfo.fieldInfoOf(CLSS, "underlyingType");
    }

    public static class JCErroneousInfo extends JCExpressionInfo<JCErroneous> {
        public static final JCTreeClassInfo<JCErroneous> CLSS = JCTreeInfo.classInfoOf(JCErroneous.class, "Erroneous", Tag.ERRONEOUS);

        public static final JCTreeFieldInfo<List<? extends JCTree>> F_errs = JCTreeInfo.fieldInfoOf(CLSS, "errs", JCTree.class);
    }

    /** (let int x = 3; in x+2) */
    public static class LetExprInfo extends JCExpressionInfo<LetExpr> {
        public static final JCTreeClassInfo<LetExpr> CLSS = JCTreeInfo.classInfoOf(LetExpr.class, "Let", Tag.LETEXPR);

        public static final JCTreeFieldInfo<List<JCVariableDecl>> F_defs = JCTreeInfo.fieldInfoOf(CLSS, "defs", JCVariableDecl.class);
        public static final JCTreeFieldInfo<JCTree> F_expr = JCTreeInfo.fieldInfoOf(CLSS, "expr");
    }

    
    
}
