package com.melissadata.kettle.cv.address;

import com.melissadata.cz.ui.MDAbstractDialog;
import com.melissadata.cz.ui.MDInputCombo;
import com.melissadata.kettle.*;
import com.melissadata.cz.support.MDPropTags;
import com.melissadata.kettle.support.MDCheckHelper;
import com.melissadata.kettle.support.MDTab;
import com.melissadata.kettle.cv.MDCheckFullDialog;
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
import org.pentaho.di.core.Const;
import org.pentaho.di.i18n.BaseMessages;

public class AddressVerifyTab implements MDTab {
	private static Class<?> PKG = MDCheckFullDialog.class;
	private MDCheckDialog     dialog;
	private MDCheckHelper     helper;
	private MDCheckStepData   mdcStepData;
	private AddressVerifyMeta addrMeta;
	private Group             gInput;
	private MDInputCombo      wInputLastName;
	private MDInputCombo      wInputCompany;
	private MDInputCombo      wInputAddressLine1;
	private MDInputCombo      wInputAddressLine2;
	private MDInputCombo      wInputCity;
	private MDInputCombo      wInputState;
	private MDInputCombo      wInputZip;
	private MDInputCombo      wInputCountry;
	private Group             gOutput;
	private Text              wOutputAddressLine1;
	private Text              wOutputAddressLine2;
	private Text              wOutputCity;
	private Text              wOutputState;
	private Text              wOutputZip;
	private Text              wOutputCountry;
	private Text              wAddressKey;
	private Text              wMAK;
	private Text              wBaseMAK;
	private Button            wOptionAddressParsed;
	private Button            bAdditionalOutput;
	private Button            bAdditionalInput;
	private Button            badvMetaOptions;
	private SelectionListener enableListener = new SelectionListener() {
		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);
		}

		public void widgetSelected(SelectionEvent event) {
			enable();
		}
	};

	public AddressVerifyTab(MDCheckDialog dialog) {
		this.dialog = dialog;
		helper = dialog.getHelper();
		mdcStepData = dialog.getData();
		// Is name parsing tab enabled?
		boolean doNameParse = ((dialog.getCheckTypes() & MDCheckMeta.MDCHECK_NAME) != 0);
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
		description.setText(getString("Description"));
		helper.setLook(description);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(0, helper.margin);
		description.setLayoutData(fd);
		// Add the input/output groups side-by-side in a single composite just below the description
		Composite wIOComp = new Composite(wComp, 0);
		helper.setLook(wIOComp);
		wIOComp.setLayout(new FormLayout());
		fd = new FormData();
		fd.top = new FormAttachment(description, 4 * helper.margin);
		fd.left = new FormAttachment(0, 0);
		fd.right = new FormAttachment(100, 0);
		fd.bottom = new FormAttachment(50, 0);
		wIOComp.setLayoutData(fd);
		gInput = new Group(wIOComp, SWT.NONE);
		gInput.setText(getString("InputAddressGroup.Label"));
		helper.setLook(gInput);
		fl = new FormLayout();
		fl.marginHeight = helper.margin;
		fl.marginWidth = helper.margin;
		gInput.setLayout(fl);
		gOutput = new Group(wIOComp, SWT.NONE);
		gOutput.setText(getString("OutputAddressGroup.Label"));
		helper.setLook(gOutput);
		fl = new FormLayout();
		fl.marginHeight = helper.margin;
		fl.marginWidth = helper.margin;
		gOutput.setLayout(fl);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment(50, -helper.margin / 2);
		fd.bottom = new FormAttachment(100, -helper.margin);
		gInput.setLayoutData(fd);
		fd = new FormData();
		fd.left = new FormAttachment(50, helper.margin / 2);
		fd.top = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		fd.bottom = new FormAttachment(100, -helper.margin);
		gOutput.setLayoutData(fd);
		// Add fields to the input group
		// The last name field is an input combox box that has a special option
		// to take its last name from the result of the name parser
		wInputLastName = helper.addInputComboBox(gInput, null, "AddressVerifyTab.InputLastName", "AInLastName");
		wInputCompany = helper.addInputComboBox(gInput, wInputLastName.getComboBox(), "AddressVerifyTab.InputCompany", "AInCompany");
		if (doNameParse) {
			wInputLastName.addSpecialValue(getString("InputLastName.NameParserLastName1"), AddressVerifyMeta.NAME_PARSER_LAST_NAME1);
			wInputLastName.addSpecialValue(getString("InputLastName.NameParserLastName2"), AddressVerifyMeta.NAME_PARSER_LAST_NAME2);
			wInputCompany.addSpecialValue(getString("InputLastName.NameParserCompany"), AddressVerifyMeta.NAME_PARSER_COMPANY);
		}
		// The remaining input fields
// wInputCompany = helper.addInputComboBox(gInput, wInputLastName.getComboBox(), "AddressVerifyTab.InputCompany",
// "AInCompany");
		wInputAddressLine1 = helper.addInputComboBox(gInput, wInputCompany.getComboBox(), "AddressVerifyTab.InputAddressLine1", "AInAddress");
		wInputAddressLine2 = helper.addInputComboBox(gInput, wInputAddressLine1.getComboBox(), "AddressVerifyTab.InputAddressLine2", "AInAddress2");
		wInputCity = helper.addInputComboBox(gInput, wInputAddressLine2.getComboBox(), "AddressVerifyTab.InputCity", "AInCity");
		wInputState = helper.addInputComboBox(gInput, wInputCity.getComboBox(), "AddressVerifyTab.InputState", "AInState");
		wInputZip = helper.addInputComboBox(gInput, wInputState.getComboBox(), "AddressVerifyTab.InputZip", "AInZip");
		wInputCountry = helper.addInputComboBox(gInput, wInputZip.getComboBox(), "AddressVerifyTab.InputCountry", "AInCountry");
		// Add fields to the output group
		wOutputAddressLine1 = helper.addTextBox(gOutput, null, "AddressVerifyTab.OutputAddressLine1");
		wOutputAddressLine2 = helper.addTextBox(gOutput, wOutputAddressLine1, "AddressVerifyTab.OutputAddressLine2");
		wOutputCity = helper.addTextBox(gOutput, wOutputAddressLine2, "AddressVerifyTab.OutputCity");
		wOutputState = helper.addTextBox(gOutput, wOutputCity, "AddressVerifyTab.OutputState");
		wOutputZip = helper.addTextBox(gOutput, wOutputState, "AddressVerifyTab.OutputZip");
		wOutputCountry = helper.addTextBox(gOutput, wOutputZip, "AddressVerifyTab.OutputCountry");
		wAddressKey = helper.addTextBox(gOutput, wOutputCountry, "AddressVerifyTab.AddressKey");
		wMAK = helper.addTextBox(gOutput, wAddressKey, "AddressVerifyTab.MAK");
		wBaseMAK = helper.addTextBox(gOutput, wMAK, "AddressVerifyTab.BaseMAK");
		// Additional Address Info button
		bAdditionalOutput = helper.addPushButton(gOutput, wBaseMAK, "AddressVerifyTab.AdditionalInfo", new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent arg0) {
				additionalInfo();
			}
		});
		bAdditionalOutput.setToolTipText(getString("AdditionalInfo.ToolTip"));
		((FormData) bAdditionalOutput.getLayoutData()).top = null;
		((FormData) bAdditionalOutput.getLayoutData()).bottom = new FormAttachment(100, 0);
		// Address verify Additional input columns
		bAdditionalInput = helper.addPushButton(gInput, wInputCountry.getComboBox(), "AddressVerifyTab.AdditionalInput", new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {
				additionalInput();
			}
		});
		bAdditionalInput.setToolTipText(getString("AdditionalInput.ToolTip"));
		((FormData) bAdditionalInput.getLayoutData()).top = null;
		((FormData) bAdditionalInput.getLayoutData()).bottom = new FormAttachment(100, 0);
		// Address verify options button (local service only)
		badvMetaOptions = helper.addPushButton(wComp, wIOComp, "AddressVerifyTab.Options", new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {
				addressVerifyOptions();
			}
		});
		badvMetaOptions.setToolTipText(getString("Options.ToolTip"));
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

	public boolean companyInputEmpty() {
		return Const.isEmpty(wInputCompany.getValue());
	}

	public void dispose() {
		// Nothing to do
	}

	/**
	 * Loads the dialog data into the meta structure
	 */
	public void getData(MDCheckStepData data) {
		AddressVerifyMeta addressVerifyMeta = data.getAddressVerify();
		// Fill in the input meta data
		addressVerifyMeta.setInputLastName(wInputLastName.getValue());
		addressVerifyMeta.setInputCompany(wInputCompany.getValue());
		addressVerifyMeta.setInputAddressLine1(wInputAddressLine1.getValue());
		addressVerifyMeta.setInputAddressLine2(wInputAddressLine2.getValue());
		addressVerifyMeta.setInputCity(wInputCity.getValue());
		addressVerifyMeta.setInputState(wInputState.getValue());
		addressVerifyMeta.setInputZip(wInputZip.getValue());
		addressVerifyMeta.setInputCountry(wInputCountry.getValue());
		// Fill in the outputmeta data
		addressVerifyMeta.setOutputAddressLine1(wOutputAddressLine1.getText());
		addressVerifyMeta.setOutputAddressLine2(wOutputAddressLine2.getText());
		addressVerifyMeta.setOutputCity(wOutputCity.getText());
		addressVerifyMeta.setOutputState(wOutputState.getText());
		addressVerifyMeta.setOutputZip(wOutputZip.getText());
		addressVerifyMeta.setOutputCountry(wOutputCountry.getText());
		addressVerifyMeta.setOutputAddressKey(wAddressKey.getText());

		addressVerifyMeta.setOutputMAK(wMAK.getText());
		addressVerifyMeta.setOutputBaseMAK(wBaseMAK.getText());
		// extras
		if (false) {
			addressVerifyMeta.setOptionAddressParsed(wOptionAddressParsed.getSelection());
		}
	}

	/**
	 * @return The URL for help information
	 */
	public String getHelpURLKey() {
		return "MDCheck.Help.AddressVerifyTab";
	}

	public String getInputAddressLine1() {
		return wInputAddressLine1.getValue();
	}

	/**
	 * Loads the meta data into the dialog tab
	 *
	 * @param data
	 */
	public boolean init(MDCheckStepData data) {
		mdcStepData = data;
		// Get the address verify meta data
		addrMeta = mdcStepData.getAddressVerify();
		// If it didn't initialize correctly or it is not licensed then reset to
		// defaults
		if (!addrMeta.isInitializeOK() || !addrMeta.isLicensed()) {
			// Load the input controls
			wInputLastName.setValue("");
			wInputCompany.setValue("");
			wInputAddressLine1.setValue("");
			wInputAddressLine2.setValue("");
			wInputCity.setValue("");
			wInputState.setValue("");
			wInputZip.setValue("");
			wInputCountry.setValue("");
		} else {
			// Load the input controls
			wInputLastName.setValue(addrMeta.getInputLastName());
			wInputCompany.setValue(addrMeta.getInputCompany());
			wInputAddressLine1.setValue(addrMeta.getInputAddressLine1());
			wInputAddressLine2.setValue(addrMeta.getInputAddressLine2());
			wInputCity.setValue(addrMeta.getInputCity());
			wInputState.setValue(addrMeta.getInputState());
			wInputZip.setValue(addrMeta.getInputZip());
			wInputCountry.setValue(addrMeta.getInputCountry());
		}
		// Load the output controls
		wOutputAddressLine1.setText(addrMeta.getOutputAddressLine1());
		wOutputAddressLine2.setText(addrMeta.getOutputAddressLine2());
		wOutputCity.setText(addrMeta.getOutputCity());
		wOutputState.setText(addrMeta.getOutputState());
		wOutputZip.setText(addrMeta.getOutputZip());
		wOutputCountry.setText(addrMeta.getOutputCountry());
		wAddressKey.setText(addrMeta.getOutputAddressKey());
		wMAK.setText(addrMeta.getOutputMAK());
		wBaseMAK.setText(addrMeta.getOutputBaseMAK());
		// extras
		if (false) {
			wOptionAddressParsed.setSelection(addrMeta.getOptionAddressParsed());
		}
		// Set initial enablement
		enable();
		// Add listeners to track enablement
		wInputLastName.getComboBox().addSelectionListener(enableListener);
		wInputCompany.getComboBox().addSelectionListener(enableListener);
		wInputAddressLine1.getComboBox().addSelectionListener(enableListener);
		wInputAddressLine2.getComboBox().addSelectionListener(enableListener);
		wInputCity.getComboBox().addSelectionListener(enableListener);
		wInputState.getComboBox().addSelectionListener(enableListener);
		wInputZip.getComboBox().addSelectionListener(enableListener);
		wInputCountry.getComboBox().addSelectionListener(enableListener);
		return false;
	}

	public boolean lastnameInputEmpty() {
		return Const.isEmpty(wInputLastName.getValue());
	}

	public boolean minInput() {
		boolean minIn = true;
		if (Const.isEmpty(wInputAddressLine1.getValue()) && Const.isEmpty(wInputAddressLine2.getValue())) {
			minIn = false;
		}
		if (Const.isEmpty(wInputCity.getValue()) || Const.isEmpty(wInputState.getValue())) {
			if (Const.isEmpty(wInputZip.getValue())) {
				minIn = false;
			}
		}
		return minIn;
	}

	/**
	 * Called to display destinations for additional address information
	 */
	private void additionalInfo() {
		MDAbstractDialog aid = new AdditionalInfoDialog(dialog);
		if (aid.open()) {
			dialog.setChanged();
		}
	}

	/**
	 * Called to display Additional Input Fields
	 */
	private void additionalInput() {
		MDAbstractDialog aind = new AdditionalInputDialog(dialog);
		if (aind.open()) {
			dialog.setChanged();
		}
	}

	/**
	 * Called to display the address verify options dialog
	 */
	private void addressVerifyOptions() {
		MDAbstractDialog avod = new AddressVerifyOptionsDialog(dialog);
		if (avod.open()) {
			dialog.setChanged();
		}
	}

	/**
	 * Called to handle enablement of controls based on input settings
	 */
	private void enable() {
		// Ignore if controls are already disposed
		if (wOutputAddressLine1.isDisposed()) {
			return;
		}
		// See if address verification is licensed
		//	AddressVerifyMeta avMeta = dialog.getTabData().getAddressVerify();
		boolean isLicensed = addrMeta.isLicensed();
		// Check all inputs to see if any are set
		boolean noInputSelected =
				Const.isEmpty(wInputLastName.getValue()) && Const.isEmpty(wInputCompany.getValue()) && Const.isEmpty(wInputAddressLine1.getValue()) && Const.isEmpty(wInputAddressLine2.getValue()) && Const.isEmpty(wInputCity.getValue()) && Const
						.isEmpty(wInputState.getValue()) && Const.isEmpty(wInputZip.getValue()) && Const.isEmpty(wInputCountry.getValue());
		// Update display based on enabled features
		if (!isLicensed) {
			gInput.setText(getString("AddrObjNotLicensed.Label"));
			for (Control child : gInput.getChildren()) {
				child.setEnabled(false);
			}
			gOutput.setText(getString("AddrObjNotLicensed.Label"));
			for (Control child : gOutput.getChildren()) {
				child.setEnabled(false);
			}
		} else if (!addrMeta.isInitializeOK()) {
			gInput.setText("Not Initialized");// getString("AddrObjNotLicensed.Label")
			for (Control child : gInput.getChildren()) {
				child.setEnabled(false);
			}
			gOutput.setText("Not Initialized");
			for (Control child : gOutput.getChildren()) {
				child.setEnabled(false);
			}
		} else if (noInputSelected) {
			gInput.setText(getString("InputAddressGroup.Label"));
			for (Control child : gInput.getChildren()) {
				child.setEnabled(true);
			}
			gOutput.setText(getString("NoInputAddressSpecified.Label"));
			for (Control child : gOutput.getChildren()) {
				child.setEnabled(false);
			}
		} else {
			gInput.setText(getString("InputAddressGroup.Label"));
			for (Control child : gInput.getChildren()) {
				child.setEnabled(true);
			}
			gOutput.setText(getString("OutputAddressGroup.Label"));
			for (Control child : gOutput.getChildren()) {
				child.setEnabled(true);
			}
		}
		if (!minInput()) {
			for (Control child : gOutput.getChildren()) {
				child.setEnabled(false);
			}
		}

		if (!AdvancedConfigurationMeta.isEnterprise(MDPropTags.MDLICENSE_PRODUCT_AddressObject)) {
			wAddressKey.setEnabled(false);
			wMAK.setEnabled(false);
			wBaseMAK.setEnabled(false);
		}

		badvMetaOptions.setEnabled(isLicensed && !noInputSelected);
	}

	/**
	 * @param key
	 * @param args
	 * @return
	 */
	private String getString(String key, String... args) {
		return BaseMessages.getString(PKG, "MDCheckDialog.AddressVerifyTab." + key, args);
	}
}
