package fr.an.eclipse.pattern.internal.ui.search;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.search.ui.text.Match;

public class PatternSearchMatch extends Match {

	public static class PatternSearchCapturedGroupMatch {
		String groupName;
		
		ICompilationUnit compilationUnit;
		
		Object element;
		int unit;
		int offset;
		int length;
		
		
		public PatternSearchCapturedGroupMatch(String groupName, 
				ICompilationUnit compilationUnit,
				Object element, int unit, int offset, int length) {
			super();
			this.groupName = groupName;
			this.compilationUnit = compilationUnit;
			this.element = element;
			this.unit = unit;
			this.offset = offset;
			this.length = length;
		}
		
		
		public String getGroupName() {
			return groupName;
		}
		public ICompilationUnit getCompilationUnit() {
			return compilationUnit;
		}
		public Object getElement() {
			return element;
		}
		public int getUnit() {
			return unit;
		}
		public int getOffset() {
			return offset;
		}
		public int getLength() {
			return length;
		}
		
	}
	
	
	private PatternSearchCapturedGroupMatch[] capturedGroups;
	
	// ------------------------------------------------------------------------
	
	public PatternSearchMatch(Object element, int unit, int offset, int length,
			PatternSearchCapturedGroupMatch[] capturedGroups) {
		super(element, unit, offset, length);
		this.capturedGroups = capturedGroups;
	}

	public PatternSearchMatch(Object element, int offset, int length,
			PatternSearchCapturedGroupMatch[] capturedGroups) {
		this(element, UNIT_CHARACTER, offset, length, capturedGroups);
	}

	// ------------------------------------------------------------------------
	
	public PatternSearchCapturedGroupMatch[] getCapturedGroups() {
		return this.capturedGroups;
	}

	
}
