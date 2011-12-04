package fr.an.eclipse.pattern.internal.ui.search;

import java.util.Comparator;

import com.ibm.icu.text.Collator;

import org.eclipse.ui.IWorkingSet;

class WorkingSetsComparator implements Comparator<IWorkingSet[]> {

	private Collator fCollator= Collator.getInstance();

	/*
	 * @see Comparator#compare(Object, Object)
	 */
	public int compare(IWorkingSet[] w1, IWorkingSet[] w2) {
		return fCollator.compare(w1[0].getLabel(), w2[0].getLabel());
	}
}
