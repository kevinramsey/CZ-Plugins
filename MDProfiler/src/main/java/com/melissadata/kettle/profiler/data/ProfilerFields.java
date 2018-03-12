package com.melissadata.kettle.profiler.data;

import java.util.HashMap;

import org.pentaho.di.core.Const;

import com.melissadata.kettle.profiler.data.ProfilerEnum.AppendMode;
import com.melissadata.kettle.profiler.support.MetaVal;

public class ProfilerFields {
	public static final String		TAG_PROFILER_OPTIONS		= "profiler_options";
	public static final String		TAG_PROFILER_OPTION_FIELD	= "profiler_option_field";
	// JOB SETTINGS
	public static String			TAG_OPTION_SORT				= "opt_sort_analysis";
	public static String			TAG_OPTION_MATCHUP			= "opt_matchup_analysis";
	public static String			TAG_OPTION_RIGHTFIELDER		= "opt_right_field_analysis";
	public static String			TAG_OPTION_DATA_AGGREGATION	= "opt_data_aggregation";
	public static String			TAG_OPTION_TABLE_NAME		= "opt_table_name";
	public static String			TAG_OPTION_USER_NAME		= "opt_user_name";
	public static String			TAG_OPTION_JOB_NAME			= "opt_job_name";
	public static String			TAG_OPTION_JOB_DESCRIPTION	= "opt_job_description";
	public static String			TAG_OPTION_OUTPUTFILE		= "opt_output_file";
	public static String			TAG_OPTION_APPEND_DATE		= "opt_append_date";
	public static String			TAG_OPTION_FILE_HANDLING	= "opt_file_handling";
	public HashMap<String, MetaVal>	optionFields;
	public String					webVersion					= "";

	/**
	 * checks to see if the minimum requirements to run are met
	 *
	 * @return
	 */
	public boolean hasMinRequirements() {
		if (Const.isEmpty(optionFields.get(TAG_OPTION_OUTPUTFILE).metaValue))
			return false;
		else
			return true;
	}

	/**
	 * initializes all the default value
	 */
	public void init() {
		if (optionFields == null) {
			optionFields = new HashMap<String, MetaVal>();
		}
		String userhome = System.getProperty("user.home");
		// repository xml key tag, metavalue default, web tag, and size are set here
		optionFields.put(TAG_OPTION_SORT, new MetaVal("1", TAG_OPTION_SORT, 0));
		optionFields.put(TAG_OPTION_MATCHUP, new MetaVal("1", TAG_OPTION_MATCHUP, 0));
		optionFields.put(TAG_OPTION_RIGHTFIELDER, new MetaVal("1", TAG_OPTION_RIGHTFIELDER, 0));
		optionFields.put(TAG_OPTION_DATA_AGGREGATION, new MetaVal("1", TAG_OPTION_DATA_AGGREGATION, 0));
		optionFields.put(TAG_OPTION_TABLE_NAME, new MetaVal("", TAG_OPTION_TABLE_NAME, 0));
		optionFields.put(TAG_OPTION_USER_NAME, new MetaVal("", TAG_OPTION_USER_NAME, 0));
		optionFields.put(TAG_OPTION_JOB_NAME, new MetaVal("", TAG_OPTION_JOB_NAME, 0));
		optionFields.put(TAG_OPTION_JOB_DESCRIPTION, new MetaVal("", TAG_OPTION_JOB_DESCRIPTION, 0));
		optionFields.put(TAG_OPTION_OUTPUTFILE, new MetaVal(userhome + Const.FILE_SEPARATOR + "profile_tmp.prf", TAG_OPTION_OUTPUTFILE, 0));
		optionFields.put(TAG_OPTION_APPEND_DATE, new MetaVal("false", TAG_OPTION_APPEND_DATE, 0));
		optionFields.put(TAG_OPTION_FILE_HANDLING, new MetaVal(AppendMode.OVERWRITE.name(), TAG_OPTION_FILE_HANDLING, 0));
	}
}
