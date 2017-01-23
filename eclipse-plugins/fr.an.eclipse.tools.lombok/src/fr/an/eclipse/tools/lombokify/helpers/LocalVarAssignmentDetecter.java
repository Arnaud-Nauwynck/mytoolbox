package fr.an.eclipse.tools.lombokify.helpers;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclaration;

public class LocalVarAssignmentDetecter {

	public static boolean tempHasAssignmentsOtherThanInitialization(ASTNode method, VariableDeclaration varDecl) {
		TempAssignmentFinder assignmentFinder = new TempAssignmentFinder(varDecl);
		method.accept(assignmentFinder);
		return assignmentFinder.hasAssignments();
	}
	
	// Copy&paste from org.eclipse.jdt.internal.corext.refactoring.code
	private static class TempAssignmentFinder extends ASTVisitor {
		private ASTNode fFirstAssignment;
		private IVariableBinding fTempBinding;

		TempAssignmentFinder(VariableDeclaration tempDeclaration) {
			this.fTempBinding = tempDeclaration.resolveBinding();
		}

		private boolean isNameReferenceToTemp(Name name) {
			return (this.fTempBinding == name.resolveBinding());
		}

		private boolean isAssignmentToTemp(Assignment assignment) {
			if (this.fTempBinding == null) {
				return false;
			}
			if (!(assignment.getLeftHandSide() instanceof Name))
				return false;
			Name ref = (Name) assignment.getLeftHandSide();
			return isNameReferenceToTemp(ref);
		}

		boolean hasAssignments() {
			return (this.fFirstAssignment != null);
		}

		ASTNode getFirstAssignment() {
			return this.fFirstAssignment;
		}

		public boolean visit(Assignment assignment) {
			if (!(isAssignmentToTemp(assignment))) {
				return true;
			}
			this.fFirstAssignment = assignment;
			return false;
		}

		public boolean visit(PostfixExpression postfixExpression) {
			if (postfixExpression.getOperand() == null)
				return true;
			if (!(postfixExpression.getOperand() instanceof SimpleName))
				return true;
			SimpleName simpleName = (SimpleName) postfixExpression.getOperand();
			if (!(isNameReferenceToTemp(simpleName))) {
				return true;
			}
			this.fFirstAssignment = postfixExpression;
			return false;
		}

		public boolean visit(PrefixExpression prefixExpression) {
			if (prefixExpression.getOperand() == null)
				return true;
			if (!(prefixExpression.getOperand() instanceof SimpleName))
				return true;
			if ((!(prefixExpression.getOperator().equals(PrefixExpression.Operator.DECREMENT)))
					&& (!(prefixExpression.getOperator().equals(PrefixExpression.Operator.INCREMENT))))
				return true;
			SimpleName simpleName = (SimpleName) prefixExpression.getOperand();
			if (!(isNameReferenceToTemp(simpleName))) {
				return true;
			}
			this.fFirstAssignment = prefixExpression;
			return false;
		}
	}
}
