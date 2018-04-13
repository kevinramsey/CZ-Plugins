package com.melissadata.kettle.personator.ui;


import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.pentaho.di.i18n.BaseMessages;

import com.melissadata.cz.ui.MDInputCombo;
import com.melissadata.kettle.personator.MDPersonatorDialog;
import com.melissadata.kettle.personator.MDPersonatorMeta;
import com.melissadata.kettle.personator.data.PersonatorFields;
import com.melissadata.kettle.personator.support.MDPersonatorHelper;
import com.melissadata.kettle.personator.support.MDTab;


public class InputPersonatorTab implements MDTab {

	private static Class<?> PKG = MDPersonatorMeta.class;

	private MDPersonatorHelper helper;

	private Group parsedGroup;
	private Group freeFormGroup;
	
	private Group nameGroup;
	private Group addressGroup;
	private Group phoneEmailGroup;
	private Group ipGroup;
	private Group dobGroup;
	
	private MDInputCombo wCompany;
	private MDInputCombo wFullName;
	private MDInputCombo wFirstName;
	private MDInputCombo wLastName;
	
	private MDInputCombo wAddressLine1;
	private MDInputCombo wAddressLine2;
	private MDInputCombo wCity;
	private MDInputCombo wState;
	private MDInputCombo wZip;
	private MDInputCombo wCountry;
	
	private MDInputCombo wFreeForm;
	
	private MDInputCombo wPhone;
	private MDInputCombo wEmail;

	private MDInputCombo wIPAddress;

	private MDInputCombo wBirthDay;
	private MDInputCombo wBirthMonth;
	private MDInputCombo wBirthYear;
	
	private Button rFreeForm;
	private Button rParsedAddr;
	
	private MDPersonatorDialog dialog;


	public InputPersonatorTab(MDPersonatorDialog dialog) {
		this.dialog = dialog;
		helper = dialog.getHelper();
		
		// Create the tab
		final CTabFolder wTabFolder = dialog.getTabFolder();
		final CTabItem wTab = new CTabItem(dialog.getTabFolder(), SWT.NONE);
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
		
		Label spacer = helper.addSpacer(wComp, description);
		rFreeForm = helper.addRadioButton(wComp, spacer, "InputTab.FreeForm");
		
		freeFormGroup = new Group(wComp, SWT.NONE);
		freeFormGroup.setText(getString("InputFreeFormGroup.Label"));
		helper.setLook(freeFormGroup);
		
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(rFreeForm, 2 * helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		//fd.bottom = new FormAttachment(10, -helper.margin);
		freeFormGroup.setLayoutData(fd);
		
		freeFormGroup.setLayout(fl);
		
		//Label spacer = helper.addSpacer(freeFormGroup, null);
		//rFreeForm = helper.addRadioButton(freeFormGroup, null, "InputTab.FreeForm");
		wFreeForm = helper.addInputComboBox(freeFormGroup, null, "InputTab.FreeForm.Input","NInFreeForm");
		
		Label spacer2 = helper.addSpacer(wComp, freeFormGroup);
		rParsedAddr = helper.addRadioButton(wComp, spacer2, "InputTab.ParsedAddr");
		
		parsedGroup = new Group(wComp, SWT.NONE);
		parsedGroup.setText(getString("InputParsedGroup.Label"));
		helper.setLook(parsedGroup);
		
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(rParsedAddr, 2 * helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		//fd.bottom = new FormAttachment(35, -helper.margin);
		parsedGroup.setLayoutData(fd);
		
		parsedGroup.setLayout(fl);
		// input name group
		nameGroup = new Group(parsedGroup, SWT.NONE);
		nameGroup.setText(getString("InputNameGroup.Label"));
		helper.setLook(nameGroup);

		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(5, 2 * helper.margin);
		fd.right = new FormAttachment(50, -helper.margin);
		fd.bottom = new FormAttachment(50, -helper.margin);
		nameGroup.setLayoutData(fd);
		
		nameGroup.setLayout(fl);


		// set the column widths for proper placing
		helper.colWidth[0] = 30;
		
		wCompany = helper.addInputComboBox(nameGroup, null, "InputTab.Company","NInCompany");
		wFullName = helper.addInputComboBox(nameGroup, wCompany.getComboBox(), "InputTab.FullName","NInFullName");
		wFirstName = helper.addInputComboBox(nameGroup, wFullName.getComboBox(), "InputTab.FirstName","NInFirstName");
		wLastName = helper.addInputComboBox(nameGroup, wFirstName.getComboBox(), "InputTab.LastName","NInLastName");

		// input address group
		addressGroup = new Group(parsedGroup, SWT.NONE);
		addressGroup.setText(getString("InputAddressGroup.Label"));
		helper.setLook(addressGroup);

		fd = new FormData();
		fd.left = new FormAttachment(50, helper.margin);
		fd.top = new FormAttachment(5, 2 * helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		fd.bottom = new FormAttachment(50, -helper.margin);
		addressGroup.setLayoutData(fd);

		addressGroup.setLayout(fl);
		wAddressLine1 = helper.addInputComboBox(addressGroup, null, "InputTab.AddressLine1","NInAddressLine1");
		wAddressLine2 = helper.addInputComboBox(addressGroup, wAddressLine1.getComboBox(), "InputTab.AddressLine2","NInAddressLine2");
		wCity = helper.addInputComboBox(addressGroup, wAddressLine2.getComboBox(), "InputTab.City","NInCity");
		wState = helper.addInputComboBox(addressGroup, wCity.getComboBox(), "InputTab.State","NInState");
		wZip = helper.addInputComboBox(addressGroup, wState.getComboBox(), "InputTab.Zip","NInZip");
		wCountry = helper.addInputComboBox(addressGroup, wZip.getComboBox(), "InputTab.Country","NInCountry");

		// input phone Email group
		phoneEmailGroup = new Group(parsedGroup, SWT.NONE);
		phoneEmailGroup.setText(getString("InputPhoneEmailGroup.Label"));
		helper.setLook(phoneEmailGroup);
		fd = new FormData();
		fd.left = new FormAttachment(50, helper.margin);
		fd.top = new FormAttachment(nameGroup, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		phoneEmailGroup.setLayoutData(fd);
		phoneEmailGroup.setLayout(fl);
		wPhone = helper.addInputComboBox(phoneEmailGroup, null, "InputTab.Phone","NInPhone");
		wEmail = helper.addInputComboBox(phoneEmailGroup, wPhone.getComboBox(), "InputTab.Email","NInEmail");

		// input IP group
		ipGroup = new Group(parsedGroup, SWT.NONE);
		ipGroup.setText(getString("InputIPGroup.Label"));
		helper.setLook(ipGroup);
		fd = new FormData();
		fd.left = new FormAttachment(50, helper.margin);
		fd.top = new FormAttachment(phoneEmailGroup, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		ipGroup.setLayoutData(fd);
		ipGroup.setLayout(fl);
		wIPAddress = helper.addInputComboBox(ipGroup, null, "InputTab.IP","NInIP");

		// input DOB group
		dobGroup = new Group(parsedGroup, SWT.NONE);
		dobGroup.setText(getString("InputDOBGroup.Label"));
		helper.setLook(dobGroup);
		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(nameGroup, helper.margin);
		fd.right = new FormAttachment(50, -helper.margin);
		dobGroup.setLayoutData(fd);
		dobGroup.setLayout(fl);
		wBirthDay = helper.addInputComboBox(dobGroup, null, "InputTab.BirthDay","NInBirthDay");
		wBirthMonth = helper.addInputComboBox(dobGroup, null, "InputTab.BirthMonth","NInBirthMonth");
		wBirthYear = helper.addInputComboBox(dobGroup, null, "InputTab.BirthYear","NInBirthYear");
		
		wCompany.getComboBox().setToolTipText(getString("Company.Tooltip"));
		wFullName.getComboBox().setToolTipText(getString("FullName.Tooltip"));
		wFirstName.getComboBox().setToolTipText(getString("FirstName.Tooltip"));
		wLastName.getComboBox().setToolTipText(getString("LastName.Tooltip"));
		wAddressLine1.getComboBox().setToolTipText(getString("AddressLine1.Tooltip"));
		wAddressLine2.getComboBox().setToolTipText(getString("AddressLine2.Tooltip"));
		wCity.getComboBox().setToolTipText(getString("City.Tooltip"));
		wState.getComboBox().setToolTipText(getString("State.Tooltip"));
		wZip.getComboBox().setToolTipText(getString("Zip.Tooltip"));
		wCountry.getComboBox().setToolTipText(getString("Country.Tooltip"));
		wPhone.getComboBox().setToolTipText(getString("Phone.Tooltip"));
		wEmail.getComboBox().setToolTipText(getString("Email.Tooltip"));
		wIPAddress.getComboBox().setToolTipText(getString("IP.Tooltip"));
		wBirthDay.getComboBox().setToolTipText(getString("BirthDay.Tooltip"));
		wBirthMonth.getComboBox().setToolTipText(getString("BirthMonth.Tooltip"));
		wBirthYear.getComboBox().setToolTipText(getString("BirthYear.Tooltip"));
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
		
		wTabFolder.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent event) {
				widgetSelected(event);
			}

			public void widgetSelected(SelectionEvent event) {
				if (wTabFolder.getSelection() == wTab) {
					setMapping();
				}
			}
			
		});
		
		rFreeForm.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent event) {
				widgetSelected(event);
			}

			public void widgetSelected(SelectionEvent event) {
				enable();
			}
			
		});
		
		rParsedAddr.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent event) {
				widgetSelected(event);
			}

			public void widgetSelected(SelectionEvent event) {
				enable();
			}
			
		});
		
		
	}
	
	private void setMapping(){
		dialog.enableMapping();
	}

	/**
	 * Loads the meta data into the dialog tab
	 * 
	 * @param metaPersonator
	 * @return 
	 */
	public boolean init(MDPersonatorMeta metaPersonator) {

		rFreeForm.setSelection(Boolean.valueOf(metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_FREE_FORM).metaValue));
		rParsedAddr.setSelection(Boolean.valueOf(metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_PARSED_ADDR).metaValue));
		// Load the controls
		wCompany.setValue(metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_COMPANY).metaValue);
		wFullName.setValue(metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_FULL_NAME).metaValue);
		wFirstName.setValue(metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_FIRST_NAME).metaValue);
		wLastName.setValue(metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_LAST_NAME).metaValue);
		
		wAddressLine1.setValue(metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_ADDRESS_LINE1).metaValue);
		wAddressLine2.setValue(metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_ADDRESS_LINE2).metaValue);
		wCity.setValue(metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_CITY).metaValue);
		wState.setValue(metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_STATE).metaValue);
		wZip.setValue(metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_ZIP).metaValue);
		wCountry.setValue(metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_COUNTRY).metaValue);
		
		wFreeForm.setValue(metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_FREE_FORM).metaValue);
		
		wPhone.setValue(metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_PHONE).metaValue);
		wEmail.setValue(metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_EMAIL).metaValue);

		wBirthDay.setValue(metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_DOB_BIRTHDAY).metaValue);
		wBirthMonth.setValue(metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_DOB_BIRTHMONTH).metaValue);
		wBirthYear.setValue(metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_DOB_BIRTHYEAR).metaValue);
		// Set initial enable
		enable();

		return true;
	}

	/**
	 * 
	 * Loads the dialog data into the meta structure
	 */
	public void getData(MDPersonatorMeta metaPersonator) {

		if(rParsedAddr.getSelection()){
			metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_FREE_FORM).metaValue = null;
			
			metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_COMPANY).metaValue = wCompany.getValue();
			metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_FULL_NAME).metaValue = wFullName.getValue();
			metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_FIRST_NAME).metaValue = wFirstName.getValue();
			metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_LAST_NAME).metaValue = wLastName.getValue();

			metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_ADDRESS_LINE1).metaValue = wAddressLine1.getValue();
			metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_ADDRESS_LINE2).metaValue = wAddressLine2.getValue();
			metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_CITY).metaValue = wCity.getValue();
			metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_STATE).metaValue = wState.getValue();
			metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_ZIP).metaValue = wZip.getValue();
			metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_COUNTRY).metaValue = wCountry.getValue();
	
			metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_PHONE).metaValue = wPhone.getValue();
			metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_EMAIL).metaValue = wEmail.getValue();

			metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_IP_ADDRESS).metaValue = wIPAddress.getValue();

			metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_DOB_BIRTHDAY).metaValue = wBirthDay.getValue();
			metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_DOB_BIRTHMONTH).metaValue = wBirthMonth.getValue();
			metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_DOB_BIRTHYEAR).metaValue = wBirthYear.getValue();
		
		} else {
			metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_FREE_FORM).metaValue = wFreeForm.getValue();
			
			metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_COMPANY).metaValue = null;
			metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_FULL_NAME).metaValue = null;
			metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_FIRST_NAME).metaValue = null;
			metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_LAST_NAME).metaValue = null;

			metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_ADDRESS_LINE1).metaValue = null;
			metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_ADDRESS_LINE2).metaValue = null;
			metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_CITY).metaValue = null;
			metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_STATE).metaValue = null;
			metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_ZIP).metaValue = null;
			metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_COUNTRY).metaValue = null;
	
			metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_PHONE).metaValue = null;
			metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_EMAIL).metaValue = null;

			metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_IP_ADDRESS).metaValue = null;

			metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_DOB_BIRTHDAY).metaValue = null;
			metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_DOB_BIRTHMONTH).metaValue = null;
			metaPersonator.personatorFields.inputFields.get(PersonatorFields.TAG_INPUT_DOB_BIRTHYEAR).metaValue = null;
			
		}
		metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_FREE_FORM).metaValue = String.valueOf(rFreeForm.getSelection());
		metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_PARSED_ADDR).metaValue = String.valueOf(rParsedAddr.getSelection());
		
	
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDTab#advancedConfigChanged()
	 */
	public void advancedConfigChanged() {
		// Check for enablement change
		enable();
	}
	
	public void setParsedFieldSelection(MDPersonatorMeta metaPersonator){
		rFreeForm.setSelection(false);
		rParsedAddr.setSelection(true);
		
		metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_FREE_FORM).metaValue = String.valueOf(rFreeForm.getSelection());
		metaPersonator.personatorFields.optionFields.get(PersonatorFields.TAG_OPTION_PARSED_ADDR).metaValue = String.valueOf(rParsedAddr.getSelection());
	}

	/**
	 * Called to handle enable of controls based on input settings
	 */
	public void enable() {
		//dialog.enableMapping();
		if(rFreeForm.getSelection()){
			freeFormGroup.setEnabled(true);
			parsedGroup.setEnabled(false);
			addressGroup.setEnabled(false);
			nameGroup.setEnabled(false);
			phoneEmailGroup.setEnabled(false);
			ipGroup.setEnabled(false);
			dobGroup.setEnabled(false);

			wFreeForm.setEnabled(true);
			
			wAddressLine1.setEnabled(false);
			wAddressLine2.setEnabled(false);
			wCity.setEnabled(false);
			wState.setEnabled(false);
			wZip.setEnabled(false);
			wCountry.setEnabled(false);
			
			wCompany.setEnabled(false);
			wFirstName.setEnabled(false);
			wLastName.setEnabled(false);
			wFullName.setEnabled(false);
			wEmail.setEnabled(false);
			wPhone.setEnabled(false);

			wIPAddress.setEnabled(false);

			wBirthDay.setEnabled(false);
			wBirthMonth.setEnabled(false);
			wBirthYear.setEnabled(false);
			
		} else if(rParsedAddr.getSelection()){
			freeFormGroup.setEnabled(false);
			parsedGroup.setEnabled(true);
			addressGroup.setEnabled(true);
			nameGroup.setEnabled(true);
			phoneEmailGroup.setEnabled(true);
			ipGroup.setEnabled(true);
			dobGroup.setEnabled(false);
			
			wFreeForm.setEnabled(false);
			
			wAddressLine1.setEnabled(true);
			wAddressLine2.setEnabled(true);
			wCity.setEnabled(true);
			wState.setEnabled(true);
			wZip.setEnabled(true);
			wCountry.setEnabled(true);
			
			wCompany.setEnabled(true);
			wFirstName.setEnabled(true);
			wLastName.setEnabled(true);
			wFullName.setEnabled(true);
			wEmail.setEnabled(true);
			wPhone.setEnabled(true);

			wIPAddress.setEnabled(true);

			wBirthDay.setEnabled(true);
			wBirthMonth.setEnabled(true);
			wBirthYear.setEnabled(true);
			
		}
		
	}

	/**
	 * @param key
	 * @param args
	 * @return
	 */
	private String getString(String key, String... args) {
		return BaseMessages.getString(PKG, "MDPersonatorDialog.InputTab." + key, args);
	}
	/**
	 * @return The URL for help information
	 */
	public String getHelpURLKey() {
//		if(!MDPersonatorMeta.isPlugin)
//			return "MDPersonatorDialog.Help.InputTab";
//		else
		return BaseMessages.getString(PKG,"MDPersonatorDialog.Plugin.Help.InputTab");
	}

}
