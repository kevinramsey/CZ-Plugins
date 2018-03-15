/*
 * Performs validation checks
 * Checks Performed:
 * Duplicate input column usage is not allowed. For example, if the user specified 'FullName' as an input for the Name
 * Splitter, he can't use it as an input to the Phone Verifier.
 * So far it's been a good rule, as 99% of the time this check exposes a mistake on the user's part.
 * Duplicate output column usage is also not allowed. Two outputs going to the same column is definitely a user error.
 * ** Filters - all expressions are checked to ensure that they are valid.
 * This is done in OutputFilterMeta using MDBinaryEvaluator.
 * Filters - checks against known result codes
 * Filters - no "(no filter)" filter should precede a specified filter.
 * Filters - check for output target.
 * Specific tests (CVC):
 * Name processing requires an input Full Name (Name processing will be skipped otherwise, no error reported).
 * Address processing requires: (Address OR Address2) AND ((City AND State) OR Zip)
 * GeoCoder processing requirements depend on the Input Mode:
 * o Address Object Results - that Address processing will be done
 * o Address Key - the AddressKey is required.
 * o Specified Fields - Same fields as Address Processing, above (but using the ones specified on the GeoCoder tab).
 * Phone validation requires an input Phone (otherwise, Phone processing is skipped, no error reported).
 * Email validation requires an input Email (otherwise, skipped, no error).
 */
package com.melissadata.kettle.validation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import com.melissadata.kettle.AdvancedConfigurationMeta;
import com.melissadata.kettle.MDCheck;
import com.melissadata.kettle.MDCheckMeta;
import com.melissadata.kettle.MDCheckStepData;
import com.melissadata.kettle.cv.address.AddressVerifyMeta;
import com.melissadata.kettle.cv.email.EmailVerifyMeta;
import com.melissadata.kettle.cv.name.NameParseMeta;
import com.melissadata.kettle.cv.phone.PhoneVerifyMeta;
import com.melissadata.kettle.support.FilterTarget;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.i18n.GlobalMessages;

import com.melissadata.cz.MDProps;
import com.melissadata.kettle.cv.geocode.GeoCoderMeta.AddrKeySource;

public class Validations {
	public static boolean dateExpired(String date) {
		if (date.equalsIgnoreCase("EXPIRED")) { return true; }
		Calendar today = Calendar.getInstance();
		Calendar cDate = stringToDate(date);
		if (cDate.before(today)) { return true; }
		return false;
	}

	/**
	 * Converts a date object to its string equivalent
	 *
	 * @param date
	 * @return
	 */
	public static String dateToString(Calendar date) {
		String sDate = "";
		if ((date.get(Calendar.MONTH) + 1) < 10) {
			sDate = "0" + String.valueOf(date.get(Calendar.MONTH) + 1);
		} else {
			sDate = String.valueOf(date.get(Calendar.MONTH) + 1);
		}
		if (date.get(Calendar.DAY_OF_MONTH) < 10) {
			sDate += "-0" + String.valueOf(date.get(Calendar.DAY_OF_MONTH));
		} else {
			sDate += "-" + String.valueOf(date.get(Calendar.DAY_OF_MONTH));
		}
		sDate += "-" + String.valueOf(date.get(Calendar.YEAR));
		return sDate;
	}

	/**
	 * @param args
	 * @return A validation message
	 */
	public static String getValidationMessage(String name, String... args) {
		return BaseMessages.getString(PKG, "MDCheckDialog.Validation." + name, args);
	}

	public static boolean isDataExpiring(String Date) {
		Calendar today = Calendar.getInstance();
		Calendar expDate;
		boolean expiring = false;
		if (!Const.isEmpty(Date) && !Date.equals("N/A")) {
			expDate = stringToDate(Date);
			today.add(Calendar.DAY_OF_YEAR, 14);
			expiring = today.after(expDate);
		}
		return expiring;
	}

	// method subtracts 1 from month to accommodate
	// Calendar starting at 0
	public static Calendar stringToDate(String sDate) {
		Calendar date = Calendar.getInstance();
		String[] YMD = new String[3];
		int year = 2011;
		int month = 5;
		int dayOfMonth = 15;
		if (sDate != null) {
			if (sDate.indexOf("-") == 4) {
				YMD = sDate.split("-");
				year = Integer.parseInt(YMD[0]);
				month = Integer.parseInt(YMD[1]) - 1;
				dayOfMonth = Integer.parseInt(YMD[2]);
				date.set(year, month, dayOfMonth);
			} else if (sDate.indexOf("-") == 2) {
				YMD = sDate.split("-");
				month = Integer.parseInt(YMD[0]) - 1;
				dayOfMonth = Integer.parseInt(YMD[1]);
				year = Integer.parseInt(YMD[2]);
				date.set(year, month, dayOfMonth);
			}
		}
		if ((sDate != null) && sDate.equalsIgnoreCase("NEVER")) {
			date.add(Calendar.MONTH, 1);
		}
		return date;
	}
	private static Class<?>							PKG							= Validations.class;
	private static final String						TAG_LICENCE_TIME_STAMP		= "md_licence_timestamp";
	private static final String						TAG_ADDRESS_TIME_STAMP		= "md_address_timestamp";
	private static final String						TAG_CA_ADDRESS_TIME_STAMP	= "md_ca_address_timestamp";
	private static final String						TAG_NAME_TIME_STAMP			= "md_name_timestamp";
	private static final String						TAG_PHONE_TIME_STAMP		= "md_phone_timestamp";
	private static final String						TAG_EMAIL_TIME_STAMP		= "md_email_timestamp";
	private static final String						TAG_GEO_TIME_STAMP			= "md_geo_timestamp";
	private String									licenseTimeStamp;
	private String									addressTimeStamp;
	private String									caAddressTimeStamp;
	private String									nameTimeStamp;
	private String									phoneTimeStamp;
	private String									emailTimeStamp;
	private String									geoTimeStamp;
	public String									todayStamp;
	public String									checkDate;
	public List<String>								errors						= new ArrayList<String>();
	public List<String>								warnings					= new ArrayList<String>();
	public boolean									somethingToShow				= false;
	public boolean									showErrors					= false;
	public boolean									showWarnings				= false;
	private static final HashMap<String, String>	outFieldNames				= new HashMap<String, String>();
	private static final HashMap<String, String>	InFieldNames				= new HashMap<String, String>();
	private static final HashMap<String, String>	GeoFieldNames				= new HashMap<String, String>();
	private MDCheckStepData stepData;

	public Validations() {
		loadTimeProps();
	}

	public boolean addrSafe(AddressVerifyMeta avMeta) {
		boolean sfe = false;
		if (Const.isEmpty(avMeta.getInputAddressLine1()) && Const.isEmpty(avMeta.getInputAddressLine2()) && Const.isEmpty(avMeta.getInputCity()) && Const.isEmpty(avMeta.getInputCompany()) && Const.isEmpty(avMeta.getInputDeliveryPoint())
				&& Const.isEmpty(avMeta.getInputLastName()) && Const.isEmpty(avMeta.getInputPlus4()) && Const.isEmpty(avMeta.getInputState()) && Const.isEmpty(avMeta.getInputSuite()) && Const.isEmpty(avMeta.getInputUrbanization())
				&& Const.isEmpty(avMeta.getInputZip())) {
			sfe = true;
		}
		return sfe;
	}

	// Check needed input for address processing and duplicate input columns
	public void checkAddressInputFields(MDCheckStepData data, List<String> warnings, List<String> errors) {
		boolean chkOutput = false;
		String tabMarker = BaseMessages.getString(PKG, "MDCheckDialog.Validation.AddressTab.Marker");
		// make sure minimum input for address processing
		// this may become obsolete
		AddressVerifyMeta avMeta = data.getAddressVerify();
		/*
		 * GeoCoderMeta gcMeta = data.getGeoCoder();
		 * if (!gcMeta.getAddrKeySource().name().equals("AddrVerifyResults")) {
		 * if (Const.isEmpty(avMeta.getInputAddressLine1()) && Const.isEmpty(avMeta.getInputAddressLine2())) {
		 * warnings.add(tabMarker + BaseMessages.getString(PKG, "MDCheckDialog.Validation.MissingInputColumnsAddressLine"));
		 * }
		 * if (Const.isEmpty(avMeta.getInputCity()) || Const.isEmpty(avMeta.getInputState())) {
		 * if (Const.isEmpty(avMeta.getInputZip()))
		 * warnings.add(tabMarker + BaseMessages.getString(PKG, "MDCheckDialog.Validation.MissingInputColumnsCityStateZip"));
		 * }
		 * }
		 */
		if (avMeta.getOptionPerformSuiteLink()) {
			if (Const.isEmpty(avMeta.getInputCompany())) {
				errors.add(tabMarker + BaseMessages.getString(PKG, "MDCheckDialog.Validation.MissingInputCompany"));
			}
		}
		if (avMeta.getOptionPerformAddrPlus()) {
			if (Const.isEmpty(avMeta.getInputLastName())) {
				errors.add(tabMarker + BaseMessages.getString(PKG, "MDCheckDialog.Validation.MissingInputLastName"));
			}
		}
		// check for duplicate input columns returns true if field is not empty
		// I think the logic here may be wrong.
		// chkOutput will be set to true only if the last input field checked is not empty.
		// Should be "chkOutput |= checkDuplicateInputFields(...)"
		// May need to check the other checkXXX() methods
		// This is ok chkOutput gets set to true if any fields are not empty.
		// if all fields are empty it stays false and there is no need to check output.
		// checkDuplicateInputFields() only returns false if field value is empty, but here we only call it
		// if the value in not empty so it will only return true. A bit odd but that part of the logic is
		// not used here so we override it. It is used in other places so it is left in.
		if (!Const.isEmpty(avMeta.getInputLastName())) {
			chkOutput = checkDuplicateInputFields(tabMarker + getTextName("AddressVerifyTab.InputLastName"), avMeta.getInputLastName(), warnings, errors);
		}
		if (!Const.isEmpty(avMeta.getInputCompany())) {
			chkOutput = checkDuplicateInputFields(tabMarker + getTextName("AddressVerifyTab.InputCompany"), avMeta.getInputCompany(), warnings, errors);
		}
		if (!Const.isEmpty(avMeta.getInputAddressLine1())) {
			chkOutput = checkDuplicateInputFields(tabMarker + getTextName("AddressVerifyTab.InputAddressLine1"), avMeta.getInputAddressLine1(), warnings, errors);
		}
		if (!Const.isEmpty(avMeta.getInputAddressLine2())) {
			chkOutput = checkDuplicateInputFields(tabMarker + getTextName("AddressVerifyTab.InputAddressLine2"), avMeta.getInputAddressLine2(), warnings, errors);
		}
		if (!Const.isEmpty(avMeta.getInputCity())) {
			chkOutput = checkDuplicateInputFields(tabMarker + getTextName("AddressVerifyTab.InputCity"), avMeta.getInputCity(), warnings, errors);
		}
		if (!Const.isEmpty(avMeta.getInputState())) {
			chkOutput = checkDuplicateInputFields(tabMarker + getTextName("AddressVerifyTab.InputState"), avMeta.getInputState(), warnings, errors);
		}
		if (!Const.isEmpty(avMeta.getInputZip())) {
			chkOutput = checkDuplicateInputFields(tabMarker + getTextName("AddressVerifyTab.InputZip"), avMeta.getInputZip(), warnings, errors);
		}
		if (!Const.isEmpty(avMeta.getInputCountry())) {
			chkOutput = checkDuplicateInputFields(tabMarker + getTextName("AddressVerifyTab.InputCountry"), avMeta.getInputCountry(), warnings, errors);
		}
		// look for duplicates in additional input
		if (!Const.isEmpty(avMeta.getInputSuite())) {
			chkOutput = checkDuplicateInputFields(tabMarker + getTextName("AddressVerifyTab.InputSuite"), avMeta.getInputSuite(), warnings, errors);
		}
		if (!Const.isEmpty(avMeta.getInputPlus4())) {
			chkOutput = checkDuplicateInputFields(tabMarker + getTextName("AddressVerifyTab.InputPlus4"), avMeta.getInputPlus4(), warnings, errors);
		}
		if (!Const.isEmpty(avMeta.getInputUrbanization())) {
			chkOutput = checkDuplicateInputFields(tabMarker + getTextName("AddressVerifyTab.Urbanization"), avMeta.getInputUrbanization(), warnings, errors);
		}
		if (chkOutput) {
			if (Const.isEmpty(avMeta.getInputAddressLine1()) && Const.isEmpty(avMeta.getInputAddressLine2())) {
				chkOutput = false;
				warnings.add(tabMarker + BaseMessages.getString(PKG, "MDCheckDialog.Validation.MissingInputColumnsAddressLine"));
			}
			if (Const.isEmpty(avMeta.getInputCity()) || Const.isEmpty(avMeta.getInputState())) {
				if (Const.isEmpty(avMeta.getInputZip())) {
					chkOutput = false;
					warnings.add(tabMarker + BaseMessages.getString(PKG, "MDCheckDialog.Validation.MissingInputColumnsCityStateZip"));
				}
			}
		}
		if (chkOutput) {
			checkAddressOutputFields(data, warnings, errors);
		}
	}

	// check address output
	public void checkAddressOutputFields(MDCheckStepData data, List<String> warnings, List<String> errors) {
		boolean allEmpty = true;
		String tabMarker = BaseMessages.getString(PKG, "MDCheckDialog.Validation.AddressTab.Marker");
		// check for at least one output column and check duplicate out put columns
		AddressVerifyMeta avMeta = data.getAddressVerify();
		if (!Const.isEmpty(avMeta.getOutputAddressLine1())) {
			allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("AddressVerifyTab.OutputAddressLine1"), avMeta.getOutputAddressLine1(), warnings, errors);
		}
		if (!Const.isEmpty(avMeta.getOutputAddressLine2())) {
			allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("AddressVerifyTab.OutputAddressLine2"), avMeta.getOutputAddressLine2(), warnings, errors);
		}
		if (!Const.isEmpty(avMeta.getOutputCity())) {
			allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("AddressVerifyTab.OutputCity"), avMeta.getOutputCity(), warnings, errors);
		}
		if (!Const.isEmpty(avMeta.getOutputState())) {
			allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("AddressVerifyTab.OutputState"), avMeta.getOutputState(), warnings, errors);
		}
		if (!Const.isEmpty(avMeta.getOutputZip())) {
			allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("AddressVerifyTab.OutputZip"), avMeta.getOutputZip(), warnings, errors);
		}
		if (!Const.isEmpty(avMeta.getOutputCountry())) {
			allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("AddressVerifyTab.OutputCountry"), avMeta.getOutputCountry(), warnings, errors);
		}
		//FIXME add in for MAK and BaseMAK
		if (!Const.isEmpty(avMeta.getOutputAddressKey())) {
			allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("AddressVerifyTab.AddressKey"), avMeta.getOutputAddressKey(), warnings, errors);
		}
		if (allEmpty) {
			warnings.add(tabMarker + BaseMessages.getString(PKG, "MDCheckDialog.Validation.NoOutputFields"));
		}
		// Additional address output only check for duplicates. ok if blank.
		tabMarker = BaseMessages.getString(PKG, "MDCheckDialog.Validation.AdditionalAddressOptions.Marker");
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalInfoDialog.OutputSuite"), avMeta.getOutputSuite(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalInfoDialog.OutputDPAndCheckDigit"), avMeta.getOutputDPAndCheckDigit(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalInfoDialog.OutputUrbanization"), avMeta.getOutputUrbanization(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalInfoDialog.OutputPlus4"), avMeta.getOutputPlus4(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalInfoDialog.OutputPrivateMailBox"), avMeta.getOutputPrivateMailBox(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalInfoDialog.AddressTypeCode"), avMeta.getOutputAddressTypeCode(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalInfoDialog.ZipTypeCode"), avMeta.getOutputZipTypeCode(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalInfoDialog.CarrierRoute"), avMeta.getOutputCarrierRoute(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalInfoDialog.CityAbbreviation"), avMeta.getOutputCityAbbreviation(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalInfoDialog.CountyName"), avMeta.getOutputCountyName(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalInfoDialog.CountyFIPS"), avMeta.getOutputCountyFips(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalInfoDialog.CongressionalDistrict"), avMeta.getOutputCongressionalDistrict(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalInfoDialog.TimeZone"), avMeta.getOutputTimezone(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalInfoDialog.TimeZoneCode"), avMeta.getOutputTimezoneCode(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalInfoDialog.ElotNumber"), avMeta.getOutputElotNumber(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalInfoDialog.ElotOrder"), avMeta.getOutputElotOrder(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalInfoDialog.DeliveryIndication"), avMeta.getOutputDeliveryIndication(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalInfoDialog.AddressRange"), avMeta.getOutputParsedAddressRange(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalInfoDialog.PreDirectional"), avMeta.getOutputParsedPreDirectional(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalInfoDialog.StreetName"), avMeta.getOutputParsedStreetName(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalInfoDialog.Suffix"), avMeta.getOutputParsedSuffix(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalInfoDialog.PostDirectional"), avMeta.getOutputParsedPostDirectional(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalInfoDialog.SuiteName"), avMeta.getOutputParsedSuiteName(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalInfoDialog.SuiteRange"), avMeta.getOutputParsedSuiteRange(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalInfoDialog.PMBName"), avMeta.getOutputParsedPMBName(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalInfoDialog.PMBRange"), avMeta.getOutputParsedPMBRange(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalInfoDialog.RouteService"), avMeta.getOutputParsedRouteService(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalInfoDialog.LockBox"), avMeta.getOutputParsedLockBox(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalInfoDialog.DeliveryInstallation"), avMeta.getOutputParsedDeliveryInstallation(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalInfoDialog.ExtraInformation"), avMeta.getOutputParsedExtraInformation(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalInfoDialog.AddressTypeDescription"), avMeta.getOutputAddressTypeDescription(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalInfoDialog.CMRA"), avMeta.getOutputCMRA(), warnings, errors);
	}

	// check Email input for processing must have Email for processing
	// check for duplicate input
	public void checkEmailInputFields(MDCheckStepData data, List<String> warnings, List<String> errors) {
		String tabMarker = BaseMessages.getString(PKG, "MDCheckDialog.Validation.PhoneEmailTab.Marker");
		if (!Const.isEmpty(data.getEmailVerify().getInputEmail())) {
			checkDuplicateInputFields(tabMarker + getTextName("PhoneEmailTab.InputEmail"), data.getEmailVerify().getInputEmail(), warnings, errors);
			checkEmailOutputFields(data, warnings, errors);
		} else {
			// warnings.add(tabMarker + BaseMessages.getString(PKG, "MDCheckDialog.Validation.MissingEmail"));
		}
	}

	// check email output
	public void checkEmailOutputFields(MDCheckStepData data, List<String> warnings, List<String> errors) {
		String tabMarker = BaseMessages.getString(PKG, "MDCheckDialog.Validation.PhoneEmailTab.Marker");
		// check for output column and duplicates
		if (!Const.isEmpty(data.getEmailVerify().getOutputEmail())) {
			checkDuplicateOutputFields(tabMarker + getTextName("PhoneEmailTab.OutputEmail"), data.getEmailVerify().getOutputEmail(), warnings, errors);
		} else {
			warnings.add(tabMarker + BaseMessages.getString(PKG, "MDCheckDialog.Validation.NoOutputFields"));
		}
		// *********additional Email
		checkDuplicateOutputFields(tabMarker + getTextName("EmailVerifyOptionsDialog.PhoneEmailTab.OutputMailboxName"), data.getEmailVerify().getOutputMailboxName(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("EmailVerifyOptionsDialog.PhoneEmailTab.OutputDomain"), data.getEmailVerify().getOutputTLDDescription(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("EmailVerifyOptionsDialog.PhoneEmailTab.OutputTLDName"), data.getEmailVerify().getOutputDomain(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("EmailVerifyOptionsDialog.PhoneEmailTab.OutputTLD"), data.getEmailVerify().getOutputTLD(), warnings, errors);
	}

	public void checkErrors(MDCheckStepData stepData, LogChannelInterface log) throws KettleException {
		writeLogMessage(log);
		System.out.println(" Validation checkErrors : " + stepData.getAddressVerify());
		if (stepData.getAddressVerify().isLicensed() && !stepData.getAddressVerify().isInitializeOK() && !addrSafe(stepData.getAddressVerify())) { throw new KettleException(BaseMessages.getString(MDCheck.class,
				"MDCheckMeta.AddressMeta.Error.AddrObject") + (!Const.isEmpty(stepData.getAddressVerify().getInitializeError()) ? stepData.getAddressVerify().getInitializeError() : stepData.getAddressVerify().getInitializeCaError())); }
		if (stepData.getGeoCoder().isLicensed() && !stepData.getGeoCoder().isInitializeOK() && !geoSafe(stepData)) { throw new KettleException(BaseMessages.getString(MDCheck.class, "MDCheckMeta.GeoMeta.Error.GeoObject")
				+ stepData.getGeoCoder().getInitializeError()); }
		if (stepData.getNameParse().isLicensed() && !stepData.getNameParse().isInitializeOK() && !nameSafe(stepData.getNameParse())) { throw new KettleException(BaseMessages.getString(MDCheck.class, "MDCheckMeta.NameMeta.Error.NameObject")
				+ stepData.getNameParse().getInitializeError()); }
		if (stepData.getEmailVerify().isLicensed() && !stepData.getEmailVerify().isInitializeOK() && !emailSafe(stepData.getEmailVerify())) { throw new KettleException(BaseMessages.getString(MDCheck.class, "MDCheckMeta.EmailMeta.Error.EmailObject")
				+ stepData.getEmailVerify().getInitializeError()); }
		if (stepData.getPhoneVerify().isLicensed() && !stepData.getPhoneVerify().isInitializeOK() && !phoneSafe(stepData.getPhoneVerify())) { throw new KettleException(BaseMessages.getString(MDCheck.class, "MDCheckMeta.PhoneMeta.Error.PhoneObject")
				+ stepData.getPhoneVerify().getInitializeError()); }
	}

	// check filter rule order and target
	public void checkFilterOrder(MDCheckStepData data, List<String> warnings, List<String> errors) {
		String tabMarker = BaseMessages.getString(PKG, "MDCheckDialog.Validation.OutputFilterTab.Marker");
		List<FilterTarget> filterTargets = data.getOutputFilter().getFilterTargets();
		for (int index = 0; index < filterTargets.size(); index++) {
			if (Const.isEmpty(filterTargets.get(index).getTargetStepname())) {
				warnings.add(tabMarker + BaseMessages.getString(PKG, "MDCheckDialog.Validation.NoTargetSelected", filterTargets.get(index).getName()));
			}
			if (Const.isEmpty(filterTargets.get(index).getRule())) {
				for (int i = index + 1; i < filterTargets.size(); i++) {
					if (!Const.isEmpty(filterTargets.get(i).getRule())) {
						warnings.add(tabMarker + BaseMessages.getString(PKG, "MDCheckDialog.Validation.BadFilterOrder"));
					}
				}
			}
		}
	}

	// check for the proper input columns for GeoCoder
	public void checkGeoCodeInputFields(MDCheckStepData data, List<String> warnings, List<String> errors) {
		boolean chkOutput = true;
		String tabMarker = BaseMessages.getString(PKG, "MDCheckDialog.Validation.GeoCoderTab.Marker");
		switch (data.getGeoCoder().getAddrKeySource()) {
			case None:
				// don't need to do anything
				chkOutput = false;
				break;
			case AddrVerifyResults:
				// make sure appropriate address fields have input from addressverifyTab
				if (Const.isEmpty(data.getAddressVerify().getInputAddressLine1()) && Const.isEmpty(data.getAddressVerify().getInputAddressLine2())) {
					warnings.add(tabMarker + BaseMessages.getString(PKG, "MDCheckDialog.Validation.MissingInputColumnsAddressTabAddressLine"));
					chkOutput = false;
				}
				if (Const.isEmpty(data.getAddressVerify().getInputCity()) || Const.isEmpty(data.getAddressVerify().getInputState())) {
					if (Const.isEmpty(data.getAddressVerify().getInputZip())) {
						warnings.add(tabMarker + BaseMessages.getString(PKG, "MDCheckDialog.Validation.MissingInputColumnsAddressTabCityStateZip"));
						chkOutput = false;
					}
				}
				break;
			case InputColumn:
				// make sure address key field has input
				if (Const.isEmpty(data.getGeoCoder().getAddrKey())) {
					warnings.add(tabMarker + BaseMessages.getString(PKG, "MDCheckDialog.Validation.MissingAddressKey"));
					chkOutput = false;
				}
				break;
			case ComponentColumns:
				// make sure appropriate address fields have input from GeoCodeTab
				if (Const.isEmpty(data.getGeoCoder().getAddrCompLine1()) && Const.isEmpty(data.getGeoCoder().getAddrCompLine2())) {
					warnings.add(tabMarker + BaseMessages.getString(PKG, "MDCheckDialog.Validation.MissingInputColumnsAddressLine"));
					chkOutput = false;
				}
				if (Const.isEmpty(data.getGeoCoder().getAddrCompCity()) || Const.isEmpty(data.getGeoCoder().getAddrCompState())) {
					if (Const.isEmpty(data.getGeoCoder().getAddrCompZip())) {
						warnings.add(tabMarker + BaseMessages.getString(PKG, "MDCheckDialog.Validation.MissingInputColumnsCityStateZip"));
						chkOutput = false;
					}
				}
				if (!Const.isEmpty(data.getGeoCoder().getAddrCompLine1())) {
					checkDuplicateGeoFields(tabMarker + getTextName("GeoCoderTab.AddrCompLine1"), data.getGeoCoder().getAddrCompLine1(), warnings, errors);
				}
				if (!Const.isEmpty(data.getGeoCoder().getAddrCompLine2())) {
					checkDuplicateGeoFields(tabMarker + getTextName("GeoCoderTab.AddrCompLine2"), data.getGeoCoder().getAddrCompLine2(), warnings, errors);
				}
				if (!Const.isEmpty(data.getGeoCoder().getAddrCompCity())) {
					checkDuplicateGeoFields(tabMarker + getTextName("GeoCoderTab.AddrCompCity"), data.getGeoCoder().getAddrCompCity(), warnings, errors);
				}
				if (!Const.isEmpty(data.getGeoCoder().getAddrCompState())) {
					checkDuplicateGeoFields(tabMarker + getTextName("GeoCoderTab.AddrCompState"), data.getGeoCoder().getAddrCompState(), warnings, errors);
				}
				if (!Const.isEmpty(data.getGeoCoder().getAddrCompZip())) {
					checkDuplicateGeoFields(tabMarker + getTextName("GeoCoderTab.AddrCompZip"), data.getGeoCoder().getAddrCompZip(), warnings, errors);
				}
				break;
		}
		if (chkOutput) {
			checkGeoCodeOutputFields(data, warnings, errors);
		}
	}

	// check GeoCoder Output
	public void checkGeoCodeOutputFields(MDCheckStepData data, List<String> warnings, List<String> errors) {
		String tabMarker = BaseMessages.getString(PKG, "MDCheckDialog.Validation.GeoCoderTab.Marker");
		// check for latitude and longitude output
		if (Const.isEmpty(data.getGeoCoder().getLatitude()) && Const.isEmpty(data.getGeoCoder().getLongitude())) {
			warnings.add(tabMarker + BaseMessages.getString(PKG, "MDCheckDialog.Validation.NoOutputFields"));
		} else if (Const.isEmpty(data.getGeoCoder().getLatitude())) {
			warnings.add(tabMarker + BaseMessages.getString(PKG, "MDCheckDialog.Validation.NoOutputFields") + getTextName("GeoCoderTab.OutputLatitude"));
		} else if (Const.isEmpty(data.getGeoCoder().getLongitude())) {
			warnings.add(tabMarker + BaseMessages.getString(PKG, "MDCheckDialog.Validation.NoOutputFields") + getTextName("GeoCoderTab.OutputLongitude"));
		}
		// check for duplicate output columns
		checkDuplicateOutputFields(tabMarker + getTextName("GeoCoderTab.OutputLatitude"), data.getGeoCoder().getLatitude(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("GeoCoderTab.OutputLongitude"), data.getGeoCoder().getLongitude(), warnings, errors);
		// Addional out put
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalGeoCoderOutputDialog.CountyName"), data.getGeoCoder().getCountyName(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalGeoCoderOutputDialog.CountyFIPS"), data.getGeoCoder().getCountyFIPS(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalGeoCoderOutputDialog.PlaceCode"), data.getGeoCoder().getPlaceCode(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalGeoCoderOutputDialog.PlaceName"), data.getGeoCoder().getPlaceName(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalGeoCoderOutputDialog.TimeZone"), data.getGeoCoder().getTimeZone(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalGeoCoderOutputDialog.TimeZoneCode"), data.getGeoCoder().getTimeZoneCode(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalGeoCoderOutputDialog.CBSACode"), data.getGeoCoder().getCBSACode(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalGeoCoderOutputDialog.CBSALevel"), data.getGeoCoder().getCBSALevel(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalGeoCoderOutputDialog.CBSATitle"), data.getGeoCoder().getCBSATitle(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalGeoCoderOutputDialog.CBSADivisionCode"), data.getGeoCoder().getCBSADivisionCode(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalGeoCoderOutputDialog.CBSADivisionLevel"), data.getGeoCoder().getCBSADivisionLevel(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalGeoCoderOutputDialog.CBSADivisionTitle"), data.getGeoCoder().getCBSADivisionTitle(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalGeoCoderOutputDialog.CensusBlock"), data.getGeoCoder().getCensusBlock(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalGeoCoderOutputDialog.CensusTract"), data.getGeoCoder().getCensusTract(), warnings, errors);
	}

	public void checkInitialization(MDCheckStepData data) {
		stepData = data;
		// Load the time stamp properties
		// loadTimeProps();
		errors.clear();
		warnings.clear();
		Calendar c = Calendar.getInstance();
		checkDate = dateToString(c);
		c.add(Calendar.DAY_OF_MONTH, 1);
		todayStamp = dateToString(c);
		int licensedProducts = data.getAdvancedConfiguration().getProducts();
		String licenseExpiration = data.getAdvancedConfiguration().getLicenceExpiration();
		if (isDataExpiring(licenseExpiration)) {
			warnings.add(BaseMessages.getString(PKG, "MDCheckDialog.Validation.LicenseExpiring", licenseExpiration));
			if (checkTimeStamp(licenseTimeStamp)) {
				licenseTimeStamp = todayStamp;
				somethingToShow = true;
			}
		}
		if ((data.checkTypes & MDCheckMeta.MDCHECK_FULL) != 0) {
			File f = new File(data.getAdvancedConfiguration().getLocalDataPath());
			if (f.exists()) {
				if ((licensedProducts & AdvancedConfigurationMeta.MDLICENSE_Name) != 0 || AdvancedConfigurationMeta.isCommunity()) {
					data.getNameParse().checkInit(this);
				}
				if ((licensedProducts & AdvancedConfigurationMeta.MDLICENSE_GeoCode) != 0 || AdvancedConfigurationMeta.isCommunity()) {
					data.getGeoCoder().checkInit(this);
				}
				if ((licensedProducts & AdvancedConfigurationMeta.MDLICENSE_Address) != 0 || AdvancedConfigurationMeta.isCommunity()) {
					data.getAddressVerify().checkInit(this);
					data.getAddressVerify().checkZipInit(this);
				}
				if ((licensedProducts & AdvancedConfigurationMeta.MDLICENSE_Phone) != 0 || AdvancedConfigurationMeta.isCommunity()) {
					data.getPhoneVerify().checkInit(this);
				}
				if ((licensedProducts & AdvancedConfigurationMeta.MDLICENSE_Email) != 0 || AdvancedConfigurationMeta.isCommunity()) {
					data.getEmailVerify().checkInit(this);
				}
			} else {
				data.getAddressVerify().setInitializeOK(true);
				data.getGeoCoder().setInitializeOK(true);
				data.getNameParse().setInitializeOK(true);
				data.getEmailVerify().setInitializeOK(true);
				data.getPhoneVerify().setInitializeOK(true);
			}
		}
	}

	// check if full name field has input for name processing and look for duplicate input columns
	public void checkNameInputFields(MDCheckStepData data, List<String> warnings, List<String> errors) {
		// boolean chkOutput = false;
		String tabMarker = BaseMessages.getString(PKG, "MDCheckDialog.Validation.NameTab.Marker");
		// check input fields first
		if (!Const.isEmpty(data.getNameParse().getFullName())) {
			checkDuplicateInputFields(tabMarker + getTextName("NameParseTab.FullName"), data.getNameParse().getFullName(), warnings, errors);
			checkNameOutputFields(data, warnings, errors);
		}
	}

	public void checkNameOutputFields(MDCheckStepData data, List<String> warnings, List<String> errors) {
		boolean allEmpty = true;
		String tabMarker = BaseMessages.getString(PKG, "MDCheckDialog.Validation.NameTab.Marker");
		// check for at least one output and duplicates
		if (!Const.isEmpty(data.getNameParse().getPrefix(0))) {
			allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("NameParseTab.Prefix"), data.getNameParse().getPrefix(0), warnings, errors);
		}
		if (!Const.isEmpty(data.getNameParse().getFirstName(0))) {
			allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("NameParseTab.FirstName"), data.getNameParse().getFirstName(0), warnings, errors);
		}
		if (!Const.isEmpty(data.getNameParse().getMiddleName(0))) {
			allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("NameParseTab.MiddleName"), data.getNameParse().getMiddleName(0), warnings, errors);
		}
		if (!Const.isEmpty(data.getNameParse().getLastName(0))) {
			allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("NameParseTab.LastName"), data.getNameParse().getLastName(0), warnings, errors);
		}
		if (!Const.isEmpty(data.getNameParse().getSuffix(0))) {
			allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("NameParseTab.Suffix"), data.getNameParse().getSuffix(0), warnings, errors);
		}
		if (!Const.isEmpty(data.getNameParse().getGender(0))) {
			allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("NameParseTab.Gender"), data.getNameParse().getGender(0), warnings, errors);
		}
		if (!Const.isEmpty(data.getNameParse().getPrefix(1))) {
			allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("NameParseTab.Prefix"), data.getNameParse().getPrefix(1), warnings, errors);
		}
		if (!Const.isEmpty(data.getNameParse().getFirstName(1))) {
			allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("NameParseTab.FirstName"), data.getNameParse().getFirstName(1), warnings, errors);
		}
		if (!Const.isEmpty(data.getNameParse().getMiddleName(1))) {
			allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("NameParseTab.MiddleName"), data.getNameParse().getMiddleName(1), warnings, errors);
		}
		if (!Const.isEmpty(data.getNameParse().getLastName(1))) {
			allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("NameParseTab.LastName"), data.getNameParse().getLastName(1), warnings, errors);
		}
		if (!Const.isEmpty(data.getNameParse().getSuffix(1))) {
			allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("NameParseTab.Suffix"), data.getNameParse().getSuffix(1), warnings, errors);
		}
		if (!Const.isEmpty(data.getNameParse().getGender(1))) {
			allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("NameParseTab.Gender"), data.getNameParse().getGender(1), warnings, errors);
		}
		if (!Const.isEmpty(data.getNameParse().getSalutation())) {
			allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("NameParseTab.Salutation"), data.getNameParse().getSalutation(), warnings, errors);
		}
		if (allEmpty) {
			warnings.add(tabMarker + BaseMessages.getString(PKG, "MDCheckDialog.Validation.NoOutputFields"));
		}
	}

	// check phone input for processing must have phone number for processing
	// check for duplicate input
	public void checkPhoneInputFields(MDCheckStepData data, List<String> warnings, List<String> errors) {
		String tabMarker = BaseMessages.getString(PKG, "MDCheckDialog.Validation.PhoneEmailTab.Marker");
		if (!Const.isEmpty(data.getPhoneVerify().getInputPhone())) {
			checkDuplicateInputFields(tabMarker + getTextName("PhoneEmailTab.InputPhone"), data.getPhoneVerify().getInputPhone(), warnings, errors);
			checkPhoneOutputFields(data, warnings, errors);
		} else {
			// warnings.add(tabMarker + BaseMessages.getString(PKG, "MDCheckDialog.Validation.MissingPhone"));
		}
	}

	// check phone output
	public void checkPhoneOutputFields(MDCheckStepData data, List<String> warnings, List<String> errors) {
		boolean allEmpty = true;
		String tabMarker = BaseMessages.getString(PKG, "MDCheckDialog.Validation.PhoneEmailTab.Marker");
		// see if any output columns selected and check for duplicate out columns
		if (!Const.isEmpty(data.getPhoneVerify().getOutputPhone())) {
			allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("PhoneEmailTab.OutputPhone"), data.getPhoneVerify().getOutputPhone(), warnings, errors);
		}
		if (!Const.isEmpty(data.getPhoneVerify().getOutputPrefix())) {
			allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("PhoneEmailTab.OutputPrefix"), data.getPhoneVerify().getOutputPrefix(), warnings, errors);
		}
		if (!Const.isEmpty(data.getPhoneVerify().getOutputSuffix())) {
			allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("PhoneEmailTab.OutputSuffix"), data.getPhoneVerify().getOutputSuffix(), warnings, errors);
		}
		if (!Const.isEmpty(data.getPhoneVerify().getOutputAreaCode())) {
			allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("PhoneEmailTab.OutputAreaCode"), data.getPhoneVerify().getOutputAreaCode(), warnings, errors);
		}
		if (!Const.isEmpty(data.getPhoneVerify().getOutputExtension())) {
			allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("PhoneEmailTab.OutputExtension"), data.getPhoneVerify().getOutputExtension(), warnings, errors);
		}
		if (allEmpty) {
			warnings.add(tabMarker + BaseMessages.getString(PKG, "MDCheckDialog.Validation.NoOutputFields"));
		}
		// check for duplicate output columns in additional phone output
		checkDuplicateOutputFields(tabMarker + getTextName("PhoneOutputDialog.OutputCity"), data.getPhoneVerify().getOutputCity(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("PhoneOutputDialog.OutputState"), data.getPhoneVerify().getOutputState(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("PhoneOutputDialog.OutputCounty"), data.getPhoneVerify().getOutputCounty(), warnings, errors);// check
// this
		checkDuplicateOutputFields(tabMarker + getTextName("PhoneOutputDialog.OutputCountyFips"), data.getPhoneVerify().getOutputCountyFips(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("PhoneOutputDialog.OutputCountryCode"), data.getPhoneVerify().getOutputCountry(), warnings, errors);// and
// this
		checkDuplicateOutputFields(tabMarker + getTextName("PhoneOutputDialog.OutputTimeZone"), data.getPhoneVerify().getOutputTimeZone(), warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("PhoneOutputDialog.OutputTimeZoneCode"), data.getPhoneVerify().getOutputTZCode(), warnings, errors);
	}

	// check email output
	public void checkReportingOutputFields(MDCheckStepData data, List<String> warnings, List<String> errors) {
		if (isReportEnabled(data)) {
			String tabMarker = BaseMessages.getString(PKG, "MDCheckDialog.Validation.ReportingTab.Marker");
			// check for output column and duplicates
			if (Const.isEmpty(data.getReportMeta().getOutputReportDirname(null)) && data.getReportMeta().isOptToFile()) {
				errors.add(tabMarker + BaseMessages.getString(PKG, "MDCheckDialog.Validation.MissingDirPath"));
			}
			Properties propsPlugin = MDProps.getProperties();
			SortedMap<String, String> reportDesc = new TreeMap<String, String>();
			for (Object key : propsPlugin.keySet()) {
				if (((String) key).startsWith("ReportDesc")) {
					reportDesc.put((String) key, propsPlugin.getProperty((String) key));
				}
			}
			Object[] keys = reportDesc.keySet().toArray();
			for (int i = 0; i < reportDesc.size(); i++) {
				if (reportDesc.get(keys[i]).contains("Name") && (Const.isEmpty(data.getNameParse().getFullName()) && Const.isEmpty(data.getNameParse().getInputCompanyName())) && data.getReportMeta().getOptsSubReports()[i]) {
					warnings.add(tabMarker + BaseMessages.getString(PKG, "MDCheckDialog.Validation.ReportSetName"));
				}
				if (reportDesc.get(keys[i]).contains("Address") && Const.isEmpty(data.getAddressVerify().getInputAddressLine1()) && data.getReportMeta().getOptsSubReports()[i]) {
					warnings.add(tabMarker + BaseMessages.getString(PKG, "MDCheckDialog.Validation.ReportSetAddress"));
				}
				if (reportDesc.get(keys[i]).contains("Email") && Const.isEmpty(data.getEmailVerify().getInputEmail()) && data.getReportMeta().getOptsSubReports()[i]) {
					warnings.add(tabMarker + BaseMessages.getString(PKG, "MDCheckDialog.Validation.ReportSetEmail"));
				}
				if (reportDesc.get(keys[i]).contains("Geo") && (data.getGeoCoder().getAddrKeySource() == AddrKeySource.None) && data.getReportMeta().getOptsSubReports()[i]) {
					warnings.add(tabMarker + BaseMessages.getString(PKG, "MDCheckDialog.Validation.ReportSetGeo"));
				}
				if (reportDesc.get(keys[i]).contains("Phone") && Const.isEmpty(data.getPhoneVerify().getInputPhone()) && data.getReportMeta().getOptsSubReports()[i]) {
					warnings.add(tabMarker + BaseMessages.getString(PKG, "MDCheckDialog.Validation.ReportSetPhone"));
				}
			}
		}
	}

	// check smart mover duplicate Intput columns
	public void checkSmartMoverInputFields(MDCheckStepData data, List<String> warnings, List<String> errors) {
		// Smart mover does its own validating
	}

	// check smart mover duplicate output columns
	public void checkSmartMoverOutputFields(MDCheckStepData data, List<String> warnings, List<String> errors) {
		// smart mover does its own validations
	}

	public boolean checkTimeStamp(String timeStamp) {
		return (timeStamp.length() < 1) || !stringToDate(timeStamp).after(stringToDate(checkDate));
	}

	public void clearInputList() {
		InFieldNames.clear();
		GeoFieldNames.clear();
	}

	public void clearOutputList() {
		outFieldNames.clear();
	}

	public String getAddressTimeStamp() {
		return addressTimeStamp;
	}

	public String getCaAddressTimeStamp() {
		return caAddressTimeStamp;
	}

	public String getEmailTimeStamp() {
		return emailTimeStamp;
	}

	public String getGeoTimeStamp() {
		return geoTimeStamp;
	}

	public String getLicenseTimeStamp() {
		return licenseTimeStamp;
	}

	public String getNameTimeStamp() {
		return nameTimeStamp;
	}

	public String getPhoneTimeStamp() {
		return phoneTimeStamp;
	}

	public String getTodayStamp() {
		return todayStamp;
	}

	public boolean isReportEnabled(MDCheckStepData data) {
		boolean returnBool = false;
		if ((data.getAddressVerify() != null) && data.getAddressVerify().isEnabled()) {
			returnBool = true;
		}
		if ((data.getPhoneVerify() != null) && data.getPhoneVerify().isEnabled()) {
			returnBool = true;
		}
		if ((data.getEmailVerify() != null) && data.getEmailVerify().isEnabled()) {
			returnBool = true;
		}
		if ((data.getNameParse() != null) && (data.getNameParse().isEnabled() || data.getNameParse().isCompanyEnabled())) {
			returnBool = true;
		}
		if ((data.getGeoCoder() != null) && (data.getGeoCoder().getAddrKeySource() != AddrKeySource.None) && data.getGeoCoder().isEnabled()) {
			returnBool = true;
		}
		return returnBool;
	}

	public boolean needChange() {
		boolean needChange = false;
		if (stepData.getAddressVerify().isLicensed() && !stepData.getAddressVerify().isInitializeOK() && !addrSafe(stepData.getAddressVerify())) {
			needChange = true;
		}
		if (stepData.getGeoCoder().isLicensed() && !stepData.getGeoCoder().isInitializeOK() && !geoSafe(stepData)) {
			needChange = true;
		}
		if (stepData.getNameParse().isLicensed() && !stepData.getNameParse().isInitializeOK() && !nameSafe(stepData.getNameParse())) {
			needChange = true;
		}
		if (stepData.getEmailVerify().isLicensed() && !stepData.getEmailVerify().isInitializeOK() && !emailSafe(stepData.getEmailVerify())) {
			needChange = true;
		}
		if (stepData.getPhoneVerify().isLicensed() && !stepData.getPhoneVerify().isInitializeOK() && !phoneSafe(stepData.getPhoneVerify())) {
			needChange = true;
		}
		return needChange;
	}

	/**
	 * Called to save validation time stamp properties
	 *
	 * @throws KettleException
	 */
	public void saveTimeProps() throws KettleException {
		// Save the specific time stamp properties
		MDProps.setProperty(TAG_LICENCE_TIME_STAMP, licenseTimeStamp);
		MDProps.setProperty(TAG_ADDRESS_TIME_STAMP, addressTimeStamp);
		MDProps.setProperty(TAG_CA_ADDRESS_TIME_STAMP, caAddressTimeStamp);
		MDProps.setProperty(TAG_NAME_TIME_STAMP, nameTimeStamp);
		MDProps.setProperty(TAG_PHONE_TIME_STAMP, phoneTimeStamp);
		MDProps.setProperty(TAG_EMAIL_TIME_STAMP, emailTimeStamp);
		MDProps.setProperty(TAG_GEO_TIME_STAMP, geoTimeStamp);
		// Make sure they are persisted
		try {
			MDProps.save();
		} catch (IOException e) {
			MDProps.revert();
			throw new KettleException("Problem saving properties", e);
		}
	}

	public void setAddressTimeStamp(String addressTimeStamp) {
		this.addressTimeStamp = addressTimeStamp;
	}

	public void setCaAddressTimeStamp(String caAddressTimeStamp) {
		this.caAddressTimeStamp = caAddressTimeStamp;
	}

	public void setEmailTimeStamp(String emailTimeStamp) {
		this.emailTimeStamp = emailTimeStamp;
	}

	public void setGeoTimeStamp(String geoTimeStamp) {
		this.geoTimeStamp = geoTimeStamp;
	}

	public void setLicenseTimeStamp(String licenceTimeStamp) {
		licenseTimeStamp = licenceTimeStamp;
	}

	public void setNameTimeStamp(String nameTimeStamp) {
		this.nameTimeStamp = nameTimeStamp;
	}

	public void setPhoneTimeStamp(String phoneTimeStamp) {
		this.phoneTimeStamp = phoneTimeStamp;
	}

	public boolean showDialog() {
		return somethingToShow;
	}

	// verify if given result code matches a known result code
	//
	public boolean verifyResultCode(String resultCode, int checkType) {
		boolean codeMatch = false;  // Flag for if we have a match
		Collection<String> knownCodesList = new ArrayList<String>(); // list to hold known result codes
		knownCodesList = GetResultCodes(checkType); // get list of known result codes
		for (String kc : knownCodesList) {  // step through list and look for a match
			if (resultCode.equalsIgnoreCase(kc)) {
				codeMatch = true;				// if match found set flag to true
			}
		}
		return codeMatch;
	}

	public void writeLogMessage(LogChannelInterface log) {
		// write log message if any
		if ((warnings.size() > 0) || (errors.size() > 0)) {
			// Build the warning/error message
			StringBuffer message = new StringBuffer();
			message.append(getValidationMessage("MessageBoxPreamble")).append("\n");
			message.append("\n");
			if (warnings.size() > 0) {
				if (warnings.size() == 1) {
					message.append(getValidationMessage("MessageBoxWarning")).append("\n");
				} else {
					message.append(getValidationMessage("MessageBoxWarnings", "" + warnings.size())).append("\n");
				}
				for (String warning : warnings) {
					message.append("   -  ").append(warning).append("\n");
				}
				message.append("\n");
				log.logBasic(message.toString());
			}
			if (errors.size() > 0) {
				message.delete(0, message.length());
				if (errors.size() == 1) {
					message.append(getValidationMessage("MessageBoxError")).append("\n");
				} else {
					message.append(getValidationMessage("MessageBoxErrors", "" + errors.size())).append("\n");
				}
				for (String error : errors) {
					message.append("   -  ").append(error).append("\n");
				}
				log.logError(message.toString());
			}
		}
	}

	// this checks for duplicate geoCoder fields seperate only
	// within geoCoderTab to allow for duplicate address fields
	// i.e. addressVerify has fieldX for zip input geoCoder should also be allowed
	// to use fieldX for its zip input.
	private boolean checkDuplicateGeoFields(String fieldName, String fieldValue, List<String> warnings, List<String> errors) {
		if (!Const.isEmpty(fieldValue)) {
			if (GeoFieldNames.containsKey(fieldValue)) {// gets value
				warnings.add(BaseMessages.getString(PKG, "MDCheckDialog.Validation.DuplicateInputColumns", fieldValue, GeoFieldNames.get(fieldValue), fieldName));
			} else {
				GeoFieldNames.put(fieldValue, fieldName);
			}
			return false;
		}
		return true;
	}

	private boolean checkDuplicateInputFields(String fieldName, String fieldValue, List<String> warnings, List<String> errors) {
		if (!Const.isEmpty(fieldValue)) {
			if (InFieldNames.containsKey(fieldValue)) {// gets value
				warnings.add(BaseMessages.getString(PKG, "MDCheckDialog.Validation.DuplicateInputColumns", fieldValue, InFieldNames.get(fieldValue), fieldName));
			} else {
				InFieldNames.put(fieldValue, fieldName);
			}
			return true;
		}
		return false;
	}

	private boolean checkDuplicateOutputFields(String fieldName, String fieldValue, List<String> warnings, List<String> errors) {
		if (!Const.isEmpty(fieldValue)) {
			if (outFieldNames.containsKey(fieldValue)) {// gets value
				warnings.add(BaseMessages.getString(PKG, "MDCheckDialog.Validation.DuplicateOutputColumns", fieldValue, outFieldNames.get(fieldValue), fieldName));
			} else {
				outFieldNames.put(fieldValue, fieldName);
			}
			return false;
		}
		return true;
	}

	private boolean emailSafe(EmailVerifyMeta eMeta) {
		boolean sfe = false;
		if (Const.isEmpty(eMeta.getInputEmail())) {
			sfe = true;
		}
		return sfe;
	}

	private boolean geoSafe(MDCheckStepData mdData) {
		boolean sfe = false;
		if ((mdData.getGeoCoder().getAddrKeySource() == AddrKeySource.None)) {
			sfe = true;
		}
		return sfe;
	}

	/*
	 * Returns the defined error codes that begin with the given prefix
	 */
	private Collection<String> getResultCodes(String category, String prefix) {
		Set<String> rcs = new TreeSet<String>();
		ResourceBundle bundle = GlobalMessages.getBundle(PKG.getPackage().getName() + ".messages.messages", PKG);
		if (bundle != null) {
			Enumeration<String> keys = bundle.getKeys();
			while (keys.hasMoreElements()) {
				String rc = keys.nextElement();
				String categoryPrefix = "MDCheck.ResultCode." + category + ".";
				if (rc.startsWith(categoryPrefix + prefix)) {
					rc = rc.substring(categoryPrefix.length());
					int i = rc.indexOf(".");
					if (i != -1) {
						rc = rc.substring(0, i);
					}
					rcs.add(rc);
				}
			}
		}
		return rcs;
	}

	// Generates a list of result codes -- uses getResultCodes(String category, String prefix)
	// Slightly modified from MDControlSpaceKeyAdapter.java to get result code without message
	private Collection<String> GetResultCodes(int checkTypes) {
		List<String> rcs = new ArrayList<String>();
		boolean doName = (checkTypes & MDCheckMeta.MDCHECK_NAME) != 0;
		boolean doAddress = (checkTypes & MDCheckMeta.MDCHECK_ADDRESS) != 0;
		boolean doEmail = (checkTypes & MDCheckMeta.MDCHECK_EMAIL) != 0;
		boolean doPhone = (checkTypes & MDCheckMeta.MDCHECK_PHONE) != 0;
		boolean doSmartMover = (checkTypes & MDCheckMeta.MDCHECK_SMARTMOVER) != 0;
		boolean doMatchUp = (checkTypes & MDCheckMeta.MDCHECK_MATCHUP) != 0;
		boolean doMatchUpGlobal = (checkTypes & MDCheckMeta.MDCHECK_MATCHUP_GLOBAL) != 0;
		boolean doIpLoc = (checkTypes & MDCheckMeta.MDCHECK_IPLOCATOR) != 0;
		if (doName) {
			rcs.addAll(getResultCodes("Name", "NS"));	// name success
			rcs.addAll(getResultCodes("Name", "NE"));	// name error
		}
		if (doAddress || doSmartMover) {
			// Address verify is called as part of smart mover
			rcs.addAll(getResultCodes("Addr", "AS"));	// address success
			rcs.addAll(getResultCodes("Addr", "AE")); 	// address error
			rcs.addAll(getResultCodes("Addr", "AC"));	// address check
			if (!doSmartMover) {
				// Geocoder is NOT called during smart mover
				rcs.addAll(getResultCodes("Geo", "GS"));	// geo-coder success
				rcs.addAll(getResultCodes("Geo", "GE"));	// geo-coder error
				rcs.addAll(getResultCodes("Geo", "DE"));	// geo-coder error
			}
		}
		if (doEmail) {
			rcs.addAll(getResultCodes("Email", "ES"));	// email success
			rcs.addAll(getResultCodes("Email", "EE"));	// email error
			rcs.addAll(getResultCodes("Email", "DE"));	// email error
		}
		if (doPhone) {
			rcs.addAll(getResultCodes("Phone", "PS"));	// phone success
			rcs.addAll(getResultCodes("Phone", "PE"));	// phone error
		}
		if (doSmartMover) {
			rcs.addAll(getResultCodes("Smart", "CS"));
			rcs.addAll(getResultCodes("Smart", "CM"));
		}
		if (doMatchUp || doMatchUpGlobal) {
			rcs.addAll(getResultCodes("Matchup", "MS"));
		}
		if (doIpLoc) {
			rcs.addAll(getResultCodes("IpLocator", "IS"));
			rcs.addAll(getResultCodes("IpLocator", "IE"));
		}
		return rcs;
	}

	// gets label text associated to text box
	private String getTextName(String tabName) {
		return BaseMessages.getString(PKG, "MDCheckDialog." + tabName + ".Label");
	}

	/**
	 * Called to load validation time stamp properties
	 */
	private void loadTimeProps() {
		// Make sure the global properties are loaded
		MDProps.load();
		// Load the specific time stamp properties
		licenseTimeStamp = MDProps.getProperty(TAG_LICENCE_TIME_STAMP, "");
		addressTimeStamp = MDProps.getProperty(TAG_ADDRESS_TIME_STAMP, "");
		caAddressTimeStamp = MDProps.getProperty(TAG_CA_ADDRESS_TIME_STAMP, "");
		nameTimeStamp = MDProps.getProperty(TAG_NAME_TIME_STAMP, "");
		phoneTimeStamp = MDProps.getProperty(TAG_PHONE_TIME_STAMP, "");
		emailTimeStamp = MDProps.getProperty(TAG_EMAIL_TIME_STAMP, "");
		geoTimeStamp = MDProps.getProperty(TAG_GEO_TIME_STAMP, "");
	}

	private boolean nameSafe(NameParseMeta nameMeta) {
		boolean sfe = false;
		if (Const.isEmpty(nameMeta.getFullName())) {
			sfe = true;
		}
		return sfe;
	}

	private boolean phoneSafe(PhoneVerifyMeta phMeta) {
		boolean sfe = false;
		if (Const.isEmpty(phMeta.getInputPhone())) {
			sfe = true;
		}
		return sfe;
	}
}
