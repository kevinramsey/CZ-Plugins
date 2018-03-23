package com.melissadata.kettle.globalverify.ui;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.ui.core.widget.TextVar;

import com.melissadata.cz.ui.MDInputCombo;
import com.melissadata.kettle.globalverify.MDGlobalDialog;
import com.melissadata.kettle.globalverify.MDGlobalMeta;
import com.melissadata.kettle.globalverify.data.EmailFields;
import com.melissadata.kettle.globalverify.support.MDGlobalAddressHelper;
import com.melissadata.kettle.globalverify.support.MDTab;

public class EmailTab implements MDTab {
	private static Class<?>			PKG	= MDGlobalMeta.class;
	private MDGlobalDialog			dialog;
	private MDGlobalAddressHelper	helper;
	private Group					gOutput;
	private Text					wOutEmail;
	private Text					wOutMailBox;
	private Text					wOutDomain;
	private Text					wOutTopDomain;
	private Text					wOutDomainDescription;
	private Text					wOutDateChecked;
	private Group					gOptions;
	//private Group					gAdvanced;
	private Button					bFast;
	private Button					bRealTime;
	//private Text					wDays;
	private Group					gInput;
	private MDInputCombo			wInEmail;

	public EmailTab(MDGlobalDialog dialog) {
		this.dialog = dialog;
		helper = dialog.getHelper();
		// Create the tab
		CTabItem wTab = new CTabItem(dialog.getTabFolder(), SWT.NONE);
		dialog.getTabFolder().addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				enable();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				enable();
			}
		});
		wTab.setText(getString("TabTitle"));
		wTab.setData(this);
		dialog.getTabFolder().getItems();
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
		createInput(wComp, description);
		createOutput(wComp, description);
		createOptions(wComp, gInput);
		//createAdvancedOptions(wComp, gOptions);
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

	private void createInput(Composite parent, Control last) {
		gInput = new Group(parent, SWT.NONE);
		helper.setLook(gInput);
		FormLayout fl = new FormLayout();
		fl.marginHeight = helper.margin;
		fl.marginWidth = helper.margin;
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin);
		fd.right = new FormAttachment(50, -helper.margin);
		// fd.bottom = new FormAttachment(100, -helper.margin);
		gInput.setLayout(fl);
		gInput.setLayoutData(fd);
		gInput.setText(getString("InputGroup.Name"));
		wInEmail = helper.addInputComboBox(gInput, null, "EmailTab.InputGroup.Email", "NInEmail");
		helper.setLook(wInEmail.getComboBox());
		wInEmail.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// not used
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				enable();
			}
		});
	}

	private void createOutput(Composite parent, Control last) {
		gOutput = new Group(parent, SWT.NONE);
		helper.setLook(gOutput);
		FormLayout fl = new FormLayout();
		fl.marginHeight = helper.margin;
		fl.marginWidth = helper.margin;
		FormData fd = new FormData();
		fd.left = new FormAttachment(50, helper.margin);
		fd.top = new FormAttachment(last, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		// fd.bottom = new FormAttachment(100, -helper.margin);
		gOutput.setLayout(fl);
		gOutput.setLayoutData(fd);
		gOutput.setText(getString("OutputGroup.Name"));
		wOutEmail = helper.addTextBox(gOutput, null, "EmailTab.OutputGroup.Email");
		wOutMailBox = helper.addTextBox(gOutput, wOutEmail, "EmailTab.OutputGroup.MailBox");
		wOutDomain = helper.addTextBox(gOutput, wOutMailBox, "EmailTab.OutputGroup.Domain");
		wOutTopDomain = helper.addTextBox(gOutput, wOutDomain, "EmailTab.OutputGroup.TopDomain");
		wOutDomainDescription = helper.addTextBox(gOutput, wOutTopDomain, "EmailTab.OutputGroup.DomainDescription");
		wOutDateChecked = helper.addTextBox(gOutput, wOutDomainDescription, "EmailTab.OutputGroup.DateChecked");
	}

	private void createOptions(Composite parent, Control last) {
		gOptions = new Group(parent, SWT.NONE);
		helper.setLook(gOptions);
		FormLayout fl = new FormLayout();
		fl.marginHeight = helper.margin;
		fl.marginWidth = helper.margin;
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin);
		fd.right = new FormAttachment(50, -helper.margin);
		// fd.bottom = new FormAttachment(100, -helper.margin);
		gOptions.setLayout(fl);
		gOptions.setLayoutData(fd);
		gOptions.setText(getString("OptionsGroup.Name"));

		bFast = helper.addRadioButton(gOptions, null, null);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(null, helper.margin);
		// fd.right = new FormAttachment(100, -helper.margin);
		// fd.bottom = new FormAttachment(100, -helper.margin);
		bFast.setLayoutData(fd);
		Label lFast = new Label(gOptions, SWT.NONE);
		fd = new FormData();
		fd.left = new FormAttachment(bFast, helper.margin);
		fd.top = new FormAttachment(null, helper.margin);
	//	fd.right = new FormAttachment(50, -helper.margin);
		// fd.bottom = new FormAttachment(100, -helper.margin);
		lFast.setLayoutData(fd);
		lFast.setText(getString("OptionsGroup.Fast.Label"));
		helper.setLook(lFast);
		Label spacer = helper.addSpacer(gOptions, lFast);

		bRealTime = helper.addRadioButton(gOptions, spacer, null/* "EmailTab.OptionsGroup.Off" */);
		fd = new FormData();
		fd.left = new FormAttachment(lFast, helper.margin);
		fd.top = new FormAttachment(null, helper.margin);
		// fd.right = new FormAttachment(100, -helper.margin);
		// fd.bottom = new FormAttachment(100, -helper.margin);
		bRealTime.setLayoutData(fd);
		Label lRealTime = new Label(gOptions, SWT.NONE);
		fd = new FormData();
		fd.left = new FormAttachment(bRealTime, helper.margin);
		fd.top = new FormAttachment(null, helper.margin);
	//	fd.right = new FormAttachment(100, -helper.margin);
		// fd.bottom = new FormAttachment(100, -helper.margin);
		lRealTime.setLayoutData(fd);
		lRealTime.setText(getString("OptionsGroup.Realtime.Label"));
		helper.setLook(lRealTime);
		bRealTime.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				enable();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				enable();
			}
		});
		bFast.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				enable();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				enable();
			}
		});
	}

	private void createAdvancedOptions(Composite parent, Control last) {
//		gAdvanced = new Group(parent, SWT.NONE);
//		helper.setLook(gAdvanced);
//		FormLayout fl = new FormLayout();
//		fl.marginHeight = helper.margin;
//		fl.marginWidth = helper.margin;
//		FormData fd = new FormData();
//		fd.left = new FormAttachment(0, helper.margin);
//		fd.top = new FormAttachment(last, helper.margin);
//		fd.right = new FormAttachment(100, -helper.margin);
//		// fd.bottom = new FormAttachment(100, -helper.margin);
//		gAdvanced.setLayout(fl);
//		gAdvanced.setLayoutData(fd);
//		gAdvanced.setText(getString("AdvacedOptionsGroup.Name"));
//		Label lDays = new Label(gAdvanced, SWT.NONE);
//		fd = new FormData();
//		fd.left = new FormAttachment(10, helper.margin);
//		fd.top = new FormAttachment(null, 25);
//		// fd.right = new FormAttachment(100, -helper.margin);
//		// fd.bottom = new FormAttachment(100, -helper.margin);
//		lDays.setLayoutData(fd);
//		helper.setLook(lDays);
//		lDays.setText(getString("AdvacedOptionsGroup.DaysSince.Label"));
//		wDays = new Text(gAdvanced, SWT.SINGLE | SWT.LEFT | SWT.BORDER);// "EmailTab.AdvacedOptionsGroup.DaysSince"
//		fd = new FormData();
//		fd.left = new FormAttachment(lDays, helper.margin);
//		fd.top = new FormAttachment(null, 25);
//		fd.right = new FormAttachment(30, -helper.margin);
//		// fd.bottom = new FormAttachment(100, -helper.margin);
//		wDays.setLayoutData(fd);
//		helper.setLook(wDays);
//		wDays.addModifyListener(new ModifyListener() {
//			
//			@Override
//			public void modifyText(ModifyEvent arg0) {
//				validateDays();
//				enable();
//				dialog.setChanged();
//			}
//		});
	}

	public void validateDays() {
//		String msg = "";
//		int dd = 0;
//		if (!Const.isEmpty(wDays.getText())) {
//			try {
//				dd = Integer.parseInt(wDays.getText());
//				if (dd > 365) {
//					msg = "Must be a number between 3 and 365";
//				}
//			} catch (NumberFormatException nfe) {
//				msg = "Must be a number between 3 and 365";
//			}
//		}
//		if (!Const.isEmpty(msg)) {
//			System.out.println(msg);
//			wDays.setText("3");
//			wDays.setFocus();
//			wDays.selectAll();
//			MessageDialog.openError(dialog.getShell(), dialog.getTitle(), "Must be a number between 3 and 365");
//		}
	}

	/**
	 * Loads the meta data into the dialog tab
	 * 
	 * @param meta
	 * @return
	 */
	@Override
	public boolean init(MDGlobalMeta meta) {
		EmailFields eFields = meta.getEmailMeta().emailFields;
		wInEmail.setValue(eFields.inputFields.get(EmailFields.TAG_INPUT_EMAIL).metaValue);
		wOutEmail.setText(eFields.outputFields.get(EmailFields.TAG_OUTPUT_EMAIL).metaValue);
		wOutMailBox.setText(eFields.outputFields.get(EmailFields.TAG_OUTPUT_BOX_NAME).metaValue);
		wOutDomain.setText(eFields.outputFields.get(EmailFields.TAG_OUTPUT_DOMAIN).metaValue);
		wOutTopDomain.setText(eFields.outputFields.get(EmailFields.TAG_OUTPUT_TOP_LEVEL_DOMAIN).metaValue);
		wOutDomainDescription.setText(eFields.outputFields.get(EmailFields.TAG_OUTPUT_DOMAIN_DESCRIPTION).metaValue);
		wOutDateChecked.setText(eFields.outputFields.get(EmailFields.TAG_OUTPUT_DATE_CHECKED).metaValue);
		
		bFast.setSelection(eFields.optionFields.get(EmailFields.TAG_OPTION_VERIFY_MAIBOX).metaValue.equals("Express"));
		bRealTime.setSelection(eFields.optionFields.get(EmailFields.TAG_OPTION_VERIFY_MAIBOX).metaValue.equals("Premium"));
		//wDays.setText(eFields.webOptionFields.get(EmailFields.TAG_OPTION_DAYS_SINCE).metaValue);
		return false;
	}

	/**
	 * Loads the dialog data into the meta structure
	 */
	@Override
	public void getData(MDGlobalMeta meta) {
		EmailFields eFields = meta.getEmailMeta().emailFields;
		eFields.inputFields.get(EmailFields.TAG_INPUT_EMAIL).metaValue = wInEmail.getValue();
		eFields.outputFields.get(EmailFields.TAG_OUTPUT_EMAIL).metaValue = wOutEmail.getText();
		eFields.outputFields.get(EmailFields.TAG_OUTPUT_BOX_NAME).metaValue = wOutMailBox.getText();
		eFields.outputFields.get(EmailFields.TAG_OUTPUT_DOMAIN).metaValue = wOutDomain.getText();
		eFields.outputFields.get(EmailFields.TAG_OUTPUT_TOP_LEVEL_DOMAIN).metaValue = wOutTopDomain.getText();
		eFields.outputFields.get(EmailFields.TAG_OUTPUT_DOMAIN_DESCRIPTION).metaValue = wOutDomainDescription.getText();
		eFields.outputFields.get(EmailFields.TAG_OUTPUT_DATE_CHECKED).metaValue = wOutDateChecked.getText();
		
		eFields.optionFields.get(EmailFields.TAG_OPTION_VERIFY_MAIBOX).metaValue = bFast.getSelection() ? "Express" : "Premium";
		//eFields.webOptionFields.get(EmailFields.TAG_OPTION_DAYS_SINCE).metaValue = wDays.getText();
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
	 * @see com.melissadata.kettle.MDTab#advancedConfigChanged()
	 */
	@Override
	public void advancedConfigChanged() {
		// Update enablement
		enable();
	}

	/**
	 * Called to handle enablement of controls based on input settings
	 */
	private void enable() {
		boolean isLicensed = dialog.getInput().getEmailMeta().isLicensed();
		if (!isLicensed) {
			gInput.setText(getString("NotLicensed.Label"));
			gOutput.setText(getString("NotLicensed.Label"));
			gOptions.setText(getString("NotLicensed.Label"));
			for (Control child : gInput.getChildren()) {
				child.setEnabled(false);
			}
			for (Control child : gOutput.getChildren()) {
				child.setEnabled(false);
			}
			for (Control child : gOptions.getChildren()) {
				child.setEnabled(false);
			}
			return;
		}
		if (Const.isEmpty(wInEmail.getValue())) {
			gInput.setText(getString("InputGroup.Name"));
			gOutput.setText(getString("MissingInput.Label"));
			gOptions.setText(getString("MissingInput.Label"));
			for (Control child : gInput.getChildren()) {
				child.setEnabled(true);
			}
			for (Control child : gOutput.getChildren()) {
				child.setEnabled(false);
			}
			for (Control child : gOptions.getChildren()) {
				child.setEnabled(false);
			}
			return;
		}
		gInput.setText(getString("InputGroup.Name"));
		gOutput.setText(getString("OutputGroup.Name"));
		gOptions.setText(getString("OptionsGroup.Name"));
		for (Control child : gInput.getChildren()) {
			child.setEnabled(true);
		}
		for (Control child : gOutput.getChildren()) {
			child.setEnabled(true);
		}
		for (Control child : gOptions.getChildren()) {
			child.setEnabled(true);
		}
//		if (bFast.getSelection()) {
//			wDays.setEnabled(false);
//		} else {
//			wDays.setEnabled(true);
//		}
		return;
	}

	/**
	 * @param key
	 * @param args
	 * @return
	 */
	private String getString(String key, String... args) {
		return BaseMessages.getString(PKG, "MDGlobalDialog.EmailTab." + key, args);
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDTab#getHelpURLKey()
	 */
	@Override
	public String getHelpURLKey() {
		return BaseMessages.getString(PKG,  "MDGlobalVerify.Help.EmailTab");
	}
}
