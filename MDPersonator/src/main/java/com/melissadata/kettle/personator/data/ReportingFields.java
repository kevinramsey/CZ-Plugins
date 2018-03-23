package com.melissadata.kettle.personator.data;

import java.util.HashMap;

import com.melissadata.kettle.personator.support.MetaVal;




public class ReportingFields {

	public static final String TAG_REPORT_GLOBAL_REPORTS = "report_global_reports";

	public static final String TAG_REPORT_OPTION_GLOBAL_REPORTS = "report_options_global_reports";

	public static final String TAG_REPORT_OPTION_REPORTS = "run_reports";

	public static final String TAG_OPT_TO_FILE = "opt_to_file";

	public static final String TAG_OPT_SUB_REPORT_GLOBAL_VERIFY = "opt_sub_report_global_verify";

	public static final String TAG_OUTPUT_REPORT_DIRNAME = "output_report_dirname";

	public static final String TAG_OUTPUT_REPORT_NAME = "output_report_name";

	
	public HashMap<String, MetaVal> reportFields = new HashMap<String,MetaVal>();
	public HashMap<String, Boolean> reportOptions = new HashMap<String,Boolean>();
	
	public void init(){
		reportOptions.put(TAG_OPT_TO_FILE, new Boolean(false));
		reportOptions.put(TAG_OPT_SUB_REPORT_GLOBAL_VERIFY, new Boolean(false));
		reportOptions.put(TAG_REPORT_OPTION_GLOBAL_REPORTS, new Boolean(false));
		reportOptions.put(TAG_REPORT_OPTION_REPORTS, new Boolean(false));
		
		reportFields.put(TAG_OUTPUT_REPORT_DIRNAME, new MetaVal("","",50));
		reportFields.put(TAG_OUTPUT_REPORT_NAME, new MetaVal("","",50));
	}
	

}
