package fr.an.eclipse.pattern.internal.ui.search;

import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.text.Match;

import com.ibm.icu.text.MessageFormat;

import fr.an.eclipse.pattern.PatternUIPlugin;
import fr.an.eclipse.pattern.ast.IPattern;
import fr.an.eclipse.pattern.internal.ui.search.PatternSearchMatch.PatternSearchCapturedGroupMatch;
import fr.an.eclipse.pattern.matcher.ASTNodePatternMatcher;
import fr.an.eclipse.pattern.matcher.IMatchResult;
import fr.an.eclipse.pattern.matcher.IMatchResultIterator;
import fr.an.eclipse.pattern.util.ASTNode2IJavaElementUtil;
import fr.an.eclipse.pattern.util.JavaASTUtil;

public class PatternSearchQuery implements ISearchQuery {

	private ISearchResult fResult;

	private final PatternASTQuerySpecification fPatternData;

	
	public PatternSearchQuery(PatternASTQuerySpecification data) {
		if (data == null) {
			throw new IllegalArgumentException("data must not be null"); //$NON-NLS-1$
		}
		fPatternData= data;
	}

	public IStatus run(IProgressMonitor monitor) {
		final PatternSearchResult searchResult= (PatternSearchResult) getSearchResult();
		searchResult.removeAll();

		monitor.beginTask("pattern matching on compilations units", 100);

		// Step 1: find compilation units
		MultiStatus status = PatternUIPlugin.newMultiStatus("find compilation units in scope");
		ICompilationUnit[] icus;
		SubProgressMonitor subMonitorFind= new SubProgressMonitor(monitor, 10);
		try {
			PatternSearchScope scope = fPatternData.getScope();
			subMonitorFind.beginTask("find compilation units", scope.getRoots().length);
			icus = scope.evaluateCompilationUnitsInScope(subMonitorFind, status);
		} finally {
			subMonitorFind.done();
		}
		
		// Step 2: iterate on ICompilationUnits, parse as AST and execute ASTPAttern 
		SubProgressMonitor subMonitorPattern= new SubProgressMonitor(monitor, 90);
		subMonitorPattern.beginTask("analysing pattern in " + icus.length + " compilation units", icus.length);
		for (ICompilationUnit icu : icus) {
			if (monitor.isCanceled()) {
				return PatternUIPlugin.newStatusOK("interrupted search");
			}
			monitor.subTask("analysing " + icu);
			try {
				doAnalyseCompilationUnit(subMonitorPattern, status, icu, searchResult);
			} catch(Exception ex) {
				status.add(PatternUIPlugin.newStatusError("Failed to analyse " + icu, ex));
			}
			monitor.worked(1);
		}
		monitor.done();
		
		if (status.getChildren().length == 0) {
			String message= MessageFormat.format(PatternSearchMessages.JavaSearchQuery_status_ok_message, String.valueOf(searchResult.getMatchCount()));
			return PatternUIPlugin.newStatusOK(message);
		} else {
			return PatternUIPlugin.newMultiStatusError("Failed", status);
		}
	}


	private void doAnalyseCompilationUnit(IProgressMonitor monitor, MultiStatus status, 
			ICompilationUnit icu,
			PatternSearchResult searchResult) {
		
		CompilationUnit cuAST = JavaASTUtil.parseCompilationUnit(icu, monitor);
		
		IPattern<Object> astPattern = fPatternData.getPattern();
		
		
		
		IMatchResultIterator<Object> matchIter = ASTNodePatternMatcher.matches(astPattern, cuAST);
		
		Map<String,IPattern<?>> patternGroups = matchIter.getPatternGroups();
		final int patternGroupSize = patternGroups.size();
		// String[] patternGroupNames = new

		
		for (IMatchResult matchRes = null; matchIter.hasNext(); ) { // *** The Biggy ***
			matchRes = matchIter.next();

			PatternSearchCapturedGroupMatch[] capturedGroups = new PatternSearchCapturedGroupMatch[patternGroupSize];

			Object mainElementOfMatch = null;
			int mainElementOffset = 0;
			int mainElementLength = 0;

			int groupIndex = 0;
			for(String groupName : patternGroups.keySet()) {
				Object groupElement = matchRes.group(groupName);
				int offset = 0;
				int length = 0;
				if (groupElement instanceof ASTNode) {
					ASTNode groupASTNode = (ASTNode) groupElement;
					offset = groupASTNode.getStartPosition();
					length = groupASTNode.getLength();
					
					if (mainElementOfMatch == null) {
						mainElementOfMatch = ASTNode2IJavaElementUtil.getEnclosingJavaElementDeclaration(groupASTNode);
						mainElementOffset = offset;
						mainElementLength = length;
					}
				}
				
				PatternSearchCapturedGroupMatch groupMaptch = 
						new PatternSearchCapturedGroupMatch(groupName, icu, 
								groupElement, Match.UNIT_CHARACTER, offset, length);
				capturedGroups[groupIndex++] = groupMaptch;
			}

			if (mainElementOfMatch == null) {
				mainElementOfMatch = icu;
			}
			PatternSearchMatch match = new PatternSearchMatch(
					mainElementOfMatch, mainElementOffset, mainElementLength, 
					capturedGroups); 
			searchResult.addMatch(match);
		}
		
	}

	public String getLabel() {
		return PatternSearchMessages.PatternSearchResult_label; //JavaSearchQuery_label;
	}


	public String getResultLabel(int nMatches) {
		if (nMatches == 1) {
			Object[] args= { getSearchPatternDescription(), fPatternData.getScopeDescription() };
			return MessageFormat.format(PatternSearchMessages.PatternSearchResult_label_singular, args);
		} else {
			Object[] args= { getSearchPatternDescription(), new Integer(nMatches), fPatternData.getScopeDescription() };
			return MessageFormat.format(PatternSearchMessages.PatternSearchResult_label_singular, args);
		}
	}
	
	private String getSearchPatternDescription() {
		return fPatternData.getPatternDescription();
	}

	ImageDescriptor getImageDescriptor() {
		return JavaPluginImages.DESC_OBJS_SEARCH_DECL;
	}

	public boolean canRerun() {
		return true;
	}

	public boolean canRunInBackground() {
		return true;
	}

	public ISearchResult getSearchResult() {
		if (fResult == null) {
			PatternSearchResult result= new PatternSearchResult(this);
			new SearchResultUpdater(result);
			fResult= result;
		}
		return fResult;
	}

	PatternASTQuerySpecification getSpecification() {
		return fPatternData;
	}
}
