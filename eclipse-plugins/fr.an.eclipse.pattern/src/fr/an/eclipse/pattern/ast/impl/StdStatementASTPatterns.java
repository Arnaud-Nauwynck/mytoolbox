package fr.an.eclipse.pattern.ast.impl;

import java.util.List;

import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import fr.an.eclipse.pattern.ast.IPattern;
import fr.an.eclipse.pattern.ast.PatternMatchVisitor;
import fr.an.eclipse.pattern.ast.PatternVisitor;

public class StdStatementASTPatterns {

	/** pattern for @see org.eclipse.jdt.core.dom.Statement */
	public static abstract class StatementPattern<T extends Statement> extends AbstractASTNodePattern<T> {

	}

	// ------------------------------------------------------------------------
	
	/** pattern for org.eclipse.jdt.core.dom.Block */
	public static class BlockPattern extends AbstractASTNodePattern<Block> {
		
		private IPattern<List<Statement>> statements;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof Block)) return false;
			return v.visitMatch(this, (Block)node);
		}
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (statements != null) statements.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, Block node) {
			if (statements != null && !statements.acceptMatch(v, node.statements())) return v.mismatch("statements");
			return true;
		}


		public IPattern<List<Statement>> getStatements() {
			return statements;
		}

		public void setStatements(IPattern<List<Statement>> statements) {
			this.statements = statements;
		}
		
	}

	// ------------------------------------------------------------------------
	
	/** pattern for org.eclipse.jdt.core.dom.AssertStatement */
	public static class AssertStatementPattern extends StatementPattern<AssertStatement> {

		/**
		 * The expression pattern
		 */
		private IPattern<Expression> expression;

		/**
		 * The message expression pattern
		 */
		private IPattern<Expression> optionalMessageExpression;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof AssertStatement)) return false;
			return v.visitMatch(this, (AssertStatement)node);
		}
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (expression != null) expression.accept(v);
			if (optionalMessageExpression != null) optionalMessageExpression.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, AssertStatement node) {
			if (expression != null && !expression.acceptMatch(v, node.getExpression())) return v.mismatch("expression");
			if (optionalMessageExpression != null && !optionalMessageExpression.acceptMatch(v, node.getMessage())) return v.mismatch("optionalMessageExpression");
			return true;
		}

		
		public IPattern<Expression> getExpression() {
			return expression;
		}

		public void setExpression(IPattern<Expression> expression) {
			this.expression = expression;
		}

		public IPattern<Expression> getOptionalMessageExpression() {
			return optionalMessageExpression;
		}

		public void setOptionalMessageExpression(IPattern<Expression> optionalMessageExpression) {
			this.optionalMessageExpression = optionalMessageExpression;
		}
		
	}

	// ------------------------------------------------------------------------
	
	/** pattern for org.eclipse.jdt.core.dom.BreakStatement */
	public static class BreakStatementPattern extends StatementPattern<BreakStatement> {

		/**
		 * The label.
		 */
		private IPattern<SimpleName> optionalLabel;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof BreakStatement)) return false;
			return v.visitMatch(this, (BreakStatement)node);
		}

		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (optionalLabel != null) optionalLabel.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, BreakStatement node) {
			if (optionalLabel != null && !optionalLabel.acceptMatch(v, node.getLabel())) return v.mismatch("optionalLabel");
			return true;
		}
		
		public IPattern<SimpleName> getLabel() {
			return optionalLabel;
		}

		public void setLabel(IPattern<SimpleName> p) {
			this.optionalLabel = p;
		}
		
	}
	
	// ------------------------------------------------------------------------

	/** pattern for org.eclipse.jdt.core.dom.CatchClause */
	public static class CatchClausePattern extends AbstractASTNodePattern<CatchClause> {

		/**
		 * The body
		 */
		private IPattern<Block> body;

		/**
		 * The exception variable declaration
		 */
		private IPattern<SingleVariableDeclaration> exceptionDecl;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof CatchClause)) return false;
			return v.visitMatch(this, (CatchClause)node);
		}
		
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (body != null) body.accept(v);
			if (exceptionDecl != null) exceptionDecl.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, CatchClause node) {
			if (body != null && !body.acceptMatch(v, node.getBody())) return v.mismatch("body");
			if (exceptionDecl != null && !exceptionDecl.acceptMatch(v, node.getException())) return v.mismatch("exceptionDecl");
			return true;
		}

		
		public IPattern<Block> getBody() {
			return body;
		}

		public void setBody(IPattern<Block> body) {
			this.body = body;
		}

		public IPattern<SingleVariableDeclaration> getExceptionDecl() {
			return exceptionDecl;
		}

		public void setExceptionDecl(IPattern<SingleVariableDeclaration> exceptionDecl) {
			this.exceptionDecl = exceptionDecl;
		}
		
	}
	
	// ------------------------------------------------------------------------
	
	/** pattern for pattern for org.eclipse.jdt.core.dom.ContinueStatement */
	public static class ContinueStatementPattern extends StatementPattern<ContinueStatement> {
		
		/**
		 * The label, or <code>null</code> 
		 */
		private IPattern<SimpleName> optionalLabel;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof ContinueStatement)) return false;
			return v.visitMatch(this, (ContinueStatement)node);
		}
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (optionalLabel != null) optionalLabel.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, ContinueStatement node) {
			if (optionalLabel != null && !optionalLabel.acceptMatch(v, node.getLabel())) return v.mismatch("optionalLabel");
			return true;
		}

		
		public IPattern<SimpleName> getOptionalLabel() {
			return optionalLabel;
		}

		public void setOptionalLabel(IPattern<SimpleName> optionalLabel) {
			this.optionalLabel = optionalLabel;
		}
		
	}

	// ------------------------------------------------------------------------
	
	/** pattern for org.eclipse.jdt.core.dom.ConstructorInvocation */
	public static class ConstructorInvocationPattern extends StatementPattern<ConstructorInvocation> {

		/**
		 * The type arguments pattern
		 */
		private IPattern<List<Type>> typeArguments;

		/**
		 * The list of argument expressions pattern
		 */
		private IPattern<List<Expression>> arguments;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof ConstructorInvocation)) return false;
			return v.visitMatch(this, (ConstructorInvocation)node);
		}

		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (typeArguments != null) typeArguments.accept(v);
			if (arguments != null) arguments.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, ConstructorInvocation node) {
			if (typeArguments != null && !typeArguments.acceptMatch(v, node.typeArguments())) return v.mismatch("typeArguments");
			if (arguments != null && !arguments.acceptMatch(v, node.arguments())) return v.mismatch("arguments");
			return true;
		}

		
		public IPattern<List<Type>> getTypeArguments() {
			return typeArguments;
		}

		public void setTypeArguments(IPattern<List<Type>> p) {
			this.typeArguments = p;
		}

		public IPattern<List<Expression>> getArguments() {
			return arguments;
		}

		public void setArguments(IPattern<List<Expression>> p) {
			this.arguments = p;
		}
		
	}
	
	// ------------------------------------------------------------------------

	/** pattern for org.eclipse.jdt.core.dom.DoStatement */
	public static class DoStatementPattern extends StatementPattern<DoStatement> {

		/**
		 * The expression
		 */
		private IPattern<Expression> expression;

		/**
		 * The body statement
		 */
		private IPattern<Statement>body;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof DoStatement)) return false;
			return v.visitMatch(this, (DoStatement)node);
		}
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (expression != null) expression.accept(v);
			if (body != null) body.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, DoStatement node) {
			if (expression != null && !expression.acceptMatch(v, node.getExpression())) return v.mismatch("expression");
			if (body != null && !body.acceptMatch(v, node.getBody())) return v.mismatch("body");
			return true;
		}
		
		public IPattern<Expression> getExpression() {
			return expression;
		}

		public void setExpression(IPattern<Expression> expression) {
			this.expression = expression;
		}

		public IPattern<Statement>getBody() {
			return body;
		}

		public void setBody(IPattern<Statement>body) {
			this.body = body;
		}
		
	}
		
	// ------------------------------------------------------------------------
	
	/** pattern for pattern for org.eclipse.jdt.core.dom.EmptyStatement */
	public static class EmptyStatementPattern extends StatementPattern<EmptyStatement> {
		// no fields
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof EmptyStatement)) return false;
			return v.visitMatch(this, (EmptyStatement)node);
		}
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			// no recurse
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, EmptyStatement node) {
			// no recurse
			return true;
		}

	}
	
	// ------------------------------------------------------------------------
	
	/** pattern for pattern for org.eclipse.jdt.core.dom.EnhancedForStatement */
	public static class EnhancedForStatementPattern extends StatementPattern<EnhancedForStatement> {

		/**
		 * The parameter
		 */
		private IPattern<SingleVariableDeclaration> parameter = null;

		/**
		 * The expression
		 */
		private IPattern<Expression> expression = null;

		/**
		 * The body statement
		 */
		private IPattern<Statement>body = null;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof EnhancedForStatement)) return false;
			return v.visitMatch(this, (EnhancedForStatement)node);
		}

		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (parameter != null) parameter.accept(v);
			if (expression != null) expression.accept(v);
			if (body != null) body.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, EnhancedForStatement node) {
			if (parameter != null && !parameter.acceptMatch(v, node.getParameter())) return v.mismatch("parameter");
			if (expression != null && !expression.acceptMatch(v, node.getExpression())) return v.mismatch("expression");
			if (body != null && !body.acceptMatch(v, node.getBody())) return v.mismatch("body");
			return true;
		}

		
		public IPattern<SingleVariableDeclaration> getParameter() {
			return parameter;
		}

		public void setParameter(IPattern<SingleVariableDeclaration> parameter) {
			this.parameter = parameter;
		}

		public IPattern<Expression> getExpression() {
			return expression;
		}

		public void setExpression(IPattern<Expression> expression) {
			this.expression = expression;
		}

		public IPattern<Statement>getBody() {
			return body;
		}

		public void setBody(IPattern<Statement>body) {
			this.body = body;
		}
		
	}
	
	// ------------------------------------------------------------------------
	
	/** pattern for pattern for org.eclipse.jdt.core.dom.ExpressionStatement */
	public static class ExpressionStatementPattern extends StatementPattern<ExpressionStatement> {
		/**
		 * The expression
		 */
		private IPattern<Expression> expression = null;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof ExpressionStatement)) return false;
			return v.visitMatch(this, (ExpressionStatement)node);
		}
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (expression != null) expression.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, ExpressionStatement node) {
			if (expression != null && !expression.acceptMatch(v, node.getExpression())) return v.mismatch("expression");
			return true;
		}

		
		public IPattern<Expression> getExpression() {
			return expression;
		}

		public void setExpression(IPattern<Expression> expression) {
			this.expression = expression;
		}
		
	}
	

	// ------------------------------------------------------------------------
	
	/** pattern for pattern for org.eclipse.jdt.core.dom.ForStatement */
	public static class ForStatementPattern extends StatementPattern<ForStatement> {

		/**
		 * The list of initializer expressions
		 */
		private IPattern<List<Expression>> initializers;

		/**
		 * The condition expression
		 */
		private IPattern<Expression> optionalConditionExpression = null;

		/**
		 * The list of update expressions
		 */
		private IPattern<List<Expression>> updaters;

		/**
		 * The body statement
		 */
		private IPattern<Statement>body = null;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof ForStatement)) return false;
			return v.visitMatch(this, (ForStatement)node);
		}
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (initializers != null) initializers.accept(v);
			if (optionalConditionExpression != null) optionalConditionExpression.accept(v);
			if (updaters != null) updaters.accept(v);
			if (body != null) body.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, ForStatement node) {
			if (initializers != null) initializers.acceptMatch(v,node.initializers());
			if (optionalConditionExpression != null && !optionalConditionExpression.acceptMatch(v, node.getExpression())) return v.mismatch("optionalConditionExpression");
			if (updaters != null && !updaters.acceptMatch(v, node.updaters())) return v.mismatch("updaters");
			if (body != null && !body.acceptMatch(v, node.getBody())) return v.mismatch("body");
			return true;
		}

		
		public IPattern<List<Expression>> getInitializers() {
			return initializers;
		}

		public void setInitializers(IPattern<List<Expression>> initializers) {
			this.initializers = initializers;
		}

		public IPattern<Expression> getOptionalConditionExpression() {
			return optionalConditionExpression;
		}

		public void setOptionalConditionExpression(IPattern<Expression> optionalConditionExpression) {
			this.optionalConditionExpression = optionalConditionExpression;
		}

		public IPattern<List<Expression>> getUpdaters() {
			return updaters;
		}

		public void setUpdaters(IPattern<List<Expression>> updaters) {
			this.updaters = updaters;
		}

		public IPattern<Statement>getBody() {
			return body;
		}

		public void setBody(IPattern<Statement>body) {
			this.body = body;
		}

		
	}

	// ------------------------------------------------------------------------
	
	/** pattern for pattern for org.eclipse.jdt.core.dom.IfStatement */
	public static class IfStatementPattern extends StatementPattern<IfStatement> {

		/**
		 * The expression
		 */
		private IPattern<Expression> expression;

		/**
		 * The then statement
		 */
		private IPattern<Statement> thenStatement;

		/**
		 * The else statement
		 */
		private IPattern<Statement> optionalElseStatement;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof IfStatement)) return false;
			return v.visitMatch(this, (IfStatement)node);
		}
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (expression != null) expression.accept(v);
			if (thenStatement != null) thenStatement.accept(v);
			if (optionalElseStatement != null) optionalElseStatement.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, IfStatement node) {
			if (expression != null && !expression.acceptMatch(v, node.getExpression())) return v.mismatch("expression");
			if (thenStatement != null && !thenStatement.acceptMatch(v, node.getThenStatement())) return v.mismatch("thenStatement");
			if (optionalElseStatement != null && !optionalElseStatement.acceptMatch(v, node.getElseStatement())) return v.mismatch("optionalElseStatement");
			return true;
		}


		
		public IPattern<Expression> getExpression() {
			return expression;
		}

		public void setExpression(IPattern<Expression> expression) {
			this.expression = expression;
		}

		public IPattern<Statement>getThenStatement() {
			return thenStatement;
		}

		public void setThenStatement(IPattern<Statement>thenStatement) {
			this.thenStatement = thenStatement;
		}

		public IPattern<Statement>getOptionalElseStatement() {
			return optionalElseStatement;
		}

		public void setOptionalElseStatement(IPattern<Statement>optionalElseStatement) {
			this.optionalElseStatement = optionalElseStatement;
		}
		
	}
	
	
	// ------------------------------------------------------------------------
	
	/** pattern for pattern for org.eclipse.jdt.core.dom.LabeledStatement */
	public static class LabeledStatementPattern extends StatementPattern<LabeledStatement> {

		/**
		 * The label
		 */
		private IPattern<SimpleName> labelName = null;

		/**
		 * The body statement
		 */
		private IPattern<Statement> body = null;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof LabeledStatement)) return false;
			return v.visitMatch(this, (LabeledStatement)node);
		}
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (labelName != null) labelName.accept(v);
			if (body != null) body.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, LabeledStatement node) {
			if (labelName != null && !labelName.acceptMatch(v, node.getLabel())) return v.mismatch("labelName");
			if (body != null && !body.acceptMatch(v, node.getBody())) return v.mismatch("body");
			return true;
		}

		
		public IPattern<SimpleName> getLabelName() {
			return labelName;
		}

		public void setLabelName(IPattern<SimpleName> labelName) {
			this.labelName = labelName;
		}

		public IPattern<Statement>getBody() {
			return body;
		}

		public void setBody(IPattern<Statement>body) {
			this.body = body;
		}
		
	}
	
	// ------------------------------------------------------------------------
	
	/** pattern for pattern for org.eclipse.jdt.core.dom.ReturnStatement */
	public static class ReturnStatementPattern extends StatementPattern<ReturnStatement> {

		/**
		 * The expression; <code>null</code> for none
		 */
		private IPattern<Expression> expression = null;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof ReturnStatement)) return false;
			return v.visitMatch(this, (ReturnStatement)node);
		}
		
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (expression != null) expression.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, ReturnStatement node) {
			if (expression != null && !expression.acceptMatch(v, node.getExpression())) return v.mismatch("expression");
			return true;
		}
		
		public IPattern<Expression> getExpression() {
			return expression;
		}

		public void setExpression(IPattern<Expression> p) {
			this.expression = p;
		}
		
	}

	// ------------------------------------------------------------------------
	
	/** pattern for pattern for org.eclipse.jdt.core.dom.SuperConstructorInvocation */
	public static class SuperConstructorInvocationPattern extends StatementPattern<SuperConstructorInvocation> {

		/**
		 * The expression; <code>null</code> for none; defaults to none.
		 */
		private IPattern<Expression> expression;

		/**
		 * The type arguments
		 */
		private IPattern<List<Type>> typeArguments;

		/**
		 * The list of argument expressions
		 */
		private IPattern<List<Expression>> arguments;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof SuperConstructorInvocation)) return false;
			return v.visitMatch(this, (SuperConstructorInvocation)node);
		}
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (expression != null) expression.accept(v);
			if (typeArguments != null) typeArguments.accept(v);
			if (arguments != null) arguments.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, SuperConstructorInvocation node) {
			if (expression != null && !expression.acceptMatch(v, node.getExpression())) return v.mismatch("expression");
			if (typeArguments != null && !typeArguments.acceptMatch(v, node.typeArguments())) return v.mismatch("typeArguments");
			if (arguments != null && !arguments.acceptMatch(v, node.arguments())) return v.mismatch("arguments");
			return true;
		}

		
		public IPattern<Expression> getExpression() {
			return expression;
		}

		public void setExpression(IPattern<Expression> optionalExpression) {
			this.expression = optionalExpression;
		}

		public IPattern<List<Type>> getTypeArguments() {
			return typeArguments;
		}

		public void setTypeArguments(IPattern<List<Type>> typeArguments) {
			this.typeArguments = typeArguments;
		}

		public IPattern<List<Expression>> getArguments() {
			return arguments;
		}

		public void setArguments(IPattern<List<Expression>> arguments) {
			this.arguments = arguments;
		}
		

	}

	// ------------------------------------------------------------------------
	
	/** pattern for pattern for org.eclipse.jdt.core.dom.SwitchCase */
	public static class SwitchCasePattern extends StatementPattern<SwitchCase> {

		/**
		 * The expression; <code>null</code> for none
		 */
		private IPattern<Expression> optionalExpression;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof SwitchCase)) return false;
			return v.visitMatch(this, (SwitchCase)node);
		}

		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (optionalExpression != null) optionalExpression.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, SwitchCase node) {
			if (optionalExpression != null && !optionalExpression.acceptMatch(v, node.getExpression())) return v.mismatch("optionalExpression");
			return true;
		}
		
		public IPattern<Expression> getOptionalExpression() {
			return optionalExpression;
		}

		public void setOptionalExpression(IPattern<Expression> optionalExpression) {
			this.optionalExpression = optionalExpression;
		}

		
	}

	// ------------------------------------------------------------------------
	
	/** pattern for pattern for org.eclipse.jdt.core.dom.SwitchStatement */
	public static class SwitchStatementPattern extends StatementPattern<SwitchStatement> {

		/**
		 * The expression
		 */
		private IPattern<Expression> expression;

		/**
		 * The statements and SwitchCase nodes
		 */
		private IPattern<List<Statement>> statements;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof SwitchStatement)) return false;
			return v.visitMatch(this, (SwitchStatement)node);
		}

		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (expression != null) expression.accept(v);
			if (statements != null) statements.accept(v);
		}
		
		public boolean recursevisitMatch(PatternMatchVisitor v, SwitchStatement node) {
			if (expression != null && !expression.acceptMatch(v, node.getExpression())) return v.mismatch("expression");
			if (statements != null && !statements.acceptMatch(v, node.statements())) return v.mismatch("statements");
			return true;
		}


		public IPattern<Expression> getExpression() {
			return expression;
		}

		public void setExpression(IPattern<Expression> expression) {
			this.expression = expression;
		}

		public IPattern<List<Statement>> getStatements() {
			return statements;
		}

		public void setStatements(IPattern<List<Statement>> statements) {
			this.statements = statements;
		}
		
	}

	// ------------------------------------------------------------------------
	
	/** pattern for pattern for org.eclipse.jdt.core.dom.SynchronizedStatement */
	public static class SynchronizedStatementPattern extends StatementPattern<SynchronizedStatement> {
		/**
		 * The expression
		 */
		private IPattern<Expression> expression;

		/**
		 * The body
		 */
		private IPattern<Block> body;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof SynchronizedStatement)) return false;
			return v.visitMatch(this, (SynchronizedStatement)node);
		}

		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (expression != null) expression.accept(v);
			if (body != null) body.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, SynchronizedStatement node) {
			if (expression != null && !expression.acceptMatch(v, node.getExpression())) return v.mismatch("expression");
			if (body != null && !body.acceptMatch(v, node.getBody())) return v.mismatch("body");
			return true;
		}

		public IPattern<Expression> getExpression() {
			return expression;
		}

		public void setExpression(IPattern<Expression> expression) {
			this.expression = expression;
		}

		public IPattern<Block> getBody() {
			return body;
		}

		public void setBody(IPattern<Block> body) {
			this.body = body;
		}
		
	}

	// ------------------------------------------------------------------------
	
	/** pattern for pattern for org.eclipse.jdt.core.dom.ThrowStatement */
	public static class ThrowStatementPattern extends StatementPattern<ThrowStatement> {

		/**
		 * The expression
		 */
		private IPattern<Expression> expression;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof ThrowStatement)) return false;
			return v.visitMatch(this, (ThrowStatement)node);
		}

		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (expression != null) expression.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, ThrowStatement node) {
			if (expression != null && !expression.acceptMatch(v, node.getExpression())) return v.mismatch("expression");
			return true;
		}

		
		public IPattern<Expression> getExpression() {
			return expression;
		}

		public void setExpression(IPattern<Expression> expression) {
			this.expression = expression;
		}
		
	}

	// ------------------------------------------------------------------------
	
	/** pattern for pattern for org.eclipse.jdt.core.dom.TryStatement */
	public static class TryStatementPattern extends StatementPattern<TryStatement> {

		/**
		 * The resource expressions (element type: {@link VariableDeclarationExpression}).
		 */
		private IPattern<List<VariableDeclarationExpression>> resources;

		/**
		 * The body
		 */
		private IPattern<Block> body;

		/**
		 * The catch clauses pattern
		 */
		private IPattern<List<CatchClause>> catchClauses;

		/**
		 * The finally block
		 */
		private IPattern<Block> optionalFinallyBody;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof TryStatement)) return false;
			return v.visitMatch(this, (TryStatement)node);
		}
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (resources != null) resources.accept(v);
			if (body != null) body.accept(v);
			if (catchClauses != null) catchClauses.accept(v);
			if (optionalFinallyBody != null) optionalFinallyBody.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, TryStatement node) {
			if (resources != null && !resources.acceptMatch(v, node.resources())) return v.mismatch("resources");
			if (body != null && !body.acceptMatch(v, node.getBody())) return v.mismatch("body");
			if (catchClauses != null && !catchClauses.acceptMatch(v, node.catchClauses())) return v.mismatch("catchClauses");
			if (optionalFinallyBody != null && !optionalFinallyBody.acceptMatch(v, node.getFinally())) return v.mismatch("optionalFinallyBody");
			return true;
		}
		
		public IPattern<List<VariableDeclarationExpression>> getResources() {
			return resources;
		}

		public void setResources(IPattern<List<VariableDeclarationExpression>> resources) {
			this.resources = resources;
		}

		public IPattern<Block> getBody() {
			return body;
		}

		public void setBody(IPattern<Block> body) {
			this.body = body;
		}

		public IPattern<List<CatchClause>> getCatchClauses() {
			return catchClauses;
		}

		public void setCatchClauses(IPattern<List<CatchClause>> catchClauses) {
			this.catchClauses = catchClauses;
		}

		public IPattern<Block> getOptionalFinallyBody() {
			return optionalFinallyBody;
		}

		public void setOptionalFinallyBody(IPattern<Block> optionalFinallyBody) {
			this.optionalFinallyBody = optionalFinallyBody;
		}

	}

	// ------------------------------------------------------------------------

	/** pattern for org.eclipse.jdt.core.dom.TypeDeclarationStatement */
	public static class TypeDeclarationStatementPattern extends StatementPattern<TypeDeclarationStatement> {

		/**
		 * The type declaration
		 */
		private IPattern<AbstractTypeDeclaration> typeDecl = null;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof TypeDeclarationStatement)) return false;
			return v.visitMatch(this, (TypeDeclarationStatement)node);
		}

		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (typeDecl != null) typeDecl.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, TypeDeclarationStatement node) {
			if (typeDecl != null && !typeDecl.acceptMatch(v, node.getDeclaration())) return v.mismatch("typeDecl");
			return true;
		}
		
		public IPattern<AbstractTypeDeclaration> getTypeDecl() {
			return typeDecl;
		}

		public void setTypeDecl(IPattern<AbstractTypeDeclaration> typeDecl) {
			this.typeDecl = typeDecl;
		}
		
	}
	

	// ------------------------------------------------------------------------
	
	/** pattern for pattern for org.eclipse.jdt.core.dom.VariableDeclarationStatement */
	public static class VariableDeclarationStatementPattern extends StatementPattern<VariableDeclarationStatement> {

		/**
		 * The extended modifiers
		 */
		private IPattern<List<IExtendedModifier>> modifiers;

		/**
		 * The modifier flags
		 */
		private IPattern<Integer> modifierFlags;

		/**
		 * The base type
		 */
		private IPattern<Type> baseType;

		/**
		 * The list of variable variable declaration fragments
		 */
		private IPattern<List<VariableDeclarationFragment>> variableDeclarationFragments;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof VariableDeclarationStatement)) return false;
			return v.visitMatch(this, (VariableDeclarationStatement)node);
		}
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (modifiers != null) modifiers.accept(v);
			if (modifierFlags != null) modifierFlags.accept(v);
			if (baseType != null) baseType.accept(v);
			if (variableDeclarationFragments != null) variableDeclarationFragments.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, VariableDeclarationStatement node) {
			if (modifiers != null && !modifiers.acceptMatch(v, node.getModifiers())) return v.mismatch("modifiers");
			if (modifierFlags != null && !modifierFlags.acceptMatch(v, node.modifiers())) return v.mismatch("modifierFlags");
			if (baseType != null && !baseType.acceptMatch(v, node.getType())) return v.mismatch("baseType");
			if (variableDeclarationFragments != null && !variableDeclarationFragments.acceptMatch(v, node.fragments())) return v.mismatch("variableDeclarationFragments");
			return true;
		}
		
		public IPattern<List<IExtendedModifier>> getModifiers() {
			return modifiers;
		}

		public void setModifiers(IPattern<List<IExtendedModifier>> p) {
			this.modifiers = p;
		}

		public IPattern<Integer> getModifierFlags() {
			return modifierFlags;
		}

		public void setModifierFlags(IPattern<Integer> p) {
			this.modifierFlags = p;
		}

		public IPattern<Type> getBaseType() {
			return baseType;
		}

		public void setBaseType(IPattern<Type> baseType) {
			this.baseType = baseType;
		}

		public IPattern<List<VariableDeclarationFragment>> getVariableDeclarationFragments() {
			return variableDeclarationFragments;
		}

		public void setVariableDeclarationFragments(IPattern<List<VariableDeclarationFragment>> p) {
			this.variableDeclarationFragments = p;
		}

		
	}

	// ------------------------------------------------------------------------
	
	/** pattern for pattern for org.eclipse.jdt.core.dom.WhileStatement */
	public static class WhileStatementPattern extends StatementPattern<WhileStatement> {
		/**
		 * The expression
		 */
		private IPattern<Expression> expression;

		/**
		 * The body statement
		 */
		private IPattern<Statement> body = null;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof WhileStatement)) return false;
			return v.visitMatch(this, (WhileStatement)node);
		}
		
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (expression != null) expression.accept(v);
			if (body != null) body.accept(v);
		}
		
		public boolean recursevisitMatch(PatternMatchVisitor v, WhileStatement node) {
			if (expression != null && !expression.acceptMatch(v, node.getExpression())) return v.mismatch("expression");
			if (body != null && !body.acceptMatch(v, node.getBody())) return v.mismatch("body");
			return true;
		}
		
		public IPattern<Expression> getExpression() {
			return expression;
		}

		public void setExpression(IPattern<Expression> expression) {
			this.expression = expression;
		}

		public IPattern<Statement> getBody() {
			return body;
		}

		public void setBody(IPattern<Statement> body) {
			this.body = body;
		}
		
	}

}
