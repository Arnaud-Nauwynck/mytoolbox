package fr.an.eclipse.pattern.helper;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.CompilationUnit;

import com.thoughtworks.xstream.XStream;

import fr.an.eclipse.pattern.ast.IPattern;
import fr.an.eclipse.pattern.ast.utils.PatternXStreamUtils;
import fr.an.eclipse.pattern.impl.ASTNodeToPatternBuilder;

/**
 *
 */
public class CodeToPatternHelper extends AbstractCompilationUnitActionHelper {

	private String result;
	
	// ------------------------------------------------------------------------
	
	public CodeToPatternHelper(IProgressMonitor monitor, Set<ICompilationUnit> compilationUnits, List<IJavaElement> jelts) {
		super(monitor, compilationUnits, jelts);
	}

	// ------------------------------------------------------------------------
	
	@Override
	protected void handleUnit(CompilationUnit unit) throws Exception {
		ASTNodeToPatternBuilder patternBuilder = new ASTNodeToPatternBuilder();

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

		IPattern<CompilationUnit> unitPattern = patternBuilder.toPattern(unit);
		
		XStream xstream = PatternXStreamUtils.snewXStream();
		this.result = xstream.toXML(unitPattern);
		
	}
	
	

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
}
