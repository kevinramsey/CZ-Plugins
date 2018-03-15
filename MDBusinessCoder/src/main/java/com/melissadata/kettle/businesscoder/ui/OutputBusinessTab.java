package com.melissadata.kettle.businesscoder.ui;

/**
 * Created by Kevin on 3/16/2017.
 */

import com.melissadata.cz.support.MDTab;
import com.melissadata.cz.support.MetaVal;
import com.melissadata.kettle.businesscoder.MDBusinessCoderDialog;
import com.melissadata.kettle.businesscoder.MDBusinessCoderMeta;
import com.melissadata.kettle.businesscoder.data.BusinessCoderFields;
import com.melissadata.kettle.businesscoder.support.MDBusinessCoderHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.step.BaseStepMeta;

import java.util.HashMap;

public class OutputBusinessTab implements MDTab {

	private static Class<?>			PKG	= MDBusinessCoderMeta.class;
	private Label                 description;
	private MDBusinessCoderHelper helper;



//	private Group					businessGroup;
	private Group                 demosGroup;
	private Group                 nicsGroup;
	private Group                 sicGroup;
//	private Group					cenGroup;
//	private Group 					geoGroup;

//	private Text					wCompanyName;
//	private Text					wAddressLine1;
//	private Text					wSuite;
//	private Text					wCity;
//	private Text					wState;
//	private Text					wPostalCode;
//	private Text					wPhone;
//	private Text					wWebAddress;
	private Text                  wStockTicker;

// private Text wFemaleOwned;
// private Text wHomeBasedBusiness;

	private Text					wLocationType;
	private Text					wTotalEmployeesEstimate;
// private Text wLocalEmployeesEstimate;

	private Text					wTotalSalesEstimate;
// private Text wLocalSalesEstimate;
// private Text wSmallBusiness;

	private Text					wNAICSCode;
	private Text					wNAICSCode2;
	private Text					wNAICSCode3;
	private Text					wNAICSDescription;
	private Text					wNAICSDescription2;
	private Text					wNAICSDescription3;

	private Text					wSICCode;
	private Text					wSICCode2;
	private Text					wSICCode3;
	private Text					wSICDescription;
	private Text					wSICDescription2;
	private Text                  wSICDescription3;

//	private Button					ckCensus;
//	private Text					wCensusBlock;
//	private Text					wCensusTract;
//	private Text					wCountyFIPS;
//	private Text					wCountyName;
//	private Text					wdeliveryIndicator;
//	private Text					wLatitude;
//	private Text					wLongitude;
//	private Text					wMAK;
//	private Text					wMAKbase;
//	private Text					wPlus4;
//	private Text					wPlaceName;
//	private Text					wPlaceCode;

	public OutputBusinessTab(MDBusinessCoderDialog dialog) {

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
	//	createBusinessGroup(wComp, description);
	//	createCensusGroup(wComp, description);
		createNAICSGroup(wComp, description);
		createSICGroup(wComp, nicsGroup);
		createDemosGroup(wComp, sicGroup);
//		createGeoGroup(wComp,demosGroup);

//		// reset business Gorup so it looks right
//
//		fd = new FormData();
//		fd.left = new FormAttachment(0, helper.margin);
//		fd.top = new FormAttachment(description, helper.margin * 2);
//		fd.right = new FormAttachment(50, -helper.margin);
//		//	fd.bottom = new FormAttachment(nicsGroup, -helper.margin);
//		businessGroup.setLayoutData(fd);
//
//		fd = new FormData();
//		fd.left = new FormAttachment(0, helper.margin);
//		fd.top = new FormAttachment(businessGroup, helper.margin);
//		fd.right = new FormAttachment(50, -helper.margin);
//		fd.bottom = new FormAttachment(demosGroup, -helper.margin);
//		geoGroup.setLayoutData(fd);
//
//		fd = new FormData();
//		fd.left = new FormAttachment(0, helper.margin);
//		fd.top = new FormAttachment(geoGroup, helper.margin);
//		fd.right = new FormAttachment(50, -helper.margin);
//		//fd.bottom = new FormAttachment(nicsGroup, -helper.margin);
//		cenGroup.setLayoutData(fd);
//
//		fd = new FormData();
//		fd.left = new FormAttachment(50, helper.margin);
//		fd.top = new FormAttachment(description, helper.margin * 2);
//		fd.right = new FormAttachment(100, -helper.margin);
//		//fd.bottom = new FormAttachment(nicsGroup, helper.margin);
//		nicsGroup.setLayoutData(fd);
//
//		fd = new FormData();
//		fd.left = new FormAttachment(50, helper.margin);
//		fd.top = new FormAttachment(nicsGroup, helper.margin);
//		fd.right = new FormAttachment(100, -helper.margin);
////		fd.bottom = new FormAttachment(cenGroup, - helper.margin);
//		sicGroup.setLayoutData(fd);
//
//		fd = new FormData();
//		fd.left = new FormAttachment(50, helper.margin);
//		fd.top = new FormAttachment(sicGroup, helper.margin);
//		fd.right = new FormAttachment(100, -helper.margin);
//		//	fd.bottom = new FormAttachment(tt, -helper.margin);
//		demosGroup.setLayoutData(fd);

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

//		outputFields.get(BusinessCoderFields.TAG_OUTPUT_COMPANY_NAME).metaValue = wCompanyName.getText();
//
//		outputFields.get(BusinessCoderFields.TAG_OUTPUT_ADDRESS_LINE1).metaValue = wAddressLine1.getText();
//		outputFields.get(BusinessCoderFields.TAG_OUTPUT_SUITE).metaValue = wSuite.getText();
//		outputFields.get(BusinessCoderFields.TAG_OUTPUT_CITY).metaValue = wCity.getText();
//		outputFields.get(BusinessCoderFields.TAG_OUTPUT_STATE).metaValue = wState.getText();
//		outputFields.get(BusinessCoderFields.TAG_OUTPUT_POSTAL_CODE).metaValue = wPostalCode.getText();
//		outputFields.get(BusinessCoderFields.TAG_OUTPUT_PHONE).metaValue = wPhone.getText();
//		outputFields.get(BusinessCoderFields.TAG_OUTPUT_WEB_ADDRESS).metaValue = wWebAddress.getText();
		outputFields.get(BusinessCoderFields.TAG_OUTPUT_STOCK_TICKER).metaValue = wStockTicker.getText();
		// outputFields.get(BusinessCoderFields.TAG_OUTPUT_FEMALE_OWNED).metaValue = wFemaleOwned.getText();
		// outputFields.get(BusinessCoderFields.TAG_OUTPUT_HOME_BASED_BUSINESS).metaValue = wHomeBasedBusiness.getText();
		outputFields.get(BusinessCoderFields.TAG_OUTPUT_LOCATION_TYPE).metaValue = wLocationType.getText();
		outputFields.get(BusinessCoderFields.TAG_OUTPUT_EMPLOYEES_ESTIMATE).metaValue = wTotalEmployeesEstimate.getText();
		// outputFields.get(BusinessCoderFields.TAG_OUTPUT_LOCAL_EMPLOYEES_ESTIMATE).metaValue =
// wLocalEmployeesEstimate.getText();
		outputFields.get(BusinessCoderFields.TAG_OUTPUT_SALES_ESTIMATE).metaValue = wTotalSalesEstimate.getText();
		// outputFields.get(BusinessCoderFields.TAG_OUTPUT_LOCAL_SALES_ESTIMATE).metaValue = wLocalSalesEstimate.getText();
		// outputFields.get(BusinessCoderFields.TAG_OUTPUT_SMALL_BUSINESS).metaValue = wSmallBusiness.getText();
		outputFields.get(BusinessCoderFields.TAG_OUTPUT_SIC_CODE).metaValue = wSICCode.getText();
		outputFields.get(BusinessCoderFields.TAG_OUTPUT_SIC_DESCRIPTION).metaValue = wSICDescription.getText();
		outputFields.get(BusinessCoderFields.TAG_OUTPUT_SIC_CODE2).metaValue = wSICCode2.getText();
		outputFields.get(BusinessCoderFields.TAG_OUTPUT_SIC_DESCRIPTION2).metaValue = wSICDescription2.getText();
		outputFields.get(BusinessCoderFields.TAG_OUTPUT_SIC_CODE3).metaValue = wSICCode3.getText();
		outputFields.get(BusinessCoderFields.TAG_OUTPUT_SIC_DESCRIPTION3).metaValue = wSICDescription3.getText();
		outputFields.get(BusinessCoderFields.TAG_OUTPUT_NAICS_CODE).metaValue = wNAICSCode.getText();
		outputFields.get(BusinessCoderFields.TAG_OUTPUT_NAICS_DESCRIPTION).metaValue = wNAICSDescription.getText();
		outputFields.get(BusinessCoderFields.TAG_OUTPUT_NAICS_CODE2).metaValue = wNAICSCode2.getText();
		outputFields.get(BusinessCoderFields.TAG_OUTPUT_NAICS_DESCRIPTION2).metaValue = wNAICSDescription2.getText();
		outputFields.get(BusinessCoderFields.TAG_OUTPUT_NAICS_CODE3).metaValue = wNAICSCode3.getText();
		outputFields.get(BusinessCoderFields.TAG_OUTPUT_NAICS_DESCRIPTION3).metaValue = wNAICSDescription3.getText();

//		outputFields.get(BusinessCoderFields.TAG_OUTPUT_CENSUS_BLOCK).metaValue = wCensusBlock.isEnabled() ? enablString(wCensusBlock.getText()) : disablString(wCensusBlock.getText());
//		outputFields.get(BusinessCoderFields.TAG_OUTPUT_CENSUS_TRACT).metaValue = wCensusTract.isEnabled() ? enablString(wCensusTract.getText()) : disablString(wCensusTract.getText());
//		outputFields.get(BusinessCoderFields.TAG_OUTPUT_COUNTY_FIPS).metaValue = wCountyFIPS.isEnabled() ? enablString(wCountyFIPS.getText()) : disablString(wCountyFIPS.getText());
//		outputFields.get(BusinessCoderFields.TAG_OUTPUT_COUNTY_NAME).metaValue = wCountyName.isEnabled() ? enablString(wCountyName.getText()) : disablString(wCountyName.getText());
//		outputFields.get(BusinessCoderFields.TAG_OUTPUT_DELIVERY_INDICATOR).metaValue = wdeliveryIndicator.isEnabled() ? enablString(wdeliveryIndicator.getText()) : disablString(wdeliveryIndicator.getText());
//		outputFields.get(BusinessCoderFields.TAG_OUTPUT_PLACE_NAME).metaValue = wPlaceName.isEnabled() ? enablString(wPlaceName.getText()) : disablString(wPlaceName.getText());
//		outputFields.get(BusinessCoderFields.TAG_OUTPUT_PLACE_CODE).metaValue = wPlaceCode.isEnabled() ? enablString(wPlaceCode.getText()) : disablString(wPlaceCode.getText());
//
//
//		outputFields.get(BusinessCoderFields.TAG_OUTPUT_LONGITUDE).metaValue = wLongitude.getText();
//		outputFields.get(BusinessCoderFields.TAG_OUTPUT_LATITUDE).metaValue = wLatitude.getText();
//
//		outputFields.get(BusinessCoderFields.TAG_OUTPUT_MD_ADDRESS_KEY).metaValue = wMAK.getText();
//		outputFields.get(BusinessCoderFields.TAG_OUTPUT_MD_ADDRESS_KEY_BASE).metaValue = wMAKbase.getText();
//		outputFields.get(BusinessCoderFields.TAG_OUTPUT_PLUS_4).metaValue = wPlus4.getText();

//		bcMeta.businessCoderFields.optionFields.get(BusinessCoderFields.TAG_OPTION_INCLUDE_CENSUS).metaValue = String.valueOf(ckCensus.getSelection());
	}

//	private String enablString(String dirtyString) {
//
//		String clnString = "";
//		if(dirtyString.startsWith(BusinessCoderFields.DISABLE_STRING)){
//			clnString = dirtyString.substring(BusinessCoderFields.DISABLE_STRING.length());
//
//		} else {
//			clnString = dirtyString;
//		}
//		return clnString;
//	}
//
//	private String disablString(String dirtyString) {
//
//		// Just incase bug catcher
//		String clnString = "";
//		if(dirtyString.startsWith(BusinessCoderFields.DISABLE_STRING)){
//			clnString = dirtyString.substring(BusinessCoderFields.DISABLE_STRING.length());
//			System.out.println(" Error with disable string " + dirtyString);
//
//		} else {
//			clnString = dirtyString;
//		}
//		return BusinessCoderFields.DISABLE_STRING + clnString;
//
//
//	}

	@Override
	public String getHelpURLKey() {
		return BaseMessages.getString(PKG, "MDBusinessCoder.Plugin.Help.OutputInfoTab");
	}

	@Override
	public boolean init(BaseStepMeta meta) {

		MDBusinessCoderMeta bcMeta = (MDBusinessCoderMeta) meta;

		HashMap<String, MetaVal> outputFields = bcMeta.businessCoderFields.outputFields;
		HashMap<String, MetaVal> optionFields = bcMeta.businessCoderFields.optionFields;

//		wCompanyName.setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_COMPANY_NAME).metaValue);
//		wAddressLine1.setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_ADDRESS_LINE1).metaValue);
//		wSuite.setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_SUITE).metaValue);
//		wCity.setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_CITY).metaValue);
//		wState.setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_STATE).metaValue);
//		wPostalCode.setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_POSTAL_CODE).metaValue);
//		wPhone.setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_PHONE).metaValue);
//		wWebAddress.setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_WEB_ADDRESS).metaValue);
		wStockTicker.setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_STOCK_TICKER).metaValue);
		// wFemaleOwned.setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_FEMALE_OWNED).metaValue);
		// wHomeBasedBusiness.setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_HOME_BASED_BUSINESS).metaValue);
		wLocationType.setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_LOCATION_TYPE).metaValue);
		wTotalEmployeesEstimate.setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_EMPLOYEES_ESTIMATE).metaValue);
		// wLocalEmployeesEstimate.setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_LOCAL_EMPLOYEES_ESTIMATE).metaValue);
		wTotalSalesEstimate.setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_SALES_ESTIMATE).metaValue);
		// wLocalSalesEstimate.setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_LOCAL_SALES_ESTIMATE).metaValue);
		// wSmallBusiness.setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_SMALL_BUSINESS).metaValue);
		wSICCode.setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_SIC_CODE).metaValue);
		wSICDescription.setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_SIC_DESCRIPTION).metaValue);
		wSICCode2.setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_SIC_CODE2).metaValue);
		wSICDescription2.setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_SIC_DESCRIPTION2).metaValue);
		wSICCode3.setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_SIC_CODE3).metaValue);
		wSICDescription3.setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_SIC_DESCRIPTION3).metaValue);
		wNAICSCode.setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_NAICS_CODE).metaValue);
		wNAICSDescription.setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_NAICS_DESCRIPTION).metaValue);

		wNAICSCode2.setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_NAICS_CODE2).metaValue);
		wNAICSDescription2.setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_NAICS_DESCRIPTION2).metaValue);

		wNAICSCode3.setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_NAICS_CODE3).metaValue);
		wNAICSDescription3.setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_NAICS_DESCRIPTION3).metaValue);

//		wCensusBlock.setText(enablString(outputFields.get(BusinessCoderFields.TAG_OUTPUT_CENSUS_BLOCK).metaValue));
//		wCensusTract.setText(enablString(outputFields.get(BusinessCoderFields.TAG_OUTPUT_CENSUS_TRACT).metaValue));
//		wCountyFIPS.setText(enablString(outputFields.get(BusinessCoderFields.TAG_OUTPUT_COUNTY_FIPS).metaValue));
//		wCountyName.setText(enablString(outputFields.get(BusinessCoderFields.TAG_OUTPUT_COUNTY_NAME).metaValue));
//		wdeliveryIndicator.setText(enablString(outputFields.get(BusinessCoderFields.TAG_OUTPUT_DELIVERY_INDICATOR).metaValue));
//		wPlaceName.setText(enablString(outputFields.get(BusinessCoderFields.TAG_OUTPUT_PLACE_NAME).metaValue));
//		wPlaceCode.setText(enablString(outputFields.get(BusinessCoderFields.TAG_OUTPUT_PLACE_CODE).metaValue));
//
//		wLatitude.setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_LATITUDE).metaValue);
//		wLongitude.setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_LONGITUDE).metaValue);
//		wMAK.setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_MD_ADDRESS_KEY).metaValue);
//		wMAKbase.setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_MD_ADDRESS_KEY_BASE).metaValue);
//		wPlus4.setText(outputFields.get(BusinessCoderFields.TAG_OUTPUT_PLUS_4).metaValue);
//
//
//		ckCensus.setSelection(Boolean.valueOf(optionFields.get(BusinessCoderFields.TAG_OPTION_INCLUDE_CENSUS).metaValue));

		enable();
		return true;
	}

//	private void createBusinessGroup(Composite parent, Control last) {
//
//		businessGroup = new Group(parent, SWT.NONE);
//		businessGroup.setText(getString("OutputBusinessGroup.Label"));
//		helper.setLook(businessGroup);
//		FormLayout fl = new FormLayout();
//		fl.marginWidth = 3;
//		fl.marginHeight = 3;
//		businessGroup.setLayout(fl);
//
//		FormData fd = new FormData();
//		fd.left = new FormAttachment(0, helper.margin);
//		fd.top = new FormAttachment(last, helper.margin * 2);
//		fd.right = new FormAttachment(50, -helper.margin);
//		// fd.bottom = new FormAttachment(nicsGroup, 0);
//		businessGroup.setLayoutData(fd);
//
//		// set the column widths for proper placing
//		helper.colWidth[0] = 30;
//		last = null;
//		last = wCompanyName = helper.addTextBox(businessGroup, null, "OutputBusinessTab.CompanyName");
//		last = wAddressLine1 = helper.addTextBox(businessGroup, last, "OutputBusinessTab.AddressLine1");
//		last = wSuite = helper.addTextBox(businessGroup, last, "OutputBusinessTab.Suite");
//		last = wCity = helper.addTextBox(businessGroup, last, "OutputBusinessTab.City");
//		last = wState = helper.addTextBox(businessGroup, last, "OutputBusinessTab.State");
//		last = wPostalCode = helper.addTextBox(businessGroup, last, "OutputBusinessTab.PostalCode");
////		last = wLatitude = helper.addTextBox(businessGroup, last, "OutputBusinessTab.Latitude");
////		last = wLongitude = helper.addTextBox(businessGroup, last, "OutputBusinessTab.Longitude");
//		last = wMAK = helper.addTextBox(businessGroup, last, "OutputBusinessTab.MAK");
//		last = wMAKbase = helper.addTextBox(businessGroup, last, "OutputBusinessTab.MAKbase");
//		last = wPlus4 = helper.addTextBox(businessGroup, last, "OutputBusinessTab.Plus4");
//
//	}

	private void createDemosGroup(Composite parent, Control last) {

		demosGroup = new Group(parent, SWT.NONE);
		demosGroup.setText(getString("OutputDemosGroup.Label"));
		helper.setLook(demosGroup);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		demosGroup.setLayout(fl);

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		// fd.bottom = new FormAttachment(100, 0);
		demosGroup.setLayoutData(fd);

		// set the column widths for proper placing
		helper.colWidth[0] = 30;
		last = null;
		// last = wFemaleOwned = helper.addTextBox(demosGroup, last, "OutputBusinessTab.FemaleOwned");
		// last = wHomeBasedBusiness = helper.addTextBox(demosGroup, last, "OutputBusinessTab.HomeBasedBusiness");
		last = wLocationType = helper.addTextBox(demosGroup, last, "OutputBusinessTab.LocationType");
		last = wTotalEmployeesEstimate = helper.addTextBox(demosGroup, last, "OutputBusinessTab.TotalEmployeesEstimate");
		// last = wLocalEmployeesEstimate = helper.addTextBox(demosGroup, last, "OutputBusinessTab.LocalEmployeesEstimate");
		last = wTotalSalesEstimate = helper.addTextBox(demosGroup, last, "OutputBusinessTab.TotalSalesEstimate");
		// last = wLocalSalesEstimate = helper.addTextBox(demosGroup, last, "OutputBusinessTab.LocalSalesEstimate");
		// last = wSmallBusiness = helper.addTextBox(demosGroup, last, "OutputBusinessTab.SmallBusiness");
	//	last = wPhone = helper.addTextBox(demosGroup, last, "OutputBusinessTab.Phone");
	//	last = wWebAddress = helper.addTextBox(demosGroup, last, "OutputBusinessTab.WebAddress");
		last = wStockTicker = helper.addTextBox(demosGroup, last, "OutputBusinessTab.StockTicker");
		Label spacer = helper.addSpacer(demosGroup, wStockTicker);

		fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin * 12);
		fd.right = new FormAttachment(50, -helper.margin);
		// fd.bottom = new FormAttachment(sicGroup, -helper.margin);
		spacer.setLayoutData(fd);

	}

//	private void createGeoGroup(Composite parent, Control last) {
//
//		geoGroup = new Group(parent, SWT.NONE);
//		geoGroup.setText(getString("OutputGeoGroup.Label"));
//		helper.setLook(geoGroup);
//		FormLayout fl = new FormLayout();
//		fl.marginWidth = 3;
//		fl.marginHeight = 3;
//		geoGroup.setLayout(fl);
//
//		// set the column widths for proper placing
//		helper.colWidth[0] = 30;
//		last = null;
//		last = wLatitude = helper.addTextBox(geoGroup, last, "OutputBusinessTab.Latitude");
//		last = wLongitude = helper.addTextBox(geoGroup, last, "OutputBusinessTab.Longitude");
//
//	}

	private void createNAICSGroup(Composite parent, Control last) {

		nicsGroup = new Group(parent, SWT.NONE);
		nicsGroup.setText(getString("OutputNAICSGroup.Label"));
		helper.setLook(nicsGroup);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		nicsGroup.setLayout(fl);

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		// fd.bottom = new FormAttachment(100, 0);
		nicsGroup.setLayoutData(fd);

		// set the column widths for proper placing
		helper.colWidth[0] = 30;
		last = null;
		last = wNAICSCode = helper.addTextBox(nicsGroup, last, "OutputBusinessTab.NAICSCode");
		last = wNAICSCode2 = helper.addTextBox(nicsGroup, last, "OutputBusinessTab.NAICSCode2");
		last = wNAICSCode3 = helper.addTextBox(nicsGroup, last, "OutputBusinessTab.NAICSCode3");
		last = wNAICSDescription = helper.addTextBox(nicsGroup, last, "OutputBusinessTab.NAICSDescription");
		last = wNAICSDescription2 = helper.addTextBox(nicsGroup, last, "OutputBusinessTab.NAICSDescription2");
		last = wNAICSDescription3 = helper.addTextBox(nicsGroup, last, "OutputBusinessTab.NAICSDescription3");

	}

	private void createSICGroup(Composite parent, Control last) {

		sicGroup = new Group(parent, SWT.NONE);
		sicGroup.setText(getString("OutputSICGroup.Label"));
		helper.setLook(sicGroup);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		sicGroup.setLayout(fl);

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin);
		fd.right = new FormAttachment(100, -helper.margin);
		// fd.bottom = new FormAttachment(100, helper.margin);
		sicGroup.setLayoutData(fd);

		// set the column widths for proper placing
		helper.colWidth[0] = 30;
		last = null;
		last = wSICCode = helper.addTextBox(sicGroup, last, "OutputBusinessTab.SICCode");
		last = wSICCode2 = helper.addTextBox(sicGroup, last, "OutputBusinessTab.SICCode2");
		last = wSICCode3 = helper.addTextBox(sicGroup, last, "OutputBusinessTab.SICCode3");
		last = wSICDescription = helper.addTextBox(sicGroup, last, "OutputBusinessTab.SICDescription");
		last = wSICDescription2 = helper.addTextBox(sicGroup, last, "OutputBusinessTab.SICDescription2");
		last = wSICDescription3 = helper.addTextBox(sicGroup, last, "OutputBusinessTab.SICDescription3");

	}

//	private void createCensusGroup(Composite parent, Control last) {
//
//		cenGroup = new Group(parent, SWT.NONE);
//		cenGroup.setText(getString("OutputCensusGroup.Label"));
//		helper.setLook(cenGroup);
//		FormLayout fl = new FormLayout();
//		fl.marginWidth = 3;
//		fl.marginHeight = 3;
//		cenGroup.setLayout(fl);
//
//		FormData fd = new FormData();
//		fd.left = new FormAttachment(50, helper.margin);
//		fd.top = new FormAttachment(last, helper.margin * 2);
//		fd.right = new FormAttachment(100, -helper.margin);
//		// fd.bottom = new FormAttachment(100, helper.margin);
//
//		cenGroup.setLayoutData(fd);
//
//		// set the column widths for proper placing
//		helper.colWidth[0] = 30;
//		last = null;
//		last = ckCensus = helper.addCheckBox(cenGroup, last, "OutputBusinessTab.IncludeCensus");
//		last = helper.addSpacer(cenGroup, last);
//		last = wCensusBlock = helper.addTextBox(cenGroup, last, "OutputBusinessTab.CensusBlock");
//		last = wCensusTract = helper.addTextBox(cenGroup, last, "OutputBusinessTab.CensusTract");
//		last = wCountyFIPS = helper.addTextBox(cenGroup, last, "OutputBusinessTab.CountyFIPS");
//		last = wCountyName = helper.addTextBox(cenGroup, last, "OutputBusinessTab.CountyName");
//		last = wdeliveryIndicator = helper.addTextBox(cenGroup, last, "OutputBusinessTab.DeliveryIndicator");
//// last = wLatitude = helper.addTextBox(cenGroup, last, "OutputBusinessTab.Latitude");
//// last = wLongitude = helper.addTextBox(cenGroup, last, "OutputBusinessTab.Longitude");
//// last = wMAK = helper.addTextBox(cenGroup, last, "OutputBusinessTab.MAK");
//// last = wMAKbase = helper.addTextBox(cenGroup, last, "OutputBusinessTab.MAKbase");
//// last = wPlus4 = helper.addTextBox(cenGroup, last, "OutputBusinessTab.Plus4");
//		last = wPlaceName = helper.addTextBox(cenGroup, last, "OutputBusinessTab.PlaceName");
//		last = wPlaceCode = helper.addTextBox(cenGroup, last, "OutputBusinessTab.PlaceCode");
//
//		ckCensus.addSelectionListener(new SelectionListener() {
//
//			@Override
//			public void widgetDefaultSelected(SelectionEvent arg0) {
//
//				// Not nessary
//			}
//
//			@Override
//			public void widgetSelected(SelectionEvent arg0) {
//
//				enable();
//			}
//
//		});
//	}

	private void enable() {
//
//		if (ckCensus.getSelection()) {
//
//			wCensusBlock.setEnabled(true);
//			wCensusTract.setEnabled(true);
//			wCountyFIPS.setEnabled(true);
//			wCountyName.setEnabled(true);
//			wdeliveryIndicator.setEnabled(true);
//// wLatitude.setEnabled(true);
//// wLongitude.setEnabled(true);
//// wMAK.setEnabled(true);
//// wMAKbase.setEnabled(true);
//// wPlus4.setEnabled(true);
//			wPlaceName.setEnabled(true);
//			wPlaceCode.setEnabled(true);
//		} else {
//			wCensusBlock.setEnabled(false);
//			wCensusTract.setEnabled(false);
//			wCountyFIPS.setEnabled(false);
//			wCountyName.setEnabled(false);
//			wdeliveryIndicator.setEnabled(false);
//// wLatitude.setEnabled(false);
//// wLongitude.setEnabled(false);
//// wMAK.setEnabled(false);
//// wMAKbase.setEnabled(false);
//// wPlus4.setEnabled(false);
//			wPlaceName.setEnabled(false);
//			wPlaceCode.setEnabled(false);
//		}
	}

	public void dispose(){
		// nothing to dispose
	}

	private String getString(String key, String... args) {

		return BaseMessages.getString(PKG, "MDBusinessCoderDialog.OutputBusinessTab." + key, args);
	}

}

