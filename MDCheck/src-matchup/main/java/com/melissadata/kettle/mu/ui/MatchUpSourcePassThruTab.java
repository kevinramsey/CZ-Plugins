package com.melissadata.kettle.mu.ui;

import com.melissadata.cz.SourceFieldInfo;
import com.melissadata.kettle.*;
import com.melissadata.kettle.mu.MatchUpMeta;
import com.melissadata.kettle.mu.evaluator.AddColumnDialog;
import com.melissadata.kettle.mu.evaluator.EvalItem.eDataType;
import com.melissadata.kettle.mu.evaluator.SurvivorEditorDialog;
import com.melissadata.kettle.mu.evaluator.SurvivorField;
import com.melissadata.kettle.mu.evaluator.SurvivorField.ConsolidationMethod;
import com.melissadata.kettle.support.MDCheckHelper;
import com.melissadata.kettle.support.MDCheckTransfer;
import com.melissadata.kettle.support.MDTab;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.ui.core.gui.GUIResource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;

public class MatchUpSourcePassThruTab implements MDTab {
	private static final int[]    EMPTY        = new int[0];
	private static final String   STACK_PREFIX = "Stack Group";
	private static       Class<?> PKG          =  MatchUpMeta.class;
	private MDCheckStepData mdcStepData;
	private MDCheckDialog dialog;
	private MDCheckHelper         helper;
	private ScrolledComposite     wScrollComp;
	private Composite             wControlsComp;
	private Composite             wPassThruComp;
	private Composite             wSimpleComp;
	private Composite             wSurvivorPassThruComp;
	private List<SourceFieldInfo> passThru;
	private List<SourceFieldInfo> filterOut;
	private List<SurvivorField>   survivorPassThru;
	private RowMetaInterface      vmiList;
	private boolean               isSurvivorSource;
	private Label                 lblFilterOutHeader;
	private TableViewer           tvPassThru;
	private TableViewer           tvFilterOut;
	private Button                btnGet;
	private Button                btnAdd;
	private Button                btnRemove;
	private List<String>          survivorOutputNames;
	private Button                btnSimplePassThrough;
	private Button                btnSurvivorPassThrough;
	private TableViewer           tvSurvivorPassThru;

	public MatchUpSourcePassThruTab(MDCheckDialog dialog, TransMeta transMeta) {
		this.dialog = dialog;
		helper = dialog.getHelper();
		vmiList = dialog.getRowMetaInterface();
		mdcStepData = dialog.getData();
		// Create the tab
		final CTabFolder wTabFolder = dialog.getTabFolder();
		final CTabItem wTab = new CTabItem(wTabFolder, SWT.NONE);
		wTab.setText(getTabTitle());
		wTab.setData(this);
		//	isSurvivorSource = dialog.getTabData().getSourcePassThru().isSurvivorPass() && MDCheckMeta.isPentahoPlugin();
		// Create a scrolling region within the tab
		wScrollComp = new ScrolledComposite(wTabFolder, SWT.V_SCROLL | SWT.H_SCROLL);
		wScrollComp.setLayout(new FillLayout());
		// Create the tab controls
		createControls(wTabFolder);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(0, 0);
		fd.right = new FormAttachment(100, 0);
		fd.bottom = new FormAttachment(100, 0);
		wControlsComp.setLayoutData(fd);
		// Pack the composite and get its size
		wControlsComp.pack();
		Rectangle bounds = wControlsComp.getBounds();
		// Initialize the Scrolled Composite with the new composite
		wScrollComp.setContent(wControlsComp);
		wScrollComp.setExpandHorizontal(true);
		wScrollComp.setExpandVertical(true);
		wScrollComp.setMinWidth(bounds.width);
		wScrollComp.setMinHeight(bounds.height);
		// Initialize the tab with the scrolled composite
		wTab.setControl(wScrollComp);
		// Add a selection listener to the tab folder so that the usage information for fields can be updated
		wTabFolder.addSelectionListener(new SelectionListener() {
			// TODO do we use this listener
			public void widgetDefaultSelected(SelectionEvent event) {
				widgetSelected(event);
			}

			public void widgetSelected(SelectionEvent event) {
				if (wTabFolder.getSelection() == wTab) {
				}
			}
		});
	}

	public void advancedConfigChanged() {
		enable();
	}

	public void dispose() {
		// Nothing to do
	}

	/**
	 * Loads the dialog mdcStepData into the meta structure
	 */
	@Override public void getData(MDCheckStepData data) {
		// Get pass thru fields
		getPassThruMeta(data).setSurvivorPass(isSurvivorSource);
		if (!isSurvivorSource) {
			List<String> passThruNames = getPassThruMeta(data).getPassThru();
			passThruNames.clear();
			List<SourceFieldInfo> passThru = (List<SourceFieldInfo>) tvPassThru.getInput();
			for (SourceFieldInfo field : passThru) {
				passThruNames.add(field.getName());
			}
			getPassThruMeta(data).setPassThru(passThruNames);
			// Get filtered fields
			List<String> filterOutNames = getPassThruMeta(data).getFilterOut();
			filterOutNames.clear();
			List<SourceFieldInfo> filterOut = (List<SourceFieldInfo>) tvFilterOut.getInput();
			for (SourceFieldInfo field : filterOut) {
				filterOutNames.add(field.getName());
			}
			getPassThruMeta(data).setFilterOut(filterOutNames);
		} else {
			survivorPassThru = (List<SurvivorField>) tvSurvivorPassThru.getInput();
			getPassThruMeta(data).setSurvivorPassThru(survivorPassThru);
		}
	}

	public String getHelpURLKey() {
		return "MDCheck.Help.MatchUpSourcePassThroughTab";
	}

	/**
	 * Loads the meta mdcStepData into the dialog tab
	 *
	 * @param data
	 * @return
	 */
	@Override public boolean init(MDCheckStepData data) {
		mdcStepData = data;
		isSurvivorSource = mdcStepData.getSourcePassThru().isSurvivorPass() && MDCheckMeta.isPentahoPlugin();
		boolean chaged = false;
		// Build a list of the elements for the filter out and pass thru tables
		passThru = new ArrayList<SourceFieldInfo>();
		survivorPassThru = new ArrayList<SurvivorField>();
		filterOut = new ArrayList<SourceFieldInfo>();
		List<String> passThruNames = getPassThruMeta(mdcStepData).getPassThru();
		survivorOutputNames = new ArrayList<String>();
		List<String> filterOutNames = getPassThruMeta(mdcStepData).getFilterOut();
		SortedMap<String, SourceFieldInfo> sourceFields = getSourceFields();
		for (String field : sourceFields.keySet()) {
			if (passThruNames.contains(field)) {
				passThru.add(sourceFields.get(field));
			}
			if (filterOutNames.contains(field)) {
				filterOut.add(sourceFields.get(field));
			}
		}
		for (SurvivorField survivorField : getPassThruMeta(mdcStepData).getSurvivorPassThru()) {
			try {
				survivorPassThru.add(survivorField.clone());
				survivorOutputNames.add(survivorField.getOutputName());
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
		if (isSurvivorSource) {
			btnSurvivorPassThrough.setSelection(true);
			btnSimplePassThrough.setSelection(false);
		} else {
			btnSurvivorPassThrough.setSelection(false);
			btnSimplePassThrough.setSelection(true);
		}
		tvSurvivorPassThru.setInput(survivorPassThru);
		// Survivor table
		if (survivorPassThru.isEmpty()) {
			getSurvivorFields(true);
			chaged = false;
		}
		tvPassThru.setInput(passThru);
		tvFilterOut.setInput(filterOut);
		// simple
		if (passThru.isEmpty() && filterOut.isEmpty()) {
			getFields(true);
			chaged = false;
		}
		enable();
		setButtonPosition();
		getData(mdcStepData);
		// send changed = false so we don't get a false positive.
		return chaged = false;
	}

	private void addColumn(SurvivorField survivor) {
		if (survivor == null) {
			survivor = new SurvivorField();
			//FIXME is this correct ?
			survivor.setStackIndex(survivorPassThru.size() );
		}
		AddColumnDialog acd = new AddColumnDialog(dialog, mdcStepData);
		acd.setSurvivor(survivor);
		if (acd.open()) {
			survivorPassThru.add(survivor);
			updateSurvivorTable();
		}
	}

	private void attachCellEditors(final TableViewer viewer, Composite parent) {
		viewer.setColumnProperties(new String[] { "OUTPUT_NAME", "DATA_TYPE", "CONSOLIDATION", "SOURCE", "PRIORITIZATION", "BUTTON" });
		viewer.setCellModifier(new ICellModifier() {
			String removedName = "";

			public boolean canModify(Object element, String property) {
				if (property.equals("OUTPUT_NAME")) {
					removedName = ((SurvivorField) element).getOutputName();
					return true;
				}
				return false;
			}

			public Object getValue(Object element, String property) {
				if (property.equals("OUTPUT_NAME")) {
					return ((SurvivorField) element).getOutputName();
				} else {
					return ((SurvivorField) element).getDisplaySource();
				}
			}

			public void modify(Object element, String property, Object value) {
				TableItem tableItem = (TableItem) element;
				SurvivorField survivor = (SurvivorField) tableItem.getData();
				if (property.equals("OUTPUT_NAME")) {
					survivor.setOutputName(value.toString());
					survivorOutputNames.remove(removedName);
					survivorOutputNames.add(value.toString());
				} else {
					survivor.setSource(value.toString());
				}
				viewer.refresh(survivor);
				dialog.setChanged();
			}
		});
		viewer.setCellEditors(new CellEditor[] { new TextCellEditor(parent), new ComboBoxCellEditor(), new TextCellEditor(parent), new TextCellEditor(parent) });
	}

	private void clearRows() {
		Control[] rows = tvSurvivorPassThru.getTable().getChildren();
		for (int i = 0; i < rows.length; i++) {
			// Start at 3 because the text box columns are handled different
			if (i > 2) {
				rows[i].dispose();
			}
		}
		getData(mdcStepData);
	}

	/**
	 * Called to create the tab controls
	 *
	 * @param wTabFolder
	 * @return
	 */
	// @Override
	private Composite createControls(CTabFolder wTabFolder) {// Composite parent
		wScrollComp = new ScrolledComposite(wTabFolder, SWT.V_SCROLL | SWT.H_SCROLL);
		wScrollComp.setLayout(new FillLayout());
		// Create the composite that will hold the contents of the tab
		wControlsComp = new Composite(wScrollComp/* parent */, SWT.NONE);
		helper.setLook(wControlsComp);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		wControlsComp.setLayout(fl);
		// Description line
		Label description = new Label(wControlsComp, SWT.LEFT | SWT.WRAP);
		description.setText(getDescription());
		helper.setLook(description);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(0, helper.margin);
		description.setLayoutData(fd);
		// Control last = description;
		btnSimplePassThrough = new Button(wControlsComp, SWT.RADIO);
		btnSimplePassThrough.setText("Simple Pass");
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(description, helper.margin * 2);
		fd.right = new FormAttachment(70, -helper.margin / 2);
		btnSimplePassThrough.setLayoutData(fd);
		helper.setLook(btnSimplePassThrough);
		btnSimplePassThrough.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) {
				//
			}

			public void widgetSelected(SelectionEvent e) {
				boolean isSelected = ((Button) e.getSource()).getSelection();
				if (isSelected) {
					isSurvivorSource = false;
					if (passThru.isEmpty() && filterOut.isEmpty()) {
						getFields(false);
					}
					refresh();
					setButtonPosition();
					enable();
					dialog.setChanged();
				}
			}
		});
		btnSurvivorPassThrough = new Button(wControlsComp, SWT.RADIO);
		btnSurvivorPassThrough.setText("Survivor Pass");
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(btnSimplePassThrough, helper.margin);
		fd.right = new FormAttachment(70, -helper.margin / 2);
		btnSurvivorPassThrough.setLayoutData(fd);
		helper.setLook(btnSurvivorPassThrough);
		btnSurvivorPassThrough.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) {
				//
			}

			public void widgetSelected(SelectionEvent e) {
				boolean isSelected = ((Button) e.getSource()).getSelection();
				if (isSelected) {
					isSurvivorSource = true;
					if (survivorPassThru.isEmpty()) {
						getSurvivorFields(false);
						//
					} else {
						updateSurvivorTable();
					}
					setButtonPosition();
					enable();
					dialog.setChanged();
				}
			}
		});
		createSurvivorPassThru(wControlsComp);
		createSimplePassThru(wControlsComp);
		// set up the Get Fields button
		btnGet = new Button(wControlsComp, SWT.PUSH);
		helper.setLook(btnGet);
		btnGet.setText(BaseMessages.getString(PKG, "System.Button.GetFields"));
		btnGet.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				if (isSurvivorSource) {
					getSurvivorFields(false);
				} else {
					getFields(false);
				}
			}
		});
		btnAdd = new Button(wControlsComp, SWT.PUSH);
		helper.setLook(btnAdd);
		btnAdd.setText("Add Field");
		fd = new FormData();
		fd.left = new FormAttachment(btnGet, helper.margin);
		// fd.right = new FormAttachment(labelAlign, -helper.margin / 2);
		fd.bottom = new FormAttachment(100, -helper.margin);
		btnAdd.setLayoutData(fd);
		btnAdd.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) {
				//
			}

			public void widgetSelected(SelectionEvent arg0) {
				addColumn(null);
				getData(mdcStepData);
			}
		});
		btnRemove = new Button(wControlsComp, SWT.PUSH);
		helper.setLook(btnRemove);
		btnRemove.setText("Remove Field");
		fd = new FormData();
		fd.left = new FormAttachment(btnAdd, helper.margin);
		// fd.right = new FormAttachment(labelAlign, -helper.margin / 2);
		fd.bottom = new FormAttachment(100, -helper.margin);
		btnRemove.setLayoutData(fd);
		btnRemove.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				removeRow();
			}
		});
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(btnSurvivorPassThrough, helper.margin * 2);
		fd.right = new FormAttachment(100, -helper.margin / 2);
		fd.bottom = new FormAttachment(btnGet, -helper.margin);
		wSimpleComp.setLayoutData(fd);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(btnSurvivorPassThrough, helper.margin * 2);
		fd.right = new FormAttachment(100, -helper.margin / 2);
		fd.bottom = new FormAttachment(btnGet, -helper.margin);
		wSurvivorPassThruComp.setLayoutData(fd);
		return wControlsComp;
	}

	private void createSimplePassThru(Composite parent) {
		// Create the composites that will hold the table viewers
		wSimpleComp = new Composite(parent, SWT.NONE);
		helper.setLook(wSimpleComp);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		wSimpleComp.setLayout(fl);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(btnSurvivorPassThrough, helper.margin * 2);
		fd.right = new FormAttachment(100, -helper.margin / 2);
		// fd.bottom = new FormAttachment(btnGet, -helper.margin);
		wSimpleComp.setLayoutData(fd);
		// Table descriptions
		Label lblPassThruHeader = new Label(wSimpleComp, SWT.LEFT | SWT.WRAP);
		lblPassThruHeader.setText(getString("PassThruDescription"));
		helper.setLook(lblPassThruHeader);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(0, helper.margin * 2);
		fd.right = new FormAttachment(50, -helper.margin / 2);
		lblPassThruHeader.setLayoutData(fd);
		lblFilterOutHeader = new Label(wSimpleComp, SWT.LEFT | SWT.WRAP);// wPassThruComp
		lblFilterOutHeader.setText(getString("FilterOutDescription"));
		helper.setLook(lblFilterOutHeader);
		fd = new FormData();
		fd.left = new FormAttachment(50, helper.margin / 2);
		fd.top = new FormAttachment(0, helper.margin * 2);
		fd.right = new FormAttachment(100, -helper.margin);
		lblFilterOutHeader.setLayoutData(fd);
		Composite wPassThruComp = new Composite(wSimpleComp, SWT.BORDER);
		helper.setLook(wPassThruComp);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(lblPassThruHeader, helper.margin * 2);
		fd.right = new FormAttachment(50, -helper.margin / 2);
		wPassThruComp.setLayoutData(fd);
		Composite wFilterOutComp = new Composite(wSimpleComp, SWT.BORDER);
		helper.setLook(wFilterOutComp);
		fd = new FormData();
		fd.left = new FormAttachment(50, helper.margin / 2);
		fd.top = new FormAttachment(lblPassThruHeader, helper.margin * 2);
		fd.right = new FormAttachment(100, -helper.margin);
		wFilterOutComp.setLayoutData(fd);
		// Create the tables that will hold the pass thru and filter out fields
		tvPassThru = createTable(wPassThruComp);
		tvFilterOut = createTable(wFilterOutComp);
		// Add drag-n-drop functionality
		addDND(tvPassThru, "passthru");
		addDND(tvFilterOut, "filterout");
		// make sure tables reach to the button
		((FormData) wPassThruComp.getLayoutData()).bottom = new FormAttachment(100, -helper.margin);
		((FormData) wFilterOutComp.getLayoutData()).bottom = new FormAttachment(100, -helper.margin);
	}

	private void createSurvivorPassThru(Composite parent) {
		// Create the composites that will hold the table viewers
		wSurvivorPassThruComp = new Composite(parent, SWT.NONE);
		helper.setLook(wSurvivorPassThruComp);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(btnSurvivorPassThrough, helper.margin * 2);
		fd.right = new FormAttachment(100, -helper.margin / 2);
		wSurvivorPassThruComp.setLayoutData(fd);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		wSurvivorPassThruComp.setLayout(fl);
		Label lblPassThruHeader = new Label(wSurvivorPassThruComp, SWT.LEFT | SWT.WRAP);
		lblPassThruHeader.setText(getString("PassThruDescription"));
		helper.setLook(lblPassThruHeader);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(0, helper.margin * 2);
		fd.right = new FormAttachment(70, -helper.margin / 2);
		lblPassThruHeader.setLayoutData(fd);
		wPassThruComp = new Composite(wSurvivorPassThruComp, SWT.BORDER);
		helper.setLook(wPassThruComp);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(lblPassThruHeader, helper.margin * 2);
		fd.right = new FormAttachment(100, -helper.margin / 2);
		wPassThruComp.setLayoutData(fd);
		tvSurvivorPassThru = createSurvivorshipTable(wPassThruComp);
		// make sure tables reach to the button
		((FormData) wPassThruComp.getLayoutData()).bottom = new FormAttachment(100, -helper.margin);
	}

	private void createSurvivorshipColumns(Composite comp, TableViewer viewer, TableColumnLayout layout) {
		viewer.setContentProvider(new ArrayContentProvider());
		String[] titles = { "Output Column Name", "Data Type", "Consolidation Method", "Source Column or Expression", "Prioritization", " " };
		// Output Name
		TableViewerColumn col;
		col = createSUVTableViewerColumn(viewer, titles[0], 40, 0, layout);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override public String getText(Object element) {
				SurvivorField field = (SurvivorField) element;
				return field.getOutputName();
			}
		});
		// DataType
		col = createSUVTableViewerColumn(viewer, titles[1], 30, 1, layout);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override public void update(ViewerCell cell) {
				final TableItem item = (TableItem) cell.getItem();
				final SurvivorField survivor = (SurvivorField) item.getData();
				final CCombo wCombo = new CCombo((Composite) cell.getViewerRow().getControl(), SWT.NONE);
				for (eDataType cm : eDataType.values()) {
					wCombo.add(cm.name());
				}
				wCombo.setVisibleItemCount(9);
				// we add "  " to the end so it displays correctly
				wCombo.setText(survivor.getDataType().name() + "  ");
				wCombo.setEditable(false);
				helper.setLook(wCombo);
				wCombo.addSelectionListener(new SelectionListener() {
					public void widgetDefaultSelected(SelectionEvent arg0) {
						//
					}

					public void widgetSelected(SelectionEvent e) {
						String tt = wCombo.getText();
						survivor.setDataType(eDataType.valueOf(tt));
						tvSurvivorPassThru.getTable().setSelection(survivorPassThru.indexOf(survivor));
					}
				});
				// Update dialog when something changes
				wCombo.addModifyListener(new ModifyListener() {
					public void modifyText(ModifyEvent arg0) {
						dialog.setChanged();
					}
				});
				final TableEditor editor = new TableEditor(item.getParent());
				editor.grabHorizontal = true;
				editor.grabVertical = true;
				editor.setEditor(wCombo, item, cell.getColumnIndex());
				editor.layout();
			}
		});
		// Consolidation Method
		col = createSUVTableViewerColumn(viewer, titles[2], 40, 2, layout);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override public void update(ViewerCell cell) {
				final TableItem item = (TableItem) cell.getItem();
				final SurvivorField survivor = (SurvivorField) item.getData();
				final CCombo wCombo = new CCombo((Composite) cell.getViewerRow().getControl(), SWT.NONE);
				for (ConsolidationMethod cm : ConsolidationMethod.values()) {
					wCombo.add(cm.toString());
				}
				wCombo.setVisibleItemCount(9);
				// we add "  " to the end so it displays correctly
				wCombo.setText(survivor.getConsolidationMethod().toString() + "  ");
				wCombo.setEditable(false);
				helper.setLook(wCombo);
				wCombo.addSelectionListener(new SelectionListener() {
					public void widgetDefaultSelected(SelectionEvent arg0) {
						//
					}

					public void widgetSelected(SelectionEvent e) {
						String consolidatioString = wCombo.getText();
						int idx = -2;
						if (consolidatioString.startsWith(STACK_PREFIX)) {
							List<Integer> idxList = new ArrayList<Integer>();
							for (SurvivorField existingSurvivor : survivorPassThru) {
								// See if we already have the stack group
								if (existingSurvivor.getConsolidationMethod().equals(ConsolidationMethod.fromString(consolidatioString))) {
									idxList.add(survivorPassThru.indexOf(existingSurvivor));
								}
							}// end for loop
							survivor.setConsolidationMethod(ConsolidationMethod.fromString(consolidatioString));
							idx = survivorPassThru.indexOf(survivor);
							if ((idx >= 0) && !idxList.contains(idx)) {
								idxList.add(idx);
							} else {
								if (dialog.isDebug()) {
									dialog.logDebug("INDEX ERROR: " + idx + " -- " + survivor);
								}
							}
							orderStackGroup(idxList, consolidatioString);
						} else {
							if (survivor.getDisplaySource().contains(STACK_PREFIX)) {
								survivor.setSource("");
								survivor.setPrioritization(survivor.defaultPriortization());
							}
						}
						survivor.setConsolidationMethod(ConsolidationMethod.fromString(consolidatioString));
						updateSurvivorTable();
						tvSurvivorPassThru.getTable().setSelection(survivorPassThru.indexOf(survivor));
					}
				});
				// Update dialog when something changes
				wCombo.addModifyListener(new ModifyListener() {
					public void modifyText(ModifyEvent arg0) {
						dialog.setChanged();
					}
				});
				final TableEditor editor = new TableEditor(item.getParent());
				editor.grabHorizontal = true;
				editor.grabVertical = true;
				editor.setEditor(wCombo, item, cell.getColumnIndex());
				editor.layout();
			}
		});
		// Source
		col = createSUVTableViewerColumn(viewer, titles[3], 60, 3, layout);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override public String getText(Object element) {
				SurvivorField survivor = (SurvivorField) element;
				return survivor.getDisplaySource();
			}
		});
		// Prioritization
		col = createSUVTableViewerColumn(viewer, titles[4], 100, 4, layout);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override public String getText(Object element) {
				SurvivorField survivor = (SurvivorField) element;
				return survivor.getDisplayPrioritization();
			}
		});
		// Edit Button
		col = createSUVTableViewerColumn(viewer, titles[5], 50, 5, layout);
		col.setLabelProvider(new ColumnLabelProvider() {
			int iddx = -1;

			@Override public void update(ViewerCell cell) {
				TableItem item = (TableItem) cell.getItem();
				final SurvivorField survivor = (SurvivorField) item.getData();
				Button button = new Button((Composite) cell.getViewerRow().getControl(), SWT.NONE);
				button.setText("...");
				button.addSelectionListener(new SelectionListener() {
					public void widgetDefaultSelected(SelectionEvent arg0) {
						//
					}

					public void widgetSelected(SelectionEvent arg0) {
						// FIXME need to fix bug with the editing of Stack groups
						// currently after edit dialog must be closed before
						// it can be changed

						iddx = getSurvivorIndex(survivor);
						doThatThing(survivorPassThru.get(iddx), iddx);

					}
				});
				TableEditor editor = new TableEditor(item.getParent());
				editor.grabHorizontal = true;
				editor.grabVertical = true;
				editor.setEditor(button, item, cell.getColumnIndex());
				editor.layout();
			}
		});
	}

	private int getSurvivorIndex(SurvivorField survivor){
		int index = -1;
		String fieldName = survivor.getOutputName();

		for(SurvivorField sf : survivorPassThru){
			if(sf.getOutputName().equals(fieldName)){
				index = sf.getStackIndex();
			}
		}

		return index;
	}

	/**
	 * Called to create the table viewer for fields values
	 *
	 * @param parent
	 * @return
	 */
	private TableViewer createSurvivorshipTable(Composite parent) {
		// Give this composite a table column layout view so we can control the
		// number of columns displayed. Only a single table viewer can be held in each
		// composite in order for this to work
		if (tvSurvivorPassThru != null) {
			tvSurvivorPassThru = null;
		}
		TableColumnLayout tcl = new TableColumnLayout();
		parent.setLayout(tcl);
		// Create table viewer
		TableViewer tv = new TableViewer(parent, SWT.VIRTUAL | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		// Create columns in table viewer
		createSurvivorshipColumns(parent, tv, tcl);
		// Make sure the table is layed out correctly and looks cnsistent
		Table table = tv.getTable();
		helper.setLook(table, Props.WIDGET_STYLE_TABLE);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		attachCellEditors(tv, table);
		return tv;
	}

	private TableViewerColumn createSUVTableViewerColumn(TableViewer viewer, String title, int bound, int colNumber, TableColumnLayout layout) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(false);
		if ((colNumber == 0) || (colNumber == 1) || (colNumber == 2) || (colNumber == 3)) {
			layout.setColumnData(column, new ColumnWeightData(3/* 4 */, ColumnWeightData.MINIMUM_WIDTH, true));
		} else if (/* colNumber == 3 || */colNumber == 4) {
			layout.setColumnData(column, new ColumnWeightData(4 /* 6 */, ColumnWeightData.MINIMUM_WIDTH, true));
		} else {
			layout.setColumnData(column, new ColumnWeightData(0, ColumnWeightData.MINIMUM_WIDTH, true));
		}
		return viewerColumn;
	}

	private TableViewerColumn createTableViewerColumn(TableViewer viewer, String title, int bound, int colNumber, TableColumnLayout layout) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		if (colNumber == 0) {
			layout.setColumnData(column, new ColumnWeightData(5, ColumnWeightData.MINIMUM_WIDTH, true));
		} else {
			layout.setColumnData(column, new ColumnWeightData(10, ColumnWeightData.MINIMUM_WIDTH, true));
		}
		return viewerColumn;
	}

	private void doThatThing(SurvivorField survivor, int cIdx) {
		if (survivorPassThru.contains(survivor)) {
			SurvivorField tmpSF;
			if ((tmpSF = openEditor(survivor)) != null) {
// System.out.println("  # 1 " + item.getTabData().toString());
// item.setData(tmpSF);
// System.out.println("  # 2 " + item.getTabData().toString());
//
// System.out.println("\nOPEN SUCCESS sv: " + tmpSF.toString() + "     " + survivorPassThru.indexOf(tmpSF));
// System.out.println("OPEN SUCCESS it: " + item.getTabData().toString());
				String fieldSource = tmpSF.getConsolidationMethod().toString();
				if (fieldSource.startsWith(STACK_PREFIX)) {
					// create a list of the survivors index
					List<Integer> idxList = new ArrayList<Integer>();
					for (SurvivorField existingSurvivor : survivorPassThru) {
						// See if we already have the stack group
						if (existingSurvivor.getConsolidationMethod().equals(ConsolidationMethod.fromString(fieldSource))) {
							existingSurvivor.setDescending(survivor.isDescending());
							idxList.add(survivorPassThru.indexOf(existingSurvivor));
						}
					}// end for loop
					orderStackGroup(idxList, fieldSource);
				}
			}
		} else {// tmpSF is null
		// System.out.println("\n --- What happes here: " + survivor + "\n");
// for (SurvivorField svf: survivorPassThru){
// if(svf.getOutputName().contains("st1")){
// System.out.println(" * - " + svf.toString() + "   " + svf.getOutputName());
// }
// }
		}
	}

	private void enable() {
		if (isSurvivorSource) {
			wSurvivorPassThruComp.setVisible(true);
			wSimpleComp.setVisible(false);
			btnAdd.setVisible(true);
			btnRemove.setVisible(true);
		} else {
			wSurvivorPassThruComp.setVisible(false);
			wSimpleComp.setVisible(true);
			btnAdd.setVisible(false);
			btnRemove.setVisible(false);
		}
	}

	// @Override
	private String getDescription() {
		return getString("MatchupSourceDescription");
	}

	@SuppressWarnings("unchecked") private void getFields(boolean isInit) {
		// Get the currently displayed mdcStepData
		// @SuppressWarnings("unchecked")
		List<SourceFieldInfo> passThru = (List<SourceFieldInfo>) tvPassThru.getInput();
		// @SuppressWarnings("unchecked")
		List<SourceFieldInfo> filterOut = (List<SourceFieldInfo>) tvFilterOut.getInput();
		// If there are any fields then confirm that they want to clear and add the new mdcStepData
		if ((passThru.size() != 0) || (filterOut.size() != 0)) {
			if (!isInit) {
				MessageDialog md = new MessageDialog(dialog.getShell(), getString("GetFields.Title"), //$NON-NLS-1$
						null, getString("GetFields.Message"), //$NON-NLS-1$
						MessageDialog.WARNING, new String[] { getString("GetFields.AddNew"), //$NON-NLS-1$
						getString("GetFields.ClearAndAdd"), //$NON-NLS-1$
						getString("GetFields.Cancel"), }, 0); //$NON-NLS-1$
				Window.setDefaultImage(GUIResource.getInstance().getImageSpoon());
				// Cancel clicked?
				int button = md.open() & 0xFF;
				if (button == 2) {
					return;
				}
				// Clear before add?
				if (button == 1) {
					passThru.clear();
					filterOut.clear();
				}
			}
		}
		// Add all new source fields to the pass thru table
		SortedMap<String, SourceFieldInfo> sourceFields = getSourceFields();
		for (SourceFieldInfo field : sourceFields.values()) {
			if (!passThru.contains(field) && !filterOut.contains(field)) {
				passThru.add(field);
			}
		}
		dialog.logDebug("Get " + passThru.size() + " fields");
		// Refresh tables
		refresh();
		// Indicate something has changed
		dialog.setChanged();
	}

	// @Override
	private PassThruMeta getPassThruMeta(MDCheckStepData data) {
		return data.getSourcePassThru();
	}

	// @Override
	private SortedMap<String, SourceFieldInfo> getSourceFields() {
		return dialog.getSourceFields();
	}

	@SuppressWarnings("unchecked") private void getSurvivorFields(boolean isInit) {
		List<SurvivorField> srvPassThru = null;
		if (tvSurvivorPassThru != null) {
			srvPassThru = (List<SurvivorField>) tvSurvivorPassThru.getInput();
		} else {
			srvPassThru = new ArrayList<SurvivorField>();
		}
		// If there are any fields then confirm that they want to clear and add
		// the new mdcStepData
		if (!Const.isEmpty(srvPassThru)) {
			if (!isInit) {
				MessageDialog md = new MessageDialog(dialog.getShell(), getString("GetFields.Title"), //$NON-NLS-1$
						null, getString("GetFields.Message"), //$NON-NLS-1$
						MessageDialog.WARNING, new String[] { getString("GetFields.AddNew"), //$NON-NLS-1$
						getString("GetFields.ClearAndAdd"), //$NON-NLS-1$
						getString("GetFields.Cancel"), }, 0); //$NON-NLS-1$
				Window.setDefaultImage(GUIResource.getInstance().getImageSpoon());
				// Cancel clicked?
				int button = md.open() & 0xFF;
				if (button == 2) {
					return;
				}
				// Clear before add?
				if (button == 1) {
					srvPassThru.clear();
					survivorOutputNames.clear();
					clearRows();
				}
			}
		}
		for (int i = 0; i < vmiList.size(); i++) {
			String name = vmiList.getValueMeta(i).getName();
			if (!survivorOutputNames.contains(name)) {
				SurvivorField survivor = new SurvivorField(vmiList.getValueMeta(i), i);
				srvPassThru.add(survivor);
			}
		}
		survivorOutputNames.clear();
		for (SurvivorField sr : srvPassThru) {
			survivorOutputNames.add(sr.getOutputName());
		}
		updateSurvivorTable();
		// Indicate something has changed
		dialog.setChanged();
	}

	// @Override
	private String getTabTitle() {
		return getString("MatchupSourceTabTitle");
	}

	private SurvivorField openEditor(SurvivorField field) {
		if (!field.getDisplayPrioritization().contains(STACK_PREFIX)) {
			SurvivorEditorDialog sed = new SurvivorEditorDialog(dialog, mdcStepData);
			try {
				sed.setSurvivorRecord(field);
			} catch (CloneNotSupportedException e) {
				// TODO Handle Clone Not Supported ?
			}
			int fieldIdx = survivorPassThru.indexOf(field);
			if (sed.open()) {
				survivorPassThru.remove(field);
				field = sed.getSurvivorRecord();
				survivorPassThru.add(fieldIdx, field);
				updateSurvivorTable();
				dialog.setChanged();
				return field;
			} else {
				return null;
			}
		} else {
			MessageBox box = new MessageBox(dialog.getShell(), SWT.APPLICATION_MODAL | SWT.ICON_INFORMATION);
			box.setText(BaseMessages.getString(PKG, "MDCheckDialog.WarningEditDialog.Title"));
			box.setMessage(BaseMessages.getString(PKG, "MDCheckDialog.WarningEditDialog.Message", field.getConsolidationMethod()));
			box.open();
			return null;
		}
	}

	private void orderStackGroup(List<Integer> idxList, String stkGrp) {
		String prior = "";
		String colSrc = "";
		int stackIdx = 0;
		Collections.sort(idxList);
		SurvivorField field;
		for (int idx : idxList) {
			field = survivorPassThru.get(idx);
			if (Const.isEmpty(colSrc) && !Const.isEmpty(field.getSource())) {
				colSrc = field.getSource();
			}
			if (Const.isEmpty(prior) && !Const.isEmpty(field.getPrioritization())) {
				prior = field.getPrioritization();
			}
		}
		for (int idx : idxList) {
			field = survivorPassThru.get(idx);
			if (stackIdx == 0) {
				field.setSource(colSrc);
				field.setPrioritization(prior);
				field.setStackIndex(stackIdx);
			} else {
				if (!Const.isEmpty(colSrc)) {
					field.setSource("[" + stkGrp + " column]:" + colSrc);
				}
				if (!Const.isEmpty(prior)) {
					field.setPrioritization("[" + stkGrp + " priortization]:" + prior);
				}
				field.setStackIndex(stackIdx);
			}
			stackIdx++;
		}
	}

	private void removeRow() {
		int index = 0;
		index = tvSurvivorPassThru.getTable().getSelectionIndex();
		if (index < 0) {
			return;
		}
		SurvivorField sr = (SurvivorField) tvSurvivorPassThru.getTable().getItem(index).getData();
		survivorOutputNames.remove(sr.getOutputName());
		survivorPassThru.remove(sr);
		Control[] rows = tvSurvivorPassThru.getTable().getChildren();
		for (int i = 0; i < rows.length; i++) {
			if (i > 2) {
				rows[i].dispose();
			}
		}
		tvSurvivorPassThru.getTable().remove(index);
		getData(mdcStepData);
		updateSurvivorTable();
	}

	private void setButtonPosition() {
		if (isSurvivorSource) {
			// This code centers the button and moves it correctly when dialog
			// is resized
			{
				btnGet.pack(true);
				int width = btnGet.getBounds().width + 20;// 10
				if ((width % 2) == 1) {
					width++;
				}
				FormData formData = new FormData();
				int leftOffset = -(width + helper.margin) / 2;
				formData.left = new FormAttachment(50, leftOffset * 3);
				formData.right = new FormAttachment(btnGet, width + helper.margin);
				formData.bottom = new FormAttachment(100, -helper.margin);
				btnGet.setLayoutData(formData);
				formData = new FormData();
				formData.left = new FormAttachment(btnGet, helper.margin);
				// fd.right = new FormAttachment(labelAlign, -helper.margin / 2);
				formData.bottom = new FormAttachment(100, -helper.margin);
				formData.width = width;
				btnAdd.setLayoutData(formData);
				formData = new FormData();
				formData.left = new FormAttachment(btnAdd, helper.margin);
				// fd.right = new FormAttachment(labelAlign, -helper.margin / 2);
				formData.bottom = new FormAttachment(100, -helper.margin);
				formData.width = width;
				btnRemove.setLayoutData(formData);
			}
		} else {
			{
				btnAdd.setVisible(false);
				btnRemove.setVisible(false);
				btnGet.pack(true);
				int width = btnGet.getBounds().width + 20;// 10
				if ((width % 2) == 1) {
					width++;
				}
				FormData formData = new FormData();
				int leftOffset = -(width + helper.margin) / 2;
				formData.left = new FormAttachment(50, leftOffset);
				formData.right = new FormAttachment(btnGet, width + helper.margin);
				formData.bottom = new FormAttachment(100, -helper.margin);
				btnGet.setLayoutData(formData);
			}
		}
		wControlsComp.pack(true);
		wControlsComp.redraw();
		updateSurvivorTable();
	}

	private void updateSurvivorTable() {
		tvSurvivorPassThru.setInput(survivorPassThru);
		tvSurvivorPassThru.refresh();
	}

	/**
	 * Enables a drag-n-drop interface between the tables.
	 *
	 * @param viewer
	 * @param id
	 */
	protected void addDND(final TableViewer viewer, final String id) {
		// Enable moving from table
		final Table table = viewer.getTable();
		DragSource source = new DragSource(table, DND.DROP_MOVE);
		// We will be moving a text string
		final MDCheckTransfer myTransfer = MDCheckTransfer.getInstance();
		source.setTransfer(new Transfer[] { myTransfer });
		// Listen for drag source events
		source.addDragListener(new DragSourceListener() {
			public void dragFinished(DragSourceEvent event) {
				// System.out.println("dragFinished(event=" + event + ")");
				if (event.detail == DND.DROP_MOVE) {
					// System.out.println("dragFinished.1:");
					// Get the fields displayed by this viewer
					@SuppressWarnings("unchecked") List<SourceFieldInfo> fields = (List<SourceFieldInfo>) viewer.getInput();
					// Delete selected fields
					int[] indecis = table.getSelectionIndices();
					for (int i = 0; i < indecis.length; i++) {
						fields.remove(indecis[i] - i);
					}
					// Refresh the viewer
					viewer.refresh();
					// Clear selection
					table.setSelection(EMPTY);
					// Indicate something has changed
					dialog.setChanged();
				}
			}

			public void dragSetData(DragSourceEvent event) {
				// System.out.println("dragSetData(event=" + event + ")");
				if (myTransfer.isSupportedType(event.dataType)) {
					// System.out.println("dragSetData.1:");
					// Get the fields displayed by this viewer
					@SuppressWarnings("unchecked") List<SourceFieldInfo> fields = (List<SourceFieldInfo>) viewer.getInput();
					// Convert selected elements into a comma-separated list of field names
					MDCheckTransfer.Type datum = new MDCheckTransfer.Type();
					datum.setID(id);
					int[] indices = table.getSelectionIndices();
					for (int indice : indices) {
						datum.addField(fields.get(indice).getName());
					}
					event.data = datum;
					// System.out.println("dragSetData.2: event.mdcStepData=" + event.mdcStepData);
				}
			}

			public void dragStart(DragSourceEvent event) {
				// System.out.println("dragStart(event=" + event + ")");
				// Drag if one or mroe rows are selected
				TableItem[] selection = table.getSelection();
				if ((selection == null) || (selection.length == 0)) {
					event.doit = false;
					// System.out.println("dragStart.1: event.doit=" + event.doit);
				}
			}
		});
		// Enable moving to table
		final DropTarget target = new DropTarget(table, DND.DROP_MOVE);
		// Receive mdcStepData in text format
		target.setTransfer(new Transfer[] { myTransfer });
		// List for drag drop events
		target.addDropListener(new DropTargetListener() {
			public void dragEnter(DropTargetEvent event) {
				// System.out.println("dragEnter(event=" + event + ")");
				// System.out.println("dragEnter.1: event.detail=" + event.detail);
				if (event.detail == DND.DROP_DEFAULT) {
					if ((event.operations & DND.DROP_MOVE) != 0) {
						event.detail = DND.DROP_MOVE;
					} else {
						event.detail = DND.DROP_NONE;
					}
				}
				// System.out.println("dragEnter.2: event.detail=" + event.detail);
				for (TransferData dataType : event.dataTypes) {
					if (myTransfer.isSupportedType(dataType)) {
						// System.out.println("dragEnter.3:");
						// Don't drop on ourselves.
						MDCheckTransfer.Type datum = (MDCheckTransfer.Type) myTransfer.nativeToJava(event.currentDataType);
						if (datum != null) {
							if (!datum.getID().equals(id)) {
								event.currentDataType = dataType;
								// System.out.println("dragEnter.4: event.currentDataType=" + event.currentDataType);
							} else {
								event.detail = DND.DROP_NONE;
							}
						}
						if (event.detail != DND.DROP_MOVE) {
							event.detail = DND.DROP_NONE;
						}
						// System.out.println("dragEnter.5: event.detail=" + event.detail);
						break;
					}
				}
				// System.out.println("dragEnter.6:");
			}

			public void dragLeave(DropTargetEvent event) {
			}

			public void dragOperationChanged(DropTargetEvent event) {
				if (event.detail == DND.DROP_DEFAULT) {
					if ((event.operations & DND.DROP_MOVE) != 0) {
						event.detail = DND.DROP_MOVE;
					} else {
						event.detail = DND.DROP_NONE;
					}
				}
				if (myTransfer.isSupportedType(event.currentDataType)) {
					if (event.detail != DND.DROP_MOVE) {
						event.detail = DND.DROP_NONE;
					}
				}
			}

			public void dragOver(DropTargetEvent event) {
				// System.out.println("dragOver(event=" + event + ")");
				event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;
				// System.out.println("dragOver.1: event.feedback=" + event.feedback);
				if (myTransfer.isSupportedType(event.currentDataType)) {
					MDCheckTransfer.Type datum = (MDCheckTransfer.Type) myTransfer.nativeToJava(event.currentDataType);
					if (datum != null) {
						// System.out.println("dragOver.2: datum = " + datum);
						if (datum.getID().equals(id)) {
							event.feedback = DND.FEEDBACK_NONE;
							// System.out.println("dragOver.3: event.feedback=" + event.feedback);
						}
					}
				}
			}

			public void drop(DropTargetEvent event) {
				// System.out.println("drop(event=" + event + ")");
				if (myTransfer.isSupportedType(event.currentDataType)) {
					// System.out.println("drop.1:");
					// Get the fields displayed by this viewer
					@SuppressWarnings("unchecked") List<SourceFieldInfo> fields = (List<SourceFieldInfo>) viewer.getInput();
					// Get the possible source fields
					SortedMap<String, SourceFieldInfo> sourceFields = getSourceFields();
					// Add dropped fields to end of table
					int added = 0;
					MDCheckTransfer.Type datum = (MDCheckTransfer.Type) event.data;
					if (datum != null) {
						// System.out.println("drop.1: datum=" + datum);
						// Don't accept drops on source
						if (datum.getID().equals(id)) {
							event.detail = DND.DROP_NONE;
							// System.out.println("drop.2:: event.detail=" + event.detail);
							return;
						}
						for (int i = 0; i < datum.numFields(); i++) {
							String field = datum.getField(i);
							SourceFieldInfo sourceField = sourceFields.get(field);
							if (sourceField != null) {
								fields.add(sourceField);
								added++;
							}
						}
					}
					// Refresh the viewer
					viewer.refresh();
					// Show the fields just added
					int last = fields.size() - 1;
					int first = last - (added - 1);
					table.showItem(table.getItem(first));
					table.showItem(table.getItem(last));
					table.setSelection(EMPTY);
					// Indicate that something has changed
					dialog.setChanged();
				}
			}

			public void dropAccept(DropTargetEvent event) {
				// System.out.println("dragAccept(event=" + event + ")");
				if (myTransfer.isSupportedType(event.currentDataType)) {
					// System.out.println("dragAccept.1:");
					MDCheckTransfer.Type datum = (MDCheckTransfer.Type) myTransfer.nativeToJava(event.currentDataType);
					if (datum != null) {
						// System.out.println("dragAccept.2: datum = " + datum);
						// Don't accept drops on source
						if (datum.getID().equals(id)) {
							event.detail = DND.DROP_NONE;
							// System.out.println("dragAccept.3:: event.detail=" + event.detail);
						}
					}
				}
			}
		});
	}

	/**
	 * @param comp
	 * @param viewer
	 * @param layout
	 */
	protected void createColumns(Composite comp, TableViewer viewer, TableColumnLayout layout) {
		viewer.setContentProvider(new ArrayContentProvider());
		String[] titles = { getString("ColumnInfo.ColumnName"), getString("ColumnInfo.CurrentUsage") };
		int[] bounds = { 100, 100 };
		// First column is for the field name
		TableViewerColumn col = createTableViewerColumn(viewer, titles[0], bounds[0], 0, layout);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override public String getText(Object element) {
				SourceFieldInfo field = (SourceFieldInfo) element;
				return field.getName();
			}
		});
	}

	/**
	 * Called to create the table viewer for fields values
	 *
	 * @param parent
	 * @return
	 */
	protected TableViewer createTable(Composite parent) {
		// Give this composite a table column layout view so we can control the number
		// of columns displayed. Only a single table viewer can be held in each composite in order
		// for this to work
		TableColumnLayout tcl = new TableColumnLayout();
		parent.setLayout(tcl);
		// Create table viewer
		TableViewer tv = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		// Create columns in table viewer
		createColumns(parent, tv, tcl);
		// Make sure the table is layed out correctly and looks cnsistent
		Table table = tv.getTable();
		helper.setLook(table, Props.WIDGET_STYLE_TABLE);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		return tv;
	}

	protected String getString(String key, String... args) {
		return BaseMessages.getString(PKG, "MDCheckDialog.PassThruTab." + key, args);
	}

	protected void refresh() {
		tvPassThru.refresh();
		tvFilterOut.refresh();
	}
}
