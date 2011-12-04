package fr.an.eclipse.pattern.helper;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.CompilationUnit;

import fr.an.eclipse.pattern.ast.IPattern;
import fr.an.eclipse.pattern.matcher.ASTNodePatternMatcher;
import fr.an.eclipse.pattern.matcher.IMatchResult;
import fr.an.eclipse.pattern.matcher.IMatchResultIterator;
import fr.an.eclipse.pattern.util.ConsoleUtil;

/**
 *
 */
public class FindPatternHelper extends AbstractASTRewriteRefactoringHelper {

	private IPattern<?> pattern;
	
	public FindPatternHelper(IProgressMonitor monitor, Set<ICompilationUnit> selection, List<IJavaElement> jelts, IPattern<?> pattern) {
		super(monitor, selection, jelts);
		this.pattern = pattern;
	}
	
	// -------------------------------------------------------------------------
	
	@Override
	protected Object prepareRefactorUnit(CompilationUnit unit) throws Exception {
		Object res = null;
		
		if (javaElements != null) {
			// TODO
//			ICompilationUnit icu = (ICompilationUnit) unit.getJavaElement();
//			List<IJavaElement> jeltsCU = getJavaElementsForCompilationUnit(icu); 
//			
//			// CompilationUnit.
//			for(IJavaElement jelt : jeltsCU) {
//				// IBinding[] bindings = astParser.createBindings(new IJavaElement[] { jelt }, null);
//			}
		} else {
			// TODO
		}
		
		
		@SuppressWarnings("unchecked")
		IPattern<CompilationUnit> pattern2 = (IPattern<CompilationUnit>) pattern;

		IMatchResultIterator<CompilationUnit> matchIter = 
				ASTNodePatternMatcher.matches(pattern2, unit);
		
		Map<String,IPattern<?>> groups = matchIter.getPatternGroups();
		
		StringBuilder matchResText = new StringBuilder();
		int matchResCount = 0;
		for (IMatchResult matchRes = null; matchIter.hasNext(); matchResCount++) { // *** The Biggy ***
			matchRes = matchIter.next();
			
			matchResText.append("result[" + matchResCount + "] :"
					// ...
					+ "\n");
			for (String group : groups.keySet()) {
				Object groupRes = matchRes.group(group);
				matchResText.append("\t" + group + ":" + groupRes + "\n");
			}
		
		}
		
		ConsoleUtil.log(matchResText.toString(), new Object[0]);
		
		return res;
	}

	@Override
	protected void doRefactorUnit(CompilationUnit unit, Object refactoringInfoObject) throws Exception {
	}
	
}
