package com.melissadata.kettle.cleanser.ui;

import com.melissadata.cz.support.MDTab;
import com.melissadata.kettle.cleanser.*;
import com.melissadata.kettle.cleanser.MDCleanserEnum.*;
import com.melissadata.kettle.cleanser.support.MDCleanserHelper;
import com.melissadata.kettle.evaluator.ExpressionBuilderDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Props;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.step.BaseStepMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class InputFieldsTab2 implements MDTab {
	private static Class<?> PKG = InputFieldsTab2.class;
	public  HashMap<String, MDCleanserOperation> savedOperations;
	private MDCleanserHelper                     helper;
	private MDCleanserDialog                     dialog;
	private MDCleanserMeta                       cleanserMeta;
	private ScrolledComposite                    mainSComp;
	private Composite                            mainComp;
	private Rectangle                            mainBounds;
	private Label                                description;
	// Input Table
	private Composite                            inputTableComp;
	private Label                                inputTableTitle;
	private Table                                inputTable;
	private String[]                             inputTableColumns;
	private TableViewer                          tvInputFields;
	// Operation settings
	private Composite                            operationDefinitionComp;
	private Label                                inputFieldLabel;
	private Text                                 txInputField;
	private Label                                operationNameLabel;
	private CCombo                               cOperationName;
	private Label                                operationDescriptionLabel;
	private Text                                 txOperationDescription;
	private Button                               btnAddRule;
	private Button                               btnRemoveObject;
	private Composite                            rulesButtonComp;
	private HashMap<String, Button>              rulesButtonMap;
	private Color                                colorSelected;
	private Color                                colorNotSelected;
	private Composite                            ruleSettinsComp;
	private CCombo                               cRuleOperation;
	private Control[]                            ruleOperationControls;
	private CCombo                               cCasingMode;
	private Control[]                            casingModeControls;
	private CCombo                               cPunctuationMode;
	private Control[]                            punctuationModeControls;
	private CCombo                               cAbbreviationMode;
	private Control[]                            abbreviationModeControls;
	private CCombo                               cFieldDataType;
	private Control[]                            fieldDataTypeControls;
	private Button                               ckCaseSensitive;
	private Button                               ckPartialWord;
	private Button                               ckFuzzySearch;
	private Button                               ckSingleOccurence;
	private Button                               ckCasingAcronym;
	private Button                               ckUseLookupCasing;
	private Button                               ckAbriviateTargetSize;
	private Text                                 txTargetSize;
	private Group                                gOptions;
	private Button                               btnUseTrigger;
	private Button                               btnExpressionTrigger;
	private Text                                 txExpressionTrigger;
	private Button                               btnRegExTrigger;
	private Text                                 txRegExTrigger;
	private Button                               btnRegExTester;
	private Button                               btnExpressionBuilder;
	private Group                                gRegExGroup;
	private Button                               btnUseRegEx;
	private Text                                 txRegExSearch;
	private Text                                 txRegExReplace;
	private Button                               btnUseRegExTable;
	private Text                                 txRegExTablePath;
	private Group                                gTransformExpressionGroup;
	private Text                                 txTransformExpression;
	private Group                                gSearchGroup;
	private Button                               btnUseSearchTerm;
	private Text                                 txSearchTerm;
	private Text                                 txReplaceTerm;
	private Button                               btnUseSearchTable;
	private Text                                 txSearchTablePath;
	private Button                               btnSaveOperation;
	private Button                               btnRemoveOperation;
	private Group                                gTriggerGroup;
	private Composite                            saveOperationsComp;
	private HashMap<String, MDCleanserOperation> cleanserOperations;
	private MDCleanserOperation                  currentOperation;
	private CleanserRule                         currentRule;
	private TableItem                            currentTableItem;
	//private boolean clearlist = false;
	private List<RuleOption>                     currentOptions;
	private Label                                scrollPosition;

	public InputFieldsTab2(MDCleanserDialog dialog) {
		this.helper = dialog.getHelper();
		helper.setPKG(PKG);
		this.dialog = dialog;
//		cleanserData = (MDCleanserData) dialog.getData();

		// Create the tab
		CTabItem wTab = new CTabItem(dialog.getTabFolder(), SWT.NONE);
		wTab.setText(getString("Title"));
		wTab.setData(this);

		// Create a scrolling region within the tab
		ScrolledComposite wSComp = new ScrolledComposite(dialog.getTabFolder(), SWT.V_SCROLL | SWT.H_SCROLL);
		wSComp.setLayout(new FillLayout());

		// Create the composite that will hold the contents of the tab
		final Composite wComp = new Composite(wSComp, SWT.NONE);
		helper.setLook(wComp);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		wComp.setLayout(fl);

		// Description line
		description = new Label(wComp, SWT.LEFT | SWT.WRAP);
		helper.setLook(description);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment(100, helper.margin);
		fd.top = new FormAttachment(0, helper.margin);
		description.setLayoutData(fd);
		description.setText("");

		// Create Groups
		inputTableComp = createInputTableComp(wComp, null);
		Control last = createOperationGroup(wComp, inputTableComp);

		saveOperationsComp = new Composite(ruleSettinsComp/*wComp*/, SWT.None);
		helper.setLook(saveOperationsComp);
		fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		saveOperationsComp.setLayout(fl);

		fd = new FormData();
		//fd.left = new FormAttachment(60, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		//fd.top = new FormAttachment(gOptions, helper.margin);
		fd.bottom = new FormAttachment(100, helper.margin);
		saveOperationsComp.setLayoutData(fd);
		helper.setLook(saveOperationsComp);

		Label lSaveDescription = helper.addLabel(saveOperationsComp, null, "InputTab.SaveDescription");
		btnSaveOperation = new Button(saveOperationsComp, SWT.PUSH);
		btnSaveOperation.setText("Save Operation");
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment(50, -helper.margin);
		fd.top = new FormAttachment(lSaveDescription, helper.margin);
		btnSaveOperation.setLayoutData(fd);
		helper.setLook(btnSaveOperation);
		btnSaveOperation.addListener(SWT.Selection, new Listener() {
			@Override public void handleEvent(Event arg0) {
				saveOperation();
			}
		});

		btnRemoveOperation = new Button(saveOperationsComp, SWT.PUSH);
		btnRemoveOperation.setText("Remove Saved Operation");
		fd = new FormData();
		fd.left = new FormAttachment(btnSaveOperation, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		fd.top = new FormAttachment(lSaveDescription, helper.margin);
		btnRemoveOperation.setLayoutData(fd);
		helper.setLook(btnRemoveOperation);
		btnRemoveOperation.addListener(SWT.Selection, new Listener() {
			@Override public void handleEvent(Event arg0) {
				removeOperation();
			}
		});

		scrollPosition = helper.addSpacer(wComp, saveOperationsComp);

		// Fit the composite within its container (the scrolled composite)
		fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(0, 0);
		fd.right = new FormAttachment(100, 0);
		fd.bottom = new FormAttachment(100, 0);
		wComp.setLayoutData(fd);
		// Pack the composite and get its size
		wComp.pack();
		Rectangle bounds = wComp.getBounds();
		bounds.height = bounds.height + 60;

		// Initialize the Scrolled Composite with the new composite
		wSComp.setContent(wComp);
		wSComp.setExpandHorizontal(true);
		wSComp.setExpandVertical(true);
		wSComp.setMinWidth(bounds.width - 20);
		wSComp.setMinHeight(bounds.height);

		mainSComp = wSComp;
		mainComp = wComp;
		mainBounds = bounds;
		// Initialize the tab with the scrolled composite
		wTab.setControl(wSComp);
		description.setText(getString("Description"));// This is done here due to sizing of dialog
	}

	@Override public void advancedConfigChanged() {
		// Check for enablement change
		enable();
	}

	/**
	 * Called to handle enable of controls based on input settings
	 */
	public void enable() {

		ruleOperationControls[0].setVisible(false);
		ruleOperationControls[1].setVisible(false);

		fieldDataTypeControls[0].setVisible(false);
		fieldDataTypeControls[1].setVisible(false);

		casingModeControls[0].setVisible(false);
		casingModeControls[1].setVisible(false);

		punctuationModeControls[0].setVisible(false);
		punctuationModeControls[1].setVisible(false);

		abbreviationModeControls[0].setVisible(false);
		abbreviationModeControls[1].setVisible(false);

//		ruleOptionControls[0].setVisible(false);
//		ruleOptionControls[1].setVisible(false);

		for (Control child : gOptions.getChildren()) {
			child.setVisible(false);
		}

		gOptions.setVisible(false);
		gRegExGroup.setVisible(false);
		gSearchGroup.setVisible(false);
		gTransformExpressionGroup.setVisible(false);
		gTriggerGroup.setVisible(false);

//		txTargetSize.setVisible(false);

		if (Const.isEmpty(txInputField.getText())) {
			txOperationDescription.setEnabled(false);
			cOperationName.setEnabled(false);
		} else {
			txOperationDescription.setEnabled(true);
			cOperationName.setEnabled(true);
		}

		RuleOperation opr = RuleOperation.fromString(cRuleOperation.getText());
		if (opr != null) {
			gTriggerGroup.setVisible(true);

			ruleOperationControls[0].setVisible(true);
			ruleOperationControls[1].setVisible(true);

			fieldDataTypeControls[0].setVisible(true);
			fieldDataTypeControls[1].setVisible(true);
			switch (opr) {

				case OperationCase:
					casingModeControls[0].setVisible(true);
					casingModeControls[1].setVisible(true);
					gOptions.setVisible(true);
					ckCasingAcronym.setVisible(true);
//					ruleOptionControls[0].setVisible(true);
//					ruleOptionControls[1].setVisible(true);
					break;

				case OperationPunctuation:
					punctuationModeControls[0].setVisible(true);
					punctuationModeControls[1].setVisible(true);
					gOptions.setVisible(true);
					ckUseLookupCasing.setVisible(true);
					break;

				case OperationAbbreviation:
					abbreviationModeControls[0].setVisible(true);
					abbreviationModeControls[1].setVisible(true);

					gOptions.setVisible(true);
					ckAbriviateTargetSize.setVisible(true);
					txTargetSize.setVisible(true);
					ckUseLookupCasing.setVisible(true);
					break;

				case OperationExpression:
					gTransformExpressionGroup.setVisible(true);
//					ruleOptionControls[0].setVisible(true);
//					ruleOptionControls[1].setVisible(true);
					break;

				case OperationRegularExpression:
//					ruleOptionControls[0].setVisible(true);
//					ruleOptionControls[1].setVisible(true);
					gRegExGroup.setVisible(true);
					break;

				case OperationTextSearchReplace:
					gOptions.setVisible(true);
					ckCaseSensitive.setVisible(true);
					ckPartialWord.setVisible(true);
					ckFuzzySearch.setVisible(true);
					ckSingleOccurence.setVisible(true);

//					ruleOptionControls[0].setVisible(true);
//					ruleOptionControls[1].setVisible(true);
					gSearchGroup.setVisible(true);
					break;
			}
		}

		// Trigger
		if (btnUseTrigger.getSelection()) {

			btnExpressionTrigger.setEnabled(true);
			btnRegExTrigger.setEnabled(true);
			if (btnExpressionTrigger.getSelection()) {
				txExpressionTrigger.setEnabled(true);
				btnExpressionBuilder.setEnabled(true);

				txRegExTrigger.setEnabled(false);
				btnRegExTester.setEnabled(false);
			} else {
//				txExpressionTrigger.setEnabled(false);
//				txRegExTrigger.setEnabled(true);
//				btnExpressionBuilder.setEnabled(false);
//				btnRegExTester.setEnabled(true);
			}

			if (btnRegExTrigger.getSelection()) {
				txRegExTrigger.setEnabled(true);
				txExpressionTrigger.setEnabled(false);
				btnExpressionBuilder.setEnabled(false);
				btnRegExTester.setEnabled(true);
			} else {
//				txRegExTrigger.setEnabled(false);
//				txExpressionTrigger.setEnabled(true);
//				btnExpressionBuilder.setEnabled(false);
//				btnRegExTester.setEnabled(true);
			}
		} else {
			btnExpressionTrigger.setEnabled(false);
			txExpressionTrigger.setEnabled(false);
			btnRegExTrigger.setEnabled(false);
			txRegExTrigger.setEnabled(false);
			btnRegExTester.setEnabled(false);
			btnExpressionBuilder.setEnabled(false);
		}

		// Search & Repalce
		if (btnUseSearchTable.getSelection()) {
			txSearchTerm.setEnabled(false);
			txReplaceTerm.setEnabled(false);
			txSearchTablePath.setEnabled(true);
		} else {
			txSearchTerm.setEnabled(true);
			txReplaceTerm.setEnabled(true);
			txSearchTablePath.setEnabled(false);
		}

		// Reg Ex
		if (btnUseRegExTable.getSelection()) {
			txRegExSearch.setEnabled(false);
			txRegExReplace.setEnabled(false);
			txRegExTablePath.setEnabled(true);
		} else {
			txRegExSearch.setEnabled(true);
			txRegExReplace.setEnabled(true);
			txRegExTablePath.setEnabled(false);
		}

		if (cOperationName.getText().isEmpty()) {
			saveOperationsComp.setVisible(false);
		} else {
			saveOperationsComp.setVisible(true);
		}
	}

	@Override public void getData(BaseStepMeta meta) {
		MDCleanserMeta cleanerMeta = (MDCleanserMeta) meta;
		cleanerMeta.setCleanserFieldOperations(cleanserOperations);
	}

	@Override public boolean init(BaseStepMeta meta) {

		cleanserMeta = (MDCleanserMeta) meta;
		HashMap<String, MDCleanserOperation> originalMap = (HashMap<String, MDCleanserOperation>) cleanserMeta.getCleanserFieldOperations();
		cleanserOperations = new HashMap<String, MDCleanserOperation>();
		for (Entry<String, MDCleanserOperation> operationEntry : originalMap.entrySet()) {
			try {
				//System.out.println(" Init - " + operationEntry.getKey());
				cleanserOperations.put(operationEntry.getKey(), operationEntry.getValue().clone());
			} catch (CloneNotSupportedException e) {
				if (dialog.getLog() != null) {
					dialog.getLog().logError("Unable to clone Operation: " + e.getMessage());
				}
			}
		}
		savedOperations = ((MDCleanserData) cleanserMeta.getStepData()).lsSavedOperations;

		Display display = Display.getCurrent();
		colorSelected = display.getSystemColor(SWT.COLOR_BLUE);
		String holderKey = "";
		for (Entry<String, MDCleanserOperation> entry : cleanserOperations.entrySet()) {
//			final TableItem item = new TableItem(inputTable, SWT.NONE);
//			//item.setText(new String[] { entry.getKey(), entry.getValue().getOperationName(), entry.getValue().getOperationDescription() });
//			//FIXME Check Box table item set text
//			item.setText(new String[] { "",entry.getKey(), entry.getValue().getOperationName(), entry.getValue().getOperationDescription() });
//			item.setData(entry.getValue());
//			// if we don't already have it get our first
//			if (Const.isEmpty(holderKey))
//				holderKey = entry.getKey();
//			if (currentTableItem == null) {
//				currentTableItem = item;
//				inputTable.select(0);
//			}
		}

		tvInputFields.setInput(cleanserOperations.values());
		tvInputFields.getTable().layout();

		currentOperation = cleanserOperations.get(holderKey);
		if (currentOperation != null)
			updateNameCombo(currentOperation.getOperationName());

		updateOperationDetails(currentOperation);
		cOperationName.setFocus();

		currentOptions = new ArrayList<RuleOption>();
		if (currentRule != null && currentRule.getOptionList() != null) {
			currentOptions.addAll(currentRule.getOptionList());
		}
		setOptionCheckBoxes();

		enable();

		dialog.clearChanged();
		return true;
	}

	private Composite createInputTableComp(Composite parent, Control last) {

		Composite tableComp = new Composite(parent, SWT.NONE);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		tableComp.setLayout(fl);

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(description, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		fd.height = 225;
		//fd.bottom = new FormAttachment(31, -helper.margin);
		tableComp.setLayoutData(fd);
		helper.setLook(tableComp);
		tvInputFields = createInputTable(tableComp);

		tvInputFields.getTable().addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				if (tvInputFields.getTable().getSelectionIndex() >= 0) {
					MDCleanserOperation cop = (MDCleanserOperation) tvInputFields.getTable().getItem(tvInputFields.getTable().getSelectionIndex()).getData();
					currentTableItem = tvInputFields.getTable().getItem(tvInputFields.getTable().getSelectionIndex());
					updateOperationDetails(cop);
					updateNameCombo(cop.getOperationName());
				}
			}
		});

		return tableComp;
	}

	private TableViewer createInputTable(Composite parent) {
		// Give this composite a table column layout view so we can control the
		// number of columns displayed. Only a single table viewer can be held in each
		// composite in order for this to work
		if (tvInputFields != null) {
			tvInputFields = null;
		}
		TableColumnLayout tcl = new TableColumnLayout();
		parent.setLayout(tcl);
		// Create table viewer
		TableViewer tv = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		// Create columns in table viewer
		createInputColumns(parent, tv, tcl);
		// Make sure the table is layed out correctly and looks cnsistent
		Table table = tv.getTable();
		helper.setLook(table, Props.WIDGET_STYLE_TABLE);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		attachCellEditors(tv, table);
		return tv;
	}

	private void createInputColumns(Composite comp, TableViewer viewer, TableColumnLayout layout) {
		viewer.setContentProvider(new ArrayContentProvider());
		getColumnNames();
		String[] titles = inputTableColumns;
		// ############################################################

		// here we try to get an appropriate size for columns
		int shellWidth = dialog.getShell().getBounds().width;
		int columnWidth = (int) (shellWidth * .250);

		TableViewerColumn col;
		// ############################################################
		// Source Fields
		col = createSUVTableViewerColumn(viewer, titles[0], columnWidth, 1, layout);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				MDCleanserOperation operation = (MDCleanserOperation)element;
				return operation.getSourceFieldName();
			}
		});

		// ############################################################
		// Operation Name
		col = createSUVTableViewerColumn(viewer, titles[1], columnWidth, 2, layout);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				MDCleanserOperation operation = (MDCleanserOperation)element;
				return operation.getOperationName();
			}
		});


		// ############################################################
		// Operation Description
		col = createSUVTableViewerColumn(viewer, titles[2], columnWidth, 4, layout);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				MDCleanserOperation operation = (MDCleanserOperation)element;
				return operation.getOperationDescription();
			}
		});
// ############################################################
		// Pass Through
		col = createSUVTableViewerColumn(viewer, titles[3], columnWidth/7, 3, layout);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				TableItem item = (TableItem) cell.getItem();
				//final ProfileRecord profileRecord = (ProfileRecord) item.getData();
				final MDCleanserOperation operation = (MDCleanserOperation) item.getData();
				final Button button = new Button((Composite) cell.getViewerRow().getControl(), SWT.CHECK);

				button.setSelection(operation.isPassThrough());
				button.addSelectionListener(new SelectionListener() {
					@Override
					public void widgetDefaultSelected(SelectionEvent arg0) {
						//
					}

					@Override
					public void widgetSelected(SelectionEvent arg0) {
						operation.setPassThrough(button.getSelection());
						dialog.setChanged();
					}
				});
				TableEditor editor = new TableEditor(item.getParent());
				editor.grabHorizontal = true;
				editor.grabVertical = true;
				editor.horizontalAlignment = SWT.CENTER;
				editor.setEditor(button, item, cell.getColumnIndex());
				editor.layout();
			}
		});
	}

	private TableViewerColumn createSUVTableViewerColumn(TableViewer viewer, String title, int bound, int colNumber, TableColumnLayout layout) {



		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);


		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setAlignment(SWT.CENTER);

		layout.setColumnData(column, new ColumnWeightData(1, bound, true));

		return viewerColumn;
	}

	private void attachCellEditors(final TableViewer viewer, Composite parent) {
		final String[] colProps = new String[] { "SOURCE_FIELD", "OPERATION_NAME", "OPERATION_DESCRIPTION", "PASS_THRU"};
		viewer.setColumnProperties(colProps);
		viewer.setCellModifier(new ICellModifier() {
			@Override
			public boolean canModify(Object element, String property) {

				return false;
			}
			// FIXME I believe this can all be removed
			@Override
			public Object getValue(Object element, String property) {
				//System.out.println("Get Value for " + property);
				if (property.equals("SOURCE_FIELD"))
					return "SOURCE";//((ProfileRecord) element).getLength();
				else if (property.equals("OPERATION_NAME"))
					return "OPERATION_NAME";//((ProfileRecord) element).getPrecision();
				else if (property.equals("OPERATION_DESCRIPTION"))
					return "OPERATION_DESCRIPTION";//((ProfileRecord) element).getScale();
				else if (property.equals("PASS_THRU"))
					return "PASS_THRU";//((ProfileRecord) element).getSettingsString();
				return null;
			}

			@Override
			public void modify(Object element, String property, Object value) {
				TableItem tableItem = (TableItem) element;
				System.out.println("Modify for " + property);
				//ProfileRecord profileRecord = (ProfileRecord) tableItem.getData();
				if (property.equals("LENGTH") || property.equals("PRECISION") || property.equals("SCALE") || property.equals("SETTINGS")) {
					//editProfileRecord(profileRecord);
					//viewer.refresh(profileRecord);
				//	dialog.setChanged();
				} else {
					//viewer.refresh(profileRecord);
				}
				//dialog.setChanged();
			}
		});
		//CheckboxCellEditor cbCE = new CheckboxCellEditor(parent);
		viewer.setCellEditors(new CellEditor[] { new TextCellEditor(parent), new TextCellEditor(parent), new TextCellEditor(parent), new CheckboxCellEditor(parent)});
	}


	
	private void getColumnNames() {
		//inputTableColumns = getString("ColumnNames").split(",");
		//FIXME Check Box get column names
		inputTableColumns = "Source Fields,Operation Name,Operation Description,Pass Thru".split(",");
	}

	private Group createOperationGroup(Composite parent, Control last) {

		Group grpDetails = new Group(parent, SWT.NONE);
		grpDetails.setText(getString("OperationGroup.Title"));
		helper.setLook(grpDetails);
		FormLayout gl = new FormLayout();
		gl.marginWidth = 3;
		gl.marginHeight = 3;
		grpDetails.setLayout(gl);

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		// fd.bottom = new FormAttachment(90, -helper.margin);
		grpDetails.setLayoutData(fd);

		operationDefinitionComp = createOperationInfoComp(grpDetails, null);
		//scRulesButtonComp = createRuleButtonsComp(grpDetails, operationDefinitionComp);
		rulesButtonComp = createRuleButtonsComp(grpDetails, operationDefinitionComp);

		btnAddRule = new Button(grpDetails, SWT.PUSH);
		btnAddRule.setText(getString("AddRule.Label"));
		fd = new FormData();
		fd.left = new FormAttachment(5, helper.margin);
		fd.right = new FormAttachment(15, -helper.margin);
		//fd.top = new FormAttachment(scRulesButtonComp, helper.margin);
		fd.top = new FormAttachment(rulesButtonComp, helper.margin);
		btnAddRule.setLayoutData(fd);
		colorNotSelected = btnAddRule.getBackground();
		btnAddRule.addListener(SWT.Selection, new Listener() {
			@Override public void handleEvent(Event arg0) {
				addCleanserRule();
			}
		});

		btnRemoveObject = new Button(grpDetails, SWT.PUSH);
		btnRemoveObject.setText("Remove Rule");
		fd = new FormData();
		fd.left = new FormAttachment(btnAddRule, helper.margin);
		fd.right = new FormAttachment(25, -helper.margin);
		//fd.top = new FormAttachment(scRulesButtonComp, helper.margin);
		fd.top = new FormAttachment(rulesButtonComp, helper.margin);
		btnRemoveObject.setLayoutData(fd);
		colorNotSelected = btnRemoveObject.getBackground();
		btnRemoveObject.addListener(SWT.Selection, new Listener() {
			@Override public void handleEvent(Event arg0) {
				try {
					removeCleanserRule();
					enable();
				} catch (CloneNotSupportedException e) {
					System.out.println(" ERROR REMOVING RULE");
					e.printStackTrace();
				}
			}
		});

		ruleSettinsComp = createRuleSettingComp(grpDetails, btnAddRule);

		helper.setLook(operationDefinitionComp);
		//helper.setLook(scRulesButtonComp);
		helper.setLook(ruleSettinsComp);

		return grpDetails;
	}

	private void saveOperation() {

		String name = cOperationName.getText();
		boolean saved = false;
		if (Const.isEmpty(name))
			return;

		if (!savedOperations.containsKey(name)) {
			savedOperations.put(currentOperation.getOperationName(), currentOperation);
			cleanserMeta.writeSavedOperations();
			cleanserMeta.getSavedOperations();
			savedOperations = ((MDCleanserData) cleanserMeta.getStepData()).lsSavedOperations;
			saved = true;
		} else {
			String message = "There is already a saved operation named : " + name + "\nDo you want to overwrite";
			MessageBox box = new MessageBox(dialog.getShell(), SWT.YES | SWT.NO | SWT.APPLICATION_MODAL | SWT.ICON_WARNING);
			box.setText("Operation");
			box.setMessage(message);
			if (box.open() == SWT.YES) {
				savedOperations.put(currentOperation.getOperationName(), currentOperation);
				cleanserMeta.writeSavedOperations();
				cleanserMeta.getSavedOperations();
				savedOperations = ((MDCleanserData) cleanserMeta.getStepData()).lsSavedOperations;
				saved = true;
			}
		}

		if (saved) {
			String message = "Operation \"" + name + "\" has been saved.";
			MessageBox box = new MessageBox(dialog.getShell(), SWT.OK | SWT.ICON_INFORMATION);
			box.setText("Operation");
			box.setMessage(message);
			box.open();
		}

		updateNameCombo(currentOperation.getOperationName());
	}

	private void removeOperation() {

		String message = "";
		MessageBox box = new MessageBox(dialog.getShell(), SWT.YES | SWT.NO | SWT.ICON_WARNING);
		box.setText("Operation");

		String name = cOperationName.getText();
		if (Const.isEmpty(name))
			return;

		if (savedOperations.containsKey(name)) {

			message = "Remove saved operation \"" + name + "\" ?";

			box.setMessage(message);
			if (box.open() == SWT.YES) {

				savedOperations.remove(name);
				cleanserMeta.writeSavedOperations();
				cleanserMeta.getSavedOperations();
				savedOperations = ((MDCleanserData) cleanserMeta.getStepData()).lsSavedOperations;
			}
		} else {

			message = "No operation named : " + name + " Can't Remove";
			box = new MessageBox(dialog.getShell(), SWT.OK | SWT.ICON_INFORMATION);
			box.setText("Operation");
			box.setMessage(message);
			box.open();
		}

		updateNameCombo(currentOperation.getOperationName());
	}

	private Composite createOperationInfoComp(Composite parent, Control last) {

		Composite infoComp = new Composite(parent, SWT.NONE);

		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		infoComp.setLayout(fl);

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(0/* last */, 3 * helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		infoComp.setLayoutData(fd);
		helper.setLook(infoComp);

		// Input Field
		inputFieldLabel = new Label(infoComp, SWT.NONE | SWT.TOP | SWT.RIGHT);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(null, helper.margin);
		inputFieldLabel.setLayoutData(fd);
		inputFieldLabel.setText(getString("InputField.Label"));
		helper.setLook(inputFieldLabel);
		txInputField = new Text(infoComp, SWT.BORDER);
		fd = new FormData();
		fd.left = new FormAttachment(inputFieldLabel, helper.margin);
		fd.top = new FormAttachment(null, helper.margin);
		fd.right = new FormAttachment(20, -helper.margin);
		txInputField.setLayoutData(fd);
		txInputField.setEditable(false);
		helper.setLook(txInputField);

		// Operation Name
		operationNameLabel = new Label(infoComp, SWT.NONE | SWT.TOP | SWT.RIGHT);
		fd = new FormData();
		fd.left = new FormAttachment(txInputField, 5 * helper.margin);
		fd.top = new FormAttachment(null, helper.margin);
		operationNameLabel.setLayoutData(fd);
		operationNameLabel.setText(getString("OperationName.Label"));
		helper.setLook(operationNameLabel);

		cOperationName = new CCombo(infoComp, SWT.BORDER);
		fd = new FormData();
		fd.left = new FormAttachment(operationNameLabel, helper.margin);
		fd.top = new FormAttachment(null, helper.margin);
		fd.right = new FormAttachment(50, -helper.margin);
		cOperationName.setLayoutData(fd);
		helper.setLook(cOperationName);

		cOperationName.addModifyListener(new ModifyListener() {
			@Override public void modifyText(ModifyEvent arg0) {

				String hName = currentOperation.getOperationName();
				currentOperation.setOperationName(cOperationName.getText());
				if (currentTableItem != null) {
					//currentTableItem.setText(1, cOperationName.getText());
					//FIXME Check Box operation modify listener
					currentTableItem.setText(1, cOperationName.getText());
				}

				if (!(Const.isEmpty(currentOperation.getOperationName())) && !(Const.isEmpty(hName)) && !(currentOperation.getOperationName() == hName)) {
					dialog.setChanged();
				}
				enable();
			}
		});

		cOperationName.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {
				try {
					MDCleanserOperation clonedOpr = isSavedOperation(cOperationName.getText());
					if (clonedOpr != null) {
						String fieldName = currentOperation.getSourceFieldName();
						cleanserOperations.remove(fieldName);
						clonedOpr.setSourceFieldName(fieldName);
						cleanserOperations.put(fieldName, clonedOpr);
						updateOperationDetails(clonedOpr);
					} else {
						currentOperation.setOperationName(cOperationName.getText());
					}
				} catch (CloneNotSupportedException cns) {
					System.out.println("Error cloning operation: " + cns.toString());
				}

				if (currentTableItem != null) {
					currentTableItem.setText(1, cOperationName.getText());
				}
				dialog.setChanged();
			}
		});

		cOperationName.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				// Do nothing
			}

			;

			public void focusLost(FocusEvent e) {
				if (currentOperation != null)
					updateNameCombo(currentOperation.getOperationName());
			}
		});

		// Operation Description
		operationDescriptionLabel = new Label(infoComp, SWT.NONE | SWT.TOP | SWT.RIGHT);
		fd = new FormData();
		fd.left = new FormAttachment(cOperationName, 5 * helper.margin);
		fd.top = new FormAttachment(null, helper.margin);
		operationDescriptionLabel.setLayoutData(fd);
		operationDescriptionLabel.setText(getString("OperationDescription.Label"));
		helper.setLook(operationDescriptionLabel);
		txOperationDescription = new Text(infoComp, SWT.BORDER);
		fd = new FormData();
		fd.left = new FormAttachment(operationDescriptionLabel, helper.margin);
		fd.top = new FormAttachment(null, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		txOperationDescription.setLayoutData(fd);
		helper.setLook(txOperationDescription);
		txOperationDescription.addModifyListener(new ModifyListener() {
			@Override public void modifyText(ModifyEvent arg0) {
				currentOperation.setOperationDescription(txOperationDescription.getText());
				if (currentTableItem != null) {
					currentTableItem.setText(2, txOperationDescription.getText());
				}
				dialog.setChanged();
			}
		});

		txOperationDescription.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {

				dialog.setChanged();
			}
		});

		return infoComp;
	}

	private MDCleanserOperation isSavedOperation(String name) throws CloneNotSupportedException {

		MDCleanserOperation savedOp = null;
		if (savedOperations.get(name) != null)
			savedOp = savedOperations.get(name).clone();

		return savedOp;
	}

	private Composite createRuleButtonsComp(Composite parent, Control last) {

		// cleanser Objects
		rulesButtonMap = new HashMap<String, Button>();
		rulesButtonComp = new Composite(parent, SWT.BORDER);

		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		rulesButtonComp.setLayout(fl);

		FormData fd = new FormData();
		fd.left = new FormAttachment(5, helper.margin);
		fd.top = new FormAttachment(last, 5 * helper.margin);
		fd.right = new FormAttachment(95, -helper.margin);
		// fd.bottom = new FormAttachment(25, -helper.margin);
		rulesButtonComp.setLayoutData(fd);
		helper.setLook(rulesButtonComp);
		return rulesButtonComp;
	}

	private Composite createRuleSettingComp(Composite parent, Control last) {
		Composite ruleSetComp = new Composite(parent, SWT.NONE);

		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		ruleSetComp.setLayout(fl);

		FormData fd = new FormData();
		fd.left = new FormAttachment(5, helper.margin);
		fd.top = new FormAttachment(last, 3 * helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		ruleSetComp.setLayoutData(fd);
		helper.setLook(ruleSetComp);

		last = null;
		Label lDescription = helper.addLabel(ruleSetComp, last, "InputTab.RuleSetting.Description");
		last = lDescription;

		Label spacer = helper.addSpacer(ruleSetComp, last);
		// Object Enums
		ruleOperationControls = helper.addComboBox(ruleSetComp, null, "InputTab.CleanserRule");
		cRuleOperation = (CCombo) ruleOperationControls[1];
		addComboItems(cRuleOperation, RuleOperation.stringValues());
		cRuleOperation.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {
				currentOptions.clear();
				currentRule.getOptionList().clear();
				currentRule.setOperation(RuleOperation.fromString(cRuleOperation.getText()));
				updateRuleDetails(currentRule.getIndex());
			}
		});
		placeEnumCombos(ruleOperationControls, spacer);

		fieldDataTypeControls = helper.addComboBox(ruleSetComp, cAbbreviationMode, "InputTab.FieldDataType");
		cFieldDataType = (CCombo) fieldDataTypeControls[1];
		addComboItems(cFieldDataType, FieldDataType.stringValues());
		cFieldDataType.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {
				currentRule.setDataType(FieldDataType.fromString(cFieldDataType.getText()));
				updateRuleDetails(currentRule.getIndex());
			}
		});
		placeEnumCombos(fieldDataTypeControls, ruleOperationControls[1]);

		casingModeControls = helper.addComboBox(ruleSetComp, cRuleOperation, "InputTab.CasingMode");
		cCasingMode = (CCombo) casingModeControls[1];
		addComboItems(cCasingMode, CasingMode.stringValues());
		cCasingMode.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {
				currentRule.setCaseMode(CasingMode.fromString(cCasingMode.getText()));
				updateRuleDetails(currentRule.getIndex());
			}
		});
		placeEnumCombos(casingModeControls, fieldDataTypeControls[1]);

		punctuationModeControls = helper.addComboBox(ruleSetComp, cCasingMode, "InputTab.PunctuationMode");
		cPunctuationMode = (CCombo) punctuationModeControls[1];// ,
		addComboItems(cPunctuationMode, PunctuationMode.stringValues());
		cPunctuationMode.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {
				currentRule.setPunctMode(PunctuationMode.fromString(cPunctuationMode.getText()));
				updateRuleDetails(currentRule.getIndex());
			}
		});
		placeEnumCombos(punctuationModeControls, fieldDataTypeControls[1]);

		abbreviationModeControls = helper.addComboBox(ruleSetComp, cPunctuationMode, "InputTab.AbbreviationMode");
		cAbbreviationMode = (CCombo) abbreviationModeControls[1];// ,
		addComboItems(cAbbreviationMode, AbbreviationMode.stringValues());
		cAbbreviationMode.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {
				currentRule.setAbrivMode(AbbreviationMode.fromString(cAbbreviationMode.getText()));
				updateRuleDetails(currentRule.getIndex());
			}
		});
		placeEnumCombos(abbreviationModeControls, fieldDataTypeControls[1]);

		gRegExGroup = createRegExGroup(ruleSetComp, spacer/* gTriggerGroup */);
		gSearchGroup = createSearchReplaceGroup(ruleSetComp, spacer /* gTriggerGroup */);

		gOptions = createOptionsGroup(ruleSetComp, cFieldDataType);

		gTriggerGroup = createTriggerGroup(ruleSetComp, gOptions/*cRuleOption*/);
		gTransformExpressionGroup = createTransformExpressionGroup(ruleSetComp, spacer /* gTriggerGroup */);

		return ruleSetComp;
	}

	public Group createTriggerGroup(Composite parent, Control last) {
		Group triggerGroup = new Group(parent, SWT.NONE);

		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		triggerGroup.setLayout(fl);

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin);
		fd.right = new FormAttachment(gSearchGroup, -helper.margin * 5);
		triggerGroup.setLayoutData(fd);
		triggerGroup.setText("Triggers");
		helper.setLook(triggerGroup);

		Composite triggerComp = new Composite(triggerGroup, SWT.NONE);
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		triggerComp.setLayout(fl);

		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(0, helper.margin);
		triggerComp.setLayoutData(fd);
		helper.setLook(triggerComp);

		btnUseTrigger = new Button(triggerComp, SWT.CHECK);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(0, helper.margin);
		btnUseTrigger.setText("Use Trigger Expression:");
		btnUseTrigger.setLayoutData(fd);
		helper.setLook(btnUseTrigger);
		btnUseTrigger.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {
				if (currentRule != null) {
					currentRule.setUseTrigger(btnUseTrigger.getSelection());
					enable();
				}
			}
		});

		// Expression settings
		btnExpressionTrigger = new Button(triggerGroup, SWT.RADIO | SWT.RIGHT);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin * 5);
		fd.top = new FormAttachment(triggerComp, helper.margin * 3);
		fd.right = new FormAttachment(28, -helper.margin);
		btnExpressionTrigger.setText("Expression:");
		btnExpressionTrigger.setLayoutData(fd);
		helper.setLook(btnExpressionTrigger);
		btnExpressionTrigger.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {
				if (currentRule != null) {
					currentRule.setUseExpressionTrigger(btnExpressionTrigger.getSelection());
					currentRule.setUseRegExTrigger(btnRegExTrigger.getSelection());
					enable();
				}
			}
		});

		btnExpressionBuilder = new Button(triggerGroup, SWT.PUSH);
		fd = new FormData();
		// fd.left = new FormAttachment(txExpressionTrigger, helper.margin);
		fd.top = new FormAttachment(triggerComp, helper.margin * 3);
		fd.right = new FormAttachment(100, -helper.margin);
		btnExpressionBuilder.setLayoutData(fd);
		btnExpressionBuilder.setText("Expression Builder");
		helper.setLook(btnExpressionBuilder);
		btnExpressionBuilder.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {
//
//				if (btnExpressionTrigger.getSelection())
//					System.out.println(" FROM btnExpressionTrigger");
				openExpressionBuilder(txExpressionTrigger);
			}
		});

		txExpressionTrigger = new Text(triggerGroup, SWT.BORDER);
		fd = new FormData();
		fd.left = new FormAttachment(btnExpressionTrigger, helper.margin);
		fd.top = new FormAttachment(triggerComp, helper.margin * 3);
		fd.right = new FormAttachment(btnExpressionBuilder, -helper.margin);
		txExpressionTrigger.setLayoutData(fd);
		helper.setLook(txExpressionTrigger);

		txExpressionTrigger.addModifyListener(new ModifyListener() {
			@Override public void modifyText(ModifyEvent arg0) {
				if (currentRule != null) {
					currentRule.setExpressionTrigger(txExpressionTrigger.getText());
				}
			}
		});

		// /// END Expression settings

		btnRegExTrigger = new Button(triggerGroup, SWT.RADIO | SWT.RIGHT);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin * 5);
		fd.top = new FormAttachment(btnExpressionTrigger, helper.margin * 3);
		fd.right = new FormAttachment(28, -helper.margin);
		btnRegExTrigger.setText("Regular Expression:");
		btnRegExTrigger.setLayoutData(fd);
		helper.setLook(btnRegExTrigger);
		btnRegExTrigger.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {
				if (currentRule != null) {
					currentRule.setUseRegExTrigger(btnRegExTrigger.getSelection());
					currentRule.setRegExTrigger(txRegExTrigger.getText());
					enable();
				}
			}
		});

		btnRegExTester = new Button(triggerGroup, SWT.PUSH);
		fd = new FormData();
		fd.left = new FormAttachment(txExpressionTrigger, helper.margin);
		fd.top = new FormAttachment(btnExpressionBuilder, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		btnRegExTester.setLayoutData(fd);
		btnRegExTester.setText("RegEx Tester");
		helper.setLook(btnRegExTester);
		btnRegExTester.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {
				if (btnRegExTrigger.getSelection())
					openRegExTester(txRegExTrigger, null);
			}
		});

		txRegExTrigger = new Text(triggerGroup, SWT.BORDER);
		fd = new FormData();
		fd.left = new FormAttachment(btnRegExTrigger, helper.margin);
		fd.top = new FormAttachment(txExpressionTrigger, helper.margin * 2);
		fd.right = new FormAttachment(btnRegExTester, -helper.margin);
		txRegExTrigger.setLayoutData(fd);
		helper.setLook(txExpressionTrigger);

		txRegExTrigger.addModifyListener(new ModifyListener() {
			@Override public void modifyText(ModifyEvent arg0) {
				if (currentRule != null) {
					currentRule.setRegExTrigger(txRegExTrigger.getText());
				}
			}
		});

		return triggerGroup;
	}

	private Group createTransformExpressionGroup(Composite parent, Control last) {
		Group expressionGroup = new Group(parent, SWT.NONE);

		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		expressionGroup.setLayout(fl);

		FormData fd = new FormData();
		fd.left = new FormAttachment(cCasingMode, helper.margin * 5);
		fd.top = new FormAttachment(last, 0/* helper.margin */);
		fd.right = new FormAttachment(100, -helper.margin);
		fd.bottom = new FormAttachment(gTriggerGroup, 0);
		expressionGroup.setLayoutData(fd);
		expressionGroup.setText("Expression Options");
		helper.setLook(expressionGroup);

		Label spacer = helper.addSpacer(expressionGroup, null);

		Label exLabel = new Label(expressionGroup, SWT.RIGHT);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(spacer, helper.margin);
		fd.right = new FormAttachment(20, -helper.margin);
		exLabel.setText("Expression:");
		exLabel.setLayoutData(fd);
		helper.setLook(exLabel);

		txTransformExpression = new Text(expressionGroup, SWT.BORDER);
		fd = new FormData();
		fd.left = new FormAttachment(exLabel, helper.margin);
		fd.top = new FormAttachment(spacer, helper.margin);
		fd.right = new FormAttachment(80, -helper.margin);
		txTransformExpression.setLayoutData(fd);
		helper.setLook(txTransformExpression);
		txTransformExpression.addModifyListener(new ModifyListener() {
			@Override public void modifyText(ModifyEvent arg0) {
				if (currentRule != null) {
					currentRule.setTransformExpression(txTransformExpression.getText());
				}
			}
		});

		Button btnTranformExpressionBuilder = new Button(expressionGroup, SWT.PUSH);
		fd = new FormData();
		fd.left = new FormAttachment(txTransformExpression, helper.margin);
		fd.top = new FormAttachment(spacer, 0);
		btnTranformExpressionBuilder.setLayoutData(fd);
		btnTranformExpressionBuilder.setText("Expression Builder");
		helper.setLook(btnTranformExpressionBuilder);
		btnTranformExpressionBuilder.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {

				if (currentRule.getOperation() == RuleOperation.OperationExpression)
					openExpressionBuilder(txTransformExpression);
			}
		});

		txTransformExpression.addModifyListener(new ModifyListener() {
			@Override public void modifyText(ModifyEvent arg0) {
				if (currentRule != null) {
					currentRule.setTransformExpression(txTransformExpression.getText());
				}
			}
		});

		return expressionGroup;
	}

	public Group createOptionsGroup(Composite parent, Control last) {

		Group optionsGroup = new Group(parent, SWT.NONE);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		optionsGroup.setLayout(fl);

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin * 6);
		fd.right = new FormAttachment(50, -helper.margin);
		//fd.bottom = new FormAttachment(100, 0/*-helper.margin*/);
		optionsGroup.setLayoutData(fd);
		optionsGroup.setText("OPTIONS"/*getString("SerchReplaceGroup.Title")*/);
		helper.setLook(optionsGroup);

		ckCaseSensitive = new Button(optionsGroup, SWT.CHECK);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment(25, -helper.margin);
		ckCaseSensitive.setText("Case Sensitive"/*&getString("SerchReplaceGroup.Search.Label")*/);
		ckCaseSensitive.setLayoutData(fd);
		helper.setLook(ckCaseSensitive);
		ckCaseSensitive.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {
				if (currentRule != null) {
					if (ckCaseSensitive.getSelection())
						currentRule.getOptionList().add(RuleOption.OptCaseSensitive);
					else
						currentRule.getOptionList().remove(RuleOption.OptCaseSensitive);

					dialog.setChanged();
					enable();
				}
			}
		});

		ckCasingAcronym = new Button(optionsGroup, SWT.CHECK);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment(25, -helper.margin);
		ckCasingAcronym.setText("Casing Acronym"/*&getString("SerchReplaceGroup.Search.Label")*/);
		ckCasingAcronym.setLayoutData(fd);
		helper.setLook(ckCasingAcronym);
		ckCasingAcronym.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {
				if (currentRule != null) {
					if (ckCasingAcronym.getSelection())
						currentRule.getOptionList().add(RuleOption.OptCasingAcronym);
					else
						currentRule.getOptionList().remove(RuleOption.OptCasingAcronym);
//					currentRule.setUseSearchTerm(btnUseSearchTerm.getSelection());
//					currentRule.setUseSearchTable(btnUseSearchTable.getSelection());
					dialog.setChanged();
					enable();
				}
			}
		});

		ckUseLookupCasing = new Button(optionsGroup, SWT.CHECK);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment(25, -helper.margin);
		ckUseLookupCasing.setText("Use Lookup Casing"/*&getString("SerchReplaceGroup.Search.Label")*/);
		ckUseLookupCasing.setLayoutData(fd);
		helper.setLook(ckUseLookupCasing);
		ckUseLookupCasing.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {
				if (currentRule != null) {
					if (ckUseLookupCasing.getSelection())
						currentRule.getOptionList().add(RuleOption.OptUseLookupCasing);
					else
						currentRule.getOptionList().remove(RuleOption.OptUseLookupCasing);
//					currentRule.setUseSearchTerm(btnUseSearchTerm.getSelection());
//					currentRule.setUseSearchTable(btnUseSearchTable.getSelection());
					dialog.setChanged();
					enable();
				}
			}
		});

		ckPartialWord = new Button(optionsGroup, SWT.CHECK);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(ckUseLookupCasing, helper.margin);
		fd.right = new FormAttachment(25, -helper.margin);
		ckPartialWord.setText("Partial Word" /*getString("SerchReplaceGroup.Replace.Label")*/);
		ckPartialWord.setLayoutData(fd);
		helper.setLook(ckPartialWord);
		ckPartialWord.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {
				if (currentRule != null) {
					if (ckPartialWord.getSelection())
						currentRule.getOptionList().add(RuleOption.OptPartialWord);
					else
						currentRule.getOptionList().remove(RuleOption.OptPartialWord);
//					currentRule.setUseSearchTerm(btnUseSearchTerm.getSelection());
//					currentRule.setUseSearchTable(btnUseSearchTable.getSelection());
					dialog.setChanged();
					enable();
				}
			}
		});

		ckAbriviateTargetSize = new Button(optionsGroup, SWT.CHECK);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(ckUseLookupCasing, helper.margin);
		fd.right = new FormAttachment(25, -helper.margin);
		ckAbriviateTargetSize.setText("Abriviate Target Size" /*getString("SerchReplaceGroup.Replace.Label")*/);
		ckAbriviateTargetSize.setLayoutData(fd);
		helper.setLook(ckAbriviateTargetSize);
		ckAbriviateTargetSize.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {
				if (currentRule != null) {
					if (ckAbriviateTargetSize.getSelection())
						currentRule.getOptionList().add(RuleOption.OptAbbreviateTargetSize);
					else
						currentRule.getOptionList().remove(RuleOption.OptAbbreviateTargetSize);
//					currentRule.setUseSearchTerm(btnUseSearchTerm.getSelection());
//					currentRule.setUseSearchTable(btnUseSearchTable.getSelection());
					dialog.setChanged();
					enable();
				}
			}
		});

		txTargetSize = new Text(optionsGroup, SWT.BORDER);
		fd = new FormData();
		fd.left = new FormAttachment(ckAbriviateTargetSize, helper.margin);
		fd.top = new FormAttachment(ckUseLookupCasing, helper.margin);
		fd.right = new FormAttachment(40, -helper.margin);
		txTargetSize.setLayoutData(fd);
		helper.setLook(txTargetSize);

		txTargetSize.addModifyListener(new ModifyListener() {
			@Override public void modifyText(ModifyEvent arg0) {
				if (currentRule != null) {
					currentRule.setAbrivTargetSize(txTargetSize.getText());
					//currentRule.setSearch_ReplaceTerm(txReplaceTerm.getText());
					dialog.setChanged();
				}
			}
		});

		ckFuzzySearch = new Button(optionsGroup, SWT.CHECK);
		fd = new FormData();
		fd.left = new FormAttachment(25, helper.margin);
		fd.top = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment(50, -helper.margin);
		ckFuzzySearch.setText("Fuzzy Search");
		ckFuzzySearch.setLayoutData(fd);
		helper.setLook(ckFuzzySearch);
		ckFuzzySearch.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {
				if (currentRule != null) {
					if (ckFuzzySearch.getSelection())
						currentRule.getOptionList().add(RuleOption.OptFuzzySearch);
					else
						currentRule.getOptionList().remove(RuleOption.OptFuzzySearch);
//					currentRule.setUseSearchTerm(btnUseSearchTerm.getSelection());
//					currentRule.setUseSearchTable(btnUseSearchTable.getSelection());
					dialog.setChanged();
					enable();
				}
			}
		});

		ckSingleOccurence = new Button(optionsGroup, SWT.CHECK);
		fd = new FormData();
		fd.left = new FormAttachment(25, helper.margin);
		fd.top = new FormAttachment(ckFuzzySearch, helper.margin);
		fd.right = new FormAttachment(50, -helper.margin);
		ckSingleOccurence.setText("Single Occurance");
		ckSingleOccurence.setLayoutData(fd);
		helper.setLook(ckSingleOccurence);
		ckSingleOccurence.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {
				if (currentRule != null) {
					if (ckSingleOccurence.getSelection())
						currentRule.getOptionList().add(RuleOption.OptSingleOccurrence);
					else
						currentRule.getOptionList().remove(RuleOption.OptSingleOccurrence);
//					currentRule.setUseSearchTerm(btnUseSearchTerm.getSelection());
//					currentRule.setUseSearchTable(btnUseSearchTable.getSelection());
					dialog.setChanged();
					enable();
				}
			}
		});

		return optionsGroup;
	}

	private Group createRegExGroup(Composite parent, Control last) {
		Group regExGroup = new Group(parent, SWT.NONE);

		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		regExGroup.setLayout(fl);

		FormData fd = new FormData();
		fd.left = new FormAttachment(cCasingMode, helper.margin * 5);
		fd.top = new FormAttachment(last, 0/* helper.margin */);
		fd.right = new FormAttachment(100, -helper.margin);
		//	fd.bottom = new FormAttachment(100, 0);
		regExGroup.setLayoutData(fd);
		regExGroup.setText(getString("RegExGroup.Title"));
		helper.setLook(regExGroup);

		btnUseRegEx = new Button(regExGroup, SWT.RADIO);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(0, helper.margin);
		btnUseRegEx.setText(getString("RegExGroup.UseExpression"));
		btnUseRegEx.setLayoutData(fd);
		helper.setLook(btnUseRegEx);
		btnUseRegEx.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {
				if (currentRule != null) {
					currentRule.setUseRegEx(btnUseRegEx.getSelection());
					currentRule.setUseRegExTable(btnUseRegExTable.getSelection());
					enable();
				}
			}
		});

		Label exLabel = new Label(regExGroup, SWT.RIGHT);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(btnUseRegEx, helper.margin);
		fd.right = new FormAttachment(20, -helper.margin);
		exLabel.setText(getString("RegExGroup.Search.Label"));
		exLabel.setLayoutData(fd);
		helper.setLook(exLabel);

		txRegExSearch = new Text(regExGroup, SWT.BORDER);
		fd = new FormData();
		fd.left = new FormAttachment(exLabel, helper.margin);
		fd.top = new FormAttachment(btnUseRegEx, helper.margin);
		fd.right = new FormAttachment(80, -helper.margin);
		txRegExSearch.setLayoutData(fd);
		helper.setLook(txRegExSearch);

		Button btnRegExTester = new Button(regExGroup, SWT.PUSH);
		fd = new FormData();
		fd.left = new FormAttachment(txRegExSearch, helper.margin);
		fd.top = new FormAttachment(btnUseRegEx, 0);
		btnRegExTester.setLayoutData(fd);
		btnRegExTester.setText(getString("RegExGroup.RegExTest.Label"));
		helper.setLook(btnRegExTester);

		btnRegExTester.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {

				if (btnUseRegEx.getSelection())
					openRegExTester(txRegExSearch, txRegExReplace);
			}
		});

		txRegExSearch.addModifyListener(new ModifyListener() {
			@Override public void modifyText(ModifyEvent arg0) {
				if (currentRule != null) {
					currentRule.setRegExSearch(txRegExSearch.getText());
				}
			}
		});

		Label replaceLabel = new Label(regExGroup, SWT.RIGHT);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(txRegExSearch, helper.margin);
		fd.right = new FormAttachment(20, -helper.margin);
		replaceLabel.setText(getString("RegExGroup.Replace.Label"));
		replaceLabel.setLayoutData(fd);
		helper.setLook(replaceLabel);

		txRegExReplace = new Text(regExGroup, SWT.BORDER);
		fd = new FormData();
		fd.left = new FormAttachment(replaceLabel, helper.margin);
		fd.top = new FormAttachment(txRegExSearch, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		txRegExReplace.setLayoutData(fd);
		helper.setLook(txRegExReplace);

		txRegExReplace.addModifyListener(new ModifyListener() {
			@Override public void modifyText(ModifyEvent arg0) {
				if (currentRule != null) {
					currentRule.setRegExReplace(txRegExReplace.getText());
				}
			}
		});

		btnUseRegExTable = new Button(regExGroup, SWT.RADIO);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(txRegExReplace, 2 * helper.margin);
		btnUseRegExTable.setText(getString("RegExGroup.UseTable.Label"));
		btnUseRegExTable.setLayoutData(fd);
		helper.setLook(btnUseRegExTable);
		btnUseRegExTable.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {
				if (currentRule != null) {
					currentRule.setUseRegEx(btnUseRegEx.getSelection());
					currentRule.setUseRegExTable(btnUseRegExTable.getSelection());
					enable();
				}
			}
		});

		txRegExTablePath = helper.createPathControl(regExGroup, btnUseRegExTable, "InputTab.RegExGroup.SearchTablePath", false);

		txRegExTablePath.addModifyListener(new ModifyListener() {
			@Override public void modifyText(ModifyEvent arg0) {
				if (currentRule != null) {
					currentRule.setRegExTablePath(txRegExTablePath.getText());
				}
			}
		});

		return regExGroup;
	}

	public Group createSearchReplaceGroup(Composite parent, Control last) {

		Group searchReplageGroup = new Group(parent, SWT.None);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		searchReplageGroup.setLayout(fl);

		FormData fd = new FormData();
		fd.left = new FormAttachment(cCasingMode, helper.margin * 5);
		fd.top = new FormAttachment(last, 0/* helper.margin */);
		fd.right = new FormAttachment(100, -helper.margin);
		//	fd.bottom = new FormAttachment(100, 0/*-helper.margin*/);
		searchReplageGroup.setLayoutData(fd);
		searchReplageGroup.setText(getString("SerchReplaceGroup.Title"));
		helper.setLook(searchReplageGroup);

		btnUseSearchTerm = new Button(searchReplageGroup, SWT.RADIO);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(0, helper.margin);
		btnUseSearchTerm.setText("Use Search Term:");
		btnUseSearchTerm.setLayoutData(fd);
		helper.setLook(btnUseSearchTerm);
		btnUseSearchTerm.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {
				if (currentRule != null) {
					currentRule.setUseSearchTerm(btnUseSearchTerm.getSelection());
					currentRule.setUseSearchTable(btnUseSearchTable.getSelection());
					enable();
				}
			}
		});

		Label exLabel = new Label(searchReplageGroup, SWT.RIGHT);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(btnUseSearchTerm, helper.margin);
		fd.right = new FormAttachment(15, -helper.margin);
		exLabel.setText(getString("SerchReplaceGroup.Search.Label"));
		exLabel.setLayoutData(fd);
		helper.setLook(exLabel);

		txSearchTerm = new Text(searchReplageGroup, SWT.BORDER);
		fd = new FormData();
		fd.left = new FormAttachment(exLabel, helper.margin);
		fd.top = new FormAttachment(btnUseSearchTerm, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		txSearchTerm.setLayoutData(fd);
		helper.setLook(txSearchTerm);

		txSearchTerm.addModifyListener(new ModifyListener() {
			@Override public void modifyText(ModifyEvent arg0) {
				if (currentRule != null) {
					currentRule.setSearch_SerchTerm(txSearchTerm.getText());
				}
			}
		});

		Label replaceLabel = new Label(searchReplageGroup, SWT.RIGHT);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(txSearchTerm, helper.margin);
		fd.right = new FormAttachment(15, -helper.margin);
		replaceLabel.setText(getString("SerchReplaceGroup.Replace.Label"));
		replaceLabel.setLayoutData(fd);
		helper.setLook(replaceLabel);

		txReplaceTerm = new Text(searchReplageGroup, SWT.BORDER);
		fd = new FormData();
		fd.left = new FormAttachment(replaceLabel, helper.margin);
		fd.top = new FormAttachment(txSearchTerm, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		txReplaceTerm.setLayoutData(fd);
		helper.setLook(txReplaceTerm);

		txReplaceTerm.addModifyListener(new ModifyListener() {
			@Override public void modifyText(ModifyEvent arg0) {
				if (currentRule != null) {
					currentRule.setSearch_ReplaceTerm(txReplaceTerm.getText());
				}
			}
		});

		btnUseSearchTable = new Button(searchReplageGroup, SWT.RADIO);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(txReplaceTerm, 2 * helper.margin);
		btnUseSearchTable.setText(getString("SerchReplaceGroup.UseTable.Label"));
		btnUseSearchTable.setLayoutData(fd);
		helper.setLook(btnUseSearchTable);
		btnUseSearchTable.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {
				if (currentRule != null) {
					currentRule.setUseSearchTerm(btnUseSearchTerm.getSelection());
					currentRule.setUseSearchTable(btnUseSearchTable.getSelection());
					enable();
				}
			}
		});

		int cWt = helper.colWidth[0];
		helper.colWidth[0] = cWt / 2;
		txSearchTablePath = helper.createPathControl(searchReplageGroup, btnUseSearchTable, "InputTab.SerchReplaceGroup.SearchTablePath", false);
		helper.colWidth[0] = dialog.getProps().getMiddlePct();
		txSearchTablePath.addModifyListener(new ModifyListener() {
			@Override public void modifyText(ModifyEvent arg0) {
				if (currentRule != null) {
					currentRule.setSearch_TablePath(txSearchTablePath.getText());
				}
			}
		});

		return searchReplageGroup;
	}

	private void addComboItems(CCombo combo, String[] values) {
		combo.removeAll();
		for (String s : values) {
			if (s != null)
				combo.add(s);
		}
	}

	private void placeEnumCombos(Control[] comboControls, Control last) {

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin);
		fd.right = new FormAttachment(25, -helper.margin);
		// fd.bottom = new FormAttachment(50, -helper.margin);
		comboControls[0].setLayoutData(fd);

		fd = new FormData();
		fd.left = new FormAttachment(comboControls[0], helper.margin);
		fd.top = new FormAttachment(last, helper.margin);
		fd.right = new FormAttachment(50, -helper.margin);
		// fd.bottom = new FormAttachment(50, -helper.margin);
		comboControls[1].setLayoutData(fd);
	}

	private void removeCleanserRule() throws CloneNotSupportedException {

		if (currentOperation != null) {
			if (currentRule != null) {
				String key = currentRule.getIndex();
				int intKey = Integer.parseInt(key);
				HashMap<String, CleanserRule> oldRulesMap = currentOperation.getCleanserRuleMap();
				HashMap<String, CleanserRule> newRulesMap = new HashMap<String, CleanserRule>();
				String index;
				CleanserRule rule;
				int idx;

				for (Entry<String, CleanserRule> entry : oldRulesMap.entrySet()) {
					index = entry.getKey();
					idx = Integer.parseInt(index);
					rule = entry.getValue();

					if (idx < intKey) {
						newRulesMap.put(index, rule);
					} else if (idx == intKey) {
						// do nothing we are removing
					} else if (idx > intKey) {
						index = String.valueOf(idx - 1);
						rule.setIndex(index);
						newRulesMap.put(index, rule);
					}
				}

				currentOperation.setClenserObjectsMap(newRulesMap);
				updateOperationDetails(currentOperation);
				dialog.setChanged();
			}
		}
	}

	private void updateNameCombo(String newItem) {

		cOperationName.removeAll();
		if (!Const.isEmpty(newItem)) {
			cOperationName.add(newItem);
		}

		for (String savedOperationName : savedOperations.keySet()) {
			if (!newItem.equals(savedOperationName))
				cOperationName.add(savedOperationName);
		}

		cOperationName.setText(newItem);
	}

	private void addCleanserRule() {

		if (currentOperation != null) {

			if (!Const.isEmpty(currentOperation.getOperationName())) {

				String key = String.valueOf(currentOperation.getCleanserRuleMap().size());
				currentOperation.getCleanserRuleMap().put(key, new CleanserRule(key));
				currentRule = currentOperation.getCleanserRuleMap().get(key);
				updateOperationDetails(currentOperation);
				dialog.setChanged();
			} else {
				// TODO put in messages
				String message = "Please enter a name for the operation.";
				MessageBox box = new MessageBox(dialog.getShell(), SWT.OK | SWT.APPLICATION_MODAL | SWT.ICON_WARNING);
				box.setText("Operation");
				box.setMessage(message);
				if (box.open() == SWT.OK) {
					cOperationName.setFocus();
				}
			}
		}
	}

	private Button addCleanserRulebutton(Composite parent, int left, int right, Control top) {
		final Button btn = new Button(parent, SWT.BORDER);
		FormData fd = new FormData();
		fd.left = new FormAttachment(left, helper.margin);
		fd.top = new FormAttachment(top, helper.margin);
		fd.right = new FormAttachment(right, -helper.margin);
		btn.setLayoutData(fd);
		btn.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				currentRule = (CleanserRule) btn.getData();
				if (currentRule != null) {
					btn.setBackground(colorSelected);
					updateRuleDetails(currentRule.getIndex());
					enable();
				}
			}
		});

		helper.setLook(btn);
		return btn;
	}

	private void updateOperationDetails(MDCleanserOperation opr) {

		Button newCleserObjectButton = null;
		Control top = null;
		if (opr == null) {
			newCleserObjectButton = addCleanserRulebutton(rulesButtonComp, 0, 13, top);
			rulesButtonMap.put("0", newCleserObjectButton);
			newCleserObjectButton.setVisible(false);
			rulesButtonComp.layout();

			return;
		}

		currentOperation = opr;

		txInputField.setText(currentOperation.getSourceFieldName());
		cOperationName.setText(currentOperation.getOperationName());
		txOperationDescription.setText(currentOperation.getOperationDescription());

		rulesButtonMap.clear();
		if (Const.isEmpty(currentOperation.getOperationName())) {
			currentOperation.getCleanserRuleMap().clear();
		}

		for (Control con : rulesButtonComp.getChildren()) {
			con.dispose();
		}

		int left = 1;
		int right = 15;
		int index = 0;
		int increase = 0;
		String sIndex = "0";
		for (index = 0; index < currentOperation.getCleanserRuleMap().size(); index++) {
			if (index % 7 == 0) {
				top = newCleserObjectButton;
				left = 1;
				right = 15;
			}

			sIndex = String.valueOf(index);
			newCleserObjectButton = addCleanserRulebutton(rulesButtonComp, left, right, top);
			newCleserObjectButton.setData(currentOperation.getCleanserRuleMap().get(sIndex));
			if (currentOperation.getCleanserRuleMap().get(sIndex).getOperation() != null)
				newCleserObjectButton.setText(currentOperation.getCleanserRuleMap().get(sIndex).getOperation().getValue());

			rulesButtonMap.put(sIndex, newCleserObjectButton);
			left += 14;
			right += 14;
		}

		if (index <= 7) {

			Button holderBtm = new Button(rulesButtonComp, SWT.BORDER);
			FormData fd = new FormData();
			fd.left = new FormAttachment(left, helper.margin);
			fd.top = new FormAttachment(newCleserObjectButton, helper.margin);
			fd.right = new FormAttachment(right, -helper.margin);
			holderBtm.setLayoutData(fd);
			holderBtm.setVisible(false);
		}

		if (rulesButtonMap.isEmpty()) {

			Button holderTop = new Button(rulesButtonComp, SWT.BORDER);
			FormData fd = new FormData();
			fd.left = new FormAttachment(left, helper.margin);
			fd.top = new FormAttachment(0, helper.margin);
			fd.right = new FormAttachment(right, -helper.margin);
			holderTop.setLayoutData(fd);
			holderTop.setVisible(false);

			Button holderBtm = new Button(rulesButtonComp, SWT.BORDER);
			fd = new FormData();
			fd.left = new FormAttachment(left, helper.margin);
			fd.top = new FormAttachment(holderTop, helper.margin);
			fd.right = new FormAttachment(right, -helper.margin);
			holderBtm.setLayoutData(fd);
			holderBtm.setVisible(false);
		}

//		scRulesButtonComp.setContent(rulesButtonComp);
//		scRulesButtonComp.setExpandVertical(true);
//		scRulesButtonComp.setExpandHorizontal(true);
//		scRulesButtonComp.layout();
		rulesButtonComp.layout();
//		scRulesButtonComp.setMinSize(scrollCompMinSize/* rulesButtonComp.computeSize(SWT.DEFAULT, SWT.DEFAULT) */);

//		if (newCleserObjectButton != null)
//			scRulesButtonComp.showControl(newCleserObjectButton);

		currentRule = currentOperation.getCleanserRuleMap().get(sIndex);

		if (currentRule != null)
			updateRuleDetails(currentRule.getIndex());
		else
			updateRuleDetails(null);

		increase = getCompositeSizeIncrease(index);
		resizeMainComposite(increase);
	}

	private int getCompositeSizeIncrease(int index) {
		//	System.out.println(" ------- > INDEX = " + index);
		int increase = 0;
		if (index > 14 && index < 28)
			increase = 150;
		else if (index > 27 && index < 43)
			increase = 200;
		else if (index > 42 && index < 57)
			increase = 250;
		else if (index > 56 && index < 71)
			increase = 325;
		else if (index > 70 && index < 85)
			increase = 400;
		else if (index > 84 && index < 106)
			increase = 475;
		else if (index >= 106)
			increase = 800;

		return increase;
	}

	private void resizeMainComposite(int increase) {

		//	int cb = ((FormData) inputTableComp.getLayout()).bottom;
//		int bottom = (int) (increase *.1);
//
//		FormData fd = new FormData();
//		fd.left = new FormAttachment(0, helper.margin);
//		fd.top = new FormAttachment(description, helper.margin);
//		fd.right = new FormAttachment(100, -helper.margin);
//		fd.bottom = new FormAttachment(bottom, -helper.margin);
//		inputTableComp.setLayoutData(fd);
//		helper.setLook(inputTableComp);

		Rectangle bounds = mainComp.getBounds();
		// Initialize the Scrolled Composite with the new composite
		bounds = mainBounds;
		mainSComp.setContent(mainComp);
		mainSComp.setExpandHorizontal(true);
		mainSComp.setExpandVertical(true);
		mainSComp.setMinWidth(bounds.width - 50);
		mainSComp.setMinHeight(bounds.height + increase/* 50 */);
		mainSComp.showControl(scrollPosition);
	}

	private void setOptionCheckBoxes() {

		//	List<RuleOption> listOptions = currentRule.getOptionList();

		if (currentRule != null) {
			currentOptions.clear();
			currentOptions.addAll(currentRule.getOptionList());
		}

		if (currentOptions != null) {
			//currentRule.getOptionList().clear();

			ckAbriviateTargetSize.setSelection(false);
			ckCaseSensitive.setSelection(false);
			ckPartialWord.setSelection(false);
			ckFuzzySearch.setSelection(false);
			ckSingleOccurence.setSelection(false);
			ckCasingAcronym.setSelection(false);
			ckUseLookupCasing.setSelection(false);
		}

		for (RuleOption opt : currentOptions) {
			if (opt.equals(RuleOption.OptAbbreviateTargetSize) && currentRule.getOperation() == RuleOperation.OperationAbbreviation) {
				ckAbriviateTargetSize.setSelection(true);
			}
			if (opt.equals(RuleOption.OptCaseSensitive) && currentRule.getOperation() == RuleOperation.OperationTextSearchReplace) {
				ckCaseSensitive.setSelection(true);
			}
			if (opt.equals(RuleOption.OptPartialWord) && currentRule.getOperation() == RuleOperation.OperationTextSearchReplace) {
				ckPartialWord.setSelection(true);
			}
			if (opt.equals(RuleOption.OptFuzzySearch) && currentRule.getOperation() == RuleOperation.OperationTextSearchReplace) {
				ckFuzzySearch.setSelection(true);
			}
			if (opt.equals(RuleOption.OptSingleOccurrence) && currentRule.getOperation() == RuleOperation.OperationTextSearchReplace) {
				ckSingleOccurence.setSelection(true);
			}
			if (opt.equals(RuleOption.OptCasingAcronym) && currentRule.getOperation() == RuleOperation.OperationCase) {
				ckCasingAcronym.setSelection(true);
			}
			if (opt.equals(RuleOption.OptUseLookupCasing) && (currentRule.getOperation() == RuleOperation.OperationAbbreviation || currentRule.getOperation() == RuleOperation.OperationPunctuation)) {
				ckUseLookupCasing.setSelection(true);
			}
		}
	}

	private void updateRuleDetails(String key) {

		Button objButton;
		String firstRecordkey = key;

		// Clear each button.
		for (Entry<String, Button> entry : rulesButtonMap.entrySet()) {
			objButton = entry.getValue();
			objButton.setText("");
			objButton.setData(null);
			objButton.setBackground(colorNotSelected);
			objButton.setSelection(false);
			objButton.setVisible(false);
		}

		if (key == null) {
			cRuleOperation.setText("");
			cCasingMode.setText("");
			cPunctuationMode.setText("");
			cAbbreviationMode.setText("");
			cFieldDataType.setText("");
//			cRuleOption.setText("");
//			txTargetSize.setText("");

			btnUseTrigger.setSelection(false);
			btnExpressionTrigger.setSelection(false);
			btnRegExTrigger.setSelection(false);
			txExpressionTrigger.setText("");
			txRegExTrigger.setText("");

			btnUseRegEx.setSelection(false);
			txRegExSearch.setText("");
			txRegExReplace.setText("");

			btnUseRegExTable.setSelection(false);
			txRegExTablePath.setText("");

			btnUseSearchTerm.setSelection(false);
			;
			txSearchTerm.setText("");
			txReplaceTerm.setText("");

			btnUseSearchTable.setSelection(false);
			;
			txSearchTablePath.setText("");

			txTransformExpression.setText("");
			enable();

			return;
		}

		HashMap<String, CleanserRule> objectsMap = currentOperation.getCleanserRuleMap();
		// set buttons with objects
		for (Entry<String, CleanserRule> entry : objectsMap.entrySet()) {
			CleanserRule co = entry.getValue();
			if (Const.isEmpty(firstRecordkey)) {
				// just to set
				firstRecordkey = entry.getKey();
			}

			objButton = rulesButtonMap.get(entry.getKey());
			if (objButton != null && co.getOperation() != null) {
				objButton.setText(entry.getKey() + " - " + co.getOperation().getValue());
				objButton.setData(co);
				objButton.setVisible(true);
			}
		}

		// Set default selection.
		rulesButtonMap.get(firstRecordkey).setSelection(true);
		rulesButtonMap.get(firstRecordkey).setBackground(colorSelected);
		currentRule = currentOperation.getCleanserRuleMap().get(firstRecordkey);

		if (currentRule != null) {

			try {
				cRuleOperation.setText(currentRule.getOperation().getValue());
				cCasingMode.setText(currentRule.getCaseMode().getValue());
				cPunctuationMode.setText(currentRule.getPunctMode().getValue());
				cAbbreviationMode.setText(currentRule.getAbrivMode().getValue());
				cFieldDataType.setText(currentRule.getDataType().getValue());
				setOptionCheckBoxes();
			} catch (NullPointerException npe) {
				// Just don't set the text then
			}

//			addComboItems(cRuleOption, RuleOption.optionsFor(currentRule.getOperation()));
//
//			if (hasOption())
//				cRuleOption.setText(currentRule.getOptionList().getValue());
//			else
//				cRuleOption.select(0);

//			txTargetSize.setText(currentRule.getAbrivTargetSize());

			btnUseTrigger.setSelection(currentRule.isUseTrigger());
			btnExpressionTrigger.setSelection(currentRule.isUseExpressionTrigger());
			txExpressionTrigger.setText(currentRule.getExpressionTrigger());
			btnRegExTrigger.setSelection(currentRule.isUseRegExTrigger());
			txRegExTrigger.setText(currentRule.getRegExTrigger());

			btnUseRegEx.setSelection(currentRule.isUseRegEx());
			txRegExSearch.setText(currentRule.getRegExSearch());
			txRegExReplace.setText(currentRule.getRegExReplace());

			btnUseRegExTable.setSelection(currentRule.isUseRegExTable());
			txRegExTablePath.setText(currentRule.getRegExTablePath());

			btnUseSearchTerm.setSelection(currentRule.isUseSearchTerm());
			txSearchTerm.setText(currentRule.getSearch_SerchTerm());
			txReplaceTerm.setText(currentRule.getSearch_ReplaceTerm());

			btnUseSearchTable.setSelection(currentRule.isUseSearchTable());
			txSearchTablePath.setText(currentRule.getSearch_TablePath());

			// addComboItems(cRuleOption, RuleOption.optionsFor(currentRule.getOperation()));

			txTransformExpression.setText(currentRule.getTransformExpression());
		}

		enable();
	}

	private boolean hasOption() {

		if (!currentRule.getOptionList().isEmpty()) {
			return true;
		}

//		List<String> myList = Arrays.asList(RuleOption.optionsFor(currentRule.getOperation()));
//
//		if (currentRule.getOptionList() == null)
//			return false;
//
//		if (myList.contains(currentRule.getOptionList().getValue()) || currentRule.getOptionList().getValue() == "") { return true; }

		return false;
	}

	private void openRegExTester(Text wSearchText, Text wReplaceText) {

		String[] values = new String[2];
		values[0] = wSearchText.getText();
		boolean doReplace;
		if (wReplaceText != null) {
			values[1] = wReplaceText.getText();
			doReplace = true;
		} else {
			doReplace = false;
		}

		RegExBuilderDialog builderDialog = new RegExBuilderDialog(dialog, (MDCleanserData) dialog.getData(), doReplace);
		builderDialog.setExpressionValues(values);
		builderDialog.open();

		wSearchText.setText(values[0]);
		if (wReplaceText != null)
			wReplaceText.setText(values[1]);
		dialog.setChanged();
	}

	private void openExpressionBuilder(Text wText) {
		ExpressionBuilderDialog expressionBuilderDialog = null;
		expressionBuilderDialog = new ExpressionBuilderDialog(dialog, (MDCleanserData) dialog.getData(), txExpressionTrigger.getText());
		expressionBuilderDialog.setRowMeta(dialog.getVMIList());
		expressionBuilderDialog.setExString(wText.getText());

		if (expressionBuilderDialog != null) {
			boolean dTest = expressionBuilderDialog.open();

			if (dTest) {
				String expSTR = expressionBuilderDialog.getExString();
				wText.setText(expSTR);
				dialog.setChanged();
			}
		}
	}

	@Override public void dispose() {
		// nothing to do
	}

	@Override public String getHelpURLKey() {
		return BaseMessages.getString(PKG, "MDCleanser.Plugin.Help.InputFieldsTab");
	}

	private String getString(String key, String... args) {
		return BaseMessages.getString(PKG, "MDCleanserDialog.InputTab." + key, args);
	}
}
