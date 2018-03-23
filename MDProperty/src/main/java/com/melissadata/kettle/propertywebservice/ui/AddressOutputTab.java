package com.melissadata.kettle.propertywebservice.ui;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
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
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.step.BaseStepMeta;
import com.melissadata.kettle.propertywebservice.MDPropertyWebServiceDialog;
import com.melissadata.kettle.propertywebservice.MDPropertyWebServiceMeta;
import com.melissadata.kettle.propertywebservice.data.PropertyWebServiceFields;
import com.melissadata.kettle.propertywebservice.support.MDPropertyWebServiceHelper;
import com.melissadata.cz.support.MDTab;
import com.melissadata.cz.support.MetaVal;

public class AddressOutputTab implements MDTab {

	private static Class<?> PKG          = MDPropertyWebServiceMeta.class;
	private final  String   TAG_EXPAND   = "Expand";
	private final  String   TAG_COLLAPSE = "Collapse";
	private Label                      description;
	private MDPropertyWebServiceHelper helper;
	private MDPropertyWebServiceDialog dialog;
	private Group                      parcelGroup;
	private Group                      propertyAddressGroup;
	private Group                      parsedPropertyAddressGroup;
	private Group                      legalGroup;
	private Composite                  wComp;
	// Parcel
	private Button                     ckIncludeParcel;
	private Label                      parcelMoreLess;
	private Text                       wFIPSCode;
	private Text                       wCounty;
	private Text                       wUnformattedAPN;
	private Text                       wFormattedAPN;
	private Text                       wAlternateAPN;
	private Text                       wAPNYearChange;
	private Text                       wPreviousAPN;
	private Text                       wAccountNumber;
	private Text                       wYearAdded;
	private Text                       wMapBook;
	private Text                       wMapPage;
	//Legal
	private Button                     ckIncludeLegal;
	private Label                      legalMoreLess;
	private Text                       wLegalDescription;
	private Text                       wRange;
	private Text                       wTownship;
	private Text                       wSection;
	private Text                       wQuarter;
	private Text                       wQuarterQuarter;
	private Text                       wSubdivision;
	private Text                       wPhase;
	private Text                       wTractNumber;
	private Text                       wBlock1;
	private Text                       wBlock2;
	private Text                       wLotNumber1;
	private Text                       wLotNumber2;
	private Text                       wLotNumber3;
	private Text                       wUnit;
	// Property Address
	private Button                     ckIncludePropertyAddress;
	private Label                      addrMoreLess;
	private Text                       wPropAddress;
	private Text                       wPropCity;
	private Text                       wPropState;
	private Text                       wPropZip;
	private Text                       wMAK;
	private Text                       wBaseMAK;
	private Text                       wAddressKey;
	private Text                       wLatitude;
	private Text                       wLongitude;
	// Parsed Property Address
	private Button                     ckIncludeParsedAddress;
	private Label                      parsedMoreLess;
	private Text                       wParsedRange;
	private Text                       wPreDirectional;
	private Text                       wStreetName;
	private Text                       wSuffix;
	private Text                       wPostDirectional;
	private Text                       wSuiteName;
	private Text                       wSuiteRange;

	public AddressOutputTab(MDPropertyWebServiceDialog dialog) {

		this.dialog = dialog;
		helper = dialog.getHelper();

		// Create the tab
		CTabItem wTab = new CTabItem(dialog.getTabFolder(), SWT.NONE);
		wTab.setText(getString("Title"));
		wTab.setData(this);

		// Create a scrolling region within the tab
		ScrolledComposite wSComp = new ScrolledComposite(dialog.getTabFolder(), SWT.V_SCROLL | SWT.H_SCROLL);
		wSComp.setLayout(new FillLayout());

		// Create the composite that will hold the contents of the tab
		wComp = new Composite(wSComp, SWT.NONE);
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
		description.setText("");

		// Create Groups
		createPropertyAddressGroup(wComp, description);
		createParsedPropertyAddressGroup(wComp, propertyAddressGroup);
		createParcelGroup(wComp, parsedPropertyAddressGroup);
		createLegalGroup(wComp, parcelGroup);

		// Fit the composite within its container (the scrolled composite)
		fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(0, 0);
		fd.right = new FormAttachment(50, 0);
		fd.bottom = new FormAttachment(150, 0);
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

		MDPropertyWebServiceMeta bcMeta = (MDPropertyWebServiceMeta) meta;

		HashMap<String, MetaVal> outputFields = bcMeta.propertyWebServiceFields.outputFields;
		HashMap<String, MetaVal> optionFields = bcMeta.propertyWebServiceFields.optionFields;

		optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_PARCEL).metaValue = String.valueOf(ckIncludeParcel.getSelection());
		optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_PROPERTY_ADDRESS).metaValue = String.valueOf(ckIncludePropertyAddress.getSelection());
		optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_PARSED_PROPERTY_ADDRESS).metaValue = String.valueOf(ckIncludeParsedAddress.getSelection());
		optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_LEGAL).metaValue = String.valueOf(ckIncludeLegal.getSelection());

// PARCEL GROUP
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_FIPSCODE).metaValue = wFIPSCode.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_COUNTY).metaValue = wCounty.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_UNFORMATTEDAPN).metaValue = wUnformattedAPN.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_FORMATTEDAPN).metaValue = wFormattedAPN.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ALTERNATEAPN).metaValue = wAlternateAPN.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_APN_YEAR_CHANGE).metaValue = wAPNYearChange.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PREVIOUS_APN).metaValue = wPreviousAPN.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ACCOUNT_NUMBER).metaValue = wAccountNumber.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_YEAR_ADDED).metaValue = wYearAdded.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MAP_BOOK).metaValue = wMapBook.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MAP_PAGE).metaValue = wMapPage.getText();

// PROPERTY ADDRESS
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_ADDRESS).metaValue = wPropAddress.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_CITY).metaValue = wPropCity.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_STATE).metaValue = wPropState.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_ZIP).metaValue = wPropZip.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_ADDRESSKEY).metaValue = wAddressKey.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_MAK).metaValue = wMAK.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_BASE_MAK).metaValue = wBaseMAK.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_LATITUDE).metaValue = wLatitude.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_LONGITUDE).metaValue = wLongitude.getText();

// PARSED PROPERTY ADDRESS
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PARSED_RANGE).metaValue = wParsedRange.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PARSED_PREDIRECTIONAL).metaValue = wPreDirectional.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PARSED_STREETNAME).metaValue = wStreetName.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PARSED_SUFFIX).metaValue = wSuffix.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PARSED_POSTDIRECTIONAL).metaValue = wPostDirectional.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PARSED_SUITENAME).metaValue = wSuiteName.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PARSED_SUITERANGE).metaValue = wSuiteRange.getText();

// LEGAL
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LEGAL_DESCRIPTION).metaValue = wLegalDescription.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_RANGE).metaValue = wRange.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_TOWNSHIP).metaValue = wTownship.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SECTION).metaValue = wSection.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_QUARTER).metaValue = wQuarter.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_QUARTER_QUATER).metaValue = wQuarterQuarter.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SUBDIVISION).metaValue = wSubdivision.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PHASE).metaValue = wPhase.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_TRACT_NUMBER).metaValue = wTractNumber.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BLOCK_1).metaValue = wBlock1.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BLOCK_2).metaValue = wBlock2.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LOT_NUMBER_1).metaValue = wLotNumber1.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LOT_NUMBER_2).metaValue = wLotNumber2.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LOT_NUMBER_3).metaValue = wLotNumber3.getText();
		outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_UNIT).metaValue = wUnit.getText();
	}

	@Override
	public String getHelpURLKey() {

		return BaseMessages.getString(PKG, "MDPropertyWebService.Plugin.Help.OutputFieldsTab.AddressOutput");
	}

	@Override
	public boolean init(BaseStepMeta meta) {

		MDPropertyWebServiceMeta bcMeta       = (MDPropertyWebServiceMeta) meta;
		HashMap<String, MetaVal> optionFields = bcMeta.propertyWebServiceFields.optionFields;
		HashMap<String, MetaVal> outputFields = bcMeta.propertyWebServiceFields.outputFields;

//// Parcel
		boolean includeParcel = Boolean.valueOf(optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_PARCEL).metaValue);
		ckIncludeParcel.setSelection(includeParcel);
		if (!includeParcel) {
			collapse(1);
			parcelMoreLess.setText(getString("More"));
		}
		wFIPSCode.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_FIPSCODE).metaValue);
		wCounty.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_COUNTY).metaValue);
		wUnformattedAPN.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_UNFORMATTEDAPN).metaValue);
		wFormattedAPN.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_FORMATTEDAPN).metaValue);
		wAlternateAPN.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ALTERNATEAPN).metaValue);
		wAPNYearChange.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_APN_YEAR_CHANGE).metaValue);
		wPreviousAPN.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PREVIOUS_APN).metaValue);
		wAccountNumber.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ACCOUNT_NUMBER).metaValue);
		wYearAdded.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_YEAR_ADDED).metaValue);
		wMapBook.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MAP_BOOK).metaValue);
		wMapPage.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_MAP_PAGE).metaValue);
// Property Address
		ckIncludePropertyAddress.setSelection(Boolean.valueOf(optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_PROPERTY_ADDRESS).metaValue));
		wPropAddress.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_ADDRESS).metaValue);
		wPropCity.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_CITY).metaValue);
		wPropState.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_STATE).metaValue);
		wPropZip.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_ZIP).metaValue);
		wAddressKey.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_ADDRESSKEY).metaValue);
		wMAK.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_MAK).metaValue);
		wBaseMAK.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_BASE_MAK).metaValue);
		wLatitude.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_LATITUDE).metaValue);
		wLongitude.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PROPERTY_LONGITUDE).metaValue);

// Parsed Property Address
		ckIncludeParsedAddress.setSelection(Boolean.valueOf(optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_PARSED_PROPERTY_ADDRESS).metaValue));
		wParsedRange.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PARSED_RANGE).metaValue);
		wPreDirectional.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PARSED_PREDIRECTIONAL).metaValue);
		wStreetName.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PARSED_STREETNAME).metaValue);
		wSuffix.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PARSED_SUFFIX).metaValue);
		wPostDirectional.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PARSED_POSTDIRECTIONAL).metaValue);
		wSuiteName.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PARSED_SUITENAME).metaValue);
		wSuiteRange.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PARSED_SUITERANGE).metaValue);

// Legal
		ckIncludeLegal.setSelection(Boolean.valueOf(optionFields.get(PropertyWebServiceFields.TAG_OPTION_INCLUDE_LEGAL).metaValue));
		wLegalDescription.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LEGAL_DESCRIPTION).metaValue);
		wRange.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_RANGE).metaValue);
		wTownship.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_TOWNSHIP).metaValue);
		wSection.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SECTION).metaValue);
		wQuarter.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_QUARTER).metaValue);
		wQuarterQuarter.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_QUARTER_QUATER).metaValue);
		wSubdivision.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SUBDIVISION).metaValue);
		wPhase.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PHASE).metaValue);
		wTractNumber.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_TRACT_NUMBER).metaValue);
		wBlock1.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BLOCK_1).metaValue);
		wBlock2.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BLOCK_2).metaValue);
		wLotNumber1.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LOT_NUMBER_1).metaValue);
		wLotNumber2.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LOT_NUMBER_2).metaValue);
		wLotNumber3.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LOT_NUMBER_3).metaValue);
		wUnit.setText(outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_UNIT).metaValue);

		enable();
		return true;
	}

	private void createParcelGroup(Composite parent, Control last) {

		parcelGroup = new Group(parent, SWT.NONE);
		parcelGroup.setText(getString("ParcelGroup.Label"));
		helper.setLook(parcelGroup);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		parcelGroup.setLayout(fl);

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin);

		fd.right = new FormAttachment(100, -helper.margin);
		// fd.bottom = new FormAttachment(100, 0);
		parcelGroup.setLayoutData(fd);

		// set the column widths for proper placing
		helper.colWidth[0] = 20;
		helper.colWidth[2] = 50;
		last = null;
		Label top;
		last = ckIncludeParcel = helper.addCheckBox(parcelGroup, last, "AddressOutputTab.IncludeParcel");
		ckIncludeParcel.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {

				enable();
			}
		});

		parcelMoreLess = helper.addLabel(parcelGroup, last, "");

		Listener addrExpandCollapse = new Listener() {
			@Override
			public void handleEvent(Event event) {

				String tx = parcelMoreLess.getText();
				if (tx.equals(getString("More"))) {

					expand(3);
					parcelMoreLess.setText(getString("Less"));
				} else {

					collapse(3);
					parcelMoreLess.setText(getString("More"));
				}
			}
		};

		parcelMoreLess.addListener(SWT.MouseDown, addrExpandCollapse);


		parcelMoreLess.setForeground(dialog.getShell().getDisplay().getSystemColor(SWT.COLOR_BLUE));
		parcelMoreLess.setText(getString("Less"));

		last = parcelMoreLess;

		last = top = helper.addSpacer(parcelGroup, last);
		last = wFIPSCode = helper.addTextBox(parcelGroup, last, "AddressOutputTab.FIPSCode");
		wFIPSCode.setToolTipText(getString("FIPSCode.ToolTip"));
		last = wCounty = helper.addTextBox(parcelGroup, last, "AddressOutputTab.County");
		last = wUnformattedAPN = helper.addTextBox(parcelGroup, last, "AddressOutputTab.UnformattedAPN");
		last = wFormattedAPN = helper.addTextBox(parcelGroup, last, "AddressOutputTab.FormattedAPN");
		last = wAlternateAPN = helper.addTextBox(parcelGroup, last, "AddressOutputTab.AlternateAPN");
		last = wAPNYearChange = helper.addTextBox(parcelGroup, last, "AddressOutputTab.APNYearChange");
		helper.colWidth[0] = 70;
		helper.colWidth[2] = 100;
		last = top;
		last = wPreviousAPN = helper.addTextBox(parcelGroup, last, "AddressOutputTab.PreviousAPN");
		last = wAccountNumber = helper.addTextBox(parcelGroup, last, "AddressOutputTab.AccountNumber");
		last = wYearAdded = helper.addTextBox(parcelGroup, last, "AddressOutputTab.YearAdded");
		last = wMapBook = helper.addTextBox(parcelGroup, last, "AddressOutputTab.MapBook");
		last = wMapPage = helper.addTextBox(parcelGroup, last, "AddressOutputTab.MapPage");
	}

	private void createPropertyAddressGroup(Composite parent, Control last) {

		propertyAddressGroup = new Group(parent, SWT.NONE);
		propertyAddressGroup.setText(getString("PropertyAddressGroup.Label"));
		helper.setLook(propertyAddressGroup);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		propertyAddressGroup.setLayout(fl);

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin * 2);
		fd.right = new FormAttachment(100, -helper.margin);
		// fd.bottom = new FormAttachment(basicElementGroup, -helper.margin);
		propertyAddressGroup.setLayoutData(fd);

		// set the column widths for proper placing
		helper.colWidth[0] = 20;
		helper.colWidth[2] = 50;
		Label top;
		last = null;
		last = ckIncludePropertyAddress = helper.addCheckBox(propertyAddressGroup, last, "AddressOutputTab.IncludePropertyAddress");
		ckIncludePropertyAddress.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {

				enable();
			}
		});

		addrMoreLess = helper.addLabel(propertyAddressGroup, last, "");

		Listener addrExpandCollapse = new Listener() {
			@Override
			public void handleEvent(Event event) {

				String tx = addrMoreLess.getText();
				if (tx.equals(getString("More"))) {
					expand(1);
					addrMoreLess.setText(getString("Less"));
				} else {
					collapse(1);
					addrMoreLess.setText(getString("More"));
				}
			}
		};

		addrMoreLess.addListener(SWT.MouseDown, addrExpandCollapse);

		addrMoreLess.setForeground(dialog.getShell().getDisplay().getSystemColor(SWT.COLOR_BLUE));
		addrMoreLess.setText(getString("Less"));

		last = addrMoreLess;

		last = top = helper.addSpacer(propertyAddressGroup, last);

		last = wPropAddress = helper.addTextBox(propertyAddressGroup, last, "AddressOutputTab.PropAddress");
		last = wPropCity = helper.addTextBox(propertyAddressGroup, last, "AddressOutputTab.PropCity");
		last = wPropState = helper.addTextBox(propertyAddressGroup, last, "AddressOutputTab.PropState");
		last = wPropZip = helper.addTextBox(propertyAddressGroup, last, "AddressOutputTab.PropZip");
		last = wAddressKey = helper.addTextBox(propertyAddressGroup, last, "AddressOutputTab.AddressKey");
		helper.colWidth[0] = 70;
		helper.colWidth[2] = 100;
		last = top;
		last = wMAK = helper.addTextBox(propertyAddressGroup, last, "AddressOutputTab.MAK");
		last = wBaseMAK = helper.addTextBox(propertyAddressGroup, last, "AddressOutputTab.BaseMAK");
		last = wLatitude = helper.addTextBox(propertyAddressGroup, last, "AddressOutputTab.Latitude");
		last = wLongitude = helper.addTextBox(propertyAddressGroup, last, "AddressOutputTab.Longitude");
	}

	private void createParsedPropertyAddressGroup(Composite parent, Control last) {

		parsedPropertyAddressGroup = new Group(parent, SWT.NONE);
		parsedPropertyAddressGroup.setText(getString("ParsedPropertyAddressGroup.Label"));
		helper.setLook(parsedPropertyAddressGroup);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		parsedPropertyAddressGroup.setLayout(fl);

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin * 2);
		fd.right = new FormAttachment(100, -helper.margin);
		// fd.bottom = new FormAttachment(100, 0);
		parsedPropertyAddressGroup.setLayoutData(fd);

		// set the column widths for proper placing
		helper.colWidth[0] = 20;
		helper.colWidth[2] = 50;
		Label top;
		last = null;
		last = ckIncludeParsedAddress = helper.addCheckBox(parsedPropertyAddressGroup, last, "AddressOutputTab.IncludeParsedAddress");
		ckIncludeParsedAddress.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {

				enable();
			}
		});

		parsedMoreLess = helper.addLabel(parsedPropertyAddressGroup, last, "");

		Listener parsedExpandCollapse = new Listener() {
			@Override
			public void handleEvent(Event event) {

				String tx = parsedMoreLess.getText();
				if (tx.equals(getString("More"))) {
					expand(2);
					parsedMoreLess.setText(getString("Less"));
				} else {
					collapse(2);
					parsedMoreLess.setText(getString("More"));
				}
			}
		};

		parsedMoreLess.addListener(SWT.MouseDown, parsedExpandCollapse);

		parsedMoreLess.setForeground(dialog.getShell().getDisplay().getSystemColor(SWT.COLOR_BLUE));
		parsedMoreLess.setText(getString("Less"));

		last = parsedMoreLess;

		last = top = helper.addSpacer(parsedPropertyAddressGroup, last);
		last = wParsedRange = helper.addTextBox(parsedPropertyAddressGroup, last, "AddressOutputTab.ParsedRange");
		last = wPreDirectional = helper.addTextBox(parsedPropertyAddressGroup, last, "AddressOutputTab.PreDirectional");
		last = wStreetName = helper.addTextBox(parsedPropertyAddressGroup, last, "AddressOutputTab.StreetName");
		last = wSuffix = helper.addTextBox(parsedPropertyAddressGroup, last, "AddressOutputTab.Suffix");

		helper.colWidth[0] = 70;
		helper.colWidth[2] = 100;
		last = top;
		last = wPostDirectional = helper.addTextBox(parsedPropertyAddressGroup, last, "AddressOutputTab.PostDirectional");
		last = wSuiteName = helper.addTextBox(parsedPropertyAddressGroup, last, "AddressOutputTab.SuiteName");
		last = wSuiteRange = helper.addTextBox(parsedPropertyAddressGroup, last, "AddressOutputTab.SuiteRange");
	}

	private void createLegalGroup(Composite parent, Control last) {

		legalGroup = new Group(parent, SWT.NONE);
		legalGroup.setText(getString("LegalGroup.Label"));
		helper.setLook(legalGroup);
		FormLayout fl = new FormLayout();
		fl.marginWidth = 3;
		fl.marginHeight = 3;
		legalGroup.setLayout(fl);

		FormData fd = new FormData();
		fd.left = new FormAttachment(0, helper.margin);
		fd.top = new FormAttachment(last, helper.margin * 2);
		fd.right = new FormAttachment(100, -helper.margin);
		// fd.bottom = new FormAttachment(100, 0);
		legalGroup.setLayoutData(fd);

		// set the column widths for proper placing
		helper.colWidth[0] = 20;
		helper.colWidth[2] = 50;
		Label top;
		last = null;
		last = ckIncludeLegal = helper.addCheckBox(legalGroup, last, "AddressOutputTab.IncludeLegal");
		ckIncludeLegal.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {

				enable();
			}
		});

		legalMoreLess = helper.addLabel(legalGroup, last, "");

		Listener legalExpandCollapse = new Listener() {
			@Override
			public void handleEvent(Event event) {

				String tx = legalMoreLess.getText();
				if (tx.equals(getString("More"))) {
					expand(4);
					legalMoreLess.setText(getString("Less"));
				} else {
					collapse(4);
					legalMoreLess.setText(getString("More"));
				}
			}
		};

		legalMoreLess.addListener(SWT.MouseDown, legalExpandCollapse);

		legalMoreLess.setForeground(dialog.getShell().getDisplay().getSystemColor(SWT.COLOR_BLUE));
		legalMoreLess.setText(getString("Less"));

		last = legalMoreLess;

		last = top = helper.addSpacer(legalGroup, last);
		last = wLegalDescription = helper.addTextBox(legalGroup, last, "AddressOutputTab.LegalDescription");
		last = wRange = helper.addTextBox(legalGroup, last, "AddressOutputTab.Range");
		last = wTownship = helper.addTextBox(legalGroup, last, "AddressOutputTab.Township");
		last = wSection = helper.addTextBox(legalGroup, last, "AddressOutputTab.Section");
		last = wQuarter = helper.addTextBox(legalGroup, last, "AddressOutputTab.Quarter");
		last = wQuarterQuarter = helper.addTextBox(legalGroup, last, "AddressOutputTab.QuarterQuarter");
		last = wSubdivision = helper.addTextBox(legalGroup, last, "AddressOutputTab.Subdivision");
		last = wPhase = helper.addTextBox(legalGroup, last, "AddressOutputTab.Phase");

		helper.colWidth[0] = 70;
		helper.colWidth[2] = 100;
		last = top;
		last = wTractNumber = helper.addTextBox(legalGroup, last, "AddressOutputTab.TractNumber");
		last = wBlock1 = helper.addTextBox(legalGroup, last, "AddressOutputTab.Block1");
		last = wBlock2 = helper.addTextBox(legalGroup, last, "AddressOutputTab.Block2");
		last = wLotNumber1 = helper.addTextBox(legalGroup, last, "AddressOutputTab.LotNumber1");
		last = wLotNumber2 = helper.addTextBox(legalGroup, last, "AddressOutputTab.LotNumber2");
		last = wLotNumber3 = helper.addTextBox(legalGroup, last, "AddressOutputTab.LotNumber3");
		last = wUnit = helper.addTextBox(legalGroup, last, "AddressOutputTab.Unit");
	}

	private void expandDDD(int group) {

		FormData addressFormData = (FormData) propertyAddressGroup.getLayoutData();
		FormData parsedFormData  = (FormData) parsedPropertyAddressGroup.getLayoutData();
		FormData parcelFormData  = (FormData) parcelGroup.getLayoutData();
		FormData legalFormData   = (FormData) legalGroup.getLayoutData();

		int offset          = 0;
		int collapsedHeight = 80;
		wComp.layout();
		{
			//Address
			addressFormData.top = new FormAttachment(description, helper.margin);
			if (addrMoreLess.getText().equalsIgnoreCase(getString("More"))) {
				addressFormData.bottom = null;
			} else {
				addressFormData.bottom = new FormAttachment(description, collapsedHeight);
			}
			for (Control child : propertyAddressGroup.getChildren()) {
				child.setVisible(true);
			}
			propertyAddressGroup.layout();
			//
			//
			// parsed
			parsedFormData.top = new FormAttachment(propertyAddressGroup, helper.margin);
			if(parsedMoreLess.getText().equalsIgnoreCase(getString("More"))){
				parsedFormData.bottom = null;
			} else {
				offset = collapsedHeight + (helper.margin * 2);
				parsedFormData.bottom = new FormAttachment(propertyAddressGroup, offset);
			}
			for (Control child : parsedPropertyAddressGroup.getChildren()) {
				child.setVisible(true);
			}
			parsedPropertyAddressGroup.layout();
			//
			//
			// Parcel
			parcelFormData.top = new FormAttachment(parsedPropertyAddressGroup, helper.margin * 2);
			if(parcelMoreLess.getText().equalsIgnoreCase(getString("More"))){
				parcelFormData.bottom = null;
			} else {
				offset = (collapsedHeight + (helper.margin * 2));
				parcelFormData.bottom = new FormAttachment(parsedPropertyAddressGroup, offset);
			}
			for (Control child : parcelGroup.getChildren()) {
				child.setVisible(true);
			}
			parcelGroup.layout();
			//
			//
			// Legal
			legalFormData.top = new FormAttachment(parcelGroup, helper.margin * 2);

			if(legalMoreLess.getText().equalsIgnoreCase(getString("More"))){
				legalFormData.bottom = null;
			} else {
				offset = collapsedHeight + (helper.margin * 2);
				legalFormData.bottom = new FormAttachment(parcelGroup, offset);
			}
			for (Control child : legalGroup.getChildren()) {
				child.setVisible(true);
			}
			legalGroup.layout();

		}


		enable();
	}

	private void expand(int group) {

		FormData addressFormData = (FormData) propertyAddressGroup.getLayoutData();
		FormData parsedFormData  = (FormData) parsedPropertyAddressGroup.getLayoutData();
		FormData parcelFormData  = (FormData) parcelGroup.getLayoutData();
		FormData legalFormData   = (FormData) legalGroup.getLayoutData();

		int offset          = 0;
		int collapsedHeight = 80;

		if (group == 1) {
			//Address expand

			addressFormData.top = new FormAttachment(description, helper.margin);
			addressFormData.bottom = null;
			wComp.layout();
			offset = (propertyAddressGroup.getBounds().height + (helper.margin * 2));
			for (Control child : propertyAddressGroup.getChildren()) {
				child.setVisible(true);
			}

			parsedFormData.top = new FormAttachment(description, offset);
			if (parsedMoreLess.getText().equals("less")) {
				parsedFormData.bottom = null;
			} else {
				offset = (propertyAddressGroup.getBounds().height + collapsedHeight + (helper.margin * 2));
				parsedFormData.bottom = new FormAttachment(description, offset);
			}
		}

		if (group == 2) {
			// Parsed expand
			parsedFormData.top = new FormAttachment(propertyAddressGroup, helper.margin);
			parsedFormData.bottom = null;
			for (Control child : parsedPropertyAddressGroup.getChildren()) {
				child.setVisible(true);
			}
			wComp.layout();
			offset = (parsedPropertyAddressGroup.getBounds().height + (helper.margin * 2));
			parcelFormData.top = new FormAttachment(parsedPropertyAddressGroup, (helper.margin * 2));
			if (parcelMoreLess.getText().equals("less")) {
				parcelFormData.bottom = null;
			} else {
				offset = (parsedPropertyAddressGroup.getBounds().height + collapsedHeight + (helper.margin * 2));
				parcelFormData.bottom = new FormAttachment(parsedPropertyAddressGroup, offset);
			}
		}

		if (group == 3) {
			//parcel expand
			parcelFormData.top = new FormAttachment(parsedPropertyAddressGroup, helper.margin * 2);
			parcelFormData.bottom = null;
			for (Control child : parcelGroup.getChildren()) {
				child.setVisible(true);
			}

			wComp.layout();
			offset = (parcelGroup.getBounds().height + (helper.margin * 2));
			legalFormData.top = new FormAttachment(parcelGroup, (helper.margin * 2));
			if (legalMoreLess.getText().equals("less")) {
				legalFormData.bottom = null;
			} else {
				offset = (parcelGroup.getBounds().height + collapsedHeight + (helper.margin * 2));
				legalFormData.bottom = new FormAttachment(parcelGroup, offset);
			}
		}

		if (group == 4) {
			//legal expand
			legalFormData.top = new FormAttachment(parcelGroup, helper.margin * 2);
			legalFormData.bottom = null;
			for (Control child : legalGroup.getChildren()) {
				child.setVisible(true);
			}
		}

		enable();
	}

	private void collapse(int group) {



		FormData addressFormData = (FormData) propertyAddressGroup.getLayoutData();
		FormData parsedFormData  = (FormData) parsedPropertyAddressGroup.getLayoutData();
		FormData parcelFormData  = (FormData) parcelGroup.getLayoutData();
		FormData legalFormData   = (FormData) legalGroup.getLayoutData();

		int offset          = 0;
		int collapsedHeight = 80;

		if (group == 1) {
			//Address collapse
			addressFormData.top = new FormAttachment(description, helper.margin);
			addressFormData.bottom = new FormAttachment(description, collapsedHeight);
			for (Control child : propertyAddressGroup.getChildren()) {
				child.setVisible(false);
			}
			wComp.layout();
			parsedFormData.top = new FormAttachment(propertyAddressGroup, helper.margin);
			if (parsedMoreLess.getText().equals("less")) {
				parsedFormData.bottom = null;
			} else {
				offset = (propertyAddressGroup.getBounds().height + collapsedHeight + (helper.margin * 2));
				parsedFormData.bottom = new FormAttachment(propertyAddressGroup, offset);
			}
		}

		if (group == 2) {

			//parsed collapse
			parsedFormData.top = new FormAttachment(description, propertyAddressGroup.getBounds().height + (helper.margin * 2));
			parsedFormData.bottom = new FormAttachment(description, propertyAddressGroup.getBounds().height + (helper.margin * 2) + 80);
			for (Control child : parsedPropertyAddressGroup.getChildren()) {
				child.setVisible(false);
			}
			wComp.layout();
			parcelFormData.top = new FormAttachment(parsedPropertyAddressGroup, helper.margin);
			if (parcelMoreLess.getText().equals("less")) {
				parcelFormData.bottom = null;
			} else {
				parcelFormData.bottom = new FormAttachment(parsedPropertyAddressGroup, parsedPropertyAddressGroup.getBounds().height + 80);
			}
		}

		//parcel collapse
		if (group == 3) {
			for (Control child : parcelGroup.getChildren()) {
				child.setVisible(false);
			}
			wComp.layout();
			parcelFormData.top = new FormAttachment(parsedPropertyAddressGroup, helper.margin * 2);
			parcelFormData.bottom = new FormAttachment(parsedPropertyAddressGroup, parsedPropertyAddressGroup.getBounds().height + 80);

			wComp.layout();
			legalFormData.top = new FormAttachment(parcelGroup, helper.margin);
			if (legalMoreLess.getText().equals("less")) {
				legalFormData.bottom = null;
			} else {
				legalFormData.bottom = new FormAttachment(parcelGroup, parcelGroup.getBounds().height + 80);
			}
		}

		//legal collapse
		if (group == 4) {
			for (Control child : legalGroup.getChildren()) {
				child.setVisible(false);
			}
			wComp.layout();
			legalFormData.top = new FormAttachment(parcelGroup, helper.margin * 2);
			legalFormData.bottom = new FormAttachment(parcelGroup, parcelGroup.getBounds().height + 80);
		}

		enable();
	}

	private void enable() {

		// Address
		if (ckIncludePropertyAddress.getSelection()) {
			for (Control child : propertyAddressGroup.getChildren()) {
				child.setEnabled(true);
			}
		} else {
			for (Control child : propertyAddressGroup.getChildren()) {
				child.setEnabled(false);
			}
		}

		// Parsed
		if (ckIncludeParsedAddress.getSelection()) {
			for (Control child : parsedPropertyAddressGroup.getChildren()) {
				child.setEnabled(true);
			}
		} else {
			for (Control child : parsedPropertyAddressGroup.getChildren()) {
				child.setEnabled(false);
			}
		}

		//PARCEL
		if (ckIncludeParcel.getSelection()) {
			for (Control child : parcelGroup.getChildren()) {
				child.setEnabled(true);
			}
		} else {
			for (Control child : parcelGroup.getChildren()) {
				child.setEnabled(false);
			}
		}

		//Legal
		if (ckIncludeLegal.getSelection()) {
			for (Control child : legalGroup.getChildren()) {
				child.setEnabled(true);
			}
		} else {
			for (Control child : legalGroup.getChildren()) {
				child.setEnabled(false);
			}
		}

		ckIncludePropertyAddress.setEnabled(true);
		ckIncludePropertyAddress.setVisible(true);
		ckIncludeParsedAddress.setEnabled(true);
		ckIncludeParsedAddress.setVisible(true);
		ckIncludeParcel.setEnabled(true);
		ckIncludeParcel.setVisible(true);
		ckIncludeLegal.setVisible(true);
		ckIncludeLegal.setEnabled(true);

		addrMoreLess.setVisible(true);
		addrMoreLess.setEnabled(true);
		parsedMoreLess.setVisible(true);
		parsedMoreLess.setEnabled(true);
		parcelMoreLess.setVisible(true);
		parcelMoreLess.setEnabled(true);
		legalMoreLess.setVisible(true);
		legalMoreLess.setEnabled(true);

		wComp.layout();
	}

	@Override
	public void dispose() {

	}

	private String getString(String key, String... args) {

		return BaseMessages.getString(PKG, "MDPropertyWebServiceDialog.AddressOutputTab." + key, args);
	}
}
