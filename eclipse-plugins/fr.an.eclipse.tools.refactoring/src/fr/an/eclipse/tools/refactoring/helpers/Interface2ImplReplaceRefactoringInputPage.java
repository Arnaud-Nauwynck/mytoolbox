package fr.an.eclipse.tools.refactoring.helpers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class Interface2ImplReplaceRefactoringInputPage extends UserInputWizardPage {

	protected Interface2ImplReplaceRefactoring fRefactoring;
	
	protected Text interface2ImplText;
	protected Text scanPackageNameText;
	protected Button detectEjbCheckbox;
	
	public Interface2ImplReplaceRefactoringInputPage(String name) {
		super(name);
	}
	
	@Override
	public void createControl(Composite parent) {
		fRefactoring= (Interface2ImplReplaceRefactoring) getRefactoring();

		Composite result= new Composite(parent, SWT.NONE);
		setControl(result);

		GridLayout layout= new GridLayout();
		layout.numColumns= 2;
		result.setLayout(layout);

		Label paramTypeLabel = new Label(result, 0);
		GridData gdLabel = new GridData();
		paramTypeLabel.setLayoutData(gdLabel);
		paramTypeLabel.setText("Interface-Impl fqns (i=c,i=c..)");
		
		interface2ImplText = new Text(result, SWT.BORDER);
		interface2ImplText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		interface2ImplText.addModifyListener(e -> {
			setPageComplete(isInputValid());
			String text = interface2ImplText.getText();
			Map<String,String> replacements = new HashMap<>();
			for(String r : text.split(",")) {
				String[] kv = r.split("=");
				replacements.put(kv[0].trim(), kv[1].trim());
			}
			fRefactoring.setReplacements(replacements);
		});
		
		Label scanPackagesLabel = new Label(result, 0);
		scanPackagesLabel.setLayoutData(new GridData());
		scanPackagesLabel.setText("scan packages");
		
		
		scanPackageNameText = new Text(result, SWT.BORDER);
		scanPackageNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		scanPackageNameText.addModifyListener(e -> {
			setPageComplete(isInputValid());
			String text = scanPackageNameText.getText();
			List<String> scanPackages = Arrays.asList(text.split(","));
			fRefactoring.setScanPackages(scanPackages);
		});
		
		
		Label detectEjbLabel = new Label(result, 0);
		detectEjbLabel.setLayoutData(new GridData());
		detectEjbLabel.setText("detect ejb @Local");
		detectEjbCheckbox = new Button(result, SWT.CHECK);
		// detectEjbCheckbox.setText("detect ejb @Local");
		detectEjbCheckbox.setSelection(true);
		detectEjbCheckbox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean value = detectEjbCheckbox.getSelection(); 
				fRefactoring.setDetectEjb(value);
			}
		});
	}
	
	private static final Pattern REPLACE_PATTERN = Pattern.compile("[\\w\\.]+=[\\w\\.]+(\\s*,\\s*[\\w\\.]+=[\\w\\.]+)*");
	
	protected boolean isInputValid() {
		boolean res = isReplaceValid() || !scanPackageNameText.getText().trim().isEmpty();
		return res;
	}
	
	protected boolean isReplaceValid() {
		String text = interface2ImplText.getText().trim();
		if (text.isEmpty()) return false;
		boolean res = REPLACE_PATTERN.matcher(text).matches();
		return res;
	}

}
