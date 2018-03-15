package com.melissadata.kettle.ui;

import com.melissadata.cz.SourceFieldInfo;
import com.melissadata.kettle.MDCheckDialog;
import com.melissadata.kettle.MDCheckMeta;
import com.melissadata.kettle.MDCheckStepData;
import com.melissadata.kettle.PassThruMeta;
import com.melissadata.kettle.support.MDCheckHelper;
import com.melissadata.kettle.support.MDCheckTransfer;
import com.melissadata.kettle.support.MDTab;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;
import org.pentaho.di.core.Props;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.ui.core.gui.GUIResource;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

public abstract class PassThruTab implements MDTab {
	protected static final int[]    EMPTY = new int[0];
	protected static       Class<?> PKG   = MDCheckMeta.class;
	protected MDCheckDialog         dialog;
	protected MDCheckHelper         helper;
	protected Composite             wControlsComp;
	protected List<SourceFieldInfo> passThru;
	protected List<SourceFieldInfo> filterOut;
	protected Service               compType;
	protected boolean               isMuSource;
	//	protected boolean               isSurvivorSource;
	protected Label                 lblPassThruHeader;
	protected Label                 lblFilterOutHeader;
	protected TableViewer           tvPassThru;
	protected TableViewer           tvFilterOut;
	protected Button                btnGet;
	private   ScrolledComposite     wScrollComp;
	private   MDCheckStepData       mdcStepData;

	public PassThruTab(MDCheckDialog dialog, TransMeta transMeta) {
		this.dialog = dialog;
		helper = dialog.getHelper();
		mdcStepData = dialog.getData();
		setService(dialog.getCheckTypes());
		// Create the tab
		final CTabFolder wTabFolder = dialog.getTabFolder();
		final CTabItem wTab = new CTabItem(wTabFolder, SWT.NONE);
		wTab.setText(getTabTitle());
		wTab.setData(this);
		isMuSource = (compType == Service.MatchUp) && getTabTitle().contains("Source");
		//isSurvivorSource = dialog.getTabData().getSourcePassThru().isSurvivorPass() && MDCheckMeta.isPentahoPlugin();
		// Create a scrolling region within the tab
		setScrollComp(new ScrolledComposite(wTabFolder, SWT.V_SCROLL | SWT.H_SCROLL));
		getScrollComp().setLayout(new FillLayout());
		// Create the tab controls
		createControls(getScrollComp());
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
		getScrollComp().setContent(wControlsComp);
		getScrollComp().setExpandHorizontal(true);
		getScrollComp().setExpandVertical(true);
		getScrollComp().setMinWidth(bounds.width);
		getScrollComp().setMinHeight(bounds.height);
		// Initialize the tab with the scrolled composite
		wTab.setControl(getScrollComp());
		// Add a selection listener to the tab folder so that the usage information for fields can be updated
		wTabFolder.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent event) {
				widgetSelected(event);
			}

			public void widgetSelected(SelectionEvent event) {
				if (wTabFolder.getSelection() == wTab) {
					refresh();
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.support.MDTab#advancedConfigChanged()
	 */
	public void advancedConfigChanged() {
		// Check for enablement change
		enable();
	}

	public void dispose() {
		// Nothing to do
	}

	/**
	 * Loads the dialog data into the meta structure
	 */
	public void getData(MDCheckStepData data) {
		// Get pass thru fields
		List<String> passThruNames = getPassThruMeta(data).getPassThru();
		passThruNames.clear();
		@SuppressWarnings("unchecked") List<SourceFieldInfo> passThru = (List<SourceFieldInfo>) tvPassThru.getInput();
		for (SourceFieldInfo field : passThru) {
			passThruNames.add(field.getName());
		}
		// Get filtered fields
		List<String> filterOutNames = getPassThruMeta(data).getFilterOut();
		filterOutNames.clear();
		@SuppressWarnings("unchecked") List<SourceFieldInfo> filterOut = (List<SourceFieldInfo>) tvFilterOut.getInput();
		for (SourceFieldInfo field : filterOut) {
			filterOutNames.add(field.getName());
		}
	}

	/**
	 * @return The URL for help information
	 */
	public String getHelpURLKey() {
		return "MDCheck.Help.PassThruTab." + compType.toString();
	}

	public ScrolledComposite getScrollComp() {
		return wScrollComp;
	}

	public void setScrollComp(ScrolledComposite wSComp) {
		wScrollComp = wSComp;
	}

	/**
	 * Loads the meta data into the dialog tab
	 *
	 * @param data
	 * @return
	 */
	public boolean init(MDCheckStepData data) {

		mdcStepData = data;
		// Build a list of the elements for the filter out and pass thru tables
		passThru = new ArrayList<SourceFieldInfo>();
		filterOut = new ArrayList<SourceFieldInfo>();
		List<String> passThruNames = getPassThruMeta(mdcStepData).getPassThru();
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
		tvPassThru.setInput(passThru);
		tvFilterOut.setInput(filterOut);
		// Handle control enablement
		enable();
		return false;
	}

	private TableViewerColumn createTableViewerColumn(TableViewer viewer, String title, int bound, int colNumber, TableColumnLayout layout) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
// column.setMoveable(true);
		if (colNumber == 0) {
			layout.setColumnData(column, new ColumnWeightData(5, ColumnWeightData.MINIMUM_WIDTH, true));
		} else {
			layout.setColumnData(column, new ColumnWeightData(10, ColumnWeightData.MINIMUM_WIDTH, true));
		}
		return viewerColumn;
	}

	private void getFields() {
		// Get the currently displayed data
		@SuppressWarnings("unchecked") List<SourceFieldInfo> passThru = (List<SourceFieldInfo>) tvPassThru.getInput();
		@SuppressWarnings("unchecked") List<SourceFieldInfo> filterOut = (List<SourceFieldInfo>) tvFilterOut.getInput();
		// If there are any fields then confirm that they want to clear and add the new data
		if ((passThru.size() != 0) || (filterOut.size() != 0)) {
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

	private void setService(int n) {
		if ((n & MDCheckMeta.MDCHECK_FULL) != 0) {
			compType = Service.ContactVerify;
		} else if ((n & MDCheckMeta.MDCHECK_SMARTMOVER) != 0) {
			compType = Service.SmartMover;
		} else if ((n & MDCheckMeta.MDCHECK_MATCHUP) != 0) {
			compType = Service.MatchUp;
		} else if ((n & MDCheckMeta.MDCHECK_IPLOCATOR) != 0) {
			compType = Service.IpLocator;
		}
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
// System.out.println("dragSetData.2: event.data=" + event.data);
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
		// Receive data in text format
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
		// Second column is for the usage (don't display for matchup)
		if (compType != Service.MatchUp) {
			col = createTableViewerColumn(viewer, titles[1], bounds[1], 1, layout);
			col.setLabelProvider(new ColumnLabelProvider() {
				@Override public String getText(Object element) {
					SourceFieldInfo field = (SourceFieldInfo) element;
					return field.getUsage();
				}
			});
		} else {
		}
	}

	/**
	 * Called to create the tab controls
	 *
	 * @param parent
	 * @return
	 */
	protected Composite createControls(Composite parent) {
		// Create the composite that will hold the contents of the tab
		// Composite wComp = new Composite(parent, SWT.NONE);
		wControlsComp = new Composite(parent, SWT.NONE);
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
		Control last = description;
		int labelAlign = 50;
		// Table descriptions
		lblPassThruHeader = new Label(wControlsComp, SWT.LEFT | SWT.WRAP);
		lblPassThruHeader.setText(getString("PassThruDescription"));
		helper.setLook(lblPassThruHeader);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin * 2);
		// fd.right = new FormAttachment(50, -helper.margin / 2);
		fd.right = new FormAttachment(labelAlign, -helper.margin / 2);
		lblPassThruHeader.setLayoutData(fd);
		lblFilterOutHeader = new Label(wControlsComp, SWT.LEFT | SWT.WRAP);
		lblFilterOutHeader.setText(getString("FilterOutDescription"));
		helper.setLook(lblFilterOutHeader);
		fd = new FormData();
		// fd.left = new FormAttachment(50, helper.margin / 2);
		fd.left = new FormAttachment(labelAlign, helper.margin / 2);
		fd.top = new FormAttachment(last, helper.margin * 2);
		fd.right = new FormAttachment(100, -helper.margin);
		lblFilterOutHeader.setLayoutData(fd);
		// Create the composites that will hold the table viewers
		Composite wPassThruComp = new Composite(wControlsComp, SWT.BORDER);
		helper.setLook(wPassThruComp);
		Composite wFilterOutComp = new Composite(wControlsComp, SWT.BORDER);
		helper.setLook(wFilterOutComp);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(lblPassThruHeader, helper.margin * 2);
		fd.right = new FormAttachment(labelAlign, -helper.margin / 2);
		wPassThruComp.setLayoutData(fd);
		fd = new FormData();
		fd.left = new FormAttachment(labelAlign, helper.margin / 2);
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
		btnGet = new Button(wControlsComp, SWT.PUSH);
		helper.setLook(btnGet);
		btnGet.setText(BaseMessages.getString(PKG, "System.Button.GetFields"));
		{
			// This code centers the button and moves it correctly when dialog is resized
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
		btnGet.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				getFields();
			}
		});
		// make sure tables reach to the button
		((FormData) wPassThruComp.getLayoutData()).bottom = new FormAttachment(btnGet, -helper.margin);
		((FormData) wFilterOutComp.getLayoutData()).bottom = new FormAttachment(btnGet, -helper.margin);
		return wControlsComp;
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

	/**
	 * Handle enablement of controls
	 */
	protected void enable() {
		// if input is not defined then disable tables
		boolean defined = isInputDefined();
		lblPassThruHeader.setEnabled(defined);
		tvPassThru.getTable().setEnabled(defined);
		if (!(mdcStepData.getSourcePassThru().isSurvivorPass() && MDCheckMeta.isPentahoPlugin())) {
			lblFilterOutHeader.setEnabled(defined);
			tvFilterOut.getTable().setEnabled(defined);
		}
		btnGet.setEnabled(defined);
	}

	/**
	 * @return A description of this tab's functionality
	 */
	protected abstract String getDescription();

	/**
	 * Called to retrieve the pass thru meta structure
	 *
	 * @param data
	 * @return
	 */
	protected abstract PassThruMeta getPassThruMeta(MDCheckStepData data);

	/**
	 * @return the lookup or source fields
	 */
	protected abstract SortedMap<String, SourceFieldInfo> getSourceFields();

	/**
	 * @param key
	 * @param args
	 * @return
	 */
	protected String getString(String key, String... args) {
		return BaseMessages.getString(PKG, "MDCheckDialog.PassThruTab." + key, args);
	}

	/**
	 * @return A unique identification title for this tab
	 */
	protected abstract String getTabTitle();

	/**
	 * @return true if input source is defined
	 */
	protected boolean isInputDefined() {
		// base class is always defined
		return true;
	}

	/**
	 * Called to refresh the table controls
	 */
	protected void refresh() {
		tvPassThru.refresh();
		// if(!isSurvivorSource)
		tvFilterOut.refresh();
	}

	protected enum Service {
		ContactVerify, SmartMover, MatchUp, IpLocator;

		@Override public String toString() {
			// only capitalize the first letter
			String s = super.toString();
			return s.substring(0, 1) + s.substring(1).toLowerCase();
		}
	}
}
