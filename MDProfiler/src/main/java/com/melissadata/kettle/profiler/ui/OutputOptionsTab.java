package com.melissadata.kettle.profiler.ui;

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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.pentaho.di.core.Const;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.util.ImageUtil;

import com.melissadata.kettle.profiler.FilterTarget;
import com.melissadata.kettle.profiler.MDProfilerDialog;
import com.melissadata.kettle.profiler.MDProfilerMeta;
import com.melissadata.kettle.profiler.data.ProfilerEnum.AppendMode;
import com.melissadata.kettle.profiler.data.ProfilerEnum.OutputPin;
import com.melissadata.kettle.profiler.data.ProfilerFields;
import com.melissadata.kettle.profiler.support.MDProfilerHelper;
import com.melissadata.kettle.profiler.support.MDTab;

public class OutputOptionsTab implements MDTab {
	private static Class<?>		PKG	= MDProfilerMeta.class;
	private MDProfilerHelper	helper;
	private MDProfilerDialog	dialog;
	private TextVar				txOutputFile;
	private Button				ckAutoAppend;
	private Button				rAppend;
	private Button				rOverwrite;
	private Button				rCancel;
	private Label				lDateTimePin;
	private Label				lLenFreqPin;
	private Label				lOverAllPin;
	private Label				lPassThruPin;
	private Label				lRegExPin;
	private Label				lSessionPin;
	private Label				lSoundExPin;
	private Label				lValueFrqPin;
	private Label				lWordLenPin;
	private Label				lWordValPin;
	private Label				lColBasedPin;
	private Label				lDateTimeOut;
	private Label				lLenFreqOut;
	private Label				lOverAllOut;
	private Label				lPassThruOut;
	private Label				lRegExOut;
	private Label				lSessionOut;
	private Label				lSoundExOut;
	private Label				lValueFrqOut;
	private Label				lWordLenOut;
	private Label				lWordValOut;
	private Label				lColBasedOut;

	public OutputOptionsTab(MDProfilerDialog dialog) {
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
		// Description line
		Label description = new Label(wComp, SWT.LEFT | SWT.WRAP);
		helper.setLook(description);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		// fd.right = new FormAttachment(100, helper.margin);
		fd.top = new FormAttachment(0, helper.margin * 5);
		description.setLayoutData(fd);
		Label spacer = helper.addSpacer(wComp, description);
		Group setupGroup = new Group(wComp, SWT.NONE);
		setupGroup.setText(getString("OutputFileGroup.Label"));
		helper.setLook(setupGroup);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(spacer, 8 * helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		// fd.bottom = new FormAttachment(30, -helper.margin);
		setupGroup.setLayoutData(fd);
		setupGroup.setLayout(fl);
		helper.rightAttach = 90;
		txOutputFile = helper.addTextVarBox(setupGroup, null, "OutputTab.OutputFileGroup.OutputFile");
		helper.rightAttach = 100;
		Button btnBrowse = helper.addPushButton(setupGroup, null, null, new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browseOutput(txOutputFile);
			}
		});
		btnBrowse.setImage(ImageUtil.getImage(helper.getDisplay(), PKG, "com/melissadata/kettle/profiler/images/OpenFolder.gif"));
		fd = new FormData();
		fd.left = new FormAttachment(txOutputFile, helper.margin);
		fd.top = new FormAttachment(null, helper.margin);
		// fd.right = new FormAttachment(100, 0);
		// fd.bottom = new FormAttachment(100, 0);
		btnBrowse.setLayoutData(fd);
		Label sp1 = helper.addSpacer(setupGroup, txOutputFile);
		ckAutoAppend = helper.addCheckBox(setupGroup, sp1, "OutputTab.OutputFileGroup.AutoAppend");
		Label sp2 = helper.addSpacer(setupGroup, ckAutoAppend);
		Label lExists = helper.addLabel(setupGroup, sp2, "OutputTab.OutputFileGroup.IfExists");
		rAppend = helper.addRadioButton(setupGroup, lExists, "OutputTab.OutputFileGroup.Append");
		rOverwrite = helper.addRadioButton(setupGroup, rAppend, "OutputTab.OutputFileGroup.Overwrite");
		rCancel = helper.addRadioButton(setupGroup, rOverwrite, "OutputTab.OutputFileGroup.Cancel");
		createLabelGroup(wComp, setupGroup);
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

	/**
	 * Called to browse for a directory
	 *
	 * @param wFile
	 */
	private void browseOutput(TextVar wDir) {
		FileDialog fileChooser = new FileDialog(dialog.getShell(), SWT.OPEN);
		fileChooser.setFilterExtensions(new String[] { "*.prf" });
		String dataPath = ((wDir != null) && (wDir.getText() != null)) ? wDir.getText() : "";
		String oldPath = dialog.getSpace().environmentSubstitute(dataPath);
		fileChooser.setFilterPath(oldPath);
		fileChooser.setFileName(oldPath);
		if (fileChooser.open() != null) {
			String dirName = fileChooser.getFilterPath() + Const.FILE_SEPARATOR + fileChooser.getFileName();
			if (!dirName.equalsIgnoreCase(oldPath)) {
				wDir.setText(dirName);
			}
		}
	}

	private void createLabelGroup(Composite parent, Control last) {
		Group pinGroup = new Group(parent, SWT.NONE);
		pinGroup.setText(getString("PinsGroup.Label"));
		helper.setLook(pinGroup);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, 8 * helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		// fd.bottom = new FormAttachment(30, -helper.margin);
		pinGroup.setLayoutData(fd);
		pinGroup.setLayout(fl);
		last = null;
		Label lPinName = helper.addLabel(pinGroup, last, "PinHeader");
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin * 3);
		fd.right = new FormAttachment(25, -helper.margin);
		lPinName.setLayoutData(fd);
		Label lOutHeader = helper.addLabel(pinGroup, last, "PinTarget");
		fd = new FormData();
		fd.left = new FormAttachment(lPinName, helper.margin);
		fd.top = new FormAttachment(last, helper.margin * 3);
		lOutHeader.setLayoutData(fd);
		last = helper.addSpacer(pinGroup, lPinName);
		// DATE TIME
		lDateTimePin = helper.addLabel(pinGroup, last, "DateTimePin");
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin);
		fd.right = new FormAttachment(25, -helper.margin);
		lDateTimePin.setLayoutData(fd);
		lDateTimeOut = helper.addLabel(pinGroup, last, "DateTimePin");
		fd = new FormData();
		fd.left = new FormAttachment(25, helper.margin);
		fd.top = new FormAttachment(last, helper.margin);
		lDateTimeOut.setLayoutData(fd);
		// lDateTimeOut.setText("Where it goes");
		last = lDateTimePin;
		// Length Freq
		lLenFreqPin = helper.addLabel(pinGroup, last, "LenFreqPin");
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin + 2);
		fd.right = new FormAttachment(25, -helper.margin);
		lLenFreqPin.setLayoutData(fd);
		lLenFreqOut = helper.addLabel(pinGroup, last, "LenFreqPin");
		fd = new FormData();
		fd.left = new FormAttachment(25, helper.margin);
		fd.top = new FormAttachment(last, helper.margin + 2);
		lLenFreqOut.setLayoutData(fd);
		last = lLenFreqPin;
		// Over All
		lOverAllPin = helper.addLabel(pinGroup, last, "OverAllPin");
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin + 2);
		fd.right = new FormAttachment(25, -helper.margin);
		lOverAllPin.setLayoutData(fd);
		lOverAllOut = helper.addLabel(pinGroup, last, "OverAllPin");
		fd = new FormData();
		fd.left = new FormAttachment(25, helper.margin);
		fd.top = new FormAttachment(last, helper.margin + 2);
		lOverAllOut.setLayoutData(fd);
		last = lOverAllOut;
		// /////////////////////////////////////////////////
		// PASS THRU
		lPassThruPin = helper.addLabel(pinGroup, last, "PassThruPin");
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin + 2);
		fd.right = new FormAttachment(25, -helper.margin);
		lPassThruPin.setLayoutData(fd);
		lPassThruOut = helper.addLabel(pinGroup, last, "PassThruPin");
		fd = new FormData();
		fd.left = new FormAttachment(25, helper.margin);
		fd.top = new FormAttachment(last, helper.margin + 2);
		lPassThruOut.setLayoutData(fd);
		last = lPassThruPin;
		// REG EX
		lRegExPin = helper.addLabel(pinGroup, last, "RegExPin");
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin + 2);
		fd.right = new FormAttachment(25, -helper.margin);
		lRegExPin.setLayoutData(fd);
		lRegExOut = helper.addLabel(pinGroup, last, "RegExPin");
		fd = new FormData();
		fd.left = new FormAttachment(25, helper.margin);
		fd.top = new FormAttachment(last, helper.margin + 2);
		lRegExOut.setLayoutData(fd);
		last = lRegExPin;
		// SESSION
		lSessionPin = helper.addLabel(pinGroup, last, "SessionPin");
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin + 2);
		fd.right = new FormAttachment(25, -helper.margin);
		lSessionPin.setLayoutData(fd);
		lSessionOut = helper.addLabel(pinGroup, last, "SessionPin");
		fd = new FormData();
		fd.left = new FormAttachment(25, helper.margin);
		fd.top = new FormAttachment(last, helper.margin + 2);
		lSessionOut.setLayoutData(fd);
		last = lSessionPin;
		// SOUND EX
		lSoundExPin = helper.addLabel(pinGroup, last, "SoundExPin");
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin + 2);
		fd.right = new FormAttachment(25, -helper.margin);
		lSoundExPin.setLayoutData(fd);
		lSoundExOut = helper.addLabel(pinGroup, last, "SoundExPin");
		fd = new FormData();
		fd.left = new FormAttachment(25, helper.margin);
		fd.top = new FormAttachment(last, helper.margin + 2);
		lSoundExOut.setLayoutData(fd);
		last = lSoundExPin;
		// VAL FRQ
		lValueFrqPin = helper.addLabel(pinGroup, last, "ValueFrqPin");
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin + 2);
		fd.right = new FormAttachment(25, -helper.margin);
		lValueFrqPin.setLayoutData(fd);
		lValueFrqOut = helper.addLabel(pinGroup, last, "ValueFrqPin");
		fd = new FormData();
		fd.left = new FormAttachment(25, helper.margin);
		fd.top = new FormAttachment(last, helper.margin + 2);
		lValueFrqOut.setLayoutData(fd);
		last = lValueFrqOut;
		// WORD LEN
		lWordLenPin = helper.addLabel(pinGroup, last, "WordLenPin");
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin + 2);
		fd.right = new FormAttachment(25, -helper.margin);
		lWordLenPin.setLayoutData(fd);
		lWordLenOut = helper.addLabel(pinGroup, last, "WordLenPin");
		fd = new FormData();
		fd.left = new FormAttachment(25, helper.margin);
		fd.top = new FormAttachment(last, helper.margin + 2);
		lWordLenOut.setLayoutData(fd);
		last = lWordLenPin;
		// WORD VAL
		lWordValPin = helper.addLabel(pinGroup, last, "WordValPin");
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin + 2);
		fd.right = new FormAttachment(25, -helper.margin);
		lWordValPin.setLayoutData(fd);
		lWordValOut = helper.addLabel(pinGroup, last, "WordValPin");
		fd = new FormData();
		fd.left = new FormAttachment(25, helper.margin);
		fd.top = new FormAttachment(last, helper.margin + 2);
		lWordValOut.setLayoutData(fd);
		last = lWordValPin;
		// COL Base
		lColBasedPin = helper.addLabel(pinGroup, last, "ColBasedPin");
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin + 2);
		fd.right = new FormAttachment(25, -helper.margin);
		lColBasedPin.setLayoutData(fd);
		lColBasedOut = helper.addLabel(pinGroup, last, "ColBasedPin");
		fd = new FormData();
		fd.left = new FormAttachment(25, helper.margin);
		fd.top = new FormAttachment(last, helper.margin + 2);
		lColBasedOut.setLayoutData(fd);
		last = lColBasedPin;
	}

	@Override
	public void getData(MDProfilerMeta meta) {
		ProfilerFields pFields = meta.profilerFields;
		pFields.optionFields.get(ProfilerFields.TAG_OPTION_OUTPUTFILE).metaValue = txOutputFile.getText().endsWith(".prf") ? txOutputFile.getText() : txOutputFile.getText() + ".prf";
		pFields.optionFields.get(ProfilerFields.TAG_OPTION_APPEND_DATE).metaValue = String.valueOf(ckAutoAppend.getSelection());
		if (rAppend.getSelection()) {
			meta.setAppendMode(AppendMode.APPEND);
		} else if (rOverwrite.getSelection()) {
			meta.setAppendMode(AppendMode.OVERWRITE);
		} else if (rCancel.getSelection()) {
			meta.setAppendMode(AppendMode.MUST_NOT_EXIST);
		}
	}

	@Override
	public String getHelpURLKey() {
		return BaseMessages.getString(PKG, "MDProfiler.Plugin.Help.OutputOptionsTab");
	}

	private String getString(String key, String... args) {
		return BaseMessages.getString(PKG, "MDProfilerDialog.OutputTab." + key, args);
	}

	@Override
	public boolean init(MDProfilerMeta meta) {
		ProfilerFields pFields = meta.profilerFields;
		meta.checkTarget("");
		String outFile = pFields.optionFields.get(ProfilerFields.TAG_OPTION_OUTPUTFILE).metaValue;
		txOutputFile.setText(outFile);
		ckAutoAppend.setSelection(Boolean.valueOf(pFields.optionFields.get(ProfilerFields.TAG_OPTION_APPEND_DATE).metaValue));
		int appendVal = meta.getAppendMode().getSwigValue();
		switch (appendVal) {
			case 1:
				rAppend.setSelection(true);
				break;
			case 2:
				rOverwrite.setSelection(true);
				break;
			case 4:
				rCancel.setSelection(true);
				break;
		}
		// Target Stream
		FilterTarget ft = meta.oFilterFields.filterTargets.get(OutputPin.DATE_TIME_FREQUENCIES.name());
		String stString = ft.getTargetStep() != null ? ft.getTargetStepname() : "-";
		lDateTimeOut.setText(stString);
		ft = meta.oFilterFields.filterTargets.get(OutputPin.LENGTH_FREQUENCIES.name());
		stString = ft.getTargetStep() != null ? ft.getTargetStepname() : "-";
		lLenFreqOut.setText(stString);
		ft = meta.oFilterFields.filterTargets.get(OutputPin.OVER_ALL_COUNTS.name());
		stString = ft.getTargetStep() != null ? ft.getTargetStepname() : "-";
		lOverAllOut.setText(stString);
		ft = meta.oFilterFields.filterTargets.get(OutputPin.PASSTHRU_RESULTCODE.name());
		stString = ft.getTargetStep() != null ? ft.getTargetStepname() : "-";
		lPassThruOut.setText(stString);
		ft = meta.oFilterFields.filterTargets.get(OutputPin.PATTERN_REGEX_FREQUENCIES.name());
		stString = ft.getTargetStep() != null ? ft.getTargetStepname() : "-";
		lRegExOut.setText(stString);
		ft = meta.oFilterFields.filterTargets.get(OutputPin.PROFILE_SESSION.name());
		stString = ft.getTargetStep() != null ? ft.getTargetStepname() : "-";
		lSessionOut.setText(stString);
		ft = meta.oFilterFields.filterTargets.get(OutputPin.SOUND_ALIKE_FREQUENCIES.name());
		stString = ft.getTargetStep() != null ? ft.getTargetStepname() : "-";
		lSoundExOut.setText(stString);
		ft = meta.oFilterFields.filterTargets.get(OutputPin.VALUE_FREQUENCIES.name());
		stString = ft.getTargetStep() != null ? ft.getTargetStepname() : "-";
		lValueFrqOut.setText(stString);
		ft = meta.oFilterFields.filterTargets.get(OutputPin.WORD_LENGTH_FREQUENCIES.name());
		stString = ft.getTargetStep() != null ? ft.getTargetStepname() : "-";
		lWordLenOut.setText(stString);
		ft = meta.oFilterFields.filterTargets.get(OutputPin.WORD_VALUE_FREQUENCIES.name());
		stString = ft.getTargetStep() != null ? ft.getTargetStepname() : "-";
		lWordValOut.setText(stString);
		ft = meta.oFilterFields.filterTargets.get(OutputPin.COLUMN_BASED_COUNTS.name());
		stString = ft.getTargetStep() != null ? ft.getTargetStepname() : "-";
		lColBasedOut.setText(stString);
		return true;
	}
}
