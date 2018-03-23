package com.melissadata.kettle.globalverify.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.melissadata.cz.support.FilterTarget;
import com.melissadata.kettle.globalverify.support.MetaVal;

public class OutputFilterFields implements Cloneable {
	public static final String		TAG_RESULT_CODES	= "result_codes";
	public static final String		TAG_FILTER			= "filter_fields";
	public static final String		TAG_FILTER_NAME		= "filter_name";
	public static final String		TAG_FILTER_RULE		= "filter_rule";
	public static final String		TAG_FILTER_TARGET	= "filter_target";
	public static final String		TAG_FILTERS			= "filters";
	public static final String		TAG_OUTPUT_FILTER	= "output_filter";
	private static final int		MD_SIZE_RESULTCODES	= 100;
	public List<FilterTarget>		filterTargets;
	public HashMap<String, MetaVal>	filterFields;

	public void init() {
		filterFields = new HashMap<String, MetaVal>();
		filterFields.put(TAG_RESULT_CODES, new MetaVal("MD_Results", "", MD_SIZE_RESULTCODES));
		filterTargets = new ArrayList<FilterTarget>(1);
	}
}
