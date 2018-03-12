package com.melissadata.kettle.profiler.ui;

import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.i18n.BaseMessages;

import com.melissadata.kettle.profiler.MDProfilerDialog;
import com.melissadata.kettle.profiler.MDProfilerMeta;
import com.melissadata.kettle.profiler.data.ProfilerEnum.ColumnType;
import com.melissadata.kettle.profiler.data.ProfilerEnum.ExpectedContent;
import com.melissadata.kettle.profiler.editor.RecordSettingsDialog;
import com.melissadata.kettle.profiler.support.MDProfilerHelper;
import com.melissadata.kettle.profiler.support.MDTab;

public class InputFieldsTab implements MDTab {
	private static Class<?>					PKG	= MDProfilerMeta.class;
	private MDProfilerHelper				helper;
	private MDProfilerDialog				dialog;
	private Composite						wProfileComp;
	private Composite						wTableComp;
	private TableViewer						tvInputFields;
	private MDProfilerMeta					profilerMeta;
	private HashMap<String, ProfileRecord>	profileRecords;
	private Label							description;

	public InputFieldsTab(MDProfilerDialog dialog) {
		helper = dialog.getHelper();
		this.dialog = dialog;
		// Create the tab
		CTabItem wTab = new CTabItem(dialog.getTabFolder(), SWT.NONE);
		wTab.setText(getString("TabTitle"));
		wTab.setData(this);
		// Create a scrolling region within the tab
		ScrolledComposite wSComp = new ScrolledComposite(dialog.getTabFolder(), SWT.V_SCROLL | SWT.H_SCROLL);
		wSComp.setLayout(new FillLayout());
		// Create the composite that will hold the contents of the tab
		Composite wComp = new Composite(wSComp, SWT.NONE);
		helper.setLook(wComp);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		wComp.setLayout(fl);
		// Description line
		description = new Label(wComp, SWT.LEFT | SWT.WRAP);
		// description.setText(getString("Description"));
		helper.setLook(description);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment(100, helper.margin);
		fd.top = new FormAttachment(0, helper.margin);
		description.setLayoutData(fd);
		description.setText("Map");
		createProfilerTable(wComp);
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
		// Initialize the Scrolled Composite with the new composite
		wSComp.setContent(wComp);
		wSComp.setExpandHorizontal(true);
		wSComp.setExpandVertical(true);
		wSComp.setMinWidth(bounds.width);
		wSComp.setMinHeight(bounds.height);
		// Initialize the tab with the scrolled composite
		wTab.setControl(wSComp);
		description.setText(getString("Description"));// This is done here due to sizing of dialog
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDTab#advancedConfigChanged()
	 */
	@Override
	public void advancedConfigChanged() {
		// Check for enablement change
		enable();
	}

	private void attachCellEditors(final TableViewer viewer, Composite parent) {
		final String[] colProps = new String[] { "PROFILE", "PASSHROUGH", "RESULTS", "COL_NAME", "EXPECTED_CONTENT", "DATA_TYPE", "LENGTH", "PRECISION", "SCALE", "SETTINGS", "EDIT" };
		viewer.setColumnProperties(colProps);
		viewer.setCellModifier(new ICellModifier() {
			@Override
			public boolean canModify(Object element, String property) {
				if (property.equals("LENGTH") || property.equals("PRECISION") || property.equals("SCALE") || property.equals("SETTINGS"))
					return true;
				return false;
			}

			@Override
			public Object getValue(Object element, String property) {
				if (property.equals("LENGTH"))
					return ((ProfileRecord) element).getLength();
				else if (property.equals("PRECISION"))
					return ((ProfileRecord) element).getPrecision();
				else if (property.equals("SCALE"))
					return ((ProfileRecord) element).getScale();
				else if (property.equals("SETTINGS"))
					return ((ProfileRecord) element).getSettingsString();
				return null;
			}

			@Override
			public void modify(Object element, String property, Object value) {
				TableItem tableItem = (TableItem) element;
				ProfileRecord profileRecord = (ProfileRecord) tableItem.getData();
				if (property.equals("LENGTH") || property.equals("PRECISION") || property.equals("SCALE") || property.equals("SETTINGS")) {
					editProfileRecord(profileRecord);
					viewer.refresh(profileRecord);
					dialog.setChanged();
				} else {
					viewer.refresh(profileRecord);
				}
				dialog.setChanged();
			}
		});
		CheckboxCellEditor cbCE = new CheckboxCellEditor(parent);
		viewer.setCellEditors(new CellEditor[] { cbCE, new CheckboxCellEditor(parent), new CheckboxCellEditor(parent), new TextCellEditor(parent), new ComboBoxCellEditor(), new TextCellEditor(parent), new TextCellEditor(parent),
				new TextCellEditor(parent), new TextCellEditor(parent), new TextCellEditor(parent) });
	}

	private void createInputColumns(Composite comp, TableViewer viewer, TableColumnLayout layout) {
		viewer.setContentProvider(new ArrayContentProvider());
		//FIXME put in messages
		String[] titles = { "Profile", "Pass Through", "Results", "Column Name", "Expected Content", "Data Type", "Length", "Precision", "Scale", "Settings", " " };
		// ############################################################
		// Profile
		TableViewerColumn col;
		col = createSUVTableViewerColumn(viewer, titles[0], 10, 0, layout);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				TableItem item = (TableItem) cell.getItem();
				final ProfileRecord profileRecord = (ProfileRecord) item.getData();
				final Button button = new Button((Composite) cell.getViewerRow().getControl(), SWT.CHECK);
				button.setSelection(profileRecord.isDoProfile());
				button.addSelectionListener(new SelectionListener() {
					@Override
					public void widgetDefaultSelected(SelectionEvent arg0) {
						//
					}

					@Override
					public void widgetSelected(SelectionEvent arg0) {
						profileRecord.setDoProfile(button.getSelection());
						dialog.setChanged();
						enable();
					}
				});
				TableEditor editor = new TableEditor(item.getParent());
				editor.grabHorizontal = true;
				editor.grabVertical = true;
				editor.setEditor(button, item, cell.getColumnIndex());
				editor.layout();
			}
		});
		// ############################################################
		// Pass Through
		col = createSUVTableViewerColumn(viewer, titles[1], 35, 1, layout);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				TableItem item = (TableItem) cell.getItem();
				final ProfileRecord profileRecord = (ProfileRecord) item.getData();
				final Button button = new Button((Composite) cell.getViewerRow().getControl(), SWT.CHECK);
				button.setSelection(profileRecord.isDoPassThrough());
				button.addSelectionListener(new SelectionListener() {
					@Override
					public void widgetDefaultSelected(SelectionEvent arg0) {
						//
					}

					@Override
					public void widgetSelected(SelectionEvent arg0) {
						profileRecord.setDoPassThrough(button.getSelection());
						dialog.setChanged();
					}
				});
				TableEditor editor = new TableEditor(item.getParent());
				editor.grabHorizontal = true;
				editor.grabVertical = true;
				editor.setEditor(button, item, cell.getColumnIndex());
				editor.layout();
			}
		});
		// ############################################################
		// Results
		col = createSUVTableViewerColumn(viewer, titles[2], 10, 2, layout);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				TableItem item = (TableItem) cell.getItem();
				final ProfileRecord profileRecord = (ProfileRecord) item.getData();
				final Button button = new Button((Composite) cell.getViewerRow().getControl(), SWT.CHECK);
				button.setText(" ");
				button.setSelection(profileRecord.isDoResults());
				button.addSelectionListener(new SelectionListener() {
					@Override
					public void widgetDefaultSelected(SelectionEvent arg0) {
						//
					}

					@Override
					public void widgetSelected(SelectionEvent arg0) {
						if (button.getSelection()) {
							if (!profileRecord.isDoProfile()) {
								showProfileMessage(profileRecord.getColumnName());
								button.setSelection(false);
							} else {
								profileRecord.setDoResults(button.getSelection());
							}
						} else {
							profileRecord.setDoResults(button.getSelection());
						}
						dialog.setChanged();
					}
				});
				TableEditor editor = new TableEditor(item.getParent());
				editor.grabHorizontal = true;
				editor.grabVertical = true;
				editor.setEditor(button, item, cell.getColumnIndex());
				editor.layout();
			}
		});
		// ############################################################
		// Column Name
		col = createSUVTableViewerColumn(viewer, titles[3], 100, 3, layout);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				ProfileRecord profileRecord = (ProfileRecord) element;
				return profileRecord.getColumnName();
			}
		});
		// ############################################################
		// Expected Content
		col = createSUVTableViewerColumn(viewer, titles[4], 80, 4, layout);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				final TableItem item = (TableItem) cell.getItem();
				final ProfileRecord profileRecord = (ProfileRecord) item.getData();
				final CCombo wCombo = new CCombo((Composite) cell.getViewerRow().getControl(), SWT.NONE);
				for (ExpectedContent cm : ExpectedContent.values()) {
					wCombo.add(cm.name() + "  (" + cm.getDescription() + ")");
				}
				wCombo.setVisibleItemCount(9);
				wCombo.setText(profileRecord.getExpectedContent().name());
				wCombo.setEditable(false);
				helper.setLook(wCombo);
				wCombo.addSelectionListener(new SelectionListener() {
					@Override
					public void widgetDefaultSelected(SelectionEvent arg0) {
						//
					}

					@Override
					public void widgetSelected(SelectionEvent e) {
						String tt = wCombo.getText();
						wCombo.setText(tt.substring(0, tt.indexOf("(")));// don't display description
						profileRecord.setExpectedContent(wCombo.getText());
					}
				});
				// Update dialog when something changes
				wCombo.addModifyListener(new ModifyListener() {
					@Override
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
		// ############################################################
		// Data Type
		col = createSUVTableViewerColumn(viewer, titles[5], 25, 5, layout);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				ProfileRecord profileRecord = (ProfileRecord) element;
				return profileRecord.getColumnType().encode();
			}
		});
		// ############################################################
		// Length
		col = createSUVTableViewerColumn(viewer, titles[6], 10, 6, layout);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				ProfileRecord profileRecord = (ProfileRecord) element;
				return profileRecord.getLength();
			}
		});
		// ############################################################
		// Precision
		col = createSUVTableViewerColumn(viewer, titles[7], 10, 7, layout);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				ProfileRecord profileRecord = (ProfileRecord) element;
				return profileRecord.getPrecision();
			}
		});
		// ############################################################
		// Scale
		col = createSUVTableViewerColumn(viewer, titles[8], 10, 8, layout);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				ProfileRecord profileRecord = (ProfileRecord) element;
				return profileRecord.getScale();
			}
		});
		// ############################################################
		// Settings
		col = createSUVTableViewerColumn(viewer, titles[9], 90, 9, layout);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				ProfileRecord profileRecord = (ProfileRecord) element;
				return profileRecord.getSettingsString();
			}
		});
		// ############################################################
		// Edit Button
		col = createSUVTableViewerColumn(viewer, titles[10], 20, 10, layout);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				TableItem item = (TableItem) cell.getItem();
				final ProfileRecord profileRecord = (ProfileRecord) item.getData();
				Button button = new Button((Composite) cell.getViewerRow().getControl(), SWT.NONE);
				button.setText("...");
				button.addSelectionListener(new SelectionListener() {
					@Override
					public void widgetDefaultSelected(SelectionEvent arg0) {
						//
					}

					@Override
					public void widgetSelected(SelectionEvent arg0) {
						editProfileRecord(profileRecord);
						dialog.setChanged();
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

	private void createProfilerTable(Composite parent) {
		// Create the composites that will hold the table viewers
		wProfileComp = new Composite(parent, SWT.NONE);
		helper.setLook(wProfileComp);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(description, helper.margin * 2);
		fd.right = new FormAttachment(100, -helper.margin / 2);
		wProfileComp.setLayoutData(fd);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		wProfileComp.setLayout(fl);
		Label lblProfileOptions = new Label(wProfileComp, SWT.LEFT | SWT.WRAP);
		lblProfileOptions.setText(getString("ProfileOptionsDescription"));
		helper.setLook(lblProfileOptions);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(0, helper.margin * 2);
		fd.right = new FormAttachment(70, -helper.margin / 2);
		lblProfileOptions.setLayoutData(fd);
		wTableComp = new Composite(wProfileComp, SWT.BORDER);
		helper.setLook(wTableComp);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(lblProfileOptions, helper.margin * 2);
		fd.right = new FormAttachment(100, -helper.margin / 2);
		wTableComp.setLayoutData(fd);
		tvInputFields = createInputTable(wTableComp);
		// make sure tables reach to the button
		((FormData) wTableComp.getLayoutData()).bottom = new FormAttachment(100, -helper.margin);
		((FormData) wProfileComp.getLayoutData()).bottom = new FormAttachment(100, -helper.margin);
	}

	private TableViewerColumn createSUVTableViewerColumn(TableViewer viewer, String title, int bound, int colNumber, TableColumnLayout layout) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(false);
		if (colNumber == 10) {
			layout.setColumnData(column, new ColumnWeightData(0, bound, true));
		} else {
			layout.setColumnData(column, new ColumnWeightData(1, bound, true));
		}
		return viewerColumn;
	}

	@SuppressWarnings("unchecked")
	private void editProfileRecord(ProfileRecord profileRecord) {
		RecordSettingsDialog rsd = new RecordSettingsDialog(dialog, SWT.NONE);
		rsd.setProfileRecord(profileRecord.getColumnName());
		rsd.open();
		profileRecords = (HashMap<String, ProfileRecord>) profilerMeta.getProfileRecords().clone();
		tvInputFields.setInput(profileRecords.values());
	}

	/**
	 * Called to handle enable of controls based on input settings
	 */
	// FIXME enable
	public void enable() {
	}

	/**
	 * Loads the dialog data into the meta structure
	 */
	@Override
	public void getData(MDProfilerMeta profileMeta) {
		for (ProfileRecord pr : profileRecords.values()) {
			if (pr.isDoResults() && !pr.isDoProfile()) {
				pr.setDoProfile(true);
			}
		}
		profileMeta.setProfileRecords(profileRecords);
	}

	/**
	 * @return The URL for help information
	 */
	@Override
	public String getHelpURLKey() {
		return BaseMessages.getString(PKG, "MDProfiler.Plugin.Help.InputFieldsTab");
	}

	/**
	 * @param key
	 * @param args
	 * @return
	 */
	private String getString(String key, String... args) {
		return BaseMessages.getString(PKG, "MDProfilerDialog.InputTab." + key, args);
	}

	/**
	 * Loads the meta data into the dialog tab
	 *
	 * @param profilerMeta
	 * @return
	 */
	@Override
	public boolean init(MDProfilerMeta profilerMeta) {
		this.profilerMeta = profilerMeta;
		List<ValueMetaInterface> inputMeta = dialog.getInputValueMeta();
		profileRecords = new HashMap<String, ProfileRecord>();
		for (ValueMetaInterface vmi : inputMeta) {
			String columnName = vmi.getName();
			if (profilerMeta.getProfileRecords().get(vmi.getName()) == null) {
				profileRecords.clear();
				break;
			} else {
				profileRecords.put(columnName, profilerMeta.getProfileRecords().get(columnName).clone());
			}
		}
		if (profileRecords.isEmpty()) {
			ProfileRecord pr;
			for (ValueMetaInterface vmi : inputMeta) {
				pr = new ProfileRecord(vmi.clone(), true, true, true, false, "", false, "", "", false, "", vmi.getName(), ExpectedContent.String, translateType(vmi.getTypeDesc()), false, String.valueOf(vmi.getLength()), false, String.valueOf(vmi
						.getPrecision()), false, "0");
				profileRecords.put(vmi.getName(), pr);
			}
			profilerMeta.setProfileRecords(profileRecords);
		}
		tvInputFields.setInput(profileRecords.values());
		// Set initial enable
		enable();
		return true;
	}

	private boolean showProfileMessage(String fieldName) {
		String message = BaseMessages.getString(PKG, "MDProfilerDialog.InputTab.SelectProfile.Message", fieldName);
		MessageBox box = new MessageBox(dialog.getShell(), SWT.OK | SWT.APPLICATION_MODAL | SWT.ICON_WARNING);
		box.setText(BaseMessages.getString(PKG, "MDProfilerDialog.InputTab.SelectProfile.Title"));
		box.setMessage(message);
		if (box.open() == SWT.NO) {
		}
		return true;
	}

	private ColumnType translateType(String vmiType) {
		if ("BigNumber".equals(vmiType))
			return ColumnType.BigNumber;
		if ("Binary".equals(vmiType))
			return ColumnType.Binary;
		if ("Boolean".equals(vmiType))
			return ColumnType.Boolean;
		if ("Date".equals(vmiType))
			return ColumnType.Date;
		if ("Integer".equals(vmiType))
			return ColumnType.Integer;
		if ("Internet Address".equals(vmiType))
			return ColumnType.String;
		if ("Number".equals(vmiType))
			return ColumnType.Number;
		if ("Timestamp".equals(vmiType))
			return ColumnType.TimeStamp;
		if ("String".equals(vmiType))
			return ColumnType.String;
		return null;
	}
}
