package fr.an.eclipse.tools.lombokify.helpers;

import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class LombokifyRefactoringInputPage extends UserInputWizardPage {

	protected LombokifyRefactoring fRefactoring;
	
//	protected Text scanPackageNameText;
	protected Button getterSetterCheckbox;
	protected Button valVarCheckbox;
	
	public LombokifyRefactoringInputPage(String name) {
		super(name);
	}
	
	@Override
	public void createControl(Composite parent) {
		fRefactoring= (LombokifyRefactoring) getRefactoring();

		Composite result= new Composite(parent, SWT.NONE);
		setControl(result);

		GridLayout layout= new GridLayout();
		layout.numColumns= 2;
		result.setLayout(layout);

//		Label scanPackagesLabel = new Label(result, 0);
//		scanPackagesLabel.setLayoutData(new GridData());
//		scanPackagesLabel.setText("scan packages");
//		scanPackageNameText = new Text(result, SWT.BORDER);
//		scanPackageNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		
//		scanPackageNameText.addModifyListener(e -> {
//			setPageComplete(isInputValid());
//			String text = scanPackageNameText.getText();
//			List<String> scanPackages = Arrays.asList(text.split(","));
//			fRefactoring.setScanPackages(scanPackages);
//		});

		
		
		Label getterSetterLabel = new Label(result, 0);
		GridData gdLabel = new GridData();
		getterSetterLabel.setLayoutData(gdLabel);
		getterSetterLabel.setText("use @Getter,@Setter");
		
		getterSetterCheckbox = new Button(result, SWT.CHECK);
		getterSetterCheckbox.setSelection(true);
		getterSetterCheckbox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean value = getterSetterCheckbox.getSelection(); 
				fRefactoring.setUseGetterSetter(value);
			}
		});

		Label valVarLabel = new Label(result, 0);
		valVarLabel.setLayoutData(new GridData());
		valVarLabel.setText("use val, var (JEP286)");
		
		valVarCheckbox = new Button(result, SWT.CHECK);
		valVarCheckbox.setSelection(true);
		valVarCheckbox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean value = valVarCheckbox.getSelection(); 
				fRefactoring.setUseValVar(value);
			}
		});

	}
	
	protected boolean isInputValid() {
		return true;
	}

}
