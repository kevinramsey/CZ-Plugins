package com.melissadata.kettle.profiler.support;

public class MDPropTags {
	// License string and customer id
	public static final String	TAG_PRIMARY_LICENSE			= "license";
	public static final String	TAG_PRIMARY_ID				= "primary_customerID";
	public static final String	TAG_PRIMARY_RETVAL			= "license_retVal";
	public static final String	TAG_PRIMARY_EXPIRATION		= "license_expiration";
	public static final String	TAG_PRIMARY_TEST_RESULT		= "primary_TestResult";
	public static final String	TAG_TRIAL_LICENSE			= "trial_license";
	public static final String	TAG_TRIAL_ID				= "trial_customerID";
	public static final String	TAG_TRIAL_RETVAL			= "trial_retVal";
	public static final String	TAG_TRIAL_EXPIRATION		= "trial_license_expiration";
	public static final String	TAG_TRIAL_TEST_RESULT		= "trial_TestResult";
	public static final int		MDLICENSE_Profiler			= 0x02000;
	public static final String	TAG_LOCAL_DATA_PATH			= "data_path";
	// Common web/appliance tags
	public static final String	TAG_MAX_THREADS				= "max_threads";
	public static final String	TAG_MAX_REQUESTS			= "max_requests";
	public static final String	TAG_PROFILER_OUTPUT			= "profiler_output";
	// COLUMN SETTINGS
	public static final String	TAG_PROFILER_INPUT			= "profiler_input";
	public static final String	TAG_PROFILER_INPUT_FIELD	= "profiler_input_field";
	public static final String	TAG_INPUT_SET_DEFAULT		= "opt_set_default";
	public static final String	TAG_INPUT_DEFAULT_VALUE		= "opt_default_value";
	public static final String	TAG_INPUT_SET_BOUNDS		= "opt_set_bounds";
	public static final String	TAG_INPUT_UPPER_BOUNDS		= "opt_upper_bounds";
	public static final String	TAG_INPUT_LOWER_BOUNDS		= "opt_lower_bounds";
	public static final String	TAG_INPUT_SET_CUSTOM		= "opt_set_custom";
	public static final String	TAG_INPUT_CUSTOM_PATTERN	= "opt_custom_pattern";
	public static final String	TAG_INPUT_DO_PROFILE		= "input_do_profile";
	public static final String	TAG_INPUT_DO_PASSTHROUGH	= "input_do_passthrough";
	public static final String	TAG_INPUT_DO_RESULTS		= "input_do_results";
	public static final String	TAG_INPUT_COLUMN_NAME		= "input_column_name";
	public static final String	TAG_INPUT_EXPECTED_CONTENT	= "input_expected_content";
	public static final String	TAG_INPUT_DATA_TYPE			= "input_data_type";
	public static final String	TAG_INPUT_SET_LENGTH		= "input_set_length";
	public static final String	TAG_INPUT_LENGTH			= "input_length";
	public static final String	TAG_INPUT_SET_PERSICION		= "input_set_percision";
	public static final String	TAG_INPUT_PERCISION			= "input_percsion";
	public static final String	TAG_INPUT_SET_SCALE			= "input_set_scale";
	public static final String	TAG_INPUT_SCALE				= "input_scale";
	public static final String	TAG_INPUT_SETTINGS			= "input_settings";
}
