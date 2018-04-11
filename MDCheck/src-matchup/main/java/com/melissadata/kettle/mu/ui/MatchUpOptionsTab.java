package com.melissadata.kettle.mu.ui;

import com.melissadata.cz.ui.MDAbstractDialog;
import com.melissadata.kettle.MDCheckDialog;
import com.melissadata.kettle.MDCheckMeta;
import com.melissadata.kettle.MDCheckStepData;
import com.melissadata.kettle.mu.MatchUpMeta;
import com.melissadata.kettle.mu.evaluator.*;
import com.melissadata.kettle.support.MDCheckHelper;
import com.melissadata.kettle.support.MDTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.ui.util.ImageUtil;

import java.util.*;
import java.util.List;

public class MatchUpOptionsTab implements MDTab {
	private static Class<?> PKG =  MatchUpMeta.class;
	private MDCheckDialog dialog;
	private MDCheckHelper              helper;
	private MatchUpMeta muMeta;
	private MDCheckStepData mdcStepData;
	private Text                       wResultCodes;
	private Text                       wDupeGroup;
	private Text                       wDupeCount;
	private Text                       wMatchcodeKey;
	private Button                     rbListSuppress;
	private Button                     rbListIntersect;
	private Button                     cbNoPurge;
	private Button                     ckLastUpdated;
	private Button                               ckMostComplete;
	private Button                               ckQualityScore;
	private Button                               ckCustom;
	private Button                               editLastUpdated;
	private Button                               editMostComplete;
	private Button                               editQualityScore;
	private Button                               editCustom;
	private Button                               pItemDown;
	private Button                               pItemUp;
	private Table                                goldenTable;
	private List<Algorithm>                      algList;
	private Map<Algorithm.AlgorithmType, Button> ckBtnMap;
	private Map<Algorithm.AlgorithmType, Button> editBtnMap;
	private MatchUpFieldMappingTab               fieldMappingTab;

	public MatchUpOptionsTab(MDCheckDialog dialog) {
		this.dialog = dialog;
		helper = dialog.getHelper();
		mdcStepData = dialog.getData();
		//	muMeta = dialog.getTabData().getMatchUp();
		// Create the tab
		CTabItem wTab = new CTabItem(dialog.getTabFolder(), SWT.NONE);
		wTab.setText(getString("TabTitle"));
		wTab.setData(this);
		// Create a scrolling region within the tab
		ScrolledComposite wSComp = new ScrolledComposite(dialog.getTabFolder(), SWT.V_SCROLL | SWT.H_SCROLL);
		wSComp.setLayout(new FillLayout());
		// Create the tab contents
		Composite wComp = createControls(wSComp);
		// Fit the composite within its container (the scrolled composite)
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(0, 0);
		fd.right = new FormAttachment(100, 0);
		fd.bottom = new FormAttachment(100, 0);
		wComp.setLayoutData(fd);
		// Pack the composite and get its size
		wComp.pack();
		Rectangle bounds = wComp.getBounds();
		// Initialize the Scrolled Composite with the new composite
		wSComp.setContent(wComp);
		wSComp.setExpandHorizontal(true);
		wSComp.setExpandVertical(true);
		wSComp.setMinWidth(bounds.width);
		wSComp.setMinHeight(bounds.height);
		// Initialzie the tab with the scrolled composite
		wTab.setControl(wSComp);
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.support.MDTab#advancedConfigChanged()
	 */
	public void advancedConfigChanged() {
		// Update enablement
		enable();
	}

	public void dispose() {
		// Nothing to do
	}

	/**
	 * Loads the dialog data into the meta structure
	 */
	public void getData(MDCheckStepData data) {
		MatchUpMeta matchUpMeta = data.getMatchUp();
		// Output fields
		matchUpMeta.setResultCodes(wResultCodes.getText());
		matchUpMeta.setDupeGroup(wDupeGroup.getText());
		matchUpMeta.setDupeCount(wDupeCount.getText());
		matchUpMeta.setMatchcodeKey(wMatchcodeKey.getText());
		// Lookup options
		matchUpMeta.setListSuppress(rbListSuppress.getSelection());
		matchUpMeta.setNoPurge(cbNoPurge.getSelection());
		//FIXME why only if PentahoPlugin ?
		if (MDCheckMeta.isPentahoPlugin()) {
			matchUpMeta.setAlgorithms(algList);
		}
	}

	/**
	 * @return The URL for help information
	 */
	public String getHelpURLKey() {
		return "MDCheck.Help.MatchUpOptionsTab";
	}

	/**
	 * Loads the meta data into the dialog tab
	 *
	 * @param data
	 * @return
	 * @throws CloneNotSupportedException
	 */
	public boolean init(MDCheckStepData data) {
		//FIXME selection
		// Get the match up meta data
		mdcStepData = data;
		muMeta = mdcStepData.getMatchUp();
		// Output fields
		wResultCodes.setText(muMeta.getResultCodes());
		wDupeGroup.setText(muMeta.getDupeGroup());
		wDupeCount.setText(muMeta.getDupeCount());
		wMatchcodeKey.setText(muMeta.getMatchcodeKey());
		// Lookup options
		if (muMeta.getListSuppress()) {
			rbListSuppress.setSelection(true);
			rbListIntersect.setSelection(false);
		} else {
			rbListSuppress.setSelection(false);
			rbListIntersect.setSelection(true);
		}
		cbNoPurge.setSelection(muMeta.getNoPurge());
		if (MDCheckMeta.isPentahoPlugin()) {
			algList = new ArrayList<Algorithm>(muMeta.getAlgorithms().size());
			for (Algorithm alg : muMeta.getAlgorithms()) {
				try {
					algList.add(alg.clone());
				} catch (CloneNotSupportedException e) {
					System.out.println("Error cloning algorythm");
					e.printStackTrace();
				}
			}
			for (Algorithm alg : algList) {
				addTableItem(alg, ckBtnMap.get(alg.algorithmType), editBtnMap.get(alg.algorithmType));
				ckBtnMap.get(alg.algorithmType).setSelection(alg.isSelected());
			}
		}
		// Handle initial enablement
		enable();
		return false;
	}

	/**
	 * Called when the lookup step changes in the field mappings tab
	 */
	public void lookupChanged() {
		// Adjust enablement
		enable();
	}

	/**
	 * Add reference to field mappings tab
	 *
	 * @param fieldMappingTab
	 */
	public void setFieldMappingTab(MatchUpFieldMappingTab fieldMappingTab) {
		this.fieldMappingTab = fieldMappingTab;
	}

	/**
	 * Helper method that creates the output field text boxes
	 *
	 * @param parent
	 * @param wLeft
	 * @param wTop
	 * @param name
	 * @return
	 */
	private Text addOutputTextBox(Composite parent, Control wLeft, Control wTop, String name) {
		// Create a label
		Label label = new Label(parent, SWT.RIGHT);
		label.setText(getString(name + ".Label")); //$NON-NLS-1$
		helper.setLook(label);
		// Create one text box
		Text wText = new Text(parent, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		// Inform dialog when something changes
		wText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				dialog.setChanged();
			}
		});
		// Handle CR
		wText.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetDefaultSelected(SelectionEvent event) {
				dialog.ok();
			}
		});
		// Select all text when focus gained
		final Text wBox = wText;
		wText.addFocusListener(new FocusAdapter() {
			@Override public void focusGained(FocusEvent arg0) {
				wBox.selectAll();
			}
		});
		// Perform layout
		int labelWidth = helper.colWidth[0] / 2;
		FormData fd = new FormData();
		fd.left = new FormAttachment(wLeft, helper.margin);
		fd.top = new FormAttachment(wTop, helper.margin);
		fd.right = new FormAttachment(labelWidth + ((wLeft == null) ? 0 : 50), -helper.margin);
		label.setLayoutData(fd);
		fd = new FormData();
		fd.left = new FormAttachment(label, helper.margin);
		fd.top = new FormAttachment(wTop, helper.margin);
		fd.right = new FormAttachment((wLeft == null) ? 50 : 100, -helper.margin);
		wText.setLayoutData(fd);
		return wText;
	}

	private TableItem addTableItem(final Algorithm alg, Button ckBtn, Button pEdit) {
		final TableItem item = new TableItem(goldenTable, SWT.NONE);
		item.setText(0, String.valueOf(alg.getIndex()));
		item.setText(2, alg.getAlgoType().getDescription());
		item.setText(3, alg.getOption());
		item.setData(alg);
		TableEditor editor = new TableEditor(goldenTable);
		ckBtn.pack();
		ckBtn.setData(alg);
		helper.setLook(ckBtn);
		editor.minimumWidth = ckBtn.getSize().x;
		editor.horizontalAlignment = SWT.CENTER;
		editor.setEditor(ckBtn, item, 1);
		editor = new TableEditor(goldenTable);
		pEdit.setText("...");
		pEdit.pack();
		pEdit.setData("item", item);
		pEdit.setData("alg", alg);
		helper.setLook(pEdit);
		editor.minimumWidth = pEdit.getSize().x;
		editor.horizontalAlignment = SWT.CENTER;
		editor.setEditor(pEdit, item, 4);
		editor.setItem(item);
		return item;
	}

	/**
	 * Called to create the tab controls
	 *
	 * @param parent
	 * @return
	 */
	private Composite createControls(Composite parent) {
		// Create the composite that will hold the contents of the tab
		Composite wComp = new Composite(parent, SWT.NONE);
		helper.setLook(wComp);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		wComp.setLayout(fl);
		// Create the output columns
		Control wOutput = createOutputControls(wComp, null);
		// Create the lookup options
		Control wLookup = createLookupOptions(wComp, wOutput);
		if (MDCheckMeta.isPentahoPlugin()) {
			createGoldenRecordOptions(wComp, wLookup);
		}
		return wComp;
	}

	/**
	 * Called to create the lookup option controls
	 *
	 * @param parent
	 * @param wLastLine
	 * @return
	 */
	private Control createGoldenRecordOptions(Composite parent, Control wLastLine) {
		// Create group
		Group wGroup = new Group(parent, SWT.NONE);
		wGroup.setText(getString("GoldenRecordGroup.Label"));
		helper.setLook(wGroup);
		FormLayout fl = new FormLayout();
		fl.marginHeight = helper.margin;
		fl.marginWidth = helper.margin;
		wGroup.setLayout(fl);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(wLastLine, helper.margin);
		fd.right = new FormAttachment(100, helper.margin);
		wGroup.setLayoutData(fd);
		// Create the lookup option controls
		Label label = helper.addLabel(wGroup, null, "MatchUpOptionsTab.GoldenRecordInfo");
		Label spacer = helper.addSpacer(wGroup, label);
		Label label2 = helper.addLabel(wGroup, spacer, "MatchUpOptionsTab.GoldenRecordInfo2");
		Label spacer2 = helper.addSpacer(wGroup, label2);
		Image iUp = ImageUtil.getImage(helper.getDisplay(), PKG, "com/melissadata/kettle/images/UpArrow.gif");
		pItemUp = new Button(wGroup, SWT.PUSH);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(spacer2, helper.margin);
		pItemUp.setLayoutData(fd);
		pItemUp.setImage(iUp);
		pItemUp.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {
				moveItemUp(goldenTable.getSelectionIndex());
				dialog.setChanged();
			}
		});
		Image iDn = ImageUtil.getImage(helper.getDisplay(), PKG, "com/melissadata/kettle/images/DownArrow.gif");
		pItemDown = new Button(wGroup, SWT.PUSH);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(pItemUp, helper.margin);
		pItemDown.setLayoutData(fd);
		pItemDown.setImage(iDn);
		pItemDown.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {
				moveItemDown(goldenTable.getSelectionIndex());
				dialog.setChanged();
			}
		});
		createTable(wGroup, spacer2);
		return wGroup;
	}

	/**
	 * Called to create the lookup option controls
	 *
	 * @param parent
	 * @param wLastLine
	 * @return
	 */
	private Control createLookupOptions(Composite parent, Control wLastLine) {
		// Create group
		Group wGroup = new Group(parent, SWT.NONE);
		wGroup.setText(getString("LookupOptionGroup.Label"));
		helper.setLook(wGroup);
		FormLayout fl = new FormLayout();
		fl.marginHeight = helper.margin;
		fl.marginWidth = helper.margin;
		wGroup.setLayout(fl);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(wLastLine, helper.margin);
		fd.right = new FormAttachment(100, helper.margin);
		wGroup.setLayoutData(fd);
		// Create the lookup option controls
		Label label = helper.addLabel(wGroup, null, "MatchUpOptionsTab.LookupOptionsWarning");
		label = helper.addSpacer(wGroup, label);
		rbListSuppress = helper.addRadioButton(wGroup, label, "MatchUpOptionsTab.ListSuppress");
		rbListIntersect = helper.addRadioButton(wGroup, rbListSuppress, "MatchUpOptionsTab.ListIntersect");
		label = helper.addSpacer(wGroup, rbListIntersect);
		cbNoPurge = helper.addCheckBox(wGroup, label, "MatchUpOptionsTab.NoPurge");
		return wGroup;
	}

	/**
	 * Called to create the output fields
	 *
	 * @param parent
	 * @param wLastLine
	 * @return
	 */
	private Control createOutputControls(Composite parent, Control wLastLine) {
		// Create group
		Group wGroup = new Group(parent, SWT.NONE);
		wGroup.setText(getString("OutputGroup.Label"));
		helper.setLook(wGroup);
		FormLayout fl = new FormLayout();
		fl.marginHeight = helper.margin;
		fl.marginWidth = helper.margin;
		wGroup.setLayout(fl);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(wLastLine, helper.margin);
		fd.right = new FormAttachment(100, helper.margin);
		wGroup.setLayoutData(fd);
		// Create the output field controls
		wResultCodes = addOutputTextBox(wGroup, null, null, "ResultCodes");
		wDupeGroup = addOutputTextBox(wGroup, wResultCodes, null, "DupeGroup");
		wDupeCount = addOutputTextBox(wGroup, null, wResultCodes, "DupeCount");
		wMatchcodeKey = addOutputTextBox(wGroup, wDupeCount, wDupeGroup, "MatchcodeKey");
		return wGroup;
	}

	private void createTable(Composite parent, Control last) {
		goldenTable = new Table(parent, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION | SWT.V_SCROLL);
		goldenTable.setHeaderVisible(true);
		goldenTable.setLinesVisible(true);
		FormData fd = new FormData();
		fd.left = new FormAttachment(pItemDown, helper.margin);
		fd.top = new FormAttachment(last, helper.margin);
		goldenTable.setLayoutData(fd);
		// index
		TableColumn tableColumn = new TableColumn(goldenTable, SWT.NONE);
		tableColumn.setWidth(20);
		tableColumn.setMoveable(false);
		tableColumn.setResizable(false);
		tableColumn.setText(" ");
		// check
		TableColumn tableColumn1 = new TableColumn(goldenTable, SWT.NONE);
		tableColumn1.setWidth(25);
		tableColumn1.setMoveable(false);
		tableColumn1.setResizable(false);
		tableColumn1.setText(" ");
		// Algorithm
		TableColumn tableColumn2 = new TableColumn(goldenTable, SWT.NONE);
		tableColumn2.setWidth(200);
		tableColumn2.setMoveable(false);
		tableColumn2.setResizable(false);
		tableColumn2.setText(getString("Algorithm.Label"));
		// Option
		TableColumn tableColumn3 = new TableColumn(goldenTable, SWT.NONE);
		tableColumn3.setWidth(200);
		tableColumn3.setMoveable(false);
		tableColumn3.setResizable(false);
		tableColumn3.setText(getString("Option.Label"));
		// edit button
		TableColumn tableColumn4 = new TableColumn(goldenTable, SWT.NONE);
		tableColumn4.setWidth(25);
		tableColumn4.setMoveable(false);
		tableColumn4.setResizable(false);
		tableColumn4.setText(" ");
		loadBtnMaps();
		for (final Button btn : ckBtnMap.values()) {
			btn.addSelectionListener(new SelectionAdapter() {
				@Override public void widgetSelected(SelectionEvent event) {
					Algorithm sel = (Algorithm) btn.getData();
					goldenTable.setSelection(sel.getIndex() - 1);
					sel.setSelected(btn.getSelection());
					dialog.setChanged();
					if(sel.isSelected()  ){
						try {
							openExpressionBuilder(sel.getAlgoType().getDescription());//((TableItem) btn.getData("item")).getText(2)
						} catch (KettleException e) {
							dialog.getLog().logError("Error opening Expression Builder");
							e.printStackTrace();
						}
					}
				}
			});
		}
		for (final Button btn : editBtnMap.values()) {
			btn.addSelectionListener(new SelectionAdapter() {
				@Override public void widgetSelected(SelectionEvent event) {
					try {
						openExpressionBuilder(((TableItem) btn.getData("item")).getText(2));
						((TableItem) btn.getData("item")).setText(3, ((Algorithm) btn.getData("alg")).getOption());
					} catch (KettleException e) {
						dialog.getLog().logError("Error opening Expression Builder");
						e.printStackTrace();
					}
				}
			});
		}
	}

	private boolean hasDateType(){
		int dateFields = 0;

		for(ValueMetaInterface vmi : dialog.getRowMetaInterface().getValueMetaList()){
			if(vmi.getType() == 3 || vmi.getType() == 9){
				dateFields++;
			}
		}
		return (dateFields > 0);
	};

	/**
	 * Called to enable components based on current state of input selection
	 */
	private void enable() {
		boolean enabled = !Const.isEmpty(fieldMappingTab.getLookupStepName());
		rbListSuppress.setEnabled(enabled);
		rbListIntersect.setEnabled(enabled);
		cbNoPurge.setEnabled(enabled);
		ckLastUpdated.setEnabled(hasDateType());
	}

	/**
	 * @param key
	 * @param args
	 * @return
	 */
	private String getString(String key, String... args) {
		return BaseMessages.getString(PKG, "MDCheckDialog.MatchUpOptionsTab." + key, args);
	}

	private void loadBtnMaps() {
		ckBtnMap = new HashMap<Algorithm.AlgorithmType, Button>();
		ckBtnMap.put(Algorithm.AlgorithmType.LASTUPDATED, ckLastUpdated = new Button(goldenTable, SWT.CHECK));
		ckBtnMap.put(Algorithm.AlgorithmType.MOSTCOMPLETE, ckMostComplete = new Button(goldenTable, SWT.CHECK));
		ckBtnMap.put(Algorithm.AlgorithmType.DATAQUALITYSCORE, ckQualityScore = new Button(goldenTable, SWT.CHECK));
		ckBtnMap.put(Algorithm.AlgorithmType.CUSTOM, ckCustom = new Button(goldenTable, SWT.CHECK));
		editBtnMap = new HashMap<Algorithm.AlgorithmType, Button>();
		editBtnMap.put(Algorithm.AlgorithmType.LASTUPDATED, editLastUpdated = new Button(goldenTable, SWT.PUSH));
		editBtnMap.put(Algorithm.AlgorithmType.MOSTCOMPLETE, editMostComplete = new Button(goldenTable, SWT.PUSH));
		editBtnMap.put(Algorithm.AlgorithmType.DATAQUALITYSCORE, editQualityScore = new Button(goldenTable, SWT.PUSH));
		editBtnMap.put(Algorithm.AlgorithmType.CUSTOM, editCustom = new Button(goldenTable, SWT.PUSH));
	}

	private void moveItemDown(int selected) {
		if (selected < 0 || selected > 2) {
			return;
		}
		// Index values start at 1 but selected starts at 0
		// so compensate
		algList.get(selected).setIndex(selected + 2);
		algList.get(selected + 1).setIndex(selected + 1);
		refreshAlgs();
		goldenTable.setSelection(selected + 1);
	}

	private void moveItemUp(int selected) {
		if (selected < 1) {
			return;
		}
		// Index values start at 1 but selected starts at 0
		// so compensate
		algList.get(selected - 1).setIndex(selected + 1);
		algList.get(selected).setIndex(selected);
		refreshAlgs();
		goldenTable.setSelection(selected - 1);
	}

	private void openExpressionBuilder(String dName) throws KettleException {
		MDAbstractDialog grDialog = null;
		Algorithm.AlgorithmType aType = Algorithm.AlgorithmType.decode(dName.toUpperCase().replaceAll(" ", ""));
		if (dName.equals("Custom")) {
			grDialog = new GRCustomDialog(dialog, mdcStepData);
			((GRCustomDialog) grDialog).setRowMeta(dialog.getRowMetaInterface());
			for (Algorithm alg : algList) {
				if (alg.getAlgoType().equals(Algorithm.AlgorithmType.CUSTOM)) {
					((GRCustomDialog) grDialog).setAlgorythm(alg);
				}
			}
		}
		if (dName.equals("Last Updated")) {
			if(hasDateType()) {
				grDialog = new GRLastUpdatedDialog(dialog, mdcStepData);
				((GRLastUpdatedDialog) grDialog).setRow(dialog.getRowMetaInterface());
				((GRLastUpdatedDialog) grDialog).loadColNames(mdcStepData);
			} else {
				MessageBox box = new MessageBox(dialog.getShell(), SWT.OK | SWT.APPLICATION_MODAL | SWT.ICON_INFORMATION);
				box.setText("Date Field Not Found");
				box.setMessage("No fields of date or timestamp type were found in the input fields.\n\"Last Updated\" Algorithm can only be used with fields with a type of Date or Timestamp. ");
				box.open();
				return;
			}
		}
		if (dName.equals("Most Complete")) {
			grDialog = new GRMostCompleteDialog(dialog, mdcStepData);
		}
		if (dName.equals("Data Quality Score")) {
			grDialog = new GRQualityScoreDialog(dialog, mdcStepData);
		}
		if (ckBtnMap.get(aType).getSelection()) {
			// Nothing here if already selected
		} else {
			MessageBox box = new MessageBox(dialog.getShell(), SWT.YES | SWT.NO | SWT.APPLICATION_MODAL | SWT.ICON_INFORMATION);
			box.setText("Algorithm Not Enabled");
			box.setMessage("You have not enabled the " + dName + " algorithm and cannot chage settings until you do so.\n\nWould you like to enable " + dName + " now?");
			if (box.open() == SWT.YES) {
				ckBtnMap.get(aType).setSelection(true);
			} else {
				return;
			}
		}
		if (grDialog != null) {
			boolean changed = grDialog.open();
			boolean cleanUp = false;
			for (Algorithm alg : algList) {
				if ((aType == Algorithm.AlgorithmType.DATAQUALITYSCORE) && (alg.getAlgoType() == Algorithm.AlgorithmType.DATAQUALITYSCORE) && (ckBtnMap.get(aType).getSelection())) {
					cleanUp = true;
					for (QualityScore qs : muMeta.getQualityScores()) {
						if (qs.isSelected() && !qs.getResultField().isEmpty()) {
							cleanUp = false;
						}
					}
				} else if (ckBtnMap.get(aType).getSelection() && (alg.getAlgoType() == aType) && alg.getExpression().isEmpty()) {
					cleanUp = true;
				}

				if (cleanUp) {
					MessageBox box     = new MessageBox(dialog.getShell(), SWT.OK | SWT.APPLICATION_MODAL | SWT.ICON_WARNING);
					String     message = "";
					message = aType.getDescription() + " : No expression set for " + aType.getDescription() + ".\nIf you would like to use " + aType.getDescription() + " please go back and select a field or expression.";
					box.setText("Algorithm Not Set");
					box.setMessage(message);
					box.open();
					ckBtnMap.get(aType).setSelection(false);

					return;
				}
			}
			dialog.setChanged();
		}
	}

	private void refreshAlgs() {
		goldenTable.removeAll();
		Collections.sort(algList);
		for (Algorithm alg : algList) {
			addTableItem(alg, ckBtnMap.get(alg.algorithmType), editBtnMap.get(alg.algorithmType));
		}
	}
}
