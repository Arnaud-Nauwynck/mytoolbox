package fr.an.eclipse.pattern.internal.ui.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.formatter.IndentManipulation;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.actions.SelectionConverter;
import org.eclipse.jdt.internal.ui.browsing.LogicalPackage;
import org.eclipse.jdt.internal.ui.dialogs.TextFieldNavigationHandler;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.util.SWTUtil;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.search.internal.ui.SearchMessages;
import org.eclipse.search.internal.ui.util.FileTypeEditor;
import org.eclipse.search.ui.ISearchPage;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.fieldassist.ContentAssistCommandAdapter;
import org.eclipse.ui.model.IWorkbenchAdapter;

import fr.an.eclipse.pattern.ast.IPattern;
import fr.an.eclipse.pattern.ast.impl.StdAlgebraPatterns.WildcardPattern;
import fr.an.eclipse.pattern.ast.impl.StdBasePatterns.DefaultBooleanPattern;
import fr.an.eclipse.pattern.ast.impl.StdDeclarationASTPatterns;
import fr.an.eclipse.pattern.ast.impl.StdDeclarationASTPatterns.CompilationUnitPattern;
import fr.an.eclipse.pattern.ast.impl.StdDeclarationASTPatterns.FieldDeclarationPattern;
import fr.an.eclipse.pattern.ast.impl.StdDeclarationASTPatterns.VariableDeclarationFragmentPattern;
import fr.an.eclipse.pattern.ast.impl.StdExpressionASTPatterns.SimpleNamePattern;
import fr.an.eclipse.pattern.ast.impl.StdListPatterns.DefaultASTListPattern;
import fr.an.eclipse.pattern.ast.impl.StdTypesASTPatterns.TypePattern;
import fr.an.eclipse.pattern.ast.utils.PatternXStreamUtils;
import fr.an.eclipse.pattern.impl.ASTNodeToPatternBuilder;
import fr.an.eclipse.pattern.internal.ui.IASTPatternHelpContextIds;
import fr.an.eclipse.pattern.util.JavaASTUtil;

@SuppressWarnings("restriction")
public class PatternSearchPage extends DialogPage implements ISearchPage {

	private static class SearchPatternData {
		private IPattern<?> pattern;
		private String patternText;

		private final int scope;
		private final IWorkingSet[] workingSets;
		private IJavaElement javaElement;
		public String[] fileNamePatterns;

		public SearchPatternData(IPattern<?> pattern, 
				IJavaElement element) {
			this(pattern, element, ISearchPageContainer.WORKSPACE_SCOPE, null);
		}

		public SearchPatternData(IPattern<?> pattern, 
				IJavaElement element, int scope, IWorkingSet[] workingSets) {
			this.pattern= pattern;
			this.scope= scope;
			this.workingSets= workingSets;

			setJavaElement(element);
		}

		public void setJavaElement(IJavaElement javaElement) {
			this.javaElement= javaElement;
		}

		public IJavaElement getJavaElement() {
			return javaElement;
		}

		public IPattern<?> getPattern() {
			return pattern;
		}
		
		public String getPatternText() {
			return patternText;
		}

		public void setPatternText(String patternText) {
			this.patternText = patternText;
		}

		public int getScope() {
			return scope;
		}

		public IWorkingSet[] getWorkingSets() {
			return workingSets;
		}

		public void store(IDialogSettings settings) {
			settings.put("scope", scope); //$NON-NLS-1$
			
			settings.put("patternText", patternText); //$NON-NLS-1$
			
			settings.put("javaElement", javaElement != null ? javaElement.getHandleIdentifier() : ""); //$NON-NLS-1$ //$NON-NLS-2$
			if (workingSets != null) {
				String[] wsIds= new String[workingSets.length];
				for (int i= 0; i < workingSets.length; i++) {
					wsIds[i]= workingSets[i].getName();
				}
				settings.put("workingSets", wsIds); //$NON-NLS-1$
			} else {
				settings.put("workingSets", new String[0]); //$NON-NLS-1$
			}
		}

		public static SearchPatternData create(IDialogSettings settings) {
			String patternText= settings.get("patternText"); //$NON-NLS-1$
			if (patternText == null || patternText.length() == 0) {
				return null;
			}
			IPattern<?> pattern;
			try {
				pattern = (IPattern<?>) PatternXStreamUtils.snewXStream().fromXML(patternText);
			} catch(Exception ex) {
				pattern = null;
				// PatternUIPlugin.logError("Failed to read pattern as xml ... using null", ex); //$NON-NLS-1$
			}
			IJavaElement elem= null;
			String handleId= settings.get("javaElement"); //$NON-NLS-1$
			if (handleId != null && handleId.length() > 0) {
				IJavaElement restored= JavaCore.create(handleId);
				if (restored != null && isSearchableType(restored) && restored.exists()) {
					elem= restored;
				}
			}
			String[] wsIds= settings.getArray("workingSets"); //$NON-NLS-1$
			IWorkingSet[] workingSets= null;
			if (wsIds != null && wsIds.length > 0) {
				IWorkingSetManager workingSetManager= PlatformUI.getWorkbench().getWorkingSetManager();
				workingSets= new IWorkingSet[wsIds.length];
				for (int i= 0; workingSets != null && i < wsIds.length; i++) {
					workingSets[i]= workingSetManager.getWorkingSet(wsIds[i]);
					if (workingSets[i] == null) {
						workingSets= null;
					}
				}
			}

			try {
				int scope= settings.getInt("scope"); //$NON-NLS-1$

				return new SearchPatternData(pattern, elem, scope, workingSets);
			} catch (NumberFormatException e) {
				return null;
			}
		}

	}



	private static final int HISTORY_SIZE= 12;

	// Dialog store id constants
	private final static String PAGE_NAME= "PatternSearchPage"; //$NON-NLS-1$
	private final static String STORE_HISTORY= "HISTORY"; //$NON-NLS-1$
	private final static String STORE_HISTORY_SIZE= "HISTORY_SIZE"; //$NON-NLS-1$

	private final List<SearchPatternData> fPreviousSearchPatterns;

	private SearchPatternData fInitialData;
	private IJavaElement fJavaElement;
	private boolean fFirstTime= true;
	private IDialogSettings fDialogSettings;

	private Combo fPatternDescriptionCombo;
	private StyledText fPatternTextEditor;
	private Label fPatternTextEditStatus;
	
	private Combo fExtensions;
	private Button fIsRegExCheckbox;
	private CLabel fStatusLabel;
	private Button fSearchDerivedCheckbox;
	private boolean fSearchDerived;

	private FileTypeEditor fFileTypeEditor;
	private ContentAssistCommandAdapter fPatterFieldContentAssist;
	private String[] fPreviousExtensions;

	
	private ISearchPageContainer fContainer;
	
	/**
	 *
	 */
	public PatternSearchPage() {
		fPreviousSearchPatterns= new ArrayList<SearchPatternData>();
	}


	//---- Action Handling ------------------------------------------------

	public boolean performAction() {
		return performNewSearch();
	}

	private boolean performNewSearch() {
		SearchPatternData data= getPatternData();

		// Setup search scope
		PatternSearchScope scope= null;
		String scopeDescription= ""; //$NON-NLS-1$

		PatternSearchScopeFactory factory= PatternSearchScopeFactory.getInstance();

		switch (getContainer().getSelectedScope()) {
			case ISearchPageContainer.WORKSPACE_SCOPE:
				scopeDescription= factory.getWorkspaceScopeDescription();
				scope= factory.createWorkspaceScope();
				break;
			case ISearchPageContainer.SELECTION_SCOPE:
				IJavaElement[] javaElements= new IJavaElement[0];
				if (getContainer().getActiveEditorInput() != null) {
					IFile file= (IFile)getContainer().getActiveEditorInput().getAdapter(IFile.class);
					if (file != null && file.exists())
						javaElements= new IJavaElement[] { JavaCore.create(file) };
				} else {
					javaElements= factory.getJavaElements(getContainer().getSelection());
				}
				scope= factory.createPatternSearchScope(javaElements);
				scopeDescription= factory.getSelectionScopeDescription(javaElements);
				break;
			case ISearchPageContainer.SELECTED_PROJECTS_SCOPE: {
				String[] projectNames= getContainer().getSelectedProjectNames();
				scope= factory.createProjectSearchScope(projectNames);
				scopeDescription= factory.getProjectScopeDescription(projectNames);
				break;
			}
			case ISearchPageContainer.WORKING_SET_SCOPE: {
				IWorkingSet[] workingSets= getContainer().getSelectedWorkingSets();
				// should not happen - just to be sure
				if (workingSets == null || workingSets.length < 1)
					return false;
				scopeDescription= factory.getWorkingSetScopeDescription(workingSets);
				scope= factory.createJavaSearchScope(workingSets);
				SearchUtil.updateLRUWorkingSets(workingSets);
			}
		}

		PatternASTQuerySpecification querySpec= null;

//		if (data.getJavaElement() != null && getPattern().equals(fInitialData.getPattern())) {
//			querySpec= new ElementQuerySpecification(data.getJavaElement(), limitTo, scope, scopeDescription);
//		} else {
//			querySpec= new PatternASTQuerySpecification(data.getPattern(), searchFor, data.isCaseSensitive(), limitTo, scope, scopeDescription);
//			data.setJavaElement(null);
//		}
		querySpec= new PatternASTQuerySpecification(data.getPattern(), 
				data.getPatternText(), 
				scope, scopeDescription);
		
		PatternSearchQuery textSearchJob= new PatternSearchQuery(querySpec);
		NewSearchUI.runQueryInBackground(textSearchJob);
		return true;
	}


	private String[] getPreviousSearchPatterns() {
		// Search results are not persistent
		int patternCount= fPreviousSearchPatterns.size();
		String [] patterns= new String[patternCount];
		for (int i= 0; i < patternCount; i++)
			patterns[i]= fPreviousSearchPatterns.get(i).getPatternText();
		return patterns;
	}

	private String getPatternText() {
		return fPatternTextEditor.getText();
	}

	private IPattern<?> getPattern() {
		IPattern<?> pattern;
		try {
			String patternText = getPatternText();
			pattern = (IPattern<?>) PatternXStreamUtils.snewXStream().fromXML(patternText);
		} catch(Exception ex) {
			// PatternUIPlugin.logError("Failed to parse pattern", ex);
			pattern = null;
		}
		return pattern;
	}

	private SearchPatternData findInPrevious(String pattern) {
		for (Iterator<SearchPatternData> iter= fPreviousSearchPatterns.iterator(); iter.hasNext();) {
			SearchPatternData element= iter.next();
			if (pattern.equals(element.getPattern())) {
				return element;
			}
		}
		return null;
	}

	/**
	 * Return search pattern data and update previous searches.
	 * An existing entry will be updated.
	 * @return the pattern data
	 */
	private SearchPatternData getPatternData() {
		String patternText= getPatternText();
		IPattern<?> pattern= getPattern();
		SearchPatternData match= findInPrevious(patternText);
		if (match != null) {
			fPreviousSearchPatterns.remove(match);
		}
		match= new SearchPatternData(
				pattern,
				fJavaElement,
				getContainer().getSelectedScope(),
				getContainer().getSelectedWorkingSets()
		);

		fPreviousSearchPatterns.add(0, match); // insert on top
		return match;
	}

	/*
	 * Implements method from IDialogPage
	 */
	@Override
	public void setVisible(boolean visible) {
		if (visible && fPatternDescriptionCombo != null) {
			if (fFirstTime) {
				fFirstTime= false;
				// Set item and text here to prevent page from resizing
				fPatternDescriptionCombo.setItems(getPreviousSearchPatterns());
				initSelections();
			}
			fPatternDescriptionCombo.setFocus();
		}
		updateOKStatus();

		IEditorInput editorInput= getContainer().getActiveEditorInput();
		getContainer().setActiveEditorCanProvideScopeSelection(editorInput != null && editorInput.getAdapter(IFile.class) != null);

		super.setVisible(visible);
	}

	public boolean isValid() {
		return true;
	}

	//---- Widget creation ------------------------------------------------


	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		readConfiguration();

		Composite result= new Composite(parent, SWT.NONE);

		GridLayout layout= new GridLayout(2, false);
		layout.horizontalSpacing= 10;
		result.setLayout(layout);

		Control expressionComposite= createExpression(result);
		expressionComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

		Label separator= new Label(result, SWT.NONE);
		separator.setVisible(false);
		GridData data= new GridData(GridData.FILL, GridData.FILL, false, false, 2, 1);
		data.heightHint= convertHeightInCharsToPixels(1) / 3;
		separator.setLayoutData(data);

//		Control fileNameIncludeMask= createFilenameIncludeMask(result);
//		fileNameIncludeMask.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
//
//		//createParticipants(result);
//
//		SelectionAdapter javaElementInitializer= new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent event) {
//				fJavaElement= null; // fInitialData.getJavaElement();??
//				setFileNameIncludeMask(getFileNameIncludeMask());
//				doPatternModified();
//			}
//		};

		addFileNameControls(result);
		
		setControl(result);

		Dialog.applyDialogFont(result);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(result, IASTPatternHelpContextIds.PATTERN_SEARCH_PAGE);
	}




	private Control createExpression(Composite parent) {
		Composite result= new Composite(parent, SWT.NONE);
		GridLayout layout= new GridLayout(2, false);
		layout.marginWidth= 0;
		layout.marginHeight= 0;
		result.setLayout(layout);

		// Pattern text + info
		Label label= new Label(result, SWT.LEFT);
		label.setText(PatternSearchMessages.SearchPage_expression_label);
		label.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 2, 1));

		// Pattern combo
		fPatternDescriptionCombo= new Combo(result, SWT.SINGLE | SWT.BORDER);
		SWTUtil.setDefaultVisibleItemCount(fPatternDescriptionCombo);
		fPatternDescriptionCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handlePatternSelected();
				updateOKStatus();
			}
		});

		TextFieldNavigationHandler.install(fPatternDescriptionCombo);
		GridData data= new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1);
		// data.widthHint= convertWidthInCharsToPixels(50);
		fPatternDescriptionCombo.setLayoutData(data);

		fPatternTextEditor = new StyledText(result, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData textLayoutData = new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1);
		textLayoutData.heightHint= convertVerticalDLUsToPixels(150);
//		textLayoutData.minimumHeight= convertVerticalDLUsToPixels(100);
		fPatternTextEditor.setLayoutData(textLayoutData);
		
		fPatternTextEditor.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				doPatternModified();
				updateOKStatus();
			}
		});

		fPatternTextEditStatus = new Label(result, SWT.NONE);
		fPatternTextEditStatus.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
		
		return result;
	}


	private void addFileNameControls(Composite group) {
		// grid layout with 2 columns

		// Line with label, combo and button
		Label label= new Label(group, SWT.LEAD);
		label.setText(SearchMessages.SearchPage_fileNamePatterns_text);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		label.setFont(group.getFont());

		fExtensions= new Combo(group, SWT.SINGLE | SWT.BORDER);
		fExtensions.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateOKStatus();
			}
		});
		GridData data= new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1);
		data.widthHint= convertWidthInCharsToPixels(50);
		fExtensions.setLayoutData(data);
		fExtensions.setFont(group.getFont());

//		Button button= new Button(group, SWT.PUSH);
//		button.setText(SearchMessages.SearchPage_browse);
//		GridData gridData= new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 1, 1);
//		gridData.widthHint= SWTUtil.getButtonWidthHint(button);
//		button.setLayoutData(gridData);
//		button.setFont(group.getFont());
//
//		fFileTypeEditor= new FileTypeEditor(fExtensions, button);

		// Text line which explains the special characters
		Label description= new Label(group, SWT.LEAD);
		description.setText(SearchMessages.SearchPage_fileNamePatterns_hint);
		description.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		description.setFont(group.getFont());

		fSearchDerivedCheckbox= new Button(group, SWT.CHECK);
		fSearchDerivedCheckbox.setText(SearchMessages.TextSearchPage_searchDerived_label);

		fSearchDerivedCheckbox.setSelection(fSearchDerived);
		fSearchDerivedCheckbox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				fSearchDerived= fSearchDerivedCheckbox.getSelection();
				writeConfiguration();
			}
		});
		fSearchDerivedCheckbox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		fSearchDerivedCheckbox.setFont(group.getFont());
  	}

	
	final void updateOKStatus() {
		boolean ok = true;
		String msg = null;
		String patternText = getPatternText();
		if (patternText == null || patternText.equals("")) {
			ok = false;
			msg = "no pattern text";
		}
		if (ok) {
			IPattern<?> pattern;
			try {
				pattern = (IPattern<?>) PatternXStreamUtils.snewXStream().fromXML(patternText);
				if (pattern == null) {
					ok = false;
					msg = "null pattern (parsed from xstream)";
				}
			} catch(Exception ex) {
				ok = false;
				msg = "failed to parse pattern (as xstream format), error=" + ex.getMessage();
			}
		}
		if (ok) {
			fPatternTextEditStatus.setText("");
			// fPatternTextEditStatus.setBackground();
			fPatternTextEditStatus.setVisible(false);
		} else {
			fPatternTextEditStatus.setText(msg);
			fPatternTextEditStatus.setVisible(true);
		}
		getContainer().setPerformActionEnabled(ok);
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.DialogPage#dispose()
	 */
	@Override
	public void dispose() {
		writeConfiguration();
		super.dispose();
	}

	private void doPatternModified() {
		if (fInitialData != null && fInitialData.getJavaElement() != null 
				&& getPattern() != null && getPattern().equals(fInitialData.getPattern()) 
				) {
			fJavaElement= fInitialData.getJavaElement();
		} else {
			fJavaElement= null;
		}
	}

	private void handlePatternSelected() {
		int selectionIndex= fPatternDescriptionCombo.getSelectionIndex();
		if (selectionIndex < 0 || selectionIndex >= fPreviousSearchPatterns.size())
			return;

		SearchPatternData initialData= fPreviousSearchPatterns.get(selectionIndex);

		fPatternDescriptionCombo.setText(initialData.getPatternText());
		fJavaElement= initialData.getJavaElement();


		if (initialData.getWorkingSets() != null)
			getContainer().setSelectedWorkingSets(initialData.getWorkingSets());
		else
			getContainer().setSelectedScope(initialData.getScope());

//		fFileTypeEditor.setFileTypes(initialData.fileNamePatterns);
		
		fInitialData= initialData;
	}

//	private Button createMethodLocationRadio(boolean isSelected) {
//		Composite specificComposite= new Composite(fLimitToGroup, SWT.NONE);
//		specificComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
//		GridLayout layout= new GridLayout(2, false);
//		layout.marginWidth= 0;
//		layout.marginHeight= 0;
//		layout.horizontalSpacing= 0;
//		specificComposite.setLayout(layout);
//
//		Button button= createButton(specificComposite, SWT.RADIO, SearchMessages.JavaSearchPage_match_locations_label, SPECIFIC_REFERENCES, isSelected);
//		fMatchLocationsLink= new Link(specificComposite, SWT.NONE);
//		fMatchLocationsLink.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
//		fMatchLocationsLink.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetDefaultSelected(SelectionEvent e) {
//				performConfigureMatchLocation();
//			}
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				performConfigureMatchLocation();
//			}
//		});
//		updateMatchLocationText();
//
//		return button;
//	}
//
//	private void updateMatchLocationText() {
//		if (fMatchLocationsLink != null) {
//			int searchFor= getSearchFor();
//			int totNum= MatchLocations.getTotalNumberOfSettings(searchFor);
//			int currNum= MatchLocations.getNumberOfSelectedSettings(fMatchLocations, searchFor);
//
//			fMatchLocationsLink.setText(Messages.format(SearchMessages.JavaSearchPage_match_location_link_label, new Object[] { new Integer(currNum), new Integer(totNum) }));
//			fMatchLocationsLink.setToolTipText(SearchMessages.JavaSearchPage_match_location_link_label_tooltip);
//		}
//	}
//
//	protected final void performLimitToSelectionChanged(Button button) {
//		if (button.getSelection()) {
//			for (int i= 0; i < fLimitTo.length; i++) {
//				Button curr= fLimitTo[i];
//				if (curr != button) {
//					curr.setSelection(false);
//				}
//			}
//		}
//		updateUseJRE();
//	}


	private void initSelections() {
		ISelection sel= getContainer().getSelection();

		IWorkbenchPage activePage= JavaPlugin.getActivePage();
		if (activePage != null) {
			IWorkbenchPart activePart= activePage.getActivePart();
			if (activePart instanceof JavaEditor) {
				JavaEditor javaEditor= (JavaEditor) activePart;
				if (javaEditor.isBreadcrumbActive()) {
					sel= javaEditor.getBreadcrumb().getSelectionProvider().getSelection();
				}
			}
		}

		SearchPatternData initData= null;

		if (sel instanceof IStructuredSelection) {
			initData= tryStructuredSelection((IStructuredSelection) sel);
		} else if (sel instanceof ITextSelection) {
			ITextSelection textSel = (ITextSelection) sel;
			IEditorPart activeEditor= getActiveEditor();
			if (activeEditor instanceof JavaEditor) {
				JavaEditor javaEditor = (JavaEditor) activeEditor;
				try {
					IJavaElement[] elements= SelectionConverter.codeResolve(javaEditor);
					if (elements != null && elements.length > 0) {
						initData= determineInitValuesFrom(elements[0]);
					}
				} catch (JavaModelException e) {
					// ignore
				}
				
				// TODO arn ...
				if (initData == null) {
					// try detect selection as AST ... 
					ICompilationUnit icu= JavaPlugin.getDefault().getWorkingCopyManager().getWorkingCopy(javaEditor.getEditorInput(), true);
					if (icu != null) {
						IProgressMonitor monitor = new NullProgressMonitor(); // TODO
						CompilationUnit cu = JavaASTUtil.parseCompilationUnit(icu, monitor);
						
						ASTNode astNode = null; // JavaElement2ASTNodeUtil.getNodeAtPosition(icu, textSel.getOffset(), false);
						NodeFinder nodeFinder = new NodeFinder(cu, textSel.getOffset(), textSel.getLength());
						astNode = nodeFinder.getCoveredNode();
					
						if (astNode != null) {
							ASTNodeToPatternBuilder patternBuilder = new ASTNodeToPatternBuilder();
							IPattern<?> pattern = patternBuilder.toPattern(astNode);
							IJavaElement elem= null;
							IWorkingSet[] workingSets= null;
							int scope= 0; //??
							initData= new SearchPatternData(pattern, elem, scope, workingSets);
							
							String patternText = PatternXStreamUtils.snewXStream().toXML(pattern);
							initData.setPatternText(patternText);
						}
					}
					
					
				}
				
				
			}
			if (initData == null) {
				initData= trySimpleTextSelection((ITextSelection) sel);
			}
		}
		if (initData == null) {
			initData= getDefaultInitValues();
		}

		fInitialData= initData;
		fJavaElement= initData.getJavaElement();

		String patternText = initData.getPatternText();
		if (patternText == null) {
			patternText = "";
		}
		fPatternTextEditor.setText(patternText);
		
//		fFileTypeEditor.setFileTypes(initData.fileNamePatterns);
	}

	private SearchPatternData tryStructuredSelection(IStructuredSelection selection) {
		if (selection == null || selection.size() > 1)
			return null;

		SearchPatternData res= null;
		Object o= selection.getFirstElement();
		IPattern<?> pattern = new WildcardPattern<Object>();
		if (o instanceof IJavaElement) {
			res= determineInitValuesFrom((IJavaElement) o);
		} else if (o instanceof LogicalPackage) {
			// LogicalPackage lp= (LogicalPackage)o;
			// lp.getElementName() 
			return new SearchPatternData(pattern, null);
		} else if (o instanceof IAdaptable) {
			IJavaElement element= (IJavaElement) ((IAdaptable) o).getAdapter(IJavaElement.class);
			if (element != null) {
				res= determineInitValuesFrom(element);
			}
		}
		if (res == null && o instanceof IAdaptable) {
			IWorkbenchAdapter adapter= (IWorkbenchAdapter)((IAdaptable)o).getAdapter(IWorkbenchAdapter.class);
			if (adapter != null) {
				// adapter.getLabel(o), 
				return new SearchPatternData(pattern, null);
			}
		}
		return res;
	}

	final static boolean isSearchableType(IJavaElement element) {
		switch (element.getElementType()) {
			case IJavaElement.PACKAGE_FRAGMENT:
			case IJavaElement.PACKAGE_DECLARATION:
			case IJavaElement.IMPORT_DECLARATION:
			case IJavaElement.TYPE:
			case IJavaElement.FIELD:
			case IJavaElement.METHOD:
				return true;
		}
		return false;
	}

	private SearchPatternData determineInitValuesFrom(IJavaElement element) {
		try {
			switch (element.getElementType()) {
				case IJavaElement.PACKAGE_FRAGMENT:
				case IJavaElement.PACKAGE_DECLARATION: {
					StdDeclarationASTPatterns.PackageDeclarationPattern pattern = 
							new StdDeclarationASTPatterns.PackageDeclarationPattern();
					// String name = element.getElementName();
					// TOADD.. decompose name as AST: ... Name(SimpleName(), Name(SimpleName(), ...)
					return new SearchPatternData(pattern, element);
				}
				case IJavaElement.IMPORT_DECLARATION: {
					IImportDeclaration declaration= (IImportDeclaration) element;
					StdDeclarationASTPatterns.ImportDeclarationPattern pattern = 
							new StdDeclarationASTPatterns.ImportDeclarationPattern();
					// String importName = declaration.getElementName();
					// TOADD.. pattern.setImportName(importName);
					// isStatic not available in IJavaElement? pattern.setIsStatic(new DefaultBooleanPattern(declaration.));
					pattern.setOnDemand(new DefaultBooleanPattern(declaration.isOnDemand()));
					return new SearchPatternData(pattern, element);
				}
				case IJavaElement.TYPE:
					// IType type = (IType) element;
					TypePattern<Type> typePattern = new TypePattern<Type>();
					// TOADD.. type.getFullyQualifiedName()
					return new SearchPatternData(typePattern, element);
				case IJavaElement.COMPILATION_UNIT: {
					IType mainType= ((ICompilationUnit) element).findPrimaryType();
					CompilationUnitPattern pattern = new CompilationUnitPattern(); 
					if (mainType != null) {
						return new SearchPatternData(pattern, mainType);
					}
					break;
				}
				case IJavaElement.CLASS_FILE: {
					return null;
				}
				case IJavaElement.FIELD: {
					IField field = (IField) element;
					FieldDeclarationPattern pattern = new FieldDeclarationPattern(); 
					// TOADD.. type, modifiers..
					List<IPattern<VariableDeclarationFragment>> frags = new ArrayList<IPattern<VariableDeclarationFragment>>();
					VariableDeclarationFragmentPattern varDeclFragPattern = new VariableDeclarationFragmentPattern();
					varDeclFragPattern.setVariableName(new SimpleNamePattern(field.getElementName()));
					frags.add(varDeclFragPattern);
					pattern.setVariableDeclarationFragments(new DefaultASTListPattern<VariableDeclarationFragment>(frags));
					return new SearchPatternData(pattern, element);
				}
				case IJavaElement.METHOD: {
					// IMethod method= (IMethod) element;
					// TOADD..
					return null; // TODO return new SearchPatternData(pattern, PatternStrings.getMethodSignature(method), element, includeMask);
				}
			}

//		} catch (JavaModelException e) { 
//			if (!e.isDoesNotExist()) {
//				ExceptionHandler.handle(e, SearchMessages.Search_Error_javaElementAccess_title, SearchMessages.Search_Error_javaElementAccess_message);
//			}
//			// element might not exist
		} catch (Exception e) {
			// ??
		}
		return null;
	}

	private SearchPatternData trySimpleTextSelection(ITextSelection selection) {
		String selectedText= selection.getText();
		if (selectedText != null && selectedText.length() > 0) {
			int i= 0;
			while (i < selectedText.length() && !IndentManipulation.isLineDelimiterChar(selectedText.charAt(i))) {
				i++;
			}
			if (i > 0) {
				IPattern<?> pattern = new WildcardPattern<Object>(); // selectedText.substring(0, i)
				return new SearchPatternData(pattern, null);
			}
		}
		return null;
	}

	private SearchPatternData getDefaultInitValues() {
		if (!fPreviousSearchPatterns.isEmpty()) {
			return fPreviousSearchPatterns.get(0);
		}

		return new SearchPatternData(new WildcardPattern<Object>(), null); //$NON-NLS-1$
	}

	/*
	 * Implements method from ISearchPage
	 */
	public void setContainer(ISearchPageContainer container) {
		fContainer= container;
	}

	/**
	 * Returns the search page's container.
	 * @return the search page container
	 */
	private ISearchPageContainer getContainer() {
		return fContainer;
	}

	private IEditorPart getActiveEditor() {
		IWorkbenchPage activePage= JavaPlugin.getActivePage();
		if (activePage != null) {
			return activePage.getActiveEditor();
		}
		return null;
	}

	//--------------- Configuration handling --------------

	/**
	 * Returns the page settings for this Java search page.
	 *
	 * @return the page settings to be used
	 */
	private IDialogSettings getDialogSettings() {
		if (fDialogSettings == null) {
			fDialogSettings= JavaPlugin.getDefault().getDialogSettingsSection(PAGE_NAME);
		}
		return fDialogSettings;
	}

	/**
	 * Initializes itself from the stored page settings.
	 */
	private void readConfiguration() {
		IDialogSettings s= getDialogSettings();

		try {
			int historySize= s.getInt(STORE_HISTORY_SIZE);
			for (int i= 0; i < historySize; i++) {
				IDialogSettings histSettings= s.getSection(STORE_HISTORY + i);
				if (histSettings != null) {
					SearchPatternData data= SearchPatternData.create(histSettings);
					if (data != null) {
						fPreviousSearchPatterns.add(data);
					}
				}
			}
		} catch (NumberFormatException e) {
			// ignore
		}
	}

	/**
	 * Stores the current configuration in the dialog store.
	 */
	private void writeConfiguration() {
		IDialogSettings s= getDialogSettings();

		int historySize= Math.min(fPreviousSearchPatterns.size(), HISTORY_SIZE);
		s.put(STORE_HISTORY_SIZE, historySize);
		for (int i= 0; i < historySize; i++) {
			IDialogSettings histSettings= s.addNewSection(STORE_HISTORY + i);
			SearchPatternData data= fPreviousSearchPatterns.get(i);
			data.store(histSettings);
		}
	}
}
