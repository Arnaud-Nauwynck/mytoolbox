package fr.an.eclipse.pattern.internal.ui.search;

import fr.an.eclipse.pattern.ast.IPattern;

public class PatternASTQuerySpecification {

	protected IPattern<Object> pattern;
	protected String patternDescription;
	
	protected PatternSearchScope fScope;
	protected String scopeDescription;
	
	// ------------------------------------------------------------------------
	

	public PatternASTQuerySpecification(IPattern<?> pattern, String patternDescription,
			PatternSearchScope fScope, String scopeDescription) {
		super();
		this.pattern = (IPattern<Object>) pattern;
		this.patternDescription = patternDescription;
		this.fScope = fScope;
		this.scopeDescription = scopeDescription;
	}

	// ------------------------------------------------------------------------

	public IPattern<Object> getPattern() {
		return pattern;
	}

	public void setPattern(IPattern<Object> pattern) {
		this.pattern = pattern;
	}

	public PatternSearchScope getScope() {
		return fScope;
	}

	public String getPatternDescription() {
		return patternDescription;
	}

	public void setPatternDescription(String patternDescription) {
		this.patternDescription = patternDescription;
	}

	public void setScope(PatternSearchScope fScope) {
		this.fScope = fScope;
	}

	public String getScopeDescription() {
		return scopeDescription;
	}

	public void setScopeDescription(String scopeDescription) {
		this.scopeDescription = scopeDescription;
	}
	
}
