package com.melissadata.kettle.businesscoder.data;

import java.util.HashMap;

import com.melissadata.cz.support.MetaVal;

public class BusinessCoderFields {

	public static final String DISABLE_STRING               = "_census_disabled_";

	public static final String TAG_BUSINESS_CODER_OPTIONS   = "business_coder_options";
	public static final String TAG_BUSINESS_CODER_INPUT     = "business_coder_input";
	public static final String TAG_BUSINESS_CODER_OUTPUT    = "business_coder_output";

	public static String       TAG_OPTION_DOMINANT_BUSINESS = "opt_dominant_business";
	public static String       TAG_OPTION_INCLUDE_CENSUS    = "opt_include_census";

	// INPUT FIELDS
	public static String       TAG_INPUT_BUSINESS_NAME      = "input_company_name";
	public static String       TAG_INPUT_ADDRESS_LINE1      = "input_addressline1";
	public static String       TAG_INPUT_ADDRESS_LINE2      = "input_addressline2";
	public static String       TAG_INPUT_CITY               = "input_city";
	public static String       TAG_INPUT_STATE              = "input_state";
	public static String       TAG_INPUT_POSTAL_CODE        = "input_postalcode";
	public static String			TAG_INPUT_COUNTRY				= "input_country";
	public static String			TAG_INPUT_ADDRESS_KEY			= "input_address_key";
	public static String			TAG_INPUT_WEB_ADDRESS			= "input_web_address";
	public static String			TAG_INPUT_STOCK_TICKER			= "input_stock_ticker";
	public static String			TAG_INPUT_PHONE					= "input_phone";

	// OUTPUT FIELDS
	public static String			TAG_OUTPUT_RESULTS				= "output_results";
	public static String			TAG_OUTPUT_COMPANY_NAME			= "output_company_name";
	public static String			TAG_OUTPUT_ADDRESS_LINE1		= "output_address_line1";
	public static String			TAG_OUTPUT_SUITE				= "output_suite";
	public static String			TAG_OUTPUT_CITY					= "output_city";
	public static String			TAG_OUTPUT_STATE				= "output_state";
	public static String			TAG_OUTPUT_POSTAL_CODE			= "output_postal_code";

	public static String			TAG_OUTPUT_COUNTRY_NAME			= "output_country_name";
	public static String			TAG_OUTPUT_COUNTRY_CODE			= "output_country_code";
	public static String            TAG_OUTPUT_EIN                  = "output_ein";

	public static String			TAG_OUTPUT_LOCATION_TYPE		= "output_location_type";
	public static String			TAG_OUTPUT_PHONE				= "output_phone";
	public static String			TAG_OUTPUT_STOCK_TICKER			= "output_stock_ticker";
	public static String			TAG_OUTPUT_WEB_ADDRESS			= "output_web_address";
// public static String TAG_OUTPUT_LOCAL_EMPLOYEES_ESTIMATE = "output_local_employees_estimate";
// public static String TAG_OUTPUT_LOCAL_SALES_ESTIMATE = "output_local_sales_estimate";
	public static String			TAG_OUTPUT_EMPLOYEES_ESTIMATE	= "output_employees_estimate";
	public static String			TAG_OUTPUT_SALES_ESTIMATE		= "output_sales_estimate";
// public static String TAG_OUTPUT_FEMALE_OWNED = "output_female_owned";
// public static String TAG_OUTPUT_SMALL_BUSINESS = "output_small_business";
// public static String TAG_OUTPUT_HOME_BASED_BUSINESS = "output_home_based_business";
	public static String			TAG_OUTPUT_SIC_CODE				= "output_sic_code";
	public static String			TAG_OUTPUT_SIC_CODE2			= "output_sic_code2";
	public static String			TAG_OUTPUT_SIC_CODE3			= "output_sic_code3";
	public static String			TAG_OUTPUT_SIC_DESCRIPTION		= "output_sic_discription";
	public static String			TAG_OUTPUT_SIC_DESCRIPTION2		= "output_sic_discription2";
	public static String			TAG_OUTPUT_SIC_DESCRIPTION3		= "output_sic_discription3";
	public static String			TAG_OUTPUT_NAICS_CODE			= "output_naics_code";
	public static String			TAG_OUTPUT_NAICS_CODE2			= "output_naics_code2";
	public static String			TAG_OUTPUT_NAICS_CODE3			= "output_naics_code3";
	public static String			TAG_OUTPUT_NAICS_DESCRIPTION	= "output_naics_discription";
	public static String			TAG_OUTPUT_NAICS_DESCRIPTION2	= "output_naics_discription2";
	public static String			TAG_OUTPUT_NAICS_DESCRIPTION3	= "output_naics_discription3";

	public static String			TAG_OUTPUT_RECORD_ID			= "output_record_id";

	public static String			TAG_OUTPUT_CENSUS_BLOCK			= "output_census_block";
	public static String			TAG_OUTPUT_CENSUS_TRACT			= "output_census_tract";
	public static String			TAG_OUTPUT_COUNTY_FIPS			= "output_county_fips";
	public static String			TAG_OUTPUT_COUNTY_NAME			= "output_county_name";
	public static String			TAG_OUTPUT_DELIVERY_INDICATOR	= "output_delivery_indicator";
	public static String			TAG_OUTPUT_LATITUDE				= "output_latitude";
	public static String			TAG_OUTPUT_LONGITUDE			= "output_longitude";
	public static String			TAG_OUTPUT_MD_ADDRESS_KEY		= "output_md_address_key";
	public static String			TAG_OUTPUT_MD_ADDRESS_KEY_BASE	= "output_md_address_key_base";
	public static String			TAG_OUTPUT_PLUS_4				= "output_plus_4";
	public static String			TAG_OUTPUT_PLACE_NAME			= "output_place_name";
	public static String			TAG_OUTPUT_PLACE_CODE			= "output_place_code";

	public static String			TAG_OUTPUT_FIRST_NAME_1			= "output_first_name_1";
	public static String			TAG_OUTPUT_LAST_NAME_1			= "output_last_name_1";
	public static String			TAG_OUTPUT_GENDER_1				= "output_gender_1";
	public static String			TAG_OUTPUT_TITLE_1				= "output_title_1";
	public static String			TAG_OUTPUT_EMAIL_1				= "output_email_1";

	public static String			TAG_OUTPUT_FIRST_NAME_2			= "output_first_name_2";
	public static String			TAG_OUTPUT_LAST_NAME_2			= "output_last_name_2";
	public static String			TAG_OUTPUT_GENDER_2				= "output_gender_2";
	public static String			TAG_OUTPUT_TITLE_2				= "output_title_2";
	public static String			TAG_OUTPUT_EMAIL_2				= "output_email_2";

	public HashMap<String, MetaVal>	optionFields;
	public HashMap<String, MetaVal>	inputFields;
	public HashMap<String, MetaVal>	outputFields;
	public String					webVersion						= "";
	public int						fieldsAdded;

	/**
	 * checks to see if the minimum requirements to run are met
	 *
	 * @return
	 */
	public boolean hasMinRequirements() {
		// FIXME MIN input
		return true;
	}

	/**
	 * initializes all the default value
	 */
	public void init() {

		if (optionFields == null) {
			optionFields = new HashMap<String, MetaVal>();
		}
		if (inputFields == null) {
			inputFields = new HashMap<String, MetaVal>();
		}
		if (outputFields == null) {
			outputFields = new HashMap<String, MetaVal>();
		}

		// repository xml key tag, metavalue default, web tag, and size are set here
		// OPTIONS
		optionFields.put(TAG_OPTION_DOMINANT_BUSINESS, new MetaVal("yes", "ReturnDominantBusiness:", 0));
		optionFields.put(TAG_OPTION_INCLUDE_CENSUS, new MetaVal("false", "GrpCensus", 0));


		// INPUTS
		inputFields.put(TAG_INPUT_BUSINESS_NAME, new MetaVal("", "comp", 50));
		inputFields.put(TAG_INPUT_ADDRESS_LINE1, new MetaVal("", "a1", 50));
		inputFields.put(TAG_INPUT_ADDRESS_LINE2, new MetaVal("", "a2", 20));
		inputFields.put(TAG_INPUT_CITY, new MetaVal("", "city", 50));
		inputFields.put(TAG_INPUT_STATE, new MetaVal("", "state", 3));
		inputFields.put(TAG_INPUT_POSTAL_CODE, new MetaVal("", "postal", 10));
		inputFields.put(TAG_INPUT_COUNTRY, new MetaVal("US", "ctry", 50));
		inputFields.put(TAG_INPUT_PHONE, new MetaVal("", "phone", 12));
		inputFields.put(TAG_INPUT_ADDRESS_KEY, new MetaVal("", "mak", 50));
		inputFields.put(TAG_INPUT_STOCK_TICKER, new MetaVal("", "stock", 10));
		inputFields.put(TAG_INPUT_WEB_ADDRESS, new MetaVal("", "web", 50));

		// OUTPUTS
		outputFields.put(TAG_OUTPUT_RESULTS, new MetaVal("MD_Results", "Results", 50));
		outputFields.put(TAG_OUTPUT_COMPANY_NAME, new MetaVal("MD_CompanyName", "CompanyName", 50));
		outputFields.put(TAG_OUTPUT_ADDRESS_LINE1, new MetaVal("MD_AddressLine1", "AddressLine1", 50));
		outputFields.put(TAG_OUTPUT_SUITE, new MetaVal("MD_Suite", "Suite", 50));
		outputFields.put(TAG_OUTPUT_CITY, new MetaVal("MD_City", "City", 50));
		outputFields.put(TAG_OUTPUT_STATE, new MetaVal("MD_State", "State", 50));

		outputFields.put(TAG_OUTPUT_COUNTRY_NAME, new MetaVal("MD_CountryName", "CountryName", 50));
		outputFields.put(TAG_OUTPUT_COUNTRY_CODE, new MetaVal("MD_CountryCode", "CountryCode", 50));
		outputFields.put(TAG_OUTPUT_EIN, new MetaVal("MD_EIN", "EIN", 50));

		outputFields.put(TAG_OUTPUT_POSTAL_CODE, new MetaVal("MD_PostalCode", "PostalCode", 10));
		outputFields.put(TAG_OUTPUT_LOCATION_TYPE, new MetaVal("MD_LocationType", "LocationType", 50));
// outputFields.put(TAG_OUTPUT_FEMALE_OWNED, new MetaVal("MD_FemaleOwned", "FemaleOwned", 50));
// outputFields.put(TAG_OUTPUT_SMALL_BUSINESS, new MetaVal("MD_SmallBusiness", "SmallBusiness", 50));
// outputFields.put(TAG_OUTPUT_HOME_BASED_BUSINESS, new MetaVal("MD_HomeBasedBusiness", "HomeBasedBusiness", 50));
		outputFields.put(TAG_OUTPUT_PHONE, new MetaVal("MD_Phone", "Phone", 50));
// outputFields.put(TAG_OUTPUT_LOCAL_EMPLOYEES_ESTIMATE, new MetaVal("MD_LocalEmployeesEstimate", "LocalEmployeesEstimate",
// 50));
// outputFields.put(TAG_OUTPUT_LOCAL_SALES_ESTIMATE, new MetaVal("MD_LocalSalesEstimate", "LocalSalesEstimate", 50));
		outputFields.put(TAG_OUTPUT_EMPLOYEES_ESTIMATE, new MetaVal("MD_TotalEmployeesEstimate", "EmployeesEstimate", 50));
		outputFields.put(TAG_OUTPUT_SALES_ESTIMATE, new MetaVal("MD_TotalSalesEstimate", "SalesEstimate", 50));
		outputFields.put(TAG_OUTPUT_STOCK_TICKER, new MetaVal("MD_StockTicker", "StockTicker", 50));
		outputFields.put(TAG_OUTPUT_WEB_ADDRESS, new MetaVal("MD_WebAddress", "WebAddress", 50));
		outputFields.put(TAG_OUTPUT_SIC_CODE, new MetaVal("MD_SICCode1", "SICCode1", 50));
		outputFields.put(TAG_OUTPUT_SIC_DESCRIPTION, new MetaVal("MD_SICDescription1", "SICDescription1", 50));
		outputFields.put(TAG_OUTPUT_SIC_CODE2, new MetaVal("MD_SICCode2", "SICCode2", 50));
		outputFields.put(TAG_OUTPUT_SIC_DESCRIPTION2, new MetaVal("MD_SICDescription2", "SICDescription2", 50));
		outputFields.put(TAG_OUTPUT_SIC_CODE3, new MetaVal("MD_SICCode3", "SICCode3", 50));
		outputFields.put(TAG_OUTPUT_SIC_DESCRIPTION3, new MetaVal("MD_SICDescription3", "SICDescription3", 50));
		outputFields.put(TAG_OUTPUT_NAICS_CODE, new MetaVal("MD_NAICSCode1", "NAICSCode1", 50));
		outputFields.put(TAG_OUTPUT_NAICS_DESCRIPTION, new MetaVal("MD_NAICSDescription1", "NAICSDescription1", 50));
		outputFields.put(TAG_OUTPUT_NAICS_CODE2, new MetaVal("MD_NAICSCode2", "NAICSCode2", 50));
		outputFields.put(TAG_OUTPUT_NAICS_DESCRIPTION2, new MetaVal("MD_NAICSDescription2", "NAICSDescription2", 50));
		outputFields.put(TAG_OUTPUT_NAICS_CODE3, new MetaVal("MD_NAICSCode3", "NAICSCode3", 50));
		outputFields.put(TAG_OUTPUT_NAICS_DESCRIPTION3, new MetaVal("MD_NAICSDescription3", "NAICSDescription3", 50));

		outputFields.put(TAG_OUTPUT_CENSUS_BLOCK, new MetaVal("MD_CensusBlock", "CensusBlock", 50));
		outputFields.put(TAG_OUTPUT_CENSUS_TRACT, new MetaVal("MD_CensusTract", "CensusTract", 50));
		outputFields.put(TAG_OUTPUT_COUNTY_FIPS, new MetaVal("MD_CountyFIPS", "CountyFIPS", 50));
		outputFields.put(TAG_OUTPUT_COUNTY_NAME, new MetaVal("MD_CountyName", "CountyName", 50));
		outputFields.put(TAG_OUTPUT_DELIVERY_INDICATOR, new MetaVal("MD_DeliveryIndicator", "DeliveryIndicator", 50));
		outputFields.put(TAG_OUTPUT_LATITUDE, new MetaVal("MD_Latitude", "Latitude", 50));
		outputFields.put(TAG_OUTPUT_LONGITUDE, new MetaVal("MD_Longitude", "Longitude", 50));
		outputFields.put(TAG_OUTPUT_MD_ADDRESS_KEY, new MetaVal("MD_MelissaAddressKey", "MelissaAddressKey", 50));
		outputFields.put(TAG_OUTPUT_MD_ADDRESS_KEY_BASE, new MetaVal("MD_MelissaAddressKeyBase", "MelissaAddressKeyBase", 50));
		outputFields.put(TAG_OUTPUT_PLUS_4, new MetaVal("MD_Plus4", "Plus4", 50));
		outputFields.put(TAG_OUTPUT_PLACE_NAME, new MetaVal("MD_PlaceName", "PlaceName", 50));
		outputFields.put(TAG_OUTPUT_PLACE_CODE, new MetaVal("MD_PlaceCode", "PlaceCode", 50));

		outputFields.put(TAG_OUTPUT_FIRST_NAME_1, new MetaVal("MD_FirstName1", "NameFirst", 50));
		outputFields.put(TAG_OUTPUT_LAST_NAME_1, new MetaVal("MD_LastName1", "NameLast", 50));
		outputFields.put(TAG_OUTPUT_GENDER_1, new MetaVal("MD_Gender1", "Gender", 50));
		outputFields.put(TAG_OUTPUT_TITLE_1, new MetaVal("MD_Title1", "Title", 50));
		outputFields.put(TAG_OUTPUT_EMAIL_1, new MetaVal("MD_Email1", "Email", 50));

		outputFields.put(TAG_OUTPUT_FIRST_NAME_2, new MetaVal("MD_FirstName2", "NameFirst", 50));
		outputFields.put(TAG_OUTPUT_LAST_NAME_2, new MetaVal("MD_LastName2", "NameLast", 50));
		outputFields.put(TAG_OUTPUT_GENDER_2, new MetaVal("MD_Gender2", "Gender", 50));
		outputFields.put(TAG_OUTPUT_TITLE_2, new MetaVal("MD_Title2", "Title", 50));
		outputFields.put(TAG_OUTPUT_EMAIL_2, new MetaVal("MD_Email2", "Email", 50));

	}
}
