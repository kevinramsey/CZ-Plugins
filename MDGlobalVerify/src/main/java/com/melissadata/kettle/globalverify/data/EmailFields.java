package com.melissadata.kettle.globalverify.data;

import java.util.HashMap;
import com.melissadata.kettle.globalverify.support.MetaVal;
import org.pentaho.di.core.Const;

public class EmailFields {
	public static final String		TAG_GLOBAL_EMAIL_OPTIONS		= "globalemail_options";
	public static final String		TAG_GLOBAL_EMAIL_INPUT			= "globalemail_input";
	public static final String		TAG_GLOBAL_EMAIL_OUTPUT			= "globalemail_output";
	// Inputs
	public static String			TAG_INPUT_EMAIL					= "globalemail_input_email";
	// Output
	public static String			TAG_OUTPUT_EMAIL				= "globalemail_output_email";
	public static String			TAG_OUTPUT_BOX_NAME				= "globalemail_output_box_name";
	public static String			TAG_OUTPUT_DOMAIN				= "globalemail_output_domain";
	public static String			TAG_OUTPUT_TOP_LEVEL_DOMAIN		= "globalemail_output_top_level_domain";
	public static String			TAG_OUTPUT_DOMAIN_DESCRIPTION	= "globalemail_output_domain_description";
	public static String 			TAG_OUTPUT_DATE_CHECKED			= "globalemail_output_date_checked";
	
	public static String			TAG_OPTION_VERIFY_MAIBOX		= "globalemail_option_verify_mailbox";
	
	public HashMap<String, MetaVal>	optionFields;
	public HashMap<String, MetaVal>	inputFields;
	public HashMap<String, MetaVal>	outputFields;
	public String					webVersion						= "";
	public int						fieldsAdded;

	public boolean hasMinRequirements() {
		return !Const.isEmpty(inputFields.get(TAG_INPUT_EMAIL).metaValue);
	}

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

		inputFields.put(TAG_INPUT_EMAIL, new MetaVal("", "Email", 50));
		outputFields.put(TAG_OUTPUT_EMAIL, new MetaVal("MD_Email", "EmailAddress", 50));
		outputFields.put(TAG_OUTPUT_BOX_NAME, new MetaVal("MD_Mailbox_Name", "MailboxName", 50));
		outputFields.put(TAG_OUTPUT_DOMAIN, new MetaVal("MD_Domain_Name", "DomainName", 50));
		outputFields.put(TAG_OUTPUT_TOP_LEVEL_DOMAIN, new MetaVal("MD_Top_Level_Domain", "TopLevelDomain", 50));
		outputFields.put(TAG_OUTPUT_DOMAIN_DESCRIPTION, new MetaVal("MD_Top_Level_Domain_Name", "TopLevelDomainName", 50));
		outputFields.put(TAG_OUTPUT_DATE_CHECKED, new MetaVal("MD_Email_Date_Checked", "DateChecked", 25));
		optionFields.put(TAG_OPTION_VERIFY_MAIBOX, new MetaVal("Express", "VERIFYMAILBOX:", 0));
		//webOptionFields.put(TAG_OPTION_DAYS_SINCE, new MetaVal("3", "DaysSinceLastVerified:", 0));
	}
}
