package com.melissadata.kettle.profiler.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.i18n.BaseMessages;

import com.melissadata.kettle.profiler.MDProfilerDialog;
import com.melissadata.kettle.profiler.MDProfilerMeta;
import com.melissadata.kettle.profiler.data.ProfilerFields;
import com.melissadata.kettle.profiler.support.MDProfilerHelper;
import com.melissadata.kettle.profiler.support.MDTab;

public class AnalysisOptionsTab implements MDTab {
	private static Class<?>		PKG	= MDProfilerMeta.class;
	private MDProfilerHelper	helper;
	private Group				analysisGroup;
	private Group				setupGroup;
	private Button				ckSortAnalysis;
	private Button				ckMatchUpAnalysis;
	private Button				ckRightFielderAnalysis;
	private Button				ckDataAggregation;
	private Text				txTableName;
	private Text				txUserName;
	private Text				txJobName;
	private Text				txJobDescription;

	public AnalysisOptionsTab(MDProfilerDialog dialog) {
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
		// Description line
		Label description = new Label(wComp, SWT.LEFT | SWT.WRAP);
		helper.setLook(description);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(0, helper.margin * 5);
		description.setLayoutData(fd);
		Label optionsList = new Label(wComp, SWT.LEFT | SWT.WRAP);
		helper.setLook(optionsList);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin * 5);
		fd.top = new FormAttachment(description, helper.margin);
		optionsList.setLayoutData(fd);
		optionsList.setText(getString("OptionsList.Label"));
		Label setupOptions = new Label(wComp, SWT.LEFT | SWT.WRAP);
		helper.setLook(setupOptions);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(optionsList, helper.margin * 5);
		setupOptions.setLayoutData(fd);
		setupOptions.setText(getString("Setup.Label"));
		createAnaysisGroup(wComp, setupOptions);
		createSetupGroup(wComp, setupOptions);
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

	@Override
	public void advancedConfigChanged() {
	}

	private void createAnaysisGroup(Composite parent, Control last) {
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		analysisGroup = new Group(parent, SWT.NONE);
		analysisGroup.setText(getString("AalysisOptionsGroup.Label"));
		helper.setLook(analysisGroup);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin * 8);
		fd.right = new FormAttachment(30, -helper.margin);
		// fd.bottom = new FormAttachment(30, -helper.margin);
		analysisGroup.setLayoutData(fd);
		ckSortAnalysis = helper.addCheckBox(analysisGroup, null, "AnalysisOptionsTab.AnalysisGroup.SortAnalysis");
		ckMatchUpAnalysis = helper.addCheckBox(analysisGroup, ckSortAnalysis, "AnalysisOptionsTab.AnalysisGroup.MatchUpAnalysis");
		ckRightFielderAnalysis = helper.addCheckBox(analysisGroup, ckMatchUpAnalysis, "AnalysisOptionsTab.AnalysisGroup.RightFielderAnalysis");
		ckDataAggregation = helper.addCheckBox(analysisGroup, ckRightFielderAnalysis, "AnalysisOptionsTab.AnalysisGroup.DataAggregation");
		helper.addSpacer(analysisGroup, ckDataAggregation);
		analysisGroup.setLayout(fl);
	}

	private void createSetupGroup(Composite parent, Control last) {
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		setupGroup = new Group(parent, SWT.NONE);
		setupGroup.setText(getString("SetupOptionsGroup.Label"));
		helper.setLook(setupGroup);
		FormData fd = new FormData();
		fd.left = new FormAttachment(analysisGroup, helper.margin);
		fd.top = new FormAttachment(last, 8 * helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		setupGroup.setLayoutData(fd);
		txTableName = helper.addTextBox(setupGroup, null, "AnalysisOptionsTab.SetupGroup.TableName");
		txUserName = helper.addTextBox(setupGroup, txTableName, "AnalysisOptionsTab.SetupGroup.UserName");
		txJobName = helper.addTextBox(setupGroup, txUserName, "AnalysisOptionsTab.SetupGroup.JobName");
		txJobDescription = helper.addTextBox(setupGroup, txJobName, "AnalysisOptionsTab.SetupGroup.JobDescription");
		setupGroup.setLayout(fl);
	}

	@Override
	public void getData(MDProfilerMeta meta) {
		ProfilerFields pFields = meta.profilerFields;
		pFields.optionFields.get(ProfilerFields.TAG_OPTION_SORT).metaValue = ckSortAnalysis.getSelection() ? "1" : "0";
		pFields.optionFields.get(ProfilerFields.TAG_OPTION_MATCHUP).metaValue = ckMatchUpAnalysis.getSelection() ? "1" : "0";
		pFields.optionFields.get(ProfilerFields.TAG_OPTION_RIGHTFIELDER).metaValue = ckRightFielderAnalysis.getSelection() ? "1" : "0";
		pFields.optionFields.get(ProfilerFields.TAG_OPTION_DATA_AGGREGATION).metaValue = ckDataAggregation.getSelection() ? "1" : "0";
		pFields.optionFields.get(ProfilerFields.TAG_OPTION_TABLE_NAME).metaValue = txTableName.getText();
		pFields.optionFields.get(ProfilerFields.TAG_OPTION_USER_NAME).metaValue = txUserName.getText();
		pFields.optionFields.get(ProfilerFields.TAG_OPTION_JOB_NAME).metaValue = txJobName.getText();
		pFields.optionFields.get(ProfilerFields.TAG_OPTION_JOB_DESCRIPTION).metaValue = txJobDescription.getText();
	}

	@Override
	public String getHelpURLKey() {
		return BaseMessages.getString(PKG, "MDProfiler.Plugin.Help.AnalysisOptionTab");
	}

	private String getString(String key, String... args) {
		return BaseMessages.getString(PKG, "MDProfilerDialog.AnalysisOptionsTab." + key, args);
	}

	@Override
	public boolean init(MDProfilerMeta meta) {
		ProfilerFields pFields = meta.profilerFields;
		int optChoice = Integer.valueOf(pFields.optionFields.get(ProfilerFields.TAG_OPTION_SORT).metaValue);
		ckSortAnalysis.setSelection(optChoice == 1);
		optChoice = Integer.valueOf(pFields.optionFields.get(ProfilerFields.TAG_OPTION_MATCHUP).metaValue);
		ckMatchUpAnalysis.setSelection(optChoice == 1);
		optChoice = Integer.valueOf(pFields.optionFields.get(ProfilerFields.TAG_OPTION_RIGHTFIELDER).metaValue);
		ckRightFielderAnalysis.setSelection(optChoice == 1);
		optChoice = Integer.valueOf(pFields.optionFields.get(ProfilerFields.TAG_OPTION_DATA_AGGREGATION).metaValue);
		ckDataAggregation.setSelection(optChoice == 1);
		txTableName.setText(pFields.optionFields.get(ProfilerFields.TAG_OPTION_TABLE_NAME).metaValue);
		txUserName.setText(pFields.optionFields.get(ProfilerFields.TAG_OPTION_USER_NAME).metaValue);
		txJobName.setText(pFields.optionFields.get(ProfilerFields.TAG_OPTION_JOB_NAME).metaValue);
		txJobDescription.setText(pFields.optionFields.get(ProfilerFields.TAG_OPTION_JOB_DESCRIPTION).metaValue);
		return true;
	}
}
