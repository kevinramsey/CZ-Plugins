package com.melissadata.kettle.cv.address;

import com.melissadata.kettle.AdvancedConfigurationMeta;
import com.melissadata.kettle.MDCheckDialog;
import com.melissadata.kettle.MDCheckStepData;
import com.melissadata.cz.support.MDPropTags;
import org.eclipse.swt.SWT;
// import org.eclipse.swt.events.SelectionAdapter;
// import org.eclipse.swt.events.SelectionEvent;
// import org.eclipse.swt.layout.FormAttachment;
// import org.eclipse.swt.layout.FormData;
// import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
// import org.eclipse.swt.widgets.Event;
// import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
// import org.eclipse.swt.events.SelectionListener;
// import org.pentaho.di.i18n.BaseMessages;

import com.melissadata.cz.ui.MDAbstractDialog;
import com.melissadata.kettle.AdvancedConfigurationMeta.ServiceType;

// Address object additional output
public class AdditionalInfoDialog extends MDAbstractDialog {
	private static Class<?>	PKG	= AdditionalInfoDialog.class;
	private Text			wOutputSuite;
	private Text			wOutputDPAndCheckDigit;
// private Text wOutputZip11;
	private Text			wOutputUrbanization;
	private Text			wOutputPrivateMailBox;
	private Text			wOutputPlus4;
	private Text			wAddressTypeCode;
	private Text			wCMRA;
	private Text			wElotNumber;
	private Text			wElotOrder;
	private Text			wDeliveryIndication;
	private Text			wZipTypeCode;
	private Text			wCarrierRoute;
	private Text			wCityAbbreviation;
	private Text			wCountyName;
	private Text			wCountyFIPS;
	private Text			wCongressionalDistrict;
	private Text			wTimeZone;
	private Text			wTimeZoneCode;
	private Text			wAddressRange;
	private Text			wPreDirectional;
	private Text			wStreetName;
	private Text			wSuffix;
	private Text			wPostDirectional;
	private Text			wSuiteName;
	private Text			wSuiteRange;
	private Text			wPMBName;
	private Text			wPMBRange;
	private Text			wRouteService;
	private Text			wLockBox;
	private Text			wDeliveryInstallation;
	private Text			wExtraInformation;
	// extra
	private Text			wCompany;
	private Text			wStateName;
	private Text			wAddressTypeDescription;
	private Text			wZipTypeDescription;
	private Text			wCountryName;
	private Text			wAddressKey;

	// private Button bSaveDefaults;
	/**
	 * Called to create the Additional Info dialog box.
	 *
	 * @param dialog
	 *
	 */
	public AdditionalInfoDialog(MDCheckDialog dialog) {
		super(dialog, SWT.NONE);
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContents(Composite parent, Object data) {
		// Address information group
		Composite[] addrInfoGroup = add2ColumnGroup(parent, null, "AddressInformationGroup");
		wOutputSuite = addTextBox(addrInfoGroup[1], null, "OutputSuite");
		wOutputPrivateMailBox = addTextBox(addrInfoGroup[1], wOutputSuite, "OutputPrivateMailBox");
		wOutputUrbanization = addTextBox(addrInfoGroup[1], wOutputPrivateMailBox, "OutputUrbanization");
		wOutputPlus4 = addTextBox(addrInfoGroup[1], wOutputUrbanization, "OutputPlus4");
		wOutputDPAndCheckDigit = addTextBox(addrInfoGroup[1], wOutputPlus4, "OutputDPAndCheckDigit");
		wCarrierRoute = addTextBox(addrInfoGroup[1], wOutputDPAndCheckDigit, "CarrierRoute");
		// wOutputZip11 = addTextBox(addrInfoGroup[1], wOutputDPAndCheckDigit, "OutputZip11");
		wZipTypeCode = addTextBox(addrInfoGroup[2], null, "ZipTypeCode");
		wAddressTypeCode = addTextBox(addrInfoGroup[2], wZipTypeCode, "AddressTypeCode");
		wAddressTypeDescription = addTextBox(addrInfoGroup[2], wAddressTypeCode, "AddressTypeDescription");
		wCMRA = addTextBox(addrInfoGroup[2], wAddressTypeDescription, "CMRA");
		wElotNumber = addTextBox(addrInfoGroup[2], wCMRA, "ElotNumber");
		wElotOrder = addTextBox(addrInfoGroup[2], wElotNumber, "ElotOrder");
		wDeliveryIndication = addTextBox(addrInfoGroup[2], wElotOrder, "DeliveryIndication");
		// Geographic information group
		Composite[] geoInfoGroup = add2ColumnGroup(parent, addrInfoGroup[0], "GeoInformationGroup");
		wCityAbbreviation = addTextBox(geoInfoGroup[1], null, "CityAbbreviation");
		wCountyName = addTextBox(geoInfoGroup[1], wCityAbbreviation, "CountyName");
		wCountyFIPS = addTextBox(geoInfoGroup[1], wCountyName, "CountyFIPS");
		wCongressionalDistrict = addTextBox(geoInfoGroup[2], null, "CongressionalDistrict");
		wTimeZone = addTextBox(geoInfoGroup[2], wCongressionalDistrict, "TimeZone");
		wTimeZoneCode = addTextBox(geoInfoGroup[2], wTimeZone, "TimeZoneCode");
		// Geographic information group
		Composite[] parsedAddressGroup = add2ColumnGroup(parent, geoInfoGroup[0], "ParsedAddressGroup");
		wAddressRange = addTextBox(parsedAddressGroup[1], null, "AddressRange");
		wPreDirectional = addTextBox(parsedAddressGroup[1], wAddressRange, "PreDirectional");
		wStreetName = addTextBox(parsedAddressGroup[1], wPreDirectional, "StreetName");
		wSuffix = addTextBox(parsedAddressGroup[1], wStreetName, "Suffix");
		wPostDirectional = addTextBox(parsedAddressGroup[1], wSuffix, "PostDirectional");
		wSuiteName = addTextBox(parsedAddressGroup[1], wPostDirectional, "SuiteName");
		wSuiteRange = addTextBox(parsedAddressGroup[1], wSuiteName, "SuiteRange");
		wPMBName = addTextBox(parsedAddressGroup[2], wSuiteRange, "PMBName");
		wPMBRange = addTextBox(parsedAddressGroup[2], wPMBName, "PMBRange");
		wRouteService = addTextBox(parsedAddressGroup[2], wPMBRange, "RouteService");
		wLockBox = addTextBox(parsedAddressGroup[2], wRouteService, "LockBox");
		wDeliveryInstallation = addTextBox(parsedAddressGroup[2], wLockBox, "DeliveryInstallation");
		wExtraInformation = addTextBox(parsedAddressGroup[2], wDeliveryInstallation, "ExtraInformation");
		// extra
		if (false) {
			wCompany = addTextBox(null, null, "Company");
			wStateName = addTextBox(null, null, "StateName");
			wZipTypeDescription = addTextBox(null, null, "ZipTypeDescription");
			wCountryName = addTextBox(null, null, "CountryName");
			wAddressKey = addTextBox(null, null, "AddressKey");
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#getTabData(java.lang.Object)
	 */
	@Override
	protected boolean getData(Object data) {
		// Local version of input that is dialog specific
		AddressVerifyMeta avMeta = ((MDCheckStepData) data).getAddressVerify();
		// Load data from controls
		avMeta.setOutputSuite(wOutputSuite.getText());
		avMeta.setOutputDPAndCheckDigit(wOutputDPAndCheckDigit.getText());
		// avMeta.setOutputZip11(wOutputZip11.getText());
		avMeta.setOutputUrbanization(wOutputUrbanization.getText());
		avMeta.setOutputPlus4(wOutputPlus4.getText());
		avMeta.setOutputPrivateMailBox(wOutputPrivateMailBox.getText());
		avMeta.setOutputAddressTypeCode(wAddressTypeCode.getText());
		avMeta.setOutputAddressTypeDescription(wAddressTypeDescription.getText());
		avMeta.setOutputZipTypeCode(wZipTypeCode.getText());
		avMeta.setOutputCarrierRoute(wCarrierRoute.getText());
		avMeta.setOutputCityAbbreviation(wCityAbbreviation.getText());
		avMeta.setOutputCMRA(wCMRA.getText());
		avMeta.setOutputElotNumber(wElotNumber.getText());
		avMeta.setOutputElotOrder(wElotOrder.getText());
		avMeta.setOutputDeliveryIndication(wDeliveryIndication.getText());
		avMeta.setOutputCountyName(wCountyName.getText());
		avMeta.setOutputCountyFips(wCountyFIPS.getText());
		avMeta.setOutputCongressionalDistrict(wCongressionalDistrict.getText());
		avMeta.setOutputTimezone(wTimeZone.getText());
		avMeta.setOutputTimezoneCode(wTimeZoneCode.getText());
		avMeta.setOutputParsedAddressRange(wAddressRange.getText());
		avMeta.setOutputParsedPreDirectional(wPreDirectional.getText());
		avMeta.setOutputParsedStreetName(wStreetName.getText());
		avMeta.setOutputParsedSuffix(wSuffix.getText());
		avMeta.setOutputParsedPostDirectional(wPostDirectional.getText());
		avMeta.setOutputParsedSuiteName(wSuiteName.getText());
		avMeta.setOutputParsedSuiteRange(wSuiteRange.getText());
		avMeta.setOutputParsedPMBName(wPMBName.getText());
		avMeta.setOutputParsedPMBRange(wPMBRange.getText());
		avMeta.setOutputParsedRouteService(wRouteService.getText());
		avMeta.setOutputParsedLockBox(wLockBox.getText());
		avMeta.setOutputParsedDeliveryInstallation(wDeliveryInstallation.getText());
		avMeta.setOutputParsedExtraInformation(wExtraInformation.getText());
		// extrap
		if (false) {
			avMeta.setOutputCompany(wCompany.getText());
			avMeta.setOutputStateName(wStateName.getText());
			avMeta.setOutputZipTypeDescription(wZipTypeDescription.getText());
			avMeta.setOutputCountryName(wCountryName.getText());
			avMeta.setOutputAddressKey(wAddressKey.getText());
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#getHelpURLKey()
	 */
	@Override
	protected String getHelpURLKey() {
			return "MDCheck.Help.AdditionalInfoDialog";
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#getPackage()
	 */
	@Override
	protected Class<?> getPackage() {
		return PKG;
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#getStringPrefix()
	 */
	@Override
	protected String getStringPrefix() {
		return "MDCheckDialog.AdditionalInfoDialog";
	}

	/*
	 * (non-Javadoc)
	 * @see com.melissadata.kettle.MDAbstractDialog#init(java.lang.Object)
	 */
	@Override
	protected void init(Object data) {
		// Local version of input that is dialog specific
		AddressVerifyMeta avMeta = ((MDCheckStepData) data).getAddressVerify();
		AdvancedConfigurationMeta acMeta = ((MDCheckStepData) data).getAdvancedConfiguration();
		// Initialize controls
		wOutputSuite.setText(avMeta.getOutputSuite());
		wOutputDPAndCheckDigit.setText(avMeta.getOutputDPAndCheckDigit());
		// wOutputZip11.setText(avMeta.getOutputZip11());
		wOutputUrbanization.setText(avMeta.getOutputUrbanization());
		wOutputPlus4.setText(avMeta.getOutputPlus4());
		wOutputPrivateMailBox.setText(avMeta.getOutputPrivateMailBox());
		wAddressTypeCode.setText(avMeta.getOutputAddressTypeCode());
		wAddressTypeDescription.setText(avMeta.getOutputAddressTypeDescription());
		wCMRA.setText(avMeta.getOutputCMRA());
		wElotNumber.setText(avMeta.getOutputElotNumber());
		wElotOrder.setText(avMeta.getOutputElotOrder());
		// disable Delivery Indication output if option is not selected
		// if(!avMeta.getOptionPerformRBDI())
		// wDeliveryIndication.setEnabled(false);
		// else
		wDeliveryIndication.setText(avMeta.getOutputDeliveryIndication());
		wZipTypeCode.setText(avMeta.getOutputZipTypeCode());
		wCarrierRoute.setText(avMeta.getOutputCarrierRoute());
		wCityAbbreviation.setText(avMeta.getOutputCityAbbreviation());
		if (!(acMeta.getServiceType() == ServiceType.Local)) {
			wCountyName.setEnabled(false);
			wCountyFIPS.setEnabled(false);
			wTimeZone.setEnabled(false);
			wTimeZoneCode.setEnabled(false);
		}
		wCountyName.setText(avMeta.getOutputCountyName());
		wCountyFIPS.setText(avMeta.getOutputCountyFips());
		wCongressionalDistrict.setText(avMeta.getOutputCongressionalDistrict());
		wTimeZone.setText(avMeta.getOutputTimezone());
		wTimeZoneCode.setText(avMeta.getOutputTimezoneCode());
		wAddressRange.setText(avMeta.getOutputParsedAddressRange());
		wPreDirectional.setText(avMeta.getOutputParsedPreDirectional());
		wStreetName.setText(avMeta.getOutputParsedStreetName());
		wSuffix.setText(avMeta.getOutputParsedSuffix());
		wPostDirectional.setText(avMeta.getOutputParsedPostDirectional());
		wSuiteName.setText(avMeta.getOutputParsedSuiteName());
		wSuiteRange.setText(avMeta.getOutputParsedSuiteRange());
		wPMBName.setText(avMeta.getOutputParsedPMBName());
		wPMBRange.setText(avMeta.getOutputParsedPMBRange());
		wRouteService.setText(avMeta.getOutputParsedRouteService());
		wLockBox.setText(avMeta.getOutputParsedLockBox());
		wDeliveryInstallation.setText(avMeta.getOutputParsedDeliveryInstallation());
		wExtraInformation.setText(avMeta.getOutputParsedExtraInformation());
		// extra
		if (false) {
			wCompany.setText(avMeta.getOutputCompany());
			wStateName.setText(avMeta.getOutputStateName());
			wZipTypeDescription.setText(avMeta.getOutputZipTypeDescription());
			wCountryName.setText(avMeta.getOutputCountryName());
			wAddressKey.setText(avMeta.getOutputAddressKey());
		}

		if(!AdvancedConfigurationMeta.isEnterprise(MDPropTags.MDLICENSE_PRODUCT_AddressObject)){

			wOutputDPAndCheckDigit.setEnabled(false);
			wOutputUrbanization.setEnabled(false);
			wAddressTypeCode.setEnabled(false);
			wAddressTypeDescription.setEnabled(false);
			wCMRA.setEnabled(false);
			wElotNumber.setEnabled(false);
			wElotOrder.setEnabled(false);
			wDeliveryIndication.setEnabled(false);
			wZipTypeCode.setEnabled(false);
			wCarrierRoute.setEnabled(false);
			wCityAbbreviation.setEnabled(false);
			wCountyName.setEnabled(false);
			wCountyFIPS.setEnabled(false);
			wTimeZone.setEnabled(false);
			wTimeZoneCode.setEnabled(false);
			wCongressionalDistrict.setEnabled(false);
			wRouteService.setEnabled(false);
			wDeliveryInstallation.setEnabled(false);
			wExtraInformation.setEnabled(false);
		}
		getShell().pack();
	}
}
