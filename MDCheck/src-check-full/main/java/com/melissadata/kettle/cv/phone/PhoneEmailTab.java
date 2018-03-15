package com.melissadata.kettle.cv.phone;

import com.melissadata.cz.ui.MDAbstractDialog;
import com.melissadata.cz.ui.MDInputCombo;
import com.melissadata.kettle.*;
import com.melissadata.cz.support.MDPropTags;
import com.melissadata.kettle.cv.email.EmailVerifyMeta;
import com.melissadata.kettle.cv.MDCheckFullDialog;
import com.melissadata.kettle.cv.email.EmailVerifyOptionsDialog;
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
import org.pentaho.di.core.Const;
import org.pentaho.di.i18n.BaseMessages;

public class PhoneEmailTab implements MDTab {
	private static Class<?> PKG = MDCheckFullDialog.class;
	private MDCheckDialog   dialog;
	private MDCheckHelper   helper;
	private MDCheckStepData mdcStepData;
	private EmailVerifyMeta evMeta;
	private PhoneVerifyMeta pvMeta;
	private boolean         doPhone;
	private boolean         doEmail;
	// Phone stuff
	private Group           gPhoneInput;
	private MDInputCombo    wInputPhone;
	private Group           gPhoneOutput;
	private Text            wOutputPhone;
	private ComboViewer     vOutputFormat;
	private Text            wOutputAreaCode;
	private Text            wOutputPrefix;
	private Text            wOutputSuffix;
	private Text            wOutputExtension;
	private Button          bPhoneOutputOptions;
	// Email stuff
	private Group           gEmailInput;
	private MDInputCombo    wInputEmail;
	private Group           gEmailOutput;
	private Text            wOutputEmail;
	private Button          bEmailOptions;

	public PhoneEmailTab(MDCheckDialog dialog) {
		this.dialog = dialog;
		helper = dialog.getHelper();
		mdcStepData = dialog.getData();
		// Determine what to display
		doPhone = ((dialog.getCheckTypes() & MDCheckMeta.MDCHECK_PHONE) != 0);
		doEmail = ((dialog.getCheckTypes() & MDCheckMeta.MDCHECK_EMAIL) != 0);
		// Get a naming prefix
		String namingPrefix;
		if (doPhone && !doEmail) {
			namingPrefix = "Phone.";
		} else if (doEmail && !doPhone) {
			namingPrefix = "Email.";
		} else {
			namingPrefix = "Both.";
		}
		// Create the tab
		CTabItem wTab = new CTabItem(dialog.getTabFolder(), SWT.NONE);
		wTab.setText(getString(namingPrefix + "TabTitle"));
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
		// Fit the composite within its container (the scrolled composite)
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(0, 0);
		fd.right = new FormAttachment(100, 0);
		fd.bottom = new FormAttachment(100, 0);
		wComp.setLayoutData(fd);
		// Description line
		final Label description = new Label(wComp, SWT.LEFT | SWT.WRAP);
		description.setText(getString(namingPrefix + "Description"));
		helper.setLook(description);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(0, helper.margin);
		description.setLayoutData(fd);
		// Create a composite that the two columns will be placed in
		Composite wPEComp = new Composite(wComp, SWT.NONE);
		helper.setLook(wPEComp);
		wPEComp.setLayout(new FormLayout());
		fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(description, 4 * helper.margin);
		fd.right = new FormAttachment(100, 0);
		wPEComp.setLayoutData(fd);
		// Create phone column
		if (doPhone) {
			Composite wPhoneComp = new Composite(wPEComp, 0);
			helper.setLook(wPhoneComp);
			wPhoneComp.setLayout(new FormLayout());
			fd = new FormData();
			fd.top = new FormAttachment(0, 0);
			fd.left = new FormAttachment(0, 0);
			fd.right = new FormAttachment((doEmail ? 50 : 100), 0);
			fd.bottom = new FormAttachment(100, 0);
			wPhoneComp.setLayoutData(fd);
			// Create I/O groups in the phone column
			gPhoneInput = new Group(wPhoneComp, SWT.NONE);
			gPhoneInput.setText(getString("InputPhoneGroup.Label"));
			helper.setLook(gPhoneInput);
			fl = new FormLayout();
			fl.marginHeight = helper.margin;
			fl.marginWidth = helper.margin;
			gPhoneInput.setLayout(fl);
			fd = new FormData();
			fd.left = new FormAttachment(0, helper.margin);
			fd.top = new FormAttachment(0, helper.margin);
			fd.right = new FormAttachment(100, -helper.margin / 2);
			gPhoneInput.setLayoutData(fd);
			gPhoneOutput = new Group(wPhoneComp, SWT.NONE);
			gPhoneOutput.setText(getString("OutputPhoneGroup.Label"));
			helper.setLook(gPhoneOutput);
			fl = new FormLayout();
			fl.marginHeight = helper.margin;
			fl.marginWidth = helper.margin;
			gPhoneOutput.setLayout(fl);
			fd = new FormData();
			fd.left = new FormAttachment(0, helper.margin);
			fd.top = new FormAttachment(gPhoneInput, helper.margin);
			fd.right = new FormAttachment(100, -helper.margin / 2);
			fd.bottom = new FormAttachment(100, -helper.margin);
			gPhoneOutput.setLayoutData(fd);
			// Add fields to the input phone group
			wInputPhone = helper.addInputComboBox(gPhoneInput, null, "PhoneEmailTab.InputPhone", "PInPhoneNumber");
			// Add fields to the output phone group
			wOutputPhone = helper.addTextBox(gPhoneOutput, null, "PhoneEmailTab.OutputPhone");
			// Add a combo selector for the output phone format
			vOutputFormat = helper.addEnumComboBox(gPhoneOutput, wOutputPhone, "PhoneEmailTab.OutputPhoneFormat", PhoneVerifyMeta.OutputPhoneFormat.values());
			// Add the rest of the standard output fields
			wOutputAreaCode = helper.addTextBox(gPhoneOutput, vOutputFormat.getCCombo(), 4 * helper.margin, "PhoneEmailTab.OutputAreaCode");
			wOutputPrefix = helper.addTextBox(gPhoneOutput, wOutputAreaCode, "PhoneEmailTab.OutputPrefix");
			wOutputSuffix = helper.addTextBox(gPhoneOutput, wOutputPrefix, "PhoneEmailTab.OutputSuffix");
			wOutputExtension = helper.addTextBox(gPhoneOutput, wOutputSuffix, "PhoneEmailTab.OutputExtension");
			// Phone options button
			bPhoneOutputOptions = helper.addPushButton(gPhoneOutput, wOutputExtension, "PhoneEmailTab.PhoneOutputButton", new SelectionAdapter() {
				@Override public void widgetSelected(SelectionEvent e) {
					phoneOutputOptions();
				}
			});
			bPhoneOutputOptions.setToolTipText(getString("PhoneOptions.ToolTip"));
		}
		// Create email column
		Composite wEmailComp;
		if (doEmail) {
			wEmailComp = new Composite(wPEComp, 0);
			helper.setLook(wEmailComp);
			wEmailComp.setLayout(new FormLayout());
			fd = new FormData();
			fd.top = new FormAttachment(0, 0);
			fd.left = new FormAttachment((doPhone ? 50 : 0), 0);
			fd.right = new FormAttachment(100, 0);
			fd.bottom = new FormAttachment(100, 0);
			wEmailComp.setLayoutData(fd);
			// Create I/O groups in the email column
			gEmailInput = new Group(wEmailComp, SWT.NONE);
			gEmailInput.setText(getString("InputEmailGroup.Label"));
			helper.setLook(gEmailInput);
			fl = new FormLayout();
			fl.marginHeight = helper.margin;
			fl.marginWidth = helper.margin;
			gEmailInput.setLayout(fl);
			fd = new FormData();
			fd.left = new FormAttachment(0, helper.margin);
			fd.top = new FormAttachment(0, helper.margin);
			fd.right = new FormAttachment(100, -helper.margin / 2);
			gEmailInput.setLayoutData(fd);
			gEmailOutput = new Group(wEmailComp, SWT.NONE);
			gEmailOutput.setText(getString("OutputEmailGroup.Label"));
			helper.setLook(gEmailOutput);
			fl = new FormLayout();
			fl.marginHeight = helper.margin;
			fl.marginWidth = helper.margin;
			gEmailOutput.setLayout(fl);
			fd = new FormData();
			fd.left = new FormAttachment(0, helper.margin);
			fd.top = new FormAttachment(gEmailInput, helper.margin);
			fd.right = new FormAttachment(100, -helper.margin / 2);
			fd.bottom = new FormAttachment(100, -helper.margin);
			gEmailOutput.setLayoutData(fd);
			// Add fields to the input email group
			wInputEmail = helper.addInputComboBox(gEmailInput, null, "PhoneEmailTab.InputEmail", "EInEmail");
			// Add fields to the output email group
			wOutputEmail = helper.addTextBox(gEmailOutput, null, "PhoneEmailTab.OutputEmail");
			// Address verify options button (local service only)
			bEmailOptions = helper.addPushButton(gEmailOutput, wOutputEmail, "PhoneEmailTab.EmailOptions", new SelectionAdapter() {
				@Override public void widgetSelected(SelectionEvent e) {
					emailOptions();
				}
			});
			bEmailOptions.setToolTipText(getString("EmailOptions.ToolTip"));
			// Shift button so that it aligns with the button in the phone tab
			if (doPhone) {
				((FormData) bEmailOptions.getLayoutData()).top = null;
				((FormData) bEmailOptions.getLayoutData()).bottom = new FormAttachment(100, -helper.margin / 2);
			}
		}
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

		PhoneVerifyMeta phoneMeta = data.getPhoneVerify();
		if (phoneMeta != null) {
			// Fill in the input phone meta data
			phoneMeta.setInputPhone(wInputPhone.getValue());
			// Fill in the output phone meta data
			phoneMeta.setOutputPhone(wOutputPhone.getText());
			phoneMeta.setOptionFormat((PhoneVerifyMeta.OutputPhoneFormat) (((IStructuredSelection) vOutputFormat.getSelection()).getFirstElement()));
			phoneMeta.setOutputAreaCode(wOutputAreaCode.getText());
			phoneMeta.setOutputPrefix(wOutputPrefix.getText());
			phoneMeta.setOutputSuffix(wOutputSuffix.getText());
			phoneMeta.setOutputExtension(wOutputExtension.getText());
		}

		EmailVerifyMeta emailMeta = data.getEmailVerify();
		if (emailMeta != null) {
			// Fill in the input email meta data
			emailMeta.setInputEmail(wInputEmail.getValue());
			// Fill in the output email meta data
			emailMeta.setOutputEmail(wOutputEmail.getText());
		}
	}

	/**
	 * @return The URL for help information
	 */
	public String getHelpURLKey() {
		return "MDCheck.Help.PhoneEmailTab";
	}

	public String getInputEmail() {
		return wInputEmail.getValue();
	}

	public String getInputPhone() {
		return wInputPhone.getValue();
	}

	/**
	 * Loads the meta data into the dialog tab
	 *
	 * @param data
	 * @return
	 */
	public boolean init(MDCheckStepData data) {
		// Get the phone verify meta data (if enabled)
		mdcStepData = data;
		pvMeta = mdcStepData.getPhoneVerify();
		evMeta = mdcStepData.getEmailVerify();
		if (pvMeta != null) {
			// If there is a problem with the data then reset it
			if (!pvMeta.isInitializeOK() || !pvMeta.isLicensed()) {
				wInputPhone.setValue("");
			} else {
				// Load the phone input controls
				wInputPhone.setValue(pvMeta.getInputPhone());
			}
			// Load the phone output controls
			wOutputPhone.setText(pvMeta.getOutputPhone());
			vOutputFormat.setSelection(new StructuredSelection(pvMeta.getOptionFormat()));
			wOutputAreaCode.setText(pvMeta.getOutputAreaCode());
			wOutputPrefix.setText(pvMeta.getOutputPrefix());
			wOutputSuffix.setText(pvMeta.getOutputSuffix());
			wOutputExtension.setText(pvMeta.getOutputExtension());
			// Set initial enablement
			phoneEnable();
			// Add listener to track enablement
			wInputPhone.addSelectionListener(new SelectionAdapter() {
				@Override public void widgetSelected(SelectionEvent event) {
					phoneEnable();
				}
			});
		}
		// Get the email verify meta data (if enabled)
		if (evMeta != null) {
			// If the configuration has problems then reset it
			if (!evMeta.isInitializeOK() || !evMeta.isLicensed()) {
				wInputEmail.setValue("");
			} else {
				// Load the email input controls
				wInputEmail.setValue(evMeta.getInputEmail());
			}
			// Load the phone output controls
			wOutputEmail.setText(evMeta.getOutputEmail());
			// Set initial enablement
			emailEnable();
			// Add listener to track enablement
			wInputEmail.getComboBox().addSelectionListener(new SelectionAdapter() {
				@Override public void widgetSelected(SelectionEvent arg0) {
					emailEnable();
				}
			});
		}
		return false;
	}

	/**
	 * Called to handle enablement of controls based on input settings
	 */
	private void emailEnable() {
		// Ignore if already disposed
		if (wInputEmail.getComboBox().isDisposed()) {
			return;
		}
		// See if email verification is enabled
		boolean isLicensed = evMeta.isLicensed();
		// Check all inputs to see if any are set
		boolean noInputFields = Const.isEmpty(wInputEmail.getValue());
		// Update display based on enabled features
		if (!isLicensed) {
			gEmailInput.setText(getString("EmailObjNotLicensed.Label"));
			for (Control child : gEmailInput.getChildren()) {
				child.setEnabled(false);
			}
			gEmailOutput.setText(getString("EmailObjNotLicensed.Label"));
			for (Control child : gEmailOutput.getChildren()) {
				child.setEnabled(false);
			}
		} else if (!evMeta.isInitializeOK()) {
			gEmailInput.setText(getString("EmailObjNotInitialized.Label"));
			for (Control child : gEmailInput.getChildren()) {
				child.setEnabled(false);
			}
			gEmailOutput.setText(getString("NoInputEmailSpecified.Label"));
			for (Control child : gEmailOutput.getChildren()) {
				child.setEnabled(false);
			}
		} else if (noInputFields) {
			gEmailInput.setText(getString("InputEmailGroup.Label"));
			for (Control child : gEmailInput.getChildren()) {
				child.setEnabled(true);
			}
			gEmailOutput.setText(getString("NoInputEmailSpecified.Label"));
			for (Control child : gEmailOutput.getChildren()) {
				child.setEnabled(false);
			}
		} else {
			gEmailInput.setText(getString("InputEmailGroup.Label"));
			for (Control child : gEmailInput.getChildren()) {
				child.setEnabled(true);
			}
			gEmailOutput.setText(getString("OutputEmailGroup.Label"));
			for (Control child : gEmailOutput.getChildren()) {
				child.setEnabled(true);
			}
		}
	}

	/**
	 * Called to display the email options dialog
	 */
	private void emailOptions() {
		MDAbstractDialog eod = new EmailVerifyOptionsDialog(dialog);
		if (eod.open()) {
			dialog.setChanged();
		}
	}

	/**
	 * Called to handle enablement of all controls based on input settings
	 */
	private void enable() {
		phoneEnable();
		emailEnable();
	}

	/**
	 * @param key
	 * @param args
	 * @return
	 */
	private String getString(String key, String... args) {
		return BaseMessages.getString(PKG, "MDCheckDialog.PhoneEmailTab." + key, args);
	}

	/**
	 * Called to handle enablement of controls based on input settings
	 */
	private void phoneEnable() {
		// Ignore if controls are already disposed
		if (wInputPhone.getComboBox().isDisposed()) {
			return;
		}
		// See if phone verification is enabled
		boolean isLicensed = pvMeta.isLicensed();
		// Check all inputs to see if any are set
		boolean noInputFields = Const.isEmpty(wInputPhone.getValue());
		// Update display based on enabled features
		if (!isLicensed) {
			gPhoneInput.setText(getString("PhoneObjNotLicensed.Label"));
			for (Control child : gPhoneInput.getChildren()) {
				child.setEnabled(false);
			}
			gPhoneOutput.setText(getString("PhoneObjNotLicensed.Label"));
			for (Control child : gPhoneOutput.getChildren()) {
				child.setEnabled(false);
			}
		} else if (!pvMeta.isInitializeOK()) {
			gPhoneInput.setText("Not Intialized");
			for (Control child : gPhoneInput.getChildren()) {
				child.setEnabled(false);
			}
			gPhoneOutput.setText(getString("NoInputPhoneSpecified.Label"));
			for (Control child : gPhoneOutput.getChildren()) {
				child.setEnabled(false);
			}
		} else if (noInputFields) {
			gPhoneInput.setText(getString("InputPhoneGroup.Label"));
			for (Control child : gPhoneInput.getChildren()) {
				child.setEnabled(true);
			}
			gPhoneOutput.setText(getString("NoInputPhoneSpecified.Label"));
			for (Control child : gPhoneOutput.getChildren()) {
				child.setEnabled(false);
			}
		} else {
			gPhoneInput.setText(getString("InputPhoneGroup.Label"));
			for (Control child : gPhoneInput.getChildren()) {
				child.setEnabled(true);
			}
			gPhoneOutput.setText(getString("OutputPhoneGroup.Label"));
			for (Control child : gPhoneOutput.getChildren()) {
				child.setEnabled(true);
			}
		}

		if (!AdvancedConfigurationMeta.isEnterprise(MDPropTags.MDLICENSE_PRODUCT_PhoneObject)) {

			wOutputAreaCode.setEnabled(false);
			wOutputPrefix.setEnabled(false);
			wOutputSuffix.setEnabled(false);
			wOutputExtension.setEnabled(false);
		}
	}

	/**
	 * Called to display the phone output options dialog
	 */
	private void phoneOutputOptions() {
		MDAbstractDialog pod = new PhoneOutputDialog(dialog);
		if (pod.open()) {
			dialog.setChanged();
		}
	}
}
