package fr.an.eclipse.pattern.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * utility wrapper for include+exclude list of java.util.regex.Pattern
 */
public class IncludeExcludePatternList {

	public final static String PATTERN_NEGATOR= "!"; //$NON-NLS-1$

	private Pattern[] includes;
	private Pattern[] excludes;
	
	// ------------------------------------------------------------------------


	public IncludeExcludePatternList(Pattern[] includes, Pattern[] excludes) {
		super();
		this.includes = includes;
		this.excludes = excludes;
	}

	/**
	 * 
	 */
	public IncludeExcludePatternList(String[] patterns) {
		List<Pattern> tmpincludes = new ArrayList<Pattern>();
		List<Pattern> tmpexcludes = new ArrayList<Pattern>();
		if (patterns != null && patterns.length != 0) {
			for(String pattern : patterns) {
				if (pattern.startsWith(PATTERN_NEGATOR)) {
					// excluded pattern
					pattern = pattern.substring(1);
					tmpexcludes.add(Pattern.compile(pattern));
				} else {
					tmpincludes.add(Pattern.compile(pattern));
				}
			}
		}
		this.includes = (!tmpincludes.isEmpty())? tmpincludes.toArray(new Pattern[tmpincludes.size()]) : null;
		this.excludes = (!tmpexcludes.isEmpty())? tmpexcludes.toArray(new Pattern[tmpexcludes.size()]) : null;
	}
	
	// ------------------------------------------------------------------------

	public Pattern[] getIncludes() {
		return includes;
	}

	public Pattern[] getExcludes() {
		return excludes;
	}
	
	public boolean accept(String text) {
		boolean res = true;
		if (res && includes != null && includes.length != 0) {
			res = (null != findFirstMatch(text, includes)); // find at least 1 include 
		}
		if (res && excludes != null && excludes.length != 0) {
			res = (null == findFirstMatch(text, excludes)); // find no exclude
		}
		return res;
	}
	
	public static Pattern findFirstMatch(String text, Pattern[] patterns) {
		Pattern res = null;
		if (patterns == null) return null;
		for (Pattern pattern : patterns) {
			if (pattern.matcher(text).matches()) {
				res = pattern;
				break;
			}
		}
		return res;
	}
	
	
}
