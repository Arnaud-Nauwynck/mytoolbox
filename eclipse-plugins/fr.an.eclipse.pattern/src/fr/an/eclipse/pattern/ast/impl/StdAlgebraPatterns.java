package fr.an.eclipse.pattern.ast.impl;

import java.util.List;

import fr.an.eclipse.pattern.ast.IPattern;
import fr.an.eclipse.pattern.ast.PatternMatchVisitor;
import fr.an.eclipse.pattern.ast.PatternVisitor;

public class StdAlgebraPatterns {
	
	/**
	 * a (marker) super-class for all Algebra pattern sub-classes
	 * @param <T>
	 */
	public static abstract class AbstractAlgebraPattern<T> extends AbstractASTPattern<T> {
		
	}
	
	/** the "(" ... ")" group capture pattern ... */
	public static class GroupCapturePattern<T> extends AbstractAlgebraPattern<T> {
		
		protected String name;

		protected IPattern<T> captured;
		
		// ------------------------------------------------------------------------
				
		public GroupCapturePattern() {
		}
		
		public GroupCapturePattern(String name, IPattern<T> underlying) {
			this();
			this.captured = underlying;
		}

		public IPattern<T> getUnderlying() {
			return captured;
		}

		public void setUnderlying(IPattern<T> underlying) {
			this.captured = underlying;
		}
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			return v.visitMatch(this, node);
		}
		
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (captured != null) captured.accept(v);
		}
		
		public <R> boolean recursevisitMatch(PatternMatchVisitor v, Object node) {
			if (captured != null && !captured.acceptMatch(v, node)) return v.mismatch("captured");
			return true;
		}

	}
	
	// ------------------------------------------------------------------------
	
	/** the star ("**") matcher ... accepts anything as a match */ 
	public static class WildcardPattern<T> extends AbstractAlgebraPattern<T> {

		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			return v.visitMatch(this, node);
		}
		
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			// do nothing
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, Object node) {
			// do nothing
			return true;
		}
	}

	// ------------------------------------------------------------------------
	
	public static class MatchNotPattern<T> extends AbstractAlgebraPattern<T> {

		private IPattern<T> operand;

		public IPattern<T> getOperand() {
			return operand;
		}

		public void setOperand(IPattern<T> operand) {
			this.operand = operand;
		}
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			return v.visitMatch(this, node);
		}
		
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}

		public void recurseVisit(PatternVisitor v) {
			if (operand != null) operand.accept(v);
		}

		public <R> boolean recursevisitMatch(PatternMatchVisitor v, Object node) {
			if (operand != null && !operand.acceptMatch(v, node)) return v.mismatch("operand");
			return true;
		}

	}
	
	// ------------------------------------------------------------------------
	
	public static abstract class AbstractBinaryOpPattern<T> extends AbstractAlgebraPattern<T> {
		protected IPattern<T> lhs;
		protected IPattern<T> rhs;
		
		// ------------------------------------------------------------------------

		public IPattern<T> getLhs() {
			return lhs;
		}
		public void setLhs(IPattern<T> lhs) {
			this.lhs = lhs;
		}
		public IPattern<T> getRhs() {
			return rhs;
		}
		public void setRhs(IPattern<T> rhs) {
			this.rhs = rhs;
		}
		
		public void recurseVisit(PatternVisitor v) {
			if (lhs != null) lhs.accept(v);
			if (rhs != null) rhs.accept(v);
		}
		
		public <R> boolean recursevisitMatch(PatternMatchVisitor v, Object node) {
			if (lhs != null && !lhs.acceptMatch(v, node)) return v.mismatch("lhs");
			if (rhs != null && !rhs.acceptMatch(v, node)) return v.mismatch("rhs");
			return true;
		}
	
	}
	
	
	public static abstract class MatchAndPattern<T> extends AbstractBinaryOpPattern<T> {

		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			return v.visitMatch(this, node);
		}
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}

		/** accept when lhs match AND rhs match */
		public <R> boolean recursevisitMatch(PatternMatchVisitor v, Object node) {
			if (lhs != null && !lhs.acceptMatch(v, node)) return v.mismatch("lhs");
			if (rhs != null && !rhs.acceptMatch(v, node)) return v.mismatch("rhs");
			return true;
		}
		

	}

	public static abstract class MatchOrPattern<T> extends AbstractBinaryOpPattern<T> {

		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			return v.visitMatch(this, node);
		}
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		/** accept when lhs match OR rhs match */
		public <R> boolean recursevisitMatch(PatternMatchVisitor v, Object node) {
			boolean res = false;
			if (!res) {
				res = (lhs != null && lhs.acceptMatch(v, node));
			}
			if (!res) {
				res = (rhs != null && rhs.acceptMatch(v, node));
			}
			return res;
		}



	}

	
	// ------------------------------------------------------------------------
	

	public static abstract class AbstractMatchNAryOpPattern<T> extends AbstractAlgebraPattern<T> {

		protected List<IPattern<T>> patterns;

		// ------------------------------------------------------------------------

		public List<IPattern<T>> getPatterns() {
			return patterns;
		}

		public void setPatterns(List<IPattern<T>> patterns) {
			this.patterns = patterns;
		}

		public void recurseVisit(PatternVisitor v) {
			if (patterns != null && !patterns.isEmpty()) {
				for (IPattern<?> pattern : patterns) {
					if (pattern != null) pattern.accept(v);
				}
			}
		}
		
		// WARN redefine in sub-class for AND / OR !!
		public boolean recursevisitMatch(PatternMatchVisitor v, Object node) {
			if (patterns != null && !patterns.isEmpty()) {
				for (IPattern<?> pattern : patterns) {
					if (pattern != null && !pattern.acceptMatch(v, node)) return v.mismatch("pattern");
				}
			}
			return true;
		}


		
	}
	
	// ------------------------------------------------------------------------
	
	/**
	 *
	 * @param <T>
	 */
	public static abstract class MatchAllOfPattern<T> extends AbstractMatchNAryOpPattern<T> {

		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			return v.visitMatch(this, node);
		}
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}

	}

	// ------------------------------------------------------------------------
	
	/**
	 *
	 * @param <T>
	 */
	public static abstract class MatchOneOfPattern<T> extends AbstractMatchNAryOpPattern<T> {
		
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			return v.visitMatch(this, node);
		}
		
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		

		@Override // redefine for OR !!
		public boolean recursevisitMatch(PatternMatchVisitor v, Object node) {
			boolean res = false;
			if (patterns != null && !patterns.isEmpty()) {
				for (IPattern<?> pattern : patterns) {
					if (pattern != null && pattern.acceptMatch(v, node)) {
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
	 *
	 * @param <T>
	 */
	public static class WildcardContainingPattern<T> extends AbstractAlgebraPattern<T> {

		protected IPattern<T> underlyingPattern;
		
		// ------------------------------------------------------------------------
		
		public WildcardContainingPattern() {
		}
		
		public WildcardContainingPattern(IPattern<T> underlyingPattern) {
			this.underlyingPattern = underlyingPattern;
		}

		// ------------------------------------------------------------------------

		@Override
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}

		@Override
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			return v.visitMatch(this, node);
		}

		public void recurseVisit(PatternVisitor v) {
			if (underlyingPattern != null) underlyingPattern.accept(v);
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, Object node) {
			// this implementation MUST be overloaded ... 
			if (underlyingPattern != null && !underlyingPattern.acceptMatch(v, node)) return v.mismatch("underlyingPattern");
			return true;
		}

		public IPattern<T> getUnderlyingPattern() {
			return underlyingPattern;
		}

		public void setUnderlyingPattern(IPattern<T> underlyingPattern) {
			this.underlyingPattern = underlyingPattern;
		}
		
	}
	
}
