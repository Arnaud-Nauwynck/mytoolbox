package fr.an.eclipse.pattern.ast.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import fr.an.eclipse.pattern.ast.IPattern;
import fr.an.eclipse.pattern.ast.PatternMatchVisitor;
import fr.an.eclipse.pattern.ast.PatternVisitor;

public class StdListPatterns {
	
	/**
	 * abstract super-class (marker) for pattern on list
	 *
	 * @param <T>
	 */
	public static abstract class AbstractListPattern<T> extends AbstractASTPattern<List<T>> {
		
	}
	
	// ------------------------------------------------------------------------
	
	/**
	 * pattern to match a list of elements by a list of corresponding patterns
	 *
	 * @param <T>
	 */
	public static class DefaultASTListPattern<T> extends AbstractListPattern<T> {
		
		private List<IPattern<T>> elements;

		// ------------------------------------------------------------------------
		
		public DefaultASTListPattern() {
		}
		
		public DefaultASTListPattern(@SuppressWarnings("unchecked") IPattern<T>... elements) {
			this(new ArrayList<IPattern<T>>(Arrays.asList(elements)));
		}
		
		public DefaultASTListPattern(List<IPattern<T>> elements) {
			super();
			this.elements = elements;
		}

		// ------------------------------------------------------------------------
		
		public List<IPattern<T>> getElements() {
			return elements;
		}

		public void setElements(List<IPattern<T>> elements) {
			this.elements = elements;
		}

		
		@SuppressWarnings("unchecked")
		@Override
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof List)) return false;
			return v.visitMatch(this, (List<T>) node);
		}

		@Override
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, List<T> node) {
			if (elements == null || elements.size() != node.size()) return false;
			boolean res = true;
			if (elements != null) {
				Iterator<IPattern<T>> elementIter = elements.iterator();
				Iterator<T> nodeIter = node.iterator();
				for(; elementIter.hasNext() && nodeIter.hasNext(); ) {
					IPattern<T> elt = elementIter.next();
					T eltNode = nodeIter.next();
					if (!elt.acceptMatch(v, eltNode)) {
						res = false;
						break;
					}
				}
			}
			return res;
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (elements != null) {
				for(IPattern<?> elt : elements) {
					elt.accept(v);
				}
			}
		}
		
	}
	
	// ------------------------------------------------------------------------
	
	/**
	 * pattern to find match one element of a list
	 *
	 * @param <T>
	 */
	public static class MatchOneEltOfListPattern<T> extends AbstractListPattern<T> {
		
		private IPattern<T> elementPattern;

		// ------------------------------------------------------------------------
		
		public MatchOneEltOfListPattern() {
		}
		
		public MatchOneEltOfListPattern(IPattern<T> elementPattern) {
			this();
			this.elementPattern = elementPattern;
		}
		
		public IPattern<T> getElementPattern() {
			return elementPattern;
		}

		public void setElementPattern(IPattern<T> elementPattern) {
			this.elementPattern = elementPattern;
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof List)) return false;
			return v.visitMatch(this, (List<T>) node);
		}

		@Override
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}

		public void recurseVisit(PatternVisitor v) {
			if (elementPattern != null) elementPattern.accept(v);
		}
		

		public boolean recursevisitMatch(PatternMatchVisitor v, List<T> node) {
			if (elementPattern == null) return true;
			boolean res = false;
			if (node != null) {
				for (T nodeElt : node) {
					if (elementPattern.acceptMatch(v, nodeElt)) {
						res = true;
						break;
					}
				}
			}
			return res;
		}
		
	}
	
	// ------------------------------------------------------------------------
	
	/**
	 * pattern to find match all elements of a list
	 *
	 * @param <T>
	 */
	public static class MatchAllEltsOfListPattern<T> extends AbstractListPattern<T> {
		
		private IPattern<T> elementPattern;

		// ------------------------------------------------------------------------
		
		public MatchAllEltsOfListPattern() {
		}
		
		public MatchAllEltsOfListPattern(IPattern<T> elementPattern) {
			this();
			this.elementPattern = elementPattern;
		}
		
		public IPattern<T> getElementPattern() {
			return elementPattern;
		}

		public void setElementPattern(IPattern<T> elementPattern) {
			this.elementPattern = elementPattern;
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof List)) return false;
			return v.visitMatch(this, (List<T>) node);
		}

		@Override
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}

		public void recurseVisit(PatternVisitor v) {
			if (elementPattern != null) elementPattern.accept(v);
		}
		

		public boolean recursevisitMatch(PatternMatchVisitor v, List<T> node) {
			if (elementPattern == null) return true;
			boolean res = true;
			if (node != null) {
				for (T nodeElt : node) {
					if (!elementPattern.acceptMatch(v, nodeElt)) {
						res = false;
						break;
					}
				}
			}
			return res;
		}
		
	}
	
}
