package fr.an.eclipse.pattern.ast.impl;

import java.util.regex.Pattern;

import fr.an.eclipse.pattern.ast.PatternMatchVisitor;
import fr.an.eclipse.pattern.ast.PatternVisitor;

public class StdBasePatterns {

	/**
	 * base super-class (marker) for all values (primitive or immutable objects) pattern 
	 *
	 * @param <T>
	 */
	public static abstract class AbstractValuePattern<T> extends AbstractASTPattern<T> {
		
	}
	
	// ------------------------------------------------------------------------
	
	public static class DefaultIntegerPattern extends AbstractValuePattern<Integer> {
		
		protected Integer value;

		public Integer getValue() {
			return value;
		}

		public void setValue(Integer value) {
			this.value = value;
		}

		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof Integer)) return false;
			return v.visitMatch(this, ((Integer) node).intValue());
		}
		
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			// do nothing
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, Integer value) {
			// do nothing
			return true;
		}

	}
	

	// ------------------------------------------------------------------------
	
	public static class DefaultStringPattern extends AbstractValuePattern<String> {

		protected String value;
		
		public DefaultStringPattern() {
		}
		
		public DefaultStringPattern(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String p) {
			this.value = p;
		}
	
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof String)) return v.mismatch("obj is not a String");
			return v.visitMatch(this, (String) node);
		}
		
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			// no recurse
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, String node) {
			// no recurse
			if (value != null && !value.equals(node)) return v.mismatch("text '" + node + "' differs, expected '" + value +"'");
			return true;
		}

	}
	
	// ------------------------------------------------------------------------
	
	public static class RegexpStringPattern extends AbstractValuePattern<String> {

		protected String value;
		protected transient Pattern textPattern; 
		
		public String getRegexp() {
			return value;
		}

		public void setRegexp(String regexp) {
			this.value = regexp;
			this.textPattern = (regexp != null)? Pattern.compile(regexp) : null; 
		}
	
		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof String)) return v.mismatch("obj is not a String");
			return v.visitMatch(this, (String) node);
		}
		
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			// no recurse
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, String node) {
			// no recurse
			if (textPattern != null && !textPattern.matcher(node).matches()) return v.mismatch("text '" + node + "'does not match regexp " + textPattern);
			return true;
		}

	}
	
	// ------------------------------------------------------------------------
	
	public static class DefaultBooleanPattern extends AbstractValuePattern<Boolean> {

		protected boolean value;

		public DefaultBooleanPattern() {
		}
		
		public DefaultBooleanPattern(boolean value) {
			this.value = value;
		}

		public boolean isValue() {
			return value;
		}

		public void setValue(boolean value) {
			this.value = value;
		}

		public boolean acceptMatch0(PatternMatchVisitor v, Object node) {
			if (node != null && !(node instanceof Boolean)) return false;
			return v.visitMatch(this, (Boolean) node);
		}
		
		public void accept0(PatternVisitor v) {
			v.visit(this);
		}
		
		public void recurseVisit(PatternVisitor v) {
			// no recurse
		}

		public boolean recursevisitMatch(PatternMatchVisitor v, boolean node) {
			// no recurse
			return true;
		}

	}
	
}
