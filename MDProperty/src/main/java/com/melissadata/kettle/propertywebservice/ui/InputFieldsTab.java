package com.melissadata.kettle.propertywebservice.ui;

import java.util.HashMap;

import javax.swing.JOptionPane;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.step.BaseStepMeta;

import com.melissadata.cz.ui.MDInputCombo;
import com.melissadata.kettle.propertywebservice.MDPropertyWebServiceDialog;
import com.melissadata.kettle.propertywebservice.MDPropertyWebServiceMeta;
import com.melissadata.kettle.propertywebservice.data.PropertyWebServiceFields;
import com.melissadata.kettle.propertywebservice.support.MDPropertyWebServiceHelper;
import com.melissadata.cz.support.MDTab;
import com.melissadata.cz.support.MetaVal;

public class InputFieldsTab implements MDTab {

	private static Class<?>			PKG	= MDPropertyWebServiceMeta.class;
	private MDPropertyWebServiceHelper	helper;
	private Label					description;
	private Group					addressGroup;
	private Group					apnFipsGroup;

	private MDInputCombo			wAddress_Key;
	private MDInputCombo			wAPN;
	private MDInputCombo			wFIPS;
	
	public InputFieldsTab(MDPropertyWebServiceDialog dialog) {

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
		createInputGroups(wComp);

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


		MDPropertyWebServiceMeta bcMeta = (MDPropertyWebServiceMeta) meta;
		HashMap<String, MetaVal> inputFields = bcMeta.propertyWebServiceFields.inputFields;

		inputFields.get(PropertyWebServiceFields.TAG_INPUT_ADDRESS_KEY).metaValue = wAddress_Key.getValue();
		inputFields.get(PropertyWebServiceFields.TAG_INPUT_APN).metaValue = wAPN.getValue();
		inputFields.get(PropertyWebServiceFields.TAG_INPUT_FIPS).metaValue = wFIPS.getValue();
	}
	

	@Override
	public String getHelpURLKey() {
		return BaseMessages.getString(PKG,"MDPropertyWebService.Plugin.Help.InputFieldsTab");
	}

	@Override
	public boolean init(BaseStepMeta meta) {

		MDPropertyWebServiceMeta bcMeta = (MDPropertyWebServiceMeta) meta;
		HashMap<String, MetaVal> inputFields = bcMeta.propertyWebServiceFields.inputFields;


		wAddress_Key.setValue(inputFields.get(PropertyWebServiceFields.TAG_INPUT_ADDRESS_KEY).metaValue);
		wAPN.setValue(inputFields.get(PropertyWebServiceFields.TAG_INPUT_APN).metaValue);
		wFIPS.setValue(inputFields.get(PropertyWebServiceFields.TAG_INPUT_FIPS).metaValue);


		return true;
	}

	private Group createInputGroups(Composite parent) {

		Label spacer = helper.addSpacer(parent, description);
		createAddressGroup(parent, spacer);
		createAPN_FIPSGroup(parent, addressGroup);
		return null;
	}
	
	private void createAPN_FIPSGroup(Composite parent, Control last) {

		apnFipsGroup = new Group(parent, SWT.NONE);
		apnFipsGroup.setText(getString("InputAPN_FIPSGroup.Label"));
		helper.setLook(apnFipsGroup);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		apnFipsGroup.setLayout(fl);
		// set the column widths for proper placing
		helper.colWidth[0] = 30;
	//	wAddress_Key 	= helper.addInputComboBox(addressGroup, null, "InputTab.Address_Key", "NInAddress_Key");	
		wAPN 			= helper.addInputComboBox(apnFipsGroup, null, "InputTab.APN", "NInAPN");
		wFIPS 			= helper.addInputComboBox(apnFipsGroup, wAPN.getComboBox(), "InputTab.FIPS", "NInFIPS");
		
	// Set Group Location
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(last, helper.margin * 2);
		fd.right = new FormAttachment(100, 0);
	 // fd.bottom = new FormAttachment(100, 0);
		apnFipsGroup.setLayoutData(fd);
				
	}

	private void createAddressGroup(Composite parent, Control last) {

		addressGroup = new Group(parent, SWT.NONE);
		addressGroup.setText(getString("InputAddressGroup.Label"));
		helper.setLook(addressGroup);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		addressGroup.setLayout(fl);
		// set the column widths for proper placing
		helper.colWidth[0] = 30;
		wAddress_Key 	= helper.addInputComboBox(addressGroup, null, "InputTab.Address_Key", "NInAddress_Key");	
	//	wAPN 			= helper.addInputComboBox(addressGroup, wAddress_Key.getComboBox(), "InputTab.APN", "NInAPN");
	//	wFIPS 			= helper.addInputComboBox(addressGroup, wAPN.getComboBox(), "InputTab.FIPS", "NInFIPS");
		
	// Set Group Location
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(last, helper.margin * 2);
		fd.right = new FormAttachment(100, 0);
	 // fd.bottom = new FormAttachment(100, 0);
		addressGroup.setLayoutData(fd);
				
	}
	@Override
	public void dispose(){

	}

	private String getString(String key, String... args) {

		return BaseMessages.getString(PKG, "MDPropertyWebServiceDialog.InputTab." + key, args);
	}
}
