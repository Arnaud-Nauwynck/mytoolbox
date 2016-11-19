package fr.an.eclipse.tools.refactoring.helpers;

import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class MethodCtxParamRemoveInputPage extends UserInputWizardPage {

	protected MethodCtxParamRemoveRefactoring fRefactoring;
	
	protected Text paramTypeText;
	
	public MethodCtxParamRemoveInputPage(String name) {
		super(name);
	}
	
	@Override
	public void createControl(Composite parent) {
		fRefactoring= (MethodCtxParamRemoveRefactoring)getRefactoring();

		Composite result= new Composite(parent, SWT.NONE);
		setControl(result);
		GridLayout layout= new GridLayout();
		layout.numColumns= 2;
		result.setLayout(layout);

		Label paramTypeLabel = new Label(result, 0);
		GridData gdLabel = new GridData();
		paramTypeLabel.setLayoutData(gdLabel);
		paramTypeLabel.setText("removed param Type");
		
		paramTypeText = new Text(result, SWT.BORDER);
		GridData gdEdit= new GridData(GridData.FILL_HORIZONTAL);
		paramTypeText.setLayoutData(gdEdit);
		
		paramTypeText.addModifyListener(e -> {
			setPageComplete(isParamTypeValid());
			fRefactoring.setRemoveParamType(paramTypeText.getText());
		});
	}
	
	protected boolean isParamTypeValid() {
		String text = paramTypeText.getText().trim();
		return !text.trim().isEmpty();
	}
	
	@Override
	public boolean canFlipToNextPage() {
		if (!isParamTypeValid()) return false;
		return super.canFlipToNextPage();
	}
	
}
