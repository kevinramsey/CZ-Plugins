package com.melissadata.kettle.personator.ui;

import java.text.Collator;
import java.util.HashMap;
import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.i18n.BaseMessages;
import com.melissadata.kettle.personator.MDPersonatorDialog;
import com.melissadata.kettle.personator.MDPersonatorMeta;
import com.melissadata.kettle.personator.MDPersonatorMeta.RowOutput;
import com.melissadata.kettle.personator.data.PersonatorFields;
import com.melissadata.kettle.personator.support.MDPersonatorHelper;
import com.melissadata.kettle.personator.support.MDTab;
import com.melissadata.kettle.personator.support.MetaVal;

public class OutputPersonatorTab implements MDTab {

	private static Class<?> PKG = MDPersonatorMeta.class;
	private                             MDPersonatorDialog dialog;
	private                             MDPersonatorHelper helper;
	private                             Group              outputGroup;
	@SuppressWarnings("unused") private Group              infoGroup;
	private                             Composite          gtComp;
	private                             Table              groupTable;
	private String[] groupTableTitles = { "Output Group", "Output Name" };
	private Table outTable;
	private String[] groupOutTitles = { "Group", "Output", "Column Name" };
	private       Button           bAddGroup;
	private       Button           bAddColumn;
	private       Button           bAddAll;
	private       Button           bDeleteRow;
	private       Button           bDeleteGroup;
	private       Button           bDeleteAll;
	private       Label            lGrpDetails;
	private       Label            lRowDetails;
	private       Label            groupTableTitle;
	private       Label            outTableTitle;

//	private boolean isGeoCoding;
//	private boolean doReresh;
	final private TableEditor      editor;
	private       SelectionAdapter lsTableEdit;
	final int EDITABLECOLUMN = 2;

//	private int grpPos = 0;
//	private int outPos = 0;

	public OutputPersonatorTab(MDPersonatorDialog dialog) {

		helper = dialog.getHelper();
		this.dialog = dialog;

		// Create the tab
		final CTabFolder wTabFolder = dialog.getTabFolder();
		final CTabItem   wTab       = new CTabItem(dialog.getTabFolder(), SWT.NONE);
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
		Label description = new Label(wComp, SWT.LEFT | SWT.WRAP);
		description.setText(getString("Description"));
		helper.setLook(description);

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(0, helper.margin);
		description.setLayoutData(fd);

		// output address group
		outputGroup = new Group(wComp, SWT.NONE);
		outputGroup.setText(getString("OutputGroup.Label"));
		helper.setLook(outputGroup);

		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(description, 2 * helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		fd.bottom = new FormAttachment(90, -helper.margin);
		outputGroup.setLayoutData(fd);

		outputGroup.setLayout(fl);

		gtComp = createGroupsTable(outputGroup, null);
		createInfo(outputGroup, null);

		createOutputTable(outputGroup, gtComp);

		editor = new TableEditor(outTable);
		//The editor must have the same size as the cell and must
		//not be any smaller than 50 pixels.
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.minimumWidth = 50;

		lsTableEdit = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {

				editOutName();
			}
		};

		outTable.addSelectionListener(lsTableEdit);

		// Fit the composite within its container (the scrolled composite)
		fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(0, 0);
		fd.right = new FormAttachment(100, 0);
		fd.bottom = new FormAttachment(100, 0);
		wComp.setLayoutData(fd);

		Rectangle bounds = wComp.getBounds();

		// Initialize the Scrolled Composite with the new composite
		wSComp.setContent(wComp);
		wSComp.setExpandHorizontal(true);
		wSComp.setExpandVertical(true);
		wSComp.setMinWidth(bounds.width);
		wSComp.setMinHeight(bounds.height);

		// Initialize the tab with the scrolled composite
		wTab.setControl(wSComp);

		wTabFolder.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent event) {

				widgetSelected(event);
			}

			public void widgetSelected(SelectionEvent event) {

				if (wTabFolder.getSelection() == wTab) {
					setMapping();
				}
			}
		});
	}

	private void setMapping() {

		dialog.disableMapping();
	}

	private void editOutName() {

		// Clean up any previous editor control
		Control oldEditor = editor.getEditor();
		if (oldEditor != null) {
			oldEditor.dispose();
		}

		// Identify the selected row
		TableItem item = outTable.getItem(outTable.getSelectionIndex());
		if (item == null) {
			return;
		}

		// The control that will be the editor must be a child of the Table
		Text newEditor = new Text(outTable, SWT.NONE);
		newEditor.setText(item.getText(EDITABLECOLUMN));
		newEditor.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent me) {

				Text      text   = (Text) editor.getEditor();
				RowOutput rowTst = (RowOutput) editor.getItem().getData();
				rowTst.fieldName = text.getText();
				editor.getItem().setText(EDITABLECOLUMN, rowTst.fieldName);
			}
		});
		newEditor.selectAll();
		newEditor.setFocus();
		editor.setEditor(newEditor, item, EDITABLECOLUMN);
		dialog.setChanged();
	}

	private Composite createGroupsTable(Composite parent, Control last) {

		HashMap<Integer, RowOutput> availableOutputs = MDPersonatorMeta.getOutputgroups();
		Composite                   groupsComp       = new Composite(parent, SWT.NONE);
		helper.setLook(groupsComp);

		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		groupsComp.setLayout(fl);

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin);
		fd.right = new FormAttachment(40, -helper.margin);
		fd.bottom = new FormAttachment(55, -helper.margin);
		groupsComp.setLayoutData(fd);

		groupTableTitle = helper.addLabel(groupsComp, null, "OutputTab.AvailableOutput");

		groupTable = new Table(groupsComp, SWT.BORDER | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);
		groupTable.setHeaderVisible(true);
		groupTable.setLinesVisible(true);

		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(groupTableTitle, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		fd.bottom = new FormAttachment(90, -helper.margin);
		groupTable.setLayoutData(fd);

		// add the columns
		for (int i = 0; i < groupTableTitles.length; i++) {
			TableColumn column = new TableColumn(groupTable, SWT.NONE);
			column.setText(groupTableTitles[i]);
			column.setMoveable(false);
			column.setResizable(false);
		}

		// here we try to get an appropriate size for columns
		int sW   = dialog.getShell().getBounds().width;
		int comW = (int) (sW * .43);

		for (int i = 0; i < groupTableTitles.length; i++) {
			groupTable.getColumn(i).pack();
			groupTable.getColumn(i).setWidth(comW / 2);
		}

		RowOutput rowHolder;
		for (int i = 0; i < availableOutputs.size(); i++) {
			rowHolder = availableOutputs.get(i);
			if (!rowHolder.isAdded) {
				final TableItem item = new TableItem(groupTable, SWT.NONE);
				item.setText(new String[] { rowHolder.groupName, rowHolder.outputName });
				item.setData(rowHolder);
			}
		}

		groupTable.pack();

		// listener to change the details/description label.
		groupTable.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {

				RowOutput ro = (RowOutput) groupTable.getItem(groupTable.getSelectionIndex()).getData();
				lGrpDetails.setText(ro.groupDescription);
				String desc = ro.outputDescription.replaceAll("\\\\n", "\n");

				lRowDetails.setText(desc);
			}
		});

		bAddGroup = helper.addPushButton(groupsComp, groupTable, "OutputTab.AddGroup", new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {

				widgetSelected(arg0);
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {

				addGroups(groupTable.getSelectionIndices());
				setOutputTable();
				setGroupsTable();
				dialog.setChanged();
			}
		});

		bAddColumn = helper.addPushButton(groupsComp, groupTable, "OutputTab.AddColumn", new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {

				widgetSelected(arg0);
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {

				addColumns(groupTable.getSelectionIndices());
				setOutputTable();
				setGroupsTable();
				dialog.setChanged();
			}
		});

		bAddAll = helper.addPushButton(groupsComp, groupTable, "OutputTab.AddAll", new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {

				widgetSelected(arg0);
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {

				addAll();
				setOutputTable();
				setGroupsTable();
				dialog.setChanged();
			}
		});

		((FormData) bAddAll.getLayoutData()).left = new FormAttachment(bAddGroup, helper.margin);
		((FormData) bAddAll.getLayoutData()).right = new FormAttachment(50, 0);
		((FormData) bAddGroup.getLayoutData()).right = new FormAttachment(25, 0);
		((FormData) bAddColumn.getLayoutData()).left = new FormAttachment(75, 0);
		((FormData) bAddColumn.getLayoutData()).right = new FormAttachment(100, 0);

		return groupsComp;
	}

	private void sortTable(Table table, boolean outTable) {

		RowOutput   rowHolder;
		TableItem[] items    = table.getItems();
		Collator    collator = Collator.getInstance(Locale.getDefault());
		for (int i = 1; i < items.length; i++) {
			String value1 = items[i].getText(0);
			rowHolder = (RowOutput) items[i].getData();
			for (int j = 0; j < i; j++) {
				String value2 = items[j].getText(0);
				if (collator.compare(value1, value2) < 0) {
					String[] values = null;
					if (outTable) {
						String[] Ovalues = { items[i].getText(0), items[i].getText(1), items[i].getText(2) };
						values = Ovalues;
					} else {
						String[] Avalues = { items[i].getText(0), items[i].getText(1) };
						values = Avalues;
					}
					items[i].dispose();
					TableItem item = new TableItem(table, SWT.NONE, j);
					item.setText(values);
					item.setData(rowHolder);
					items = table.getItems();
					break;
				}
			}
		}
	}

	private void addAll() {

		HashMap<Integer, RowOutput> availableOutputs = MDPersonatorMeta.getOutputgroups();
		RowOutput                   rowHolder;
		for (int i = 0; i < availableOutputs.size(); i++) {
			rowHolder = availableOutputs.get(i);
			rowHolder.isAdded = true;
		}
	}

	private void deleteAll() {

		HashMap<Integer, RowOutput> availableOutputs = MDPersonatorMeta.getOutputgroups();
		RowOutput                   rowHolder;
		for (int i = 0; i < availableOutputs.size(); i++) {
			rowHolder = availableOutputs.get(i);
			rowHolder.isAdded = false;
			// reset default field name
			rowHolder.fieldName = "MD_" + rowHolder.outputName;
		}
	}

	private void addGroups(int[] selections) {

		for (int i = 0; i < selections.length; i++) {
//			grpPos = selections[i];
			addGroupOutput(groupTable.getItem(selections[i]));
		}
	}

	private void addGroupOutput(TableItem selection) {

		RowOutput selectedRow;
		RowOutput row;
		TableItem item;

		selectedRow = (RowOutput) selection.getData();

		// get all output rows with same group.
		for (int i = 0; i < groupTable.getItemCount(); i++) {
			item = groupTable.getItem(i);
			row = (RowOutput) item.getData();

			if (row.groupName.equals(selectedRow.groupName) && !row.groupName.equals("---")) {
				// addColumnOutput will decide if it needs to add.
				addColumnOutput(item);
			}
		}
	}

	private void deleteRow() {

		int[]     selections = outTable.getSelectionIndices();
		RowOutput currentRow;

		for (int i = 0; i < selections.length; i++) {
			currentRow = (RowOutput) outTable.getItem(selections[i]).getData();
			currentRow.isAdded = false;
			//	if("Geocode".equals(currentRow.groupName)){
			//		doReresh = true;
			//		isGeoCoding = false;
			//	}
			// reset default field name
			currentRow.fieldName = "MD_" + currentRow.outputName;
		}
	}

	private void delColumnOutput(TableItem selection) {

		RowOutput rowToAdd = (RowOutput) selection.getData();

		if (rowToAdd.isAdded) {
			rowToAdd.isAdded = false;
			rowToAdd.fieldName = "MD_" + rowToAdd.outputName;
		}
	}

	private void deleteGroupRow(TableItem selection) {

		RowOutput selectedRow;
		RowOutput row;
		TableItem item;

		selectedRow = (RowOutput) selection.getData();

		// get all output rows with same group.
		for (int i = 0; i < outTable.getItemCount(); i++) {
			item = outTable.getItem(i);
			row = (RowOutput) item.getData();

			if (row.groupName.equals(selectedRow.groupName) && !row.groupName.equals("---")) {
				// 
				delColumnOutput(item);
			}
		}
	}

	private void deleteGroups(int[] selections) {

		for (int i = 0; i < selections.length; i++) {
			deleteGroupRow(outTable.getItem(selections[i]));
		}
	}

	private void addColumns(int[] selections) {

		for (int i = 0; i < selections.length; i++) {
			addColumnOutput(groupTable.getItem(selections[i]));
		}
	}

	private void addColumnOutput(TableItem selection) {

		RowOutput rowToAdd = (RowOutput) selection.getData();

		if (!rowToAdd.isAdded) {
			rowToAdd.isAdded = true;
		}
	}

	private Group createInfo(Composite parent, Control last) {

		Group iGroup = new Group(parent, SWT.NONE);
		iGroup.setText("Details");
		helper.setLook(iGroup);

		FormLayout gl = new FormLayout();
		gl.marginWidth = 3;
		gl.marginHeight = 3;

		iGroup.setLayout(gl);

		FormData fd = new FormData();
		fd.left = new FormAttachment(40, helper.margin);
		fd.top = new FormAttachment(last, 5 * helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		fd.bottom = new FormAttachment(49, -helper.margin);
		iGroup.setLayoutData(fd);

		lGrpDetails = new Label(iGroup, SWT.NONE | SWT.TOP | SWT.LEFT | SWT.WRAP);

		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(0, 2 * helper.margin);
		fd.right = new FormAttachment(80, -helper.margin);
		lGrpDetails.setLayoutData(fd);
		helper.setLook(lGrpDetails);

		lRowDetails = new Label(iGroup, SWT.NONE | SWT.LEFT | SWT.WRAP);

		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(lGrpDetails, 2 * helper.margin);
		fd.right = new FormAttachment(80, -helper.margin);
		fd.bottom = new FormAttachment(80, -helper.margin);
		lRowDetails.setLayoutData(fd);
		helper.setLook(lRowDetails);

		return iGroup;
	}

	private Composite createOutputTable(Composite parent, Control last) {

		Composite groupsComp = new Composite(parent, SWT.NONE);

		helper.setLook(groupsComp);

		FormLayout gl = new FormLayout();
		gl.marginWidth = 3;
		gl.marginHeight = 3;

		groupsComp.setLayout(gl);

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin);
		fd.right = new FormAttachment(60, -helper.margin);
		fd.bottom = new FormAttachment(100, -helper.margin);
		groupsComp.setLayoutData(fd);

		outTableTitle = helper.addLabel(groupsComp, null, "OutputTab.SelectedOutput");

		outTable = new Table(groupsComp, SWT.BORDER | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);
		outTable.setHeaderVisible(true);
		outTable.setLinesVisible(true);

		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(outTableTitle, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		fd.bottom = new FormAttachment(90, -helper.margin);

		outTable.setLayoutData(fd);

		for (int i = 0; i < groupOutTitles.length; i++) {
			TableColumn column = new TableColumn(outTable, SWT.NONE);
			column.setText(groupOutTitles[i]);
			column.setMoveable(false);
			column.setResizable(false);
		}

		int sW   = dialog.getShell().getBounds().width;
		int comW = (int) (sW * .65);

		for (int i = 0; i < groupOutTitles.length; i++) {
			outTable.getColumn(i).pack();
			outTable.getColumn(i).setWidth(comW / 3);
		}

		bDeleteRow = helper.addPushButton(groupsComp, outTable, "OutputTab.DeleteRow", new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {

				widgetSelected(arg0);
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {

				deleteRow();
				setOutputTable();
				setGroupsTable();
				dialog.setChanged();
			}
		});

		((FormData) bDeleteRow.getLayoutData()).left = new FormAttachment(75, 0);
		((FormData) bDeleteRow.getLayoutData()).right = new FormAttachment(100, 0);

		bDeleteGroup = helper.addPushButton(groupsComp, outTable, "OutputTab.DeleteGroup", new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {

				widgetSelected(arg0);
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {

				deleteGroups(outTable.getSelectionIndices());
				setOutputTable();
				setGroupsTable();
				dialog.setChanged();
			}
		});

		bDeleteAll = helper.addPushButton(groupsComp, outTable, "OutputTab.DeleteAll", new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {

				widgetSelected(arg0);
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {

				deleteAll();
				setOutputTable();
				setGroupsTable();
				dialog.setChanged();
			}
		});

		((FormData) bDeleteGroup.getLayoutData()).right = new FormAttachment(25, 0);
		((FormData) bDeleteAll.getLayoutData()).left = new FormAttachment(bDeleteGroup, helper.margin);
		((FormData) bDeleteAll.getLayoutData()).right = new FormAttachment(50, 0);

		outTable.pack();
		return groupsComp;
	}

	/**
	 * Loads the meta data into the dialog tab
	 *
	 * @param metaPersonator
	 * @return
	 */
	public boolean init(MDPersonatorMeta metaPersonator) {

		//	isGeoCoding = Boolean.valueOf(metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_ADDR_GEOCODE).metaValue);

		sortTable(groupTable, false);
		setOutputTable();
		// Set initial enable
		enable();

		return true;
	}

	private void setGroupsTable() {

		HashMap<Integer, RowOutput> availableOutputs = MDPersonatorMeta.getOutputgroups();
		RowOutput                   rowHolder;

		if (editor.getEditor() != null) {
			editor.getEditor().dispose();
		}

		groupTable.removeAll();
		for (int i = 0; i < availableOutputs.size(); i++) {
			rowHolder = availableOutputs.get(i);

			if (!rowHolder.isAdded) {
				final TableItem item = new TableItem(groupTable, SWT.NONE);
				item.setText(new String[] { rowHolder.groupName, rowHolder.outputName, rowHolder.fieldName });
				item.setData(rowHolder);
			}
		}

		sortTable(groupTable, false);
	}

	private void setOutputTable() {

		HashMap<Integer, RowOutput> availableOutputs = MDPersonatorMeta.getOutputgroups();
		RowOutput                   rowHolder;

		outTable.removeSelectionListener(lsTableEdit);

		if (editor.getEditor() != null) {
			editor.getEditor().dispose();
		}

		outTable.removeAll();
		for (int i = 0; i < availableOutputs.size(); i++) {
			rowHolder = availableOutputs.get(i);

			if (rowHolder.isAdded) {
				final TableItem item = new TableItem(outTable, SWT.NONE);
				item.setText(new String[] { rowHolder.groupName, rowHolder.outputName, rowHolder.fieldName });
				item.setData(rowHolder);
			}
		}

		outTable.addSelectionListener(lsTableEdit);

		sortTable(outTable, true);
	}

	/**
	 * Loads the dialog data into the meta structure
	 */
	public void getData(MDPersonatorMeta metaPersonator) {

		RowOutput   rowHolder;
		MetaVal     mvResults;
		TableItem[] tableItems = outTable.getItems();

		mvResults = (MetaVal) metaPersonator.personatorFields.outputFields.get(PersonatorFields.TAG_OUTPUT_RESULTS);

		metaPersonator.personatorFields.outputFields.clear();
		for (int i = 0; i < tableItems.length; i++) {

			rowHolder = (RowOutput) tableItems[i].getData();
			rowHolder.fieldName = tableItems[i].getText(EDITABLECOLUMN);
			metaPersonator.personatorFields.outputFields.put(rowHolder.tag, new MetaVal(rowHolder.fieldName, rowHolder.outputName, 50));
		}

		// put back results
		metaPersonator.personatorFields.outputFields.put(PersonatorFields.TAG_OUTPUT_RESULTS, mvResults);
	}

	/**
	 * Called to handle enable of controls based on output settings
	 */
	public void enable() {

		for (Control child : outputGroup.getChildren())
			child.setEnabled(true);
	}

	/**
	 * @param key
	 * @param args
	 * @return
	 */
	private String getString(String key, String... args) {

		return BaseMessages.getString(PKG, "MDPersonatorDialog.OutputTab." + key, args);
	}

	/**
	 * @return The URL for help information
	 */
	public String getHelpURLKey() {
//		if(!MDPersonatorMeta.isPlugin)
//			return "MDPersonatorDialog.Help.OutputTab";
//		else
		return BaseMessages.getString(PKG, "MDPersonatorDialog.Plugin.Help.OutputTab");
	}

	@Override
	public void advancedConfigChanged() {
		//not used		
	}
}
