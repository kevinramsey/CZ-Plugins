package com.melissadata.kettle.globalverify.data;

import java.util.HashMap;
import com.melissadata.kettle.globalverify.support.MetaVal;
import org.pentaho.di.core.Const;

public class NameFields {
	public static final String		TAG_GLOBAL_NAME_OPTIONS			= "globalname_options";
	public static final String		TAG_GLOBAL_NAME_INPUT			= "globalname_input";
	public static final String		TAG_GLOBAL_NAME_OUTPUT			= "globalname_output";
	// Options
	public static String			TAG_OPTION_MIDDLE_NAME_LOGIC	= "globalname_middle_name_logic";
	public static String			TAG_OPTION_NAME_ORDER			= "globalname_name_order";
	public static String			TAG_OPTION_CORRECT_MISSPELLING	= "globalname_correct_misspelling";
	// Inputs
	public static String			TAG_INPUT_FULLNAME				= "globalname_input_fullname";
	public static String			TAG_INPUT_COMPANY_NAME			= "globalname_input_company_name";
	// Outputs
	public static String			TAG_OUTPUT_RESULTS				= "globalname_output_results";
	public static String			TAG_OUTPUT_COMPANY_NAME			= "globalname_output_company_name";
	public static String			TAG_OUTPUT_NAME1_PREFIX			= "globalname_output_name1_prefix";
	public static String			TAG_OUTPUT_NAME1_FIRST			= "globalname_output_name1_first";
	public static String			TAG_OUTPUT_NAME1_MIDDLE			= "globalname_output_name1_middle";
	public static String			TAG_OUTPUT_NAME1_LAST			= "globalname_output_name1_last";
	public static String			TAG_OUTPUT_NAME1_SUFFIX			= "globalname_output_name1_suffix";
	public static String			TAG_OUTPUT_NAME1_GENDER			= "globalname_output_name1_gender";
	public static String			TAG_OUTPUT_NAME2_PREFIX			= "globalname_output_name2_prefix";
	public static String			TAG_OUTPUT_NAME2_FIRST			= "globalname_output_name2_first";
	public static String			TAG_OUTPUT_NAME2_MIDDLE			= "globalname_output_name2_middle";
	public static String			TAG_OUTPUT_NAME2_LAST			= "globalname_output_name2_last";
	public static String			TAG_OUTPUT_NAME2_SUFFIX			= "globalname_output_name2_suffix";
	public static String			TAG_OUTPUT_NAME2_GENDER			= "globalname_output_name2_gender";
	public HashMap<String, MetaVal>	optionFields;
	public HashMap<String, MetaVal>	inputFields;
	public HashMap<String, MetaVal>	outputFields;
	public String					webVersion						= "";
	public int						fieldsAdded;

	public boolean hasMinRequirements() {
		return !Const.isEmpty(inputFields.get(TAG_INPUT_FULLNAME).metaValue) || !Const.isEmpty(inputFields.get(TAG_INPUT_COMPANY_NAME).metaValue);
	}

	public boolean doFullName(){
		return !Const.isEmpty(inputFields.get(TAG_INPUT_FULLNAME).metaValue);
	}

	public boolean doCompany(){
		return !Const.isEmpty(inputFields.get(TAG_INPUT_COMPANY_NAME).metaValue);
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
		optionFields.put(TAG_OPTION_NAME_ORDER, new MetaVal("Varying", "NAMEHINT:", 0));
		optionFields.put(TAG_OPTION_MIDDLE_NAME_LOGIC, new MetaVal("ParseLogic", "MIDDLENAMELOGIC:", 0));
		optionFields.put(TAG_OPTION_CORRECT_MISSPELLING, new MetaVal("OFF", "CORRECTFIRSTNAME:", 0));
		inputFields.put(TAG_INPUT_FULLNAME, new MetaVal("", "FullName", 50));
		inputFields.put(TAG_INPUT_COMPANY_NAME, new MetaVal("", "Company", 50));
		outputFields.put(TAG_OUTPUT_COMPANY_NAME, new MetaVal("MD_Company", "Company", 50));
		outputFields.put(TAG_OUTPUT_NAME1_PREFIX, new MetaVal("MD_Prefix1", "Prefix", 50));
		outputFields.put(TAG_OUTPUT_NAME1_FIRST, new MetaVal("MD_FirstName1", "NameFirst", 50));
		outputFields.put(TAG_OUTPUT_NAME1_MIDDLE, new MetaVal("MD_MiddleName1", "NameMiddle", 50));
		outputFields.put(TAG_OUTPUT_NAME1_LAST, new MetaVal("MD_LastName1", "NameLast", 50));
		outputFields.put(TAG_OUTPUT_NAME1_SUFFIX, new MetaVal("MD_Suffix1", "NameSuffix", 50));
		outputFields.put(TAG_OUTPUT_NAME1_GENDER, new MetaVal("MD_Gender1", "Gender", 5));
		outputFields.put(TAG_OUTPUT_NAME2_PREFIX, new MetaVal("MD_Prefix2", "Prefix2", 50));
		outputFields.put(TAG_OUTPUT_NAME2_FIRST, new MetaVal("MD_FirstName2", "NameFirst2", 50));
		outputFields.put(TAG_OUTPUT_NAME2_MIDDLE, new MetaVal("MD_LastMiddle2", "NameMiddle2", 50));
		outputFields.put(TAG_OUTPUT_NAME2_LAST, new MetaVal("MD_LastName2", "NameLast2", 50));
		outputFields.put(TAG_OUTPUT_NAME2_SUFFIX, new MetaVal("MD_Suffix2", "NameSuffix2", 50));
		outputFields.put(TAG_OUTPUT_NAME2_GENDER, new MetaVal("MD_Gender2", "Gender2", 5));
	}
}
