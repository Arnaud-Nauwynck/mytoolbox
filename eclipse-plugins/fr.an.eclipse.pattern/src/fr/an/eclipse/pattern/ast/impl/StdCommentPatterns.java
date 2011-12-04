package fr.an.eclipse.pattern.ast.impl;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MemberRef;
import org.eclipse.jdt.core.dom.MethodRef;
import org.eclipse.jdt.core.dom.MethodRefParameter;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.Type;

import fr.an.eclipse.pattern.ast.IPattern;
import fr.an.eclipse.pattern.ast.PatternMatchVisitor;
import fr.an.eclipse.pattern.ast.PatternVisitor;


public class StdCommentPatterns {

	/** pattern for @see org.eclipse.jdt.core.dom.Comment */
	public static class CommentPattern<T extends Comment> extends AbstractASTNodePattern<T> {
		
		/**
		 * Alternate root node pattern
		 */
		private IPattern<ASTNode> alternateRoot;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof Comment)) return false;
			return v.visitMatch(this, (Comment)node);
		}
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (alternateRoot != null) alternateRoot.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, Comment node) {
			if (alternateRoot != null && !alternateRoot.acceptMatch(v, node.getAlternateRoot())) return v.mismatch("alternateRoot");
			return true;
		}

		
		public IPattern<ASTNode> getAlternateRoot() {
			return alternateRoot;
		}

		public void setAlternateRoot(IPattern<ASTNode> alternateRoot) {
			this.alternateRoot = alternateRoot;
		}
		
	}

	// ------------------------------------------------------------------------
	
	/** pattern for @see org.eclipse.jdt.core.dom.BlockComment */
	public static class BlockCommentPattern extends CommentPattern<BlockComment> {
		// no fields

		public void recurseVisit(PatternVisitor v) {
			super.recurseVisit(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, Comment node) {
			return recursevisitMatch(v, (BlockComment) node);
		}
		
		public boolean recursevisitMatch(PatternMatchVisitor v, BlockComment node) {
			if (!super.recursevisitMatch(v, node)) return false;
			return true;
		}

	}
	
	// ------------------------------------------------------------------------
	
	/** pattern for @see org.eclipse.jdt.core.dom.Javadoc */
	public static class JavadocPattern extends CommentPattern<Javadoc> {
		
		/**
		 * The list of tag elements pattern
		 */
		private IPattern<List<TagElement>> tags;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof Javadoc)) return false;
			return v.visitMatch(this, (Javadoc) node);
		}
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			super.recurseVisit(v);
			if (tags != null) tags.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, Javadoc node) {
			if (!super.recursevisitMatch(v, node)) return false;
			if (tags != null && !tags.acceptMatch(v, node.tags())) return v.mismatch("tags");
			return true;
		}

		
		public IPattern<List<TagElement>> getTags() {
			return tags;
		}

		public void setTags(IPattern<List<TagElement>> tags) {
			this.tags = tags;
		}
				
		
	}

	// ------------------------------------------------------------------------
	
	/** pattern for @see org.eclipse.jdt.core.dom.LineComment */
	public static class LineCommentPattern extends CommentPattern<LineComment> {
		// no fields, cf sub-classes

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof LineComment)) return false;
			return v.visitMatch(this, (LineComment) node);
		}
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			super.recurseVisit(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, LineComment node) {
			if (!super.recursevisitMatch(v, node)) return false;
			return true;
		}

	}
	
	

	// ------------------------------------------------------------------------
	
	/** pattern for IDocElement */
	public static class DocElementPattern<T /*package protected.. extends IDocElement*/> extends AbstractASTNodePattern<T> {
		// no fields, cf sub-classes

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			// ... TODO package protected ... no type test on IDocElement interface!
			// if (node != null && !(node instanceof )) return false;
			return v.visitMatch(this, node);
		}
		
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			// no recurse
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, Object node) {
			// no recurse
			return true;
		}


	}
	
	// ------------------------------------------------------------------------
	
	/** pattern for org.eclipse.jdt.core.dom.TagElement */
	public static class TagElementPattern extends DocElementPattern<TagElement> {

		/**
		 * The tag name
		 */
		private IPattern<String> optionalTagName;

		/**
		 * The list of doc elements
		 */
		private IPattern<List<Object/*IDocElement*/>> fragments;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof TagElement)) return false;
			return v.visitMatch(this, (TagElement)node);
		}

		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			super.recurseVisit(v);
			if (optionalTagName != null) optionalTagName.accept(v);
			if (fragments != null) fragments.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, TagElement node) {
			if (!super.recursevisitMatch(v, node)) return false;
			if (optionalTagName != null && !optionalTagName.acceptMatch(v, node.getTagName())) return v.mismatch("optionalTagName");
			if (fragments != null && !fragments.acceptMatch(v, node.fragments())) return v.mismatch("fragments");
			return true;
		}


		
		public IPattern<String> getOptionalTagName() {
			return optionalTagName;
		}

		public void setOptionalTagName(IPattern<String> optionalTagName) {
			this.optionalTagName = optionalTagName;
		}

		public IPattern<List<Object/*IDocElement*/>> getFragments() {
			return fragments;
		}

		public void setFragments(IPattern<List<Object/*IDocElement*/>> fragments) {
			this.fragments = fragments;
		}
		
	}

	// ------------------------------------------------------------------------
	
	/** pattern for org.eclipse.jdt.core.dom.TextElement */
	public static class TextElementPattern extends DocElementPattern<TextElement> {
		/**
		 * The text element
		 */
		private IPattern<String> text;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof TextElement)) return false;
			return v.visitMatch(this, (TextElement)node);
		}
		
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			super.recurseVisit(v);
			if (text != null) text.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, TextElement node) {
			if (!super.recursevisitMatch(v, node)) return false;
			if (text != null && !text.acceptMatch(v, node.getText())) return v.mismatch("text");
			return true;
		}

		
		public IPattern<String> getText() {
			return text;
		}

		public void setText(IPattern<String> text) {
			this.text = text;
		}
		
	}
	
	// ------------------------------------------------------------------------
	
	/** pattern for org.eclipse.jdt.core.dom.MemberRef */
	public static class MemberRefPattern extends DocElementPattern<MemberRef> {

		/**
		 * The optional qualifier
		 */
		private IPattern<Name> optionalQualifier;

		/**
		 * The member name
		 */
		private IPattern<SimpleName> memberName = null;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof MemberRef)) return false;
			return v.visitMatch(this, (MemberRef)node);
		}
		
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			super.recurseVisit(v);
			if (optionalQualifier != null) optionalQualifier.accept(v);
			if (memberName != null) memberName.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, MemberRef node) {
			if (!super.recursevisitMatch(v, node)) return false;
			if (optionalQualifier != null && !optionalQualifier.acceptMatch(v, node.getQualifier())) return v.mismatch("optionalQualifier");
			if (memberName != null && !memberName.acceptMatch(v, node.getName())) return v.mismatch("memberName");
			return true;
		}

		
		public IPattern<Name> getOptionalQualifier() {
			return optionalQualifier;
		}

		public void setOptionalQualifier(IPattern<Name> optionalQualifier) {
			this.optionalQualifier = optionalQualifier;
		}

		public IPattern<SimpleName> getMemberName() {
			return memberName;
		}

		public void setMemberName(IPattern<SimpleName> memberName) {
			this.memberName = memberName;
		}

	}

	// ------------------------------------------------------------------------
	
	/** pattern for org.eclipse.jdt.core.dom.MethodRef */
	public static class MethodRefPattern extends DocElementPattern<MethodRef> {

		/**
		 * The optional qualifier
		 */
		private IPattern<Name> optionalQualifier;

		/**
		 * The method name
		 */
		private IPattern<SimpleName> methodName = null;

		/**
		 * The parameter declarations
		 */
		private IPattern<List<MethodRefParameterPattern>> parameters;
		
		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof MethodRef)) return false;
			return v.visitMatch(this, (MethodRef)node);
		}

		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			super.recurseVisit(v);
			if (optionalQualifier != null) optionalQualifier.accept(v);
			if (methodName != null) methodName.accept(v);
			if (parameters != null) parameters.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, MethodRef node) {
			if (!super.recursevisitMatch(v, node)) return false;
			if (optionalQualifier != null && !optionalQualifier.acceptMatch(v, node.getQualifier())) return v.mismatch("optionalQualifier");
			if (methodName != null && !methodName.acceptMatch(v, node.getName())) return v.mismatch("methodName");
			if (parameters != null && !parameters.acceptMatch(v, node.parameters())) return v.mismatch("parameters");
			return true;
		}

		
		public IPattern<Name> getOptionalQualifier() {
			return optionalQualifier;
		}

		public void setOptionalQualifier(IPattern<Name> optionalQualifier) {
			this.optionalQualifier = optionalQualifier;
		}

		public IPattern<SimpleName> getMethodName() {
			return methodName;
		}

		public void setMethodName(IPattern<SimpleName> methodName) {
			this.methodName = methodName;
		}

		public IPattern<List<MethodRefParameterPattern>> getParameters() {
			return parameters;
		}

		public void setParameters(IPattern<List<MethodRefParameterPattern>> parameters) {
			this.parameters = parameters;
		}
		
	}

	
	// ------------------------------------------------------------------------
	
	/** pattern for org.eclipse.jdt.core.dom.MethodRefParameter */
	public static class MethodRefParameterPattern extends DocElementPattern<MethodRefParameter> {

		/**
		 * The type
		 */
		private IPattern<Type> type = null;

		/**
		 * Indicates the last parameter of a variable arity method
		 */
		private IPattern<Boolean> variableArity;

		/**
		 * The parameter name, or <code>null</code> if none
		 */
		private IPattern<SimpleName> optionalParameterName;

		// ------------------------------------------------------------------------
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof MethodRefParameter)) return false;
			return v.visitMatch(this, (MethodRefParameter)node);
		}
		
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			super.recurseVisit(v);
			if (type != null) type.accept(v);
			if (variableArity != null) variableArity.accept(v);
			if (optionalParameterName != null) optionalParameterName.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, MethodRefParameter node) {
			if (!super.recursevisitMatch(v, node)) return false;
			if (type != null && !type.acceptMatch(v, node.getType())) return v.mismatch("type");
			if (variableArity != null && !variableArity.acceptMatch(v, node.isVarargs())) return v.mismatch("variableArity");
			if (optionalParameterName != null && !optionalParameterName.acceptMatch(v, node.getName())) return v.mismatch("optionalParameterName");
			return true;
		}

		
		public IPattern<Type> getType() {
			return type;
		}

		public void setType(IPattern<Type> type) {
			this.type = type;
		}

		public IPattern<Boolean> getVariableArity() {
			return variableArity;
		}

		public void setVariableArity(IPattern<Boolean> p) {
			this.variableArity = p;
		}

		public IPattern<SimpleName> getOptionalParameterName() {
			return optionalParameterName;
		}

		public void setOptionalParameterName(IPattern<SimpleName> p) {
			this.optionalParameterName = p;
		}
		
	}


}
