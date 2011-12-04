package fr.an.eclipse.pattern.matcher;

import fr.an.eclipse.pattern.ast.IPattern;
import fr.an.eclipse.pattern.matcher.impl.MatchResultIterator;

public class ASTNodePatternMatcher {

	public static <T> IMatchResultIterator<T> matches(IPattern<T> pattern, T node) {
		MatchResultIterator<T> res = new MatchResultIterator<T>(pattern, node);
		return res;
	}
}
