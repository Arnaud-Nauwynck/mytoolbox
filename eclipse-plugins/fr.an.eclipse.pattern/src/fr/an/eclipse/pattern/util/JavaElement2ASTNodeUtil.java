package fr.an.eclipse.pattern.util;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;


public class JavaElement2ASTNodeUtil {


	// returns the most specific ASTNode for the given compilation unit and
	// position
	public static ASTNode getNodeAtPosition(ICompilationUnit unit, int position) {
		return getNodeAtPosition(unit, position, false);
	}

	public static ASTNode getNodeAtPosition(ICompilationUnit unit, int position, boolean lookForDeclaration) {
		ASTNode node = new Visitor(lookForDeclaration).findNode(unit,position);
		return node;
	}	

	// returns the character position at the start of the given line number
	static int getPosition(String source, int lineNumber) {
		int lines = 0;
		for (int i = 0; i < source.length(); i++) {
			if (lines == lineNumber - 1)
				return i;
			if (source.charAt(i) == '\n')
				lines++;
		}

		return -1;
	}

	public static ASTNode getNode(IJavaElement element) {
		if (element instanceof ISourceReference) {
			ISourceReference ref = (ISourceReference)element;
			ICompilationUnit unit = (ICompilationUnit)element.getAncestor(IJavaElement.COMPILATION_UNIT);

			if (unit != null)
				try {
					return getNodeAtPosition(unit, ref.getSourceRange().getOffset(), true);
				} catch (JavaModelException e) {
					// we'll just return null in this situation
				}
		}

		return null;
	}

	// returns the most specific ASTNode for the given compilation unit and
	// line
	public static ASTNode getNodeAtLine(ICompilationUnit unit, int line) {
		try {
			int position = 0;
			String source = unit.getSource();
			for (char c : source.toCharArray()) {
				position++;
				if (c == '\n')
					line--;
				if (line==0)
					break;
			}

			return getNodeAtPosition(unit, position);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	// a simple visitor for finding nodes based on position
	static class Visitor extends ASTVisitor {
		ASTNode found = null;
		int position = 0;
		boolean lookForDeclaration = false;

		Visitor(boolean lookForDeclaration) {
			this.lookForDeclaration = lookForDeclaration;
		}

		public void preVisit(ASTNode node) {
			if (contains(node.getStartPosition(), node.getLength(), position)) {
				if (!lookForDeclaration)
					found = node;
				else if (node.getNodeType() == ASTNode.TYPE_DECLARATION ||
						node.getNodeType() == ASTNode.METHOD_DECLARATION ||
						node.getNodeType() == ASTNode.FIELD_DECLARATION)
					found = node;
			}
		}

		public void postVisit(ASTNode node) {
		}

		char precedingChar(char[] source, int position) {
			for (int i = position; i >= 0; i--)
				if (!Character.isWhitespace(source[i]))
					return source[i];
			return 0;
		}

		void doSearch(ICompilationUnit unit, int position) {
			IProgressMonitor monitor = new NullProgressMonitor(); // TODO
			CompilationUnit node = JavaASTUtil.parseCompilationUnit(unit, monitor);
			found = null;
			this.position = position;
			node.accept(this);
		}

		// returns the most specific ASTNode for the given compilation unit and
		// position (exception: if a block is found we try to find a statement in
		// the block to return, if possible.)
		public ASTNode findNode(ICompilationUnit unit, int position) {
			try {
				char[] source = unit.getSource().toCharArray();
				doSearch(unit, position);
				while (found != null && found.getNodeType() == ASTNode.BLOCK && 
						precedingChar(source, position) == ';')
					doSearch(unit, --position);
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
			return found;
		}

		ASTNode previousNode() {
			return null;
		}

		// return true if the given position is within start and start+length
		boolean contains(int start, int length, int position) {
			return position >= start && position < (start + length);
		}
	}
}
