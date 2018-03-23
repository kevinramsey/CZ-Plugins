package com.melissadata.kettle.globalverify.data;

import java.util.HashMap;
import com.melissadata.kettle.globalverify.support.MetaVal;
import org.pentaho.di.core.Const;

public class PhoneFields {
	public static final String		TAG_GLOBAL_PHONE_INPUT			= "globalphone_input";
	public static final String		TAG_GLOBAL_PHONE_OUTPUT			= "globalphone_output";
	public static final String		TAG_GLOBAL_PHONE_OPTION			= "globalphone_option";
	// Inputs
	public static String			TAG_INPUT_PHONE					= "globalphone_input_phone";
	public static String			TAG_INPUT_COUNTRY				= "globalphone_input_country";
	public static String			TAG_INPUT_DEFAULT_COUNTRY		= "globalphone_input_default_country";
	public static String			TAG_INPUT_DIALING_COUNTRY		= "globalphone_input_dialing_country";
	// Outputs
	public static String			TAG_OUTPUT_RESULTS				= "globalphone_output_results";
	public static String			TAG_OUTPUT_PHONE				= "globalphone_output_phone";
	public static String			TAG_OUTPUT_SUBSCRIBER			= "globalphone_output_subscriber";
	public static String			TAG_OUTPUT_CARRIER				= "globalphone_output_carrier";
	public static String			TAG_OUTPUT_CALLER_ID			= "globalphone_output_caller_id";
	public static String			TAG_OUTPUT_COUNTRY				= "globalphone_output_country";
	public static String			TAG_OUTPUT_COUNTRY_CODE			= "globalphone_output_country_code";
	public static String			TAG_OUTPUT_COUNTRY_ABBREVIATION	= "globalphone_output_country_abbrivation";
	public static String			TAG_OUTPUT_INTERNATIONAL_PREFIX	= "globalphone_output_international_prefix";
	public static String			TAG_OUTPUT_NATIONAL_PREFIX		= "globalphone_output_national_prefix";
	public static String			TAG_OUTPUT_DEST_CODE			= "globalphone_output_destination_code";
	public static String			TAG_OUTPUT_LOCALITY				= "globalphone_output_locality";
	public static String			TAG_OUTPUT_ADMIN_AREA			= "globalphone_output_admin_area";
	public static String			TAG_OUTPUT_LANGUAGE				= "globalphone_output_language";
	public static String			TAG_OUTPUT_UTC					= "globalphone_output_utc";
	public static String			TAG_OUTPUT_DST					= "globalphone_output_dst";
	public static String			TAG_OUTPUT_LATITUDE				= "globalphone_output_latitude";
	public static String			TAG_OUTPUT_LONGITUDE			= "globalphone_output_longitude";
	public static String			TAG_OUTPUT_SUGGESTIONS			= "globalphone_output_suggestions";
	public static String 			TAG_OUTPUT_INTERNATIONAL_NUMBER = "globalphone_output_internationalNumber";
	public static String 			TAG_OUTPUT_POSTAL_CODE			= "globalphone_output_postal_code";
	
	public static String			TAG_OPTION_DIALING_CODE			= "globalphone_option_dialing_code";
	public static String			TAG_OPTION_VERIFY				= "globalphone_option_verify";
	public static String            TAG_OPTION_CALLER_ID            = "globalphone_option_callerID";
	
	

	public HashMap<String, MetaVal>	inputFields;
	public HashMap<String, MetaVal>	outputFields;
	public HashMap<String, MetaVal>	optionFields;

	public String					webVersion						= "";
	public int						fieldsAdded;

	public boolean hasMinRequirements() {
		return !Const.isEmpty(inputFields.get(TAG_INPUT_PHONE).metaValue);
	}

	/**
	 * initializes all the default value
	 */
	public void init() {
		if (inputFields == null) {
			inputFields = new HashMap<String, MetaVal>();
		}
		if (outputFields == null) {
			outputFields = new HashMap<String, MetaVal>();
		}

		if (optionFields == null) {
			optionFields = new HashMap<String, MetaVal>();
		}

		inputFields.put(TAG_INPUT_PHONE, new MetaVal("", "PhoneNumber", 50));
		inputFields.put(TAG_INPUT_COUNTRY, new MetaVal("", "CountryName", 50));
		inputFields.put(TAG_INPUT_DEFAULT_COUNTRY, new MetaVal("United States", "", 50));
		inputFields.put(TAG_INPUT_DIALING_COUNTRY, new MetaVal("United States", "CountryOfOrigin", 50));

		optionFields.put(TAG_OPTION_DIALING_CODE, new MetaVal("", "DefaultCallingCode", 5));
		optionFields.put(TAG_OPTION_VERIFY, new MetaVal("Express", "VerifyPhone", 5));
		//FIXME check on caller ID
		optionFields.put(TAG_OPTION_CALLER_ID, new MetaVal("false", "CallerID", 5));

		outputFields.put(TAG_OUTPUT_PHONE, new MetaVal("MD_Phone", "PhoneNumber", 50));
		outputFields.put(TAG_OUTPUT_SUBSCRIBER, new MetaVal("MD_Phone_Subscriber_number", "PhoneSubscriberNumber", 50));
		outputFields.put(TAG_OUTPUT_CARRIER, new MetaVal("MD_Phone_Carrier", "Carrier", 50));
		outputFields.put(TAG_OUTPUT_CALLER_ID, new MetaVal("MD_Phone_CallerID", "CallerID", 50));
		outputFields.put(TAG_OUTPUT_COUNTRY, new MetaVal("MD_Phone_Country_name", "CountryName", 50));
		outputFields.put(TAG_OUTPUT_COUNTRY_ABBREVIATION, new MetaVal("MD_Phone_Country_Abbreviation", "CountryAbbreviation", 50));
		outputFields.put(TAG_OUTPUT_COUNTRY_CODE, new MetaVal("MD_Phone_Country_Dialing_Code", "PhoneCountryDialingCode", 50));
		outputFields.put(TAG_OUTPUT_INTERNATIONAL_PREFIX, new MetaVal("MD_Phone_International_Prefix", "PhoneInternationalPrefix", 50));
		outputFields.put(TAG_OUTPUT_NATIONAL_PREFIX, new MetaVal("MD_Phone_National_Prefix", "PhoneNationPrefix", 50));
		outputFields.put(TAG_OUTPUT_DEST_CODE, new MetaVal("MD_Phone_Destination_Code", "PhoneNationalDestinationCode", 50));
		outputFields.put(TAG_OUTPUT_LOCALITY, new MetaVal("MD_Phone_Locality", "Locality", 50));
		outputFields.put(TAG_OUTPUT_ADMIN_AREA, new MetaVal("MD_Phone_Administrative_Area", "AdministrativeArea", 50));
		outputFields.put(TAG_OUTPUT_LANGUAGE, new MetaVal("MD_Phone_Language", "Language", 50));
		outputFields.put(TAG_OUTPUT_UTC, new MetaVal("MD_Phone_UTC", "UTC", 50));
		outputFields.put(TAG_OUTPUT_DST, new MetaVal("MD_Phone_DST", "DST", 50));
		outputFields.put(TAG_OUTPUT_LATITUDE, new MetaVal("MD_Phone_Latitude", "Latitude", 50));
		outputFields.put(TAG_OUTPUT_LONGITUDE, new MetaVal("MD_Phone_Longitude", "Longitude", 50));
		outputFields.put(TAG_OUTPUT_INTERNATIONAL_NUMBER, new MetaVal("MD_Phone_International_Phone", "InternationalPhoneNumber", 50));
		outputFields.put(TAG_OUTPUT_POSTAL_CODE, new MetaVal("MD_Phone_Postal_Code", "PostalCode", 50));
		outputFields.put(TAG_OUTPUT_SUGGESTIONS, new MetaVal("MD_Phone_Suggestions", "Suggestions", 50));
	}
}
