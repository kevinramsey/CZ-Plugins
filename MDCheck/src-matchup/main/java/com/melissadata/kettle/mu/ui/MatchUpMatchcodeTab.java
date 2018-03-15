package com.melissadata.kettle.mu.ui;

import com.melissadata.cz.DQTObjectException;
import com.melissadata.cz.DQTObjectFactory;
import com.melissadata.kettle.*;
import com.melissadata.kettle.mu.MatchUpUtil;
import com.melissadata.kettle.support.MDCheckHelper;
import com.melissadata.kettle.support.MDTab;
import com.melissadata.mdMUMatchcode;
import com.melissadata.mdMUMatchcodeComponent;
import com.melissadata.mdMUMatchcodeList;
import com.melissadata.kettle.mu.MatchUpMeta;
import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.operation.IRunnableWithProgress;
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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MatchUpMatchcodeTab implements MDTab {
	private static final int                 NB_DESCRIPTION_LINES         = 10;
	private static final int                 NB_MATCHING_RULES_LINES      = 16;
	private static final boolean             SHOW_MATCHCODE_DESCRIPTION   = true;
	private static final boolean             SHOW_MATCHCODE_INFOS         = false;
	private static final boolean             SHOW_MATCHCODE_EDITOR_BUTTON = false;//Const.isWindows();
	/* Matchcode info structures */
	private static final int                 MCINFO_DATATYPE              = 0;
	private static final int                 MCINFO_SIZE                  = 1;
	private static final int                 MCINFO_START                 = 2;
	private static final int                 MCINFO_FUZZY                 = 3;
	private static final int                 MCINFO_FIELDMATCH            = 4;
	private static final int                 MCINFO_SWAP                  = 5;
	private static final int                 MCINFO_COMBO_1               = 6;
	private static final int                 MCINFO_COMBO_16              = MCINFO_COMBO_1 + 15;
	private static final int                 MCINFO_MAX                   = MCINFO_COMBO_16 + 1;
	private static final int[]               MCINFO_WEIGHTS               = new int[] { 10, 10, 10, 10, 10, 10, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, };
	private static final ColumnLabelProvider MCINFO_LABEL_PROVIDER[]      = new ColumnLabelProvider[MCINFO_MAX];
	private static final int                 MAX_LINE                     = 100;
	private static final String              MATCHCODE_EDITOR_EXE         = "MatchUpEditor.exe";
	private static       Class<?>            PKG                          = MatchUpMeta.class;
	private static final String[]            MCINFO_TITLES                = new String[] { getString("MCInfo.DataType"), getString("MCInfo.Size"), getString("MCInfo.Start"), getString("MCInfo.Fuzzy"), getString("MCInfo.FieldMatch"),
			getString("MCInfo.Swap"), "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", };

	static {
		for (int i = 0; i < MCINFO_MAX; i++) {
			final int j = i;
			MCINFO_LABEL_PROVIDER[i] = new ColumnLabelProvider() {
				@Override public String getText(Object element) {
					return ((MatchcodeInfo) element).get(j);
				}
			};
		}
	}

	private MDCheckDialog dialog;
	private MDCheckHelper          helper;
	private MatchUpFieldMappingTab fieldMappingTab;
	private MatchUpMeta muMeta;
	/* Dialog controls */
	private CCombo                 cbMatchcode;
	private Label                  lblMCDescription;
	private Label                  lblMCRules;
	private TableViewer            tvMatchcodeInfos;
	private Button                 btnMatchcodeEditor;
	private mdMUMatchcode          dispMatchcode;

	public MatchUpMatchcodeTab(MDCheckDialog dialog) {
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
		return BaseMessages.getString(PKG, "MDCheckDialog.MatchUpMatchcodeTab." + key, args);
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.support.MDTab#advancedConfigChanged()
	 */
	public void advancedConfigChanged() {
		// Get the currently selected matchcode
		String matchcode = getMatchcodeName();
		// The configured matchcodes may have changed.
		// Repopulate the matchcode list and reselect the current matchcode name
		// This may result in changes in the field mapping tab as well.
		populateMatchcodeControl();
		// Re-initialize the controls with the previous match code
		initMatchcodeControl(matchcode);
		initMatchcodeInfos();
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
		// Fill in the input meta data
		data.getMatchUp().setMatchcodeName(getMatchcodeName());
	}

	/**
	 * @return The URL for help information
	 */
	public String getHelpURLKey() {
		return "MDCheck.Help.MatchUpMatchcodeTab";
	}

	/**
	 * @return The current value of the matchcode control
	 */
	public String getMatchcodeName() {
		int i = cbMatchcode.getSelectionIndex();
		String matchcode = (i >= 0) ? cbMatchcode.getItem(i) : "";
		return matchcode;
	}

	/**
	 * Loads the meta data into the dialog tab
	 *
	 * @param data
	 * @return
	 */
	public boolean init(MDCheckStepData data) {
		// Get the matchup meta data
		muMeta = data.getMatchUp();
		// Load the matchcode control
		boolean changed = initMatchcodeControl(muMeta.getMatchcodeName());
		// Load the matchcode info table
		if (MatchUpMeta.initOK) {
			initMatchcodeInfos();
		}
		return changed;
	}

	/**
	 * Called to connect this tab to the field mapping tab
	 *
	 * @param fieldMappingTab
	 */
	public void setFieldMappingTab(MatchUpFieldMappingTab fieldMappingTab) {
		this.fieldMappingTab = fieldMappingTab;
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
		Control wLastLine = null;
		// Create matchcode selector
		Control[] cbs = helper.addComboBoxes(wComp, null, "MatchUpMatchcodeTab.MatchcodeName", true);
		Label lblMatchcode = (Label) cbs[0];
		cbMatchcode = (CCombo) cbs[1];
		cbMatchcode.setEditable(false);
		cbMatchcode.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {
				matchcodeChanged();
			}
		});
		((FormData) lblMatchcode.getLayoutData()).right.numerator = 10;
		((FormData) cbMatchcode.getLayoutData()).left.numerator = 10;
		wLastLine = cbMatchcode;
		// Add description box (adjust its height)
		if (SHOW_MATCHCODE_DESCRIPTION) {
			Label label = helper.addLabel(wComp, wLastLine, "MatchUpMatchcodeTab.MatchcodeNameDescription");
			helper.setFontStyle(label, SWT.BOLD);
			lblMCDescription = helper.addLabel(wComp, label, null);
			((FormData) lblMCDescription.getLayoutData()).height = NB_DESCRIPTION_LINES * lblMCDescription.getFont().getFontData()[0].getHeight();
			// Add matching rules box (adjust its height)
			label = helper.addLabel(wComp, lblMCDescription, "MatchUpMatchcodeTab.MatchcodeNameRules");
			helper.setFontStyle(label, SWT.BOLD);
			lblMCRules = helper.addLabel(wComp, label, null);
			((FormData) lblMCRules.getLayoutData()).height = NB_MATCHING_RULES_LINES * lblMCRules.getFont().getFontData()[0].getHeight();
			wLastLine = lblMCRules;
		}
		// Create table for matchcode information
		if (SHOW_MATCHCODE_INFOS) {
			wLastLine = createMatchcodeInfos(wComp, wLastLine);
		}
		if (SHOW_MATCHCODE_EDITOR_BUTTON) {
			// Button for launching matchcode editor
			btnMatchcodeEditor = helper.addPushButton(wComp, wLastLine, "MatchUpMatchcodeTab.MatchcodeEditor", new SelectionAdapter() {
				@Override public void widgetSelected(SelectionEvent e) {
					matchcodeEditor();
				}
			});
			// Line up button with matchcode combo box control
			((FormData) btnMatchcodeEditor.getLayoutData()).left.numerator = 10;
			// If matchcode info is being displayed then pin the button to the bottom of its parent
			// and pin the matchcode info composite to the top of the button.
			if (tvMatchcodeInfos != null) {
				((FormData) wLastLine.getLayoutData()).bottom = new FormAttachment(btnMatchcodeEditor, -helper.margin);
				((FormData) btnMatchcodeEditor.getLayoutData()).top = null;
				((FormData) btnMatchcodeEditor.getLayoutData()).bottom = new FormAttachment(100, -helper.margin);
			}
		}
		// Populate the tab controls
		populateControls();
		return wComp;
	}

	/**
	 * Called to create one column of the matchcode table
	 *
	 * @param viewer
	 * @param title
	 * @param weight
	 * @param colNumber
	 * @param layout
	 * @param labelProvider
	 * @return
	 */
	private TableViewerColumn createMatchcodeInfoColumn(TableViewer viewer, String title, int weight, int colNumber, TableColumnLayout layout, ColumnLabelProvider labelProvider) {
		final TableViewerColumn tvColumn = new TableViewerColumn(viewer, SWT.NONE);
		final TableColumn tblColumn = tvColumn.getColumn();
		tblColumn.setText(title);
		tblColumn.setResizable(true);
		layout.setColumnData(tblColumn, new ColumnWeightData(weight, ColumnWeightData.MINIMUM_WIDTH, true));
		tvColumn.setLabelProvider(labelProvider);
		return tvColumn;
	}

	/**
	 * Called to fill in the columns of the matchcode table
	 *
	 * @param viewer
	 * @param layout
	 */
	private void createMatchcodeInfoColumns(TableViewer viewer, TableColumnLayout layout) {
		viewer.setContentProvider(new ArrayContentProvider());
		for (int i = 0; i < MCINFO_MAX; i++) {
			createMatchcodeInfoColumn(viewer, MCINFO_TITLES[i], MCINFO_WEIGHTS[i], i, layout, MCINFO_LABEL_PROVIDER[i]);
		}
	}

	/**
	 * Called to create the table viewer for the matchcode fields
	 *
	 * @param parent
	 * @param wLastLine
	 * @return
	 */
	private Control createMatchcodeInfos(Composite parent, Control wLastLine) {
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
		tvMatchcodeInfos = new TableViewer(wComp, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		// Create columns in table viewer
		createMatchcodeInfoColumns(tvMatchcodeInfos, tcl);
		// Make sure the table is layed out correctly and looks cnsistent
		Table table = tvMatchcodeInfos.getTable();
		helper.setLook(table, Props.WIDGET_STYLE_TABLE);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		return wComp;
	}

	/**
	 * Called to extract the information for this matchcode component.
	 *
	 * @param component
	 * @return
	 */
	private MatchcodeInfo getComponentInfo(mdMUMatchcodeComponent component) {
		MatchcodeInfo info = new MatchcodeInfo();
		info.dataType = MatchUpUtil.getComponentDescription(component, true);
		info.size = MatchUpUtil.getSizeDescription(component);
		info.start = MatchUpUtil.getStartDescription(component);
		info.fuzzy = MatchUpUtil.getFuzzyDescription(component, true);
		info.fieldMatch = MatchUpUtil.getFieldMatchDescription(component);
		info.swap = MatchUpUtil.getSwapDescription(component);
		String[] combos = MatchUpUtil.getCombinationDescriptions(component);
		info.combos = new String[combos.length];
		for (int i = 0; i < combos.length; i++) {
			info.combos[i] = combos[i];
		}
		return info;
	}

	/**
	 * Returns the description of the currently selected matchcode
	 *
	 * @return
	 */
	private String getMatchcodeDescription() {
		// Get the matchcode object
		mdMUMatchcode mc;
		try {
			mc = getMatchcodeObject();
		} catch (DQTObjectException e) {
			e.printStackTrace(System.err);
			return wrap("!ERR! " + e.getLocalizedMessage());
		}
		// Get detailed description
		String description = "";
		if (!Const.isEmpty(getMatchcodeName())) {
			description = wrap(mc.GetDescription());
		}
		return description;
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
			String matchcodeName = getMatchcodeName();
			String dataPath = dialog.getAdvancedConfigMeta().getLocalDataPath();
			int rc[] = new int[1];
			dispMatchcode = MatchUpUtil.getMatchcodeObject(matchcodeName, dataPath, rc);
			if ((rc[0] == 0) && !Const.isEmpty(matchcodeName)) {
				// Issue a warning message
				MessageDialog.openWarning(dialog.getShell(), dialog.getTitle(), BaseMessages.getString(PKG, "MDCheckDialog.MatchcodeComponentProblem.Message", matchcodeName, dispMatchcode.GetInitializeErrorString()));
			}
		}
		return dispMatchcode;
	}

	/**
	 * Returns the list of matching rules associated with the currently selected matchcode
	 *
	 * @return
	 */
	private String getMatchcodeRules() {
		// Get the matchcode object
		mdMUMatchcode mc;
		try {
			mc = getMatchcodeObject();
		} catch (DQTObjectException e) {
			e.printStackTrace(System.err);
			return wrap("!ERR! " + e.getLocalizedMessage());
		}
		// Get detailed description
		StringBuffer rules = new StringBuffer();
		String sep = "";
		if (!Const.isEmpty(getMatchcodeName())) {
			for (int i = 1; i < 16; i++) {
				String rule = mc.GetRuleDescription(i, 0);
				if (!Const.isEmpty(rule)) {
					rule = wrap(String.format("%1$2d: %2$s", i, rule));
					rules.append(sep).append(rule);
					sep = "\n";
				}
			}
		}
		return rules.toString();
	}

	/**
	 * Called to initialize the matchcode combo control
	 *
	 * @param newMatchcode
	 */
	private boolean initMatchcodeControl(String newMatchcode) {
		boolean changed = false;
		// Clear the selection
		cbMatchcode.select(-1);
		// Select the defined matchcode
		if (!Const.isEmpty(newMatchcode)) {
			String[] matchcodes = cbMatchcode.getItems();
			for (int i = 0; i < matchcodes.length; i++) {
				String matchcode = matchcodes[i];
				if (matchcode.equals(newMatchcode)) {
					cbMatchcode.select(i);
					break;
				}
			}
		}
		// If no match code selected...
		if (cbMatchcode.getSelectionIndex() < 0) {
			// If the matchcode was defined then it must have been removed since we were last called. Warn the user.
			if (!Const.isEmpty(newMatchcode)) {
				MessageDialog.openWarning(dialog.getShell(), dialog.getTitle(), BaseMessages.getString(PKG, "MDCheckDialog.MatchcodeNoLongerExists.Message", newMatchcode));
				// set it to default to prevent crash
				cbMatchcode.select(0);
				// Otherwise, select the first element by default
			} else if (cbMatchcode.getItemCount() > 0) {
				cbMatchcode.select(0);
			}
			changed = true;
		}
		// Update the matchcode descriptions
		if ((lblMCDescription != null) && MatchUpMeta.initOK) {
			lblMCDescription.setText(getMatchcodeDescription());
			lblMCRules.setText(getMatchcodeRules());
		}
		return changed;
	}

	/**
	 * Called to initialize the matchcode info table based on the currently selected matchcode
	 */
	private void initMatchcodeInfos() {
		if (tvMatchcodeInfos != null) {
			// Clear the current match code info list
			@SuppressWarnings("unchecked") List<MatchcodeInfo> matchcodeInfos = (List<MatchcodeInfo>) tvMatchcodeInfos.getInput();
			matchcodeInfos.clear();
			mdMUMatchcode Matchcode = null;
			mdMUMatchcodeComponent MatchcodeComponent = null;
			try {
				// Get the currently selected matchcode
				String matchcode = getMatchcodeName();
				// If the matchcode is blank then skip this
				if (Const.isEmpty(matchcode)) {
					return;
				}
				// Initialize a matchcode object
				String dataPath = dialog.getAdvancedConfigMeta().getLocalDataPath();
				int[] rc = new int[1];
				try {
					Matchcode = MatchUpUtil.getMatchcodeObject(matchcode, dataPath, rc);
				} catch (DQTObjectException e) {
					// TODO Better error handling?
					e.printStackTrace(System.err);
					MessageDialog.openWarning(dialog.getShell(), dialog.getTitle(), BaseMessages.getString(PKG, "MDCheckDialog.MatchcodeComponentProblem.Message", matchcode, e.getMessage()));
					return;
				}
				if (rc[0] == 0) {
					MessageDialog.openWarning(dialog.getShell(), dialog.getTitle(), BaseMessages.getString(PKG, "MDCheckDialog.MatchcodeComponentProblem.Message", matchcode, Matchcode.GetInitializeErrorString()));
					return;
				}
				// Get a row of descriptive information for each component
				int compCount = Matchcode.GetMatchcodeItemCount();
				for (int i = 1; i <= compCount; i++) {
					MatchcodeComponent = Matchcode.GetMatchcodeItem(i);
					MatchcodeInfo info = getComponentInfo(MatchcodeComponent);
					matchcodeInfos.add(info);
					MatchcodeComponent.delete();
					MatchcodeComponent = null;
				}
			} finally {
				// Cleanup
				if (Matchcode != null) {
					Matchcode.delete();
				}
				if (MatchcodeComponent != null) {
					MatchcodeComponent.delete();
				}
				// Always refres the viewer, even if something went wrong
				tvMatchcodeInfos.refresh();
			}
		}
	}

	/**
	 * Open the match code editor
	 */
	private void matchcodeEditor() {
		// Find the matchcode executable
		String localDataPath = dialog.getAdvancedConfigMeta().getLocalDataPath();
		File mcEdit = new File(localDataPath, MATCHCODE_EDITOR_EXE);
		if (!mcEdit.exists() || !mcEdit.isFile() || !mcEdit.canExecute()) {
			MessageDialog.openError(dialog.getShell(), dialog.getTitle(), BaseMessages.getString(PKG, "MDCheckDialog.MatchcodeEditorNotFound.Message", mcEdit.getAbsolutePath()));
			return;
		}
		// flag for community and lite
		boolean changed = dialog.isChanged();
		try {
			new ProgressMonitorDialog(dialog.getShell()).run(true, false, new RunMatchcodeEditor(mcEdit, localDataPath, getMatchcodeName()));
		} catch (InvocationTargetException e) {
			// Something went wrong. Throw it back up.
			Throwable cause = e.getTargetException();
			MessageDialog.openError(dialog.getShell(), dialog.getTitle(), BaseMessages.getString(PKG, "MDCheckDialog.MatchcodeEditorFailed.Message", cause.toString()));
			return;
		} catch (InterruptedException e) {
			MessageDialog.openError(dialog.getShell(), dialog.getTitle(), BaseMessages.getString(PKG, "MDCheckDialog.MatchcodeEditorFailed.Message", e.toString()));
			return;
		}
		boolean enterprise = (dialog.getAdvancedConfigMeta().getProducts(true) & AdvancedConfigurationMeta.MDLICENSE_MatchUp) != 0;
		// revert to original .mc file if not Enterprise
		MatchUpUtil.revertMCfile(dialog.getAdvancedConfigMeta().getLocalDataPath(), enterprise);
		// The matchcodes may have changed by the editor, therefore reload
		// everything.
		String matchcode = getMatchcodeName();
		// Repopulate the matchcode list and reselect the current matchcode name
		// This may result in changes in the field mapping tab as well.
		populateMatchcodeControl();
		// Re-initialize the controls with the previous match code
		initMatchcodeControl(matchcode);
		initMatchcodeInfos();
		if (!enterprise) {
			MessageBox box = new MessageBox(dialog.getShell(), SWT.OK | SWT.APPLICATION_MODAL | SWT.ICON_INFORMATION);
			box.setText(getString("MatchcodeEditor.Label"));
			box.setMessage(getString("MatchUpLiteInfo"));
			box.open();
			// we do this because using the editor sets the changed flag
			// since we can't change it from lite or community, if the dialog
			// has not been changed before we start the editor we clear it here.
			if (!changed) {
				dialog.clearChanged();
			}
		}
	}

	/**
	 * Called to populate the controls
	 */
	private void populateControls() {
		// Populate the matchcode combo box
		populateMatchcodeControl();
		// Populate the matchcode info table
		if (tvMatchcodeInfos != null) {
			tvMatchcodeInfos.setInput(new ArrayList<MatchcodeInfo>());
		}
	}

	/**
	 * Called to populate the matchcode combo list with configured matchcodes from the MatchUp object
	 */
	private void populateMatchcodeControl() {
		boolean enterprise = (dialog.getAdvancedConfigMeta().getProducts(true) & AdvancedConfigurationMeta.MDLICENSE_MatchUp) != 0;
		exportMCFile(dialog.getAdvancedConfigMeta().getLocalDataPath());
		if (Const.isWindows()) {
			MatchUpUtil.copyMCfile(dialog.getAdvancedConfigMeta().getLocalDataPath(), enterprise);
		}
		// Clear the current list
		cbMatchcode.removeAll();
		// Query the matchup component to get a list of the current match codes
		mdMUMatchcodeList matchcodeList = null;
		try {
			// Get the matchcode list object
			try {
				matchcodeList = DQTObjectFactory.newMatchcodeList();
			} catch (DQTObjectException e) {
				// / TODO: better error handling failure to get matchcode list
				e.printStackTrace(System.err);
				return;
			}
			// Fill in the list
			//FIXME matchUp Global
			//System.out.println(" MU data path : " + dialog.getAdvancedConfigMeta().getLocalDataPath());
			matchcodeList.SetPathToMatchUpFiles(dialog.getAdvancedConfigMeta().getLocalDataPath());
			if (matchcodeList.InitializeDataFiles() == mdMUMatchcodeList.ProgramStatus.ErrorNone) {
				int prod = dialog.getAdvancedConfigMeta().getProducts(true);
				// If not Enterprise
				if (((prod & AdvancedConfigurationMeta.MDLICENSE_MatchUp) == 0) && ((prod & AdvancedConfigurationMeta.MDLICENSE_MatchUpLite) == 0)) {
					cbMatchcode.add(matchcodeList.GetMatchcodeName(0));
					cbMatchcode.add(matchcodeList.GetMatchcodeName(1));
					cbMatchcode.add(matchcodeList.GetMatchcodeName(2));
					cbMatchcode.add(matchcodeList.GetMatchcodeName(9));
					cbMatchcode.add(matchcodeList.GetMatchcodeName(10));
					cbMatchcode.add(matchcodeList.GetMatchcodeName(11));
					cbMatchcode.add(matchcodeList.GetMatchcodeName(12));
					cbMatchcode.add(matchcodeList.GetMatchcodeName(16));
				} else {
					int count = matchcodeList.GetMatchcodeCount();
					for (int i = 0; i < count; i++) {
						cbMatchcode.add(matchcodeList.GetMatchcodeName(i));
					}
				}
			}
		} finally {
			if (matchcodeList != null) {
				matchcodeList.delete();
			}
		}
	}

	public void exportMCFile(String dataPath) {
		// Sometimes the mdMatchup.mc doesn't get copied correctly so lets make sure we have one.
		URL inputUrl = getClass().getResource("/com/melissadata/kettle/images/mdMatchup.mc");
		File dest = new File(dataPath + Const.FILE_SEPARATOR + "mdMatchup.mc");
		//System.out.println("Checking for .mc file in : " + dest);
		boolean getFile = false;
		try {
			if (FileUtils.readFileToString(dest).trim().isEmpty()) {
				getFile = true;
			}
		} catch (IOException ie) {
			getFile = true;
		}
		if (getFile) {
			//System.out.println(" - lets go get it ***** ");
			try {
				FileUtils.copyURLToFile(inputUrl, dest);
			} catch (IOException ie) {
				System.out.println("IO error getting .mc files from jar : " + ie.getMessage());
			}
		}
	}

	/**
	 * Makes sure a string description wraps cleanly.
	 *
	 * @param line
	 * @return
	 */
	private String wrap(String line) {
		String words[] = line.split("\\s");
		StringBuffer buf = new StringBuffer();
		int width = 0;
		for (String word : words) {
			// Check for new line
			if ((width != 0) && ((width + word.length()) > MAX_LINE)) {
				buf.append("\n");
				width = 0;
			}
			// Add separator (if needed)
			if (width > 0) {
				buf.append(" ");
				width++;
			}
			// Add word
			buf.append(word);
			width += word.length();
		}
		return buf.toString();
	}

	/**
	 * Called when the matchcode combo changes.
	 */
	protected void matchcodeChanged() {
		// Throw out the matchcode object we are currently using
		if (dispMatchcode != null) {
			dispMatchcode.delete();
			dispMatchcode = null;
		}
		// Update the description and rules
		if (lblMCDescription != null) {
			lblMCDescription.setText(getMatchcodeDescription());
			lblMCRules.setText(getMatchcodeRules());
		}
		// Re-initialize the matchcode info table
		initMatchcodeInfos();
		// Need to update the field mapping tab as well
		fieldMappingTab.matchcodeChanged();
	}

	public class MatchcodeInfo {
		public String   dataType;
		public String   size;
		public String   start;
		public String   fuzzy;
		public String   fieldMatch;
		public String   swap;
		public String[] combos;

		public String get(int i) {
			switch (i) {
				case MCINFO_DATATYPE:
					return dataType;
				case MCINFO_SIZE:
					return size;
				case MCINFO_START:
					return start;
				case MCINFO_FUZZY:
					return fuzzy;
				case MCINFO_FIELDMATCH:
					return fieldMatch;
				case MCINFO_SWAP:
					return swap;
				default:
					return combos[i - MCINFO_COMBO_1];
			}
		}
	}


	/**
	 * Class that wraps the execution of the matchcode editror
	 */
	public class RunMatchcodeEditor implements IRunnableWithProgress {
		private File   mcEdit;
		private String localDataPath;
		private String matchcode;

		public RunMatchcodeEditor(File mcEdit, String localDataPath, String matchcode) {
			this.mcEdit = mcEdit;
			this.localDataPath = localDataPath;
			this.matchcode = matchcode;
		}

		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			// Start monitoring
			monitor.beginTask(BaseMessages.getString(PKG, "MDCheckDialog.RunMatchcodeEditor.Message"), IProgressMonitor.UNKNOWN);
			// Execute the matchcode editor
			Process process = null;
			try {
				Runtime runtime = Runtime.getRuntime();
				String cmdArray[] = new String[3];
				cmdArray[0] = mcEdit.getAbsolutePath();
				cmdArray[1] = localDataPath;
				cmdArray[2] = matchcode;
				process = runtime.exec(cmdArray);
				process.waitFor();
			} catch (IOException e) {
				throw new InvocationTargetException(e);
			} finally {
				if (process != null) {
					process.destroy();
				}
				monitor.done();
			}
		}
	}


	/**
	 * Class that wraps the execution of the matchcode editror
	 */
	public class RunMatchcodeEditor1 {
		private File   mcEdit;
		private String localDataPath;
		private String matchcode;

		public RunMatchcodeEditor1(File mcEdit, String localDataPath, String matchcode) {
			this.mcEdit = mcEdit;
			this.localDataPath = localDataPath;
			this.matchcode = matchcode;
		}

		// Execute the matchcode editor
		public void openEditor(Button btn) {
			btn.setText("Launching Editor");
			Process process = null;
			try {
				Runtime runtime = Runtime.getRuntime();
				String cmdArray[] = new String[3];
				cmdArray[0] = mcEdit.getAbsolutePath();
				cmdArray[1] = localDataPath;
				cmdArray[2] = matchcode;
				process = runtime.exec(cmdArray);
				process.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (process != null) {
					process.destroy();
				}
				btn.setText("Matchcode Editor...");
			}
		}
	}
}
