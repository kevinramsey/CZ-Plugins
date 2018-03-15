package com.melissadata.kettle.mu.ui;

import com.melissadata.cz.DQTObjectException;
import com.melissadata.cz.SourceFieldInfo;
import com.melissadata.kettle.*;
import com.melissadata.kettle.mu.MappingRegularExpressions;
import com.melissadata.kettle.mu.MatchUpMeta;
import com.melissadata.kettle.mu.MatchUpMeta.MapField;
import com.melissadata.kettle.support.MDCheckHelper;
import com.melissadata.kettle.support.MDTab;
import com.melissadata.mdMUMatchcode;
import com.melissadata.kettle.mu.MatchUpUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Props;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.errorhandling.StreamInterface;

import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

public class MatchUpFieldMappingTab implements MDTab {
	private static final char[]   REG_EX_CHARS = new char[] { '\\', '[', '.', '*', '+', '?', '|', '{' };
	private static       Class<?> PKG          =  MatchUpMeta.class;
	private MDCheckDialog             dialog;
	private MDCheckHelper             helper;
	private MatchUpMeta               muMeta;
	private MappingRegularExpressions mapRegExp;
	private mdMUMatchcode             dispMatchcode;
	private MatchUpMatchcodeTab       matchcodeTab;
	private MatchUpOptionsTab         optionsTab;
	private LookupPassThruTab         lookupPassThruTab;
	/* tab controls */
	private TableViewer               tvSourceMap;
	private ViewerColumn[]            sourceColumns;
	private Group                     wLookupGroup;
	private Label                     lblLookupDescription;
	private CCombo                    cbLookupName;
	private TableViewer               tvLookupMap;
	private ViewerColumn[]            lookupColumns;
	private String                    lastMatchcode;
	private String lastLookupName = "";

	public MatchUpFieldMappingTab(MDCheckDialog dialog) {
		this.dialog = dialog;
		helper = dialog.getHelper();
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

	/**
	 * @param key
	 * @param args
	 * @return
	 */
	private static String getString(String key, String... args) {
		return BaseMessages.getString(PKG, "MDCheckDialog.MatchUpFieldMappingTab." + key, args);
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.support.MDTab#advancedConfigChanged()
	 */
	public void advancedConfigChanged() {
		// Re-initialize the mappings
		if (MatchUpMeta.initOK) {
			updateMappings(lastMatchcode, matchcodeTab.getMatchcodeName());
		}
		// Update enablement
		enable();
	}

	/**
	 * Called to clean up as dialog is closing
	 */
	public void dispose() {
		// Dispose of the matchup object
		if (dispMatchcode != null) {
			dispMatchcode.delete();
		}
	}

	/**
	 * Loads the dialog data into the meta structure
	 */
	public void getData(MDCheckStepData data) {
		// Get the meta data
		//muMeta = ((MDCheckMeta) meta).getTabData().getMatchUp();
		// Get clean mapping changes
		MatchUpMeta matchUpMeta = data.getMatchUp();
		List<MapField> newSourceMapping = new ArrayList<MapField>();
		List<MapField> newLookupMapping = new ArrayList<MapField>();
		getMappings(tvSourceMap, newSourceMapping);
		getMappings(tvLookupMap, newLookupMapping);
		// Update source mappings
		matchUpMeta.setSourceMapping(newSourceMapping);
		// Update the lookup selection
		StepMeta lookupStep = dialog.findStep(getLookupStepName());
		matchUpMeta.setLookupStep(lookupStep);
		// Update the lookup mappings
		matchUpMeta.setLookupMapping(newLookupMapping);
	}

	/**
	 * @return The URL for help information
	 */
	public String getHelpURLKey() {
		return "MDCheck.Help.MatchUpFieldMappingTab";
	}

	/**
	 * @return The currently selected lookup step name
	 */
	public String getLookupStepName() {
		int i = cbLookupName.getSelectionIndex();
		String lookupStep = (i > 0) ? cbLookupName.getItem(i) : "";
		return lookupStep;
	}

	/**
	 * Loads the meta data into the dialog tab
	 *
	 * @param data
	 */
	public boolean init(MDCheckStepData data) {
		boolean changed = false;
		if (MatchUpMeta.initOK) {
			// Load the mapping regular expressions
			mapRegExp = new MappingRegularExpressions();
			// The first info stream is the input stream
			muMeta = data.getMatchUp();
			StreamInterface lookupStream = muMeta.getLookupStream();
			String lookupName = (lookupStream != null) ? Const.NVL(lookupStream.getStepname(), "") : "";
			changed = initLookupSelector(lookupName) || changed;
			// Setup the source and lookup mappings with their initial values
			setupMappings(true);
			setupMappings(false);
			// Save a copy of the current mappings for comparisons after we update the mappings
			@SuppressWarnings("unchecked") List<MappingInfo> oldSourceMappings = new ArrayList<MappingInfo>((List<MappingInfo>) tvSourceMap.getInput());
			@SuppressWarnings("unchecked") List<MappingInfo> oldLookupMappings = new ArrayList<MappingInfo>((List<MappingInfo>) tvLookupMap.getInput());
			// Update the mappings using the initial match code
			updateMappings(matchcodeTab.getMatchcodeName(), matchcodeTab.getMatchcodeName());
			// De-reference the new mappings for comparisons with the old
			// mappings
			@SuppressWarnings("unchecked") List<MappingInfo> newSourceMappings = (List<MappingInfo>) tvSourceMap.getInput();
			@SuppressWarnings("unchecked") List<MappingInfo> newLookupMappings = (List<MappingInfo>) tvLookupMap.getInput();
			// Mappings could have changed since the last time this step was
			// edited. This code will detect this.
			Set<MappingInfo> setDiff = new HashSet<MappingInfo>(oldSourceMappings);
			setDiff.removeAll(newSourceMappings);
			if (setDiff.size() > 0) {
				// There were some in the old source that are not in the new
				// source
				changed = true;
			} else {
				setDiff = new HashSet<MappingInfo>(newSourceMappings);
				setDiff.removeAll(oldSourceMappings);
				if (setDiff.size() > 0) {
					// There are some in the new source that were not in the old
					// source
					changed = true;
				} else if (!Const.isEmpty(lookupName)) {
					setDiff = new HashSet<MappingInfo>(oldLookupMappings);
					setDiff.removeAll(newLookupMappings);
					if (setDiff.size() > 0) {
						// There were some in the old lookup that are not in the
						// new lookup
						changed = true;
					} else {
						setDiff = new HashSet<MappingInfo>(newLookupMappings);
						setDiff.removeAll(oldLookupMappings);
						if (setDiff.size() > 0) {
							// There are some in the new lookup that are no in
							// the old lookup
							changed = true;
						}
					}
				}
			}
		}
		// Handle initial enablement
		enable();
		return changed;
	}

	/**
	 * Called when there is a change to the matchcode managed by the matchcode tab
	 */
	public void matchcodeChanged() {
		// Throw out the matchcode object we are currently using
		if (dispMatchcode != null) {
			dispMatchcode.delete();
			dispMatchcode = null;
		}
		// Re-initialize the mappings
		updateMappings(lastMatchcode, matchcodeTab.getMatchcodeName());
		// Update enablement
		enable();
	}

	/**
	 * Called to connect this tab to the lookup pass thru tab
	 *
	 * @param lookupPassThruTab
	 */
	public void setLookupPassThruTab(LookupPassThruTab lookupPassThruTab) {
		this.lookupPassThruTab = lookupPassThruTab;
	}

	/**
	 * Called to connect this tab to the matchcode tab
	 *
	 * @param matchcodeTab
	 */
	public void setMatchcodeTab(MatchUpMatchcodeTab matchcodeTab) {
		this.matchcodeTab = matchcodeTab;
	}

	/**
	 * Called to connect this tab to the options tab
	 *
	 * @param optionsTab
	 */
	public void setOptionsTab(MatchUpOptionsTab optionsTab) {
		this.optionsTab = optionsTab;
	}

	/**
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
		// Create a composite that the two groups will be placed in
		Composite wMappingComp = new Composite(wComp, SWT.NONE);
		helper.setLook(wMappingComp);
		wMappingComp.setLayout(new FormLayout());
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(0, 0);
		fd.right = new FormAttachment(100, 0);
		fd.bottom = new FormAttachment(100, 0);
		wMappingComp.setLayoutData(fd);
		// Create source mapping controls
		createSourceMappingControls(wMappingComp);
		// Create lookup mapping controls
		createLookupMappingControls(wMappingComp);
		// Populate the tab controls
		populateControls();
		return wComp;
	}

	/**
	 * @param parent
	 */
	private void createLookupMappingControls(Composite parent) {
		// Create group
		wLookupGroup = new Group(parent, SWT.NONE);
		wLookupGroup.setText(getString("LookupGroup.Label"));
		helper.setLook(wLookupGroup);
		FormLayout fl = new FormLayout();
		fl.marginHeight = helper.margin;
		fl.marginWidth = helper.margin;
		wLookupGroup.setLayout(fl);
		FormData fd = new FormData();
		fd.left = new FormAttachment(50, helper.margin / 2);
		fd.top = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		fd.bottom = new FormAttachment(100, -helper.margin);
		wLookupGroup.setLayoutData(fd);
		// Create description
		lblLookupDescription = helper.addLabel(wLookupGroup, null, "MatchUpFieldMappingTab.LookupDescription");
		((FormData) lblLookupDescription.getLayoutData()).height = 4 * lblLookupDescription.getFont().getFontData()[0].getHeight();
		// Create lookup step name selector
		cbLookupName = (CCombo) helper.addComboBox(wLookupGroup, lblLookupDescription, "MatchUpFieldMappingTab.LookupStep")[1];
		cbLookupName.setEditable(false);
		cbLookupName.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {
				lookupChanged();
			}
		});
		// Create the mapping table
		lookupColumns = new ViewerColumn[3];
		tvLookupMap = createMappingTable(wLookupGroup, cbLookupName, lookupColumns);
	}

	/**
	 * Called to create one column of the mapping table
	 *
	 * @param viewer
	 * @param title
	 * @param weight
	 * @param layout
	 * @param labelProvider
	 * @return
	 */
	private TableViewerColumn createMappingColumn(TableViewer viewer, String title, int weight, TableColumnLayout layout, ColumnLabelProvider labelProvider) {
		final TableViewerColumn tvColumn = new TableViewerColumn(viewer, SWT.NONE);
		final TableColumn tblColumn = tvColumn.getColumn();
		tblColumn.setText(title);
		tblColumn.setResizable(true);
		layout.setColumnData(tblColumn, new ColumnWeightData(weight, ColumnWeightData.MINIMUM_WIDTH, true));
		tvColumn.setLabelProvider(labelProvider);
		return tvColumn;
	}

	/**
	 * Called to fill in the columns of the mapping table
	 *
	 * @param viewer
	 * @param layout
	 * @param mappingColumns
	 * @return
	 */
	private void createMappingColumns(TableViewer viewer, TableColumnLayout layout, final ViewerColumn[] mappingColumns) {
		// The content is an array of objects
		viewer.setContentProvider(new ArrayContentProvider());
		// Create the viewer columns
		// Match field label
		mappingColumns[0] = createMappingColumn(viewer, getString("MapInfo.Label"), 1, layout, new ColumnLabelProvider() {
			@Override public String getText(Object element) {
				try {
					MappingInfo info = (MappingInfo) element;
					return getMatchcodeObject().GetMappingItemLabel((info.pos + 1));
				} catch (DQTObjectException e) {
					// TODO Better error handling MappingInfo
					e.printStackTrace(System.err);
					return "!ERR!";
				}
			}
		});
		// Input source field
		mappingColumns[1] = createMappingColumn(viewer, getString("MapInfo.Input"), 1, layout, new ColumnLabelProvider() {
			@Override public String getText(Object element) {
				return ((MappingInfo) element).input;
			}
		});
		// Matchcode data type
		mappingColumns[2] = createMappingColumn(viewer, getString("MapInfo.DataType"), 1, layout, new ColumnLabelProvider() {
			@Override public String getText(Object element) {
				MappingInfo info = (MappingInfo) element;
				if (Const.isEmpty(info.input)) {
					return "";
				}
				return MatchUpUtil.getInputMappingLabel(info.type);
			}
		});
	}

	/**
	 * Called to create the table viewer for the mapping fields
	 *
	 * @param parent
	 * @param mappingColumns
	 * @return
	 */
	private TableViewer createMappingTable(Composite parent, Control wLastLine, ViewerColumn[] mappingColumns) {
		Composite wComp = new Composite(parent, SWT.BORDER);
		helper.setLook(wComp);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(wLastLine, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		fd.bottom = new FormAttachment(100, -helper.margin);
		wComp.setLayoutData(fd);
		// Give this composite a table column layout view so we can control the number
		// of columns displayed. Only a single table viewer can be held in each composite in order
		// for this to work
		TableColumnLayout tcl = new TableColumnLayout();
		wComp.setLayout(tcl);
		// Create table viewer
		TableViewer tv = new TableViewer(wComp, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		// Create columns in table viewer
		createMappingColumns(tv, tcl, mappingColumns);
		// Make sure the table is layed out correctly and looks cnsistent
		Table table = tv.getTable();
		helper.setLook(table, Props.WIDGET_STYLE_TABLE);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		// Customize editing features
		TableViewerFocusCellManager focusCellManager = new TableViewerFocusCellManager(tv, new FocusCellOwnerDrawHighlighter(tv));
		ColumnViewerEditorActivationStrategy editorActivationStrategy = new ColumnViewerEditorActivationStrategy(tv) {
			@Override protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
				// Activates cell editing when CR is pressed
				return super.isEditorActivationEvent(event) || ((event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED) && (event.keyCode == SWT.CR));
			}
		};
		TableViewerEditor.create(tv, focusCellManager, editorActivationStrategy, ColumnViewerEditor.KEYBOARD_ACTIVATION | ColumnViewerEditor.TABBING_VERTICAL);
		// Needed to get the table to refresh after a cell is edited.
		final TableViewer tvFinal = tv;
		tv.getColumnViewerEditor().addEditorActivationListener(new ColumnViewerEditorActivationListener() {
			@Override public void afterEditorActivated(ColumnViewerEditorActivationEvent event) {
			}

			@Override public void afterEditorDeactivated(ColumnViewerEditorDeactivationEvent event) {
				tvFinal.refresh();
			}

			@Override public void beforeEditorActivated(ColumnViewerEditorActivationEvent event) {
			}

			@Override public void beforeEditorDeactivated(ColumnViewerEditorDeactivationEvent event) {
			}
		});
		// NOTE: The cell editor will be created in the initMappings method
		return tv;
	}

	/**
	 * @param parent
	 */
	private void createSourceMappingControls(Composite parent) {
		// Create group
		Group wGroup = new Group(parent, SWT.NONE);
		wGroup.setText(getString("SourceGroup.Label"));
		helper.setLook(wGroup);
		FormLayout fl = new FormLayout();
		fl.marginHeight = helper.margin;
		fl.marginWidth = helper.margin;
		wGroup.setLayout(fl);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment(50, -helper.margin / 2);
		fd.bottom = new FormAttachment(100, -helper.margin);
		wGroup.setLayoutData(fd);
		// Create description
		Label lblDescription = helper.addLabel(wGroup, null, "MatchUpFieldMappingTab.SourceDescription");
		((FormData) lblDescription.getLayoutData()).height = 4 * lblDescription.getFont().getFontData()[0].getHeight();
		// Create fake combo box that is invisible. Needed to make the controls in the source group align with
		// the controls in the lookup group.
		CCombo cbFake = (CCombo) helper.addComboBox(wGroup, lblDescription, null)[1];
		cbFake.setVisible(false);
		// Create the mapping table
		sourceColumns = new ViewerColumn[3];
		tvSourceMap = createMappingTable(wGroup, cbFake, sourceColumns);
	}

	/**
	 * This method will try and resolve the actual mapping for a field
	 *
	 * @param mapping
	 * @param columns
	 * @param history
	 * @param Matchcode
	 * @param mcPos
	 */
	private void determineFieldMapping(MappingInfo mapping, SortedMap<String, SourceFieldInfo> columns, HistoricalMapping[] history, mdMUMatchcode Matchcode, int mcPos) {
		// TODO: The following may not be needed
// String[] sMappings = Matchcode.GetInputMappingEnum().split(",");
// int[] mappings = new int[sMappings.length];
// for (int i = 0; i < mappings.length; i++)
// mappings[i] = Integer.valueOf(sMappings[i]);
		Object[] swigValues = MatchUpUtil.getSwigValues(mdMUMatchcode.MatchcodeMapping.class);
		mdMUMatchcode.MatchcodeMappingTarget target = Matchcode.GetMappingItemType(mcPos);
//FIXME matchUp Global
		mdMUMatchcode.MatchcodeMappingTarget testTarg = null;
		int pos = 0;
//		System.out.println(" ***********************************************************");
//
//		do {
//			try {
//				testTarg = Matchcode.GetMappingItemType(pos);
//				System.out.println(" mapping - " + mapping.toString());
//				System.out.println(" Test Target : " + pos + " - " + testTarg.toString() + "    " + Matchcode.GetMappingItemLabel(pos) + "     " + MatchUpUtil.getBestInputMappingType(testTarg).toString());
//			} catch (IllegalArgumentException ia) {
//				System.out.println(" Test Target : is null");
//			}
//
//			pos++;
//
//
//		}while(pos < 10);
//
//		System.out.println(" ********--***---**---**---*---**---*--******************************");


		String label = Matchcode.GetMappingItemLabel(mcPos);
		// 1: Matchcode Component's label matches a field name:
		for (SourceFieldInfo column : columns.values()) {
			if (column.getName().equalsIgnoreCase(label)) {
				mapping.input = column.getName();
				mapping.type = MatchUpUtil.getBestInputMappingType(target);
				return;
			}
		}
		// 2: History's label matches this label:
		for (int i = 0; (history != null) && (i < history.length); i++) {
			if (!Const.isEmpty(history[i].label) && history[i].label.equalsIgnoreCase(label) && !((history[i].mapSource == mdMUMatchcode.MatchcodeMapping.General) && Const.isEmpty(history[i].field))) {
				mapping.input = history[i].field;
				mapping.type = history[i].mapSource;
				return;
			}
		}
		// 3: History's mapping has an identical target data type:
		for (int i = 0; (history != null) && (i < history.length); i++) {
			if ((history[i].mapTarget == target) && !((history[i].mapSource == mdMUMatchcode.MatchcodeMapping.General) && Const.isEmpty(history[i].field))) {
				mapping.input = history[i].field;
				mapping.type = history[i].mapSource;
				return;
			}
		}

		// NOTE : We do not map Address Lines 2-8 we leave that for the user if there is more than
		// one Address Line.
		if ((target == mdMUMatchcode.MatchcodeMappingTarget.Address2Type) || (target == mdMUMatchcode.MatchcodeMappingTarget.Address3Type) || (target == mdMUMatchcode.MatchcodeMappingTarget.Address4Type) || (target
				== mdMUMatchcode.MatchcodeMappingTarget.Address5Type) || (target == mdMUMatchcode.MatchcodeMappingTarget.Address6Type) || (target == mdMUMatchcode.MatchcodeMappingTarget.Address7Type) || (target
				== mdMUMatchcode.MatchcodeMappingTarget.Address8Type)) {
			return;
		}

		// 4: History has a direct-conversion input data type:
		for (int i = 0; (history != null) && (i < history.length); i++) {
			if (MatchUpUtil.IsDirectConversion(history[i].mapSource, target)) {
				mapping.input = history[i].field;
				mapping.type = history[i].mapSource;
				return;
			}
		}
		// 5: History has a convertable input data type:
		for (int i = 0; (history != null) && (i < history.length); i++) {
			if (MatchUpUtil.IsConvertable(history[i].mapSource, target)) {
				mapping.input = history[i].field;
				mapping.type = history[i].mapSource;
				return;
			}
		}
		// 6: Non-wildcarded fieldname found, direct conversion:
		for (SourceFieldInfo column : columns.values()) {
			for (Object swigValue : swigValues) {
				mdMUMatchcode.MatchcodeMapping eMapping = (mdMUMatchcode.MatchcodeMapping) swigValue;
				if (MatchUpUtil.IsDirectConversion(eMapping, target) && NonWildcardMatch(column.getName(), mapRegExp.get(eMapping))) {
					mapping.input = column.getName();
					mapping.type = eMapping;
					return;
				}
			}
		}
		// 7: Non-wildcarded fieldname found, indirect conversion:
		for (SourceFieldInfo column : columns.values()) {
			for (Object swigValue : swigValues) {
				mdMUMatchcode.MatchcodeMapping eMapping = (mdMUMatchcode.MatchcodeMapping) swigValue;
				if (MatchUpUtil.IsConvertable(eMapping, target) && NonWildcardMatch(column.getName(), mapRegExp.get(eMapping))) {
					mapping.input = column.getName();
					mapping.type = eMapping;
					return;
				}
			}
		}
		// 8: Wildcarded fieldname found, direct conversion:
		for (SourceFieldInfo column : columns.values()) {
			for (Object swigValue : swigValues) {
				mdMUMatchcode.MatchcodeMapping eMapping = (mdMUMatchcode.MatchcodeMapping) swigValue;
				if (MatchUpUtil.IsDirectConversion(eMapping, target) && WildcardMatch(column.getName(), mapRegExp.get(eMapping))) {
					mapping.input = column.getName();
					mapping.type = eMapping;
					return;
				}
			}
		}
		// 9: Non-wildcarded fieldname found, indirect conversion:
		for (SourceFieldInfo column : columns.values()) {
			for (Object swigValue : swigValues) {
				mdMUMatchcode.MatchcodeMapping eMapping = (mdMUMatchcode.MatchcodeMapping) swigValue;
				if (MatchUpUtil.IsConvertable(eMapping, target) && WildcardMatch(column.getName(), mapRegExp.get(eMapping))) {
					mapping.input = column.getName();
					mapping.type = eMapping;
					return;
				}
			}
		}
	}

	/**
	 * Called to enable components based on current state of input selection
	 */
	private void enable() {
		// Disable the entire lookup group if it has only one element (blank)
		boolean enabled = (cbLookupName.getItemCount() > 1);
		cbLookupName.setEnabled(enabled);
		tvLookupMap.getControl().setEnabled(enabled);
		// Set labels and descriptions appropriately
		if (enabled) {
			wLookupGroup.setText(getString("LookupGroup.Label"));
			lblLookupDescription.setText(getString("LookupDescription.Label"));
		} else {
			wLookupGroup.setText(getString("LookupGroupDisabled.Label"));
			lblLookupDescription.setText(getString("LookupDescriptionDisabled.Label"));
		}
		// Disable the lookup table if the lookup step isn't defined
		if (enabled) {
			enabled = !Const.isEmpty(getLookupStepName());
			tvLookupMap.getControl().setEnabled(enabled);
		}
	}

	/**
	 * Called to update the preserve mapping with (possibly changed) values from the mapping tables
	 *
	 * @param tvMap
	 * @param preservedMapping
	 */
	private void getMappings(TableViewer tvMap, List<MapField> preservedMapping) {
		// Get the table mapping info list
		@SuppressWarnings("unchecked") List<MappingInfo> mappingInfos = (List<MappingInfo>) tvMap.getInput();
		// For each entry, update the mapping
		for (MappingInfo mappingInfo : mappingInfos) {
			String input = Const.isEmpty(mappingInfo.input) ? "" : mappingInfo.input;
			MapField field = new MapField(input, mappingInfo.type);
			preservedMapping.add(field);
		}
	}

	/**
	 * Manages a single instance of the matchcode object
	 *
	 * @return
	 * @throws DQTObjectException
	 */
	private mdMUMatchcode getMatchcodeObject() throws DQTObjectException {
		if (dispMatchcode == null) {
			// Create the object with the current configuration
			String matchcodeName = matchcodeTab.getMatchcodeName();
			String dataPath = dialog.getAdvancedConfigMeta().getLocalDataPath();
			int rc[] = new int[1];
			dispMatchcode = MatchUpUtil.getMatchcodeObject(matchcodeName, dataPath, rc);
			if (rc[0] == 0) {
				// Issue a warning message
				MessageDialog.openWarning(dialog.getShell(), dialog.getTitle(), BaseMessages.getString(PKG, "MDCheckDialog.MatchcodeComponentProblem.Message", matchcodeName, dispMatchcode.GetInitializeErrorString()));
			}
		}
		return dispMatchcode;
	}

	/**
	 * Returns the index of the first character in an array that is found in the string
	 *
	 * @param s
	 * @param anyOf
	 * @return
	 */
	private int indexOfAny(String s, char[] anyOf) {
		for (char ch : REG_EX_CHARS) {
			int index;
			if ((index = s.indexOf(ch)) != -1) {
				return index;
			}
		}
		return -1;
	}

	/**
	 * Called to initialize the lookup combo selector
	 *
	 * @param newLookupName
	 */
	private boolean initLookupSelector(String newLookupName) {
		// Clear the selection
		cbLookupName.select(0); // First is always blank
		// Handle a blank lookup name
		if (Const.isEmpty(newLookupName)) {
			return false;
		}
		// Is that match code still defined?
		String[] names = cbLookupName.getItems();
		for (int i = 0; i < names.length; i++) {
			String name = names[i];
			if (name.equals(newLookupName)) {
				cbLookupName.select(i);
				lastLookupName = newLookupName;
				return false;
			}
		}
		// Warn the user if we couldn't set the control
		MessageDialog.openWarning(dialog.getShell(), dialog.getTitle(), BaseMessages.getString(PKG, "MDCheckDialog.LookupStepNotExist.Message", newLookupName));
		// Since lookup step could not be found, we need to save the currently selected blank
		return true;
	}

	/**
	 * Called to handle a change in the lookup input stream
	 */
	private void lookupChanged() {
		// If changing from the undefiend lookup step then we need to clear the lookup mappings
		// before calling the update code. Otherwise, the update will "remember" all the previously
		// set blank input fields instead of trying to intelligently fill them.
		if (Const.isEmpty(lastLookupName)) {
			@SuppressWarnings("unchecked") List<MappingInfo> lookupMappings = (List<MappingInfo>) tvLookupMap.getInput();
			lookupMappings.clear();
		}
		// Re-initialize the mappings
		updateMappings(lastMatchcode, matchcodeTab.getMatchcodeName());
		// Remember the new lookup step
		lastLookupName = getLookupStepName();
		// Update enablement
		enable();
		// Cascade to options and lookup passthru tab
		optionsTab.lookupChanged();
		lookupPassThruTab.lookupChanged();
	}

	/**
	 * Called to determine if a field matches any of the non-wildcarded expressions
	 *
	 * @param field
	 * @param regExps
	 * @return
	 */
	private boolean NonWildcardMatch(String field, String[] regExps) {
		for (String regExp : regExps) {
			String regex = regExp;
			if ((regex.length() > 0) && (indexOfAny(regex, REG_EX_CHARS) == -1)) {
				// Force regex to match the whole string
				regex = (regex.charAt(0) != '^' ? "^" + regex : regex);
				regex = (regex.charAt(regex.length() - 1) != '$' ? regex + "$" : regex);
				// See if the pattern matches
				boolean matches = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(field).matches();
				if (matches) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Called to populate the controls
	 */
	private void populateControls() {
		// Allow for no lookup step
		cbLookupName.add("");
		// Populate the lookup selector
		// If there is only one input step available then it must be used as the primary source.
		// Therefore, the lookup controls will be disabled when there is only one input step.
		List<StepMeta> previousSteps = dialog.findPreviousSteps();
		if (previousSteps.size() > 1) {
			for (StepMeta stepMeta : previousSteps) {
				cbLookupName.add(stepMeta.getName());
			}
		}
		// Populate the mapping tables
		tvSourceMap.setInput(new ArrayList<MappingInfo>());
		tvLookupMap.setInput(new ArrayList<MappingInfo>());
	}

	/**
	 * Called to setup the field mapping tables
	 *
	 * @param source
	 */
	private void setupMappings(boolean source) {
		// Determine the parameters for source or lookup mapping
		//MatchUpMeta matchUp = dialog.getTabData().getMatchUp();
		TableViewer tvMap;
		List<MapField> mappings;
		SortedSet<String> sourceFields;
		ViewerColumn[] columns;
		boolean enabled;
		if (source) {
			tvMap = tvSourceMap;
			mappings = new ArrayList<MapField>(muMeta.getSourceMapping());
			sourceFields = new TreeSet<String>(dialog.getSourceFields().keySet());
			columns = sourceColumns;
			enabled = true;
		} else {
			tvMap = tvLookupMap;
			mappings = new ArrayList<MapField>(muMeta.getLookupMapping());
			sourceFields = new TreeSet<String>(dialog.getLookupFields(getLookupStepName()).keySet());
			columns = lookupColumns;
			enabled = !Const.isEmpty(getLookupStepName());
		}
		// Clear the current mapping info list
		@SuppressWarnings("unchecked") List<MappingInfo> mappingInfos = (List<MappingInfo>) tvMap.getInput();
		mappingInfos.clear();
		// Only fill in the table if it is enabled
		if (!enabled) {
			// Disable editing on disabled table
			columns[1].setEditingSupport(null);
			columns[2].setEditingSupport(null);
			return;
		}
		// Only fill in the table if there is a matchcode defined
		String matchcodeName = matchcodeTab.getMatchcodeName();
		if (Const.isEmpty(matchcodeName)) {
			return;
		}
		for (int i = 0; i < mappings.size(); i++) {
			MapField mapping = mappings.get(i);
			// Make sure the source field is still there
			String sourceField = validateSourceField(mapping.input, sourceFields);
			MappingInfo info = new MappingInfo(i, sourceField, mapping.type);
			mappingInfos.add(info);
		}
		// Create editor for the input column
		columns[1].setEditingSupport(new InputEditingSupport(tvMap, sourceFields));
		// Create editor for the input data type column
		columns[2].setEditingSupport(new DataTypeEditingSupport(tvMap));
	}

	/**
	 * Called to initialize the mapping controls based on the matchcode name from the matchcode tab and
	 * the current source and lookup mappings.
	 *
	 * @param oldMatchcodeName
	 * @param newMatchcode
	 */
	private void updateMappings(String oldMatchcodeName, String newMatchcode) {
		// Setup the source and lookup mappings
		if (MatchUpMeta.initOK) {
			setupMappings(true);
			setupMappings(false);
			// De-reference the currently display source and lookup mappings
			@SuppressWarnings("unchecked") List<MappingInfo> sourceMappings = (List<MappingInfo>) tvSourceMap.getInput();
			@SuppressWarnings("unchecked") List<MappingInfo> lookupMappings = (List<MappingInfo>) tvLookupMap.getInput();
			// Get the path to the configuration
			String dataPath = dialog.getAdvancedConfigMeta().getLocalDataPath();
			// Load historical information about the current mapping
			HistoricalMapping[] oldSourceMappings = null;
			HistoricalMapping[] oldLookupMappings = null;
			if (!Const.isEmpty(oldMatchcodeName)) {
				mdMUMatchcode Matchcode = null;
				try {
					int[] rc = new int[1];
					Matchcode = MatchUpUtil.getMatchcodeObject(oldMatchcodeName, dataPath, rc);
					if (rc[0] != 0) {
						// Old source mappings
						int size = Math.min(sourceMappings.size(), Matchcode.GetMappingItemCount());
						oldSourceMappings = new HistoricalMapping[size];
						for (int i = 0; i < size; i++) {
							oldSourceMappings[i] = new HistoricalMapping();
							oldSourceMappings[i].label = Matchcode.GetMappingItemLabel(i + 1);
							oldSourceMappings[i].mapSource = sourceMappings.get(i).type;
							oldSourceMappings[i].mapTarget = Matchcode.GetMappingItemType(i + 1);
							oldSourceMappings[i].field = sourceMappings.get(i).input;
						}
						// Old lookup mappings
						size = Math.min(lookupMappings.size(), Matchcode.GetMappingItemCount());
						oldLookupMappings = new HistoricalMapping[size];
						for (int i = 0; i < size; i++) {
							oldLookupMappings[i] = new HistoricalMapping();
							oldLookupMappings[i].label = Matchcode.GetMappingItemLabel(i + 1);
							oldLookupMappings[i].mapSource = lookupMappings.get(i).type;
							oldLookupMappings[i].mapTarget = Matchcode.GetMappingItemType(i + 1);
							oldLookupMappings[i].field = lookupMappings.get(i).input;
						}
					}
				} catch (DQTObjectException e) {
					// TODO Better error handling get matchcode obj
					e.printStackTrace(System.err);
				} finally {
					if (Matchcode != null) {
						Matchcode.delete();
					}
				}
			}

			if (lookupMappings.isEmpty() && !Const.isEmpty(getLookupStepName())) {

				lookupMappings.clear();
				mdMUMatchcode Matchcode = null;
				try {
					int[] rc = new int[1];
					Matchcode = MatchUpUtil.getMatchcodeObject(newMatchcode, dataPath, rc);
					if (rc[0] != 0) {
						// Fill it with blank fields first
						for (int i = 0; i < Matchcode.GetMappingItemCount(); i++) {
							lookupMappings.add(new MappingInfo(i, "", mdMUMatchcode.MatchcodeMapping.General));
						}
						// Determine the field mappings for the lookup
						SortedMap<String, SourceFieldInfo> lookupFields = dialog.getLookupFields(getLookupStepName());
						for (int i = 0; i < Matchcode.GetMappingItemCount(); i++) {
							determineFieldMapping(lookupMappings.get(i), lookupFields, oldLookupMappings, Matchcode, i + 1);
						}
					}
				} catch (DQTObjectException e) {
					// TODO Better error handling matchcode obj
					e.printStackTrace(System.err);
				} finally {
					if (Matchcode != null) {
						Matchcode.delete();
					}
				}
			}


			// Create mappings based on the new matchcode
			if ((!Const.isEmpty(newMatchcode) && !oldMatchcodeName.equals(newMatchcode)) || sourceMappings.isEmpty()) {
				// Reset the currently defined mappings
				sourceMappings.clear();
				lookupMappings.clear();
				mdMUMatchcode Matchcode = null;
				try {
					int[] rc = new int[1];
					Matchcode = MatchUpUtil.getMatchcodeObject(newMatchcode, dataPath, rc);
					if (rc[0] != 0) {
						// Fill it with blank fields first
						for (int i = 0; i < Matchcode.GetMappingItemCount(); i++) {
							sourceMappings.add(new MappingInfo(i, "", mdMUMatchcode.MatchcodeMapping.General));
							lookupMappings.add(new MappingInfo(i, "", mdMUMatchcode.MatchcodeMapping.General));
						}
						// Determine the field mappings for the source
						SortedMap<String, SourceFieldInfo> sourceFields = dialog.getSourceFields();
						for (int i = 0; i < Matchcode.GetMappingItemCount(); i++) {
							determineFieldMapping(sourceMappings.get(i), sourceFields, oldSourceMappings, Matchcode, i + 1);
						}
						// Determine the field mappings for the lookup
						SortedMap<String, SourceFieldInfo> lookupFields = dialog.getLookupFields(getLookupStepName());
						for (int i = 0; i < Matchcode.GetMappingItemCount(); i++) {
							determineFieldMapping(lookupMappings.get(i), lookupFields, oldLookupMappings, Matchcode, i + 1);
						}
					}
				} catch (DQTObjectException e) {
					// TODO Better error handling matchcode obj
					e.printStackTrace(System.err);
				} finally {
					if (Matchcode != null) {
						Matchcode.delete();
					}
				}
			}
			// Remember the new matchcode
			lastMatchcode = newMatchcode;
			// Refresh the tables
			tvSourceMap.refresh();
			tvLookupMap.refresh();
		}
	}

	/**
	 * Called to make sure a source field still exists
	 *
	 * @param newInput
	 * @param sourceFields
	 * @return
	 */
	private String validateSourceField(String newInput, SortedSet<String> sourceFields) {
		// TODO MatchUp: display a warning dialog if it isn't defined?
		if ((newInput == null) || !sourceFields.contains(newInput)) {
			newInput = "";
		}
		return newInput;
	}

	/**
	 * Called to determine if a field matches any of the non-wildcarded expressions
	 *
	 * @param field
	 * @param regExps
	 * @return
	 */
	private boolean WildcardMatch(String field, String[] regExps) {
		for (String regExp : regExps) {
			String regex = regExp;
			if ((regex.length() > 0) && (indexOfAny(regex, REG_EX_CHARS) > 0)) {
				// Force regex to match the whole string
				regex = (regex.charAt(0) != '^' ? "^" + regex : regex);
				regex = (regex.charAt(regex.length() - 1) != '$' ? regex + "$" : regex);
				// See if the pattern matches
				boolean matches = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(field).matches();
				if (matches) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Editing support for changing the data type fields for matchode columns
	 */
	public class DataTypeEditingSupport extends EditingSupport {
		private mdMUMatchcode.MatchcodeMapping[] comboTypes;

		public DataTypeEditingSupport(TableViewer tv) {
			super(tv);
		}

		@Override protected boolean canEdit(Object element) {
			// Dereference the mapping info
			MappingInfo info = (MappingInfo) element;
			if (Const.isEmpty(info.input)) {
				return false;
			}
			// Make sure the matchcode object can be loaded
			try {
				getMatchcodeObject();
			} catch (DQTObjectException e) {
				// TODO Better error handling getMatchcodeObject
				e.printStackTrace(System.err);
				return false;
			}
			// We can edit it
			return true;
		}

		/*
		 * Creates the combo box cell editor that will be used to select a source field
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.EditingSupport#getCellEditor(java.lang.Object)
		 */
		@Override protected CellEditor getCellEditor(Object element) {
			// Dereference the mapping info
			MappingInfo info = (MappingInfo) element;
			// Get the mapping item type for this info's position.
			mdMUMatchcode.MatchcodeMappingTarget mappingItemType;
			try {
				mappingItemType = getMatchcodeObject().GetMappingItemType(info.pos + 1);
			} catch (DQTObjectException e) {
				// The canEdit() call checked this already. If we get here then something seriously bad has happened
				throw new RuntimeException(e);
			}
			// Build the combo box item array for that type
			comboTypes = new mdMUMatchcode.MatchcodeMapping[MatchUpUtil.getAllowedInputMappingCount(mappingItemType)];
			String[] labels = new String[comboTypes.length];
			for (int i = 0; i < comboTypes.length; i++) {
				comboTypes[i] = MatchUpUtil.getAllowedInputMappingType(mappingItemType, i);
				labels[i] = MatchUpUtil.getInputMappingLabel(comboTypes[i]);
			}
			// Create the editor
			ComboBoxCellEditor editor = new ComboBoxCellEditor((Composite) getViewer().getControl(), labels, SWT.READ_ONLY);
			return editor;
		}

		/*
		 * The managed element is the mapping info for a specific field.
		 * Returns an index into the source fields array that matches the current value.
		 * If there is no match, -1 is returned.
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.EditingSupport#getValue(java.lang.Object)
		 */
		@Override protected Object getValue(Object element) {
			MappingInfo info = (MappingInfo) element;
			// Return the index of the item in the item array
			for (int i = 0; i < comboTypes.length; i++) {
				if (comboTypes[i] == info.type) {
					return i;
				}
			}
			return -1;
		}

		/*
		 * The managed element is the mapping info for a specific field.
		 * The value is an index into the source fields array.
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.EditingSupport#setValue(java.lang.Object, java.lang.Object)
		 */
		@Override protected void setValue(Object element, Object value) {
			MappingInfo info = (MappingInfo) element;
			// The value is an index in the item array
			int index = (Integer) value;
			mdMUMatchcode.MatchcodeMapping newType = (index < 0) || (index >= comboTypes.length) ? mdMUMatchcode.MatchcodeMapping.General : comboTypes[index];
			// Set it if changed
			if (info.type != newType) {
				info.type = newType;
				// Indicate that configuration has changed
				dialog.setChanged();
			}
		}
	}


	/**
	 * Editing support for changing the input source fields for matchode columns
	 */
	public class InputEditingSupport extends EditingSupport {
		private SortedSet<String> sourceFields;
		private String[]          sourceFieldsArray;

		public InputEditingSupport(TableViewer tv, SortedSet<String> sourceFields) {
			super(tv);
			this.sourceFields = new TreeSet<String>(sourceFields);
			// Add a blank option
			this.sourceFields.add("");
			// Convert to the array that will be used in the combo box
			sourceFieldsArray = this.sourceFields.toArray(new String[this.sourceFields.size()]);
		}

		@Override protected boolean canEdit(Object element) {
			return true;
		}

		/*
		 * Creates the combo box cell editor that will be used to select a source field
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.EditingSupport#getCellEditor(java.lang.Object)
		 */
		@Override protected CellEditor getCellEditor(Object element) {
			ComboBoxCellEditor editor = new ComboBoxCellEditor((Composite) getViewer().getControl(), sourceFieldsArray, SWT.READ_ONLY);
			return editor;
		}

		/*
		 * The managed element is the mapping info for a specific field.
		 * Returns an index into the source fields array that matches the current value.
		 * If there is no match, -1 is returned.
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.EditingSupport#getValue(java.lang.Object)
		 */
		@Override protected Object getValue(Object element) {
			MappingInfo info = (MappingInfo) element;
			// Make sure the source field is valid
			String input = validateSourceField(info.input, sourceFields);
			// Translate the input field to an index into the defined source fields
			for (int i = 0; i < sourceFieldsArray.length; i++) {
				String sourceField = sourceFieldsArray[i];
				if (sourceField.equals(input)) {
					return i;
				}
			}
			return -1;
		}

		/*
		 * The managed element is the mapping info for a specific field.
		 * The value is an index into the source fields array.
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.EditingSupport#setValue(java.lang.Object, java.lang.Object)
		 */
		@Override protected void setValue(Object element, Object value) {
			MappingInfo info = (MappingInfo) element;
			// The value is an index in the source field array
			int index = (Integer) value;
			String newInput = (index < 0) || (index >= sourceFieldsArray.length) ? "" : sourceFieldsArray[index];
			// Set it if changed
			if (!info.input.equals(newInput)) {
				info.input = newInput;
				mdMUMatchcode.MatchcodeMappingTarget target = null;
				try {
					target = getMatchcodeObject().GetMappingItemType(info.pos + 1);//
				} catch (DQTObjectException e) {
					// TODO Better error handling GetMappingItemType
					e.printStackTrace();
				}
				info.type = MatchUpUtil.getBestInputMappingType(target);
				// Indicate that configuration has changed
				dialog.setChanged();
			}
		}
	}


	/*
	 * temporary structure used in resolving mappings
	 */
	private class HistoricalMapping {
		public String                               label;
		public mdMUMatchcode.MatchcodeMapping       mapSource;
		public mdMUMatchcode.MatchcodeMappingTarget mapTarget;
		public String                               field;
	}


	private class MappingInfo {
		public int                            pos;
		public String                         input;
		public mdMUMatchcode.MatchcodeMapping type;

		public MappingInfo(int pos, String input, mdMUMatchcode.MatchcodeMapping type) {
			this.pos = pos;
			this.input = input;
			this.type = type;
		}

		@Override public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			MappingInfo other = (MappingInfo) obj;
			if (!getOuterType().equals(other.getOuterType())) {
				return false;
			}
			if (input == null) {
				if (other.input != null) {
					return false;
				}
			} else if (!input.equals(other.input)) {
				return false;
			}
			if (pos != other.pos) {
				return false;
			}
			if (type == null) {
				if (other.type != null) {
					return false;
				}
			} else if (!type.equals(other.type)) {
				return false;
			}
			return true;
		}

		@Override public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = (prime * result) + getOuterType().hashCode();
			result = (prime * result) + ((input == null) ? 0 : input.hashCode());
			result = (prime * result) + pos;
			result = (prime * result) + ((type == null) ? 0 : type.hashCode());
			return result;
		}

		private MatchUpFieldMappingTab getOuterType() {
			return MatchUpFieldMappingTab.this;
		}
	}
}
