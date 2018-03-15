package com.melissadata.kettle.businesscoder.ui;

import com.melissadata.cz.support.MDTab;
import com.melissadata.cz.support.MetaVal;
import com.melissadata.kettle.businesscoder.MDBusinessCoderDialog;
import com.melissadata.kettle.businesscoder.MDBusinessCoderMeta;
import com.melissadata.kettle.businesscoder.data.BusinessCoderFields;
import com.melissadata.kettle.businesscoder.support.MDBusinessCoderHelper;
import org.eclipse.swt.SWT;
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
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.step.BaseStepMeta;

import java.util.HashMap;

public class OutputAddressTab implements MDTab {

	private static Class<?>			PKG	= MDBusinessCoderMeta.class;
	private Label                 description;
	private MDBusinessCoderHelper helper;
	
	

	private Group                 businessGroup;
	private Group                 cenGroup;
	private Group                 geoGroup;

	private Text                  wAddressLine1;
	private Text                  wSuite;
	private Text                  wCity;
	private Text                  wState;
	private Text                  wPostalCode;
//	private Text                  wPhone;

	private Button                ckCensus;
	private Text                  wCensusBlock;
	private Text                  wCensusTract;
	private Text                  wCountyFIPS;
	private Text                  wCountyName;
	private Text                  wdeliveryIndicator;
	private Text                  wLatitude;
	private Text                  wLongitude;
	private Text                  wMAK;
	private Text                  wMAKbase;
	private Text                  wPlus4;
	private Text                  wPlaceName;
	private Text                  wPlaceCode;

	public OutputAddressTab(MDBusinessCoderDialog dialog) {

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
		createBusinessGroup(wComp, description);
		createGeoGroup(wComp,businessGroup);
		createCensusGroup(wComp, geoGroup);

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

		// DO nothing

	}

	@Override
	public void getData(BaseStepMeta meta) {

		MDBusinessCoderMeta bcMeta = (MDBusinessCoderMeta) meta;

		HashMap<String, MetaVal> outputFields = bcMeta.businessCoderFields.outputFields;

		outputFields.get(BusinessCoderFields.TAG_OUTPUT_ADDRESS_LINE1).metaValue = wAddressLine1.getText();
		outputFields.get(BusinessCoderFields.TAG_OUTPUT_SUITE).metaValue = wSuite.getText();
		outputFields.get(BusinessCoderFields.TAG_OUTPUT_CITY).metaValue = wCity.getText();
		outputFields.get(BusinessCoderFields.TAG_OUTPUT_STATE).metaValue = wState.getText();
		outputFields.get(BusinessCoderFields.TAG_OUTPUT_POSTAL_CODE).metaValue = wPostalCode.getText();
//		outputFields.get(BusinessCoderFields.TAG_OUTPUT_PHONE).metaValue = wPhone.getText();

		outputFields.get(BusinessCoderFields.TAG_OUTPUT_CENSUS_BLOCK).metaValue = wCensusBlock.isEnabled() ? enablString(wCensusBlock.getText()) : disablString(wCensusBlock.getText());
		outputFields.get(BusinessCoderFields.TAG_OUTPUT_CENSUS_TRACT).metaValue = wCensusTract.isEnabled() ? enablString(wCensusTract.getText()) : disablString(wCensusTract.getText());
		outputFields.get(BusinessCoderFields.TAG_OUTPUT_COUNTY_FIPS).metaValue = wCountyFIPS.isEnabled() ? enablString(wCountyFIPS.getText()) : disablString(wCountyFIPS.getText());
		outputFields.get(BusinessCoderFields.TAG_OUTPUT_COUNTY_NAME).metaValue = wCountyName.isEnabled() ? enablString(wCountyName.getText()) : disablString(wCountyName.getText());
		outputFields.get(BusinessCoderFields.TAG_OUTPUT_DELIVERY_INDICATOR).metaValue = wdeliveryIndicator.isEnabled() ? enablString(wdeliveryIndicator.getText()) : disablString(wdeliveryIndicator.getText());
		outputFields.get(BusinessCoderFields.TAG_OUTPUT_PLACE_NAME).metaValue = wPlaceName.isEnabled() ? enablString(wPlaceName.getText()) : disablString(wPlaceName.getText());
		outputFields.get(BusinessCoderFields.TAG_OUTPUT_PLACE_CODE).metaValue = wPlaceCode.isEnabled() ? enablString(wPlaceCode.getText()) : disablString(wPlaceCode.getText());

		outputFields.get(BusinessCoderFields.TAG_OUTPUT_LONGITUDE).metaValue = wLongitude.getText();
		outputFields.get(BusinessCoderFields.TAG_OUTPUT_LATITUDE).metaValue = wLatitude.getText();

		outputFields.get(BusinessCoderFields.TAG_OUTPUT_MD_ADDRESS_KEY).metaValue = wMAK.getText();
		outputFields.get(BusinessCoderFields.TAG_OUTPUT_MD_ADDRESS_KEY_BASE).metaValue = wMAKbase.getText();
		outputFields.get(BusinessCoderFields.TAG_OUTPUT_PLUS_4).metaValue = wPlus4.getText();

		bcMeta.businessCoderFields.optionFields.get(BusinessCoderFields.TAG_OPTION_INCLUDE_CENSUS).metaValue = String.valueOf(ckCensus.getSelection());
	}

	private String enablString(String dirtyString) {

		String clnString = "";
		if(dirtyString.startsWith(BusinessCoderFields.DISABLE_STRING)){
			clnString = dirtyString.substring(BusinessCoderFields.DISABLE_STRING.length());
			
		} else {
			clnString = dirtyString;
		}
		return clnString;
	}

	private String disablString(String dirtyString) {

		// Just incase bug catcher
		String clnString = "";
		if(dirtyString.startsWith(BusinessCoderFields.DISABLE_STRING)){
			clnString = dirtyString.substring(BusinessCoderFields.DISABLE_STRING.length());
			System.out.println(" Error with disable string " + dirtyString);
			
		} else {
			clnString = dirtyString;
		}
		return BusinessCoderFields.DISABLE_STRING + clnString;
		
		
	}

	@Override
	public String getHelpURLKey() {
		return BaseMessages.getString(PKG, "MDBusinessCoder.Plugin.Help.OutputAddressTab");
	}

	@Override
	public boolean init(BaseStepMeta meta) {

		MDBusinessCoderMeta bcMeta = (MDBusinessCoderMeta) meta;

		HashMap<String, MetaVal> outputFields = bcMeta.businessCoderFields.outputFields;
		HashMap<String, MetaVal> optionFields = bcMeta.businessCoderFields.optionFields;

		wAddressLine1.setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_ADDRESS_LINE1).metaValue);
		wSuite.setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_SUITE).metaValue);
		wCity.setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_CITY).metaValue);
		wState.setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_STATE).metaValue);
		wPostalCode.setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_POSTAL_CODE).metaValue);

		wCensusBlock.setText(enablString(outputFields.get(BusinessCoderFields.TAG_OUTPUT_CENSUS_BLOCK).metaValue));
		wCensusTract.setText(enablString(outputFields.get(BusinessCoderFields.TAG_OUTPUT_CENSUS_TRACT).metaValue));
		wCountyFIPS.setText(enablString(outputFields.get(BusinessCoderFields.TAG_OUTPUT_COUNTY_FIPS).metaValue));
		wCountyName.setText(enablString(outputFields.get(BusinessCoderFields.TAG_OUTPUT_COUNTY_NAME).metaValue));
		wdeliveryIndicator.setText(enablString(outputFields.get(BusinessCoderFields.TAG_OUTPUT_DELIVERY_INDICATOR).metaValue));
		wPlaceName.setText(enablString(outputFields.get(BusinessCoderFields.TAG_OUTPUT_PLACE_NAME).metaValue));
		wPlaceCode.setText(enablString(outputFields.get(BusinessCoderFields.TAG_OUTPUT_PLACE_CODE).metaValue));
		
		wLatitude.setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_LATITUDE).metaValue);
		wLongitude.setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_LONGITUDE).metaValue);
		wMAK.setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_MD_ADDRESS_KEY).metaValue);
		wMAKbase.setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_MD_ADDRESS_KEY_BASE).metaValue);
		wPlus4.setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_PLUS_4).metaValue);

		ckCensus.setSelection(Boolean.valueOf(optionFields.get(BusinessCoderFields.TAG_OPTION_INCLUDE_CENSUS).metaValue));

		enable();
		return true;
	}

	private void createBusinessGroup(Composite parent, Control last) {

		businessGroup = new Group(parent, SWT.NONE);
		businessGroup.setText(getString("OutputBusinessGroup.Label"));
		helper.setLook(businessGroup);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		businessGroup.setLayout(fl);

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin * 2);
		fd.right = new FormAttachment(100, -helper.margin);
		// fd.bottom = new FormAttachment(nicsGroup, 0);
		businessGroup.setLayoutData(fd);

		// set the column widths for proper placing
	//	helper.colWidth[0] = 30;
		last = null;
		last = wAddressLine1 = helper.addTextBox(businessGroup, last, "OutputAddressTab.AddressLine1");
		last = wSuite = helper.addTextBox(businessGroup, last, "OutputAddressTab.Suite");
		last = wCity = helper.addTextBox(businessGroup, last, "OutputAddressTab.City");
		last = wState = helper.addTextBox(businessGroup, last, "OutputAddressTab.State");
		last = wPostalCode = helper.addTextBox(businessGroup, last, "OutputAddressTab.PostalCode");
		last = wPlus4 = helper.addTextBox(businessGroup, last, "OutputAddressTab.Plus4");
		last = wMAK = helper.addTextBox(businessGroup, last, "OutputAddressTab.MAK");
		last = wMAKbase = helper.addTextBox(businessGroup, last, "OutputAddressTab.MAKbase");


	}


	
	private void createGeoGroup(Composite parent, Control last) {

		geoGroup = new Group(parent, SWT.NONE);
		geoGroup.setText(getString("OutputGeoGroup.Label"));
		helper.setLook(geoGroup);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		geoGroup.setLayout(fl);

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin * 2);
		fd.right = new FormAttachment(100, -helper.margin);
		// fd.bottom = new FormAttachment(100, helper.margin);
		geoGroup.setLayoutData(fd);

		// set the column widths for proper placing
	//	helper.colWidth[0] = 30;
		last = null;
		last = wLatitude = helper.addTextBox(geoGroup, last, "OutputAddressTab.Latitude");
		last = wLongitude = helper.addTextBox(geoGroup, last, "OutputAddressTab.Longitude");

	}



	private void createCensusGroup(Composite parent, Control last) {

		cenGroup = new Group(parent, SWT.NONE);
		cenGroup.setText(getString("OutputCensusGroup.Label"));
		helper.setLook(cenGroup);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		cenGroup.setLayout(fl);

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin * 2);
		fd.right = new FormAttachment(100, -helper.margin);
		// fd.bottom = new FormAttachment(100, helper.margin);
		cenGroup.setLayoutData(fd);

		// set the column widths for proper placing
	//	helper.colWidth[0] = 30;
		last = null;
		last = ckCensus = helper.addCheckBox(cenGroup, last, "OutputAddressTab.IncludeCensus");
		last = helper.addSpacer(cenGroup, last);
		last = wCensusBlock = helper.addTextBox(cenGroup, last, "OutputAddressTab.CensusBlock");
		last = wCensusTract = helper.addTextBox(cenGroup, last, "OutputAddressTab.CensusTract");
		last = wCountyFIPS = helper.addTextBox(cenGroup, last, "OutputAddressTab.CountyFIPS");
		last = wCountyName = helper.addTextBox(cenGroup, last, "OutputAddressTab.CountyName");
		last = wdeliveryIndicator = helper.addTextBox(cenGroup, last, "OutputAddressTab.DeliveryIndicator");
		last = wPlaceName = helper.addTextBox(cenGroup, last, "OutputAddressTab.PlaceName");
		last = wPlaceCode = helper.addTextBox(cenGroup, last, "OutputAddressTab.PlaceCode");
		
		ckCensus.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {

				// Not nessary
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {

				enable();
			}

		});
	}

	private void enable() {

		if (ckCensus.getSelection()) {

			wCensusBlock.setEnabled(true);
			wCensusTract.setEnabled(true);
			wCountyFIPS.setEnabled(true);
			wCountyName.setEnabled(true);
			wdeliveryIndicator.setEnabled(true);
			wPlaceName.setEnabled(true);
			wPlaceCode.setEnabled(true);
		} else {
			wCensusBlock.setEnabled(false);
			wCensusTract.setEnabled(false);
			wCountyFIPS.setEnabled(false);
			wCountyName.setEnabled(false);
			wdeliveryIndicator.setEnabled(false);
			wPlaceName.setEnabled(false);
			wPlaceCode.setEnabled(false);
		}
	}

	public void dispose(){
		// nothing to dispose
	}

	private String getString(String key, String... args) {

		return BaseMessages.getString(PKG, "MDBusinessCoderDialog.OutputAddressTab." + key, args);
	}

}
