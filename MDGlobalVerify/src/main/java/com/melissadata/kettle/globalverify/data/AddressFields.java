package com.melissadata.kettle.globalverify.data;

import com.melissadata.kettle.globalverify.support.MetaVal;
import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.Const;

import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

public class AddressFields {

	public static final String                   TAG_GLOBAL_ADDRESS_WEB_OPTIONS                   = "globaladdress_options";
	public static final String                   TAG_GLOBAL_ADDRESS_OP_COUNTRIES                  = "globaladdress_op_countries";
	public static final String                   TAG_GLOBAL_ADDRESS_COUNTRY_INDEX                 = "globaladdress_country_field_index";
	public static final String                   TAG_GLOBAL_ADDRESS_INPUT                         = "globaladdress_input";
	public static final String                   TAG_GLOBAL_ADDRESS_WEB_OUTPUT                    = "globaladdress_output";
	public static final String                   TAG_GLOBAL_ADDRESS_PROCESS_OPTION                = "globaladdress_processing_option";
	public static final String                   TAG_GLOBAL_ADDRESS_ADD_CODES                     = "globaladdress_add_codes";
	// Option Tags
	public static final String                   TAG_OPTION_LINE_SEPARATOR                        = "opt_line_separator";
	public static final String                   TAG_OPTION_OUTPUT_SCRIPT                         = "opt_output_script";
	public static final String                   TAG_OPTION_COUNTRY_OF_ORIGIN                     = "opt_country_of_origin";
	public static final String                   TAG_OPTION_DELIVERY_LINE                         = "opt_delivery_line";
	public static final String                   TAG_OPTION_INPUT_DISPLAY                         = "opt_input_display";
	// Input Address Parameters
	public static final String                   TAG_INPUT_ORGANIZATION                           = "input_organization";
	public static final String                   TAG_INPUT_ADDRESS_LINE1                          = "input_address_line1";
	public static final String                   TAG_INPUT_ADDRESS_LINE2                          = "input_address_line2";
	public static final String                   TAG_INPUT_ADDRESS_LINE3                          = "input_address_line3";
	public static final String                   TAG_INPUT_ADDRESS_LINE4                          = "input_address_line4";
	public static final String                   TAG_INPUT_ADDRESS_LINE5                          = "input_address_line5";
	public static final String                   TAG_INPUT_ADDRESS_LINE6                          = "input_address_line6";
	public static final String                   TAG_INPUT_ADDRESS_LINE7                          = "input_address_line7";
	public static final String                   TAG_INPUT_ADDRESS_LINE8                          = "input_address_line8";
	// Input Address parsed Parameters
	public static final String                   TAG_INPUT_SUB_PREMISES                           = "input_sub_premises";
	public static final String                   TAG_INPUT_DOUBLE_DEPENDENT_LOCALITY              = "input_double_dependent_locality";
	public static final String                   TAG_INPUT_DEPENDENT_LOCALITY                     = "input_dependent_locality";
	public static final String                   TAG_INPUT_LOCALITY                               = "input_locality";
	public static final String                   TAG_INPUT_SUB_ADMINISTRATIVE_AREA                = "input_sub_administrative_area";
	public static final String                   TAG_INPUT_ADMINISTRATIVE_AREA                    = "input_administrative_area";
	public static final String                   TAG_INPUT_POSTAL_CODE                            = "input_postal_code";
	public static final String                   TAG_INPUT_SUB_NATIONAL_AREA                      = "input_sub_national_area";
	public static final String                   TAG_INPUT_COUNTRY                                = "input_country";
	public static final String                   TAG_INPUT_DEFAULT_COUNTRY                        = "input_default_country";
	//  Output
	//Output Address Parameters
	public static final String                   TAG_OUTPUT_ORGANIZATION                          = "output_organization";
	public static final String                   TAG_OUTPUT_ADDRESS_LINE1                         = "output_address_line1";
	public static final String                   TAG_OUTPUT_ADDRESS_LINE2                         = "output_address_line2";
	public static final String                   TAG_OUTPUT_ADDRESS_LINE3                         = "output_address_line3";
	public static final String                   TAG_OUTPUT_ADDRESS_LINE4                         = "output_address_line4";
	public static final String                   TAG_OUTPUT_ADDRESS_LINE5                         = "output_address_line5";
	public static final String                   TAG_OUTPUT_ADDRESS_LINE6                         = "output_address_line6";
	public static final String                   TAG_OUTPUT_ADDRESS_LINE7                         = "output_address_line7";
	public static final String                   TAG_OUTPUT_ADDRESS_LINE8                         = "output_address_line8";
	public static final String                   TAG_OUTPUT_DELIVERY_LINE                         = "output_delivery_line";
	public static final String                   TAG_OUTPUT_FORMATED_ADDRESS                      = "output_full_address";
	//Parsed Sub-Premises Parameters
	public static final String                   TAG_OUTPUT_BUILDING                              = "output_building";
	public static final String                   TAG_OUTPUT_SUB_BUILDING                          = "output_sub_building";
	public static final String                   TAG_OUTPUT_SUB_BUILDING_NUMBER                   = "output_sub_building_number";
	public static final String                   TAG_OUTPUT_SUB_BUILDING_TYPE                     = "output_sub_building_type";
	public static final String                   TAG_OUTPUT_SUB_PREMISES                          = "output_sub_premises";
	public static final String                   TAG_OUTPUT_SUB_PREMISES_NUMBER                   = "output_sub_premises_number";
	public static final String                   TAG_OUTPUT_SUB_PREMISES_TYPE                     = "output_sub_premises_type";
	public static final String                   TAG_OUTPUT_SUB_PREMISES_LEVEL                    = "output_sub_premises_level";
	public static final String                   TAG_OUTPUT_SUB_PREMISES_LEVEL_NUMBER             = "output_sub_premises_level_number";
	public static final String                   TAG_OUTPUT_SUB_PREMISES_LEVEL_TYPE               = "output_sub_premises_level_type";
	//Parsed Thoroughfare Parameters
	public static final String                   TAG_OUTPUT_PREMISES                              = "output_premises";
	public static final String                   TAG_OUTPUT_PREMISES_NUMBER                       = "output_premises_number";
	public static final String                   TAG_OUTPUT_PREMISES_TYPE                         = "output_premises_type";
	public static final String                   TAG_OUTPUT_THOROUGHFARE                          = "output_thoroughfare";
	public static final String                   TAG_OUTPUT_LEADING_TYPE                          = "output_leading_type";
	public static final String                   TAG_OUTPUT_THOROUGHFARE_NAME                     = "output_thoroughfare_name";
	public static final String                   TAG_OUTPUT_POST_DIRECTION                        = "output_post_direction";
	public static final String                   TAG_OUTPUT_PRE_DIRECTION                         = "output_pre_direction";
	public static final String                   TAG_OUTPUT_TRAILING_TYPE                         = "output_trailing_type";
	public static final String                   TAG_OUTPUT_TYPE_ATTACHED                         = "output_type_attached";
	//Parsed Dependent Thoroughfare Columns
	public static final String                   TAG_OUTPUT_DEPENDENT_THOROUGHFARE                = "output_dependent_thoroughfare";
	public static final String                   TAG_OUTPUT_DEPENDENT_THOROUGHFARE_LEADING_TYPE   = "output_dependent_thoroughfare_leading_type";
	public static final String                   TAG_OUTPUT_DEPENDENT_THOROUGHFARE_NAME           = "output_dependent_thoroughfare_name";
	public static final String                   TAG_OUTPUT_DEPENDENT_THOROUGHFARE_POST_DIRECTION = "output_dependent_thoroughfare_post_direction";
	public static final String                   TAG_OUTPUT_DEPENDENT_THOROUGHFARE_PRE_DIRECTION  = "output_dependent_thoroughfare_pre_direction";
	public static final String                   TAG_OUTPUT_DEPENDENT_THOROUGHFARE_TRAILING_TYPE  = "output_dependent_thoroughfare_trailing_type";
	public static final String                   TAG_OUTPUT_DEPENDENT_THOROUGHFARE_TYPE_ATTACHED  = "output_dependant_thoroughfare_type_attached";
	//Parsed Postal Facility Columns
	public static final String                   TAG_OUTPUT_POSTBOX                               = "output_postbox";
	public static final String                   TAG_OUTPUT_POSTAL_CODE                           = "output_postal_code";
	public static final String                   TAG_OUTPUT_PERSONAL_ID                           = "output_personal_id";
	public static final String                   TAG_OUTPUT_POST_OFFICE_LOCATION                  = "output_postoffice_location";
	//Parsed Regional Columns
	public static final String                   TAG_OUTPUT_ADMINISTRATIVE_AREA                   = "output_administrative_area";
	public static final String                   TAG_OUTPUT_COUNTY_NAME                           = "output_county_name";
	public static final String                   TAG_OUTPUT_DEPENDENT_LOCALITY                    = "output_dependent_locality";
	public static final String                   TAG_OUTPUT_DOUBLE_DEPENDENT_LOCALITY             = "output_double_dependent_locality";
	public static final String                   TAG_OUTPUT_LOCALITY                              = "output_locality";
	public static final String                   TAG_OUTPUT_SUB_ADMINISTRATIVE_AREA               = "output_sub_administrative_area";
	public static final String                   TAG_OUTPUT_SUB_NATIONAL_AREA                     = "output_sub_national_area";
	//Extra Output Address Parameters
	public static final String                   TAG_OUTPUT_ADDRESS_TYPE                          = "output_address_type";
	public static final String                   TAG_OUTPUT_LATITUDE                              = "output_latitude";
	public static final String                   TAG_OUTPUT_LONGITUDE                             = "output_longitude";
	//Extra Output Country Parameters
	public static final String                   TAG_OUTPUT_COUNTRY_SUBDIVISION_CODE              = "output_country_subdivision_code";
	public static final String                   TAG_OUTPUT_COUNTRY_CODE                          = "output_country_code";
	public static final String                   TAG_OUTPUT_COUNTRY_TIMEZONE                      = "output_country_timezome";
	public static final String                   TAG_OUTPUT_COUNTRY_UTC                           = "output_country_utc";
	public static final String                   TAG_OUTPUT_COUNTRY_NAME                          = "output_country_name";
	public static final String                   TAG_OUTPUT_COUNTRY_ISO_ALPHA2                    = "output_country_iso3166_1_alpha2";
	public static final String                   TAG_LOCAL_COUNTRY_ISO_ALPHA2                     = "iso2Code";
	public static final String                   TAG_OUTPUT_COUNTRY_ISO_ALPHA3                    = "output_country_iso3166_1_alpha3";
	public static final String                   TAG_LOCAL_COUNTRY_ISO_ALPHA3                     = "iso3Code";
	public static final String                   TAG_OUTPUT_COUNTRY_ISO_NUMERIC                   = "output_country_iso3166_1_numeric";
	public static final String                   TAG_LOCAL_COUNTRY_ISO_NUMERIC                    = "isoCountryNumber";
	public static final String                   TAG_OUTPUT_COUNTRY_FORMAL_NAME                   = "output_country_formal_name";
	//Web only
	public static final String                   TAG_OUTPUT_ADDRESS_KEY                           = "output_address_key";
	public static final String                   TAG_PROCESS_LOCAL                                = "LOCAL";
	public static final String                   TAG_PROCESS_WEB                                  = "WEB";
	public static final String                   TAG_PROCESS_VARIED                               = "VARIED";
	//
	private             SortedSet<Integer>       opSelectedCountriesIndex                         = null;
	private             String                   processType                                      = "WEB";
	private             int                      countryFieldIndex                                = -1;
	public              HashMap<String, MetaVal> webOptionFields                                  = null;
	public              HashMap<String, MetaVal> inputFields                                      = null;
	public              HashMap<String, MetaVal> webOutputFields                                  = null;
	public              String                   webVersion                                       = "";
	public              String                   defaultCountry                                   = null;
	public              int                      fieldsAdded                                      = 0;
	public              String[]                 inputOpts                                        = new String[] { "US", "Simplified Global", "Full Global" };
	public              boolean                  addAdditionalCode                                = true;

	public boolean hasSelectedCountries() {

		return opSelectedCountriesIndex.size() > 0;
	}

	// Takes a comma seperated list of countries
	public void setSelectedCountriesFromString(String countries) {

		if (opSelectedCountriesIndex == null) {
			opSelectedCountriesIndex = new TreeSet<Integer>();
		}
		if (countries == null || countries.isEmpty()) {
			return;
		}
		String[] aCountries = countries.split(",");
		opSelectedCountriesIndex.clear();
		for (String cou : aCountries) {
			if (StringUtils.isNumeric(cou)) {
				opSelectedCountriesIndex.add(Integer.parseInt(cou));
			}
		}
	}

	public String getSelectedCountriesAsString() {

		String sCountries = "";

		for (int cou : opSelectedCountriesIndex) {
			sCountries += Integer.toString(cou) + ",";
		}
		// drop last comma
		if (!sCountries.isEmpty()) {
			sCountries = sCountries.substring(0, sCountries.length() - 1);
		}

		return sCountries;
	}

	public SortedSet<Integer> getOpSelectedCountriesIndex() {

		return opSelectedCountriesIndex;
	}

	public String getProcessType() {

		return processType;
	}

	public void setProcessType(String processType) {

		this.processType = processType;
	}

	public int getCountryFieldIndex() {

		return countryFieldIndex;
	}

	public void setCountryFieldIndex(int index) {

		this.countryFieldIndex = index;
	}

	public boolean hasMinRequirements() {

		if (Const.isEmpty(inputFields.get(TAG_INPUT_ADDRESS_LINE1).metaValue) || (Const.isEmpty(inputFields.get(TAG_INPUT_COUNTRY).metaValue) && Const.isEmpty(defaultCountry))) {
			return false;
		} else {
			return true;
		}
	}

	public String getRealLineSeporator() {

		String sep           = ",";
		String lineSeparator = webOptionFields.get(TAG_OPTION_LINE_SEPARATOR).metaValue;
		if ("SEMICOLON".equals(lineSeparator)) {
			sep = ";";
		} else if ("PIPE".equals(lineSeparator)) {
			sep = "|";
		} else if ("CR".equals(lineSeparator)) {
			sep = "\r";
		} else if ("LF".equals(lineSeparator)) {
			sep = "\n";
		} else if ("CRLF".equals(lineSeparator)) {
			sep = "\r\n";
		} else if ("TAB".equals(lineSeparator)) {
			sep = "\t";
		} else if ("BR".equals(lineSeparator)) {
			sep = "<BR>";
		}

		return sep;
	}

	/**
	 * initializes all the default value
	 */
	public void init() {

		if (webOptionFields == null) {
			webOptionFields = new HashMap<String, MetaVal>();
		}
		if (inputFields == null) {
			inputFields = new HashMap<String, MetaVal>();
		}
		if (webOutputFields == null) {
			webOutputFields = new HashMap<String, MetaVal>();
		}
		setSelectedCountriesFromString("");
		// repository xml key tag, metavalue default, web tag, and size are set here
		// Web Options
		webOptionFields.put(TAG_OPTION_LINE_SEPARATOR, new MetaVal("SEMICOLON", "LineSeparator:", 0));
		webOptionFields.put(TAG_OPTION_OUTPUT_SCRIPT, new MetaVal("NOCHANGE", "OutputScript:", 0));
		webOptionFields.put(TAG_OPTION_COUNTRY_OF_ORIGIN, new MetaVal("", "CountryOfOrigin:", 0));
		webOptionFields.put(TAG_OPTION_DELIVERY_LINE, new MetaVal("OFF", "DeliveryLines:", 0));
		webOptionFields.put(TAG_OPTION_INPUT_DISPLAY, new MetaVal(inputOpts[0], "DISPLAYOPTS", 0));
		// Input fields
		inputFields.put(TAG_INPUT_ORGANIZATION, new MetaVal("", "Organization", 50)); // Company
		inputFields.put(TAG_INPUT_ADDRESS_LINE1, new MetaVal("", "AddressLine1", 50)); // Address Lines
		inputFields.put(TAG_INPUT_ADDRESS_LINE2, new MetaVal("", "AddressLine2", 50));
		inputFields.put(TAG_INPUT_ADDRESS_LINE3, new MetaVal("", "AddressLine3", 50));
		inputFields.put(TAG_INPUT_ADDRESS_LINE4, new MetaVal("", "AddressLine4", 50));
		inputFields.put(TAG_INPUT_ADDRESS_LINE5, new MetaVal("", "AddressLine5", 50));
		inputFields.put(TAG_INPUT_ADDRESS_LINE6, new MetaVal("", "AddressLine6", 50));
		inputFields.put(TAG_INPUT_ADDRESS_LINE7, new MetaVal("", "AddressLine7", 50));
		inputFields.put(TAG_INPUT_ADDRESS_LINE8, new MetaVal("", "AddressLine8", 50));
		inputFields.put(TAG_INPUT_SUB_PREMISES, new MetaVal("", "SubPremises", 10)); // suite
		inputFields.put(TAG_INPUT_DOUBLE_DEPENDENT_LOCALITY, new MetaVal("", "DoubleDependentLocality", 50));
		inputFields.put(TAG_INPUT_DEPENDENT_LOCALITY, new MetaVal("", "DependentLocality", 50)); // Urbanization?
		inputFields.put(TAG_INPUT_LOCALITY, new MetaVal("", "Locality", 35)); // city *required field*
		inputFields.put(TAG_INPUT_SUB_ADMINISTRATIVE_AREA, new MetaVal("", "SubAdministrativeArea", 25)); // county
		inputFields.put(TAG_INPUT_ADMINISTRATIVE_AREA, new MetaVal("", "AdministrativeArea", 15)); // state *required field*
		inputFields.put(TAG_INPUT_POSTAL_CODE, new MetaVal("", "PostalCode", 10)); // zip *required field*
		inputFields.put(TAG_INPUT_SUB_NATIONAL_AREA, new MetaVal("", "SubNationalArea", 50));
		inputFields.put(TAG_INPUT_COUNTRY, new MetaVal("US", "Country", 50)); // country *required field*
		defaultCountry = "USA";
		//Output Address Parameters
		webOutputFields.put(TAG_OUTPUT_ORGANIZATION, new MetaVal("MD_Organization", "Organization", 50));
		webOutputFields.put(TAG_OUTPUT_ADDRESS_LINE1, new MetaVal("MD_AddressLine1", "AddressLine1", 50));
		webOutputFields.put(TAG_OUTPUT_ADDRESS_LINE2, new MetaVal("MD_AddressLine2", "AddressLine2", 50));
		webOutputFields.put(TAG_OUTPUT_ADDRESS_LINE3, new MetaVal("MD_AddressLine3", "AddressLine3", 50));
		webOutputFields.put(TAG_OUTPUT_ADDRESS_LINE4, new MetaVal("MD_AddressLine4", "AddressLine4", 50));
		webOutputFields.put(TAG_OUTPUT_ADDRESS_LINE5, new MetaVal("MD_AddressLine5", "AddressLine5", 50));
		webOutputFields.put(TAG_OUTPUT_ADDRESS_LINE6, new MetaVal("MD_AddressLine6", "AddressLine6", 50));
		webOutputFields.put(TAG_OUTPUT_ADDRESS_LINE7, new MetaVal("MD_AddressLine7", "AddressLine7", 50));
		webOutputFields.put(TAG_OUTPUT_ADDRESS_LINE8, new MetaVal("MD_AddressLine8", "AddressLine8", 50));
		webOutputFields.put(TAG_OUTPUT_DELIVERY_LINE, new MetaVal("MD_DeliveryLine", "deliveryLine", 50));                                      //Local
		webOutputFields.put(TAG_OUTPUT_FORMATED_ADDRESS, new MetaVal("MD_FormattedAddress", "FormattedAddress", 50));

		//Parsed Sub-Premises Parameters
		webOutputFields.put(TAG_OUTPUT_BUILDING, new MetaVal("MD_Building", "Building", 10));
		webOutputFields.put(TAG_OUTPUT_SUB_BUILDING, new MetaVal("MD_SubBuilding", "subBuilding", 10));                                         //Local
		webOutputFields.put(TAG_OUTPUT_SUB_BUILDING_NUMBER, new MetaVal("MD_SubBuildingNumber", "SubBuildingNumber", 10));                      //Local
		webOutputFields.put(TAG_OUTPUT_SUB_BUILDING_TYPE, new MetaVal("MD_SubBuildingType", "SubBuildingType", 10));                            //Local
		webOutputFields.put(TAG_OUTPUT_SUB_PREMISES, new MetaVal("MD_SubPremises", "SubPremises", 20));                                         //Local
		webOutputFields.put(TAG_OUTPUT_SUB_PREMISES_NUMBER, new MetaVal("MD_SubPremisesNumber", "SubPremisesNumber", 10));
		webOutputFields.put(TAG_OUTPUT_SUB_PREMISES_TYPE, new MetaVal("MD_SubPremisesType", "SubPremisesType", 10));
		webOutputFields.put(TAG_OUTPUT_SUB_PREMISES_LEVEL, new MetaVal("MD_SubPremisesLevel", "SubPremisesLevel", 10));                         //Local
		webOutputFields.put(TAG_OUTPUT_SUB_PREMISES_LEVEL_NUMBER, new MetaVal("MD_SubPremisesLevelNumber", "SubPremisesLevelNumber", 10));      //Local
		webOutputFields.put(TAG_OUTPUT_SUB_PREMISES_LEVEL_TYPE, new MetaVal("MD_SubPremisesLevelType", "SubPremisesLevelType", 10));            //Local

		//Parsed Thoroughfare Parameters
		webOutputFields.put(TAG_OUTPUT_PREMISES, new MetaVal("MD_Premises", "Premises", 10));                                                   //Local
		webOutputFields.put(TAG_OUTPUT_PREMISES_NUMBER, new MetaVal("MD_PremisesNumber", "PremisesNumber", 10));
		webOutputFields.put(TAG_OUTPUT_PREMISES_TYPE, new MetaVal("MD_PremisesType", "PremisesType", 10));
		webOutputFields.put(TAG_OUTPUT_THOROUGHFARE, new MetaVal("MD_Thoroughfare", "Thoroughfare", 40));
		webOutputFields.put(TAG_OUTPUT_LEADING_TYPE, new MetaVal("MD_LeadingType", "ThoroughfareLeadingType", 5));
		webOutputFields.put(TAG_OUTPUT_THOROUGHFARE_NAME, new MetaVal("MD_ThoroughfareName", "ThoroughfareName", 40));
		webOutputFields.put(TAG_OUTPUT_POST_DIRECTION, new MetaVal("MD_PostDirection", "ThoroughfarePostDirection", 5));
		webOutputFields.put(TAG_OUTPUT_PRE_DIRECTION, new MetaVal("MD_PreDirection", "ThoroughfarePreDirection", 5));
		webOutputFields.put(TAG_OUTPUT_TRAILING_TYPE, new MetaVal("MD_TrailingType", "ThoroughfareTrailingType", 5));
		webOutputFields.put(TAG_OUTPUT_TYPE_ATTACHED, new MetaVal("MD_TypeAttached", "ThoroughfareTypeAttached", 5));                           //Local

		//Parsed Dependent Thoroughfare Columns
		webOutputFields.put(TAG_OUTPUT_DEPENDENT_THOROUGHFARE, new MetaVal("MD_DepThoroughfare", "DependentThoroughfare", 50));
		webOutputFields.put(TAG_OUTPUT_DEPENDENT_THOROUGHFARE_LEADING_TYPE, new MetaVal("MD_DepThoroughfareLeadingType", "DependentThoroughfareLeadingType", 5));
		webOutputFields.put(TAG_OUTPUT_DEPENDENT_THOROUGHFARE_NAME, new MetaVal("MD_DepThoroughfareName", "DependentThoroughfareName", 40));
		webOutputFields.put(TAG_OUTPUT_DEPENDENT_THOROUGHFARE_POST_DIRECTION, new MetaVal("MD_DepThoroughfarePostDirection", "DependentThoroughfarePostDirection", 5));
		webOutputFields.put(TAG_OUTPUT_DEPENDENT_THOROUGHFARE_PRE_DIRECTION, new MetaVal("MD_DepThoroughfarePreDirection", "DependentThoroughfarePreDirection", 5));
		webOutputFields.put(TAG_OUTPUT_DEPENDENT_THOROUGHFARE_TRAILING_TYPE, new MetaVal("MD_DepThoroughfareTrailingType", "DependentThoroughfareTrailingType", 5));
		webOutputFields.put(TAG_OUTPUT_DEPENDENT_THOROUGHFARE_TYPE_ATTACHED, new MetaVal("MD_DepThoroughfareTypeAttached", "DependentThoroughfareTypeAttached", 5));  //Local

		//Parsed Postal Facility Columns
		webOutputFields.put(TAG_OUTPUT_POSTBOX, new MetaVal("MD_PostBox", "PostBox", 10));
		webOutputFields.put(TAG_OUTPUT_POSTAL_CODE, new MetaVal("MD_PostalCode", "PostalCode", 10)); // Zip
		webOutputFields.put(TAG_OUTPUT_PERSONAL_ID, new MetaVal("MD_PersonalID", "personalID", 15));                                                //Local
		webOutputFields.put(TAG_OUTPUT_POST_OFFICE_LOCATION, new MetaVal("MD_PostOfficeLocation", "PostOfficeLocation", 50));                       //Local

		//Parsed Regional Columns
		webOutputFields.put(TAG_OUTPUT_ADMINISTRATIVE_AREA, new MetaVal("MD_AdminArea", "AdministrativeArea", 15)); // state
		webOutputFields.put(TAG_OUTPUT_COUNTY_NAME, new MetaVal("MD_CountyName", "CountyName", 50));                                                //Local
		webOutputFields.put(TAG_OUTPUT_DEPENDENT_LOCALITY, new MetaVal("MD_DepLocality", "DependentLocality", 50));
		webOutputFields.put(TAG_OUTPUT_DOUBLE_DEPENDENT_LOCALITY, new MetaVal("MD_DoubleDepLocality", "DoubleDependentLocality", 50));
		webOutputFields.put(TAG_OUTPUT_LOCALITY, new MetaVal("MD_Locality", "Locality", 35)); // city
		webOutputFields.put(TAG_OUTPUT_SUB_ADMINISTRATIVE_AREA, new MetaVal("MD_SubAdminArea", "SubAdministrativeArea", 25)); // county
		webOutputFields.put(TAG_OUTPUT_SUB_NATIONAL_AREA, new MetaVal("MD_SubNationalArea", "SubNationalArea", 50));

		//Extra Output Address Parameters
		webOutputFields.put(TAG_OUTPUT_LATITUDE, new MetaVal("MD_Latitude", "Latitude", 50));
		webOutputFields.put(TAG_OUTPUT_LONGITUDE, new MetaVal("MD_Longitude", "Longitude", 50));
		webOutputFields.put(TAG_OUTPUT_ADDRESS_TYPE, new MetaVal("MD_Addr_TypeCodes", "AddressType", 50));

		//Extra Output CountryName Parameters
		webOutputFields.put(TAG_OUTPUT_COUNTRY_CODE, new MetaVal("MD_CountryCode", "countryCode", 50));   //Local  CountrySubdivisionCode
		webOutputFields.put(TAG_OUTPUT_COUNTRY_SUBDIVISION_CODE, new MetaVal("MD_CountrySubdivisionCode", "CountrySubdivisionCode", 10));
		webOutputFields.put(TAG_OUTPUT_COUNTRY_TIMEZONE, new MetaVal("MD_CountryTimeZone", "timezome", 25));
		webOutputFields.put(TAG_OUTPUT_COUNTRY_UTC, new MetaVal("MD_CountryUTC", "utc", 25));

		webOutputFields.put(TAG_OUTPUT_COUNTRY_NAME, new MetaVal("MD_CountryName", "CountryName", 50));
		webOutputFields.put(TAG_OUTPUT_COUNTRY_ISO_ALPHA2, new MetaVal("MD_CountryAlphaISO2", "CountryISO3166_1_Alpha2", 50));
		webOutputFields.put(TAG_OUTPUT_COUNTRY_ISO_ALPHA3, new MetaVal("MD_CountryAlphaISO3", "CountryISO3166_1_Alpha3", 50));
		webOutputFields.put(TAG_OUTPUT_COUNTRY_ISO_NUMERIC, new MetaVal("MD_CountryNumericISO", "CountryISO3166_1_Numeric", 50));
		webOutputFields.put(TAG_OUTPUT_COUNTRY_FORMAL_NAME, new MetaVal("MD_CountryFormalName", "formalCountryName", 50));                          //Local

		// web only
		webOutputFields.put(TAG_OUTPUT_ADDRESS_KEY, new MetaVal("MD_AddressKey", "AddressKey", 11));
	}
}
