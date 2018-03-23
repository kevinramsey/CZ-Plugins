package com.melissadata.kettle.globalverify.ui;

import com.melissadata.cz.ui.MDInputCombo;
import com.melissadata.kettle.globalverify.MDGlobalDialog;
import com.melissadata.kettle.globalverify.MDGlobalMeta;
import com.melissadata.kettle.globalverify.data.PhoneFields;
import com.melissadata.kettle.globalverify.support.CountryUtil;
import com.melissadata.kettle.globalverify.support.MDGlobalAddressHelper;
import com.melissadata.kettle.globalverify.support.MDTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
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

import java.util.SortedSet;

public class PhoneTab implements MDTab {

	private static Class<?> PKG = MDGlobalMeta.class;
	private MDGlobalAddressHelper helper;
	private Group                 gInput;
	private MDInputCombo          wInPhone;
	private MDInputCombo          wInCountry;
	private Control[]             wInDefaultCountry;
	private Control[]             wInDialingCountry;
	private Button                btnExpress;
	private Button                btnPremium;
	private Button                ckCallerID;
	private Group                 gOutput;
	private Text                  wOutPhone;
	private Text                  wOutSubscriber;
	private Text                  wOutCountryName;
	private Text                  wOutCountryAbbriv;
	private Text                  wOutCarrier;
	private Text                  wOutCallerID;
	private Text                  wOutPhoneCountryDialingCode;
	private Text                  wOutInternationalPrefix;
	private Text                  wOutNationlPrefix;
	private Text                  wOutNationalDestCode;
	private Text                  wOutLocality;
	private Text                  wOutAdminArea;
	private Text                  wOutLanguage;
	private Text                  wOutUTC;
	private Text                  wOutDST;
	private Text                  wOutLatitude;
	private Text                  wOutLogitude;
	private Text                  wInternationalNumber;
	private Text                  wPostalCode;
	private Text                  wOutSuggestions;
	private SortedSet<String>     countryShortList;
	private SelectionListener     enableListener;
	private MDGlobalDialog        dialog;
	private CountryUtil           countryUtil;

	public PhoneTab(MDGlobalDialog dialog) {

		this.dialog = dialog;
		helper = dialog.getHelper();
		countryUtil = new CountryUtil();
		enableListener = new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// not used
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {

				enable();
			}
		};
		// Create the tab
		CTabItem wTab = new CTabItem(dialog.getTabFolder(), SWT.NONE);
		wTab.setText(getString("TabTitle"));
		wTab.setData(this);
		// Create a scrolling region within the tab
		ScrolledComposite wSComp = new ScrolledComposite(dialog.getTabFolder(), SWT.V_SCROLL | SWT.H_SCROLL);
		wSComp.setLayout(new FillLayout());
		Composite wComp = new Composite(wSComp, SWT.NONE);
		helper.setLook(wComp);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		wComp.setLayout(fl);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(0, 0);
		fd.right = new FormAttachment(100, 0);
		// fd.bottom = new FormAttachment(100, 0);
		wComp.setLayoutData(fd);
		// Description line
		Label description = new Label(wComp, SWT.LEFT | SWT.WRAP);
		description.setText(getString("Description"));
		helper.setLook(description);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(0, helper.margin);
		description.setLayoutData(fd);
		addInputGroup(wComp, description);
		addOutputGroup(wComp, description);
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

	private void addInputGroup(Composite parent, Control last) {

		gInput = new Group(parent, SWT.NONE);
		helper.setLook(gInput);
		FormLayout fl = new FormLayout();
		fl.marginHeight = helper.margin;
		fl.marginWidth = helper.margin;
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin);
		fd.right = new FormAttachment(50, -helper.margin);
		fd.bottom = new FormAttachment(100, -helper.margin);
		gInput.setLayout(fl);
		gInput.setLayoutData(fd);
		gInput.setText(getString("InputGroup.Name"));
		wInPhone = helper.addInputComboBox(gInput, null, "PhoneTab.InputGroup.Phone", "NInPhone");
		wInCountry = helper.addInputComboBox(gInput, wInPhone.getComboBox(), "PhoneTab.InputGroup.Country", "NInPhone");
		wInPhone.addSelectionListener(enableListener);
		wInCountry.addSelectionListener(enableListener);
		helper.setLook(wInPhone.getComboBox());
		helper.setLook(wInCountry.getComboBox());
		Label spacer = helper.addSpacer(gInput, wInCountry.getComboBox());
		wInDefaultCountry = helper.addComboBox(gInput, spacer, "PhoneTab.InputGroup.DefaultCountry");
		fd = new FormData();
		fd.left = new FormAttachment(wInCountry.getLabel(), helper.margin);
		fd.top = new FormAttachment(spacer, helper.margin);
		// fd.right = new FormAttachment(50, -helper.margin);
		// fd.bottom = new FormAttachment(25, -helper.margin);
		wInDefaultCountry[0].setLayoutData(fd);
		fd = new FormData();
		fd.left = new FormAttachment(wInCountry.getLabel(), helper.margin);
		fd.top = new FormAttachment(wInDefaultCountry[0], helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		// fd.bottom = new FormAttachment(25, -helper.margin);
		wInDefaultCountry[1].setLayoutData(fd);
		((CCombo) wInDefaultCountry[1]).addSelectionListener(enableListener);
		Label spacer2 = helper.addSpacer(gInput, wInDefaultCountry[1]);
		wInDialingCountry = helper.addComboBox(gInput, spacer2, "PhoneTab.InputGroup.DialingCountry");
		fd = new FormData();
		fd.left = new FormAttachment(wInCountry.getLabel(), helper.margin);
		fd.top = new FormAttachment(spacer2, helper.margin);
		// fd.right = new FormAttachment(50, -helper.margin);
		// fd.bottom = new FormAttachment(25, -helper.margin);
		wInDialingCountry[0].setLayoutData(fd);
		fd = new FormData();
		fd.left = new FormAttachment(wInCountry.getLabel(), helper.margin);
		fd.top = new FormAttachment(wInDialingCountry[0], helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		// fd.bottom = new FormAttachment(25, -helper.margin);
		wInDialingCountry[1].setLayoutData(fd);
		((CCombo) wInDialingCountry[1]).addSelectionListener(enableListener);



		Label spacer4 = helper.addSpacer(gInput, wInDialingCountry[1]);

		Group gCallerID = new Group(gInput, SWT.NONE);
		fl = new FormLayout();
		fl.marginHeight = helper.margin;
		fl.marginWidth = helper.margin;
		fd = new FormData();
		fd.left = new FormAttachment(50, helper.margin);
		fd.top = new FormAttachment(spacer4, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		// fd.bottom = new FormAttachment(80, -helper.margin);
		gCallerID.setLayout(fl);
		gCallerID.setLayoutData(fd);
		gCallerID.setText(getString("CallerIDGroup.Description.Label"));
		helper.setLook(gCallerID);

		ckCallerID = helper.addCheckBox(gCallerID, null, "PhoneTab.CallerIDGroup.CallerID");
		ckCallerID.addSelectionListener(enableListener);

		Group gOptopion = new Group(gInput, SWT.NONE);
		fl = new FormLayout();
		fl.marginHeight = helper.margin;
		fl.marginWidth = helper.margin;
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(80, helper.margin);
		//fd.right = new FormAttachment(100, -helper.margin);
		 fd.bottom = new FormAttachment(100, -helper.margin);
		gOptopion.setLayout(fl);
		gOptopion.setLayoutData(fd);
		gOptopion.setText(getString("OptionGroup.Description.Label"));
		helper.setLook(gOptopion);

		btnExpress = new Button(gOptopion,SWT.RADIO | SWT.WRAP);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(null, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		// fd.bottom = new FormAttachment(80, -helper.margin);
		btnExpress.setLayoutData(fd);
		btnExpress.setText(getString("OptionGroup.Express.Label"));
		helper.setLook(btnExpress);

		Label spacerD = helper.addSpacer(gOptopion, btnExpress);

		btnPremium = new Button(gOptopion,SWT.RADIO | SWT.WRAP);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(spacerD, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		// fd.bottom = new FormAttachment(80, -helper.margin);
		btnPremium.setLayoutData(fd);
		btnPremium.setText(getString("OptionGroup.Premium.Label"));
		helper.setLook(btnPremium);


//		btnExpress = helper.addRadioButton(gOptopion, null, "PhoneTab.OptionGroup.Express");
//		//btnPremium = helper.addRadioButton(gOptopion, btnExpress, "PhoneTab.OptionGroup.Premium");
//		btnPremium = helper.addRadioButton(gOptopion, btnExpress, "PhoneTab.OptionGroup.Express");


	}

	private void addOutputGroup(Composite parent, Control last) {

		gOutput = new Group(parent, SWT.NONE);
		helper.setLook(gOutput);
		FormLayout fl = new FormLayout();
		fl.marginHeight = helper.margin;
		fl.marginWidth = helper.margin;
		FormData fd = new FormData();
		fd.left = new FormAttachment(50, helper.margin);
		fd.top = new FormAttachment(last, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		fd.bottom = new FormAttachment(100, -helper.margin);
		gOutput.setLayout(fl);
		gOutput.setLayoutData(fd);
		gOutput.setText(getString("OutputGroup.Name"));
		wOutPhone = helper.addTextBox(gOutput, last, "PhoneTab.OutputGroup.PhoneNumber");
		wOutSubscriber = helper.addTextBox(gOutput, wOutPhone, "PhoneTab.OutputGroup.Subscriber");
		wOutCarrier = helper.addTextBox(gOutput, wOutSubscriber, "PhoneTab.OutputGroup.Carrier");
		wOutCallerID = helper.addTextBox(gOutput, wOutCarrier, "PhoneTab.OutputGroup.CallerID");
		wOutCountryName = helper.addTextBox(gOutput, wOutCallerID, "PhoneTab.OutputGroup.CountryName");
		wOutCountryAbbriv = helper.addTextBox(gOutput, wOutCountryName, "PhoneTab.OutputGroup.CountryAbbreviation");
		wOutPhoneCountryDialingCode = helper.addTextBox(gOutput, wOutCountryAbbriv, "PhoneTab.OutputGroup.DialingCode");
		wOutInternationalPrefix = helper.addTextBox(gOutput, wOutPhoneCountryDialingCode, "PhoneTab.OutputGroup.InternationalPrefix");
		wOutNationlPrefix = helper.addTextBox(gOutput, wOutInternationalPrefix, "PhoneTab.OutputGroup.NationalPrefix");
		wOutNationalDestCode = helper.addTextBox(gOutput, wOutNationlPrefix, "PhoneTab.OutputGroup.NationalDestCode");
		wInternationalNumber = helper.addTextBox(gOutput, wOutNationalDestCode, "PhoneTab.OutputGroup.InternationalNumber");
		wOutLocality = helper.addTextBox(gOutput, wInternationalNumber, "PhoneTab.OutputGroup.Locality");
		wOutAdminArea = helper.addTextBox(gOutput, wOutLocality, "PhoneTab.OutputGroup.AdminArea");
		wPostalCode = helper.addTextBox(gOutput, wOutAdminArea, "PhoneTab.OutputGroup.PostalCode");
		wOutLanguage = helper.addTextBox(gOutput, wPostalCode, "PhoneTab.OutputGroup.Language");
		wOutUTC = helper.addTextBox(gOutput, wOutLanguage, "PhoneTab.OutputGroup.UTC");
		wOutDST = helper.addTextBox(gOutput, wOutUTC, "PhoneTab.OutputGroup.DST");
		wOutLatitude = helper.addTextBox(gOutput, wOutDST, "PhoneTab.OutputGroup.Latitude");
		wOutLogitude = helper.addTextBox(gOutput, wOutLatitude, "PhoneTab.OutputGroup.Longitude");
		wOutSuggestions = helper.addTextBox(gOutput, wOutLogitude, "PhoneTab.OutputGroup.Suggestions");
	}

	@Override
	public void advancedConfigChanged() {
		//
		enable();
	}

	private void enable() {

		boolean isLicensed = dialog.getInput().getPhoneMeta().isLicensed();
		if (!isLicensed) {
			gInput.setText(getString("PhoneNotLicensed.Label"));
			gOutput.setText(getString("PhoneNotLicensed.Label"));
			for (Control child : gInput.getChildren()) {
				child.setEnabled(false);
			}
			for (Control child : gOutput.getChildren()) {
				child.setEnabled(false);
			}
			return;
		}
		boolean missingInput = Const.isEmpty(wInPhone.getValue()) || ((Const.isEmpty(wInCountry.getValue()) && Const.isEmpty(((CCombo) wInDefaultCountry[1]).getText())) || Const.isEmpty(((CCombo) wInDialingCountry[1]).getText()));
		if (missingInput) {
			gInput.setText(getString("InputGroup.Name"));
			gOutput.setText(getString("MissingInput.Label"));
			for (Control child : gInput.getChildren()) {
				child.setEnabled(true);
			}
			for (Control child : gOutput.getChildren()) {
				child.setEnabled(false);
			}
			return;
		}
		gInput.setText(getString("InputGroup.Name"));
		gOutput.setText(getString("OutputGroup.Name"));
		for (Control child : gInput.getChildren()) {
			child.setEnabled(true);
		}
		for (Control child : gOutput.getChildren()) {
			child.setEnabled(true);
		}

		if(ckCallerID.getSelection()){
			wOutCallerID.setEnabled(true);
		} else {
			wOutCallerID.setEnabled(false);
		}

	}

	public void getCountries() {

		countryShortList = countryUtil.getSortedSetCountryNames();
		//dialog.getCountryDesc();
	}

	@Override
	public void getData(MDGlobalMeta meta) {
		//
		PhoneFields phFields = meta.getPhoneMeta().phoneFields;
		phFields.inputFields.get(PhoneFields.TAG_INPUT_PHONE).metaValue = wInPhone.getValue();
		phFields.inputFields.get(PhoneFields.TAG_INPUT_COUNTRY).metaValue = wInCountry.getValue();
		phFields.inputFields.get(PhoneFields.TAG_INPUT_DEFAULT_COUNTRY).metaValue = ((CCombo) wInDefaultCountry[1]).getText();
		phFields.inputFields.get(PhoneFields.TAG_INPUT_DIALING_COUNTRY).metaValue = ((CCombo) wInDialingCountry[1]).getText();

		phFields.outputFields.get(PhoneFields.TAG_OUTPUT_PHONE).metaValue = wOutPhone.getText();
		phFields.outputFields.get(PhoneFields.TAG_OUTPUT_SUBSCRIBER).metaValue = wOutSubscriber.getText();
		phFields.outputFields.get(PhoneFields.TAG_OUTPUT_CARRIER).metaValue = wOutCarrier.getText();
		if(ckCallerID.getSelection()) {
			phFields.outputFields.get(PhoneFields.TAG_OUTPUT_CALLER_ID).metaValue = wOutCallerID.getText();
		} else {
			phFields.outputFields.get(PhoneFields.TAG_OUTPUT_CALLER_ID).metaValue = "";
		}
		phFields.outputFields.get(PhoneFields.TAG_OUTPUT_COUNTRY).metaValue = wOutCountryName.getText();
		phFields.outputFields.get(PhoneFields.TAG_OUTPUT_COUNTRY_ABBREVIATION).metaValue = wOutCountryAbbriv.getText();
		phFields.outputFields.get(PhoneFields.TAG_OUTPUT_COUNTRY_CODE).metaValue = wOutPhoneCountryDialingCode.getText();
		phFields.outputFields.get(PhoneFields.TAG_OUTPUT_INTERNATIONAL_PREFIX).metaValue = wOutInternationalPrefix.getText();
		phFields.outputFields.get(PhoneFields.TAG_OUTPUT_NATIONAL_PREFIX).metaValue = wOutNationlPrefix.getText();
		phFields.outputFields.get(PhoneFields.TAG_OUTPUT_DEST_CODE).metaValue = wOutNationalDestCode.getText();
		phFields.outputFields.get(PhoneFields.TAG_OUTPUT_LANGUAGE).metaValue = wOutLanguage.getText();
		phFields.outputFields.get(PhoneFields.TAG_OUTPUT_LOCALITY).metaValue = wOutLocality.getText();
		phFields.outputFields.get(PhoneFields.TAG_OUTPUT_ADMIN_AREA).metaValue = wOutAdminArea.getText();
		phFields.outputFields.get(PhoneFields.TAG_OUTPUT_UTC).metaValue = wOutUTC.getText();
		phFields.outputFields.get(PhoneFields.TAG_OUTPUT_DST).metaValue = wOutDST.getText();
		phFields.outputFields.get(PhoneFields.TAG_OUTPUT_LATITUDE).metaValue = wOutLatitude.getText();
		phFields.outputFields.get(PhoneFields.TAG_OUTPUT_LONGITUDE).metaValue = wOutLogitude.getText();
		phFields.outputFields.get(PhoneFields.TAG_OUTPUT_SUGGESTIONS).metaValue = wOutSuggestions.getText();

		phFields.outputFields.get(PhoneFields.TAG_OUTPUT_INTERNATIONAL_NUMBER).metaValue = wInternationalNumber.getText();
		phFields.outputFields.get(PhoneFields.TAG_OUTPUT_POSTAL_CODE).metaValue = wPostalCode.getText();

		String opt = btnPremium.getSelection() ? "Premium" : "Express";

		phFields.optionFields.get(PhoneFields.TAG_OPTION_VERIFY).metaValue = opt;

		phFields.optionFields.get(PhoneFields.TAG_OPTION_CALLER_ID).metaValue = ckCallerID.getSelection() ? "True" : "False";
	}

	@Override
	public String getHelpURLKey() {

		return BaseMessages.getString(PKG, "MDGlobalVerify.Help.PhoneTab");
	}

	private String getString(String key, String... args) {

		return BaseMessages.getString(PKG, "MDGlobalDialog.PhoneTab." + key, args);
	}

	@Override
	public boolean init(MDGlobalMeta meta) {

		PhoneFields phFields = meta.getPhoneMeta().phoneFields;
		wInPhone.setValue(phFields.inputFields.get(PhoneFields.TAG_INPUT_PHONE).metaValue);
		wInCountry.setValue(phFields.inputFields.get(PhoneFields.TAG_INPUT_COUNTRY).metaValue);
		wOutPhone.setText(phFields.outputFields.get(PhoneFields.TAG_OUTPUT_PHONE).metaValue);
		wOutSubscriber.setText(phFields.outputFields.get(PhoneFields.TAG_OUTPUT_SUBSCRIBER).metaValue);
		wOutCarrier.setText(phFields.outputFields.get(PhoneFields.TAG_OUTPUT_CARRIER).metaValue);
		wOutCallerID.setText(phFields.outputFields.get(PhoneFields.TAG_OUTPUT_CALLER_ID).metaValue);
		wOutCountryName.setText(phFields.outputFields.get(PhoneFields.TAG_OUTPUT_COUNTRY).metaValue);
		wOutCountryAbbriv.setText(phFields.outputFields.get(PhoneFields.TAG_OUTPUT_COUNTRY_ABBREVIATION).metaValue);
		wOutPhoneCountryDialingCode.setText(phFields.outputFields.get(PhoneFields.TAG_OUTPUT_COUNTRY_CODE).metaValue);
		wOutInternationalPrefix.setText(phFields.outputFields.get(PhoneFields.TAG_OUTPUT_INTERNATIONAL_PREFIX).metaValue);
		wOutNationlPrefix.setText(phFields.outputFields.get(PhoneFields.TAG_OUTPUT_NATIONAL_PREFIX).metaValue);
		wOutNationalDestCode.setText(phFields.outputFields.get(PhoneFields.TAG_OUTPUT_DEST_CODE).metaValue);
		wOutLocality.setText(phFields.outputFields.get(PhoneFields.TAG_OUTPUT_LOCALITY).metaValue);
		wOutAdminArea.setText(phFields.outputFields.get(PhoneFields.TAG_OUTPUT_ADMIN_AREA).metaValue);
		wOutLanguage.setText(phFields.outputFields.get(PhoneFields.TAG_OUTPUT_LANGUAGE).metaValue);
		wOutUTC.setText(phFields.outputFields.get(PhoneFields.TAG_OUTPUT_UTC).metaValue);
		wOutDST.setText(phFields.outputFields.get(PhoneFields.TAG_OUTPUT_DST).metaValue);
		wOutLatitude.setText(phFields.outputFields.get(PhoneFields.TAG_OUTPUT_LATITUDE).metaValue);
		wOutLogitude.setText(phFields.outputFields.get(PhoneFields.TAG_OUTPUT_LONGITUDE).metaValue);

		wInternationalNumber.setText(phFields.outputFields.get(PhoneFields.TAG_OUTPUT_INTERNATIONAL_NUMBER).metaValue);
		wPostalCode.setText(phFields.outputFields.get(PhoneFields.TAG_OUTPUT_POSTAL_CODE).metaValue);
		wOutSuggestions.setText(phFields.outputFields.get(PhoneFields.TAG_OUTPUT_SUGGESTIONS).metaValue);
		getCountries();
		for (String country : countryShortList) {
			if (country != null && !country.isEmpty()) {
				((CCombo) wInDefaultCountry[1]).add(country);
				((CCombo) wInDialingCountry[1]).add(country);
			}
		}
		if (!Const.isEmpty(phFields.inputFields.get(PhoneFields.TAG_INPUT_DEFAULT_COUNTRY).metaValue)) {
			((CCombo) wInDefaultCountry[1]).setText(phFields.inputFields.get(PhoneFields.TAG_INPUT_DEFAULT_COUNTRY).metaValue);
		}
		if (!Const.isEmpty(phFields.inputFields.get(PhoneFields.TAG_INPUT_DIALING_COUNTRY).metaValue)) {
			((CCombo) wInDialingCountry[1]).setText(phFields.inputFields.get(PhoneFields.TAG_INPUT_DIALING_COUNTRY).metaValue);
		}

		String verifyOpt = phFields.optionFields.get(PhoneFields.TAG_OPTION_VERIFY).metaValue;

		if (verifyOpt.equals("Premium")) {
			btnPremium.setSelection(true);
		} else {
			btnExpress.setSelection(true);
		}

		ckCallerID.setSelection(Boolean.valueOf(phFields.optionFields.get(PhoneFields.TAG_OPTION_CALLER_ID).metaValue));



		enable();
		return true;
	}
}
