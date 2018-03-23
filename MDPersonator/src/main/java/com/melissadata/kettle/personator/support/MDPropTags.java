package com.melissadata.kettle.personator.support;

public class MDPropTags {
	// License string and customer id
	public static final String TAG_PRIMARY_LICENSE = "license";
	public static final String TAG_PRIMARY_ID = "primary_customerID";
	public static final String TAG_PRIMARY_RETVAL = "license_retVal";
	public static final String TAG_PRIMARY_TEST_RESULT = "primary_TestResult";
	public static final String TAG_PRIMARY_EXPIRATION = "license_expiration";
	
	public static final String TAG_TRIAL_LICENSE = "trial_license";
	public static final String TAG_TRIAL_ID = "trial_customerID";
	public static final String TAG_TRIAL_RETVAL = "trial_retVal";
	public static final String TAG_TRIAL_EXPIRATION = "trial_license_expiration";
	public static final String TAG_TRIAL_TEST_RESULT = "trial_TestResult";
	
	public static final int MDLICENSE_Personator = 0x10000;
	
	// Common web/appliance tags
	public static final String TAG_PERSONATOR_THREADS = "personator_max_threads";
	public static final String TAG_PERSONATOR_REQUESTS = "personator_max_requests";
	public static final String TAG_PERSONATOR_WEB_TIMEOUT = "personator_timeout";
	public static final String TAG_PERSONATOR_WEB_RETRIES = "personator_retries";
	
	// Web service tags
	public static final String TAG_WEB_PROXY_HOST = "proxy_host";
	public static final String TAG_WEB_PROXY_PORT = "proxy_port";
	public static final String TAG_WEB_PROXY_USER = "proxy_user";
	public static final String TAG_WEB_PROXY_PASS = "proxy_pass";


	public static final String TAG_WEB_OPT_ABORT = "opt_abort";
	public static final String TAG_PERSONATOR_URL = "personator_url";

}
