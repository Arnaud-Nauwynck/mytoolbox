package fr.an.eclipse.tools.lombokify.helpers;

import java.util.List;

import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.NullLiteral;

import fr.an.eclipse.pattern.util.JavaNamingUtil;

@SuppressWarnings("unchecked")
public class MatchASTUtils {

	/**
	 * match if meth is a basic getter method, like
	 * <code>public T get<Upper(F)ieldName>() {
	 *   return <fieldName>;
	 * }
	 * </code>
	 * @return null or propertyName 
	 */
	public static String matchBasicGetterMeth(MethodDeclaration m) {
		String methName = m.getName().getIdentifier();
		String propName;
		if (methName.startsWith("get")) {
			propName = JavaNamingUtil.uncapitalize(methName.substring(3));
		} else if (methName.startsWith("is")) {
			propName = JavaNamingUtil.uncapitalize(methName.substring(2));
		} else {
			return null;
		}

		if (! matchPublicModifierList(m.modifiers())) {
			return null;
		}
		if (! m.parameters().isEmpty()) {
			return null;
		}
		Statement bodyStmt = matchSingleStmtBlock(m.getBody());
		if (bodyStmt == null) {
			return null;
		}
		if (!(bodyStmt instanceof ReturnStatement)) {
			return null;
		}
		ReturnStatement retStmt = (ReturnStatement) bodyStmt;
		Expression retExpr = retStmt.getExpression();
		String fieldName = matchSimpleName_or_ThisSimpleName(retExpr);
		if (fieldName == null) {
			return null;
		}
		if (! fieldName.equals(propName)) {
			return null;
		}
		return propName;
	}

	/**
	 * match if meth is a basic setter method, like
	 * <code>public void set<Upper(F)ieldName>(T <param>) {
	 *   this.<fieldName> = <param>;
	 * }
	 * </code>
	 * @return null or propertyName 
	 */
	public static String matchBasicSetterMeth(MethodDeclaration m) {
		String methName = m.getName().getIdentifier();
		String propName;
		if (methName.startsWith("set")) {
			propName = JavaNamingUtil.uncapitalize(methName.substring(3));
		} else {
			return null;
		}
		if (! matchPublicModifierList(m.modifiers())) {
			return null;
		}
		List<SingleVariableDeclaration> params = m.parameters();
		if (params.size() != 1) {
			return null;
		}
		SingleVariableDeclaration param0 = params.get(0);
		String param0Name = param0.getName().getIdentifier();
		Statement bodyStmt = matchSingleStmtBlock(m.getBody());
		if (bodyStmt == null) {
			return null;
		}
		Assignment assign = matchAssignmentStmt(bodyStmt);
		if (assign == null) {
			return null;
		}
		Expression lhs = assign.getLeftHandSide();
		Expression rhs = assign.getRightHandSide();
		String fieldName = matchSimpleName_or_ThisSimpleName(lhs);
		if (fieldName == null) {
			return null;
		}
		if (! fieldName.equals(propName)) {
			return null;
		}
		if (! matchSimpleName(rhs, param0Name)) {
			return null;
		}
		return propName;
	}
	

	public static boolean matchPublicModifierList(List<IExtendedModifier> modifiers) {
		if (modifiers.size() != 1) {
			return false;
		}
		IExtendedModifier modifier0 = modifiers.get(0);
		if (!( modifier0 instanceof Modifier) || !((Modifier)modifier0).isPublic()) {
			return false;
		}
		return true;
	}

	public static int findModifier(List<IExtendedModifier> src, ModifierKeyword modifierKeyword) {
		final int len = src.size();
		for(int i = 0; i < len; i++) {
			IExtendedModifier elt = src.get(i);
			if (elt instanceof Modifier) {
				Modifier mod = (Modifier) elt;
				if (mod.getKeyword() == modifierKeyword) {
					return i;
				}
			}
		}
		return -1;
	}

	
	public static Statement matchSingleStmtBlock(Block block) {
		if (block == null) {
			return null;
		}
		List<Statement> stmts = block.statements();
		if (stmts.size() != 1) {
			return null;
		}
		return stmts.get(0);
	}

	public static boolean matchThisExpr(Expression expr) {
		if (!(expr instanceof ThisExpression)) {
			return false;
		}
		ThisExpression thisExpr = (ThisExpression) expr;
		return null == thisExpr.getQualifier();
	}

	/**
	 * match either "fieldName" : SimpleName(fieldName)
	 * or "this.fieldName" : FieldAccessExpr(ThisExpr, SimpleName(fieldName))
 	 * @return null or fieldName
	 */
	public static String matchSimpleName_or_ThisSimpleName(Expression expr) {
		String tmpres = matchSimpleName(expr);
		if (tmpres != null) {
			return tmpres;
		}
		return matchThisSimpleName(expr);
	}
	
	public static String matchSimpleName(Expression expr) {
		if (expr instanceof SimpleName) {
			SimpleName sn = (SimpleName) expr;
			return sn.getIdentifier();
		}
		return null;
	}

	public static boolean matchSimpleName(Expression expr, String expectedName) {
		String tmpres = matchSimpleName(expr);
		if (tmpres == null) {
			return false;
		}
		return tmpres.equals(expectedName);
	}
	
	public static String matchThisSimpleName(Expression expr) {
		if (!(expr instanceof FieldAccess)) {
			return null;
		}
		FieldAccess fa = (FieldAccess) expr;
		if (! matchThisExpr(fa.getExpression())) {
			return null;
		}
		return fa.getName().getIdentifier();
	}

	public static Assignment matchAssignmentStmt(Statement stmt) {
		if (!(stmt instanceof ExpressionStatement)) {
			return null;
		}
		Expression expr = ((ExpressionStatement) stmt).getExpression();
		if (!(expr instanceof Assignment)) {
			return null;
		}
		return (Assignment) expr;
	}

	public static Expression matchVarDeclSingleInitializer(Statement stmt) {
		if (!(stmt instanceof VariableDeclarationStatement)) {
			return null;
		}
		VariableDeclarationStatement vdecl = (VariableDeclarationStatement) stmt;
		List<VariableDeclarationFragment> fragments = vdecl.fragments();
		if (fragments.size() != 1) {
			return null;
		}
		VariableDeclarationFragment frag0 = fragments.get(0);
		Expression initializer = frag0.getInitializer();
		if (initializer instanceof NullLiteral) {
			return null;
		}
		return initializer;
	}

	public static boolean matchClassInstanceCreationDiamon(Expression expr) {
		if (!( expr instanceof ClassInstanceCreation)) {
			return false;
		}
		ClassInstanceCreation cic = (ClassInstanceCreation) expr;
		Type cicType = cic.getType();
		if (cicType.isParameterizedType()) {
			if (cicType instanceof ParameterizedType) {
				ParameterizedType pt = (ParameterizedType) cicType;
				if (pt.typeArguments().isEmpty()) {
					return true;
				}
			}
		}
		return false;
	}

	public static String matchSimpleTypeName(Type type) {
		if (!(type instanceof SimpleType)) {
			return null;
		}
		SimpleType st = (SimpleType) type;
		return matchSimpleName(st.getName());
	}
	
	
}
