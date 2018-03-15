package com.melissadata.kettle.report;

import com.melissadata.cz.MDProps;
import com.melissadata.kettle.MDCheckDialog;
import com.melissadata.kettle.MDCheckStepData;
import com.melissadata.kettle.cv.address.AddressVerifyTab;
import com.melissadata.kettle.cv.geocode.GeoCoderTab;
import com.melissadata.kettle.cv.name.NameParseTab;
import com.melissadata.kettle.cv.phone.PhoneEmailTab;
import com.melissadata.kettle.support.MDCheckHelper;
import com.melissadata.kettle.support.MDTab;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.util.ImageUtil;

import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

public class ReportingTab implements MDTab {
	private static Class<?> PKG = ReportingTab.class;
	private MDCheckDialog dialog;
	private MDCheckHelper helper;
	private ReportingMeta reportingMeta;
	private CTabItem[]    tabs;
	private Group         gOutput;
	private Button        bPerformGlobalReports;
	private Button        bSaveToFile;
	private TextVar       wRepOutputName;
	private TextVar       wRepDirname;
	private Group         gOptions;
	private Button[]      bSubReports;

	public ReportingTab(MDCheckDialog dialog) {
		this.dialog = dialog;
		helper = dialog.getHelper();

		// Create the tab
		CTabItem wTab = new CTabItem(dialog.getTabFolder(), SWT.NONE);
		dialog.getTabFolder().addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				enable();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				enable();
			}
		});
		wTab.setText(getString("TabTitle"));
		wTab.setData(this);

		tabs = dialog.getTabFolder().getItems();

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

		// Fit the composite within its container (the scrolled composite)
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(0, 0);
		fd.right = new FormAttachment(100, 0);
		fd.bottom = new FormAttachment(100, 0);
		wComp.setLayoutData(fd);

		// Description line
		Label description = new Label(wComp, SWT.LEFT | SWT.WRAP);
		description.setText(getString("Description"));
		helper.setLook(description);

		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(0, helper.margin);
		description.setLayoutData(fd);

		fl = new FormLayout();
		fl.marginHeight = helper.margin;
		fl.marginWidth = helper.margin;

		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(3, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		fd.bottom = new FormAttachment(25, -helper.margin);

		gOutput = new Group(wComp, SWT.NONE);
		gOutput.setLayout(fl);
		gOutput.setLayoutData(fd);
		helper.setLook(gOutput);

		Label descriptionOpt = new Label(wComp, SWT.LEFT | SWT.WRAP);
		descriptionOpt.setText(getString("DescriptionOptions"));
		helper.setLook(descriptionOpt);

		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(gOutput, helper.margin);
		descriptionOpt.setLayoutData(fd);

		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(28, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		fd.bottom = new FormAttachment(100, -helper.margin);

		gOptions = new Group(wComp, SWT.NONE);
		gOptions.setLayout(fl);
		gOptions.setLayoutData(fd);
		helper.setLook(gOptions);

		bPerformGlobalReports = helper.addCheckBox(gOutput, null, "ReportingTab.GenerateReportsGlobal");
		bPerformGlobalReports.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				enable();
				if (bPerformGlobalReports.getSelection())
					selectSubReports();
				else
					deselectSubReports();
			}
		});

		helper.colWidth[0] = 5;

		bSaveToFile = helper.addCheckBox(gOutput, bPerformGlobalReports, "ReportingTab.RepFilename");
		bSaveToFile.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				enableNames();
			}
		});

		Label lblOutputLabel = helper.addLabel(gOutput, bSaveToFile, "ReportingTab.RepOutputName");
		wRepOutputName = helper.addTextVarBox(gOutput, bSaveToFile, null);
		Label lblDirLabel = helper.addLabel(gOutput, lblOutputLabel, "ReportingTab.RepFilenameLocation");
		wRepDirname = helper.addTextVarBox(gOutput, lblOutputLabel, null);

		Button btnBrowse = helper.addPushButton(gOutput, lblOutputLabel, null, new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {
				browseOutput(wRepDirname);
			}
		});
		btnBrowse.setImage(ImageUtil.getImage(helper.getDisplay(), PKG, "com/melissadata/kettle/images/OpenFolder.gif"));

		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(bPerformGlobalReports, 15);
		bSaveToFile.setLayoutData(fd);

		// Adjust control layout
		FormData layoutData = (FormData) lblOutputLabel.getLayoutData();
		layoutData.left = new FormAttachment(0, helper.margin);
		layoutData.top = new FormAttachment(bSaveToFile, (helper.margin * 4));
		layoutData.right = new FormAttachment(5, helper.margin);
		layoutData = (FormData) wRepOutputName.getLayoutData();
		layoutData.left = new FormAttachment(lblOutputLabel, helper.margin);
		layoutData.top = new FormAttachment(bSaveToFile, (helper.margin * 4));
		layoutData.right = new FormAttachment(10, helper.margin);
		layoutData = (FormData) lblDirLabel.getLayoutData();
		layoutData.left = new FormAttachment(0, helper.margin);
		layoutData.top = new FormAttachment(lblOutputLabel, (helper.margin * 4));
		layoutData.right = new FormAttachment(5, helper.margin);
		layoutData = (FormData) wRepDirname.getLayoutData();
		layoutData.left = new FormAttachment(lblDirLabel, helper.margin);
		layoutData.top = new FormAttachment(lblOutputLabel, (helper.margin * 4));
		layoutData.right = new FormAttachment(20, -(helper.margin * 2));

		layoutData = (FormData) btnBrowse.getLayoutData();
		layoutData.left = new FormAttachment(20, helper.margin);
		layoutData.top = new FormAttachment(lblOutputLabel, (helper.margin * 4));
		layoutData.right = new FormAttachment(23, -helper.margin);

		// Generate the reporting description information related to each tab
		Map<String, String> reportFiles = new TreeMap<String, String>();
		Map<String, String> reportDesc = new TreeMap<String, String>();
		Map<String, String> reportPopup = new TreeMap<String, String>();

		Properties props = MDProps.getProperties();
		for (Map.Entry<Object, Object> entry : props.entrySet()) {
			String key = (String) entry.getKey();
			if (key.startsWith("ReportFile"))
				reportFiles.put(key, (String) entry.getValue());
			else if (key.startsWith("ReportDesc"))
				reportDesc.put(key, (String) entry.getValue());
			else if (key.startsWith("ReportPopup"))
				reportPopup.put(key, (String) entry.getValue());
		}

		// Create enablement controls for each sub-report
		bSubReports = new Button[reportFiles.size()];
		Button bLastButton = null;
		int i = 0;
		for (String key : reportFiles.keySet()) {

			// Create enablement control for one sub-report
			Button bSubReport = bSubReports[i++] = helper.addCheckBox(gOptions, bLastButton, null);
			bLastButton = bSubReport;

			// Add a descriptive decoration to the control
			String popKey = "ReportPopup" + key.substring(10);
			if (reportPopup.containsKey(popKey)) {
				ControlDecoration controlDecoration = new ControlDecoration(bSubReport, SWT.TOP | SWT.RIGHT);
				controlDecoration.setImage(ImageUtil.getImage(dialog.getShell().getDisplay(), this.getClass(), "com/melissadata/kettle/images/question-mark-small.png"));
				controlDecoration.setDescriptionText(reportPopup.get(popKey));
			}

			// Set the buttons label
			String descKey = "ReportDesc" + key.substring(10);
			if (reportDesc.containsKey(descKey))
				bSubReport.setText(reportDesc.get(descKey));
			else
				dialog.getLog().logError("Missing description for report: " + descKey);
		}

		// Indicate a no sub-report condition
		if (reportFiles.size() == 0)
			helper.addLabel(gOptions, null, "ReportingTab.NoSubs");

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

	/**
	 * Loads the meta data into the dialog tab
	 *
	 * @param data
	 * @return
	 */
	public boolean init(MDCheckStepData data) {
		// General report
		reportingMeta = data.getReportMeta();
		bPerformGlobalReports.setSelection(reportingMeta.isOptGlobalReports());

		// Sub-reports
		boolean[] subReports = reportingMeta.getOptsSubReports();
		for (int i = 0; i < bSubReports.length; i++)
			bSubReports[i].setSelection(i < subReports.length ? subReports[i] : false);

		// Report output directory
		wRepDirname.setText(reportingMeta.getOutputReportDirname(null));
		if (reportingMeta.getOutputReportName(null) != null && reportingMeta.getOutputReportName(null).length() > 0)
			wRepOutputName.setText(reportingMeta.getOutputReportName(null));
		else
			wRepOutputName.setText("");

		// Should it save to a file?
		bSaveToFile.setSelection(reportingMeta.isOptToFile());

		// Set initial enablement
		enable();

		return false;
	}

	/**
	 * Loads the dialog data into the meta structure
	 */
	public void getData(MDCheckStepData data) {

		ReportingMeta reportMeta = data.getReportMeta();
		reportMeta.setOptGlobalReports(bPerformGlobalReports.getSelection());

		boolean[] subReports = new boolean[bSubReports.length];
		for (int i = 0; i < bSubReports.length; i++)
			subReports[i] = bSubReports[i].getSelection();

		reportMeta.setOptsSubReports(subReports);
		reportMeta.setOptToFile(bSaveToFile.getSelection());
		reportMeta.setOutputReportDirname(wRepDirname.getText());
		reportMeta.setOutputReportName(wRepOutputName.getText());
	}

	/**
	 * Called to browse for a directory
	 *
	 * @param wDir
	 */
	private void browseOutput(TextVar wDir) {
		DirectoryDialog dirDialog = new DirectoryDialog(dialog.getShell(), SWT.OPEN);

		String dataPath = (wDir != null && wDir.getText() != null) ? wDir.getText() : "";

		String oldPath = dialog.getSpace().environmentSubstitute(dataPath);
		dirDialog.setFilterPath(oldPath);

		if (dirDialog.open() != null) {
			String dirName = dirDialog.getFilterPath();
			if (!dirName.equalsIgnoreCase(oldPath))
				wDir.setText(dirName);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.support.MDTab#advancedConfigChanged()
	 */
	public void advancedConfigChanged() {
		// Update enablement
		enable();
	}

	/**
	 * Called to handle enablement of controls based on input settings
	 */
	private void enable() {
		enableNames();

		boolean enabled = bPerformGlobalReports.getSelection();
		for (Control child : gOptions.getChildren())
			child.setEnabled(enabled);
		for (Control child : gOutput.getChildren())
			child.setEnabled(enabled);
		bPerformGlobalReports.setEnabled(true);
		if (bPerformGlobalReports.getSelection())
			autoEnableSubReports();
	}

	/**
	 * Called to enable the controls related to output file names
	 */
	private void enableNames() {
		if (bSaveToFile.getSelection()) {
			wRepOutputName.setEnabled(true);
			wRepDirname.setEnabled(true);
		} else {
			wRepOutputName.setEnabled(false);
			wRepDirname.setEnabled(false);
		}
	}

	/**
	 * Called to enable the sub-reports for sufficiently configured tabs
	 */
	private void selectSubReports() {
		for (int i = 0; i < bSubReports.length; i++) {
			for (int j = 0; j < tabs.length; j++) {
				String buttonText = bSubReports[i].getText();
				String tabText = tabs[j].getText();
				Object tab = tabs[j].getData();

				// Enable sub-reports for tabs that have required inputs set
				if (buttonText.contains("Phone") && tabText.contains("Phone") && ((PhoneEmailTab) tab).getInputPhone().length() != 0) {
					bSubReports[i].setSelection(true);
					bSubReports[i].setEnabled(true);
				}

				if (buttonText.contains("Email") && tabText.contains("Email") && ((PhoneEmailTab) tab).getInputEmail().length() != 0) {
					bSubReports[i].setSelection(true);
					bSubReports[i].setEnabled(true);
				}

				if (buttonText.contains("Geo") && tabText.contains("Geo") && !((GeoCoderTab) tab).getSourceNotGeoCoding()) {
					bSubReports[i].setSelection(true);
					bSubReports[i].setEnabled(true);
				}

				if (buttonText.contains("Address") && tabText.contains("Address") && ((AddressVerifyTab) tab).getInputAddressLine1().length() != 0) {
					bSubReports[i].setSelection(true);
					bSubReports[i].setEnabled(true);
				}

				if (buttonText.contains("Name") && tabText.contains("Name") && ((NameParseTab) tab).getFullName().length() != 0) {
					bSubReports[i].setSelection(true);
					bSubReports[i].setEnabled(true);
				}
			}
		}
	}

	/**
	 * Called to de-select sub-report tabs
	 */
	private void deselectSubReports() {
		for (Button bSubReport : bSubReports)
			bSubReport.setSelection(false);
		bSaveToFile.setSelection(false);
	}

	/**
	 * Called to do automatic enablement of sub-reports based on configuration of tabs
	 */
	private void autoEnableSubReports() {
		for (int i = 0; i < bSubReports.length; i++) {
			for (int j = 0; j < tabs.length; j++) {
				String buttonText = bSubReports[i].getText();
				String tabText = tabs[j].getText();
				Object tab = tabs[j].getData();

				if (buttonText.contains("Phone") && tabText.contains("Phone")) {
					if (((PhoneEmailTab) tab).getInputPhone().length() != 0) {
						bSubReports[i].setEnabled(true);
					} else {
						bSubReports[i].setEnabled(false);
						bSubReports[i].setSelection(false);
					}
				}

				if (buttonText.contains("Email") && tabText.contains("Email")) {
					if (((PhoneEmailTab) tab).getInputEmail().length() != 0) {
						bSubReports[i].setEnabled(true);
					} else {
						bSubReports[i].setEnabled(false);
						bSubReports[i].setSelection(false);
					}
				}

				if (buttonText.contains("Geo") && tabText.contains("Geo")) {
					if (!((GeoCoderTab) tab).getSourceNotGeoCoding()) {
						bSubReports[i].setEnabled(true);
					} else {
						bSubReports[i].setEnabled(false);
						bSubReports[i].setSelection(false);
					}
				}

				if (buttonText.contains("Address") && tabText.contains("Address")) {
					if (((AddressVerifyTab) tab).getInputAddressLine1().length() != 0) {
						bSubReports[i].setEnabled(true);
					} else {
						bSubReports[i].setEnabled(false);
						bSubReports[i].setSelection(false);
					}
				}

				if (buttonText.contains("Name") && tabText.contains("Name")) {
					if (((((NameParseTab) tab).getFullName().length() != 0) || (((NameParseTab) tab).getCompanyName().length() != 0))) {
						bSubReports[i].setEnabled(true);
					} else {
						bSubReports[i].setEnabled(false);
						bSubReports[i].setSelection(false);
					}
				}
			}
		}
	}

	/**
	 * @param key
	 * @param args
	 * @return
	 */
	private String getString(String key, String... args) {
		return BaseMessages.getString(PKG, "MDCheckDialog.ReportingTab." + key, args);
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.support.MDTab#getHelpURLKey()
	 */
	public String getHelpURLKey() {
		return "MDCheck.Help.ReportingTab";
	}

	public void dispose() {
		// nothing to do

	}
}
