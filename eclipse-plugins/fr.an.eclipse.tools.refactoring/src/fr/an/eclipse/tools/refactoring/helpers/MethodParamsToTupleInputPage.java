package fr.an.eclipse.tools.refactoring.helpers;

import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class MethodParamsToTupleInputPage extends UserInputWizardPage {

	protected MethodParamsToTupleRefactoring fRefactoring;
	
	public MethodParamsToTupleInputPage(String name) {
		super(name);
	}
	
	@Override
	public void createControl(Composite parent) {
		fRefactoring= (MethodParamsToTupleRefactoring)getRefactoring();

		Composite result= new Composite(parent, SWT.NONE);
		setControl(result);
//		GridLayout layout= new GridLayout();
//		layout.numColumns= 2;
//		result.setLayout(layout);
//		RowLayouter layouter= new RowLayouter(2);
//		GridData gd= null;

	}
	

}
