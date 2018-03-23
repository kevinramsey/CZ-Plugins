package com.melissadata.kettle.globalverify.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.pentaho.di.core.Props;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.gui.GUIResource;

import com.melissadata.cz.SourceFieldInfo;
import com.melissadata.kettle.globalverify.MDGlobalDialog;
import com.melissadata.kettle.globalverify.MDGlobalMeta;
import com.melissadata.kettle.globalverify.data.PassThruFields;
import com.melissadata.kettle.globalverify.support.MDGlobalAddressHelper;
import com.melissadata.kettle.globalverify.support.MDGlobalAddressTransfer;
import com.melissadata.kettle.globalverify.support.MDTab;

public abstract class PassThruTab implements MDTab {
	private static Class<?>			PKG		= MDGlobalMeta.class;
	protected static final int[]	EMPTY	= new int[0];
	protected MDGlobalDialog		dialog;
	private MDGlobalAddressHelper	helper;
	private Label					lblPassThruHeader;
	private Label					lblFilterOutHeader;
	protected TableViewer			tvPassThru;
	protected TableViewer			tvFilterOut;
	private Button					btnGet;

	public PassThruTab(MDGlobalDialog dialog, TransMeta transMeta) {
		this.dialog = dialog;
		helper = dialog.getHelper();
		// Create the tab
		final CTabFolder wTabFolder = dialog.getTabFolder();
		final CTabItem wTab = new CTabItem(wTabFolder, SWT.NONE);
		wTab.setText(getTabTitle());
		wTab.setData(this);
		// Create a scrolling region within the tab
		ScrolledComposite wSComp = new ScrolledComposite(wTabFolder, SWT.V_SCROLL | SWT.H_SCROLL);
		wSComp.setLayout(new FillLayout());
		// Create the tab controls
		Composite wComp = createControls(wSComp);
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
		// Initialize the tab with the scrolled composite
		wTab.setControl(wSComp);
		// Add a selection listener to the tab folder so that the usage information for fields can be updated
		wTabFolder.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
				widgetSelected(event);
			}

			@Override
			public void widgetSelected(SelectionEvent event) {
				if (wTabFolder.getSelection() == wTab) {
					refresh();
				}
			}
		});
	}

	/**
	 * @return A unique identification title for this tab
	 */
	protected abstract String getTabTitle();

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
		// Description line
		Label description = new Label(wComp, SWT.LEFT | SWT.WRAP);
		description.setText(getDescription());
		helper.setLook(description);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(0, helper.margin);
		description.setLayoutData(fd);
		// Table descriptions
		lblPassThruHeader = new Label(wComp, SWT.LEFT | SWT.WRAP);
		lblPassThruHeader.setText(getString("PassThruDescription"));
		helper.setLook(lblPassThruHeader);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(description, helper.margin * 2);
		fd.right = new FormAttachment(50, -helper.margin / 2);
		lblPassThruHeader.setLayoutData(fd);
		lblFilterOutHeader = new Label(wComp, SWT.LEFT | SWT.WRAP);
		lblFilterOutHeader.setText(getString("FilterOutDescription"));
		helper.setLook(lblFilterOutHeader);
		fd = new FormData();
		fd.left = new FormAttachment(50, helper.margin / 2);
		fd.top = new FormAttachment(description, helper.margin * 2);
		fd.right = new FormAttachment(100, -helper.margin);
		lblFilterOutHeader.setLayoutData(fd);
		// Create the composites that will hold the table viewers
		Composite wPassThruComp = new Composite(wComp, SWT.BORDER);
		helper.setLook(wPassThruComp);
		Composite wFilterOutComp = new Composite(wComp, SWT.BORDER);
		helper.setLook(wFilterOutComp);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(lblPassThruHeader, helper.margin * 2);
		fd.right = new FormAttachment(50, -helper.margin / 2);
		wPassThruComp.setLayoutData(fd);
		fd = new FormData();
		fd.left = new FormAttachment(50, helper.margin / 2);
		fd.top = new FormAttachment(lblFilterOutHeader, helper.margin * 2);
		fd.right = new FormAttachment(100, -helper.margin);
		wFilterOutComp.setLayoutData(fd);
		// Create the tables that will hold the pass thru and filter out fields
		tvPassThru = createTable(wPassThruComp);
		tvFilterOut = createTable(wFilterOutComp);
		// Add drag-n-drop functionality
		addDND(tvPassThru, "passthru");
		addDND(tvFilterOut, "filterout");
		// set up the Get Fields button
		btnGet = new Button(wComp, SWT.PUSH);
		helper.setLook(btnGet);
		btnGet.setText(BaseMessages.getString(PKG, "System.Button.GetFields"));
		{
			// This code centers the button and moves it correctly when dialog is resized
			btnGet.pack(true);
			int width = btnGet.getBounds().width + 10;
			if ((width % 2) == 1)
				width++;
			FormData formData = new FormData();
			int leftOffset = -(width + helper.margin) / 2;
			formData.left = new FormAttachment(50, leftOffset);
			formData.right = new FormAttachment(btnGet, width + helper.margin);
			formData.bottom = new FormAttachment(100, -helper.margin);
			btnGet.setLayoutData(formData);
		}
		btnGet.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {
				getFields();
			}
		});
		// make sure tables reach to the button
		((FormData) wPassThruComp.getLayoutData()).bottom = new FormAttachment(btnGet, -helper.margin);
		((FormData) wFilterOutComp.getLayoutData()).bottom = new FormAttachment(btnGet, -helper.margin);
		return wComp;
	}

	/**
	 * @return A description of this tab's functionality
	 */
	protected abstract String getDescription();

	/**
	 * Called to create the table viewer for fields values
	 * 
	 * @param parent
	 * @return
	 */
	private TableViewer createTable(Composite parent) {
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

	/**
	 * Enables a drag-n-drop interface between the tables.
	 * 
	 * @param viewer
	 * @param id
	 */
	private void addDND(final TableViewer viewer, final String id) {
		// Enable moving from table
		final Table table = viewer.getTable();
		DragSource source = new DragSource(table, DND.DROP_MOVE);
		// We will be moving a text string
		final MDGlobalAddressTransfer myTransfer = MDGlobalAddressTransfer.getInstance();
		source.setTransfer(new Transfer[] { myTransfer });
		// Listen for drag source events
		source.addDragListener(new DragSourceListener() {
			@Override
			public void dragStart(DragSourceEvent event) {
				// System.out.println("dragStart(event=" + event + ")");
				// Drag if one or mroe rows are selected
				TableItem[] selection = table.getSelection();
				if (selection == null || selection.length == 0)
					event.doit = false;
				// System.out.println("dragStart.1: event.doit=" + event.doit);
			}

			@Override
			public void dragSetData(DragSourceEvent event) {
				// System.out.println("dragSetData(event=" + event + ")");
				if (myTransfer.isSupportedType(event.dataType)) {
					// System.out.println("dragSetData.1:");
					// Get the fields displayed by this viewer
					@SuppressWarnings("unchecked")
					List<SourceFieldInfo> fields = (List<SourceFieldInfo>) viewer.getInput();
					// Convert selected elements into a comma-separated list of field names
					MDGlobalAddressTransfer.Type datum = new MDGlobalAddressTransfer.Type();
					datum.setID(id);
					int[] indices = table.getSelectionIndices();
					for (int i = 0; i < indices.length; i++)
						datum.addField(fields.get(indices[i]).getName());
					event.data = datum;
					// System.out.println("dragSetData.2: event.data=" + event.data);
				}
			}

			@Override
			public void dragFinished(DragSourceEvent event) {
				// System.out.println("dragFinished(event=" + event + ")");
				if (event.detail == DND.DROP_MOVE) {
					// System.out.println("dragFinished.1:");
					// Get the fields displayed by this viewer
					@SuppressWarnings("unchecked")
					List<SourceFieldInfo> fields = (List<SourceFieldInfo>) viewer.getInput();
					// Delete selected fields
					int[] indecis = table.getSelectionIndices();
					for (int i = 0; i < indecis.length; i++)
						fields.remove(indecis[i] - i);
					// Refresh the viewer
					viewer.refresh();
					// Clear selection
					table.setSelection(EMPTY);
					// Indicate something has changed
					dialog.setChanged();
				}
			}
		});
		// Enable moving to table
		final DropTarget target = new DropTarget(table, DND.DROP_MOVE);
		// Receive data in text format
		target.setTransfer(new Transfer[] { myTransfer });
		// List for drag drop events
		target.addDropListener(new DropTargetListener() {
			@Override
			public void dragEnter(DropTargetEvent event) {
				// System.out.println("dragEnter(event=" + event + ")");
				// System.out.println("dragEnter.1: event.detail=" + event.detail);
				if (event.detail == DND.DROP_DEFAULT) {
					if ((event.operations & DND.DROP_MOVE) != 0)
						event.detail = DND.DROP_MOVE;
					else
						event.detail = DND.DROP_NONE;
				}
				// System.out.println("dragEnter.2: event.detail=" + event.detail);
				for (int i = 0; i < event.dataTypes.length; i++) {
					if (myTransfer.isSupportedType(event.dataTypes[i])) {
						// System.out.println("dragEnter.3:");
						// Don't drop on ourselves.
						MDGlobalAddressTransfer.Type datum = (MDGlobalAddressTransfer.Type) myTransfer.nativeToJava(event.currentDataType);
						if (datum != null) {
							if (!datum.getID().equals(id)) {
								event.currentDataType = event.dataTypes[i];
								// System.out.println("dragEnter.4: event.currentDataType=" + event.currentDataType);
							} else
								event.detail = DND.DROP_NONE;
						}
						if (event.detail != DND.DROP_MOVE)
							event.detail = DND.DROP_NONE;
						// System.out.println("dragEnter.5: event.detail=" + event.detail);
						break;
					}
				}
				// System.out.println("dragEnter.6:");
			}

			@Override
			public void dragOver(DropTargetEvent event) {
				// System.out.println("dragOver(event=" + event + ")");
				event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;
				// System.out.println("dragOver.1: event.feedback=" + event.feedback);
				if (myTransfer.isSupportedType(event.currentDataType)) {
					MDGlobalAddressTransfer.Type datum = (MDGlobalAddressTransfer.Type) myTransfer.nativeToJava(event.currentDataType);
					if (datum != null) {
						// System.out.println("dragOver.2: datum = " + datum);
						if (datum.getID().equals(id)) {
							event.feedback = DND.FEEDBACK_NONE;
							// System.out.println("dragOver.3: event.feedback=" + event.feedback);
						}
					}
				}
			}

			@Override
			public void dragOperationChanged(DropTargetEvent event) {
				if (event.detail == DND.DROP_DEFAULT) {
					if ((event.operations & DND.DROP_MOVE) != 0)
						event.detail = DND.DROP_MOVE;
					else
						event.detail = DND.DROP_NONE;
				}
				if (myTransfer.isSupportedType(event.currentDataType)) {
					if (event.detail != DND.DROP_MOVE)
						event.detail = DND.DROP_NONE;
				}
			}

			@Override
			public void dragLeave(DropTargetEvent event) {
			}

			@Override
			public void dropAccept(DropTargetEvent event) {
				// System.out.println("dragAccept(event=" + event + ")");
				if (myTransfer.isSupportedType(event.currentDataType)) {
					// System.out.println("dragAccept.1:");
					MDGlobalAddressTransfer.Type datum = (MDGlobalAddressTransfer.Type) myTransfer.nativeToJava(event.currentDataType);
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

			@Override
			public void drop(DropTargetEvent event) {
				// System.out.println("drop(event=" + event + ")");
				if (myTransfer.isSupportedType(event.currentDataType)) {
					// System.out.println("drop.1:");
					// Get the fields displayed by this viewer
					@SuppressWarnings("unchecked")
					List<SourceFieldInfo> fields = (List<SourceFieldInfo>) viewer.getInput();
					// Get the possible source fields
					SortedMap<String, SourceFieldInfo> sourceFields = getSourceFields();
					// Add dropped fields to end of table
					int added = 0;
					MDGlobalAddressTransfer.Type datum = (MDGlobalAddressTransfer.Type) event.data;
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
		});
	}

	protected void getFields() {
		// Get the currently displayed data
		@SuppressWarnings("unchecked")
		List<SourceFieldInfo> passThru = (List<SourceFieldInfo>) tvPassThru.getInput();
		@SuppressWarnings("unchecked")
		List<SourceFieldInfo> filterOut = (List<SourceFieldInfo>) tvFilterOut.getInput();
		// If there are any fields then confirm that they want to clear and add the new data
		if (passThru.size() != 0 || filterOut.size() != 0) {
			MessageDialog md = new MessageDialog(dialog.getShell(), getString("GetFields.Title"), //$NON-NLS-1$
					null, getString("GetFields.Message"), //$NON-NLS-1$  
					MessageDialog.WARNING, new String[] { getString("GetFields.AddNew"), //$NON-NLS-1$  
							getString("GetFields.ClearAndAdd"), //$NON-NLS-1$  
							getString("GetFields.Cancel"), }, 0); //$NON-NLS-1$
			Window.setDefaultImage(GUIResource.getInstance().getImageSpoon());
			// Cancel clicked?
			int button = md.open() & 0xFF;
			if (button == 2)
				return;
			// Clear before add?
			if (button == 1) {
				passThru.clear();
				filterOut.clear();
			}
		}
		// Add all new source fields to the pass thru table
		SortedMap<String, SourceFieldInfo> sourceFields = getSourceFields();
		for (SourceFieldInfo field : sourceFields.values()) {
			if (!passThru.contains(field) && !filterOut.contains(field))
				passThru.add(field);
		}
		dialog.logDebug("Get " + passThru.size() + " fields");
		// Refresh tables
		refresh();
		// Indicate something has changed
		dialog.setChanged();
	}

	/**
	 * @return the lookup or source fields
	 */
	protected abstract SortedMap<String, SourceFieldInfo> getSourceFields();

	/**
	 * @param comp
	 * @param viewer
	 * @param layout
	 */
	private void createColumns(Composite comp, TableViewer viewer, TableColumnLayout layout) {
		viewer.setContentProvider(new ArrayContentProvider());
		String[] titles = { getString("ColumnInfo.ColumnName"), getString("ColumnInfo.CurrentUsage") };
		int[] bounds = { 100, 100 };
		// First column is for the field name
		TableViewerColumn col = createTableViewerColumn(viewer, titles[0], bounds[0], 0, layout);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				SourceFieldInfo field = (SourceFieldInfo) element;
				return field.getName();
			}
		});
	}

	private TableViewerColumn createTableViewerColumn(TableViewer viewer, String title, int bound, int colNumber, TableColumnLayout layout) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		// column.setMoveable(true);
		if (colNumber == 0)
			layout.setColumnData(column, new ColumnWeightData(5, ColumnWeightData.MINIMUM_WIDTH, true));
		else
			layout.setColumnData(column, new ColumnWeightData(10, ColumnWeightData.MINIMUM_WIDTH, true));
		return viewerColumn;
	}

	/**
	 * Called to refresh the table controls
	 */
	private void refresh() {
		tvPassThru.refresh();
		tvFilterOut.refresh();
	}

	/**
	 * Loads the meta data into the dialog tab
	 * 
	 * @param meta
	 * @return
	 */
	@Override
	public boolean init(MDGlobalMeta meta) {
		// Build a list of the elements for the filter out and pass thru tables
		List<SourceFieldInfo> passThru = new ArrayList<SourceFieldInfo>();
		List<SourceFieldInfo> filterOut = new ArrayList<SourceFieldInfo>();
		List<String> passThruNames = meta.passThruFields.passFilterFields.get(PassThruFields.TAG_PASS_THROUGH);
		List<String> filterOutNames = meta.passThruFields.passFilterFields.get(PassThruFields.TAG_FILTER_OUT);
		SortedMap<String, SourceFieldInfo> sourceFields = getSourceFields();
		for (String field : sourceFields.keySet()) {
			if (passThruNames != null && passThruNames.contains(field))
				passThru.add(sourceFields.get(field));
			if (filterOutNames != null && filterOutNames.contains(field))
				filterOut.add(sourceFields.get(field));
		}
		tvPassThru.setInput(passThru);
		tvFilterOut.setInput(filterOut);
		// Handle control enablement
		enable();
		return false;
	}

	/**
	 * Handle enablement of controls
	 */
	protected void enable() {
		// if input is not defined then disable tables
		boolean defined = isInputDefined();
		lblPassThruHeader.setEnabled(defined);
		tvPassThru.getTable().setEnabled(defined);
		lblFilterOutHeader.setEnabled(defined);
		tvFilterOut.getTable().setEnabled(defined);
		btnGet.setEnabled(defined);
	}

	/**
	 * @return true if input source is defined
	 */
	protected boolean isInputDefined() {
		// base class is always defined
		return true;
	}

	/**
	 * Loads the dialog data into the meta structure
	 */
	@Override
	public void getData(MDGlobalMeta meta) {
		// Get pass thru fields
		List<String> passThruNames = meta.passThruFields.passFilterFields.get(PassThruFields.TAG_PASS_THROUGH);
		if (passThruNames == null) {
			passThruNames = new ArrayList<String>();
		}
		passThruNames.clear();
		@SuppressWarnings("unchecked")
		List<SourceFieldInfo> passThru = (List<SourceFieldInfo>) tvPassThru.getInput();
		for (SourceFieldInfo field : passThru)
			passThruNames.add(field.getName());
		meta.passThruFields.passFilterFields.put(PassThruFields.TAG_PASS_THROUGH, passThruNames);
		// Get filtered fields
		List<String> filterOutNames = meta.passThruFields.passFilterFields.get(1);
		if (filterOutNames == null) {
			filterOutNames = new ArrayList<String>();
		}
		filterOutNames.clear();
		@SuppressWarnings("unchecked")
		List<SourceFieldInfo> filterOut = (List<SourceFieldInfo>) tvFilterOut.getInput();
		for (SourceFieldInfo field : filterOut)
			filterOutNames.add(field.getName());
		meta.passThruFields.passFilterFields.put(PassThruFields.TAG_FILTER_OUT, filterOutNames);
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

	/**
	 * @param key
	 * @param args
	 * @return
	 */
	protected String getString(String key, String... args) {
		return BaseMessages.getString(PKG, "MDGlobalDialog.PassThruTab." + key, args);
	}

	/**
	 * @return The URL for help information
	 */
	@Override
	public String getHelpURLKey() {
		return BaseMessages.getString(PKG, "MDGlobalVerify.Help.PassThroughTab");
	}
}
