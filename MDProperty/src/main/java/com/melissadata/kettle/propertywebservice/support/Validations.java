package com.melissadata.kettle.propertywebservice.support;

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


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.i18n.GlobalMessages;

import com.melissadata.kettle.propertywebservice.MDPropertyWebServiceData;
import com.melissadata.kettle.propertywebservice.MDPropertyWebServiceMeta;
import com.melissadata.cz.support.FilterTarget;

public class Validations {

	public static String getAddressTimeStamp() {

		return addressTimeStamp;
	}

	public static String getCaAddressTimeStamp() {

		return caAddressTimeStamp;
	}

	public static String getEmailTimeStamp() {

		return emailTimeStamp;
	}

	public static String getGeoTimeStamp() {

		return geoTimeStamp;
	}

	public static String getLicenceTimeStamp() {

		return licenceTimeStamp;
	}

	public static String getNameTimeStamp() {

		return nameTimeStamp;
	}

	public static String getPhoneTimeStamp() {

		return phoneTimeStamp;
	}

	/**
	 * @param args
	 * @return A validation message
	 */
	public static String getValidationMessage(String name, String... args) {

		return BaseMessages.getString(PKG, "MDPropertyWebServiceDialog.Validation." + name, args);
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

	public static void setAddressTimeStamp(String addressTimeStamp) {

		Validations.addressTimeStamp = addressTimeStamp;
	}

	public static void setCaAddressTimeStamp(String caAddressTimeStamp) {

		Validations.caAddressTimeStamp = caAddressTimeStamp;
	}

	public static void setEmailTimeStamp(String emailTimeStamp) {

		Validations.emailTimeStamp = emailTimeStamp;
	}

	public static void setGeoTimeStamp(String geoTimeStamp) {

		Validations.geoTimeStamp = geoTimeStamp;
	}

	public static void setLicenceTimeStamp(String licenceTimeStamp) {

		Validations.licenceTimeStamp = licenceTimeStamp;
	}

	public static void setNameTimeStamp(String nameTimeStamp) {

		Validations.nameTimeStamp = nameTimeStamp;
	}

	public static void setPhoneTimeStamp(String phoneTimeStamp) {

		Validations.phoneTimeStamp = phoneTimeStamp;
	}

	/**
	 * Called if there is no properties file or it is formated badly.
	 *
	 * @return
	 */

	private static Properties getDefaultDefaults() {

		Properties props = new Properties();
		return props;
	}

	/**
	 * @return The location of the property file
	 */

	private static File getVersionPropFile() {

		String propertyPath = MDPropertyWebServiceData.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		propertyPath = propertyPath.replaceAll("steps.*", "steps");
		File propFile = null;
		propFile = new File(new File(propertyPath), TAG_TIME_STAMP_FILE);
		return propFile;
	}

	// method subtracts 1 from month to accommodate
	// Calendar starting at 0
	private static Calendar stringToDate(String sDate) {

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

		if (sDate.equalsIgnoreCase("NEVER")) {
			date.add(Calendar.MONTH, 1);
		}

		return date;
	}

	private static Class<?>							PKG							= MDPropertyWebServiceMeta.class;
	private final String							TAG_LICENCE_TIME_STAMP		= "md_licence_timestamp";

	private final String							TAG_ADDRESS_TIME_STAMP		= "md_address_timestamp";
	private final String							TAG_CA_ADDRESS_TIME_STAMP	= "md_ca_address_timestamp";
	private final String							TAG_NAME_TIME_STAMP			= "md_name_timestamp";
	private final String							TAG_PHONE_TIME_STAMP		= "md_phone_timestamp";

	private final String							TAG_EMAIL_TIME_STAMP		= "md_email_timestamp";
	private final String							TAG_GEO_TIME_STAMP			= "md_geo_timestamp";
	private final static String						TAG_TIME_STAMP_FILE			= "md_time_stamp.prop";
	private static final Pattern					RESULT_WILD_CODE_PAT		= Pattern.compile("[F]([SE|\\?][0-2|\\?][0-9|\\?])");//|[A][CEV|\\?][0-2|\\?]['*']$|[A][CEV|\\?]['*']$|[A]['*']");
	private static Properties						timeStampProps;

	private static String							licenceTimeStamp;

	private static String							addressTimeStamp;

	private static String							caAddressTimeStamp;

	private static String							nameTimeStamp;

	private static String							phoneTimeStamp;

	private static String							emailTimeStamp;

	private static String							geoTimeStamp;

	public static boolean							checkDone;

	private static List<String>						errors						= new ArrayList<String>();

	private static List<String>						warnings					= new ArrayList<String>();

	private boolean									somethingToShow				= false;

	public static boolean							showErrors					= false;

	public static boolean							showWarn					= false;

	public static boolean							geoSafe						= false;

	private static final HashMap<String, String>	outFieldNames				= new HashMap<String, String>();

	private static final HashMap<String, String>	InFieldNames				= new HashMap<String, String>();

	private static final HashMap<String, String>	GeoFieldNames				= new HashMap<String, String>();

	public Validations() {

	}

	// Check needed input for address processing and duplicate input columns
	public void checkAddressInputFields(MDPropertyWebServiceMeta meta, List<String> warnings, List<String> errors) {

		boolean chkOutput = false;
		String tabMarker = BaseMessages.getString(PKG, "MDPropertyWebServiceDialog.Validation.InputTab.Marker");

// // check for duplicate input columns returns true if field is not empty
// if (!Const.isEmpty(meta.addrFields.inputFields.get(PropertyWebServiceFields.TAG_INPUT_ORGANIZATION).metaValue))
// chkOutput = checkDuplicateInputFields(tabMarker + getTextName("InputTab.Organization"),
// meta.addrFields.inputFields.get(PropertyWebServiceFields.TAG_INPUT_ORGANIZATION).metaValue, warnings, errors);
// if (!Const.isEmpty(meta.addrFields.inputFields.get(PropertyWebServiceFields.TAG_INPUT_ADDRESS_LINE1).metaValue))
// chkOutput = checkDuplicateInputFields(tabMarker + getTextName("InputTab.AddressLine1"),
// meta.addrFields.inputFields.get(PropertyWebServiceFields.TAG_INPUT_ADDRESS_LINE1).metaValue, warnings, errors);
// if (!Const.isEmpty(meta.addrFields.inputFields.get(PropertyWebServiceFields.TAG_INPUT_ADDRESS_LINE2).metaValue))
// chkOutput = checkDuplicateInputFields(tabMarker + getTextName("InputTab.AddressLine2"),
// meta.addrFields.inputFields.get(PropertyWebServiceFields.TAG_INPUT_ADDRESS_LINE2).metaValue, warnings, errors);
// if (!Const.isEmpty(meta.addrFields.inputFields.get(PropertyWebServiceFields.TAG_INPUT_ADDRESS_LINE3).metaValue))
// chkOutput = checkDuplicateInputFields(tabMarker + getTextName("InputTab.AddressLine3"),
// meta.addrFields.inputFields.get(PropertyWebServiceFields.TAG_INPUT_ADDRESS_LINE3).metaValue, warnings, errors);
// if (!Const.isEmpty(meta.addrFields.inputFields.get(PropertyWebServiceFields.TAG_INPUT_ADDRESS_LINE4).metaValue))
// chkOutput = checkDuplicateInputFields(tabMarker + getTextName("InputTab.AddressLine4"),
// meta.addrFields.inputFields.get(PropertyWebServiceFields.TAG_INPUT_ADDRESS_LINE4).metaValue, warnings, errors);
// if (!Const.isEmpty(meta.addrFields.inputFields.get(PropertyWebServiceFields.TAG_INPUT_ADDRESS_LINE5).metaValue))
// chkOutput = checkDuplicateInputFields(tabMarker + getTextName("InputTab.AddressLine5"),
// meta.addrFields.inputFields.get(PropertyWebServiceFields.TAG_INPUT_ADDRESS_LINE5).metaValue, warnings, errors);
// if (!Const.isEmpty(meta.addrFields.inputFields.get(PropertyWebServiceFields.TAG_INPUT_ADDRESS_LINE6).metaValue))
// chkOutput = checkDuplicateInputFields(tabMarker + getTextName("InputTab.AddressLine6"),
// meta.addrFields.inputFields.get(PropertyWebServiceFields.TAG_INPUT_ADDRESS_LINE6).metaValue, warnings, errors);
// if (!Const.isEmpty(meta.addrFields.inputFields.get(PropertyWebServiceFields.TAG_INPUT_ADDRESS_LINE7).metaValue))
// chkOutput = checkDuplicateInputFields(tabMarker + getTextName("InputTab.AddressLine7"),
// meta.addrFields.inputFields.get(PropertyWebServiceFields.TAG_INPUT_ADDRESS_LINE7).metaValue, warnings, errors);
// if (!Const.isEmpty(meta.addrFields.inputFields.get(PropertyWebServiceFields.TAG_INPUT_ADDRESS_LINE8).metaValue))
// chkOutput = checkDuplicateInputFields(tabMarker + getTextName("InputTab.AddressLine8"),
// meta.addrFields.inputFields.get(PropertyWebServiceFields.TAG_INPUT_ADDRESS_LINE8).metaValue, warnings, errors);
// if (!Const.isEmpty(meta.addrFields.inputFields.get(PropertyWebServiceFields.TAG_INPUT_DOUBLE_DEPENDENT_LOCALITY).metaValue))
// chkOutput = checkDuplicateInputFields(tabMarker + getTextName("InputTab.DoubleDependentLocality"),
// meta.addrFields.inputFields.get(PropertyWebServiceFields.TAG_INPUT_DOUBLE_DEPENDENT_LOCALITY).metaValue, warnings, errors);
// if (!Const.isEmpty(meta.addrFields.inputFields.get(PropertyWebServiceFields.TAG_INPUT_DEPENDENT_LOCALITY).metaValue))
// chkOutput = checkDuplicateInputFields(tabMarker + getTextName("InputTab.DependentLocality"),
// meta.addrFields.inputFields.get(PropertyWebServiceFields.TAG_INPUT_DEPENDENT_LOCALITY).metaValue, warnings, errors);
// if (!Const.isEmpty(meta.addrFields.inputFields.get(PropertyWebServiceFields.TAG_INPUT_LOCALITY).metaValue))
// chkOutput = checkDuplicateInputFields(tabMarker + getTextName("InputTab.Locality"),
// meta.addrFields.inputFields.get(PropertyWebServiceFields.TAG_INPUT_LOCALITY).metaValue, warnings, errors);
// if (!Const.isEmpty(meta.addrFields.inputFields.get(PropertyWebServiceFields.TAG_INPUT_SUB_ADMINISTRATIVE_AREA).metaValue))
// chkOutput = checkDuplicateInputFields(tabMarker + getTextName("InputTab.SubAdministrativeArea"),
// meta.addrFields.inputFields.get(PropertyWebServiceFields.TAG_INPUT_SUB_ADMINISTRATIVE_AREA).metaValue, warnings, errors);
// if (!Const.isEmpty(meta.addrFields.inputFields.get(PropertyWebServiceFields.TAG_INPUT_ADMINISTRATIVE_AREA).metaValue))
// chkOutput = checkDuplicateInputFields(tabMarker + getTextName("InputTab.AdministrativeArea"),
// meta.addrFields.inputFields.get(PropertyWebServiceFields.TAG_INPUT_ADMINISTRATIVE_AREA).metaValue, warnings, errors);
// if (!Const.isEmpty(meta.addrFields.inputFields.get(PropertyWebServiceFields.TAG_INPUT_SUB_NATIONAL_AREA).metaValue))
// chkOutput = checkDuplicateInputFields(tabMarker + getTextName("InputTab.SubNationalArea"),
// meta.addrFields.inputFields.get(PropertyWebServiceFields.TAG_INPUT_SUB_NATIONAL_AREA).metaValue, warnings, errors);
// if (!Const.isEmpty(meta.addrFields.inputFields.get(PropertyWebServiceFields.TAG_INPUT_POSTAL_CODE).metaValue))
// chkOutput = checkDuplicateInputFields(tabMarker + getTextName("InputTab.PostalCode"),
// meta.addrFields.inputFields.get(PropertyWebServiceFields.TAG_INPUT_POSTAL_CODE).metaValue, warnings, errors);
// if (!Const.isEmpty(meta.addrFields.inputFields.get(PropertyWebServiceFields.TAG_INPUT_COUNTRY).metaValue))
// chkOutput = checkDuplicateInputFields(tabMarker + getTextName("InputTab.Country"),
// meta.addrFields.inputFields.get(PropertyWebServiceFields.TAG_INPUT_COUNTRY).metaValue, warnings, errors);
// if (!Const.isEmpty(meta.addrFields.defaultCountry))
// chkOutput = checkDuplicateInputFields(tabMarker + getTextName("InputTab.Countries"), meta.addrFields.defaultCountry,
// warnings, errors);

		if (chkOutput) {

// if(Const.isEmpty(meta.addrFields.inputFields.get(PropertyWebServiceFields.TAG_INPUT_ADDRESS_LINE1).metaValue)){
// chkOutput = false;
// warnings.add(tabMarker + BaseMessages.getString(PKG, "MDPropertyWebServiceDialog.Validation.MissingInputAddressLine1"));
//
// }
// if(Const.isEmpty(meta.addrFields.inputFields.get(PropertyWebServiceFields.TAG_INPUT_COUNTRY).metaValue)
// && Const.isEmpty(meta.addrFields.defaultCountry)){
// chkOutput = false;
// warnings.add(tabMarker + BaseMessages.getString(PKG, "MDPropertyWebServiceDialog.Validation.MissingInputCountry"));
// }

		}

		if (chkOutput) {
			checkAddressOutputFields(meta, warnings, errors);
		}

	}

	// check address output
	public void checkAddressOutputFields(MDPropertyWebServiceMeta meta, List<String> warnings, List<String> errors) {

		boolean allEmpty = true;
		String tabMarker = BaseMessages.getString(PKG, "MDPropertyWebServiceDialog.Validation.AddressTab.Marker");

// // check for at least one output column and check duplicate output columns
// if (!Const.isEmpty(meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ORGANIZATION).metaValue))
// allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("OutputTab.Organization"),
// meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ORGANIZATION).metaValue, warnings, errors);
// if (!Const.isEmpty(meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ADDRESS_LINE1).metaValue))
// allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("OutputTab.AddressLine1"),
// meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ADDRESS_LINE1).metaValue, warnings, errors);
// if (!Const.isEmpty(meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ADDRESS_LINE2).metaValue))
// allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("OutputTab.AddressLine2"),
// meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ADDRESS_LINE2).metaValue, warnings, errors);
// if (!Const.isEmpty(meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ADDRESS_LINE3).metaValue))
// allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("OutputTab.AddressLine3"),
// meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ADDRESS_LINE3).metaValue, warnings, errors);
// if (!Const.isEmpty(meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ADDRESS_LINE4).metaValue))
// allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("OutputTab.AddressLine4"),
// meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ADDRESS_LINE4).metaValue, warnings, errors);
// if (!Const.isEmpty(meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ADDRESS_LINE5).metaValue))
// allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("OutputTab.AddressLine5"),
// meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ADDRESS_LINE5).metaValue, warnings, errors);
// if (!Const.isEmpty(meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ADDRESS_LINE6).metaValue))
// allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("OutputTab.AddressLine6"),
// meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ADDRESS_LINE6).metaValue, warnings, errors);
// if (!Const.isEmpty(meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ADDRESS_LINE7).metaValue))
// allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("OutputTab.AddressLine7"),
// meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ADDRESS_LINE7).metaValue, warnings, errors);
// if (!Const.isEmpty(meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ADDRESS_LINE8).metaValue))
// allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("OutputTab.AddressLine8"),
// meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ADDRESS_LINE8).metaValue, warnings, errors);
// if (!Const.isEmpty(meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LATITUDE).metaValue))
// allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("OutputTab.Latitude"),
// meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LATITUDE).metaValue, warnings, errors);
// if (!Const.isEmpty(meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LONGITUDE).metaValue))
// allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("OutputTab.Longitude"),
// meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LONGITUDE).metaValue, warnings, errors);
// if (!Const.isEmpty(meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ADDRESS_TYPE).metaValue))
// allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("OutputTab.AddressType"),
// meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ADDRESS_TYPE).metaValue, warnings, errors);
// if (!Const.isEmpty(meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_COUNTRY).metaValue))
// allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("OutputTab.Country"),
// meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_COUNTRY).metaValue, warnings, errors);

		if (allEmpty) {
			warnings.add(tabMarker + BaseMessages.getString(PKG, "MDPropertyWebServiceDialog.Validation.NoOutputFields"));
		}

		// Additional address output only check for duplicates. ok if blank.
		tabMarker = BaseMessages.getString(PKG, "MDPropertyWebServiceDialog.Validation.AdditionalOutputOptions.Marker");

// checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.Alpha2"),
// meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_COUNTRY_ISO_ALPHA2).metaValue, warnings, errors);
// checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.Alpha3"),
// meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_COUNTRY_ISO_ALPHA3).metaValue, warnings, errors);
// checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.Numeric"),
// meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_COUNTRY_NUMERIC).metaValue, warnings, errors);
// checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.SubPremises"),
// meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SUB_PREMISES).metaValue, warnings, errors);
// checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.DoubleDependentLocality"),
// meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_DOUBLE_DEPENDENT_LOCALITY).metaValue, warnings, errors);
// checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.DependentLocality"),
// meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_DEPENDENT_LOCALITY).metaValue, warnings, errors);
// checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.Locality"),
// meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LOCALITY).metaValue, warnings, errors);
// checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.Building"),
// meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_BUILDING).metaValue, warnings, errors);
// checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.PremisesType"),
// meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PREMISES_TYPE).metaValue, warnings, errors);
// checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.PremisesNumber"),
// meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PREMISES_NUMBER).metaValue, warnings, errors);
// checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.Thoroughfare"),
// meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_THOROUGHFARE).metaValue, warnings, errors);
// checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.ThoroughfarePreDirection"),
// meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_PRE_DIRECTION).metaValue, warnings, errors);
// checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.ThoroughfareLeadingType"),
// meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_LEADING_TYPE).metaValue, warnings, errors);
// checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.ThoroughfareName"),
// meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_THOROUGHFARE_NAME).metaValue, warnings, errors);
// checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.ThoroughfareTrailingType"),
// meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_TRAILING_TYPE).metaValue, warnings, errors);
// checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.ThoroughfarePostDirection"),
// meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_POST_DIRECTION).metaValue, warnings, errors);
// checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.PostBox"),
// meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_POSTBOX).metaValue, warnings, errors);
// checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.SubAdminArea"),
// meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SUB_ADMINISTRATIVE_AREA).metaValue, warnings, errors);
// checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.AdminArea"),
// meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_ADMINISTRATIVE_AREA).metaValue, warnings, errors);
// checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.PostalCode"),
// meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_POSTAL_CODE).metaValue, warnings, errors);
// checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.SubNationalArea"),
// meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SUB_NATIONAL_AREA).metaValue, warnings, errors);
// checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.SubPremisesType"),
// meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SUB_PREMISES_TYPE).metaValue, warnings, errors);
// checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.SubPremisesNumber"),
// meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_SUB_PREMISES_NUMBER).metaValue, warnings, errors);
// checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.DependentThoroughfare"),
// meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE).metaValue, warnings, errors);
// checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.DependentThoroughfarePreDirection"),
// meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE_PRE_DIRECTION).metaValue, warnings,
// errors);
// checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.DependentThoroughfareLeadingType"),
// meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE_LEADING_TYPE).metaValue, warnings,
// errors);
// checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.DependentThoroughfareName"),
// meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE_NAME).metaValue, warnings, errors);
// checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.DependentThoroughfareTrailingType"),
// meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE_TRAILING_TYPE).metaValue, warnings,
// errors);
// checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.DependentThoroughfarePostDirection"),
// meta.addrFields.outputFields.get(PropertyWebServiceFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE_POST_DIRECTION).metaValue,
// warnings, errors);

	}

	// check filter rule order and target
	public void checkFilterOrder(MDPropertyWebServiceMeta meta, List<String> warnings, List<String> errors) {

		String tabMarker = BaseMessages.getString(PKG, "MDPropertyWebServiceDialog.Validation.OutputFilterTab.Marker");

		List<FilterTarget> filterTargets = meta.oFilterFields.filterTargets;

		for (int index = 0; index < filterTargets.size(); index++) {
			if (Const.isEmpty(filterTargets.get(index).getTargetStepname())) {
				warnings.add(tabMarker + BaseMessages.getString(PKG, "MDPropertyWebServiceDialog.Validation.NoTargetSelected", filterTargets.get(index).getName()));
			}
			if (Const.isEmpty(filterTargets.get(index).getRule())) {
				for (int i = index + 1; i < filterTargets.size(); i++) {
					if (!Const.isEmpty(filterTargets.get(i).getRule())) {
						warnings.add(tabMarker + BaseMessages.getString(PKG, "MDPropertyWebServiceDialog.Validation.BadFilterOrder"));
					}
				}
			}
		}
	}

	public void checkInitialization(MDPropertyWebServiceMeta meta) {

/*
 * stepData = data;
 * loadProps();
 * errors.clear();
 * warnings.clear();
 * File f = new File(data.getAdvancedConfiguration().getLocalDataPath() + File.separator + "mdAddr.nat");
 * if (f.exists()) {
 * todayStamp = Calendar.getInstance();
 * todayStamp.add(Calendar.DAY_OF_MONTH, 1);
 * Calendar checkDate = Calendar.getInstance();
 * chkDateStr = dateToString(checkDate);
 * int licensedProducts = data.getAdvancedConfiguration().getProducts();
 * String licenseExpiration = data.getAdvancedConfiguration().getLicenceExpiration();
 * if (isDataExpiring(licenseExpiration)) {
 * warnings.add(BaseMessages.getString(PKG, "MDCheckDialog.Validation.LicenseExpiring", licenseExpiration));
 * if( nameTimeStamp.length() < 1 || !stringToDate(nameTimeStamp).after(stringToDate(chkDateStr))){
 * somethingToShow = true;
 * }
 * }
 * if ((licensedProducts & AdvancedConfigurationMeta.MDLICENSE_Name) != 0){
 * checkName(data);
 * }
 * if ((licensedProducts & AdvancedConfigurationMeta.MDLICENSE_GeoCode) != 0){
 * checkGeoCoder(data);
 * }else{
 * data.getGeoCoder().setInitializeOK(true);
 * }
 * if ((licensedProducts & AdvancedConfigurationMeta.MDLICENSE_Address) != 0) {
 * try {
 * checkAddress(data);
 * } catch (KettleException e) {
 * warnings.add("Address: " + e.getMessage());
 * }
 * checkZip(data);
 * }
 * if ((licensedProducts & AdvancedConfigurationMeta.MDLICENSE_Phone) != 0){
 * checkPhone(data);
 * }
 * if ((licensedProducts & AdvancedConfigurationMeta.MDLICENSE_Email) != 0){
 * checkEmail(data);
 * }
 * }
 * else{
 * data.getAddressVerify().setInitializeOK(true);
 * data.getGeoCoder().setInitializeOK(true);
 * data.getNameParse().setInitializeOK(true);
 * data.getEmailVerify().setInitializeOK(true);
 * data.getPhoneVerify().setInitializeOK(true);
 * }
 * //checkDone = true;
 * }
 * private void checkName(MDCheckStepData data) {
 * boolean initialOK = true;
 * NameParseMeta nameParse = data.getNameParse();
 * nameParse.setInitializeError("");
 * nameParse.setInitializeWarn("");
 * mdName Name = null;
 * try {
 * Name = NameParseMeta.newName();
 * Name.SetLicenseString(data.getAdvancedConfiguration().getLicense());
 * Name.SetPathToNameFiles(data.getAdvancedConfiguration().getLocalDataPath());
 * if (Name.InitializeDataFiles() == mdName.ProgramStatus.NoError) {
 * if (isDataExpiring(Name.GetDatabaseExpirationDate())) {
 * warnings.add("Name data is expiring on " + Name.GetDatabaseExpirationDate());
 * if (nameTimeStamp.length() < 1 || !stringToDate(nameTimeStamp).after(stringToDate(chkDateStr))) {
 * nameParse.setInitializeWarn("Name data is expiring on " + Name.GetDatabaseExpirationDate());
 * showWarn = true;
 * somethingToShow = true;
 * }
 * nameTimeStamp = dateToString(todayStamp);
 * }
 * } else {
 * errors.add("Name: " + Name.GetInitializeErrorString());
 * if (nameTimeStamp.length() < 1 || !stringToDate(nameTimeStamp).after(stringToDate(chkDateStr))) {
 * nameParse.setInitializeError("Name: " + Name.GetInitializeErrorString());
 * showErrors = true;
 * somethingToShow = true;
 * nameTimeStamp = dateToString(todayStamp);
 * }
 * initialOK = false;
 * }
 * nameParse.setInitializeOK(initialOK);
 * } catch (Throwable t) {
 * t.printStackTrace(System.err);
 * nameParse.setInitializeError("Name: " + t.getLocalizedMessage());
 * showErrors = true;
 * somethingToShow = true;
 * nameTimeStamp = dateToString(todayStamp);
 * }
 * finally {
 * if (Name != null)
 * Name.delete();
 * }
 */
	}

	// check email output
	public void checkReportingOutputFields(MDPropertyWebServiceMeta meta, List<String> warnings, List<String> errors) {

/*
 * if(isReportEnabled(meta)){
 * String tabMarker = BaseMessages.getString(PKG, "MDPropertyWebServiceDialog.Validation.ReportingTab.Marker");
 * //check for output column and duplicates
 * if (Const.isEmpty(data.getReportMeta().getOutputReportDirname()) && data.getReportMeta().isOptToFile()){
 * errors.add(tabMarker + BaseMessages.getString(PKG, "MDPropertyWebServiceDialog.Validation.MissingDirPath"));
 * }
 * Properties propsPlugin = MDProps.getProperties();
 * SortedMap<String,String> reportDesc = new TreeMap<String,String>();
 * for(Object key: propsPlugin.keySet()){
 * if(((String)key).startsWith("ReportDesc")){
 * reportDesc.put((String)key, propsPlugin.getProperty((String)key));
 * }
 * }
 * Object[] keys = reportDesc.keySet().toArray();
 * for(int i = 0; i < reportDesc.size(); i++){
 * if(reportDesc.get((String)keys[i]).contains("Name") && (Const.isEmpty(data.getNameParse().getFullName()) &&
 * Const.isEmpty(data.getNameParse().getInputCompanyName())) && data.getReportMeta().getOptsSubReports()[i]){
 * warnings.add(tabMarker + BaseMessages.getString(PKG, "MDPropertyWebServiceDialog.Validation.ReportSetName"));
 * }
 * if(reportDesc.get((String)keys[i]).contains("Address") && Const.isEmpty(data.getAddressVerify().getInputAddressLine1()) &&
 * data.getReportMeta().getOptsSubReports()[i]){
 * warnings.add(tabMarker + BaseMessages.getString(PKG, "MDCheckDialog.Validation.ReportSetAddress"));
 * }
 * if(reportDesc.get((String)keys[i]).contains("Email") && Const.isEmpty(data.getEmailVerify().getInputEmail()) &&
 * data.getReportMeta().getOptsSubReports()[i]){
 * warnings.add(tabMarker + BaseMessages.getString(PKG, "MDCheckDialog.Validation.ReportSetEmail"));
 * }
 * if(reportDesc.get((String)keys[i]).contains("Geo") && data.getGeoCoder().getAddrKeySource() == AddrKeySource.None &&
 * data.getReportMeta().getOptsSubReports()[i]){
 * warnings.add(tabMarker + BaseMessages.getString(PKG, "MDCheckDialog.Validation.ReportSetGeo"));
 * }
 * if(reportDesc.get((String)keys[i]).contains("Phone") && Const.isEmpty(data.getPhoneVerify().getInputPhone()) &&
 * data.getReportMeta().getOptsSubReports()[i]){
 * warnings.add(tabMarker + BaseMessages.getString(PKG, "MDCheckDialog.Validation.ReportSetPhone"));
 * }
 * }
 * }
 */
	}

	public void clearInputList() {

		InFieldNames.clear();
		GeoFieldNames.clear();
	}

	public void clearOutputList() {

		outFieldNames.clear();
	}

	public boolean dateExpired(String date) {

		if (date.equalsIgnoreCase("EXPIRED")) { return true; }

		Calendar today = Calendar.getInstance();
		Calendar cDate = stringToDate(date);

		if (cDate.before(today)) { return true; }

		return false;
	}

	public void saveTimeProps() throws KettleException {

		// System.out.println("Saving props");
		// clear the properties object
		timeStampProps.clear();
		setProps(timeStampProps);
		// Save the new properties
		File defaultFile = getVersionPropFile();
		BufferedOutputStream out = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(defaultFile));
			timeStampProps.store(out, "MD Time Stamps");

		} catch (IOException e) {
			throw new KettleException("Problem reading " + defaultFile, e);

		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException ignored) {
				}
			}
		}
	}

	public boolean showDialog() {

		return somethingToShow;
	}

	public void validate(MDPropertyWebServiceMeta meta, List<String> warnings, List<String> errors) {

		checkAddressInputFields(meta, warnings, errors);
	}

	// verify if given result code matches a known result code
	//
	public boolean verifyResultCode(String resultCode) {

		boolean codeMatch = false;  // Flag for if we have a match
		new ArrayList<String>();

		GetResultCodes();

		// for (String kc : knownCodesList){ // step through list and look for a match
		// if(resultCode.equalsIgnoreCase(kc)){
		// codeMatch = true; // if match found set flag to true
		// }

		Matcher matcher = RESULT_WILD_CODE_PAT.matcher(resultCode.trim());
		if (matcher.matches()) {
			// resultCode[0] = matcher.group(1);
			// return true;
			codeMatch = true;
		}
		// }

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

	private Properties getProperties() {

		// Get the properties from the properties file
		File propFile = getVersionPropFile();
		Properties props = new Properties();
		InputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(propFile));
			props.load(in);

		} catch (IOException e) {
			// Create a default
			props = getDefaultDefaults();

		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ignored) {
				}
			}
		}

		return props;
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
				String categoryPrefix = "MDPropertyWebService.ResultCode." + category + ".";
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
	private Collection<String> GetResultCodes() {

		List<String> rcs = new ArrayList<String>();

		// Address verify is called as part of smart mover
		rcs.addAll(getResultCodes("Addr", "AV"));	// address success
		rcs.addAll(getResultCodes("Addr", "AE")); 	// address error
		rcs.addAll(getResultCodes("Addr", "AC"));	// address correction

		return rcs;
	}

	private void setProps(Properties prop) {

		prop.setProperty(TAG_LICENCE_TIME_STAMP, licenceTimeStamp);
		prop.setProperty(TAG_ADDRESS_TIME_STAMP, addressTimeStamp);
		prop.setProperty(TAG_CA_ADDRESS_TIME_STAMP, caAddressTimeStamp);
		prop.setProperty(TAG_NAME_TIME_STAMP, nameTimeStamp);
		prop.setProperty(TAG_PHONE_TIME_STAMP, phoneTimeStamp);
		prop.setProperty(TAG_EMAIL_TIME_STAMP, emailTimeStamp);
		prop.setProperty(TAG_GEO_TIME_STAMP, geoTimeStamp);
	}

}

