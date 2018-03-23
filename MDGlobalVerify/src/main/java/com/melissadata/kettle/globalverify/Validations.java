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
package com.melissadata.kettle.globalverify;

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
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.i18n.GlobalMessages;

import com.melissadata.cz.support.FilterTarget;
import com.melissadata.kettle.globalverify.data.AddressFields;

public class Validations {
	private static Class<?>							PKG							= Validations.class;
	private final String							TAG_LICENCE_TIME_STAMP		= "md_licence_timestamp";
	private final String							TAG_ADDRESS_TIME_STAMP		= "md_address_timestamp";
	private final String							TAG_CA_ADDRESS_TIME_STAMP	= "md_ca_address_timestamp";
	private final String							TAG_NAME_TIME_STAMP			= "md_name_timestamp";
	private final String							TAG_PHONE_TIME_STAMP		= "md_phone_timestamp";
	private final String							TAG_EMAIL_TIME_STAMP		= "md_email_timestamp";
	private final String							TAG_GEO_TIME_STAMP			= "md_geo_timestamp";
	private final static String						TAG_TIME_STAMP_FILE			= "md_time_stamp.prop";
	private static final Pattern					RESULT_WILD_CODE_PAT		= Pattern.compile("[A]([CEV|\\?][0-2|\\?][0-9|\\?])|[A][CEV|\\?][0-2|\\?]['*']$|[A][CEV|\\?]['*']$|[A]['*']");
	private static Properties						timeStampProps;
	private static String							licenceTimeStamp;
	private static String							addressTimeStamp;
	private static String							caAddressTimeStamp;
	private static String							nameTimeStamp;
	private static String							phoneTimeStamp;
	private static String							emailTimeStamp;
	private static String							geoTimeStamp;
	public static boolean							checkDone;
	@SuppressWarnings("unused")
	private static List<String>						errors						= new ArrayList<String>();
	@SuppressWarnings("unused")
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

	public void validate(MDGlobalMeta meta, List<String> warnings, List<String> errors) {
		// FIXME do checks on other input fields
		checkAddressInputFields(meta, warnings, errors);
	}

	// Generates a list of result codes -- uses getResultCodes(String category, String prefix)
	// Slightly modified from MDControlSpaceKeyAdapter.java to get result code without message
	private Collection<String> GetResultCodes() {
		List<String> rcs = new ArrayList<String>();
		// Address verify is called as part of smart mover
		rcs.addAll(getResultCodes("Addr", "AV")); // address success
		rcs.addAll(getResultCodes("Addr", "AE")); // address error
		rcs.addAll(getResultCodes("Addr", "AC")); // address correction
		return rcs;
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
				String categoryPrefix = "MDGlobalAddress.ResultCode." + category + ".";
				if (rc.startsWith(categoryPrefix + prefix)) {
					rc = rc.substring(categoryPrefix.length());
					int i = rc.indexOf(".");
					if (i != -1)
						rc = rc.substring(0, i);
					rcs.add(rc);
				}
			}
		}
		return rcs;
	}

	// verify if given result code matches a known result code
	//
	public boolean verifyResultCode(String resultCode) {
		boolean codeMatch = false; // Flag for if we have a match
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

	// check filter rule order and target
	public void checkFilterOrder(MDGlobalMeta meta, List<String> warnings, List<String> errors) {
		String tabMarker = BaseMessages.getString(PKG, "MDGlobalAddressDialog.Validation.OutputFilterTab.Marker");
		List<FilterTarget> filterTargets = meta.oFilterFields.filterTargets;
		for (int index = 0; index < filterTargets.size(); index++) {
			if (Const.isEmpty(filterTargets.get(index).getTargetStepname()))
				warnings.add(tabMarker + BaseMessages.getString(PKG, "MDGlobalAddressDialog.Validation.NoTargetSelected", filterTargets.get(index).getName()));
			if (Const.isEmpty(filterTargets.get(index).getRule())) {
				for (int i = index + 1; i < filterTargets.size(); i++) {
					if (!Const.isEmpty(filterTargets.get(i).getRule()))
						warnings.add(tabMarker + BaseMessages.getString(PKG, "MDGlobalAddressDialog.Validation.BadFilterOrder"));
				}
			}
		}
	}

	// Check needed input for address processing and duplicate input columns
	public void checkAddressInputFields(MDGlobalMeta meta, List<String> warnings, List<String> errors) {
		boolean chkOutput = false;
		String tabMarker = BaseMessages.getString(PKG, "MDGlobalAddressDialog.Validation.InputTab.Marker");
		// check for duplicate input columns returns true if field is not empty
		if (!Const.isEmpty(meta.getAddrMeta().getAddrFields().inputFields.get(AddressFields.TAG_INPUT_ORGANIZATION).metaValue))
			chkOutput = checkDuplicateInputFields(tabMarker + getTextName("InputTab.Organization"), meta.getAddrMeta().getAddrFields().inputFields.get(AddressFields.TAG_INPUT_ORGANIZATION).metaValue, warnings, errors);
		if (!Const.isEmpty(meta.getAddrMeta().getAddrFields().inputFields.get(AddressFields.TAG_INPUT_ADDRESS_LINE1).metaValue))
			chkOutput = checkDuplicateInputFields(tabMarker + getTextName("InputTab.AddressLine1"), meta.getAddrMeta().getAddrFields().inputFields.get(AddressFields.TAG_INPUT_ADDRESS_LINE1).metaValue, warnings, errors);
		if (!Const.isEmpty(meta.getAddrMeta().getAddrFields().inputFields.get(AddressFields.TAG_INPUT_ADDRESS_LINE2).metaValue))
			chkOutput = checkDuplicateInputFields(tabMarker + getTextName("InputTab.AddressLine2"), meta.getAddrMeta().getAddrFields().inputFields.get(AddressFields.TAG_INPUT_ADDRESS_LINE2).metaValue, warnings, errors);
		if (!Const.isEmpty(meta.getAddrMeta().getAddrFields().inputFields.get(AddressFields.TAG_INPUT_ADDRESS_LINE3).metaValue))
			chkOutput = checkDuplicateInputFields(tabMarker + getTextName("InputTab.AddressLine3"), meta.getAddrMeta().getAddrFields().inputFields.get(AddressFields.TAG_INPUT_ADDRESS_LINE3).metaValue, warnings, errors);
		if (!Const.isEmpty(meta.getAddrMeta().getAddrFields().inputFields.get(AddressFields.TAG_INPUT_ADDRESS_LINE4).metaValue))
			chkOutput = checkDuplicateInputFields(tabMarker + getTextName("InputTab.AddressLine4"), meta.getAddrMeta().getAddrFields().inputFields.get(AddressFields.TAG_INPUT_ADDRESS_LINE4).metaValue, warnings, errors);
		if (!Const.isEmpty(meta.getAddrMeta().getAddrFields().inputFields.get(AddressFields.TAG_INPUT_ADDRESS_LINE5).metaValue))
			chkOutput = checkDuplicateInputFields(tabMarker + getTextName("InputTab.AddressLine5"), meta.getAddrMeta().getAddrFields().inputFields.get(AddressFields.TAG_INPUT_ADDRESS_LINE5).metaValue, warnings, errors);
		if (!Const.isEmpty(meta.getAddrMeta().getAddrFields().inputFields.get(AddressFields.TAG_INPUT_ADDRESS_LINE6).metaValue))
			chkOutput = checkDuplicateInputFields(tabMarker + getTextName("InputTab.AddressLine6"), meta.getAddrMeta().getAddrFields().inputFields.get(AddressFields.TAG_INPUT_ADDRESS_LINE6).metaValue, warnings, errors);
		if (!Const.isEmpty(meta.getAddrMeta().getAddrFields().inputFields.get(AddressFields.TAG_INPUT_ADDRESS_LINE7).metaValue))
			chkOutput = checkDuplicateInputFields(tabMarker + getTextName("InputTab.AddressLine7"), meta.getAddrMeta().getAddrFields().inputFields.get(AddressFields.TAG_INPUT_ADDRESS_LINE7).metaValue, warnings, errors);
		if (!Const.isEmpty(meta.getAddrMeta().getAddrFields().inputFields.get(AddressFields.TAG_INPUT_ADDRESS_LINE8).metaValue))
			chkOutput = checkDuplicateInputFields(tabMarker + getTextName("InputTab.AddressLine8"), meta.getAddrMeta().getAddrFields().inputFields.get(AddressFields.TAG_INPUT_ADDRESS_LINE8).metaValue, warnings, errors);
		if (!Const.isEmpty(meta.getAddrMeta().getAddrFields().inputFields.get(AddressFields.TAG_INPUT_DOUBLE_DEPENDENT_LOCALITY).metaValue))
			chkOutput = checkDuplicateInputFields(tabMarker + getTextName("InputTab.DoubleDependentLocality"), meta.getAddrMeta().getAddrFields().inputFields.get(AddressFields.TAG_INPUT_DOUBLE_DEPENDENT_LOCALITY).metaValue, warnings, errors);
		if (!Const.isEmpty(meta.getAddrMeta().getAddrFields().inputFields.get(AddressFields.TAG_INPUT_DEPENDENT_LOCALITY).metaValue))
			chkOutput = checkDuplicateInputFields(tabMarker + getTextName("InputTab.DependentLocality"), meta.getAddrMeta().getAddrFields().inputFields.get(AddressFields.TAG_INPUT_DEPENDENT_LOCALITY).metaValue, warnings, errors);
		if (!Const.isEmpty(meta.getAddrMeta().getAddrFields().inputFields.get(AddressFields.TAG_INPUT_LOCALITY).metaValue))
			chkOutput = checkDuplicateInputFields(tabMarker + getTextName("InputTab.Locality"), meta.getAddrMeta().getAddrFields().inputFields.get(AddressFields.TAG_INPUT_LOCALITY).metaValue, warnings, errors);
		if (!Const.isEmpty(meta.getAddrMeta().getAddrFields().inputFields.get(AddressFields.TAG_INPUT_SUB_ADMINISTRATIVE_AREA).metaValue))
			chkOutput = checkDuplicateInputFields(tabMarker + getTextName("InputTab.SubAdministrativeArea"), meta.getAddrMeta().getAddrFields().inputFields.get(AddressFields.TAG_INPUT_SUB_ADMINISTRATIVE_AREA).metaValue, warnings, errors);
		if (!Const.isEmpty(meta.getAddrMeta().getAddrFields().inputFields.get(AddressFields.TAG_INPUT_ADMINISTRATIVE_AREA).metaValue))
			chkOutput = checkDuplicateInputFields(tabMarker + getTextName("InputTab.AdministrativeArea"), meta.getAddrMeta().getAddrFields().inputFields.get(AddressFields.TAG_INPUT_ADMINISTRATIVE_AREA).metaValue, warnings, errors);
		if (!Const.isEmpty(meta.getAddrMeta().getAddrFields().inputFields.get(AddressFields.TAG_INPUT_SUB_NATIONAL_AREA).metaValue))
			chkOutput = checkDuplicateInputFields(tabMarker + getTextName("InputTab.SubNationalArea"), meta.getAddrMeta().getAddrFields().inputFields.get(AddressFields.TAG_INPUT_SUB_NATIONAL_AREA).metaValue, warnings, errors);
		if (!Const.isEmpty(meta.getAddrMeta().getAddrFields().inputFields.get(AddressFields.TAG_INPUT_POSTAL_CODE).metaValue))
			chkOutput = checkDuplicateInputFields(tabMarker + getTextName("InputTab.PostalCode"), meta.getAddrMeta().getAddrFields().inputFields.get(AddressFields.TAG_INPUT_POSTAL_CODE).metaValue, warnings, errors);
		if (!Const.isEmpty(meta.getAddrMeta().getAddrFields().inputFields.get(AddressFields.TAG_INPUT_COUNTRY).metaValue))
			chkOutput = checkDuplicateInputFields(tabMarker + getTextName("InputTab.CountryName"), meta.getAddrMeta().getAddrFields().inputFields.get(AddressFields.TAG_INPUT_COUNTRY).metaValue, warnings, errors);
		if (!Const.isEmpty(meta.getAddrMeta().getAddrFields().defaultCountry))
			chkOutput = checkDuplicateInputFields(tabMarker + getTextName("InputTab.Countries"), meta.getAddrMeta().getAddrFields().defaultCountry, warnings, errors);
		if (chkOutput) {
			if (Const.isEmpty(meta.getAddrMeta().getAddrFields().inputFields.get(AddressFields.TAG_INPUT_ADDRESS_LINE1).metaValue)) {
				chkOutput = false;
				warnings.add(tabMarker + BaseMessages.getString(PKG, "MDGlobalAddressDialog.Validation.MissingInputAddressLine1"));
			}
			if (Const.isEmpty(meta.getAddrMeta().getAddrFields().inputFields.get(AddressFields.TAG_INPUT_COUNTRY).metaValue) && Const.isEmpty(meta.getAddrMeta().getAddrFields().defaultCountry)) {
				chkOutput = false;
				warnings.add(tabMarker + BaseMessages.getString(PKG, "MDGlobalAddressDialog.Validation.MissingInputCountry"));
			}
		}
		if (chkOutput) {
			checkAddressOutputFields(meta, warnings, errors);
		}
	}

	// check address output
	public void checkAddressOutputFields(MDGlobalMeta meta, List<String> warnings, List<String> errors) {
		boolean allEmpty = true;
		String tabMarker = BaseMessages.getString(PKG, "MDCheckDialog.Validation.AddressTab.Marker");
		// check for at least one output column and check duplicate output columns
		if (!Const.isEmpty(meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_ORGANIZATION).metaValue))
			allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("OutputTab.Organization"), meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_ORGANIZATION).metaValue, warnings, errors);
		if (!Const.isEmpty(meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE1).metaValue))
			allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("OutputTab.AddressLine1"), meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE1).metaValue, warnings, errors);
		if (!Const.isEmpty(meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE2).metaValue))
			allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("OutputTab.AddressLine2"), meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE2).metaValue, warnings, errors);
		if (!Const.isEmpty(meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE3).metaValue))
			allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("OutputTab.AddressLine3"), meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE3).metaValue, warnings, errors);
		if (!Const.isEmpty(meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE4).metaValue))
			allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("OutputTab.AddressLine4"), meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE4).metaValue, warnings, errors);
		if (!Const.isEmpty(meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE5).metaValue))
			allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("OutputTab.AddressLine5"), meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE5).metaValue, warnings, errors);
		if (!Const.isEmpty(meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE6).metaValue))
			allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("OutputTab.AddressLine6"), meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE6).metaValue, warnings, errors);
		if (!Const.isEmpty(meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE7).metaValue))
			allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("OutputTab.AddressLine7"), meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE7).metaValue, warnings, errors);
		if (!Const.isEmpty(meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE8).metaValue))
			allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("OutputTab.AddressLine8"), meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_LINE8).metaValue, warnings, errors);
		if (!Const.isEmpty(meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_LATITUDE).metaValue))
			allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("OutputTab.Latitude"), meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_LATITUDE).metaValue, warnings, errors);
		if (!Const.isEmpty(meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_LONGITUDE).metaValue))
			allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("OutputTab.Longitude"), meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_LONGITUDE).metaValue, warnings, errors);
		if (!Const.isEmpty(meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_TYPE).metaValue))
			allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("OutputTab.USAddressTypeCode"), meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_ADDRESS_TYPE).metaValue, warnings, errors);
		if (!Const.isEmpty(meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_COUNTRY_NAME).metaValue))
			allEmpty = checkDuplicateOutputFields(tabMarker + getTextName("OutputTab.CountryName"), meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_COUNTRY_NAME).metaValue, warnings, errors);
		if (allEmpty)
			warnings.add(tabMarker + BaseMessages.getString(PKG, "MDCheckDialog.Validation.NoOutputFields"));
		// Additional address output only check for duplicates. ok if blank.
		tabMarker = BaseMessages.getString(PKG, "MDGlobalAddressDialog.Validation.AdditionalOutputOptions.Marker");
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.Alpha2"), meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_COUNTRY_ISO_ALPHA2).metaValue, warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.Alpha3"), meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_COUNTRY_ISO_ALPHA3).metaValue, warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.Numeric"), meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_COUNTRY_ISO_NUMERIC).metaValue, warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.SubPremises"), meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_PREMISES).metaValue, warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.DoubleDependentLocality"), meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_DOUBLE_DEPENDENT_LOCALITY).metaValue, warnings, errors);
		// checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.DependentLocality"),
// meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_DEPENDENT_LOCALITY).metaValue, warnings,
// errors);
		// checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.Locality"),
// meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_LOCALITY).metaValue, warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.Building"), meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_BUILDING).metaValue, warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.PremisesType"), meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_PREMISES_TYPE).metaValue, warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.PremisesNumber"), meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_PREMISES_NUMBER).metaValue, warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.Thoroughfare"), meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_THOROUGHFARE).metaValue, warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.ThoroughfarePreDirection"), meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_PRE_DIRECTION).metaValue, warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.ThoroughfareLeadingType"), meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_LEADING_TYPE).metaValue, warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.ThoroughfareName"), meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_THOROUGHFARE_NAME).metaValue, warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.ThoroughfareTrailingType"), meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_TRAILING_TYPE).metaValue, warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.ThoroughfarePostDirection"), meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_POST_DIRECTION).metaValue, warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.PostBox"), meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_POSTBOX).metaValue, warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.SubAdminArea"), meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_ADMINISTRATIVE_AREA).metaValue, warnings, errors);
		// checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.AdminArea"),
// meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_ADMINISTRATIVE_AREA).metaValue, warnings,
// errors);
		// checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.PostalCode"),
// meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_POSTAL_CODE).metaValue, warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.SubNationalArea"), meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_NATIONAL_AREA).metaValue, warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.SubPremisesType"), meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_PREMISES_TYPE).metaValue, warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.SubPremisesNumber"), meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_SUB_PREMISES_NUMBER).metaValue, warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.DependentThoroughfare"), meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE).metaValue, warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.DependentThoroughfarePreDirection"), meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE_PRE_DIRECTION).metaValue,
				warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.DependentThoroughfareLeadingType"), meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE_LEADING_TYPE).metaValue,
				warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.DependentThoroughfareName"), meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE_NAME).metaValue, warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.DependentThoroughfareTrailingType"), meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE_TRAILING_TYPE).metaValue,
				warnings, errors);
		checkDuplicateOutputFields(tabMarker + getTextName("AdditionalOutputDialog.DependentThoroughfarePostDirection"), meta.getAddrMeta().getAddrFields().webOutputFields.get(AddressFields.TAG_OUTPUT_DEPENDENT_THOROUGHFARE_POST_DIRECTION).metaValue,
				warnings, errors);
	}

	// check email output
	public void checkReportingOutputFields(MDGlobalMeta meta, List<String> warnings, List<String> errors) {
		/*
		 * if(isReportEnabled(meta)){ String tabMarker = BaseMessages.getString(PKG,
		 * "MDGlobalAddressDialog.Validation.ReportingTab.Marker"); //check for output column and duplicates if
		 * (Const.isEmpty(data.getReportMeta().getOutputReportDirname()) &&
		 * data.getReportMeta().isOptToFile()){ errors.add(tabMarker + BaseMessages.getString(PKG,
		 * "MDGlobalAddressDialog.Validation.MissingDirPath")); } Properties propsPlugin = MDProps.getProperties();
		 * SortedMap<String,String> reportDesc = new
		 * TreeMap<String,String>(); for(Object key: propsPlugin.keySet()){ if(((String)key).startsWith("ReportDesc")){
		 * reportDesc.put((String)key, propsPlugin.getProperty((String)key)); } } Object[] keys =
		 * reportDesc.keySet().toArray(); for(int i =
		 * 0; i < reportDesc.size(); i++){ if(reportDesc.get((String)keys[i]).contains("Name") &&
		 * (Const.isEmpty(data.getNameParse().getFullName()) && Const.isEmpty(data.getNameParse().getInputCompanyName())) &&
		 * data.getReportMeta().getOptsSubReports()[i]){ warnings.add(tabMarker + BaseMessages.getString(PKG,
		 * "MDCheckDialog.Validation.ReportSetName")); } if(reportDesc.get((String)keys[i]).contains("Address") &&
		 * Const.isEmpty(data.getAddressVerify().getInputAddressLine1()) && data.getReportMeta().getOptsSubReports()[i]){
		 * warnings.add(tabMarker + BaseMessages.getString(PKG, "MDCheckDialog.Validation.ReportSetAddress")); }
		 * if(reportDesc.get((String)keys[i]).contains("Email") && Const.isEmpty(data.getEmailVerify().getInputEmail()) &&
		 * data.getReportMeta().getOptsSubReports()[i]){ warnings.add(tabMarker + BaseMessages.getString(PKG,
		 * "MDCheckDialog.Validation.ReportSetEmail")); } if(reportDesc.get((String)keys[i]).contains("Geo") &&
		 * data.getGeoCoder().getAddrKeySource() == AddrKeySource.None && data.getReportMeta().getOptsSubReports()[i]){
		 * warnings.add(tabMarker +
		 * BaseMessages.getString(PKG, "MDCheckDialog.Validation.ReportSetGeo")); }
		 * if(reportDesc.get((String)keys[i]).contains("Phone") && Const.isEmpty(data.getPhoneVerify().getInputPhone()) &&
		 * data.getReportMeta().getOptsSubReports()[i]){
		 * warnings.add(tabMarker + BaseMessages.getString(PKG, "MDCheckDialog.Validation.ReportSetPhone")); } } }
		 */
	}

	public boolean showDialog() {
		return somethingToShow;
	}

	/*
	 * public void checkInitialization(MDGlobalMeta meta) { stepData = data; loadProps(); errors.clear(); warnings.clear();
	 * File f = new File(data.getAdvancedConfiguration().getLocalDataPath() + File.separator + "mdAddr.nat"); if (f.exists())
	 * {
	 * todayStamp = Calendar.getInstance(); todayStamp.add(Calendar.DAY_OF_MONTH, 1); Calendar checkDate =
	 * Calendar.getInstance(); chkDateStr = dateToString(checkDate); int licensedProducts =
	 * data.getAdvancedConfiguration().getProducts(); String
	 * licenseExpiration = data.getAdvancedConfiguration().getLicenceExpiration(); if (isDataExpiring(licenseExpiration)) {
	 * warnings.add(BaseMessages.getString(PKG, "MDCheckDialog.Validation.LicenseExpiring", licenseExpiration)); if(
	 * nameTimeStamp.length() < 1 || !stringToDate(nameTimeStamp).after(stringToDate(chkDateStr))){ somethingToShow = true; }
	 * } if ((licensedProducts & AdvancedConfigurationMeta.MDLICENSE_Name) != 0){ checkName(data); } if ((licensedProducts &
	 * AdvancedConfigurationMeta.MDLICENSE_GeoCode) != 0){ checkGeoCoder(data); }else{
	 * data.getGeoCoder().setInitializeOK(true); } if ((licensedProducts & AdvancedConfigurationMeta.MDLICENSE_Address) != 0)
	 * { try { checkAddress(data); } catch
	 * (KettleException e) { warnings.add("Address: " + e.getMessage()); } checkZip(data); } if ((licensedProducts &
	 * AdvancedConfigurationMeta.MDLICENSE_Phone) != 0){ checkPhone(data); } if ((licensedProducts &
	 * AdvancedConfigurationMeta.MDLICENSE_Email) != 0){ checkEmail(data); } } else{
	 * data.getAddressVerify().setInitializeOK(true); data.getGeoCoder().setInitializeOK(true);
	 * data.getNameParse().setInitializeOK(true);
	 * data.getEmailVerify().setInitializeOK(true); data.getPhoneVerify().setInitializeOK(true); } //checkDone = true; }
	 * private void checkName(MDCheckStepData data) { boolean initialOK = true; NameMeta nameParse = data.getNameParse(); //
	 * nameParse.setInitializeError(""); // nameParse.setInitializeWarn(""); mdName Name = null; try { Name =
	 * NameParseMeta.newName(); Name.SetLicenseString(data.getAdvancedConfiguration().getLicense());
	 * Name.SetPathToNameFiles(data.getAdvancedConfiguration().getLocalDataPath()); if (Name.InitializeDataFiles() ==
	 * mdName.ProgramStatus.NoError) { if (isDataExpiring(Name.GetDatabaseExpirationDate())) {
	 * warnings.add("Name data is expiring on " +
	 * Name.GetDatabaseExpirationDate()); if (nameTimeStamp.length() < 1 ||
	 * !stringToDate(nameTimeStamp).after(stringToDate(chkDateStr))) {
	 * nameParse.setInitializeWarn("Name data is expiring on " + Name.GetDatabaseExpirationDate()); showWarn = true;
	 * somethingToShow = true; } nameTimeStamp = dateToString(todayStamp); } } else { errors.add("Name: " +
	 * Name.GetInitializeErrorString()); if (nameTimeStamp.length() < 1 ||
	 * !stringToDate(nameTimeStamp).after(stringToDate(chkDateStr))) {
	 * nameParse.setInitializeError("Name: " + Name.GetInitializeErrorString()); showErrors = true; somethingToShow = true;
	 * nameTimeStamp = dateToString(todayStamp); } initialOK = false; } nameParse.setInitializeOK(initialOK); } catch
	 * (Throwable t) {
	 * t.printStackTrace(System.err); nameParse.setInitializeError("Name: " + t.getLocalizedMessage()); showErrors = true;
	 * somethingToShow = true; nameTimeStamp = dateToString(todayStamp); } finally { if (Name != null) Name.delete(); } }
	 * public void
	 * writeLogMessage(LogChannelInterface log){ //write log message if any if (warnings.size() > 0 || errors.size() > 0) {
	 * // Build the warning/error message StringBuffer message = new StringBuffer();
	 * message.append(getValidationMessage("MessageBoxPreamble")).append("\n"); message.append("\n"); if (warnings.size() >
	 * 0) { if (warnings.size() == 1) message.append(getValidationMessage("MessageBoxWarning")).append("\n"); else
	 * message.append(getValidationMessage("MessageBoxWarnings", "" + warnings.size())).append("\n"); for (String warning :
	 * warnings) message.append("   -  ").append(warning).append("\n"); message.append("\n");
	 * log.logBasic(message.toString()); } if
	 * (errors.size() > 0) { message.delete(0, message.length()); if (errors.size() == 1)
	 * message.append(getValidationMessage("MessageBoxError")).append("\n"); else
	 * message.append(getValidationMessage("MessageBoxErrors", "" +
	 * errors.size())).append("\n"); for (String error : errors) message.append("   -  ").append(error).append("\n");
	 * log.logError(message.toString()); } } }
	 */
	/*
	 * public boolean addrSafe(AddressFields avMeta){ boolean sfe = false; if (Const.isEmpty(avMeta.getInputAddressLine1())
	 * && Const.isEmpty(avMeta.getInputAddressLine2()) && Const.isEmpty(avMeta.getInputCity()) &&
	 * Const.isEmpty(avMeta.getInputCompany()) && Const.isEmpty(avMeta.getInputDeliveryPoint()) &&
	 * Const.isEmpty(avMeta.getInputLastName()) && Const.isEmpty(avMeta.getInputPlus4()) &&
	 * Const.isEmpty(avMeta.getInputState()) &&
	 * Const.isEmpty(avMeta.getInputSuite()) && Const.isEmpty(avMeta.getInputUrbanization()) &&
	 * Const.isEmpty(avMeta.getInputZip())){ sfe = true; } return sfe; }
	 */
	/*
	 * public static boolean isDataExpiring(String Date) { Calendar today = Calendar.getInstance(); Calendar expDate; boolean
	 * expiring = false; if (!Const.isEmpty(Date) && !Date.equals("N/A")) { expDate = stringToDate(Date);
	 * today.add(Calendar.DAY_OF_YEAR, 14); expiring = today.after(expDate); } return expiring; } public boolean
	 * dateExpired(String date){ if (date.equalsIgnoreCase("EXPIRED")) return true; Calendar today = Calendar.getInstance();
	 * Calendar cDate =
	 * stringToDate(date); if(cDate.before(today)) return true; return false; }
	 */
	// method subtracts 1 from month to accommodate
	// Calendar starting at 0
	@SuppressWarnings("unused")
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
		} else {
			// TODO handel null values ?
		}
		if (sDate.equalsIgnoreCase("NEVER"))
			date.add(Calendar.MONTH, 1);
		return date;
	}

	@SuppressWarnings("unused")
	private String dateToString(Calendar date) {
		String sDate = "";
		if ((date.get(Calendar.MONTH) + 1) < 10)
			sDate = "0" + String.valueOf(date.get(Calendar.MONTH) + 1);
		else
			sDate = String.valueOf(date.get(Calendar.MONTH) + 1);
		if (date.get(Calendar.DAY_OF_MONTH) < 10)
			sDate += "-0" + String.valueOf(date.get(Calendar.DAY_OF_MONTH));
		else
			sDate += "-" + String.valueOf(date.get(Calendar.DAY_OF_MONTH));
		sDate += "-" + String.valueOf(date.get(Calendar.YEAR));
		return sDate;
	}

	public void clearOutputList() {
		outFieldNames.clear();
	}

	public void clearInputList() {
		InFieldNames.clear();
		GeoFieldNames.clear();
	}

	private boolean checkDuplicateInputFields(String fieldName, String fieldValue, List<String> warnings, List<String> errors) {
		if (!Const.isEmpty(fieldValue)) {
			if (InFieldNames.containsKey(fieldValue)) {// gets value
				warnings.add(BaseMessages.getString(PKG, "MDGlobalAddressDialog.Validation.DuplicateInputColumns", fieldValue, InFieldNames.get(fieldValue), fieldName));
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
				warnings.add(BaseMessages.getString(PKG, "MDGlobalAddressDialog.Validation.DuplicateOutputColumns", fieldValue, outFieldNames.get(fieldValue), fieldName));
			} else {
				outFieldNames.put(fieldValue, fieldName);
			}
			return false;
		}
		return true;
	}

	// gets label text associated to text box
	private String getTextName(String tabName) {
		return BaseMessages.getString(PKG, "MDGlobalAddressDialog." + tabName + ".Label");
	}

	@SuppressWarnings("unused")
	private void loadProps() {
		timeStampProps = getProperties();
		licenceTimeStamp = timeStampProps.getProperty(TAG_LICENCE_TIME_STAMP, "");
		addressTimeStamp = timeStampProps.getProperty(TAG_ADDRESS_TIME_STAMP, "");
		caAddressTimeStamp = timeStampProps.getProperty(TAG_CA_ADDRESS_TIME_STAMP, "");
		nameTimeStamp = timeStampProps.getProperty(TAG_NAME_TIME_STAMP, "");
		phoneTimeStamp = timeStampProps.getProperty(TAG_PHONE_TIME_STAMP, "");
		emailTimeStamp = timeStampProps.getProperty(TAG_EMAIL_TIME_STAMP, "");
		geoTimeStamp = timeStampProps.getProperty(TAG_GEO_TIME_STAMP, "");
	}

	public void saveTimeProps() throws KettleException {
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
			if (out != null)
				try {
					out.close();
				} catch (IOException ignored) {
				}
		}
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
			if (in != null)
				try {
					in.close();
				} catch (IOException ignored) {
				}
		}
		return props;
	}

	/**
	 * @return The location of the property file
	 */
	private static File getVersionPropFile() {
		String propertyPath = MDGlobalData.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		propertyPath = propertyPath.replaceAll("steps.*", "steps");
		File propFile = null;
		propFile = new File(new File(propertyPath), TAG_TIME_STAMP_FILE);
		return propFile;
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

	public static String getAddressTimeStamp() {
		return addressTimeStamp;
	}

	public static void setAddressTimeStamp(String addressTimeStamp) {
		Validations.addressTimeStamp = addressTimeStamp;
	}

	public static String getNameTimeStamp() {
		return nameTimeStamp;
	}

	public static void setNameTimeStamp(String nameTimeStamp) {
		Validations.nameTimeStamp = nameTimeStamp;
	}

	public static String getPhoneTimeStamp() {
		return phoneTimeStamp;
	}

	public static void setPhoneTimeStamp(String phoneTimeStamp) {
		Validations.phoneTimeStamp = phoneTimeStamp;
	}

	public static String getEmailTimeStamp() {
		return emailTimeStamp;
	}

	public static void setEmailTimeStamp(String emailTimeStamp) {
		Validations.emailTimeStamp = emailTimeStamp;
	}

	public static String getGeoTimeStamp() {
		return geoTimeStamp;
	}

	public static void setGeoTimeStamp(String geoTimeStamp) {
		Validations.geoTimeStamp = geoTimeStamp;
	}

	public static String getLicenceTimeStamp() {
		return licenceTimeStamp;
	}

	public static void setLicenceTimeStamp(String licenceTimeStamp) {
		Validations.licenceTimeStamp = licenceTimeStamp;
	}

	public static String getCaAddressTimeStamp() {
		return caAddressTimeStamp;
	}

	public static void setCaAddressTimeStamp(String caAddressTimeStamp) {
		Validations.caAddressTimeStamp = caAddressTimeStamp;
	}

	/**
	 * @param args
	 * @return A validation message
	 */
	public static String getValidationMessage(String name, String... args) {
		return BaseMessages.getString(PKG, "MDGlobalAddressDialog.Validation." + name, args);
	}

	public boolean isReportEnabled(MDGlobalMeta meta) {
		boolean returnBool = false;
		/*
		 * if (data.getAddressVerify() != null && data.getAddressVerify().isEnabled()){ returnBool = true; }
		 */
		return returnBool;
	}
}
