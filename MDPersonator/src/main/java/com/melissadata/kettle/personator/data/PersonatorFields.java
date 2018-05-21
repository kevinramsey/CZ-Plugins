package com.melissadata.kettle.personator.data;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.melissadata.kettle.personator.MDPersonatorMeta;
import com.melissadata.kettle.personator.MDPersonatorMeta.OutputPhoneFormat;
import com.melissadata.kettle.personator.MDPersonatorMeta.RowOutput;
import com.melissadata.kettle.personator.support.MetaVal;

public class PersonatorFields {

	public static final String TAG_PERSONATOR_OPTIONS                = "personator_options";
	public static final String TAG_PERSONATOR_INPUT                  = "personator_input";
	public static final String TAG_PERSONATOR_OUTPUT                 = "personator_output";
	//
	public static final String ACTION_VERIFY                         = "Verify";
	public static final String ACTION_APPEND                         = "Append";
	public static final String ACTION_MOVE                           = "Move";
	public static final String ACTION_CHECK                          = "Check";
	//
	public static final String APPEND_ALWAYS                         = "Always";
	public static final String APPEND_BLANK                          = "Blank";
	public static final String APPEND_CHECK_ERROR                    = "CheckError";
	//
	public static final String MOOVE_STRICT                          = "Strict";
	//
	public static final String DEMOGRAPHICS_MASK_OPTION_NONE         = "None";
	public static final String DEMOGRAPHICS_MASK_OPTION_YES          = "Yes";
	public static final String DEMOGRAPHICS_MASK_OPTION_MASK         = "Mask";
	public static final String DEMOGRAPHICS_MASK_OPTION_MASKONLY     = "MaskOnly";
	public static final String DEMOGRAPHICS_MASK_OPTION_VALUEONLY    = "ValueOnly";
	//
	public static       String TAG_OPTION_ACTION                     = "Actions";
	public static       String TAG_OPTION                            = "Options";
	public static       String TAG_OPTION_COLUMNS                    = "Columns";
	public static       String TAG_OPTION_APPEND                     = "Append:";
	public static       String TAG_OPTION_MOVE_CONFIDENCE            = "MoveConfidence:";
	public static       String TAG_OPTION_CENTRIC_HINT               = "CentricHint:";
	public static       String TAG_OPTION_NAME_HINT                  = "NameHint:";
	public static       String TAG_OPTION_NAME_GENDER_POP            = "GenderPopulation:";
	public static       String TAG_OPTION_NAME_GENDER_AGG            = "GenderAggression:";
	public static       String TAG_OPTION_NAME_MIDDLE_LOGIC          = "MiddleNameLogic:";
	public static       String TAG_OPTION_NAME_SALUTATION            = "SalutationFormat:";
	public static       String TAG_OPTION_NAME_CORRECT_FIRST         = "CorrectFirstName:";
	public static       String TAG_OPTION_NAME_STANDARD_COMPANY      = "StandardizeCompany:";
	public static       String TAG_OPTION_ADDR_PREFERRED_CITY        = "UsePreferredCity:";
	public static       String TAG_OPTION_ADDR_DIACRITICS            = "Diacritics:";
	public static       String TAG_OPTION_ADDR_ADVANCED_ADDR_CORRECT = "AdvancedAddressCorrection:";
	public static       String TAG_OPTION_FREE_FORM                  = "FreeForm";
	public static       String TAG_OPTION_PARSED_ADDR                = "ParsedAddr";
	public static       String TAG_OPTION_PHONE_FORMAT               = "opt_phone_format";
	public static       String TAG_OPTION_EMAIL_SYNTAX               = "CorrectSyntax:";
	public static       String TAG_OPTION_EMAIL_UPDATE_DOMAIN        = "UpdateDomain:";
	public static       String TAG_OPTION_EMAIL_DB_LOOKUP            = "DatabaseLookup:";
	public static       String TAG_OPTION_EMAIL_STANDARD_CASING      = "StandardizeCasing:";
	//
	public static       String TAG_OPTION_DEMOGRAPHICS               = "Demographics:";
	public static       String TAG_INPUT_COMPANY                     = "input_company";
	public static       String TAG_INPUT_FULL_NAME                   = "input_full_name";
	public static       String TAG_INPUT_FIRST_NAME                  = "input_first_name";
	public static       String TAG_INPUT_LAST_NAME                   = "input_last_name";
	public static       String TAG_INPUT_ADDRESS_LINE1               = "input_address_line1";
	public static       String TAG_INPUT_ADDRESS_LINE2               = "input_address_line2";
	public static       String TAG_INPUT_CITY                        = "input_city";
	public static       String TAG_INPUT_STATE                       = "input_state";
	public static       String TAG_INPUT_ZIP                         = "input_zip";
	public static       String TAG_INPUT_COUNTRY                     = "input_country";
	public static       String TAG_INPUT_PHONE                       = "input_phone";
	public static       String TAG_INPUT_EMAIL                       = "input_email";
	// Ip
	public static       String TAG_INPUT_IP_ADDRESS                  = "input_ip_address";
	//
	public static       String TAG_INPUT_DOB_BIRTHDAY             = "input_dob_birthDay";
	public static       String TAG_INPUT_DOB_BIRTHMONTH           = "input_dob_birthMonth";
	public static       String TAG_INPUT_DOB_BIRTHYEAR            = "input_dob_birthYear";

	// Free form
	public static       String TAG_INPUT_FREE_FORM                   = "input_free_form";
	public static       String TAG_OUTPUT_RESULTS                    = "output_results";
	public ConcurrentHashMap<String, MetaVal> realOptionFields;
	public HashMap<String, MetaVal>           optionFields;
	public HashMap<String, MetaVal>           inputFields;
	public HashMap<String, MetaVal>           outputFields;
	public String webVersion = "";
	public int fieldsAdded;

	public boolean DemographicsLicensed = false;

	/**
	 * checks to see if the minimum requirements to run are met
	 *
	 * @return
	 */
	public boolean hasMinRequirements() {
		//TODO check min requirements
		return true;
	}

	public ConcurrentHashMap<String, MetaVal> getRealWebOptions() {

		realOptionFields = new ConcurrentHashMap<String, MetaVal>();

		String value   = "";
		String actions = ACTION_CHECK;

		// ACTIONS
		value = optionFields.get(TAG_OPTION_ACTION).metaValue;
		if (value.contains(ACTION_VERIFY)) {
			actions += ";" + ACTION_VERIFY;
		}
		if (value.contains(ACTION_APPEND)) {
			actions += ";" + ACTION_APPEND;
		}
		if (value.contains(ACTION_MOVE)) {
			actions += ";" + ACTION_MOVE;
		}
		realOptionFields.put(TAG_OPTION_ACTION, new MetaVal(actions, TAG_OPTION_ACTION, 0));

		// now create a delimited String for the requested columns
		// we will get them by group and filter out any unwanted columns during processing.
		value = "";
		HashMap<Integer, RowOutput> grpList = MDPersonatorMeta.getOutputgroups();
		RowOutput                   ro;
		for (int n = 0; n < grpList.size(); n++) {
			ro = grpList.get(n);
			if (ro.isAdded && !"Basic".equals(ro.groupName)) {

				if ("---".equals(ro.groupName)) {
					value += ro.outputName + ",";
				} else if (!value.contains(ro.groupName)) {
					value += "Grp" + ro.groupName + ",";
				}
			}
		}
		realOptionFields.put(TAG_OPTION_COLUMNS, new MetaVal(value, TAG_OPTION_COLUMNS, 0));


		value = "";
		// now create a delimited String for the options
		String sOnOff = "";
		String name   = "";
		for (String key : optionFields.keySet()) {
			if(key.equals(TAG_OPTION_DEMOGRAPHICS) && (optionFields.get(key).metaValue.equals(DEMOGRAPHICS_MASK_OPTION_NONE))){
				continue;
			}
			if (key.contains(":")) {
				name = optionFields.get(key).webTag;
				sOnOff = optionFields.get(key).metaValue;
				if ("true".equalsIgnoreCase(sOnOff)) {
					sOnOff = "On";
				} else if ("false".equalsIgnoreCase(sOnOff)) {
					sOnOff = "Off";
				}
				// only some are on off options if its not true/false
				// leave it as it is.
				value += name + sOnOff + ";";
			}
		}

		realOptionFields.put(TAG_OPTION, new MetaVal(value, TAG_OPTION, 0));

		return realOptionFields;
	}

	/**
	 * initializes all the default value
	 */
	public void init(MDPersonatorMeta pMeta) {

		if (optionFields == null) {
			optionFields = new HashMap<String, MetaVal>();
		}
		if (inputFields == null) {
			inputFields = new HashMap<String, MetaVal>();
		}
		if (outputFields == null) {
			outputFields = new HashMap<String, MetaVal>();
		}

		// repository xml key tag, metavalue default, web tag, and size are set
		// here
		optionFields.put(TAG_OPTION_ACTION, new MetaVal("Verify", "Actions", 0));
		optionFields.put(TAG_OPTION_CENTRIC_HINT, new MetaVal("Auto", TAG_OPTION_CENTRIC_HINT, 0));
		optionFields.put(TAG_OPTION_APPEND, new MetaVal("", TAG_OPTION_APPEND, 0));
		optionFields.put(TAG_OPTION_MOVE_CONFIDENCE, new MetaVal(MOOVE_STRICT, TAG_OPTION_MOVE_CONFIDENCE, 0));

		optionFields.put(TAG_OPTION_NAME_HINT, new MetaVal("Varying", TAG_OPTION_NAME_HINT, 0));
		optionFields.put(TAG_OPTION_NAME_GENDER_POP, new MetaVal("Mixed", TAG_OPTION_NAME_GENDER_POP, 0));
		optionFields.put(TAG_OPTION_NAME_GENDER_AGG, new MetaVal("Neutral", TAG_OPTION_NAME_GENDER_AGG, 0));
		optionFields.put(TAG_OPTION_NAME_MIDDLE_LOGIC, new MetaVal("Parse Logic", TAG_OPTION_NAME_MIDDLE_LOGIC, 0));
		optionFields.put(TAG_OPTION_NAME_SALUTATION, new MetaVal("Formal", TAG_OPTION_NAME_SALUTATION, 0));
		optionFields.put(TAG_OPTION_NAME_CORRECT_FIRST, new MetaVal("true", TAG_OPTION_NAME_CORRECT_FIRST, 0));
		optionFields.put(TAG_OPTION_NAME_STANDARD_COMPANY, new MetaVal("true", TAG_OPTION_NAME_STANDARD_COMPANY, 0));
		optionFields.put(TAG_OPTION_DEMOGRAPHICS, new MetaVal(DEMOGRAPHICS_MASK_OPTION_NONE, TAG_OPTION_DEMOGRAPHICS, 0));

		optionFields.put(TAG_OPTION_ADDR_PREFERRED_CITY, new MetaVal("false", TAG_OPTION_ADDR_PREFERRED_CITY, 0));
		optionFields.put(TAG_OPTION_ADDR_DIACRITICS, new MetaVal("Auto", TAG_OPTION_ADDR_DIACRITICS, 0));
		optionFields.put(TAG_OPTION_ADDR_ADVANCED_ADDR_CORRECT, new MetaVal("true", TAG_OPTION_ADDR_ADVANCED_ADDR_CORRECT, 0));

		optionFields.put(TAG_OPTION_FREE_FORM, new MetaVal("false", TAG_OPTION_FREE_FORM, 0));
		optionFields.put(TAG_OPTION_PARSED_ADDR, new MetaVal("true", TAG_OPTION_PARSED_ADDR, 0));

		pMeta.setPhoneFormat(OutputPhoneFormat.FORMAT1);
		optionFields.put(TAG_OPTION_PHONE_FORMAT, new MetaVal(MDPersonatorMeta.getPhoneFormat().encode(), TAG_OPTION_PHONE_FORMAT, 0));

		optionFields.put(TAG_OPTION_EMAIL_SYNTAX, new MetaVal("true", TAG_OPTION_EMAIL_SYNTAX, 0));
		optionFields.put(TAG_OPTION_EMAIL_UPDATE_DOMAIN, new MetaVal("true", TAG_OPTION_EMAIL_UPDATE_DOMAIN, 0));
		optionFields.put(TAG_OPTION_EMAIL_DB_LOOKUP, new MetaVal("false", TAG_OPTION_EMAIL_DB_LOOKUP, 0));
		optionFields.put(TAG_OPTION_EMAIL_STANDARD_CASING, new MetaVal("true", TAG_OPTION_EMAIL_STANDARD_CASING, 0));

		inputFields.put(TAG_INPUT_COMPANY, new MetaVal("", "CompanyName", 50));
		inputFields.put(TAG_INPUT_FULL_NAME, new MetaVal("", "FullName", 50));
		inputFields.put(TAG_INPUT_FIRST_NAME, new MetaVal("", "FirstName", 50));
		inputFields.put(TAG_INPUT_LAST_NAME, new MetaVal("", "LastName", 50));

		inputFields.put(TAG_INPUT_ADDRESS_LINE1, new MetaVal("", "AddressLine1", 50));
		inputFields.put(TAG_INPUT_ADDRESS_LINE2, new MetaVal("", "AddressLine2", 50));
		inputFields.put(TAG_INPUT_CITY, new MetaVal("", "City", 50));
		inputFields.put(TAG_INPUT_STATE, new MetaVal("", "State", 50));
		inputFields.put(TAG_INPUT_ZIP, new MetaVal("", "PostalCode", 50));
		inputFields.put(TAG_INPUT_COUNTRY, new MetaVal("", "Country", 50));
		inputFields.put(TAG_INPUT_FREE_FORM, new MetaVal("", "FreeForm", 50));

		inputFields.put(TAG_INPUT_PHONE, new MetaVal("", "PhoneNumber", 50));
		inputFields.put(TAG_INPUT_EMAIL, new MetaVal("", "EmailAddress", 50));
		//IP
		inputFields.put(TAG_INPUT_IP_ADDRESS, new MetaVal("", "IPAddress", 50));
		//DOB
		inputFields.put(TAG_INPUT_DOB_BIRTHDAY, new MetaVal("", "BirthDay", 50));
		inputFields.put(TAG_INPUT_DOB_BIRTHMONTH, new MetaVal("", "BirthMonth", 50));
		inputFields.put(TAG_INPUT_DOB_BIRTHYEAR, new MetaVal("", "BirthYear", 50));

		HashMap<Integer, RowOutput> grpList = MDPersonatorMeta.getOutputgroups();
		RowOutput                   ro;
		for (int n = 0; n < grpList.size(); n++) {
			ro = grpList.get(n);
			outputFields.put(ro.tag, new MetaVal(ro.fieldName, ro.outputName, 50));
		}

		outputFields.put(TAG_OUTPUT_RESULTS, new MetaVal("MD_Results", "Results", 75));
	}
}
