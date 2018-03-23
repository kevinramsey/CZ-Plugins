package com.melissadata.kettle.personator.ui;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.pentaho.di.core.Const;
import org.pentaho.di.i18n.BaseMessages;

import com.melissadata.cz.ui.BrowserDialog;
import com.melissadata.cz.ui.MDAbstractDialog;
import com.melissadata.kettle.personator.MDPersonatorDialog;
import com.melissadata.kettle.personator.MDPersonatorMeta;
import com.melissadata.kettle.personator.MDPersonatorMeta.OutputPhoneFormat;
import com.melissadata.kettle.personator.data.PersonatorFields;
import com.melissadata.kettle.personator.support.MDPersonatorHelper;

public class AdvancedOptionsDialog extends MDAbstractDialog {
	
	private static Class<?> PKG = MDPersonatorMeta.class;

	private MDPersonatorHelper helper;

	private ComboViewer vOutputFormat;

	private Button ckCorrectFirstName;
	private Button ckStandardizeCompany;
	private CCombo ccNameHint;
	private CCombo ccGenderPopulation;
	private CCombo ccGenderAggression;
	private CCombo ccMiddleNameLogic;
	private CCombo ccSalutationFormat;

	private Button rCentricAuto;
	private Button rCentricAddress;
	private Button rCentricPhone;
	private Button rCentricEmail;

	private Button ckPreferredCity;
	private CCombo  ccDiacritics;
	
	private Button ckCorrectSyntax;
	private Button ckUpdateDomain;
	private Button ckLookup;
	private Button ckStandardizeCasing;

	private Label phoneOptDescription;

	private MDPersonatorMeta pMeta;
	

	public AdvancedOptionsDialog(MDPersonatorDialog dialog) {
		super(dialog, SWT.NONE);
	}

	@Override
	protected void createContents(Composite parent, Object data) {
		pMeta = (MDPersonatorMeta) data;
		helper = new MDPersonatorHelper((MDPersonatorDialog) dialog);

		// Create the composite that will hold the contents of the tab
		Composite wComp = new Composite(parent, SWT.NONE);
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
		Control last = null;

		last = createActionsGroup(wComp, description);
		last = createNameOptionsGroup(wComp, last);
		last = createAddressGroup(wComp, last);
		last = createPhoneGroup(wComp, last);
		createEmailGroup(wComp, last);

		// Fit the composite within its container (the scrolled composite)
		fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(0, 0);
		fd.right = new FormAttachment(100, 0);
		fd.bottom = new FormAttachment(100, 0);
		wComp.setLayoutData(fd);

		wComp.pack();
	}
	
	@Override
	protected Button[] getDialogButtons() {
		Button wValOK = new Button(getShell(), SWT.PUSH);
		wValOK.setText(BaseMessages.getString(PKG, "System.Button.OK"));
		
		Button wValCancel = new Button(getShell(), SWT.PUSH);
		wValCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel"));
		
		Button wValHelp = new Button(getShell(), SWT.PUSH);
		wValHelp.setText(BaseMessages.getString(PKG, "MDPersonator.Button.Help"));
		
		
		wValOK.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				getData(pMeta);
				dispose();
			}
		});
		
		wValCancel.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				cancel();
			}
		});
		
		wValHelp.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				help();
			}
		});


		return new Button[] {wValOK, wValCancel , wValHelp };
	}
	
	private void help() {
		String url = getHelpURLKey();
		BrowserDialog.displayURL(getShell(), dialog, url);
	}
	
	private Group createActionsGroup(Composite parent, Control last) {
		
		Group actionGroup = new Group(parent, SWT.NONE);
		actionGroup.setText(getString("ActionOptionGroup.Label"));
		helper.setLook(actionGroup);
		
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, 2*helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		actionGroup.setLayoutData(fd);

		actionGroup.setLayout(fl);
		
		Composite wActionsComp = new Composite(actionGroup, 0);
		helper.setLook(wActionsComp);
		wActionsComp.setLayout(new FormLayout());

		fd = new FormData();
		fd.top = new FormAttachment(0, 0);
		fd.left = new FormAttachment(0, 0);
		fd.right = new FormAttachment(100, 0);
		fd.bottom = new FormAttachment(100, 0);
		wActionsComp.setLayoutData(fd);
		
		last = null;
		
		Label lDescription = helper.addLabel(wActionsComp, last, "AdvancedOptions.ActionOptionDescription");
		last = lDescription;
		Label spacer1 = helper.addSpacer(wActionsComp, last);
		last = spacer1;

		//Auto
		last = rCentricAuto = helper.addRadioButton(wActionsComp, last, "AdvancedOptions.CentricHint.Auto");
		fd = new FormData();
		fd.top = new FormAttachment(spacer1, helper.margin);
		fd.left = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment(8, helper.margin);
		rCentricAuto.setLayoutData(fd);
		
		Label lCentricAuto = helper.addLabel(wActionsComp, last, "AdvancedOptions.CentricHint.Auto.Description");
		fd = new FormData();
		fd.top = new FormAttachment(spacer1, helper.margin);
		fd.left = new FormAttachment(rCentricAuto, helper.margin);
		lCentricAuto.setLayoutData(fd);
		
		//Address
		last = rCentricAddress = helper.addRadioButton(wActionsComp, last, "AdvancedOptions.CentricHint.Address");
		fd = new FormData();
		fd.top = new FormAttachment(rCentricAuto, helper.margin);
		fd.left = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment(8, helper.margin);
		rCentricAddress.setLayoutData(fd);
		
		Label lCentricAddress = helper.addLabel(wActionsComp, last, "AdvancedOptions.CentricHint.Address.Description");
		fd = new FormData();
		fd.top = new FormAttachment(rCentricAuto, helper.margin);
		fd.left = new FormAttachment(rCentricAddress, helper.margin);
		lCentricAddress.setLayoutData(fd);
		
		//Phone
		last = rCentricPhone = helper.addRadioButton(wActionsComp, last, "AdvancedOptions.CentricHint.Phone");
		fd = new FormData();
		fd.top = new FormAttachment(rCentricAddress, helper.margin);
		fd.left = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment(8, helper.margin);
		rCentricPhone.setLayoutData(fd);
		
		Label lCentricPhone = helper.addLabel(wActionsComp, last, "AdvancedOptions.CentricHint.Phone.Description");
		fd = new FormData();
		fd.top = new FormAttachment(rCentricAddress, helper.margin);
		fd.left = new FormAttachment(rCentricPhone, helper.margin);
		lCentricPhone.setLayoutData(fd);
		
		//Email
		last = rCentricEmail = helper.addRadioButton(wActionsComp, last, "AdvancedOptions.CentricHint.Email");
		fd = new FormData();
		fd.top = new FormAttachment(rCentricPhone, helper.margin);
		fd.left = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment(8, helper.margin);
		rCentricEmail.setLayoutData(fd);
		
		Label lCentricEmail = helper.addLabel(wActionsComp, last, "AdvancedOptions.CentricHint.Email.Description");
		fd = new FormData();
		fd.top = new FormAttachment(rCentricPhone, helper.margin);
		fd.left = new FormAttachment(rCentricEmail, helper.margin);
		lCentricEmail.setLayoutData(fd);

		return actionGroup;

	}
	
	private Group createNameOptionsGroup(Composite parent, Control last) {

		String[] arNameHint = { "Varying", "Definitely Full", "Very Likely Full", "Probably Full", "Probably Inverse", "Very Likely Inverse",
				"Definitely Inverse", "Mixed First Name", "Mixed Last Name" };
		String[] arGenderPoupulation = { "Mixed", "Male", "Female" };
		String[] arGenderAggression = { "Neutral", "Conservative", "Aggressive" };
		String[] arMiddleNameLogic = { "Parse Logic", "Hyphenated Last", "Middle Name" };
		String[] arSalutationFormat = { "Formal", "Informal", "First Last" };

		Group nameGroup = new Group(parent, SWT.NONE);
		nameGroup.setText(getString("NameOptGroup.Label"));
		helper.setLook(nameGroup);

		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, 2 * helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		nameGroup.setLayoutData(fd);

		nameGroup.setLayout(fl);

		Composite wNameComp = new Composite(nameGroup, 0);
		helper.setLook(wNameComp);
		wNameComp.setLayout(new FormLayout());

		fd = new FormData();
		fd.top = new FormAttachment(0, 0);
		fd.left = new FormAttachment(0, 0);
		fd.right = new FormAttachment(100, 0);
		fd.bottom = new FormAttachment(100, 0);
		wNameComp.setLayoutData(fd);

		last = null;
		helper.colWidth[0] = 20;
		helper.colWidth[2] = 40;

		Label lNameHint = helper.addLabel(wNameComp, last, "AdvancedOptions.NameHint.Description");

		last = ccNameHint = (CCombo) helper.addComboBox(wNameComp, last, "AdvancedOptions.NameHint")[1];
		ccNameHint.setItems(arNameHint);
		ccNameHint.setEditable(false);
		fd = new FormData();
		fd.top = new FormAttachment(null, helper.margin);
		fd.left = new FormAttachment(ccNameHint, 5 * helper.margin);
		lNameHint.setLayoutData(fd);

		Label lMidName = helper.addLabel(wNameComp, last, "AdvancedOptions.MiddleNameLogic.Description");
		fd = new FormData();
		fd.top = new FormAttachment(last, helper.margin);
		fd.left = new FormAttachment(ccNameHint, 5 * helper.margin);
		lMidName.setLayoutData(fd);
		last = ccMiddleNameLogic = (CCombo) helper.addComboBox(wNameComp, last, "AdvancedOptions.MiddleNameLogic")[1];
		ccMiddleNameLogic.setItems(arMiddleNameLogic);
		ccMiddleNameLogic.setEditable(false);

		Label lSalutation = helper.addLabel(wNameComp, last, "AdvancedOptions.SalutationFormat.Description");
		fd = new FormData();
		fd.top = new FormAttachment(last, helper.margin);
		fd.left = new FormAttachment(ccNameHint, 5 * helper.margin);
		lSalutation.setLayoutData(fd);

		last = ccSalutationFormat = (CCombo) helper.addComboBox(wNameComp, last, "AdvancedOptions.SalutationFormat")[1];
		ccSalutationFormat.setItems(arSalutationFormat);
		ccSalutationFormat.setEditable(false);

		Label lGenderPop = helper.addLabel(wNameComp, last, "AdvancedOptions.GenderPopulation.Description");
		fd = new FormData();
		fd.top = new FormAttachment(last, helper.margin);
		fd.left = new FormAttachment(ccNameHint, 5 * helper.margin);
		lGenderPop.setLayoutData(fd);

		last = ccGenderPopulation = (CCombo) helper.addComboBox(wNameComp, last, "AdvancedOptions.GenderPopulation")[1];
		ccGenderPopulation.setItems(arGenderPoupulation);
		ccGenderPopulation.setEditable(false);

		Label lGenderAg = helper.addLabel(wNameComp, last, "AdvancedOptions.GenderAggression.Description");
		fd = new FormData();
		fd.top = new FormAttachment(last, helper.margin);
		fd.left = new FormAttachment(ccNameHint, 5 * helper.margin);
		lGenderAg.setLayoutData(fd);

		last = ccGenderAggression = (CCombo) helper.addComboBox(wNameComp, last, "AdvancedOptions.GenderAggression")[1];
		ccGenderAggression.setItems(arGenderAggression);
		ccGenderAggression.setEditable(false);

		helper.colWidth[2] = 100;
		Label spacer1 = helper.addSpacer(wNameComp, last);
		last = spacer1;

		last = ckCorrectFirstName = helper.addCheckBox(wNameComp, last, "AdvancedOptions.CorrectFirst");
		last = ckStandardizeCompany = helper.addCheckBox(wNameComp, last, "AdvancedOptions.StandardizeComp");

		return nameGroup;

	}
	
	private Group createAddressGroup(Composite parent, Control last) {

		Group addressGroup = new Group(parent, SWT.NONE);
		addressGroup.setText(getString("AddressGroup.Label"));
		helper.setLook(addressGroup);

		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, 2 * helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		addressGroup.setLayoutData(fd);

		addressGroup.setLayout(fl);

		Composite wAddressComp = new Composite(addressGroup, 0);
		helper.setLook(wAddressComp);
		wAddressComp.setLayout(new FormLayout());

		fd = new FormData();
		fd.top = new FormAttachment(0, 0);
		fd.left = new FormAttachment(0, 0);
		fd.right = new FormAttachment(100, 0);
		fd.bottom = new FormAttachment(100, 0);
		wAddressComp.setLayoutData(fd);

		last = null;
		helper.colWidth[0] = 20;
		helper.colWidth[2] = 40;

		String[] arDiacritics = { "Auto", "On", "Off" };
		last = ccDiacritics = (CCombo) helper.addComboBox(wAddressComp, last, "AdvancedOptions.Diacritics")[1];
		ccDiacritics.setItems(arDiacritics);
		ccDiacritics.setEditable(false);
		helper.colWidth[2] = 100;

		Label lDiacritics = helper.addLabel(wAddressComp, last, "AdvancedOptions.Diacritics.Description");
		fd = new FormData();
		fd.top = new FormAttachment(null, helper.margin);
		fd.left = new FormAttachment(ccDiacritics, 5 * helper.margin);
		lDiacritics.setLayoutData(fd);

		Label spacer1 = helper.addSpacer(wAddressComp, last);
		last = spacer1;

		last = ckPreferredCity = helper.addCheckBox(wAddressComp, last, "AdvancedOptions.PreferedCity");
		fd = new FormData();
		fd.top = new FormAttachment(spacer1, helper.margin);
		fd.left = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment(10, helper.margin);
		ckPreferredCity.setLayoutData(fd);

		Label lPreferredCity = helper.addLabel(wAddressComp, last, "AdvancedOptions.PreferedCity.Description");
		fd = new FormData();
		fd.top = new FormAttachment(spacer1, helper.margin);
		fd.left = new FormAttachment(ckPreferredCity, helper.margin);
		lPreferredCity.setLayoutData(fd);

		return addressGroup;

	}
	
	private Group createPhoneGroup(Composite parent, Control last) {

		Group phoneGroup = new Group(parent, SWT.NONE);
		phoneGroup.setText(getString("PhoneGroup.Label"));
		helper.setLook(phoneGroup);

		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, 2 * helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		phoneGroup.setLayoutData(fd);

		phoneGroup.setLayout(fl);

		Composite wPhoneComp = new Composite(phoneGroup, 0);
		helper.setLook(wPhoneComp);
		wPhoneComp.setLayout(new FormLayout());

		fd = new FormData();
		fd.top = new FormAttachment(0, 0);
		fd.left = new FormAttachment(0, 0);
		fd.right = new FormAttachment(100, 0);
		fd.bottom = new FormAttachment(100, 0);
		wPhoneComp.setLayoutData(fd);

		last = null;

		helper.colWidth[2] = 40;

		// Add a combo selector for the output phone format
		vOutputFormat = helper.addEnumComboBox(wPhoneComp, last, "AdvancedOptions.PhoneFormat", OutputPhoneFormat.values());

		last = vOutputFormat.getCCombo();
		helper.colWidth[2] = 100;
		helper.colWidth[0] = ((MDPersonatorDialog) dialog).getProps().getMiddlePct();

		phoneOptDescription = helper.addLabel(wPhoneComp, null, "AdvancedOptions.PhoneGroup.PhoneFormat");

		fd = new FormData();
		fd.top = new FormAttachment(null, helper.margin);
		fd.left = new FormAttachment(last, 5 * helper.margin);
		fd.right = new FormAttachment(90, 0);
		fd.bottom = new FormAttachment(100, 0);
		phoneOptDescription.setLayoutData(fd);

		return phoneGroup;

	}
	
	private Group createEmailGroup(Composite parent, Control last) {

		Group emailGroup = new Group(parent, SWT.NONE);
		emailGroup.setText(getString("EmailGroup.Label"));
		helper.setLook(emailGroup);

		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, 2 * helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		emailGroup.setLayoutData(fd);

		emailGroup.setLayout(fl);

		Composite wEmailComp = new Composite(emailGroup, 0);
		helper.setLook(wEmailComp);
		wEmailComp.setLayout(new FormLayout());

		fd = new FormData();
		fd.top = new FormAttachment(0, 0);
		fd.left = new FormAttachment(0, 0);
		fd.right = new FormAttachment(100, 0);
		fd.bottom = new FormAttachment(100, 0);
		wEmailComp.setLayoutData(fd);

		last = null;

		last = ckCorrectSyntax = helper.addCheckBox(wEmailComp, last, "AdvancedOptions.CorrectSyntax");
		fd = new FormData();
		fd.top = new FormAttachment(null, helper.margin);
		fd.left = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment(15, helper.margin);
		ckCorrectSyntax.setLayoutData(fd);

		Label lCorrectSyntax = helper.addLabel(wEmailComp, last, "AdvancedOptions.CorrectSyntax.Description");
		fd = new FormData();
		fd.top = new FormAttachment(null, helper.margin);
		fd.left = new FormAttachment(ckCorrectSyntax, helper.margin);
		lCorrectSyntax.setLayoutData(fd);

		last = ckUpdateDomain = helper.addCheckBox(wEmailComp, last, "AdvancedOptions.UpdateDomain");
		fd = new FormData();
		fd.top = new FormAttachment(ckCorrectSyntax, helper.margin);
		fd.left = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment(15, helper.margin);
		ckUpdateDomain.setLayoutData(fd);

		Label lUpdateDomain = helper.addLabel(wEmailComp, last, "AdvancedOptions.UpdateDomain.Description");
		fd = new FormData();
		fd.top = new FormAttachment(ckCorrectSyntax, helper.margin);
		fd.left = new FormAttachment(ckUpdateDomain, helper.margin);
		lUpdateDomain.setLayoutData(fd);

		last = ckLookup = helper.addCheckBox(wEmailComp, last, "AdvancedOptions.Lookup");
		fd = new FormData();
		fd.top = new FormAttachment(ckUpdateDomain, helper.margin);
		fd.left = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment(15, helper.margin);
		ckLookup.setLayoutData(fd);

		Label lLookup = helper.addLabel(wEmailComp, last, "AdvancedOptions.Lookup.Description");
		fd = new FormData();
		fd.top = new FormAttachment(ckUpdateDomain, helper.margin);
		fd.left = new FormAttachment(ckLookup, helper.margin);
		lLookup.setLayoutData(fd);

		last = ckStandardizeCasing = helper.addCheckBox(wEmailComp, last, "AdvancedOptions.StandardizeCasing");
		fd = new FormData();
		fd.top = new FormAttachment(ckLookup, helper.margin);
		fd.left = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment(15, helper.margin);
		ckStandardizeCasing.setLayoutData(fd);

		Label lStandardizeCasing = helper.addLabel(wEmailComp, last, "AdvancedOptions.StandardizeCasing.Description");
		fd = new FormData();
		fd.top = new FormAttachment(ckLookup, helper.margin);
		fd.left = new FormAttachment(ckStandardizeCasing, helper.margin);
		lStandardizeCasing.setLayoutData(fd);

		return emailGroup;

	}

	
	private void setCentricHint(String hint) {
		rCentricAuto.setSelection(false);
		rCentricAddress.setSelection(false);
		rCentricPhone.setSelection(false);
		rCentricEmail.setSelection(false);

		if ("Auto".equals(hint)) {
			rCentricAuto.setSelection(true);
		}
		if ("Address".equals(hint)) {
			rCentricAddress.setSelection(true);
		}
		if ("Phone".equals(hint)) {
			rCentricPhone.setSelection(true);
		}
		if ("Email".equals(hint)) {
			rCentricEmail.setSelection(true);
		}
	}

	private String getCentricHint() {
		String hint = "";

		if (rCentricAuto.getSelection()) {
			hint = "Auto";
		}
		if (rCentricAddress.getSelection()) {
			hint = "Address";
		}
		if (rCentricPhone.getSelection()) {
			hint = "Phone";
		}
		if (rCentricEmail.getSelection()) {
			hint = "Email";
		}

		return hint;
	}

	@Override
	protected boolean getData(Object data) {
		// Fill it in

		MDPersonatorMeta metaPersonator = (MDPersonatorMeta) data;

		metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_CENTRIC_HINT).metaValue = getCentricHint();

		metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_NAME_HINT).metaValue = ccNameHint.getText();
		metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_NAME_GENDER_POP).metaValue = ccGenderPopulation.getText();
		metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_NAME_GENDER_AGG).metaValue = ccGenderAggression.getText();
		metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_NAME_MIDDLE_LOGIC).metaValue = ccMiddleNameLogic.getText();
		metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_NAME_SALUTATION).metaValue = ccSalutationFormat.getText();
		metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_NAME_CORRECT_FIRST).metaValue = String.valueOf(ckCorrectFirstName
				.getSelection());
		metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_NAME_STANDARD_COMPANY).metaValue = String.valueOf(ckStandardizeCompany
				.getSelection());

		metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_ADDR_PREFERRED_CITY).metaValue = String.valueOf(ckPreferredCity
				.getSelection());
		metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_ADDR_DIACRITICS).metaValue = ccDiacritics.getText();

		metaPersonator.setPhoneFormat((OutputPhoneFormat) (((IStructuredSelection) vOutputFormat.getSelection()).getFirstElement()));
		metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_PHONE_FORMAT).metaValue = MDPersonatorMeta.getPhoneFormat().encode();

		metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_EMAIL_SYNTAX).metaValue = String.valueOf(ckCorrectSyntax.getSelection());
		metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_EMAIL_UPDATE_DOMAIN).metaValue = String.valueOf(ckUpdateDomain
				.getSelection());
		metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_EMAIL_DB_LOOKUP).metaValue = String.valueOf(ckLookup.getSelection());
		metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_EMAIL_STANDARD_CASING).metaValue = String.valueOf(ckStandardizeCasing
				.getSelection());

		return true;
	}

	@Override
	protected String getHelpURLKey() {
//		if(!MDPersonatorMeta.isPlugin)
//			return "MDPersonatorDialog.Help.AdvancedOptionsTab";
//		else
		return BaseMessages.getString(PKG,"MDPersonatorDialog.Plugin.Help.AdvancedOptionsTab");
	}

	@Override
	protected Class<?> getPackage() {
		return PKG;
	}

	/**
	 * @param key
	 * @param args
	 * @return
	 */
	public String getString(String key, String... args) {
		return BaseMessages.getString(PKG, "MDPersonatorDialog.AdvancedOptions." + key, args);
	}
	
	@Override
	protected String getStringPrefix() {
		return BaseMessages.getString(PKG, "MDPersonatorDialog.AdvancedOptions");
	}

	@Override
	protected void init(Object meta) {

		MDPersonatorMeta metaPersonator = (MDPersonatorMeta) meta;

		if (!Const.isEmpty(metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_CENTRIC_HINT).metaValue))
			setCentricHint(metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_CENTRIC_HINT).metaValue);

		if (!Const.isEmpty(metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_NAME_HINT).metaValue))
			ccNameHint.setText(metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_NAME_HINT).metaValue);

		if (!Const.isEmpty(metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_NAME_GENDER_POP).metaValue))
			ccGenderPopulation.setText(metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_NAME_GENDER_POP).metaValue);

		if (!Const.isEmpty(metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_NAME_GENDER_AGG).metaValue))
			ccGenderAggression.setText(metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_NAME_GENDER_AGG).metaValue);

		if (!Const.isEmpty(metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_NAME_MIDDLE_LOGIC).metaValue))
			ccMiddleNameLogic.setText(metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_NAME_MIDDLE_LOGIC).metaValue);

		if (!Const.isEmpty(metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_NAME_SALUTATION).metaValue))
			ccSalutationFormat.setText(metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_NAME_SALUTATION).metaValue);

		if (!Const.isEmpty(metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_NAME_CORRECT_FIRST).metaValue))
			ckCorrectFirstName
					.setSelection(Boolean.valueOf(metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_NAME_CORRECT_FIRST).metaValue));

		if (!Const.isEmpty(metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_NAME_STANDARD_COMPANY).metaValue))
			ckStandardizeCompany.setSelection(Boolean.valueOf(metaPersonator.personatorFields.optionFields
					.get(PersonatorFields.TAG_OPTION_NAME_STANDARD_COMPANY).metaValue));

		if (!Const.isEmpty(metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_ADDR_PREFERRED_CITY).metaValue))
			ckPreferredCity
					.setSelection(Boolean.valueOf(metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_ADDR_PREFERRED_CITY).metaValue));

		if (!Const.isEmpty(metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_ADDR_DIACRITICS).metaValue))
			ccDiacritics.setText(metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_ADDR_DIACRITICS).metaValue);

		if (!Const.isEmpty(metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_PHONE_FORMAT).metaValue)) {
			vOutputFormat.setSelection(new StructuredSelection(MDPersonatorMeta.getPhoneFormat()));
		}

		if (!Const.isEmpty(metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_EMAIL_SYNTAX).metaValue))
			ckCorrectSyntax.setSelection(Boolean.valueOf(metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_EMAIL_SYNTAX).metaValue));

		if (!Const.isEmpty(metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_EMAIL_UPDATE_DOMAIN).metaValue))
			ckUpdateDomain
					.setSelection(Boolean.valueOf(metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_EMAIL_UPDATE_DOMAIN).metaValue));

		if (!Const.isEmpty(metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_EMAIL_DB_LOOKUP).metaValue))
			ckLookup.setSelection(Boolean.valueOf(metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_EMAIL_DB_LOOKUP).metaValue));

		if (!Const.isEmpty(metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_EMAIL_STANDARD_CASING).metaValue))
			ckStandardizeCasing.setSelection(Boolean.valueOf(metaPersonator.personatorFields.optionFields
					.get(PersonatorFields.TAG_OPTION_EMAIL_STANDARD_CASING).metaValue));

		phoneOptDescription.setText(getString("PhoneGroup.PhoneFormat"));

	}

}
