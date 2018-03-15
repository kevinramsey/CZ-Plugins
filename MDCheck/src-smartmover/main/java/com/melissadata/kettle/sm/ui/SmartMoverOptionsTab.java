package com.melissadata.kettle.sm.ui;

import com.melissadata.cz.ui.MDAbstractDialog;
import com.melissadata.kettle.MDCheckDialog;
import com.melissadata.kettle.MDCheckMeta;
import com.melissadata.kettle.MDCheckStepData;
import com.melissadata.kettle.sm.SmartMoverMeta;
import com.melissadata.kettle.support.MDCheckHelper;
import com.melissadata.kettle.support.MDTab;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
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
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.util.ImageUtil;

import java.io.File;

public class SmartMoverOptionsTab implements MDTab {
	private static final String   REPORT_EXTENSION  = ".htm";
	private static final String[] REPORT_EXTENSIONS = new String[] { "*" + REPORT_EXTENSION };
	private static       Class<?> PKG               = SmartMoverMeta.class;
	private MDCheckDialog  dialog;
	private MDCheckHelper  helper;
	private SmartMoverMeta smMeta;
	private ComboViewer    vOptionCountry;
	private ComboViewer    vOptionProcessingType;
	private Spinner        wMailFrequency;
	private Spinner        wMonthsRequested;
	private Group          gSummaryReports;
	private Button         wOptReports;
	private Text           wJobIdOverride;
	private Button         wOptJobOver;
	private Button         btnReprintReport;
	private Label          lblJobIDInfo;
	private TextVar        wListName;
	private TextVar        wNCOAFile;
	private TextVar        wCASSFile;

	public SmartMoverOptionsTab(MDCheckDialog dialog) {
		this.dialog = dialog;
		helper = dialog.getHelper();
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
		// Add descriptive label
		Label description = helper.addLabel(wComp, null, "SmartMoverOptionsTab.Description");
		Label spacer = helper.addSpacer(wComp, description);
		spacer = helper.addSpacer(wComp, spacer);
		// Add the option groups
		Group gProcessingOptions = new Group(wComp, SWT.NONE);
		gProcessingOptions.setText(getString("ProcessingOptionsGroup.Label"));
		helper.setLook(gProcessingOptions);
		fl = new FormLayout();
		fl.marginHeight = helper.margin;
		fl.marginWidth = helper.margin;
		gProcessingOptions.setLayout(fl);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin / 2);
		fd.top = new FormAttachment(spacer, helper.margin / 2);
		fd.right = new FormAttachment(100, -helper.margin / 2);
		gProcessingOptions.setLayoutData(fd);
		// Control for enabling summary reports
		wOptReports = helper.addCheckBox(wComp, gProcessingOptions, "SmartMoverOptionsTab.SummaryReportsGroup");
		wOptReports.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {
				enable();
			}
		});
		((FormData) wOptReports.getLayoutData()).top.offset -= 2;
		((FormData) wOptReports.getLayoutData()).left.offset += 8;
		// Summary Reports option group
		gSummaryReports = new Group(wComp, SWT.NONE);
// gSummaryReports.setText(getString("SummaryReportsGroup.Label"));
		helper.setLook(gSummaryReports);
		fl = new FormLayout();
		fl.marginHeight = helper.margin;
		fl.marginWidth = helper.margin;
		gSummaryReports.setLayout(fl);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin / 2);
		fd.top = new FormAttachment(gProcessingOptions, helper.margin / 2);
		fd.right = new FormAttachment(100, -helper.margin / 2);
		gSummaryReports.setLayoutData(fd);
		// Fill in processing options group
		vOptionCountry = helper.addEnumComboBox(gProcessingOptions, null, "SmartMoverOptionsTab.OptionCountries", SmartMoverMeta.Countries.values());
		vOptionProcessingType = helper.addEnumComboBox(gProcessingOptions, vOptionCountry.getControl(), "SmartMoverOptionsTab.OptionProcessingType", SmartMoverMeta.ProcessingType.values());
		spacer = helper.addSpacer(gProcessingOptions, vOptionProcessingType.getControl());
		wMailFrequency = helper.addSpinner(gProcessingOptions, spacer, "SmartMoverOptionsTab.OptionMailFrequency", "SmartMoverOptionsTab.OptionMailFrequencyDescription");
		wMonthsRequested = helper.addSpinner(gProcessingOptions, wMailFrequency, "SmartMoverOptionsTab.OptionMonthsRequested", "SmartMoverOptionsTab.OptionMonthsRequestedDescription");
		wMailFrequency.setMinimum(SmartMoverMeta.MAIL_FREQUENCY_MIN);
		wMailFrequency.setMaximum(SmartMoverMeta.MAIL_FREQUENCY_MAX);
		wMonthsRequested.setMinimum(SmartMoverMeta.MONTHS_REQUESTED_MIN);
		wMonthsRequested.setMaximum(SmartMoverMeta.MONTHS_REQUESTED_MAX);
		// Fill in summary reports group
		wListName = helper.addTextVarBox(gSummaryReports, null, "SmartMoverOptionsTab.OptionListName");
		wNCOAFile = helper.addTextVarBox(gSummaryReports, wListName, "SmartMoverOptionsTab.OptionNCOAFile");
		Button btnNCOABrowse = helper.addPushButton(gSummaryReports, wListName, null, new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {
				browse(wNCOAFile);
			}
		});
		btnNCOABrowse.setImage(ImageUtil.getImage(helper.getDisplay(), PKG, "com/melissadata/kettle/images/OpenFolder.gif"));
		wCASSFile = helper.addTextVarBox(gSummaryReports, wNCOAFile, "SmartMoverOptionsTab.OptionCASSFile");
		Button btnCASSBrowse = helper.addPushButton(gSummaryReports, wNCOAFile, null, new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {
				browse(wCASSFile);
			}
		});
		btnCASSBrowse.setImage(ImageUtil.getImage(helper.getDisplay(), PKG, "com/melissadata/kettle/images/OpenFolder.gif"));
		// Job ID override
		wOptJobOver = helper.addCheckBox(gSummaryReports, wCASSFile, "SmartMoverOptionsTab.OptionJobId");
		wOptJobOver.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {
				enable();
			}
		});
		wJobIdOverride = helper.addTextBox(gSummaryReports, wCASSFile, null);
		// Reprint report button
		btnReprintReport = helper.addPushButton(gSummaryReports, wCASSFile, "SmartMoverOptionsTab.RetrievePastJobs", new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent arg0) {
				reprintReport();
			}
		});
		btnReprintReport.setToolTipText(getString("RetrievePastJobs.ToolTip"));
		// Adjust layout
		((FormData) wOptJobOver.getLayoutData()).left = null;
		((FormData) wOptJobOver.getLayoutData()).top.offset += 3 * helper.margin;
		((FormData) wOptJobOver.getLayoutData()).right = new FormAttachment(wJobIdOverride, -helper.margin);
		((FormData) wJobIdOverride.getLayoutData()).right = new FormAttachment(btnReprintReport, -helper.margin);
		((FormData) wJobIdOverride.getLayoutData()).top.offset += 3 * helper.margin;
		((FormData) btnReprintReport.getLayoutData()).left = null;
		((FormData) btnReprintReport.getLayoutData()).top.offset += 2.5 * helper.margin;
		((FormData) btnReprintReport.getLayoutData()).right = new FormAttachment(100, -helper.margin);
		// Job Id information
		// TODO what should be displayed in this Label ?
		lblJobIDInfo = helper.addLabel(gSummaryReports, wJobIdOverride, "SmartMoverOptionsTab.JobIdInfo");
		((FormData) lblJobIDInfo.getLayoutData()).left = new FormAttachment(helper.colWidth[0], helper.margin);
//
// ((FormData)wJobIdOverride.getLayoutData()).right = new FormAttachment(labelJobProcess,-helper.margin);
//
// // Place label in first column
// fd = new FormData();
// fd.left = null;
// fd.top = new FormAttachment(XbtnJobRetrv, helper.margin);
// fd.right = new FormAttachment(100, -helper.margin);
// labelJobProcess.setLayoutData(fd);
//
// // Adjust layout
// ((FormData)btnReprintReport.getLayoutData()).left = null;
// ((FormData)btnReprintReport.getLayoutData()).right= new FormAttachment(100, -helper.margin);
// ((FormData)wOptJobOver.getLayoutData()).right = new FormAttachment(btnReprintReport,-helper.margin);
		((FormData) wNCOAFile.getLayoutData()).right = new FormAttachment(btnNCOABrowse, -helper.margin);
		((FormData) btnNCOABrowse.getLayoutData()).left = null;
		((FormData) btnNCOABrowse.getLayoutData()).right = new FormAttachment(100, -helper.margin);
		((FormData) wCASSFile.getLayoutData()).right = new FormAttachment(btnCASSBrowse, -helper.margin);
		((FormData) btnCASSBrowse.getLayoutData()).left = null;
		((FormData) btnCASSBrowse.getLayoutData()).right = new FormAttachment(100, -helper.margin);
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
		SmartMoverMeta smartMoverMeta = data.getSmartMover();
		smartMoverMeta.setOptionCountries((SmartMoverMeta.Countries) (((IStructuredSelection) vOptionCountry.getSelection()).getFirstElement()));
		smartMoverMeta.setOptionProcessingType((SmartMoverMeta.ProcessingType) (((IStructuredSelection) vOptionProcessingType.getSelection()).getFirstElement()));
		smartMoverMeta.setOptionMailFrequency(wMailFrequency.getSelection());
		smartMoverMeta.setOptionMonthsRequested(wMonthsRequested.getSelection());
		smartMoverMeta.setOptionSummaryReports(wOptReports.getSelection());
		smartMoverMeta.setOptionListName(wListName.getText());
		smartMoverMeta.setOptionNCOAFile(wNCOAFile.getText());
		smartMoverMeta.setOptionCASSFile(wCASSFile.getText());
		smartMoverMeta.setOptJobOverride(wOptJobOver.getSelection());
		smartMoverMeta.setOptJobOverrideId(wJobIdOverride.getText());
	}

	/**
	 * @return The URL for help information
	 */
	public String getHelpURLKey() {
		if (!MDCheckMeta.isPentahoPlugin()) {
			return "MDCheck.Help.SmartMoverOptionsTab";
		} else {
			return "MDCheck.Plugin.Help.SmartMoverOptionsTab";
		}
	}

	/**
	 * Loads the meta data into the dialog tab
	 *
	 * @param data
	 */
	public boolean init(MDCheckStepData data) {
		// Get the smart mover meta data
		smMeta = data.getSmartMover();
		// Initialize controls
		vOptionCountry.setSelection(new StructuredSelection(smMeta.getOptionCountries()));
		vOptionProcessingType.setSelection(new StructuredSelection(smMeta.getOptionProcessingType()));
		wMailFrequency.setSelection(smMeta.getOptionMailFrequency());
		wMonthsRequested.setSelection(smMeta.getOptionMonthsRequest());
		wOptReports.setSelection(smMeta.getOptionSummaryReports());
		wListName.setText(smMeta.getOptionListName());
		wNCOAFile.setText(smMeta.getOptionNCOAFile());
		wCASSFile.setText(smMeta.getOptionCASSFile());
		wOptJobOver.setSelection(smMeta.isOptJobOverride());
		wJobIdOverride.setText(smMeta.getOptJobOverrideId());
		// Set initial enablement
		enable();
		return false;
	}

	/**
	 * Called to browse for a directory
	 *
	 * @param wFile
	 */
	private void browse(TextVar wFile) {
		FileDialog fileDialog = new FileDialog(dialog.getShell(), SWT.SAVE);
		String dataPath = (wFile.getText() != null) ? wFile.getText() : "";
		String oldPath = dialog.getSpace().environmentSubstitute(dataPath);
		fileDialog.setFilterPath(oldPath);
		fileDialog.setFileName(oldPath);
		fileDialog.setFilterExtensions(REPORT_EXTENSIONS);
		if (fileDialog.open() != null) {
			String fileName = fileDialog.getFileName();
			if (!fileName.toLowerCase().endsWith(REPORT_EXTENSION)) {
				fileName += REPORT_EXTENSION;
			}
			String newPath = new File(new File(fileDialog.getFilterPath()), fileName).getAbsolutePath();
			if (!newPath.equalsIgnoreCase(oldPath)) {
				wFile.setText(newPath);
			}
		}
	}

	/**
	 * Called to handle enablement of controls based on input settings
	 */
	private void enable() {
		boolean reportEnable = wOptReports.getSelection();
		for (Control control : gSummaryReports.getChildren()) {
			control.setEnabled(reportEnable);
		}
		gSummaryReports.setEnabled(true);
		wOptJobOver.setEnabled(true);
		wOptReports.setEnabled(true);
		lblJobIDInfo.setEnabled(true);
		btnReprintReport.setEnabled(true);
		boolean overrideEnable = wOptJobOver.getSelection();
		wJobIdOverride.setEnabled(overrideEnable);
	}

	/**
	 * @param key
	 * @param args
	 * @return
	 */
	private String getString(String key, String... args) {
		return BaseMessages.getString(PKG, "MDCheckDialog.SmartMoverOptionsTab." + key, args);
	}

	/**
	 * Called to display destinations for additional address information
	 */
	private void reprintReport() {
		MDAbstractDialog rrd = new ReprintReportsDialog(dialog, wNCOAFile.getText(), wCASSFile.getText());
		if (rrd.open()) {
			dialog.setChanged();
		}
	}
}
