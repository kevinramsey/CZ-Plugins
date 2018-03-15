package com.melissadata.kettle.businesscoder.ui;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.step.BaseStepMeta;

import com.melissadata.cz.ui.MDInputCombo;
import com.melissadata.kettle.businesscoder.MDBusinessCoderDialog;
import com.melissadata.kettle.businesscoder.MDBusinessCoderMeta;
import com.melissadata.kettle.businesscoder.data.BusinessCoderFields;
import com.melissadata.kettle.businesscoder.support.MDBusinessCoderHelper;
import com.melissadata.cz.support.MDTab;
import com.melissadata.cz.support.MetaVal;

public class InputFieldsTab implements MDTab {

	private static Class<?>			PKG	= MDBusinessCoderMeta.class;
	private MDBusinessCoderHelper	helper;
	private Label					description;
	private Group					nameGroup;
	private Group					addressGroup;
	private Group					phoneEmailGroup;
	private MDInputCombo			wCompany;
	private MDInputCombo			wAddressLine1;
	private MDInputCombo			wAddressLine2;
	private MDInputCombo			wCity;
	private MDInputCombo			wState;
	private MDInputCombo			wPostalCode;
	private MDInputCombo			wPhone;
	private MDInputCombo			wWebAddress;
	private MDInputCombo			wAddressKey;
	private MDInputCombo			wStockTicker;

	public InputFieldsTab(MDBusinessCoderDialog dialog) {

		helper = dialog.getHelper();

		// Create the tab
		CTabItem wTab = new CTabItem(dialog.getTabFolder(), SWT.NONE);
		wTab.setText(getString("Title"));
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
		description = new Label(wComp, SWT.LEFT | SWT.WRAP);
		helper.setLook(description);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.right = new FormAttachment(100, helper.margin);
		fd.top = new FormAttachment(0, helper.margin);
		description.setLayoutData(fd);
		description.setText("Map");

		// Create Groups
		createInputGroup(wComp);

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

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDTab#advancedConfigChanged()
	 */
	@Override
	public void advancedConfigChanged() {

		// Check for enablement change
		enable();
	}

	/**
	 * Called to handle enable of controls based on input settings
	 */
	public void enable() {
		// nothing to enable
	}

	@Override
	public void getData(BaseStepMeta meta) {

		// public void getData(MDBusinessCoderMeta meta) {

		MDBusinessCoderMeta bcMeta = (MDBusinessCoderMeta) meta;
		HashMap<String, MetaVal> inputFields = bcMeta.businessCoderFields.inputFields;

		inputFields.get(BusinessCoderFields.TAG_INPUT_BUSINESS_NAME).metaValue = wCompany.getValue();
		inputFields.get(BusinessCoderFields.TAG_INPUT_ADDRESS_LINE1).metaValue = wAddressLine1.getValue();
		inputFields.get(BusinessCoderFields.TAG_INPUT_ADDRESS_LINE2).metaValue = wAddressLine2.getValue();
		inputFields.get(BusinessCoderFields.TAG_INPUT_CITY).metaValue = wCity.getValue();
		inputFields.get(BusinessCoderFields.TAG_INPUT_STATE).metaValue = wState.getValue();
		inputFields.get(BusinessCoderFields.TAG_INPUT_POSTAL_CODE).metaValue = wPostalCode.getValue();
		inputFields.get(BusinessCoderFields.TAG_INPUT_PHONE).metaValue = wPhone.getValue();
		inputFields.get(BusinessCoderFields.TAG_INPUT_WEB_ADDRESS).metaValue = wWebAddress.getValue();
		inputFields.get(BusinessCoderFields.TAG_INPUT_ADDRESS_KEY).metaValue = wAddressKey.getValue();
		inputFields.get(BusinessCoderFields.TAG_INPUT_STOCK_TICKER).metaValue = wStockTicker.getValue();
	}

	@Override
	public String getHelpURLKey() {

		return BaseMessages.getString(PKG, "MDBusinessCoder.Plugin.Help.InputFieldsTab");
	}

	@Override
	public boolean init(BaseStepMeta meta) {

		MDBusinessCoderMeta bcMeta = (MDBusinessCoderMeta) meta;
		HashMap<String, MetaVal> inputFields = bcMeta.businessCoderFields.inputFields;

		wCompany.setValue(inputFields.get(BusinessCoderFields.TAG_INPUT_BUSINESS_NAME).metaValue);
		wAddressLine1.setValue(inputFields.get(BusinessCoderFields.TAG_INPUT_ADDRESS_LINE1).metaValue);
		wAddressLine2.setValue(inputFields.get(BusinessCoderFields.TAG_INPUT_ADDRESS_LINE2).metaValue);
		wCity.setValue(inputFields.get(BusinessCoderFields.TAG_INPUT_CITY).metaValue);
		wState.setValue(inputFields.get(BusinessCoderFields.TAG_INPUT_STATE).metaValue);
		wPostalCode.setValue(inputFields.get(BusinessCoderFields.TAG_INPUT_POSTAL_CODE).metaValue);
		wPhone.setValue(inputFields.get(BusinessCoderFields.TAG_INPUT_PHONE).metaValue);
		wWebAddress.setValue(inputFields.get(BusinessCoderFields.TAG_INPUT_WEB_ADDRESS).metaValue);
		wAddressKey.setValue(inputFields.get(BusinessCoderFields.TAG_INPUT_ADDRESS_KEY).metaValue);
		wStockTicker.setValue(inputFields.get(BusinessCoderFields.TAG_INPUT_STOCK_TICKER).metaValue);

		return true;
	}

	private void createAddressGroup(Composite parent) {

		// input address group
		addressGroup = new Group(parent, SWT.NONE);
		addressGroup.setText(getString("InputAddressGroup.Label"));
		helper.setLook(addressGroup);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		addressGroup.setLayout(fl);
		wAddressLine1 = helper.addInputComboBox(addressGroup, null, "InputTab.AddressLine1", "NInAddressLine1");
		wAddressLine2 = helper.addInputComboBox(addressGroup, wAddressLine1.getComboBox(), "InputTab.AddressLine2", "NInAddressLine2");
		wCity = helper.addInputComboBox(addressGroup, wAddressLine2.getComboBox(), "InputTab.City", "NInCity");
		wState = helper.addInputComboBox(addressGroup, wCity.getComboBox(), "InputTab.State", "NInState");
		wPostalCode = helper.addInputComboBox(addressGroup, wState.getComboBox(), "InputTab.Zip", "NInZip");
		wAddressKey = helper.addInputComboBox(addressGroup, wPostalCode.getComboBox(), "InputTab.AddressKey", "NInMAK");
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(nameGroup, helper.margin * 2);
		fd.right = new FormAttachment(100, 0);
		// fd.bottom = new FormAttachment(100, 0);
		addressGroup.setLayoutData(fd);
	}

	private Group createInputGroup(Composite parent) {

		createNameGroup(parent);
		createAddressGroup(parent);
		createPhoneEmailGroup(parent);
		return null;
	}

	private void createNameGroup(Composite parent) {

		nameGroup = new Group(parent, SWT.NONE);
		nameGroup.setText(getString("InputNameGroup.Label"));
		helper.setLook(nameGroup);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		nameGroup.setLayout(fl);
		// set the column widths for proper placing
		helper.colWidth[0] = 30;
		wCompany = helper.addInputComboBox(nameGroup, null, "InputTab.Company", "NInCompany");
		wStockTicker = helper.addInputComboBox(nameGroup, wCompany.getComboBox(), "InputTab.StockTicker", "NInStockTicker");

		// Set Group Location
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(description, helper.margin * 2);
		fd.right = new FormAttachment(100, 0);
		// fd.bottom = new FormAttachment(100, 0);
		nameGroup.setLayoutData(fd);
	}

	private void createPhoneEmailGroup(Composite parent) {

		phoneEmailGroup = new Group(parent, SWT.NONE);
		phoneEmailGroup.setText(getString("InputPhoneEmailGroup.Label"));
		helper.setLook(phoneEmailGroup);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		phoneEmailGroup.setLayout(fl);
		wPhone = helper.addInputComboBox(phoneEmailGroup, null, "InputTab.Phone", "NInPhone");
		wWebAddress = helper.addInputComboBox(phoneEmailGroup, wPhone.getComboBox(), "InputTab.WebAddress", "NInEmail");
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(addressGroup, helper.margin * 2);
		fd.right = new FormAttachment(100, 0);
		// fd.bottom = new FormAttachment(100, 0);
		phoneEmailGroup.setLayoutData(fd);
	}

	public void dispose(){
		// nothing to dispose
	}

	private String getString(String key, String... args) {

		return BaseMessages.getString(PKG, "MDBusinessCoderDialog.InputTab." + key, args);
	}
}
