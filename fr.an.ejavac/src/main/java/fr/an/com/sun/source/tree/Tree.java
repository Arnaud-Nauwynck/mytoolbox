/*
 * Copyright (c) 2005, 2013, Oracle and/or its affiliates. All rights reserved.
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

package fr.an.com.sun.source.tree;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Common interface for all nodes in an abstract syntax tree.
 *
 * <p><b>WARNING:</b> This interface and its sub-interfaces are
 * subject to change as the Java&trade; programming language evolves.
 * These interfaces are implemented by the JDK Java compiler (javac)
 * and should not be implemented either directly or indirectly by
 * other applications.
 *
 * @author Peter von der Ah&eacute;
 * @author Jonathan Gibbons
 *
 * @since 1.6
 */
@jdk.Supported
public interface Tree {

    /**
     * Enumerates all kinds of trees.
     */
    @jdk.Supported
    public enum Kind {

        /**
         * Used for instance of {@link ExtendedASTAnnotationTree}
         * representing AST-level tree annotation "@@"
        */
        AST_ANNOTATION(null, "ASTAnnotation"), // ExtendedASTAnnotationTree.class ... break API compatility with visitor ...

        ANNOTATED_TYPE(AnnotatedTypeTree.class, "AnnotatedType", "AtType"),

        /**
         * Used for instances of {@link AnnotationTree}
         * representing declaration annotations.
         */
        ANNOTATION(AnnotationTree.class, "Annotation", "At"),

        /**
         * Used for instances of {@link AnnotationTree}
         * representing type annotations.
         */
        TYPE_ANNOTATION(AnnotationTree.class, "TypeAnnotation", "Annotation", "At"),

        /**
         * Used for instances of {@link ArrayAccessTree}.
         */
        ARRAY_ACCESS(ArrayAccessTree.class, "ArrayAccess"),

        /**
         * Used for instances of {@link ArrayTypeTree}.
         */
        ARRAY_TYPE(ArrayTypeTree.class, "ArrayType"),

        /**
         * Used for instances of {@link AssertTree}.
         */
        ASSERT(AssertTree.class, "Assert", "assert"),

        /**
         * Used for instances of {@link AssignmentTree}.
         */
        ASSIGNMENT(AssignmentTree.class, "Assign", "Eq", "="),

        /**
         * Used for instances of {@link BlockTree}.
         */
        BLOCK(BlockTree.class, "Block", "{}"),

        /**
         * Used for instances of {@link BreakTree}.
         */
        BREAK(BreakTree.class, "Break", "break"),

        /**
         * Used for instances of {@link CaseTree}.
         */
        CASE(CaseTree.class, "Case", "case"),

        /**
         * Used for instances of {@link CatchTree}.
         */
        CATCH(CatchTree.class, "Catch", "catch"),

        /**
         * Used for instances of {@link ClassTree} representing classes.
         */
        CLASS(ClassTree.class, "Class", "class"),

        /**
         * Used for instances of {@link CompilationUnitTree}.
         */
        COMPILATION_UNIT(CompilationUnitTree.class, "CompilationUnit", "cu"),

        /**
         * Used for instances of {@link ConditionalExpressionTree}.
         */
        CONDITIONAL_EXPRESSION(ConditionalExpressionTree.class, "ConditionalExpression", "?"),

        /**
         * Used for instances of {@link ContinueTree}.
         */
        CONTINUE(ContinueTree.class, "Continue", "continue"),

        /**
         * Used for instances of {@link DoWhileLoopTree}.
         */
        DO_WHILE_LOOP(DoWhileLoopTree.class, "DoWhile", "do"),

        /**
         * Used for instances of {@link EnhancedForLoopTree}.
         */
        ENHANCED_FOR_LOOP(EnhancedForLoopTree.class, "EnhancedForLoop", "for"),

        /**
         * Used for instances of {@link ExpressionStatementTree}.
         */
        EXPRESSION_STATEMENT(ExpressionStatementTree.class, "ExpressionStatement", "expr;", "e;"),

        /**
         * Used for instances of {@link MemberSelectTree}.
         */
        MEMBER_SELECT(MemberSelectTree.class, "MemberSelect", "."),

        /**
         * Used for instances of {@link MemberReferenceTree}.
         */
        MEMBER_REFERENCE(MemberReferenceTree.class, "MemberReference", "#"),

        /**
         * Used for instances of {@link ForLoopTree}.
         */
        FOR_LOOP(ForLoopTree.class, "ForLoop", "for"),

        /**
         * Used for instances of {@link IdentifierTree}.
         */
        IDENTIFIER(IdentifierTree.class, "Identifier", "id", "name"),

        /**
         * Used for instances of {@link IfTree}.
         */
        IF(IfTree.class, "If", "if"),

        /**
         * Used for instances of {@link ImportTree}.
         */
        IMPORT(ImportTree.class, "Import", "import"),

        /**
         * Used for instances of {@link InstanceOfTree}.
         */
        INSTANCE_OF(InstanceOfTree.class, "InstanceOf", "instanceof"),

        /**
         * Used for instances of {@link LabeledStatementTree}.
         */
        LABELED_STATEMENT(LabeledStatementTree.class, "LabeledStatement", "label"),

        /**
         * Used for instances of {@link MethodTree}.
         */
        METHOD(MethodTree.class, "Method", "method"),

        /**
         * Used for instances of {@link MethodInvocationTree}.
         */
        METHOD_INVOCATION(MethodInvocationTree.class, "MethodInvocation", "invoke", "call"),

        /**
         * Used for instances of {@link ModifiersTree}.
         */
        MODIFIERS(ModifiersTree.class, "Modifiers"),

        /**
         * Used for instances of {@link NewArrayTree}.
         */
        NEW_ARRAY(NewArrayTree.class, "NewArray", "new"),

        /**
         * Used for instances of {@link NewClassTree}.
         */
        NEW_CLASS(NewClassTree.class, "NewClass", "new"),

        /**
         * Used for instances of {@link LambdaExpressionTree}.
         */
        LAMBDA_EXPRESSION(LambdaExpressionTree.class, "Lambda", "lambda"),

        /**
         * Used for instances of {@link ParenthesizedTree}.
         */
        PARENTHESIZED(ParenthesizedTree.class, "Parenthesized"),  // should not use prefix with "(e)" as it might confuse the parser ?

        /**
         * Used for instances of {@link PrimitiveTypeTree}.
         */
        PRIMITIVE_TYPE(PrimitiveTypeTree.class, "PrimitiveType", "type"),

        /**
         * Used for instances of {@link ReturnTree}.
         */
        RETURN(ReturnTree.class, "Return", "return"),

        /**
         * Used for instances of {@link EmptyStatementTree}.
         */
        EMPTY_STATEMENT(EmptyStatementTree.class, "EmptyStatement", "empty"),

        /**
         * Used for instances of {@link SwitchTree}.
         */
        SWITCH(SwitchTree.class, "Switch", "switch"),

        /**
         * Used for instances of {@link SynchronizedTree}.
         */
        SYNCHRONIZED(SynchronizedTree.class, "Synchronized", "synchronized"),

        /**
         * Used for instances of {@link ThrowTree}.
         */
        THROW(ThrowTree.class, "Throw", "throw"),

        /**
         * Used for instances of {@link TryTree}.
         */
        TRY(TryTree.class, "Try", "try"),

        /**
         * Used for instances of {@link ParameterizedTypeTree}.
         */
        PARAMETERIZED_TYPE(ParameterizedTypeTree.class, "ParameterizedType", "type<>", "type"),

        /**
         * Used for instances of {@link UnionTypeTree}.
         */
        UNION_TYPE(UnionTypeTree.class, "UnionType", "union", "|", "uniontype", "type"),

        /**
         * Used for instances of {@link IntersectionTypeTree}.
         */
        INTERSECTION_TYPE(IntersectionTypeTree.class, "IntersectionType", "intersectiontype", "type"),

        /**
         * Used for instances of {@link TypeCastTree}.
         */
        TYPE_CAST(TypeCastTree.class, "TypeCast", "cast"), // should not use prefix with "()" as it might confuse the parser ?

        /**
         * Used for instances of {@link TypeParameterTree}.
         */
        TYPE_PARAMETER(TypeParameterTree.class, "TypeParameter", "type"),

        /**
         * Used for instances of {@link VariableTree}.
         */
        VARIABLE(VariableTree.class, "Variable", "var", "vardecl"),

        /**
         * Used for instances of {@link WhileLoopTree}.
         */
        WHILE_LOOP(WhileLoopTree.class, "WhileLoop", "while"),

        /**
         * Used for instances of {@link UnaryTree} representing postfix
         * increment operator {@code ++}.
         */
        POSTFIX_INCREMENT(UnaryTree.class, "PostfixIncrement", "e++", "++"),

        /**
         * Used for instances of {@link UnaryTree} representing postfix
         * decrement operator {@code --}.
         */
        POSTFIX_DECREMENT(UnaryTree.class, "PostfixDecrement", "e--", "--"),

        /**
         * Used for instances of {@link UnaryTree} representing prefix
         * increment operator {@code ++}.
         */
        PREFIX_INCREMENT(UnaryTree.class, "PrefixIncrement", "++e", "++"),

        /**
         * Used for instances of {@link UnaryTree} representing prefix
         * decrement operator {@code --}.
         */
        PREFIX_DECREMENT(UnaryTree.class, "PrefixDecrement", "--e", "--"),

        /**
         * Used for instances of {@link UnaryTree} representing unary plus
         * operator {@code +}.
         */
        UNARY_PLUS(UnaryTree.class, "UnaryPlus", "+e", "+"),

        /**
         * Used for instances of {@link UnaryTree} representing unary minus
         * operator {@code -}.
         */
        UNARY_MINUS(UnaryTree.class, "UnaryMinus", "-e", "-"),

        /**
         * Used for instances of {@link UnaryTree} representing bitwise
         * complement operator {@code ~}.
         */
        BITWISE_COMPLEMENT(UnaryTree.class, "BitwiseComplement", "~"),

        /**
         * Used for instances of {@link UnaryTree} representing logical
         * complement operator {@code !}.
         */
        LOGICAL_COMPLEMENT(UnaryTree.class, "LogicalComlement", "!"),

        /**
         * Used for instances of {@link BinaryTree} representing
         * multiplication {@code *}.
         */
        MULTIPLY(BinaryTree.class, "Multiply", "mult", "*"),

        /**
         * Used for instances of {@link BinaryTree} representing
         * division {@code /}.
         */
        DIVIDE(BinaryTree.class, "Divide", "div", "/"),

        /**
         * Used for instances of {@link BinaryTree} representing
         * remainder {@code %}.
         */
        REMAINDER(BinaryTree.class, "Remainder", "rem", "%"),

        /**
         * Used for instances of {@link BinaryTree} representing
         * addition or string concatenation {@code +}.
         */
        PLUS(BinaryTree.class, "BinaryPlus", "e+e", "+"),

        /**
         * Used for instances of {@link BinaryTree} representing
         * subtraction {@code -}.
         */
        MINUS(BinaryTree.class, "BinaryMinus", "e-e", "-"),

        /**
         * Used for instances of {@link BinaryTree} representing
         * left shift {@code <<}.
         */
        LEFT_SHIFT(BinaryTree.class, "LeftShift", "<<"),

        /**
         * Used for instances of {@link BinaryTree} representing
         * right shift {@code >>}.
         */
        RIGHT_SHIFT(BinaryTree.class, "RightShift", ">>"),

        /**
         * Used for instances of {@link BinaryTree} representing
         * unsigned right shift {@code >>>}.
         */
        UNSIGNED_RIGHT_SHIFT(BinaryTree.class, "UnsignedRightShift", ">>>"),

        /**
         * Used for instances of {@link BinaryTree} representing
         * less-than {@code <}.
         */
        LESS_THAN(BinaryTree.class, "LessThan", "lt", "<"),

        /**
         * Used for instances of {@link BinaryTree} representing
         * greater-than {@code >}.
         */
        GREATER_THAN(BinaryTree.class, "GreaterThan", "gt", ">"),

        /**
         * Used for instances of {@link BinaryTree} representing
         * less-than-equal {@code <=}.
         */
        LESS_THAN_EQUAL(BinaryTree.class, "LessThanEqual", "le", "<="),

        /**
         * Used for instances of {@link BinaryTree} representing
         * greater-than-equal {@code >=}.
         */
        GREATER_THAN_EQUAL(BinaryTree.class, "GreaterThanEqual", "ge", ">="),

        /**
         * Used for instances of {@link BinaryTree} representing
         * equal-to {@code ==}.
         */
        EQUAL_TO(BinaryTree.class, "Equals", "equals", "=="),

        /**
         * Used for instances of {@link BinaryTree} representing
         * not-equal-to {@code !=}.
         */
        NOT_EQUAL_TO(BinaryTree.class, "NotEquals", "notequals", "!="),

        /**
         * Used for instances of {@link BinaryTree} representing
         * bitwise and logical "and" {@code &}.
         */
        AND(BinaryTree.class, "And", "and", "&"),

        /**
         * Used for instances of {@link BinaryTree} representing
         * bitwise and logical "xor" {@code ^}.
         */
        XOR(BinaryTree.class, "Xor", "xor", "^"),

        /**
         * Used for instances of {@link BinaryTree} representing
         * bitwise and logical "or" {@code |}.
         */
        OR(BinaryTree.class, "Or", "or", "|"),

        /**
         * Used for instances of {@link BinaryTree} representing
         * conditional-and {@code &&}.
         */
        CONDITIONAL_AND(BinaryTree.class, "ConditionalAnd", "&&"),

        /**
         * Used for instances of {@link BinaryTree} representing
         * conditional-or {@code ||}.
         */
        CONDITIONAL_OR(BinaryTree.class, "ConditionalOr", "||"),

        /**
         * Used for instances of {@link CompoundAssignmentTree} representing
         * multiplication assignment {@code *=}.
         */
        MULTIPLY_ASSIGNMENT(CompoundAssignmentTree.class, "MultiplyAssignment", "*="),

        /**
         * Used for instances of {@link CompoundAssignmentTree} representing
         * division assignment {@code /=}.
         */
        DIVIDE_ASSIGNMENT(CompoundAssignmentTree.class, "DivideAssignment", "/="),

        /**
         * Used for instances of {@link CompoundAssignmentTree} representing
         * remainder assignment {@code %=}.
         */
        REMAINDER_ASSIGNMENT(CompoundAssignmentTree.class, "RemainderAssignment", "%="),

        /**
         * Used for instances of {@link CompoundAssignmentTree} representing
         * addition or string concatenation assignment {@code +=}.
         */
        PLUS_ASSIGNMENT(CompoundAssignmentTree.class, "PLusAssignment", "+="),

        /**
         * Used for instances of {@link CompoundAssignmentTree} representing
         * subtraction assignment {@code -=}.
         */
        MINUS_ASSIGNMENT(CompoundAssignmentTree.class, "MinusAssignment", "-="),

        /**
         * Used for instances of {@link CompoundAssignmentTree} representing
         * left shift assignment {@code <<=}.
         */
        LEFT_SHIFT_ASSIGNMENT(CompoundAssignmentTree.class, "LeftShiftAssignment", "<<="),

        /**
         * Used for instances of {@link CompoundAssignmentTree} representing
         * right shift assignment {@code >>=}.
         */
        RIGHT_SHIFT_ASSIGNMENT(CompoundAssignmentTree.class, "RightShiftAssignment", ">>="),

        /**
         * Used for instances of {@link CompoundAssignmentTree} representing
         * unsigned right shift assignment {@code >>>=}.
         */
        UNSIGNED_RIGHT_SHIFT_ASSIGNMENT(CompoundAssignmentTree.class, "UnsignedRightShiftAssignment", ">>>="),

        /**
         * Used for instances of {@link CompoundAssignmentTree} representing
         * bitwise and logical "and" assignment {@code &=}.
         */
        AND_ASSIGNMENT(CompoundAssignmentTree.class, "AndAssignment", "&="),

        /**
         * Used for instances of {@link CompoundAssignmentTree} representing
         * bitwise and logical "xor" assignment {@code ^=}.
         */
        XOR_ASSIGNMENT(CompoundAssignmentTree.class, "XorAssignment", "^="),

        /**
         * Used for instances of {@link CompoundAssignmentTree} representing
         * bitwise and logical "or" assignment {@code |=}.
         */
        OR_ASSIGNMENT(CompoundAssignmentTree.class, "OrAssignment", "|="),

        /**
         * Used for instances of {@link LiteralTree} representing
         * an integral literal expression of type {@code int}.
         */
        INT_LITERAL(LiteralTree.class, "IntLiteral", "int", "literal"),

        /**
         * Used for instances of {@link LiteralTree} representing
         * an integral literal expression of type {@code long}.
         */
        LONG_LITERAL(LiteralTree.class, "LongLiteral", "long", "literal"),

        /**
         * Used for instances of {@link LiteralTree} representing
         * a floating-point literal expression of type {@code float}.
         */
        FLOAT_LITERAL(LiteralTree.class, "FloatLiteral", "float", "literal"),

        /**
         * Used for instances of {@link LiteralTree} representing
         * a floating-point literal expression of type {@code double}.
         */
        DOUBLE_LITERAL(LiteralTree.class, "DoubleLiteral", "double", "literal"),

        /**
         * Used for instances of {@link LiteralTree} representing
         * a boolean literal expression of type {@code boolean}.
         */
        BOOLEAN_LITERAL(LiteralTree.class, "BooleanLiteral", "boolean", "bool", "literal"),

        /**
         * Used for instances of {@link LiteralTree} representing
         * a character literal expression of type {@code char}.
         */
        CHAR_LITERAL(LiteralTree.class, "CharLiteral", "char", "literal"),

        /**
         * Used for instances of {@link LiteralTree} representing
         * a string literal expression of type {@link String}.
         */
        STRING_LITERAL(LiteralTree.class, "StringLiteral", "string", "literal"),

        /**
         * Used for instances of {@link LiteralTree} representing
         * the use of {@code null}.
         */
        NULL_LITERAL(LiteralTree.class, "NullLiteral", "null", "literal"),

        /**
         * Used for instances of {@link WildcardTree} representing
         * an unbounded wildcard type argument.
         */
        UNBOUNDED_WILDCARD(WildcardTree.class, "UnboundedWildcard", "?"),

        /**
         * Used for instances of {@link WildcardTree} representing
         * an extends bounded wildcard type argument.
         */
        EXTENDS_WILDCARD(WildcardTree.class, "ExtendsWildcard", "?extends"),

        /**
         * Used for instances of {@link WildcardTree} representing
         * a super bounded wildcard type argument.
         */
        SUPER_WILDCARD(WildcardTree.class, "SuperWildcard", "?super"),

        /**
         * Used for instances of {@link ErroneousTree}.
         */
        ERRONEOUS(ErroneousTree.class, "Erroneous", "ERROR", "error"),

        /**
         * Used for instances of {@link ClassTree} representing interfaces.
         */
        INTERFACE(ClassTree.class, "Interface", "interface"),

        /**
         * Used for instances of {@link ClassTree} representing enums.
         */
        ENUM(ClassTree.class, "Enum", "enum"),

        /**
         * Used for instances of {@link ClassTree} representing annotation types.
         */
        ANNOTATION_TYPE(ClassTree.class, "AnnotationType", "At"),

        /**
         * An implementation-reserved node. This is the not the node
         * you are looking for.
         */
        OTHER(null, "Other");


		Kind(Class<? extends Tree> intf, String displayName, String... prefixAliases) {
            associatedInterface = intf;
            this.displayName = displayName;
            this.preferredPrefix = (displayName != null)? displayName : toString();
            this.preferredPrefixLowerCase = preferredPrefix.toLowerCase();
            if (prefixAliases == null) prefixAliases = new String[0];
            this.prefixAliases = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(prefixAliases)));
            Set<String> tmpPrefixAliasesLowerCase = new HashSet<String>();
            for(String e : prefixAliases) {
            	tmpPrefixAliasesLowerCase.add(e.toLowerCase());
            }
            this.prefixAliasesLowerCase = Collections.unmodifiableSet(tmpPrefixAliasesLowerCase);
        }

        private final Class<? extends Tree> associatedInterface;
        
        private final String displayName;
        
        private final String preferredPrefix;

        private final String preferredPrefixLowerCase;

        private final Set<String> prefixAliases;
        
        private final Set<String> prefixAliasesLowerCase;


        public Class<? extends Tree> asInterface() {
            return associatedInterface;
        }

        public String getDisplayName() {
			return displayName;
		}

		public String preferredPrefix() {
        	return preferredPrefix;
        }

        public boolean hasAliasPrefix(String prefix) {
        	return prefixAliases.contains(prefix);
        }

        public boolean acceptPrefix(String prefix) {
        	if (prefix == null) return false;
        	return preferredPrefix.equals(prefix)
        			|| prefixAliases.contains(prefix);
        }

        public boolean acceptPrefixIgnoreCase(String prefix) {
        	if (prefix == null) return false;
        	String prefixLowerCase = prefix.toLowerCase();
        	return preferredPrefixLowerCase.equals(prefixLowerCase)
        			|| prefixAliasesLowerCase.contains(prefixLowerCase);
        }

    }

    /**
     * Gets the kind of this tree.
     *
     * @return the kind of this tree.
     */
    Kind getKind();

    /**
     * Accept method used to implement the visitor pattern.  The
     * visitor pattern is used to implement operations on trees.
     *
     * @param <R> result type of this operation.
     * @param <D> type of additional data.
     */
    <R,D> R accept(TreeVisitor<R,D> visitor, D data);

    /**
     * @since jdk1.8-ARN
     */
    List<? extends ASTExtensionTree> getASTExtensions();

    /**
     * @since jdk1.8-ARN
     */
    List<ASTAnnotationTree> getASTAnnotations();
    
    
}
